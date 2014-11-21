package com.jovision.bean;

public class ClientBean {
	private String deviceUUID;
	private int platformType;// 1:android
	private int languageType;// 语言 中文 0 英文 1
	// private int alarmFlag;// 语言 中文 0 英文 1

	public int getPlatformType() {
		return platformType;
	}

	public void setPlatformType(int platformType) {
		this.platformType = platformType;
	}

	public String getDeviceUUID() {
		return deviceUUID;
	}

	public void setDeviceUUID(String deviceUUID) {
		this.deviceUUID = deviceUUID;
	}

	public int getLanguageType() {
		return languageType;
	}

	public void setLanguageType(int languageType) {
		this.languageType = languageType;
	}

}
