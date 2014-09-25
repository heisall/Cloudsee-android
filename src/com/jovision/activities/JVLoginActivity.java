package com.jovision.activities;

import java.util.ArrayList;

import android.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.Handler.LoginHandler;
import com.jovision.adapters.UserSpinnerAdapter;
import com.jovision.newbean.Device;
import com.jovision.newbean.UserBean;
import com.jovision.thread.LoginThread;
import com.jovision.utils.ConfigUtil;

@SuppressLint("SetJavaScriptEnabled")
public class JVLoginActivity extends BaseActivity {

	private String userName = "";
	private String passWord = "";

	private int userListIndex;
	/** userlogin Fuction */
	private EditText userNameET;
	private EditText passwordET;
	private RelativeLayout userNameLayout;

	private Button onlineLoginBtn;
	private Animation mAnimationRight;// 演示点动画
	private Button registBtn;
	private Button localLoginBtn;
	private TextView showPointTV;
	private TextView findPassTV;
	private LoginHandler loginHandler;

	// 下拉箭头图片组件
	private ImageView moreUserIV;
	private ArrayList<UserBean> userList = new ArrayList<UserBean>();
	private PopupWindow pop;
	private UserSpinnerAdapter userAdapter;
	private ListView userListView;

	public static ArrayList<Device> deviceList = new ArrayList<Device>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.LOGIN_FUNCTION:// 登陆返回的结果
			Message msg = Message.obtain();
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.what = what;
			loginHandler.sendMessage(msg);
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.handleMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		loginHandler = new LoginHandler(this, false);
		// // 判断是否免登陆
		// UserBean user = SqlLiteUtil.queryLoginUser();
		// if (ConfigUtil.isConnected(JVLoginActivity.this) && null != user) {
		// statusHashMap.put(Consts.KEY_USERNAME, user.getUserName());
		// statusHashMap.put(Consts.KEY_PASSWORD, user.getUserPwd());
		// LoginThread loginThread = new LoginThread(JVLoginActivity.this);
		// loginThread.start();
		// } else {// 未联网或者是非免登陆状态
		//
		// }
		// deviceList.add(new Device("", 9101, "S", 53530352, "admin", "123",
		// true, 1, 0));
		// deviceList
		// .add(new Device("", 9101, "A", 361, "abc", "123", false, 1, 1));
		// deviceList.add(new Device("", 9101, "S", 26680286, "admin", "123",
		// true, 1, 2));
		// deviceList.add(new Device("", 9101, "S", 52942216, "admin", "123",
		// false, 1, 3));
		//
		// int size = deviceList.size();
		// for (int i = 0; i < size; i++) {
		// Jni.setLinkHelper(PlayUtil.genLinkHelperJson(deviceList));
		// }
		for (int i = 0; i < 5; i++) {
			UserBean userBean = new UserBean();
			userBean.setUserName("refactor" + i);
			userBean.setUserPwd("123456");
			userBean.setUserEmail("juyang@jovision.com");
			userBean.setLastLogin(0);
			userList.add(userBean);
		}

		// String str = userBean.toString();
		//
		// UserBean userBean2 = BeanUtil.toUser(str);
		// userBean2.setLastLogin(0);

		//
		// Device dev = new Device();
		// dev.setIp("192.168.14.15");
		// dev.setPort(9101);
		// dev.setGid("A");
		// dev.setNo(361);
		// dev.setFullNo("A361");
		// dev.setUser("abc");
		// dev.setPwd("123");
		// dev.setHomeProduct(false);
		// dev.setHelperEnabled(false);
		// dev.setDeviceType(05);
		// dev.setO5(true);
		// dev.setNickName("演示点");
		// dev.setIsDevice(0);
		// dev.setOnlineState(0);
		// dev.setHasWifi(1);
		//
		// ArrayList<Channel> list = new ArrayList<Channel>();
		// for (int i = 0; i < 4; i++) {
		// Channel channel = new Channel();
		// channel.setIndex(i);
		// channel.setChannel(i);
		// channel.setChannelName("A361_" + i);
		// channel.setConnecting(false);
		// channel.setConnecting(false);
		// channel.setRemotePlay(false);
		// channel.setConfigChannel(false);
		// channel.setAuto(false);
		// channel.setVoiceCall(true);
		// channel.setSurfaceCreated(true);
		// channel.setSendCMD(true);
		// list.add(channel);
		// }
		// dev.setChannelList(list);
		//
		// String devString = dev.toString();
		// MyLog.v("devString", devString);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.login_layout);

		/** userlogin Fuction */
		userNameET = (EditText) findViewById(R.id.username_et);
		passwordET = (EditText) findViewById(R.id.password_et);
		onlineLoginBtn = (Button) findViewById(R.id.onlinelogin_btn);
		findPassTV = (TextView) findViewById(R.id.findpass_tv);
		showPointTV = (TextView) findViewById(R.id.showpoint_tv);
		registBtn = (Button) findViewById(R.id.regist_btn);
		localLoginBtn = (Button) findViewById(R.id.locallogin_btn);

