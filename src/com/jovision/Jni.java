package com.jovision;

import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyUtils;

/**
 * 所有与 NDK 交互的接口都在这儿
 * 
 * @author neo
 * 
 */
public class Jni {

	/**
	 * dummy test foo
	 * 
	 */
	public static native byte[] foo(byte[] array);

	public static native void setStat(boolean on);

	/**
	 * 获取底层库版本
	 * 
	 * @return json: {"jni":"xx","net":"xx"}
	 */
	public static native String getVersion();

	/**
	 * 初始化，参考 {@link JVSUDT#JVC_InitSDK(int, Object)}
	 * 
	 * @param handle
	 *            回调句柄，要传 MainApplication 的实例对象哦，因为回调方式是：<br />
	 *            {@link MainApplication#onJniNotify(int, int, int, Object)}
	 * @param port
	 *            本地端口
	 * @param path
	 *            日志路径
	 * @return
	 */
	public static native boolean init(Object handle, int port, String path);

	/**
	 * 卸载，参考 {@link JVSUDT#JVC_ReleaseSDK()}
	 * 
	 */
	public static native void deinit();

	/**
	 * 搜索局域网服务端，参考 {@link JVSUDT#JVC_StartLANSerchServer(int, int)}
	 * 
	 * @param localPort
	 *            默认 9400
	 * @param serverPort
	 *            默认 6666
	 * @return
	 */
	public static native int searchLanServer(int localPort, int serverPort);

	/**
	 * 停止搜索局域网服务端，参考 {@link JVSUDT#JVC_StopLANSerchServer()}
	 * 
	 */
	public static native void stopSearchLanServer();

	/**
	 * 获取通道个数，参考 {@link JVSUDT#JVC_WANGetChannelCount(String, int, int)}
	 * 
	 * @param group
	 * @param cloudSeeId
	 * @param timeout
	 *            注意：单位是秒
	 * @return
	 * 
	 */
	public static native int getChannelCount(String group, int cloudSeeId,
			int timeout);

	/**
	 * 搜索局域网设备，参考
	 * {@link JVSUDT#JVC_MOLANSerchDevice(String, int, int, int, String, int)}
	 * 
	 * @param group
	 * @param cloudSeeId
	 * @param cardType
	 * @param variety
	 * @param deviceName
	 * @param timeout
	 *            单位是毫秒
	 * @param frequence
	 * @return
	 */
	public static native int searchLanDevice(String group, int cloudSeeId,
			int cardType, int variety, String deviceName, int timeout,
			int frequence);

	/**
	 * 开启快速链接服务，参考 {@link JVSUDT#JVC_EnableHelp(boolean, int)}
	 * 
	 * @param enable
	 * @param typeId
	 *            enable == ture 时
	 *            <ul>
	 *            <li>1: 使用者是开启独立进程的云视通小助手</li>
	 *            <li>2: 使用者是云视通客户端，支持独立进程的云视通小助手</li>
	 *            <li>3: 使用者是云视通客户端，不支持独立进程的云视通小助手</li>
	 *            </ul>
	 * 
	 *            难以理解的语言
	 * @param maxLimit
	 *            允许最大限制
	 * 
	 * @return
	 */
	public static native boolean enableLinkHelper(boolean enable, int typeId,
			int maxLimit);

	/**
	 * 设置连接小助手，参考 {@link JVSUDT#JVC_SetHelpYSTNO(byte[], int)}
	 * 
	 * @param json
	 *            [{gid: "A", no: 361, channel: 1, name: "abc", pwd:
	 *            "123"},{gid: "A", no: 362, channel: 1, name: "abc", pwd:
	 *            "123"}]
	 * @return
	 */
	public static native boolean setLinkHelper(String json);

	/**
	 * 获取已设置的云视通列表，参考 {@link JVSUDT#JVC_GetHelpYSTNO(byte[], int)}
	 * 
	 * @return json [{cno: "A361", enable: false},{no: "A362", enable: false}]
	 */
	public static native String getAllDeviceStatus();

	/**
	 * 检查设备是否在线，参考 {@link JVSUDT#JVC_GetYSTStatus(String, int, int)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param group
	 * @param cloudSeeId
	 * @param timeout
	 *            单位是秒
	 * @return
	 */
	public static native int isDeviceOnline(String group, int cloudSeeId,
			int timeout);

