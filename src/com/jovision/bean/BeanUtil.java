package com.jovision.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jovision.commons.MyLog;

public class BeanUtil {

	/**
	 * JSON转成User 对象
	 * 
	 * @param string
	 * @return
	 */
	public static UserBean toUser(String string) {
		UserBean user = new UserBean();
		try {
			JSONObject object = new JSONObject(string);
			user.setUserName(object.getString("userName"));
			user.setUserPwd(object.getString("userPwd"));
			user.setUserEmail(object.getString("userEmail"));
			user.setLastLogin(object.getInt("lastLogin"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;
	}

	/**
	 * 通道列表转成JSON
	 * 
	 * @param channelList
	 * @return
	 */
	public static String userListToString(ArrayList<UserBean> userlList) {
		StringBuilder sBuilder = new StringBuilder();

		if (null == userlList || 0 == userlList.size()) {
			return sBuilder.toString();
		}

		if (null != userlList && 0 != userlList.size()) {
			int size = userlList.size();
			for (int i = 0; i < size; i++) {
				sBuilder.append(userlList.get(i).toString()).append(",");
			}
		}
		sBuilder.deleteCharAt(sBuilder.lastIndexOf(","));
		return sBuilder.toString();
	}

	/**
	 * JSON转成通道列表
	 * 
	 * @param str
	 * @param dev
	 * @return
	 */
	public static ArrayList<UserBean> stringToUserList(String str) {
		ArrayList<UserBean> list = new ArrayList<UserBean>();
		try {
			JSONArray jArray = new JSONArray(str);
			if (null != jArray && 0 != jArray.length()) {
				int length = jArray.length();
				for (int i = 0; i < length; i++) {
					UserBean user = toUser(jArray.get(i).toString());
					list.add(user);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/************************************************* 通道 *********************************************/
	/**
	 * JSON转成channel 对象
	 * 
	 * @param string
	 * @return
	 */
	public static Channel toChannel(String string) {
		Channel channel = new Channel();
		try {
			JSONObject object = new JSONObject(string);
			channel.setIndex(object.getInt("index"));
			channel.setChannel(object.getInt("channel"));
			channel.setChannelName(object.getString("channelName"));
			// channel.setConnecting(object.getBoolean("isConnecting"));
			// channel.setConnecting(object.getBoolean("isConnected"));
			// channel.setRemotePlay(object.getBoolean("isRemotePlay"));
			// channel.setConfigChannel(object.getBoolean("isConfigChannel"));
			// channel.setAuto(object.getBoolean("isAuto"));
			// channel.setVoiceCall(object.getBoolean("isVoiceCall"));
			// channel.setSurfaceCreated(object.getBoolean("surfaceCreated"));
			// channel.setSendCMD(object.getBoolean("isSendCMD"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return channel;
	}

	/**
	 * 通道列表转成JSON
	 * 
	 * @param channelList
	 * @return
	 */
	public static String channelListToString(ArrayList<Channel> channelList) {

		StringBuilder sBuilder = new StringBuilder();
		if (null == channelList || 0 == channelList.size()) {
			return sBuilder.toString();
		}
		try {
			if (null != channelList && 0 != channelList.size()) {
				int size = channelList.size();
				for (int i = 0; i < size; i++) {
					sBuilder.append(channelList.get(i).toString()).append(",");
				}
			}
			sBuilder.deleteCharAt(sBuilder.lastIndexOf(","));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sBuilder.toString();
	}

	/**
	 * JSON转成通道列表
	 * 
	 * @param str
	 * @param dev
	 * @return
	 */
	public static ArrayList<Channel> stringToChannelList(String str, Device dev) {
		ArrayList<Channel> list = new ArrayList<Channel>();
		try {
			JSONArray jArray = new JSONArray(str);
			if (null != jArray && 0 != jArray.length()) {
				int length = jArray.length();
				for (int i = 0; i < length; i++) {
					Channel channel = toChannel(jArray.get(i).toString());
					channel.setParent(dev);
					list.add(channel);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/************************************************* 设备 *********************************************/

	/**
	 * JSON转成Device
	 * 
	 * @param string
	 * @return
	 */
	public static Device toDevice(String string) {
		Device dev = new Device();
		try {
			JSONObject object = new JSONObject(string);
			dev.setIp(object.getString("ip"));
			dev.setPort(object.getInt("port"));
			dev.setGid(object.getString("gid"));
			dev.setNo(object.getInt("no"));
			dev.setFullNo(object.getString("fullNo"));
			dev.setUser(object.getString("user"));
			dev.setPwd(object.getString("pwd"));
			dev.setHomeProduct(object.getBoolean("isHomeProduct"));
			// dev.setHelperEnabled(object.getBoolean("isHelperEnabled"));
			dev.setDeviceType(object.getInt("deviceType"));
			dev.setO5(object.getBoolean("is05"));
			try {
				// [Neo] may not contains this value
				dev.setNickName(object.getString("nickName"));
			} catch (Exception e) {
			}
			dev.setIsDevice(object.getInt("isDevice"));
			dev.setOnlineState(object.getInt("onlineState"));
			dev.setHasWifi(object.getInt("hasWifi"));
			dev.setChannelList(stringToChannelList(
					object.getString("channelList"), dev));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dev;
	}

	/**
	 * 通道列表转成JSON
	 * 
	 * @param channelList
	 * @return
	 */
	public static String deviceListToString(ArrayList<Device> devList) {
		if (null == devList || 0 == devList.size()) {
			return "";
		}
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("[");
		if (null != devList && 0 != devList.size()) {
			int size = devList.size();
			for (int i = 0; i < size; i++) {
				sBuilder.append(devList.get(i).toString()).append(",");
			}
		}
		sBuilder.deleteCharAt(sBuilder.lastIndexOf(","));
		sBuilder.append("]");
		return sBuilder.toString();
	}

	/**
	 * JSON转成通道列表
	 * 
	 * @param str
	 * @param dev
	 * @return
	 */
	public static ArrayList<Device> stringToDevList(String str) {
		ArrayList<Device> list = new ArrayList<Device>();

		if (str.equalsIgnoreCase("")) {
			return list;
		}
		try {
			JSONArray jArray = new JSONArray(str);
			if (null != jArray && 0 != jArray.length()) {
				int length = jArray.length();
				for (int i = 0; i < length; i++) {
					Device dev = toDevice(jArray.get(i).toString());
					list.add(dev);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
