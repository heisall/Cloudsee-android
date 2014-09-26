package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.MyDeviceListAdapter;
import com.jovision.bean.BeanUtil;
import com.jovision.bean.Device;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
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

	private RefreshableView refreshableView;
	/** 广告位 */
	private LayoutInflater inflater;
	private View adView;
	private ImageViewPager imageScroll; // 图片容器
	private LinearLayout ovalLayout; // 圆点容器
	private List<View> listViews; // 图片组

	/** 设备列表 */
	private ListView myDeviceListView;
	private ArrayList<Device> myDeviceList = new ArrayList<Device>();
	MyDeviceListAdapter myDLAdapter;

	/** 3分钟广播 */
	private Timer broadTimer;
	private TimerTask broadTimerTask;

	boolean localFlag = false;// 本地登陆标志位

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

		myDeviceList = null;

		localFlag = Boolean.valueOf(mActivity.statusHashMap
				.get(Consts.LOCAL_LOGIN));

		inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		refreshableView = (RefreshableView) mParent
				.findViewById(R.id.refreshable_view);
		adView = inflater.inflate(R.layout.ad_layout, null);

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
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 0);

		broadTimer = new Timer();
		broadTimerTask = new TimerTask() {

			@Override
			public void run() {
				PlayUtil.broadCast(mActivity);
				Log.v(TAG, "三分钟时间到--发广播");
			}

		};

		broadTimer.schedule(broadTimerTask, 3 * 60 * 1000, 3 * 60 * 1000);
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
				Intent addIntent = new Intent();
				addIntent.setClass(mActivity, JVAddDeviceActivity.class);
				String devJsonString = BeanUtil
						.deviceListToString(myDeviceList);
				addIntent.putExtra("DeviceList", devJsonString);
				mActivity.startActivity(addIntent);
				break;

			default:
				break;
			}

		}

	};

	@Override
	public void onResume() {
		String devJsonString = MySharedPreference.getString(Consts.DEVICE_LIST);
		myDeviceList = BeanUtil.stringToDevList(devJsonString);
		myDLAdapter.setData(myDeviceList);
		myDLAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		imageScroll.stopTimer();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		String devJsonString = BeanUtil.deviceListToString(myDeviceList);
		MySharedPreference.putString(Consts.DEVICE_LIST, devJsonString);
		imageScroll.stopTimer();
		super.onPause();
	}

	@Override
	public void onStop() {
		imageScroll.stopTimer();
		super.onStop();
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
					((BaseActivity) mActivity).showTextToast("点击了:"
							+ imageScroll.getCurIndex());
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
		case Consts.TAB_ONRESUME: {// activity起来后开始加载设备
			((BaseActivity) mActivity).createDialog("");
			GetDevTask task = new GetDevTask();
			String[] strParams = new String[3];
			task.execute(strParams);
			break;
		}
		// 广播回调
		case Consts.CALL_LAN_SEARCH: {
			// onTabAction:what=168;arg1=0;arg2=0;obj={"count":1,"curmod":0,"gid":"A","ip":"192.168.21.238","netmod":0,"no":283827713,"port":9101,"timeout":0,"type":59162,"variety":3}
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

			break;
		}
		case Consts.DEVICE_ITEM_CLICK: {// 设备单击事件
			boolean changeRes = myDLAdapter.setShowDelete(false);
			if (changeRes) {
				myDLAdapter.notifyDataSetChanged();
			} else {
				if (1 == myDeviceList.get(arg1).getChannelList().size()) {// 1个通道直接播放
					((BaseActivity) mActivity).showTextToast(arg1 + "");
					Intent intentPlay = new Intent(mActivity,
							JVPlayActivity.class);
					String devJsonString = BeanUtil
							.deviceListToString(myDeviceList);
					intentPlay.putExtra("DeviceList", devJsonString);
					intentPlay.putExtra("DeviceIndex", arg1);
					intentPlay.putExtra("ChannelIndex", 0);
					mActivity.startActivity(intentPlay);
				} else {// 多个通道查看通道列表
					Intent intentPlay = new Intent(mActivity,
							JVChannelsActivity.class);
					String devJsonString = BeanUtil
							.deviceListToString(myDeviceList);
					intentPlay.putExtra("DeviceList", devJsonString);
					intentPlay.putExtra("DeviceIndex", arg1);
					intentPlay.putExtra("ChannelIndex", 0);
					mActivity.startActivity(intentPlay);
				}

			}
			break;
		}
		case Consts.DEVICE_ITEM_LONG_CLICK: {// 设备长按事件
			myDLAdapter.setShowDelete(true);
			myDLAdapter.notifyDataSetChanged();
			break;
		}
		case Consts.DEVICE_ITEM_DEL_CLICK: {// 设备删除事件
			((BaseActivity) mActivity).showTextToast("删除设备--" + arg1);
			DelDevTask task = new DelDevTask();
			String[] strParams = new String[3];
			strParams[0] = String.valueOf(arg1);
			task.execute(strParams);
			break;
		}
		case Consts.DEVICE_EDIT_CLICK: {// 设备编辑事件
			((BaseActivity) mActivity).showTextToast("编辑设备--" + arg1);
		}
		}
	}

	// 删除设备线程
	private class DelDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			boolean localFlag = Boolean
					.valueOf(((BaseActivity) mActivity).statusHashMap
							.get(Consts.LOCAL_LOGIN));
			try {
				int delIndex = Integer.parseInt(params[0]);
				if (localFlag) {// 本地添加
					myDeviceList.remove(delIndex);
					MySharedPreference.putString(Consts.DEVICE_LIST,
							myDeviceList.toString());
					delRes = 0;
				} else {
					delRes = DeviceUtil.unbindDevice(
							((BaseActivity) mActivity).statusHashMap
									.get("KEY_USERNAME"),
							myDeviceList.get(delIndex).getFullNo());
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
				myDLAdapter.notifyDataSetChanged();
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
	private class GetDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int getRes = 0;
			try {
				if (!localFlag) {// 非本地登录，无论是否刷新都执行
					// 获取所有设备列表和通道列表 ,如果设备请求失败，多请求一次
					myDeviceList = DeviceUtil
							.getUserDeviceList(mActivity.statusHashMap
									.get(Consts.KEY_USERNAME));
					if (null == myDeviceList || 0 == myDeviceList.size()) {
						myDeviceList = DeviceUtil
								.getUserDeviceList(mActivity.statusHashMap
										.get(Consts.KEY_USERNAME));
					}
					JVMyDeviceFragment.this.onNotify(
							Consts.GET_DEVICE_LIST_FUNCTION,
							JVAccountConst.REFERSH_DEVICE_LIST_ONLY, 0, null);

					if (null != myDeviceList && 0 != myDeviceList.size()) {
						int size = myDeviceList.size();
						if (0 != size) {
							for (int i = 0; i < myDeviceList.size(); i++) {
								myDeviceList.get(i)
										.setChannelList(
												DeviceUtil.getDevicePointList(
														myDeviceList.get(i),
														myDeviceList.get(i)
																.getFullNo()));
							}
						}
					}

				} else if (localFlag) {// 本地登录且是刷新
					String devJsonString = MySharedPreference
							.getString(Consts.DEVICE_LIST);
					myDeviceList = BeanUtil.stringToDevList(devJsonString);
				}

				if (null != myDeviceList && 0 != myDeviceList.size()) {// 获取设备成功,去广播设备列表
					getRes = Consts.DEVICE_GETDATA_SUCCESS;
					// if (ConfigUtil.is3G(mActivity, false)) {// 3G直接加载设备
					// JVMyDeviceFragment.this.onNotify(
					// Consts.GET_DEVICE_LIST_FUNCTION,
					// Consts.DEVICE_GETDATA_SUCCESS, 0, null);
					// } else {
					// JVMyDeviceFragment.this.onNotify(
					// Consts.GET_DEVICE_LIST_FUNCTION,
					// Consts.DEVICE_GETDATA_SUCCESS, 0, myDeviceList);
					// }

				} else if (null != myDeviceList && 0 == myDeviceList.size()) {// 无数据
					getRes = Consts.DEVICE_NO_DEVICE;
					// MyLog.v(TAG, "nonedata");
					// JVMyDeviceFragment.this.onNotify(
					// Consts.GET_DEVICE_LIST_FUNCTION,
					// Consts.DEVICE_NO_DEVICE, 0, myDeviceList);
				} else {// 获取设备失败
					getRes = Consts.DEVICE_GETDATA_FAILED;
					// JVMyDeviceFragment.this.onNotify(
					// Consts.GET_DEVICE_LIST_FUNCTION,
					// Consts.DEVICE_GETDATA_FAILED, 0, myDeviceList);
				}
			} catch (Exception e) {
				// TODO: handle exception
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
			switch (result) {
			// 从服务器端获取设备成功
			case Consts.DEVICE_GETDATA_SUCCESS: {
				// 给设备列表设置小助手
				PlayUtil.setHelperToList(myDeviceList);
				PlayUtil.broadCast(mActivity);
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				((BaseActivity) mActivity).dismissDialog();
				break;
			}
			// 从服务器端获取设备成功，但是没有设备
			case Consts.DEVICE_NO_DEVICE: {
				MyLog.v(TAG, "nonedata-too");
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				((BaseActivity) mActivity).dismissDialog();
				break;
			}
			// 从服务器端获取设备失败
			case Consts.DEVICE_GETDATA_FAILED: {
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				((BaseActivity) mActivity).dismissDialog();
				break;
			}
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

}
