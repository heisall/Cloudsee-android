package com.jovision.activities;

import android.graphics.Color;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;

public class JVRegisterActivity extends BaseActivity {
	private boolean agreeProtocol = true;

	private Button back;
	private TextView currentMenu;
	private Button rightButton;

	private Button regist;
	private ToggleButton agreeTBtn;
	private TextView agreeMent;
	private EditText userNameEditText;
	private WebView mWebView;
	private LinearLayout agreeLayout;
	private TextView registercode;
	private int Count = 10;
	private final int COUNT = 1000;

	/** 注册信息提示文本 */
	private TextView registTips;

	/** 用户名是否存在 */
	private int nameExists;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		/** 注册用户名检测成功 */
		case JVAccountConst.USERNAME_DETECTION_SUCCESS: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(21, 103, 215));
			registTips.setText(getResources().getString(
					R.string.str_user_not_exist2));
			break;
		}
		/** 注册用户名检测成功、失败 */
		case JVAccountConst.USERNAME_DETECTION_FAILED: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_user_has_exist));
			break;
		}
		/** 手机号不符合规则 */
		case JVAccountConst.PHONE_DETECTION_FAILED: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_phone_num_error));
			break;
		}
		/** 默认宏 */
		case JVAccountConst.DEFAULT: {
			dismissDialog();
			break;
		}
		case COUNT:
			//TODO
			if (Count == 0) {
				registercode.setText("验证码");
			}else {
				registercode.setText(Count+"");
			}
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// 进注册清缓存
		MySharedPreference.putString(Consts.CACHE_DEVICE_LIST, "");
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.regist_layout);
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.login_str_user_regist);
		rightButton = (Button) findViewById(R.id.btn_right);
		rightButton.setVisibility(View.GONE);

		regist = (Button) findViewById(R.id.regist);
		registercode = (TextView)findViewById(R.id.registercode);
		userNameEditText = (EditText) findViewById(R.id.registusername);
		registTips = (TextView) findViewById(R.id.regist_tips);
		agreeTBtn = (ToggleButton) findViewById(R.id.agree);
		agreeMent = (TextView) findViewById(R.id.agreement);
		mWebView = (WebView) findViewById(R.id.mywebview);
		agreeLayout = (LinearLayout) findViewById(R.id.registagreelayout);

		if (Consts.LANGUAGE_ZH == ConfigUtil
				.getLanguage2(JVRegisterActivity.this)) {// 中文
			mWebView.loadUrl("file:///android_asset/UserResign.html");
		} else {// 英文
			mWebView.loadUrl("file:///android_asset/UserResign_en.html");
		}

		// 给条款加超链接
		SpannableString sp = new SpannableString(getResources().getString(
				R.string.str_agreement));
		agreeMent.setText(sp);

		registercode.setOnClickListener(onClickListener);
		back.setOnClickListener(onClickListener);
		regist.setOnClickListener(onClickListener);
		agreeMent.setOnClickListener(onClickListener);
		agreeTBtn.setChecked(true);
		agreeProtocol = true;
		agreeTBtn.setOnCheckedChangeListener(onCheckedChangeListener);

		// 中性版本的隐藏注册协议
		if ("true".equalsIgnoreCase(statusHashMap.get(Consts.NEUTRAL_VERSION))) {
			agreeLayout.setVisibility(View.GONE);
		}
		userNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if ("".equalsIgnoreCase(userNameEditText.getText()
							.toString())) {
						registTips.setVisibility(View.VISIBLE);
						registTips.setTextColor(Color.rgb(217, 34, 38));
						registTips.setText(getResources().getString(
								R.string.login_str_username_notnull));
					} else {
						int res = AccountUtil.VerifyUserName(
								JVRegisterActivity.this, userNameEditText
								.getText().toString());
						if (res >= 0) {
							createDialog("", true);
							new Thread() {
								public void run() {
									nameExists = AccountUtil
											.isUserExsit(userNameEditText
													.getText().toString());
									if (JVAccountConst.USER_HAS_EXIST == nameExists) {
										handler.sendMessage(handler
												.obtainMessage(
														JVAccountConst.USERNAME_DETECTION_FAILED,
														0, 0));
									} else if (JVAccountConst.USER_NOT_EXIST == nameExists) {
										handler.sendMessage(handler
												.obtainMessage(
														JVAccountConst.USERNAME_DETECTION_SUCCESS,
														0, 0));
									} else if (JVAccountConst.PHONE_NOT_TRUE == nameExists) {
										handler.sendMessage(handler
												.obtainMessage(
														JVAccountConst.PHONE_DETECTION_FAILED,
														0, 0));
									} else {
										handler.sendMessage(handler
												.obtainMessage(
														JVAccountConst.DEFAULT,
														0, 0));
									}
								};
							}.start();
						} else {
							if (-1 == res) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_username_tips4));
							} else if (-2 == res) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_loginemail_tips));
							} else if (-3 == res) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_username_tips2));
							} else if (-4 == res) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_username_tips3));
							}
						}
					}
				}
			}
		});
	}
	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				backMethod();
				break;
			case R.id.registercode:
				//TODO
				break;
			case R.id.regist:
				userNameEditText = (EditText) findViewById(R.id.registusername);
				if ("".equalsIgnoreCase(userNameEditText.getText().toString())) {
					showTextToast(R.string.login_str_username_notnull);
				} else if (-1 == AccountUtil.VerifyUserName(
						JVRegisterActivity.this, userNameEditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_username_tips4);
				} else if (-2 == AccountUtil.VerifyUserName(
						JVRegisterActivity.this, userNameEditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginemail_tips);
				} else if (-3 == AccountUtil.VerifyUserName(
						JVRegisterActivity.this, userNameEditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_username_tips2);
				} else if (-4 == AccountUtil.VerifyUserName(
						JVRegisterActivity.this, userNameEditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_username_tips3);
				} else if (!agreeProtocol) {
					showTextToast(R.string.login_str_agreement_tips);
				} else {
					//TODO
				}
				break;
			case R.id.agreement:
				currentMenu.setText(R.string.str_agreement);
				mWebView.setVisibility(View.VISIBLE);
				break;
			}
		}

	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backMethod();
		}
		return false;
	}

	private void backMethod() {
		if (mWebView.getVisibility() == View.VISIBLE) {
			currentMenu.setText(R.string.login_str_user_regist);
			mWebView.setVisibility(View.GONE);
		} else {
			JVRegisterActivity.this.finish();
		}
	}

	/**
	 * 单选框事件
	 */
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			agreeProtocol = arg1;
			agreeTBtn.setChecked(arg1);
		}

	};
	@Override
	protected void onResume() {
		super.onResume();
	}
}
