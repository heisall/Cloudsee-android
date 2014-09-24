package com.jovision.thread;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.newbean.BeanUtil;
import com.jovision.newbean.Device;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;

public class GetDeviceListThread extends Thread {
	private static final String TAG = "GetDeviceListThread";
	private Context context;
	private HashMap<String, String> statusHashMap = null;
	private ArrayList<Device> deviceList;

	public GetDeviceListThread(Context con) {
		context = con;
		statusHashMap = ((MainApplication) context.getApplicationContext())
				.getStatusHashMap();
	}

	@Override
	public void run() {

		try {
			boolean localFlag = Boolean.valueOf(statusHashMap
					.get(Consts.LOCAL_LOGIN));
			if (!localFlag) {// 非本地登录，无论是否刷新都执行
				// 获取所有设备列表和通道列表
				// 如果设备请求失败，多请求一次
				// ArrayList<Device> temlist = deviceList;
				deviceList = DeviceUtil.getUserDeviceList(statusHashMap
						.get(Consts.KEY_USERNAME));
				if (null == deviceList || 0 == deviceList.size()) {
					deviceList = DeviceUtil.getUserDeviceList(statusHashMap
							.get(Consts.KEY_USERNAME));
				}
				((MainApplication) context.getApplicationContext()).onNotify(
						Consts.GET_DEVICE_LIST_FUNCTION,
						JVAccountConst.REFERSH_DEVICE_LIST_ONLY, 0, null);

				if (null != deviceList && 0 != deviceList.size()) {
					int size = deviceList.size();
					if (0 != size) {
						for (int i = 0; i < deviceList.size(); i++) {
							deviceList.get(i).setChannelList(
									DeviceUtil.getDevicePointList(deviceList
											.get(i), deviceList.get(i)
											.getFullNo()));
						}
					}
				}

			} else if (localFlag) {// 本地登录且是刷新

				// ArrayList<Device> demoList = new ArrayList<Device>();
				// demoList.add(new Device("", 9101, "S", 53530352, "admin",
				// "123", true, 1, 0));
				// demoList.add(new Device("", 9101, "A", 361, "abc", "123",
				// false, 1, 1));
				// demoList.add(new Device("", 9101, "S", 26680286, "admin",
				// "123", true, 1, 2));
				// demoList.add(new Device("", 9101, "S", 52942216, "admin", "",
				// false, 1, 3));
				String devJsonString = MySharedPreference
						.getString(Consts.DEVICE_LIST);
				// BeanUtil.deviceListToString(demoList);
				// String devJsonString =
				// MySharedPreference.getString(Consts.DEVICE_LIST);
				deviceList = BeanUtil.stringToDevList(devJsonString);
				MyLog.v(TAG, "size=" + deviceList.size());
			}

			if (null != deviceList && 0 != deviceList.size()) {// 获取设备成功,去广播设备列表
																// msg.what
																// =
																// DEVICE_GETDATA_SUCCESS;

				if (ConfigUtil.is3G(context, false)) {// 3G直接加载设备
					((MainApplication) context.getApplicationContext())
							.onNotify(Consts.GET_DEVICE_LIST_FUNCTION,
									Consts.DEVICE_GETDATA_SUCCESS, 0, null);
				} else {
					((MainApplication) context.getApplicationContext())
							.onNotify(Consts.GET_DEVICE_LIST_FUNCTION,
									Consts.DEVICE_GETDATA_SUCCESS, 0,
									deviceList);
				}

			} else if (null != deviceList && 0 == deviceList.size()) {// 无数据
				MyLog.v(TAG, "nonedata");
				((MainApplication) context.getApplicationContext()).onNotify(
						Consts.GET_DEVICE_LIST_FUNCTION,
						Consts.DEVICE_NO_DEVICE, 0, deviceList);
			} else {// 获取设备失败
				((MainApplication) context.getApplicationContext()).onNotify(
						Consts.GET_DEVICE_LIST_FUNCTION,
						Consts.DEVICE_GETDATA_FAILED, 0, deviceList);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		super.run();
	}
}
