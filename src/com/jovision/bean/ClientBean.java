package com.jovision.bean;

public class ClientBean {
	private String deviceUUID;
	private int platformType;// 1:android
	private int languageType;// 语言 中文 0 英文 1
	private int alarmFlag;//0:开 1：关，注意哦 亲

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

	public void setAlarmFlag(int flag) {
		this.alarmFlag = flag;
	}

	public int getLanguageType() {
		return languageType;
	}

	public void setLanguageType(int languageType) {
		this.languageType = languageType;
	}

	public int getAlarmFlag() {
		return alarmFlag;
	}

}
