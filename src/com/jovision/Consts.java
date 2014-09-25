package com.jovision;

import java.io.File;

import android.os.Environment;

public class Consts {

	public static final String SD_CARD_PATH = Environment
			.getExternalStorageDirectory().getPath() + File.separator;

	public static final String LOG_PATH = SD_CARD_PATH + "CSL";
	public static final String ACCOUNT_PATH = SD_CARD_PATH + "ACCOUNT";

	public static final String APP_NAME = "CloudSEE";
	public static final String CAPTURE_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "capture" + File.separator;
	public static final String VIDEO_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "video" + File.separator;

	public static final String DB_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "db" + File.separator;

	public static final String DB_NAME = "CloudSEE.db";

	public static final String TAG_APP = "CS_APP";
	public static final String TAG_UI = "CS_UI";
	public static final String TAG_PLAY = "CS_PLAY";
	public static final String TAG_JY = "CS_JY";

	public static final int CALL_CONNECT_CHANGE = 0xA1;
	public static final int CALL_NORMAL_DATA = 0xA2;
	public static final int CALL_CHECK_RESULT = 0xA3;
	public static final int CALL_CHAT_DATA = 0xA4;
	public static final int CALL_TEXT_DATA = 0xA5;
	public static final int CALL_DOWNLOAD = 0xA6;
	public static final int CALL_PLAY_DATA = 0xA7;
	public static final int CALL_LAN_SEARCH = 0xA8;
	public static final int CALL_FRAME_I_REPORT = 0xA9;
	public static final int CALL_STAT_REPORT = 0xAA;
	public static final int CALL_GOT_SCREENSHOT = 0xAB;
	public static final int CALL_PLAY_DOOMED = 0xAC;
	public static final int CALL_PLAY_AUDIO = 0xAD;
	public static final int CALL_QUERY_DEVICE = 0xAE;

	public static final int CHANNEL_JY = 5555;

	public static final int DEVICE_TYPE_UNKOWN = -1;
	public static final int DEVICE_TYPE_DVR = 0x01;
	public static final int DEVICE_TYPE_950 = 0x02;
	public static final int DEVICE_TYPE_951 = 0x03;
	public static final int DEVICE_TYPE_IPC = 0x04;
	public static final int DEVICE_TYPE_NVR = 0x05;

	public static final int JAE_ENCODER_SAMR = 0x00;
	public static final int JAE_ENCODER_ALAW = 0x01;
	public static final int JAE_ENCODER_ULAW = 0x02;

	public static final int TEXT_REMOTE_CONFIG = 0x01;
	public static final int TEXT_AP = 0x02;
	public static final int TEXT_GET_STREAM = 0x03;

	public static final int FLAG_WIFI_CONFIG = 0x01;
	public static final int FLAG_WIFI_AP = 0x02;
	public static final int FLAG_BPS_CONFIG = 0x03;
	public static final int FLAG_CONFIG_SCCUESS = 0x04;
	public static final int FLAG_CONFIG_FAILED = 0x05;
	public static final int FLAG_CONFIG_ING = 0x06;
	public static final int FLAG_SET_PARAM = 0x07;

	public static final int EX_WIFI_CONFIG = 0x0A;

	public static final int ARG1_PLAY_BAD = 0x01;

	public static final int BAD_STATUS_NOOP = 0x00;
	public static final int BAD_STATUS_OMX = 0x01;
	public static final int BAD_STATUS_FFMPEG = 0x02;
	public static final int BAD_STATUS_OPENGL = 0x03;
	public static final int BAD_STATUS_AUDIO = 0x04;
	public static final int BAD_STATUS_DECODE = 0x05;
	public static final int PLAYBACK_DONE = 0x06;

	public static final int BAD_SCREENSHOT_NOOP = 0x00;//
	public static final int BAD_SCREENSHOT_INIT = 0x01;//
	public static final int BAD_SCREENSHOT_CONV = 0x02;//
	public static final int BAD_SCREENSHOT_OPEN = 0x03;//

	public static final int WHAT_DUMMY = 0x04;

	public static final String IPC_DEFAULT_USER = "jwifiApuser";
	public static final String IPC_DEFAULT_PWD = "^!^@#&1a**U";
	public static final String IPC_DEFAULT_IP = "10.10.0.1";
	public static final int IPC_DEFAULT_PORT = 9101;

	/** 登录方法 */
	public static final int LOGIN_FUNCTION = 0xB1;
	/** 注册方法 */
	public static final int REGIST_FUNCTION = 0xB2;
	/** 获取设备列表方法 */
	public static final int GET_DEVICE_LIST_FUNCTION = 0xB3;
	/** 获取设备列表方法 */
	public static final int GUID_FUNCTION = 0xB4;

	/** tab页 onstart */
	public static final int TAB_ONRESUME = 1;
	/** JVMyDeviceFragmentActivity */
	public static final int DEVICE_GETDATA_SUCCESS = 2;// 设备加载成功--
	public static final int DEVICE_GETDATA_FAILED = 3;// 设备加载失败--
	public static final int DEVICE_ITEM_CLICK = 4;// 设备单击事件--
	public static final int DEVICE_ITEM_LONG_CLICK = 5;// 设备长按事件--
	public static final int DEVICE_ITEM_DEL_CLICK = 6;// 设备删除按钮事件--
	public static final int DEVICE_EDIT_CLICK = 7;// 设备编辑按钮事件--

	public static final int DEVICE_NO_DEVICE = 19;// 暂无设备--

	/** ChannelFragment */
	public static final int CHANNEL_ITEM_CLICK = 20;// 通道单击事件--
	public static final int CHANNEL_ITEM_LONG_CLICK = 21;// 通道长按事件--
	public static final int CHANNEL_ITEM_DEL_CLICK = 22;// 通道删除按钮事件--
	public static final int CHANNEL_ADD_CLICK = 23;// 通道添加事件--
	public static final int CHANNEL_EDIT_CLICK = 24;// 通道删除按钮事件--

	/** ChannelFragment onResume */
	public static final int CHANNEL_FRAGMENT_ONRESUME = 40;// ChannelFragment
															// onResume

	/** 引导界面滑屏 */
	public static final int GUID_PAGE_SCROLL = 500;// --

	/*********************** 以下是状态变量key的声明 ********************************/

	/** 账号sdk是否初始化的key */

	/** 云视通sdk是否初始化的key */
	public static final String KEY_SHOW_GUID = "ShowGuide";
	public static final String KEY_INIT_ACCOUNT_SDK = "initAccountSdk";
	/** 云视通sdk是否初始化的key */
	public static final String KEY_INIT_CLOUD_SDK = "initCloudSdk";
	/** 用户名key */
	public static final String KEY_USERNAME = "userName";
	/** 密码key */
	public static final String KEY_PASSWORD = "passWord";
	/** IMEIkey */
	public static final String IMEI = "IMEI";
	/** 公共版本key */
	public static final String NEUTRAL_VERSION = "NEUTRAL_VERSION";
	/** 本地登陆key */
	public static final String LOCAL_LOGIN = "LOCAL_LOGIN";
	/** 屏幕宽度key */
	public static final String SCREEN_WIDTH = "SCREEN_WIDTH";
	/** 屏幕高度key */
	public static final String SCREEN_HEIGHT = "SCREEN_HEIGHT";

	/** 存储设备列表key */
	public static String DEVICE_LIST = "";

	/** 本地存储设备列表key */
	public static final String LOCAL_DEVICE_LIST = "LOCAL_DEVICE_LIST";

	/** 缓存设备列表key */
	public static final String CACHE_DEVICE_LIST = "CACHE_DEVICE_LIST";

}
