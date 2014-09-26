package com.jovision.activities;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVConst;
import com.jovision.utils.AccountUtil;

public class JVBoundEmailActivity extends BaseActivity {

	private EditText userEmailEditText;
	private TextView currentMenu;
	private Button back;
	private Button skip;
	private Button finish;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		Intent intent = new Intent();
		switch (what) {
		case JVAccountConst.REGIST_SUCCESS_LOGIN_SUCCESS:// 注册成功登陆成功
			intent.setClass(JVBoundEmailActivity.this, JVTabActivity.class);
			JVBoundEmailActivity.this.startActivity(intent);
			JVBoundEmailActivity.this.finish();
			break;
		case JVAccountConst.BOUND_EMAIL_FAILED:// 绑定邮箱失败
			showTextToast(R.string.str_bound_email_failed);
			break;
		case JVAccountConst.BOUND_EMAIL_EXIST:// 邮箱已被绑定，绑定失败
			showTextToast(R.string.str_bound_email_exist);
			break;
		default:
			break;
		}
		dismissDialog();
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		userEmailEditText = (EditText) findViewById(R.id.registemail);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		back = (Button) findViewById(R.id.back);
		back.setVisibility(View.GONE);
		skip = (Button) findViewById(R.id.btn_right);
		currentMenu.setVisibility(View.VISIBLE);
		skip.setVisibility(View.VISIBLE);
		skip.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_save));
		skip.setText(getResources().getString(R.string.str_skip));
		skip.setTextColor(Color.WHITE);
		currentMenu.setText(getResources().getString(R.string.str_bound_email));
		finish = (Button) findViewById(R.id.finish);
		skip.setOnClickListener(onClickListener);
		finish.setOnClickListener(onClickListener);

	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_right:
				Intent intent = new Intent();
				intent.setClass(JVBoundEmailActivity.this, JVTabActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.finish:
				if ("".equalsIgnoreCase(userEmailEditText.getText().toString())) {
					showTextToast(R.string.login_str_loginemail_notnull);
				} else if (!AccountUtil.verifyEmail(userEmailEditText.getText()
						.toString())) {
					showTextToast(R.string.login_str_loginemail_tips);
				} else {
					createDialog(R.string.login_str_loging);
					DirectLoginThread loginThread = new DirectLoginThread(
							JVBoundEmailActivity.this);
					loginThread.start();
				}
				break;
			default:
				break;
			}
		}
	};

	private static class DirectLoginThread extends Thread {
		private final WeakReference<JVBoundEmailActivity> mActivity;

		public DirectLoginThread(JVBoundEmailActivity activity) {
			mActivity = new WeakReference<JVBoundEmailActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			JVBoundEmailActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				int res = AccountUtil
						.bindMailOrPhone(activity.userEmailEditText.getText()
								.toString());
				if (0 == res) {
					activity.handler
							.sendMessage(activity.handler.obtainMessage(
									JVConst.REGIST_SUCCESS_LOGIN_SUCCESS, 0, 0));// 注册成功登陆成功
				} else if (2 == res) {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.BOUND_EMAIL_EXIST, 0,
									0));// 邮箱已被绑定，绑定失败
				} else {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVAccountConst.BOUND_EMAIL_FAILED,
									0, 0));// 绑定邮箱失败
				}
				super.run();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(JVBoundEmailActivity.this, JVTabActivity.class);
			startActivity(intent);
			finish();
		}
		return true;
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
