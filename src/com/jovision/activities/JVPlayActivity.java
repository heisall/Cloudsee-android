package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.PlayViewPagerAdapter;
import com.jovision.adapters.ScreenAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.PlayUtil;

public class JVPlayActivity extends PlayActivity implements
		PlayWindowManager.OnUiListener {
	private final String TAG = "JV_PLAY";

	private PlayWindowManager manager;

	private int selectedScreen = 4;// 默认分四屏

	private int currentScreen = 1;// 当前分屏数
	private int lastClickIndex = 0;

	private int currentPage = 0;// 当前页
	private int currentWindow = 0;// 当窗口
	private int currentIndex = 0;// 当前下标

	private int oneScreen = 1;// 单屏
	private int fourScreen = 4;// 四屏
	private int nineScreen = 9;// 九屏
	// private int sixteenScreen = 16;// 十六屏

	private int connNum = 4;// 保持连接数

	private HashMap<Integer, Channel> channelMap;

	private int startWindowIndex;
	private ArrayList<Channel> playChannelList;

	private boolean hasCheckDoubleClick = false;
	private boolean isSwitching = false;
	private View mLastPlayView;

	/** intent传递过来的设备和通道下标 */
	private int deviceIndex;
	private int channelIndex;
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	HashMap<Integer, Boolean> surfaceCreatMap = new HashMap<Integer, Boolean>();

	private int playFlag = -1;

	private boolean isOmx = false;
	private Button decodeBtn;

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	// int what, int uchType, int channel,
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		// 抓拍回调
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

		// 每秒状态回调
		case Consts.CALL_STAT_REPORT: {
			// PlayUtil.updateState(manager.getChannelList());

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
									object.getInt("index"),
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
					if (isOmx) {
						decodeBtn.setText("硬解");
					} else {
						decodeBtn.setText("软解");
					}

					int index = object.getInt("index");
					loadingState(index, 0, JVConst.PLAY_CONNECTTED);
				}
				linkMode.setText(sBuilder.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		// 等待I帧成功
		case Consts.CALL_FRAME_I_REPORT: {
			loadingState(arg1, 0, JVConst.PLAY_CONNECTTED);
			MyLog.e(TAG + "-IFrame", "new Frame I: index = " + arg1
					+ ", left = " + arg2);
			// 多于四屏只发关键帧
			if (currentScreen > fourScreen
					&& !manager.getChannel(arg1).isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_ONLYI, new byte[0],
						8);
				manager.getChannel(arg1).setSendCMD(true);
			} else if (currentScreen <= fourScreen
					&& manager.getChannel(arg1).isSendCMD()) {
				Jni.sendCmd(arg1, (byte) JVNetConst.JVN_CMD_FULL, new byte[0],
						8);
				manager.getChannel(arg1).setSendCMD(true);
			}

			manager.getChannel(arg1).setConnecting(false);
			manager.getChannel(arg1).setConnected(true);
			break;
		}

		// 连接结果
		case Consts.CALL_CONNECT_CHANGE: {
			Channel channel = manager.getChannel(arg2);
			if (null == channel) {
				return;
			}
			switch (arg1) {
			// 1 -- 连接成功
			case JVNetConst.CONNECT_OK: {
				channel.setConnecting(false);
				channel.setConnected(true);
				loadingState(arg2, R.string.connecting_buffer,
						JVConst.PLAY_CONNECTING_BUFFER);
				break;
			}

			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
				loadingState(arg2, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				channel.setConnecting(false);
				channel.setConnected(false);
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
				// MyLog.e(TAG, "connect--arg1=" + arg1 + "--arg2=" +
				// arg2+"--obj="+obj.toString());
				// connect--arg1=4--arg2=0--obj={"data":0,"msg":"connect timeout!"}
				try {
					JSONObject connectObj = new JSONObject(obj.toString());
					String errorMsg = connectObj.getString("msg");
					if ("password is wrong!".equalsIgnoreCase(errorMsg)
							|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
						loadingState(arg2, R.string.connfailed_auth,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("channel is not open!"
							.equalsIgnoreCase(errorMsg)) {// 无该通道服务
						loadingState(arg2, R.string.connfailed_channel_notopen,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("connect type invalid!"
							.equalsIgnoreCase(errorMsg)) {// 连接类型无效
						loadingState(arg2, R.string.connfailed_type_invalid,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
						loadingState(arg2, R.string.connfailed_maxcount,
								JVConst.PLAY_DIS_CONNECTTED);
					} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
						loadingState(arg2, R.string.connfailed_timeout,
								JVConst.PLAY_DIS_CONNECTTED);
					} else {
						loadingState(arg2, R.string.connect_failed,
								JVConst.PLAY_DIS_CONNECTTED);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				channel.setConnecting(false);
				channel.setConnected(false);
				break;
			}

			// 5 -- 没有连接
			case JVNetConst.NO_CONNECT: {
				channel.setConnecting(false);
				channel.setConnected(false);
				break;
			}

			// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT: {
				loadingState(arg2, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				channel.setConnecting(false);
				channel.setConnected(false);
				break;
			}

			// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP: {
				loadingState(arg2, R.string.closed, JVConst.PLAY_DIS_CONNECTTED);
				channel.setConnecting(false);
				channel.setConnected(false);
				break;
			}

			// 8 -- 断开连接失败
			case JVNetConst.DISCONNECT_FAILED: {
				channel.setConnecting(false);
				channel.setConnected(true);
				break;
			}

			// 9 -- 其他错误
			case JVNetConst.OHTER_ERROR: {
				channel.setConnecting(false);
				channel.setConnected(false);
				break;
			}
			default:
				break;
			}

			break;
		}

		// O帧
		case Consts.CALL_NORMAL_DATA: {

			Channel channel = manager.getChannel(arg2);
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
					if (4 == jobj.optInt("device_type")) {
						channel.getParent().setHomeProduct(true);
					} else {
						channel.getParent().setHomeProduct(false);
					}
					channel.getParent().setO5(jobj.optBoolean("is05"));

					if (!jobj.optBoolean("is05")) {// 提示不支持04版本解码器
						errorDialog(getResources().getString(
								R.string.not_support_old));
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}

		// 开始连接
		case JVConst.WHAT_STARTING_CONNECT: {
			// handler.sendEmptyMessageDelayed(Consts.WHAT_DUMMY, 100);
			// MyLog.v(TAG + ":222", arg1 + "");
			channelMap.put(arg1 % connNum, manager.getChannel(arg1));
			loadingState(arg1, R.string.connecting, JVConst.PLAY_CONNECTING);
			break;
		}

		// 取消视频双击
		case JVConst.WHAT_CHECK_DOUBLE_CLICK: {
			hasCheckDoubleClick = false;
			break;
		}

		// 视频双击事件
		case JVConst.WHAT_CHANGE_LAYOUT: {
			isSwitching = true;
			if (currentScreen == oneScreen) {
				// jy计算分屏数据并连接
				computeScreenData(currentScreen, selectedScreen);
			} else {
				// jy计算分屏数据并连接
				computeScreenData(currentScreen, oneScreen);
			}
			// jy连接所有
			connectAll(currentPage);
			break;
		}

		// 下拉选择多屏
		case JVConst.WHAT_SELECT_SCREEN: {
			selectedScreen = screenList.get(arg1);
			screenAdapter.selectIndex = arg1;
			screenAdapter.notifyDataSetChanged();
			popScreen.dismiss();
			// jy计算分屏数据并连接
			computeScreenData(currentScreen, selectedScreen);
			// jy连接所有
			connectAll(currentPage);
			break;
		}

		// 音频监听
		case Consts.CALL_PLAY_AUDIO: {
			if (null != obj && null != audioQueue) {
				byte[] data = (byte[]) obj;
				audioQueue.offer(data);
			}
			break;
		}
		// 对讲数据
		case Consts.CALL_CHAT_DATA: {
			MyLog.v(TAG + "Chat", "CALL_CHAT_DATA:arg1=" + arg1);
			switch (arg1) {
			// 语音数据
			case JVNetConst.JVN_RSP_CHATDATA: {
				MyLog.v(TAG, "chatdata");
				break;
			}

			// 同意语音请求
			case JVNetConst.JVN_RSP_CHATACCEPT: {
				voiceCallSelected(true);
				recorder.start();
				break;
			}

			// 暂停语音聊天
			case JVNetConst.JVN_CMD_CHATSTOP: {
				break;
			}
			}
			break;
		}
		}
	}

	@Override
	protected void initSettings() {
		PlayUtil.setContext(JVPlayActivity.this);
		manager = PlayWindowManager.getIntance(this);
		manager.setArrowId(R.drawable.left, R.drawable.up, R.drawable.right,
				R.drawable.down);

		// [Neo] TODO make omx enable as default
		isOmx = true;

		Intent intent = getIntent();
		deviceIndex = intent.getIntExtra("DeviceIndex", 0);
		channelIndex = intent.getIntExtra("ChannelIndex", 0);
		playFlag = intent.getIntExtra("PlayFlag", 0);

		if (Consts.PLAY_NORMAL == playFlag) {
			deviceList = CacheUtil.getDevList();
		} else if (Consts.PLAY_DEMO == playFlag) {
			String devJsonString = MySharedPreference
					.getString(Consts.KEY_PLAY_DEMO);
			deviceList = Device.fromJsonArray(devJsonString);
		}

		// [Neo] precheck
		if (deviceList.size() < deviceIndex
				|| false == deviceList.get(deviceIndex).getChannelList()
						.hasIndex(channelIndex)) {
			MyLog.e(Consts.TAG_XX, "JVPlay init: precheck 1 failed!");
			// [Neo] TODO 错误的参数，需要检查之前的活动
		}

		startWindowIndex = 0;
		playChannelList = new ArrayList<Channel>();

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
						if (cList.get(j).getChannel() < channelIndex) {
							startWindowIndex++;
						}
					}
				}

				playChannelList.addAll(cList);
			}
		} else {
			ArrayList<Channel> cList = deviceList.get(deviceIndex)
					.getChannelList().toList();
			int csize = cList.size();
			for (int j = 0; j < csize; j++) {
				if (cList.get(j).getChannel() < channelIndex) {
					startWindowIndex++;
				}
			}

			playChannelList.addAll(cList);
		}

		int size = playChannelList.size();
		for (int i = 0; i < size; i++) {
			manager.addChannel(playChannelList.get(i));
		}

		currentPage = startWindowIndex;
		lastClickIndex = playChannelList.get(startWindowIndex).getIndex();
		currentIndex = lastClickIndex;
		MyLog.i(Consts.TAG_XX, "JVPlay.init: " + currentPage + "/"
				+ playChannelList.size() + ", channel/index = "
				+ playChannelList.get(startWindowIndex).getChannel() + "/"
				+ playChannelList.get(startWindowIndex).getIndex());
	}

	@SuppressLint("UseSparseArrays")
	@Override
	protected void initUi() {
		super.initUi();

		channelMap = new HashMap<Integer, Channel>();

		/** 上 */
		back.setOnClickListener(myOnClickListener);
		rightFuncButton.setOnClickListener(myOnClickListener);
		selectScreenNum.setOnClickListener(myOnClickListener);
		currentMenu.setOnClickListener(myOnClickListener);
		currentMenu.setText(R.string.str_video_play);
		selectScreenNum.setVisibility(View.VISIBLE);
		linkMode.setVisibility(View.GONE);
		decodeBtn = (Button) findViewById(R.id.decodeway);

		/** 中 */
		viewPager.setVisibility(View.VISIBLE);
		playSurface.setVisibility(View.GONE);
		viewPager.setOnPageChangeListener(onPageChangeListener);
		pagerAdapter = new PlayViewPagerAdapter();
		pagerAdapter.update(manager.genPageList(currentScreen));
		viewPager.setAdapter(pagerAdapter);
		playFunctionList.setOnItemClickListener(onItemClickListener);

		audioMonitor.setOnClickListener(myOnClickListener);
		ytOperate.setOnClickListener(myOnClickListener);
		remotePlayback.setOnClickListener(myOnClickListener);

		autoimage.setOnTouchListener(new LongClickListener());
		zoomIn.setOnTouchListener(new LongClickListener());
		zoomout.setOnTouchListener(new LongClickListener());
		scaleSmallImage.setOnTouchListener(new LongClickListener());
		scaleAddImage.setOnTouchListener(new LongClickListener());
		upArrow.setOnTouchListener(new LongClickListener());
		downArrow.setOnTouchListener(new LongClickListener());
		leftArrow.setOnTouchListener(new LongClickListener());
		rightArrow.setOnTouchListener(new LongClickListener());

		/** 下 */
		capture.setOnClickListener(myOnClickListener);
		voiceCall.setOnClickListener(myOnClickListener);
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

		Jni.setStat(true);
		// 2.几的系统有可能花屏
		if (MobileUtil.mobileSysVersion(JVPlayActivity.this).startsWith("2")) {
			errorDialog(getResources().getString(R.string.system_lower)
					.replace("$",
							MobileUtil.mobileSysVersion(JVPlayActivity.this)));
		}

		viewPager.setCurrentItem(currentPage);

	}

	/**
	 * 设备升级Dialog
	 * 
	 * @param tag
	 */
	private void errorDialog(String errorMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				JVPlayActivity.this);
		builder.setCancelable(false);

		// builder.setTitle(getResources().getString(
		// R.string.str_quick_setting_new_dev));
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
				MyLog.e(TAG, "--loadingState--manager.getView(index)--isNull");
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
				if (currentScreen == oneScreen) {// 单屏
					centerResId = R.drawable.play_l;
				} else if (currentScreen > oneScreen
						&& currentScreen <= nineScreen) {// 4-9屏
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
	 * 页面切换事件
	 */
	OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {// arg0==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做，就是停在那

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			stopAll(currentIndex, manager.getChannel(currentIndex));
			if (arg0 - currentPage > 0) {// 向左滑
				currentPage = arg0;
				currentWindow = 0;
				currentIndex = currentPage * currentScreen + currentWindow;
				connectAll(currentPage);
			} else if (arg0 - currentPage < 0) {// 向右滑
				currentPage = arg0;
				currentWindow = 0;
				currentIndex = currentPage * currentScreen + currentWindow;
				connectAll(currentPage);
			}

			MyLog.v(TAG, "onPageScrolled-----currentPage=" + currentPage
					+ "---;currentIndex=" + currentIndex);
		}

		@Override
		public void onPageSelected(int arg0) {

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
			ViewGroup container = (ViewGroup) manager.getView(currentIndex)
					.getParent();
			int action = event.getAction();
			int cmd = 0;
			ArrayList<Channel> list = manager.getValidChannelList(currentPage);
			MyLog.v("list-size=", list.size() + "");
			Channel channel = manager.getChannel(currentIndex);
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

						if (currentScreen == oneScreen) {// jy单屏为当前页，lastClickIndex或currentPage对连接数取余
							channel.setAuto(false);
						} else {
							channel.setAuto(false);
						}

					} else {// 发开始命令
						cmd = JVNetConst.JVN_YTCTRL_A;

						if (currentScreen == oneScreen) {// jy单屏为当前页，lastClickIndex或currentPage对连接数取余
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
						PlayUtil.sendCtrlCMDLongPush(currentWindow, cmd, true);
					}
				} else if (action == MotionEvent.ACTION_UP) {

					if (channel != null && channel.isConnected()) {
						PlayUtil.sendCtrlCMDLongPush(currentWindow, cmd, false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

	}

	/**
	 * 大分辨率功能列表点击事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (0 == arg2) {// 音频监听
				if (allowThisFuc(true)) {
					initAudio();
					if (!PlayUtil.audioPlay(currentIndex)) {
						functionListAdapter.selectIndex = -1;
					} else {
						functionListAdapter.selectIndex = arg2;
					}
				} else {
					functionListAdapter.selectIndex = -1;
				}
			} else if (1 == arg2) {// 云台
				if (allowThisFuc(false)) {
					showPTZ();
				} else {
					functionListAdapter.selectIndex = -1;
				}

			} else if (2 == arg2) {// 远程回放
				if (allowThisFuc(true)) {
					startRemote();
				} else {
					functionListAdapter.selectIndex = -1;
				}
			}
			functionListAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 所有按钮事件
	 */
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left:// 左边按钮
				backMethod();
				break;
			case R.id.btn_right:// 右边按钮

				if (View.VISIBLE == linkMode.getVisibility()) {
					linkMode.setVisibility(View.GONE);
				} else {
					linkMode.setVisibility(View.VISIBLE);
				}
				break;
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
			case R.id.audio_monitor:// 音频监听
				initAudio();
				if (allowThisFuc(false)) {
					PlayUtil.audioPlay(currentIndex);
				}
				break;
			case R.id.yt_operate:// 云台
				if (allowThisFuc(false)) {
					showPTZ();
				}

				break;
			case R.id.remote_playback:// 远程回放
				if (allowThisFuc(true)) {
					startRemote();
				}

				break;
			case R.id.capture:// 抓拍
				if (hasSDCard() && allowThisFuc(false)) {
					boolean capture = PlayUtil.capture(currentIndex);
					MyLog.v(TAG, "capture=" + capture);
				}
				break;
			case R.id.voicecall:// 语音对讲
				initAudio();
				if (allowThisFuc(true)) {
					if (manager.getChannel(currentIndex).isVoiceCall()) {
						PlayUtil.stopVoiceCall(currentIndex);
						recorder.stop();
						manager.getChannel(currentIndex).setVoiceCall(false);
						voiceCallSelected(false);
					} else {
						PlayUtil.startVoiceCall(currentIndex);
						manager.getChannel(currentIndex).setVoiceCall(true);
					}
				}
				break;
			case R.id.videotape:// 录像
				if (hasSDCard() && allowThisFuc(true)) {
					if (PlayUtil.videoRecord(currentIndex)) {// 打开
						tapeSelected(true);
						showTextToast(R.string.str_start_record);
					} else {// 关闭
						showTextToast(Consts.VIDEO_PATH);
						tapeSelected(false);
					}
				}

				break;
			case R.id.more_features:// 更多
				// Intent moreIntent = new Intent();
				// moreIntent.setClass(JVPlayActivity.this,
				// JVMoreFeatureActivity.class);
				// JVPlayActivity.this.startActivity(moreIntent);
				break;

			case R.id.bottom_but1:
				Toast.makeText(JVPlayActivity.this, "点击图标1", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but2:
				Toast.makeText(JVPlayActivity.this, "点击图标2", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but3:
				Toast.makeText(JVPlayActivity.this, "点击图标3", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but4:
				Toast.makeText(JVPlayActivity.this, "点击图标4", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but5:
				Toast.makeText(JVPlayActivity.this, "点击图标5", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but6:
				Toast.makeText(JVPlayActivity.this, "点击图标6", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but7:
				Toast.makeText(JVPlayActivity.this, "点击图标7", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.bottom_but8:
				Toast.makeText(JVPlayActivity.this, "点击图标8", Toast.LENGTH_SHORT)
						.show();
				break;
			}

		}

	};

	/**
	 * jy计算分屏数据
	 * 
	 * @param source
	 * @param target
	 * @param init
	 *            ,初始化不做当前页非当前页判断
	 */
	public void computeScreenData(int source, int target) {
		stopAll(currentIndex, manager.getChannel(currentIndex));
		// 计算保持连接数
		if (target == oneScreen) {
			connNum = fourScreen;// 单屏保持四路连接
		} else {
			connNum = target;
		}
		currentScreen = target;

		if (currentScreen == oneScreen) {
			currentPage = lastClickIndex;
			currentWindow = 0;
		} else {
			currentPage = lastClickIndex / currentScreen;
			currentWindow = lastClickIndex % currentScreen;
		}

		// 不同多屏播放文字，loading框大小，文字颜色
		if (currentScreen == oneScreen) {// 单屏
			manager.setCenterInfo(60, 16, Color.GREEN);
		} else if (currentScreen > oneScreen && currentScreen <= nineScreen) {// 4-9屏
			manager.setCenterInfo(40, 14, Color.GREEN);
		} else {// 其他
			manager.setCenterInfo(20, 12, Color.GREEN);
		}

		// [Neo] no need to call it anymore
		// manager.clearPageLayout();
		viewPager.removeAllViews();

		manager.setArrowId(R.drawable.left, R.drawable.up, R.drawable.right,
				R.drawable.down);
		pagerAdapter.update(manager.genPageList(currentScreen));
		// surfaceWidth = (int) (disMetrics.widthPixels/Math.sqrt(target));
		// surfaceHeight = (int) (disMetrics.heightPixels/Math.sqrt(target));
		// MyLog.e(TAG, "w="+surfaceWidth+ ";h="+surfaceHeight);
		viewPager.setAdapter(pagerAdapter);

		if (oneScreen == target) {//
			viewPager.setCurrentItem(lastClickIndex, false);
		} else {
			viewPager.setCurrentItem(lastClickIndex / target, true);
		}

		mLastPlayView = manager.getView(lastClickIndex);
		// 多屏选中才变蓝色
		if (currentScreen > oneScreen) {
			((View) mLastPlayView.getParent())
					.setBackgroundColor(getResources().getColor(
							R.color.videoselect));
		}

		manager.resumePage(currentPage);
		isSwitching = false;

	}

	/**
	 * 连接单页所有通道
	 * 
	 */
	public void connectAll(final int page) {

		MyLog.v(TAG, "---------connectAll----------page=" + page
				+ "--currentIndex=" + currentIndex);
		try {
			Thread connectThread = new Thread() {
				@Override
				public void run() {
					PlayUtil.disAndPause(currentScreen, page, channelMap);// 连接之前暂停该暂停的，断开多余的
					ArrayList<Channel> channleList = manager
							.getValidChannelList(page);
					int size = channleList.size();
					MyLog.v(TAG,
							"---------connectAll-----channleList.size()-----"
									+ size + "");
					for (int i = 0; i < size; i++) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Channel channel = channleList.get(i);
						Device dev = channel.getParent();

						while (!surfaceCreatMap.get(channel.getIndex())) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						PlayUtil.connect(dev, channel.getChannel(), isOmx);
					}

					super.run();
				}

			};
			connectThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 调用功能前判断是否可调用
	 * 
	 * @return
	 */
	public boolean allowThisFuc(boolean changToOneScreen) {
		boolean allow = false;
		MyLog.v(TAG, "currentIndex = " + currentIndex);
		if (currentScreen != oneScreen && changToOneScreen) {
			// jy计算分屏数据并连接
			computeScreenData(currentScreen, oneScreen);
			// jy连接所有
			connectAll(currentPage);
		}

		if (manager.getChannel(currentIndex).isConnected()) {
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
		remoteIntent.putExtra("ChannelIndex", manager.getChannel(currentIndex)
				.getIndex());
		remoteIntent.putExtra("DeviceType", manager.getChannel(currentIndex)
				.getParent().getDeviceType());
		remoteIntent.putExtra("is05", manager.getChannel(currentIndex)
				.getParent().is05());
		JVPlayActivity.this.startActivity(remoteIntent);
	}

	/**
	 * 返回事件
	 */
	private void backMethod() {
		if (View.VISIBLE == ytLayout.getVisibility()) {
			ytLayout.setVisibility(View.GONE);
			if (bigScreen) {
				playFunctionList.setVisibility(View.VISIBLE);
				playFuctionLayout.setVisibility(View.GONE);
			} else {
				playFunctionList.setVisibility(View.GONE);
				playFuctionLayout.setVisibility(View.VISIBLE);
			}
		} else {
			PlayUtil.disConnectAll(manager.getChannelList());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			JVPlayActivity.this.finish();
		}

	}

	@Override
	public void onBackPressed() {
		backMethod();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		super.freeMe();
		manager.destroy();
		PlayUtil.disConnectAll(manager.getChannelList());
	}

	@Override
	public void onGesture(int direction) {
		// [Neo] TODO surface 上下左右的手势
	}

	@Override
	public void onLongClick(Channel channel) {
		// [Neo] TODO surface 长按事件
	}

	@Override
	public void onLifecycle(final int index, int status, final Surface surface,
			int width, int height) {
		switch (status) {
		case PlayWindowManager.STATUS_CREATED:
			surfaceCreatMap.put(index, true);
			Thread resumeThread = new Thread() {
				@Override
				public void run() {
					manager.getChannel(index).setSurfaceCreated(true);
					Jni.resume(index, surface);
					super.run();
				}
			};
			resumeThread.start();
			break;
		case PlayWindowManager.STATUS_DESTROYED:
			surfaceCreatMap.put(index, false);
			Thread pauseThread = new Thread() {
				@Override
				public void run() {
					manager.getChannel(index).setSurfaceCreated(false);
					Jni.pause(index);
					super.run();
				}
			};
			pauseThread.start();
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(Channel channel, boolean isFromImageView, int viewId) {
		if (isFromImageView) {// 播放按钮事件
			try {
				loadingState(channel.getIndex(), R.string.connecting,
						JVConst.PLAY_CONNECTTED);

				if (null != channel) {
					while (!surfaceCreatMap.get(channel.getIndex())) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				PlayUtil.connect(channel.getParent(), channel.getChannel(),
						isOmx);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {// OPenglFrame
			if (hasCheckDoubleClick && lastClickIndex == channel.getIndex()) {// 双击
				if (isSwitching) {
					MyLog.e(TAG, "JVPlay.onClick.double: "
							+ "switching, ignore");
					return;
				}
				isSwitching = true;
				hasCheckDoubleClick = false;
				handler.sendMessage(handler.obtainMessage(
						JVConst.WHAT_CHANGE_LAYOUT, JVConst.ARG1_SET_ITEM,
						channel.getIndex()));

			} else {// 单击
				if (null != mLastPlayView
						&& mLastPlayView != manager.getView(channel.getIndex())) {
					((View) mLastPlayView.getParent())
							.setBackgroundColor(getResources().getColor(
									R.color.videounselect));
				}
				mLastPlayView = manager.getView(channel.getIndex());
				lastClickIndex = channel.getIndex();
				currentWindow = lastClickIndex % currentScreen;
				currentIndex = channel.getIndex();
				hasCheckDoubleClick = true;
				// 多屏选中才变蓝色
				if (currentScreen > oneScreen) {
					((View) mLastPlayView.getParent())
							.setBackgroundColor(getResources().getColor(
									R.color.videoselect));
				} else {
					bottom.setVisibility(View.VISIBLE);
					topBartwo.setVisibility(View.VISIBLE);
					init();
				}
				handler.sendEmptyMessageDelayed(
						JVConst.WHAT_CHECK_DOUBLE_CLICK,
						JVConst.CHECK_DOUBLE_DELAY);
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		manager.resumeAll();
		PlayUtil.resumeAll(manager.getValidChannelList(currentPage), isOmx);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != popScreen && popScreen.isShowing()) {
			screenAdapter.notifyDataSetChanged();
			popScreen.dismiss();
		}
		stopAll(currentIndex, manager.getChannel(currentIndex));
		manager.pauseAll();
		PlayUtil.pauseAll(manager.getValidChannelList(currentPage));

		if (Consts.PLAY_NORMAL == playFlag) {
			CacheUtil.saveDevList(deviceList);
		}
	}

	@Override
	public void onFlip(View view) {
		super.onFlip(view);

		isOmx = !isOmx;
		boolean result = Jni.setOmx(0, isOmx);
		MyLog.e(TAG, "onFlip: set " + isOmx + " = " + result);

		if (false == result) {
			isOmx = !isOmx;
		}
	}

}