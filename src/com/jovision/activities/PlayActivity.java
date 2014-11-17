package com.jovision.activities;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.FuntionAdapter;
import com.jovision.adapters.ScreenAdapter;
import com.jovision.adapters.StreamAdapter;
import com.jovision.audio.MICRecorder;
import com.jovision.audio.PlayAudio;
import com.jovision.bean.Channel;
import com.jovision.commons.JVConst;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.MyViewPager;

public abstract class PlayActivity extends BaseActivity {

	protected boolean bigScreen = false;// 大小屏标识

	protected boolean isOmx = true;
	protected Boolean lowerSystem = false;// 低于4.1的系统
	protected boolean realStop = false;

	/** 播放相关 */
	protected RelativeLayout.LayoutParams reParamsV;
	protected RelativeLayout.LayoutParams reParamsH;
	protected int surfaceWidth = -1;
	protected int surfaceHeight = -1;
	protected int playFlag = -1;

	/** layout 上 */
	protected LinearLayout topBar;// 顶部标题栏
	protected Button back;// 返回
	protected Button rightFuncButton;// 右边按钮事件
	protected TextView currentMenu;// 当前标题
	protected ImageView selectScreenNum;// 下拉选择当前分屏数按钮
	protected PopupWindow popScreen;// 选择框
	protected ScreenAdapter screenAdapter;
	protected ListView screenListView;
	protected ArrayList<Integer> screenList = new ArrayList<Integer>();// 分屏下拉选择列表

	/** layout 中 */
	protected MyViewPager viewPager;
	protected SurfaceView playSurface;
	protected TextView linkMode;// 测试显示连接方式

	protected LinearLayout playBackBar;// 远程回放工具条
	protected SeekBar progressBar;// 远程回放进度
	protected Button playBackPause;// 远程回放暂停继续播

	/** 　竖屏播放工具bar　 */
	protected RelativeLayout verPlayBarLayout;

	/** 　横屏播放工具bar　 */
	protected RelativeLayout horPlayBarLayout;

	/** 远程回放连接状态 */
	protected TextView linkState;// 连接文字
	protected ProgressBar loading;// 加载进度

	// protected Button audioMonitor;// 音频监听
	// protected Button ytOperate;// 云台
	// protected Button remotePlayback;// 远程回放
	protected LinearLayout ytLayout;// 云台布局
	// protected LinearLayout playFuctionLayout;// 小分辨率时功能界面
	protected ListView playFunctionList;// 大分辨率时功能列表
	protected FuntionAdapter functionListAdapter;
	protected ArrayList<String> functionList = new ArrayList<String>();

	/** 云台操作 */
	protected ImageView autoimage, zoomIn, zoomout, scaleSmallImage,
			scaleAddImage, upArrow, downArrow, leftArrow, rightArrow,
			yt_cancle;

	/** layout 下 */
	protected Button capture;// 抓拍
	protected Button voiceCall;// 喊话
	protected Button videoTape;// 录像
	protected Button moreFeature;// 更多
	protected LinearLayout footerBar;// 底部工具栏
	protected LinearLayout apFuncLayout;// 底部Ap下一步按钮
	protected Button nextStep;// 下一步

	protected Boolean recoding = false;

	/** 录像按钮 */
	protected Drawable videoTapeTop1 = null;
	protected Drawable videoTapeTop2 = null;
	/** 对讲按钮 */
	protected Drawable voiceCallTop1 = null;
	protected Drawable voiceCallTop2 = null;

	protected PlayAudio playAudio;// 音频监听
	protected LinkedBlockingQueue<byte[]> audioQueue;

	protected MICRecorder recorder;// 音频采集

	protected Button left_btn_h;// 横屏返回键
	protected TextView currentMenu_h;// 横屏手动录像，报警录像键
	protected Button right_btn_h;// 横屏手动录像，报警录像键
	protected RelativeLayout topBarH;// 横屏topbar

