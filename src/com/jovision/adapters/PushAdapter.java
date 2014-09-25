package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovision.bean.PushInfo;

public class PushAdapter extends BaseAdapter {

	public ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();
	public Context mContext = null;
	public LayoutInflater inflater;
	public int wifiIndex = -1;

	public PushAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<PushInfo> list) {
		pushList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (null != pushList && 0 != pushList.size()) {
			size = pushList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pushList.get(position);
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

		wifiHolder.wifiName.setText(pushList.get(position).strAlert);

		wifiHolder.wifiImg.setVisibility(View.GONE);
		wifiHolder.wifiDetail.setVisibility(View.GONE);

		return convertView;
	}

	class WifiHolder {
		TextView wifiName;
		ImageView wifiImg;
		ImageView wifiDetail;
	}
}
