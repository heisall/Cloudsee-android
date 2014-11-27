//package com.jovision.utils;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.text.TextUtils;
//
//import com.jovision.Consts;
//import com.jovision.commons.MyLog;
//
//public class MyRecevier extends BroadcastReceiver {
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		String action = intent.getAction();
//		if (TextUtils.equals(action, Consts.CONNECTIVITY_CHANGE_ACTION)) {// 网络变化的时候会发送通知
//			MyLog.i("MyRecevier", "网络变化了");
//			return;
//		}
//	}
//
//}

package com.jovision.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.jovision.Consts;
import com.jovision.commons.MyLog;

public class MyRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (TextUtils.equals(action, Consts.CONNECTIVITY_CHANGE_ACTION)) {// 网络变化的时候会发送通知
			String wifi = getCurrentWifi(context);
			MyLog.i("MyRecevier", "网络变化了,当前网络：" + wifi);
			return;
		}

		// State wifiState = null;
		// State mobileState = null;
		// ConnectivityManager cm = (ConnectivityManager)
		// context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// wifiState =
		// cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		// mobileState =
		// cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		// if (wifiState != null && mobileState != null
		// && State.CONNECTED != wifiState
		// && State.CONNECTED == mobileState) {
		// // 手机网络连接成功
		// } else if (wifiState != null && mobileState != null
		// && State.CONNECTED != wifiState
		// && State.CONNECTED != mobileState) {
		// // 手机没有任何的网络
		// } else if (wifiState != null && State.CONNECTED == wifiState) {
		// // 无线网络连接成功
		// MyLog.i("MyRecevier", "无线网络连接成功");
		// }

	}

	public String getCurrentWifi(Context context) {
		String wifiName = "";
		try {
			WifiManager mWifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
			if (null != mWifiInfo) {
				wifiName = mWifiInfo.getSSID();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wifiName;
	}
}