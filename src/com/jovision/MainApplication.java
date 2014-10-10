package com.jovision;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.test.JVACCOUNT;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVTabActivity;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyLog;

/**
 * 整个应用的入口，管理状态、活动集合，消息队列以及漏洞汇报
 * 
 * @author neo
 * 
 */
public class MainApplication extends Application implements IHandlerLikeNotify {

	public HashMap<String, String> statusHashMap;
	private ArrayList<BaseActivity> openedActivityList;
	private IHandlerLikeNotify currentNotifyer;

	protected NotificationManager mNotifyer;

	/**
	 * 获取活动集合
	 * 
	 * @return
	 */
	public ArrayList<BaseActivity> getOpenedActivityList() {
		return openedActivityList;
	}

	/**
	 * 获取状态集合
	 * 
	 * @return
	 */
	public HashMap<String, String> getStatusHashMap() {
		return statusHashMap;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.init(Consts.LOG_PATH);
		statusHashMap = new HashMap<String, String>();
		openedActivityList = new ArrayList<BaseActivity>();

		currentNotifyer = null;
	}

	/**
	 * 底层所有的回调接口，将代替以下回调
	 * <p>
	 * {@link JVACCOUNT#JVOnLineCallBack(int)} <br>
	 * {@link JVACCOUNT#JVPushCallBack(int, String, String, String)}<br>
	 * {@link JVSUDT#enqueueMessage(int, int, int, int)}<br>
	 * {@link JVSUDT#saveCaptureCallBack(int, int, int)}<br>
	 * {@link JVSUDT#ConnectChange(String, byte, int)}<br>
	 * {@link JVSUDT#NormalData(byte, int, int, int, int, double, int, int, int, int, byte[], int)}
	 * <br>
	 * {@link JVSUDT#m_pfLANSData(String, int, int, int, int, int, boolean, int, int, String)}
	 * <br>
	 * {@link JVSUDT#CheckResult(int, byte[], int)}<br>
	 * {@link JVSUDT#ChatData(int, byte, byte[], int)}<br>
	 * {@link JVSUDT#TextData(int, byte, byte[], int, int)}<br>
	 * {@link JVSUDT#PlayData(int, byte, byte[], int, int, int, int, double, int, int, int)}
	 * 
	 * @param what
	 *            分类
	 * @param arg1
	 *            参数1
	 * @param arg2
	 *            参数2
	 * @param obj
	 *            附加对象
	 */
	public synchronized void onJniNotify(int what, int uchType, int channel,
			Object obj) {
		if (null != currentNotifyer) {
			currentNotifyer.onNotify(what, uchType, channel, obj);
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		if (null != currentNotifyer) {
			currentNotifyer.onNotify(what, arg1, arg2, obj);
		}
	}

	/**
	 * 修改当前显示的 Activity 引用
	 * 
	 * @param currentNotifyer
	 */
	public void setCurrentNotifyer(IHandlerLikeNotify currentNotifyer) {
		this.currentNotifyer = currentNotifyer;
	}

	public static int errorCount = 0;

	/**
	 * 7-保持在线的回调函数
	 * 
	 * @param res
	 */
	public void JVOnLineCallBack(int res) {
		MyLog.v("保持在线的回调函数", "----:" + res);
		try {
			if (res == 0) {// 保持在线成功
				errorCount = 0;
			} else {// 保持在线失败
				MyLog.v("保持在线失败", "----:" + res);
				errorCount++;
				if (4 == errorCount) {// 失败4次
					JVACCOUNT.StopHeartBeat();// 先停止心跳
					if (null != currentNotifyer) {
						currentNotifyer.onNotify(
								Consts.ACCOUNT_KEEP_ONLINE_FAILED, 0, 0, null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 8-推送的回调函数
	 * 
	 * @param res
	 * @param userName
	 * @param time
	 * @param msg
	 */

	@SuppressWarnings("deprecation")
	public void JVPushCallBack(int res, String userName, String time, String msg) {
		try {
			MyLog.v("推送的回调函数", "res----:" + res + ";;time----:" + time
					+ ";;msg----:" + msg);
			if (JVAccountConst.MESSAGE_PUSH_TAG == res) {
				if (null != currentNotifyer) {
					if (null != msg && !"".equalsIgnoreCase(msg)) {
						JSONObject obj = new JSONObject(msg);
						String arrayStr = statusHashMap
								.get(Consts.PUSH_JSONARRAY);
						JSONArray pushArray = null;
						if (null == arrayStr || "".equalsIgnoreCase(arrayStr)) {
							pushArray = new JSONArray();
						} else {
							pushArray = new JSONArray(arrayStr);
						}
						pushArray.put(obj);
						statusHashMap.put(Consts.PUSH_JSONARRAY,
								pushArray.toString());

						String[] alarmArray = getResources().getStringArray(
								R.array.alarm_type);

						String ns = Context.NOTIFICATION_SERVICE;
						mNotifyer = (NotificationManager) getSystemService(ns);
						// 定义通知栏展现的内容信息
						int icon = R.drawable.notification_icon;
						CharSequence tickerText = getResources().getString(
								R.string.str_alarm);
						long when = System.currentTimeMillis();
						Notification notification = new Notification(icon,
								tickerText, when);

						notification.defaults |= Notification.DEFAULT_SOUND;// 声音
						// notification.defaults |=
						// Notification.DEFAULT_LIGHTS;//灯
						// notification.defaults |=
						// Notification.DEFAULT_VIBRATE;//震动

						// 定义下拉通知栏时要展现的内容信息
						Context context = this;

						CharSequence contentText = obj
								.optString(JVAlarmConst.JK_ALARM_ALARMTIME
										+ "-"
										+ alarmArray[obj
												.optInt(JVAlarmConst.JK_ALARM_ALARMTYPE)].replace(
												"%%",
												obj.optString(JVAlarmConst.JK_ALARM_CLOUDNAME)));

						CharSequence contentTitle = getResources().getString(
								R.string.str_alarm_info);
						// CharSequence contentText = pushMessage;

						Intent notificationIntent = new Intent(this,
								JVTabActivity.class);
						notificationIntent.putExtra("tabIndex", 1);

						PendingIntent contentIntent = PendingIntent
								.getActivity(this, 0, notificationIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						notification.setLatestEventInfo(context, contentTitle,
								contentText, contentIntent);

						// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
						mNotifyer.notify(0, notification);
					}

				}
			} else if (JVAccountConst.MESSAGE_OFFLINE == res) {// 提掉线

				if (null != currentNotifyer) {
					MyLog.v("currentNotifyer-1", "" + currentNotifyer);
					currentNotifyer
							.onNotify(Consts.ACCOUNT_OFFLINE, 0, 0, null);
				}
			} else if (JVAccountConst.PTCP_ERROR == res) {// TCP错误
				JVACCOUNT.StopHeartBeat();// 先停止心跳
				if (null != currentNotifyer) {
					currentNotifyer.onNotify(Consts.ACCOUNT_TCP_ERROR, 0, 0,
							null);
				}
			} else if (JVAccountConst.PTCP_CLOSED == res) {// TCP关闭
				JVACCOUNT.StopHeartBeat();// 先停止心跳
				if (null != currentNotifyer) {
					currentNotifyer.onNotify(Consts.ACCOUNT_TCP_ERROR, 0, 0,
							null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
