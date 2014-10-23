package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

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
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.PlayUtil;

public class JVPlayActivity extends PlayActivity implements
		PlayWindowManager.OnUiListener {

	private static final int CONNECTING = 0x70;
	private static final int BUFFERING = 0x71;

	private PlayWindowManager manager;

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
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	HashMap<Integer, Boolean> surfaceCreatMap = new HashMap<Integer, Boolean>();

	private boolean needToast = false;

	private WifiAdmin wifiAdmin;
	private String ssid;

	protected MyPagerAdapter pagerAdapter;

	// xq
	private static final int WHAT_CHECK_SURFACE = 0x20;
	private static final int DELAY_CHECK_SURFACE = 200;
	private static final int DELAY_DOUBLE_CHECKER = 500;

	private int lastItemIndex;// 上次页数
	private int lastClickIndex;// 上次窗口索引

	private Timer doubleClickTimer;
	private boolean isDoubleClickCheck;

	private ArrayList<Channel> channelList;
	private ArrayList<Channel> currentPageChannelList;

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

		switch (what) {
		case Consts.CALL_CONNECT_CHANGE: {
			Log.i("TAG", channelList.size()+"size"+arg1);
			Channel channel = channelList.get(arg1);
			if (null == channel) {
				return;
			}
			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				channel.setConnecting(false);
				channel.setConnected(true);
				break;
			}

			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				channel.setConnecting(false);
				channel.setConnected(false);
				channel.setPaused(true);
				break;
			}

			// 3 -- 不必要重复连接
			case JVNetConst.NO_RECONNECT: {
				channel.setConnecting(false);
				channel.setConnected(true);
				break;
			}

			// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED: {
				channel.setConnecting(false);
				channel.setConnected(false);
				channel.setPaused(true);
				break;
			}

			// 5 -- 没有连接
			case JVNetConst.NO_CONNECT: {
				channel.setConnecting(false);
				channel.setConnected(false);
				channel.setPaused(true);
				break;
			}

			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				channel.setConnecting(false);
				channel.setConnected(false);
				channel.setPaused(true);
				break;
			}

			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				channel.setConnecting(false);
				channel.setConnected(false);
				channel.setPaused(true);
				break;
			}

			// 8 -- 断开连接失败
			case JVNetConst.DISCONNECT_FAILED: {
				channel.setConnecting(false);
				channel.setConnected(true);
				channel.setPaused(true);
				break;
			}

			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				channel.setConnecting(false);
				channel.setConnected(false);
				channel.setPaused(true);
				break;
			}
			default:
				break;
			}

			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			viewPager.setDisableSliding(false);
			break;
		}

		default:
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

		switch (what) {
		// 开始连接
		case JVConst.WHAT_STARTING_CONNECT: {
			loadingState(arg1, R.string.connecting, JVConst.PLAY_CONNECTING);
			break;
		}

		case Consts.CALL_CONNECT_CHANGE: {
			Channel channel = channelList.get(arg1);
			if (null == channel) {
				return;
			}
			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				loadingState(arg1, R.string.connecting_buffer,
						JVConst.PLAY_CONNECTING_BUFFER);
				break;
			}

			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
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
					} else {// "Connect failed!"
						loadingState(arg1, R.string.connect_failed,
								JVConst.PLAY_DIS_CONNECTTED);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}

			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				break;
			}

			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				loadingState(arg1, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				break;
			}
			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				resetFunc(channel);
				break;
			}
			default:
				break;
			}
			break;
		}

		case Consts.CALL_NORMAL_DATA: {

			Channel channel = channelList.get(arg1);
			if (null == channel) {
				return;
			}
			MyLog.v("NORMALDATA", obj.toString());
			// {"autdio_bit":16,"autdio_channel":1,"autdio_sample_rate":8000,"autdio_type":2,"auto_stop_recorder":false,"device_type":4,"fps":15.0,"height":288,"is05":true,"reserved":0,"start_code":290674250,"width":352}
			try {
				JSONObject jobj;
				jobj = new JSONObject(obj.toString());
				if (null != jobj) {
					channel.getParent().setDeviceType(
							jobj.optInt("device_type"));
					if (Consts.DEVICE_TYPE_IPC == jobj.optInt("device_type")) {
						channel.getParent().setHomeProduct(true);
						// channel.setSingleVoice(true);
					} else {
						channel.getParent().setHomeProduct(false);
						// channel.setSingleVoice(false);
					}
					channel.getParent().setO5(jobj.optBoolean("is05"));
					channel.setAudioType(jobj.getInt("audio_type"));
					channel.setAudioByte(jobj.getInt("audio_bit"));

					if (!jobj.optBoolean("is05")) {// 提示不支持04版本解码器
						// TODO
						errorDialog(getResources().getString(
								R.string.not_support_old));
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			MyLog.v("ChannelTag--2", "HomeProduct="
					+ channel.getParent().isHomeProduct());
			MyLog.v("ChannelTag--3", "SingleVoice=" + channel.isSingleVoice());

			// if (arg1 == lastClickIndex) {//当前屏幕
			// 是IPC，发文本聊天请求
			if (channel.getParent().isHomeProduct()) {
				// 请求文本聊天
				Jni.sendBytes(arg1, JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
			}

			if (recoding) {
				showTextToast(R.string.video_repaked);
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
			MyLog.v(TAG, "capture--what=" + what + ";arg1=" + arg1 + ";arg2="
					+ arg2);
			switch (arg2) {
			case Consts.BAD_SCREENSHOT_NOOP:
				PlayUtil.prepareAndPlay();
				showTextToast(Consts.CAPTURE_PATH);
				break;
			case Consts.BAD_SCREENSHOT_INIT:
				showTextToast(R.string.str_capture_error1);
				break;
			case Consts.BAD_SCREENSHOT_CONV:
				showTextToast(R.string.str_capture_error2);
				break;
			case Consts.BAD_SCREENSHOT_OPEN:
				showTextToast(R.string.str_capture_error3);
				break;
			default:
				break;
			}
			break;
		}

		case Consts.CALL_PLAY_AUDIO: {
			if (null != obj && null != audioQueue) {
				if (AUDIO_SINGLE) {// 单向对讲长按才发送语音数据
					if (VOICECALL_LONG_CLICK) {
						// 长按时只发送语音，不接收语音
					} else {
						byte[] data = (byte[]) obj;
						audioQueue.offer(data);
					}
				} else {// 双向对讲直接播放设备传过来的语音
					byte[] data = (byte[]) obj;
					audioQueue.offer(data);
				}
			}

			break;
		}

		case Consts.CALL_PLAY_DOOMED: {
			if (Consts.HDEC_BUFFERING == arg2) {
				loadingState(arg1, R.string.connecting_buffer,
						JVConst.PLAY_CONNECTING_BUFFER);
			}

			break;
		}

		case Consts.CALL_TEXT_DATA: {
			MyLog.e(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// // 获取基本文本信息
				// Jni.sendTextData(arg1,
				// JVNetConst.JVN_RSP_TEXTDATA, 8,
				// JVNetConst.JVN_REMOTE_SETTING);
				// 获取主控码流信息请求
				MyLog.e(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
						+ ", " + obj);
				Jni.sendTextData(arg1, JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_STREAM_INFO);
				channelList.get(arg1).setAgreeTextData(true);
				MyLog.v("ChannelTag--4",
						"AgreeTextData="
								+ channelList.get(arg1).isSingleVoice());
				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天
				MyLog.v("ChannelTag--4",
						"AgreeTextData="
								+ channelList.get(arg1).isSingleVoice());
				channelList.get(arg1).setAgreeTextData(false);
				break;

			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				String allStr = obj.toString();
				try {
					JSONObject dataObj = new JSONObject(allStr);
					// MyLog.v(TAG, "文本数据--"+obj.toString());
					switch (dataObj.getInt("flag")) {
					// 远程配置请求，获取到配置文本数据
					case JVNetConst.JVN_REMOTE_SETTING: {
						// String settingJSON =
						dataObj.getString("msg");
						break;
					}
					case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
						break;
					case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求
						MyLog.e(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", "
								+ arg2 + ", " + obj);
						String streamJSON = dataObj.getString("msg");
						HashMap<String, String> streamMap = ConfigUtil
								.genMsgMap(streamJSON);
						if (null != streamMap) {
							if (null != streamMap.get("effect_flag")
									&& !"".equalsIgnoreCase(streamMap
											.get("effect_flag"))) {
								channelList.get(arg1).setScreenTag(
										Integer.parseInt(streamMap
												.get("effect_flag")));
							}

							if (null != streamMap.get("MainStreamQos")
									&& !"".equalsIgnoreCase(streamMap
											.get("MainStreamQos"))) {
								channelList.get(arg1).setStreamTag(
										Integer.parseInt(streamMap
												.get("MainStreamQos")));
							}

							if (null != streamMap.get("storageMode")
									&& !"".equalsIgnoreCase(streamMap
											.get("storageMode"))) {
								channelList.get(arg1).setStorageMode(
										Integer.parseInt(streamMap
												.get("storageMode")));
							}

							if (null != streamMap.get("MobileCH")
									&& "2".equalsIgnoreCase(streamMap
											.get("MobileCH"))) {
								channelList.get(arg1).setSingleVoice(true);
							}

							MyLog.v("ChannelTag--5", "ScreenTag="
									+ channelList.get(arg1).getScreenTag());
							MyLog.v("ChannelTag--6", "StreamTag="
									+ channelList.get(arg1).getStreamTag());
							MyLog.v("ChannelTag--7", "StorageMode="
									+ channelList.get(arg1).getStorageMode());
						}

						MyLog.v("refreshIPCFun--Stream=", arg1 + "");

						if (currentScreen == ONE_SCREEN) {
							refreshIPCFun(channelList.get(arg1));
						} else {
							decodeBtn.setVisibility(View.GONE);
							rightFuncButton.setVisibility(View.GONE);
							right_btn_h.setVisibility(View.GONE);
							videTurnBtn.setVisibility(View.GONE);
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
						if (Consts.STORAGEMODE_NORMAL == channelList.get(arg1)
								.getStorageMode()) {
							channelList.get(arg1).setStorageMode(
									Consts.STORAGEMODE_ALARM);
						} else if (Consts.STORAGEMODE_ALARM == channelList.get(
								arg1).getStorageMode()) {
							channelList.get(arg1).setStorageMode(
									Consts.STORAGEMODE_NORMAL);
						}
						MyLog.v("refreshIPCFun--record=", arg1 + "");
						if (currentScreen == ONE_SCREEN) {
							refreshIPCFun(channelList.get(arg1));
						} else {
							decodeBtn.setVisibility(View.GONE);
							rightFuncButton.setVisibility(View.GONE);
							right_btn_h.setVisibility(View.GONE);
							videTurnBtn.setVisibility(View.GONE);
						}
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
			MyLog.v(TAG + "Chat", "CALL_CHAT_DATA:arg1=" + arg1 + ",arg2="
					+ arg2);
			switch (arg2) {
			// 语音数据
			case JVNetConst.JVN_RSP_CHATDATA: {
				MyLog.v(TAG, "chatdata");
				break;
			}

			// 同意语音请求
			case JVNetConst.JVN_RSP_CHATACCEPT: {
				channelList.get(lastClickIndex).setVoiceCall(true);
				VOICECALLING = true;
				voiceCallSelected(true);
				// [Neo] TODO 根据连接的设备 NORMAL_DATA 取出 audio_type，填进来
				recorder.start(channelList.get(lastClickIndex).getAudioType(),
						channelList.get(lastClickIndex).getAudioByte());
				break;
			}

			// 暂停语音聊天
			case JVNetConst.JVN_CMD_CHATSTOP: {
				if (realStop) {
					realStop = false;
				} else {
					showTextToast(R.string.has_calling);
				}

				break;
			}
			}
			break;
		}

		case Consts.CALL_QUERY_DEVICE: {
			MyLog.d(Consts.TAG_PLAY, "query-" + obj);
			break;
		}

		case Consts.CALL_HDEC_TYPE: {
			String objStr = obj.toString();
			break;
		}

		case Consts.CALL_FRAME_I_REPORT: {

			Channel channel = channelList.get(arg1);
			channel.setConnected(true);
			channel.setPaused(false);
			channel.setConnected(true);

			loadingState(arg1, 0, JVConst.PLAY_CONNECTTED);
			MyLog.e(TAG + "-IFrame", "new Frame I: index = " + arg1
					+ ", arg2 = " + arg2);// arg2 --- 0软 1硬
			// 多于四屏只发关键帧
			if (currentScreen > FOUR_SCREEN
					&& !channelList.get(arg1).isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_ONLYI, new byte[0],
						0);
				channel.setSendCMD(true);
			} else if (currentScreen <= FOUR_SCREEN
					&& channelList.get(arg1).isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_FULL, new byte[0],
						0);
				channel.setSendCMD(true);
			}

			channel.setConnecting(false);
			channel.setConnected(true);

			MyLog.v("ChannelTag--IFrame=arg2", "arg2=" + arg2 + ",isomx="
					+ channelList.get(arg1).isOMX());

			if (!channel.isOMX() && arg2 == Consts.DECODE_SOFT) {
				// TODO
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

			MyLog.v("refreshIPCFun--IFrame=", arg1 + "");
			if (currentScreen == ONE_SCREEN) {
				refreshIPCFun(channelList.get(arg1));
			} else {
				decodeBtn.setVisibility(View.GONE);
				rightFuncButton.setVisibility(View.GONE);
				right_btn_h.setVisibility(View.GONE);
				videTurnBtn.setVisibility(View.GONE);
			}
			MyLog.i(Consts.TAG_PLAY, "new Frame I: window = " + arg1
					+ ", omx = " + arg2);
			break;
		}

		case WHAT_CHECK_SURFACE: {
			boolean isReady = true;
			final int size = currentPageChannelList.size();
			for (int i = 0; i < size; i++) {
				if (null == currentPageChannelList.get(i).getSurface()) {
					isReady = false;
					break;
				}
			}

			if (isReady) {
				if (1 != currentScreen) {
					@SuppressWarnings("unchecked")
					final ArrayList<Channel> temList = (ArrayList<Channel>) currentPageChannelList
							.clone();
					new Thread() {
						@Override
						public void run() {
							for (int i = 0; i < size; i++) {
								resumeChannel(temList.get(i));
								try {
									sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							}
						}

					};

					for (int i = 0; i < size; i++) {
						resumeChannel(currentPageChannelList.get(i));
					}
				} else {
					resumeChannel(channelList.get(arg2));
				}
			} else {
				handler.sendMessageDelayed(
						handler.obtainMessage(WHAT_CHECK_SURFACE, arg1, arg2),
						DELAY_CHECK_SURFACE);
			}
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
					isOmx = object.getBoolean("is_omx");
					// channelList.get(arg2).setOMX(isOmx);

					// MyLog.v("ChannelTag--IFrame=isOmx", "isOmx=" + isOmx);
					//
					// if (isOmx) {
					// decodeBtn.setText(R.string.is_omx);
					// } else {
					// decodeBtn.setText(R.string.not_omx);
					// }

					// [Neo] TODO
					// int index = object.getInt("window");
					// loadingState(index, 0, JVConst.PLAY_CONNECTTED);
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
			break;
		}
		case StreamAdapter.STREAM_ITEM_CLICK: {// 码流切换
			String streamParam = "MainStreamQos=" + (arg1 + 1);
			Jni.changeStream(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
					streamParam);
			streamListView.setVisibility(View.GONE);
			break;
		}

		default:
			MyLog.e(Consts.TAG_PLAY, "NO switch:" + what);
			break;
		}
	}

	@Override
	protected void initSettings() {
		TAG = "PlayA";

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
		manager.setArrowId(R.drawable.left, R.drawable.up, R.drawable.right,
				R.drawable.down);

		// [Neo] TODO make omx enable as default
		isOmx = true;

		Intent intent = getIntent();
		deviceIndex = intent.getIntExtra("DeviceIndex", 0);
		channelOfChannel = intent.getIntExtra("ChannelofChannel", 0);
		playFlag = intent.getIntExtra("PlayFlag", 0);

		currentScreen = intent.getIntExtra("Screen", 1);

		if (Consts.PLAY_NORMAL == playFlag) {
			deviceList = CacheUtil.getDevList();
		} else if (Consts.PLAY_DEMO == playFlag) {
			String devJsonString = MySharedPreference
					.getString(Consts.KEY_PLAY_DEMO);
			deviceList = Device.fromJsonArray(devJsonString);
		} else if (Consts.PLAY_AP == playFlag) {
			String devJsonString = MySharedPreference
					.getString(Consts.KEY_PLAY_AP);
			deviceList = Device.fromJsonArray(devJsonString);
		}
		startWindowIndex = 0;
		channelList = new ArrayList<Channel>();

		if (MySharedPreference.getBoolean("PlayDeviceMode")) {
			int size = deviceList.size();
			for (int i = 0; i < size; i++) {
				ArrayList<Channel> cList = deviceList.get(i).getChannelList()
						.toList();
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

	}

	@Override
	protected void initUi() {
		super.initUi();
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

		/** 上 */
		back.setOnClickListener(myOnClickListener);
		left_btn_h.setOnClickListener(myOnClickListener);

		selectScreenNum.setOnClickListener(myOnClickListener);
		currentMenu.setOnClickListener(myOnClickListener);
		if (playFlag == Consts.PLAY_AP) {
			currentMenu_h.setText(R.string.video_check);
			currentMenu.setText(R.string.video_check);
			selectScreenNum.setVisibility(View.GONE);
		} else {
			currentMenu_h.setText(R.string.str_video_play);
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
		pagerAdapter = new MyPagerAdapter();
		changeWindow(currentScreen);
		currentPageChannelList = manager.getValidChannelList(lastItemIndex);
		// handler.sendMessage(handler.obtainMessage(WHAT_CHECK_SURFACE,
		// lastItemIndex, lastItemIndex));

		viewPager.setLongClickable(true);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				stopAllFunc();
				MyLog.i(Consts.TAG_UI, ">>> pageSelected: " + arg0 + ", to "
						+ ((arg0 > lastItemIndex) ? "right" : "left"));

				viewPager.setDisableSliding(true);

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

				handler.removeMessages(WHAT_CHECK_SURFACE);
				handler.sendMessage(handler.obtainMessage(WHAT_CHECK_SURFACE,
						lastItemIndex, arg0));

				if (1 != currentScreen) {
					final ArrayList<Channel> lastList = manager
							.getValidChannelList(lastItemIndex);

					new Thread() {

						@Override
						public void run() {
							int size = lastList.size();
							Channel channel = null;
							for (int i = 0; i < size; i++) {
								channel = lastList.get(i);
								if (channel.isConnected()) {
									Jni.disconnect(channel.getIndex());
								}
							}
						}

					}.start();
				} else {
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
				}

				lastItemIndex = arg0;
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
		bottombut1.setOnClickListener(myOnClickListener);
		bottombut2.setOnClickListener(myOnClickListener);
		bottombut3.setOnClickListener(myOnClickListener);
		bottombut4.setOnClickListener(myOnClickListener);
		bottombut5.setOnClickListener(myOnClickListener);
		bottombut6.setOnClickListener(myOnClickListener);
		bottombut7.setOnClickListener(myOnClickListener);
		bottombut8.setOnClickListener(myOnClickListener);
		bottomStream.setOnClickListener(myOnClickListener);

		nextStep.setOnClickListener(myOnClickListener);

		Jni.setStat(true);
		// 2.几的系统有可能花屏
		if (MobileUtil.mobileSysVersion(JVPlayActivity.this).startsWith("2")) {
			errorDialog(getResources().getString(R.string.system_lower)
					.replace("$",
							MobileUtil.mobileSysVersion(JVPlayActivity.this)));
		} else if (Build.VERSION_CODES.JELLY_BEAN > Build.VERSION.SDK_INT) {// 小于4.1的系统，不允许硬解
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

				if (1 == currentScreen) {
					Channel channel = manager.getChannel(position);
					if (channel.isConnected()
							&& lastClickIndex != channel.getIndex()) {
						final int index = channel.getIndex();
						new Thread() {

							@Override
							public void run() {
								Jni.disconnect(index);
							}

						}.start();
					}
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

	public boolean resumeChannel(final Channel channel) {
		boolean result = false;

		if (null != channel) {
			if (false == channel.isConnected()) {
				new Thread() {

					@Override
					public void run() {
						if (connect(channel.getParent(), channel, true, false,
								false)) {
							channel.setPaused(false);
						}
					}

				}.start();

			} else if (channel.isPaused() && null != channel.getSurface()) {
				// loadingState(channel.getIndex(), R.string.connecting_buffer,
				// JVConst.PLAY_CONNECTING_BUFFER);
				result = Jni.resume(channel.getIndex(), channel.getSurface());
				if (result) {
					Jni.sendBytes(channel.getIndex(), JVNetConst.JVN_CMD_VIDEO,
							new byte[0], 8);
					channel.setPaused(false);
					handler.sendMessage(handler.obtainMessage(
							Consts.CALL_CONNECT_CHANGE, channel.getIndex(),
							JVNetConst.CONNECT_OK));

					// [Neo] TODO
					viewPager.setDisableSliding(false);
				}
			}
		}

		MyLog.d(Consts.TAG_UI, "resume: " + result + ", " + channel);
		return result;
	}

	public boolean pauseChannel(Channel channel) {
		boolean result = false;

		if (null != channel) {
			if (channel.isConnected()) {
				if (false == channel.isPaused()) {
					channel.setPaused(true);
					result = Jni.pause(channel.getIndex());

					if (result) {
						Jni.sendBytes(channel.getIndex(),
								JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0], 8);
					}
				}
			}
		}

		MyLog.d(Consts.TAG_UI, "pause: " + result + ", " + channel);
		return result;
	}

	private void changeWindow(int count) {
		currentScreen = count;
		pagerAdapter.update(manager.genPageList(count));
		viewPager.setAdapter(pagerAdapter);
		lastItemIndex = ((lastClickIndex >= 0) ? (lastClickIndex / count) : 0);
		viewPager.setCurrentItem(lastItemIndex, false);
		viewPager.setDisableSliding(true);
		currentPageChannelList = manager.getValidChannelList(lastItemIndex);
		changeBorder((lastClickIndex >= 0) ? lastClickIndex : 0);

		handler.removeMessages(WHAT_CHECK_SURFACE);
		handler.sendMessage(handler.obtainMessage(WHAT_CHECK_SURFACE,
				lastItemIndex, lastItemIndex));
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

	private boolean connect(Device device, Channel channel,
			boolean isPlayDirectly, boolean isTryOmx, boolean isEnableAudio) {
		boolean result = false;

		if (null != device && null != channel) {
			// 如果是域名添加的设备需要先去解析IP
			String ip = "";
			int port = 0;
			if (2 == device.getIsDevice()) {
				ip = ConfigUtil.getInetAddress(device.getDoMain());
				port = 9101;
			}

			handler.sendMessage(handler.obtainMessage(
					JVConst.WHAT_STARTING_CONNECT, channel.getIndex(), 0));

			if (null != ssid
					&& channel.getParent().getFullNo().equalsIgnoreCase(ssid)) {
				// IP直连
				MyLog.v(TAG, device.getNo() + "--AP--直连接：" + device.getIp());
				Jni.connect(channel.getIndex(), channel.getChannel(), ip, port,
						device.getUser(), device.getPwd(), -1, device.getGid(),
						true, 1, true, (device.isHomeProduct() ? 6 : 6),
						channel.getSurfaceView().getHolder().getSurface(),
						isOmx);
			} else {
				int number = device.getNo();
				if (false == ("".equalsIgnoreCase(device.getIp()) || 0 == device
						.getPort())) {
					number = -1;
				}
				if (isPlayDirectly) {
					result = Jni.connect(channel.getIndex(),
							channel.getChannel(), device.getIp(),
							device.getPort(), device.getUser(),
							device.getPwd(), number, device.getGid(), true, 1,
							true, 6,// (device.isHomeProduct() ? 6 : 5),
							channel.getSurface(), isTryOmx);
				} else {
					result = Jni.connect(channel.getIndex(),
							channel.getChannel(), device.getIp(),
							device.getPort(), device.getUser(),
							device.getPwd(), number, device.getGid(), true, 1,
							true, 6,// (device.isHomeProduct() ? 6 : 5),
							null, isTryOmx);
					channel.setPaused(true);
				}

				channel.setConnected(true);

			}

			// Jni.enablePlayAudio(channel.getIndex(), isEnableAudio);
		}

		return result;
	}

	@Override
	public void onClick(Channel channel, boolean isFromImageView, int viewId) {
		MyLog.v(Consts.TAG_UI, ">>> click: " + channel.getIndex());
		if (isFromImageView) {// 播放按钮事件
			try {
				loadingState(channel.getIndex(), R.string.connecting,
						JVConst.PLAY_CONNECTTED);
				connect(channel.getParent(), channel, false, false, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
				return;
			}

			if (isDoubleClickCheck && lastClickIndex == channel.getIndex()) {// 双击
				if (ONE_SCREEN == currentScreen) {
					changeWindow(selectedScreen);
				} else {
					changeWindow(ONE_SCREEN);

					new Thread() {

						@Override
						public void run() {
							int size = channelList.size();
							Channel channel = null;
							for (int i = 0; i < size; i++) {
								channel = channelList.get(i);
								if (channel.isConnected()
										&& lastClickIndex != channel.getIndex()) {
									Jni.disconnect(channel.getIndex());
								}
							}
						}

					}.start();
				}
			} else {// 单击
				changeBorder(channel.getIndex());

				lastClickIndex = channel.getIndex();
				// 多屏选中才变蓝色
				if (currentScreen > ONE_SCREEN) {
				} else {
					if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
						if (View.VISIBLE == horPlayBarLayout.getVisibility()) {
							horPlayBarLayout.setVisibility(View.GONE);
						} else {
							horPlayBarLayout.setVisibility(View.VISIBLE);
						}
					}
				}

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

	@Override
	public void onLongClick(Channel channel) {
		MyLog.v(Consts.TAG_UI, "onLongClick: " + channel.getIndex());
	}

	@Override
	public void onLifecycle(int index, int status, Surface surface, int width,
			int height) {
		final Channel channel = channelList.get(index);

		boolean isFromCurrent = false;
		int size = currentPageChannelList.size();
		for (int i = 0; i < size; i++) {
			if (index == currentPageChannelList.get(i).getIndex()) {
				isFromCurrent = true;
				break;
			}
		}

		switch (status) {
		case PlayWindowManager.STATUS_CREATED:
			MyLog.i(Consts.TAG_UI, ">>> surface created: " + index + ", "
					+ isFromCurrent);
			channel.setSurface(surface);

			if (1 == currentScreen && false == isFromCurrent) {
				new Thread() {

					@Override
					public void run() {
						connect(deviceList.get(0), channel, false, false, false);
					}

				}.start();
			}
			break;

		case PlayWindowManager.STATUS_CHANGED:
			// [Neo] Empty
			break;

		case PlayWindowManager.STATUS_DESTROYED:
			MyLog.i(Consts.TAG_PLAY, ">>> surface destory: " + index);
			channel.setSurface(null);
			pauseChannel(channel);
			break;

		default:
			break;
		}
	}

	private String genLinkHelperJson() {
		JSONArray array = new JSONArray();
		JSONObject object = null;

		for (Device device : deviceList) {
			if (device.getNo() > 0) {
				try {
					object = new JSONObject();
					object.put("gid", device.getGid());
					object.put("no", device.getNo());
					object.put("channel", 1);
					object.put("name", device.getUser());
					object.put("pwd", device.getPwd());
					array.put(object);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return array.toString();
	}

	private class DoubleClickChecker extends TimerTask {

		@Override
		public void run() {
			cancel();
			isDoubleClickCheck = false;
		}

	}

	@Override
	protected void onResume() {
		manager.resumeAll();
		super.onResume();
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
			case R.id.nextstep: {// AP下一步
				backMethod(false);
				break;
			}
			case R.id.btn_left: {// 左边按钮
				backMethod(true);
				break;
			}
			case R.id.bottom_but2:
			case R.id.decodeway: {// 软硬解切换
				if (allowThisFuc(false)) {
					createDialog("");
					if (channel.isOMX()) {
						Jni.setOmx(lastClickIndex, false);
					} else {
						needToast = true;
						Jni.setOmx(lastClickIndex, true);
					}
					// I帧通知成功失败
				}
				break;
			}
			case R.id.bottom_but6:
			case R.id.overturn: {// 视频翻转
				if (allowThisFuc(false)) {

					String turnParam = "";
					if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
						turnParam = "effect_flag=" + Consts.SCREEN_OVERTURN;
					} else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
						turnParam = "effect_flag=" + Consts.SCREEN_NORMAL;
					}
					Jni.rotateVideo(lastClickIndex,
							JVNetConst.JVN_RSP_TEXTDATA, turnParam);

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
				if (popScreen == null) {
					if (null != screenList && 0 != screenList.size()) {

						screenAdapter = new ScreenAdapter(JVPlayActivity.this,
								screenList);
						screenListView = new ListView(JVPlayActivity.this);
						screenListView.setDivider(null);
						if (disMetrics.widthPixels < 1080) {
							popScreen = new PopupWindow(screenListView, 240,
									LinearLayout.LayoutParams.WRAP_CONTENT);
						} else {
							popScreen = new PopupWindow(screenListView, 400,
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
						screenListView.setCacheColorHint(JVPlayActivity.this
								.getResources().getColor(R.color.transparent));
						popScreen.showAsDropDown(currentMenu);
					}
				} else if (popScreen.isShowing()) {
					screenAdapter.notifyDataSetChanged();
					popScreen.dismiss();
				} else if (!popScreen.isShowing()) {
					screenAdapter.notifyDataSetChanged();
					popScreen.showAsDropDown(currentMenu);
				}
				break;
			case R.id.bottom_but8:
			case R.id.audio_monitor:// 音频监听
				initAudio(channelList.get(lastClickIndex).getAudioByte());
				if (allowThisFuc(true)) {
					if (channelList.get(lastClickIndex).isVoiceCall()) {
						showTextToast(R.string.audio_monitor_forbidden);
					} else {
						initAudio(channelList.get(lastClickIndex)
								.getAudioByte());
						if (!PlayUtil.audioPlay(lastClickIndex)) {
							functionListAdapter.selectIndex = -1;
							bottombut8
									.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.video_monitor_icon));
							if (null != playAudio) {
								playAudio.interrupt();
								playAudio = null;
							}
						} else {
							functionListAdapter.selectIndex = 0;
							bottombut8
									.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.video_monitorselect_icon));
						}
					}

				} else {
					functionListAdapter.selectIndex = -1;
					bottombut8.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.video_monitor_icon));
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
				if (hasSDCard() && allowThisFuc(false)) {
					boolean capture = PlayUtil.capture(lastClickIndex);
					MyLog.v(TAG, "capture=" + capture);
				}
				break;
			case R.id.bottom_but5:
			case R.id.funclayout:// AP功能列表对讲功能
			case R.id.voicecall:// 语音对讲
				initAudio(channelList.get(lastClickIndex).getAudioByte());
				if (allowThisFuc(true)) {
					// 停止音频监听
					if (PlayUtil.isPlayAudio(lastClickIndex)) {
						PlayUtil.audioPlay(lastClickIndex);
						functionListAdapter.selectIndex = -1;
						bottombut8.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.video_monitor_icon));
						playAudio.setIndex(lastClickIndex);
						if (null != playAudio) {
							playAudio.interrupt();
							playAudio = null;
						}

					}

					if (channelList.get(lastClickIndex).isVoiceCall()) {
						if (null != recorder) {
							recorder.stop();
						}
						if (null != audioQueue) {// 清队列，防止停止后仍然播放
							audioQueue.clear();
						}
						PlayUtil.stopVoiceCall(lastClickIndex);
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
						if (channelList.get(lastClickIndex).isSingleVoice()) {
							showTextToast(R.string.voice_tips2);
						}

						PlayUtil.startVoiceCall(lastClickIndex);
						if (Consts.PLAY_AP == playFlag) {
							functionListAdapter.selectIndex = 2;
						}
					}
				}
				functionListAdapter.notifyDataSetChanged();
				break;
			case R.id.bottom_but7:
			case R.id.videotape:// 录像
				if (hasSDCard() && allowThisFuc(true)) {
					if (PlayUtil.videoRecord(lastClickIndex)) {// 打开
						tapeSelected(true);
						showTextToast(R.string.str_start_record);
					} else {// 关闭
						showTextToast(Consts.VIDEO_PATH);
						tapeSelected(false);
					}
				}

				break;
			case R.id.video_bq:
			case R.id.more_features:// 码流
				if (View.VISIBLE == streamListView.getVisibility()) {
					streamListView.setVisibility(View.GONE);
				} else {
					if (allowThisFuc(true)) {
						if (-1 == channelList.get(lastClickIndex)
								.getStreamTag()) {
							showTextToast(R.string.stream_not_support);
						} else {
							streamListView.setVisibility(View.VISIBLE);
						}
					}
				}
				break;

			// case R.id.bottom_but1:// 暂停继续播
			// if (channelList.get(lastClickIndex).isPause()) {
			// Jni.sendBytes(lastClickIndex,
			// (byte) JVNetConst.JVN_CMD_VIDEO, new byte[0], 8);
			// channelList.get(lastClickIndex).setPause(false);
			// bottombut1
			// .setBackgroundResource(R.drawable.video_stop_icon);
			// } else {
			// Jni.sendBytes(lastClickIndex,
			// (byte) JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0],
			// 8);
			// channelList.get(lastClickIndex).setPause(true);
			// bottombut1
			// .setBackgroundResource(R.drawable.video_play_icon);
			// }
			// break;

			case R.id.bottom:

				break;
			}

		}

	};

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
			if (Consts.PLAY_AP == playFlag) {
				Jni.disconnect(0);
			} else {
				PlayUtil.disConnectAll(manager.getChannelList());
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (Consts.PLAY_AP == playFlag) {
				Intent aintent = new Intent();
				if (apBack) {
					aintent.putExtra("AP_Back", true);
				} else {// next
					aintent.putExtra("AP_Back", false);
				}
				setResult(JVConst.AP_CONNECT_FINISHED, aintent);
				JVPlayActivity.this.finish();
			} else {
				JVPlayActivity.this.finish();
			}
		}

	}

	@Override
	public void onBackPressed() {
		backMethod(true);
	}

	/**
	 * 停止所有事件
	 */
	@SuppressWarnings("deprecation")
	public void stopAllFunc() {
		// 停止音频监听
		if (PlayUtil.isPlayAudio(lastClickIndex)) {
			PlayUtil.audioPlay(lastClickIndex);
			functionListAdapter.selectIndex = -1;
			bottombut8.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_monitor_icon));
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
			if (null != recorder) {
				recorder.stop();
			}
			if (null != audioQueue) {
				audioQueue.clear();
			}
			channelList.get(lastClickIndex).setVoiceCall(false);
			realStop = true;
			voiceCallSelected(false);
			PlayUtil.stopVoiceCall(lastClickIndex);
		}
	}

	@Override
	protected void freeMe() {
		stopAllFunc();
		manager.destroy();
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
			Thread.sleep(400);
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
		MyLog.v(TAG, "lastClickIndex = " + lastClickIndex);
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
		remoteIntent.putExtra("DeviceType", channelList.get(lastClickIndex)
				.getParent().getDeviceType());
		remoteIntent.putExtra("is05", channelList.get(lastClickIndex)
				.getParent().is05());
		remoteIntent.putExtra("AudioByte", channelList.get(lastClickIndex)
				.getAudioByte());
		JVPlayActivity.this.startActivity(remoteIntent);
	}

	/**
	 * 单项对讲用功能
	 */
	OnTouchListener callOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if (channelList.get(lastClickIndex).isSingleVoice()) {// 单向对讲
				if (arg1.getAction() == MotionEvent.ACTION_UP
						|| arg1.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
					new TalkThread(lastClickIndex, 0).start();
					VOICECALL_LONG_CLICK = false;
					voiceTip.setVisibility(View.GONE);
				}
			}
			return false;
		}

	};

	/**
	 * 单项对讲用功能
	 */
	OnLongClickListener callOnLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View arg0) {
			if (channelList.get(lastClickIndex).isSingleVoice()) {// 单向对讲
				if (VOICECALLING) {// 正在喊话
					VOICECALL_LONG_CLICK = true;
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
	private void errorDialog(String errorMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVPlayActivity.this);
		builder.setCancelable(false);
		builder.setMessage(errorMsg);

		builder.setNegativeButton(R.string.download,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Uri uri = Uri
								.parse("http://www.jovetech.com/UpLoadFiles/file/CloudSEE_V3.0.2_anzhi.apk");
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(it);
					}

				});

		builder.setPositiveButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						handler.sendMessageDelayed(handler
								.obtainMessage(JVConst.WHAT_START_CONNECT),
								1000);
					}

				});

		builder.show();
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
			ViewGroup container = (ViewGroup) manager.getView(index)
					.getParent();
			switch (tag) {
			case JVConst.PLAY_CONNECTING:// 连接中
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, View.VISIBLE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.VISIBLE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				manager.setInfo(container,
						getResources().getString(loadingState));// 连接文字
				break;
			case JVConst.PLAY_CONNECTTED:// 已连接
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, View.GONE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.GONE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				break;
			case JVConst.PLAY_DIS_CONNECTTED:// 断开
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, View.GONE);// loading
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
						getResources().getString(loadingState));// 连接文字
				break;
			case JVConst.PLAY_CONNECTING_BUFFER:// 缓冲中
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, View.VISIBLE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.VISIBLE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				manager.setInfo(container,
						getResources().getString(loadingState));// 连接文字
				break;
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

			if (0 == arg2) {// 音频监听
				if (allowThisFuc(true)) {
					if (channelList.get(lastClickIndex).isVoiceCall()) {
						showTextToast(R.string.audio_monitor_forbidden);
					} else {
						initAudio(channelList.get(lastClickIndex)
								.getAudioByte());
						if (!PlayUtil.audioPlay(lastClickIndex)) {
							functionListAdapter.selectIndex = -1;
							bottombut8
									.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.video_monitor_icon));
							if (null != playAudio) {
								playAudio.interrupt();
								playAudio = null;
							}
						} else {
							functionListAdapter.selectIndex = arg2;
							bottombut8
									.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.video_monitorselect_icon));
						}
					}

				} else {
					functionListAdapter.selectIndex = -1;
					bottombut8.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.video_monitor_icon));
				}

			} else if (1 == arg2) {// 云台
				if (allowThisFuc(false)) {
					showPTZ();
				} else {
					functionListAdapter.selectIndex = -1;
				}

			} else if (2 == arg2) {// 远程回放 或 对讲

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
	protected void onPause() {
		super.onPause();
		if (null != popScreen && popScreen.isShowing()) {
			screenAdapter.notifyDataSetChanged();
			popScreen.dismiss();
		}
		stopAll(lastClickIndex, channelList.get(lastClickIndex));
		manager.pauseAll();
		// PlayUtil.pauseAll(manager.getValidChannelList(lastItemIndex));

		if (Consts.PLAY_NORMAL == playFlag) {
			CacheUtil.saveDevList(deviceList);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
			if (channelList.get(lastClickIndex).isSingleVoice()) {// 单向对讲
				if (VOICECALL_LONG_CLICK) {
					new TalkThread(lastClickIndex, 0).start();
					VOICECALL_LONG_CLICK = false;
					voiceTip.setVisibility(View.GONE);
				}
			}

			if (currentScreen == ONE_SCREEN) {
				refreshIPCFun(channelList.get(lastClickIndex));
			} else {
				changeWindow(ONE_SCREEN);
				decodeBtn.setVisibility(View.GONE);
				rightFuncButton.setVisibility(View.GONE);
				right_btn_h.setVisibility(View.GONE);
				videTurnBtn.setVisibility(View.GONE);
			}
			viewPager.setDisableSliding(true);
		}
	}

}
