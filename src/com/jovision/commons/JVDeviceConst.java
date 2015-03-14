package com.jovision.commons;

public class JVDeviceConst {
	/** 连接类型 云视通连接 */
	public static final int JV_CONNECT_CS = 1;
	/** 连接类型 IP连接 */
	public static final int JV_CONNECT_IP = 2;

	public static final int DEVICE_ALARTM_SWITCH = 0; // 安全防护
	public static final int DEVICE_ALARTM_TIME_SWITCH = 1; // 防护的时间
	public static final int DEVICE_TF_SWITCH = 2; // TF卡存储
	public static final int DEVICE_ALARTM_ANYTIME_SWITCH = 3;// 全监控
	public static final int DEVICE_ALARTM_BABY_SWITCH = 4; // baby模式
	public static final int DEVICE_NICK_NAME_SWITCH = 5; // 昵称修改

	public static final int DEVICE_SWITCH_OPEN = 1; // 打开
	public static final int DEVICE_SWITCH_CLOSE = 0; // 关闭

	public static final int DEVICE_SERVER_ONLINE = 1; // 设备服务器上上线
	public static final int DEVICE_SERVER_OFFLINE = 0; // 设备服务器上离线
	/**
	 * 2014_2_19
	 */
	public static final int TOKEN_LENGTH = 5;
	public static final String TOKEN_STR = "@#1$6";

	/* JK(JSON KEY) */
	/** pv = 1.0 */
	public static final String PROTO_VERSION = "1.0";
	/** pv = 2.0 */
	public static final String PROTO_VERSION_2 = "2.0";
	/** mid */
	public static final String JK_MESSAGE_ID = "mid";
	/** mt */
	public static final String JK_MESSAGE_TYPE = "mt";
	/** pv */
	public static final String JK_PROTO_VERSION = "pv";
	/** lpt */
	public static final String JK_LOGIC_PROCESS_TYPE = "lpt";
	/** rt */
	public static final String JK_RESULT = "rt";
	/** cfd */
	public static final String JK_CLINET_SFD = "cfd";
	/** cfdid */
	public static final String JK_CLINET_SFD_ID = "cfdid";
	/** sid */
	public static final String JK_SESSION_ID = "sid";

	public static final String JK_USERNAME = "username";
	public static final String JK_PASSWORD = "password";
	public static final String JK_NEW_PASSWORD = "newpass";
	public static final String JK_PEER_USERNAME = "pusername";
	public static final String JK_USER_OTHER_INFO = "uoi";
	public static final String JK_SECURITY_MAIL = "sm";

	public static final String JK_CLIENT_LOGIN_INFO = "cli";
	public static final String JK_CLIENT_LOGIN_PLATFORM = "clp";
	public static final String JK_LOGIN_MOBILE_ID = "lmi";
	public static final String JK_FEEDBACK = "fb";

	public static final String JK_USER_ONLINE_STATUS = "uls";

	public static final String JK_IM_SERVER_NO = "isn";
	public static final String JK_RELAY_MESSAGE = "rm";
	public static final String JK_RELAY_MESSAGE_GUID = "rmg";
	public static final String JK_RELAY_MESSAGE_TIMESTAMP = "rmt";

	public static final String JK_P2RELAY_MESSAGE_TYPE = "p2rmt";

	public static final String JK_ONLINE_SERVER_NO = "osn";
	public static final String JK_ONLINE_SERVER_FD = "osf";
	public static final String JK_ONLINE_SERVER_FD_ID = "osfi";

	public static final String JK_CREATE_TIME = "ct";

	public static final String JK_PUSH_MESSAGE_TYPE = "pmt";

