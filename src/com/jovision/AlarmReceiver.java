package com.jovision;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.JVWelcomeActivity;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.AlarmUtil;

public class AlarmReceiver extends BroadcastReceiver {

	protected NotificationManager mNotifyer;
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.e("GPush", "onReceive() action=" + bundle.getInt("action"));
		Log.e("GPush", "onReceive() clientid=" + bundle.getString("clientid"));
		MySharedPreference.init(context);
		if (MySharedPreference.getBoolean(Consts.MANUAL_LOGOUT_TAG)) {
			MyLog.e("GPush", "账号手动注销，不处理离线报警");
			return;
		}		
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			String content = "";
			if (payload != null) {
				content = new String(payload);
				Log.i("GPush", "Got Payload:" + content);

			}
			String ns = Context.NOTIFICATION_SERVICE;
			mNotifyer = (NotificationManager) context.getSystemService(ns);
			// 定义通知栏展现的内容信息
			int icon = R.drawable.notification_icon;
			CharSequence tickerText = context.getResources().getString(
					R.string.str_alarm);
			long when = System.currentTimeMillis();

			Activity currentActivity = MyActivityManager.getActivityManager()
					.currentActivity();
			if (currentActivity == null
					|| currentActivity.getClass().getName()
							.equals("com.jovision.activities.JVLoginActivity")) {
				Log.e("GPush", "当前程序没有在运行，在通知栏显示....");
				Notification notification = new Notification(icon, tickerText, when);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				// notification.defaults |= Notification.DEFAULT_SOUND;// 声音
				notification.defaults |= Notification.DEFAULT_LIGHTS;// 灯
				notification.defaults |= Notification.DEFAULT_VIBRATE;// 震动

				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.alarm);
				// 定义下拉通知栏时要展现的内容信息

				// CharSequence contentText = arg1.getContent();
				// CharSequence contentTitle = arg1.getTitle();
				CharSequence contentText = "";
				CharSequence contentTitle = "";		
				try {
					// JSONObject object = new JSONObject(content);
					// if(!object.isNull("key")){
					// String strJsonValueString = object.getString("key");
					JSONObject obj = new JSONObject(content);
					PushInfo pi = new PushInfo();
					String[] alarmArray = context.getResources()
							.getStringArray(R.array.alarm_type);
					pi.alarmType = obj
							.optInt(JVAlarmConst.JK_ALARM_NEW_ALARMTYPE);
					if (pi.alarmType == 7 || pi.alarmType == 4) {
						pi.deviceNickName = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
					} else if (pi.alarmType == 11)// 第三方
					{
						pi.deviceNickName = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_ALARM_THIRD_NICKNAME);
					} else {

					}
					String strTempTime = "";
					strTempTime = obj
							.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME_STR);
					if (!strTempTime.equals("")) {
						// 有限取这个字段的时间，格式：yyyyMMddhhmmss
						pi.timestamp = "";
						pi.alarmTime = AlarmUtil.formatStrTime2(strTempTime);
					} else {
						pi.timestamp = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME);
						pi.alarmTime = AlarmUtil.getStrTime(pi.timestamp);
					}
					String disInfo = obj
							.optString(JVAlarmConst.JK_ALARM_NEW_ALARM_CUST_INFO);
					if (disInfo == null || disInfo.equals("")) {
						disInfo = alarmArray[pi.alarmType].replace("%%",
								pi.deviceNickName);
					} else {
						disInfo = disInfo.replace("%%", pi.deviceNickName);
					}
					contentText = pi.alarmTime + " " + disInfo;

					contentTitle = context.getResources().getString(
							R.string.str_alarm_info);
					// }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("GPush", "json is error:" + e.getMessage());
					e.printStackTrace();
					return;
				}
			
				Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.setClass(context, JVWelcomeActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
						notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(context, contentTitle+"-个推", contentText,
						contentIntent);
				// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
				mNotifyer.notify(1, notification);		
			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			Log.e("GPush", "cid:"+cid);
			if(null!=cid && !cid.equals("")){
				if (MySharedPreference.getString(
						Consts.KEY_DEV_TOKEN).equals("")) {
					MySharedPreference.putString(
							Consts.KEY_DEV_TOKEN, "@"+cid);
					AccountUtil.reportClientPlatformInfo(context);
				} else {
					MySharedPreference.putString(
							Consts.KEY_DEV_TOKEN, "@"+cid);
				}				
			}
			
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "taskid = " + taskid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "result = " + result);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);*/
			break;
		default:
			break;
		}
	}
}

