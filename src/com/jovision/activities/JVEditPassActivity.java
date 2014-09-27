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
import com.jovision.utils.AccountUtil;

public class JVEditPassActivity extends BaseActivity {
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	// private EditText userName = null;
	private EditText userOldPass = null;
	private EditText userNewPass = null;
	private EditText userEnsurePass = null;
	private Button finishEdit = null;

	/** 修改信息提示文本 */
	private TextView registTips;
	private TextView registTips2;
	private TextView registTips3;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.handleMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.editpass_layout);
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		if (!TextUtils.isEmpty(statusHashMap.get(Consts.KEY_USERNAME))) {
			currentMenu.setText(statusHashMap.get(Consts.KEY_USERNAME));
		}
		back.setOnClickListener(onClickListener);
		userOldPass = (EditText) findViewById(R.id.editoldpass);
		userNewPass = (EditText) findViewById(R.id.editnewpass);
		userEnsurePass = (EditText) findViewById(R.id.editensurepass);
		finishEdit = (Button) findViewById(R.id.editfinish);
		registTips = (TextView) findViewById(R.id.regist_tips);
		registTips2 = (TextView) findViewById(R.id.regist_tips2);
		registTips3 = (TextView) findViewById(R.id.regist_tips3);
		finishEdit.setOnClickListener(onClickListener);

	}

	// onclick事件
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				JVEditPassActivity.this.finish();
				break;
			case R.id.editfinish:
				if ("".equalsIgnoreCase(userOldPass.getText().toString().trim())) {
					showTextToast(R.string.str_oldpass_notnull);
				} else if (!statusHashMap.get(Consts.KEY_PASSWORD).equals(
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
					EditThread editThread = new EditThread(currentMenu
							.getText().toString(), userOldPass.getText()
							.toString(), userNewPass.getText().toString(),
							JVEditPassActivity.this);
					editThread.start();
				}
				break;
			}
		}
	};

	// 修改密码线程
	private class EditThread extends Thread {
		String username = "";
		String oldpwd = "";
		String newpwd = "";
		Context context;

		public EditThread(String arg0, String arg1, String arg2, Context c) {
			username = arg0;
			oldpwd = arg1;
			newpwd = arg2;
			context = c;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int res = AccountUtil.modifyUserPassword(oldpwd, newpwd);
			if (JVAccountConst.SUCCESS == res) {
				// 修改数据库中的用户名密码
				User user = new User();
				user.setUserName(username);
				user.setUserPwd(newpwd);
				// BaseApp.modifyUserInfo(user);
				// BaseApp.resetUser();
				if (JVConst.KEEP_ONLINE_FLAG) {// 已经开启服务
					Intent serviceIntent = new Intent(context.getResources()
							.getString(R.string.str_offline_class_name));
					context.stopService(serviceIntent);
					JVConst.KEEP_ONLINE_FLAG = false;
				}
				AccountUtil.signOut(context);
				handler.sendMessage(handler
						.obtainMessage(JVConst.EDIT_PWD_SUCCESS));
			} else {
				handler.sendMessage(handler
						.obtainMessage(JVConst.EDIT_PWD_FAILED));
			}

		}

	}

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

//
// // 检查软件更新handler
// public static class EditHandler extends Handler {
// private final WeakReference<JVEditPassActivity> mActivity;
//
// public EditHandler(JVEditPassActivity activity) {
// mActivity = new WeakReference<JVEditPassActivity>(activity);
// }
//
// @Override
// public void handleMessage(Message msg) {
// // TODO Auto-generated method stub
// super.handleMessage(msg);
//
// JVEditPassActivity activity = mActivity.get();
// if (null != activity && !activity.isFinishing()) {
// activity.dismissDialog(activity.proDialog);
//
// switch (msg.what) {
// case JVConst.EDIT_PWD_SUCCESS:
// activity.showTextToast(R.string.str_edit_success);
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {
// BaseApp.deviceList.clear();
// }
// try {
//
// Intent intent = new Intent();
// intent.setClass(activity, JVLoginActivity.class);
// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
// activity.startActivity(intent);
// activity.finish();
// } catch (Exception e) {
// e.printStackTrace();
// }
// break;
// case JVConst.EDIT_PWD_FAILED:
// activity.showTextToast(
// R.string.str_edit_oldpass_error);
// activity.userOldPass.requestFocus();
// activity.userOldPass.setSelection(activity.userOldPass
// .getText().toString().length());
// break;
// }
//
// }
// }
//
// };
//
