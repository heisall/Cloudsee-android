package com.jovision.bean;

import java.io.Serializable;

import com.jovision.views.Image;

//通道
/**
 * {"DeviceID":6696, "PointNum":1, "Owner":0, "Name":"A33381046_1", "OID":42951
 * 
 * @author Administrator
 * 
 */
public class ConnPoint implements Serializable {
	public int deviceID = 0;
	public int pointNum = 0;// 通道号，从1开始
	public int pointOwner = 0;
	public String pointName = "";
	public int pointOID = 0;
	// public String Type = "ConnPoint";

	public String pointlat = "";// 纬度
	public String pointlng = "";// 经度
	public Image connImage = new Image();

	public int ystNum;// 父 云视通号 361
	public String group;// 父云视通号组 A

	public boolean isParent = true;// 是否是设备，true是设备，false是通道

	// // 通道转成JVChannel
	// public JVChannel toJVChannel() {
	// JVChannel jvc = new JVChannel();
	// jvc.setChannelName(pointName);
	// jvc.setChannelNum(pointNum);
	// jvc.setYstNum(ystNum);
	// return jvc;
	// }
	//
	// // 通道转成JVConnectInfo
	// public JVConnectInfo toJVConnectInfo() {
	// JVConnectInfo info = new JVConnectInfo(System.currentTimeMillis());
	// info.setGroup(group);
	// info.setCsNumber(ystNum);
	// info.setParent(false);
	// info.setChannel(pointNum);
	// info.setNickName(pointName);
	//
	// return info;
	// }

}
