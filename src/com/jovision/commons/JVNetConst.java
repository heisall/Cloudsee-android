package com.jovision.commons;

public final class JVNetConst {

	public final static int POWER_GUEST = 0x0001;
	public final static int POWER_USER = 0x0002;
	public final static int POWER_ADMIN = 0x0004;

	/* 连接结果 */
	public static final int CONNECT_OK = 0x01;// 连接成功
	public static final int DISCONNECT_OK = 0x02;// 断开连接成功
	public static final int NO_RECONNECT = 0x03;// 不必要重复连接
	public static final int CONNECT_FAILED = 0x04;// 连接失败
	public static final int NO_CONNECT = 0x05;// 没有连接
	public static final int ABNORMAL_DISCONNECT = 0x06;// 连接异常断开
	public static final int SERVICE_STOP = 0x07;// 服务停止连接，连接断开
	public static final int DISCONNECT_FAILED = 0x08;// 断开连接失败
	public static final int OHTER_ERROR = 0x09;// 其他错误

	/* 实时监控数据类型 */
	public final static int JVN_DATA_I = 0x01;// 视频I帧
	public final static int JVN_DATA_B = 0x02;// 视频B帧
	public final static int JVN_DATA_P = 0x03;// 视频P帧
	public final static int JVN_DATA_A = 0x04;// 音频
	public final static int JVN_DATA_S = 0x05;// 帧尺寸
	public final static int JVN_DATA_OK = 0x06;// 视频帧收到确认
	public final static int JVN_DATA_DANDP = 0x07;// 下载或回放收到确认
	public final static int JVN_DATA_O = 0x08;// 其他自定义数据
	public final static int JVN_DATA_SKIP = 0x09;// 视频S帧
	/* 解码器startcode的数值 */
	public final static int JVN_DSC_CARD = 0x0453564A;// 板卡解码类型04版解码器
	public final static int JVN_DSC_9800CARD = 0x0953564A;// 9800板卡解码类型04版解码器

	public final static int JVN_DSC_960CARD = 0x0A53564A;// 新版标准解码器05版解码器

	public final static int JVN_DSC_DVR = 0x0553563A;// DVR的解码器类型
	public final static int JVN_DSC_952CARD = 0x0153564A;// 952
	public final static int JVN_DVR_8004CARD = 0x0253564A;// 宝宝在线

	public final static int DVR8004_STARTCODE = 0x0553564A;//
	public final static int JVSC951_STARTCODE = 0x0753564A;//
	public final static int IPC3507_STARTCODE = 0x1053564A;//
	public final static int IPC_DEC_STARTCODE = 0x1153564A;//

	public final static int JVN_NVR_STARTCODE = 0x2053564A;// nvr

	/* 请求类型 */
	public final static byte JVN_REQ_CHECK = (byte) 0x10;// 请求录像检索
	public final static byte JVN_REQ_DOWNLOAD = (byte) 0x20;// 请求录像下载
	public final static byte JVN_REQ_PLAY = (byte) 0x30;// 请求远程回放
	public final static byte JVN_REQ_CHAT = (byte) 0x40;// 请求语音聊天
	public final static byte JVN_REQ_TEXT = (byte) 0x50;// 请求文本聊天
	public final static byte JVN_REQ_CHECKPASS = (byte) 0x71;// 请求身份验证
	/* 请求返回结果类型 */
	public final static byte JVN_RSP_CHECKDATA = (byte) 0x11;// 检索结果
	public final static byte JVN_RSP_CHECKOVER = (byte) 0x12;// 检索完成
	public final static byte JVN_RSP_DOWNLOADDATA = (byte) 0x21;// 下载数据
	public final static byte JVN_RSP_DOWNLOADOVER = (byte) 0x22;// 下载数据完成
	public final static byte JVN_RSP_DOWNLOADE = (byte) 0x23;// 下载数据失败
	public final static byte JVN_RSP_PLAYDATA = (byte) 0x31;// 回放数据
	public final static byte JVN_RSP_PLAYOVER = (byte) 0x32;// 回放完成
	public final static byte JVN_RSP_PLAYE = (byte) 0x39;// 回放失败
	public final static byte JVN_RSP_CHATDATA = (byte) 0x41;// 语音数据
	public final static byte JVN_RSP_CHATACCEPT = (byte) 0x42;// 同意语音请求
	public final static byte JVN_RSP_TEXTDATA = (byte) 0x51;// 文本数据
	public final static byte JVN_RSP_TEXTACCEPT = (byte) 0x52;// 同意文本请求
	public final static byte JVN_RSP_CHECKPASST = (byte) 0x72;// 身份验证成功
	public final static byte JVN_RSP_CHECKPASSF = (byte) 0x73;// 身份验证失败
	public final static byte JVN_RSP_NOSERVER = (byte) 0x74;// 无该通道服务
	public final static byte JVN_RSP_INVALIDTYPE = (byte) 0x7A;// 连接类型无效
	public final static byte JVN_RSP_OVERLIMIT = (byte) 0x7B;// 连接超过主控允许的最大数目
	public final static byte JVN_RSP_DLTIMEOUT = (byte) 0x76;// 下载超时
	public final static byte JVN_RSP_PLTIMEOUT = (byte) 0x77;// 回放超时
	public final static byte JVN_RSP_DISCONN = (byte) 0x7C;// 断开连接确认

