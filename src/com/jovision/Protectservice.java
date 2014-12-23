package com.jovision;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Protectservice extends Service{

	private static final String TAG = "Protectservice";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	Intent service=new Intent(this,WakeLockService.class);
    	//�����
    	this.startService(service);
    	Log.i(TAG, "�������������!");
    	super.onDestroy();
    }
}
