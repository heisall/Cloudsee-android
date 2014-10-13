package com.jovision.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.DemoListAdapter;
import com.jovision.bean.Device;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.PlayUtil;
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
			demoList.add(new Device("", 9101, "A", 365, "abc", "123", true, 1,
					0));
			demoList.add(new Device("", 9101, "A", 362, "abc", "123", true, 4,
					1));
		}

		View view = inflater.inflate(R.layout.fragment_demo, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();
		currentMenu.setText(R.string.demo);
		rightBtn.setVisibility(View.GONE);
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

		demoAdapter.setData(demoList, true);
		demoListView.setAdapter(demoAdapter);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_right:
				break;
			default:
				break;
			}
		}
	};

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Device dev = demoList.get(arg2);
			// mActivity.showTextToast("arg2="
			// + dev.getChannelList().toList().get(0).getChannel()
			// + ";dev=" + dev.getFullNo());
			PlayUtil.prepareConnect(demoList, arg2);
			String devJsonString = Device.listToString(demoList);
			Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
			intentPlay.putExtra("DeviceIndex", arg2);
			intentPlay.putExtra("ChannelofChannel", dev.getChannelList()
					.toList().get(0).getChannel());
			// intentPlay.putExtra("DevJsonString", devJsonString);
			intentPlay.putExtra("PlayFlag", Consts.PLAY_DEMO);
			MySharedPreference.putString(Consts.KEY_PLAY_DEMO, devJsonString);

			mActivity.startActivity(intentPlay);

		}

	};

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}
}
