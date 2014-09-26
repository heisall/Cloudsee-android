package com.jovision.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.DemoListAdapter;
import com.jovision.bean.BeanUtil;
import com.jovision.bean.Device;
import com.jovision.views.RefreshableView;
import com.jovision.views.RefreshableView.PullToRefreshListener;

/**
 * 演示点
 */
public class JVDemoFragment extends BaseFragment {

	private String TAG = "JVDemoFragment";

	private RefreshableView refreshableView;
	/** 设备列表 */
	private ListView demoListView;
	private ArrayList<Device> demoList = new ArrayList<Device>();
	DemoListAdapter demoAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (null == demoList || 0 == demoList.size()) {
			demoList.add(new Device("", 9101, "S", 53530352, "admin", "123",
					true, 1, 0));
			demoList.add(new Device("", 9101, "A", 361, "abc", "123", false, 1,
					1));
			demoList.add(new Device("", 9101, "S", 26680286, "admin", "123",
					true, 1, 2));
			demoList.add(new Device("", 9101, "S", 52942216, "admin", "123",
					false, 1, 3));
		}
		View view = inflater.inflate(R.layout.fragment_demo, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();
		refreshableView = (RefreshableView) mParent
				.findViewById(R.id.demo_refreshable_view);
		demoAdapter = new DemoListAdapter(mActivity);
		demoListView = (ListView) mParent.findViewById(R.id.demo_listview);
		demoListView.setOnItemClickListener(myOnItemClickListener);
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

			@Override
			public void onLayoutTrue() {
				refreshableView.finishRefreshing();
			}

		}, 0);

		demoAdapter.setData(demoList);
		demoListView.setAdapter(demoAdapter);
	}

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			String devJsonString = BeanUtil.deviceListToString(demoList);
			// MySharedPreference.putString(Consts.DEVICE_LIST, devJsonString);
			Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
			intentPlay.putExtra("DeviceList", devJsonString);
			intentPlay.putExtra("DeviceIndex", arg2);
			intentPlay.putExtra("ChannelIndex", 0);
			mActivity.startActivity(intentPlay);

		}

	};

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		// case Consts.TAB_ONRESUME: {// activity起来后开始加载设备
		//
		// break;
		// }
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}
}
