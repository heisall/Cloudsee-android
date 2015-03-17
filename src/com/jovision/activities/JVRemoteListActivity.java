package com.jovision.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.RemoteVideoAdapter;
import com.jovision.bean.RemoteVideo;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.PlayUtil;

public class JVRemoteListActivity extends BaseActivity {

	private final String TAG = "JV_RemoteList";

	private EditText selectDate;// 选择的日期
	private ImageView moreData;// 下三角
	private Button search;// 检索
	private RemoteVideoAdapter remoteVideoAdapter;
	private ListView remoteListView;

	private String date = "";
	private Calendar rightNow = Calendar.getInstance();
	private int year;
	private int month;
	private int day;

	private ArrayList<RemoteVideo> videoList;
	private int deviceType;// 设备类型
	private int indexOfChannel;// 通道index
	private int channelOfChannel;// 通道index
	private boolean isJFH;// 是否05版解码器,05版解码器才支持录像下载supportDownload
	private int audioByte;// 音频监听比特率
	private boolean is05;// 是否05版解码器

	private int hasDownLoadSize = 0;// 已下载文件大小
	private int downLoadFileSize = 0;// 下载文件大小
	private ProgressDialog downloadDialog;// 下载进度
	boolean downloading = false;
	private String downFileFullName = "";// 下载的文件的路径
	private boolean iscancel;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

