package com.jovision.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
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
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
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
	private EditText pass1EditText;
	private EditText pass2EditText;
	private WebView mWebView;
	private LinearLayout agreeLayout;

	/** 注册信息提示文本 */
	private TextView registTips;
	private TextView registTips2;
	private TextView registTips3;

	/** 用户名是否存在 */
	private int nameExists;
	private int focusView;

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
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
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
		agreeTBtn = (ToggleButton) findViewById(R.id.agree);
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

					// RegistThread registThread = new RegistThread(
					// JVRegisterActivity.this);
					// registThread.start();

					RegisterTask task = new RegisterTask();
					String[] strParams = new String[3];
					task.execute(strParams);
				}

				break;
			case R.id.agreement:
				mWebView.setVisibility(View.VISIBLE);
				break;
			}
		}

	};

	private int errorCode = 0;
	private int verifyCode = 0;

	// 用户注册线程
	private class RegisterTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int registerRes = -1;
			try {
				registerRes = AccountUtil.isUserExsit(statusHashMap
						.get(Consts.KEY_USERNAME));
				if (JVAccountConst.USER_HAS_EXIST == registerRes) {
					return registerRes;
				} else if (JVAccountConst.PHONE_NOT_TRUE == registerRes) {
					return registerRes;
				}
				User user = new User();
				user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
				user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
				registerRes = AccountUtil.userRegister(user);
				if (JVAccountConst.SUCCESS == registerRes) {
					verifyCode = AccountUtil.VerifyUserName(user.getUserName());
					return registerRes;
				} else {
					errorCode = registerRes;
					return JVAccountConst.FAILED;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return registerRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			switch (result) {
			case JVAccountConst.SUCCESS:// 注册成功
				if (verifyCode > 0) {
					Intent emailIntent = new Intent(JVRegisterActivity.this,
							JVBoundEmailActivity.class);
					String userName = userNameEditText.getText().toString();
					String userPass = pass1EditText.getText().toString();
					emailIntent.putExtra("AutoLogin", true);
					emailIntent.putExtra("UserName", userName);
					emailIntent.putExtra("UserPass", userPass);
					JVRegisterActivity.this.startActivity(emailIntent);
					JVRegisterActivity.this.finish();
				} else {
					Intent intent = new Intent();
					intent.setClass(JVRegisterActivity.this,
							JVLoginActivity.class);
					String userName = userNameEditText.getText().toString();
					String userPass = pass1EditText.getText().toString();
					intent.putExtra("AutoLogin", true);
					intent.putExtra("UserName", userName);
					intent.putExtra("UserPass", userPass);
					JVRegisterActivity.this.startActivity(intent);
					JVRegisterActivity.this.finish();
				}
				showTextToast(R.string.login_str_regist_success);
				break;
			case JVAccountConst.USER_HAS_EXIST:// 账号已注册
				showTextToast(R.string.str_user_has_exist);
				break;
			case JVAccountConst.PHONE_NOT_TRUE:// 邮箱已注册
				showTextToast(R.string.str_phone_num_error);
				break;
			case JVAccountConst.FAILED:// 注册失败
				if (0 < errorCode) {
					if (JVAccountConst.PASSWORD_ERROR == errorCode) {
						showTextToast(R.string.str_user_password_error);
					} else if (JVAccountConst.SESSION_NOT_EXSIT == errorCode) {
						showTextToast(R.string.str_session_not_exist);
					} else if (JVAccountConst.USER_HAS_EXIST == errorCode) {
						showTextToast(R.string.str_user_has_exist);
					} else if (JVAccountConst.USER_NOT_EXIST == errorCode) {
						showTextToast(R.string.str_user_not_exist2);
					} else {
						showTextToast(R.string.str_other_error);
					}
				} else {
					if (-5 == errorCode) {
						showTextToast(R.string.str_error_code_5);
					} else if (-6 == errorCode) {
						showTextToast(R.string.str_error_code_6);
					} else {
						showTextToast(getResources().getString(
								R.string.str_error_code)
								+ (errorCode - 1000));
					}
				}
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

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
			agreeTBtn.setChecked(arg1);
		}

	};

	private void inputMethod(final int view) {
		// Timer timer = new Timer();
		// timer.schedule(new TimerTask() {
		// public void run() {
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
		// }
		// }, 500);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
