package com.jovision.utils;

import java.util.ArrayList;

import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.bean.User;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;

public class CacheUtil {
	/**
	 * 重新获取用户列表
	 * 
	 * @return
	 */
	public static ArrayList<User> getUserList() {
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		return userList;
	}

	/**
	 * 保存用户列表
	 * 
	 * @param userList
	 */
	public static void saveUserList(ArrayList<User> userList) {
		if (null != userList) {
			MySharedPreference.putString(Consts.LOCAL_USER_LIST,
					userList.toString());
		}

	}

	/**
	 * 重新获取设备列表
	 * 
	 * @return
	 */
	public static ArrayList<Device> getDevList() {
		MyLog.e("CacheUtil---1", "getDevList");
		ArrayList<Device> devList = new ArrayList<Device>();
		String devJsonString = MySharedPreference.getString(Consts.DEVICE_LIST);
		if (null != devJsonString && !"".equals(devJsonString)) {
			devList = Device.fromJsonArray(devJsonString);
		}
		return devList;
	}

	/**
	 * 保存设备列表
	 * 
	 * @param userList
	 */
	public static void saveDevList(ArrayList<Device> devList) {
		MyLog.e("CacheUtil---2", "saveDevList");
		// if (MySharedPreference.getBoolean(Consts.NEED_BROAD1)) {
		// devList = getDevList();
		// MySharedPreference.putBoolean(Consts.NEED_BROAD1, false);
		// MyLog.v("NEED_BROAD-saveDevList", devList.toString());
		// } else {
		// MyLog.v("NEED_BROAD-NO-saveDevList", devList.toString());
		// }
		if (null != devList) {
			MySharedPreference
					.putString(Consts.DEVICE_LIST, devList.toString());
		}

	}
}
