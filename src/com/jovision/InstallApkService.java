
package com.jovision;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class InstallApkService extends Service {
    private Handler myhandler = new Handler();
    private int count;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        myhandler.postDelayed(myTasks, 1000);
    }

    private Runnable myTasks = new Runnable() {
        /**
         * 进程运行
         */
        @Override
        public void run() {
            count++;
            Log.i("TAG", "数值。。。。。。。" + count++);
            myhandler.postDelayed(myTasks, 1000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Intent sevice = new Intent(this, InstallApkService.class);
        this.startService(sevice);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
