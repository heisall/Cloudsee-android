package com.jovision.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.AlarmUtil;

public class CustomDialogActivity extends BaseActivity implements
		android.view.View.OnClickListener {
	/** 查看按钮 **/
	private Button lookVideoBtn;
	/** 报警图片 **/
	private ImageView alarmImage;
	/** 关闭按钮 **/
	private ImageView alarmClose;
	/** 报警时间 **/
	private TextView alarmTime;

	private String vod_uri_ = "";
	private String strImgUrl = "";

	private int msg_tag;

	// private String imgLoaderFilePath = "";
	private String localImgName = "", localImgPath = "";
	private String localVodName = "", localVodPath = "";
	private boolean bLocalFile = false;
	private boolean bConnectFlag = false;
	private int bDownLoadFileType = 0; // 0图片 1视频
	private String strYstNum = "";
	private ProgressDialog progressdialog;
	private PlayWindowManager manager;
	private int device_type = Consts.DEVICE_TYPE_IPC;
	private int audio_type = 16;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_alarm_dialog);
		InitViews();

		Intent intent = getIntent();
		bDownLoadFileType = 0;// 默认先下载图片
		PushInfo pushInfo = (PushInfo) intent.getSerializableExtra("PUSH_INFO");
		if (pushInfo == null) {
			showTextToast(R.string.str_alarm_pushinfo_obj_null);
			return;
		}
		msg_tag = pushInfo.messageTag;

		if (msg_tag == JVAccountConst.MESSAGE_NEW_PUSH_TAG) {// 新报警

			strYstNum = pushInfo.ystNum;
			String strAlarmTime = new String(pushInfo.alarmTime);
			strImgUrl = new String(pushInfo.pic);
			vod_uri_ = new String(pushInfo.video);
			alarmTime.setText(strAlarmTime);
			if (vod_uri_.equals("") || vod_uri_ == null) {
				lookVideoBtn.setEnabled(false);
				lookVideoBtn.setText(getResources().getString(
						R.string.str_alarm_no_video));
			}
			// strImgUrl = "./rec/00/20141017/A01185730.jpg";
			// vod_uri_ = "./rec/00/20141017/A01183434.mp4";

			if (!vod_uri_.equals("")) {
				String temp[] = vod_uri_.split("/");
				localVodName = temp[temp.length - 1];
				localVodPath = Consts.SD_CARD_PATH + "CSAlarmVOD/"
						+ localVodName;

			}

			if (!strImgUrl.equals("")) {
				String temp[] = strImgUrl.split("/");
				localImgName = temp[temp.length - 1];
				localImgPath = Consts.SD_CARD_PATH + "CSAlarmIMG/"
						+ localImgName;
				// imgLoaderFilePath = "file://"+localImgPath;
			}

			MyLog.d("New Alarm", "img_url:" + strImgUrl + ", vod_url:"
					+ vod_uri_);
			MyLog.d("New Alarm", "localVodPath:" + localVodPath);
			MyLog.d("New Alarm", "localImgPath:" + localImgPath);

			if (!fileIsExists(localImgPath)) {
				bLocalFile = false;
				if (!strImgUrl.equals("")) {
					Jni.setDownloadFileName(localImgPath);
					if (!AlarmUtil.OnlyConnect(strYstNum)) {
						showTextToast(R.string.str_alarm_connect_failed_1);
						if (!vod_uri_.equals("")) {
							lookVideoBtn.setEnabled(true);
						}
					} else {
						lookVideoBtn.setEnabled(false);
					}

				} else if (!vod_uri_.equals("")) {
					lookVideoBtn.setEnabled(true);
				}
			} else {
				if (!vod_uri_.equals("")) {
					lookVideoBtn.setEnabled(true);
				}

				Bitmap bmp = getLoacalBitmap(localImgPath);
				if (null != bmp) {
					alarmImage.setImageBitmap(bmp);
				}
				bLocalFile = true;
				// showToast("文件已存在", Toast.LENGTH_SHORT);
			}
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub"
		super.onPause();

	}

	@Override
	public void onDestroy() {
		if (bConnectFlag) {
			Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
		}

		super.onDestroy();
	}

	private void InitViews() {

		lookVideoBtn = (Button) findViewById(R.id.alarm_lookup_video_btn);
		lookVideoBtn.setOnClickListener(this);
		lookVideoBtn.setText(getResources().getString(
				R.string.str_alarm_check_video));
		lookVideoBtn.setEnabled(false);
		alarmImage = (ImageView) findViewById(R.id.alarm_img);
		alarmTime = (TextView) findViewById(R.id.alarm_datetime_tv);
		progressdialog = new ProgressDialog(CustomDialogActivity.this);
		progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressdialog.setMessage(getResources().getString(
				R.string.str_downloading_vod));
		progressdialog.setIndeterminate(false);
		progressdialog.setCancelable(false);

		alarmClose = (ImageView) findViewById(R.id.dialog_cancle_img);
		alarmClose.setOnClickListener(this);
	}

	/**
	 * 加载本地图片 http://bbs.3gstdy.com
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean fileIsExists(String strFilePath) {
		try {
			File f = new File(strFilePath);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.alarm_lookup_video_btn:// 查看录像

			if (null != vod_uri_ && !"".equalsIgnoreCase(vod_uri_)) {
				lookVideoBtn.setEnabled(false);
				bDownLoadFileType = 1;
				if (!bConnectFlag) {
					progressdialog.show();
					if (!AlarmUtil.OnlyConnect(strYstNum)) {
						progressdialog.dismiss();
						showTextToast(R.string.str_alarm_connect_failed_1);
						lookVideoBtn.setEnabled(true);
					}
				} else {
					// 已经连接上走远程回放
					Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
					progressdialog.show();
					// try {
					// Thread.sleep(100);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					if (!AlarmUtil.OnlyConnect(strYstNum)) {
						progressdialog.dismiss();
						showTextToast(R.string.str_alarm_connect_failed_1);
						lookVideoBtn.setEnabled(true);
					}
					// Intent intent = new Intent();
					// intent.setClass(this, JVRemotePlayBackActivity.class);
					// intent.putExtra("IndexOfChannel", 1);
					// intent.putExtra("acBuffStr", vod_uri_);
					// intent.putExtra("AudioByte", 0);
					// this.startActivity(intent);
				}

			}
			break;
		case R.id.dialog_cancle_img:
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showToast(String text, int duration) {
		Toast.makeText(this, text, duration).show();
	}

	/**
	 * 复制单个文件
	 * 
	 * @param srcPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param dstPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return void
	 */
	public void copyFile(String srcPath, String dstPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File srcfile = new File(srcPath);
			if (!srcfile.exists()) {
				MyLog.e("New Alarm", "文件不存在");
				return;
			}
			if (!srcfile.isFile()) {
				MyLog.e("New Alarm", "不是一个文件");
				return;
			}
			if (!srcfile.canRead()) {
				MyLog.e("New Alarm", "文件不能读");
				return;
			}

			InputStream inStream = new FileInputStream(srcPath); // 读入原文件
			FileOutputStream fs = new FileOutputStream(dstPath);
			int file_size = inStream.available();
			MyLog.e("New Alarm", "file size:" + file_size);
			byte[] buffer = new byte[file_size];

			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; // 字节数 文件大小
				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		MyLog.v(TAG, "onHandler--what=" + what + ";arg1=" + arg1 + ";arg2="
				+ arg1);
		switch (what) {
		// 连接结果
		case Consts.CALL_CONNECT_CHANGE:

			switch (arg2) {

			case JVNetConst.NO_RECONNECT:// 1 -- 连接成功//3 不必重新连接
			case JVNetConst.CONNECT_OK: {// 1 -- 连接成功
				MyLog.e("New alarm", "连接成功");
				bConnectFlag = true;
				// showToast("连接成功", Toast.LENGTH_SHORT);
				String strFilePath = "";
				if (bDownLoadFileType == 0) {
					strFilePath = strImgUrl;
					MyLog.e("Alarm", "DownFile Path:" + strFilePath);
					Jni.sendBytes(Consts.ONLY_CONNECT_INDEX,
							(byte) JVNetConst.JVN_CMD_DOWNLOADSTOP,
							new byte[0], 0);
					byte[] dataByte = strFilePath.getBytes();
					Jni.sendBytes(Consts.ONLY_CONNECT_INDEX,
							(byte) JVNetConst.JVN_REQ_DOWNLOAD, dataByte,
							dataByte.length);
				} else if (bDownLoadFileType == 1) {
					if (progressdialog.isShowing()) {
						progressdialog.dismiss();
					}
					strFilePath = vod_uri_;
					lookVideoBtn.setEnabled(true);
					// 走远程回放
					Intent intent = new Intent();
					intent.setClass(this, JVRemotePlayBackActivity.class);
					intent.putExtra("IndexOfChannel", 0);
					intent.putExtra("acBuffStr", vod_uri_);
					intent.putExtra("AudioByte", audio_type);
					intent.putExtra("DeviceType", device_type);
					intent.putExtra("bFromAlarm", true);
					intent.putExtra("is05", true);
					this.startActivity(intent);
				} else {

				}
			}
				break;
			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {

				bConnectFlag = false;
				if (progressdialog.isShowing()) {
					progressdialog.dismiss();
				}
				if (!vod_uri_.equals("")) {
					lookVideoBtn.setEnabled(true);
				}
			}
				break;
			// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED: {
				bConnectFlag = false;
				if (progressdialog.isShowing()) {
					progressdialog.dismiss();
				}
				if (!vod_uri_.equals("")) {
					lookVideoBtn.setEnabled(true);
				}
				try {
					JSONObject connectObj = new JSONObject(obj.toString());
					String errorMsg = connectObj.getString("msg");
					if ("password is wrong!".equalsIgnoreCase(errorMsg)
							|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
						showTextToast(R.string.connfailed_auth);
					} else if ("channel is not open!"
							.equalsIgnoreCase(errorMsg)) {// 无该通道服务
						showTextToast(R.string.connfailed_channel_notopen);
					} else if ("connect type invalid!"
							.equalsIgnoreCase(errorMsg)) {// 连接类型无效
						showTextToast(R.string.connfailed_type_invalid);
					} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
						showTextToast(R.string.connfailed_maxcount);
					} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
						showTextToast(R.string.connfailed_timeout);
					} else {// "Connect failed!"
						showTextToast(R.string.connect_failed);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				break;
			default:
				if (progressdialog.isShowing()) {
					progressdialog.dismiss();
				}
				// showTextToast(R.string.connect_failed);
				if (!vod_uri_.equals("")) {
					lookVideoBtn.setEnabled(true);
				}
				break;
			}
			;
		case Consts.CALL_NORMAL_DATA: {
			if (obj == null) {
				MyLog.i("ALARM NORMALDATA", "normal data obj is null");
				device_type = Consts.DEVICE_TYPE_IPC;
				audio_type = 16;
			} else {
				MyLog.i("ALARM NORMALDATA", obj.toString());
				try {
					JSONObject jobj;
					jobj = new JSONObject(obj.toString());
					device_type = jobj.optInt("device_type",
							Consts.DEVICE_TYPE_IPC);
					if (null != jobj) {
						audio_type = jobj.optInt("audio_type", 16);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					device_type = Consts.DEVICE_TYPE_IPC;
					audio_type = 16;
				}
			}

		}
			break;
		case Consts.CALL_DOWNLOAD: {
			switch (arg2) {
			case JVNetConst.JVN_RSP_DOWNLOADOVER:// 文件下载完毕
				// showToast("文件下载完毕", Toast.LENGTH_SHORT);
				// JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接,如果视频走远程回放
				Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
				if (bDownLoadFileType == 0) {
					// 下载图片
					if (!vod_uri_.equals("")) {
						lookVideoBtn.setEnabled(true);
					}

					Bitmap bmp = getLoacalBitmap(localImgPath);
					if (null != bmp) {
						alarmImage.setImageBitmap(bmp);
					}
				} else if (bDownLoadFileType == 1) {
					// 下载录像完毕
					if (progressdialog.isShowing()) {
						progressdialog.dismiss();
					}
					// 启动播放界面
					Intent intent = new Intent();
					intent.setClass(CustomDialogActivity.this,
							JVVideoActivity.class);
					intent.putExtra("URL", localVodPath);
					intent.putExtra("IS_LOCAL", true);
					startActivity(intent);
				} else {

				}
				if (progressdialog.isShowing()) {
					progressdialog.dismiss();
				}
				break;
			case JVNetConst.JVN_CMD_DOWNLOADSTOP:// 停止文件下载
				showTextToast(R.string.str_alarm_download_alarmimg_stopped);
				break;
			case JVNetConst.JVN_RSP_DOWNLOADE:// 文件下载失败
				showTextToast(R.string.str_alarm_download_alarmimg_failed);
				break;
			case JVNetConst.JVN_RSP_DLTIMEOUT:// 文件下载超时
				showTextToast(R.string.str_alarm_download_alarmimg_timeout);
				break;
			default:
				break;
			}
			;
			if (!vod_uri_.equals("")) {
				lookVideoBtn.setEnabled(true);
			}
		}
			break;
		// case JVNetConst.JVN_REQ_DOWNLOAD:
		// {
		// switch (arg1) { //二级: 结果代码
		//
		// }
		// break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

}
