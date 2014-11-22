package com.jovision.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AD {
	private int index;// (广告图片序号)
	private String adImgUrl;// (广告图片URL)
	private String adLink;// (图片超链接)
	private int version;// (广告版本)
	// private String savePath;
	// private String fileName;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getAdImgUrl() {
		return adImgUrl;
	}

	public void setAdImgUrl(String adImgUrl) {
		this.adImgUrl = adImgUrl;
	}

	public String getAdLink() {
		return adLink;
	}

	public void setAdLink(String adLink) {
		this.adLink = adLink;
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		try {
			object.put("index", index);
			object.put("adImgUrl", adImgUrl);
			object.put("adLink", adLink);
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
			ad.setIndex(object.getInt("index"));
			ad.setAdImgUrl(object.getString("adImgUrl"));
			ad.setAdLink(object.getString("adLink"));
			ad.setVersion(object.getInt("version"));
			// ad.setSavePath(object.getString("savePath"));
			// ad.setFileName(object.getString("fileName"));
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
