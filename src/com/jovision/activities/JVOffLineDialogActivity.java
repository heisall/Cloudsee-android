package com.jovision.activities;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.Url;
import com.jovision.utils.AccountUtil;

public class JVOffLineDialogActivity extends BaseActivity {

	private static final int COUNTS = 15;// 15秒倒计时
	protected static final int COUNT_END = 0x50;// 倒计时结束
	protected static final int COUNTING = 0x51;// 倒计时

	/** 账号踢退 */
	private LinearLayout otherLoginLayout;
	private TextView lastCount;// 15秒倒计时
	private int lastSeconds;// 剩余秒数
	private Button exit;
	private Button keepOnline;
	Timer offlineTimer;

	/** 账号离线 */
	private LinearLayout offlineLayout;
	private Button sure;

	private int errorCode = 0;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case COUNTING: {
			lastCount.setText(String.valueOf(arg1));
			break;
		}
		case COUNT_END: {
			reLogin();
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
		errorCode = getIntent().getIntExtra("ErrorCode", 0);
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.dialog_offline);
		/** 账号踢退 */
		otherLoginLayout = (LinearLayout) findViewById(R.id.other_login_layout);
		lastCount = (TextView) findViewById(R.id.lastcount);
		exit = (Button) findViewById(R.id.exit);
		keepOnline = (Button) findViewById(R.id.keeponline);
		exit.setOnClickListener(mOnClickListener);
		keepOnline.setOnClickListener(mOnClickListener);

		/** 账号离线 */
		offlineLayout = (LinearLayout) findViewById(R.id.offline_layout);
		sure = (Button) findViewById(R.id.sure);
		sure.setOnClickListener(mOnClickListener);

		if (errorCode == JVAccountConst.MESSAGE_OFFLINE) {// 提掉线
			otherLoginLayout.setVisibility(View.VISIBLE);
			offlineLayout.setVisibility(View.GONE);
			lastSeconds = COUNTS;
			offlineTimer = new Timer();
			TimerTask offlineTask = new TimerTask() {

				@Override
				public void run() {
					lastSeconds--;
					if (0 == lastSeconds) {
						handler.sendMessage(handler.obtainMessage(COUNT_END, 0,
								0, null));
					} else {
						handler.sendMessage(handler.obtainMessage(COUNTING,
								lastSeconds, 0, null));
					}
				}

			};
			offlineTimer.schedule(offlineTask, 1 * 1000, 1 * 1000);
		} else {
			otherLoginLayout.setVisibility(View.GONE);
			offlineLayout.setVisibility(View.VISIBLE);
		}

	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.keeponline: {
				createDialog(R.string.login_str_loging);
				LoginTask task = new LoginTask();
				String[] strParams = new String[3];
				task.execute(strParams);
				break;
			}
			case R.id.exit:
			case R.id.sure: {
				reLogin();
				break;
			}
			}

		}

	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 登陆线程
	private class LoginTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int loginRes = 0;
			String strRes = AccountUtil.onLoginProcess(
					JVOffLineDialogActivity.this,
					statusHashMap.get(Consts.KEY_USERNAME),
					statusHashMap.get(Consts.KEY_PASSWORD), Url.SHORTSERVERIP,
					Url.LONGSERVERIP);
			JSONObject respObj = null;
			try {
				respObj = new JSONObject(strRes);
				loginRes = respObj.optInt("arg1", 1);
			} catch (JSONException e) {
				loginRes = JVAccountConst.LOGIN_FAILED_2;
				e.printStackTrace();
			}
			return loginRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。

			if (JVAccountConst.LOGIN_SUCCESS == result) {
				showTextToast(R.string.keeponline_success);
				JVOffLineDialogActivity.this.finish();
			} else {
				showTextToast(R.string.keeponline_failed);
				reLogin();
			}
			dismissDialog();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	/**
	 * 重新进登陆
	 */
	public void reLogin() {
		MyActivityManager.getActivityManager().popAllActivityExceptOne(
				JVLoginActivity.class);
		Intent intent = new Intent();
		intent.setClass(JVOffLineDialogActivity.this, JVLoginActivity.class);
		JVOffLineDialogActivity.this.startActivity(intent);
		JVOffLineDialogActivity.this.finish();
	}

}