	/**
	 * 连接，参考
	 * {@link JVSUDT#JVC_Connect(int, int, String, int, String, String, int, String, boolean, int, boolean, int, Object)}
	 * 
	 * @param window
	 *            窗口索引，从 0 开始
	 * @param channel
	 *            设备通道，从 1 开始
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 * @param cloudSeeId
	 * @param groupId
	 * @param isLocalDetect
	 * @param turnType
	 * @param isPhone
	 * @param connectType
	 * @param surface
	 * @param isTryOmx
	 * @return 连接结果，成功时返回窗口索引，失败时返回原因值
	 */
	public static native int connect(int window, int channel, String ip,
			int port, String username, String password, int cloudSeeId,
			String groupId, boolean isLocalDetect, int turnType,
			boolean isPhone, int connectType, Object surface, boolean isTryOmx);

	/**
	 * 设置当前解码方式
	 * 
	 * @param window
	 *            窗口索引
	 * @param isOmx
	 *            是否硬解
	 * @return 设置是否生效
	 */
	public static native boolean setOmx(int window, boolean isOmx);

	/**
	 * 设置窗口颜色
	 * 
	 * @param window
	 *            窗口索引
	 * @param red
	 *            红，0~1
	 * @param green
	 *            绿，0~1
	 * @param blue
	 *            蓝，0~1
	 * @param alpha
	 *            透明，0~1
	 * @return
	 */
	public static native boolean setColor(int window, float red, float green,
			float blue, float alpha);

	/**
	 * 设置下载文件路径
	 * 
	 * @param fileName
	 */
	public static native void setDownloadFileName(String fileName);

	/**
	 * 获取下载文件路径
	 * 
	 * @return
	 */
	public static native String getDownloadFileName();

	/**
	 * 暂停底层显示
	 * 
	 * @param window
	 *            窗口索引
	 * @return
	 */
	public static native boolean pause(int window);

	/**
	 * 恢复底层显示
	 * 
	 * @param window
	 *            窗口索引
	 * @param surface
	 * @return
	 */
	public static native boolean resume(int window, Object surface);

	/**
	 * 将指定通道的视频快进到最新内容
	 * 
	 * @param window
	 *            窗口索引
	 */
	public static native boolean fastForward(int window);

	/**
	 * 开始录制，参考
	 * {@link JVSUDT#StartRecordMP4(String, int, int, int, int, int, double, int)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param path
	 * @param video
	 * @param audio
	 * @return
	 */
	public static native boolean startRecord(int window, String path,
			boolean enableVideo, boolean enableAudio);

	/**
	 * 检查对应窗口是否处于录像状态，TODO: 现在只有单路可用
	 * 
	 * @param window
	 *            窗口索引
	 * @return
	 */
	public static native boolean checkRecord(int window);

	/**
	 * 停止录制，参考 {@link JVSUDT#StopRecordMP4(int)}
	 * 
	 * @return
	 */
	public static native boolean stopRecord();

	/**
	 * 截图，参考 {@link JVSUDT#SaveCapture(int)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param name
	 *            待保存的文件名
	 * @param quality
	 *            画面质量
	 * @return
	 */
	public static native boolean screenshot(int window, String name, int quality);

	/**
	 * 断开，参考 {@link JVSUDT#JVC_DisConnect(int)}
	 * 
	 * @param window
	 *            窗口索引
	 */
	public static native boolean disconnect(int window);

	/**
	 * 清理本地缓存，参考 {@link JVSUDT#JVC_ClearBuffer(int)}
	 * 
	 * @param window
	 *            窗口索引
	 */
	public static native boolean clearBuffer(int window);

	/**
	 * 查询某个设备是否被搜索出来
	 * 
	 * @param groudId
	 *            组标识
	 * @param cloudSeeId
	 *            云视通编号
	 * @param timeout
	 *            超时时间，毫秒
	 * @return 调用是否成功，等回调
	 */
	public static native boolean queryDevice(String groudId, int cloudSeeId,
			int timeout);

	/**
	 * 发送字节数据，参考 {@link JVSUDT#JVC_SendData(int, byte, byte[], int)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param data
	 * @param size
	 */
	public static native boolean sendBytes(int window, byte uchType,
			byte[] data, int size);

