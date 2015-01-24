package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.commons.Url;
import com.jovision.net.AsyncHttpClient;
import com.jovision.net.AsyncHttpResponseHandler;
import com.jovision.net.RequestParams;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.JSONUtil;
import com.jovision.views.AlarmDialog;

public class JVFeedbackActivity extends BaseActivity {

	private EditText content; // 意见反馈内容
	private TextView wordsNum; // 文字数量统计

	String connectStr = "";// 联系方式
	String contentStr = "";// 反馈内容

	private EditText connection; // 意见反馈联系方式
	private int number = 500;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_FEEDBACK_SUCCESS:// 反馈成功
			dismissDialog();
			AlertDialog.Builder builder = new AlertDialog.Builder(
					JVFeedbackActivity.this);
			builder.setMessage(R.string.str_commit_success);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							JVFeedbackActivity.this.finish();
							dialog.dismiss();
						}
					});
			builder.create().show();
			break;

		case Consts.WHAT_FEEDBACK_FAILED:// 反馈失败
			// 提交建议失败，什么也不做
			break;
		case Consts.WHAT_PUSH_MESSAGE:
			// 弹出对话框
			new AlarmDialog(this).Show(obj);
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.feedback_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		rightBtn = (Button) findViewById(R.id.btn_right);
		content = (EditText) findViewById(R.id.content);
		wordsNum = (TextView) findViewById(R.id.wordsnum);
		leftBtn.setVisibility(View.VISIBLE);
		rightBtn.setVisibility(View.VISIBLE);

		// back.setTextColor(Color.WHITE);
		// back.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.setting_save));
		//
		// commit.setTextColor(Color.WHITE);
		// commit.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.setting_save));

		// commit.setTextColor(getResources().getColor(R.color.white));
		// back.setText(getResources().getString(R.string.cancel));
		rightBtn.setText(getResources().getString(R.string.commit));

		connection = (EditText) findViewById(R.id.connectway);

		// back.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.send_btn_selector_1));
		// commit.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.send_btn_selector_2));
		currentMenu.setText(getResources().getString(
				R.string.str_idea_and_feedback));
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn.setOnClickListener(myOnClickListener);
		wordsNum.setOnClickListener(myOnClickListener);
		content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 设置内容不得超过字数限制，超过时删除多余文字
				int num = number - s.length();
				wordsNum.setText(num + "");
				selectionStart = content.getSelectionStart();
				selectionEnd = content.getSelectionEnd();
				if (temp.length() > number) {
					s.delete(selectionStart - (temp.length() - number),
							selectionEnd);
					int tempSelection = selectionEnd;
					content.setText(s);
					content.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:
				connectStr = connection.getText().toString();// 联系方式
				contentStr = content.getText().toString();// 反馈内容
				if ("".equals(connectStr) && !"".equals(contentStr)) {
					createDialog("", true);
					FeedbackThread feedbackThread = new FeedbackThread();
					feedbackThread.start();
				} else if (!"".equals(connectStr)
						&& !ConfigUtil.checkUserConnect(connectStr)) {
					showTextToast(R.string.str_connect_error);
				} else if (0 == content.getText().toString().length()
						|| ("").equals(contentStr)) {
					showTextToast(R.string.str_notice_content);
				} else if (!ConfigUtil.checkUserContent(contentStr)) {
					showTextToast(R.string.str_content_error);
				} else {
					createDialog("", true);
					FeedbackThread feedbackThread = new FeedbackThread();
					feedbackThread.start();
				}
				break;
			case R.id.wordsnum:
				content.setText("");
			default:
				break;
			}
		}

	};

	private void feedbackPost(String content, String contect) {
		String model = android.os.Build.MODEL;
		String version = android.os.Build.VERSION.RELEASE;
		String fingerprint = android.os.Build.FINGERPRINT;
		String country = ConfigUtil.getCountry();
		String cpu = Build.CPU_ABI;
		String softwareVersion = this.getResources().getString(
				R.string.app_name)
				+ ConfigUtil.getVersion(this);
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("mod", "mobile");
		params.put("platform", "1");
		params.put("model", model);
		params.put("version", version);
		params.put("fingerprint", fingerprint);
		params.put("country", country);
		params.put("cpu", cpu);
		params.put("softversion", softwareVersion);
		params.put("content", content);
		params.put("contact", contect);
		params.put("device", encryptInfo1());
		client.post("http://182.92.242.230/api.php", params, new landHandler());
	}

	class landHandler extends AsyncHttpResponseHandler {

		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onFinish() {
			super.onFinish();
		}

		@Override
		public void onSuccess(String content) {
			super.onSuccess(content);
			Log.i("TAG", content + "成功");
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
		}
	}

	class FeedbackThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				// Calendar rightNow = Calendar.getInstance();
				// String str = rightNow.get(Calendar.YEAR)
				// + "_"
				// + (rightNow.get(Calendar.MONTH) + 1 + "_" + rightNow
				// .get(Calendar.DATE));
				// MailSenderInfo mailInfo = new MailSenderInfo();
				// mailInfo.setMailServerHost("smtp.qq.com");
				// mailInfo.setMailServerPort("25");
				// mailInfo.setValidate(true);
				// mailInfo.setUserName("741376209@qq.com"); // 你的邮箱地址
				// mailInfo.setPassword("mfq_zsw");// 您的邮箱密码
				// mailInfo.setFromAddress("741376209@qq.com");
				// mailInfo.setToAddress("suifupeng@jovision.com");//
				// jovetech1203**
				// // mailInfo.setToAddress("jy0329@163.com");
				// mailInfo.setSubject("["
				// + getResources().getString(R.string.app_name) + "]"
				// + getResources().getString(R.string.str_feedback)
				// + ConfigUtil.getVersion(JVFeedbackActivity.this));
				// mailInfo.setContent(content + mobileInfo() + encryptInfo1());
				// MyLog.e("feedback=", content + mobileInfo() +
				// encryptInfo1());
				// String[] receivers = new String[] { "juyang@jovision.com",
				// "mfq@jovision.com" };
				// String[] ccs = receivers;
				// mailInfo.setReceivers(receivers);
				// mailInfo.setCcs(ccs);
				// // 这个类主要来发送邮件
				// // BaseApp.sendMailtoMultiReceiver(mailInfo);
				// // SimpleMailSender sms = new SimpleMailSender();
				// boolean flag =
				// ConfigUtil.sendMailtoMultiReceiver(mailInfo);// 发送文体格式
				// // sms.sendHtmlMail(mailInfo);//发送html格式

				String model = android.os.Build.MODEL;
				String version = android.os.Build.VERSION.RELEASE;
				String fingerprint = android.os.Build.FINGERPRINT;
				String country = ConfigUtil.getCountry();
				String cpu = Build.CPU_ABI;
				String softwareVersion = JVFeedbackActivity.this.getResources()
						.getString(R.string.app_name)
						+ ConfigUtil.getVersion(JVFeedbackActivity.this);
				HashMap<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("mod", "mobile");
				paramsMap.put("platform", "1");
				paramsMap.put("model", model);
				paramsMap.put("version", version);
				paramsMap.put("fingerprint", fingerprint);
				paramsMap.put("country", country);
				paramsMap.put("cpu", cpu);
				paramsMap.put("softversion", softwareVersion);
				paramsMap.put("content", contentStr);
				paramsMap.put("contact", connectStr);
				paramsMap.put("device", encryptInfo1());

				String result = JSONUtil.httpPost(Url.FEED_BACK_URL, paramsMap);

				if (null != result && "1".equalsIgnoreCase(result)) {
					handler.sendMessage(handler
							.obtainMessage(Consts.WHAT_FEEDBACK_SUCCESS));// 反馈成功
				} else {
					handler.sendMessage(handler
							.obtainMessage(Consts.WHAT_FEEDBACK_FAILED)); // 反馈失败
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVFeedbackActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	/**
	 * 收集手机信息
	 */
	private String mobileInfo() {
		String mobileInfo = "";
		try {
			String model = android.os.Build.MODEL;
			String version = android.os.Build.VERSION.RELEASE;
			String fingerprint = android.os.Build.FINGERPRINT;
			String country = ConfigUtil.getCountry();
			String cpu = Build.CPU_ABI;
			String softwareVersion = this.getResources().getString(
					R.string.app_name)
					+ ConfigUtil.getVersion(this);
			mobileInfo = "<br />" + "[1-MODEL]=" + model + "<br />"
					+ "[2-VERSION]=" + version + "<br />" + "[3-FINGERPRINT]="
					+ fingerprint + "<br />" + "[4-country]=" + country
					+ "<br />" + "[5-CPU]=" + cpu + "<br />"
					+ "[6-SOFTVERSION]=" + ConfigUtil.getVersion(this)
					+ "<br />";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mobileInfo;
	}

	private String encryptInfo1() {
		String info = statusHashMap.get(Consts.KEY_USERNAME) + " , "
				+ statusHashMap.get(Consts.KEY_PASSWORD);
		ArrayList<Device> deviceList = CacheUtil.getDevList();

		if (null != deviceList && 0 != deviceList.size()) {
			for (int i = 0; i < deviceList.size(); i++) {
				info += " , " + deviceList.get(i).getFullNo() + " , "
						+ deviceList.get(i).getUser() + " , "
						+ deviceList.get(i).getPwd();
			}
		}
		return ConfigUtil.getBase64(info);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
