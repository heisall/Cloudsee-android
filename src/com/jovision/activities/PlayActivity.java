package com.jovision.activities;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
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
import com.jovision.adapters.PlayViewPagerAdapter;
import com.jovision.adapters.ScreenAdapter;
import com.jovision.adapters.StreamAdapter;
import com.jovision.audio.MICRecorder;
import com.jovision.audio.PlayAudio;
import com.jovision.bean.Channel;
import com.jovision.commons.MyLog;
import com.jovision.utils.PlayUtil;
import com.jovision.views.MyViewPager;

public class PlayActivity extends BaseActivity {

	private final String TAG = "PLAY_BASE";

	protected boolean bigScreen = false;// 大小屏标识

	protected boolean isOmx = false;

	/** 播放相关 */
	protected RelativeLayout.LayoutParams reParamsV;
	protected RelativeLayout.LayoutParams reParamsH;
	protected int surfaceWidth = -1;
	protected int surfaceHeight = -1;
	protected int playFlag = -1;

	/** layout 上 */
	protected LinearLayout topBar;// 顶部标题栏
	protected LinearLayout topBartwo;// 顶部标题栏
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
	protected PlayViewPagerAdapter pagerAdapter;
	protected SurfaceView playSurface;
	protected TextView linkMode;// 测试显示连接方式

	protected SeekBar progressBar;// 远程回放进度

	/** 远程回放连接状态 */
	protected TextView linkState;// 连接文字
	protected ProgressBar loading;// 加载进度

	protected Button audioMonitor;// 音频监听
	protected Button ytOperate;// 云台
	protected Button remotePlayback;// 远程回放
	protected LinearLayout ytLayout;// 云台布局
	protected LinearLayout playFuctionLayout;// 小分辨率时功能界面
	protected ListView playFunctionList;// 大分辨率时功能列表
	protected FuntionAdapter functionListAdapter;
	protected ArrayList<String> functionList = new ArrayList<String>();

	/** 云台操作 */
	protected ImageView autoimage, zoomIn, zoomout, scaleSmallImage,
			scaleAddImage, upArrow, downArrow, leftArrow, rightArrow;

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
	protected Drawable videoTapeLeft1 = null;
	protected Drawable videoTapeLeft2 = null;
	/** 对讲按钮 */
	protected Drawable voiceCallLeft1 = null;
	protected Drawable voiceCallLeft2 = null;

	protected PlayAudio playAudio;// 音频监听
	protected LinkedBlockingQueue<byte[]> audioQueue;

	protected MICRecorder recorder;// 音频采集

	protected RelativeLayout bottom;

	protected ImageView bottombut1;
	protected ImageView bottombut2;
	protected ImageView bottombut3;
	protected ImageView bottombut4;
	protected ImageView bottombut5;
	protected ImageView bottombut6;
	protected ImageView bottombut7;
	protected ImageView bottombut8;
	protected boolean bottomboolean1;
	protected boolean bottomboolean2;
	protected boolean bottomboolean3;
	protected boolean bottomboolean4;
	protected boolean bottomboolean5;
	protected boolean bottomboolean6;
	protected boolean bottomboolean7;
	protected boolean bottomboolean8;

	/** IPC独有特性 */
	protected Button decodeBtn;
	protected Button videTurnBtn;// 视频翻转
	// 录像模式----rightFuncButton
	// 码流切换----moreFeature
	protected String[] streamArray;
	protected ListView streamListView;// 码流listview
	protected StreamAdapter streamAdapter;// 码流adapter
	protected RelativeLayout voiceTip;// 单向对讲提示

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

		recorder = MICRecorder.getInstance();

		/** 上 */
		topBar = (LinearLayout) findViewById(R.id.top_bar);// 顶部标题栏
		topBartwo = (LinearLayout) findViewById(R.id.top_bartwo);// 顶部标题栏
		back = (Button) findViewById(R.id.btn_left);
		rightFuncButton = (Button) findViewById(R.id.btn_right);

		currentMenu = (TextView) findViewById(R.id.currentmenu);
		selectScreenNum = (ImageView) findViewById(R.id.selectscreen);

		int[] screenArray = getResources().getIntArray(R.array.array_screen);
		if (null != screenArray) {
			int length = screenArray.length;
			screenList.clear();
			for (int i = 0; i < length; i++) {
				screenList.add(screenArray[i]);
			}
		}

		/** 中 */
		viewPager = (MyViewPager) findViewById(R.id.viewpager);
		playSurface = (SurfaceView) findViewById(R.id.remotesurfaceview);
		linkMode = (TextView) findViewById(R.id.linkstate);
		progressBar = (SeekBar) findViewById(R.id.playback_seekback);
		linkMode.setVisibility(View.VISIBLE);

		linkState = (TextView) findViewById(R.id.playstate);// 连接文字
		loading = (ProgressBar) findViewById(R.id.videoloading);// 加载进度

		decodeBtn = (Button) findViewById(R.id.decodeway);
		videTurnBtn = (Button) findViewById(R.id.overturn);
		voiceTip = (RelativeLayout) findViewById(R.id.voicetip);

		/** 下 */

		bottom = (RelativeLayout) findViewById(R.id.bottom);
		bottombut1 = (ImageView) findViewById(R.id.bottom_but1);
		bottombut2 = (ImageView) findViewById(R.id.bottom_but2);
		bottombut3 = (ImageView) findViewById(R.id.bottom_but3);
		bottombut4 = (ImageView) findViewById(R.id.bottom_but4);
		bottombut5 = (ImageView) findViewById(R.id.bottom_but5);
		bottombut6 = (ImageView) findViewById(R.id.bottom_but6);
		bottombut7 = (ImageView) findViewById(R.id.bottom_but7);
		bottombut8 = (ImageView) findViewById(R.id.bottom_but8);

