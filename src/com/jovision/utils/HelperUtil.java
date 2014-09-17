package com.jovision.utils;

public class HelperUtil {
	// /**
	// * 给缓存的设备设置小助手
	// */
	// public static void setHelpToSavedDevice() {
	//
	// String str = MySharedPreference.getString("saveDevice");
	// MyLog.v("取出来的JSON--", str);
	// if ("".equalsIgnoreCase(str)) {
	// return;
	// }
	// JSONArray dataArray;
	// try {
	// dataArray = new JSONArray(str);
	// if (null != dataArray && 0 != dataArray.length()) {
	// for (int i = 0; i < dataArray.length(); i++) {
	// JSONObject obj = dataArray.getJSONObject(i);
	// Device dev = new Device();
	// dev.deviceNum = obj.getString("DeviceNum");
	// dev.deviceLoginUser = obj.getString("DeviceUser");
	// dev.deviceLoginPwd = obj.getString("DevicePass");
	// MyLog.e("设置小助手的设备", dev.deviceNum);
	// ConfigUtil.setHelpToDevice(dev);
	// MyLog.v("sss", i + " " + dev.deviceNum);
	// }
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
