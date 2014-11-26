package com.jovision.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.jovision.Consts;
import com.jovision.commons.MyLog;

public class MyRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, Consts.CONNECTIVITY_CHANGE_ACTION)) {// 网络变化的时候会发送通知
			MyLog.i("MyRecevier", "网络变化了");
			return;
		}
	}

}