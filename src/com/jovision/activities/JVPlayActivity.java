package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.ScreenAdapter;
import com.jovision.adapters.StreamAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.bean.WifiAdmin;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyGestureDispatcher;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.PlayUtil;

public class JVPlayActivity extends PlayActivity implements
		PlayWindowManager.OnUiListener {

	private static final int WHAT_CHECK_SURFACE = 0x20;
	private static final int WHAT_RESTORE_UI = 0x21;
	private static final int WHAT_PLAY_STATUS = 0x22;
	private static final int WHAT_SHOW_PROGRESS = 0x23;
	private static final int WHAT_DISMISS_PROGRESS = 0x24;
	private static final int WHAT_FINISH = 0x25;

	private static final int ARG2_STATUS_CONNECTING = 0x01;
	private static final int ARG2_STATUS_CONNECTED = 0x02;
	private static final int ARG2_STATUS_BUFFERING = 0x03;
	private static final int ARG2_STATUS_DISCONNECTED = 0x04;
	private static final int ARG2_STATUS_HAS_CONNECTED = 0x05;
	private static final int ARG2_STATUS_CONN_OVERFLOW = 0x06;
	private static final int ARG2_STATUS_UNKNOWN = 0x07;
	private static final int STOP_AUDIO_GATHER = 0x08;
	private static final int START_AUDIO_GATHER = 0x09;

	private static final int DELAY_CHECK_SURFACE = 500;
	private static final int DELAY_DOUBLE_CHECKER = 500;
	private static final int CONNECTION_MIN_PEROID = 300;
	private static final int DISCONNECTION_MIN_PEROID = 100;
	private static final int RESUME_VIDEO_MIN_PEROID = 100;

	private boolean isQuit;
	private boolean isBlockUi;

	private int lastItemIndex;
	private int lastClickIndex;

	private Timer doubleClickTimer;
	private boolean isDoubleClickCheck;

	private ArrayList<Device> deviceList;
	private ArrayList<Channel> channelList;

	private PlayWindowManager manager;
	private ArrayList<Channel> currentPageChannelList;

	private ArrayList<Channel> connectChannelList;
	private ArrayList<Channel> disconnectChannelList;

	private MyPagerAdapter adapter;

	private boolean showingDialog = false;

	private int selectedScreen = 4;// 默认分四屏
	private int currentScreen = 1;// 当前分屏数

	private final int ONE_SCREEN = 1;// 单屏
	private final int FOUR_SCREEN = 4;// 四屏
	private final int NINE_SCREEN = 9;// 九屏
	private final int SIXTEEN_SCREEN = 16;// 十六屏

	private int startWindowIndex;

	/** intent传递过来的设备和通道下标 */
	private int deviceIndex;
	private int channelOfChannel;

	private boolean needToast = false;

	private WifiAdmin wifiAdmin;
	private String ssid;

	private Dialog initDialog;
	private TextView dialogCancel;
	private TextView dialogCompleted;
	private TextView devicepwd_name;
	private EditText devicepwd_nameet;
	private ImageView devicepwd_nameet_cancle;
	private EditText devicepwd_passwordet;
	private ImageView devicepwd_password_cancleI;
	private ImageView dialogpwd_cancle_img;

	/** 基于断开视频不走回调加此list */
	private MyList<Message> msgList = new MyList<Message>(0);

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		if (isQuit) {
			return;
		}

		switch (what) {
		case PLAY_AUDIO_WHAT: {
			// public static final int ARG1_PLAY = 0x01;
			// public static final int ARG1_RECORD = 0x02;
			//
			// public static final int ARG2_START = 0x01;
			// public static final int ARG2_FINISH = 0x02;
			// PLAY_AUDIO_WHAT:what=38,arg1=1,arg2=2,obj=null
			MyLog.v(TAG, "PLAY_AUDIO_WHAT:what=" + what + ",arg1=" + arg1
					+ ",arg2=" + arg2 + ",obj=" + obj);
			break;
		}
		case Consts.CALL_LAN_SEARCH: {
			break;
		}

		case Consts.CALL_CONNECT_CHANGE: {
			MyLog.i(TAG, "CALL_CONNECT_CHANGE:what=" + what + ",arg1=" + arg1
					+ ",arg2=" + arg2 + ",obj=" + obj);
			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
				channel.setConnecting(false);
			}

			if (null == channel) {
				MyLog.e(Consts.TAG_LOGICAL, "connect changed bad size = "
						+ arg1);
				return;
			}

			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				channel.setConnected(true);

				if (currentPageChannelList.contains(channel)) {
					if (channel.isPaused()) {
						resumeChannel(channel);
					}
				}

				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}

			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK:
				// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED:
				// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT:
				// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP:
				channel.setPaused(true);
				msgList.add(arg1, handler.obtainMessage(what, arg1, arg2, obj));
				break;
			case Consts.BAD_NOT_CONNECT: {
				channel.setConnected(false);
				// TODO
				channel.setAgreeTextData(false);
				channel.setNewIpcFlag(false);
				channel.setOMX(false);
				channel.setSingleVoice(false);
				channel.setScreenTag(-1);
				channel.setStreamTag(-1);
				channel.setStorageMode(-1);
				channel.setSupportVoice(false);
				if (null != msgList && null != msgList.get(arg1)) {
					Message msg = new Message();
					msg.arg1 = msgList.get(arg1).arg1;
					msg.arg2 = msgList.get(arg1).arg2;
					msg.what = msgList.get(arg1).what;
					msg.obj = msgList.get(arg1).obj;
					handler.sendMessage(msg);
				}

				break;
			}

			// 3 -- 不必要重复连接
			case JVNetConst.NO_RECONNECT: {
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}

			// 5 -- 没有连接
			case JVNetConst.NO_CONNECT: {
				channel.setPaused(true);
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}

			// 8 -- 断开连接失败
			case JVNetConst.DISCONNECT_FAILED: {
				channel.setPaused(true);
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}

			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				channel.setPaused(true);
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}

			default:
				break;
			}
			break;
		}

		default:
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		if (isQuit) {
			return;
		}

		switch (what) {
		case STOP_AUDIO_GATHER: {// 停止采集音频数据
			GATHER_AUDIO_DATA = false;
			break;
		}
		case START_AUDIO_GATHER: {// 开始采集音频数据
			GATHER_AUDIO_DATA = true;
			break;
		}
		case WHAT_CHECK_SURFACE: {
			boolean hasNull = false;
			boolean hasChannel = false;

			int size = currentPageChannelList.size();
			for (int i = 0; i < size; i++) {
				hasChannel = true;
				if (null == currentPageChannelList.get(i).getSurface()) {
					hasNull = true;
					break;
				}
			}

			if (hasChannel && false == hasNull) {
				new Connecter().start();
			} else {
				handler.sendMessageDelayed(
						handler.obtainMessage(WHAT_CHECK_SURFACE, arg1, arg2),
						DELAY_CHECK_SURFACE);
			}
			break;
		}

		case WHAT_RESTORE_UI: {
			isBlockUi = false;
			if (null != viewPager) {
				viewPager.setDisableSliding(isBlockUi);
			}
			break;
		}

		case WHAT_SHOW_PROGRESS: {
			createDialog("");
			break;
		}
		case WHAT_FINISH: {
			// isQuit = true;
			// finish();
			dismissDialog();
			break;
		}
		case WHAT_DISMISS_PROGRESS: {

			dismissDialog();
			break;
		}

		case Consts.CALL_CONNECT_CHANGE: {
			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}
			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				loadingState(arg1, R.string.connecting_buffer1,
						JVConst.PLAY_CONNECTING_BUFFER);
				break;
			}

			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}
			// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED: {
				try {
					JSONObject connectObj = new JSONObject(obj.toString());
					String errorMsg = connectObj.getString("msg");
					if ("password is wrong!".equalsIgnoreCase(errorMsg)
							|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
						loadingState(arg1, R.string.connfailed_auth,
								JVConst.PLAY_DIS_CONNECTTED);
						if (ONE_SCREEN == currentScreen) {
							passErrorDialog(arg1);
						}

					} else if ("channel is not open!"
							.equalsIgnoreCase(errorMsg)) {// 无该通道服务
						loadingState(arg1, R.string.connfailed_channel_notopen,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("connect type invalid!"
							.equalsIgnoreCase(errorMsg)) {// 连接类型无效
						loadingState(arg1, R.string.connfailed_type_invalid,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
						loadingState(arg1, R.string.connfailed_maxcount,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
						loadingState(arg1, R.string.connfailed_timeout,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("check password timeout!"
							.equalsIgnoreCase(errorMsg)) {// 验证密码超时
						loadingState(arg1,
								R.string.connfailed_checkpass_timout,
								JVConst.PLAY_DIS_CONNECTTED);
					} else {// "Connect failed!"
						loadingState(arg1, R.string.connect_failed,
								JVConst.PLAY_DIS_CONNECTTED);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					loadingState(arg1, R.string.closed,
							JVConst.PLAY_DIS_CONNECTTED);
				}

				// resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			default:
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				break;
			}
			break;
		}

		case Consts.CALL_NORMAL_DATA: {
			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}

			loadingState(arg1, R.string.connecting_buffer2,
					JVConst.PLAY_CONNECTING_BUFFER);

			if (currentScreen > FOUR_SCREEN && !channel.isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_ONLYI, new byte[0],
						0);
				channel.setSendCMD(true);
			} else if (currentScreen <= FOUR_SCREEN && channel.isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_FULL, new byte[0],
						0);
				channel.setSendCMD(true);
			}

			MyLog.i("NORMALDATA", obj.toString());
			int newWidth = 0;
			int newHeight = 0;

			try {
				JSONObject jobj;
				jobj = new JSONObject(obj.toString());
				int type = jobj.optInt("device_type");
				if (null != jobj) {
					channel.getParent().setType(type);
					if (Consts.DEVICE_TYPE_IPC == type
							|| Consts.DEVICE_TYPE_DVR == type
							|| Consts.DEVICE_TYPE_NVR == type) {
						channel.getParent().setCard(false);
					} else {
						channel.getParent().setCard(true);
					}

					if (Consts.DEVICE_TYPE_IPC == type) {
						channel.getParent().setHomeProduct(true);
						// channel.setSingleVoice(true);
					} else {
						channel.getParent().setHomeProduct(false);
						// channel.setSingleVoice(false);
					}
					channel.getParent().setJFH(jobj.optBoolean("is_jfh"));
					channel.getParent().setO5(jobj.optBoolean("is05"));
					channel.setAudioType(jobj.getInt("audio_type"));
					channel.setAudioByte(jobj.getInt("audio_bit"));
					channel.setAudioEncType(jobj.getInt("audio_enc_type"));

					if (8 == channel.getAudioByte()
							&& Consts.DEVICE_TYPE_DVR == type) {
						channel.setSupportVoice(false);
						// [Neo] TODO 不支持此设备的语音对讲，可监听

						// private static final int AUDIO_WHAT = 0x51;
						// MyAudio audio = MyAudio.getIntance(AUDIO_WHAT, this);
						//
						// // [Neo] 开启音频监听
						// Jni.enablePlayAudio(index, true);
						// audio.startPlay(channelList.get(index).getAudioBitCount(),
						// true);
						//
						// // [Neo] 关闭音频监听
						// Jni.enablePlayAudio(index, false);
						// audio.stopPlay();
						//
						// // [Neo] 开启语音对讲
						// Channel channel = channelList.get(index);
						// audio.startPlay(bit, true);
						// audio.startRec(channel.getAudioEncType(),
						// channel.getAudioByte(), channel.getAudioBlock(),
						// true);
						//
						// // [Neo] 关闭语音对讲
						// audio.stopPlay();
						// audio.stopRec();
						//
						// // [Neo] 将音频填入缓存队列
						// audio.put(data);

					}

					newWidth = jobj.getInt("width");
					newHeight = jobj.getInt("height");
					// if (!jobj.optBoolean("is05")) {// 提示不支持04版本解码器
					// if (!Jni.disconnect(arg1)) {
					// loadingState(arg1, R.string.closed,
					// JVConst.PLAY_DIS_CONNECTTED);
					// }
					// if (ONE_SCREEN == currentScreen
					// && arg1 == lastClickIndex) {
					// if (!MySharedPreference
					// .getBoolean(Consts.DIALOG_NOT_SUPPORT04)) {
					// errorDialog(
					// Consts.DIALOG_NOT_SUPPORT04,
					// getResources().getString(
					// R.string.not_support_old));
					// }
					// }
					// }

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// TODO 不应该只对比宽高
			if (newWidth != channel.getWidth()
					|| newHeight != channel.getHeight()) {// 宽高变了才发文本聊天
				if (ONE_SCREEN == currentScreen && arg1 == lastClickIndex) {
					channel.setHeight(newHeight);
					channel.setWidth(newWidth);
					// 是IPC，发文本聊天请求
					if (channel.getParent().isHomeProduct()) {

						if (channel.isAgreeTextData()) {
							// 获取主控码流信息请求
							Jni.sendTextData(arg1, JVNetConst.JVN_RSP_TEXTDATA,
									8, JVNetConst.JVN_STREAM_INFO);
						} else {
							// 请求文本聊天
							Jni.sendBytes(arg1, JVNetConst.JVN_REQ_TEXT,
									new byte[0], 8);
						}

					}
				}
			} else {
				// if (channel.isHasGotParams()) {
				// showFunc(channel, currentScreen, lastClickIndex);
				// } else {
				// 是IPC，发文本聊天请求
				if (channel.isAgreeTextData()) {
					// 获取主控码流信息请求
					Jni.sendTextData(arg1, JVNetConst.JVN_RSP_TEXTDATA, 8,
							JVNetConst.JVN_STREAM_INFO);
				} else {
					// 请求文本聊天
					Jni.sendBytes(arg1, JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
				}
				// }

			}

			if (recoding) {
				// showTextToast(R.string.video_repaked);
				PlayUtil.videoRecord(lastClickIndex);
			}

			// }

			break;
		}

		case Consts.CALL_DOWNLOAD: {
			MyLog.d(Consts.TAG_PLAY, "download: " + arg1 + ", " + arg2 + ", "
					+ obj);
			break;
		}

		case Consts.CALL_GOT_SCREENSHOT: {
			MyLog.i(TAG, "CALL_GOT_SCREENSHOT:what=" + what + ";arg1=" + arg1
					+ ";arg2=" + arg2);
			switch (arg2) {
			case Consts.BAD_SCREENSHOT_NOOP:
				PlayUtil.prepareAndPlay();
				showTextToast(Consts.CAPTURE_PATH);
				break;
			case Consts.BAD_SCREENSHOT_INIT:
				showTextToast(R.string.str_capture_error);
				break;
			case Consts.BAD_SCREENSHOT_CONV:
				showTextToast(R.string.str_capture_error);
				break;
			case Consts.BAD_SCREENSHOT_OPEN:
				showTextToast(R.string.str_capture_error);
				break;
			default:
				break;
			}
			break;
		}

		case Consts.CALL_PLAY_AUDIO: {
			if (!GATHER_AUDIO_DATA) {
				break;
			}

			if (null != obj && null != playAudio) {
				if (AUDIO_SINGLE) {// 单向对讲长按才发送语音数据
					if (VOICECALL_LONG_CLICK) {
						// 长按时只发送语音，不接收语音
					} else {
						byte[] data = (byte[]) obj;
						// audioQueue.offer(data);
						// [Neo] 将音频填入缓存队列
						playAudio.put(data);
					}
				} else {// 双向对讲直接播放设备传过来的语音
					byte[] data = (byte[]) obj;
					// audioQueue.offer(data);
					// [Neo] 将音频填入缓存队列
					playAudio.put(data);
				}
			}

			break;
		}

		// [Neo] removed
		// case Consts.CALL_PLAY_DOOMED: {
		// if (Consts.HDEC_BUFFERING == arg2) {
		// loadingState(arg1, R.string.connecting_buffer2,
		// JVConst.PLAY_CONNECTING_BUFFER);
		// }
		// break;
		// }

		case WHAT_PLAY_STATUS: {
			switch (arg2) {
			case ARG2_STATUS_CONNECTING:
				loadingState(arg1, R.string.connecting, JVConst.PLAY_CONNECTING);
				break;

			case ARG2_STATUS_CONNECTED:
				loadingState(arg1, R.string.connecting_buffer1,
						JVConst.PLAY_CONNECTING_BUFFER);
				break;

			case ARG2_STATUS_BUFFERING:
				loadingState(arg1, R.string.connecting_buffer2,
						JVConst.PLAY_CONNECTING_BUFFER);
				break;

			case ARG2_STATUS_DISCONNECTED:
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				break;

			case ARG2_STATUS_HAS_CONNECTED:
				loadingState(arg1, R.string.connfailed_timeout,
						JVConst.PLAY_DIS_CONNECTTED);
				break;

			case ARG2_STATUS_CONN_OVERFLOW:
				loadingState(arg1, R.string.overflow,
						JVConst.PLAY_DIS_CONNECTTED);
				break;

			case ARG2_STATUS_UNKNOWN:
				loadingState(arg1, R.string.closed, JVConst.PLAY_STATUS_UNKNOWN);
				break;

			default:
				break;
			}
			break;
		}

		case Consts.CALL_TEXT_DATA: {
			MyLog.i(TAG, "CALL_TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 获取主控码流信息请求
				Jni.sendTextData(arg1, JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_STREAM_INFO);
				channel.setAgreeTextData(true);
				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天
				channel.setAgreeTextData(false);
				break;

			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				String allStr = obj.toString();

				MyLog.v(TAG, "文本数据--" + allStr);
				try {
					JSONObject dataObj = new JSONObject(allStr);

					switch (dataObj.getInt("flag")) {
					// 远程配置请求，获取到配置文本数据
					case JVNetConst.JVN_REMOTE_SETTING: {

						break;
					}
					case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
						break;
					case Consts.FLAG_GET_MD_STATE:// 慧通获取移动侦测
						String motionJSON = dataObj.getString("msg");
						HashMap<String, String> motionMap = ConfigUtil
								.genMsgMap(motionJSON);
						if (null != motionMap.get("bMDEnable")) {
							int bMDEnable = Integer.valueOf(motionMap
									.get("bMDEnable"));
							if (bMDEnable == 0) {
								ht_motion
										.setBackgroundResource(R.drawable.ht_motiondetec_close);
								Consts.MOTION_DETECTION_FLAG = false;
							} else if (bMDEnable == 1) {
								Consts.MOTION_DETECTION_FLAG = true;
								ht_motion
										.setBackgroundResource(R.drawable.ht_motiondetec_open);
							}
						}
						break;
					case Consts.FLAG_CAPTURE_FLASH:// 慧通抓拍请求
						String captureJSON = dataObj.getString("result");
						Log.i("TAG", captureJSON);
						// if (null!=captureMap.get("result")) {
						int result = Integer.valueOf(captureJSON);
						if (result == Consts.RESULT_SUCCESS) {
							Log.i("TAG", "抓拍成功");
						}
						if (result == Consts.RESULT_NO_FILENAME) {
							Log.i("TAG", "抓拍路径错误");
						}
						if (result == Consts.RESULT_OPEN_FAILED) {
							Log.i("TAG", "抓拍失败");
						}
						// }
						break;
					case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求
						MyLog.i(TAG, "JVN_STREAM_INFO:TEXT_DATA: " + what
								+ ", " + arg1 + ", " + arg2 + ", " + obj);
						String streamJSON = dataObj.getString("msg");
						// HashMap<String, String> streamCH1 =
						// ConfigUtil.getCH1("CH1",streamJSON);

						HashMap<String, String> streamMap = ConfigUtil
								.genMsgMap(streamJSON);
						if (null != streamMap) {
							if (null != streamMap.get("effect_flag")
									&& !"".equalsIgnoreCase(streamMap
											.get("effect_flag"))) {

								int effect_flag = Integer.parseInt(streamMap
										.get("effect_flag"));
								MyLog.v(TAG, "effect_flag=" + effect_flag);
								channel.setEffect_flag(Integer
										.parseInt(streamMap.get("effect_flag")));

								if (0 == (0x04 & effect_flag)) {
									channel.setScreenTag(Consts.SCREEN_NORMAL);
								} else {
									channel.setScreenTag(Consts.SCREEN_OVERTURN);
								}
								// TODO
							}
							// if (null != streamMap.get("FlashMode")) {
							// int FlashMode = Integer.valueOf(streamMap
							// .get("FlashMode"));
							// if (FlashMode == 0) {
							// ht_fight.setBackgroundResource(R.drawable.ht_flight_auto);
							// } else if (FlashMode == 1) {
							// ht_fight.setBackgroundResource(R.drawable.ht_flight_open);
							// } else if (FlashMode == 2) {
							// ht_fight.setBackgroundResource(R.drawable.ht_flight_close);
							// }
							// Consts.FLIGHT_FLAG = FlashMode;
							// }
							if (null != streamMap.get("MobileQuality")
									&& !"".equalsIgnoreCase(streamMap
											.get("MobileQuality"))) {
								MyLog.v(TAG,
										"MobileQuality="
												+ streamMap
														.get("MobileQuality"));
								channel.setStreamTag(Integer.parseInt(streamMap
										.get("MobileQuality")));
								channel.setNewIpcFlag(true);
							} else {

								if (null != streamMap.get("MobileCH")
										&& "2".equalsIgnoreCase(streamMap
												.get("MobileCH"))) {
									MyLog.v(TAG,
											"MobileCH="
													+ streamMap.get("MobileCH"));

									String strParam = streamJSON
											.substring(streamJSON
													.lastIndexOf("[CH2];") + 6,
													streamJSON.length());
									HashMap<String, String> ch2Map = ConfigUtil
											.genMsgMap1(strParam);
									int width = Integer.valueOf(ch2Map
											.get("width"));
									// int height = Integer.valueOf(ch2Map
									// .get("height"));
									// if (720 == width && 480 == height) {
									// channel.setStreamTag(2);
									// } else if (352 == width && 288 == height)
									// {
									// channel.setStreamTag(3);
									// }
									if (624 <= width) {
										channel.setStreamTag(2);
									} else {
										channel.setStreamTag(3);
									}
									channel.setNewIpcFlag(false);
								}
								// if (null != streamMap.get("MobileStreamQos")
								// && !"".equalsIgnoreCase(streamMap
								// .get("MobileStreamQos"))) {
								// MyLog.v(TAG,
								// "MobileStreamQos="
								// + streamMap
								// .get("MobileStreamQos"));
								// channel.setStreamTag(Integer
								// .parseInt(streamMap
								// .get("MobileStreamQos")));
								// }
							}

							if (null != streamMap.get("storageMode")
									&& !"".equalsIgnoreCase(streamMap
											.get("storageMode"))) {
								MyLog.v(TAG,
										"storageMode="
												+ streamMap.get("storageMode"));
								channel.setStorageMode(Integer
										.parseInt(streamMap.get("storageMode")));
							}

							if (null != streamMap.get("MobileCH")
									&& "2".equalsIgnoreCase(streamMap
											.get("MobileCH"))) {
								MyLog.v(TAG,
										"MobileCH=" + streamMap.get("MobileCH"));
								channel.setSingleVoice(true);
							}
							// channel.setHasGotParams(true);
							showFunc(channel, currentScreen, lastClickIndex);
						}

						break;
					case JVNetConst.EX_WIFI_AP_CONFIG:// 11 ---新wifi配置流程
						break;
					case JVNetConst.JVN_WIFI_SETTING_SUCCESS:// 4-- wifi配置成功
						break;
					case JVNetConst.JVN_WIFI_SETTING_FAILED:// 5--WIFI配置失败
						break;
					case JVNetConst.JVN_WIFI_IS_SETTING:// -- 6 正在配置wifi

						break;
					case JVNetConst.JVN_RECORD_RESULT:// -- 100 录像模式切换回调
						dismissDialog();
						// 录像模式
						if (Consts.STORAGEMODE_NORMAL == channel
								.getStorageMode()) {
							channel.setStorageMode(Consts.STORAGEMODE_ALARM);
						} else if (Consts.STORAGEMODE_ALARM == channelList.get(
								arg1).getStorageMode()) {
							channel.setStorageMode(Consts.STORAGEMODE_NORMAL);
						}
						showFunc(channel, currentScreen, lastClickIndex);
						break;
					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
			break;
		}

		case Consts.CALL_CHAT_DATA: {
			dismissDialog();
			MyLog.i(TAG, "CALL_CHAT_DATA:arg1=" + arg1 + ",arg2=" + arg2);
			switch (arg2) {
			// 语音数据
			case JVNetConst.JVN_RSP_CHATDATA: {
				MyLog.i(TAG, "JVN_RSP_CHATDATA");
				break;
			}

			// 同意语音请求
			case JVNetConst.JVN_RSP_CHATACCEPT: {
				Channel channel = channelList.get(lastClickIndex);
				if (channel.isSingleVoice()) {
					showTextToast(R.string.voice_tips2);
				}
				channel.setVoiceCall(true);
				VOICECALLING = true;
				voiceCallSelected(true);
				// recorder.start(channelList.get(lastClickIndex).getAudioType(),
				// channelList.get(lastClickIndex).getAudioByte());

				if (Consts.JAE_ENCODER_G729 == channel.getAudioEncType()) {
					// 开启语音对讲
					playAudio.startPlay(16, true);
					playAudio.startRec(channel.getIndex(),
							channel.getAudioEncType(), 16,
							channel.getAudioBlock(), true);
				} else {
					// 开启语音对讲
					playAudio.startPlay(channel.getAudioByte(), true);
					playAudio.startRec(channel.getIndex(),
							channel.getAudioEncType(), channel.getAudioByte(),
							channel.getAudioBlock(), true);

				}
				break;
			}

			// 暂停语音聊天
			case JVNetConst.JVN_CMD_CHATSTOP: {
				if (realStop) {
					realStop = false;
				} else {
					bottombut5.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.video_talkback_icon));
					showTextToast(R.string.has_calling);
				}

				break;
			}
			}
			break;
		}

		case Consts.CALL_QUERY_DEVICE: {
			MyLog.e(Consts.TAG_PLAY,
					"jvplayactivity  query 7777777777777777777777---------------------------"
							+ obj);
			break;
		}

		case Consts.CALL_HDEC_TYPE: {
			// String objStr = obj.toString();
			break;
		}

		case Consts.CALL_NEW_PICTURE: {
			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}
			loadingState(arg1, 0, JVConst.PLAY_CONNECTTED);
			if (!channel.isOMX() && arg2 == Consts.DECODE_SOFT
					&& ONE_SCREEN == currentScreen) {
				if (needToast) {
					showTextToast(R.string.not_support_oxm);
					needToast = false;
				}

			}
			dismissDialog();

			if (Consts.DECODE_OMX == arg2) {
				channel.setOMX(true);
			} else if (Consts.DECODE_SOFT == arg2) {
				channel.setOMX(false);
			}
			needToast = false;
			showFunc(channel, currentScreen, lastClickIndex);
			MyLog.i(Consts.TAG_PLAY, "new Frame I: window = " + arg1
					+ ", omx = " + arg2);
			break;
		}

		case Consts.CALL_STAT_REPORT: {
			try {
				// MyLog.e(Consts.TAG_PLAY, obj.toString());
				JSONArray array = new JSONArray(obj.toString());
				JSONObject object = null;

				int size = array.length();
				StringBuilder sBuilder = new StringBuilder(1024 * size);
				for (int i = 0; i < size; i++) {
					object = array.getJSONObject(i);
					String msg = String
							.format("%d(%s|%s), kbps=%.0fK, fps=%.0f+%.0f/%.0f/%d, %.0fms+%.0fms, left=%2d,\n",// \t\t%dx%d;
									// audio(%d):
									// kbps=%.2fK,
									// fps=%.0f/%.0f,
									// %.2fms+%.2fms
									object.getInt("window"),
									(object.getBoolean("is_turn") ? "TURN"
											: "P2P"),
									(object.getBoolean("is_omx") ? "HD" : "ff"),
									object.getDouble("kbps"), object
											.getDouble("decoder_fps"), object
											.getDouble("jump_fps"), object
											.getDouble("network_fps"), object
											.getInt("space"), object
											.getDouble("decoder_delay"), object
											.getDouble("render_delay"), object
											.getInt("left")
							// ,object.getInt("width"), object
							// .getInt("height"), object
							// .getInt("audio_type"), object
							// .getDouble("audio_kbps"), object
							// .getDouble("audio_decoder_fps"),
							// object.getDouble("audio_network_fps"),
							// object.getDouble("audio_decoder_delay"),
							// object.getDouble("audio_play_delay")
							);
					sBuilder.append(msg).append("\n");

					// [Neo] you fool
					if (ONE_SCREEN == currentScreen) {
						// currentPageChannelList.get(0).setOMX(
						// object.getBoolean("is_omx"));

						int window = object.getInt("window");
						if (window == lastClickIndex) {
							currentKbps.setText(String.format("%.0fkBps",
									object.getDouble("kbps")));
						}
					}

				}
				linkMode.setText(sBuilder.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		}

		// 下拉选择多屏
		case JVConst.WHAT_SELECT_SCREEN: {
			selectedScreen = screenList.get(arg1);
			screenAdapter.selectIndex = arg1;
			screenAdapter.notifyDataSetChanged();
			popScreen.dismiss();
			changeWindow(selectedScreen);

			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}

			showFunc(channel, selectedScreen, lastClickIndex);
			break;
		}
		case StreamAdapter.STREAM_ITEM_CLICK: {// 码流切换

			Channel channel = null;
			if (lastClickIndex < channelList.size()) {
				channel = channelList.get(lastClickIndex);
			}

			if (null == channel) {
				return;
			}

			if (channel.isNewIpcFlag()) {
				int index = arg1 + 1;
				String params = "MobileQuality=" + index + ";";// "MobileStreamQos="
				// + index +
				// 2014-11-26
				// 去掉MobileStreamQos字段

				MyLog.v(TAG, "changeStream--" + params);
				Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
						false, 0, Consts.TYPE_SET_PARAM, params);
			} else {
				if (0 == arg1) {
					Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
							false, 0, Consts.TYPE_SET_PARAM, String.format(
									Consts.FORMATTER_SET_BPS_FPS, 2, 1280, 720,
									1024, 15, 1));
				} else if (1 == arg1) {
					Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
							false, 0, Consts.TYPE_SET_PARAM, String.format(
									Consts.FORMATTER_SET_BPS_FPS, 2, 720, 480,
									512, 20, 1));
				} else if (2 == arg1) {
					Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
							false, 0, Consts.TYPE_SET_PARAM, String.format(
									Consts.FORMATTER_SET_BPS_FPS, 2, 352, 288,
									512, 25, 1));
				}
			}

			streamListView.setVisibility(View.GONE);
			break;
		}

		default:
			MyLog.e(Consts.TAG_PLAY, "NO switch:" + what);
			break;
		}
	}

	private void passErrorDialog(int index) {
		if (!showingDialog && index == lastClickIndex) {
			showingDialog = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					JVPlayActivity.this.getResources().getString(
							R.string.connfailed_auth_tips))
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
									showingDialog = false;
									initSummaryDialog(deviceList
											.get(deviceIndex));
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
									showingDialog = false;
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void initSummaryDialog(final Device device) {
		initDialog = new Dialog(JVPlayActivity.this, R.style.mydialog);
		View view = LayoutInflater.from(JVPlayActivity.this).inflate(
				R.layout.dialog_password, null);
		initDialog.setContentView(view);
		dialogpwd_cancle_img = (ImageView) view
				.findViewById(R.id.dialogpwd_cancle_img);
		dialogCancel = (TextView) view.findViewById(R.id.dialogpwd_cancel);
		dialogCompleted = (TextView) view
				.findViewById(R.id.dialogpwd_completed);
		devicepwd_name = (TextView) view.findViewById(R.id.devicepwd_namew);
		devicepwd_nameet = (EditText) view.findViewById(R.id.devicepwd_nameet);
		devicepwd_nameet_cancle = (ImageView) view
				.findViewById(R.id.devicepwd_nameet_cancle);
		devicepwd_passwordet = (EditText) view
				.findViewById(R.id.devicepwd_passwrodet);
		devicepwd_password_cancleI = (ImageView) view
				.findViewById(R.id.devicepwd_passwrodet_cancle);
		dialogpwd_cancle_img.setOnClickListener(myOnClickListener);
		devicepwd_nameet_cancle.setOnClickListener(myOnClickListener);
		devicepwd_password_cancleI.setOnClickListener(myOnClickListener);

		devicepwd_name.setText(device.getFullNo());
		devicepwd_nameet.setText(device.getUser());
		devicepwd_passwordet.setText(device.getPwd());
		initDialog.show();
		devicepwd_name.setFocusable(true);
		devicepwd_name.setFocusableInTouchMode(true);
		dialogCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initDialog.dismiss();
			}
		});
		dialogCompleted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设备用户名不为空
				if ("".equalsIgnoreCase(devicepwd_nameet.getText().toString())) {
					JVPlayActivity.this
							.showTextToast(R.string.login_str_device_account_notnull);
				}
				// 设备用户名验证
				else if (!ConfigUtil.checkDeviceUsername(devicepwd_nameet
						.getText().toString())) {
					JVPlayActivity.this.showTextToast(JVPlayActivity.this
							.getResources().getString(
									R.string.login_str_device_account_error));
				} else {
					ModifyDevTask task = new ModifyDevTask();
					String[] strParams = new String[5];
					strParams[1] = device.getFullNo();
					strParams[2] = devicepwd_nameet.getText().toString();
					strParams[3] = devicepwd_passwordet.getText().toString();
					strParams[4] = device.getNickName();
					task.execute(strParams);
				}
			}
		});
	}

	// 保存更改设备信息线程
	class ModifyDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int editRes = -1;
			try {
				if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 本地保存修改信息
					editRes = 0;
				} else {
					String name = statusHashMap.get(Consts.KEY_USERNAME);
					editRes = DeviceUtil.modifyDevice(name, params[1],
							params[4], params[2], params[3]);
				}
				if (0 == editRes) {
					deviceList.get(deviceIndex).setUser(params[2]);
					deviceList.get(deviceIndex).setPwd(params[3]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return editRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			if (0 == result) {
				CacheUtil.saveDevList(deviceList);
				JVPlayActivity.this
						.showTextToast(R.string.login_str_device_edit_success);
			} else {
				JVPlayActivity.this
						.showTextToast(R.string.login_str_device_edit_failed);
			}
			initDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initSettings() {
		TAG = "PlayA";

		isQuit = false;
		isOmx = false;
		isBlockUi = false;

		lastItemIndex = 0;
		lastClickIndex = 0;
		isDoubleClickCheck = false;

		connectChannelList = new ArrayList<Channel>();
		disconnectChannelList = new ArrayList<Channel>();

		try {
			wifiAdmin = new WifiAdmin(JVPlayActivity.this);

			// wifi打开的前提下,获取oldwifiSSID
			if (wifiAdmin.getWifiState()) {
				if (null != wifiAdmin.getSSID()) {
					if (wifiAdmin.getSSID().contains(Consts.IPC_TAG)) {
						ssid = wifiAdmin.getSSID().replace("\"", "")
								.replace(Consts.IPC_TAG, "");
					} else {
						ssid = null;
					}

				}
			}

			PlayUtil.setContext(JVPlayActivity.this);
			manager = PlayWindowManager.getIntance(this);
			manager.setArrowId(R.drawable.left, R.drawable.up,
					R.drawable.right, R.drawable.down);

			Intent intent = getIntent();
			deviceIndex = intent.getIntExtra("DeviceIndex", 0);
			channelOfChannel = intent.getIntExtra("ChannelofChannel", 0);
			playFlag = intent.getIntExtra("PlayFlag", 0);

			currentScreen = intent.getIntExtra("Screen", 1);
			if (Consts.PLAY_NORMAL == playFlag) {
				String devJsonString = intent
						.getStringExtra(Consts.KEY_PLAY_NORMAL);
				deviceList = Device.fromJsonArray(devJsonString);
			} else if (Consts.PLAY_DEMO == playFlag) {
				String devJsonString = intent
						.getStringExtra(Consts.KEY_PLAY_DEMO);
				deviceList = Device.fromJsonArray(devJsonString);
			} else if (Consts.PLAY_AP == playFlag) {
				String devJsonString = intent
						.getStringExtra(Consts.KEY_PLAY_AP);
				deviceList = Device.fromJsonArray(devJsonString);
			}

			startWindowIndex = 0;
			channelList = new ArrayList<Channel>();

			if (MySharedPreference.getBoolean("PlayDeviceMode")) {
				int size = deviceList.size();
				for (int i = 0; i < size; i++) {
					ArrayList<Channel> cList = deviceList.get(i)
							.getChannelList().toList();
					int csize = cList.size();

					if (i < deviceIndex) {
						startWindowIndex += csize;
					} else if (i == deviceIndex) {
						for (int j = 0; j < csize; j++) {
							if (cList.get(j).getChannel() < channelOfChannel) {
								startWindowIndex++;
							}
						}
					}
					channelList.addAll(cList);
				}
			} else {
				ArrayList<Channel> cList = deviceList.get(deviceIndex)
						.getChannelList().toList();
				int csize = cList.size();
				for (int j = 0; j < csize; j++) {
					if (cList.get(j).getChannel() < channelOfChannel) {
						startWindowIndex++;
					}
				}
				channelList.addAll(cList);
			}
			int size = channelList.size();
			for (int i = 0; i < size; i++) {
				manager.addChannel(channelList.get(i));
			}
			isDoubleClickCheck = false;
			lastClickIndex = channelList.get(startWindowIndex).getIndex();
			lastItemIndex = lastClickIndex;
			MyLog.i(Consts.TAG_XX, "JVPlay.init: startWindowIndex="
					+ startWindowIndex + "," + channelList.size()
					+ ", channel/index = "
					+ channelList.get(startWindowIndex).getChannel() + "/"
					+ channelList.get(startWindowIndex).getIndex());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		super.initUi();
		// //进播放如果是横屏，先转成竖屏
		// if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// }
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		wifiAdmin = new WifiAdmin(JVPlayActivity.this);

		// wifi打开的前提下,获取oldwifiSSID
		if (wifiAdmin.getWifiState()) {
			if (null != wifiAdmin.getSSID()) {
				if (wifiAdmin.getSSID().contains(Consts.IPC_TAG)) {
					ssid = wifiAdmin.getSSID().replace("\"", "")
							.replace(Consts.IPC_TAG, "");
				} else {
					ssid = null;
				}

			}
			currentMenu_v.setText(deviceList.get(deviceIndex).getNickName()
					+ "-" + channelList.get(lastClickIndex).getChannel());
		}
		/** 上 */
		varvoice_bg.setOnClickListener(myOnClickListener);
		back.setOnClickListener(myOnClickListener);
		left_btn_h.setOnClickListener(myOnClickListener);
		ht_fight.setOnClickListener(myOnClickListener);
		ht_motion.setOnClickListener(myOnClickListener);

		selectScreenNum.setOnClickListener(myOnClickListener);
		currentMenu.setOnClickListener(myOnClickListener);
		if (playFlag == Consts.PLAY_AP) {
			currentMenu_h.setText(deviceList.get(deviceIndex).getNickName());
			currentMenu.setText(R.string.video_check);
			selectScreenNum.setVisibility(View.GONE);
		} else {
			currentMenu_h.setText(deviceList.get(deviceIndex).getNickName());
			currentMenu.setText(R.string.str_video_play);
			selectScreenNum.setVisibility(View.VISIBLE);
		}

		linkMode.setVisibility(View.GONE);

		decodeBtn.setOnClickListener(myOnClickListener);
		videTurnBtn.setOnClickListener(myOnClickListener);
		rightFuncButton.setOnClickListener(myOnClickListener);
		right_btn_h.setOnClickListener(myOnClickListener);

		/** 中 */
		viewPager.setVisibility(View.VISIBLE);
		playSurface.setVisibility(View.GONE);

		adapter = new MyPagerAdapter();
		changeWindow(currentScreen);
		viewPager.setLongClickable(true);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				try {
					stopAllFunc();
					MyLog.i(Consts.TAG_UI, ">>> pageSelected: " + arg0
							+ ", to "
							+ ((arg0 > lastItemIndex) ? "right" : "left"));

					currentPageChannelList = manager.getValidChannelList(arg0);
					int size = currentPageChannelList.size();

					int target = currentPageChannelList.get(0).getIndex();
					for (int i = 0; i < size; i++) {
						if (lastClickIndex == currentPageChannelList.get(i)
								.getIndex()) {
							target = lastClickIndex;
							break;
						}
					}
					changeBorder(target);
					Jni.sendTextData(lastClickIndex,
							JVNetConst.JVN_RSP_TEXTDATA, 8,
							JVNetConst.JVN_STREAM_INFO);
					if (false == isBlockUi) {
						if (ONE_SCREEN == currentScreen) {
							try {
								pauseChannel(channelList.get(arg0 - 1));
							} catch (Exception e) {
								// [Neo] empty
							}

							try {
								pauseChannel(channelList.get(arg0 + 1));
							} catch (Exception e) {
								// [Neo] empty
							}
						} else {
							disconnectChannelList.addAll(manager
									.getValidChannelList(lastItemIndex));
						}

						isBlockUi = true;
						viewPager.setDisableSliding(isBlockUi);

						handler.removeMessages(WHAT_CHECK_SURFACE);
						handler.sendMessageDelayed(handler.obtainMessage(
								WHAT_CHECK_SURFACE, arg0, lastClickIndex),
								DELAY_CHECK_SURFACE);
						handler.sendEmptyMessage(WHAT_SHOW_PROGRESS);
					}

					lastItemIndex = arg0;

					currentMenu_v.setText(channelList.get(lastItemIndex)
							.getParent().getNickName()
							+ "-"
							+ channelList.get(lastClickIndex).getChannel());
					currentMenu_h.setText(channelList.get(lastItemIndex)
							.getParent().getNickName());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// [Neo] Empty
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// [Neo] Empty
			}
		});

		viewPager.setCurrentItem(lastItemIndex);
		playFunctionList.setOnItemClickListener(onItemClickListener);
		// yt_cancle.setOnClickListener(myOnClickListener);
		// audioMonitor.setOnClickListener(myOnClickListener);
		// ytOperate.setOnClickListener(myOnClickListener);
		// remotePlayback.setOnClickListener(myOnClickListener);

		autoimage.setOnTouchListener(new LongClickListener());
		zoomIn.setOnTouchListener(new LongClickListener());
		zoomout.setOnTouchListener(new LongClickListener());
		scaleSmallImage.setOnTouchListener(new LongClickListener());
		scaleAddImage.setOnTouchListener(new LongClickListener());
		upArrow.setOnTouchListener(new LongClickListener());
		downArrow.setOnTouchListener(new LongClickListener());
		leftArrow.setOnTouchListener(new LongClickListener());
		rightArrow.setOnTouchListener(new LongClickListener());

		if (null == streamArray) {
			streamArray = getResources().getStringArray(R.array.array_stream);
		}
		streamListView = (ListView) findViewById(R.id.streamlistview);
		streamAdapter = new StreamAdapter(JVPlayActivity.this);
		streamAdapter.setData(streamArray);
		streamListView.setAdapter(streamAdapter);

		/** 下 */
		capture.setOnClickListener(myOnClickListener);
		videoTape.setOnClickListener(myOnClickListener);
		moreFeature.setOnClickListener(myOnClickListener);

		if (Consts.PLAY_AP == playFlag) {
			bottombut1.setOnClickListener(myOnClickListener);
			bottombut2.setOnClickListener(myOnClickListener);
			bottombut4.setOnClickListener(myOnClickListener);
			bottombut5.setOnClickListener(myOnClickListener);
			bottombut8.setOnClickListener(myOnClickListener);
			bottombut3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_notuse));
			bottombut7.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_videonotuse));
		} else {
			bottombut1.setOnClickListener(myOnClickListener);
			bottombut2.setOnClickListener(myOnClickListener);
			bottombut3.setOnClickListener(myOnClickListener);
			bottombut4.setOnClickListener(myOnClickListener);
			bottombut5.setOnClickListener(myOnClickListener);
			bottombut6.setOnClickListener(myOnClickListener);
			bottombut7.setOnClickListener(myOnClickListener);
			bottombut8.setOnClickListener(myOnClickListener);
		}

		bottomStream.setOnClickListener(myOnClickListener);

		nextStep.setOnClickListener(myOnClickListener);

		// 小于3.0的系统有可能花屏
		if (Build.VERSION_CODES.HONEYCOMB > Build.VERSION.SDK_INT) {
			if (!MySharedPreference.getBoolean(Consts.DIALOG_NOT_SUPPORT23)) {
				errorDialog(
						Consts.DIALOG_NOT_SUPPORT23,
						getResources()
								.getString(R.string.system_lower)
								.replace(
										"$",
										MobileUtil
												.mobileSysVersion(JVPlayActivity.this)));
			}

		}
		if (Build.VERSION_CODES.JELLY_BEAN > Build.VERSION.SDK_INT) {// 小于4.1的系统，不允许硬解
			lowerSystem = true;
		}

		voiceCall.setOnClickListener(myOnClickListener);
		voiceCall.setOnTouchListener(callOnTouchListener);
		voiceCall.setOnLongClickListener(callOnLongClickListener);

		bottombut5.setOnClickListener(myOnClickListener);
		bottombut5.setOnTouchListener(callOnTouchListener);
		bottombut5.setOnLongClickListener(callOnLongClickListener);

	}

	private class MyPagerAdapter extends PagerAdapter {

		private ArrayList<View> list;

		public void update(ArrayList<View> list) {
			if (null != list) {
				this.list = list;
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			int result = 0;
			if (null != list) {
				result = list.size();
			}
			return result;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (null != list && list.size() > position) {
				container.removeView(list.get(position));

				if (ONE_SCREEN == currentScreen) {
					disconnectChannelList.add(manager.getChannel(position));
				}
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Object result = null;
			if (null != list && list.size() > position) {
				container.addView(list.get(position));
				result = list.get(position);
			}
			return result;
		}

	}

	public boolean resumeChannel(Channel channel) {
		boolean result = false;

		if (null != channel && channel.isConnected() && channel.isPaused()
				&& null != channel.getSurface()) {
			result = Jni.sendBytes(channel.getIndex(),
					JVNetConst.JVN_CMD_VIDEO, new byte[0], 8);
			if (result) {
				MyLog.v(TAG, "result1=" + result + "");
				result = Jni.resume(channel.getIndex(), channel.getSurface());
				MyLog.v(TAG, "result2=" + result + "");
				if (result) {
					handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
							channel.getIndex(), ARG2_STATUS_BUFFERING));
				} else {
					// handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
					// channel.getIndex(), ARG2_STATUS_HAS_CONNECTED));
				}

				channel.setPaused(false);
			} else {
				handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
						channel.getIndex(), ARG2_STATUS_DISCONNECTED));
			}
		}

		if (ONE_SCREEN == currentScreen) {
			// 是IPC，发文本聊天请求
			if (channel.getParent().isHomeProduct()) {
				if (channel.isAgreeTextData()) {
					// 获取主控码流信息请求
					Jni.sendTextData(lastClickIndex,
							JVNetConst.JVN_RSP_TEXTDATA, 8,
							JVNetConst.JVN_STREAM_INFO);
				} else {
					// 请求文本聊天
					Jni.sendBytes(lastClickIndex, JVNetConst.JVN_REQ_TEXT,
							new byte[0], 8);
				}

			}
		}
		return result;
	}

	public boolean pauseChannel(Channel channel) {
		boolean result = false;

		if (null != channel && channel.isConnected()
				&& false == channel.isPaused()) {
			if (lastClickIndex == channel.getIndex()) {
				result = true;
			} else {
				result = Jni.sendBytes(channel.getIndex(),
						JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0], 8);
			}

			if (result) {
				Jni.pause(channel.getIndex());
				channel.setPaused(true);
			}
		}

		return result;
	}

	private void changeWindow(int count) {
		currentScreen = count;

		adapter.update(manager.genPageList(count));
		lastItemIndex = lastClickIndex / currentScreen;
		currentPageChannelList = manager.getValidChannelList(lastItemIndex);

		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(lastItemIndex, false);

		isBlockUi = true;
		viewPager.setDisableSliding(isBlockUi);
		changeBorder(lastClickIndex);

		handler.removeMessages(WHAT_CHECK_SURFACE);
		handler.sendMessageDelayed(handler.obtainMessage(WHAT_CHECK_SURFACE,
				lastItemIndex, lastClickIndex), DELAY_CHECK_SURFACE);
		handler.sendEmptyMessage(WHAT_SHOW_PROGRESS);
	}

	private void changeBorder(int currentIndex) {
		if (lastClickIndex != currentIndex) {
			if (lastClickIndex >= 0) {
				((View) manager.getView(lastClickIndex).getParent())
						.setBackgroundColor(getResources().getColor(
								R.color.videounselect));
			}
			lastClickIndex = currentIndex;
		}

		if (ONE_SCREEN != currentScreen) {
			((View) manager.getView(lastClickIndex).getParent())
					.setBackgroundColor(getResources().getColor(
							R.color.videoselect));
		}

	}

	private boolean connect(Channel channel, boolean isPlayDirectly) {
		boolean result = false;

		if (null != channel && false == channel.isConnected()
				&& false == channel.isConnecting()) {

			// 如果是域名添加的设备需要先去解析IP
			String ip = "";
			int port = 0;
			int connect = 0;

			Device device = channel.getParent();
			if (2 == device.getIsDevice()) {
				ip = ConfigUtil.getInetAddress(device.getDoMain());
				port = 9101;
			}

			if (null != ssid
					&& channel.getParent().getFullNo().equalsIgnoreCase(ssid)) {
				// IP直连
				MyLog.v(TAG, device.getNo() + "--AP--直连接：" + device.getIp());
				connect = Jni.connect(channel.getIndex(), channel.getChannel(),
						Consts.IPC_DEFAULT_IP, Consts.IPC_DEFAULT_PORT,
						device.getUser(), device.getPwd(), -1, device.getGid(),
						true, 1, true, JVNetConst.TYPE_3GMO_UDP,
						channel.getSurface(), isOmx);
				if (connect == channel.getIndex()) {
					channel.setPaused(null == channel.getSurface());
				}

			} else {
				int number = device.getNo();
				String conIp = device.getIp();
				int conPort = device.getPort();
				if (Consts.PLAY_AP == playFlag) {
					conIp = Consts.IPC_DEFAULT_IP;
					conPort = Consts.IPC_DEFAULT_PORT;
				} else {
					conIp = device.getIp();
					conPort = device.getPort();
				}

				// 有ip通过ip连接
				if (false == ("".equalsIgnoreCase(device.getIp()) || 0 == device
						.getPort())) {
					if (ConfigUtil.is3G(JVPlayActivity.this, false)
							&& 0 == device.getIsDevice()) {// 普通设备3G情况不用ip连接
						conIp = "";
						conPort = 0;
					} else {// 有ip非3G
						number = -1;
					}
				}

				if (isPlayDirectly) {
					connect = Jni.connect(channel.getIndex(),
							channel.getChannel(), conIp, conPort,
							device.getUser(), device.getPwd(), number,
							device.getGid(), true, 1, true,
							JVNetConst.TYPE_3GMO_UDP,// (device.isHomeProduct()
							// ? 6 : 5),
							channel.getSurface(), isOmx);
					if (connect == channel.getIndex()) {
						channel.setPaused(null == channel.getSurface());
					}
				} else {
					connect = Jni.connect(channel.getIndex(),
							channel.getChannel(), conIp, conPort,
							device.getUser(), device.getPwd(), number,
							device.getGid(), true, 1, true,
							JVNetConst.TYPE_3GMO_UDP,// (device.isHomeProduct()
							// ? 6 : 5),
							null, isOmx);
					if (connect == channel.getIndex()) {
						channel.setPaused(true);
					}
				}

			}

			if (Consts.BAD_HAS_CONNECTED == connect) {
				channel.setConnected(true);
				channel.setPaused(true);
				channel.setConnecting(false);
				handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
						channel.getIndex(), ARG2_STATUS_HAS_CONNECTED));
				Jni.disconnect(channel.getIndex());
			} else if (Consts.BAD_CONN_OVERFLOW == connect) {
				handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
						channel.getIndex(), ARG2_STATUS_CONN_OVERFLOW));
			} else {
				channel.setConnecting(true);
				handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
						channel.getIndex(), ARG2_STATUS_CONNECTING));
				result = true;
			}

		}

		return result;
	}

	// private void flightstate(int channelindex) {
	// StringBuffer buffer1 = new StringBuffer();
	// StringBuffer buffer2 = new StringBuffer();
	// if (channelList.get(channelindex).getHtflight() == 0) {
	// buffer1.append("FlashMode=").append(0).append(";");//
	// .append(";nMDSensitivity=").append(20).append(";");
	// ht_fight.setBackgroundResource(R.drawable.ht_flight_auto);
	// } else if (channelList.get(channelindex).getHtflight() == 1) {
	// buffer1.append("FlashMode=").append(1).append(";");//
	// .append(";nMDSensitivity=").append(20).append(";");
	// ht_fight.setBackgroundResource(R.drawable.ht_flight_open);
	// } else if (channelList.get(channelindex).getHtflight() == 2) {
	// buffer1.append("FlashMode=").append(2).append(";");//
	// .append(";nMDSensitivity=").append(20).append(";");
	// ht_fight.setBackgroundResource(R.drawable.ht_flight_close);
	// }
	//
	// if (channelList.get(channelindex).isHtmotion()) {
	// ht_motion.setBackgroundResource(R.drawable.ht_motiondetec_open);
	// buffer2.append("bMDEnable=").append(1).append(";");
	// } else {
	// ht_motion.setBackgroundResource(R.drawable.ht_motiondetec_close);
	// buffer2.append("bMDEnable=").append(0).append(";");
	// }
	//
	// }

	@Override
	public void onClick(Channel channel, boolean isFromImageView, int viewId) {
		MyLog.i(Consts.TAG_PLAY, ">>> click: " + channel.getIndex()
				+ ", isBlocked: " + isBlockUi);

		if (isBlockUi) {
			showTextToast(R.string.waiting);
		}

		if (false == isBlockUi && isFromImageView) {// 播放按钮事件
			if (false == channel.isConnected()
					&& false == channel.isConnecting()) {

				if (false == connect(channel, true)) {
					MyLog.e(Consts.TAG_PLAY, "connect failed: " + channel);
				}

			} else if (channel.isConnecting()) {
				handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
						channel.getIndex(), ARG2_STATUS_CONNECTING));
			} else if (false == channel.isPaused()) {
				handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
						channel.getIndex(), ARG2_STATUS_CONNECTED));
			} else {
				if (false == resumeChannel(channel)) {
					MyLog.e(Consts.TAG_PLAY, "resume failed: " + channel);
				}

			}
		}

		if (false == isBlockUi && isDoubleClickCheck
				&& lastClickIndex == channel.getIndex()) {// 双击

			if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation
					|| Consts.PLAY_AP == playFlag) {// 横屏
				return;
			}

			if (ONE_SCREEN != currentScreen) {
				int size = currentPageChannelList.size();
				for (int i = 0; i < size; i++) {
					if (lastClickIndex - 1 > i && lastClickIndex + 1 < i) {
						disconnectChannelList
								.add(currentPageChannelList.get(i));
					} else if (lastClickIndex == i) {
						// [Neo] Empty
					} else {
						// [Neo] stand alone for single destroy window, too
						pauseChannel(currentPageChannelList.get(i));
					}
				}
				changeWindow(ONE_SCREEN);
			} else {
				changeWindow(selectedScreen);
			}

		} else {// 单击

			if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
				if (View.VISIBLE == horPlayBarLayout.getVisibility()) {
					horPlayBarLayout.setVisibility(View.GONE);
				} else {
					horPlayBarLayout.setVisibility(View.VISIBLE);
				}
			} else {
				if (ONE_SCREEN == currentScreen) {
					if (View.VISIBLE == verPlayBarLayout.getVisibility()) {
						verPlayBarLayout.setVisibility(View.GONE);
					} else {
						verPlayBarLayout.setVisibility(View.VISIBLE);
					}
				}

				changeBorder(channel.getIndex());

				if (false == isBlockUi) {
					isDoubleClickCheck = true;
					if (null != doubleClickTimer) {
						doubleClickTimer.cancel();
					}

					doubleClickTimer = new Timer();
					doubleClickTimer.schedule(new DoubleClickChecker(),
							DELAY_DOUBLE_CHECKER);
				}
			}

		}

	}

	@Override
	public void onLongClick(Channel channel) {
		MyLog.v(Consts.TAG_UI, "onLongClick: " + channel.getIndex());
	}

	@Override
	public void onLifecycle(int index, int status, Surface surface, int width,
			int height) {
		boolean isFromCurrent = false;
		Channel channel = channelList.get(index);

		if (ONE_SCREEN == currentScreen) {
			int size = currentPageChannelList.size();
			for (int i = 0; i < size; i++) {
				if (index == currentPageChannelList.get(i).getIndex()) {
					isFromCurrent = true;
					break;
				}
			}
		}

		switch (status) {
		case PlayWindowManager.STATUS_CREATED:
			if (ONE_SCREEN == currentScreen && false == isFromCurrent
					&& false == channel.isConnected()) {
				connectChannelList.add(channel);
			}
			channel.setSurface(surface);
			handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
					channel.getIndex(), ARG2_STATUS_UNKNOWN));
			break;

		case PlayWindowManager.STATUS_CHANGED:
			// [Neo] Empty
			break;

		case PlayWindowManager.STATUS_DESTROYED:
			pauseChannel(channel);
			channel.setSurface(null);
			break;

		default:
			break;
		}
	}

	private class DoubleClickChecker extends TimerTask {

		@Override
		public void run() {
			cancel();
			isDoubleClickCheck = false;
		}

	}

	// jy
	/**
	 * 所有按钮事件
	 */

	OnClickListener myOnClickListener = new OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View view) {

			Channel channel = channelList.get(lastClickIndex);

			switch (view.getId()) {
			// case R.id.yt_cancle:
			//
			// break;
			case R.id.ht_flight:
				StringBuffer buffer1 = new StringBuffer();
				if (Consts.FLIGHT_FLAG == 0) {// 当前状态 0- 自动，改为开启
					MyLog.e("闪光灯--1", "当前状态  0- 自动，改为1-开启");
					buffer1.append("FlashMode=").append(1).append(";");// .append(";nMDSensitivity=").append(20).append(";");
					Consts.FLIGHT_FLAG = 1;
					ht_fight.setBackgroundResource(R.drawable.ht_flight_open);
				} else if (Consts.FLIGHT_FLAG == 1) {// 1-开启，改为关闭
					MyLog.e("闪光灯--2", "1-开启，改为2-关闭");
					buffer1.append("FlashMode=").append(2).append(";");// .append(";nMDSensitivity=").append(20).append(";");
					Consts.FLIGHT_FLAG = 2;
					ht_fight.setBackgroundResource(R.drawable.ht_flight_close);
				} else if (Consts.FLIGHT_FLAG == 2) {// 2-关闭，改为自动
					MyLog.e("闪光灯--3", "2-关闭，改为0-自动");
					buffer1.append("FlashMode=").append(0).append(";");// .append(";nMDSensitivity=").append(20).append(";");
					Consts.FLIGHT_FLAG = 0;
					ht_fight.setBackgroundResource(R.drawable.ht_flight_auto);
				}
				channelList.get(lastClickIndex).setHtflight(Consts.FLIGHT_FLAG);

				Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
						false, 0, JVNetConst.RC_SETPARAM, buffer1.toString());
				Jni.sendTextData(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
						8, JVNetConst.JVN_STREAM_INFO);
				break;
			case R.id.ht_motion:
				StringBuffer buffer = new StringBuffer();
				if (Consts.MOTION_DETECTION_FLAG) {// 当前状态：开着侦测
					// bMDEnable字符串的值，即是否开启移动告警 1是开，0是关
					ht_motion
							.setBackgroundResource(R.drawable.ht_motiondetec_close);
					buffer.append("bMDEnable=").append(0).append(";");// .append(";nMDSensitivity=").append(20).append(";");
					Consts.MOTION_DETECTION_FLAG = false;
				} else {
					Consts.MOTION_DETECTION_FLAG = true;
					ht_motion
							.setBackgroundResource(R.drawable.ht_motiondetec_open);
					buffer.append("bMDEnable=").append(1).append(";");// .append(";nMDSensitivity=").append(20).append(";");
				}
				channelList.get(lastClickIndex).setHtmotion(
						Consts.MOTION_DETECTION_FLAG);

				Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
						true, JVNetConst.RC_EX_MD, JVNetConst.EX_MD_SUBMIT,
						buffer.toString());
				Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
						true, JVNetConst.RC_EX_MD, JVNetConst.EX_MD_UPDATE,
						null);

				// JVSUDT.JVC_SetAlarm(getChannelMapKey() + 1,
				// (byte) JVNetConst.JVN_RSP_TEXTDATA,
				// (byte) JVNetConst.RC_EXTEND,
				// (byte) JVNetConst.RC_EX_MD,
				// (byte) JVNetConst.EX_MD_SUBMIT, buffer.toString());
				break;
			case R.id.devicepwd_nameet_cancle:

				devicepwd_nameet.setText("");

				break;
			case R.id.devicepwd_passwrodet_cancle:

				devicepwd_passwordet.setText("");

				break;
			case R.id.dialogpwd_cancle_img:

				initDialog.dismiss();

				break;
			case R.id.nextstep: {// AP下一步
				backMethod(false);
				break;
			}
			case R.id.btn_left: {// 左边按钮
				backMethod(true);
				break;
			}

			// case R.id.bottom_but1:
			// if (!bottomboolean1) {
			// bottombut1.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.video_play_icon));
			//
			// bottomboolean1 = true;
			// }else {
			// bottombut1.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.video_stop_icon));
			// bottomboolean1 = false;
			// }
			// break;
			case R.id.bottom_but2:
			case R.id.decodeway: {// 软硬解切换
				if (allowThisFuc(false)) {
					if (channel.getParent().is05()) {
						createDialog("");
						if (channel.isOMX()) {// 硬解切成软解
							Jni.setOmx(lastClickIndex, false);
						} else {// 软解切成硬解
							needToast = true;
							Jni.setOmx(lastClickIndex, true);
						}
						// I帧通知成功失败
					} else {
						showTextToast(R.string.not_support_this_func);
					}
				}
				break;
			}
			case R.id.bottom_but6:
			case R.id.overturn: {// 视频翻转
				if (allowThisFuc(false)) {
					int send = 0;
					int effect = channelList.get(lastClickIndex)
							.getEffect_flag();
					// if(0 == (0x04 & effect)) {
					// // 正
					// send = (~0x04) & effect
					// } else {
					// // 反
					// send = 0x04 | effect
					// }
					String turnParam = "";
					if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
						send = 0x04 | effect;
					} else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
						send = (~0x04) & effect;// + Consts.SCREEN_NORMAL;
					}
					turnParam = "effect_flag=" + send;
					MyLog.i(TAG, "turnParam=" + turnParam);
					Jni.rotateVideo(lastClickIndex,
							JVNetConst.JVN_RSP_TEXTDATA, turnParam);

					// Jni.sendString(lastClickIndex,
					// JVNetConst.JVN_RSP_TEXTDATA, true,
					// Consts.COUNT_EX_SENSOR, Consts.TYPE_EX_SENSOR,
					// turnParam);

					Jni.sendTextData(lastClickIndex,
							JVNetConst.JVN_RSP_TEXTDATA, 8,
							JVNetConst.JVN_STREAM_INFO);
				}
				break;
			}
			case R.id.btn_right: {// 右边按钮----录像切换
				if (allowThisFuc(false)) {
					try {
						createDialog("");
						if (Consts.STORAGEMODE_ALARM == channel
								.getStorageMode()) {
							Jni.sendString(lastClickIndex,
									JVNetConst.JVN_RSP_TEXTDATA, true,
									Consts.COUNT_EX_STORAGE,
									Consts.TYPE_EX_STORAGE_SWITCH,
									String.format(
											Consts.FORMATTER_STORAGE_MODE,
											Consts.STORAGEMODE_NORMAL));

							// Jni.setStorage(lastClickIndex,
							// JVNetConst.JVN_RSP_TEXTDATA,
							// String.valueOf(Consts.STORAGEMODE_NORMAL));
						} else if (Consts.STORAGEMODE_NORMAL == channel
								.getStorageMode()) {
							Jni.sendString(lastClickIndex,
									JVNetConst.JVN_RSP_TEXTDATA, true,
									Consts.COUNT_EX_STORAGE,
									Consts.TYPE_EX_STORAGE_SWITCH,
									String.format(
											Consts.FORMATTER_STORAGE_MODE,
											Consts.STORAGEMODE_ALARM));
							// Jni.setStorage(lastClickIndex,
							// JVNetConst.JVN_RSP_TEXTDATA,
							// String.valueOf(Consts.STORAGEMODE_ALARM));
						}

					} catch (Exception e) {
						dismissDialog();
						e.printStackTrace();
					}
				}
				break;
			}
			case R.id.currentmenu:
			case R.id.selectscreen:// 下拉选择多屏
				if (isBlockUi) {
					createDialog("");
				} else {
					if (popScreen == null) {
						if (null != screenList && 0 != screenList.size()) {

							screenAdapter = new ScreenAdapter(
									JVPlayActivity.this, screenList);
							screenListView = new ListView(JVPlayActivity.this);
							screenListView.setDivider(null);
							if (disMetrics.widthPixels < 1080) {
								popScreen = new PopupWindow(screenListView,
										240,
										LinearLayout.LayoutParams.WRAP_CONTENT);
							} else {
								popScreen = new PopupWindow(screenListView,
										400,
										LinearLayout.LayoutParams.WRAP_CONTENT);
							}

							screenListView.setAdapter(screenAdapter);

							if (FOUR_SCREEN == currentScreen) {
								screenAdapter.selectIndex = 0;
							} else if (NINE_SCREEN == currentScreen) {
								screenAdapter.selectIndex = 1;
							} else if (SIXTEEN_SCREEN == currentScreen) {
								screenAdapter.selectIndex = 2;
							}
							screenAdapter.notifyDataSetChanged();

							screenListView.setVerticalScrollBarEnabled(false);
							screenListView.setHorizontalScrollBarEnabled(false);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							screenListView.setLayoutParams(params);
							screenListView.setFadingEdgeLength(0);
							screenListView
									.setCacheColorHint(JVPlayActivity.this
											.getResources().getColor(
													R.color.transparent));
							popScreen.showAsDropDown(currentMenu);
						}
					} else if (popScreen.isShowing()) {
						screenAdapter.notifyDataSetChanged();
						popScreen.dismiss();
					} else if (!popScreen.isShowing()) {
						screenAdapter.notifyDataSetChanged();
						popScreen.showAsDropDown(currentMenu);
					}
				}
				break;
			case R.id.bottom_but8:
			case R.id.varvoice_bg:
			case R.id.audio_monitor:// 音频监听
				if (allowThisFuc(true)) {
					if (channelList.get(lastClickIndex).isVoiceCall()) {
						showTextToast(R.string.audio_monitor_forbidden);
					} else {
						// 停止音频监听
						if (PlayUtil.isPlayAudio(lastClickIndex)) {
							stopAudio(lastClickIndex);
							functionListAdapter.selectIndex = -1;
							bottombut8.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.video_monitor_ico));
							varvoice.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.video_monitor_ico));
						} else {
							startAudio(lastClickIndex,
									channelList.get(lastClickIndex)
											.getAudioByte());
							functionListAdapter.selectIndex = 2;
							bottombut8
									.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.video_monitorselect_icon));
							varvoice.setBackgroundDrawable(getResources()
									.getDrawable(
											R.drawable.video_monitorselect_icon));
						}
						// if (!PlayUtil.audioPlay(lastClickIndex)) {
						// functionListAdapter.selectIndex = -1;
						// bottombut8
						// .setBackgroundDrawable(getResources()
						// .getDrawable(
						// R.drawable.video_monitor_ico));
						// if (null != playAudio) {
						// playAudio.interrupt();
						// playAudio = null;
						// }
						// } else {
						// functionListAdapter.selectIndex = 0;
						// bottombut8
						// .setBackgroundDrawable(getResources()
						// .getDrawable(
						// R.drawable.video_monitorselect_icon));
						// }
					}

				} else {
					functionListAdapter.selectIndex = -1;
					bottombut8.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.video_monitor_ico));
					varvoice.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.video_monitor_ico));
				}

				functionListAdapter.notifyDataSetChanged();
				break;
			case R.id.yt_operate:// 云台
				if (allowThisFuc(false)) {
					showPTZ();
				}
				break;
			case R.id.bottom_but4:
			case R.id.remote_playback:// 远程回放
				if (allowThisFuc(true)) {
					startRemote();
				}

				break;
			case R.id.bottom_but3:
			case R.id.capture:// 抓拍
				if (Consts.ISHITVIS == 1) {
					// TODO
					// if (hasSDCard() && allowThisFuc(false)) {
					PlayUtil.hitviscapture(lastClickIndex);
					Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
							true, JVNetConst.RC_EX_FlashJpeg,
							JVNetConst.RC_EXTEND, null);
					// }
				} else {
					if (hasSDCard() && allowThisFuc(false)) {
						boolean capture = PlayUtil.capture(lastClickIndex);
						MyLog.i(TAG, "capture=" + capture);
					}
				}
				break;
			case R.id.bottom_but5:
			case R.id.funclayout:// AP功能列表对讲功能
			case R.id.voicecall:// 语音对讲
				if (allowThisFuc(true)) {
					// 停止音频监听
					if (PlayUtil.isPlayAudio(lastClickIndex)) {
						stopAudio(lastClickIndex);
						functionListAdapter.selectIndex = -1;
						bottombut8.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.video_monitor_ico));
					}

					if (Consts.JAE_ENCODER_G729 != channel.getAudioEncType()
							&& 8 == channel.getAudioByte()) {
						showTextToast(R.string.not_support_this_func);
						break;
					}

					if (!channelList.get(lastClickIndex).isSupportVoice()) {
						showTextToast(R.string.not_support_this_func);
					} else {
						if (channelList.get(lastClickIndex).isVoiceCall()) {
							stopVoiceCall(lastClickIndex);
							channelList.get(lastClickIndex).setVoiceCall(false);
							realStop = true;
							voiceCallSelected(false);
							VOICECALLING = false;
							if (Consts.PLAY_AP == playFlag) {
								functionListAdapter.selectIndex = -1;
							}
						} else {
							JVPlayActivity.AUDIO_SINGLE = channelList.get(
									lastClickIndex).isSingleVoice();
							createDialog("");
							startVoiceCall(lastClickIndex,
									channelList.get(lastClickIndex));
							if (Consts.PLAY_AP == playFlag) {
								functionListAdapter.selectIndex = 1;
							}
						}
					}

				}
				functionListAdapter.notifyDataSetChanged();
				break;
			case R.id.bottom_but7:
			case R.id.videotape:// 录像
				if (hasSDCard() && allowThisFuc(true)) {
					if (channelList.get(lastClickIndex).getParent().is05()) {
						if (PlayUtil.videoRecord(lastClickIndex)) {// 打开
							tapeSelected(true);
							showTextToast(R.string.str_start_record);
						} else {// 关闭
							showTextToast(Consts.VIDEO_PATH);
							tapeSelected(false);
						}
					} else {
						showTextToast(R.string.not_support_this_func);
					}
				}

				break;
			case R.id.video_bq:
			case R.id.more_features:// 码流
				if (channelList.get(lastClickIndex).isNewIpcFlag()) {
					streamListView.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.stream_selector_bg3));
				} else {
					streamListView.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.stream_selector_bg2));
				}

				if (View.VISIBLE == streamListView.getVisibility()) {
					streamListView.setVisibility(View.GONE);
				} else {
					if (allowThisFuc(true)) {
						if (-1 == channelList.get(lastClickIndex)
								.getStreamTag()) {
							showTextToast(R.string.not_support_this_func);
						} else {
							streamAdapter.setNewIpc(channelList.get(
									lastClickIndex).isNewIpcFlag());
							streamAdapter.notifyDataSetChanged();
							streamListView.setVisibility(View.VISIBLE);
						}
					}
				}
				break;

			case R.id.bottom_but1:// 暂停继续播
				if (channelList.get(lastClickIndex).isPaused()) {
					resumeChannel(channelList.get(lastClickIndex));
					bottombut1
							.setBackgroundResource(R.drawable.video_stop_icon);
				} else {
					pauseChannel(channelList.get(lastClickIndex));
					bottombut1
							.setBackgroundResource(R.drawable.video_play_icon);
				}
				break;

			case R.id.bottom:

				break;
			}

		}

	};

	// // 正在执行返回操作
	// public boolean isBacking = false;
	// private Timer backTimer = null;
	//
	// /**
	// * 返回任务
	// *
	// * @author Administrator
	// *
	// */
	// private class BackTask extends TimerTask {
	//
	// @Override
	// public void run() {
	// handler.sendMessage(handler.obtainMessage(WHAT_DISMISS_PROGRESS));
	// }
	//
	// }

	/**
	 * 返回事件
	 */
	private void backMethod(boolean apBack) {
		if (View.VISIBLE == ytLayout.getVisibility()) {
			ytLayout.setVisibility(View.GONE);
			if (bigScreen) {
				playFunctionList.setVisibility(View.VISIBLE);
			} else {
				playFunctionList.setVisibility(View.GONE);
			}
		} else {

			stopAllFunc();
			createDialog("");
			proDialog.setCancelable(false);
			// 返回超时，重新点击返回
			// backTimer = new Timer();
			// backTimer.schedule(new BackTask(), 15 * 1000);
			handler.sendEmptyMessageDelayed(WHAT_FINISH, 15 * 1000);

			DisconnetTask task = new DisconnetTask();
			String[] params = new String[3];
			params[0] = String.valueOf(apBack);
			task.execute(params);
		}

	}

	@Override
	public void onBackPressed() {
		backMethod(true);
	}

	boolean backFunc = false;

	// 设置三种类型参数分别为String,Integer,String
	class DisconnetTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int sendRes = 0;// 0成功 1失败

			backFunc = Boolean.valueOf(params[0]);
			try {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				PlayUtil.disConnectAll(channelList);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				while (!allDis(channelList)) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return sendRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			if (0 == result) {
				if (Consts.PLAY_AP == playFlag) {
					Intent aintent = new Intent();
					if (backFunc) {
						aintent.putExtra("AP_Back", true);
					} else {// next
						aintent.putExtra("AP_Back", false);
					}
					setResult(JVConst.AP_CONNECT_FINISHED, aintent);
					JVPlayActivity.this.finish();
				} else {
					JVPlayActivity.this.finish();
				}
				handler.removeMessages(WHAT_FINISH);
				isQuit = true;
			}

			dismissDialog();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	/**
	 * 判断是否已经全部断开
	 * 
	 * @param channleList
	 * @return
	 */
	public boolean allDis(ArrayList<Channel> channleList) {
		boolean allDis = true;
		if (null != channleList && 0 != channleList.size()) {
			try {
				int size = channleList.size();
				for (int i = 0; i < size; i++) {
					if (channleList.get(i).isConnected()
							|| channleList.get(i).isConnecting()) {
						allDis = false;
						MyLog.e(TAG, "Not-DisConnected-index="
								+ channleList.get(i).getIndex());
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return allDis;
	}

	/**
	 * 停止所有事件
	 */
	@SuppressWarnings("deprecation")
	public void stopAllFunc() {
		// 停止音频监听
		if (PlayUtil.isPlayAudio(lastClickIndex)) {
			stopAudio(lastClickIndex);
			functionListAdapter.selectIndex = -1;
			bottombut8.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_monitor_ico));
			functionListAdapter.notifyDataSetChanged();
		}

		// 正在录像停止录像
		if (PlayUtil.checkRecord(lastClickIndex)) {
			if (!PlayUtil.videoRecord(lastClickIndex)) {// 打开
				showTextToast(Consts.VIDEO_PATH);
				tapeSelected(false);
			}
		}

		// 停止对讲
		if (channelList.get(lastClickIndex).isVoiceCall()) {
			channelList.get(lastClickIndex).setVoiceCall(false);
			realStop = true;
			voiceCallSelected(false);
			stopVoiceCall(lastClickIndex);
		}
	}

	@Override
	protected void saveSettings() {
		super.saveSettings();
	}

	@Override
	protected void freeMe() {
		stopAllFunc();
		isQuit = true;
		adapter.update(new ArrayList<View>());
		manager.destroy();
		// adapter.notifyDataSetChanged();
		PlayUtil.disConnectAll(manager.getChannelList());
		super.freeMe();
	}

	@Override
	public void onGesture(int direction) {
		if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
			Channel channel = channelList.get(lastClickIndex);
			if (null != channel && channel.isConnected()
					&& !channel.isConnecting()) {
				int c = 0;
				// [Neo] TODO 不论时候什么，只要在 Surface 上手势，都会判断出来并汇报到这里
				switch (direction) {
				case MyGestureDispatcher.GESTURE_TO_LEFT:
					System.out.println("gesture: left");
					c = JVNetConst.JVN_YTCTRL_L;
					sendCmd(c);
					break;

				case MyGestureDispatcher.GESTURE_TO_UP:
					System.out.println("gesture: up");
					c = JVNetConst.JVN_YTCTRL_U;
					sendCmd(c);
					break;

				case MyGestureDispatcher.GESTURE_TO_RIGHT:
					System.out.println("gesture: right");
					c = JVNetConst.JVN_YTCTRL_R;
					sendCmd(c);
					break;

				case MyGestureDispatcher.GESTURE_TO_DOWN:
					System.out.println("gesture: down");
					c = JVNetConst.JVN_YTCTRL_D;
					sendCmd(c);
					break;

				default:
					break;

				}
			}

		}
	}

	public void sendCmd(int cmd) {
		PlayUtil.sendCtrlCMDLongPush(lastClickIndex, cmd, true);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		PlayUtil.sendCtrlCMDLongPush(lastClickIndex, cmd, false);
	}

	/**
	 * 调用功能前判断是否可调用
	 * 
	 * @return
	 */
	public boolean allowThisFuc(boolean changToOneScreen) {
		boolean allow = false;
		if (currentScreen != ONE_SCREEN && changToOneScreen) {
			changeWindow(ONE_SCREEN);
		}

		if (channelList.get(lastClickIndex).isConnected()) {

			allow = true;
		} else {
			showTextToast(R.string.str_wait_connect);
			allow = false;
		}
		return allow;
	}

	/**
	 * 开始远程回放
	 */
	public void startRemote() {
		Intent remoteIntent = new Intent();
		remoteIntent.setClass(JVPlayActivity.this, JVRemoteListActivity.class);
		remoteIntent.putExtra("IndexOfChannel", channelList.get(lastClickIndex)
				.getIndex());
		remoteIntent.putExtra("ChannelOfChannel",
				channelList.get(lastClickIndex).getChannel());
		remoteIntent.putExtra("is05", channelList.get(lastClickIndex)
				.getParent().is05());
		remoteIntent.putExtra("DeviceType", channelList.get(lastClickIndex)
				.getParent().getType());
		remoteIntent.putExtra("isJFH", channelList.get(lastClickIndex)
				.getParent().isJFH());
		remoteIntent.putExtra("AudioByte", channelList.get(lastClickIndex)
				.getAudioByte());
		JVPlayActivity.this.startActivity(remoteIntent);
	}

	/**
	 * 单向对讲用功能
	 */
	OnTouchListener callOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if (channelList.get(lastClickIndex).isSingleVoice()) {// 单向对讲
				if (arg1.getAction() == MotionEvent.ACTION_UP
						|| arg1.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
					handler.sendMessage(handler
							.obtainMessage(STOP_AUDIO_GATHER));
					new TalkThread(lastClickIndex, 0).start();
					VOICECALL_LONG_CLICK = false;
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					voiceTip.setVisibility(View.GONE);
					handler.sendMessageDelayed(
							handler.obtainMessage(START_AUDIO_GATHER), 2 * 1000);
				}
			}
			return false;
		}

	};

	/**
	 * 单向对讲用功能
	 */
	OnLongClickListener callOnLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View arg0) {
			if (channelList.get(lastClickIndex).isSingleVoice()) {// 单向对讲
				if (VOICECALLING) {// 正在喊话
					VOICECALL_LONG_CLICK = true;
					if (JVPlayActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
					} else if (JVPlayActivity.this.getResources()
							.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
					}
					voiceTip.setVisibility(View.VISIBLE);
					new TalkThread(lastClickIndex, 1).start();
				}
			}
			return true;
		}

	};

	/** 开关对讲线程 */
	class TalkThread extends Thread {
		private int index = 0;
		private int param = 0;

		TalkThread(int index, int param) {
			this.index = index;
			this.param = param;
		}

		@Override
		public void run() {
			// "talkSwitch=" + tag;// 1开始 0关闭
			for (int i = 0; i < 3; i++) {
				Jni.sendString(index, JVNetConst.JVN_RSP_TEXTDATA, false, 0,
						Consts.TYPE_SET_PARAM,
						String.format(Consts.FORMATTER_TALK_SWITCH, param));
			}
			super.run();
		}

	}

	/**
	 * 2.3系统提示下载老软件
	 * 
	 * @param tag
	 */
	private void errorDialog(final String key, String errorMsg) {
		if (!showingDialog) {
			showingDialog = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					JVPlayActivity.this);
			builder.setCancelable(false);
			builder.setTitle(getResources().getString(R.string.tips));
			LayoutInflater li = JVPlayActivity.this.getLayoutInflater();
			LinearLayout layout = (LinearLayout) li.inflate(
					R.layout.system_error, null);
			TextView alertMsg = (TextView) layout.findViewById(R.id.alerttext);
			ToggleButton noAlert = (ToggleButton) layout
					.findViewById(R.id.noalert);
			alertMsg.setText(errorMsg);
			noAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					MySharedPreference.putBoolean(key, arg1);
				}

			});
			builder.setView(layout);
			builder.setNegativeButton(R.string.download,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							showingDialog = false;
							Uri uri = Uri
									.parse("http://down.jovision.com:81/cn/data/CloudSEE2.8.5.apk");
							Intent it = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(it);
						}

					});

			builder.setPositiveButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							showingDialog = false;
							handler.sendMessageDelayed(handler
									.obtainMessage(JVConst.WHAT_START_CONNECT),
									1000);
						}

					});

			builder.show();
		}
	}

	/**
	 * 控制播放按钮显示隐藏
	 * 
	 * @param page
	 * @param window
	 * @param loadingState
	 * @param tag
	 */

	public void loadingState(int index, int loadingState, int tag) {
		try {
			if (null == manager.getView(index)) {
				MyLog.e(TAG,
						"--loadingState--manager.getView(index)--isNull---index="
								+ index);
				return;
			}

			int textSize = 14;
			int proWidth = 30;
			switch (currentScreen) {
			case ONE_SCREEN: {
				textSize = 18;
				proWidth = 90;
				break;
			}
			case FOUR_SCREEN: {
				textSize = 16;
				proWidth = 70;
				break;
			}
			case NINE_SCREEN: {
				textSize = 12;
				proWidth = 50;
				break;
			}
			case SIXTEEN_SCREEN: {
				textSize = 10;
				proWidth = 30;
				break;
			}
			}

			ViewGroup container = (ViewGroup) manager.getView(index)
					.getParent();
			switch (tag) {
			case JVConst.PLAY_CONNECTING: {// 连接中
				verPlayBarLayout.setVisibility(View.GONE);
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, proWidth,
						View.VISIBLE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.VISIBLE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				manager.setInfo(container,
						getResources().getString(loadingState), textSize);// 连接文字
				break;
			}
			case JVConst.PLAY_CONNECTTED: {// 已连接
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, proWidth, View.GONE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.GONE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				break;
			}
			case JVConst.PLAY_DIS_CONNECTTED: {// 断开
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, proWidth, View.GONE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);// 播放状态按钮

				int centerResId = R.drawable.play_l;
				if (currentScreen == ONE_SCREEN) {// 单屏
					centerResId = R.drawable.play_l;
				} else if (currentScreen > ONE_SCREEN
						&& currentScreen <= NINE_SCREEN) {// 4-9屏
					centerResId = R.drawable.play_m;
				} else {// 其他
					centerResId = R.drawable.play_s;
				}

				manager.setCenterResId(container, centerResId);
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.VISIBLE);// 连接文字
				manager.setInfo(container,
						getResources().getString(loadingState), textSize);// 连接文字
				break;
			}
			case JVConst.PLAY_CONNECTING_BUFFER: {// 缓冲中
				verPlayBarLayout.setVisibility(View.GONE);
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, proWidth,
						View.VISIBLE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.VISIBLE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				manager.setInfo(container,
						getResources().getString(loadingState), textSize);// 连接文字
				break;
			}
			case JVConst.PLAY_STATUS_UNKNOWN: {// 未知状态，只显示播放按钮
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, proWidth, View.GONE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);// 播放状态按钮

				int centerResId = R.drawable.play_l;
				if (currentScreen == ONE_SCREEN) {// 单屏
					centerResId = R.drawable.play_l;
				} else if (currentScreen > ONE_SCREEN
						&& currentScreen <= NINE_SCREEN) {// 4-9屏
					centerResId = R.drawable.play_m;
				} else {// 其他
					centerResId = R.drawable.play_s;
				}

				manager.setCenterResId(container, centerResId);
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.GONE);// 连接文字
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 大分辨率功能列表点击事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (2 == arg2) {
				// 设备设置
				if (allowThisFuc(false)) {
					Intent intent = new Intent(JVPlayActivity.this,
							DeviceSettingsActivity.class);
					intent.putExtra("window", lastClickIndex);
					startActivity(intent);
				}

				// 音频监听
				// if (allowThisFuc(true)) {
				// if (channelList.get(lastClickIndex).isVoiceCall()) {
				// showTextToast(R.string.audio_monitor_forbidden);
				// } else {
				// // 停止音频监听
				// if (PlayUtil.isPlayAudio(lastClickIndex)) {
				// stopAudio(lastClickIndex);
				// functionListAdapter.selectIndex = -1;
				// bottombut8
				// .setBackgroundDrawable(getResources()
				// .getDrawable(
				// R.drawable.video_monitor_ico));
				// } else {
				// startAudio(lastClickIndex,
				// channelList.get(lastClickIndex)
				// .getAudioByte());
				// functionListAdapter.selectIndex = arg2;
				// bottombut8
				// .setBackgroundDrawable(getResources()
				// .getDrawable(
				// R.drawable.video_monitorselect_icon));
				// }
				//
				// // if (!PlayUtil.audioPlay(lastClickIndex)) {
				// // functionListAdapter.selectIndex = -1;
				// // bottombut8
				// // .setBackgroundDrawable(getResources()
				// // .getDrawable(
				// // R.drawable.video_monitor_ico));
				// // if (null != playAudio) {
				// // playAudio.interrupt();
				// // playAudio = null;
				// // }
				// // } else {
				// // functionListAdapter.selectIndex = arg2;
				// // bottombut8
				// // .setBackgroundDrawable(getResources()
				// // .getDrawable(
				// // R.drawable.video_monitorselect_icon));
				// // }
				// }

				// } else {
				// functionListAdapter.selectIndex = -1;
				// bottombut8.setBackgroundDrawable(getResources()
				// .getDrawable(R.drawable.video_monitor_ico));
				// }

			} else if (0 == arg2) {// 云台
				if (allowThisFuc(false)) {
					showPTZ();
				} else {
					functionListAdapter.selectIndex = -1;
				}

			} else if (1 == arg2) {// 远程回放 或 对讲

				if (playFlag == Consts.PLAY_AP) {
					View view = arg0.getChildAt(arg2).findViewById(
							R.id.funclayout);
					view.setOnClickListener(myOnClickListener);
					view.setOnTouchListener(callOnTouchListener);
					view.setOnLongClickListener(callOnLongClickListener);

				} else {
					if (allowThisFuc(true)) {
						startRemote();
					} else {
						functionListAdapter.selectIndex = -1;
					}
				}

			}
			functionListAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 长按--云台事件
	 * 
	 * @author Administrator
	 * 
	 */
	class LongClickListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ViewGroup container = (ViewGroup) manager.getView(lastClickIndex)
					.getParent();
			int action = event.getAction();
			int cmd = 0;
			Channel channel = channelList.get(lastClickIndex);
			if (null == channel) {
				return false;
			}

			switch (v.getId()) {
			case R.id.upArrow: // up
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_UP, View.VISIBLE);
					manager.setArrowId(0, R.drawable.up, 0, 0);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_UP, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_U;
				break;
			case R.id.downArrow: // down
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_BOTTOM, View.VISIBLE);
					manager.setArrowId(0, 0, 0, R.drawable.down);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_BOTTOM, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_D;
				break;
			case R.id.leftArrow: // left
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_LEFT, View.VISIBLE);
					manager.setArrowId(R.drawable.left, 0, 0, 0);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_LEFT, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_L;
				break;
			case R.id.rightArrow:// right
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_RIGHT, View.VISIBLE);
					manager.setArrowId(0, 0, R.drawable.right, 0);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_RIGHT, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_R;
				break;
			case R.id.autoimage: // auto
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);
					manager.setCenterResId(container, R.drawable.auto);

					if (channel.isAuto()) {// 已经开启自动巡航，发送关闭命令
						cmd = JVNetConst.JVN_YTCTRL_AT;

						if (currentScreen == ONE_SCREEN) {// jy单屏为当前页，lastClickIndex或currentPage对连接数取余
							channel.setAuto(false);
						} else {
							channel.setAuto(false);
						}

					} else {// 发开始命令
						cmd = JVNetConst.JVN_YTCTRL_A;

						if (currentScreen == ONE_SCREEN) {// jy单屏为当前页，lastClickIndex或currentPage对连接数取余
							channel.setAuto(true);
						} else {
							channel.setAuto(true);
						}
					}

				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.GONE);
				}
				break;
			case R.id.zoomout: // bb+
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);
					manager.setCenterResId(container, R.drawable.bbd);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_BBD;
				break;
			case R.id.zoomin: // bb-
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);
					manager.setCenterResId(container, R.drawable.bbx);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_BBX;
				break;
			case R.id.scaleAddImage: // bj+
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);
					manager.setCenterResId(container, R.drawable.bjd);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_BJD;
				break;
			case R.id.scaleSmallImage: // bj-
				if (action == MotionEvent.ACTION_DOWN) {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.VISIBLE);
					manager.setCenterResId(container, R.drawable.bjx);
				} else {
					manager.setViewVisibility(container,
							PlayWindowManager.ID_CONTROL_CENTER, View.GONE);
				}
				cmd = JVNetConst.JVN_YTCTRL_BJX;
				break;

			}
			try {
				if (action == MotionEvent.ACTION_DOWN) {
					if (channel != null && channel.isConnected()) {
						PlayUtil.sendCtrlCMDLongPush(lastClickIndex, cmd, true);
					}
				} else if (action == MotionEvent.ACTION_UP) {

					if (channel != null && channel.isConnected()) {
						PlayUtil.sendCtrlCMDLongPush(lastClickIndex, cmd, false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		isBlockUi = true;
		handler.removeMessages(WHAT_CHECK_SURFACE);
		handler.sendMessageDelayed(handler.obtainMessage(WHAT_CHECK_SURFACE,
				lastItemIndex, lastClickIndex), DELAY_CHECK_SURFACE);
		handler.sendEmptyMessage(WHAT_SHOW_PROGRESS);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != popScreen && popScreen.isShowing()) {
			screenAdapter.notifyDataSetChanged();
			popScreen.dismiss();
		}
		stopAll(lastClickIndex, channelList.get(lastClickIndex));
		// manager.pauseAll();
		pauseAll(manager.getValidChannelList(lastItemIndex));
	}

	public void pauseAll(ArrayList<Channel> channelList) {
		int size = channelList.size();
		for (int i = 0; i < size; i++) {
			pauseChannel(channelList.get(i));
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (null != popScreen) {
			popScreen.dismiss();
		}
		if (null != streamListView) {
			streamListView.setVisibility(View.GONE);
		}

		// [Neo] add black screen time
		Jni.setColor(lastClickIndex, 0, 0, 0, 0);

		if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
			// if (channelList.get(lastClickIndex).getParent().isCard()
			// || 8 == channelList.get(lastClickIndex).getAudioByte()) {
			// bottombut5.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.video_talk));
			// }
			// if (channelList.get(lastClickIndex).isSingleVoice()) {// 单向对讲
			// if (VOICECALL_LONG_CLICK) {
			// new TalkThread(lastClickIndex, 0).start();
			// VOICECALL_LONG_CLICK = false;
			// voiceTip.setVisibility(View.GONE);
			// }
			// }
			if (ONE_SCREEN != currentScreen) {
				changeWindow(ONE_SCREEN);
			}
		}

		showFunc(channelList.get(lastClickIndex), currentScreen, lastClickIndex);
	}

	private class Connecter extends Thread {

		@Override
		public void run() {
			try {
				Channel channel = null;
				int size = disconnectChannelList.size();

				MyLog.w(Consts.TAG_PLAY, "disconnect count: " + size);
				for (int i = 0; i < size; i++) {
					channel = disconnectChannelList.get(i);

					int index = channel.getIndex();
					boolean needConnect = false;
					for (Channel currentChannel : currentPageChannelList) {
						if (index == currentChannel.getIndex()) {
							MyLog.w(Consts.TAG_PLAY,
									"disconnect not for current: " + channel);
							needConnect = true;
							break;
						}
					}
					if (needConnect) {
						continue;
					}

					if (channel.isConnected() || channel.isConnecting()) {
						boolean result = Jni.disconnect(channel.getIndex());
						if (false == result) {
							MyLog.e(Consts.TAG_PLAY, "disconnect failed: "
									+ channel);
						} else {
							boolean needSleep = true;
							while (needSleep) {
								needSleep = false;
								if (channel.isConnected()
										|| channel.isConnecting()) {
									needSleep = true;
									MyLog.w(Consts.TAG_PLAY,
											"wait for change: " + channel);
									sleep(DISCONNECTION_MIN_PEROID);
									break;
								}
							}
						}
					} else {
						MyLog.w(Consts.TAG_PLAY, "disconnect has done: "
								+ index);
					}

				}
				disconnectChannelList.clear();

				handler.sendEmptyMessage(WHAT_DISMISS_PROGRESS);

				size = currentPageChannelList.size()
						+ connectChannelList.size();
				MyLog.w(Consts.TAG_PLAY, "connect count: " + size);
				for (int i = 0; i < size; i++) {
					boolean isPlayDirectly = false;

					if (i < currentScreen) {
						isPlayDirectly = true;
						channel = currentPageChannelList.get(i);
						MyLog.v(Consts.TAG_PLAY, "current: " + channel);
					} else {
						channel = connectChannelList.get(i - currentScreen);
						MyLog.v(Consts.TAG_PLAY, "addtional: " + channel);
					}

					if (false == channel.isConnected()
							&& false == channel.isConnecting()) {

						boolean result = connect(channel, isPlayDirectly);
						if (false == result) {
							MyLog.e(Consts.TAG_PLAY, "connect failed: "
									+ channel);
						} else {
							sleep(CONNECTION_MIN_PEROID);
						}

					} else if (channel.isConnecting()) {
						handler.sendMessage(handler.obtainMessage(
								WHAT_PLAY_STATUS, channel.getIndex(),
								ARG2_STATUS_CONNECTING));
					} else if (false == channel.isPaused()) {
						handler.sendMessage(handler.obtainMessage(
								WHAT_PLAY_STATUS, channel.getIndex(),
								ARG2_STATUS_CONNECTED));

						channel.setPaused(true);
						boolean result = resumeChannel(channel);
						if (false == result) {
							MyLog.e(Consts.TAG_PLAY, "force resume failed: "
									+ channel);
						} else {
							sleep(RESUME_VIDEO_MIN_PEROID);
							MyLog.e(Consts.TAG_PLAY, "force resume: " + channel);
						}

					} else {

						boolean result = resumeChannel(channel);
						if (false == result) {
							MyLog.e(Consts.TAG_PLAY, "resume failed: "
									+ channel);
						} else {
							sleep(RESUME_VIDEO_MIN_PEROID);
						}

					}

				}
				connectChannelList.clear();

			} catch (Exception e) {
				e.printStackTrace();
			}

			handler.sendEmptyMessage(WHAT_RESTORE_UI);
		}
	}

}
