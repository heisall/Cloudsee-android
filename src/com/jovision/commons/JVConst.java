package com.jovision.commons;

public final class JVConst {
	public static final int JV_LIST_MENU_ADD = 0;
	public static final int JV_LIST_MENU_DELETE = 1;
	public static final int JV_LIST_MENU_EDIT = 2;
	public static final int JV_LIST_MENU_BACK = 3;

	public static final int JV_VIDEO_MENU_MULTI = 0;
	public static final int JV_VIDEO_MENU_EDIT = 1;
	public static final int JV_VIDEO_MENU_TRANS = 2;
	public static final int JV_VIDEO_MENU_MANAGER = 3;
	public static final int JV_VIDEO_MENU_EXIT = 5;
	public static final int JV_VIDEO_MENU_LANGUAGE = 6;
	public static final int JV_VIDEO_MENU_ABOUT = 4;

	public static final int JV_CONNECT_CS = 1;
	public static final int JV_CONNECT_IP = 2;

	public static final int JV_REQCODE_SETTING = 1;
	public static final int JV_REQCODE_CONNECT = 2;
	public static final int JV_REQCODE_MENU = 3;
	public static final int JV_REQCODE_MENU_DATA = 7;

	public static final int JV_RSTCODE_SAVE = 1;
	public static final int JV_RSTCODE_CANCEL = 2;

	public static final int JV_RSTCODE_MENU_SHOW = 0x100;// 轮显
	public static final int JV_RSTCODE_MENU_SETTING = 0x101;// 设置
	public static final int JV_RSTCODE_MENU_VERSION = 0x102;// 版本
	public static final int JV_RSTCODE_MENU_ALLUNLINK = 0x103;// 全断
	public static final int JV_RSTCODE_MENU_YT = 0x105;// 云台
	public static final int JV_RSTCODE_MENU_BROWER = 0x104;// 浏览
	public static final int JV_RSTCODE_MENU_BACK = 0x107;// 返回
	public static final int JV_RSTCODE_MENU_SHOWPOINT = 0x110;// 地图演示点
	public static final int JV_RSTCODE_MENU_MAPBACK = 0x111;// 地图返回

	public static final int JV_LOGIN_DIALOG = 0x108;
	public static final int JV_LOGIN_DIALOG2 = 0x109;
	public static final int JV_LOGIN_DIALOG3 = 0x112;

	public static final int ACTION_POINTER_2_DOWN = 0x105;
	public static final int ACTION_POINTER_UP = 0x6;

	public static final int MAX_PATH = 260;
	public static final int RET_OK = 1;
	public static final int RET_ERROR = 0;
	public static final String URL_SERVER = "www.jovetech.com";
	public static final String URL_HEADER = "http://www.jovetech.com/down/YST/";
	public static final String URL_TRAIL = "/yst.txt";
	// public static final String UPDATE_URL ="http://192.168.9.30/update.txt";
	public static final String UPDATE_URL = "http://www.jovetech.com/down/android/update.txt";

	public static final String DEFAULT_SERVER_IP_1 = "58.59.1.248";
	public static final String DEFAULT_SERVER_IP_2 = "60.215.129.99";
	public static final int DEFAULT_SERVER_PORT_1 = 9010;
	public static final int DEFAULT_SERVER_PORT_2 = 9010;
	// public static final int IMAGE_SIZE = 4096 * 30;
	public static final int BUFFER_SIZE = 1280 * 720 * 2;
	// public static final int BUFFER_SIZE = 352 * 288 *2;

	public static final int MSG_TYPE_CONNECT_RET = 0xe1;
	public static final int MSG_TYPE_NET_TIMEDOUT = 0xe2;
	public static final int MSG_TYPE_NET_CONNERROR = 0xe3;
	public static final int MSG_TYPE_NET_DATA_ERROR = 0xe4;
	public static final int MSG_TYPE_NET_YSTSSTOP = 0xe5;
	public static final int MSG_TYPE_LOCK_SCREEN = 0x2224;
	public static final int MSG_PICTURE_ID = 0xe6;// 抓拍图像的参数
	public static final int MSG_TYPE_YTCMD = 0x2225;

	public static final int MSG_TYPE_ADDSRC = 0x2226;
	public static final int MSG_TYPE_DELSRC = 0x2227;
	public static final int MSG_REFRESH_DATA = 0x2231;