	protected Button bottombut1;// 视频播放和暂停
	protected Button bottombut2;// 软硬解
	protected Button bottombut3;// 抓拍
	protected Button bottombut4;// 远程回放
	protected Button bottombut5;// 对讲
	protected Button bottombut6;// 视频翻转
	protected Button bottombut7;// 录像
	protected Button bottombut8;// 音频监听
	protected TextView bottomStream;
	protected boolean bottomboolean1;
	private LinearLayout linear;
	private RelativeLayout relative1;
	private RelativeLayout relative2;
	private RelativeLayout relative3;
	private RelativeLayout relative4;
	private RelativeLayout relative5;
	private RelativeLayout relative6;
	private RelativeLayout relative7;
	private RelativeLayout relative8;

	/** IPC独有特性 */
	protected Button decodeBtn;// 软硬解
	protected Button videTurnBtn;// 视频翻转
	protected Button currentKbps;// 当前统计
	protected TextView play_nickname;

	// 录像模式----rightFuncButton
	// 码流切换----moreFeature
	protected String[] streamArray;
	protected ListView streamListView;// 码流listview
	protected StreamAdapter streamAdapter;// 码流adapter
	protected RelativeLayout voiceTip;// 单向对讲提示

	public static boolean AUDIO_SINGLE = false;// 单向对讲标志
	public static boolean VOICECALL_LONG_CLICK = false;// 语音喊话flag长按状态,长按发送数据
	public static boolean VOICECALLING = false;// 对讲功能已经开启

	// 按钮图
	protected Drawable alarmRecordDrawableTop = null;
	protected Drawable normalRecordDrawableTop = null;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.play_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 屏幕常亮

		recorder = MICRecorder.getInstance(-1);

		/** 上 */
		topBar = (LinearLayout) findViewById(R.id.top_bar);// 顶部标题栏
		back = (Button) findViewById(R.id.btn_left);
		rightFuncButton = (Button) findViewById(R.id.btn_right);
		rightFuncButton.setVisibility(View.GONE);

		currentMenu = (TextView) findViewById(R.id.currentmenu);

		selectScreenNum = (ImageView) findViewById(R.id.selectscreen);
		alarmRecordDrawableTop = getResources().getDrawable(
				R.drawable.record_alarm);
		normalRecordDrawableTop = getResources().getDrawable(
				R.drawable.record_normal);

		int[] screenArray = getResources().getIntArray(R.array.array_screen);
		if (null != screenArray) {
			int length = screenArray.length;
			screenList.clear();
			for (int i = 0; i < length; i++) {
				screenList.add(screenArray[i]);
			}
		}

		/** 中 */
		viewPager = new MyViewPager(PlayActivity.this);
		viewPager.setContext(this);
		viewPager = (MyViewPager) findViewById(R.id.play_viewpager);
		playSurface = (SurfaceView) findViewById(R.id.remotesurfaceview);
		linkMode = (TextView) findViewById(R.id.linkstate);
		playBackBar = (LinearLayout) findViewById(R.id.playbackbar);
		progressBar = (SeekBar) findViewById(R.id.playback_seekback);
		playBackPause = (Button) findViewById(R.id.playbackpause);

		linkMode.setVisibility(View.VISIBLE);

		linkState = (TextView) findViewById(R.id.playstate);// 连接文字
		loading = (ProgressBar) findViewById(R.id.videoloading);// 加载进度

		/** 竖直播放function bar */
		verPlayBarLayout = (RelativeLayout) findViewById(R.id.play_ver_func);

		decodeBtn = (Button) findViewById(R.id.decodeway);
		videTurnBtn = (Button) findViewById(R.id.overturn);
		currentKbps = (Button) findViewById(R.id.kbps);
		play_nickname = (TextView) findViewById(R.id.play_nickname);

