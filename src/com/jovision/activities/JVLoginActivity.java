package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.test.JVACCOUNT;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.UserSpinnerAdapter;
import com.jovision.bean.Device;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.Url;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.UserUtil;
import com.tencent.stat.StatService;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SetJavaScriptEnabled")
public class JVLoginActivity extends BaseActivity {

	private final String TAG = "JVLoginActivity";
	private String userName = "";
	private String passWord = "";

	private int userListIndex;
	/** userlogin Fuction */
	private EditText userNameET;
	private EditText passwordET;
	private RelativeLayout userNameLayout;

	private Button onlineLoginBtn;
	private Animation mAnimationRight;// 演示点动画
	private TextView registBtn;
	private Button localLoginBtn;
	private TextView showPointBtn;
	private TextView findPassTV;

	// 下拉箭头图片组件
	private ImageView moreUserIV;
	private ArrayList<User> userList = new ArrayList<User>();
	private PopupWindow pop;
	private UserSpinnerAdapter userAdapter;
	private ListView userListView;

	public static ArrayList<Device> deviceList = new ArrayList<Device>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_SHOW_PRO: {
			createDialog("", false);
			break;
		}
		case Consts.WHAT_DELETE_USER:
			pop.dismiss();
			userNameET.setText("");
			passwordET.setText("");
			UserUtil.deleteUser(arg1);

