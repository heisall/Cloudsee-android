package com.jovision.activities;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.PlayUtil;

public class JVWebView2Activity extends BaseActivity implements
		PlayWindowManager.OnUiListener {

	private static final String TAG = "JVWebView2Activity";

	/** topBar **/
	private RelativeLayout topBar;

	protected RelativeLayout.LayoutParams reParamsV;
	protected RelativeLayout.LayoutParams reParamsH;

	private SurfaceHolder surfaceHolder;
	private RelativeLayout demoLayout;
	private RelativeLayout playLayout;
	private SurfaceView playSurfaceView;

	private ProgressBar loadingVideoBar;
	private TextView loadingState;
	private ImageView playImgView;
	private RelativeLayout playBar;
	private Button pause;
	private Button fullScreen;
	private WebView webView;

	private boolean fullScreenFlag = false;
	// private boolean pausedFlag = false;
	private MyAudio playAudio;
	private int audioByte = 0;
	private Channel playChannel;

	private String url = "";
	private String rtmp = "";
	private int titleID = 0;
	private ProgressBar loadingBar;

	private LinearLayout loadFailedLayout;
	private ImageView reloadImgView;
	private boolean loadFailed = false;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.CALL_CONNECT_CHANGE: {
			loadingState(arg2);
		}
		case Consts.CALL_NEW_PICTURE: {
			loadingState(Consts.CALL_NEW_PICTURE);
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

		}
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
			playChannel.setConnecting(true);
			playChannel.setConnected(false);
			playChannel.setPaused(false);
			break;
		}
		case Consts.RTMP_CONN_SCCUESS: {
			loadingVideoBar.setVisibility(View.VISIBLE);
			loadingState.setVisibility(View.VISIBLE);
			playImgView.setVisibility(View.GONE);
			loadingState.setText(R.string.connecting_buffer2);
			playChannel.setConnecting(false);
			playChannel.setConnected(true);
			playChannel.setPaused(false);
			break;
		}
		case Consts.RTMP_CONN_FAILED: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			loadingState.setText(R.string.connect_failed);
			playChannel.setConnecting(false);
			playChannel.setConnected(false);
			playChannel.setPaused(false);
			break;
		}
		case Consts.RTMP_DISCONNECTED: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			loadingState.setText(R.string.closed);
			playChannel.setConnecting(false);
			playChannel.setConnected(false);
			playChannel.setPaused(false);
			break;
		}
		case Consts.RTMP_EDISCONNECT: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			// loadingState.setText("断开失败");
			playChannel.setConnecting(false);
			playChannel.setConnected(true);
			playChannel.setPaused(false);
			break;
		}
		case Consts.CALL_NEW_PICTURE: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.GONE);
			playChannel.setConnecting(false);
			playChannel.setConnected(true);
			playChannel.setPaused(false);
			break;
		}
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
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

		reParamsH = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
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
		//
		playAudio = MyAudio.getIntance(Consts.PLAY_AUDIO_WHAT,
				JVWebView2Activity.this, 8000);

		/** topBar **/
		topBar = (RelativeLayout) findViewById(R.id.topbarh);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.demo);
		loadingBar = (ProgressBar) findViewById(R.id.loadingbar);

		loadFailedLayout = (LinearLayout) findViewById(R.id.loadfailedlayout);
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

		demoLayout = (RelativeLayout) findViewById(R.id.demolayout);
		playLayout = (RelativeLayout) findViewById(R.id.playlayout);
		playSurfaceView = (SurfaceView) findViewById(R.id.playsurface);
		loadingVideoBar = (ProgressBar) findViewById(R.id.videoloading);
		loadingState = (TextView) findViewById(R.id.playstate);
		playImgView = (ImageView) findViewById(R.id.playview);
		playBar = (RelativeLayout) findViewById(R.id.playbar);
		pause = (Button) findViewById(R.id.pause);
		fullScreen = (Button) findViewById(R.id.fullscreen);
		webView = (WebView) findViewById(R.id.webview);

		pause.setOnClickListener(myOnClickListener);
		fullScreen.setOnClickListener(myOnClickListener);
		// playSurfaceView.setOnClickListener(myOnClickListener);
		setSurfaceSize(false);
		fullScreenFlag = false;

		playChannel.setSurfaceView(playSurfaceView);
		surfaceHolder = playSurfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				pauseVideo();
				playChannel.setSurface(null);
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (playChannel.isPaused()) {
					resumeVideo();
				} else {
					loadingState(Consts.TAG_PLAY_CONNECTING);
					startConnect(rtmp, surfaceHolder.getSurface());
					Jni.enablePlayAudio(playChannel.getIndex(), true);
					playChannel.setSurface(surfaceHolder.getSurface());
				}
				tensileView(playChannel, playChannel.getSurfaceView());
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				tensileView(playChannel, playChannel.getSurfaceView());
				resumeVideo();

			}
		});

		WebChromeClient wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (-2 == titleID) {
					currentMenu.setText(title);
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
				loadingBar.setVisibility(View.VISIBLE);
				MyLog.v(TAG, "webView start load");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (loadFailed) {
					loadFailedLayout.setVisibility(View.VISIBLE);
					demoLayout.setVisibility(View.GONE);
					loadingBar.setVisibility(View.GONE);
				} else {
					webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
					loadingBar.setVisibility(View.GONE);
					demoLayout.setVisibility(View.VISIBLE);
					loadFailedLayout.setVisibility(View.GONE);
				}
				// webView.loadUrl("javascript:videopayer.play()");
				MyLog.v(TAG, "webView finish load");
			}
		});

		loadFailed = false;
		webView.loadUrl(url);
	}

	/**
	 * 设置播放显示的大小
	 */
	private void setSurfaceSize(boolean full) {
		pauseVideo();
		if (full) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
			playSurfaceView.setLayoutParams(reParamsH);
			playLayout.setLayoutParams(reParamsH);
			topBar.setVisibility(View.GONE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
			playSurfaceView.setLayoutParams(reParamsV);
			playLayout.setLayoutParams(reParamsV);
			topBar.setVisibility(View.VISIBLE);
		}
		resumeVideo();
	}

	/**
	 * 开始连接
	 */
	private void startConnect(String playUrl, Object surface) {
		Jni.connectRTMP(playChannel.getIndex(), playUrl, surface, false, "");
	};

	/**
	 * 断开连接
	 */
	private void stopConnect() {
		Jni.shutdownRTMP(playChannel.getIndex());
	};

	/**
	 * 断开连接
	 */
	private boolean pauseVideo() {
		playChannel.setPaused(true);
		return Jni.pause(playChannel.getIndex());
	}

	/**
	 * 断开连接
	 */
	private boolean resumeVideo() {
		playChannel.setPaused(false);
		boolean resumeRes = false;
		if (null != surfaceHolder && null != surfaceHolder.getSurface()) {
			resumeRes = Jni.resume(playChannel.getIndex(),
					surfaceHolder.getSurface());
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
			case R.id.refreshimg: {
				loadFailedLayout.setVisibility(View.GONE);
				loadingBar.setVisibility(View.VISIBLE);
				loadFailed = false;
				webView.loadUrl(url);
				break;
			}
			case R.id.pause: {// 暂停
				// TODO
				if (playChannel.isPaused()) {// 已暂停
					pause.setBackgroundResource(R.drawable.video_stop_icon);
					// 继续播放视频
					boolean res = resumeVideo();
				} else {
					pause.setBackgroundResource(R.drawable.video_play_icon);
					// 暂停视频
					boolean res = pauseVideo();
				}
				break;
			}
			case R.id.fullscreen: {// 全屏
				if (fullScreenFlag) {
					fullScreenFlag = false;
				} else {
					fullScreenFlag = true;
				}
				setSurfaceSize(fullScreenFlag);
				break;
			}
			case R.id.playsurface: {
				if (View.VISIBLE == playBar.getVisibility()) {
					playBar.setVisibility(View.GONE);
				} else {
					playBar.setVisibility(View.VISIBLE);
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
			finish();
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
		stopConnect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		pauseVideo();
		webView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
	}

	@Override
	public void onClick(Channel channel, boolean isFromImageView, int viewId) {

	}

	@Override
	public void onLongClick(Channel channel) {
		// TODO Auto-generated method stub

	}

	// 第一次click时间
	private long lastClickTime = 0;

	@Override
	public void onGesture(int index, int gesture, int distance, Point vector,
			Point middle) {
		if (null != playChannel && playChannel.isConnected()
				&& !playChannel.isConnecting()) {
			boolean originSize = false;
			if (playChannel.getLastPortWidth() == playChannel.getSurfaceView()
					.getWidth()) {
				originSize = true;
			}
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
		// try {
		// switch (status) {
		// case PlayWindowManager.STATUS_CREATED:
		// MyLog.w(Consts.TAG_XXX, "> surface created: " + index);
		// if (playChannel.isPaused()) {
		// resumeVideo();
		// } else {
		// loadingState(Consts.TAG_PLAY_CONNECTING);
		// startConnect(rtmp, surfaceHolder.getSurface());
		// Jni.enablePlayAudio(playChannel.getIndex(), true);
		// playChannel.setSurface(surfaceHolder.getSurface());
		// }
		// break;
		//
		// case PlayWindowManager.STATUS_CHANGED:
		// tensileView(playChannel, playChannel.getSurfaceView());
		// Jni.resume(index, surface);
		// break;
		//
		// case PlayWindowManager.STATUS_DESTROYED:
		// MyLog.w(Consts.TAG_XXX, "> surface destroyed: " + index);
		// pauseVideo();
		// playChannel.setSurface(null);
		// break;
		//
		// default:
		// break;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	private void tensileView(Channel channel, View view) {
		channel.setLastPortLeft(0);
		channel.setLastPortBottom(0);
		channel.setLastPortWidth(view.getWidth());
		channel.setLastPortHeight(view.getHeight());
		Jni.setViewPort(channel.getIndex(), 0, 0, view.getWidth(),
				view.getHeight());
	}

}
