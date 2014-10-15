package com.jovision;

import java.io.File;

import android.os.Environment;

public class Consts {
	/** 老数据库数据 **/
	public static final String JVCONFIG_DATABASE = "JVConfigTemp.db";
	public static final int JVCONFIG_DB_VER = 2;

	public static final String SD_CARD_PATH = Environment
			.getExternalStorageDirectory().getPath() + File.separator;

	public static final String LOG_PATH = SD_CARD_PATH + "JCS";
	public static final String ACCOUNT_PATH = SD_CARD_PATH + "ACCOUNT";

	public static final String APP_NAME = "CloudSEE";
	public static final String CAPTURE_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "capture" + File.separator;
	public static final String VIDEO_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "video" + File.separator;
	public static final String SOFTWARE_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "software" + File.separator;

	public static final String DB_PATH = Consts.SD_CARD_PATH + APP_NAME
			+ File.separator + "db" + File.separator;

	public static final String DB_NAME = "CloudSEE.db";

	public static final String TAG_APP = "CS_APP";
	public static final String TAG_UI = "CS_UI";
	public static final String TAG_PLAY = "CS_PLAY";
	public static final String TAG_LOGICAL = "CS_LOGICAL";
	public static final String TAG_JY = "CS_JY";

	/** [Neo] 专用调试 tag */
	public static final String TAG_XX = "CS_XX";

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
	public static final int MAX_CHANNEL_CONNECTION = 36;
	public static final int MAX_DEVICE_CHANNEL_COUNT = 64;
	public static final int DEFAULT_ADD_CHANNEL_COUNT = 4;

	public static final int TYPE_GET_PARAM = 0x02;
	public static final int TYPE_SET_PARAM = 0x03;
	public static final int COUNT_EX_STORAGE = 0x03;
	public static final int TYPE_EX_STORAGE_SWITCH = 0x07;
	public static final String FORMATTER_STORAGE_MODE = "storageMode=%d";
	public static final String FORMATTER_TALK_SWITCH = "talkSwitch=%d";// 1开始
																		// 0关闭

	{
		// [Neo] 设置存储模式
		// Jni.sendString(index, uchType, true, Consts.COUNT_EX_STORAGE,
		// Consts.TYPE_EX_STORAGE_SWITCH,
		// String.format(Consts.FORMATTER_STORAGE_MODE, mode));

		// [Neo] 获取存储模式
		// Jni.sendString(index, uchType, false, 0, Consts.TYPE_GET_PARAM,
		// null);

		// [Neo] 结果检查，通过判断 TextData 的 flag 是否等于 100

		// [Neo] 切换对讲
		// Jni.sendString(index, uchType, false, 0, Consts.TYPE_SET_PARAM,
		// String.format(Consts.FORMATTER_TALK_SWITCH, switcher));
	}

	public static int pushHisCount = 0;
	public static final int PUSH_PAGESIZE = 5;

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

	public static final String IPC_FLAG = "IPC-";
	public static final String IPC_DEFAULT_USER = "jwifiApuser";
	public static final String IPC_DEFAULT_PWD = "^!^@#&1a**U";
	public static final String IPC_DEFAULT_IP = "10.10.0.1";
	public static final int IPC_DEFAULT_PORT = 9101;

	/** 播放tag */
	public static final int PLAY_NORMAL = 0x01;
	public static final int PLAY_DEMO = 0x02;
	public static final int PLAY_AP = 0x03;

	/** 播放key */
	public static final String KEY_PLAY_NORMAL = "PLAY_NORMAL";
	public static final String KEY_PLAY_DEMO = "PLAY_DEMO";
	public static final String KEY_PLAY_AP = "PLAY_AP";

	/** 登录方法 */
	public static final int LOGIN_FUNCTION = 0xB1;
	/** 注册方法 */
	public static final int REGIST_FUNCTION = 0xB2;
	/** 获取设备列表方法 */
	public static final int GET_DEVICE_LIST_FUNCTION = 0xB3;
	/** 获取设备列表方法 */
	public static final int GUID_FUNCTION = 0xB4;

	/** tab页 onback */
	// public static final int TAB_BACK = 1;
	/** JVMyDeviceFragmentActivity */
	// public static final int DEVICE_GETDATA_SUCCESS = 2;// 设备加载成功--
	// public static final int DEVICE_GETDATA_FAILED = 3;// 设备加载失败--
	// public static final int DEVICE_ITEM_CLICK = 4;// 设备单击事件--
	// public static final int DEVICE_ITEM_LONG_CLICK = 5;// 设备长按事件--
	// public static final int DEVICE_ITEM_DEL_CLICK = 6;// 设备删除按钮事件--
	// public static final int DEVICE_EDIT_CLICK = 7;// 设备编辑按钮事件--

	// public static final int DEVICE_NO_DEVICE = 19;// 暂无设备--

	/** ChannelFragment */
	// public static final int CHANNEL_ITEM_CLICK = 20;// 通道单击事件--
	// public static final int CHANNEL_ITEM_LONG_CLICK = 21;// 通道长按事件--
	// public static final int CHANNEL_ITEM_DEL_CLICK = 22;// 通道删除按钮事件--
	// public static final int CHANNEL_ADD_CLICK = 23;// 通道添加事件--
	// public static final int CHANNEL_EDIT_CLICK = 24;// 通道删除按钮事件--