			userList = UserUtil.getUserList();
			if (null == userList || 0 == userList.size()) {
				moreUserIV.setVisibility(View.GONE);
			} else {
				moreUserIV.setVisibility(View.VISIBLE);
				userAdapter.setData(userList);
				userAdapter.notifyDataSetChanged();
			}
			break;
		case Consts.WHAT_SELECT_USER:
			userNameET.setText(((User) obj).getUserName());
			if (arg2 != Consts.WHAT_CLICK_USER) {
				passwordET.setText(((User) obj).getUserPwd());
			}
			moreUserIV.setImageResource(R.drawable.login_pull_icon);
			pop.dismiss();
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		MySharedPreference.init(getApplication());
		ConfigUtil.getJNIVersion();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.login_layout);
		userList = UserUtil.getUserList();
		/** userlogin Fuction */
		userNameET = (EditText) findViewById(R.id.username_et);
		passwordET = (EditText) findViewById(R.id.password_et);
		onlineLoginBtn = (Button) findViewById(R.id.onlinelogin_btn);
		findPassTV = (TextView) findViewById(R.id.findpass_tv);
		showPointBtn = (TextView) findViewById(R.id.showpoint_btn);
		registBtn = (TextView) findViewById(R.id.regist_btn);
		localLoginBtn = (Button) findViewById(R.id.locallogin_btn);

		findPassTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		findPassTV.getPaint().setAntiAlias(true);
		registBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		registBtn.getPaint().setAntiAlias(true);

		if (null != getIntent().getStringExtra("UserName")) {
			userNameET.setText(getIntent().getStringExtra("UserName"));
			passwordET.setText(getIntent().getStringExtra("PassWord"));
		} else {
			if (null != userList && 0 != userList.size()) {
				userNameET.setText(userList.get(0).getUserName());
				userNameET.setSelection(userList.get(0).getUserName().length());
			}
		}
		userNameET.setOnClickListener(myOnClickListener);
		userNameET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.equals(statusHashMap.get(Consts.KEY_USERNAME))) {
					passwordET.setText("");
				}
			}
		});

		moreUserIV = (ImageView) findViewById(R.id.moreuser_iv);
		userNameLayout = (RelativeLayout) findViewById(R.id.user_layout);
		if (null == userList || 0 == userList.size()) {
			moreUserIV.setVisibility(View.GONE);
		}
		// 设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
		moreUserIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(userNameET.getWindowToken(), 0);
				if (pop == null) {
					if (null != userList && 0 != userList.size()) {
						userAdapter = new UserSpinnerAdapter(
								JVLoginActivity.this);
						userAdapter.setData(userList);
						userAdapter.setName(userNameET.getText().toString());
						userListView = new ListView(JVLoginActivity.this);
						pop = new PopupWindow(userListView, userNameLayout
								.getWidth(), 400);
						userListView.setAdapter(userAdapter);
						userListView.setVerticalScrollBarEnabled(false);
						userListView.setHorizontalScrollBarEnabled(false);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, 400);
						userListView.setLayoutParams(params);
						userListView.setDivider(JVLoginActivity.this
								.getResources().getDrawable(
										R.color.login_pop_bg));
						userListView.setCacheColorHint(JVLoginActivity.this
								.getResources().getColor(R.color.transparent));
						userListView.setFadingEdgeLength(0);
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								moreUserIV
										.setImageResource(R.drawable.login_pull_up_icon);
								pop.showAsDropDown(userNameLayout);
							}
						}, 200);
					}
				} else if (pop.isShowing()) {
					userAdapter.notifyDataSetChanged();
					pop.dismiss();
					moreUserIV.setImageResource(R.drawable.login_pull_icon);
				} else if (!pop.isShowing()) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							userAdapter.notifyDataSetChanged();
							pop.showAsDropDown(userNameLayout);
							moreUserIV
									.setImageResource(R.drawable.login_pull_up_icon);
						}
					}, 200);

				}
			}
		});

		mAnimationRight = AnimationUtils.loadAnimation(JVLoginActivity.this,
				R.anim.rotate_right);
		mAnimationRight.setFillAfter(true);
		// showPointBtn.setAnimation(mAnimationRight);
		// onlineLoginBtn.setBackgroundResource(R.drawable.blue_bg);
		onlineLoginBtn.setOnClickListener(myOnClickListener);
		showPointBtn.setOnClickListener(myOnClickListener);
		registBtn.setOnClickListener(myOnClickListener);
		// findPsw.setOnClickListener(myOnClickListener);
		localLoginBtn.setOnClickListener(myOnClickListener);
		findPassTV.setOnClickListener(myOnClickListener);

		Intent intent = getIntent();
		Boolean autoLogin = intent.getBooleanExtra("AutoLogin", false);

		if (autoLogin) {
			String userName = intent.getStringExtra("UserName");
			String userPass = intent.getStringExtra("UserPass");
			statusHashMap.put(Consts.KEY_USERNAME, userName);
			statusHashMap.put(Consts.KEY_PASSWORD, userPass);
			statusHashMap.put(Consts.LOCAL_LOGIN, "false");

			userNameET.setText(userName);
			passwordET.setText(userPass);

			createDialog(R.string.logining, true);
			LoginTask task = new LoginTask();
			String[] strParams = new String[3];
			task.execute(strParams);
		}
	}

	/**
	 * click事件
	 */
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.username_et:
				if (pop != null && pop.isShowing()) {
					pop.dismiss();
					moreUserIV.setImageResource(R.drawable.login_pull_icon);
				}
				break;
			case R.id.btn_left: {
				backMethod();
				break;
			}
			/** 删除列表用户 */
			case R.id.otheruser_del: {
				userList = UserUtil.deleteUser(userListIndex);
				userAdapter.setData(userList);
				userAdapter.notifyDataSetChanged();
				break;
			}
			case R.id.findpass_tv: {
				if (!ConfigUtil.isConnected(JVLoginActivity.this)) {
					alertNetDialog();
				} else {
					Intent intentFP = new Intent(JVLoginActivity.this,
							ResetPwdChoiceActivity.class);
					JVLoginActivity.this.startActivity(intentFP);
				}
				break;
			}
			case R.id.onlinelogin_btn:// 在线登陆
				statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");
				// userNameET = null;
				if ("".equalsIgnoreCase(userNameET.getText().toString())) {
					showTextToast(R.string.login_str_username_notnull);
				} else if ("".equalsIgnoreCase(passwordET.getText().toString())) {
					showTextToast(R.string.login_str_loginpass1_notnull);
				} else {
					createDialog(R.string.logining, true);
					userName = userNameET.getText().toString();
					passWord = passwordET.getText().toString();
					statusHashMap.put(Consts.KEY_USERNAME, userName);
					statusHashMap.put(Consts.KEY_PASSWORD, passWord);
					statusHashMap.put(Consts.LOCAL_LOGIN, "false");

					// LoginThread lt = new LoginThread(JVLoginActivity.this);
					// lt.start();

					LoginTask task = new LoginTask();
					String[] strParams = new String[3];
					task.execute(strParams);
				}
				break;
			case R.id.regist_btn:// 注册
				statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");
				Intent registIntent = new Intent();
				registIntent.setClass(JVLoginActivity.this,
						JVRegisterActivity.class);
				JVLoginActivity.this.startActivity(registIntent);
				break;
			case R.id.showpoint_btn:// 演示点
				if (!ConfigUtil.isConnected(JVLoginActivity.this)) {
					alertNetDialog();
				} else {
					StatService.trackCustomEvent(
							JVLoginActivity.this,
							"Demo",
							JVLoginActivity.this.getResources().getString(
									R.string.str_demo));
					Intent demoIntent = new Intent();
					demoIntent.setClass(JVLoginActivity.this,
							JVDemoActivity.class);
					JVLoginActivity.this.startActivity(demoIntent);
				}
				break;
			case R.id.locallogin_btn:// 本地登录
				StatService.trackCustomEvent(JVLoginActivity.this,
						"locallogin", JVLoginActivity.this.getResources()
								.getString(R.string.str_login));
				statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");
				Intent intentMain = new Intent(JVLoginActivity.this,
						JVTabActivity.class);
				statusHashMap.put(Consts.LOCAL_LOGIN, "true");
				JVLoginActivity.this.startActivity(intentMain);
				JVLoginActivity.this.finish();

				// statusHashMap.put(Consts.LOCAL_LOGIN, "false");
				// statusHashMap.put(Consts.KEY_USERNAME, "");
				// statusHashMap.put(Consts.KEY_PASSWORD, "");
				// Intent intentMain = new Intent(JVLoginActivity.this,
				// JVPlayActivity.class);
				// JVLoginActivity.this.startActivity(intentMain);
				// JVLoginActivity.this.finish();
				break;
			}
		}

	};

	private int loginRes1 = 0;
	private int loginRes2 = 0;
	private int verifyCode = 0;
	String country = "";

	// 登陆线程
	private class LoginTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {

			country = ConfigUtil.getCountry();

			handler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
			String strRes = AccountUtil.onLoginProcessV2(JVLoginActivity.this,
					statusHashMap.get(Consts.KEY_USERNAME),
					statusHashMap.get(Consts.KEY_PASSWORD), Url.SHORTSERVERIP,
					Url.LONGSERVERIP);
			JSONObject respObj = null;
			try {
				respObj = new JSONObject(strRes);
				loginRes1 = respObj.optInt("arg1", 1);
				loginRes2 = respObj.optInt("arg2", 0);
				// {"arg1":8,"arg2":0,"data":{"channel_ip":"210.14.156.66","online_ip":"210.14.156.66"},"desc":"after the judge and longin , begin the big switch...","result":0}
				MyLog.v(TAG, Url.SHORTSERVERIP + "--" + Url.LONGSERVERIP + "--"
						+ country + "--" + strRes);
				String data = respObj.optString("data");
				if (null != data && !"".equalsIgnoreCase(data)) {
					JSONObject dataObj = new JSONObject(data);
					String channelIp = dataObj.optString("channel_ip");
					String onlineIp = dataObj.optString("online_ip");
					if (Consts.LANGUAGE_ZH == ConfigUtil.getServerLanguage()) {
						MySharedPreference.putString("ChannelIP", channelIp);
						MySharedPreference.putString("OnlineIP", onlineIp);
						MySharedPreference.putString("ChannelIP_en", "");
						MySharedPreference.putString("OnlineIP_en", "");
					} else {
						MySharedPreference.putString("ChannelIP_en", channelIp);
						MySharedPreference.putString("OnlineIP_en", onlineIp);
						MySharedPreference.putString("ChannelIP", "");
						MySharedPreference.putString("OnlineIP", "");
					}
				}

			} catch (JSONException e) {
				loginRes1 = JVAccountConst.LOGIN_FAILED_2;
				loginRes2 = 0;
				e.printStackTrace();
			}

			verifyCode = AccountUtil.VerifyUserName(statusHashMap
					.get(Consts.KEY_USERNAME));

			return loginRes1;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			Intent intent = new Intent();
			switch (result) {
			case JVAccountConst.LOGIN_SUCCESS: {
				StatService.trackCustomEvent(JVLoginActivity.this,
						"onlinelogin", JVLoginActivity.this.getResources()
								.getString(R.string.str_onlinelogin));
				MySharedPreference.putString("UserName",
						statusHashMap.get(Consts.KEY_USERNAME));
				MySharedPreference.putString("PassWord",
						statusHashMap.get(Consts.KEY_PASSWORD));

				User user = new User();
				user.setPrimaryID(System.currentTimeMillis());
				user.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
				user.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
				user.setLastLogin(1);
				user.setJudgeFlag(1);
				UserUtil.addUser(user);
				MyLog.v(TAG, "LoginSuccess");
				intent.setClass(JVLoginActivity.this, JVTabActivity.class);
				JVLoginActivity.this.startActivity(intent);
				finish();
				break;
			}
			case JVAccountConst.RESET_NAME_AND_PASS: {
				intent.setClass(JVLoginActivity.this,
						JVEditOldUserInfoActivity.class);
				JVLoginActivity.this.startActivity(intent);
				break;
			}
			case JVAccountConst.RESET_PASSWORD_SUCCESS: {
				User user1 = new User();
				user1.setPrimaryID(System.currentTimeMillis());
				user1.setUserName(statusHashMap.get(Consts.KEY_USERNAME));
				user1.setUserPwd(statusHashMap.get(Consts.KEY_PASSWORD));
				statusHashMap.put(Consts.LOCAL_LOGIN, "true");
				// SqlLiteUtil.addUser(user1);
				UserUtil.addUser(user1);
				String resultJson = JVACCOUNT.GetAccountInfo();
				JSONObject json = null;
				try {
					json = new JSONObject(resultJson);
				} catch (JSONException e) {
					json = null;
				}
				if (null != json && null != json.optString("mail")
						&& "".equals(json.optString("mail")) && verifyCode > 0) {
					intent.setClass(JVLoginActivity.this,
							JVBoundEmailActivity.class);
					intent.putExtra("AutoLogin", true);
					intent.putExtra("UserName",
							statusHashMap.get(Consts.KEY_USERNAME));
					intent.putExtra("UserPass",
							statusHashMap.get(Consts.KEY_PASSWORD));
					JVLoginActivity.this.startActivity(intent);
					finish();
				} else {
					intent.setClass(JVLoginActivity.this, JVTabActivity.class);
					JVLoginActivity.this.startActivity(intent);
					finish();
				}
				// {"result":0,"mail":"","phone":"","username":"aaasss","nickname":"aaasss"}
				break;
			}
			case JVAccountConst.PASSWORD_ERROR: {
				UserUtil.resetAllUser();
				showTextToast(R.string.str_userpass_error);
				break;
			}
			case JVAccountConst.SESSION_NOT_EXSIT: {
				showTextToast(R.string.str_session_not_exist);
				break;
			}
			case JVAccountConst.USER_HAS_EXIST: {

				break;
			}
			case JVAccountConst.USER_NOT_EXIST: {
				UserUtil.resetAllUser();
				showTextToast(R.string.str_user_not_exist);
				break;
			}
			case JVAccountConst.LOGIN_FAILED_1: {
				UserUtil.resetAllUser();
				if (-5 == loginRes2) {
					showTextToast(R.string.str_error_code_5);
				} else if (-6 == loginRes2) {
					showTextToast(R.string.str_error_code_6);
				} else {
					showTextToast(getResources().getString(
							R.string.str_error_code)
							+ (loginRes2 - 1000));
				}
				break;
			}
			case JVAccountConst.LOGIN_FAILED_2: {
				UserUtil.resetAllUser();
				showTextToast(R.string.str_other_error);
				break;
			}
			}

			dismissDialog();
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

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		dismissDialog();
	}

	/**
	 * 返回事件
	 */
	private void backMethod() {
		openExitDialog();
	}

	@Override
	public void onBackPressed() {
		backMethod();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
