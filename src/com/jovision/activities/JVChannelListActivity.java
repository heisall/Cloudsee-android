package com.jovision.activities;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.ChannelListAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.ChannellistBean;
import com.jovision.bean.Device;
import com.jovision.commons.MyList;
import com.jovision.utils.CacheUtil;

public class JVChannelListActivity extends BaseActivity {

	private ListView channel_listView;
	private ChannelListAdapter adapter;
	private MyList<Channel> channelList = new MyList<Channel>(1);
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private ArrayList<ChannellistBean> channelbeanList = new ArrayList<ChannellistBean>();
	private int deviceIndex;
	private Button btn_left;
	private Button btn_right;
	private TextView currentmenu;
	private boolean localFlag;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub
		deviceList = CacheUtil.getDevList();
	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.channellist_layout);
		localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		currentmenu = (TextView) findViewById(R.id.currentmenu);
		currentmenu.setText(R.string.channal_list);
		channel_listView = (ListView) findViewById(R.id.channel_listView);
		adapter = new ChannelListAdapter(JVChannelListActivity.this);
		deviceIndex = getIntent().getIntExtra("deviceIndex", 0);
		channelList = deviceList.get(deviceIndex).getChannelList();
		initChannelBean();
		adapter.setData(channelbeanList, localFlag, deviceIndex);
		channel_listView.setAdapter(adapter);

		btn_left.setOnClickListener(myOnClickListener);
		btn_right.setOnClickListener(myOnClickListener);
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	private void initChannelBean() {
		for (int i = 0; i < channelList.size(); i++) {
			ChannellistBean bean = new ChannellistBean();
			if (i != channelList.size()) {
				bean.setChannelName(channelList.get(i + 1).getChannelName());
				bean.setCloudnum(channelList.get(i + 1).getParent().getFullNo());
				bean.setChannelnum(channelList.get(i + 1).getChannel());
			}
			bean.setIspull(false);
			channelbeanList.add(bean);
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:

				break;

			default:
				break;
			}
		}
	};
}
