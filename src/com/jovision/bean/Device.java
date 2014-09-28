package com.jovision.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jovision.Consts;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;

/**
 * 简单的设备集合类
 * 
 * @author neo
 * 
 */
public class Device {

	private final static String TAG = "Device";

	private MyList<Channel> channelList;

	/** 设备IP */
	private String ip;
	/** 设备端口 */
	private int port;
	/** 分组 A */
	private String gid;
	/** 云视通号 361 */
	private int no;
	/** 云视通号 A361 */
	private String fullNo;
	/** 设备用户名 */
	private String user;
	/** 设备密码 */
	private String pwd;
	/** 是否是家用产品 */
	private boolean isHomeProduct;

	private boolean isHelperEnabled;
	/** 设备类型 */
	private int deviceType;

	private boolean is05;
	/** 设备昵称 */
	private String nickName;

	/** 连接方式标志位 0:云视通号连接 1:手动ip直连 */
	private int isDevice = 0;
	/** 是否在线 0.不在线 1.在线 */
	private int onlineState = 0;
	/** 设备是否带Wi-Fi */
	private int hasWifi = 0;

	public Device() {
		channelList = new MyList<Channel>();
	}

	/**
	 * 创建已有一个通道的配置设备
	 * 
	 * @param gid
	 * @param no
	 */
	public Device(String gid, int no) {
		ip = Consts.IPC_DEFAULT_IP;
		port = Consts.IPC_DEFAULT_PORT;
		this.gid = gid;
		this.no = no;
		this.fullNo = gid + no;
		user = Consts.IPC_DEFAULT_USER;
		pwd = Consts.IPC_DEFAULT_PWD;

		isHomeProduct = true;
		isHelperEnabled = false;

		channelList = new MyList<Channel>();
		channelList.add(new Channel(this, -1, Consts.CHANNEL_JY, false, false,
				""));
	}

	/**
	 * 创建指定通道个数的起始索引的设备
	 * 
	 * @param ip
	 * @param port
	 * @param gid
	 * @param no
	 * @param user
	 * @param pwd
	 * @param isHomeProduct
	 * @param channelCount
	 * @param startWindowIndex
	 */
	public Device(String ip, int port, String gid, int no, String user,
			String pwd, boolean isHomeProduct, int channelCount,
			int startWindowIndex) {
		this.ip = ip;
		this.port = port;
		this.gid = gid;
		this.no = no;
		this.fullNo = gid + no;
		this.nickName = fullNo;
		this.user = user;
		this.pwd = pwd;
		this.isHomeProduct = isHomeProduct;

		isHelperEnabled = false;

		channelList = new MyList<Channel>();
		Channel channel = null;

		for (int i = 0; i < channelCount; i++) {
			channel = new Channel(this, startWindowIndex + i, i, false, false,
					fullNo + "_" + (i + 1));
			channelList.add(channel);
		}
	}

	public MyList<Channel> getChannelList() {
		return channelList;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getGid() {
		return gid;
	}

	public int getNo() {
		return no;
	}

	public String getFullNo() {
		return fullNo;
	}

	public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}

	public boolean isHomeProduct() {
		return isHomeProduct;
	}

	public void setHelperEnabled(boolean isHelperEnabled) {
		this.isHelperEnabled = isHelperEnabled;
	}

	public void setHomeProduct(boolean isHomeProduct) {
		this.isHomeProduct = isHomeProduct;
	}

	public boolean isHelperEnabled() {
		return isHelperEnabled;
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();

		try {
			object.put("ip", ip);
			object.put("port", port);
			object.put("gid", gid);
			object.put("no", no);
			object.put("fullNo", fullNo);
			object.put("user", user);
			object.put("pwd", pwd);
			object.put("isHomeProduct", isHomeProduct);
			object.put("deviceType", deviceType);
			object.put("is05", is05);
			object.put("nickName", nickName);
			object.put("isDevice", isDevice);
			object.put("onlineState", onlineState);
			object.put("hasWifi", hasWifi);
			try {
				ArrayList<Channel> list = channelList.toList();
				int size = list.size();
				for (int i = 0; i < size; i++) {
					array.put(i, list.get(i).toJson());
				}
				object.put("channelList", array);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public static JSONArray toJsonArray(ArrayList<Device> devList) {
		JSONArray devArray = new JSONArray();

		try {
			if (null != devList && 0 != devList.size()) {
				int size = devList.size();
				for (int i = 0; i < size; i++) {
					devArray.put(i, devList.get(i).toJson());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return devArray;
	}

	public static String listToString(ArrayList<Device> devList) {
		return toJsonArray(devList).toString();
	}

	/**
	 * JSON转成Device
	 * 
	 * @param string
	 * @return
	 */
	public static Device fromJson(String string) {
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
				dev.setNickName("");
				e.printStackTrace();
			}

			dev.setIsDevice(object.getInt("isDevice"));
			dev.setOnlineState(object.getInt("onlineState"));
			dev.setHasWifi(object.getInt("hasWifi"));
			dev.setChannelList(Channel.fromJsonArray(
					object.getString("channelList"), dev));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dev;
	}

	public static ArrayList<Device> fromJsonArray(String string) {
		ArrayList<Device> devList = new ArrayList<Device>();
		if (null == string || "".equalsIgnoreCase(string)) {
			return devList;
		}
		JSONArray devArray;
		try {
			devArray = new JSONArray(string);
			if (null != devArray && 0 != devArray.length()) {
				int length = devArray.length();
				for (int i = 0; i < length; i++) {
					Device dev = fromJson(devArray.get(i).toString());
					if (null != dev) {
						devList.add(dev);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return devList;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public boolean is05() {
		return is05;
	}

	public void setChannelList(MyList<Channel> channelList) {
		this.channelList = channelList;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public void setFullNo(String fullNo) {
		this.fullNo = fullNo;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setO5(boolean isO5) {
		this.is05 = isO5;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getIsDevice() {
		return isDevice;
	}

	public void setIsDevice(int isDevice) {
		this.isDevice = isDevice;
	}

	public int getOnlineState() {
		return onlineState;
	}

	public void setOnlineState(int onlineState) {
		this.onlineState = onlineState;
	}

	public int getHasWifi() {
		return hasWifi;
	}

	public void setHasWifi(int hasWifi) {
		this.hasWifi = hasWifi;
	}

}
