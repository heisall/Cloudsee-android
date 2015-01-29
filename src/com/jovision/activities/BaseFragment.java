package com.jovision.activities;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.IHandlerNotify;
import com.jovision.MainApplication;
import com.jovision.bean.User;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.Url;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.UserUtil;
import com.tencent.stat.StatService;

/**
 * Fragment 基类
 */
public abstract class BaseFragment extends Fragment implements IHandlerNotify,
		IHandlerLikeNotify {
	private final static String TAG = "BaseFragment";

	protected FragHandler fragHandler = new FragHandler(this);
	protected IHandlerNotify fragNotify = this;

	protected View mParent;
	protected BaseActivity mActivity;

	/** topBar */
	protected LinearLayout topBar;
	protected Button leftBtn;
	protected TextView currentMenu;
	protected Button rightBtn;
	protected static RelativeLayout alarmnet;
	protected TextView accountError;
	private static boolean local = false;// 是否本地登陆

	// protected static boolean isshow;

	protected static class FragHandler extends Handler {

		private BaseFragment fragment;

		public FragHandler(BaseFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void handleMessage(Message msg) {
			if (null != msg) {
				fragment.fragNotify.onHandler(msg.what, msg.arg1, msg.arg2,
						msg.obj);
			}
			switch (msg.what) {
			case Consts.WHAT_ALARM_NET:
				if (null != alarmnet && !local) {
					alarmnet.setVisibility(View.VISIBLE);
				}
				break;
			case Consts.WHAT_ALARM_NET_WEEK:
				if (null != alarmnet) {
					alarmnet.setVisibility(View.GONE);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		mParent = getView();

		if (null == mActivity
				| false == mActivity instanceof IHandlerLikeNotify) {
			throw new ClassCastException(
					"mActivity must an IHandlerLikeNotify impl");
		}

		local = Boolean
				.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN));
		topBar = (LinearLayout) mParent.findViewById(R.id.top_bar);
		leftBtn = (Button) mParent.findViewById(R.id.btn_left);
		currentMenu = (TextView) mParent.findViewById(R.id.currentmenu);
		rightBtn = (Button) mParent.findViewById(R.id.btn_right);
		alarmnet = (RelativeLayout) mParent.findViewById(R.id.alarmnet);
		accountError = (TextView) mParent.findViewById(R.id.accounterror);
		try {
			if (null != leftBtn) {
				leftBtn.setOnClickListener(mOnClickListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != alarmnet) {
			alarmnet.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (null != mActivity.statusHashMap
							.get(Consts.ACCOUNT_ERROR)) {
						int errorCode = Integer
								.parseInt(mActivity.statusHashMap
										.get(Consts.ACCOUNT_ERROR));
						switch (errorCode) {
						case Consts.WHAT_ALARM_NET:// 网络异常
							if (android.os.Build.VERSION.SDK_INT > 10) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_SETTINGS));
							} else {
								startActivity(new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS));
							}
							break;
						case Consts.WHAT_HAS_NOT_LOGIN:// 账号未登录
							mActivity.createDialog("", false);
							ReloginTask reLoginTask = new ReloginTask();
							String[] params = new String[3];
							reLoginTask.execute(params);
							break;
						}
					}
				}
			});
		}
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left:
				if (!JVMyDeviceFragment.isshow) {
					mActivity.openExitDialog();
				}
				break;
			default:
				break;

			}
		}

	};

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		((MainApplication) mActivity.getApplication()).setCurrentNotifyer(this);
		StatService.onResume(mActivity);
		if (null != ((MainApplication) mActivity.getApplication()).statusHashMap
				.get(Consts.ACCOUNT_ERROR)) {
			int errorCode = Integer.parseInt(((MainApplication) mActivity
					.getApplication()).statusHashMap.get(Consts.ACCOUNT_ERROR));
			switch (errorCode) {
			case Consts.WHAT_ALARM_NET:// 网络异常
				if (null != alarmnet && !local) {
					alarmnet.setVisibility(View.VISIBLE);
					if (null != accountError) {
						accountError.setText(R.string.network_error_tips);
					}
				}
				break;
			case Consts.WHAT_ALARM_NET_WEEK:// 网络恢复正常
				if (null != alarmnet) {
					alarmnet.setVisibility(View.GONE);
				}
				break;
			case Consts.WHAT_HAS_NOT_LOGIN:// 账号未登录
				if (null != alarmnet && !local) {
					alarmnet.setVisibility(View.VISIBLE);
					if (null != accountError) {
						accountError.setText(R.string.account_error_tips);
					}
				}
				break;
			case Consts.WHAT_HAS_LOGIN_SUCCESS:// 账号正常登陆
				if (null != alarmnet) {
					alarmnet.setVisibility(View.GONE);
				}
				break;
			}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		StatService.onPause(mActivity);
		super.onPause();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private int loginRes1 = 0;

	// 设置三种类型参数分别为String,Integer,String
	class ReloginTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			if (!ConfigUtil.isConnected(mActivity)) {
				loginRes1 = JVAccountConst.LOGIN_FAILED_1;
			} else {
				MyLog.v("BaseA", "LOGIN---E");

				if ("false".equals(mActivity.statusHashMap
						.get(Consts.KEY_INIT_ACCOUNT_SDK))) {
					// Toast.makeText(mContext, "初始化账号SDK失败，请重新运行程序",
					// Toast.LENGTH_LONG)
					// .show();
					// return "";
					MyLog.e("Login", "初始化账号SDK失败");
					ConfigUtil.initAccountSDK(((MainApplication) mActivity
							.getApplication()));// 初始化账号SDK
				}

				String strRes = "";
				Log.i("TAG", MySharedPreference.getBoolean("TESTSWITCH")
						+ "LOGIN");
				if (!MySharedPreference.getBoolean("TESTSWITCH")) {
					strRes = AccountUtil.onLoginProcessV2(mActivity,
							mActivity.statusHashMap.get(Consts.KEY_USERNAME),
							mActivity.statusHashMap.get(Consts.KEY_PASSWORD),
							Url.SHORTSERVERIP, Url.LONGSERVERIP);
				} else {
					strRes = AccountUtil.onLoginProcessV2(mActivity,
							mActivity.statusHashMap.get(Consts.KEY_USERNAME),
							mActivity.statusHashMap.get(Consts.KEY_PASSWORD),
							Url.SHORTSERVERIPTEST, Url.LONGSERVERIPTEST);
				}
				JSONObject respObj = null;
				try {
					respObj = new JSONObject(strRes);
					loginRes1 = respObj.optInt("arg1", 1);
					// {"arg1":8,"arg2":0,"data":{"channel_ip":"210.14.156.66","online_ip":"210.14.156.66"},"desc":"after the judge and longin , begin the big switch...","result":0}
					if (!MySharedPreference.getBoolean("TESTSWITCH")) {
					}
					String data = respObj.optString("data");
					if (null != data && !"".equalsIgnoreCase(data)) {
						JSONObject dataObj = new JSONObject(data);
						String channelIp = dataObj.optString("channel_ip");
						String onlineIp = dataObj.optString("online_ip");
						if (Consts.LANGUAGE_ZH == ConfigUtil
								.getServerLanguage()) {
							MySharedPreference
									.putString("ChannelIP", channelIp);
							MySharedPreference.putString("OnlineIP", onlineIp);
							MySharedPreference.putString("ChannelIP_en", "");
							MySharedPreference.putString("OnlineIP_en", "");
						} else {
							MySharedPreference.putString("ChannelIP_en",
									channelIp);
							MySharedPreference.putString("OnlineIP_en",
									onlineIp);
							MySharedPreference.putString("ChannelIP", "");
							MySharedPreference.putString("OnlineIP", "");
						}
					}

				} catch (JSONException e) {
					loginRes1 = JVAccountConst.LOGIN_FAILED_2;
					e.printStackTrace();
				}
				MyLog.v("BaseA", "LOGIN---X");
			}
			return loginRes1;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			mActivity.dismissDialog();
			switch (result) {
			case JVAccountConst.LOGIN_SUCCESS: {
				StatService.trackCustomEvent(
						mActivity,
						"onlinelogin",
						mActivity.getResources().getString(
								R.string.census_onlinelogin));
				MySharedPreference.putString("UserName",
						mActivity.statusHashMap.get(Consts.KEY_USERNAME));
				MySharedPreference.putString("PassWord",
						mActivity.statusHashMap.get(Consts.KEY_PASSWORD));
				// 重置手动注销标志，离线报警使用，如果为手动注销账号，不接收离线报警
				MySharedPreference.putBoolean(Consts.MANUAL_LOGOUT_TAG, false);
				User user = new User();
				user.setPrimaryID(System.currentTimeMillis());
				user.setUserName(mActivity.statusHashMap
						.get(Consts.KEY_USERNAME));
				user.setUserPwd(mActivity.statusHashMap
						.get(Consts.KEY_PASSWORD));
				user.setLastLogin(1);
				user.setJudgeFlag(1);
				UserUtil.addUser(user);
				MyLog.v("BaseA", "LoginSuccess");
				mActivity.statusHashMap.put(Consts.ACCOUNT_ERROR,
						String.valueOf(Consts.WHAT_ACCOUNT_NORMAL));
				if (null != alarmnet) {
					alarmnet.setVisibility(View.GONE);
				}
				break;
			}
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}
}
