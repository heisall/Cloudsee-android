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
import android.widget.Button;
import android.widget.ListView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.DemoListAdapter;
import com.jovision.bean.DemoBean;
import com.jovision.bean.Device;
import com.jovision.commons.MySharedPreference;
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
	private ArrayList<DemoBean> dataList = new ArrayList<DemoBean>();
	DemoListAdapter demoAdapter;
	private Button btn_right;
	private Button btn_left;
	private boolean isclicked;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (null == demoList || 0 == demoList.size()) {
			demoList.add(new Device("", 9101, "S", 52942216, "admin", "", true,
					1, 0));
			demoList.add(new Device("", 9101, "A", 361, "abc", "123", false, 1,
					1));
			demoList.add(new Device("", 9101, "A", 362, "abc", "123", true, 1,
					2));
			demoList.add(new Device("", 9101, "S", 52942216, "admin", "",
					false, 1, 3));
			dataList.add(new DemoBean("S53530352", "山师附中幼儿园", "小三班-9室",
					"2014年09月"));
			dataList.add(new DemoBean("A361", "山大幼儿园", "幼儿班-1室", "2014年09月"));
			dataList.add(new DemoBean("S26680286", "北京幼儿园", "大班-2室", "2014年09月"));
			dataList.add(new DemoBean("S52942216", "清华附中幼儿园", "小五班-6室",
					"2014年09月"));
			dataList.add(new DemoBean("A361", "山大幼儿园", "幼儿班-1室", "2014年09月"));
			dataList.add(new DemoBean("S53530352", "山师附中幼儿园", "小三班-9室",
					"2014年09月"));
			dataList.add(new DemoBean("A361", "山大幼儿园", "幼儿班-1室", "2014年09月"));
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
		btn_left = (Button) mParent.findViewById(R.id.btn_left);
		btn_right = (Button) mParent.findViewById(R.id.btn_right);
		demoAdapter = new DemoListAdapter(mActivity);
		demoListView = (ListView) mParent.findViewById(R.id.demo_listview);

		btn_left.setOnClickListener(myOnClickListener);
		btn_right.setOnClickListener(myOnClickListener);

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

		demoAdapter.setData(dataList, true);
		demoListView.setAdapter(demoAdapter);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:

				break;
			case R.id.btn_right:
				if (!isclicked) {
					demoAdapter.setData(dataList, false);
					demoAdapter.notifyDataSetChanged();
					demoListView.setAdapter(demoAdapter);
					isclicked = true;
				} else {
					demoAdapter.setData(dataList, true);
					demoAdapter.notifyDataSetChanged();
					demoListView.setAdapter(demoAdapter);
					isclicked = false;
				}
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
			String devJsonString = Device.listToString(demoList);
			Intent intentPlay = new Intent(mActivity, JVPlayActivity.class);
			intentPlay.putExtra("DeviceIndex", arg2);
			intentPlay.putExtra("ChannelIndex", dev.getChannelList().toList()
					.get(0).getChannel());
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
