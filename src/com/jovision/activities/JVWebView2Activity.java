package com.jovision.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import com.jovision.commons.MyLog;

public class JVWebView2Activity extends BaseActivity {

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
	private boolean pausedFlag = false;

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
			// switch(arg2){
			// case Consts.RTMP_CONN_SCCUESS:{
			// break;
			// }
			// case Consts.RTMP_CONN_FAILED:{
			// break;
			// }
			// case Consts.RTMP_DISCONNECTED:{
			// break;
			// }
			// case Consts.RTMP_EDISCONNECT:{
			// break;
			// }
			//
			// }
			// break;
			loadingState(arg2);
		}
		case Consts.CALL_NEW_PICTURE: {
			loadingState(Consts.CALL_NEW_PICTURE);
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
			break;
		}
		case Consts.RTMP_DISCONNECTED: {
			loadingVideoBar.setVisibility(View.GONE);
			loadingState.setVisibility(View.GONE);
			playImgView.setVisibility(View.VISIBLE);
			loadingState.setText(R.string.closed);
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

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.webview2_layout);

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
		playSurfaceView.setOnClickListener(myOnClickListener);
		setSurfaceSize(false);
		fullScreenFlag = false;

		surfaceHolder = playSurfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

				if (pausedFlag) {
					resumeVideo();
				} else {
					loadingState(Consts.TAG_PLAY_CONNECTING);
					startConnect(rtmp,
							surfaceHolder.getSurface());
					Jni.enablePlayAudio(1, true);
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
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
	}

	/**
	 * 开始连接
	 */
	private void startConnect(String playUrl, Object surface) {
		Jni.connectRTMP(1, playUrl, surface, false, "");
	};

	/**
	 * 断开连接
	 */
	private void stopConnect() {
		Jni.shutdownRTMP(1);
	};

	/**
	 * 断开连接
	 */
	private boolean pauseVideo() {
		pausedFlag = true;
		return Jni.pause(1);
	}

	/**
	 * 断开连接
	 */
	private boolean resumeVideo() {
		pausedFlag = false;
		return Jni.resume(1, surfaceHolder.getSurface());
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

				if (pausedFlag) {// 已暂停
					pause.setBackgroundResource(R.drawable.video_stop_icon);
					// 继续播放视频
					boolean res = resumeVideo();
				} else {
					pause.setBackgroundResource(R.drawable.video_play_icon);
					// 暂停视频
					boolean res = pauseVideo();
				}
				// if (isRemotePause) {
				// playBackPause
				// .setBackgroundResource(R.drawable.video_stop_icon);
				// // 继续播放视频
				// Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYGOON,
				// new byte[0], 0);
				// Jni.enablePlayAudio(indexOfChannel, isAudio);
				// isRemotePause = false;
				// } else {
				// // 暂停视频
				// Jni.sendBytes(indexOfChannel, JVNetConst.JVN_CMD_PLAYPAUSE,
				// new byte[0], 0);
				// Jni.enablePlayAudio(indexOfChannel, false);
				// playBackPause
				// .setBackgroundResource(R.drawable.video_play_icon);
				// isRemotePause = true;
				// }
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

}
