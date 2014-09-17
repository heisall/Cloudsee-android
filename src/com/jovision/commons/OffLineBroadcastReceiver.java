package com.jovision.commons;

import java.util.Timer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.JVLoginActivity;

public class OffLineBroadcastReceiver extends BroadcastReceiver {
	int timer = 16;
	AlertDialog alert;
	Context mycontext;
	final Timer offlineTimer = new Timer();
	SharedPreferences sharedPreferences = null;
	private Editor editor = null;// 获取编辑器

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		mycontext = context;
		sharedPreferences = mycontext.getSharedPreferences("JVCONFIG",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();// 获取编辑器
		int error = intent.getIntExtra("KEEP_ONLINE_ERROR_CODE", 0);

		// if (1 == error) {// 被踢掉线
		// int message = intent.getIntExtra("message", 0);
		// TimerTask task = new TimerTask() {
		// public void run() {
		// timer = timer - 1;
		// if (timer == -1) {
		// JVConst.DIALOG_SHOW = false;
		// alert.dismiss();
		// offlineTimer.cancel();
		// if (null != BaseApp.channelMap
		// && BaseApp.channelMap.size() > 0) {
		// JVSChannel jvsChannel = BaseApp.channelMap
		// .get(BaseApp.windowIndex);
		// // 推掉线前断开该断开的（音频监听，对讲），保存该保存的
		// if (null != jvsChannel && jvsChannel.isConnected()) {
		// if (JVSUDT.PLAY_FLAG == 0) {// 正常播放
		// // 正在录像暂停录像
		// if (jvsChannel.isPkt) {
		// jvsChannel.isPkt = false;
		// // JVS1.JP_P2(0);
		// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
		// }
		// // 音频监听
		// if (BaseApp.AUDIO_FLAG) {
		// if (null != BaseApp.mAudioPlayer.mAudioTrack) {
		// BaseApp.mAudioPlayer.mAudioTrack
		// .stop();
		// }
		// if (jvsChannel.is05StartCode) {
		// JVS1.JAD_D2(0);
		// }
		// }
		//
		// // 若正在语音对讲关闭对讲
		// if (BaseApp.VOICE_CALL_FLAG) {
		// BaseApp.VOICE_CALL_FLAG = false;
		// JVSUDT.JVC_SendData(
		// BaseApp.windowIndex,
		// (byte) JVNetConst.JVN_CMD_CHATSTOP,
		// new byte[0], 0);
		//
		// if (null != BaseApp.callAudioPlayer.mAudioTrack) {// 暂停播放器
		// BaseApp.callAudioPlayer.mAudioTrack
		// .stop();
		// }
		// if (null != JVSUDT.arBean) {// 暂停采集声音起
		// JVSUDT.arBean.stopRecording();
		// }
		// }
		// } else {// 远程回放
		// // 正在录像暂停录像
		// if (jvsChannel.isPkt) {
		// // JVS1.JP_P2(0);
		// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
		// jvsChannel.isPkt = false;
		// }
		// JVSUDT.PLAY_FLAG = 0;
		// }
		// }
		//
		// }
		//
		// offLineHandler.sendEmptyMessage(0);
		// } else {
		// offLineHandler.sendEmptyMessage(1);
		// }
		// }
		// };
		//
		// offlineTimer.schedule(task, 0, 1000);
		//
		// AlertDialog.Builder builder = new Builder(
		// context.getApplicationContext());
		// if (0 == message) {
		// builder.setMessage(context.getResources().getString(
		// R.string.str_offline));
		// } else {
		// builder.setMessage(context.getResources().getString(
		// R.string.str_offline2));
		// }
		// builder.setTitle(context.getResources().getString(R.string.tips)
		// + "  " + String.valueOf(timer));
		// builder.setPositiveButton(R.string.str_offline_exit,
		// new DialogInterface.OnClickListener() {
		//
		// public void onClick(DialogInterface dialog, int which) {
		//
		// AccountUtil.signOut(mycontext);
		// alert.dismiss();
		// offlineTimer.cancel();
		// if (null != BaseApp.channelMap
		// && BaseApp.channelMap.size() > 0) {
		// JVSChannel jvsChannel = BaseApp.channelMap
		// .get(BaseApp.windowIndex);
		// // 推掉线前断开该断开的（音频监听，对讲），保存该保存的
		// if (null != jvsChannel
		// && jvsChannel.isConnected()) {
		// if (JVSUDT.PLAY_FLAG == 0) {// 正常播放
		// // 正在录像暂停录像
		// if (jvsChannel.isPkt) {
		// jvsChannel.isPkt = false;
		// // JVS1.JP_P2(0);
		// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
		// }
		// // 音频监听
		// if (BaseApp.AUDIO_FLAG) {
		// if (null != BaseApp.mAudioPlayer.mAudioTrack) {
		// BaseApp.mAudioPlayer.mAudioTrack
		// .stop();
		// }
		// if (jvsChannel.is05StartCode) {
		// JVS1.JAD_D2(0);
		// }
		// }
		//
		// // 若正在语音对讲关闭对讲
		// if (BaseApp.VOICE_CALL_FLAG) {
		// BaseApp.VOICE_CALL_FLAG = false;
		// JVSUDT.JVC_SendData(
		// BaseApp.windowIndex,
		// (byte) JVNetConst.JVN_CMD_CHATSTOP,
		// new byte[0], 0);
		//
		// if (null != BaseApp.callAudioPlayer.mAudioTrack) {// 暂停播放器
		// BaseApp.callAudioPlayer.mAudioTrack
		// .stop();
		// }
		// if (null != JVSUDT.arBean) {// 暂停采集声音起
		// JVSUDT.arBean.stopRecording();
		// }
		// }
		// } else {// 远程回放
		// // 正在录像暂停录像
		// if (jvsChannel.isPkt) {
		// // JVS1.JP_P2(0);
		// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
		// jvsChannel.isPkt = false;
		// }
		// JVSUDT.PLAY_FLAG = 0;
		// }
		// }
		//
		// }
		//
		// try {
		// Intent intent = new Intent();
		// intent.setClass(mycontext,
		// JVLoginActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// mycontext.startActivity(intent);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		// });
		// builder.setNegativeButton(
		// message == 0 ? R.string.str_offline_keeponline
		// : R.string.str_offline_keeponline2,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// LoginThread loginThread = new LoginThread();
		// loginThread.start();
		// }
		// });
		// alert = builder.create();
		// alert.getWindow().setType(
		// WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// alert.setCancelable(false);
		// alert.show();
		// JVConst.DIALOG_SHOW = true;
		// } else if (2 == error) {// 超时50次
		// AlertDialog.Builder builder = new Builder(
		// context.getApplicationContext());
		// builder.setMessage(context.getResources().getString(
		// R.string.str_no_response_data));
		// builder.setPositiveButton(R.string.str_sure,
		// new DialogInterface.OnClickListener() {
		//
		// public void onClick(DialogInterface dialog, int which) {
		//
		// AccountUtil.signOut(mycontext);
		// alert.dismiss();
		// offlineTimer.cancel();
		// if (null != BaseApp.channelMap
		// && BaseApp.channelMap.size() > 0) {
		// JVSChannel jvsChannel = BaseApp.channelMap
		// .get(BaseApp.windowIndex);
		// // 推掉线前断开该断开的（音频监听，对讲），保存该保存的
		// if (null != jvsChannel
		// && jvsChannel.isConnected()) {
		// if (JVSUDT.PLAY_FLAG == 0) {// 正常播放
		// // 正在录像暂停录像
		// if (jvsChannel.isPkt) {
		// jvsChannel.isPkt = false;
		// // JVS1.JP_P2(0);
		// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
		// }
		// // 音频监听
		// if (BaseApp.AUDIO_FLAG) {
		// if (null != BaseApp.mAudioPlayer.mAudioTrack) {
		// BaseApp.mAudioPlayer.mAudioTrack
		// .stop();
		// }
		// if (jvsChannel.is05StartCode) {
		// JVS1.JAD_D2(0);
		// }
		// }
		//
		// // 若正在语音对讲关闭对讲
		// if (BaseApp.VOICE_CALL_FLAG) {
		// BaseApp.VOICE_CALL_FLAG = false;
		// JVSUDT.JVC_SendData(
		// BaseApp.windowIndex,
		// (byte) JVNetConst.JVN_CMD_CHATSTOP,
		// new byte[0], 0);
		//
		// if (null != BaseApp.callAudioPlayer.mAudioTrack) {// 暂停播放器
		// BaseApp.callAudioPlayer.mAudioTrack
		// .stop();
		// }
		// if (null != JVSUDT.arBean) {// 暂停采集声音起
		// JVSUDT.arBean.stopRecording();
		// }
		// }
		// } else {// 远程回放
		// // 正在录像暂停录像
		// if (jvsChannel.isPkt) {
		// // JVS1.JP_P2(0);
		// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
		// jvsChannel.isPkt = false;
		// }
		// JVSUDT.PLAY_FLAG = 0;
		// //
		// JVPlayActivity.getInstance().currentMenu.setText(R.string.str_video_play);//正常播放
		// }
		// }
		//
		// }
		//
		// try {
		// Intent intent = new Intent();
		// intent.setClass(mycontext,
		// JVLoginActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// mycontext.startActivity(intent);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
		//
		// alert = builder.create();
		// alert.getWindow().setType(
		// WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// alert.show();
		// JVConst.DIALOG_SHOW = true;
		// }

	}