	public static final int MSG_SEARCH_DEVICE = 0x123;
	public static final int MSG_SEARCH_DEVICE_END = 0x125;
	public static final int MSG_SHOWIMAGE = 0x124;

	public static final int MSG_TYPE_SPEED = 0x2228;
	public static final int MSG_TYPE_INVALID_PACKAGE = 0x2229;
	public static final int MSG_TYPE_UPDATE_ERROR = 0x2230;

	public static final byte[] H264_SYMBOL = { 0, 0, 0, 1 };
	public static final String JVCONFIG_DATABASE = "JVConfigTemp.db";
	public static final int JVCONFIG_DB_VER = 2;

	public static final int TYPE_MO_TCP = 3;// 连接类型 TCP
											// 支持TCP收发简单数据,普通视频帧等不再发送，只能采用专用接口收发数据(适用于手机监控)
	public static final int TYPE_MO_UDP = 4;// 连接类型 TCP
											// 支持TCP收发简单数据,普通视频帧等不再发送，只能采用专用接口收发数据(适用于手机监控)

	public static final int JVN_NOTURN = 0;// 云视通方式时禁用转发
	public static final int JVN_TRYTURN = 1;// 云视通方式时启用转发
	public static final int JVN_ONLYTURN = 2;// 云视通方式时仅用转发

	public static final String MENU = "com.jovetech.CloudSee.temp.JVMenuActivity";// 关闭menu
	public static final String JVMATRIX = "com.jovetech.CloudSee.temp.JVMatrixActivity";// 关闭JVMatrixActivity

	public static final int REMOTE_PLAYBACK_END = 100;// 远程回放
	public static final int BACK_TO_LIST = 101;// 返回到列表界面的标志
	public static final int CHANGE_CHANNEL_FLAG = 102;// 切换通道

	public static final int EDIT_PWD_SUCCESS = 103;// 修改密码成功
	public static final int EDIT_PWD_FAILED = 104;// 修改密码失败

	public static final int GET_NEWS_SUCCESS = 105;// 获取新品成功
	public static final int VIDEO_SINGLE_CLICK_REFRESH = 106;// 视频窗口点击刷新事件
	public static final int VIDEO_DOUBLE_CLICK_REFRESH = 107;// 视频窗口双击刷新事件
	public static final int VIDEO_LONG_CLICK_REFRESH = 108;// 视频窗口长按事件
	public static final int VIDEO_PLAY_BUTTON_EVENT = 109;// 视频窗口播放按钮点击事件

	public static final int REMTE_PLAYBACK_BEGIN = 114;
	public static final int REMTE_CLOSE_FETURE_ = 115;

	public static final int BARCODE_RESULT = 113; // 条码扫瞄返回值
	public static final int CHANGE_VIDEO_FRAME = 116; // 切换视频制式,打包录像
	public static final int SDCARD_SPACENO = 117; // 内存卡空间不足
	public static final int CHANGE_VIDEO2_FRAME = 118; // 切换视频制式,视频花瓶的处理标识

	public static final int SEARCH_DEVICE_SUCCESS = 119; // 快速观看广播搜索到设备
	public static final int SEARCH_DEVICE_FAILED = 120; // 快速观看广播未搜索到设备

	// public static final int SEARCH_DEVICE_FLAG = 121; //快速观看标志位
	public static final int MANAGE_DEVICE_FLAG = 122; // 设备管理标志位

	public static final int SYC_DATA_COMPLETEDED = 123; // 同步完标志位
	public static final int DEVICE_MAN_FINISH_EDIT_PASS = 124; // 设备管理修改完密码

	public static final int DEVICE_MAN_START_CONNECT = 125;// 设备管理点击连接
	public static final int DEVICE_MAN_CONNECT_FAILED = 126;// 设备管理 连接失败
	public static final int DEVICE_MAN_IS_IPC = 127; // 设备管理是IPC
	public static final int DEVICE_MAN_NOT_IPC = 128; // 设备管理不是IPC

	public static final int RIGHT_SLIDE_SHOW_HELP = 129; // 右滑设备列表显示帮助图

	public static final int DEVICE_MAN_HOST_AGREE_TEXT = 130;// 主控同意文本聊天
	public static final int DEVICE_MAN_HOST_TEXT = 131;// 获取主控文本聊天数据

	public static final int DEVICE_SETTING_WIFI_DET = 132;// 远程设置wifi信息
	public static final int DEVICE_MAN_CONN_TIME_OUT = 133;// 进远程设置超时

