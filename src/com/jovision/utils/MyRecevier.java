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

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.bean.Device;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;

public class MyRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			MySharedPreference.init(context);

			String action = intent.getAction();
			ArrayList<Device> myDeviceList = CacheUtil.getDevList();

			if (TextUtils.equals(action, Consts.CONNECTIVITY_CHANGE_ACTION)) {// 网络变化的时候会发送通知
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mobNetInfo = connectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				NetworkInfo wifiNetInfo = connectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				MyLog.v("changewifi", "切网络");
				// Toast.makeText(context, "切网络了", 3000).show();
				// if (wifiNetInfo.isConnected()) {
				((MainApplication) context.getApplicationContext()).onNotify(
						Consts.NET_CHANGE_CLEAR_CACHE, 0, 0, 0);
				// }
			}

			if (!MySharedPreference.getBoolean(Consts.LOCAL_LOGIN)) {
				return;
			}

			if (MySharedPreference.getBoolean(Consts.AP_SETTING)) {
				MyLog.e("MyRecevier", "AP配置网络变化了");
				return;
			}

			// statusHashMap.put(Consts.HAS_LOAD_DEMO,"false");
			if (TextUtils.equals(action, Consts.CONNECTIVITY_CHANGE_ACTION)) {// 网络变化的时候会发送通知

				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mobNetInfo = connectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				NetworkInfo wifiNetInfo = connectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if (!wifiNetInfo.isConnected()) {
					MyLog.e("MyRecevier", "网络不可以用");
					// 改变背景或者 处理网络的全局变量
				} else {
					// 改变背景或者 处理网络的全局变量
					String wifi = getCurrentWifi(context);
					PlayUtil.deleteDevIp(myDeviceList);
					CacheUtil.saveDevList(myDeviceList);
					MyLog.i("MyRecevier", "清空IP并保存");
					if (!"0x".equalsIgnoreCase(wifi)
							&& !"<unknown ssid>".equalsIgnoreCase(wifi)) {
						MySharedPreference.putBoolean(Consts.NEED_BROAD, true);
						PlayUtil.broadCast(context);
						MyLog.i("MyRecevier", "网络变化了,需要重新发广播,当前网络：" + wifi);
					} else {
						MyLog.i("MyRecevier", "网络变化了,不需要重新发广播,当前网络：" + wifi);
					}
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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