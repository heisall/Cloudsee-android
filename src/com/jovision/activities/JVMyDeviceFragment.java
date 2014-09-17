package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
import com.jovision.Jni;
import com.jovision.adapters.MyDeviceListAdapter;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.newbean.BeanUtil;
import com.jovision.newbean.Device;
import com.jovision.thread.GetDeviceListThread;
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
		mActivity = getActivity();
		mParent = getView();

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

		myDLAdapter = new MyDeviceListAdapter(mActivity);
		myDeviceListView = (ListView) mParent
				.findViewById(R.id.device_listview);

		myDeviceListView.addHeaderView(adView);
		rightBtn.setOnClickListener(myOnClickListener);

		((BaseActivity) mActivity).createDialog("");
		new GetDeviceListThread(mActivity).start();
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
				Log.v(TAG, "三分钟时间到--发广播");
				Jni.searchLanDevice("", 0, 0, 0, "", 2000, 1);
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
				String devJsonString = BeanUtil.deviceListToString(myDeviceList);
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
		String devJsonString = MySharedPreference.getString(Consts.LOCAL_DEVICE_LIST);
		myDeviceList = BeanUtil.stringToDevList(devJsonString);
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
		MySharedPreference.putString(Consts.LOCAL_DEVICE_LIST, devJsonString);
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

	// private OnFragmentListener myDeviceListener;
	//
	// /** Acitivity要实现这个接口，这样Fragment和Activity就可以共享事件触发的资源了 */
	// public interface OnFragmentListener {
	// public void onFragmentAction(int what, int arg1, int arg2, Object obj);
	// }
	//
	// @Override
	// public void onAttach(Activity activity) {
	//
	// super.onAttach(activity);
	// myDeviceListener = (OnFragmentListener)activity;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public void onTabAction(int what, int arg1, int arg2, Object obj) {
		super.onTabAction(what, arg1, arg2, obj);
		// MyLog.v("TAG",
		// "onTabAction:what="+what+";arg1="+arg1+";arg2="+arg1+";obj="+obj.toString());
		switch (what) {
		case Consts.GET_DEVICE_LIST_FUNCTION: {
			switch (arg1) {
			// 从服务器端获取设备成功
			case JVConst.DEVICE_GETDATA_SUCCESS: {
				myDeviceList = (ArrayList<Device>) obj;
				// 给设备列表设置小助手
				PlayUtil.setHelperToList(myDeviceList);
				Jni.searchLanDevice("", 0, 0, 0, "", 2000, 1);

				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				((BaseActivity) mActivity).dismissDialog();
				break;
			}
			// 从服务器端获取设备成功，但是没有设备
			case JVConst.DEVICE_NO_DEVICE: {
				myDeviceList = (ArrayList<Device>) obj;
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				((BaseActivity) mActivity).dismissDialog();
				break;
			}
			// 从服务器端获取设备失败
			case JVConst.DEVICE_GETDATA_FAILED: {
				myDeviceList = (ArrayList<Device>) obj;
				myDLAdapter.setData(myDeviceList);
				myDeviceListView.setAdapter(myDLAdapter);
				((BaseActivity) mActivity).dismissDialog();
				break;
			}
			}
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
		case JVConst.DEVICE_ITEM_CLICK: {
			((BaseActivity) mActivity).showTextToast(arg1 + "");
			Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
			String devJsonString = BeanUtil.deviceListToString(myDeviceList);
			intentPlay.putExtra("DeviceList", devJsonString);
			intentPlay.putExtra("DeviceIndex", arg1);
			intentPlay.putExtra("ChannelIndex", 0);
			mActivity.startActivity(intentPlay);

			break;
		}
		}

	}

}