	public static final int DEVICE_MAN_HOST_WIFI_TEXT = 134;// 获取主控WIFI信息数据
	public static final int DEVICE_MAN_HOST_STREAM_TEXT = 135;// 获取主控码流配置信息数据
	public static final int QUICK_SETTING_HOST_SET_WIFI_SUCC = 136;// 快速设置配置wifi成功
	public static final int QUICK_SETTING_HOST_SET_WIFI_FAIL = 137;// 快速设置配置wifi失败

	public static final int RECEVICE_PUSH_MSG = 138;// 收到推送消息
	public static final int REFRESH_PUSH_MSG = 139;// 后台刷新推送消息
	public static final int REFRESH_PUSH_MSG_BTN = 140;// 刷新按钮刷新推送消息

	public static final int JVMAIN_ACTIVITY_FLAG = 141;// JVMainActivity
	public static final int JVMORE_ACTIVITY_FLAG = 142;// JVMoreFeatureActivity

	public static final int LOG_OUT_SUCCESS = 143;// 注销成功
	public static final int CHECK_UPDATE_SUCCESS = 144;// 检查到更新
	public static final int CHECK_NO_UPDATE = 145;// 没更新
	public static final int CHECK_UPDATE_FAILED = 146;// 检查失败

	public static final int DEVICE_MAN_HOST_EDIT_WIRED_SUCC = 147;// 有线修改成功
	public static final int DEVICE_MAN_HOST_EDIT_WIRED_FAIL = 148;// 有线修改失败

	public static final int THREE_MIN_BROADCAST_DEVICE = 149;// 3分钟广播一次设备

	public static final int DEVICE_MAN_HOST_NOT_AGREE_TEXT = 150;// 主控拒绝同意文本聊天

	public static final int DEVICE_MAN_HOST_NO_POWER_EDIT = 151;// 没有修改权限

	// JVPlayActivity
	public static final int FIVE_MIN_BROADCAST_FINISHED = 156;
	public static final int DATA_SOURCE_REQUEST = 157;
	public static final int DATA_SOURCE_RESULT = 158;
	public static final int DATA_SOURCE_RESULT_NOSELECT = 159;
	public static final int UPLOAD_IMAGE_FLAG = 160;
	public static final int START_PLAY_FLAG_FAILED = 161;// 回调播放成功
	public static final int START_PLAY_FLAG_SUCCESS = 162;// 回调播放失败
	public static final int NORMAL_PLAY_FLAG = 163;// 正常播放
	public static final int SHOWPOINT_PLAY_FLAG = 164;// 演示点播放只用云视通号连接
	public static final int BROADCAST_PLAY_FLAG = 165;// 快速观看广播设备播放只用ip连接
	public static final int NOT_SUPPORT_REMOTE_PLAY = 166;// 暂不支持远程回放功能
	public static final int EDIT_CONN_STATE = 167;// 连接后修改连接状态
	public static final int CLOSE_BACK_DIALOG = 168;// 关闭dialog消息

	// JVBindAccountActivity
	public static final int ACCOUNT_BIND_SUCCESS = 169;
	public static final int ACCOUNT_BIND_FAILED = 170;
	public static final int ACCOUNT_LOGIN_FAILED = 171;// 登陆失败无法绑定

	// JVEditPassActivity
	public static final int EDIT_SUCCESS = 172;
	public static final int EDIT_FAILED = 173;

	// JVLoginActivity
	public static String APP_ID = "";// "222222";QQ互联100474933
	public static final String SCOPE = "all";
	public static String OPEN_ID = "";
	public static final int QQ_AUTHO_SUCCESS = 174;
	public static final int QQ_AUTHO_FAILED = 175;
	public static final int LOGIN_SUCCESS = 176;
	public static final int LOGIN_FAILED_1 = 177;// 用户名密码错
	public static final int NET_CONNECT_FLAG = 178;
	public static final int JUMP_FLAG = 179;
	public static final int LOGIN_FAILED_2 = 180;// 网络连接错误
	public static final int LOGIN_USER_REFRESH = 181;// 刷新用户登录列表

	public static final int ACCOUNT_BIND_REQUEST = 184;
	public static final int ACCOUNT_BIND_RESULT = 185;
	public static final int DEVICE_DATA_REFRESH = 186;// 刷新列表
	public static final int DEVICE_SEARCH_BROADCAST = 187;
	public static final int RELOGIN_SUCCESS = 188;
	public static final int RELOGIN_FAILED = 189;