	/* 设备服务相关 */
	public static final String JK_DEVICES_CHANGE = "dc";
	public static final String JK_DEVICE_LIST = "dlist";
	public static final String JK_CHANNEL_LIST = "clist";
	public static final String JK_DEVICE_INFO = "dinfo";
	public static final String JK_DEVICE_GUID = "dguid";
	public static final String JK_DEVICE_SOFT_VERSION = "dsv";
	public static final String JK_DEVICE_SUB_TYPE_INT = "dstypeint";
	public static final String JK_DEVICE_TYPE = "dtype";
	public static final String JK_DEVICE_USERNAME = "dusername";
	public static final String JK_DEVICE_PASSWORD = "dpassword";
	public static final String JK_DEVICE_NAME = "dname";
	public static final String JK_DEVICE_CHANNEL_NAME = "dcname";
	public static final String JK_DEVICE_CHANNEL_NO = "dcn";
	public static final String JK_DEVICE_CHANNEL_SUM = "dcs";
	public static final String JK_DEVICE_IP = "dip";
	public static final String JK_DEVICE_PORT = "dport";
	public static final String JK_DEVICE_NET_STATE = "dnst";
	public static final String JK_NET_STORAGE_SWITCH = "netss";
	public static final String JK_TF_STORAGE_SWITCH = "tfss";
	public static final String JK_ALARM_SWITCH = "aswitch";
	public static final String JK_ALARM_VIDEO_FTP = "avftp";
	public static final String JK_ALARM_SNAP_FTP = "asnapftp";
	public static final String JK_ALARM_FTP_ACC = "aftpacc";
	public static final String JK_ALARM_FTP_PWD = "aftppwd";
	public static final String JK_ALARM_TIME = "atime";
	public static final String JK_PIC_FTP_BIG = "dpicb";
	public static final String JK_PIC_FTP_SMALL = "dpics";
	public static final String JK_PIC_FTP_ACC = "dpicacc";
	public static final String JK_PIC_FTP_PWD = "dpicpwd";
	public static final String JK_PIC_UPLOAD_TIMEING = "dpicut";
	public static final String JK_VIDEO_FLUENCY = "dvfluency";
	public static final String JK_VIDEO_LINK_TYPE = "dvlt";
	public static final String JK_DEVICE_VIDEO_USERNAME = "dvusername";
	public static final String JK_DEVICE_VIDEO_PASSWORD = "dvpassword";

	public static final String JK_DEVICE_DEMO_USERNAME = "dusername";
	public static final String JK_DEVICE_DEMO_PASSWORD = "dpassword";

	public static final String JK_DEVICE_VIDEO_IP = "dvip";
	public static final String JK_DEVICE_VIDEO_PORT = "dvport";
	public static final String JK_DEVICE_WIFI_FLAG = "dwifi";
	public static final String JK_DEVICE_VIDEO_TCP = "dvtcp";

	public static final String JK_DEVICE_TEMPERATURE = "dtem";
	public static final String JK_DEVICE_HUMIDNESS = "dhum";

	public static final String JK_DEVICE_TIMESTAMP = "dts";

	public static final String JK_DEVICES_ONLINE_STATUS = "dsls";
	public static final String JK_ONLINE_STATUS = "ols";

	public static final String JK_DEVICES_PIC = "dspic";

	public static final String JK_DEVICE_HUMITURE_LIST = "dhlist";
	public static final String JK_DEVICE_HUMITURE_DATE = "dhdate";
	public static final String JK_DEVICE_HUMITURE_HOUR = "dhour";
	public static final String JK_DEVICE_HUMITURE_NUM = "dhnum";
	public static final String JK_DEVICE_HUMITURE_RATIO = "dhratio";

	public static final String JK_DEVICE_ENV_SCORE = "descore"; // 综合环境健康指数评分
	public static final String JK_DEVICE_HUMITURE_SCORE = "dhscore"; // 温湿度评分
	public static final String JK_DEVICE_HUMITURE_TOP = "dhtop";
	public static final String JK_DEVICE_HUMITURE_LAST_SCORE = "dhlscore";
	public static final String JK_DEVICE_HUMITURE_LAST_TOP = "dhltop";

	public static final String JK_DEVICE_BABY_MODE = "dbbm";
	public static final String JK_DEVICE_FULL_ALARM_MODE = "dfam";

	public static final String JK_DEVICE_IM_ONLINE_STATUS = "dimols";

