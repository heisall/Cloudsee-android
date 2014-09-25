package com.jovision.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.Handler.LoginHandler;
import com.jovision.Handler.RegistHandler;
import com.jovision.commons.JVAccountConst;
import com.jovision.thread.RegistThread;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;

public class JVRegisterActivity extends BaseActivity {
	private boolean agreeProtocol = true;

	private Button back;
	private TextView currentMenu;
	private Button rightButton;

	private Button regist;
	private CheckBox agreeCheckBox;
	private TextView agreeMent;
	private EditText userNameEditText;
	private EditText pass1EditText;
	private EditText pass2EditText;
	private WebView mWebView;
	private LinearLayout agreeLayout;

	/** 注册信息提示文本 */
	private TextView registTips;
	private TextView registTips2;
	private TextView registTips3;
	private RegistHandler registHandler;
	private LoginHandler loginHandler;

	/** 用户名是否存在 */
	private int nameExists;
	private int focusView;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		/** 注册回调 */
		case Consts.REGIST_FUNCTION: {
			Message msg = Message.obtain();
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.what = what;
			registHandler.sendMessage(msg);
		}
		/** 登陆返回的结果 */
		case Consts.LOGIN_FUNCTION: {
			Message msg = Message.obtain();
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.what = what;
			loginHandler.sendMessage(msg);
			break;
		}
		/** 注册用户名检测成功 */
		case JVAccountConst.USERNAME_DETECTION_SUCCESS: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(21, 103, 215));
			registTips.setText(getResources().getString(
					R.string.str_user_not_exist2));
			inputMethod(focusView);
			break;
		}
		/** 注册用户名检测成功、失败 */
		case JVAccountConst.USERNAME_DETECTION_FAILED: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_user_has_exist));
			inputMethod(focusView);
			break;
		}
		/** 手机号不符合规则 */
		case JVAccountConst.PHONE_DETECTION_FAILED: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_phone_num_error));
			inputMethod(focusView);
			break;
		}
		/** 默认宏 */
		case JVAccountConst.DEFAULT: {
			dismissDialog();
			break;
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.handleMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		registHandler = new RegistHandler(this);
		loginHandler = new LoginHandler(this, false);
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
		userNameEditText = (EditText) findViewById(R.id.registusername);
		pass1EditText = (EditText) findViewById(R.id.registpass1);
		pass2EditText = (EditText) findViewById(R.id.registpass2);
		registTips = (TextView) findViewById(R.id.regist_tips);
		registTips2 = (TextView) findViewById(R.id.regist_tips2);
		registTips3 = (TextView) findViewById(R.id.regist_tips3);
		agreeCheckBox = (CheckBox) findViewById(R.id.agree);
		agreeMent = (TextView) findViewById(R.id.agreement);
		mWebView = (WebView) findViewById(R.id.mywebview);
		agreeLayout = (LinearLayout) findViewById(R.id.registagreelayout);

		if (ConfigUtil.isLanZH()) {// 中文
			mWebView.loadUrl("file:///android_asset/UserResign.html");
		} else {// 英文
			mWebView.loadUrl("file:///android_asset/UserResign_en.html");
		}

		// 给条款加超链接
		SpannableString sp = new SpannableString(getResources().getString(
				R.string.str_agreement));
		agreeMent.setText(sp);

		back.setOnClickListener(onClickListener);
		regist.setOnClickListener(onClickListener);
		agreeMent.setOnClickListener(onClickListener);
		agreeCheckBox.setChecked(true);
		agreeProtocol = true;
		agreeCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

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
						int res = AccountUtil.VerifyUserName(userNameEditText
								.getText().toString());
						if (res >= 0) {
							createDialog("");
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
		pass1EditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if ("".equalsIgnoreCase(pass1EditText.getText().toString())) {
						registTips2.setVisibility(View.VISIBLE);
						registTips2.setTextColor(Color.rgb(217, 34, 38));
						registTips2.setText(getResources().getString(
								R.string.login_str_loginpass1_notnull));
					} else if (!AccountUtil.verifyPass(pass1EditText.getText()
							.toString())) {
						registTips2.setVisibility(View.VISIBLE);
						registTips2.setTextColor(Color.rgb(217, 34, 38));
						registTips2.setText(getResources().getString(
								R.string.login_str_password_tips1));
					} else {
						registTips2.setVisibility(View.INVISIBLE);
					}
				} else {
					focusView = 1;
				}
			}
		});
		pass2EditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if ("".equalsIgnoreCase(pass2EditText.getText().toString())) {
						registTips3.setVisibility(View.VISIBLE);
						registTips3.setTextColor(Color.rgb(217, 34, 38));
						registTips3.setText(getResources().getString(
								R.string.login_str_loginpass2_notnull));
					} else if (!pass1EditText.getText().toString()
							.equals(pass2EditText.getText().toString())) {
						registTips3.setVisibility(View.VISIBLE);
						registTips3.setTextColor(Color.rgb(217, 34, 38));
						registTips3.setText(getResources().getString(
								R.string.login_str_loginpass_notsame));
					} else {
						registTips3.setVisibility(View.INVISIBLE);
					}
				} else {
					focusView = 2;
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
			case R.id.regist:
				userNameEditText = (EditText) findViewById(R.id.registusername);
				pass1EditText = (EditText) findViewById(R.id.registpass1);
				pass2EditText = (EditText) findViewById(R.id.registpass2);
				if ("".equalsIgnoreCase(userNameEditText.getText().toString())) {
					showTextToast(R.string.login_str_username_notnull);
				} else if ("".equalsIgnoreCase(pass1EditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginpass1_notnull);
				} else if ("".equalsIgnoreCase(pass2EditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginpass2_notnull);
				} else if (!pass1EditText.getText().toString()
						.equals(pass2EditText.getText().toString())) {
					showTextToast(R.string.login_str_loginpass_notsame);
					pass1EditText.setText("");
					pass2EditText.setText("");
					pass1EditText.requestFocus();
				} else if (-1 == AccountUtil.VerifyUserName(userNameEditText
						.getText().toString())) {
					showTextToast(R.string.login_str_username_tips4);
				} else if (-2 == AccountUtil.VerifyUserName(userNameEditText
						.getText().toString())) {
					showTextToast(R.string.login_str_loginemail_tips);
				} else if (-3 == AccountUtil.VerifyUserName(userNameEditText
						.getText().toString())) {
					showTextToast(R.string.login_str_username_tips2);
				} else if (-4 == AccountUtil.VerifyUserName(userNameEditText
						.getText().toString())) {
					showTextToast(R.string.login_str_username_tips3);
				} else if (!AccountUtil.verifyPass(pass1EditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_password_tips1);
				} else if (!agreeProtocol) {
					showTextToast(R.string.login_str_agreement_tips);
				} else {
					createDialog("");
					statusHashMap.put(Consts.KEY_USERNAME, userNameEditText
							.getText().toString());
					statusHashMap.put(Consts.KEY_PASSWORD, pass1EditText
							.getText().toString());
					RegistThread registThread = new RegistThread(
							JVRegisterActivity.this);
					registThread.start();
				}

				break;
			case R.id.agreement:
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
			agreeCheckBox.setChecked(arg1);
		}

	};

	private void inputMethod(final int view) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) JVRegisterActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (1 == view) {
					pass1EditText.setFocusable(true);
					pass1EditText.setFocusableInTouchMode(true);
					pass1EditText.requestFocus();
					imm.showSoftInput(pass1EditText, 0);
				} else if (2 == view) {
					pass2EditText.setFocusable(true);
					pass2EditText.setFocusableInTouchMode(true);
					pass2EditText.requestFocus();
					imm.showSoftInput(pass2EditText, 0);
				}
			}
		}, 500);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