	public static final int DEVICE_POS_DATA_REFRESH = 191;// 刷新滑动到指定位置设备列表
	public static final int MESSAGE_LOAD_IMAGE_SUCCESS = 192;
	public static final int MESSAGE_LOAD_IMAGE_FAILED = 193;
	public static final int DEVICE_DATA_REFRESH_SUCCESS = 194;
	public static final int DEVICE_DATA_REFRESH_FAILED = 195;
	public static final int NEW_PRO_REFRESH_SUCCESS = 196;
	public static final int NEW_PRO_REFRESH_FAILED = 197;
	public static final int MAIN_ACCOUNT_BIND_SUCCESS = 198;
	public static final int MAIN_ACCOUNT_BIND_FAILED = 199;
	public static final int DEVICE_ADD_SUCCESS = 200;
	public static final int DEVICE_ADD_FAILED = 201;
	public static final int DEVICE_IMAGE_CHANGE = 202;
	public static final int DEVICE_ADD_BROADCAST_SUCCESS = 203;// 广播搜索通道数量成功
	public static final int DEVICE_ADD_BROADCAST_FAILED = 204;// 广播搜索通道数量失败通过-1通道获取
	public static final int NEW_PRO_CHANGE_POSITION = 205;// 小圆点指示位置
	public static final int NEW_PRO_DETAIL = 206;// 进新品详细信息
	public static char[] str = { 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O',
			'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'B',
			'V', 'C', 'X', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's',
			'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'b', 'v', 'c', 'x',
			'z' };

	public static final String DEFAULT_IP = "127.0.0.1";
	public static final int DEFAULT_PORT = 9101;

	// JVRegisterActivity
	public static final int REGIST_SUCCESS = 207;
	public static final int REGIST_FAILED = 208;
	public static final int HAVE_REGISTED = 209;
	public static final int REGIST_SUCCESS_LOGIN_SUCCESS = 210;// 注册成功登陆成功
	public static final int REGIST_SUCCESS_LOGIN_FAILED = 211;// 注册成功登陆失败
	public static final int REGIST_SUCCESS_LOGIN_NET_ERROR = 212;// 注册成功网络错误

	// JVRemotePlayBackActivity
	public static final int REMOTE_DATA_SUCCESS = 213;// 数据加载成功
	public static final int REMOTE_DATA_FAILED = 214;// 数据加载失败
	public static final int REMOTE_NO_DATA_FAILED = 215;// 暂无远程视频
	public static final int REMOTE_START_PLAY = 216;// 开始播放远程视频
	public static final int REMOTE_PLAY_FAILED = 217;// 播放失败
	public static final int REMOTE_PLAY_NOT_SUPPORT = 218;// 手机暂不支持播放1080p的视频
	public static final int REMOTE_PLAY_EXCEPTION = 219;// 与主控断开连接

	// DeviceExpandableListAdapter
	// 编辑
	public static final int EDIT_POINT_SUCCESS = 220;
	public static final int EDIT_POINT_FAILED = 221;
	public static final int EDIT_DEVICE_SUCCESS = 222;
	public static final int EDIT_DEVICE_FAILED = 223;
	// 删除
	public static final int DELETE_POINT_SUCCESS = 224;
	public static final int DELETE_POINT_FAILED = 225;
	public static final int DELETE_DEVICE_SUCCESS = 226;
	public static final int DELETE_DEVICE_FAILED = 227;
	// 添加通道
	public static final int ADD_POINT_SUCCESS = 228;
	public static final int ADD_POINT_FAILED = 229;

	// OffLineService
	public static boolean KEEP_ONLINE_FLAG = false;// 保持在线服务开启标志
	public static final int KEEP_ONLINE_SUCCESS = 230;
	public static final int KEEP_ONLINE_FAILED = 231;
	public static final int KEEP_ONLINE_TIMEOUT = 232;
	public static boolean DIALOG_SHOW = false;

	public static final int REFRESH_PUSH_COUNT = 233;// 刷新推送条数
	public static final int PUSH_DISCONNECT = 234;// 推送断开消息

	public static final int PUSH_NET_RESTORE = 235;// 网络恢复
	public static final int PUSH_NET_DISCONNECT = 236;// 网络依然断开

	public static final int INVISIBLE_HELP = 237;// 隐藏帮助

	public static final int MORE_OPEN_PUSH_WEBCC_SUCCESS = 238;// 更多上线成功
	public static final int MORE_OPEN_PUSH_WEBCC_FAILED = 239;// 更多上线失败

