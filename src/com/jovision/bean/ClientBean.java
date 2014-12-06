package com.jovision.bean;

public class ClientBean {
	private String deviceUUID;
	private int platformType;// 1:android
	private int languageType;// 语言 中文 0 英文 1
	private int alarmFlag;// 0:开 1：关，注意哦 亲
	private int productType; // 产品类型 0-CloudSEE 1-NVSIP 2-HITVIS 3-TONGFANG

	public int getPlatformType() {
		return platformType;
	}

	public void setPlatformType(int platformType) {
		this.platformType = platformType;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
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
