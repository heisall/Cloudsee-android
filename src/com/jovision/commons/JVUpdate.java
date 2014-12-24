package com.jovision.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.utils.MobileUtil;

public class JVUpdate {

	private Context mContext;

	// 提示语
	private String updateMsg = "";

	// 是否忽略更新
	private Boolean isupdate;

	// 返回的安装包url
	// private String apkUrl =
	// "http://98.126.5.66:6161/JVCloudSeeClient.apk";

	private Dialog noticeDialog;

	private Dialog downloadDialog;

	private String saveFileName = "";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private int progress;

	private Thread downLoadThread;

	private boolean interceptFlag = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				if (null != downloadDialog) {
					downloadDialog.dismiss();
				}
				installApk();
				break;
			default:
				break;
			}
		};
	};
		
	public JVUpdate(Context context) {
		this.mContext = context;
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo(String updateContent) {
		showNoticeDialog(updateContent);
	}

	private void showNoticeDialog(String updateContent) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(mContext.getString(R.string.str_hasupate));

		// builder.setMessage(mContext.getString(R.string.str_updateinfo));

		// TextView updateTextView = new TextView(mContext);
		// updateTextView.setText(updateContent);
		// updateTextView.setTextSize(18);
		// updateTextView.setGravity(Gravity.LEFT);
		// builder.setView(updateTextView);

		builder.setMessage(updateContent);
		// LinearLayout layout = new LinearLayout(mContext);
		// layout.setOrientation(LinearLayout.HORIZONTAL);
		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// layoutParams.setMargins(20, 0, 0, 0);
		// layout.setLayoutParams(layoutParams);
		// CheckBox box = new CheckBox(mContext);
		// box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// if (isChecked) {
		// MySharedPreference.putBoolean("IsUpdate", true);
		// } else {
		// MySharedPreference.putBoolean("IsUpdate", false);
		// }
		// }
		// });
		// box.setLayoutParams(layoutParams);
		// layout.addView(box);
		// TextView view = new TextView(mContext);
		// view.setTextSize(16);
		// view.setLayoutParams(layoutParams);
		// view.setTextColor(mContext.getResources().getColor(
		// R.color.more_fragment_color3));
		// view.setText(mContext.getResources().getString(R.string.str_ignore));
		// layout.addView(view);
		// builder.setView(layout);
		builder.setPositiveButton(mContext.getString(R.string.str_upatenow),
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton(mContext.getString(R.string.str_updatelater),
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(mContext.getString(R.string.str_updating));

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton(mContext.getString(R.string.cancel),
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();

		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(Url.APK_DOWNLOAD_URL
						+ mContext.getResources().getString(
								R.string.str_save_apk_name));

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				File file = new File(Consts.SOFTWARE_PATH);
				MobileUtil.createDirectory(file);
				saveFileName = Consts.SOFTWARE_PATH
						+ mContext.getResources().getString(
								R.string.str_save_apk_name);
				// File file = new File(savePath);
				// if (!file.exists()) {
				// file.mkdir();
				// }
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private void downloadApk() {
		//		try {
		//			Uri uri = Uri
		//					.parse(Url.APK_DOWNLOAD_URL
		//							+ mContext.getResources().getString(
		//									R.string.str_save_apk_name));
		//			Intent it = new Intent(Intent.ACTION_VIEW, uri);
		//			mContext.startActivity(it);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File file = new File(Consts.SOFTWARE_PATH);
		MobileUtil.createDirectory(file);
		saveFileName = Consts.SOFTWARE_PATH
				+ mContext.getResources().getString(R.string.str_save_apk_name);
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}