	public static final int MAIN_OPEN_PUSH_WEBCC_SUCCESS = 240;// 主界面上线成功
	public static final int MAIN_OPEN_PUSH_WEBCC_FAILED = 241;// 主界面上线失败

	public static final int QUICK_SETTING_IPC_WIFI_SUCCESS = 242;// 快速设置获取IPCwifi成功
	public static final int QUICK_SETTING_IPC_WIFI_FAILED = 243;// 快速设置获取IPCwifi失败

	public static final int QUICK_SETTING_IPC_WIFI_CON_SUCCESS = 244;// 快速设置IPCwifi连接成功

	public static final int QUICK_SETTING_IS_IPC = 247;// 快速设置连接是IPC
	public static final int QUICK_SETTING_IS_NOT_IPC = 248;// 快速设置连接不是IPC

	public static final int QUICK_SETTING_HOST_AGREE_TEXT = 249;// 快速设置主控同意文本聊天
	public static final int QUICK_SETTING_HOST_NOT_AGREE_TEXT = 250;// 快速设置主控不同意文本聊天

	public static final int QUICK_SETTING_MOBILE_WIFI_SUCC = 251;// 快速设置获取手机wifi列表

	public static final int QUICK_SETTING_IPC_BROAD_SUCCESS = 252;// 快速设置获取IP广播成功
	public static final int QUICK_SETTING_IPC_BROAD_FAILED = 253;// 快速设置获取IPC广播失败

	public static final int QUICK_SETTING_AP_CON_FAILED = 254;// 快速设置AP连接失败
	public static final int QUICK_SETTING_AP_CON_TIMEOUT = 255;// 快速设置AP连接超时

	public static final int QUICK_SETTING_WIFI_CONTINUE_SET = 256;// 快速设置继续配置wifi
	public static final int QUICK_SETTING_WIFI_FINISH_SET = 257;// 快速设置完成wifi配置

	public static final int QUICK_SETTING_WIFI_CHANGED_SUCC = 258;// 快速设置退出时wifi切换
																	// 成功

	public static final int QUICK_SETTING_HOST_SETTING_WIFI = 259;// 快速设置正在配置wifi

	public static final int LOGIN_USER_SUCCESS = 260;// 登录成功
	public static final int LOGIN_USER_ERROR = 261;// 用户名或密码错误请重新输入
	public static final int LOGIN_USER_NET_ERROR = 262;// 网络连接错误，请重新验证
	public static final int UPDATE_DEVICE_STATE_SUCCESS = 263;// 更新设备状态
	public static final int UPDATE_DEVICE_STATE_FAILED = 264;// 更新设备状态
	public static final int UPDATE_LIST_BROADCAST = 265;// 更新设备列表时广播设备列表
	public static final int UPDATE_STATE_BROADCAST = 266;// 更新设备状态时广播设备列表
	public static final int CHECK_STATE_BROADCAST = 267;// 设置是否检查设备状态时广播设备列表

	public static final int JVMAIN_SEARCH_WIFI_SUCCESS = 268;// JVMain界面搜索IPC网络成功
	public static final int JVMAIN_SEARCH_WIFI_FAILED = 269;// JVMain界面搜索IPC网络失败

	public static final int VIDEO_PLAY_UPLOAD_IMAGE_FINISH = 271;// 视频播放上传完最后一帧图

	public static final int QUICK_SETTING_HOST_WIFI_TEXT_TIMEOUT = 272;// 快速设置获取主控WIFI信息数据超时

	public static final String CMD_HEARTBEAT = "0x50"; // 心跳
	public static final String CMD_PUSHALARM = "0x51"; // 推送报警消息

	public static final int DEVICE_MAN_HOST_CONNECT_WIFI_SUCC = 273;// 连接wifi成功
	public static final int DEVICE_MAN_HOST_CONNECT_WIFI_FAIL = 274;// 连接wifi失败

	public static final int FEEDBACK_SUCCESS = 275;// 提交意见反馈成功
	public static final int FEEDBACK_FAILED = 276;// 提交意见反馈失败

	public static final int GES_PTZ_UP = 327;// 手势云台上滑
	public static final int GES_PTZ_DOWN = 328;// 手势云台下滑
	public static final int GES_PTZ_LEFT = 329;// 手势云台左滑
	public static final int GES_PTZ_RIGHT = 330;// 手势云台右滑