	// // 登录线程
	// public class LoginThread extends Thread {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// int result = 0;
	// // int result = AccountUtil.userLogin(LoginUtil.userName,
	// // LoginUtil.passWord);
	// Message msg = offLineHandler.obtainMessage();
	//
	// if (0 == result) {
	// msg.what = JVAccountConst.REKEEP_ONLINE_SUCCESS;
	// } else {
	// msg.what = JVAccountConst.REKEEP_ONLINE_FAILED;
	// }
	//
	// offLineHandler.sendMessage(msg);
	//
	// super.run();
	// }
	//
	// }

	Handler offLineHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			try {
				switch (msg.what) {
				case 0:

					try {
						Intent intent = new Intent();
						intent.setClass(mycontext, JVLoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mycontext.startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 1:
					alert.setTitle(mycontext.getResources().getString(
							R.string.tips)
							+ "  " + String.valueOf(timer));
					break;

				case JVAccountConst.REKEEP_ONLINE_SUCCESS:// 重新登录成功

					offlineTimer.cancel();
					alert.dismiss();
					JVConst.DIALOG_SHOW = false;

					// BaseApp.showTextToast(mycontext,
					// R.string.str_offline_keeponline_success);
					break;
				case JVAccountConst.REKEEP_ONLINE_FAILED:// 重新登录失败

					// offlineTimer.cancel();
					// alert.dismiss();
					// JVConst.DIALOG_SHOW = false;
					// // BaseApp.showTextToast(mycontext,
					// // R.string.str_offline_keeponline_failed);
					//
					// AccountUtil.signOut(mycontext);
					// alert.dismiss();
					// offlineTimer.cancel();
					//
					// if (null != BaseApp.channelMap
					// && BaseApp.channelMap.size() > 0) {
					// JVSChannel jvsChannel = BaseApp.channelMap
					// .get(BaseApp.windowIndex);
					// // 推掉线前断开该断开的（音频监听，对讲），保存该保存的
					// if (null != jvsChannel && jvsChannel.isConnected()) {
					// if (JVSUDT.PLAY_FLAG == 0) {// 正常播放
					// // 正在录像暂停录像
					// if (jvsChannel.isPkt) {
					// jvsChannel.isPkt = false;
					// // JVS1.JP_P2(0);
					// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
					// }
					// // 音频监听
					// if (BaseApp.AUDIO_FLAG) {
					// if (null != BaseApp.mAudioPlayer.mAudioTrack) {
					// BaseApp.mAudioPlayer.mAudioTrack.stop();
					// }
					// if (jvsChannel.is05StartCode) {
					// JVS1.JAD_D2(0);
					// }
					// }
					//
					// // 若正在语音对讲关闭对讲
					// if (BaseApp.VOICE_CALL_FLAG) {
					// BaseApp.VOICE_CALL_FLAG = false;
					// JVSUDT.JVC_SendData(BaseApp.windowIndex,
					// (byte) JVNetConst.JVN_CMD_CHATSTOP,
					// new byte[0], 0);
					//
					// if (null != BaseApp.callAudioPlayer.mAudioTrack) {//
					// 暂停播放器
					// BaseApp.callAudioPlayer.mAudioTrack
					// .stop();
					// }
					// if (null != JVSUDT.arBean) {// 暂停采集声音起
					// JVSUDT.arBean.stopRecording();
					// }
					// }
					// } else {// 远程回放
					// // 正在录像暂停录像
					// if (jvsChannel.isPkt) {
					// // JVS1.JP_P2(0);
					// JVSUDT.StopRecordMP4(BaseApp.windowIndex);
					// jvsChannel.isPkt = false;
					// }
					// JVSUDT.PLAY_FLAG = 0;
					// //
					// JVPlayActivity.getInstance().currentMenu.setText(R.string.str_video_play);//正常播放
					// }
					// }
					//
					// }
					//
					// try {
					// Intent intent = new Intent();
					// intent.setClass(mycontext, JVLoginActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// mycontext.startActivity(intent);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					//
					// break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

	};
}
