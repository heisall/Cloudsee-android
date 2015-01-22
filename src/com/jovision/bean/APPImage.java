package com.jovision.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jovision.utils.ConfigUtil;

public class APPImage {
	private String appImageUrlZh;// 简体中文
	private String appImageUrlEN;// 英文
	private String appImageUrlZht;// 繁体
	private int version;// (广告版本)
	private int result;// (接口调用结果)

	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		try {
			object.put("appImageUrlZh", appImageUrlZh);
			object.put("appImageUrlEN", appImageUrlEN);
			object.put("appImageUrlZht", appImageUrlZht);
			object.put("version", version);
			object.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public JSONArray toJsonArray(ArrayList<APPImage> appList) {
		JSONArray appArray = new JSONArray();
		try {
			if (null != appList && 0 != appList.size()) {
				int size = appList.size();
				for (int i = 0; i < size; i++) {
					appArray.put(i, appList.get(i).toJson());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return appArray;
	}

	public String listToString(ArrayList<APPImage> appList) {
		return toJsonArray(appList).toString();
	}

	/**
	 * JSON转成AD 对象
	 * 
	 * @param string
	 * @return
	 */
	public static APPImage fromJson(String string) {
		APPImage app = new APPImage();
		try {
			if (null != string && !"".equalsIgnoreCase(string)) {
				JSONObject object = new JSONObject(string);
				app.setAppImageUrlZh(ConfigUtil.getString(object,
						"appImageUrlZh"));
				app.setAppImageUrlEN(ConfigUtil.getString(object,
						"appImageUrlEN"));
				app.setAppImageUrlZht(ConfigUtil.getString(object,
						"appImageUrlZht"));
				app.setVersion(ConfigUtil.getInt(object, "version"));
				app.setResult(ConfigUtil.getInt(object, "result"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return app;
	}

	public static ArrayList<APPImage> fromJsonArray(String string) {
		ArrayList<APPImage> appList = new ArrayList<APPImage>();
		if (null == string || "".equalsIgnoreCase(string)) {
			return appList;
		}
		JSONArray adArray;
		try {
			adArray = new JSONArray(string);
			if (null != adArray && 0 != adArray.length()) {
				int length = adArray.length();
				for (int i = 0; i < length; i++) {
					APPImage ad = fromJson(adArray.get(i).toString());
					if (null != ad) {
						appList.add(ad);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return appList;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getAppImageUrlEN() {
		return appImageUrlEN;
	}

	public void setAppImageUrlEN(String appImageUrlEN) {
		this.appImageUrlEN = appImageUrlEN;
	}

	public String getAppImageUrlZh() {
		return appImageUrlZh;
	}

	public void setAppImageUrlZh(String appImageUrlZh) {
		this.appImageUrlZh = appImageUrlZh;
	}

	public String getAppImageUrlZht() {
		return appImageUrlZht;
	}

	public void setAppImageUrlZht(String appImageUrlZht) {
		this.appImageUrlZht = appImageUrlZht;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

}
