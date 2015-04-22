
package com.jovision;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.jovision.commons.MyLog;

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
