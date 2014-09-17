package com.jovision.newbean;

import com.jovision.commons.MyLog;

//登录用户
public class UserBean {
	private final static String TAG = "UserBean";
	private long primaryID = 0;
	private String userName = "";
	private String userPwd = "";
	private String userEmail = "";
	private int lastLogin = 0;// 最后一次登录，0：不是 1：是

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public int getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(int lastLogin) {
		this.lastLogin = lastLogin;
	}

	public long getPrimaryID() {
		return primaryID;
	}

	public void setPrimaryID(long primaryID) {
		this.primaryID = primaryID;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("{\"").append("userName").append("\"").append(":")
				.append("\"").append(userName).append("\",").append("\"")
				.append("userPwd").append("\"").append(":").append("\"")
				.append(userPwd).append("\",").append("\"").append("userEmail")
				.append("\"").append(":").append("\"").append(userEmail)
				.append("\",").append("\"").append("lastLogin").append("\"")
				.append(":").append(lastLogin).append("}");
		MyLog.v(TAG, sBuilder.toString());
		return sBuilder.toString();
	}

}
