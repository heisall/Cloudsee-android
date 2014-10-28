package com.jovision;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.test.JVACCOUNT;
import android.util.Log;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.JVOffLineDialogActivity;
import com.jovision.activities.JVTabActivity;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AlarmUtil;
import com.jovision.utils.DefaultExceptionHandler;

public class CoreService extends Service implements IHandlerLikeNotify {

	private final static String TAG = "CloudSEE Core Service";
	private WakeLock mWakeLock = null;
	public static int errorCount = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.e(TAG, "cloudsee core service is creating...");
		acquireWakeLock();
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyLog.e(TAG, "cloudsee core service is destoryed...");
		releaseWakeLock();
	}

	private void acquireWakeLock() {
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "MY LOCATION");
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
}
