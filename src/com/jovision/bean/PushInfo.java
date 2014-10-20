package com.jovision.bean;

import java.io.Serializable;

public class PushInfo implements Serializable{
	
	private static final long serialVersionUID = -7060210544600464481L; 
	
	public int nMobileSysType;// 设备type
	public String strMobileID;// Imei
	public int nAppType;// 区分哪个软件，区分中性与非中性
	public String strAlert;//
	public String strCustomMsg;// "y":"B231723516","c":1,"time":"2013-11-08 21:43:01"
	public String strGUID;// 消息guid
	public String ystNum = "";// 云视通号
	public int coonNum = 0;// 通道
	public String alarmTime = "";// 报警时间
	public String deviceName = "";// 设备昵称
	public String deviceNickName = "";// 设备昵称
	public int alarmType = 0;// 报警类型
	public int alarmLevel = 1;// 报警级别
	public boolean newTag = false;
	public String pic = "";// 推送图片地址
	public String video = "";// 推送视频地址
	//[lkp]
	public int messageTag; // 报警标志，区分新旧报警，兼容旧的报警主控 4602老报警流程 4604 新报警流程
	public String timestamp; //时间戳
}
