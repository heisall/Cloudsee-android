package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.Wifi;

public class WifiAdapter extends BaseAdapter {

	public ArrayList<Wifi> wifiList = new ArrayList<Wifi>();
	public ArrayList<ScanResult> wifiConfigList = new ArrayList<ScanResult>();
	public Context mContext = null;
	public LayoutInflater inflater;
	public int wifiIndex = -1;
	public int tag = -1;
	public boolean showDetailIcon = false;

	public WifiAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData1(ArrayList<Wifi> list, int arg0, boolean flag) {
		wifiList = list;
		tag = arg0;
		showDetailIcon = flag;
	}

	public void setData2(ArrayList<ScanResult> list, int arg0) {
		wifiConfigList = list;
		tag = arg0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (tag == 1) {
			if (null != wifiList && 0 != wifiList.size()) {
				size = wifiList.size();
			}
		} else {
			if (null != wifiConfigList && 0 != wifiConfigList.size()) {
				size = wifiConfigList.size();
			}
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Object obj = null;
		if (tag == 1) {
			if (null != wifiList && 0 != wifiList.size()) {
				obj = wifiList.get(position);
			}
		} else {
			if (null != wifiConfigList && 0 != wifiConfigList.size()) {
				obj = wifiConfigList.get(position);
			}
		}
		return obj;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		WifiHolder wifiHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.wifi_item, null);
			wifiHolder = new WifiHolder();
			wifiHolder.wifiName = (TextView) convertView
					.findViewById(R.id.videodate);
			wifiHolder.wifiImg = (ImageView) convertView
					.findViewById(R.id.wifistate);
			wifiHolder.wifiDetail = (ImageView) convertView
					.findViewById(R.id.wifidetail);
			convertView.setTag(wifiHolder);
		} else {
			wifiHolder = (WifiHolder) convertView.getTag();
		}

		// wifiHolder.wifiImg.setVisibility(View.GONE);
		// wifiHolder.wifiDetail.setVisibility(View.GONE);

		if (1 == tag) {
			wifiHolder.wifiName.setText(wifiList.get(position).wifiUserName);
			wifiHolder.wifiDetail.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.wifi_detail_icon));
			if (wifiIndex == position) {
				wifiHolder.wifiImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.wifi_flag_open_bg));
			} else {
				wifiHolder.wifiImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.wifi_flag_close_bg));
			}

			// 显示详细信息按钮
			if (showDetailIcon) {
				wifiHolder.wifiDetail.setVisibility(View.VISIBLE);
			} else {
				wifiHolder.wifiDetail.setVisibility(View.GONE);
			}

			wifiHolder.wifiDetail
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// Message msg = BaseApp.settingHandler
							// .obtainMessage();
							// msg.arg1 = 0;
							// if (BaseApp.settingMap
							// .get("WIFI_ID")
							// .equalsIgnoreCase(
							// wifiList.get(position).wifiUserName)) {
							// msg.arg1 = 1;
							// }
							// msg.what = JVConst.DEVICE_SETTING_WIFI_DET;
							// BaseApp.settingHandler.sendMessage(msg);
						}

					});
		} else if (2 == tag) {
			wifiHolder.wifiName.setText(wifiConfigList.get(position).SSID);
			wifiHolder.wifiDetail.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.more_feature_right_icon));
			if (wifiIndex == position) {
				wifiHolder.wifiImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.wifi_flag_open_bg));
			} else {
				wifiHolder.wifiImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.wifi_flag_close_bg));
			}

			wifiHolder.wifiDetail.setVisibility(View.VISIBLE);

			convertView.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.ap_wifi_list_bg));

			wifiHolder.wifiDetail
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// Message msg =
							// BaseApp.settingHandler.obtainMessage();
							// msg.arg1 = 0;
							// if(BaseApp.settingMap.get("WIFI_ID").equalsIgnoreCase(wifiList.get(position).wifiUserName)){
							// msg.arg1 = 1;
							// }
							//
							//
							// msg.what = JVConst.DEVICE_SETTING_WIFI_DET;
							// BaseApp.settingHandler.sendMessage(msg);
						}

					});
		}

		return convertView;
	}

	class WifiHolder {
		TextView wifiName;
		ImageView wifiImg;
		ImageView wifiDetail;
	}

}
