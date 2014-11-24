package com.jovision.activities;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class JVWebViewActivity extends BaseActivity {

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;

	private WebView webView;
	private String url = "";
	private int titleID = 0;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		url = getIntent().getStringExtra("URL");
		titleID = getIntent().getIntExtra("title", 0);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.findpass_layout);
		/** topBar **/
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		if (-1 == titleID) {
			currentMenu.setText("");
		} else {
			currentMenu.setText(titleID);
		}

		back.setOnClickListener(myOnClickListener);
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setVisibility(View.GONE);

		webView = (WebView) findViewById(R.id.findpasswebview);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.requestFocus(View.FOCUS_DOWN);
		// 加快加载速度
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		webView.loadUrl(url);
	}

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				JVWebViewActivity.this.finish();
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
		JVWebViewActivity.this.finish();
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
