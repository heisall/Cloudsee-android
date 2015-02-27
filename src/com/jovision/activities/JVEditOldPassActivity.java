package com.jovision.activities;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.test.JVACCOUNT;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
import com.jovision.utils.UserUtil;

public class JVEditOldPassActivity extends BaseActivity {

	private Button finish;
	private EditText userNameEditText;
	private EditText pass1EditText;
	private EditText pass2EditText;

	/** 注册信息提示文本 */
	private TextView registTips;
	private TextView registTips2;
	private TextView registTips3;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		Intent intent = new Intent();
		switch (what) {
		case JVAccountConst.REGIST_SUCCESS:
			DirectLoginThread dLThread = new DirectLoginThread(
					JVEditOldPassActivity.this);
			dLThread.start();
			break;
		case JVAccountConst.HAVE_REGISTED:// 已注册
			showTextToast(R.string.str_user_has_exist);
			break;
		case JVAccountConst.HAVE_REGISTED2:// 已注册
			showTextToast(R.string.str_email_has_exist);
			break;
		case JVAccountConst.REGIST_FAILED:
			showTextToast(R.string.str_reset_not_success);
			break;
		case JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS:// 注册成功登陆成功
			if (1 == AccountUtil.VerifyUserName(JVEditOldPassActivity.this,
					statusHashMap.get("KEY_USERNAME"))) {
				intent.setClass(JVEditOldPassActivity.this,
						JVBoundEmailActivity.class);
				startActivity(intent);
				finish();
			} else {
				intent.setClass(JVEditOldPassActivity.this, JVTabActivity.class);
				startActivity(intent);
				finish();
			}
			break;
		case JVAccountConst.REGIST_SUCCESS_LOGIN_FAILED:// 注册成功登陆 失败
			dismissDialog();
			showTextToast(R.string.str_userpass_error);
			intent.setClass(JVEditOldPassActivity.this, JVLoginActivity.class);
			startActivity(intent);
			finish();
			break;
		// case JVAccountConst.REGIST_SUCCESS_LOGIN_NET_ERROR:
		// dismissDialog();
		// showTextToast(R.string.str_net_error);
		// intent.setClass(JVEditOldPassActivity.this, JVLoginActivity.class);
		//
		// startActivity(intent);
		// finish();
		// break;
		case JVAccountConst.SESSION_NOT_EXSIT:
			dismissDialog();
			showTextToast(R.string.str_session_not_exist);
			intent.setClass(JVEditOldPassActivity.this, JVLoginActivity.class);

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
		setContentView(R.layout.oldpass_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_modify_user_info);
		finish = (Button) findViewById(R.id.finish);
		userNameEditText = (EditText) findViewById(R.id.registusername);
		userNameEditText.setText(statusHashMap.get("KEY_USERNAME"));
		userNameEditText.setEnabled(false);
		pass1EditText = (EditText) findViewById(R.id.registpass1);
		pass2EditText = (EditText) findViewById(R.id.registpass2);
		registTips = (TextView) findViewById(R.id.regist_tips);
		registTips2 = (TextView) findViewById(R.id.regist_tips2);
		registTips3 = (TextView) findViewById(R.id.regist_tips3);

		leftBtn.setOnClickListener(onClickListener);
		finish.setOnClickListener(onClickListener);

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
				JVEditOldPassActivity.this.finish();
				break;
			case R.id.finish:
				pass1EditText = (EditText) findViewById(R.id.registpass1);
				pass2EditText = (EditText) findViewById(R.id.registpass2);
				if ("".equalsIgnoreCase(pass1EditText.getText().toString())) {
					showTextToast(R.string.login_str_loginpass1_notnull);
				} else if ("".equalsIgnoreCase(pass2EditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginpass2_notnull);
				} else if (!pass1EditText.getText().toString()
						.equals(pass2EditText.getText().toString())) {
					showTextToast(R.string.login_str_loginpass_notsame);
					pass1EditText.setText("");
					pass2EditText.setText("");
				} else if (!AccountUtil.verifyPass(pass1EditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_password_tips1);
				} else {
					createDialog("", true);
					statusHashMap.put("KEY_PASSWORD", pass1EditText.getText()
							.toString());
					FinishThread finishThread = new FinishThread(
							JVEditOldPassActivity.this);
					finishThread.start();
				}

				break;
			}
		}

	};

	// 注册线程
	private static class FinishThread extends Thread {
		private final WeakReference<JVEditOldPassActivity> mActivity;

		public FinishThread(JVEditOldPassActivity activity) {
			mActivity = new WeakReference<JVEditOldPassActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JVEditOldPassActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {

				int result = JVACCOUNT.ResetUserPassword(
						activity.statusHashMap.get("KEY_PASSWORD"),
						activity.statusHashMap.get("KEY_USERNAME"));
				if (JVAccountConst.SUCCESS == result) {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.REGIST_SUCCESS));
				} else {// 注册失败
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.REGIST_SUCCESS,
									result));
				}
				super.run();
			}

		}
	}

	// 免 登录线程
	private static class DirectLoginThread extends Thread {
		private final WeakReference<JVEditOldPassActivity> mActivity;

		public DirectLoginThread(JVEditOldPassActivity activity) {
			mActivity = new WeakReference<JVEditOldPassActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JVEditOldPassActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				String strRes = "";
				// int result = AccountUtil.userLogin(
				// activity.statusHashMap.get("KEY_USERNAME"),
				// activity.statusHashMap.get("KEY_PASSWORD"), activity);
				
					strRes = AccountUtil.onLoginProcessV2(activity,
							activity.statusHashMap.get(Consts.KEY_USERNAME),
							activity.statusHashMap.get(Consts.KEY_PASSWORD),
							Url.SHORTSERVERIP, Url.LONGSERVERIP);
				
				JSONObject respObj = null;
				int loginRes1 = -1;
				int loginRes2 = -1;
				try {
					respObj = new JSONObject(strRes);
					loginRes1 = respObj.optInt("arg1", 1);
					loginRes2 = respObj.optInt("arg2", 0);
					if (JVAccountConst.LOGIN_SUCCESS == loginRes1) {
						User user = new User();
						user.setPrimaryID(System.currentTimeMillis());
						user.setUserName(activity.statusHashMap
								.get("KEY_USERNAME"));
						user.setUserPwd(activity.statusHashMap
								.get("KEY_PASSWORD"));
						user.setJudgeFlag(1);
						// SqlLiteUtil.addUser(user);
						UserUtil.addUser(user);
						activity.statusHashMap.put(Consts.LOCAL_LOGIN, "false");
						activity.handler
								.sendMessage(activity.handler
										.obtainMessage(JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS));// 注册成功登陆成功
					} else {
						activity.handler.sendMessage(activity.handler
								.obtainMessage(JVAccountConst.REGIST_FAILED));// 注册成功网络错误

					}
				} catch (JSONException e) {
					loginRes1 = JVAccountConst.LOGIN_FAILED_2;
					loginRes2 = 0;
					e.printStackTrace();
				}
				super.run();
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVEditOldPassActivity.this.finish();
		}
		return false;
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
