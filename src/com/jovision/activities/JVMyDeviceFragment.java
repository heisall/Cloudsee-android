package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.MyDeviceListAdapter;
import com.jovision.adapters.PopWindowAdapter;
import com.jovision.bean.Device;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.ImageViewPager;
import com.jovision.views.RefreshableView;
import com.jovision.views.RefreshableView.PullToRefreshListener;

/**
 * 我的设备
 */
public class JVMyDeviceFragment extends BaseFragment {
	private String TAG = "MyDeviceFragment";

	private static final int WHAT_SHOW_PRO = 0x01;// 显示dialog
	public static final int DEVICE_GETDATA_SUCCESS = 0x02;// 设备加载成功--
	public static final int DEVICE_GETDATA_FAILED = 0x03;// 设备加载失败--
	public static final int DEVICE_NO_DEVICE = 0x04;// 暂无设备--

	public static final int BROAD_DEVICE_LIST = 0x05;// 广播设备列表--
	public static final int BROAD_ADD_DEVICE = 0x06;// 添加设备的广播--
	public static final int BROAD_THREE_MINITE = 0x07;// 三分钟广播--

	private RefreshableView refreshableView;

	/** 叠加两个 布局 */
	private LinearLayout deviceLayout; // 设备列表界面
	private LinearLayout quickSetSV; // 快速配置界面
	private Button quickSet;
	private Button addDevice;

	/** 广告位 */
	private LayoutInflater inflater;
	private View adView;
	private ImageViewPager imageScroll; // 图片容器
	private LinearLayout ovalLayout; // 圆点容器
	private List<View> listViews; // 图片组
	/** 弹出框 */
	private Dialog initDialog;// 显示弹出框
	private TextView dialogCancel;// 取消按钮
	private TextView dialogCompleted;// 确定按钮
	// 设备名称
	private TextView device_name;
	// 设备昵称
	private EditText device_nicket;
	// 设备昵称编辑键
	private ImageView device_niceet_cancle;
	// 设备用户名
	private EditText device_nameet;
	// 设备用户名编辑键
	private ImageView device_nameet_cancle;
	// 设备密码
	private EditText device_passwordet;
	// 设备密码编辑键
	private ImageView device_password_cancleI;
	/** 设备列表 */
	private ListView myDeviceListView;
	private ArrayList<Device> myDeviceList = new ArrayList<Device>();
	MyDeviceListAdapter myDLAdapter;

	/** 3分钟广播 */
	private Timer broadTimer;
	private TimerTask broadTimerTask;

	public boolean localFlag = false;// 本地登陆标志位
	public String devicename;

	public int broadTag = 0;
	private ArrayList<Device> broadList = new ArrayList<Device>();// 广播到的设备列表

	private PopupWindow popupWindow; // 声明PopupWindow对象；

	private ListView popListView;

	private String[] popFunArray;

	private PopWindowAdapter popWindowAdapter;

	private int[] popDrawarray = new int[] {
			R.drawable.mydevice_popwindowonse_icon,
			R.drawable.mydevice_popwindowtwo_icon,
			R.drawable.mydevice_popwindowthree_icon,
			R.drawable.mydevice_popwindowfour_icon };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mydevice, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		mParent = getView();

		popFunArray = mActivity.getResources()
				.getStringArray(R.array.array_pop);
		currentMenu.setText(mActivity.getResources().getString(
				R.string.my_device));
		currentMenu.setText(R.string.my_device);

		localFlag = Boolean.valueOf(mActivity.statusHashMap
				.get(Consts.LOCAL_LOGIN));
		devicename = mActivity.statusHashMap.get(Consts.KEY_USERNAME);
		inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		refreshableView = (RefreshableView) mParent
				.findViewById(R.id.device_refreshable_view);
		adView = inflater.inflate(R.layout.ad_layout, null);

		deviceLayout = (LinearLayout) mParent.findViewById(R.id.devicelayout);
		quickSetSV = (LinearLayout) mParent
				.findViewById(R.id.quickinstalllayout);
		quickSet = (Button) mParent.findViewById(R.id.quickinstall);
		addDevice = (Button) mParent.findViewById(R.id.adddevice);
		quickSet.setOnClickListener(myOnClickListener);
		addDevice.setOnClickListener(myOnClickListener);

