package com.jovision.activities;

import java.lang.ref.WeakReference;

import android.R;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.test.JVACCOUNT;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVAccountConst;
import com.jovision.newbean.UserBean;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.SqlLiteUtil;

public class JVEditOldUserInfoActivity extends BaseActivity {

	private Button back;
	private Button finish;
	private TextView currentMenu;
	private EditText userNameEditText;
	private Dialog dialog = null;
	private Dialog dialog2 = null;// 进度dialog

	/** 注册信息提示文本 */
	private TextView registTips;
	private TextView registTips2;
	private TextView registTips3;
	/** 用户名是否存在 */
	private int nameExists;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		Intent intent = new Intent();
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (null != dialog2 && dialog2.isShowing()) {
			dialog2.dismiss();
		}
		switch (what) {
		case JVAccountConst.REGIST_SUCCESS:

			DirectLoginThread dLThread = new DirectLoginThread(
					JVEditOldUserInfoActivity.this);
			dLThread.start();
			break;
		case JVAccountConst.HAVE_REGISTED:// 已注册
			showTextToast(R.string.str_user_has_exist);
			break;
		case JVAccountConst.HAVE_REGISTED2:// 已注册
			showTextToast(R.string.str_phone_num_error);
			break;
		case JVAccountConst.REGIST_FAILED:

			showTextToast(R.string.str_reset_not_success);
			break;
		case JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS:
			if (0 < AccountUtil.VerifyUserName(statusHashMap
					.get(Consts.KEY_USERNAME))) {
				Intent emailIntent = new Intent(JVEditOldUserInfoActivity.this,
						JVBoundEmailActivity.class);
				startActivity(emailIntent);
				finish();
			} else {
				intent.setClass(JVEditOldUserInfoActivity.this,
						JVMainActivity.class);
				startActivity(intent);
				finish();
			}
			// intent.setClass(activity, JVMainActivity.class);
			// startActivity(intent);
			// finish();
			break;
		case JVAccountConst.REGIST_SUCCESS_LOGIN_FAILED:
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			showTextToast(R.string.str_userpass_error);
			intent.setClass(JVEditOldUserInfoActivity.this,
					JVLoginActivity.class);
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			startActivity(intent);
			finish();
			break;
		case JVAccountConst.REGIST_SUCCESS_LOGIN_NET_ERROR:
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			showTextToast(R.string.str_net_error);
			intent.setClass(JVEditOldUserInfoActivity.this,
					JVLoginActivity.class);
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			startActivity(intent);
			finish();
			break;
		case JVAccountConst.SESSION_NOT_EXSIT:
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			showTextToast(R.string.str_session_not_exist);
			intent.setClass(JVEditOldUserInfoActivity.this,
					JVLoginActivity.class);
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
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
		case JVAccountConst.PHONE_DETECTION_FAILED:
			registTips.setVisibility(View.VISIBLE);
			registTips.setTextColor(Color.rgb(217, 34, 38));
			registTips.setText(getResources().getString(
					R.string.str_phone_num_error));
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
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_modify_user_info);
		finish = (Button) findViewById(R.id.finish);
		userNameEditText = (EditText) findViewById(R.id.registusername);
		// pass1EditText = (EditText) findViewById(R.id.registpass1);
		// pass2EditText = (EditText) findViewById(R.id.registpass2);
		registTips = (TextView) findViewById(R.id.regist_tips);
		registTips2 = (TextView) findViewById(R.id.regist_tips2);
		registTips3 = (TextView) findViewById(R.id.regist_tips3);

		back.setOnClickListener(onClickListener);
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
						int res = AccountUtil.VerifyUserName(userNameEditText
								.getText().toString());
						if (res >= 0) {
							if (null == dialog2) {
								dialog2 = BaseApp.createLoadingDialog(
										JVEditOldUserInfoActivity.this, null);
							}
							dialog2.show();
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
									} else if (JVAccountConst.PHONE_NOT_TRUE == nameExists) {
										handler.sendMessage(handler
												.obtainMessage(JVAccountConst.PHONE_DETECTION_FAILED));
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
			case R.id.back:
				JVEditOldUserInfoActivity.this.finish();
				break;
			case R.id.finish:
				userNameEditText = (EditText) findViewById(R.id.registusername);
				// pass1EditText = (EditText) findViewById(R.id.registpass1);
				// pass2EditText = (EditText) findViewById(R.id.registpass2);
				if ("".equalsIgnoreCase(userNameEditText.getText().toString())) {
					showTextToast(R.string.login_str_username_notnull);
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
				} else {
					if (null != dialog) {
						dialog.show();
					}
					statusHashMap.put(Consts.KEY_USERNAME, userNameEditText
							.getText().toString());
					FinishThread finishThread = new FinishThread(
							JVEditOldUserInfoActivity.this);
					finishThread.start();
				}

				break;
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVEditOldUserInfoActivity.this.finish();
		}
		return false;
	}

	// 注册线程
	private static class FinishThread extends Thread {
		private final WeakReference<JVEditOldUserInfoActivity> mActivity;

		public FinishThread(JVEditOldUserInfoActivity activity) {
			mActivity = new WeakReference<JVEditOldUserInfoActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JVEditOldUserInfoActivity activity = mActivity.get();
			if (null != activity) {
				int nameExists = AccountUtil
						.isUserExsit(activity.userNameEditText.getText()
								.toString());
				if (JVAccountConst.USER_HAS_EXIST == nameExists) {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.HAVE_REGISTED));
					return;
				} else if (JVAccountConst.PHONE_NOT_TRUE == nameExists) {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.HAVE_REGISTED2));
					return;
				}
				int result = JVACCOUNT.ResetUserNameAndPassword(
						activity.statusHashMap.get(Consts.KEY_USERNAME),
						activity.statusHashMap.get(Consts.KEY_PASSWORD));

				if (JVAccountConst.SUCCESS == result) {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.REGIST_SUCCESS));
				} else {// 注册失败
					activity.handler
							.sendMessage(activity.handler.obtainMessage(
									JVAccountConst.REGIST_FAILED, result));
				}
				super.run();
			}

		}
	}

	// 免 登录线程
	private static class DirectLoginThread extends Thread {
		String sessionKey = "";
		private final WeakReference<JVEditOldUserInfoActivity> mActivity;

		public DirectLoginThread(JVEditOldUserInfoActivity activity) {
			mActivity = new WeakReference<JVEditOldUserInfoActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JVEditOldUserInfoActivity activity = mActivity.get();
			if (null != activity) {
				int result = AccountUtil.userLogin(
						activity.statusHashMap.get(Consts.KEY_USERNAME),
						activity.statusHashMap.get(Consts.KEY_PASSWORD));
				if (JVAccountConst.SUCCESS == result) {
					// //登陆成功开推送，保持在线
					// LoginUtil.userOnline();
					UserBean user = new UserBean();
					user.setPrimaryID(System.currentTimeMillis());
					user.setUserName(activity.statusHashMap
							.get(Consts.KEY_USERNAME));
					user.setUserPwd(activity.statusHashMap
							.get(Consts.KEY_PASSWORD));
					SqlLiteUtil.addUser(user);
					activity.statusHashMap.put(Consts.LOCAL_LOGIN, "false");
					activity.handler
							.sendMessage(activity.handler
									.obtainMessage(
											JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS,
											result));// 注册成功登陆成功
				} else {
					activity.handler
							.sendMessage(activity.handler.obtainMessage(
									JVAccountConst.REGIST_FAILED, result));// 注册成功网络错误
				}
				super.run();
			}

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
