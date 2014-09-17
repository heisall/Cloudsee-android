package com.jovision.old;

import java.io.Serializable;

public class JVConnectInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private boolean action = false;

	private int myPage = 0;
	private int myConn = 0;
	private long primaryID = 0;
	private String srcName = "1";// 视频源名称

	private int connType = 0;// =JVConst.JV_CONNECT_IP;//连接类型
	// =JVConst.JV_CONNECT_CS;
	private String remoteIp = ""; // 服务器ip地址
	// ="127.0.0.1";
	private int nPort = 9101; // 服务器端口号
	private int nCsNum = 12345;// 9649018;//17653732;
	private int channel = -1; // 通道号,1开始
	private int index = -1; // 通道列表中的下标,0开始
	private String userName = "admin"; // 用户名
	private String passwd = ""; // 密码
	private String group = "A";
	private boolean localTry = true;
	private boolean byUDP = true; // 是否为UDP连接
	private boolean isParent;// 是设备：true;通道：false
	private String nickName;
	public int hasWifi = 0;// 设备是否带Wi-Fi
	public int useWifi = 0; // 设备是否使用wifi
	public int deviceType = 0; // 设备类型
	public int onlineState = 0; // 是否在线
	public int linkType = 0; // 连接方式标志位 0:云视通号连接 1:手动ip直连

	// 2014-6-4
	public int streamTag = -1;// 码流参数值
	public int screenTag = -1;// 屏幕方向值

	// 是否显示连接状态
	public boolean showMsg = true;

	public JVConnectInfo() {
	}

	public JVConnectInfo(long id) {
		this.primaryID = id;
	}

	public String getSrcName() {
		return srcName;
	}

	public void setSrcName(String srcName) {
		this.srcName = srcName;
	}

	public int getConnType() {
		return connType;
	}

	public void setConnType(int connType) {
		this.connType = connType;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public int getPort() {
		return nPort;
	}

	public void setPort(int nPort) {
		this.nPort = nPort;
	}

	public int getCsNumber() {
		return nCsNum;
	}

	public void setCsNumber(int n) {
		this.nCsNum = n;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setPrimaryID(long primaryID) {
		this.primaryID = primaryID;
	}

	public long getPrimaryID() {
		return primaryID;
	}

	public boolean getAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public void setLocalTry(boolean localTry) {
		this.localTry = localTry;
	}

	public boolean isLocalTry() {
		return localTry;
	}

	public void setByUDP(boolean byUDP) {
		this.byUDP = byUDP;
	}

	public boolean isByUDP() {
		return byUDP;
	}

	public int getnPort() {
		return nPort;
	}

	public void setnPort(int nPort) {
		this.nPort = nPort;
	}

	public int getnCsNum() {
		return nCsNum;
	}

	public void setnCsNum(int nCsNum) {
		this.nCsNum = nCsNum;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getHasWifi() {
		return hasWifi;
	}

	public void setHasWifi(int hasWifi) {
		this.hasWifi = hasWifi;
	}

	public int isUseWifi() {
		return useWifi;
	}

	public void setUseWifi(int useWifi) {
		this.useWifi = useWifi;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public int getOnlineState() {
		return onlineState;
	}

	public void setOnlineState(int onlineState) {
		this.onlineState = onlineState;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public Object clone() {
		JVConnectInfo info = null;
		try {
			info = (JVConnectInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return info;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getMyPage() {
		return myPage;
	}

	public void setMyPage(int myPage) {
		this.myPage = myPage;
	}

	public int getMyConn() {
		return myConn;
	}

	public void setMyConn(int myConn) {
		this.myConn = myConn;
	}

}
