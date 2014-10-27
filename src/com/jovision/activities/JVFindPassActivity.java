package com.jovision.activities;

import android.R;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.jovision.commons.Url;
import com.jovision.utils.ConfigUtil;

public class JVFindPassActivity extends BaseActivity {

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;

	private WebView findPassWebView;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.findpass_layout);
		/** topBar **/
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_find_pass);
		back.setOnClickListener(myOnClickListener);
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setVisibility(View.GONE);

		findPassWebView = (WebView) findViewById(R.id.findpasswebview);

		findPassWebView.getSettings().setJavaScriptEnabled(true);
		findPassWebView.setWebChromeClient(new WebChromeClient());
		findPassWebView.requestFocus(View.FOCUS_DOWN);
		// 加快加载速度
		findPassWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		findPassWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		if (ConfigUtil.isLanZH()) {// 中文
			findPassWebView.loadUrl(Url.RESET_PWD_URL);
		} else {// 英文
			findPassWebView.loadUrl(Url.RESET_PWD_URL_EN);
		}

	}

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				JVFindPassActivity.this.finish();
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
		JVFindPassActivity.this.finish();
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