		if ((disMetrics.heightPixels > 800 && disMetrics.widthPixels > 480)
				|| (disMetrics.heightPixels > 480 && disMetrics.widthPixels > 800)) {// 大屏
			bigScreen = true;
		}

		bigScreen = true;

		/** 小分辨率功能 */
		playFuctionLayout = (LinearLayout) findViewById(R.id.play_function_layout);
		audioMonitor = (Button) findViewById(R.id.audio_monitor);// 音频监听
		ytOperate = (Button) findViewById(R.id.yt_operate);// 云台
		remotePlayback = (Button) findViewById(R.id.remote_playback);// 远程回放

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
			playFuctionLayout.setVisibility(View.GONE);
		} else {
			playFunctionList.setVisibility(View.GONE);
			playFuctionLayout.setVisibility(View.VISIBLE);
		}

		/** 云台 布局 */
		ytLayout = (LinearLayout) findViewById(R.id.yt_layout);
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

		footerBar = (LinearLayout) findViewById(R.id.footbar);// 底部工具栏
		apFuncLayout = (LinearLayout) findViewById(R.id.apfunclayout);// 底部AP下一步
		nextStep = (Button) findViewById(R.id.nextstep);// 下一步

		videoTapeLeft1 = getResources().getDrawable(R.drawable.video_record_1);
		videoTapeLeft2 = getResources().getDrawable(R.drawable.video_record_2);

		voiceCallLeft1 = getResources().getDrawable(R.drawable.voice_call_1);
		voiceCallLeft2 = getResources().getDrawable(R.drawable.voice_call_2);
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
			getWindow()
					.setFlags(
							disMetrics.widthPixels
									- getStatusHeight(PlayActivity.this),
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
			topBar.setVisibility(View.VISIBLE);// 顶部标题栏
			footerBar.setVisibility(View.VISIBLE);// 底部工具栏

			if (Consts.PLAY_AP == playFlag) {
				apFuncLayout.setVisibility(View.VISIBLE);
			} else {
				apFuncLayout.setVisibility(View.GONE);
			}

			bottom.setVisibility(View.GONE);
			topBartwo.setVisibility(View.GONE);
			reParamsV = new RelativeLayout.LayoutParams(disMetrics.widthPixels,
					(int) (0.75 * disMetrics.widthPixels));
			MyLog.e(TAG, "v-w=" + disMetrics.widthPixels + ";v-h="
					+ (int) (0.75 * disMetrics.widthPixels));
			viewPager.setLayoutParams(reParamsV);
			playSurface.setLayoutParams(reParamsV);

			// [Neo] surface.step 0
			if (surfaceWidth < 0 || surfaceHeight < 0) {
				surfaceWidth = disMetrics.widthPixels;
				surfaceHeight = (int) (0.75 * disMetrics.widthPixels);
			}

		} else {// 横
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			topBar.setVisibility(View.GONE);// 顶部标题栏
			footerBar.setVisibility(View.GONE);// 底部工具栏
			apFuncLayout.setVisibility(View.GONE);
			bottom.setVisibility(View.VISIBLE);
			topBartwo.setVisibility(View.VISIBLE);
			init();

			reParamsH = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			MyLog.e(TAG, "h-w=" + disMetrics.heightPixels + ";h-h="
					+ disMetrics.widthPixels);
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
	 * 录像
	 * 
	 * @param selected
	 */
	@SuppressWarnings("deprecation")
	protected void tapeSelected(Boolean selected) {
		if (selected) {// 选中
			videoTape.setTextColor(getResources().getColor(
					R.color.functionbtncolor2));
			videoTape.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.function_btn_bg_2));
			videoTape.setCompoundDrawablesWithIntrinsicBounds(videoTapeLeft2,
					null, null, null);
		} else {
			videoTape.setTextColor(getResources().getColor(
					R.color.functionbtncolor1));
			videoTape.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.function_btn_bg_1));
			videoTape.setCompoundDrawablesWithIntrinsicBounds(videoTapeLeft1,
					null, null, null);
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
			voiceCall.setTextColor(getResources().getColor(
					R.color.functionbtncolor2));
			voiceCall.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.function_btn_bg_2));
			voiceCall.setCompoundDrawablesWithIntrinsicBounds(voiceCallLeft2,
					null, null, null);
		} else {
			voiceCall.setTextColor(getResources().getColor(
					R.color.functionbtncolor1));
			voiceCall.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.function_btn_bg_1));
			voiceCall.setCompoundDrawablesWithIntrinsicBounds(voiceCallLeft1,
					null, null, null);
		}
	}

	/**
	 * 显示云台功能
	 */
	protected void showPTZ() {
		if (View.GONE == ytLayout.getVisibility()) {
			ytLayout.setVisibility(View.VISIBLE);
			playFunctionList.setVisibility(View.GONE);
			playFuctionLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 音频监听时初始化audio和队列
	 */
	protected void initAudio() {
		if (null == audioQueue) {
			audioQueue = new LinkedBlockingQueue<byte[]>();
		}
		if (null == playAudio) {
			playAudio = new PlayAudio(audioQueue);
			playAudio.start();
		}
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
		}

	}

	public void onFlip(View view) {

	}

	protected void init() {
		new CountDownTimer(3000, 2000) {

			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				bottom.setVisibility(View.GONE);
				topBartwo.setVisibility(View.GONE);
			}
		}.start();
	}
}
