package com.jovision.newbean;

import java.io.Serializable;
import java.util.ArrayList;

import com.jovision.Consts;

/**
 * 简单的设备集合类
 * 
 * @author neo
 * 
 */
public class Device implements Serializable {
	private final static String TAG = "Device";
	private ArrayList<Channel> channelList;

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

	private boolean isO5;
	/** 设备昵称 */
	private String nickName;

	/** 连接方式标志位 0:云视通号连接 1:手动ip直连 */
	private int isDevice = 0;
	/** 是否在线 0.不在线 1.在线 */
	private int onlineState = 0;
	/** 设备是否带Wi-Fi */
	private int hasWifi = 0;

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("{\"").append("ip").append("\"").append(":")
				.append("\"").append(ip).append("\",").append("\"")
				.append("port").append("\"").append(":").append(port)
				.append(",").append("\"").append("gid").append("\"")
				.append(":").append("\"").append(gid).append("\",")
				.append("\"").append("no").append("\"").append(":").append(no)
				.append(",").append("\"").append("fullNo").append("\"")
				.append(":").append("\"").append(fullNo).append("\",")
				.append("\"").append("user").append("\"").append(":")
				.append("\"").append(user).append("\",")
				.append("\"")
				.append("pwd")
				.append("\"")
				.append(":")
				.append("\"")
				.append(pwd)
				.append("\",")
				.append("\"")
				.append("isHomeProduct")
				.append("\"")
				.append(":")
				.append("\"")
				.append(isHomeProduct)
				.append("\",")
				// .append("\"").append("isHelperEnabled").append("\"").append(":").append("\"").append(isHelperEnabled).append("\",")
				.append("\"").append("deviceType").append("\"").append(":")
				.append(deviceType).append(",").append("\"").append("isO5")
				.append("\"").append(":").append("\"").append(isO5)
				.append("\",").append("\"").append("nickName").append("\"")
				.append(":").append("\"").append(nickName).append("\",")
				.append("\"").append("isDevice").append("\"").append(":")
				.append(isDevice).append(",").append("\"")
				.append("onlineState").append("\"").append(":")
				.append(onlineState).append(",").append("\"").append("hasWifi")
				.append("\"").append(":").append(hasWifi).append(",")
				.append("\"").append("channelList").append("\"").append(":[")
				.append(BeanUtil.channelListToString(channelList)).append("]}");

		// MyLog.v(TAG, sBuilder.toString());
		return sBuilder.toString();
	}

	public Device() {

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

		channelList = new ArrayList<Channel>();
		channelList.add(new Channel(this, -1, Consts.CHANNEL_JY, false, false));
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
		this.user = user;
		this.pwd = pwd;
		this.isHomeProduct = isHomeProduct;

		isHelperEnabled = false;

		channelList = new ArrayList<Channel>();
		Channel channel = null;

		for (int i = 0; i < channelCount; i++) {
			channel = new Channel(this, startWindowIndex + i, i, false, false);
			channelList.add(channel);
		}
	}

	public ArrayList<Channel> getChannelList() {
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

	// @Override
	// public String toString() {
	// StringBuilder sBuilder = new StringBuilder(1024);
	// sBuilder.append(fullNo).append("(").append(ip).append(":").append(port)
	// .append("): ").append("user = ").append(user)
	// .append(", pwd = ").append(pwd).append(", enabled = ")
	// .append(isHelperEnabled);
	// if (null != channelList) {
	// int size = channelList.size();
	// for (int i = 0; i < size; i++) {
	// sBuilder.append("\n").append(channelList.get(i).toString());
	// }
	// }
	// return sBuilder.toString();
	// }
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public boolean isO5() {
		return isO5;
	}

	public void setChannelList(ArrayList<Channel> channelList) {
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
		this.isO5 = isO5;
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
