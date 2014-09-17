package com.jovision.bean;

import java.io.Serializable;
import java.util.ArrayList;

//设备
/**
 * [{"DeviceNum":"A33381046", "LoginUser":"abc", "LoginPwd":"123", "Owner":6434,
 * "Name":"A33381046", "OID":6696},
 * 
 * @author Administrator
 * 
 */
public class Device implements Serializable {

	private static final long serialVersionUID = -393878327871779543L;

	public boolean newTag = false;
	/** 云视通号 A361 */
	public String deviceNum = "";// A361
	/** 分组 A */
	public String group = "";// 分组
	/** 云视通号 361 */
	public int yst = 0;// 云通号
	/** 设备用户名 */
	public String deviceLoginUser = "";// abc
	/** 设备密码 */
	public String deviceLoginPwd = "";// 123
	public int deviceOwner = 0;// -1000代表演示点
	/** 设备昵称 */
	public String deviceName = "";// 昵称
	public int deviceOID = 0;// -1000代表演示点
	/** 设备IP */
	public String deviceLocalIp = "127.0.0.1";
	/** 设备端口 */
	public int deviceLocalPort = 9101;
	/** 通道列表集合 */
	public ArrayList<ConnPoint> pointList = null;// 通道列表集合
	public String deviceImgUrl = "";
	public ArrayList<String> deviceImageList = new ArrayList<String>();
	/** 通道数量 */
	public int devicePointCount = 0;// 一共有多少通道
	public int imageIndex = 0;
	// public String deleNum = "";//已假删除的字段号, 删除的通道号0,1,2,3

	/** 是否是设备，true是设备，false是通道 */
	public boolean isParent = true;// 是否是设备，true是设备，false是通道
	public int hasWifi = 0;// 设备是否带Wi-Fi
	public boolean useWifi = false; // 设备是否使用wifi
	public int deviceType = 0; // 设备类型
	public int onlineState = 0; // 是否在线 0.不在线 1.在线
	// public int connectType = 1; // 连接方式 1.云视通号 2.ip地址
	/** 连接方式标志位 0:云视通号连接 1:手动ip直连 */
	public int isDevice = 0;
	public boolean isBroadcast = false;

	public void getGroupYST() {
		StringBuffer groupSB = new StringBuffer();
		StringBuffer ystSB = new StringBuffer();
		if (!"".equalsIgnoreCase(deviceNum)) {
			for (int i = 0; i < deviceNum.length(); i++) {
				if (Character.isDigit(deviceNum.charAt(i))) {
					ystSB = ystSB.append(deviceNum.charAt(i));
				}
				if (Character.isLetter(deviceNum.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
					groupSB = groupSB.append(deviceNum.charAt(i));
				}
			}
		}

		group = groupSB.toString();
		if ("".equalsIgnoreCase(ystSB.toString())) {
			yst = 0;
		} else {
			yst = Integer.parseInt(ystSB.toString());
		}

	}

	// // device转成JVConnectInfo
	// public JVConnectInfo toJVConnectInfo() {
	// JVConnectInfo info = new JVConnectInfo(System.currentTimeMillis());
	// info.setAction(false);
	// // info.setBackLight(false);
	// info.setByUDP(true);
	// getGroupYST();
	// info.setCsNumber(yst);
	// // info.setHaveSearchChannel(1);
	// if ("".equalsIgnoreCase(deviceLocalIp)) {
	// info.setRemoteIp("");
	// } else {
	// info.setRemoteIp(deviceLocalIp);
	// }
	//
	// info.setPort(deviceLocalPort);
	// info.setUserName(deviceLoginUser);
	// info.setPasswd(deviceLoginPwd);
	// // info.setSavePasswd(true);
	// // info.setChannelCount(pointList.size());
	// info.setConnType(JVConst.JV_CONNECT_CS);
	// // info.setChannelRealCount(pointList.size());
	// info.setGroup(group);
	// info.setLocalTry(true);
	// info.setSrcName("1");
	// info.setNickName(deviceName);
	// info.setParent(true);
	// info.setHasWifi(hasWifi);
	// info.setUseWifi(useWifi);
	// info.setDeviceType(deviceType);
	// info.setOnlineState(onlineState);
	// info.setDevice(isDevice);
	// // ArrayList<JVChannel> childChannelList = new ArrayList<JVChannel>();
	// //
	// // if(null != pointList && 0 != pointList.size()){
	// // for(int i = 0 ; i < pointList.size() ; i++){
	// // JVChannel jvc = pointList.get(i).toJVChannel();
	// // if(null != jvc){
	// // childChannelList.add(jvc);
	// // }
	// // }
	// // }
	// // info.setDeleNum(deleNum);
	// // info.setChildChannelList(childChannelList);
	// return info;
	// }

}
