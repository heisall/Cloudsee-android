package com.jovision.utils;

import java.util.ArrayList;

import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.bean.User;
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
		MySharedPreference.putString(Consts.LOCAL_USER_LIST,
				userList.toString());
	}

	/**
	 * 重新获取设备列表
	 * 
	 * @return
	 */
	public static ArrayList<Device> getDevList() {
		ArrayList<Device> devList = new ArrayList<Device>();
		String devJsonString = MySharedPreference.getString(Consts.DEVICE_LIST);
		devList = Device.fromJsonArray(devJsonString);
		return devList;
	}

	/**
	 * 保存设备列表
	 * 
	 * @param userList
	 */
	public static void saveDevList(ArrayList<Device> devList) {
		MySharedPreference.putString(Consts.DEVICE_LIST, devList.toString());
	}
}