	public final static byte JVN_CMD_PLAYSEEK = (byte) 0x44;// 播放定位 按帧定位 需要参数
															// 帧数(1~最大帧)

	/* 命令类型 Play 开头都是远程回放 */
	public final static byte JVN_CMD_DOWNLOADSTOP = (byte) 0x24;// 停止下载数据
	public final static byte JVN_CMD_PLAYUP = (byte) 0x33;// 快进
	public final static byte JVN_CMD_PLAYDOWN = (byte) 0x34;// 慢放
	public final static byte JVN_CMD_PLAYDEF = (byte) 0x35;// 原速播放
	public final static byte JVN_CMD_PLAYSTOP = (byte) 0x36;// 停止播放
	public final static byte JVN_CMD_PLAYPAUSE = (byte) 0x37;// 暂停播放
	public final static byte JVN_CMD_PLAYGOON = (byte) 0x38;// 继续播放
	public final static byte JVN_CMD_CHATSTOP = (byte) 0x43;// 停止语音聊天
	public final static byte JVN_CMD_TEXTSTOP = (byte) 0x53;// 停止文本聊天
	public final static byte JVN_CMD_YTCTRL = (byte) 0x60;// 云台控制
	public final static byte JVN_CMD_VIDEO = (byte) 0x70;// 实时监控
	public final static byte JVN_CMD_VIDEOPAUSE = (byte) 0x75;// 暂停实时监控
	public final static byte JVN_CMD_TRYTOUCH = (byte) 0x78;// 打洞包
	public final static byte JVN_CMD_FRAMETIME = (byte) 0x79;// 帧发送时间间隔(单位ms)
	public final static byte JVN_CMD_DISCONN = (byte) 0x80;// 断开连接

	/* 与云视通服务器的交互消息 */
	public final static byte JVN_CMD_TOUCH = (byte) 0x81;// 探测包
	public final static byte JVN_REQ_ACTIVEYSTNO = (byte) 0x82;// 主控请求激活YST号码
	public final static byte JVN_RSP_YSTNO = (byte) 0x82;// 服务器返回YST号码
	public final static byte JVN_REQ_ONLINE = (byte) 0x83;// 主控请求上线
	public final static byte JVN_RSP_ONLINE = (byte) 0x84;// 服务器返回上线令牌
	public final static byte JVN_CMD_ONLINE = (byte) 0x84;// 主控地址更新
	public final static byte JVN_CMD_OFFLINE = (byte) 0x85;// 主控下线
	public final static byte JVN_CMD_KEEP = (byte) 0x86;// 主控保活
	public final static byte JVN_REQ_CONNA = (byte) 0x87;// 分控请求主控地址
	public final static byte JVN_RSP_CONNA = (byte) 0x87;// 服务器向分控返回主控地址
	public final static byte JVN_CMD_CONNB = (byte) 0x87;// 服务器命令主控向分控穿透
	public final static byte JVN_RSP_CONNAF = (byte) 0x88;// 服务器向分控返回 主控未上线
	public final static byte JVN_CMD_RELOGIN = (byte) 0x89;// 通知主控重新登陆
	public final static byte JVN_CMD_CLEAR = (byte) 0x8A;// 通知主控下线并清除网络信息包括云视通号码
	public final static byte JVN_CMD_REGCARD = (byte) 0x8B;// 主控注册板卡信息到服务器

	public final static byte JVN_CMD_ONLINES2 = (byte) 0x8C;// 服务器命令主控向转发服务器上线/主控向转发服务器上线
	public final static byte JVN_CMD_CONNS2 = (byte) 0x8D;// 服务器命令分控向转发服务器发起连接
	public final static byte JVN_REQ_S2 = (byte) 0x8E;// 分控向服务器请求转发
	public final static byte JVN_TDATA_CONN = (byte) 0x8F;// 分控向转发服务器发起连接
	public final static byte JVN_TDATA_NORMAL = (byte) 0x90;// 分控/主控向转发服务器发送普通数据

	public final static byte JVN_CMD_CARDCHECK = (byte) 0x91;// 板卡验证
	public final static byte JVN_CMD_ONLINEEX = (byte) 0x92;// 主控地址更新扩展
	public final static byte JVN_CMD_TCPONLINES2 = (byte) 0x93;// 服务器命令主控TCP向转发服务器上线
	public final static byte JVN_CMD_ONLYI = (byte) 0x61;// 该通道只发关键帧
	public final static byte JVN_CMD_FULL = (byte) 0x62;// 该通道恢复满帧
	public final static byte JVN_ALLSERVER = 0;// 所有服务
	public final static byte JVN_ONLYNET = 1;// 只局域网服务

	public final static byte JVN_NOTURN = 0;// 云视通方式时禁用转发
	public final static byte JVN_TRYTURN = 1;// 云视通方式时启用转发
	public final static byte JVN_ONLYTURN = 2;// 云视通方式时仅用转发

