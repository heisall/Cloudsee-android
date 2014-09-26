package com.jovision.activities;

import java.util.Timer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
import com.jovision.bean.PushInfo;
import com.jovision.commons.CheckUpdateTask;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;

public class JVTabActivity extends BaseActivity {

	private static final String TAG = "JVTabActivity";
	private int currentIndex = 0;// 当前页卡index
	protected BaseFragment[] mFragments;
	protected int[] frageIDArray = { R.id.mydevice_fragement,
			R.id.info_fragement, R.id.demo_fragement,
			R.id.devicemanage_fragement, R.id.more_fragement };

	protected NotificationManager mNotifyer;
	protected int timer = 16;
	protected Timer offlineTimer = new Timer();

	private void setFragmentIndicator(int whichIsDefault) {
		int length = frageIDArray.length;
		mFragments = new BaseFragment[length];
		for (int i = 0; i < length; i++) {
			mFragments[i] = (BaseFragment) getSupportFragmentManager()
					.findFragmentById(frageIDArray[i]);
		}

		for (int i = 0; i < length; i++) {
			getSupportFragmentManager().beginTransaction().hide(mFragments[i])
					.commit();
		}
		getSupportFragmentManager().beginTransaction()
				.show(mFragments[whichIsDefault]).commit();
		// tabListener = (OnTabListener) mFragments[whichIsDefault];

		JVFragmentIndicator mIndicator = (JVFragmentIndicator) findViewById(R.id.indicator);
		JVFragmentIndicator.setIndicator(whichIsDefault);
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				try {
					currentIndex = which;
					int length = frageIDArray.length;
					for (int i = 0; i < length; i++) {
						getSupportFragmentManager().beginTransaction()
								.hide(mFragments[i]).commit();
					}
					getSupportFragmentManager().beginTransaction()
							.show(mFragments[which]).commit();
					// tabListener = (OnTabListener) mFragments[which];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseFragment currentFrag = mFragments[currentIndex];
		if (null != currentFrag) {
			((IHandlerLikeNotify) currentFrag).onNotify(Consts.TAB_ONRESUME, 0,
					0, null);
		}
		MyLog.v(TAG, "TAB_onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		openExitDialog();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.PUSH_MESSAGE: {// 过来一条推送信息
			PushInfo pushInfo = (PushInfo) obj;
			String[] alarmArray = JVTabActivity.this.getResources()
					.getStringArray(R.array.alarm_type);

			String ns = Context.NOTIFICATION_SERVICE;
			mNotifyer = (NotificationManager) JVTabActivity.this
					.getSystemService(ns);
			// 定义通知栏展现的内容信息
			int icon = R.drawable.notification_icon;
			CharSequence tickerText = JVTabActivity.this.getResources()
					.getString(R.string.str_alarm);
			long when = System.currentTimeMillis();
			@SuppressWarnings("deprecation")
			Notification notification = new Notification(icon, tickerText, when);

			notification.defaults |= Notification.DEFAULT_SOUND;// 声音
			// notification.defaults |=
			// Notification.DEFAULT_LIGHTS;//灯
			// notification.defaults |=
			// Notification.DEFAULT_VIBRATE;//震动

			// 定义下拉通知栏时要展现的内容信息
			Context context = JVTabActivity.this.getApplicationContext();

			CharSequence contentText = pushInfo.alarmTime
					+ "-"
					+ alarmArray[pushInfo.alarmType].replace("%%",
							pushInfo.deviceNickName);

			CharSequence contentTitle = JVTabActivity.this.getResources()
					.getString(R.string.str_alarm_info);
			// CharSequence contentText = pushMessage;
			Intent notificationIntent = new Intent(JVTabActivity.this,
					JVNoticeActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					JVTabActivity.this, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
			mNotifyer.notify(0, notification);
			break;
		}
		case Consts.ACCOUNT_TCP_ERROR:
		case Consts.ACCOUNT_KEEP_ONLINE_FAILED: {// 连续三次保持在线失败
			AlertDialog alert;
			AlertDialog.Builder builder = new Builder(JVTabActivity.this);
			builder.setMessage(R.string.str_no_response_data);
			builder.setPositiveButton(R.string.str_sure,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							MyActivityManager.getActivityManager()
									.popAllActivityExceptOne(
											JVLoginActivity.class);
							Intent intent = new Intent();
							intent.setClass(JVTabActivity.this,
									JVLoginActivity.class);
							JVTabActivity.this.startActivity(intent);
							JVTabActivity.this.finish();
						}
					});

			alert = builder.create();
			alert.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alert.show();
			break;
		}
		case Consts.ACCOUNT_OFFLINE: {// 提掉线
		// TimerTask task = new TimerTask() {
		// public void run() {
		// timer = timer - 1;
		// if (timer == -1) {
		//
		// }
		// }
		// };
		//
		// offlineTimer.schedule(task, 0, 1000);
			AlertDialog alert;
			AlertDialog.Builder builder = new Builder(
					JVTabActivity.this.getApplicationContext());
			builder.setMessage(JVTabActivity.this.getResources().getString(
					R.string.str_offline));
			builder.setTitle(JVTabActivity.this.getResources().getString(
					R.string.tips)
					+ "  " + String.valueOf(timer));
			builder.setPositiveButton(R.string.str_offline_exit,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.setNegativeButton(R.string.str_offline_keeponline2,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO
						}
					});
			alert = builder.create();
			alert.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alert.setCancelable(false);
			alert.show();
			break;
		}

		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO 增加过滤
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
		MyLog.v(TAG, "onNotify");
	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initUi() {
		Boolean local = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		if (local) {
			Consts.DEVICE_LIST = Consts.LOCAL_DEVICE_LIST;
		} else {
			Consts.DEVICE_LIST = Consts.CACHE_DEVICE_LIST;
			// 在线登陆,检查更新
			CheckUpdateTask task = new CheckUpdateTask(JVTabActivity.this);
			String[] strParams = new String[3];
			strParams[0] = "0";// 0,自动检查更新
			task.execute(strParams);
		}

		setContentView(R.layout.tab_layout);
		setFragmentIndicator(0);
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