		decodeBtn.setVisibility(View.GONE);
		videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.turnleft_down));
		currentKbps.setVisibility(View.GONE);
		voiceTip = (RelativeLayout) findViewById(R.id.voicetip);

		/** 水平播放function bar */
		horPlayBarLayout = (RelativeLayout) findViewById(R.id.play_hor_func);

		topBarH = (RelativeLayout) horPlayBarLayout.findViewById(R.id.topbarh);
		left_btn_h = (Button) horPlayBarLayout.findViewById(R.id.btn_left);// 横屏返回键
		currentMenu_h = (TextView) horPlayBarLayout
				.findViewById(R.id.currentmenu);
		right_btn_h = (Button) horPlayBarLayout.findViewById(R.id.btn_right);// 横屏手动录像，报警录像键
		right_btn_h.setVisibility(View.GONE);
		topBarH.setBackgroundColor(getResources().getColor(
				R.color.halftransparent));

		bottombut1 = (Button) findViewById(R.id.bottom_but1);
		bottombut2 = (Button) findViewById(R.id.bottom_but2);
		bottombut3 = (Button) findViewById(R.id.bottom_but3);
		bottombut4 = (Button) findViewById(R.id.bottom_but4);
		bottombut5 = (Button) findViewById(R.id.bottom_but5);
		bottombut6 = (Button) findViewById(R.id.bottom_but6);
		bottombut7 = (Button) findViewById(R.id.bottom_but7);
		bottombut8 = (Button) findViewById(R.id.bottom_but8);
		bottomStream = (TextView) findViewById(R.id.video_bq);
		relative1 = (RelativeLayout) findViewById(R.id.relative1);
		relative2 = (RelativeLayout) findViewById(R.id.relative2);
		relative3 = (RelativeLayout) findViewById(R.id.relative3);
		relative4 = (RelativeLayout) findViewById(R.id.relative4);
		relative5 = (RelativeLayout) findViewById(R.id.relative5);
		relative6 = (RelativeLayout) findViewById(R.id.relative6);
		relative7 = (RelativeLayout) findViewById(R.id.relative7);
		relative8 = (RelativeLayout) findViewById(R.id.relative8);
		linear = (LinearLayout) findViewById(R.id.linear);
		if ((disMetrics.heightPixels > 800 && disMetrics.widthPixels > 480)
				|| (disMetrics.heightPixels > 480 && disMetrics.widthPixels > 800)) {// 大屏
			bigScreen = true;
		}

		bigScreen = true;

		/** 小分辨率功能 */
		// playFuctionLayout = (LinearLayout)
		// findViewById(R.id.play_function_layout);
		// audioMonitor = (Button) findViewById(R.id.audio_monitor);// 音频监听
		// ytOperate = (Button) findViewById(R.id.yt_operate);// 云台
		// remotePlayback = (RelativeLayout)
		// findViewById(R.id.remote_playback);// 远程回放

		/** 大分辨率功能 */
		playFunctionList = (ListView) findViewById(R.id.play_function_list_layout);
		functionList.add(getResources().getString(R.string.str_audio_monitor));
		functionList.add(getResources().getString(R.string.str_yt_operate));
		functionList
				.add(getResources().getString(R.string.str_remote_playback));
		functionListAdapter = new FuntionAdapter(PlayActivity.this, bigScreen,
				playFlag);
		functionListAdapter.setData(functionList);
		playFunctionList.setAdapter(functionListAdapter);

		if (bigScreen) {
			playFunctionList.setVisibility(View.VISIBLE);
			// playFuctionLayout.setVisibility(View.GONE);
		} else {
			playFunctionList.setVisibility(View.GONE);
			// playFuctionLayout.setVisibility(View.VISIBLE);
		}

		/** 云台 布局 */
		ytLayout = (LinearLayout) findViewById(R.id.yt_layout);
		// yt_cancle = (ImageView)ytLayout.findViewById(R.id.yt_cancled);
		autoimage = (ImageView) ytLayout.findViewById(R.id.autoimage);
		zoomIn = (ImageView) ytLayout.findViewById(R.id.zoomin);
		zoomout = (ImageView) ytLayout.findViewById(R.id.zoomout);
		scaleSmallImage = (ImageView) ytLayout
				.findViewById(R.id.scaleSmallImage);
		scaleAddImage = (ImageView) ytLayout.findViewById(R.id.scaleAddImage);
		upArrow = (ImageView) ytLayout.findViewById(R.id.upArrow);
		downArrow = (ImageView) ytLayout.findViewById(R.id.downArrow);
		leftArrow = (ImageView) ytLayout.findViewById(R.id.leftArrow);
		rightArrow = (ImageView) ytLayout.findViewById(R.id.rightArrow);
		autoimage.setOnClickListener(imageOnClickListener);
		zoomIn.setOnClickListener(imageOnClickListener);
		zoomout.setOnClickListener(imageOnClickListener);
		scaleSmallImage.setOnClickListener(imageOnClickListener);
		scaleAddImage.setOnClickListener(imageOnClickListener);
		upArrow.setOnClickListener(imageOnClickListener);
		downArrow.setOnClickListener(imageOnClickListener);
		leftArrow.setOnClickListener(imageOnClickListener);
		rightArrow.setOnClickListener(imageOnClickListener);

		/** 下 */
		capture = (Button) findViewById(R.id.capture);// 抓拍
		voiceCall = (Button) findViewById(R.id.voicecall);// 喊话
		videoTape = (Button) findViewById(R.id.videotape);// 录像
		moreFeature = (Button) findViewById(R.id.more_features);// 更多

		if (JVConst.LANGUAGE_ZH == ConfigUtil.getLanguage()) {
			capture.setTextSize(12);// 抓拍
			voiceCall.setTextSize(12);// 喊话
			videoTape.setTextSize(12);// 录像
			moreFeature.setTextSize(12);
		} else {
			capture.setTextSize(10);// 抓拍
			voiceCall.setTextSize(10);// 喊话
			videoTape.setTextSize(10);// 录像
			moreFeature.setTextSize(10);
		}

		footerBar = (LinearLayout) findViewById(R.id.footbar);// 底部工具栏
		apFuncLayout = (LinearLayout) findViewById(R.id.apfunclayout);// 底部AP下一步
		nextStep = (Button) findViewById(R.id.nextstep);// 下一步

		videoTapeTop1 = getResources().getDrawable(R.drawable.video_record_1);
		videoTapeTop2 = getResources().getDrawable(R.drawable.video_record_2);

		voiceCallTop1 = getResources().getDrawable(R.drawable.voice_call_1);
		voiceCallTop2 = getResources().getDrawable(R.drawable.voice_call_2);
		setPlayViewSize();
	}

	// 云台按钮事件
	ImageView.OnClickListener imageOnClickListener = new ImageView.OnClickListener() {
		@Override
		public void onClick(View arg0) {
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setPlayViewSize();
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 横竖屏布局隐藏显示
	 */
	protected void setPlayViewSize() {
		if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation) {// 竖屏
			viewPager.setDisableSliding(false);
			getWindow()
					.setFlags(
							disMetrics.widthPixels
									- getStatusHeight(PlayActivity.this),
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
			topBar.setVisibility(View.VISIBLE);// 顶部标题栏

			if (Consts.PLAY_AP == playFlag) {
				footerBar.setVisibility(View.GONE);// 底部工具栏
				apFuncLayout.setVisibility(View.VISIBLE);
			} else {
				footerBar.setVisibility(View.VISIBLE);// 底部工具栏
				apFuncLayout.setVisibility(View.GONE);
			}

			verPlayBarLayout.setVisibility(View.VISIBLE);
			horPlayBarLayout.setVisibility(View.GONE);
			int height = disMetrics.heightPixels;
			int width = disMetrics.widthPixels;
			int useWidth = 0;
			if (height < width) {
				useWidth = height;
			} else {
				useWidth = width;
			}

			reParamsV = new RelativeLayout.LayoutParams(useWidth,
					(int) (0.75 * useWidth));
			viewPager.setLayoutParams(reParamsV);
			playSurface.setLayoutParams(reParamsV);
			decodeBtn.setVisibility(View.VISIBLE);
			currentKbps.setVisibility(View.VISIBLE);
			// [Neo] surface.step 0
			if (surfaceWidth < 0 || surfaceHeight < 0) {
				surfaceWidth = disMetrics.widthPixels;
				surfaceHeight = (int) (0.75 * disMetrics.widthPixels);
			}

		} else {// 横
			viewPager.setDisableSliding(true);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			topBar.setVisibility(View.GONE);// 顶部标题栏
			footerBar.setVisibility(View.GONE);// 底部工具栏
			apFuncLayout.setVisibility(View.GONE);
			verPlayBarLayout.setVisibility(View.GONE);

			horPlayBarLayout.setVisibility(View.VISIBLE);
			// init();
			if (Consts.PLAY_AP == playFlag) {
				bottombut6.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turnleft_down));
				videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turnleft_down));
				bottomStream.setVisibility(View.GONE);
			}
			decodeBtn.setVisibility(View.GONE);
			videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.turnleft_down));
			currentKbps.setVisibility(View.GONE);

			reParamsH = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			viewPager.setLayoutParams(reParamsH);
			playSurface.setLayoutParams(reParamsH);
			// [Neo] surface.step 0
			if (surfaceWidth < 0 || surfaceHeight < 0) {
				surfaceWidth = disMetrics.widthPixels;
				surfaceHeight = disMetrics.heightPixels;
			}

		}
	}

	/**
	 * 显示横屏功能
	 * 
	 * @param channnel
	 */
	@SuppressWarnings("deprecation")
	public void showVerFuc(Channel channel) {
		verPlayBarLayout.setVisibility(View.VISIBLE);
		horPlayBarLayout.setVisibility(View.GONE);

		// if (Consts.PLAY_AP == playFlag) {
		// currentKbps.setVisibility(View.GONE);
		// } else {
		currentKbps.setVisibility(View.VISIBLE);
		// }
		// 获取软硬解状态
		if (channel.isOMX()) {
			decodeBtn.setText(R.string.is_omx);
		} else {
			decodeBtn.setText(R.string.not_omx);
		}

		if (lowerSystem) {
			decodeBtn.setVisibility(View.GONE);
		} else {
			decodeBtn.setVisibility(View.VISIBLE);
		}

		if (Consts.PLAY_AP == playFlag) {
			rightFuncButton.setVisibility(View.GONE);
		} else {
			// 录像模式
			rightFuncButton.setTextSize(8);
			rightFuncButton
					.setTextColor(getResources().getColor(R.color.white));
			rightFuncButton.setBackgroundDrawable(null);
			if (Consts.STORAGEMODE_NORMAL == channel.getStorageMode()) {
				rightFuncButton.setText(R.string.video_normal);
				rightFuncButton.setCompoundDrawablesWithIntrinsicBounds(null,
						normalRecordDrawableTop, null, null);

				rightFuncButton.setVisibility(View.VISIBLE);

			} else if (Consts.STORAGEMODE_ALARM == channel.getStorageMode()) {
				rightFuncButton.setText(R.string.video_alarm);
				rightFuncButton.setCompoundDrawablesWithIntrinsicBounds(null,
						alarmRecordDrawableTop, null, null);
				rightFuncButton.setVisibility(View.VISIBLE);
			} else {
				rightFuncButton.setVisibility(View.GONE);
			}
		}

		if (Consts.PLAY_AP == playFlag) {
			videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.turnleft_down));
		} else {
			// 屏幕方向
			if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
				videTurnBtn.setVisibility(View.VISIBLE);
				videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ver_left_selector));
			} else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
				videTurnBtn.setVisibility(View.VISIBLE);
				videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ver_right_selector));
			} else {
				videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turnleft_down));
			}
		}

		// 码流设置
		if (-1 != channel.getStreamTag()) {
			streamAdapter.selectStream = channel.getStreamTag() - 1;
			if (streamAdapter.selectStream < streamArray.length) {
				streamAdapter.notifyDataSetChanged();
				moreFeature.setText(streamArray[channel.getStreamTag() - 1]);

				bottomStream.setText(streamArray[channel.getStreamTag() - 1]);
			}
		}
	}

	public void showFunc(Channel channel, int screen, int index) {

		if (index != channel.getIndex()) {
			return;
		}

		if (screen > 1 || !channel.isConnected()) {
			rightFuncButton.setVisibility(View.GONE);
			right_btn_h.setVisibility(View.GONE);
			verPlayBarLayout.setVisibility(View.GONE);
			horPlayBarLayout.setVisibility(View.GONE);
		} else {
			if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation) {// 竖屏
				showVerFuc(channel);
			} else {
				showHorFuc(channel);
			}
		}
	}

	/**
	 * 显示横屏功能
	 * 
	 * @param channnel
	 */
	@SuppressWarnings("deprecation")
	public void showHorFuc(Channel channel) {
		verPlayBarLayout.setVisibility(View.GONE);
		horPlayBarLayout.setVisibility(View.VISIBLE);

		// 获取软硬解状态
		if (channel.isOMX()) {
			bottombut2.setText(R.string.is_omx);
		} else {
			bottombut2.setText(R.string.not_omx);
		}

		if (lowerSystem) {
			bottombut2.setVisibility(View.GONE);
		} else {
			bottombut2.setVisibility(View.VISIBLE);
		}

		if (Consts.PLAY_AP == playFlag) {
			right_btn_h.setVisibility(View.GONE);
		} else {
			// 录像模式
			right_btn_h.setTextSize(8);
			right_btn_h.setTextColor(getResources().getColor(R.color.white));
			right_btn_h.setBackgroundDrawable(null);
			if (Consts.STORAGEMODE_NORMAL == channel.getStorageMode()) {
				right_btn_h.setText(R.string.video_normal);
				right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
						normalRecordDrawableTop, null, null);
				right_btn_h.setVisibility(View.VISIBLE);
			} else if (Consts.STORAGEMODE_ALARM == channel.getStorageMode()) {
				right_btn_h.setText(R.string.video_alarm);
				right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
						alarmRecordDrawableTop, null, null);
				right_btn_h.setVisibility(View.VISIBLE);
			} else {
				right_btn_h.setVisibility(View.GONE);
			}

		}

		if (Consts.PLAY_AP == playFlag) {
			bottombut6.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.turnleft_down));
			videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.turnleft_down));
		} else {
			// 屏幕方向
			if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
				relative6.setVisibility(View.VISIBLE);
				bottombut6.setVisibility(View.VISIBLE);
				bottombut6.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turn_left_selector));
			} else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
				relative6.setVisibility(View.VISIBLE);
				bottombut6.setVisibility(View.VISIBLE);
				bottombut6.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turn_right_selector));
			} else {
				bottombut6.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turnleft_down));
				videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.turnleft_down));
			}
		}

		// 码流设置
		if (-1 != channel.getStreamTag()) {
			streamAdapter.selectStream = channel.getStreamTag() - 1;
			if (streamAdapter.selectStream < streamArray.length) {
				streamAdapter.notifyDataSetChanged();
				moreFeature.setText(streamArray[channel.getStreamTag() - 1]);
				bottomStream.setText(streamArray[channel.getStreamTag() - 1]);
			}
		}
	}

	/**
	 * 清空所有状态
	 */
	@SuppressWarnings("deprecation")
	public void resetFunc(Channel channel) {
		try {
			verPlayBarLayout.setVisibility(View.GONE);
			bottombut6.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.turnleft_down));
			videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.turnleft_down));
			right_btn_h.setVisibility(View.GONE);// 录像模式
			moreFeature.setText(R.string.default_stream);// 码流
			bottomStream.setText(R.string.default_stream);
			// 停止音频监听
			if (PlayUtil.isPlayAudio(channel.getIndex())) {
				PlayUtil.audioPlay(channel.getIndex());
				functionListAdapter.selectIndex = -1;
				bottombut8.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.video_monitor_icon));
				functionListAdapter.notifyDataSetChanged();
				if (null != playAudio) {
					playAudio.interrupt();
					playAudio = null;
				}
			}

			// 正在录像停止录像
			if (PlayUtil.checkRecord(channel.getIndex())) {
				if (!PlayUtil.videoRecord(channel.getIndex())) {// 打开
					showTextToast(Consts.VIDEO_PATH);
					tapeSelected(false);
				}
			}

			// 停止对讲
			if (channel.isVoiceCall()) {
				if (null != recorder) {
					recorder.stop();
				}
				if (null != audioQueue) {
					audioQueue.clear();
				}
				channel.setVoiceCall(false);
				realStop = true;
				voiceCallSelected(false);
				PlayUtil.stopVoiceCall(channel.getIndex());
			}

			tapeSelected(false);
			recorder.stop();
			functionListAdapter.selectIndex = -1;
			functionListAdapter.notifyDataSetChanged();
			voiceCallSelected(false);

			AUDIO_SINGLE = false;// 单向对讲标志
			VOICECALL_LONG_CLICK = false;// 语音喊话flag长按状态,长按发送数据
			VOICECALLING = false;// 对讲功能已经开启
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 刷新IPC状态显示
	// *
	// * @param channel
	// */
	// @SuppressWarnings("deprecation")
	// protected void refreshIPCFun(Channel channel) {
	// currentKbps.setVisibility(View.VISIBLE);
	// // 获取软硬解状态
	// if (channel.isOMX()) {
	// decodeBtn.setText(R.string.is_omx);
	// bottombut2.setText(R.string.is_omx);
	// } else {
	// decodeBtn.setText(R.string.not_omx);
	// bottombut2.setText(R.string.not_omx);
	// }
	//
	// if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
	// 横屏
	// decodeBtn.setVisibility(View.GONE);
	// } else {
	// decodeBtn.setVisibility(View.VISIBLE);
	// }
	//
	// if (lowerSystem) {
	// bottombut2.setVisibility(View.GONE);
	// decodeBtn.setVisibility(View.GONE);
	// }
	//
	// // 录像模式
	// if (Consts.STORAGEMODE_NORMAL == channel.getStorageMode()) {
	// rightFuncButton.setText(R.string.video_normal);
	// rightFuncButton.setCompoundDrawablesWithIntrinsicBounds(null,
	// normalRecordDrawableTop, null, null);
	// rightFuncButton.setTextSize(8);
	// rightFuncButton
	// .setTextColor(getResources().getColor(R.color.white));
	// rightFuncButton.setBackgroundDrawable(null);
	//
	// right_btn_h.setText(R.string.video_normal);
	// right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
	// normalRecordDrawableTop, null, null);
	// right_btn_h.setTextSize(8);
	// right_btn_h.setTextColor(getResources().getColor(R.color.white));
	// right_btn_h.setBackgroundDrawable(null);
	//
	// if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
	// 横屏
	// right_btn_h.setVisibility(View.VISIBLE);
	// rightFuncButton.setVisibility(View.GONE);
	// } else {
	// right_btn_h.setVisibility(View.GONE);
	// rightFuncButton.setVisibility(View.VISIBLE);
	// }
	//
	// } else if (Consts.STORAGEMODE_ALARM == channel.getStorageMode()) {
	// rightFuncButton.setText(R.string.video_alarm);
	// rightFuncButton.setCompoundDrawablesWithIntrinsicBounds(null,
	// alarmRecordDrawableTop, null, null);
	// rightFuncButton.setTextSize(8);
	// rightFuncButton
	// .setTextColor(getResources().getColor(R.color.white));
	// rightFuncButton.setBackgroundDrawable(null);
	//
	// right_btn_h.setText(R.string.video_alarm);
	// right_btn_h.setCompoundDrawablesWithIntrinsicBounds(null,
	// alarmRecordDrawableTop, null, null);
	// right_btn_h.setTextSize(8);
	// right_btn_h.setTextColor(getResources().getColor(R.color.white));
	// right_btn_h.setBackgroundDrawable(null);
	//
	// if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
	// 横屏
	// right_btn_h.setVisibility(View.VISIBLE);
	// rightFuncButton.setVisibility(View.GONE);
	// } else {
	// right_btn_h.setVisibility(View.GONE);
	// rightFuncButton.setVisibility(View.VISIBLE);
	// }
	// } else {
	// rightFuncButton.setVisibility(View.GONE);
	// right_btn_h.setVisibility(View.GONE);
	// }
	//
	// // 屏幕方向
	// if (Consts.SCREEN_NORMAL == channel.getScreenTag()) {
	// videTurnBtn.setVisibility(View.VISIBLE);
	// videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.turn_left_selector));
	// relative6.setVisibility(View.VISIBLE);
	// bottombut6.setVisibility(View.VISIBLE);
	// bottombut6.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.turn_left_selector));
	// if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
	// 横屏
	// relative6.setVisibility(View.VISIBLE);
	// videTurnBtn.setVisibility(View.GONE);
	// } else {
	// videTurnBtn.setVisibility(View.VISIBLE);
	// }
	// } else if (Consts.SCREEN_OVERTURN == channel.getScreenTag()) {
	// videTurnBtn.setVisibility(View.VISIBLE);
	// videTurnBtn.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.turn_right_selector));
	// relative6.setVisibility(View.VISIBLE);
	// bottombut6.setVisibility(View.VISIBLE);
	// bottombut6.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.turn_right_selector));
	// if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {//
	// 横屏
	// relative6.setVisibility(View.VISIBLE);
	// videTurnBtn.setVisibility(View.GONE);
	// } else {
	// videTurnBtn.setVisibility(View.VISIBLE);
	// }
	// } else {
	// if (relative6.getVisibility() == View.VISIBLE) {
	// linear.removeView(relative6);
	// linear.addView(relative6, linear.getChildCount());
	// bottombut6.setVisibility(View.GONE);
	// videTurnBtn.setVisibility(View.GONE);
	// }
	// }
	// // 码流设置
	// if (-1 != channel.getStreamTag()) {
	// streamAdapter.selectStream = channel.getStreamTag() - 1;
	// streamAdapter.notifyDataSetChanged();
	// moreFeature.setText(streamArray[channel.getStreamTag() - 1]);
	// bottomStream.setText(streamArray[channel.getStreamTag() - 1]);
	// }
	// }

	/**
	 * 录像
	 * 
	 * @param selected
	 */
	@SuppressWarnings("deprecation")
	protected void tapeSelected(Boolean selected) {
		if (selected) {// 选中
			// videoTape.setTextColor(getResources().getColor(
			// R.color.functionbtncolor2));

			videoTape.setCompoundDrawablesWithIntrinsicBounds(null,
					videoTapeTop2, null, null);
			bottombut7.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_record1));
		} else {
			// videoTape.setTextColor(getResources().getColor(
			// R.color.functionbtncolor1));

			videoTape.setCompoundDrawablesWithIntrinsicBounds(null,
					videoTapeTop1, null, null);
			bottombut7.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_record));
		}

		recoding = selected;
	}

	/**
	 * 对讲
	 * 
	 * @param selected
	 */
	@SuppressWarnings("deprecation")
	protected void voiceCallSelected(Boolean selected) {
		if (selected) {// 选中
			// voiceCall.setTextColor(getResources().getColor(
			// R.color.functionbtncolor2));

			voiceCall.setCompoundDrawablesWithIntrinsicBounds(null,
					voiceCallTop2, null, null);
			bottombut5.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_talkselect_icon));

		} else {
			// voiceCall.setTextColor(getResources().getColor(
			// R.color.functionbtncolor1));

			voiceCall.setCompoundDrawablesWithIntrinsicBounds(null,
					voiceCallTop1, null, null);
			bottombut5.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.video_talkback_icon));

		}
	}

	/**
	 * 显示云台功能
	 */
	protected void showPTZ() {
		if (View.GONE == ytLayout.getVisibility()) {
			ytLayout.setVisibility(View.VISIBLE);
			playFunctionList.setVisibility(View.GONE);
			// playFuctionLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 音频监听时初始化audio和队列
	 */
	protected void initAudio(int audioByte) {
		if (null == audioQueue) {
			audioQueue = new LinkedBlockingQueue<byte[]>();
		}

		if (null != playAudio) {
			playAudio.interrupt();
			playAudio = null;
		}

		playAudio = new PlayAudio(audioQueue, audioByte);
		playAudio.start();

	}

	/**
	 * 停止对讲，音频监听和录像功能
	 * 
	 * @param index
	 */
	protected void stopAll(int index, Channel channel) {
		if (PlayUtil.checkRecord(index)) {// 正在录像，停止录像
			PlayUtil.stopVideoTape();
			showTextToast(getResources().getString(R.string.str_stop_record)
					+ Consts.SD_CARD_PATH
					+ getResources().getString(R.string.str_video_path));
			tapeSelected(false);
		}

		if (PlayUtil.isPlayAudio(index)) {// 正在音频监听，停止监听
			PlayUtil.stopAudioMonitor(index);
			recorder.stop();
			functionListAdapter.selectIndex = -1;
			functionListAdapter.notifyDataSetChanged();
		}

		if (null != channel && channel.isVoiceCall()) {
			PlayUtil.stopVoiceCall(index);
			channel.setVoiceCall(false);
			realStop = true;
			voiceCallSelected(false);
		}
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void onPause() {
		dismissDialog();
		super.onPause();
	}

	@Override
	protected void freeMe() {
		if (null != recorder) {
			recorder.stop();
		}
		if (null != audioQueue) {
			audioQueue.clear();
		}
		if (null != playAudio) {
			playAudio.interrupt();
			playAudio = null;
		}

	}

	public void onFlip(View view) {

	}

	// protected void init() {
	// new CountDownTimer(6 * 1000, 2 * 1000) {
	//
	// @Override
	// public void onTick(long millisUntilFinished) {
	//
	// }
	//
	// @Override
	// public void onFinish() {
	// horPlayBarLayout.setVisibility(View.GONE);
	// }
	// }.start();
	// }
}