	public static final String JK_DEVICE_NAME_RESULT = "dnamers";
	public static final String JK_NET_STORAGE_SWITCH_RESULT = "netssrs";
	public static final String JK_TF_STORAGE_SWITCH_RESULT = "tfssrs";
	public static final String JK_ALARM_SWITCH_RESULT = "aswitchrs";
	public static final String JK_ALARM_VIDEO_FTP_RESULT = "avftprs";
	public static final String JK_ALARM_SNAP_FTP_RESULT = "asnapftprs";
	public static final String JK_ALARM_FTP_ACC_RESULT = "aftpaccrs";
	public static final String JK_ALARM_FTP_PWD_RESULT = "aftppwdrs";
	public static final String JK_ALARM_TIME_RESULT = "atimers";
	public static final String JK_PIC_FTP_BIG_RESULT = "dpicbrs";
	public static final String JK_PIC_FTP_SMALL_RESULT = "dpicsrs";
	public static final String JK_PIC_FTP_ACC_RESULT = "dpicaccrs";
	public static final String JK_PIC_FTP_PWD_RESULT = "dpicpwdrs";
	public static final String JK_PIC_UPLOAD_TIMEING_RESULT = "dpicutrs";
	public static final String JK_VIDEO_FLUENCY_RESULT = "dvfluencyrs";
	public static final String JK_DEVICE_SUB_TYPE = "dstype";

	public static final String JK_ACCOUNT_LIVE_INFO = "ali";
	public static final String JK_ALARM_FLAG = "af";

	public static final String JK_CLIENT_VERSION = "cv";
	public static final String JK_UPDATE_FILE_INFO = "ufi";
	public static final String JK_FILE_VERSION = "fv";
	public static final String JK_FILE_URL = "fu";
	public static final String JK_FILE_SIZE = "fs";
	public static final String JK_FILE_DESCRIPTION = "fd";
	public static final String JK_FILE_CHECKSUM = "fc";

	// public static final String JK_DEVICE_SUB_TYPE = "dstype";
	//
	// public static final String JK_DEVICE_SOFT_VERSION = "dsv";
	// public static final String JK_DEVICE_BABY_MODE = "dbbm";
	// public static final String JK_DEVICE_FULL_ALARM_MODE = "dfam";
	//
	public static final String JK_DEVICE_BABY_MODE_RESULT = "dbbmrs";
	public static final String JK_DEVICE_FULL_ALARM_MODE_RESULT = "dfamrs";

	public static final String JK_DEVICE_HUMITURE_ASSESSMENT = "dhass";

	public static final String JK_UPGRADE_FILE_VERSION = "ufver";
	public static final String JK_UPGRADE_FILE_URL = "ufurl";
	public static final String JK_UPGRADE_FILE_DESCRIPTION = "ufdes";
	public static final String JK_UPGRADE_FILE_SIZE = "ufsize";
	public static final String JK_UPGRADE_FILE_CHECKSUM = "ufc";
	public static final String JK_UPGRADE_DOWNLOAD_STEP = "udstep";
	public static final String JK_UPGRADE_WRITE_STEP = "uwstep";
	public static final String JK_DEVICE_RESET_FLAG = "drf";
	public static final String JK_DEVICE_VERIFY = "dverify";

	public static final int YST_INDEX_SEND_ERROR = -29;// 云视通检索服务器通信异常。

	public static final int GET_USER_DETAIL_INFO = 1016;
	public static final int GET_USER_DETAIL_INFO_RESPONSE = 1017;

	public static final int ACTIVE_SERVER_PUSH = 3010;
	public static final int ACTIVE_SERVER_PUSH_RESPONSE = 3011;

	public static final int PUSH_DEVICE_UPDATE_CMD = 3012;
	public static final int PUSH_DEVICE_UPDATE_CMD_RESPONSE = 3013;

	public static final int PUSH_DEVICE_CANCEL_CMD = 3014;
	public static final int PUSH_DEVICE_CANCEL_CMD_RESPONSE = 3015;

	public static final int GET_UPDATE_DOWNLOAD_STEP = 3016;
	public static final int GET_UPDATE_DOWNLOAD_STEP_RESPONSE = 3017;

	public static final int GET_UPDATE_WRITE_STEP = 3018;
	public static final int GET_UPDATE_WRITE_STEP_RESPONSE = 3019;

	public static final int PUSH_DEVICE_REBOOT_CMD = 3020;
	public static final int PUSH_DEVICE_REBOOT_CMD_RESPONSE = 3021;

	public static final int RELAY_DEVICE_UPDATE_CMD = 4304;
	public static final int RELAY_DEVICE_UPDATE_CMD_RESULT = 4305;