	public final static byte JVN_LANGUAGE_ENGLISH = 1;
	public final static byte JVN_LANGUAGE_CHINESE = 2;

	public final static byte TYPE_PC_UDP = 1;// 连接类型 UDP 支持UDP收发完整数据
	public final static byte TYPE_PC_TCP = 2;// 连接类型 TCP 支持TCP收发完整数据
	public final static byte TYPE_MO_TCP = 3;// 连接类型 TCP
												// 支持TCP收发简单数据,普通视频帧等不再发送，只能采用专用接口收发数据(适用于手机监控)
	public final static byte TYPE_MO_UDP = 4;// 连接类型 TCP
												// 支持TCP收发简单数据,普通视频帧等不再发送，只能采用专用接口收发数据(适用于手机监控)
	public final static byte TYPE_3GMO_UDP = 5;
	public final static byte TYPE_3GMOHOME_UDP = 6;

	// 支持TCP收发简单数据,普通视频帧等不再发送，只能采用专用接口收发数据(适用于手机监控)
	/* 云台控制类型 */
	public final static byte JVN_YTCTRL_U = 1;// 上
	public final static byte JVN_YTCTRL_D = 2;// 下
	public final static byte JVN_YTCTRL_L = 3;// 左
	public final static byte JVN_YTCTRL_R = 4;// 右
	public final static byte JVN_YTCTRL_A = 5;// 自动
	public final static byte JVN_YTCTRL_GQD = 6;// 光圈大
	public final static byte JVN_YTCTRL_GQX = 7;// 光圈小
	public final static byte JVN_YTCTRL_BJD = 8;// 变焦大
	public final static byte JVN_YTCTRL_BJX = 9;// 变焦小
	public final static byte JVN_YTCTRL_BBD = 10;// 变倍大
	public final static byte JVN_YTCTRL_BBX = 11;// 变倍小

	public final static byte JVN_YTCTRL_UT = 21;// 上停止
	public final static byte JVN_YTCTRL_DT = 22;// 下停止
	public final static byte JVN_YTCTRL_LT = 23;// 左停止
	public final static byte JVN_YTCTRL_RT = 24;// 右停止
	public final static byte JVN_YTCTRL_AT = 25;// 自动停止
	public final static byte JVN_YTCTRL_GQDT = 26;// 光圈大停止
	public final static byte JVN_YTCTRL_GQXT = 27;// 光圈小停止
	public final static byte JVN_YTCTRL_BJDT = 28;// 变焦大停止
	public final static byte JVN_YTCTRL_BJXT = 29;// 变焦小停止
	public final static byte JVN_YTCTRL_BBDT = 30;// 变倍大停止
	public final static byte JVN_YTCTRL_BBXT = 31;// 变倍小停止

	public final static byte JVN_YTCTRL_RECSTART = 41;// 远程录像开始
	public final static byte JVN_YTCTRL_RECSTOP = 42;// 远程录像开始

	public final static byte JVN_ABFRAMERET = 35; // 帧序列中每个多少帧一个回复

	public final static byte JVN_REMOTE_SETTING = 1;// 远程配置请求
	public final static byte JVN_WIFI_INFO = 2;// AP,WIFI热点请求
	public final static byte JVN_STREAM_INFO = 3;// 码流配置请求
	public final static byte JVN_WIFI_SETTING_SUCCESS = 4;// WIFI配置成功
	public final static byte JVN_WIFI_SETTING_FAILED = 5;// WIFI配置失败
	public final static byte JVN_WIFI_IS_SETTING = 6;// WIFI配置正在配置
	public final static byte JVN_RECORD_RESULT = 7;// 录像模式切换回调

	public final static byte RC_EX_FlashJpeg = (byte) 0x0a;
	public final static byte EX_MD_REFRESH = (byte) 0x01;
	public final static byte EX_MD_SUBMIT = (byte) 0x02;
	public final static byte EX_MD_UPDATE = (byte) 0x03;
	public final static byte RC_EX_STORAGE = (byte) 0x03;
	public final static byte RC_EX_MD = (byte) 0x06;
	public final static byte RC_EXTEND = (byte) 0x06;
	public final static byte EX_STORAGE_REFRESH = (byte) 0x01;
	public final static byte EX_STORAGE_REC_ON = (byte) 0x02;
	public final static byte EX_STORAGE_REC_OFF = (byte) 0x03;
	public final static byte EX_STORAGE_OK = (byte) 0x04;
	public final static byte EX_STORAGE_ERR = (byte) 0x05;
	public final static byte EX_STORAGE_FORMAT = (byte) 0x06;
	public final static byte RC_GETPARAM = (byte) 0x02;
	public final static byte RC_SETPARAM = (byte) 0x03;
	public final static byte RC_EX_NETWORK = (byte) 0x02;
	public final static byte EX_WIFI_AP_CONFIG = (byte) 0x0B; // 针对新AP配置方式，获取到手机端配置的AP信息，便立即返回
}
