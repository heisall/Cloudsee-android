package com.jovision;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.test.JVACCOUNT;

import com.jovision.activities.BaseActivity;
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
		// MyLog.e(Consts.TAG_PLAY, "onJniNotify: " + what + ", " + uchType +
		// ", "
		// + channel + ", " + obj);

		if (null != currentNotifyer) {
			currentNotifyer.onNotify(what, uchType, channel, obj);
		}

		// if (null != obj) {
		// try {
		// Object[] array = (Object[]) obj;
		// int size = array.length;
		// for (int i = 0; i < size; i++) {
		// MyLog.d(Consts.TAG_PLAY, "onJniNotify.obj[" + i + "]: "
		// + array[i]);
		// }
		// } catch (Exception e) {
		// try {
		// String str = (String) obj;
		// MyLog.d(Consts.TAG_PLAY, "onJniNotify.obj = " + str);
		// } catch (Exception e2) {
		// }
		// }
		// }

		// switch (what) {
		// case Consts.CONNECT_CHANGE:
		//
		// break;
		//
		// case Consts.NORMAL_DATA:
		// break;
		//
		// case Consts.CHECK_RESULT:
		// if (null != obj) {
		// byte[] array = (byte[]) obj;
		// int size = array.length;
		//
		// StringBuilder sBuilder = new StringBuilder(size * 2);
		// StringBuilder sBuilder2 = new StringBuilder(size * 2);
		// for (int i = 0; i < size; i++) {
		// sBuilder.append(String.format("%c", array[i]));
		// sBuilder2.append(String.format("%c", array[i]));
		// if ((i + 1) % 10 == 0) {
		// sBuilder.append("\n");
		// }
		// if ((i + 1) % 7 == 0) {
		// sBuilder2.append("\n");
		// }
		// }
		// String resultString = sBuilder.toString();
		// MyLog.d(Consts.TAG_PLAY, "check result: \n" + resultString);
		// resultString = sBuilder2.toString();
		// MyLog.d(Consts.TAG_PLAY, "check result: \n" + resultString);
		// }
		// break;
		//
		// // case Consts.FOO:
		// // Object[] array = (Object[]) obj;
		// // MyLog.e(Consts.TAG_PLAY, "onJniNotify: " + (String) array[0]);
		// // break;
		//
		// default:
		// break;
		// }

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

}
