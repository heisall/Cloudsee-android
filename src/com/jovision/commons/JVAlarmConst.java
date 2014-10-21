package com.jovision.commons;

public class JVAlarmConst {

	public static final int ALARM_ON = 0;// 报警开
	public static final int ALARM_OFF = 1;// 报警关

	/* JK(JSON KEY) 宏定义 */
	public static final String JK_ALARM_MID = "mid";
	public static final String JK_ALARM_ACCOUNT = "accountname";
	public static final String JK_ALARM_GUID = "alarmguid";
	public static final String JK_ALARM_CLOUDNUM = "cloudnum";
	public static final String JK_ALARM_CLOUDNAME = "cloudname";// 昵称
	public static final String JK_ALARM_CLOUDCHN = "cloudchn";
	public static final String JK_ALARM_ALARMTYPE = "alarmtype";
	public static final String JK_ALARM_ALARMTIME = "alarmtime";
	public static final String JK_ALARM_ALARMLEVEL = "alarmlevel";// 2014.03.08
																	// 报警级别
	public static final String JK_ALARM_PICURL = "picurl";
	public static final String JK_ALARM_VIDEOURL = "videourl";
	public static final String JK_ALARM_SEARCHINDEX = "index";
	public static final String JK_ALARM_SEARCHCOUNT = "count";
	public static final String JK_ALARM_OPTFLAG = "flag";
	public static final String JK_ALARM_LIST = "list";
	/*----------新报警流程key值-------------*/
	public static final String JK_ALARM_NEW_GUID = "aguid";
	public static final String JK_ALARM_NEW_CLOUDNUM = "dguid";
	public static final String JK_ALARM_NEW_CLOUDNAME = "dname";// 昵称
	public static final String JK_ALARM_NEW_CLOUDCHN = "dcn";
	public static final String JK_ALARM_NEW_ALARMTYPE = "atype";
	public static final String JK_ALARM_NEW_ALARMTIME = "ats";
	public static final String JK_ALARM_NEW_PICURL = "apic";
	public static final String JK_ALARM_NEW_VIDEOURL = "avd";
	public static final String JK_ALARM_NEW_MID = "mid";
	public static final String JK_ALARM_NEW_AINFO = "ainfo";
	public static final String JK_ALARM_NEW_RT = "rt";// 0:ok; -10:请求格式错误;
														// -3:缓存操作错误；-1:其它错误
	public static final String JK_ALARM_NEW_ALARM_ATS = "ats";
	public static final String JK_ALARM_NEW_ALARM_LPT = "lpt";
	public static final String JK_ALARM_NEW_ALARM_MT = "mt";
	public static final String JK_ALARM_NEW_ALARM_PV = "pv";
	public static final String JK_ALARM_NEW_ALARM_AISTART = "aistart";
	public static final String JK_ALARM_NEW_ALARM_AISTOP = "aistop";
	/* 报警服务器的消息类型 */
	public static final int MID_RESPONSE_PUSHALARM = 1000; /* 告警服务器向客户端推送报警信息 */
	public static final int MID_REQUEST_ALARMPICURL = 1001; /* 客户端获取报警图片的url地址 */
	public static final int MID_RESPONSE_ALARMPICURL = 1002; /* 服务器向客户端发送报警图片的url地址 */
	public static final int MID_REQUEST_ALARMVIDEOURL = 1003; /* 客户端获取报警视频的url地址 */
	public static final int MID_RESPONSE_ALARMVIDEOURL = 1004; /* 服务器向客户端发送报警视频的url地址 */
	public static final int MID_REQUEST_ALARMHISTORY = 1005; /* 客户端获取报警的历史记录 */
	public static final int MID_RESPONSE_ALARMHISTORY = 1006; /* 服务器向客户端发送报警的历史记录 */
	public static final int MID_REQUEST_REMOVEALARM = 1007; /* 客户端发送删除报警信息 */
	public static final int MID_RESPONSE_REMOVEALARM = 1008; /* 服务器向客户端发发送删除报警信息的结果 */
}
