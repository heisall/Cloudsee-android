package com.jovision.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.Url;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.tencent.stat.StatService;

public class JVRegisterCodeActivity extends BaseActivity {

	private EditText registerpwd;
	private EditText registerpwdsure;
	private Button register;
	private String account;

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

		setContentView(R.layout.registcode_layout);

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub

		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		account = getIntent().getStringExtra("phone");
		currentMenu.setText(R.string.login_str_user_regist);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		registerpwd = (EditText) findViewById(R.id.registpwd);
		registerpwdsure = (EditText) findViewById(R.id.registpwdsure);
		register = (Button) findViewById(R.id.regist);

		register.setOnClickListener(myonClickListener);
		leftBtn.setOnClickListener(myonClickListener);
	}

	OnClickListener myonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.regist:
				if ("".equalsIgnoreCase(registerpwd.getText().toString())) {
					showTextToast(R.string.login_str_loginpass1_notnull);
				} else if ("".equalsIgnoreCase(registerpwdsure.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginpass2_notnull);
				} else if (!registerpwd.getText().toString()
						.equals(registerpwdsure.getText().toString())) {
					showTextToast(R.string.login_str_loginpass_notsame);
					registerpwd.setText("");
					registerpwdsure.setText("");
					registerpwd.requestFocus();
				} else if (!AccountUtil.verifyPass(registerpwd.getText()
						.toString())) {
					showTextToast(R.string.login_str_password_tips1);
				} else {
					createDialog("", true);
					statusHashMap.put(Consts.KEY_USERNAME, account);
					statusHashMap.put(Consts.KEY_PASSWORD, registerpwd
							.getText().toString());

					// RegistThread registThread = new RegistThread(
					// JVRegisterActivity.this);
					// registThread.start();

					RegisterTask task = new RegisterTask();
					String[] strParams = new String[3];
					task.execute(strParams);
				}
				break;
			default:
				break;
			}
		}
	};
	private int errorCode = 0;
	private int loginRes1 = 0;

	// 用户注册线程
	private class RegisterTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int registerRes = -1;
			try {
				User user = new User();
				user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
				user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
				registerRes = AccountUtil.userRegister(user);
				if (JVAccountConst.SUCCESS == registerRes) {
					String strRes = AccountUtil.onLoginProcessV2(
							JVRegisterCodeActivity.this,
							statusHashMap.get(Consts.KEY_USERNAME),
							statusHashMap.get(Consts.KEY_PASSWORD),
							Url.SHORTSERVERIP, Url.LONGSERVERIP);
					JSONObject respObj = null;
					try {
						respObj = new JSONObject(strRes);
						loginRes1 = respObj.optInt("arg1", 1);
						// {"arg1":8,"arg2":0,"data":{"channel_ip":"210.14.156.66","online_ip":"210.14.156.66"},"desc":"after the judge and longin , begin the big switch...","result":0}

						String data = respObj.optString("data");
						if (null != data && !"".equalsIgnoreCase(data)) {
							JSONObject dataObj = new JSONObject(data);
							String channelIp = dataObj.optString("channel_ip");
							String onlineIp = dataObj.optString("online_ip");
							if (Consts.LANGUAGE_ZH == ConfigUtil
									.getServerLanguage()) {
								MySharedPreference.putString("ChannelIP",
										channelIp);
								MySharedPreference.putString("OnlineIP",
										onlineIp);
								MySharedPreference
										.putString("ChannelIP_en", "");
								MySharedPreference.putString("OnlineIP_en", "");
							} else {
								MySharedPreference.putString("ChannelIP_en",
										channelIp);
								MySharedPreference.putString("OnlineIP_en",
										onlineIp);
								MySharedPreference.putString("ChannelIP", "");
								MySharedPreference.putString("OnlineIP", "");
							}
						}

					} catch (JSONException e) {
						loginRes1 = JVAccountConst.LOGIN_FAILED_2;
						e.printStackTrace();
					}
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
				StatService.trackCustomEvent(JVRegisterCodeActivity.this,
						"Register", JVRegisterCodeActivity.this.getResources()
								.getString(R.string.census_register));
				Log.i("TAG", loginRes1 + "DDDDDDDDDDD");
				if (JVAccountConst.LOGIN_SUCCESS == loginRes1) {
					statusHashMap.put(Consts.LOCAL_LOGIN, "false");
					Intent emailIntent = new Intent(
							JVRegisterCodeActivity.this,
							JVBoundEmailActivity.class);
					String userPass = registerpwd.getText().toString();
					emailIntent.putExtra("AutoLogin", true);
					emailIntent.putExtra("UserName", account);
					emailIntent.putExtra("UserPass", userPass);
					JVRegisterCodeActivity.this.startActivity(emailIntent);
					JVRegisterCodeActivity.this.finish();
				}
				showTextToast(R.string.login_str_regist_success);
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
			createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

}