	public static final int RELAY_DEVICE_CANCEL_CMD = 4306;
	public static final int RELAY_DEVICE_CANCEL_CMD_RESULT = 4307;

	public static final int RELAY_GET_DEVICE_UPDATE_STEP = 4308;
	public static final int RELAY_GET_DEVICE_UPDATE_STEP_RESULT = 4309;

	public static final int RELAY_DEVICE_REBOOT_CMD = 4310;
	public static final int RELAY_DEVICE_REBOOT_CMD_RESULT = 4311;

	public static final int GET_DEVICE_UPDATE_INFO = 2033;
	public static final int GET_DEVICE_UPDATE_INFO_RESPONSE = 2034;
	public static final int PUSH_DEVICE_UPDATE_CMD_RESULT = 2211;
	public static final int PUSH_DEVICE_UPDATE_CMD_RESULT_RESPONSE = 2212;

	public static final int PUSH_DEVICE_CANCEL_CMD_RESULT = 2213;
	public static final int PUSH_DEVICE_CANCEL_CMD_RESULT_RESPONSE = 2214;

	public static final int GET_DEVICE_UPDATE_STEP_RESULT = 2215;
	public static final int GET_DEVICE_UPDATE_STEP_RESULT_RESPONSE = 2216;

	/** Message Type */
	public static final int IS_USER_EXIST = 997;
	public static final int IS_USER_EXIST_RESPONSE = 998;
	public static final int USER_REGISTER = 999;
	public static final int USER_REGISTER_RESPONSE = 1000;
	public static final int LOGIN = 1001;
	public static final int LOGIN_RESPONSE = 1002;
	public static final int LOGOUT = 1003;
	public static final int LOGOUT_RESPONSE = 1004;
	public static final int MODIFY_USERPASS = 1073;
	public static final int MODIFY_USERPASS_RESPONSE = 1074;
	public static final int RESET_PASSWORD = 1085;
	public static final int RESET_PASSWORD_RESPONSE = 1086;

	/** 上线服务 */
	// public static final int USER_ONLINE = 1201;
	// public static final int USER_ONLINE_RESPONSE = 1202;
	// public static final int GET_LIVE_STATUS = 1301;
	// public static final int GET_LIVE_STATUS_RESPONSE = 1302;
	// public static final int SET_ONLINE_STATUS = 1221;
	// public static final int SET_ONLINE_STATUS_RESPONSE = 1222;
	public static final int SEND_RESET_PASSWORD_MAIL = 1083;
	public static final int SEND_RESET_PASSWORD_MAIL_RESPONSE = 1084;
	public static final int RELAY_REQUEST = 3001;

	public static final int USER_ONLINE = 3000;
	public static final int USER_ONLINE_RESPONSE = 3001;
	public static final int GET_LIVE_STATUS = 3002;
	public static final int GET_LIVE_STATUS_RESPONSE = 3003;
	public static final int SET_ONLINE_STATUS = 3004;
	public static final int SET_ONLINE_STATUS_RESPONSE = 3005;

	public static final int PUSH_DEVICE_MODIFY_INFO = 3006;
	public static final int PUSH_DEVICE_MODIFY_INFO_RESPONSE = 3007;

	public static final int CANCEL_SERVER_PUSH = 3008;
	public static final int CANCEL_SERVER_PUSH_RESPONSE = 3009;

	// public static final int NOTIFY_OFFLINE = 3102;
	// public static final int RELAY_NOTIFY_OFFLINE = 3103;
	public static final int REG_MAIL = 3301;
	public static final int REG_MAIL_RESPONSE = 3302;

	/** 设备基础信息服务 */
	public static final int DEVICE_REGISTER = 2001;
	public static final int DEVICE_REGISTER_RESPONSE = 2002;

	public static final int GET_USER_DEVICES = 2003;
	public static final int GET_USER_DEVICES_RESPONSE = 2004;

	public static final int GET_DEVICE_INFO = 2005;
	public static final int GET_DEVICE_INFO_RESPONSE = 2006;

	public static final int MODIFY_DEVICE_CONF_INFO = 2007;
	public static final int MODIFY_DEVICE_CONF_INFO_RESPONSE = 2008;

