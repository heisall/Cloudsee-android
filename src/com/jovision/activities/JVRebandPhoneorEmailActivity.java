package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.test.JVACCOUNT;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.SMSReceiver;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.JVMoreFragment.CheckUserInfoTask;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.GetPhoneNumber;

public class JVRebandPhoneorEmailActivity extends BaseActivity implements
TextWatcher {

	private Button regist;
	private ToggleButton agreeTBtn;
	private TextView agreeMent;
	private EditText userNameEditText;
	private WebView mWebView;
	private LinearLayout agreeLayout;
	private TextView registercode;
	private EditText code;
	private boolean isregister;
	private GetPhoneNumber phoneNumber;
	private boolean agreeProtocol = true;

	/** 注册信息提示文本 */
	private TextView registTips;
	/** 用户名是否存在 */
	private int nameExists;
	private static final String TAG = "RESET_PWD";
	private static final int RETRY_INTERVAL = 60;
	private ProgressDialog pd;
	// 默认使用中国区号
	private static final String DEFAULT_COUNTRY_ID = "42";
	private String currentCode;
	private EventHandler mhandler;
	private int time = RETRY_INTERVAL;
	Handler athandler = new Handler();
	// 国家号码规则
	private HashMap<String, String> countryRules;

	public TextView titleTv;
	private String SMS_APP_ID, SMS_APP_SECRET;
	private String strIdentifyNum;
	private BroadcastReceiver smsReceiver;

	private boolean stop;
	private boolean isclick = false;

	private int isPhone;

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
			if (isPhone == 1) {
				if (isclick) {
					SMSSDK.getVerificationCode(currentCode, userNameEditText
							.getText().toString().trim());
					countDown();
					isregister = false;
				}
			}else {
				SentEmailCondTask task = new SentEmailCondTask();
				task.execute("");
			}
			break;
		}
		/** 注册用户名检测成功、失败 */
		case JVAccountConst.USERNAME_DETECTION_FAILED: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_user_has_exist));
			if (isPhone == 1) {
				isregister = true;
			}
			break;
		}
		/** 邮箱不符合规则 */
		case JVAccountConst.MAIL_DETECTION_FAILED: {
			dismissDialog();
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.login_str_loginemail_tips));
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
		// 进注册清缓存
		MySharedPreference.putString(Consts.CACHE_DEVICE_LIST, "");
		isPhone = getIntent().getIntExtra("isphone", -1);
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.reband_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText("解除绑定");
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);

		regist = (Button) findViewById(R.id.regist);
		code = (EditText) findViewById(R.id.code);
		registercode = (TextView) findViewById(R.id.registercode);
		userNameEditText = (EditText) findViewById(R.id.registusername);
		if (isPhone == 1) {
			userNameEditText.setHint("请输入手机号");
		}else {
			userNameEditText.setHint("请输入邮箱");
		}
		registTips = (TextView) findViewById(R.id.regist_tips);
		agreeTBtn = (ToggleButton) findViewById(R.id.agree);
		agreeMent = (TextView) findViewById(R.id.agreement);
		agreeMent.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		agreeMent.getPaint().setAntiAlias(true);
		mWebView = (WebView) findViewById(R.id.mywebview);
		agreeLayout = (LinearLayout) findViewById(R.id.registagreelayout);
		pd = new ProgressDialog(this);
		if (Consts.LANGUAGE_ZH == ConfigUtil
				.getLanguage2(JVRebandPhoneorEmailActivity.this)) {// 中文
			mWebView.loadUrl("file:///android_asset/UserResign.html");
		} else if (Consts.LANGUAGE_ZHTW == ConfigUtil
				.getLanguage2(JVRebandPhoneorEmailActivity.this)) {// 台湾
			mWebView.loadUrl("file:///android_asset/UserResign_tw.html");
		} else {// 英文
			mWebView.loadUrl("file:///android_asset/UserResign_en.html");
		}

		// 给条款加超链接
		SpannableString sp = new SpannableString(getResources().getString(
				R.string.str_agreement));
		agreeMent.setText(sp);

		code.addTextChangedListener(this);
		registercode.setOnClickListener(onClickListener);
		leftBtn.setOnClickListener(onClickListener);
		regist.setOnClickListener(onClickListener);
		agreeMent.setOnClickListener(onClickListener);
		agreeTBtn.setChecked(true);
		agreeProtocol = true;
		agreeTBtn.setOnCheckedChangeListener(onCheckedChangeListener);

		// 中性版本的隐藏注册协议
		if ("true".equalsIgnoreCase(statusHashMap.get(Consts.NEUTRAL_VERSION))) {
			agreeLayout.setVisibility(View.GONE);
		}
		// appliction MetaData读取
		ApplicationInfo info;
		try {
			info = this.getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);

			SMS_APP_ID = info.metaData.getString("MOB_SMS_V1_APP_ID");
			SMS_APP_SECRET = info.metaData.getString("MOB_SMS_V1_APP_SECRET");
			Log.i(TAG, "SMS_APP_ID:" + SMS_APP_ID + ", SMS_APP_SECRET:"
					+ SMS_APP_SECRET);

			SMSSDK.initSDK(this, SMS_APP_ID, SMS_APP_SECRET);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mhandler = new EventHandler() {
			@SuppressWarnings("unchecked")
			public void afterEvent(final int event, final int result,
					final Object data) {
				JVRebandPhoneorEmailActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
						if (result == SMSSDK.RESULT_COMPLETE) {
							if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
								// 请求支持国家列表
								onCountryListGot((ArrayList<HashMap<String, Object>>) data);
								MyLog.d(TAG, "获取支持国家列表成功");
							} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
								// 获取验证码成功后的执行动作
								afterGet(result, data);
							}
							if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
								// 提交验证码
								afterSubmit(result, data);
							}
						} else {
							// 根据服务器返回的网络错误，给toast提示
							try {
								((Throwable) data).printStackTrace();
								Throwable throwable = (Throwable) data;

								JSONObject object = new JSONObject(throwable
										.getMessage());
								String des = object.optString("detail");
								if (!TextUtils.isEmpty(des)) {
									Log.i("TAG", des);
									String errorstring1 = JVRebandPhoneorEmailActivity.this
											.getResources().getString(
													R.string.str_error_vercode);
									Log.i("TAG", errorstring1);
									int lan = ConfigUtil
											.getLanguage2(JVRebandPhoneorEmailActivity.this);
									if (errorstring1.equals(des)
											&& Consts.LANGUAGE_ZHTW == lan) {
										showTextToast(R.string.str_error_vercodetw);
									} else if (errorstring1.equals(des)
											&& Consts.LANGUAGE_ZH == lan) {
										showTextToast(R.string.str_error_vercode);
									} else {
										showTextToast(des);
									}
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							// 如果木有找到资源，默认提示
						}
					}
				});
			}
		};
		smsReceiver = new SMSReceiver(new SMSSDK.VerifyCodeReadListener() {
			@Override
			public void onReadVerifyCode(final String verifyCode) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						registercode.setText(verifyCode);
					}
				});
			}
		});
		registerReceiver(smsReceiver, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
		// InitViews
		String[] country = getCurrentCountry();
		if (country != null) {
			currentCode = country[1];
			Log.i(TAG, "currentCode:" + currentCode + ", countryName:"
					+ country[0]);
		}
		userNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (!ConfigUtil
							.isConnected(JVRebandPhoneorEmailActivity.this)) {
						alertNetDialog();
					} else {
						// checkPhoneNum(userNameEditText.getText().toString(),
						// currentCode);
						if (isPhone == 1) {
							phoneNumber = new GetPhoneNumber(userNameEditText
									.getText().toString());
							if ("".equalsIgnoreCase(userNameEditText.getText()
									.toString())) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_username_notnull));
							} else if (phoneNumber.matchNum() == 4
									|| phoneNumber.matchNum() == 5) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.str_phone_num_error));
							} else {
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
											isregister = true;
										} else if (JVAccountConst.USER_NOT_EXIST == nameExists) {
											handler.sendMessage(handler
													.obtainMessage(
															JVAccountConst.USERNAME_DETECTION_SUCCESS,
															0, 0));
											isregister = false;
											isclick = false;
										}
									};
								}.start();
							}
						}
						else if(isPhone == 0){
							if ("".equalsIgnoreCase(userNameEditText.getText()
									.toString())) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_loginemail_notnull));
							} else {
								createDialog("", hasFocus);
								new Thread() {
									public void run() {
										nameExists = AccountUtil
												.isUserExsit(userNameEditText
														.getText().toString());
										if (AccountUtil.verifyEmail(userNameEditText.getText().toString())) {
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
											} else {
												handler.sendMessage(handler
														.obtainMessage(
																JVAccountConst.DEFAULT,
																0, 0));
											}
										}else {
											handler.sendMessage(handler
													.obtainMessage(
															JVAccountConst.MAIL_DETECTION_FAILED,
															0, 0));
										}
									};
								}.start();
							}
						}
					}
				}
			}
		});
		GetVerificationCode();
	}

	private String[] getCurrentCountry() {
		String mcc = getMCC();
		String[] country = null;
		if (!TextUtils.isEmpty(mcc)) {
			country = SMSSDK.getCountryByMCC(mcc);
		}

		if (country == null) {
			Log.w("SMSSDK", "no country found by MCC: " + mcc);
			country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
		}
		return country;
	}

	private String getMCC() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
		String networkOperator = tm.getNetworkOperator();

		// 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
		String simOperator = tm.getSimOperator();

		String mcc = null;
		if (!TextUtils.isEmpty(networkOperator)
				&& networkOperator.length() >= 5) {
			mcc = networkOperator.substring(0, 3);
		}

		if (TextUtils.isEmpty(mcc)) {
			if (!TextUtils.isEmpty(simOperator) && simOperator.length() >= 5) {
				mcc = simOperator.substring(0, 3);
			}
		}

		return mcc;
	}

	private void MakeSure() {
		isregister = false;
		createDialog("", true);
		nameExists = AccountUtil.isUserExsit(userNameEditText.getText()
				.toString());
		if (JVAccountConst.USER_HAS_EXIST == nameExists) {
			handler.sendMessage(handler.obtainMessage(
					JVAccountConst.USERNAME_DETECTION_FAILED, 0, 0));
			isregister = true;
		} else if (JVAccountConst.USER_NOT_EXIST == nameExists) {
			handler.sendMessage(handler.obtainMessage(
					JVAccountConst.USERNAME_DETECTION_SUCCESS, 0, 0));
			isclick = true;
		}
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		SMSSDK.unregisterEventHandler(mhandler);
		unregisterReceiver(smsReceiver);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				backMethod();
				break;
			case R.id.registercode:
				if (isPhone == 1) {
					phoneNumber = new GetPhoneNumber(userNameEditText.getText()
							.toString());
					if (!ConfigUtil.isConnected(JVRebandPhoneorEmailActivity.this)) {
						alertNetDialog();
					} else if (phoneNumber.matchNum() == 4
							|| phoneNumber.matchNum() == 5) {
						registTips.setVisibility(View.VISIBLE);
						registTips.setTextColor(Color.rgb(217, 34, 38));
						registTips.setText(getResources().getString(
								R.string.str_phone_num_error));
					} else {
						MakeSure();
					}
				}else {
					if (!ConfigUtil.isConnected(JVRebandPhoneorEmailActivity.this)) {
						alertNetDialog();
					} else {
						if(!"已发送".equals(registercode.getText().toString())){
							if ("".equalsIgnoreCase(userNameEditText.getText()
									.toString())) {
								registTips.setVisibility(View.VISIBLE);
								registTips.setTextColor(Color.rgb(217, 34, 38));
								registTips.setText(getResources().getString(
										R.string.login_str_loginemail_notnull));
							} else {
								createDialog("", true);
								new Thread() {
									public void run() {
										nameExists = AccountUtil
												.isUserExsit(userNameEditText
														.getText().toString());
										if (AccountUtil.verifyEmail(userNameEditText.getText().toString())) {
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
											} else {
												handler.sendMessage(handler
														.obtainMessage(
																JVAccountConst.DEFAULT,
																0, 0));
											}
										}else {
											handler.sendMessage(handler
													.obtainMessage(
															JVAccountConst.MAIL_DETECTION_FAILED,
															0, 0));
										}
									};
								}.start();
							}
						} else {
							showTextToast("邮件已发送至邮箱");
						}
					}
				}
				break;
			case R.id.regist:
				if (isPhone == 1) {
					isclick = false;
					if (!agreeProtocol) {
						showTextToast(R.string.login_str_agreement_tips);
					} else if ((!"".equals(userNameEditText.getText().toString()) && !isregister)
							&& !"".equals(code.getText().toString())) {
						// 验证填入的验证码
						strIdentifyNum = code.getText().toString().trim();
						Log.i("TAG", currentCode
								+ userNameEditText.getText().toString()
								+ strIdentifyNum);
						// 提交验证
						if (!TextUtils.isEmpty(currentCode)) {
							if (pd != null && pd.isShowing()) {
								pd.dismiss();
							}
							if (pd != null) {
								pd.setMessage(getResources().getString(
										R.string.reset_passwd_tips3));
								pd.show();
							}
							Log.i("TAG", currentCode
									+ userNameEditText.getText().toString()
									+ strIdentifyNum);
							SMSSDK.submitVerificationCode(currentCode,
									userNameEditText.getText().toString(),
									strIdentifyNum);
						}
					} else if (isregister) {
						showTextToast(getResources().getString(
								R.string.str_user_has_exist));
					} else if ("".equals(registercode.getText().toString())) {
						showTextToast(R.string.reset_passwd_tips6);
					}
				}else {
					if(AccountUtil.verifyEmail(userNameEditText.getText().toString())){
						SureEmailCondTask  task = new SureEmailCondTask();
						String parms [] = new String [2]; 
						parms [0] = code.getText().toString();
						task.execute(parms);
					}else{ 
						handler.sendMessage(handler
								.obtainMessage(
										JVAccountConst.MAIL_DETECTION_FAILED,
										0, 0));
					}
				}
				break;
			case R.id.agreement:
				stop = true;
				currentMenu.setText(R.string.str_agreement);
				mWebView.setVisibility(View.VISIBLE);
				break;
			}
		}

	};

	// // 检查电话号码
	// private void checkPhoneNum(String phone, String code) {
	// if (code.startsWith("+")) {
	// code = code.substring(1);
	// }
	//
	// if (TextUtils.isEmpty(phone)) {
	// return;
	// }
	// if ("".equals(code)) {
	// String rule = countryRules.get(code);
	// Pattern p = Pattern.compile(rule);
	// Matcher m = p.matcher(phone);
	// if (!m.matches()) {
	// showTextToast(getResources().getString(
	// R.string.reset_passwd_tips5));
	// return;
	// }
	// }
	// }

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
			JVRebandPhoneorEmailActivity.this.finish();
		}
	}

	/**
	 * 单选框事件
	 */
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			agreeTBtn.setChecked(arg1);
			agreeProtocol = arg1;
		}

	};

	private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
		// 解析国家列表
		for (HashMap<String, Object> country : countries) {
			String code = (String) country.get("zone");
			String rule = (String) country.get("rule");
			if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
				continue;
			}

			if (countryRules == null) {
				countryRules = new HashMap<String, String>();
			}
			countryRules.put(code, rule);
		}
	}

	private void GetVerificationCode() {
		SMSSDK.getSupportedCountries();
	}

	/**
	 * 获取验证码成功后,的执行动作
	 * 
	 * @param result
	 * @param data
	 */
	private void afterGet(final int result, final Object data) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}

				if (result == SMSSDK.RESULT_COMPLETE) {
					time = RETRY_INTERVAL;
				} else {
					((Throwable) data).printStackTrace();
					Throwable throwable = (Throwable) data;
					// 根据服务器返回的网络错误，给toast提示
					try {
						JSONObject object = new JSONObject(
								throwable.getMessage());
						String des = object.optString("detail");
						if (!TextUtils.isEmpty(des)) {
							showTextToast(des);
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// 如果木有找到资源，默认提示
				}
			}
		});
	}

	/**
	 * 提交验证码成功后的执行事件
	 * 
	 * @param result
	 * @param data
	 */
	private void afterSubmit(final int result, final Object data) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
				if (result == SMSSDK.RESULT_COMPLETE) {//验证通过
					showTextToast("哈哈");
					//					// 跳转到设置密码界面
					//					Intent intent = new Intent(JVRebandPhoneorEmailActivity.this,
					//							JVRegisterCodeActivity.class);
					//					intent.putExtra("phone", userNameEditText.getText()
					//							.toString());
					//					startActivity(intent);
				} else {
					((Throwable) data).printStackTrace();
					// 验证码不正确
					showTextToast(R.string.reset_passwd_virificaition_code_wrong);
				}
			}
		});
	}

	private void countDown() {

		runOnUIThread(new Runnable() {
			public void run() {
				String recode = getResources().getString(
						R.string.str_resend_code);
				time--;
				if (time == 0 || stop) {
					registercode.setText(recode);
					registercode.setTextColor(getResources().getColor(
							R.color.white));
					registercode.setEnabled(true);
					time = RETRY_INTERVAL;
					stop = false;
					registercode.setBackgroundResource(R.drawable.blue_bg);
				} else {
					registercode.setText(recode + "\n" + "(" + time + ")");
					registercode.setEnabled(false);
					runOnUIThread(this, 1000);
					if (time == 59) {
						registercode.setBackgroundResource(R.drawable.vercode);
						showTextToast(R.string.str_sms_sent);
					}
				}
			}
		}, 1000);
	}
	private void runOnUIThread(Runnable runnable, int i) {
		// TODO Auto-generated method stub
		athandler.postDelayed(runnable, i);
	}

	@Override
	protected void onResume() {
		SMSSDK.registerEventHandler(mhandler);
		super.onResume();
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			regist.setEnabled(true);
		} else {
			regist.setEnabled(false);
		}
	}

	// 获取邮箱验证码线程
	private class SentEmailCondTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int Code = -1;
			try {
				createDialog("",true);
				Code = JVACCOUNT.SendResetMail(userNameEditText.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Code;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			Log.i("TAG", "发送邮箱"+result);
			switch (result) {
			case 0:
				showTextToast("发送过去了");
				registercode.setText("已发送");
				registercode.setBackgroundResource(R.drawable.vercode);
				break;
			case -1:
				registercode.setText(getResources().getString(
						R.string.str_resend_code));
				showTextToast("邮件发送错误");
				break;
			case -11:
				registercode.setText(getResources().getString(
						R.string.str_resend_code));
				showTextToast("邮件发送错误");
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("", false);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}
	// 邮箱验证线程
	private class SureEmailCondTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int Code = -1;
			try {
				createDialog("",true);
				Code = JVACCOUNT.RandCodeCheck(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Code;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			Log.i("TAG", "验证邮箱"+result);
			switch (result) {
			case 0:
				showTextToast("yanzhen");
				break;
			case -1:
				registercode.setBackgroundResource(R.drawable.vercode);
				showTextToast("验证码错误");
				break;
			case -31:
				registercode.setBackgroundResource(R.drawable.vercode);
				showTextToast("验证码错误");
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("", false);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}
}
