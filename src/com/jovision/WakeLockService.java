package com.jovision;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class WakeLockService extends Service{
	private  static String TAG="WakeLockService";
	private int count;
	private Handler mHandler = new Handler();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//��ȡ��Դ����
		//    	pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
		//��ȡ�����������
		//    	km=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		//    	Notification status = new Notification();
		//        status.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		//        startForeground(SERVICE_STATUS, status);
		super.onCreate();
	}
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		mHandler.postDelayed(runnable, 1000);
		super.onStart(intent, startId);
	}
	@Override
	public int onStartCommand(Intent mintent, int flags, int startId) {
		// TODO Auto-generated method stub 	      
		Intent intent=new Intent();
		intent.setAction("sendbroadcast.action");
		sendBroadcast(intent);
		 PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);
		//�ӿ������ʱ��ʼ��ʱ
		long fristtume=SystemClock.elapsedRealtime();
		//�õ�ȫ�ֶ�ʱ��
		AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		//��30�뷢��㲥
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, fristtume,15*1000, sender);
		Log.i(TAG, "30�뷢��һ��㲥��");
		//    	           starttheard();
		//    				flags=START_STICKY;
		return  super.onStartCommand(mintent, flags, startId);
	}
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			count++;
			Log.i("TAG", "��ֵ������������"+count);
			mHandler.postDelayed(runnable, 1000);
		}
	};
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopForeground(true);
		Intent protectintent=new Intent(this,Protectservice.class);
		//���������
		this.startService(protectintent);
		super.onDestroy();
	}
}