		if ("".equalsIgnoreCase(statusHashMap.get(Consts.KEY_USERNAME))) {
			if (null != userList && 0 != userList.size()) {
				userNameET.setText(userList.get(0).getUserName());
				userNameET.setSelection(userList.get(0).getUserName().length());
			}
		} else {
			userNameET.setText(statusHashMap.get(Consts.KEY_USERNAME));
		}
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
				if (pop == null) {
					if (null != userList && 0 != userList.size()) {
						userAdapter = new UserSpinnerAdapter(
								JVLoginActivity.this, userList, "");
						userListView = new ListView(JVLoginActivity.this);
						userListView.setDivider(null);
						pop = new PopupWindow(userListView, userNameLayout
								.getWidth(), 300);
						userListView.setAdapter(userAdapter);
						userListView.setVerticalScrollBarEnabled(false);
						userListView.setHorizontalScrollBarEnabled(false);
						userListView
								.setOnItemClickListener(mOnItemClickListener);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, 290);
						params.bottomMargin = 10;
						userListView.setLayoutParams(params);
						userListView.setFadingEdgeLength(0);
						userListView.setDivider(JVLoginActivity.this
								.getResources().getDrawable(
										R.drawable.user_list_divider));
						userListView.setCacheColorHint(JVLoginActivity.this
								.getResources().getColor(R.color.transparent));

						userListView.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.user_list_bg));
						pop.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.user_list_bg));
						pop.showAsDropDown(userNameLayout);
					}
				} else if (pop.isShowing()) {
					userAdapter.notifyDataSetChanged();
					pop.dismiss();
				} else if (!pop.isShowing()) {
					userAdapter.notifyDataSetChanged();
					pop.showAsDropDown(userNameLayout);
				}
			}
		});

		mAnimationRight = AnimationUtils.loadAnimation(JVLoginActivity.this,
				R.anim.rotate_right);
		mAnimationRight.setFillAfter(true);
		showPointTV.setAnimation(mAnimationRight);
		onlineLoginBtn.setOnClickListener(myOnClickListener);
		showPointTV.setOnClickListener(myOnClickListener);
		registBtn.setOnClickListener(myOnClickListener);
		localLoginBtn.setOnClickListener(myOnClickListener);
		findPassTV.setOnClickListener(myOnClickListener);
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int index, long arg3) {
			try {
				userListIndex = index;
				ImageView deleUserIV = (ImageView) adapterView
						.getChildAt(index).findViewById(R.id.otheruser_del);
				deleUserIV.setOnClickListener(myOnClickListener);

				userNameET.setText(userList.get(index).getUserName());
				passwordET.setText(userList.get(index).getUserPwd());
				if (pop.isShowing()) {
					pop.dismiss();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};
	/**
	 * click事件
	 */
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left: {
				backMethod();
				break;
			}
			/** 删除列表用户 */
			case R.id.otheruser_del: {
				userList.remove(userListIndex);
				userAdapter.notifyDataSetChanged();
				break;
			}
			case R.id.findpass_tv: {
				if (!ConfigUtil.isConnected(JVLoginActivity.this)) {
					alertNetDialog();
				} else {
					Intent intentFP = new Intent(JVLoginActivity.this,
							JVFindPassActivity.class);
					JVLoginActivity.this.startActivity(intentFP);
				}
				break;
			}
			case R.id.onlinelogin_btn:// 在线登陆
				if ("".equalsIgnoreCase(userNameET.getText().toString())) {
					showTextToast(R.string.login_str_username_notnull);
				} else if ("".equalsIgnoreCase(passwordET.getText().toString())) {
					showTextToast(R.string.login_str_loginpass1_notnull);
				} else {
					createDialog(R.string.login_str_loging);
					userName = userNameET.getText().toString();
					passWord = passwordET.getText().toString();
					statusHashMap.put(Consts.KEY_USERNAME, userName);
					statusHashMap.put(Consts.KEY_PASSWORD, passWord);
					statusHashMap.put(Consts.LOCAL_LOGIN, "false");
					// 登录
					LoginThread loginThread = new LoginThread(
							JVLoginActivity.this);
					loginThread.start();
				}
				break;
			case R.id.regist_btn:// 注册
				Intent registIntent = new Intent();
				registIntent.setClass(JVLoginActivity.this,
						JVRegisterActivity.class);
				JVLoginActivity.this.startActivity(registIntent);
				break;
			case R.id.showpoint_tv:// 演示点
				// Intent intent = new Intent(JVLoginActivity.this,
				// JVMarkerActivity.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// JVLoginActivity.this.startActivity(intent);
				break;
			case R.id.locallogin_btn:// 本地登录
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

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

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
	}
}