	/**
	 * 发送音频数据 {@link JVSUDT#JVC_SendAudioData(int, byte, byte[], int)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param data
	 * @param size
	 */
	public static native boolean sendAudioData(int window, byte uchType,
			byte[] data, int size);

	/**
	 * 发送整数数据，参考 {@link JVSUDT#JVC_SendPlaybackData(int, byte, int, int)}
	 * 
	 * 实际调用 {@link #sendCmd(int, byte, byte[], int)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param data
	 */
	public static native boolean sendInteger(int window, byte uchType, int data);

	/**
	 * 发送字符串数据
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 *            发送类型
	 * @param isExtend
	 *            是否扩展消息
	 * @param count
	 *            扩展包数量
	 * @param type
	 *            扩展消息类型
	 * @param data
	 *            数据
	 */
	public static native boolean sendString(int window, byte uchType,
			boolean isExtend, int count, int type, String data);

	{
		int window = 0;
		byte uchType = 0;

		int mode = 0;
		int switcher = 0;

		int type = 0;
		int flag = 0;

		int dhcp = 0;
		int ip = MyUtils.ip2int("");
		int mask = MyUtils.ip2int("");
		int gateway = MyUtils.ip2int("");
		int dns = MyUtils.ip2int("");

		int ch = 0;
		int width = 0;
		int height = 0;
		int mbph = 0;
		int fps = 0;
		int rc = 0;

		String ssid = "";
		String pwd = "";
		String auth = "";
		String enc = "";

		String custom = "";
		String filename = "";

		// [Neo] 设置存储模式
		Jni.sendString(window, uchType, true, Consts.COUNT_EX_STORAGE,
				Consts.TYPE_EX_STORAGE_SWITCH,
				String.format(Consts.FORMATTER_STORAGE_MODE, mode));

		// [Neo] 获取存储模式
		Jni.sendString(window, uchType, false, 0, Consts.TYPE_GET_PARAM, null);

		// [Neo] 结果检查，通过判断 TextData 的 flag 是否等于 100

		// [Neo] 切换对讲
		Jni.sendString(window, uchType, false, 0, Consts.TYPE_SET_PARAM,
				String.format(Consts.FORMATTER_TALK_SWITCH, switcher));

		// [Neo] 门瓷与手环
		Jni.sendString(window, uchType, false, 0, type, custom);

		// [Neo] 获取设备参数 -> flag = FLAG_GET_PARAM, 分析 msg
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
				JVNetConst.RC_GETPARAM, null);

		// [Neo] 获取码流参数
		Jni.sendTextData(0, JVNetConst.JVN_RSP_TEXTDATA, 8,
				JVNetConst.JVN_STREAM_INFO);

		/*** 忧郁的分割线 慧通 ***/

