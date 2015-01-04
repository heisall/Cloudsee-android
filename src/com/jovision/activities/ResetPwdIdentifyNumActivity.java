package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.SMSReceiver;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.MyLog;

public class ResetPwdIdentifyNumActivity extends BaseActivity implements
		OnClickListener, TextWatcher {

	private static final String TAG = "RESET_PWD";
	private static final int RETRY_INTERVAL = 60;
	private ProgressDialog pd;
	// 默认使用中国区号
	private static final String DEFAULT_COUNTRY_ID = "42";
	private EventHandler callback;
	private String currentId;
	private String currentCode;
	private EventHandler handler;
	private int time = RETRY_INTERVAL;
	Handler athandler = new Handler();
	// 国家号码规则
	private HashMap<String, String> countryRules;

	private Button backBtn;
	private Button rightBtn;
	public TextView titleTv;
	private EditText edtIdentifyNum;
	private Button nextButton;
	private String strAccount, strPhone, formatedPhone;
	private String SMS_APP_ID, SMS_APP_SECRET;
	private TextView tvGetNum, tvTopTips, tvFormatedPhone, tvPhoneNum;
	private String strIdentifyNum;
	private BroadcastReceiver smsReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reset_passwd_send_sms_verification);
		Bundle extras = getIntent().getExtras();
		if (null == extras) {
			finish();
			return;
		}
		pd = new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage(getResources().getString(R.string.reset_passwd_tips3));
		pd.show();

		strPhone = extras.getString("phone");
		strAccount = extras.getString("account");
		Log.i("TAG", "传递过来的" + strPhone);
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

		currentId = DEFAULT_COUNTRY_ID;
		String[] country = getCurrentCountry();
		if (country != null) {
			currentCode = country[1];
			Log.i(TAG, "currentCode:" + currentCode + ", countryName:"
					+ country[0]);
		}
		formatedPhone = String.format("%s  %s", currentCode, strPhone);

		handler = new EventHandler() {
			@SuppressWarnings("unchecked")
			public void afterEvent(final int event, final int result,
					final Object data) {
				ResetPwdIdentifyNumActivity.this.runOnUiThread(new Runnable() {
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
									showTextToast(des);
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
						edtIdentifyNum.setText(verifyCode);
					}
				});
			}
		});
		registerReceiver(smsReceiver, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
		// InitViews
		initViews();

		GetVerificationCode();
	}

	@Override
	public void onDestroy() {
		SMSSDK.unregisterEventHandler(handler);
		unregisterReceiver(smsReceiver);
		super.onDestroy();
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

	private void initViews() {
		backBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		titleTv = (TextView) findViewById(R.id.currentmenu);
		backBtn.setOnClickListener(this);
		edtIdentifyNum = (EditText) findViewById(R.id.edt_verification_code);
		edtIdentifyNum.addTextChangedListener(this);
		nextButton = (Button) findViewById(R.id.btn_next);
		nextButton.setOnClickListener(this);
		nextButton.setEnabled(false);
		titleTv.setText(R.string.reset_passwd_tips6);
		tvGetNum = (TextView) findViewById(R.id.tv_sms_tips);
		tvPhoneNum = (TextView) findViewById(R.id.tv_phone_code);
		tvGetNum.setOnClickListener(this);
		tvGetNum.setTextColor(getResources().getColor(R.color.link_color));
		tvGetNum.setText(getResources().getString(R.string.str_resend_code));
		tvGetNum.setEnabled(true);

		tvFormatedPhone = (TextView) findViewById(R.id.tv_formated_phone);
		tvFormatedPhone.setText(formatedPhone);
	}

	public void setRegisterCallback(EventHandler callback) {
		this.callback = callback;
	}

	public void onResume() {
		SMSSDK.registerEventHandler(handler);
		super.onResume();
	}

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
		checkPhoneNum(strPhone, currentCode);
	}

	// 检查电话号码
	private void checkPhoneNum(String phone, String code) {
		if (code.startsWith("+")) {
			code = code.substring(1);
		}

		if (TextUtils.isEmpty(phone)) {
			return;
		}

		String rule = countryRules.get(code);
		Pattern p = Pattern.compile(rule);
		Matcher m = p.matcher(phone);
		int resId = 0;
		if (!m.matches()) {
			showTextToast(getResources().getString(R.string.reset_passwd_tips5));
			return;
		}
		Log.e(TAG, "code:" + code + ",phone:" + phone);
		// pd.show();
		SMSSDK.getVerificationCode(code, phone.trim());
		showTextToast(R.string.str_sms_sent);
		countDown();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_sms_tips:
			// 请求发送短信验证码
			GetVerificationCode();
			break;
		case R.id.btn_next:
			// 验证填入的验证码
			strIdentifyNum = edtIdentifyNum.getText().toString().trim();
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
				tvGetNum.setTextColor(getResources().getColor(
						R.color.link_color));
				tvGetNum.setText(getResources().getString(
						R.string.str_resend_code));
				tvGetNum.setEnabled(true);
				athandler.removeCallbacks(timerRunable);
				SMSSDK.submitVerificationCode(currentCode, strPhone,
						strIdentifyNum);
			} else {
				showTextToast(R.string.reset_passwd_tips6);
			}
			break;
		case R.id.btn_left:
			finish();
			break;
		default:
			break;
		}
	}

	private void GetVerificationCode() {
		if (countryRules == null || countryRules.size() <= 0) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (pd != null) {
				pd.setMessage(getResources().getString(
						R.string.reset_passwd_tips7));
				pd.show();
			}

			SMSSDK.getSupportedCountries();
		} else {
			checkPhoneNum(strPhone, currentCode);
		}
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
					// countDown();
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

				if (result == SMSSDK.RESULT_COMPLETE) {
					// 跳转到设置密码界面
					Intent intent = new Intent(
							ResetPwdIdentifyNumActivity.this,
							ReSetNewPwdActivity.class);
					intent.putExtra("phone", strPhone);
					intent.putExtra("account", strAccount);
					startActivity(intent);
				} else {
					((Throwable) data).printStackTrace();
					// 验证码不正确
					showTextToast(R.string.reset_passwd_virificaition_code_wrong);
				}
			}
		});
	}

	private Runnable timerRunable = null;

	// 倒数计时
	private void countDown() {

		runOnUIThread(new Runnable() {
			public void run() {
				timerRunable = this;
				String strTips = getResources().getString(
						R.string.str_receive_sms_time);
				time--;
				if (time == 0) {
					tvGetNum.setTextColor(getResources().getColor(
							R.color.link_color));
					tvGetNum.setText(getResources().getString(
							R.string.str_resend_code));
					tvGetNum.setEnabled(true);
					time = RETRY_INTERVAL;
				} else {
					tvGetNum.setTextColor(getResources().getColor(R.color.gray));
					strTips = strTips.replace("SS", String.valueOf(time));
					tvGetNum.setText(strTips);
					tvGetNum.setEnabled(false);
					runOnUIThread(timerRunable, 1000);
				}
			}
		}, 1000);
	}

	private void runOnUIThread(Runnable runnable, int i) {
		// TODO Auto-generated method stub
		athandler.postDelayed(runnable, i);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (s.length() > 0) {
			nextButton.setEnabled(true);
		} else {
			nextButton.setEnabled(false);
		}
	}
}
