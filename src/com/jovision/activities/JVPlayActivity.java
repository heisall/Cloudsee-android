package com.jovision.activities;

import java.io.File;
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
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyGestureDispatcher;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.BitmapCache;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.PlayUtil;

public class JVPlayActivity extends PlayActivity implements
		PlayWindowManager.OnUiListener {
	private static final String TAG = "JVPlayActivity";
	private static final int DELAY_CHECK_SURFACE = 500;
	private static final int DELAY_DOUBLE_CHECKER = 500;
	private static final int CONNECTION_MIN_PEROID = 300;
	private static final int DISCONNECTION_MIN_PEROID = 100;
	private static final int RESUME_VIDEO_MIN_PEROID = 100;

	private GestureDetector mGestureDetector;

	private boolean isQuit;
	private boolean isBlockUi;

	private int lastItemIndex;
	private int lastClickIndex;

	private Timer doubleClickTimer;
	private boolean isDoubleClickCheck;// 双击事件
	private boolean isScrollClickCheck;// 手势滑动事件

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

	private boolean istalk = false;// 是否在对讲
	private boolean ishonfunctalk = false;// 横屏是否对讲

	private WifiAdmin wifiAdmin;
	private String ssid;
	private HashMap<String, String> powermap = new HashMap<String, String>();
	private Dialog initDialog;
	private TextView dialogCancel;
	private TextView dialogCompleted;
	private TextView devicepwd_name;
	private EditText devicepwd_nameet;
	private ImageView devicepwd_nameet_cancle;
	private EditText devicepwd_passwordet;
	private ImageView devicepwd_password_cancleI;
	private ImageView dialogpwd_cancle_img;
	private String mobileQuality, mobileCH;
	private HashMap<String, String> streamMap;
	private boolean updateStreaminfoFlag = false;
	/** 基于断开视频不走回调加此list */
	private MyList<Message> msgList = new MyList<Message>(0);

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		if (isQuit) {
			return;
		}

		switch (what) {
		case Consts.PLAY_AUDIO_WHAT: {
			MyLog.v(TAG, "PLAY_AUDIO_WHAT:what=" + what + ",arg1=" + arg1
					+ ",arg2=" + arg2 + ",obj=" + obj);
			break;
		}
		case Consts.CALL_LAN_SEARCH: {
			break;
		}

		case Consts.CALL_CONNECT_CHANGE: {
			MyLog.i(Consts.TAG_PLAY, "onNotify: changed, arg1=" + arg1
					+ ", arg2=" + arg2 + ", obj=" + obj);
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
				try {
					channel.setLastPortLeft(0);
					channel.setLastPortBottom(0);
					channel.setLastPortWidth(manager.getView(arg1).getWidth());
					channel.setLastPortHeight(manager.getView(arg1).getHeight());
					channel.setConnected(true);
					handler.sendMessage(handler.obtainMessage(what, arg1, arg2,
							obj));
				} catch (Exception e) {
					e.printStackTrace();
				}

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

	private class DoubleClickChecker extends TimerTask {

		@Override
		public void run() {
			cancel();
			isDoubleClickCheck = false;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		if (isQuit) {
			return;
		}
		switch (what) {
		case Consts.WHAT_DIALOG_CLOSE: {// 关闭dialog
			dismissDialog();
			break;
		}
		case Consts.WHAT_SURFACEVIEW_CLICK: {// 单击事件

			if (isScrollClickCheck) {
				MyLog.e("Click--", "手势云台：time=");
				lastClickTime = 0;
				isScrollClickCheck = false;
				return;
			}
			Channel channel = (Channel) obj;
			int x = arg1;
			int y = arg2;
			if (null != channel && channel.isConnected()
					&& !channel.isConnecting()) {
				boolean originSize = false;
				if (channel.getLastPortWidth() == channel.getSurfaceView()
						.getWidth()) {
					originSize = true;
				}

				if (isDoubleClickCheck) {
					MyLog.e("Click--", "双击：clickTimeBetween=");
				} else {
					MyLog.e("Click--", "单击：time=");
				}

				if (false == isBlockUi && isDoubleClickCheck
						&& lastClickIndex == channel.getIndex()) {// 双击

					if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
						Point vector = new Point();
						Point middle = new Point();
						middle.set(x, y);
						if (originSize) {// 双击放大
							vector.set(channel.getSurfaceView().getWidth(),
									channel.getSurfaceView().getHeight());
							gestureOnView(manager.getView(lastClickIndex),
									channel,
									MyGestureDispatcher.GESTURE_TO_BIGGER, 1,
									vector, middle);
						} else {// 双击还原
							vector.set(-channel.getSurfaceView().getWidth(),
									-channel.getSurfaceView().getHeight());
							gestureOnView(manager.getView(lastClickIndex),
									channel,
									MyGestureDispatcher.GESTURE_TO_SMALLER, -1,
									vector, middle);
						}
					}
				} else {// 单击
					closePopWindow();

					if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
						if (View.VISIBLE == horPlayBarLayout.getVisibility()) {
							horPlayBarLayout.setVisibility(View.GONE);
						} else {
							horPlayBarLayout.setVisibility(View.VISIBLE);
						}
					}
				}
			}

			lastClickTime = 0;
			break;
		}
		case Consts.WHAT_RESOLVE_IP_CONNECT: {// 解析完IP连接视频

			boolean isPlayDirectly = false;
			if (1 == arg1) {
				isPlayDirectly = true;
			}
			Channel channel = (Channel) obj;
			if (null != channel) {
				MyLog.v(TAG, "解析出来的IP=" + channel.getParent().getIp());
				boolean result = connect(channel, isPlayDirectly);
				if (false == result) {
					MyLog.e(Consts.TAG_XXX, "connect failed: " + channel);
				} else {
					MyLog.i(Consts.TAG_XXX, "connecting: " + channel);
					try {
						Thread.sleep(CONNECTION_MIN_PEROID);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			break;
		}
		case Consts.STOP_AUDIO_GATHER: {// 停止采集音频数据
			GATHER_AUDIO_DATA = false;
			break;
		}
		case Consts.START_AUDIO_GATHER: {// 开始采集音频数据
			GATHER_AUDIO_DATA = true;
			break;
		}
		case Consts.WHAT_CHECK_SURFACE: {
			MyLog.w(Consts.TAG_XXX, "> before connector : CHECK_SURFACE");

			boolean hasNull = false;
			boolean hasChannel = false;

			int size = currentPageChannelList.size();
			for (int i = 0; i < size; i++) {

				SurfaceView sf = currentPageChannelList.get(i).getSurfaceView();
				// MyLog.v(Consts.TAG_XXX,
				// "check surface.visible="+sf.getVisibility()+
				// ",width="+sf.getWidth()+",height="+sf.getHeight());
				hasChannel = true;
				if (null == currentPageChannelList.get(i).getSurface()) {
					hasNull = true;
					break;
				}
			}

			if (hasChannel && false == hasNull) {
				new Connecter().start();
			} else {
				handler.sendMessageDelayed(handler.obtainMessage(
						Consts.WHAT_CHECK_SURFACE, arg1, arg2),
						DELAY_CHECK_SURFACE);
			}
			break;
		}

		case Consts.WHAT_RESTORE_UI: {
			isBlockUi = false;
			if (null != playViewPager) {
				playViewPager.setDisableSliding(isBlockUi);
			}
			break;
		}

		case Consts.WHAT_SHOW_PROGRESS: {
			createDialog("", true);
			break;
		}
		case Consts.WHAT_FINISH: {
			// isQuit = true;
			// finish();
			dismissDialog();
			break;
		}
		case Consts.WHAT_DISMISS_PROGRESS: {

			dismissDialog();
			break;
		}

		case Consts.CALL_CONNECT_CHANGE: {
			MyLog.d(Consts.TAG_PLAY, "onHandler: changed, arg1=" + arg1
					+ ", arg2=" + arg2 + ", obj=" + obj);
			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}

			MyLog.v("resetFunc111",
					"arg1=" + arg1 + ";channel=" + channel.toString());

			switch (arg2) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				loadingState(arg1, R.string.connecting_buffer1,
						Consts.TAG_PLAY_CONNECTING_BUFFER);
				// MyLog.v(TAG,"CALL_CONNECT_CHANGE+buffering,index="+channel.getIndex());
				if (currentPageChannelList.contains(channel)) {
					MyLog.i(Consts.TAG_XXX,
							"recheck, need resume(" + channel.isPaused()
									+ "): " + channel);
					if (channel.isPaused()) {
						resumeChannel(channel);
					}
				}

				break;
			}

			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				loadingState(arg1, R.string.closed,
						Consts.TAG_PLAY_DIS_CONNECTTED);
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
								Consts.TAG_PLAY_DIS_CONNECTTED);
						if (ONE_SCREEN == currentScreen) {
							passErrorDialog(arg1);
						}

					} else if ("channel is not open!"
							.equalsIgnoreCase(errorMsg)) {// 无该通道服务
						loadingState(arg1, R.string.connfailed_channel_notopen,
								Consts.TAG_PLAY_DIS_CONNECTTED);
					} else if ("connect type invalid!"
							.equalsIgnoreCase(errorMsg)) {// 连接类型无效
						loadingState(arg1, R.string.connfailed_type_invalid,
								Consts.TAG_PLAY_DIS_CONNECTTED);
					} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
						loadingState(arg1, R.string.connfailed_maxcount,
								Consts.TAG_PLAY_DIS_CONNECTTED);
					} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
						loadingState(arg1, R.string.connfailed_timeout,
								Consts.TAG_PLAY_DIS_CONNECTTED);
					} else if ("check password timeout!"
							.equalsIgnoreCase(errorMsg)) {// 验证密码超时
						loadingState(arg1,
								R.string.connfailed_checkpass_timout,
								Consts.TAG_PLAY_DIS_CONNECTTED);
					} else {// "Connect failed!"
						loadingState(arg1, R.string.connect_failed,
								Consts.TAG_PLAY_DIS_CONNECTTED);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					loadingState(arg1, R.string.closed,
							Consts.TAG_PLAY_DIS_CONNECTTED);
				}

				// resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				loadingState(arg1, R.string.abnormal_closed,
						Consts.TAG_PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				loadingState(arg1, R.string.abnormal_closed,
						Consts.TAG_PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				loadingState(arg1, R.string.closed,
						Consts.TAG_PLAY_DIS_CONNECTTED);
				resetFunc(channel);
				showFunc(channel, currentScreen, lastClickIndex);
				break;
			}

			default:
				loadingState(arg1, R.string.closed,
						Consts.TAG_PLAY_DIS_CONNECTTED);
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
					Consts.TAG_PLAY_CONNECTING_BUFFER);
			// MyLog.v(TAG,"CALL_NORMAL_DATA+buffering,index="+channel.getIndex());
			if (currentScreen > FOUR_SCREEN && !channel.isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_ONLYI, new byte[0],
						0);
				channel.setSendCMD(true);
			} else if (currentScreen <= FOUR_SCREEN && channel.isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_FULL, new byte[0],
						0);
				channel.setSendCMD(true);
			}

			MyLog.e("NORMALDATA", obj.toString());
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

					// Boolean info = jobj.getBoolean("auto_stop_recorder");
					// MyLog.e("wodimaya", "auto_stop_recorder"+info);

					if (8 == channel.getAudioByte()
							&& Consts.DEVICE_TYPE_DVR == type) {
						channel.setSupportVoice(false);
					} else {
						channel.setSupportVoice(true);
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
				// 是IPC，发文本聊天请求
				if (channel.isAgreeTextData()) {
					// 获取主控码流信息请求
					Jni.sendTextData(arg1, JVNetConst.JVN_RSP_TEXTDATA, 8,
							JVNetConst.JVN_STREAM_INFO);
				} else {
					// 请求文本聊天
					Jni.sendBytes(arg1, JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
				}

			}

			if (recoding) {
				stopRecord(true);
				String path = PlayUtil.createRecordFile();
				if (!PlayUtil.checkRecord(lastClickIndex)) {
					startRecord(lastClickIndex, path);
				}
			}

			break;
		}

		case Consts.CALL_DOWNLOAD: {
			MyLog.d(Consts.TAG_PLAY, "download: " + arg1 + ", " + arg2 + ", "
					+ obj);
			break;
		}

		// case Consts.CALL_GOT_SCREENSHOT: {
		// MyLog.i(TAG, "CALL_GOT_SCREENSHOT:what=" + what + ";arg1=" + arg1
		// + ";arg2=" + arg2);
		// switch (arg2) {
		// case Consts.BAD_SCREENSHOT_NOOP:
		// PlayUtil.prepareAndPlay(CAPTURING);
		// if (CAPTURING) {
		// CAPTURING = false;
		// showTextToast(Consts.CAPTURE_PATH);
		// }
		// MyLog.e("capture", "success");
		// break;
		// case Consts.BAD_SCREENSHOT_INIT:
		// if (CAPTURING) {
		// showTextToast(R.string.str_capture_error);
		// }
		// break;
		// case Consts.BAD_SCREENSHOT_CONV:
		// if (CAPTURING) {
		// showTextToast(R.string.str_capture_error);
		// }
		// break;
		// case Consts.BAD_SCREENSHOT_OPEN:
		// if (CAPTURING) {
		// showTextToast(R.string.str_capture_error);
		// }
		// break;
		// default:
		// break;
		// }
		// break;
		// }

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
		case Consts.CALL_PLAY_DOOMED: {
			// if (Consts.HDEC_BUFFERING == arg2) {
			// loadingState(arg1, R.string.connecting_buffer2,
			// JVConst.PLAY_CONNECTING_BUFFER);
			// }
			switch (arg2) {
			case Consts.VIDEO_SIZE_CHANGED: {
				try {
					JSONObject object = new JSONObject(String.valueOf(obj));
					MyLog.e("PLAY_DOOMED", obj.toString());
					// showTextToast(getResources().getString(R.string.play_failed)+":"+object.getInt("width")+"x"+object.getInt("height"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			}
			}

			break;
		}

		case Consts.WHAT_PLAY_STATUS: {
			switch (arg2) {
			case Consts.ARG2_STATUS_CONNECTING:
				loadingState(arg1, R.string.connecting,
						Consts.TAG_PLAY_CONNECTING);
				break;

			case Consts.ARG2_STATUS_CONNECTED:
				loadingState(arg1, R.string.connecting_buffer1,
						Consts.TAG_PLAY_CONNECTTED);
				break;

			case Consts.ARG2_STATUS_BUFFERING:
				loadingState(arg1, R.string.connecting_buffer2,
						Consts.TAG_PLAY_CONNECTING_BUFFER);
				break;

			case Consts.ARG2_STATUS_DISCONNECTED:
				loadingState(arg1, R.string.closed,
						Consts.TAG_PLAY_DIS_CONNECTTED);
				break;

			case Consts.ARG2_STATUS_HAS_CONNECTED:
				loadingState(arg1, R.string.connfailed_timeout,
						Consts.TAG_PLAY_DIS_CONNECTTED);
				break;

			case Consts.ARG2_STATUS_CONN_OVERFLOW:
				loadingState(arg1, R.string.overflow,
						Consts.TAG_PLAY_DIS_CONNECTTED);
				break;

			// case Consts.ARG2_STATUS_UNKNOWN:
			// loadingState(arg1, R.string.closed,
			// Consts.TAG_PLAY_STATUS_UNKNOWN);
			// break;

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

				// 2014-12-25 获取设备用户名密码,不区分家用和非家用产品
				Jni.sendSuperBytes(arg1, JVNetConst.JVN_RSP_TEXTDATA, true,
						Consts.RC_EX_ACCOUNT, Consts.EX_ACCOUNT_REFRESH,
						Consts.POWER_ADMIN, 0, 0, new byte[0], 0);

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
					case JVNetConst.JVN_GET_USERINFO: {// 20
						int extend_type = dataObj.getInt("extend_type");
						if (Consts.EX_ACCOUNT_REFRESH == extend_type) {
							// CALL_TEXT_DATA: 165, 0, 81,
							// {"extend_arg1":64,"extend_arg2":0,"extend_arg3":0,
							// "extend_msg":
							// "+ID=admin;POWER=4;DESCRIPT=新帐户;+ID=abc;POWER=4;DESCRIPT=新帐户;",
							// "extend_type":3,"flag":20,"packet_count":4,"packet_id":0,"packet_length":0,"packet_type":6}
							String InfoJSON = dataObj.getString("extend_msg");
							InfoJSON = InfoJSON.replaceAll("ID", "+ID");
							String[] array = InfoJSON.split("\\+");
							for (int i = 1; i < array.length; i++) {
								if (null != array[i] && !array[i].equals("")) {
									HashMap<String, String> idomap = new HashMap<String, String>();
									idomap = ConfigUtil.genMsgMap(array[i]);
									int power = Integer.parseInt(idomap
											.get("POWER"));
									String descript = idomap.get("DESCRIPT");
									if (idomap.get("ID").equals("admin")
											&& 4 == (0x04 & power)) {
										MyLog.e("power-", "" + power);
										channelList.get(arg1).getParent()
												.setAdmin(true);
										channelList.get(arg1).getParent()
												.setPower(power);
										if (null == descript
												|| "".equals(descript)) {
											channelList.get(arg1).getParent()
													.setDescript("");
										} else {
											channelList.get(arg1).getParent()
													.setDescript(descript);
										}

									}
								}
							}
							// String [] array = InfoJSON.split(";");
							// for (int i = 0; i < array.length; i++) {
							// if
							// (null!=array[i]&&array[i].toString().substring(0,
							// 2).equals("ID")) {
							// idomap.put(i+"", array[i].toString().substring(3,
							// array[i].toString().length()));
							// Log.i("TAG", "获取用户名密码"+array[i].toString());
							// Log.i("TAG",
							// "前几位"+array[i].toString().substring(0,
							// 2));
							// }
							// if
							// (null!=array[i]&&array[i].toString().substring(0,
							// 2).equals("ID")) {
							// }
							// }
						}
						break;
					}
					// 远程配置请求，获取到配置文本数据
					case JVNetConst.JVN_REMOTE_SETTING: {// 1--

						break;
					}
					case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
						break;
					case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求
						MyLog.i(TAG, "JVN_STREAM_INFO:TEXT_DATA: " + what
								+ ", " + arg1 + ", " + arg2 + ", " + obj);
						String streamJSON = dataObj.getString("msg");
						// HashMap<String, String> streamCH1 =
						// ConfigUtil.getCH1("CH1",streamJSON);
						// HashMap<String, String>
						streamMap = ConfigUtil.genMsgMap(streamJSON);
						updateStreaminfoFlag = true;
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
							}
							// TODO
							MyLog.v(TAG,
									"SupportVoice=" + channel.isSupportVoice());
							mobileCH = streamMap.get("MobileCH");
							if (null != streamMap.get("MobileCH")
									&& "2".equalsIgnoreCase(streamMap
											.get("MobileCH"))) {
								MyLog.e(TAG,
										"MobileCH=" + streamMap.get("MobileCH")
												+ "--单向对讲");
								channel.setSingleVoice(true);
							} else {
								MyLog.e(TAG,
										"MobileCH=" + streamMap.get("MobileCH")
												+ "--双向对讲");
								channel.setSingleVoice(false);
							}
							mobileQuality = streamMap.get("MobileQuality");
							if (null != streamMap.get("MobileQuality")
									&& !"".equalsIgnoreCase(streamMap
											.get("MobileQuality"))) {
								channel.getParent().setOldDevice(false);
								MyLog.v(TAG,
										"MobileQuality="
												+ streamMap
														.get("MobileQuality"));
								channel.setStreamTag(Integer.parseInt(streamMap
										.get("MobileQuality")));
								channel.setNewIpcFlag(true);
							} else {
								channel.setNewIpcFlag(false);
								if (channel.getParent().isOldDevice()) {// 已经取到标志位是老的设备了
									if (null != streamMap.get("MobileCH")
											&& "2".equalsIgnoreCase(streamMap
													.get("MobileCH"))) {
										if (null != streamMap
												.get("MainStreamQos")
												&& !"".equalsIgnoreCase(streamMap
														.get("MainStreamQos"))) {
											MyLog.v(TAG,
													"MainStreamQos="
															+ streamMap
																	.get("MainStreamQos"));
											channel.setStreamTag(Integer.parseInt(streamMap
													.get("MainStreamQos")));

										}

									}
								} else {
									if (null != streamMap.get("MobileCH")
											&& "2".equalsIgnoreCase(streamMap
													.get("MobileCH"))) {
										String strParam = streamJSON
												.substring(
														streamJSON
																.lastIndexOf("[CH2];") + 6,
														streamJSON.length());
										HashMap<String, String> ch2Map = ConfigUtil
												.genMsgMap1(strParam);
										int width = Integer.valueOf(ch2Map
												.get("width"));
										if (624 <= width) {
											channel.setStreamTag(2);
										} else {
											channel.setStreamTag(3);
										}
										MySharedPreference.putBoolean(channel
												.getParent().getFullNo(), true);
									} else {
										MySharedPreference
												.putBoolean(channel.getParent()
														.getFullNo(), false);
									}

								}
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
				if (!ishonfunctalk) {
					ishonfunctalk = true;
					horfunc_talk.setVisibility(View.VISIBLE);
				}
				if (!istalk) {
					function.setVisibility(View.GONE);
					talk_eachother.setVisibility(View.VISIBLE);
					istalk = true;
				}
				Channel channel = channelList.get(lastClickIndex);
				if (channel.isSingleVoice()) {
					showTextToast(R.string.voice_tips2);
				}
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
				channel.setVoiceCall(true);
				VOICECALLING = true;
				voiceCallSelected(true);

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
			break;
		}

		case Consts.CALL_HDEC_TYPE: {
			// String objStr = obj.toString();
			break;
		}

		case Consts.CALL_NEW_PICTURE: {
			if (null != obj) {
				MyLog.e("NEW_PICTURE", obj.toString());
			}

			Channel channel = null;
			if (arg1 < channelList.size()) {
				channel = channelList.get(arg1);
			}

			if (null == channel) {
				return;
			}
			loadingState(arg1, 0, Consts.TAG_PLAY_CONNECTTED);
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
					// String msg = String
					// .format("%d(%s|%s), kbps=%.0fK, fps=%.0f+%.0f/%.0f/%d, %.0fms+%.0fms, left=%2d,\n",//
					// \t\t%dx%d;
					// // audio(%d):
					// // kbps=%.2fK,
					// // fps=%.0f/%.0f,
					// // %.2fms+%.2fms
					// object.getInt("window"),
					// (object.getBoolean("is_turn") ? "TURN"
					// : "P2P"),
					// (object.getBoolean("is_omx") ? "HD" : "ff"),
					// object.getDouble("kbps"), object
					// .getDouble("decoder_fps"), object
					// .getDouble("jump_fps"), object
					// .getDouble("network_fps"), object
					// .getInt("space"), object
					// .getDouble("decoder_delay"), object
					// .getDouble("render_delay"), object
					// .getInt("left")
					// // ,object.getInt("width"), object
					// // .getInt("height"), object
					// // .getInt("audio_type"), object
					// // .getDouble("audio_kbps"), object
					// // .getDouble("audio_decoder_fps"),
					// // object.getDouble("audio_network_fps"),
					// // object.getDouble("audio_decoder_delay"),
					// // object.getDouble("audio_play_delay")
					// );
					// sBuilder.append(msg).append("\n");

					// [Neo] you fool
					if (ONE_SCREEN == currentScreen) {
						// currentPageChannelList.get(0).setOMX(
						// object.getBoolean("is_omx"));

						// object.getDouble("decoder_fps"),
						// object.getDouble("jump_fps"),
						// object.getDouble("network_fps")
						int window = object.getInt("window");
						// loadingState(arg1, R.string.connecting_buffer1,
						// Consts.TAG_PLAY_CONNECTTED);

						double delay = object.getDouble("delay");
						if (window == lastClickIndex) {
							currentKbps.setText(String.format("%.1fk/%.1fk",
									object.getDouble("kbps"),
									object.getDouble("audio_network_fps")));
							// + "("
							// + (object.getBoolean("is_turn") ? "TURN"
							// : "P2P") + ")");

							Channel channel = null;
							if (arg1 < channelList.size()) {
								channel = channelList.get(arg1);
							}

							if (null == channel) {
								return;
							}

							Device device = channel.getParent();
							boolean enableTcp = device.getEnableTcpConnect() == 1 ? true
									: false;
							MyLog.e(TAG, "启用TCP连接 == " + enableTcp);

							playStatistics
									.setText(String
											.format("%.1fk/%.1fk/D:%.1fk/J:%.1fk/N:%.1fk/L:%dk",
													object.getDouble("kbps"),
													object.getDouble("audio_kbps"),
													object.getDouble("decoder_fps"),
													object.getDouble("jump_fps"),
													object.getDouble("network_fps"),
													object.getInt("left"))

											+ "/"
											+ (object.getBoolean("is_turn") ? "TURN"
													: "P2P")
											+ "/"
											+ "enableTcp=" + enableTcp + "/"
									// + PlayUtil
									// .hasEnableHelper(channelList
									// .get(lastClickIndex)
									// .getParent()
									// .getFullNo())
									);
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
		case Consts.WHAT_SELECT_SCREEN: {
			selectedScreen = screenList.get(arg1);
			screenAdapter.selectIndex = arg1;
			screenAdapter.notifyDataSetChanged();
			disconnectChannelList.addAll(channelList);
			changeWindow(selectedScreen);
			screenPopWindow.dismiss();
			streamListView.setVisibility(View.GONE);
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
		case Consts.WHAT_STREAM_ITEM_CLICK: {// 码流切换

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

				if (channel.getParent().isOldDevice()) {
					// int index = arg1 + 1;
					// String params = "MainStreamQos=" + index + ";";//
					// "MobileStreamQos="
					// MyLog.v(TAG, "changeStream--" + params);
					// Jni.sendString(lastClickIndex,
					// JVNetConst.JVN_RSP_TEXTDATA,
					// false, 0, Consts.TYPE_SET_PARAM, params);

					if (0 == arg1) {
						Jni.sendString(lastClickIndex,
								JVNetConst.JVN_RSP_TEXTDATA, false, 0,
								Consts.TYPE_SET_PARAM, String.format(
										Consts.FORMATTER_SET_BPS_FPS, 1, 1280,
										720, 800, 15, 1));
					} else if (1 == arg1) {
						Jni.sendString(lastClickIndex,
								JVNetConst.JVN_RSP_TEXTDATA, false, 0,
								Consts.TYPE_SET_PARAM, String.format(
										Consts.FORMATTER_SET_BPS_FPS, 1, 720,
										480, 512, 20, 1));
					} else if (2 == arg1) {
						Jni.sendString(lastClickIndex,
								JVNetConst.JVN_RSP_TEXTDATA, false, 0,
								Consts.TYPE_SET_PARAM, String.format(
										Consts.FORMATTER_SET_BPS_FPS, 1, 352,
										288, 512, 25, 1));
					}
				} else {
					if (0 == arg1) {
						Jni.sendString(lastClickIndex,
								JVNetConst.JVN_RSP_TEXTDATA, false, 0,
								Consts.TYPE_SET_PARAM, String.format(
										Consts.FORMATTER_SET_BPS_FPS, 2, 1280,
										720, 1024, 15, 1));
					} else if (1 == arg1) {
						Jni.sendString(lastClickIndex,
								JVNetConst.JVN_RSP_TEXTDATA, false, 0,
								Consts.TYPE_SET_PARAM, String.format(
										Consts.FORMATTER_SET_BPS_FPS, 2, 720,
										480, 512, 20, 1));
					} else if (2 == arg1) {
						Jni.sendString(lastClickIndex,
								JVNetConst.JVN_RSP_TEXTDATA, false, 0,
								Consts.TYPE_SET_PARAM, String.format(
										Consts.FORMATTER_SET_BPS_FPS, 2, 352,
										288, 512, 25, 1));
					}
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
									initSummaryDialog(channelList.get(
											lastClickIndex).getParent());
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
				showingDialog = false;
			}
		});
		dialogCompleted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设备用户名不为空
				showingDialog = false;
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
					CacheUtil.saveDevList(deviceList);
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
				JVPlayActivity.this
						.showTextToast(R.string.login_str_device_edit_success);
				playImageClickEvent(channelList.get(lastClickIndex), true);
			} else {
				JVPlayActivity.this
						.showTextToast(R.string.login_str_device_edit_failed);
			}
			initDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	protected void initSettings() {
		// TODO
		MyLog.enableLogcat(true);

		isQuit = false;
		isOmx = false;
		isBlockUi = false;

		lastItemIndex = 0;
		lastClickIndex = 0;
		isDoubleClickCheck = false;
		isScrollClickCheck = false;

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
				for (Device dev : deviceList) {
					dev.setOldDevice(MySharedPreference.getBoolean(
							dev.getFullNo(), false));
				}
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

			if (MySharedPreference.getBoolean(Consts.MORE_PLAYMODE)) {
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
			isScrollClickCheck = false;
			lastClickIndex = channelList.get(startWindowIndex).getIndex();
			lastItemIndex = lastClickIndex;
			// MyLog.i(Consts.TAG_XXX, "JVPlay.init: startWindowIndex="
			// + startWindowIndex + "," + channelList.size()
			// + ", channel/index = "
			// + channelList.get(startWindowIndex).getChannel() + "/"
			// + channelList.get(startWindowIndex).getIndex());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置标题
	 */
	private void setTitle() {
		if (Consts.PLAY_AP == playFlag) {
			currentMenu.setText(R.string.video_check);
			selectScreenNum.setVisibility(View.GONE);
			currentMenu_v.setText(channelList.get(lastClickIndex)
					.getChannelName());
			currentMenu_h.setText(channelList.get(lastClickIndex)
					.getChannelName());

		} else {
			currentMenu.setText(R.string.str_video_play);
			selectScreenNum.setVisibility(View.VISIBLE);
			currentMenu_h.setText(channelList.get(lastItemIndex).getParent()
					.getNickName());
			currentMenu_v.setText(channelList.get(lastItemIndex).getParent()
					.getNickName()
					+ "-" + channelList.get(lastClickIndex).getChannel());

		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		// TODO
		if (null != playViewPager) {
			MyLog.e("JUYANG--1", "viewPager=" + playViewPager.getChildCount());
		}
		if (null != adapter) {
			MyLog.e("JUYANG--1", "adapter=" + adapter.getCount());
		}
		super.initUi();
		playViewPager.setAdapter(null);

		MyLog.v(Consts.TAG_XXX, "playViewPager is null ");

		if (null != playViewPager) {
			MyLog.e("JUYANG--2", "viewPager=" + playViewPager.getChildCount());
		}
		if (null != adapter) {
			MyLog.e("JUYANG--2", "adapter=" + adapter.getCount());
		}

		// //进播放如果是横屏，先转成竖屏
		// if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// }

		mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
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
			if (Consts.PLAY_NORMAL == playFlag) {
				currentMenu_v.setText(channelList.get(lastClickIndex)
						.getChannelName());
			} else {
				currentMenu_v.setText(deviceList.get(deviceIndex).getNickName()
						+ "-" + channelList.get(lastClickIndex).getChannel());
			}
		}
		/** 上 */
		varvoice_bg.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		left_btn_h.setOnClickListener(myOnClickListener);
		ht_fight.setOnClickListener(myOnClickListener);
		ht_motion.setOnClickListener(myOnClickListener);

		selectScreenNum.setOnClickListener(myOnClickListener);
		currentMenu.setOnClickListener(myOnClickListener);
		// if (playFlag == Consts.PLAY_AP) {
		// currentMenu_h.setText(deviceList.get(deviceIndex).getNickName());
		// currentMenu.setText(R.string.video_check);
		// selectScreenNum.setVisibility(View.GONE);
		// } else {
		// currentMenu_h.setText(channelList.get(lastClickIndex)
		// .getChannelName());
		// currentMenu.setText(R.string.str_video_play);
		// selectScreenNum.setVisibility(View.VISIBLE);
		// }
		setTitle();

		linkMode.setVisibility(View.GONE);

		decodeBtn.setOnClickListener(myOnClickListener);
		videTurnBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);
		right_btn_h.setOnClickListener(myOnClickListener);

		/** 中 */
		playViewPager.setVisibility(View.VISIBLE);
		playSurface.setVisibility(View.GONE);

		if (null != playViewPager) {
			MyLog.e("JUYANG--3", "viewPager=" + playViewPager.getChildCount());
		}
		if (null != adapter) {
			MyLog.e("JUYANG--3", "adapter=" + adapter.getCount());
		}
		adapter = new MyPagerAdapter();
		// if (ONE_SCREEN != currentScreen) {
		// manager.genPageList(ONE_SCREEN);
		// }

		playViewPager.setLongClickable(true);
		playViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageSelected(int arg0) {
						try {
							// saveLastScreen(channelList.get(lastItemIndex));
							closePopWindow();
							varvoice.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.video_monitor_ico));
							stopAllFunc();
							MyLog.i(Consts.TAG_UI, ">>> pageSelected: "
									+ arg0
									+ ", to "
									+ ((arg0 > lastItemIndex) ? "right"
											: "left"));

							currentPageChannelList = manager
									.getValidChannelList(arg0);
							int size = currentPageChannelList.size();

							int target = currentPageChannelList.get(0)
									.getIndex();
							for (int i = 0; i < size; i++) {
								if (lastClickIndex == currentPageChannelList
										.get(i).getIndex()) {
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
								playViewPager.setDisableSliding(isBlockUi);

								handler.removeMessages(Consts.WHAT_CHECK_SURFACE);
								handler.sendMessageDelayed(handler
										.obtainMessage(
												Consts.WHAT_CHECK_SURFACE,
												arg0, lastClickIndex),
										DELAY_CHECK_SURFACE);
								handler.sendEmptyMessage(Consts.WHAT_SHOW_PROGRESS);
							}

							lastItemIndex = arg0;

							// if (Consts.PLAY_NORMAL == playFlag) {
							// currentMenu_v.setText(channelList.get(lastClickIndex)
							// .getChannelName());
							// currentMenu_h.setText(channelList.get(lastClickIndex)
							// .getChannelName());
							// } else {
							// currentMenu_h.setText(channelList.get(lastItemIndex)
							// .getParent().getNickName());
							// currentMenu_v.setText(channelList.get(lastItemIndex)
							// .getParent().getNickName()
							// + "-"
							// + channelList.get(lastClickIndex).getChannel());
							// }
							setTitle();

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
						// saveLastScreen(channelList.get(lastItemIndex));
					}
				});

		playViewPager.setCurrentItem(lastItemIndex);
		playFunctionList.setOnItemClickListener(onItemClickListener);
		talk_cancel.setOnClickListener(myOnClickListener);
		talk_img.setOnTouchListener(myOnTouchListener);

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
		if (android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 > Build.VERSION.SDK_INT) {// 小于4.1的系统，不允许硬解
			lowerSystem = true;
		}

		voiceCall.setOnClickListener(myOnClickListener);
		// voiceCall.setOnTouchListener(callOnTouchListener);
		// voiceCall.setOnLongClickListener(callOnLongClickListener);

		bottombut5.setOnClickListener(myOnClickListener);
		horfunc_talk_normal.setOnTouchListener(myOnTouchListener);
		verPlayBarLayout.setVisibility(View.VISIBLE);

		changeWindow(currentScreen);
	}

	private class MyPagerAdapter extends PagerAdapter {

		private ArrayList<View> list;

		public MyPagerAdapter() {
			this.list = new ArrayList<View>();
		}

		public void update(ArrayList<View> list) {
			if (null != list) {
				MyLog.v("JUYANG--4", "this.size=" + this.list.size() + ";size="
						+ list.size());
				this.list.clear();
				this.list.addAll(list);
			}
			notifyDataSetChanged();
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
					channel.setPaused(false);
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_PLAY_STATUS, channel.getIndex(),
							Consts.ARG2_STATUS_CONNECTED));
					// MyLog.v(TAG,"resumeChannel+buffering,index="+channel.getIndex());
				} else {
					// handler.sendMessage(handler.obtainMessage(WHAT_PLAY_STATUS,
					// channel.getIndex(), ARG2_STATUS_HAS_CONNECTED));
				}

			} else {
				handler.sendMessage(handler.obtainMessage(
						Consts.WHAT_PLAY_STATUS, channel.getIndex(),
						Consts.ARG2_STATUS_DISCONNECTED));
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
		if (null != channel) {
			if (lastClickIndex != channel.getIndex() && channel.isConnected()) {
				Jni.sendBytes(channel.getIndex(),
						JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0], 8);
			}

			result = Jni.pause(channel.getIndex());
			channel.setPaused(result);
		}
		MyLog.e("JNI_PLAY", "pauseIndex=" + channel.getIndex() + ";result="
				+ result);
		return result;
	}

	private void changeWindow(int count) {
		stopAllFunc();
		currentScreen = count;
		playViewPager.setAdapter(null);
		MyLog.v(Consts.TAG_XXX, "playViewPager is null 2");
		adapter.update(manager.genPageList(count));
		// adapter.notifyDataSetChanged();
		adapter.getCount();
		lastItemIndex = lastClickIndex / currentScreen;
		currentPageChannelList = manager.getValidChannelList(lastItemIndex);

		playViewPager.setAdapter(adapter);
		playViewPager.setCurrentItem(lastItemIndex, false);

		isBlockUi = true;
		playViewPager.setDisableSliding(isBlockUi);
		changeBorder(lastClickIndex);

		setTitle();

		handler.removeMessages(Consts.WHAT_CHECK_SURFACE);
		handler.sendMessageDelayed(handler.obtainMessage(
				Consts.WHAT_CHECK_SURFACE, lastItemIndex, lastClickIndex),
				DELAY_CHECK_SURFACE);
		handler.sendEmptyMessage(Consts.WHAT_SHOW_PROGRESS);

		if (currentScreen > ONE_SCREEN) {
			closePopWindow();
		}
		// dismissDialog();

		// if (count > ONE_SCREEN) {
		// Jni.setStat(false);
		// } else {
		// Jni.setStat(true);
		// }
	}

	private void changeBorder(int currentIndex) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 如果是域名添加的设备需要先去解析IP
	 * 
	 * @param device
	 */
	private void resolveIp(final Channel channel, final boolean isDirectly) {
		Thread resolveThread = new Thread() {
			@Override
			public void run() {
				super.run();
				channel.getParent().setIp(
						ConfigUtil
								.getIpAddress(channel.getParent().getDoMain()));
				int arg1 = isDirectly ? 1 : 0;
				handler.sendMessage(handler.obtainMessage(
						Consts.WHAT_RESOLVE_IP_CONNECT, arg1, 0, channel));
			}

		};
		resolveThread.start();
	}

	/**
	 * 视频连接
	 * 
	 * @param channel
	 * @param isPlayDirectly
	 * @return
	 */
	private boolean connect(Channel channel, boolean isPlayDirectly) {
		String fullPath = "";
		if (Consts.PLAY_AP != playFlag && hasSDCard(5) && null != channel) {
			String savePath = "";
			if (Consts.PLAY_NORMAL == playFlag) {
				if (2 == channel.getParent().getIsDevice()) {
					savePath = Consts.SCENE_PATH
							+ channel.getParent().getDoMain() + File.separator;
				} else {
					savePath = Consts.SCENE_PATH
							+ channel.getParent().getFullNo() + File.separator;
				}

			} else if (Consts.PLAY_DEMO == playFlag) {
				savePath = Consts.SCENE_PATH + "demo_"
						+ channel.getParent().getFullNo() + File.separator;
			}

			MyLog.v("capture", "savePath=" + savePath);
			MobileUtil.createDirectory(new File(savePath));
			String fileName = channel.getChannel() + Consts.IMAGE_JPG_KIND;
			fullPath = savePath + fileName;
			MyLog.v("capture", "fullPath=" + fullPath);
		}

		channel.getParent().setOldDevice(
				MySharedPreference.getBoolean(channel.getParent().getFullNo(),
						false));

		boolean result = false;

		if (null != channel && false == channel.isConnected()
				&& false == channel.isConnecting()) {
			int connect = 0;
			if (1 == channel.getVipLevel()) {
				// showTextToast("vip==1流媒体连接");
				MyLog.e(TAG, "vip == 1 ,流媒体");
				connect = Jni.connectRTMP(channel.getIndex(),
						channel.getRtmpUrl(), channel.getSurface(), false,
						fullPath);

				// .connect(
				// channel.getIndex(),
				// channel.getChannel(),
				// conIp,
				// conPort,
				// device.getUser(),
				// device.getPwd(),
				// number,
				// device.getGid(),
				// true,
				// 1,
				// true,
				// channel.getParent().isOldDevice() ?
				// JVNetConst.TYPE_3GMOHOME_UDP
				// : JVNetConst.TYPE_3GMO_UDP,// (device.isHomeProduct()
				// // ? 6 : 5),
				// channel.getSurface(), false, isOmx,
				// fullPath);
			}
			// else if (2 == channel.getVipLevel()) {
			// // 走全转发 by lkp
			// // showTextToast("vip==2全转发");
			// MyLog.e(TAG, "vip == 2,全转发");
			// Device device = channel.getParent();
			// int number = device.getNo();
			// String conIp = device.getIp();
			// int conPort = device.getPort();
			// if (Consts.PLAY_AP == playFlag) {
			// conIp = Consts.IPC_DEFAULT_IP;
			// conPort = Consts.IPC_DEFAULT_PORT;
			// } else {
			// conIp = device.getIp();
			// conPort = device.getPort();
			// }
			// // 有ip通过ip连接
			// if (false == ("".equalsIgnoreCase(device.getIp()) || 0 == device
			// .getPort())) {
			// if (ConfigUtil.is3G(JVPlayActivity.this, false)
			// && 0 == device.getIsDevice()) {// 普通设备3G情况不用ip连接
			// conIp = "";
			// conPort = 0;
			// } else {// 有ip非3G
			// number = -1;
			// }
			// }
			//
			// if (isPlayDirectly) {
			// connect = Jni.connect(channel.getIndex(),
			// channel.getChannel(), conIp, conPort,
			// device.getUser(), device.getPwd(), number,
			// device.getGid(), true, 1, true,
			// JVNetConst.JVN_ONLYTURN,// (device.isHomeProduct()
			// // ? 6 : 5),
			// channel.getSurface(), false, isOmx, fullPath);
			// if (connect == channel.getIndex()) {
			// channel.setPaused(null == channel.getSurface());
			// }
			// } else {
			// connect = Jni.connect(channel.getIndex(),
			// channel.getChannel(), conIp, conPort,
			// device.getUser(), device.getPwd(), number,
			// device.getGid(), true, 1, true,
			// JVNetConst.JVN_ONLYTURN,// (device.isHomeProduct()
			// // ? 6 : 5),
			// null, false, isOmx, fullPath);
			// if (connect == channel.getIndex()) {
			// channel.setPaused(true);
			// }
			// }
			// }
			else {
				// showTextToast("v不是vip");
				MyLog.e(TAG, "vip == 0,不是vip");
				Device device = channel.getParent();

				boolean enableTcp = device.getEnableTcpConnect() == 1 ? true
						: false;
				MyLog.e(TAG, "启用TCP连接 == " + enableTcp);

				if (null != ssid
						&& channel.getParent().getFullNo()
								.equalsIgnoreCase(ssid)) {
					MyLog.v(TAG, device.getNo() + "--AP--直连接：" + device.getIp());
					connect = Jni
							.connect(
									channel.getIndex(),
									channel.getChannel(),
									Consts.IPC_DEFAULT_IP,
									Consts.IPC_DEFAULT_PORT,
									device.getUser(),
									device.getPwd(),
									-1,
									device.getGid(),
									true,
									1,
									true,
									channel.getParent().isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
											: JVNetConst.TYPE_3GMO_UDP, channel
											.getSurface(), false, enableTcp,
									isOmx, fullPath);
					if (connect == channel.getIndex()) {
						channel.setPaused(null == channel.getSurface());
					}
					int connectWay = channel.getParent().isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
							: JVNetConst.TYPE_3GMO_UDP;
					MyLog.e(TAG, "连接方式 == " + connectWay);
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
						connect = Jni
								.connect(
										channel.getIndex(),
										channel.getChannel(),
										conIp,
										conPort,
										device.getUser(),
										device.getPwd(),
										number,
										device.getGid(),
										true,
										1,
										true,
										channel.getParent().isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
												: JVNetConst.TYPE_3GMO_UDP,// (device.isHomeProduct()
										// ? 6 : 5),
										channel.getSurface(), false, enableTcp,
										isOmx, fullPath);

						int connectWay = channel.getParent().isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
								: JVNetConst.TYPE_3GMO_UDP;
						MyLog.e(TAG, "连接方式 == " + connectWay);
						if (connect == channel.getIndex()) {
							channel.setPaused(null == channel.getSurface());
						}
					} else {
						connect = Jni
								.connect(
										channel.getIndex(),
										channel.getChannel(),
										conIp,
										conPort,
										device.getUser(),
										device.getPwd(),
										number,
										device.getGid(),
										true,
										1,
										true,
										channel.getParent().isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
												: JVNetConst.TYPE_3GMO_UDP,// (device.isHomeProduct()
										// ? 6 : 5),
										null, false, enableTcp, isOmx, fullPath);

						int connectWay = channel.getParent().isOldDevice() ? JVNetConst.TYPE_3GMOHOME_UDP
								: JVNetConst.TYPE_3GMO_UDP;
						MyLog.e(TAG, "连接方式 == " + connectWay);
						if (connect == channel.getIndex()) {
							channel.setPaused(true);
						}
					}

				}

				if (Consts.BAD_HAS_CONNECTED == connect) {
					MyLog.e(Consts.TAG_XXX, "BAD_HAS_CONNECTED");
					channel.setConnected(true);
					channel.setPaused(true);
					channel.setConnecting(false);
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_PLAY_STATUS, channel.getIndex(),
							Consts.ARG2_STATUS_HAS_CONNECTED));

					if (1 == channel.getVipLevel()) {
						Jni.shutdownRTMP(channel.getIndex());
					} else {
						Jni.disconnect(channel.getIndex());
					}

				} else if (Consts.BAD_CONN_OVERFLOW == connect) {
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_PLAY_STATUS, channel.getIndex(),
							Consts.ARG2_STATUS_CONN_OVERFLOW));
				} else {
					channel.setConnecting(true);
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_PLAY_STATUS, channel.getIndex(),
							Consts.ARG2_STATUS_CONNECTING));
					result = true;
				}
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
		playImageClickEvent(channel, isFromImageView);
	}

	/**
	 * 播放按钮事件
	 */
	private void playImageClickEvent(Channel channel, boolean isFromImageView) {
		MyLog.i(Consts.TAG_PLAY, ">>> click: " + channel.getIndex()
				+ ", isBlocked: " + isBlockUi);

		if (isBlockUi) {
			showTextToast(R.string.waiting);
		}

		if (false == isBlockUi && isFromImageView) {// 播放按钮事件
			if (false == channel.isConnected()
					&& false == channel.isConnecting()) {

				if (2 == channel.getParent().getIsDevice()) {// 域名设备先解析IP
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_PLAY_STATUS, channel.getIndex(),
							Consts.ARG2_STATUS_CONNECTING));
					resolveIp(channel, true);
				} else {
					if (false == connect(channel, true)) {
						MyLog.e(Consts.TAG_PLAY, "connect failed: " + channel);
					}
				}

			} else if (channel.isConnecting()) {
				handler.sendMessage(handler.obtainMessage(
						Consts.WHAT_PLAY_STATUS, channel.getIndex(),
						Consts.ARG2_STATUS_CONNECTING));
			} else if (false == channel.isPaused()) {
				handler.sendMessage(handler.obtainMessage(
						Consts.WHAT_PLAY_STATUS, channel.getIndex(),
						Consts.ARG2_STATUS_BUFFERING));
				// MyLog.v(TAG,"connect+buffering,index="+channel.getIndex());
			} else {
				if (false == resumeChannel(channel)) {
					showTextToast("resume failed:isConnecting="
							+ channel.isConnecting() + ";isConnected="
							+ channel.isConnected() + ";isPaused="
							+ channel.isPaused() + ";" + channel.toString());
					channel.setConnected(false);
					MyLog.e(Consts.TAG_PLAY, "resume failed: " + channel);
				}

			}
		}

		if (false == isBlockUi && isDoubleClickCheck
				&& lastClickIndex == channel.getIndex()) {// 双击

			if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation
					|| Consts.PLAY_AP == playFlag) {// 横屏
			} else {// 竖屏
				if (ONE_SCREEN != currentScreen) {
					Channel ch;
					int size = currentPageChannelList.size();
					for (int i = 0; i < size; i++) {
						ch = currentPageChannelList.get(i);
						if (lastClickIndex - 1 > ch.getIndex()
								|| lastClickIndex + 1 < ch.getIndex()) {
							disconnectChannelList.add(ch);
						} else if (lastClickIndex == ch.getIndex()) {
							// [Neo] Empty
						} else {
							// [Neo] stand alone for single destroy
							// window, too
							pauseChannel(ch);
						}
					}
					changeWindow(ONE_SCREEN);
				} else {
					disconnectChannelList.addAll(channelList);
					changeWindow(selectedScreen);
				}
			}
		} else {// 单击
			if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
			} else {// 竖屏
				closePopWindow();
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
		try {
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
				MyLog.w(Consts.TAG_XXX, "> surface created: " + index);
				if (ONE_SCREEN == currentScreen && false == isFromCurrent
						&& false == channel.isConnected()) {
					connectChannelList.add(channel);
				}
				channel.setSurface(surface);
				// handler.sendMessage(handler.obtainMessage(
				// Consts.WHAT_PLAY_STATUS, channel.getIndex(),
				// Consts.ARG2_STATUS_UNKNOWN));
				break;

			case PlayWindowManager.STATUS_CHANGED:
				MyLog.w(Consts.TAG_XXX, "> surface changed: " + index);
				tensileView(channel, channel.getSurfaceView());
				Jni.resume(index, surface);
				break;

			case PlayWindowManager.STATUS_DESTROYED:
				MyLog.w(Consts.TAG_XXX, "> surface destroyed: " + index);
				pauseChannel(channel);
				channel.setSurface(null);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
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

			case R.id.devicepwd_nameet_cancle:

				devicepwd_nameet.setText("");

				break;
			case R.id.devicepwd_passwrodet_cancle:

				devicepwd_passwordet.setText("");

				break;
			case R.id.dialogpwd_cancle_img:
				showingDialog = false;
				initDialog.dismiss();
				break;
			case R.id.nextstep: {// AP下一步
				backMethod(false);
				break;
			}
			case R.id.btn_left: {// 左边按钮
				closePopWindow();
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
				closePopWindow();
				if (allowThisFuc(false)) {
					if (channel.getParent().is05()) {
						createDialog("", true);
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
				closePopWindow();
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
				closePopWindow();
				if (allowThisFuc(false)) {
					try {
						createDialog("", true);
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

				if (Consts.PLAY_AP == playFlag) {
					break;
				}

				if (null != streamListView
						&& View.VISIBLE == streamListView.getVisibility()) {
					streamListView.setVisibility(View.GONE);
				}

				if (isBlockUi) {
					createDialog("", true);
				} else {
					if (screenPopWindow == null) {
						if (null != screenList && 0 != screenList.size()) {

							screenAdapter = new ScreenAdapter(
									JVPlayActivity.this, screenList);
							screenListView = new ListView(JVPlayActivity.this);
							screenListView.setDivider(null);
							if (disMetrics.widthPixels < 1080) {
								screenPopWindow = new PopupWindow(
										screenListView, 240,
										LinearLayout.LayoutParams.WRAP_CONTENT);
							} else {
								screenPopWindow = new PopupWindow(
										screenListView, 400,
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
							screenPopWindow.showAsDropDown(currentMenu);
						}
					} else if (screenPopWindow.isShowing()) {
						screenAdapter.notifyDataSetChanged();
						screenPopWindow.dismiss();
					} else if (!screenPopWindow.isShowing()) {
						screenAdapter.notifyDataSetChanged();
						screenPopWindow.showAsDropDown(currentMenu);
					}
				}
				handler.sendMessageDelayed(
						handler.obtainMessage(Consts.WHAT_DIALOG_CLOSE),
						3 * 1000);
				break;
			case R.id.bottom_but8:
			case R.id.varvoice_bg:
			case R.id.audio_monitor:// 音频监听
				closePopWindow();
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
				closePopWindow();
				if (allowThisFuc(true)) {
					startRemote();
				}

				break;
			case R.id.bottom_but3:
			case R.id.capture:// 抓拍
				// Jni.sendSuperBytes(lastClickIndex,
				// JVNetConst.JVN_RSP_TEXTDATA,
				// true, Consts.RC_EX_FIRMUP, Consts.EX_FIRMUP_RESTORE,
				// Consts.FIRMUP_HTTP, 0, 0, new byte[0], 0);
				closePopWindow();
				if (Consts.ISHITVIS == 1) {
					PlayUtil.hitviscapture(lastClickIndex);
					Jni.sendString(lastClickIndex, JVNetConst.JVN_RSP_TEXTDATA,
							true, JVNetConst.RC_EX_FlashJpeg,
							JVNetConst.RC_EXTEND, null);
				} else {
					if (hasSDCard(5) && allowThisFuc(false)) {
						boolean captureRes = PlayUtil.capture(lastClickIndex);
						if (captureRes) {
							PlayUtil.prepareAndPlay(mediaPlayer, true);
							showTextToast(Consts.CAPTURE_PATH);
							MyLog.e("capture", "success");
						} else {
							showTextToast(R.string.str_capture_error);
						}
					}

				}
				break;
			case R.id.bottom_but5:
				voiceCall(channel);
				if (ishonfunctalk) {
					voiceCallSelected(false);
					horfunc_talk.setVisibility(View.GONE);
					ishonfunctalk = false;
					talkMothed();
				}
				break;
			case R.id.funclayout:// AP功能列表对讲功能
			case R.id.voicecall:// 语音对讲
				voiceCall(channel);
				if (istalk) {
					istalk = false;
					talkMothed();
					voiceCallSelected(false);
					function.setVisibility(View.VISIBLE);
					talk_eachother.setVisibility(View.GONE);
				}
				break;
			case R.id.talk_cancel:
				voiceCall(channel);
				talkMothed();
				voiceCallSelected(false);
				function.setVisibility(View.VISIBLE);
				talk_eachother.setVisibility(View.GONE);
				istalk = false;
				if (Consts.PLAY_AP == playFlag) {
					horfunc_talk.setVisibility(View.GONE);
					ishonfunctalk = false;
				}
				break;
			case R.id.bottom_but7:
			case R.id.videotape:// 录像
				closePopWindow();
				if (hasSDCard(5) && allowThisFuc(true)) {
					if (channelList.get(lastClickIndex).getParent().is05()) {
						String path = PlayUtil.createRecordFile();
						if (PlayUtil.checkRecord(lastClickIndex)) {
							stopRecord(false);
						} else {
							startRecord(lastClickIndex, path);
						}
					} else {
						showTextToast(R.string.not_support_this_func);
					}
				}

				break;
			case R.id.video_bq:
			case R.id.more_features:// 码流
				if (null != screenPopWindow && screenPopWindow.isShowing()) {
					screenPopWindow.dismiss();
				}
				int rows = 3;
				if (channelList.get(lastClickIndex).isNewIpcFlag()
						|| channelList.get(lastClickIndex).getParent()
								.isOldDevice()) {
					streamListView.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.stream_selector_bg3));
					rows = 3;
				} else {
					streamListView.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.stream_selector_bg2));
					rows = 2;

				}

				if (View.VISIBLE == streamListView.getVisibility()) {
					closePopWindow();
				} else {
					if (allowThisFuc(true)) {
						if (-1 == channelList.get(lastClickIndex)
								.getStreamTag()) {
							showTextToast(R.string.not_support_this_func);
						} else {
							streamAdapter.setChangeCounts(rows);
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

	@SuppressWarnings("deprecation")
	private void voiceCall(Channel channel) {
		closePopWindow();
		if (allowThisFuc(true)) {
			// 停止音频监听
			if (PlayUtil.isPlayAudio(lastClickIndex)) {
				stopAudio(lastClickIndex);
				functionListAdapter.selectIndex = -1;
				bottombut8.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.video_monitor_ico));
				varvoice.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.video_monitor_ico));
			}

			if (Consts.JAE_ENCODER_G729 != channel.getAudioEncType()
					&& 8 == channel.getAudioByte()) {
				showTextToast(R.string.not_support_this_func);
				return;
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
					createDialog("", true);
					startVoiceCall(lastClickIndex,
							channelList.get(lastClickIndex));
					if (Consts.PLAY_AP == playFlag) {
						functionListAdapter.selectIndex = 1;
					}
				}
			}

		}
		functionListAdapter.notifyDataSetChanged();
	}

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
			createDialog("", false);
			// 返回超时，重新点击返回
			// backTimer = new Timer();
			// backTimer.schedule(new BackTask(), 15 * 1000);
			handler.sendEmptyMessageDelayed(Consts.WHAT_FINISH, 15 * 1000);

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

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if(keyCode == KeyEvent.KEYCODE_MENU){
	// if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
	// 横屏
	// if (View.VISIBLE == horPlayBarLayout.getVisibility()) {
	// horPlayBarLayout.setVisibility(View.GONE);
	// } else {
	// horPlayBarLayout.setVisibility(View.VISIBLE);
	// }
	// } else {
	// if (ONE_SCREEN == currentScreen) {
	// if (View.VISIBLE == verPlayBarLayout.getVisibility()) {
	// verPlayBarLayout.setVisibility(View.GONE);
	// } else {
	// verPlayBarLayout.setVisibility(View.VISIBLE);
	// }
	// }
	// }
	// return false;
	// }else if(keyCode == KeyEvent.KEYCODE_BACK){
	// backMethod(true);
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	boolean backFunc = false;

	// 设置三种类型参数分别为String,Integer,String
	class DisconnetTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int sendRes = 0;// 0成功 1失败

			backFunc = Boolean.valueOf(params[0]);
			// if (null != channelList && 0 != channelList.size()) {
			// try {
			// int size = channelList.size();
			// for (int i = 0; i < size; i++) {
			// if (channelList.get(i).isConnected()
			// && !channelList.get(i).isPaused()) {
			// saveLastScreen(channelList.get(i));
			// }
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }

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

				int counts = 0;
				while (!allDis(channelList)) {
					counts++;
					if (counts > 15) {
						break;
					}
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
			dismissDialog();
			if (0 == result) {
				if (Consts.PLAY_AP == playFlag) {
					Intent aintent = new Intent();
					if (backFunc) {
						aintent.putExtra("AP_Back", true);
					} else {// next
						aintent.putExtra("AP_Back", false);
					}
					setResult(Consts.WHAT_AP_CONNECT_FINISHED, aintent);
					dismissDialog();
					JVPlayActivity.this.finish();
				} else {
					dismissDialog();
					JVPlayActivity.this.finish();
				}
				handler.removeMessages(Consts.WHAT_FINISH);
				isQuit = true;
			}

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("", true);
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
						Jni.disconnect(channleList.get(i).getIndex());
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
			varvoice.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_monitor_ico));
			functionListAdapter.notifyDataSetChanged();
		}

		// 正在录像停止录像
		if (PlayUtil.checkRecord(lastClickIndex)) {
			stopRecord(false);
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
		// adapter.notifyDataSetChanged();
		manager.destroy();
		PlayUtil.disConnectAll(manager.getChannelList());
		BitmapCache.getInstance().clearCache();
		super.freeMe();
	}

	private void gestureOnView(View v, Channel channel, int gesture,
			int distance, Point vector, Point middle) {
		int viewWidth = v.getWidth();
		int viewHeight = v.getHeight();

		int left = channel.getLastPortLeft();
		int bottom = channel.getLastPortBottom();
		int width = channel.getLastPortWidth();
		int height = channel.getLastPortHeight();

		boolean needRedraw = false;

		switch (gesture) {
		case MyGestureDispatcher.GESTURE_TO_LEFT:
		case MyGestureDispatcher.GESTURE_TO_UP:
		case MyGestureDispatcher.GESTURE_TO_RIGHT:
		case MyGestureDispatcher.GESTURE_TO_DOWN:
			left += vector.x;
			bottom += vector.y;
			needRedraw = true;
			break;

		case MyGestureDispatcher.GESTURE_TO_BIGGER:
		case MyGestureDispatcher.GESTURE_TO_SMALLER:
			if (width > viewWidth || distance > 0) {
				float xFactor = (float) vector.x / viewWidth;
				float yFactor = (float) vector.y / viewHeight;
				float factor = yFactor;

				if (distance > 0) {
					if (xFactor > yFactor) {
						factor = xFactor;
					}
				} else {
					if (xFactor < yFactor) {
						factor = xFactor;
					}
				}

				int xMiddle = middle.x - left;
				int yMiddle = viewHeight - middle.y - bottom;

				factor += 1;
				left = middle.x - (int) (xMiddle * factor);
				bottom = (viewHeight - middle.y) - (int) (yMiddle * factor);
				width = (int) (width * factor);
				height = (int) (height * factor);

				if (width <= viewWidth || height < viewHeight) {
					left = 0;
					bottom = 0;
					width = viewWidth;
					height = viewHeight;
				} else if (width > 4000 || height > 4000) {
					width = channel.getLastPortWidth();
					height = channel.getLastPortHeight();

					if (width > height) {
						factor = 4000.0f / width;
						width = 4000;
						height = (int) (height * factor);
					} else {
						factor = 4000.0f / height;
						width = (int) (width * factor);
						height = 4000;
					}

					left = middle.x - (int) (xMiddle * factor);
					bottom = (viewHeight - middle.y) - (int) (yMiddle * factor);
				}

				needRedraw = true;
			}
			break;

		default:
			break;
		}

		if (needRedraw) {
			if (left + width < viewWidth) {
				left = viewWidth - width;
			} else if (left > 0) {
				left = 0;
			}

			if (bottom + height < viewHeight) {
				bottom = viewHeight - height;
			} else if (bottom > 0) {
				bottom = 0;
			}

			channel.setLastPortLeft(left);
			channel.setLastPortBottom(bottom);
			channel.setLastPortWidth(width);
			channel.setLastPortHeight(height);
			Jni.setViewPort(channel.getIndex(), left, bottom, width, height);
		}
	}

	private void tensileView(Channel channel, View view) {
		channel.setLastPortLeft(0);
		channel.setLastPortBottom(0);
		channel.setLastPortWidth(view.getWidth());
		channel.setLastPortHeight(view.getHeight());
		Jni.setViewPort(channel.getIndex(), 0, 0, view.getWidth(),
				view.getHeight());
	}

	@Override
	public void onGesture(int index, int gesture, int distance, Point vector,
			Point middle) {

		Channel channel = channelList.get(lastClickIndex);
		if (null != channel && channel.isConnected() && !channel.isConnecting()) {
			boolean originSize = false;
			if (channel.getLastPortWidth() == channel.getSurfaceView()
					.getWidth()) {
				originSize = true;
			}
			int c = 0;
			switch (gesture) {
			// 手势放大缩小
			case MyGestureDispatcher.GESTURE_TO_BIGGER:
			case MyGestureDispatcher.GESTURE_TO_SMALLER:
				if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
					isScrollClickCheck = true;
					gestureOnView(manager.getView(index),
							channelList.get(index), gesture, distance, vector,
							middle);
				}
				lastClickTime = 0;
				break;
			// 手势云台
			case MyGestureDispatcher.GESTURE_TO_LEFT:
				if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
					System.out.println("gesture: left");
					isScrollClickCheck = true;
					if (originSize) {
						c = JVNetConst.JVN_YTCTRL_R;
						sendCmd(c);
					} else {
						gestureOnView(manager.getView(index),
								channelList.get(index), gesture, distance,
								vector, middle);
					}
				}
				lastClickTime = 0;
				break;

			case MyGestureDispatcher.GESTURE_TO_UP:
				if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
					System.out.println("gesture: up");
					isScrollClickCheck = true;
					if (originSize) {
						c = JVNetConst.JVN_YTCTRL_D;
						sendCmd(c);
					} else {
						gestureOnView(manager.getView(index),
								channelList.get(index), gesture, distance,
								vector, middle);
					}
				}
				lastClickTime = 0;
				break;

			case MyGestureDispatcher.GESTURE_TO_RIGHT:
				if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
					System.out.println("gesture: right");
					isScrollClickCheck = true;
					if (originSize) {
						c = JVNetConst.JVN_YTCTRL_L;
						sendCmd(c);
					} else {
						gestureOnView(manager.getView(index),
								channelList.get(index), gesture, distance,
								vector, middle);
					}
				}
				lastClickTime = 0;
				break;

			case MyGestureDispatcher.GESTURE_TO_DOWN:
				if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
					System.out.println("gesture: down");
					isScrollClickCheck = true;
					if (originSize) {
						c = JVNetConst.JVN_YTCTRL_U;
						sendCmd(c);
					} else {
						gestureOnView(manager.getView(index),
								channelList.get(index), gesture, distance,
								vector, middle);
					}
				}
				lastClickTime = 0;
				break;
			// 手势单击双击
			case MyGestureDispatcher.CLICK_EVENT:
				if (0 == lastClickTime) {
					isDoubleClickCheck = false;
					lastClickTime = System.currentTimeMillis();
					handler.sendMessageDelayed(handler.obtainMessage(
							Consts.WHAT_SURFACEVIEW_CLICK, middle.x, middle.y,
							channel), 350);
					MyLog.e("Click1--", "单击：lastClickTime=" + lastClickTime);
				} else {
					int clickTimeBetween = (int) (System.currentTimeMillis() - lastClickTime);
					MyLog.e("Click1--", "双击：clickTimeBetween="
							+ clickTimeBetween);
					if (clickTimeBetween < 350) {// 认为双击
						isDoubleClickCheck = true;
					}
					lastClickTime = 0;
				}
				break;
			default:
				break;

			}
		}

	}

	// 第一次click时间
	private long lastClickTime = 0;

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
			Channel ch;
			int size = currentPageChannelList.size();
			for (int i = 0; i < size; i++) {
				ch = currentPageChannelList.get(i);
				if (lastClickIndex - 1 > ch.getIndex()
						|| lastClickIndex + 1 < ch.getIndex()) {
					disconnectChannelList.add(ch);
				} else if (lastClickIndex == ch.getIndex()) {
					// [Neo] Empty
				} else {
					// [Neo] stand alone for single destroy window, too
					pauseChannel(ch);
				}
			}
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
		stopAllFunc();

		boolean supportDownload = false;// 支持下载
		if (null == mobileQuality) {// 没这个字段说明是老设备，再判断MobileCH是否为2
			if (mobileCH != null) {// 这种情况，直接不让进设备设置界面
				if (mobileCH.equals("2")) {
					supportDownload = true;
				}
			}
		} else {
			supportDownload = true;
		}

		Intent remoteIntent = new Intent();
		remoteIntent.setClass(JVPlayActivity.this, JVRemoteListActivity.class);
		// remoteIntent.putExtra("supportDownload", supportDownload);

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
							try {
								Uri uri = Uri
										.parse("http://down.jovision.com:81/cn/data/CloudSEE2.8.5.apk");
								Intent it = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(it);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					});

			builder.setPositiveButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							showingDialog = false;
							handler.sendMessageDelayed(handler
									.obtainMessage(Consts.WHAT_START_CONNECT),
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
			case Consts.TAG_PLAY_CONNECTING: {// 连接中
				if (MySharedPreference.getBoolean("playhelp1")) {
					verPlayBarLayout.setVisibility(View.GONE);
				}
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
			case Consts.TAG_PLAY_CONNECTTED: {// 已连接
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_PROGRESS, proWidth, View.GONE);// loading
				manager.setViewVisibility(container,
						PlayWindowManager.ID_INFO_TEXT, View.GONE);// 连接文字
				manager.setViewVisibility(container,
						PlayWindowManager.ID_CONTROL_CENTER, View.GONE);// 播放按钮
				break;
			}
			case Consts.TAG_PLAY_DIS_CONNECTTED: {// 断开
				channelList.get(index).setConnected(false);
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
			case Consts.TAG_PLAY_CONNECTING_BUFFER: {// 缓冲中
				// verPlayBarLayout.setVisibility(View.GONE);
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
			case Consts.TAG_PLAY_STATUS_UNKNOWN: {// 未知状态，只显示播放按钮
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

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			closePopWindow();
			if (2 == arg2) {
				if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
					showTextToast(R.string.str_devicemanages);
				} else {
					// 设备设置
					if (allowThisFuc(true)) {
						if (null == mobileQuality) {// 没这个字段说明是老设备，再判断MobileCH是否为2
							if (mobileCH != null) {// 这种情况，直接不让进设备设置界面
								if (!(mobileCH.equals("2"))) {
									showTextToast(R.string.not_support_this_func);
									return;
								}
							} else {
								showTextToast(R.string.not_support_this_func);
								return;
							}
						}

						resetFunc(channelList.get(lastClickIndex));
						Intent intent = new Intent(JVPlayActivity.this,
								DeviceSettingsActivity.class);
						intent.putExtra("isadmin",
								channelList.get(lastClickIndex).getParent()
										.isAdmin());
						intent.putExtra("power", channelList
								.get(lastClickIndex).getParent().getPower());
						intent.putExtra("window", lastClickIndex);
						intent.putExtra("descript",
								channelList.get(lastClickIndex).getParent()
										.getDescript());
						intent.putExtra("deviceIndex", deviceIndex);
						intent.putExtra("fullno", deviceList.get(deviceIndex)
								.getFullNo());
						intent.putExtra("updateflag", updateStreaminfoFlag);
						intent.putExtra("streamMap", streamMap);
						startActivityForResult(intent,
								Consts.PLAY_DEVSET_REQUSET);
					}
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
					view.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							String actionName = getActionName(event.getAction());
							if (actionName == "ACTION_DOWN" && !VOICECALLING) {
								return false;
							}
							MyLog.i(getClass().getName(), "onTouch-----"
									+ actionName);
							mGestureDetector.onTouchEvent(event);
							// 一定要返回true，不然获取不到完整的事件
							return true;

						}
					});
					Channel channel = channelList.get(lastClickIndex);
					voiceCall(channel);
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

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent arg2) {
		super.onActivityResult(requestCode, responseCode, arg2);
		if (Consts.PLAY_DEVSET_REQUSET == requestCode
				&& Consts.PLAY_DEVSET_RESPONSE == responseCode) {
			this.finish();
		}
	}

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
		MyLog.v("onResume--ChannelList", channelList.toString());
		super.onResume();
		MyLog.v(Consts.TAG_XXX,
				"onResume viewpager:width=" + playViewPager.getWidth()
						+ "viewpager:height=" + playViewPager.getHeight());
		isBlockUi = true;
		updateStreaminfoFlag = false;
		handler.removeMessages(Consts.WHAT_CHECK_SURFACE);
		handler.sendMessageDelayed(handler.obtainMessage(
				Consts.WHAT_CHECK_SURFACE, lastItemIndex, lastClickIndex),
				DELAY_CHECK_SURFACE);
		handler.sendEmptyMessage(Consts.WHAT_SHOW_PROGRESS);
	}

	@Override
	protected void onPause() {
		super.onPause();
		closePopWindow();
		stopAll(lastClickIndex, channelList.get(lastClickIndex));
		// manager.pauseAll();

		if (Consts.PLAY_NORMAL == playFlag) {
			// add{3384} by lkp@2014.12.09,设备设置里边需要修改设备状态，如果不加这行，返回后状态会被重置
			deviceList = CacheUtil.getDevList();
			CacheUtil.saveDevList(deviceList);
		}

		if (null != channelList && 0 != channelList.size()) {
			try {
				int size = channelList.size();
				for (int i = 0; i < size; i++) {
					if (channelList.get(i).isConnected()) {
						pauseChannel(channelList.get(i));
					} else if (channelList.get(i).isConnecting()) {
						Jni.disconnect(channelList.get(i).getIndex());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// pauseAll(channelList);
	}

	public void pauseAll(ArrayList<Channel> channelList) {
		if (null != channelList && channelList.size() != 0) {
			int size = channelList.size();
			for (int i = 0; i < size; i++) {
				pauseChannel(channelList.get(i));
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		closePopWindow();

		// // [Neo] add black screen time
		// Jni.setColor(lastClickIndex, 0, 0, 0, 0);
		if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation) {// 竖屏
			if (View.VISIBLE == horfunc_talk.getVisibility()) {
				horfunc_talk.setVisibility(View.GONE);
				ishonfunctalk = false;
			}
		}
		if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {// 横屏
			if (View.VISIBLE == talk_eachother.getVisibility()) {
				talk_eachother.setVisibility(View.GONE);
				function.setVisibility(View.VISIBLE);
				istalk = false;
			}
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
				Channel ch;
				int size = currentPageChannelList.size();
				for (int i = 0; i < size; i++) {
					ch = currentPageChannelList.get(i);
					if (lastClickIndex - 1 > ch.getIndex()
							|| lastClickIndex + 1 < ch.getIndex()) {
						disconnectChannelList.add(ch);
					} else if (lastClickIndex == ch.getIndex()) {
						// [Neo] Empty
					} else {
						// [Neo] stand alone for single destroy window, too
						pauseChannel(ch);
					}
				}
				changeWindow(ONE_SCREEN);
			}

			playViewPager.setDisableSliding(true);
		} else {
			if (ONE_SCREEN != currentScreen) {// 当前非单屏
				changeWindow(ONE_SCREEN);
			}
			playViewPager.setDisableSliding(false);
		}
		showFunc(channelList.get(lastClickIndex), currentScreen, lastClickIndex);
		setPlayViewSize();
	}

	// /**
	// * 保存最后一帧图像
	// *
	// * @param channel
	// */
	// public void saveLastScreen(Channel channel) {
	// if (Consts.PLAY_AP == playFlag) {
	// return;
	// }
	// if (hasSDCard() && null != channel && channel.isConnected()) {
	// String savePath = Consts.SCENE_PATH;
	// String fileName = "";
	// String fullPath = "";
	// MobileUtil.createDirectory(new File(savePath));
	//
	// if (Consts.PLAY_NORMAL == playFlag) {
	// if (2 == channel.getParent().getIsDevice()) {
	// fileName = channel.getParent().getDoMain()
	// + Consts.IMAGE_PNG_KIND;
	// } else {
	// fileName = channel.getParent().getFullNo()
	// + Consts.IMAGE_PNG_KIND;
	// }
	//
	// } else if (Consts.PLAY_DEMO == playFlag) {
	// fileName = "demo_" + channel.getParent().getFullNo()
	// + Consts.IMAGE_PNG_KIND;
	// }
	// fullPath = savePath + fileName;
	// File file = new File(fullPath);
	// if (file.exists()) {// 场景图存在先删掉老的
	// MyLog.v("capture", "delete=file" + ";fullPath=" + fullPath);
	// ConfigUtil.deleteSceneFile(channel.getParent().getFullNo());
	// }
	// boolean capture = PlayUtil.capture(lastClickIndex, fullPath);
	// MyLog.i("capture", "capture=" + capture + ";fullPath=" + fullPath);
	// }
	// }

	private class Connecter extends Thread {

		@Override
		public void run() {
			MyLog.w(Consts.TAG_XXX, "Connecter E" + channelList.toString());

			try {
				Channel channel = null;
				int size = disconnectChannelList.size();

				for (int i = 0; i < size; i++) {
					MyLog.v(Consts.TAG_XXX, "disconnect " + i + "/" + size);
					channel = disconnectChannelList.get(i);

					int index = channel.getIndex();
					boolean needConnect = false;
					for (Channel currentChannel : currentPageChannelList) {
						if (index == currentChannel.getIndex()) {
							needConnect = true;
							break;
						}
					}
					if (needConnect) {
						MyLog.e(Consts.TAG_XXX, "disconnect not for current: "
								+ channel);
						continue;
					}

					if (channel.isConnected() || channel.isConnecting()) {
						boolean result = false;
						if (1 == channel.getVipLevel()) {
							result = Jni.shutdownRTMP(channel.getIndex());
						} else {
							result = Jni.disconnect(channel.getIndex());
						}

						if (false == result) {
							MyLog.e(Consts.TAG_XXX, "disconnect failed: "
									+ channel);
						} else {
							while (channel.isConnected()
									|| channel.isConnecting()) {
								sleep(DISCONNECTION_MIN_PEROID);
							}
							MyLog.i(Consts.TAG_XXX, "disconnected: " + channel);
						}
					} else {
						MyLog.e(Consts.TAG_XXX, "disconnect already done: "
								+ channel);
					}

				}
				disconnectChannelList.clear();

				handler.sendEmptyMessage(Consts.WHAT_DISMISS_PROGRESS);

				size = currentPageChannelList.size()
						+ connectChannelList.size();
				for (int i = 0; i < size; i++) {
					MyLog.v(Consts.TAG_XXX, "connect " + i + "/" + size);
					boolean isPlayDirectly = false;

					if (i < currentScreen) {
						isPlayDirectly = true;
						channel = currentPageChannelList.get(i);
					} else {
						channel = connectChannelList.get(i - currentScreen);
					}

					if (false == channel.isConnected()
							&& false == channel.isConnecting()) {

						if (2 == channel.getParent().getIsDevice()) {// 域名设备先解析IP
							handler.sendMessage(handler.obtainMessage(
									Consts.WHAT_PLAY_STATUS,
									channel.getIndex(),
									Consts.ARG2_STATUS_CONNECTING));
							resolveIp(channel, isPlayDirectly);
						} else {
							boolean result = connect(channel, isPlayDirectly);
							if (false == result) {
								MyLog.e(Consts.TAG_XXX, "connect failed: "
										+ channel);
							} else {
								MyLog.i(Consts.TAG_XXX, "connecting: "
										+ channel);
								sleep(CONNECTION_MIN_PEROID);
							}
						}

					} else if (channel.isConnecting()) {
						MyLog.i(Consts.TAG_XXX, "connect in connecting: "
								+ channel);
						handler.sendMessage(handler.obtainMessage(
								Consts.WHAT_PLAY_STATUS, channel.getIndex(),
								Consts.ARG2_STATUS_CONNECTING));
					} else if (false == channel.isPaused()) {
						MyLog.i(Consts.TAG_XXX, "connect not pause: " + channel);
						// MyLog.v(TAG,"connecter+buffering,index="+channel.getIndex());
						channel.setPaused(true);
						boolean result = resumeChannel(channel);
						if (false == result) {
							// channel.setConnected(false);
							MyLog.e(Consts.TAG_XXX,
									"connect not pause force resume failed: "
											+ channel);
						} else {
							handler.sendMessage(handler.obtainMessage(
									Consts.WHAT_PLAY_STATUS,
									channel.getIndex(),
									Consts.ARG2_STATUS_BUFFERING));
							sleep(RESUME_VIDEO_MIN_PEROID);
							MyLog.i(Consts.TAG_XXX,
									"connect not pause force resume: "
											+ channel);
						}

					} else {

						MyLog.i(Consts.TAG_XXX, "connect is pause: " + channel);
						boolean result = resumeChannel(channel);
						if (false == result) {
							// channel.setConnected(false);
							MyLog.e(Consts.TAG_XXX, "connect resume failed: "
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

			handler.sendEmptyMessage(Consts.WHAT_RESTORE_UI);
			MyLog.w(Consts.TAG_XXX, "Connecter X" + channelList.toString());
		}
	}

	// /**
	// * 单向对讲用功能
	// */
	// OnTouchListener callOnTouchListener = new OnTouchListener() {
	//
	// @Override
	// public boolean onTouch(View arg0, MotionEvent arg1) {
	//
	//
	// return false;
	// }
	//
	// };
	/*
	 * 
	 * */
	private void talkMothed() {
		if (channelList.get(lastClickIndex).isSingleVoice() && VOICECALLING) {// 单向对讲
			handler.sendMessage(handler.obtainMessage(Consts.STOP_AUDIO_GATHER));
			new TalkThread(lastClickIndex, 0).start();
			VOICECALL_LONG_CLICK = false;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			// voiceTip.setVisibility(View.GONE);
			handler.sendMessageDelayed(
					handler.obtainMessage(Consts.START_AUDIO_GATHER), 2 * 1000);
		}
	}

	OnTouchListener myOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (v.getId()) {
			case R.id.talk_img:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					startSendVoice();
					talk_img_down.setVisibility(View.VISIBLE);
					talk_img.setVisibility(View.GONE);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					talkMothed();
					talk_img_down.setVisibility(View.GONE);
					talk_img.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.horfunc_talk_normal:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					startSendVoice();
					horfunc_talk_down.setVisibility(View.VISIBLE);
					horfunc_talk_normal.setVisibility(View.GONE);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					talkMothed();
					horfunc_talk_down.setVisibility(View.GONE);
					horfunc_talk_normal.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
			return true;
		}
	};

	// /**
	// * 单向对讲用功能
	// */
	// OnLongClickListener callOnLongClickListener = new OnLongClickListener() {
	//
	// @Override
	// public boolean onLongClick(View arg0) {
	//
	// return true;
	// }
	//
	// };

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
	 * 长按发送语音数据
	 */
	private void startSendVoice() {
		if (channelList.get(lastClickIndex).isSingleVoice() && VOICECALLING) {// 单向对讲且正在对讲
			VOICECALL_LONG_CLICK = true;
			if (JVPlayActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} else if (JVPlayActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
			// voiceTip.setVisibility(View.VISIBLE);
			new TalkThread(lastClickIndex, 1).start();
		}
	}

	/**
	 * 抬起手指停止发送语音数据
	 */
	private void stopSendVoice() {
		if (channelList.get(lastClickIndex).isSingleVoice()) {
			if (VOICECALL_LONG_CLICK) {// 正在长按对讲，关闭长按功能
				handler.sendMessage(handler
						.obtainMessage(Consts.STOP_AUDIO_GATHER));
				new TalkThread(lastClickIndex, 0).start();
				VOICECALL_LONG_CLICK = false;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				// voiceTip.setVisibility(View.GONE);
				handler.sendMessageDelayed(
						handler.obtainMessage(Consts.START_AUDIO_GATHER),
						2 * 1000);
			} else if (VOICECALLING) {// 正在对讲关闭对讲
				Channel channel = channelList.get(lastClickIndex);
				voiceCall(channel);
			}
		}

	}

	class MyOnGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			MyLog.v("MyOnGestureListener", "onSingleTapUp");
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {//
			MyLog.v("MyOnGestureListener", "onLongPress");
			if (VOICECALLING) {
				startSendVoice();
			}
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float disX = e2.getX() - e1.getX();
			float disY = e2.getY() - e1.getY();
			MyLog.v("MyOnGestureListener", "onScroll");
			if (Math.abs(disX) >= 5 || Math.abs(disY) >= 5) {
				stopSendVoice();
			}
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float disX = e2.getX() - e1.getX();
			float disY = e2.getY() - e1.getY();
			MyLog.v("MyOnGestureListener", "onFling");
			if (Math.abs(disX) >= 5 || Math.abs(disY) >= 5) {
				stopSendVoice();
			}

			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			MyLog.v("MyOnGestureListener", "onShowPress");
		}

		@Override
		public boolean onDown(MotionEvent e) {
			MyLog.v("MyOnGestureListener", "onDown");
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			MyLog.v("MyOnGestureListener", "onDoubleTap");
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			MyLog.v("MyOnGestureListener", "onDoubleTapEvent");
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// Channel channel = channelList.get(lastClickIndex);
			// voiceCall(channel);
			MyLog.v("MyOnGestureListener", "onSingleTapConfirmed");
			return false;
		}

	}

	private String getActionName(int action) {
		String name = "";
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			name = "ACTION_DOWN";
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			name = "ACTION_MOVE";
			break;
		}
		case MotionEvent.ACTION_UP: {
			stopSendVoice();
			break;
		}
		default:
			stopSendVoice();
			break;
		}
		return name;
	}

}
