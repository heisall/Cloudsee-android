package com.jovision.commons;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.jovetech.CloudSee.temp.R;

public class OffLineService extends Service {
	// // 保持在线
	// public Timer onLineTimer = null;
	// public TimerTask onLineTask = null;

	public static Handler serviceHandler;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		serviceHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Intent intent = new Intent();
				switch (msg.what) {
				case JVConst.KEEP_ONLINE_SUCCESS:
					// onLineTimer = new Timer();
					// onLineTask = new KeepOnLineTask();
					// onLineTimer.schedule(onLineTask, 45000, 45000);
					break;
				case JVConst.KEEP_ONLINE_FAILED:
					// if (null != onLineTimer) {
					// onLineTimer.cancel();
					// onLineTimer = null;
					// }
					intent.putExtra("KEEP_ONLINE_ERROR_CODE", 1);// 提掉线
					intent.setAction(getResources().getString(
							R.string.str_action_flag));
					sendBroadcast(intent);
					break;
				case JVConst.KEEP_ONLINE_TIMEOUT:
					// if (null != onLineTimer) {
					// onLineTimer.cancel();
					// onLineTimer = null;
					// }
					intent.putExtra("KEEP_ONLINE_ERROR_CODE", 2);// 超时
					intent.setAction(getResources().getString(
							R.string.str_action_flag));
					sendBroadcast(intent);
					break;
				}
			}

		};

		// // 保持在线6秒钟执行一次
		// onLineTimer = new Timer();
		// onLineTask = new KeepOnLineTask();
		// onLineTimer.schedule(onLineTask, 45000, 45000);
		return super.onStartCommand(intent, flags, startId);
	}

	// public int errorCount = 0;// 错误次数，连续三次再提示给用户告诉用户已经被迫下线
	// public int errorCountTimeOut = 0;// 错误次数，连续10次再提示给用户告诉用户超时
	//
	// class KeepOnLineTask extends TimerTask {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// if (!"".equalsIgnoreCase(LoginUtil.UNIQUECODE)) {
	// Log.e("第" + errorCount + "次", "timer");
	// if (1 == LoginUtil.keepOnLine()) {// 被踢掉，累积次数加一
	// errorCount++;
	// if (!JVConst.DIALOG_SHOW) {
	// Log.e("被踢掉线", "直接弹提示框");
	// errorCount = 0;// 提示后，count置为0
	// Message msg = serviceHandler.obtainMessage();
	// msg.what = JVConst.KEEP_ONLINE_FAILED;
	// serviceHandler.sendMessage(msg);
	// }
	// }
	// if (2 == LoginUtil.keepOnLine()) {// 请求超时，累积次数加一
	// errorCountTimeOut++;
	// Log.e("第" + errorCountTimeOut + "次", "45秒时间到");
	// if (errorCountTimeOut >= 10) {// 连续10次提示错误
	// if (!JVConst.DIALOG_SHOW) {
	// Log.e("第50次", "400秒时间到，提示");
	// errorCountTimeOut = 0;// 提示后，count置为0
	// Message msg = serviceHandler.obtainMessage();
	// msg.what = JVConst.KEEP_ONLINE_TIMEOUT;
	// serviceHandler.sendMessage(msg);
	// }
	// }
	// } else {// 维持在线成功一次count置为0
	// errorCount = 0;
	// errorCountTimeOut = 0;
	// }
	// }
	// }
	//
	// }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
		MyLog.e("服务timer销毁", "服务timer销毁");
		// if (null != onLineTimer) {
		// onLineTimer.cancel();
		// onLineTimer = null;
		// }
		// if (null != onLineTask) {
		// onLineTask.cancel();
		// onLineTask = null;
		// }
	}

}
