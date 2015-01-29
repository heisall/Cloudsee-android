package com.jovision.utils;

import java.util.ArrayList;

import com.jovision.Consts;
import com.jovision.bean.User;
import com.jovision.commons.MySharedPreference;

public class UserUtil {
	/** 1.查询所有用户 */
	public static ArrayList<User> getUserList() {
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		return userList;
	}

	/** 2.增加用户 */
	public static ArrayList<User> addUser(User user) {
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		boolean find = false;
		if (null == userList) {
			userList = new ArrayList<User>();
		} else {
			int size = userList.size();
			for (int i = 0; i < size; i++) {
				if (user.getUserName().equalsIgnoreCase(
						userList.get(i).getUserName())) {
					userList.get(i).setUserName(user.getUserName());
					userList.get(i).setUserPwd(user.getUserPwd());
					userList.get(i).setUserEmail(user.getUserEmail());
					userList.get(i).setLastLogin(user.getLastLogin());
					userList.get(i).setPrimaryID(user.getPrimaryID());
					userList.get(i).setJudgeFlag(user.getJudgeFlag());
					find = true;
					break;
				}
			}
		}
		if (!find) {
			userList.add(user);
		}
		MySharedPreference.putString(Consts.LOCAL_USER_LIST,
				userList.toString());
		return userList;
	}

	/** 3.删除用户 */
	public static ArrayList<User> deleteUser(int index) {
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		userList.remove(index);
		MySharedPreference.putString(Consts.LOCAL_USER_LIST,
				userList.toString());
		return userList;
	}

	/** 4.修改用户 */
	public static ArrayList<User> editUser(User user) {
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		userList.remove(user);
		int size = userList.size();
		for (int i = 0; i < size; i++) {
			if (userList.get(i).getUserName().equals(user.getUserName())) {
				userList.get(i).setUserName(user.getUserName());
				userList.get(i).setUserPwd(user.getUserPwd());
				userList.get(i).setUserEmail(user.getUserEmail());
				userList.get(i).setLastLogin(user.getLastLogin());
				userList.get(i).setPrimaryID(user.getPrimaryID());
				userList.get(i).setJudgeFlag(user.getJudgeFlag());
			}
		}
		MySharedPreference.putString(Consts.LOCAL_USER_LIST,
				userList.toString());
		return userList;
	}

	/** 5.获取上次登陆用户 */
	public static User getLastUser() {
		User lastUser = null;
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		int size = userList.size();
		for (int i = 0; i < size; i++) {
			if (1 == userList.get(i).getLastLogin()) {
				lastUser = userList.get(i);
			}
		}
		return lastUser;
	}

	/** 6.注销，将所有用户都置为非免登陆 */
	public static void resetAllUser() {
		ArrayList<User> userList = User.fromJsonArray(MySharedPreference
				.getString(Consts.LOCAL_USER_LIST));
		int size = userList.size();
		for (int i = 0; i < size; i++) {
			userList.get(i).setLastLogin(0);
		}
		MySharedPreference.putString(Consts.LOCAL_USER_LIST,
				userList.toString());
		return;
	}

	public static User getUserByName(String userName) {
		User dstUser = null;
		try {
			ArrayList<User> userList = User.fromJsonArray(MySharedPreference
					.getString(Consts.LOCAL_USER_LIST));

			if (null == userList || 0 == userList.size()) {
				return null;
			} else {
				int size = userList.size();
				for (int i = 0; i < size; i++) {
					if (null != userName && null != userList.get(i)) {
						if (userName.equalsIgnoreCase(userList.get(i)
								.getUserName())) {
							dstUser = userList.get(i);
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dstUser;
	}
}