	public static final int GET_DEVICE_ONLINE_STATE = 2009;
	public static final int GET_DEVICE_ONLINE_STATE_RESPONSE = 2010;

	public static final int GET_DEVICE_PIC = 2011;
	public static final int GET_DEVICE_PIC_RESPONSE = 2012;
	public static final int PUSH_DEVICE_MODIFY_PASSWORD = 3022;
	public static final int PUSH_DEVICE_MODIFY_PASSWORD_RESPONSE = 3023;
	public static final int MODIFY_DEVICE_PASSWORD = 2035;
	public static final int MODIFY_DEVICE_PASSWORD_RESPONSE = 2036;
	/** 添加通道Request */
	public static final int ADD_DEVICE_CHANNEL = 2039;
	/** 添加通道Response */
	public static final int ADD_DEVICE_CHANNEL_RESPONSE = 2040;
	/** 删除通道Request */
	public static final int DELETE_DEVICE_CHANNEL = 2041;
	/** 删除通道Response */
	public static final int DELETE_DEVICE_CHANNEL_RESPONSE = 2042;
	/** 获取用户通道列表 */
	public static final int GET_USER_CHANNELS = 2049;
	/** 获取设备通道列表Request */
	public static final int GET_DEVICE_CHANNEL = 2043;
	/** 获取通道列表Response */
	public static final int GET_DEVICE_CHANNEL_RESPONSE = 2044;
	/** 修改通道名Request */
	public static final int MODIFY_DEVICE_CHANNEL_NAME = 2045;
	/** 修改通道名Response */
	public static final int MODIFY_DEVICE_CHANNEL_NAME_RESPONSE = 2046;

	/** 获取演示点 Request */
	public static final int GET_DEMO_POINT = 2057;
	/** 获取演示点 Response */
	public static final int GET_DEMO_POINT_RESPONSE = 2058;

	/** 获取演示点2 Request */
	public static final int GET_DEMO_POINT_SERVER = 2059;
	/** 获取演示点2 Response */
	public static final int GET_DEMO_POINT_SERVER_RESPONSE = 2060;
	public static final String JK_DEMO_POINT_SERVER = "dps";

	/** 软件类型 */
	public static final String JK_CUSTOM_TYPE = "custom_type";

	/**
	 * 2014-2-20 新增消息类型：
	 */
	public static final int MODIFY_DEVICE_INFO_VIDEO_LINK = 2013;
	public static final int MODIFY_DEVICE_INFO_VIDEO_LINK_RESPONSE = 2014;

	public static final int USER_BIND_DEVICE = 2015;
	public static final int USER_BIND_DEVICE_RESPONSE = 2016;

	public static final int USER_REMOVE_BIND_DEVICE = 2017;
	public static final int USER_REMOVE_BIND_DEVICE_RESPONSE = 2018;

	public static final int GET_USER_DEVICE_INFO = 2019;
	public static final int GET_USER_DEVICE_INFO_RESPONSE = 2020;

	/** 设备在线服务 */
	public static final int DEVICE_ONLINE = 2201;
	public static final int DEVICE_ONLINE_RESPONSE = 2202;

	public static final int DEVICE_HEARTBEAT = 2203;
	public static final int DEVICE_HEARTBEAT_RESPONSE = 2204;

	public static final int DEVICE_OFFLINE = 2205;
	public static final int DEVICE_OFFLINE_RESPONSE = 2206;

	public static final int DEVICE_REPORT_HUMITURE = 2207;
	public static final int DEVICE_REPORT_HUMITURE_RESPONSE = 2208;

	public static final int PUSH_DEVICE_MODIFY_RESULT = 2209;
	public static final int PUSH_DEVICE_MODIFY_RESULT_RESPONSE = 2210;

	public static final int GET_DEVICE_HUMITURE_STAT = 2021;
	public static final int GET_DEVICE_HUMITURE_STAT_RESPONSE = 2022;