		switch (what) {
		case Consts.CALL_CONNECT_CHANGE: {
			MyLog.i(TAG, "CALL_CONNECT_CHANGE:what=" + what + ",arg1=" + arg1
					+ ",arg2=" + arg2 + ",obj=" + obj);
			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				break;
			}
			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				cancelDownload();
				break;
			}
			// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED: {
				cancelDownload();
				break;
			}
			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				cancelDownload();
				break;
			}
			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				cancelDownload();
				break;
			}
			case Consts.BAD_NOT_CONNECT: {
				break;
			}
			// 3 -- 不必要重复连接
			case JVNetConst.NO_RECONNECT: {
				break;
			}
			// 5 -- 没有连接
			case JVNetConst.NO_CONNECT: {
				break;
			}
			// 8 -- 断开连接失败
			case JVNetConst.DISCONNECT_FAILED: {
				break;
			}
			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				break;
			}
			}
		}
		case Consts.PLAY_BACK_DOWNLOAD: {// 远程回放视频下载
			if (hasSDCard(0)) {
				createDialog("", true);

				hasDownLoadSize = 0;// 已下载文件大小
				downLoadFileSize = 0;// 下载文件大小

				RemoteVideo videoBean = videoList.get(arg1);
				String acBuffStr = PlayUtil.getPlayFileString(videoBean, isJFH,
						deviceType, year, month, day, arg1);
				MyLog.v(TAG, "acBuffStr:" + acBuffStr);
				byte[] dataByte = acBuffStr.getBytes();

				String downLoadPath = Consts.DOWNLOAD_VIDEO_PATH
						+ ConfigUtil.getCurrentDate() + File.separator;
				String fileName = String.valueOf(System.currentTimeMillis())
						+ Consts.VIDEO_MP4_KIND;
				downFileFullName = downLoadPath + fileName;
				File downFile = new File(downLoadPath);
				MobileUtil.createDirectory(downFile);
				iscancel = false;
				Jni.setDownloadFileName(downFileFullName);// 下载之前必须先调用此方法设置文件名

				Jni.sendBytes(indexOfChannel,
						(byte) JVNetConst.JVN_CMD_DOWNLOADSTOP, new byte[0], 8);
				Jni.sendBytes(indexOfChannel,
						(byte) JVNetConst.JVN_REQ_DOWNLOAD, dataByte,
						dataByte.length);
				downloading = true;
			} else {
				dismissDialog();
			}
			break;
		}
		case Consts.CALL_DOWNLOAD: {// 远程回放文件下载
			if (arg1 == indexOfChannel) {
				switch (arg2) {
				case JVNetConst.JVN_RSP_DOWNLOADDATA: {// 下载进度
					// 进度{"length":2230204,"size":204800}

					if (downloading) {
						if (null != obj) {

							String downRes = obj.toString();
							if (null != downRes
									&& !"".equalsIgnoreCase(downRes)) {
								try {
									JSONObject resObj = new JSONObject(downRes);
									int size = resObj.getInt("size");
									int length = resObj.getInt("length");

									if (!hasSDCard(length / 1024 / 1024)) {
										dismissDialog();
										// SD卡空间不足
										showTextToast(R.string.str_sdcard_notenough);
										downloading = false;
										// File downFile = new File(
										// downFileFullName);
										// downFile.delete();
										Jni.sendBytes(
												indexOfChannel,
												(byte) JVNetConst.JVN_CMD_DOWNLOADSTOP,
												new byte[0], 8);
										Jni.cancelDownload();
										downloadDialog.dismiss();
										break;
									}

									hasDownLoadSize = hasDownLoadSize + size;
									downLoadFileSize = length;
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							MyLog.v(TAG, "obj=" + obj.toString());
							MyLog.v(TAG, "current-process" + hasDownLoadSize);
						}

						if (null != downloadDialog
								&& downloadDialog.isShowing()) {
							downloadDialog.setProgress(hasDownLoadSize / 1024);
						} else {
							dismissDialog();
							createDownloadProDialog(downLoadFileSize);
						}
					}
					break;
				}
				case JVNetConst.JVN_RSP_DOWNLOADOVER: {// 下载完成
					if (downloading) {
						if (null != downloadDialog
								&& downloadDialog.isShowing()) {
							downloadDialog.dismiss();
							downloadDialog = null;
						}
						showTextToast(R.string.video_download_success);
						downloading = false;
					}

					break;
				}
				case JVNetConst.JVN_RSP_DOWNLOADE: {// 下载失敗
					if (downloading) {
						if (null != downloadDialog
								&& downloadDialog.isShowing()) {
							downloadDialog.dismiss();
							downloadDialog = null;
						}
						showTextToast(R.string.video_download_failed);
						downloading = false;
					}
					break;
				}
				}
			}
			break;
		}

		case Consts.CALL_CHECK_RESULT: {// 查询远程回放数据
			byte[] pBuffer = (byte[]) obj;
			// TODO
			videoList = PlayUtil.getRemoteList(pBuffer, deviceType,
					channelOfChannel);
			if (null != videoList && 0 != videoList.size()) {
				handler.sendMessage(handler
						.obtainMessage(Consts.WHAT_REMOTE_DATA_SUCCESS));
			} else {
				handler.sendMessage(handler
						.obtainMessage(Consts.WHAT_REMOTE_NO_DATA_FAILED));
			}
			break;
		}
		case Consts.WHAT_REMOTE_DATA_SUCCESS: {
			remoteVideoAdapter.setData(videoList, isJFH);
			remoteListView.setAdapter(remoteVideoAdapter);
			remoteVideoAdapter.notifyDataSetChanged();
			dismissDialog();
			break;
		}
		case Consts.WHAT_REMOTE_DATA_FAILED: {
			remoteVideoAdapter.setData(videoList, isJFH);
			remoteVideoAdapter.notifyDataSetChanged();
			showTextToast(R.string.str_video_load_failed);
			dismissDialog();
			break;
		}
		case Consts.WHAT_REMOTE_NO_DATA_FAILED: {
			remoteVideoAdapter.setData(videoList, isJFH);
			remoteVideoAdapter.notifyDataSetChanged();
			showTextToast(R.string.str_video_nodata_failed);
			dismissDialog();
			break;
		}
		case Consts.WHAT_REMOTE_START_PLAY:
			// setResult(JVConst.REMTE_PLAYBACK_BEGIN);// 开始远程回放
			finish();
			break;
		case Consts.WHAT_REMOTE_PLAY_FAILED:// 回放失败
			showTextToast(R.string.str_video_play_failed);
			break;
		case Consts.WHAT_REMOTE_PLAY_NOT_SUPPORT:
			// Message msg1 = JVPlayActivity.getInstance().playHandler
			// .obtainMessage();
			// msg1.what = JVConst.NOT_SUPPORT_REMOTE_PLAY;
			// JVPlayActivity.getInstance().playHandler.sendMessage(msg1);
			finish();
			break;
		case Consts.WHAT_REMOTE_PLAY_EXCEPTION:
			// if (JVSUDT.PLAY_EXCEPTION_FLAG) {
			// showTextToast(R.string.str_video_disconnected);
			// JVSUDT.PLAY_EXCEPTION_FLAG = false;
			// }
			//
			// JVSUDT.PLAY_FLAG = 0;// 视频预览
			// JVSUDT.ChangePlayFalg(windowIndex + 1, 0);
			// setResult(JVConst.REMTE_CLOSE_FETURE_);// 关闭远程回放界面
			// finish();
			break;

		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.remoteplayback_layout);

		leftBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		leftBtn.setVisibility(View.VISIBLE);
		rightBtn.setVisibility(View.GONE);

		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_remote_playback);
		selectDate = (EditText) findViewById(R.id.datetext);
		moreData = (ImageView) findViewById(R.id.dateselectbtn);
		search = (Button) findViewById(R.id.search);
		remoteVideoAdapter = new RemoteVideoAdapter(JVRemoteListActivity.this);
		remoteListView = (ListView) findViewById(R.id.videolist);
		remoteListView.setOnItemClickListener(onItemClickListener);
		leftBtn.setOnClickListener(onClickListener);
		selectDate.setInputType(InputType.TYPE_NULL);
		selectDate.setOnTouchListener(onTouchListener);
		moreData.setOnTouchListener(onTouchListener);
		search.setOnClickListener(onClickListener);

		year = rightNow.get(Calendar.YEAR);
		month = rightNow.get(Calendar.MONTH) + 1;
		day = rightNow.get(Calendar.DAY_OF_MONTH);
		selectDate.setText(year + "-" + month + "-" + day);

		date = String.format("%04d%02d%02d000000%04d%02d%02d000000", year,
				month, day, year, month, day);

		Intent intent = getIntent();
		if (null != intent) {
			// supportDownload = intent.getBooleanExtra("supportDownload",
			// false);
			deviceType = intent.getIntExtra("DeviceType", 0);
			indexOfChannel = intent.getIntExtra("IndexOfChannel", 0);
			channelOfChannel = intent.getIntExtra("ChannelOfChannel", 0);
			isJFH = intent.getBooleanExtra("isJFH", false);
			audioByte = intent.getIntExtra("AudioByte", 0);
			is05 = intent.getBooleanExtra("is05", false);
		}
		searchRemoteData(2 * 1000);
	}

	/**
	 * 远程回放列点击播放事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			createDialog(R.string.connecting, true);
			RemoteVideo videoBean = videoList.get(arg2);
			String acBuffStr = PlayUtil.getPlayFileString(videoBean, isJFH,
					deviceType, year, month, day, arg2);
			MyLog.v(TAG, "acBuffStr:" + acBuffStr);
			if (null != acBuffStr && !"".equalsIgnoreCase(acBuffStr)) {
				Intent intent = new Intent();
				intent.setClass(JVRemoteListActivity.this,
						JVRemotePlayBackActivity.class);
				intent.putExtra("IndexOfChannel", indexOfChannel);
				intent.putExtra("ChannelOfChannel", channelOfChannel);
				intent.putExtra("is05", is05);
				intent.putExtra("acBuffStr", acBuffStr);
				intent.putExtra("AudioByte", audioByte);
				// JVRemoteListActivity.this.startActivity(intent);
				JVRemoteListActivity.this.startActivityForResult(intent, 0);
			}
			dismissDialog();
		}
	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (0 == arg0 && Consts.WHAT_VIDEO_DISCONNECT == arg1) {
			// showTextToast(R.string.abnormal_closed);
			this.finish();
		}
	}

	/**
	 * 单击事件
	 */
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				if (null != proDialog && proDialog.isShowing() && !downloading) {
					Jni.sendBytes(indexOfChannel,
							(byte) JVNetConst.JVN_CMD_DOWNLOADSTOP,
							new byte[0], 8);
					// Jni.cancelDownload();
				} else {
					JVRemoteListActivity.this.finish();
				}
				break;
			case R.id.search:
				searchRemoteData(100);
				break;
			}
		}
	};

	/**
	 * 检索远程回放数据
	 */
	public void searchRemoteData(final int sleepCount) {

		createDialog(R.string.str_loading_data, true);

		String temStr = selectDate.getText().toString();
		String[] temArray = temStr.split("-");
		year = Integer.parseInt(temArray[0]);
		month = Integer.parseInt(temArray[1]);
		day = Integer.parseInt(temArray[2]);
		date = String.format("%04d%02d%02d000000%04d%02d%02d000000", year,
				month, day, year, month, day);

		Thread searchThread = new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					Thread.sleep(sleepCount);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				PlayUtil.checkRemoteData(indexOfChannel, date);
			}

		};
		searchThread.start();
	}

	/**
	 * 日历轻触事件
	 */
	OnTouchListener onTouchListener = new TextView.OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if (MotionEvent.ACTION_DOWN == arg1.getAction()) {
				new DatePickerDialog(JVRemoteListActivity.this,
						new DatePickerDialog.OnDateSetListener() {

							public void onDateSet(DatePicker arg0, int year,
									int month, int day) {
								selectDate.setText(year + "-" + (++month) + "-"
										+ day);
								date = String.format(
										"%04d%02d%02d000000%04d%02d%02d000000",
										year, month, day, year, month, day);
								searchRemoteData(100);
							}
						}, rightNow.get(Calendar.YEAR),
						rightNow.get(Calendar.MONTH),
						rightNow.get(Calendar.DAY_OF_MONTH)).show();
			}
			return true;
		}

	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 创建下载进度dialog
	 */
	@SuppressWarnings("deprecation")
	private void createDownloadProDialog(int max) {
		max = max / 1024;
		downloadDialog = null;
		if (downloadDialog == null) {
			downloadDialog = new ProgressDialog(JVRemoteListActivity.this);
			downloadDialog.setCancelable(false);
			downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			downloadDialog.setTitle(getResources().getString(
					R.string.downloading_update));
			downloadDialog.setIndeterminate(false);
			downloadDialog.setMax(max);
			downloadDialog.setProgressNumberFormat("%1d KB/%2d KB");
			downloadDialog.setButton(
					getResources().getString(R.string.str_crash_cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloading = false;
							// File downFile = new File(downFileFullName);
							// downFile.delete();
							MyLog.e(TAG, "Cancel Download");
							// TODO 取消下载
							Jni.sendBytes(indexOfChannel,
									(byte) JVNetConst.JVN_CMD_DOWNLOADSTOP,
									new byte[0], 8);
							iscancel = true;
							Jni.cancelDownload();
							dialog.dismiss();
						}
					});
		}
		downloadDialog.show();
	}

	/**
	 * 网络断开，如果正在下载取消下载
	 */
	private void cancelDownload() {
		if (downloading) {
			downloading = false;
			Jni.cancelDownload();
			downloadDialog.dismiss();
		}
		this.finish();
	}
}