	/** ChannelFragment onResume */
	public static final int CHANNEL_FRAGMENT_ONRESUME = 40;// ChannelFragment
															// onResume
	/** ManageFragment onResume */
	public static final int MANAGE_FRAGMENT_ONRESUME = 41;// ManageFragment
	public static final int MANAGE_ITEM_CLICK = 42;// 通道单击事件--
	// onResume

	/** 推送消息 tag */
	public static final int PUSH_MESSAGE = 50;

	/** 账号错误 tag */
	public static final int ACCOUNT_KEEP_ONLINE_FAILED = 55;// 连续三次保持在线失败
	public static final int ACCOUNT_OFFLINE = 56;// 提掉线
	public static final int ACCOUNT_TCP_ERROR = 57;// 提掉线

	/** 引导界面滑屏 */
	public static final int GUID_PAGE_SCROLL = 500;// --

	public static final int SOUNDONE = 1;// 请选择要配置设备 声音
	public static final int SOUNDTOW = 2;// 正在连接设备
	public static final int SOUNDTHREE = 3;// 请点击下一步，配置无线网络
	public static final int SOUNDFOUR = 4;// 请选择无线路由，输入密码，并点击右上角声波配置按钮
	public static final int SOUNDFIVE = 5;// 请将手机靠近设备
	public static final int SOUNDSIX = 6;// 叮
	public static final int SOUNDSEVINE = 7;// 配置完成，请点击图标查看设备
	public static final int SOUNDEIGHT = 8;// // 搜索声音

	public static final int QUICK_SETTING_ERROR = 100;// 快速设置出错
	public static final int QUICK_SETTING_DEV_ONLINE = 101;// 快速配置，设备上线
	public static final int QUICK_SETTING_CONNECT_FAILED = 102;// 快速设置IPC连接失败

	/** 设备接口对应值 **/

	public static final int STORAGEMODE_NORMAL = 1;// 手动录像
	public static final int STORAGEMODE_ALARM = 2;// 报警录像

	public static final int SCREEN_NORMAL = 0;// 0(正),4(反)
	public static final int SCREEN_OVERTURN = 4;// 0(正),4(反)

	// Jni底层规定
	public static final int DECODE_OMX = 0;// 0软 1硬
	public static final int DECODE_NOTOMX = 1;// 0软 1硬

	/*********************** 以下是状态变量key的声明 ********************************/

	/** 账号sdk是否初始化的key */

	/** 云视通sdk是否初始化的key */
	public static final String KEY_SHOW_GUID = "ShowGuide";
	public static final String KEY_INIT_ACCOUNT_SDK = "initAccountSdk";
	/** 云视通sdk是否初始化的key */
	public static final String KEY_INIT_CLOUD_SDK = "initCloudSdk";
	/** 用户名key */
	public static final String KEY_USERNAME = "USER_NAME";
	/** 密码key */
	public static final String KEY_PASSWORD = "PASSWORD";
	/** 上次登陆时间key */
	public static final String KEY_LAST_LOGIN_TIME = "LAST_TIME";
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

	/** 推送消息JsonArray key */
	public static final String PUSH_JSONARRAY = "PUSH_JSONARRAY";

	/** 本地存储user列表key */
	public static final String LOCAL_USER_LIST = "LOCAL_USER_LIST";

	/** 存储设备列表key */
	public static String DEVICE_LIST = "";

	/** 本地存储设备列表key */
	public static final String LOCAL_DEVICE_LIST = "LOCAL_DEVICE_LIST";

	/** 缓存设备列表key */
	public static final String CACHE_DEVICE_LIST = "CACHE_DEVICE_LIST";

	/** 存储取没取过设备列表key */
	public static String HAG_GOT_DEVICE = "HAG_GOT_DEVICE";

	/** 帮助图案首次显示标志 */
	public static String MORE_FREGMENT_FEEDBACK = "IS_FIRST";

	// /** 功能设置页面场景图片标志位 */
	// public static String SETTING_SCENE = "SCENE";
	//
	// /** 功能设置页面帮助标志位 */
	// public static String SETTING_HELP = "HELP";
	//
	// /** 功能设置页面报警标志位 */
	// public static String SETTING_ALERT = "ALERT";
	//
	// /** 功能设置页面观看模式标志位 */
	// public static String SETTING_SEE_MODEL = "SEE_MODEL";

	/** 进程序WiFi名称 */
	public static final String KEY_OLD_WIFI = "OLD_WIFI";

	/** 进程序WiFi状态 */
	public static final String KEY_OLD_WIFI_STATE = "OLD_WIFI_STATE";

	public static final String KEY_LAST_PUT_STAMP = "last_put_stamp";

	public static final String BO_ID = "d6mhYSnBpnv26S7aCo0GwcxX";
	public static final String BO_SECRET = "D9t2XqYQgH92eVGzlW30Pw9ZrRtqKpPs";

}
