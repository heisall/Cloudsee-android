package com.jovision;

import com.example.test2.MainActivity;
import com.example.test2.WakeLockService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

public class AlarmRecevier extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//����ѭ���㲥�������		
			//��������
		   if(intent.getAction().equals("sendbroadcast.action")){		   
				 //��ȡ��Դ����
				Intent i=new Intent();
				i.setClass(context, MainActivity.class);
				i.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
		   }
		}
}