	public static final int GET_DEVICE_HUMITURE_ONTIME = 2023;
	public static final int GET_DEVICE_HUMITURE_ONTIME_RESPONSE = 2024;
	public static final int GET_USER_DEVICES_STATUS_INFO = 2025;
	public static final int GET_USER_DEVICES_STATUS_INFO_RESPONSE = 2026;
	public static final int GET_DEVICE_HUMITURE_SCORE = 2027;
	public static final int GET_DEVICE_HUMITURE_SCORE_RESPONSE = 2028;
	public static final int GET_DEVICE_USERNAMES = 2029;
	public static final int GET_DEVICE_USERNAMES_RESPONSE = 2030;

	public static final int MODIFY_DEVICE_INFO_ADVANCED = 2031;
	public static final int MODIFY_DEVICE_INFO_ADVANCED_RESPONSE = 2032;

	public static final int JK_GET_CLOUD_STORAGE_INFO = 5208;
	/** 绑定wifi请求 */
	public static final int MODIFY_DEVICE_WIFI_FLAG = 2047;
	/** 绑定wifi响应 */
	public static final int MODIFY_DEVICE_WIFI_FLAG_RESPONSE = 2048;

	public static final int NOTIFY_OFFLINE = 4301;
	public static final int RELAY_DEVICE_MODIFY_INFO = 4302;
	public static final int RELAY_DEVICE_MODIFY_RESULT = 4303;
	public static final int GET_ACCOUNT_LIVE_INFO = 4600;
	public static final int GET_ACCOUNT_LIVE_INFO_RESPONSE = 4601;
	public static final int SEND_MESSAGE_TO_USER = 4602;
	public static final int SEND_MESSAGE_TO_USER_RESPONSE = 4603;

	/** LogicProcessNo */
	public static final int ACCOUNT_BUSINESS_PROCESS = 0;
	public static final int DEV_INFO_PRO = 1;
	public static final int IM_SERVER_DIRECT = 6;
	public static final int IM_SERVER_RELAY = 7;
	public static final int IM_SERVER_RELAY_REQUEST = 8;
	public static final int IM_DEV_DIRECT = 9;
	public static final int ALARM_SERVER_RELAY = 10;
	public static final int VAS_PROCESS = 13;
	public static final int SUCCESS = 0;
	public static final int FAILED = -1;

	public static final int USER_HAS_EXIST = 2;
	public static final int USER_NOT_EXIST = 3;
	public static final int PASSWORD_ERROR = 4;
	public static final int SESSION_NOT_EXSIT = 5;
	public static final int SQL_NOT_FIND = 6;
	public static final int PTCP_HAS_CLOSED = 7;

	/** DeviceRegisterResponse */
	public static final int DEVICE_HAS_EXIST = 8;
	public static final int DEVICE_NOT_EXIST = 9;
	public static final int DEVICE_NOT_BIND = 10;

	public static final int GENERATE_PASS_ERROR = -2;
	public static final int REDIS_OPT_ERROR = -3;
	public static final int MY_SQL_ERROR = -4;
	public static final int REQ_RES_TIMEOUT = -5;
	public static final int CONN_OTHER_ERROR = -6;
	public static final int CANT_CONNECT_SERVER = -7;
	public static final int JSON_INVALID = -8;
	public static final int REQ_RES_OTHER_ERROR = -9;
	public static final int JSON_PARSE_ERROR = -10;
	public static final int SEND_MAIL_FAILED = -11;
	public static final int DEVICE_HAS_VERIFY = -12;// 设备已经被激活
	public static final int DEVICE_NOT_ONLINE = -14;// 所有操作设备相关的业务如果设备不在线立即返回

	public static final int OTHER_ERROR = -1000;

	/** InitDeviceActivity */
	public static final int INIT_DEVICE_SUCCESS = 0;
	public static final int INIT_DEVICE_FAILED = 1;
	public static final int INIT_DEVICE_OFFLINE = 2;
	public static final int INIT_DEVICE_VERIFY = 3;

	/** EditDeviceActivity */
	public static final int EDIT_DEVICE_FLAG = 0;// 修改设备
	public static final int ACTIVE_DEVICE_FLAG = 1;// 激活设备
	public static final int DEVICE_VERIFY_NOT_TRUE = 100;// 输入的用户名或密码与设备的不一致
	public static final int DEVICE_NOT_EXISTS = 101;// 输入的用户名或密码与设备的不一致

