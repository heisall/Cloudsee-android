package com.jovision.activities;

import java.util.HashMap;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.MyAudio;
import com.jovision.commons.MyGestureDispatcher;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.CustomShareBoard;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class JVWebView2Activity extends BaseActivity implements
		PlayWindowManager.OnUiListener {

	private static final String TAG = "JVWebView2Activity";

	/** umeng share **/
	private static final String DESCRIPTOR = "com.umeng.share";
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService(DESCRIPTOR);
	private boolean mIsShare;
	private UMVideo mWeixinVideo, mCircleVideo, mWeiboVideo;

	/** topBar **/
	private RelativeLayout topBar;
	protected RelativeLayout.LayoutParams reParamsV;
	protected RelativeLayout.LayoutParams reParamsH;
	protected RelativeLayout.LayoutParams reParamstop1;
	protected RelativeLayout.LayoutParams reParamstop2;

	private RelativeLayout demoLayout;
	private RelativeLayout playLayout;
	private SurfaceView playSurfaceView;

	private ProgressBar loadingVideoBar;
	private TextView loadingState;
	private TextView linkSpeed;
	private ImageView playImgView;
	private RelativeLayout playBar;
	private ImageView pause;
	private ImageView fullScreen;
	private WebView webView;
	private LinearLayout linkSetting;
	private EditText minCache;
	private EditText desCache;
	private Button saveSetting;

	private boolean fullScreenFlag = false;
	private boolean pausedFlag = false;
	private MyAudio playAudio;
	private int audioByte = 0;
	private Channel playChannel;
	private Timer doubleClickTimer;
	private boolean isDoubleClickCheck;// 双击事件

	private String url = "";
	private String rtmp = "";
	private int titleID = 0;
	private ImageView loadingBar;
	private LinearLayout loadinglayout;

	private boolean isDisConnected = false;// 断开成功标志
	private boolean manuPause = false;// 人为暂停
	private boolean onPause = false;// onPause

	private RelativeLayout loadFailedLayout;
	private ImageView reloadImgView;
	private boolean loadFailed = false;

	private RelativeLayout zhezhaoLayout;

	private int connectRes3 = 0;
	private int connectRes4 = 0;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_NET_ERROR_DISCONNECT: {
			startConnect(rtmp, playChannel.getSurface());
			break;
		}
		case Consts.WHAT_DEMO_BUFFING: {// 缓存中
			loadingState(Consts.RTMP_CONN_SCCUESS);
			break;
		}
		case Consts.WHAT_DEMO_RESUME: {// resume Channel
			resumeVideo();
			break;
		}
		case Consts.CALL_CONNECT_CHANGE: {

			if (arg2 == Consts.BAD_NOT_CONNECT) {
				connectRes3 = arg2;
				MyLog.v("reConnect2", "connectRes3=" + connectRes3
						+ ";connectRes4=" + connectRes4);
			} else if (arg2 == Consts.RTMP_EDISCONNECT) {
				connectRes4 = arg2;
				MyLog.v("reConnect1", "connectRes3=" + connectRes3
						+ ";connectRes4=" + connectRes4);
			}
			if (connectRes4 == Consts.RTMP_EDISCONNECT
					&& connectRes3 == Consts.BAD_NOT_CONNECT) {
				MyLog.v("reConnect3", "connectRes3=" + connectRes3
						+ ";connectRes4=" + connectRes4);
				loadingState(Consts.RTMP_CONN_SCCUESS);
				connectRes3 = 0;
				connectRes4 = 0;
				handler.sendMessageDelayed(
						handler.obtainMessage(Consts.WHAT_NET_ERROR_DISCONNECT),
						200);
			} else {
				MyLog.v("reConnect0", "connectChange=" + arg2);
				loadingState(arg2);
			}
			break;
		}
		case Consts.CALL_NEW_PICTURE: {
			pause.setImageDrawable(getResources().getDrawable(
					R.drawable.video_stop_icon));
			linkSpeed.setVisibility(View.VISIBLE);
			loadingState(Consts.CALL_NEW_PICTURE);
			break;
		}

		case Consts.CALL_PLAY_BUFFER: {
			if (arg2 > 0) {
				// MyLog.i(Consts.TAG_PLAY, "buffering: " + arg2);// 0-99
				bufferingState(Consts.TAG_PLAY_BUFFERING, arg2);
			} else if (Consts.BUFFER_START == arg2) {
				// MyLog.w(Consts.TAG_PLAY, "buffer started");// show
				bufferingState(Consts.TAG_PLAY_BUFFERING, 0);
			} else if (Consts.BUFFER_FINISH == arg2) {
				// MyLog.w(Consts.TAG_PLAY, "buffer finished");// dismiss
				bufferingState(Consts.TAG_PLAY_BUFFERED, 0);
			}
			break;
		}
		case Consts.WHAT_SURFACEVIEW_CLICK: {// 单击事件
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

				if (isDoubleClickCheck) {// 双击

					Point vector = new Point();
					Point middle = new Point();
					middle.set(x, y);
					if (originSize) {// 双击放大
						vector.set(channel.getSurfaceView().getWidth(), channel
								.getSurfaceView().getHeight());
						gestureOnView(channel.getSurfaceView(), channel,
								MyGestureDispatcher.GESTURE_TO_BIGGER, 1,
								vector, middle);
					} else {// 双击还原
						vector.set(-channel.getSurfaceView().getWidth(),
								-channel.getSurfaceView().getHeight());
						gestureOnView(channel.getSurfaceView(), channel,
								MyGestureDispatcher.GESTURE_TO_SMALLER, -1,
								vector, middle);
					}
				} else {// 单击

					if (View.VISIBLE == playBar.getVisibility()) {
						playBar.setVisibility(View.GONE);
						if (MySharedPreference.getBoolean(Consts.MORE_LITTLE)) {// 调试版本
							linkSetting.setVisibility(View.GONE);
						}
					} else {
						playBar.setVisibility(View.VISIBLE);
						if (MySharedPreference.getBoolean(Consts.MORE_LITTLE)) {// 调试版本
							linkSetting.setVisibility(View.VISIBLE);
						}
					}
				}
			}

			lastClickTime = 0;
			break;
		}

		case Consts.CALL_STAT_REPORT: {
			try {
				// MyLog.e(Consts.TAG_PLAY, obj.toString());
				JSONArray array = new JSONArray(obj.toString());
				JSONObject object = null;

				int size = array.length();
				for (int i = 0; i < size; i++) {
					object = array.getJSONObject(i);
					linkSpeed.setText(String.format("%.1fk/%.1fk",
							object.getDouble("kbps"),
							object.getDouble("audio_kbps")));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		}

		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// MyLog.i(TAG, "onNotify: " + what + ", " + arg1 + ", " + arg2
		// + ", " + obj);
		switch (what) {
		case Consts.CALL_STAT_REPORT: {
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}

		case Consts.CALL_CONNECT_CHANGE: {
			playChannel.setConnecting(false);
			switch (arg2) {
			case Consts.BAD_NOT_CONNECT: {
				isDisConnected = true;
				MyLog.e("BAD_NOT_CONNECT", "-3");
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}
			case Consts.RTMP_CONN_SCCUESS: {
				playChannel.setConnected(true);
				Jni.enablePlayAudio(playChannel.getIndex(), true);

				if (playChannel.isPaused()) {
					Jni.resume(playChannel.getIndex(), playChannel.getSurface());
				}
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}
			case Consts.RTMP_CONN_FAILED: {
				playChannel.setConnected(false);
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}
			case Consts.RTMP_DISCONNECTED: {
				playChannel.setConnected(false);
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}
			case Consts.RTMP_EDISCONNECT: {
				playChannel.setConnected(false);
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}
			}
			break;
		}
		case Consts.CALL_NEW_PICTURE: {
			startAudio(playChannel.getIndex(), audioByte);
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}
		case Consts.CALL_NORMAL_DATA: {
			try {
				JSONObject jobj;
				jobj = new JSONObject(obj.toString());
				if (null != jobj) {
					audioByte = jobj.optInt("audio_bit");
					startAudio(playChannel.getIndex(), audioByte);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}
		case Consts.CALL_PLAY_AUDIO: {// 音频数据

			if (null != obj && null != playAudio) {
				byte[] data = (byte[]) obj;
				// audioQueue.offer(data);
				// [Neo] 将音频填入缓存队列
				playAudio.put(data);
			}

			break;
		}
		default: {
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}
		}
	}

	@Override
	protected void initSettings() {
		url = getIntent().getStringExtra("URL");
		// url = "http://app.ys7.com/";
		// url = "http://192.168.9.83:8080/m2obile/index.html";
		titleID = getIntent().getIntExtra("title", 0);
		rtmp = getIntent().getStringExtra("rtmp");
		String cloudNum = getIntent().getStringExtra("cloudnum");
		String channel = getIntent().getStringExtra("channel");

		Device dev = new Device();
		playChannel = new Channel(dev, 1, Integer.parseInt(channel), false,
				false, cloudNum + "_" + channel);

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
		reParamstop1 = new RelativeLayout.LayoutParams(useWidth,
				(int) (0.75 * useWidth));
		reParamstop2 = new RelativeLayout.LayoutParams(useWidth,
				(int) (0.5 * useWidth));
		reParamsH = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		// 判断是否展示分享功能
		mIsShare = checkShareEnabled(url);
		if (mIsShare) {
			MyLog.v(TAG, "share is enable");
			// 配置需要分享的相关平台
			configPlatforms();
			// 设置分享的内容
			setShareContent();
		}
	}

	/**
	 * 应用层开启音频监听功能
	 * 
	 * @param index
	 * @return
	 */
	private boolean startAudio(int index, int audioByte) {
		boolean open = false;
		if (PlayUtil.isPlayAudio(index)) {// 正在监听,确保不会重复开启
			open = true;
		} else {
			PlayUtil.startAudioMonitor(index);// enable audio
			playAudio.startPlay(audioByte, true);
			open = true;
		}
		return open;
	}

	/**
	 * 应用层关闭音频监听功能
	 * 
	 * @param index
	 * @return
	 */
	private boolean stopAudio(int index) {
		boolean close = false;
		if (PlayUtil.isPlayAudio(index)) {// 正在监听，停止监听
			PlayUtil.stopAudioMonitor(index);// stop audio
			playAudio.stopPlay();
			close = true;
		} else {// 确保不会重复关闭
			close = true;
		}
		return close;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.webview2_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		playAudio = MyAudio.getIntance(Consts.PLAY_AUDIO_WHAT,
				JVWebView2Activity.this, 8000);

		/** topBar **/
		topBar = (RelativeLayout) findViewById(R.id.topbarh);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.demo);
		zhezhaoLayout = (RelativeLayout) findViewById(R.id.zhezhao);
		zhezhaoLayout.setLayoutParams(reParamstop1);
		loadingBar = (ImageView) findViewById(R.id.loadingbar);
		loadinglayout = (LinearLayout) findViewById(R.id.loadinglayout);
		linkSetting = (LinearLayout) findViewById(R.id.linksetting);
		minCache = (EditText) findViewById(R.id.mincache);
		desCache = (EditText) findViewById(R.id.descache);
		saveSetting = (Button) findViewById(R.id.savesetting);
		saveSetting.setOnClickListener(myOnClickListener);

		if (MySharedPreference.getBoolean(Consts.MORE_LITTLE)) {// 调试版本
			linkSetting.setVisibility(View.VISIBLE);
		} else {
			linkSetting.setVisibility(View.GONE);
		}
		loadFailedLayout = (RelativeLayout) findViewById(R.id.loadfailedlayout);
		loadFailedLayout.setVisibility(View.GONE);
		reloadImgView = (ImageView) findViewById(R.id.refreshimg);
		reloadImgView.setOnClickListener(myOnClickListener);

		if (-1 == titleID) {
			currentMenu.setText("");
		} else if (-2 == titleID) {

		} else {
			currentMenu.setText(titleID);
		}

		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);

		if (mIsShare) {
			// 分享的场合，更改标题栏右上角的图标
			rightBtn.setBackgroundResource(R.drawable.share);
			rightBtn.setOnClickListener(myOnClickListener);
		}

		demoLayout = (RelativeLayout) findViewById(R.id.demolayout);
		playLayout = (RelativeLayout) findViewById(R.id.playlayout);
		playSurfaceView = (SurfaceView) findViewById(R.id.playsurface);
		loadingVideoBar = (ProgressBar) findViewById(R.id.videoloading);
		loadingState = (TextView) findViewById(R.id.playstate);
		playImgView = (ImageView) findViewById(R.id.playview);
		playBar = (RelativeLayout) findViewById(R.id.playbar);
		linkSpeed = (TextView) findViewById(R.id.linkspeed);
		pause = (ImageView) findViewById(R.id.pause);
		fullScreen = (ImageView) findViewById(R.id.fullscreen);
		webView = (WebView) findViewById(R.id.webview);

		pause.setOnClickListener(myOnClickListener);
		fullScreen.setOnClickListener(myOnClickListener);
		playImgView.setOnClickListener(myOnClickListener);

		setSurfaceSize(false);
		fullScreenFlag = false;
		playChannel.setSurfaceView(playSurfaceView);

		final MyGestureDispatcher dispatcher = new MyGestureDispatcher(
				new MyGestureDispatcher.OnGestureListener() {

					@Override
					public void onGesture(int gesture, int distance,
							Point vector, Point middle) {
						if (null != playChannel && playChannel.isConnected()
								&& !playChannel.isConnecting()) {
							// boolean originSize = false;
							// if (playChannel.getLastPortWidth() == playChannel
							// .getSurfaceView().getWidth()) {
							// originSize = true;
							// }
							switch (gesture) {
							// 手势放大缩小
							case MyGestureDispatcher.GESTURE_TO_BIGGER:
							case MyGestureDispatcher.GESTURE_TO_SMALLER:
								gestureOnView(playChannel.getSurfaceView(),
										playChannel, gesture, distance, vector,
										middle);
								lastClickTime = 0;
								break;
							// 手势云台
							case MyGestureDispatcher.GESTURE_TO_LEFT:
								gestureOnView(playChannel.getSurfaceView(),
										playChannel, gesture, distance, vector,
										middle);
								lastClickTime = 0;
								break;

							case MyGestureDispatcher.GESTURE_TO_UP:
								gestureOnView(playChannel.getSurfaceView(),
										playChannel, gesture, distance, vector,
										middle);
								lastClickTime = 0;
								break;

							case MyGestureDispatcher.GESTURE_TO_RIGHT:
								gestureOnView(playChannel.getSurfaceView(),
										playChannel, gesture, distance, vector,
										middle);
								lastClickTime = 0;
								break;

							case MyGestureDispatcher.GESTURE_TO_DOWN:
								gestureOnView(playChannel.getSurfaceView(),
										playChannel, gesture, distance, vector,
										middle);
								lastClickTime = 0;
								break;
							// 手势单击双击
							case MyGestureDispatcher.CLICK_EVENT:
								if (0 == lastClickTime) {
									isDoubleClickCheck = false;
									lastClickTime = System.currentTimeMillis();
									handler.sendMessageDelayed(
											handler.obtainMessage(
													Consts.WHAT_SURFACEVIEW_CLICK,
													middle.x, middle.y,
													playChannel), 350);
									MyLog.e("Click1--", "单击：lastClickTime="
											+ lastClickTime);
								} else {
									int clickTimeBetween = (int) (System
											.currentTimeMillis() - lastClickTime);
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
				});

		playSurfaceView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				MyLog.v("Click_EVENT", "index=" + playChannel.getIndex()
						+ ";event=" + event);
				dispatcher.motion(event);
				return true;
			}
		});
		playSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				stopConnect();
				playChannel.setSurface(null);
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				playChannel.setSurface(holder.getSurface());
				if (!manuPause) {
					loadingState(Consts.TAG_PLAY_CONNECTING);
					tensileView(playChannel, playChannel.getSurfaceView());
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				playChannel.setSurface(holder.getSurface());
				if (!onPause && !manuPause) {
					if (false == playChannel.isConnected()
							&& false == playChannel.isConnecting()) {
						startConnect(rtmp, holder.getSurface());
					} else {
						tensileView(playChannel, playChannel.getSurfaceView());
						resumeVideo();
					}
				}
			}
		});

		WebChromeClient wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (-2 == titleID) {
					currentMenu.setText(title);
					if (mIsShare) {
						MyLog.v(TAG, "website's title:" + title);
						// 从webview获取到title以后，展示分享按钮
						rightBtn.setVisibility(View.VISIBLE);
						// 设置视频标题
						mWeixinVideo.setTitle(title);
						mCircleVideo.setTitle(title);
						mWeiboVideo.setTitle(title);
					}
				}
			}
		};
		webView.getSettings().setJavaScriptEnabled(true);

		// 设置setWebChromeClient对象
		webView.setWebChromeClient(wvcc);
		webView.requestFocus(View.FOCUS_DOWN);

		// setting.setPluginState(PluginState.ON);
		// 加快加载速度
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				MyLog.v(TAG, "webView load failed");
				demoLayout.setVisibility(View.VISIBLE);
				loadFailed = true;
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
				view.loadUrl(newUrl);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				loadinglayout.setVisibility(View.VISIBLE);
				Animation anim = AnimationUtils.loadAnimation(
						JVWebView2Activity.this, R.anim.rotate);
				loadingBar.setAnimation(anim);
				MyLog.v(TAG, "webView start load");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (loadFailed) {
					loadFailedLayout.setVisibility(View.VISIBLE);
					loadinglayout.setVisibility(View.GONE);
					webView.setVisibility(View.GONE);
				} else {
					webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
					loadinglayout.setVisibility(View.GONE);
					demoLayout.setVisibility(View.VISIBLE);
					loadFailedLayout.setVisibility(View.GONE);
				}
				// webView.loadUrl("javascript:videopayer.play()");
				MyLog.v(TAG, "webView finish load");
				demoLayout.setVisibility(View.VISIBLE);
			}
		});

		loadFailed = false;
		webView.loadUrl(url);
	}

	/**
	 * 设置播放显示的大小
	 */
	private void setSurfaceSize(boolean full) {
		if (full) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
			playSurfaceView.setLayoutParams(reParamsH);
			playLayout.setLayoutParams(reParamsH);
			webView.setVisibility(View.GONE);
			topBar.setVisibility(View.GONE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
			playSurfaceView.setLayoutParams(reParamsV);
			playLayout.setLayoutParams(reParamsV);
			webView.setVisibility(View.VISIBLE);
			topBar.setVisibility(View.VISIBLE);
			webView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							// TODO Auto-generated method stub
							if ((disMetrics.heightPixels
									- disMetrics.widthPixels * 0.75 - 100)
									- webView.getHeight() > 300) {
								zhezhaoLayout.setLayoutParams(reParamstop2);
							} else {
								zhezhaoLayout.setLayoutParams(reParamstop1);
							}
						}
					});
		}

	}

	/**
	 * 开始连接
	 */
	private void startConnect(String playUrl, Object surface) {
		playChannel.setConnecting(true);
		playChannel.setPaused(false);
		manuPause = false;
		Jni.connectRTMP(playChannel.getIndex(), playUrl, surface, false, null);
	};

	/**
	 * 断开连接
	 */
	private boolean stopConnect() {
		stopAudio(playChannel.getIndex());
		return Jni.shutdownRTMP(playChannel.getIndex());
	};

	// /**
	// * 暂停连接
	// */
	// private boolean pauseVideo() {
	// playChannel.setPaused(true);
	// stopAudio(playChannel.getIndex());
	// return Jni.pause(playChannel.getIndex());
	// }

	/**
	 * resume连接
	 */
	private boolean resumeVideo() {
		boolean resumeRes = false;
		if (manuPause) {
			resumeRes = true;
			return true;
		} else {
			if (false == playChannel.isConnected()
					&& false == playChannel.isConnecting()
					&& null != playChannel.getSurface()) {
				loadingState(Consts.TAG_PLAY_CONNECTING);
				startConnect(rtmp, playChannel.getSurface());
				resumeRes = true;
			} else {
				if (playChannel.isPaused()) {
					loadingState(Consts.RTMP_CONN_SCCUESS);
				}

				if (null != playChannel.getSurface()) {
					resumeRes = Jni.resume(playChannel.getIndex(),
							playChannel.getSurface());
					Jni.setViewPort(playChannel.getIndex(),
							playChannel.getLastPortLeft(),
							playChannel.getLastPortBottom(),
							playChannel.getLastPortWidth(),
							playChannel.getLastPortHeight());
				}
				if (resumeRes) {
					playChannel.setPaused(false);
				}
			}
		}

		return resumeRes;
	}

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left: {
				backMethod();
				break;
			}
			case R.id.savesetting: {
				if ("".equalsIgnoreCase(minCache.getText().toString())) {
					showTextToast("哎呀妈呀，啥也不输让我设置啥呀！");
				} else if ("".equalsIgnoreCase(desCache.getText().toString())) {
					showTextToast("你在考验我的智商吗？");
				} else {
					int min = Integer.parseInt(minCache.getText().toString());
					int des = Integer.parseInt(desCache.getText().toString());

					if (min < 0 || min > 25) {
						showTextToast("你没看到最小限定的范围吗？");
					} else if (des < 25 || des > 1000) {
						showTextToast("你没看到最大限定的范围吗？");
					} else {
						boolean res = Jni.setFrameCounts(
								playChannel.getIndex(), min, des);
						if (res) {
							showTextToast("设置成功");
						} else {
							showTextToast("设置失败");
						}
					}
				}
				break;
			}
			case R.id.refreshimg: {
				loadFailedLayout.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
				Animation anim = AnimationUtils.loadAnimation(
						JVWebView2Activity.this, R.anim.rotate);
				loadingBar.setAnimation(anim);
				loadFailed = false;
				webView.reload();
				// webView.loadUrl(url);
				break;
			}
			case R.id.pause: {// 暂停
				if (playChannel.isConnected()) {// 已连接
					manuPause = true;
					linkSpeed.setVisibility(View.GONE);
					pause.setImageDrawable(getResources().getDrawable(
							R.drawable.video_play_icon));
					// 暂停视频
					boolean res = stopConnect();
				} else {
					manuPause = false;
					// 继续播放视频
					boolean res = resumeVideo();
				}
				break;
			}
			case R.id.fullscreen: {// 全屏
				if (playChannel.isConnected()) {
					if (fullScreenFlag) {
						fullScreenFlag = false;
						fullScreen.setImageDrawable(getResources().getDrawable(
								R.drawable.full_screen_icon));
					} else {
						fullScreenFlag = true;
						fullScreen.setImageDrawable(getResources().getDrawable(
								R.drawable.notfull_screen_icon));
					}
					setSurfaceSize(fullScreenFlag);
				}
				break;
			}
			case R.id.playview: {
				loadingState(Consts.TAG_PLAY_CONNECTING);
				startConnect(rtmp, playChannel.getSurface());
				break;
			}
			case R.id.btn_right: {
				// 分享的场合,视频连接成功后才能分享
				if (mIsShare) {
					if (playChannel.isConnected()) {
						MyLog.v(TAG, "open share pane");
						openSharePane();
					} else {
						showTextToast(R.string.str_wait_connect);
					}
				}
				break;
			}
			}
		}
	};

	/**
	 * 返回事件
	 */
	private void backMethod() {
		if (webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
		} else {
			DisConnectTask disTask = new DisConnectTask();
			String[] params = new String[3];
			disTask.execute(params);
		}
	}

	// 设置三种类型参数分别为String,Integer,String
	class DisConnectTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int disRes = 0;// 0成功 1失败
			stopConnect();
			int errorCount = 0;
			while (!isDisConnected) {
				errorCount++;
				if (errorCount > 60) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return disRes;
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
				if (mIsShare) {
					MyLog.v(TAG,
							"remove sina's sso handler and clear listeners");
					mController.getConfig().cleanListeners();
					mController.getConfig().removeSsoHandler(SHARE_MEDIA.SINA);
				}
				finish();
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

	@Override
	public void onBackPressed() {
		backMethod();
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// handler.sendMessage(handler.obtainMessage(Consts.WHAT_DEMO_BUFFING));
		onPause = true;
		if (!manuPause) {
			stopConnect();
		}

		// webView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		onPause = false;
		// webView.onResume();
		// resumeVideo();
		if (!manuPause) {
			handler.sendMessageDelayed(
					handler.obtainMessage(Consts.WHAT_DEMO_RESUME), 500);
		}
	}

	@Override
	public void onClick(Channel channel, boolean isFromImageView, int viewId) {

	}

	@Override
	public void onLongClick(Channel channel) {

	}

	// 第一次click时间
	private long lastClickTime = 0;

	@Override
	public void onGesture(int index, int gesture, int distance, Point vector,
			Point middle) {
		if (null != playChannel && playChannel.isConnected()
				&& !playChannel.isConnecting()) {
			// boolean originSize = false;
			// if (playChannel.getLastPortWidth() ==
			// playChannel.getSurfaceView()
			// .getWidth()) {
			// originSize = true;
			// }
			switch (gesture) {
			// 手势放大缩小
			case MyGestureDispatcher.GESTURE_TO_BIGGER:
			case MyGestureDispatcher.GESTURE_TO_SMALLER:
				gestureOnView(playChannel.getSurfaceView(), playChannel,
						gesture, distance, vector, middle);
				lastClickTime = 0;
				break;
			}
		}
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

	@Override
	public void onLifecycle(int index, int status, Surface surface, int width,
			int height) {
	}

	private void tensileView(Channel channel, View view) {
		channel.setLastPortLeft(0);
		channel.setLastPortBottom(0);
		channel.setLastPortWidth(view.getWidth());
		channel.setLastPortHeight(view.getHeight());
		Jni.setViewPort(channel.getIndex(), 0, 0, view.getWidth(),
				view.getHeight());
	}

	/**
	 * 修改连接状态
	 * 
	 * @param tag
	 */
	private void loadingState(int tag) {
		switch (tag) {
		case Consts.TAG_PLAY_CONNECTING: {// 连接中
			loadingVideoBar.setVisibility(View.VISIBLE);
			loadingState.setVisibility(View.VISIBLE);
			playImgView.setVisibility(View.GONE);
			loadingState.setText(R.string.connecting);
			break;
		}
		case Consts.RTMP_CONN_SCCUESS: {
			loadingVideoBar.setVisibility(View.VISIBLE);
			loadingState.setVisibility(View.VISIBLE);
			playImgView.setVisibility(View.GONE);
			loadingState.setText(R.string.connecting_buffer2);

			break;
		}
		case Consts.RTMP_CONN_FAILED: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			loadingState.setText(R.string.connect_failed);
			linkSpeed.setVisibility(View.GONE);
			break;
		}
		case Consts.RTMP_DISCONNECTED: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			loadingState.setText(R.string.closed);
			linkSpeed.setVisibility(View.GONE);
			break;
		}
		case Consts.RTMP_EDISCONNECT: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			// loadingState.setText("断开失败");
			break;
		}
		case Consts.CALL_NEW_PICTURE: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.GONE);
			break;
		}
		}

	}

	/**
	 * 修改连接状态
	 * 
	 * @param tag
	 */
	private void bufferingState(int tag, int process) {
		switch (tag) {
		case Consts.TAG_PLAY_BUFFERING: {// 已连接正在缓冲
			loadingVideoBar.setVisibility(View.VISIBLE);
			loadingState.setVisibility(View.VISIBLE);
			playImgView.setVisibility(View.GONE);
			loadingState.setText(getString(R.string.connecting_buffer2)
					+ process + "%");
			break;
		}
		case Consts.TAG_PLAY_BUFFERED: {// 缓冲完成
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.GONE);
			break;
		}

		}

	}

	/**
	 * @功能描述 : 打开自定义的分享面板</br>
	 * @return
	 */
	private void openSharePane() {
		CustomShareBoard shareBoard = new CustomShareBoard(this);
		shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM,
				0, 0);
	}

	/**
	 * @功能描述 : 配置分享平台参数</br>
	 * @return
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加微信平台分享</br>
	 * @return
	 */
	private void addWXPlatform() {
		// 微信开发平台注册应用的AppID
		String appId = "wx21141328bd509074";
		String appSecret = "f9172781187afc8853803f681ab668bf";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
		wxHandler.addToSocialSDK();
		// 设置不显示提示：大于32k 压缩图片
		wxHandler.showCompressToast(false);

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		// 设置不显示提示：大于32k 压缩图片
		wxCircleHandler.showCompressToast(false);
	}

	/**
	 * @功能描述 : 根据不同的平台设置不同的分享内容</br>
	 * @return
	 */
	private void setShareContent() {
		// 视频图标
		UMImage urlImage = new UMImage(this, R.drawable.share_logo);

		// 视频链接地址
		String videoUrl = createVideoUrl();

		// 视频内容
		String videoContent = getString(R.string.umeng_socialize_share_video_content);

		// 视频分享
		mWeixinVideo = new UMVideo(createVideoUrlByPlatform(videoUrl, "weixin"));
		mWeixinVideo.setThumb(urlImage);

		// 设置微信分享的内容
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(videoContent);
		weixinContent.setShareMedia(urlImage);
		weixinContent.setShareMedia(mWeixinVideo);
		mController.setShareMedia(weixinContent);

		// 视频分享
		mCircleVideo = new UMVideo(createVideoUrlByPlatform(videoUrl,
				"pengyouquan"));
		mCircleVideo.setThumb(urlImage);

		// 设置朋友圈分享的内容
		CircleShareContent circleContent = new CircleShareContent();
		circleContent.setShareContent(videoContent);
		circleContent.setShareMedia(urlImage);
		circleContent.setShareMedia(mCircleVideo);
		mController.setShareMedia(circleContent);

		// 视频分享
		mWeiboVideo = new UMVideo(createVideoUrlByPlatform(videoUrl, "weibo"));
		mWeiboVideo.setThumb(urlImage);

		// 设置新浪微博分享的内容
		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent.setShareContent(videoContent);
		sinaContent.setShareImage(urlImage);
		sinaContent.setShareMedia(mWeiboVideo);
		mController.setShareMedia(sinaContent);

		// 关闭默认的Toast提示,回避Toast重复问题
		mController.getConfig().closeToast();
		/*
		 * 关闭新浪微博分享时的获取地理位置功能 原因是友盟不支持多语言,定位失败的Toast被写死在了代码里
		 */
		mController.getConfig().setDefaultShareLocation(false);
	}

	/**
	 * @功能描述：判断是否展示分享功能<br/>
	 * @param pUrl
	 *            网址
	 * @return true/false
	 */
	private boolean checkShareEnabled(String pUrl) {
		MyLog.v(TAG, "check the url:" + pUrl);
		if (pUrl.contains("allowshare")) {
			return true;
		}
		return false;
	}

	/**
	 * @功能描述 : 生成视频链接地址</br>
	 * @return
	 */
	private String createVideoUrl() {
		HashMap<String, String> urlParamsMap;
		// 解析URL
		String urlParamsArray[] = url.split("\\?");
		urlParamsMap = ConfigUtil.genMsgMapFromhpget(urlParamsArray[1]);

		String lang = urlParamsMap.get("lang");
		String vid = urlParamsMap.get("vid");
		String d = urlParamsMap.get("d");

		// 拼接视频地址
		StringBuffer urlStrBuf = new StringBuffer();
		urlStrBuf.append(urlParamsArray[0]);
		// 设置平台标记，不同平台使用时进行替换
		urlStrBuf.append("?plat=platform_flag");
		urlStrBuf.append("&lang=");
		urlStrBuf.append(lang);
		urlStrBuf.append("&vid=");
		urlStrBuf.append(vid);
		urlStrBuf.append("&d=");
		urlStrBuf.append(d);

		MyLog.v(TAG, "video url:" + urlStrBuf.toString());
		return urlStrBuf.toString();
	}

	/**
	 * @功能描述 : 根据不同的平台生成视频链接地址</br>
	 * @param pVideoUrl
	 *            视频链接地址
	 * @param platform
	 *            分享平台
	 * @return
	 */
	private String createVideoUrlByPlatform(final String pVideoUrl,
			final String platform) {
		return pVideoUrl.replace("platform_flag", platform);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
