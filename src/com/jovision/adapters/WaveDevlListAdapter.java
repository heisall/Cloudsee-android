package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;
import com.jovision.bean.Device;

public class WaveDevlListAdapter extends BaseAdapter {

	private BaseActivity activity;
	private LayoutInflater inflater;
	private ArrayList<Device> devList;

	public WaveDevlListAdapter(BaseActivity activitys) {
		activity = activitys;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> dataList) {
		this.devList = dataList;
	}

	@Override
	public int getCount() {
		return devList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return devList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final DeviceHolder devHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.channellist_item_layout,
					null);
			devHolder = new DeviceHolder();

			devHolder.channel_list_text = (TextView) convertView
					.findViewById(R.id.channel_item_text);

			devHolder.channel_list_img = (ImageView) convertView
					.findViewById(R.id.channel_item_img);

			devHolder.channel_list_edit = (EditText) convertView
					.findViewById(R.id.channel_item_edit);

			devHolder.channellist_pull = (LinearLayout) convertView
					.findViewById(R.id.channellist_pull);
			devHolder.item_img = (ImageView) convertView
					.findViewById(R.id.item_img);
			devHolder.parent_relative = (RelativeLayout) convertView
					.findViewById(R.id.parent_relative);
			convertView.setTag(devHolder);
		} else {
			devHolder = (DeviceHolder) convertView.getTag();
		}

		devHolder.channel_list_text.setText(devList.get(position).getFullNo());
		devHolder.channellist_pull.setVisibility(View.GONE);
		devHolder.item_img.setImageDrawable(activity.getResources()
				.getDrawable(R.drawable.has_added_icon));
		devHolder.channel_list_img.setImageDrawable(activity.getResources()
				.getDrawable(R.drawable.has_added));

		return convertView;
	}

	class DeviceHolder {
		private RelativeLayout parent_relative;
		private TextView channel_list_text;
		private ImageView channel_list_img;
		private EditText channel_list_edit;
		private LinearLayout channellist_pull;
		private ImageView item_img;
	}
}
