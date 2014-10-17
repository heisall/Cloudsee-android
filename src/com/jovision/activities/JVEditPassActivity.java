package com.jovision.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVConst;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;

public class JVEditPassActivity extends BaseActivity {
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	// private EditText userName = null;
	private EditText userOldPass = null;
	private EditText userNewPass = null;
	private EditText userEnsurePass = null;
	private Button finishEdit = null;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

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
		setContentView(R.layout.editpass_layout);
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		if (!TextUtils.isEmpty(statusHashMap.get(Consts.KEY_USERNAME))) {
			currentMenu.setText(statusHashMap.get(Consts.KEY_USERNAME));
		}
		back.setOnClickListener(onClickListener);
		userOldPass = (EditText) findViewById(R.id.editoldpass);
		userNewPass = (EditText) findViewById(R.id.editnewpass);
		userEnsurePass = (EditText) findViewById(R.id.editcommitpass);
		finishEdit = (Button) findViewById(R.id.editfinish);
		finishEdit.setOnClickListener(onClickListener);

	}

	// onclick事件
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.editfinish:
				if ("".equalsIgnoreCase(userOldPass.getText().toString().trim())) {
					showTextToast(R.string.str_oldpass_notnull);
				} else if (!MySharedPreference.getString(Consts.KEY_PASSWORD).equals(
						userOldPass.getText().toString().trim())) {
					showTextToast(R.string.str_edit_accoutorpass_error);
					userOldPass.requestFocus();
				} else if ("".equalsIgnoreCase(userNewPass.getText().toString()
						.trim())) {
					showTextToast(R.string.str_newpass_notnull);
				} else if ("".equalsIgnoreCase(userEnsurePass.getText()
						.toString().trim())) {
					showTextToast(R.string.str_ensurenewpass_notnull);
				} else if (userOldPass.getText().toString()
						.equals(userNewPass.getText().toString())) {
					showTextToast(R.string.login_str_password_same);
					userNewPass.requestFocus();
					userNewPass.setSelection(userNewPass.getText().toString()
							.length());
				} else if (!userNewPass.getText().toString()
						.equalsIgnoreCase(userEnsurePass.getText().toString())) {
					showTextToast(R.string.login_str_loginpass_notsame);
				} else if (!AccountUtil.verifyPass(userNewPass.getText()
						.toString())) {
					showTextToast(R.string.login_str_password_tips1);
					userNewPass.requestFocus();
					userNewPass.setSelection(userNewPass.getText().toString()
							.length());
				} else {
					createDialog(R.string.str_deleting);
				}
				break;
			}
		}
	};


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVEditPassActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
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
