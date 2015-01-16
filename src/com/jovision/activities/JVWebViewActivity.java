package com.jovision.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;

public class JVWebViewActivity extends BaseActivity {

	private static final String TAG = "JVWebViewActivity";

	/** topBar **/
	private RelativeLayout topBar;

	private WebView webView;
	private String url = "";
	private int titleID = 0;
	private ProgressBar loadingBar;

	private LinearLayout loadFailedLayout;
	private ImageView reloadImgView;
	private boolean loadFailed = false;
	private boolean isfirst = false;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

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
		/** topBar **/
		topBar = (RelativeLayout) findViewById(R.id.topbarh);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.demo);
		loadingBar = (ProgressBar) findViewById(R.id.loadingbars);

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
				String param_array[] = newUrl.split("\\?"); 
				HashMap<String, String> resMap;
				resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);
				
				String rtmp_url = resMap.get("streamsvr");
				String rtmp_port = resMap.get("rtmport");
				String cloud_num = resMap.get("cloudnum");
				String channel = resMap.get("channel");
				
				String rtmp = String.format("rtmp://%s:%s/live/%s_%s", rtmp_url,rtmp_port,cloud_num,channel);
				rtmp = rtmp.toLowerCase();//转小写
				MyLog.e("RTMP", ">>>>>> "+rtmp);
//				showTextToast(rtmp);//////////////等着去掉 
				if (newUrl.contains("viewmode")) {
					Intent intentAD = new Intent(JVWebViewActivity.this,
							JVWebView2Activity.class);
					intentAD.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intentAD.putExtra("URL", newUrl);
					intentAD.putExtra("title", -2);
					intentAD.putExtra("rtmp", rtmp);
					
					JVWebViewActivity.this.startActivity(intentAD);
				} else {
					view.loadUrl(newUrl);
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!isfirst) {
					loadingBar.setVisibility(View.VISIBLE);
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
					loadingBar.setVisibility(View.GONE);
				} else {
					webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
					loadingBar.setVisibility(View.GONE);
					webView.setVisibility(View.VISIBLE);
					loadFailedLayout.setVisibility(View.GONE);
				}
				MyLog.v(TAG, "webView finish load");
			}
		});

		loadFailed = false;
		webView.loadUrl(url);
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