		// [Neo] 设置闪光灯 0: 自动 1: 开启 2: 关闭 -> flag = FLAG_SET_PARAM_OK
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
				JVNetConst.RC_SETPARAM,
				String.format(Consts.FORMATTER_FLASH_SWITCH, switcher));

		// [Neo] 抓拍 -> flag = FLAG_CAPTURE_FLASH, result =
		// RESULT_SUCCESS/RESULT_NO_FILENAME/RESULT_OPEN_FAILED
		Jni.screenshot(window, filename, -1);
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true,
				JVNetConst.RC_EX_FlashJpeg, JVNetConst.RC_EXTEND, null);

		// [Neo] 获取移动侦测状态 1: 开 0: 关 -> flag = FLAG_GET_MD_STATE, 分析 msg
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true,
				JVNetConst.RC_EX_MD, JVNetConst.EX_MD_UPDATE, null);
		// [Neo] 设置移动侦测 -> flag = FLAG_SET_MD_STATE
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, true,
				JVNetConst.RC_EX_MD, JVNetConst.EX_MD_SUBMIT, String.format(
						Consts.FORMATTER_MOTION_DETECTION_SWITCH, switcher));

		// [Neo] 设置视频制式 0: P 1: N -> flag = FLAG_SET_PARAM_OK, 通过获取码流确认
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
				JVNetConst.RC_SETPARAM,
				String.format(Consts.FORMATTER_PN_SWITCH, switcher));

		// [Neo] 切换 AP 模式 TODO 未验证 1: AP
		Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
				JVNetConst.RC_SETPARAM,
				String.format(Consts.FORMATTER_AP_SWITCH, switcher));

		/*** 忧郁的分割线 ***/

		// [Neo] 设置码流，已替换
		Jni.sendString(window, uchType, false, 0, Consts.TYPE_SET_PARAM, String
				.format(Consts.FORMATTER_SET_BPS_FPS, ch, width, height, mbph,
						fps, rc));

		/*** 忧郁的分割线 TODO ***/

		// [Neo] 设置 DHCP
		Jni.sendString(window, uchType, true, Consts.COUNT_EX_NETWORK,
				Consts.TYPE_EX_SET_DHCP, String.format(
						Consts.FORMATTER_SET_DHCP, flag, dhcp, ip, mask,
						gateway, dns));

		// [Neo] 设置 wifi
		Jni.sendString(window, uchType, true, Consts.COUNT_EX_NETWORK, type,
				String.format(Consts.FORMATTER_SET_WIFI, flag, ssid, pwd));

		// [Neo] 保存 wifi
		Jni.sendString(window, uchType, true, Consts.COUNT_EX_NETWORK, type,
				String.format(Consts.FORMATTER_SAVE_WIFI, flag, ssid, pwd,
						auth, enc));

		// [Neo] 切换码流、设置设备名称、设置存储
		Jni.sendString(window, uchType, false, 0, Consts.TYPE_SET_PARAM, custom);

		// [Neo] 翻转视频
		Jni.sendString(window, uchType, true, Consts.COUNT_EX_SENSOR,
				Consts.TYPE_EX_SENSOR, custom);

		// [Neo] 更新设备
		Jni.sendString(window, uchType, true, Consts.COUNT_EX_UPDATE,
				Consts.TYPE_EX_UPDATE, null);
	}

	/**
	 * 发送聊天命令，参考 {@link JVSUDT#JVC_SendTextData(int, byte, int, int)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param size
	 * @param flag
	 */
	public static native boolean sendTextData(int window, byte uchType,
			int size, int flag);

	/**
	 * 发送命令，参考 {@link JVSUDT#JVC_SendCMD(int, byte, byte[], int)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param data
	 * @param size
	 * @return
	 */
	public static native int sendCmd(int window, byte uchType, byte[] data,
			int size);

	/**
	 * 设置 wifi，参考
	 * {@link JVSUDT#JVC_SetWifi(int, byte, String, String, int, int)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param ssid
	 * @param password
	 * @param flag
	 * @param tag
	 */
	public static native boolean setWifi(int window, byte uchType, String ssid,
			String password, int flag, int tag);

	/**
	 * 保存 wifi 配置，参考
	 * {@link JVSUDT#JVC_SaveWifi(int, byte, String, String, int, int, String, String)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param ssid
	 * @param password
	 * @param flag
	 * @param type
	 * @param auth
	 * @param enc
	 */
	public static native boolean saveWifi(int window, byte uchType,
			String ssid, String password, int flag, int type, String auth,
			String enc);

	/**
	 * 设置 AP，参考 {@link JVSUDT#JVC_ManageAP(int, byte, String)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param json
	 */
	public static native boolean setAccessPoint(int window, byte uchType,
			String json);

	/**
	 * 设置 DHCP，参考
	 * {@link JVSUDT#JVC_SetDHCP(int, byte, int, String, String, String, String)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param dhcp
	 * @param ip
	 * @param mask
	 * @param gateway
	 * @param dns
	 * @return
	 */
	public static native boolean setDhcp(int window, byte uchType, int dhcp,
			String ip, String mask, String gateway, String dns);

	/**
	 * 修改码流，参考 {@link JVSUDT#JVC_ChangeStreams(int, byte, byte[])}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param cmd
	 */
	public static native boolean changeStream(int window, byte uchType,
			String cmd);

	/**
	 * 设置设备名称，参考 {@link JVSUDT#JVC_SetDeviceName(int, byte, byte[])}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param json
	 */
	public static native boolean setDeviceName(int window, byte uchType,
			String cmd);

	/**
	 * 设置存储地址，参考 {@link JVSUDT#JVC_SetStorage(int, byte, byte[])}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param cmd
	 */
	public static native boolean setStorage(int window, byte uchType, String cmd);

	/**
	 * 翻转视频，参考 {@link JVSUDT#JVC_TurnVideo(int, byte, byte[])}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 * @param cmd
	 */
	public static native boolean rotateVideo(int window, byte uchType,
			String cmd);

	/**
	 * 设备升级，参考 {@link JVSUDT#JVC_DeviceUpdate(int, byte)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param window
	 *            窗口索引
	 * @param uchType
	 */
	public static native boolean updateDevice(int window, byte uchType);

	/**
	 * 启用底层日志打印，参考 {@link JVSUDT#JVC_EnableLog(boolean)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param enable
	 */
	public static native void enableLog(boolean enable);

	/**
	 * 删除底层保存的错误日志
	 * 
	 */
	public static native void deleteLog();

	/**
	 * 设置日志和提示信息区域语言，参考 {@link JVSUDT#JVC_SetLanguage(int)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param typeId
	 *            参考 {@link JVNetConst#JVN_LANGUAGE_ENGLISH}，
	 *            {@link JVNetConst#JVN_LANGUAGE_CHINESE}
	 */
	public static native void setLanguage(int typeId);

	/**
	 * 修改服务器域名，参考 {@link JVSUDT#JVC_SetDomainName(String, String)}
	 * 
	 * // [Neo] TODO 未验证
	 * 
	 * @param domainName
	 * @param pathName
	 * @return
	 */
	public static native boolean changeDomain(String domainName, String pathName);

	/**
	 * 修改指定窗口播放标识位，参考 {@link JVSUDT#ChangePlayFalg(int, int)}
	 * 
	 * @param window
	 *            窗口索引
	 * @param enable
	 * @return
	 */
	public static native boolean enablePlayback(int window, boolean enable);

	/**
	 * 获取指定窗口是否处于远程回放状态
	 * 
	 * @param window
	 *            窗口索引
	 * @return
	 */
	public static native boolean isPlayback(int window);

	/**
	 * 修改指定窗口音频标识位
	 * 
	 * @param window
	 *            窗口索引
	 * @param enable
	 *            是否播放 Normaldata 的音频数据
	 * @return
	 */
	public static native boolean enablePlayAudio(int window, boolean enable);

	/**
	 * 获取指定窗口是否正在播放音频
	 * 
	 * @param window
	 *            窗口索引
	 * @param enable
	 * @return
	 */
	public static native boolean isPlayAudio(int window);

	/**
	 * 设置显示图像的顶点坐标(坐标系原点在 Surface 左下顶点)和长宽
	 * 
	 * @param window
	 *            窗口索引
	 * @param left
	 *            图像左坐标
	 * @param bottom
	 *            图像底坐标
	 * @param width
	 *            图像宽
	 * @param height
	 *            图像高
	 * @return
	 */
	public static native boolean setViewPort(int window, int left, int bottom,
			int width, int height);

	/**
	 * 初始化音频编码
	 * 
	 * @param type
	 *            类型，amr/alaw/ulaw，参考 {@link Consts#JAE_ENCODER_SAMR},
	 *            {@link Consts#JAE_ENCODER_ALAW},
	 *            {@link Consts#JAE_ENCODER_ULAW}
	 * @param sampleRate
	 * @param channelCount
	 * @param bitCount
	 * @param block
	 *            PCM 640
	 * @return
	 */
	public static native boolean initAudioEncoder(int type, int sampleRate,
			int channelCount, int bitCount, int block);

	/**
	 * 编码一帧
	 * 
	 * @param data
	 * @return 失败的话返回 null
	 */
	public static native byte[] encodeAudio(byte[] data);

	/**
	 * 销毁音频编码，如果要切换编码参数，必须销毁的重新创建
	 * 
	 * @return
	 */
	public static native boolean deinitAudioEncoder();

	/**
	 * 生成声波配置数据
	 * 
	 * @param data
	 */
	public static native void genVoice(String data);

	/**
	 * TCP 连接，参考
	 * {@link JVSUDT#JVC_TCPConnect(int, int, String, int, String, String, int, String, boolean, int, int)}
	 * 
	 * // [Neo] TODO 未实现
	 * 
	 * @param window
	 *            窗口索引
	 * @param channel
	 *            通道号，从 0 开始
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 * @param cloudSeeId
	 * @param groupId
	 * @param isLocalDetect
	 * @param connectType
	 * @param turnType
	 * 
	 */
	public static native boolean tcpConnect(int window, int channel, String ip,
			int port, String username, String password, int cloudSeeId,
			String groupId, boolean isLocalDetect, int connectType, int turnType);

}
