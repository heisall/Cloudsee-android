package com.jovision.activities;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.jovision.commons.GetDemoTask;
import com.jovision.commons.LoginTask;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;
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
						case Consts.WHAT_HEART_TCP_ERROR:
						case Consts.WHAT_HEART_TCP_CLOSED:
						case Consts.WHAT_HEART_ERROR:// 心跳异常
							if (android.os.Build.VERSION.SDK_INT > 10) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_SETTINGS));
							} else {
								startActivity(new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS));
							}
							break;
						case Consts.WHAT_HAS_NOT_LOGIN:// 账号未登录
						case Consts.WHAT_SESSION_FAILURE:// session失效
							if (null != alarmnet && !local) {
								alarmnet.setVisibility(View.VISIBLE);
								if (null != accountError) {
									accountError
											.setText(R.string.account_error_tips);
								}
							}
							mActivity.createDialog("", false);
							LoginTask task = new LoginTask(false, mActivity,
									(MainApplication) mActivity
											.getApplication(),
									mActivity.statusHashMap, alarmnet);
							String[] params = new String[3];
							task.execute(params);
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
				if (!JVMyDeviceFragment.isshow && !JVInfoFragment.isshow) {
					mActivity.openExitDialog();
				}
				if (JVInfoFragment.isshow) {
					mActivity.finish();
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
		MyLog.v("notifyer",
				((MainApplication) mActivity.getApplication()).currentNotifyer
						+ "");
		String notifer = ((MainApplication) mActivity.getApplication()).currentNotifyer
				+ "";
		if (notifer.startsWith("JVMyDeviceFragment")) {
			if (null != ((MainApplication) mActivity.getApplication()).statusHashMap
					.get(Consts.ACCOUNT_ERROR)) {
				int errorCode = Integer.parseInt(((MainApplication) mActivity
						.getApplication()).statusHashMap
						.get(Consts.ACCOUNT_ERROR));
				switch (errorCode) {
				case Consts.WHAT_HEART_ERROR:// 心跳异常
					if (null != alarmnet && !local) {
						alarmnet.setVisibility(View.VISIBLE);
						if (null != accountError) {
							accountError
									.setText(getString(R.string.network_error_tips));
						}
					}
					break;
				case Consts.WHAT_HEART_TCP_ERROR:
				case Consts.WHAT_HEART_TCP_CLOSED:
					if (null != alarmnet && !local) {
						alarmnet.setVisibility(View.VISIBLE);
						if (null != accountError) {
							accountError
									.setText(getString(R.string.network_error_tips));
						}
					}
					break;
				case Consts.WHAT_HEART_NORMAL:// 心跳恢复正常
					if (null != alarmnet) {
						alarmnet.setVisibility(View.GONE);
					}
					GetDemoTask demoTask = new GetDemoTask(mActivity);
					String[] demoParams = new String[3];
					if (!Boolean.valueOf(mActivity.statusHashMap
							.get(Consts.LOCAL_LOGIN))) {
						String sessionResult = ConfigUtil.getSession();

						MyLog.v("session", sessionResult);
						demoParams[0] = sessionResult;
					} else {
						demoParams[0] = "";
					}
					demoParams[1] = "1";
					demoParams[2] = "fragmentString";
					demoTask.execute(demoParams);
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
				case Consts.WHAT_SESSION_FAILURE:// session失效
					if (null != alarmnet && !local) {
						alarmnet.setVisibility(View.VISIBLE);
						if (null != accountError) {
							accountError.setText(R.string.account_error_tips);
						}
					}
					mActivity.createDialog("", false);
					LoginTask loginTask = new LoginTask(false, mActivity,
							(MainApplication) mActivity.getApplication(),
							mActivity.statusHashMap, alarmnet);
					String[] params = new String[3];
					loginTask.execute(params);
					break;
				}
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

}
