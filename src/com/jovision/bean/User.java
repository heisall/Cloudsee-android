package com.jovision.bean;

//登录用户
public class User {
	private long primaryID = 0;
	private String userName = "";
	private String userPwd = "";

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

}