		/** 广告条 */
		imageScroll = (ImageViewPager) adView.findViewById(R.id.imagescroll);
		ovalLayout = (LinearLayout) adView.findViewById(R.id.dot_layout);
		InitViewPager();// 初始化图片
		// 开始滚动
		imageScroll.start(mActivity, listViews, 4000, ovalLayout,
				R.layout.dot_item, R.id.ad_item_v, R.drawable.dot_focused,
				R.drawable.dot_normal);

		myDLAdapter = new MyDeviceListAdapter(mActivity, this);
		myDeviceListView = (ListView) mParent
				.findViewById(R.id.device_listview);

		myDeviceListView.addHeaderView(adView);
		rightBtn.setOnClickListener(myOnClickListener);

		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				GetDevTask task = new GetDevTask();
				String[] strParams = new String[3];
				task.execute(strParams);
			}

			@Override
			public void onLayoutTrue() {
				refreshableView.finishRefreshing();
			}

		}, 0);

		// 非3G加广播设备
		if (!ConfigUtil.is3G(mActivity, false)) {
			broadTimer = new Timer();
			broadTimerTask = new TimerTask() {
				@Override
				public void run() {
					Log.v(TAG, "三分钟时间到--发广播");
					broadTag = BROAD_THREE_MINITE;
					PlayUtil.broadCast(mActivity);
				}
			};
			broadTimer.schedule(broadTimerTask, 3 * 60 * 1000, 3 * 60 * 1000);
		}

		boolean hasGot = Boolean.parseBoolean(mActivity.statusHashMap
				.get(Consts.HAG_GOT_DEVICE));

		if (hasGot) {
			myDeviceList = CacheUtil.getDevList();
			refreshList();
		} else {
			GetDevTask task = new GetDevTask();
			String[] strParams = new String[3];
			task.execute(strParams);
		}

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_right:
				initPop();
				// 点击按钮时，pop显示状态，显示中就消失，否则显示
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				} else {
					// 显示在below正下方
					popupWindow.showAsDropDown(view,
							-mActivity.disMetrics.widthPixels / 2 + 60, 10);
				}
				break;
			case R.id.device_nameet_cancle:
				device_nameet.setText("");
				break;
			case R.id.device_passwrodet_cancle:
				device_passwordet.setText("");
				break;
			case R.id.device_nicket_cancle:
				device_nicket.setText("");
				break;
			case R.id.quickinstall:
				((ShakeActivity) mActivity).startSearch(false);
				break;
			case R.id.adddevice:
				Intent addIntent = new Intent();
				addIntent.setClass(mActivity, JVAddDeviceActivity.class);
				addIntent.putExtra("QR", false);
				mActivity.startActivity(addIntent);
				break;
			default:
				break;
			}

		}

	};

	// 点击加号弹出的popWindow
	private void initPop() {
		View v = LayoutInflater.from(mActivity).inflate(R.layout.popview, null); // 将布局转化为view
		popListView = (ListView) v.findViewById(R.id.popwindowlist);
		popWindowAdapter = new PopWindowAdapter(JVMyDeviceFragment.this);
		popWindowAdapter.setData(popFunArray, popDrawarray);
		popListView.setAdapter(popWindowAdapter);
		if (popupWindow == null) {
			/**
			 * public PopupWindow (View contentView, int width, int height)
			 * contentView:布局view width：布局的宽 height：布局的高
			 */
			popupWindow = new PopupWindow(v,
					mActivity.disMetrics.widthPixels / 2,
					LayoutParams.WRAP_CONTENT);
		}
		popupWindow.setFocusable(true); // 获得焦点
		popupWindow.setOutsideTouchable(true);// 是否可点击

		popupWindow.getContentView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				popupWindow.setFocusable(false); // 失去焦点
				popupWindow.dismiss(); // pop消失
				return false;
			}
		});
		popListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {
				case 0: {// 云视通号
					Intent addIntent = new Intent();
					addIntent.setClass(mActivity, JVAddDeviceActivity.class);
					addIntent.putExtra("QR", false);
					mActivity.startActivity(addIntent);
					break;
				}
				case 1: {// 二维码扫描
					Intent addIntent = new Intent();
					addIntent.setClass(mActivity, JVAddDeviceActivity.class);
					addIntent.putExtra("QR", true);
					mActivity.startActivity(addIntent);
					break;
				}
				case 2: {// 无线设备
					((ShakeActivity) mActivity).startSearch(false);
					break;
				}
				case 3: {// 局域网设备
					fragHandler.sendEmptyMessage(WHAT_SHOW_PRO);
					if (!ConfigUtil.is3G(mActivity, false)) {// 闈3G鍔犲箍鎾­璁惧¤
						broadTag = BROAD_ADD_DEVICE;
						broadList.clear();
						PlayUtil.broadCast(mActivity);
					} else {
						((BaseActivity) mActivity)
								.showTextToast(R.string.notwifi_forbid_func);
					}
					break;
				}

				}
				popupWindow.dismiss(); // pop消失
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		myDeviceList = CacheUtil.getDevList();
		refreshList();
	}

	/**
	 * 刷新列表
	 */
	public void refreshList() {

		boolean hasGot = Boolean.parseBoolean(mActivity.statusHashMap
				.get(Consts.HAG_GOT_DEVICE));

		myDLAdapter.setData(myDeviceList);
		myDeviceListView.setAdapter(myDLAdapter);
		myDLAdapter.notifyDataSetChanged();

		if (hasGot) {
			if (null == myDeviceList || 0 == myDeviceList.size()) {
				deviceLayout.setVisibility(View.GONE);
				quickSetSV.setVisibility(View.VISIBLE);
			} else {
				deviceLayout.setVisibility(View.VISIBLE);
				quickSetSV.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// imageScroll.stopTimer();
	}

	@Override
	public void onPause() {
		super.onPause();
		CacheUtil.saveDevList(myDeviceList);
		// imageScroll.stopTimer();
	}

	@Override
	public void onStop() {
		super.onStop();
		// imageScroll.stopTimer();
	}

	/**
	 * 初始化图片
	 */
	private void InitViewPager() {
		listViews = new ArrayList<View>();
		int[] imageResId = new int[] { R.drawable.a, R.drawable.b,
				R.drawable.c, R.drawable.d, R.drawable.e };
		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(mActivity);
			imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {// 设置图片点击事件
					// ((BaseActivity) mActivity).showTextToast("点击了:"
					// + imageScroll.getCurIndex());
				}
			});
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			listViews.add(imageView);
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// MyLog.v("TAG",
		// "onTabAction:what="+what+";arg1="+arg1+";arg2="+arg1+";obj="+obj.toString());
		switch (what) {
		case WHAT_SHOW_PRO: {
			((BaseActivity) mActivity).createDialog("");
			break;
		}
		case JVTabActivity.TAB_BACK: {// tab 返回事件，保存数据
			CacheUtil.saveDevList(myDeviceList);
			break;
		}

		// 广播回调
		case Consts.CALL_LAN_SEARCH: {
			// onTabAction:what=168;arg1=0;arg2=0;obj={"count":1,"curmod":0,"gid":"A","ip":"192.168.21.238","netmod":0,"no":283827713,"port":9101,"timeout":0,"type":59162,"variety":3}

			if (broadTag == BROAD_DEVICE_LIST || broadTag == BROAD_THREE_MINITE) {// 三分钟广播
																					// 或
																					// 广播设备列表
				JSONObject broadObj;
				try {
					broadObj = new JSONObject(obj.toString());
					if (0 == broadObj.optInt("timeout")) {
						int size = myDeviceList.size();
						for (int i = 0; i < size; i++) {
							Device device = myDeviceList.get(i);
							// 是同一个设备
							if (device.getGid().equalsIgnoreCase(
									broadObj.optString("gid"))
									&& device.getNo() == broadObj.optInt("no")) {
								device.setIp(broadObj.optString("ip"));
								device.setPort(broadObj.optInt("port"));
								device.setOnlineState(1);// 广播都在线
							}
						}
					} else if (1 == broadObj.optInt("timeout")) {

					}
					MyLog.v(TAG, "onTabAction:what=" + what + ";arg1=" + arg1
							+ ";arg2=" + arg1 + ";obj=" + obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (broadTag == BROAD_ADD_DEVICE) {// 广播添加设备
				JSONObject broadObj;
				try {
					broadObj = new JSONObject(obj.toString());
					if (0 == broadObj.optInt("timeout")) {
						String gid = broadObj.optString("gid");
						int no = broadObj.optInt("no");
						String ip = broadObj.optString("ip");
						int port = broadObj.optInt("port");
						int count = broadObj.optInt("count");
						String broadDevNum = gid + no;

						if (!hasDev(broadDevNum)) {
							Device broadDev = new Device(ip, port, gid, no,
									mActivity.getResources().getString(
											R.string.str_default_user),
									mActivity.getResources().getString(
											R.string.str_default_pass), false,
									count, 0);
							broadDev.setOnlineState(1);// 广播都在线
							broadList.add(broadDev);
							MyLog.v(TAG, "广播到一个设备--" + broadDevNum);
						}
					} else if (1 == broadObj.optInt("timeout")) {

						AddDevTask task = new AddDevTask();
						String[] strParams = new String[3];
						strParams[0] = broadList.toString();
						task.execute(strParams);
					}
					MyLog.v(TAG, "onTabAction:what=" + what + ";arg1=" + arg1
							+ ";arg2=" + arg1 + ";obj=" + obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			break;
		}
		case MyDeviceListAdapter.DEVICE_ITEM_CLICK: {// 设备单击事件
			myDLAdapter.setShowDelete(false);
			myDLAdapter.notifyDataSetChanged();
			Device dev = myDeviceList.get(arg1);
			if (1 == dev.getChannelList().size()) {// 1个通道直接播放
				PlayUtil.prepareConnect(myDeviceList, arg1);
				Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
				intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);
				intentPlay.putExtra("DeviceIndex", arg1);
				intentPlay.putExtra("ChannelofChannel", dev.getChannelList()
						.toList().get(0).getChannel());
				mActivity.startActivity(intentPlay);
			} else {// 多个通道查看通道列表
				Intent intentPlay = new Intent(mActivity,
						JVChannelsActivity.class);
				intentPlay.putExtra("DeviceIndex", arg1);
				mActivity.startActivity(intentPlay);
			}

			break;
		}
		case MyDeviceListAdapter.DEVICE_ITEM_LONG_CLICK: {// 设备长按事件
			myDLAdapter.setShowDelete(true);
			myDLAdapter.notifyDataSetChanged();
			break;
		}
		case MyDeviceListAdapter.DEVICE_ITEM_DEL_CLICK: {// 设备删除事件
			DelDevTask task = new DelDevTask();
			String[] strParams = new String[3];
			strParams[0] = String.valueOf(arg1);
			task.execute(strParams);
			break;
		}
		case MyDeviceListAdapter.DEVICE_EDIT_CLICK: {// 设备编辑事件
			initSummaryDialog(myDeviceList, arg1);
		}
		}
	}

	/**
	 * 判断设备是否在设备列表里
	 * 
	 * @param devNum
	 * @return
	 */
	public boolean hasDev(String devNum) {
		boolean has = false;
		// for (int i = 0; i < size; i++) {
		// Device device = myDeviceList.get(i);
		for (Device dev : myDeviceList) {
			if (devNum.equalsIgnoreCase(dev.getFullNo())) {
				has = true;
				break;
			}
		}
		return has;
	}

	/** 弹出框初始化 */
	private void initSummaryDialog(ArrayList<Device> myDeviceList,
			final int agr1) {
		initDialog = new Dialog(mActivity, R.style.mydialog);
		View view = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_summary, null);
		initDialog.setContentView(view);
		dialogCancel = (TextView) view.findViewById(R.id.dialog_img_cancel);
		dialogCompleted = (TextView) view
				.findViewById(R.id.dialog_img_completed);
		device_name = (TextView) view.findViewById(R.id.device_name);
		device_nicket = (EditText) view.findViewById(R.id.device_nicket);
		device_niceet_cancle = (ImageView) view
				.findViewById(R.id.device_nicket_cancle);
		device_nameet = (EditText) view.findViewById(R.id.device_nameet);
		device_nameet_cancle = (ImageView) view
				.findViewById(R.id.device_nameet_cancle);
		device_passwordet = (EditText) view
				.findViewById(R.id.device_passwrodet);
		device_password_cancleI = (ImageView) view
				.findViewById(R.id.device_passwrodet_cancle);
		device_nameet_cancle.setOnClickListener(myOnClickListener);
		device_niceet_cancle.setOnClickListener(myOnClickListener);
		device_password_cancleI.setOnClickListener(myOnClickListener);
		device_name.setText(myDeviceList.get(agr1).getFullNo());
		device_nameet.setText(myDeviceList.get(agr1).getUser());
		device_passwordet.setText(myDeviceList.get(agr1).getPwd());
		if (!("").equals(myDeviceList.get(agr1).getNickName())) {
			device_nicket.setText(myDeviceList.get(agr1).getNickName());
		}
		initDialog.show();
		device_name.setFocusable(true);
		device_name.setFocusableInTouchMode(true);
		dialogCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initDialog.dismiss();
			}
		});
		dialogCompleted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equalsIgnoreCase(device_nameet.getText().toString())) {// 用户名不可为空，其他不用验证
					mActivity.showTextToast(mActivity.getResources().getString(
							R.string.login_str_device_account_notnull));
				} else if (!ConfigUtil.checkDeviceUsername(device_nameet
						.getText().toString())) {
					mActivity.showTextToast(mActivity.getResources().getString(
							R.string.login_str_device_account_error));
				} else if (!ConfigUtil.checkDevicePwd(device_passwordet
						.getText().toString())) {
					mActivity.showTextToast(mActivity.getResources().getString(
							R.string.login_str_device_pass_error));
				} else {
					ModifyDevTask task = new ModifyDevTask();
					String[] strParams = new String[5];
					strParams[0] = agr1 + "";
					strParams[1] = device_name.getText().toString();
					strParams[2] = device_nameet.getText().toString();
					strParams[3] = device_passwordet.getText().toString();
					strParams[4] = device_nicket.getText().toString();
					task.execute(strParams);
				}
			}
		});
	}

	// 保存更改设备信息线程
	class ModifyDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			try {
				int delIndex = Integer.parseInt(params[0]);
				if (localFlag) {// 本地保存修改信息
					delRes = 0;
				} else {
					String name = mActivity.statusHashMap
							.get(Consts.KEY_USERNAME);
					delRes = DeviceUtil.modifyDevice(name, params[1],
							params[4], params[2], params[3]);
				}
				if (0 == delRes) {
					myDeviceList.get(delIndex).setFullNo(params[1]);
					myDeviceList.get(delIndex).setUser(params[2]);
					myDeviceList.get(delIndex).setPwd(params[3]);
					myDeviceList.get(delIndex).setNickName(params[4]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return delRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			((BaseActivity) mActivity).dismissDialog();
			if (0 == result) {
				((BaseActivity) mActivity)
						.showTextToast(R.string.login_str_device_edit_success);
				myDLAdapter.notifyDataSetChanged();
			} else {
				((BaseActivity) mActivity)
						.showTextToast(R.string.login_str_device_edit_failed);
			}
			initDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	// 删除设备线程
	class DelDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			boolean localFlag = Boolean
					.valueOf(((BaseActivity) mActivity).statusHashMap
							.get(Consts.LOCAL_LOGIN));
			try {
				int delIndex = Integer.parseInt(params[0]);
				if (localFlag) {// 本地删除
					delRes = 0;
				} else {
					delRes = DeviceUtil.unbindDevice(
							((BaseActivity) mActivity).statusHashMap
									.get("KEY_USERNAME"),
							myDeviceList.get(delIndex).getFullNo());
				}

				if (0 == delRes) {
					myDeviceList.remove(delIndex);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return delRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			((BaseActivity) mActivity).dismissDialog();
			if (0 == result) {
				((BaseActivity) mActivity)
						.showTextToast(R.string.del_device_succ);
				myDLAdapter.setShowDelete(false);
				refreshList();
			} else {
				((BaseActivity) mActivity)
						.showTextToast(R.string.del_device_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	// 获取设备列表线程
	class GetDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int getRes = 0;
			try {
				if (!localFlag) {// 非本地登录，无论是否刷新都执行
					fragHandler.sendEmptyMessage(WHAT_SHOW_PRO);
					// 获取所有设备列表和通道列表 ,如果设备请求失败，多请求一次
					if (null == myDeviceList || 0 == myDeviceList.size()) {
						myDeviceList = DeviceUtil
								.getUserDeviceList(mActivity.statusHashMap
										.get(Consts.KEY_USERNAME));
					}
					if (null == myDeviceList || 0 == myDeviceList.size()) {
						myDeviceList = DeviceUtil
								.getUserDeviceList(mActivity.statusHashMap
										.get(Consts.KEY_USERNAME));
					}
					if (null != myDeviceList && 0 != myDeviceList.size()) {
						int size = myDeviceList.size();
						if (0 != size) {
							for (int i = 0; i < myDeviceList.size(); i++) {
								if (null == myDeviceList.get(i)
										.getChannelList()
										|| 0 == myDeviceList.get(i)
												.getChannelList().size()) {
									myDeviceList.get(i).setChannelList(
											DeviceUtil.getDevicePointList(
													myDeviceList.get(i),
													myDeviceList.get(i)
															.getFullNo()));
								}
							}
						}
					}

				} else if (localFlag) {// 本地登录
					myDeviceList = CacheUtil.getDevList();
				}

				mActivity.statusHashMap.put(Consts.HAG_GOT_DEVICE, "true");
				if (null != myDeviceList && 0 != myDeviceList.size()) {// 获取设备成功,去广播设备列表
					getRes = DEVICE_GETDATA_SUCCESS;
				} else if (null != myDeviceList && 0 == myDeviceList.size()) {// 无数据
					getRes = DEVICE_NO_DEVICE;
				} else {// 获取设备失败
					getRes = DEVICE_GETDATA_FAILED;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return getRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			((BaseActivity) mActivity).dismissDialog();
			refreshableView.finishRefreshing();
			switch (result) {
			// 从服务器端获取设备成功
			case DEVICE_GETDATA_SUCCESS: {
				mActivity.statusHashMap.put(Consts.HAG_GOT_DEVICE, "true");
				// 给设备列表设置小助手
				PlayUtil.setHelperToList(myDeviceList);
				broadTag = BROAD_DEVICE_LIST;
				PlayUtil.broadCast(mActivity);
				refreshList();
				break;
			}
			// 从服务器端获取设备成功，但是没有设备
			case DEVICE_NO_DEVICE: {
				MyLog.v(TAG, "nonedata-too");
				refreshList();
				break;
			}
			// 从服务器端获取设备失败
			case DEVICE_GETDATA_FAILED: {
				refreshList();
				break;
			}
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			// ((BaseActivity) mActivity).createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	// 设置三种类型参数分别为String,Integer,String
	class AddDevTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int addRes = -1;
			ArrayList<Device> list = new ArrayList<Device>();// 广播到的设备列表
			list = Device.fromJsonArray(params[0]);
			try {
				for (Device addDev : list) {
					if (null != addDev) {
						if (localFlag) {// 本地添加
							addRes = 0;
						} else {
							addRes = DeviceUtil
									.addDevice(mActivity.statusHashMap
											.get("KEY_USERNAME"), addDev);
							if (0 <= addDev.getChannelList().size()) {
								if (0 == DeviceUtil.addPoint(
										addDev.getFullNo(), addDev
												.getChannelList().size())) {
									addDev.setChannelList(DeviceUtil
											.getDevicePointList(addDev,
													addDev.getFullNo()));
									addRes = 0;
								} else {
									DeviceUtil.unbindDevice(
											mActivity.statusHashMap
													.get(Consts.KEY_USERNAME),
											addDev.getFullNo());
									addRes = -1;
								}
							}
						}
					}
					if (0 == addRes) {
						myDeviceList.add(addDev);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return addRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。

			((BaseActivity) mActivity).dismissDialog();
			if (0 == result) {
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				mActivity.showTextToast(R.string.add_device_succ);
			} else {
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				mActivity.showTextToast(R.string.add_device_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

}
