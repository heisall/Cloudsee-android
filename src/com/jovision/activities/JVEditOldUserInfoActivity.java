package com.jovision.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.test.JVACCOUNT;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;

public class JVEditOldUserInfoActivity extends BaseActivity {

	/** topBar */
	private Button finish;

	/** 注册信息提示文本 */
	private EditText userNameEditText;
	private TextView registTips;
	private TextView registTips2;
	private TextView registTips3;
	/** 用户名是否存在 */
	private int nameExists;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		Intent intent = new Intent();
		switch (what) {
		case JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS:
			if (0 < AccountUtil.VerifyUserName(JVEditOldUserInfoActivity.this,
					statusHashMap.get(Consts.KEY_USERNAME))) {
				Intent emailIntent = new Intent(JVEditOldUserInfoActivity.this,
						JVBoundEmailActivity.class);
				startActivity(emailIntent);
				finish();
			} else {
				intent.setClass(JVEditOldUserInfoActivity.this,
						JVTabActivity.class);
				startActivity(intent);
				finish();
			}
			// intent.setClass(activity, JVMainActivity.class);
			// startActivity(intent);
			// finish();
			break;
		case JVAccountConst.REGIST_SUCCESS_LOGIN_FAILED:
			showTextToast(R.string.str_userpass_error);
			intent.setClass(JVEditOldUserInfoActivity.this,
					JVLoginActivity.class);
			dismissDialog();
			startActivity(intent);
			finish();
			break;
		// case JVAccountConst.REGIST_SUCCESS_LOGIN_NET_ERROR:
		// dismissDialog();
		// showTextToast(R.string.str_net_error);
		// intent.setClass(JVEditOldUserInfoActivity.this,
		// JVLoginActivity.class);
		// startActivity(intent);
		// finish();
		// break;
		case JVAccountConst.SESSION_NOT_EXSIT:
			dismissDialog();
			showTextToast(R.string.str_session_not_exist);
			intent.setClass(JVEditOldUserInfoActivity.this,
					JVLoginActivity.class);
			startActivity(intent);
			finish();
			break;
		case JVAccountConst.USERNAME_DETECTION_SUCCESS:
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(21, 103, 215));
			registTips.setText(getResources().getString(
					R.string.str_user_not_exist2));
			break;
		case JVAccountConst.USERNAME_DETECTION_FAILED:
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_user_has_exist));
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.olduser_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_modify_user_info);
		finish = (Button) findViewById(R.id.finish);
		userNameEditText = (EditText) findViewById(R.id.registusername);
		// pass1EditText = (EditText) findViewById(R.id.registpass1);
		// pass2EditText = (EditText) findViewById(R.id.registpass2);
		registTips = (TextView) findViewById(R.id.regist_tips);
		registTips2 = (TextView) findViewById(R.id.regist_tips2);
		registTips3 = (TextView) findViewById(R.id.regist_tips3);

		leftBtn.setOnClickListener(onClickListener);
		finish.setOnClickListener(onClickListener);
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
								JVEditOldUserInfoActivity.this,
								userNameEditText.getText().toString());
						if (res >= 0) {
							createDialog("", true);
							new Thread() {
								public void run() {
									nameExists = AccountUtil
											.isUserExsit(userNameEditText
													.getText().toString());
									if (JVAccountConst.USER_HAS_EXIST == nameExists) {
										handler.sendMessage(handler
												.obtainMessage(JVAccountConst.USERNAME_DETECTION_FAILED));
									} else if (JVAccountConst.USER_NOT_EXIST == nameExists) {
										handler.sendMessage(handler
												.obtainMessage(JVAccountConst.USERNAME_DETECTION_SUCCESS));
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
							// registTips.setVisibility(View.VISIBLE);
							// registTips.setTextColor(Color.rgb(217, 34, 38));
							// registTips.setText(getResources().getString(
							// R.string.login_str_username_tips));
						}
					}
				}
			}
		});

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				JVEditOldUserInfoActivity.this.finish();
				break;
			case R.id.finish:
				userNameEditText = (EditText) findViewById(R.id.registusername);
				// pass1EditText = (EditText) findViewById(R.id.registpass1);
				// pass2EditText = (EditText) findViewById(R.id.registpass2);
				if ("".equalsIgnoreCase(userNameEditText.getText().toString())) {
					showTextToast(R.string.login_str_username_notnull);
				} else if (-1 == AccountUtil.VerifyUserName(
						JVEditOldUserInfoActivity.this, userNameEditText
								.getText().toString())) {
					showTextToast(R.string.login_str_username_tips4);
				} else if (-2 == AccountUtil.VerifyUserName(
						JVEditOldUserInfoActivity.this, userNameEditText
								.getText().toString())) {
					showTextToast(R.string.login_str_loginemail_tips);
				} else if (-3 == AccountUtil.VerifyUserName(
						JVEditOldUserInfoActivity.this, userNameEditText
								.getText().toString())) {
					showTextToast(R.string.login_str_username_tips2);
				} else if (-4 == AccountUtil.VerifyUserName(
						JVEditOldUserInfoActivity.this, userNameEditText
								.getText().toString())) {
					showTextToast(R.string.login_str_username_tips3);
				} else {
					createDialog("", true);
					statusHashMap.put(Consts.KEY_USERNAME, userNameEditText
							.getText().toString());

					EditTask task = new EditTask();
					String[] params = new String[3];
					task.execute(params);
				}

				break;
			}
		}

	};

	@Override
	public void onBackPressed() {
		this.finish();
	}

	private int verifyRes = 0;

	// 设置三种类型参数分别为String,Integer,String
	class EditTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int resetRes = -1;// 0成功 2用户已存在 -15手机号格式错误
			verifyRes = 0;
			try {
				resetRes = AccountUtil.isUserExsit(userNameEditText.getText()
						.toString());
				if (JVAccountConst.USER_HAS_EXIST != nameExists
						&& JVAccountConst.PHONE_NOT_TRUE != nameExists) {
					resetRes = JVACCOUNT.ResetUserNameAndPassword(
							statusHashMap.get(Consts.KEY_USERNAME),
							statusHashMap.get(Consts.KEY_PASSWORD));

					verifyRes = AccountUtil.VerifyUserName(
							JVEditOldUserInfoActivity.this,
							statusHashMap.get(Consts.KEY_USERNAME));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return resetRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			dismissDialog();
			if (JVAccountConst.USER_HAS_EXIST == result) {
				showTextToast(R.string.str_user_has_exist);
			} else if (JVAccountConst.PHONE_NOT_TRUE == result) {
				showTextToast(R.string.str_phone_num_error);
			} else if (JVAccountConst.SUCCESS == result) {
				MySharedPreference.putBoolean("REMEMBER", true);
				if (0 < verifyRes) {
					Intent emailIntent = new Intent(
							JVEditOldUserInfoActivity.this,
							JVBoundEmailActivity.class);
					// emailIntent.putExtra("AutoLogin", true);
					emailIntent.putExtra("UserName",
							statusHashMap.get(Consts.KEY_USERNAME));
					emailIntent.putExtra("UserPass",
							statusHashMap.get(Consts.KEY_PASSWORD));
					JVEditOldUserInfoActivity.this.startActivity(emailIntent);
					JVEditOldUserInfoActivity.this.finish();
				} else {
					Intent intent = new Intent();
					intent.setClass(JVEditOldUserInfoActivity.this,
							JVLoginActivity.class);
					// intent.putExtra("AutoLogin", true);
					intent.putExtra("UserName",
							statusHashMap.get(Consts.KEY_USERNAME));
					intent.putExtra("UserPass",
							statusHashMap.get(Consts.KEY_PASSWORD));
					JVEditOldUserInfoActivity.this.startActivity(intent);
					JVEditOldUserInfoActivity.this.finish();// 注册成功登陆成功
				}
			} else {// 注册失败
				showTextToast(R.string.str_reset_not_success);
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

	@Override
	protected void onResume() {
		super.onResume();
	}
}
