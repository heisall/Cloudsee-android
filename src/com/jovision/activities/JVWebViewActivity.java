package com.jovision.activities;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MyLog;
import com.jovision.commons.Url;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.JSONUtil;

public class JVWebViewActivity extends BaseActivity {

	private static final String TAG = "JVWebViewActivity";
	private LinearLayout loadinglayout;
	/** topBar **/
	private RelativeLayout topBar;

	private WebView webView;
	private String url = "";
	private int titleID = 0;
	private ImageView loadingBar;

	private LinearLayout loadFailedLayout;
	private ImageView reloadImgView;
	private boolean loadFailed = false;
	private boolean isfirst = false;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_DEMO_URL_SUCCESS: {
			dismissDialog();
			HashMap<String, String> paramMap = (HashMap<String, String>) obj;
			Intent intentAD = new Intent(JVWebViewActivity.this,
					JVWebView2Activity.class);
			intentAD.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intentAD.putExtra("URL", paramMap.get("new_url"));
			intentAD.putExtra("title", -2);
			intentAD.putExtra("rtmp", paramMap.get("rtmpurl"));
			intentAD.putExtra("cloudnum", paramMap.get("cloud_num"));
			intentAD.putExtra("channel", paramMap.get("channel"));

			JVWebViewActivity.this.startActivity(intentAD);
			break;
		}
		case Consts.WHAT_DEMO_URL_FAILED: {
			// TODO
			dismissDialog();
			showTextToast(R.string.str_video_load_failed);
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
		titleID = getIntent().getIntExtra("title", 0);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.findpass_layout);

		// if (url.contains("rotate=x")) {
		// url = url.replace("rotate=x", "");
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//
		// 横屏
		// }

		MyLog.v(TAG, "webview-URL=" + url);
		/** topBar **/
		topBar = (RelativeLayout) findViewById(R.id.topbarh);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.demo);
		loadingBar = (ImageView) findViewById(R.id.loadingbars);
		loadinglayout = (LinearLayout) findViewById(R.id.loadinglayout);

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

		webView = (WebView) findViewById(R.id.findpasswebview);

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
				MyLog.v("new_url", newUrl);
				// showTextToast(rtmp);//////////////等着去掉
				try {
					if (newUrl.contains("viewmode")) {

						String param_array[] = newUrl.split("\\?");
						HashMap<String, String> resMap;
						resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);

						String rtmp_url = resMap.get("streamsvr");
						String rtmp_port = resMap.get("rtmport");
						String hls_port = resMap.get("hlsport");
						String cloud_num = resMap.get("cloudnum");
						String channel = resMap.get("channel");

						HashMap<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("new_url", newUrl);
						paramMap.put("rtmp_url", rtmp_url);
						paramMap.put("rtmp_port", rtmp_port);
						paramMap.put("hls_port", hls_port);
						paramMap.put("cloud_num", cloud_num);
						paramMap.put("channel", channel);

						String getPlayUtlRequest = Url.JOVISION_PUBLIC_API
								+ "server=" + rtmp_url + "&dguid=" + cloud_num
								+ "&channel=" + channel + "&hlsport="
								+ hls_port + "&rtmpport=" + rtmp_port;

						createDialog("", false);
						new GetPlayUrlThread(paramMap, getPlayUtlRequest)
								.start();
					} else {
						view.loadUrl(newUrl);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!isfirst) {
					loadinglayout.setVisibility(View.VISIBLE);
					loadingBar.setAnimation(AnimationUtils.loadAnimation(
							JVWebViewActivity.this, R.anim.rotate));
					isfirst = true;
				}
				MyLog.v(TAG, "webView start load");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// webView.loadUrl("javascript:videopayer.play()");

				if (loadFailed) {
					loadFailedLayout.setVisibility(View.VISIBLE);
					webView.setVisibility(View.GONE);
					loadinglayout.setVisibility(View.GONE);
				} else {
					webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
					loadinglayout.setVisibility(View.GONE);
					webView.setVisibility(View.VISIBLE);
					loadFailedLayout.setVisibility(View.GONE);
				}
				MyLog.v(TAG, "webView finish load");
			}
		});

		loadFailed = false;
		webView.loadUrl(url);
	}

	class GetPlayUrlThread extends Thread {
		String requestUrl;
		HashMap<String, String> paramMap;

		public GetPlayUrlThread(HashMap<String, String> map, String url) {
			requestUrl = url;
			paramMap = map;
		}

		@Override
		public void run() {
			super.run();
			MyLog.v("post_request", requestUrl);
			int rt = 0;
			String rtmpUrl = "";
			// {"rt": 0, "rtmpurl":
			// "rtmp://119.188.172.3:1935/live/a579223323_1", "hlsurl":
			// "http://119.188.172.3:8080/live/a579223323_1.m3u8", "mid": 33512}
			String result = JSONUtil.getRequest(requestUrl);
			try {
				if (null != result && !"".equalsIgnoreCase(result)) {
					JSONObject obj = new JSONObject(result);
					if (null != obj) {
						rt = obj.getInt("rt");
						rtmpUrl = obj.getString("rtmpurl");
					}
				}

				if (0 == rt) {
					paramMap.put("rtmpurl", rtmpUrl);
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_DEMO_URL_SUCCESS, 0, 0, paramMap));
				} else {
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_DEMO_URL_FAILED, 0, 0, null));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			MyLog.v("post_result", result);
		}
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
				loadinglayout.setVisibility(View.VISIBLE);
				loadingBar.setAnimation(AnimationUtils.loadAnimation(
						JVWebViewActivity.this, R.anim.rotate));
				loadFailed = false;
				webView.loadUrl(url);
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
			JVWebViewActivity.this.finish();
		}
	}

	@Override
	public void onBackPressed() {
		backMethod();
	}

	// @Override
	// // 设置回退
	// // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	// webView.goBack(); // goBack()表示返回WebView的上一页面
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// webView.onPause(); // 暂停网页中正在播放的视频
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
	}

}
