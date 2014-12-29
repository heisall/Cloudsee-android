package com.jovision.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jovision.utils.ConfigUtil;

public class AD {
	private int index;// (广告图片序号)
	private String adImgUrlCh;// (广告图片URL)--中文
	private String adLinkCh;// (图片超链接)--中文
	private String adImgUrlEn;// (广告图片URL)--英文
	private String adLinkEn;// (图片超链接)--英文
	private String adImgUrlZht;// (广告图片URL)--繁体
	private String adLinkZht;// (图片超链接)--繁体
	private int version;// (广告版本)

	// private String savePath;
	// private String fileName;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		try {
			object.put("index", index);

			object.put("adImgUrlCh", adImgUrlCh);
			object.put("adLinkCh", adLinkCh);

			object.put("adImgUrlEn", adImgUrlEn);
			object.put("adLinkEn", adLinkEn);

			object.put("adImgUrlZht", adImgUrlZht);
			object.put("adLinkZht", adLinkZht);

			object.put("version", version);
			// object.put("savePath", savePath);
			// object.put("fileName", fileName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public JSONArray toJsonArray(ArrayList<AD> adList) {
		JSONArray adArray = new JSONArray();
		try {
			if (null != adList && 0 != adList.size()) {
				int size = adList.size();
				for (int i = 0; i < size; i++) {
					adArray.put(i, adList.get(i).toJson());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return adArray;
	}

	public String listToString(ArrayList<AD> adList) {
		return toJsonArray(adList).toString();
	}

	/**
	 * JSON转成AD 对象
	 * 
	 * @param string
	 * @return
	 */
	public static AD fromJson(String string) {
		AD ad = new AD();
		try {
			JSONObject object = new JSONObject(string);
			ad.setIndex(ConfigUtil.getInt(object, "index"));

			ad.setAdImgUrlCh(ConfigUtil.getString(object, "adImgUrlCh"));
			ad.setAdLinkCh(ConfigUtil.getString(object, "adLinkCh"));

			ad.setAdImgUrlEn(ConfigUtil.getString(object, "adImgUrlEn"));
			ad.setAdLinkEn(ConfigUtil.getString(object, "adLinkEn"));

			ad.setAdImgUrlZht(ConfigUtil.getString(object, "adImgUrlZht"));
			ad.setAdLinkZht(ConfigUtil.getString(object, "adLinkZht"));

			ad.setVersion(ConfigUtil.getInt(object, "version"));
			// ad.setSavePath(ConfigUtil.getString(object,"savePath"));
			// ad.setFileName(ConfigUtil.getString(object,"fileName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ad;
	}

	public static ArrayList<AD> fromJsonArray(String string) {
		ArrayList<AD> adList = new ArrayList<AD>();
		if (null == string || "".equalsIgnoreCase(string)) {
			return adList;
		}
		JSONArray adArray;
		try {
			adArray = new JSONArray(string);
			if (null != adArray && 0 != adArray.length()) {
				int length = adArray.length();
				for (int i = 0; i < length; i++) {
					AD ad = fromJson(adArray.get(i).toString());
					if (null != ad) {
						adList.add(ad);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return adList;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getAdImgUrlCh() {
		return adImgUrlCh;
	}

	public void setAdImgUrlCh(String adImgUrlCh) {
		this.adImgUrlCh = adImgUrlCh;
	}

	public String getAdLinkCh() {
		return adLinkCh;
	}

	public void setAdLinkCh(String adLinkCh) {
		this.adLinkCh = adLinkCh;
	}

	public String getAdImgUrlEn() {
		return adImgUrlEn;
	}

	public void setAdImgUrlEn(String adImgUrlEn) {
		this.adImgUrlEn = adImgUrlEn;
	}

	public String getAdLinkEn() {
		return adLinkEn;
	}

	public void setAdLinkEn(String adLinkEn) {
		this.adLinkEn = adLinkEn;
	}

	public String getAdImgUrlZht() {
		return adImgUrlZht;
	}

	public void setAdImgUrlZht(String adImgUrlZht) {
		this.adImgUrlZht = adImgUrlZht;
	}

	public String getAdLinkZht() {
		return adLinkZht;
	}

	public void setAdLinkZht(String adLinkZht) {
		this.adLinkZht = adLinkZht;
	}

	// public String getFileName() {
	// return fileName;
	// }
	//
	// public void setFileName(String fileName) {
	// this.fileName = fileName;
	// }
	//
	// public String getSavePath() {
	// return savePath;
	// }
	//
	// public void setSavePath(String savePath) {
	// this.savePath = savePath;
	// }

}