	/** 2014-11-20广告新增 */
	public static final String JK_TERMINAL_TYPE = "tt";
	public static final String JK_PRODUCT_TYPE = "prot";
	public static final String JK_AD_VERSION = "adver";
	public static final String JK_AD_INFO = "adinfo";
	public static final String JK_AD_NO = "adno";
	public static final String JK_AD_URL = "adurl";
	public static final String JK_AD_LINK = "adl";
	public static final String JK_AD_URL_EN = "adurlen";
	public static final String JK_AD_LINK_EN = "adlen";
	public static final String JK_AD_URL_ZHT = "adurlzht";
	public static final String JK_AD_LINK_ZHT = "adlzht";
	public static final String JK_AD_DESP = "addesp";

	public static final int AD_PUBLISH_PROCESS = 12;
	public static final int GET_AD_INFO = 5500;
	public static final int GET_AD_INFO_RESPONSE = 5501;

	/** 2015-1-4 视频广场新增 */
	// 增值服务
	public static final String JK_STREAMING_MEDIA_SERVER = "smsrv";
	public static final String JK_STREAMING_MEDIA_TIME = "smt";
	public static final String JK_STREAMING_MEDIA_CHANNELS = "smcs";
	public static final String JK_STREAMING_MEDIA_FLAG = "sm";
	public static final String JK_STREAMING_MEDIA_SHARE = "sms";
	public static final String JK_CLOUD_STORAGE_FLAG = "csf";
	public static final String JK_CLOUD_STORAGE_TIME = "cst";
	public static final String JK_CLOUD_STORAGE_HOST = "cshost";
	public static final String JK_CLOUD_STORAGE_ID = "csid";
	public static final String JK_CLOUD_STORAGE_KEY = "cskey";
	public static final String JK_CLOUD_STORAGE_SPACE = "csspace";
	public static final String JK_SHARED_SERVER = "ssrv";
	public static final String JK_SHARE_CHANNELS = "scs";
	public static final String JK_RTMP_PORT = "rtmp";
	public static final String JK_HLS_PORT = "hls";

	/** 手机欢迎界面图片 */
	public static final int GET_PORTAL = 5502;
	public static final int GET_PORTAL_RESPONSE = 5503;

	public static final String JK_PORTAL_VERSION = "porver";
	public static final String JK_PORTAL = "por";
	public static final String JK_PORTAL_EN = "poren";
	public static final String JK_PORTAL_ZHT = "porzht";

	/** 软件系统消息接口 */
	public static final int GET_PUBLISH_INFO = 5504;
	public static final int AD_PUBLISH_PROCESS_RESPONSE = 5505;
	public static final String JK_LANGUAGE_TYPE = "langt";
	public static final String JK_PUB_INDEX_START = "pistart";
	public static final String JK_PUB_COUNT = "pcount";
	public static final String JK_PUB_TIME = "ptime";
	public static final String JK_PUB_INFO = "pinfo";
	public static final String JK_PUB_LIST = "plist";

	/** 自动更新 */
	public static final String JK_APP_CURRENT_VERSION = "crtver";
	public static final String JK_APP_VERSION = "appver";
	public static final String JK_APP_VERSION_FULL = "appfullver";
	public static final String JK_APP_VERSION_URL = "appverurl";
	public static final String JK_APP_VERSION_DESC = "appverdesc";
	public static final String JK_APP_CLIENT_TYPE = "appclit";

	public static final int UPDATE_PROCESS = 4;
	public static final int SOFT_UPDATE_REQUEST = 5000;
	public static final int SOFT_UPDATE_RESPONSE = 5001;

	/** 网页功能配置接口 */
	public static final int GET_WEBFUNC_INFO = 5506;
	public static final String JK_WEB_DEMO_URL = "demourl";
	public static final String JK_WEB_DEMO_FLAG = "demof";
	public static final String JK_WEB_CUST_URL = "custurl";
	public static final String JK_WEB_CUST_FLAG = "custf";
	public static final String JK_WEB_STAT_URL = "staturl";
	public static final String JK_WEB_STAT_FLAG = "statf";

	public static final String JK_WEB_BBS_URL = "bbsurl";
	public static final String JK_WEB_BBS_FLAG = "bbsf";
	public static final String JK_WEB_GCS_URL = "gcsurl";
	public static final String JK_WEB_GCS_FLAG = "gcsf";

}
