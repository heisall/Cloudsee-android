package com.jovision.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.DataSourceAdapter;
import com.jovision.bean.Device;
import com.jovision.commons.BaseApp;

@SuppressLint("InlinedApi")
public class JVDataSourceActivity extends BaseActivity {

	private Button back;
	private TextView currentMenu;
	private ListView deviceListView;
	private DataSourceAdapter dataSourceAdapter;
	private int deviceIndex = 0;// 当前设备index
	private int pointIndex = 0;// 当前通道index
	private boolean videoMode = false;// 默认单设备模式

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.datasource_layout);
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_data_source);
		deviceListView = (ListView) findViewById(R.id.dsdevicelistview);
		dataSourceAdapter = new DataSourceAdapter(JVDataSourceActivity.this);
		back.setOnClickListener(onClickListener);

		// 获取传过来的设备和通道index
		Intent intent = getIntent();
		if (null != intent) {
			videoMode = intent.getBooleanExtra("VideoMode", false);
			deviceIndex = intent.getIntExtra("DeviceIndex", 0);
			pointIndex = intent.getIntExtra("PointIndex", 0);
		}

		// 多设备模式
		if (videoMode) {
			if (null != BaseApp.onLineDeviceList
					&& deviceIndex < BaseApp.onLineDeviceList.size()) {
				dataSourceAdapter.setData(BaseApp.onLineDeviceList,
						deviceIndex, pointIndex);
				deviceListView.setAdapter(dataSourceAdapter);
				deviceListView.setSelection(pointIndex);
			}
		} else {// 单设备模式
			if (null != BaseApp.onLineDeviceList
					&& deviceIndex < BaseApp.onLineDeviceList.size()) {
				ArrayList<Device> singleDeviceList = new ArrayList<Device>();
				singleDeviceList.add(BaseApp.onLineDeviceList.get(deviceIndex));

				dataSourceAdapter.setData(singleDeviceList, 0, pointIndex);
				deviceListView.setAdapter(dataSourceAdapter);
				deviceListView.setSelection(pointIndex);
			}
		}
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				JVDataSourceActivity.this.finish();
				break;
			}
		}
	};

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