	public static final int MODIFY_DEVICE_INFO = 333;// 修改设备信息
	public static final int MODIFY_DEVICE_INFO_RESP = 334;// 修改设备信息(回调)

	public static final int WHAT_CHANGE_LAYOUT = 0x51;
	public static final int WHAT_CHECK_DOUBLE_CLICK = 0x52;
	public static final int WHAT_START_CONNECT = 0x53;// 进播放开始连接
	public static final int WHAT_STARTING_CONNECT = 0x54;// 开始连接
	public static final int WHAT_SELECT_SCREEN = 0x55;// 下拉选择多屏

	public static final int ARG1_SET_ITEM = 0x01;

	public static final int CHECK_DOUBLE_DELAY = 1000;

	// 视频播放状态
	public static final int PLAY_CONNECTING = 1;// 连接中
	public static final int PLAY_CONNECTTED = 2;// 已连接
	public static final int PLAY_DIS_CONNECTTED = 3;// 断开
	public static final int PLAY_CONNECTING_BUFFER = 4;// 连接成功，正在缓冲数据。。。

	public static final int PLAY_CONNECT_OK = 200;// 视频连接成功
	public static final int PLAY_CONNECT_IFRAME_OK = 201;// I帧等待成功
	public static final int IFRAME_OK = 0x10;// AP视频连接

	public static final int PLAY_CONNECT_RECONNECT = 202;// 重复连接

	public static final int OTHER_CONNECT = 5005;// -1通道连接
	public static final int AP_CONNECT = 5006;// AP视频连接

	public static final int DEVICE_BROAD_SUCCESS = 331;// 广播完刷新设备

	public static final int LANGUAGE_ZH = 1;// 中文
	public static final int LANGUAGE_EN = 2;// 英文

	public static final int JVN_CMD_CHATFAILED = 300;

	/*********************** 以下是 消息声明 ********************************/

	/** Ap配置界面 */
	public static final String IPC_FLAG = "IPC-";
	public static final String IPC_DEFAULT_USER = "jwifiApuser";
	public static final String IPC_DEFAULT_PWD = "^!^@#&1a**U";
	public static final String IPC_DEFAULT_IP = "10.10.0.1";
	public static final int IPC_DEFAULT_PORT = 9101;

	public static final int QUICK_SETTING_CONNECT_FAILED = 246;// 快速设置IPC连接失败
	public static final int SHAKE_IPC_WIFI_SUCCESS = 268;// 摇一摇搜索IPC网络成功
	public static final int SHAKE_IPC_WIFI_FAILED = 269;// 摇一摇搜索IPC网络失败

	public static final int QUICK_SETTING_WIFI_CON_SUCCESS = 351;// 快速配置手机连接wifi成功
	public static final int QUICK_SETTING_WIFI_CON_TIMEOUT = 352;// 快速配置手机连接wifi

	public static final int QUICK_SETTING_HOST_TEXT = 353;// 快速设置文本聊天数据
	public static final int QUICK_SETTING_WIFI_RESET_ERROR = 357;
	public static final int TCP_ERROR_OFFLINE = 354;// 连接掉线
	public static final int IPC_WIFI_CON_FAILED = 245;// 快速设置IPCwifi连接失败
	public static final int MOBILE_WIFI_CON_FAILED = 358;// 快速设置mobile wifi连接失败
	public static final int AP_SET_ORDER = 359;// 刷新AP配置提示信息
	public static final int AP_SET_SUCCESS = 360;// AP配置成功
	public static final int AP_SET_FAILED = 361;// 刷新AP配置失败
	public static final int DEVICE_MOST_COUNT = 332;// 设备超过最大数
	public static final int QUICK_SETTING_WIFI_CHANGED_FAIL = 270;// 快速设置退出时wifi切换失败

	public static final int CONNECT_CHANGE = 0xA1;
	public static final int NORMAL_DATA = 0xA2;
	public static final int CHECK_RESULT = 0xA3;
	public static final int CHAT_DATA = 0xA4;// 对讲
	public static final int TEXT_DATA = 0xA5;// 文本
	public static final int DOWNLOAD = 0xA6;
	public static final int PLAY_DATA = 0xA7;
	public static final int SEARCH_LAN_SERVER = 0xA8;
	public static final int FOO = 0xA9;

	public static final int INIT_ACCOUNT_SDK_FAILED = 360;// 初始化账号sdk失败
	public static final int REMOTE_PLAY_DISMISS_PROGRESS = 361;// 隐藏远程回放进度条
}
