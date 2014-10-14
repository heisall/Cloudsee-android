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
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.JVChannelListActivity;
import com.jovision.bean.ChannellistBean;

public class ChannelListAdapter extends BaseAdapter {

	private JVChannelListActivity activity;
	private LayoutInflater inflater;
	private ArrayList<ChannellistBean> dataList;

	public ChannelListAdapter(JVChannelListActivity activitys) {
		activity = activitys;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<ChannellistBean> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final DeviceHolder Holder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.channellist_item_layout,
					null);
			Holder = new DeviceHolder();

			Holder.channel_list_text = (TextView) convertView
					.findViewById(R.id.channel_item_text);

			Holder.channel_list_img = (ImageView) convertView
					.findViewById(R.id.channel_item_img);

			Holder.channel_list_edit = (EditText) convertView
					.findViewById(R.id.channel_item_edit);

			Holder.channellist_pull = (LinearLayout) convertView
					.findViewById(R.id.channellist_pull);
			Holder.item_img = (ImageView) convertView
					.findViewById(R.id.item_img);

			convertView.setTag(Holder);
		} else {
			Holder = (DeviceHolder) convertView.getTag();
		}
		Holder.channel_list_text.setText(dataList.get(position)
				.getChannelName());
		Holder.channel_list_edit.setText(dataList.get(position)
				.getChannelName());
		Holder.channel_list_img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int i = 0; i < dataList.size(); i++) {
					if (position == i) {
						if (dataList.get(i).isIspull()) {
							dataList.get(i).setIspull(false);
						} else {
							dataList.get(i).setIspull(true);
						}
					} else {
						dataList.get(i).setIspull(false);

					}
					notifyDataSetChanged();
				}
			}
		});
		if (!dataList.get(position).isIspull()) {
			Holder.channellist_pull.setVisibility(View.GONE);
			Holder.channel_list_img
					.setImageResource(R.drawable.devicemanage_edit_icon);
			Holder.item_img
					.setImageResource(R.drawable.devicemanage_normal_icon);
		} else {
			Holder.channellist_pull.setVisibility(View.VISIBLE);
			Holder.channel_list_edit.setFocusable(true);
			Holder.channel_list_edit.setFocusableInTouchMode(true);
			Holder.channel_list_edit.requestFocus();
			Holder.channel_list_img
					.setImageResource(R.drawable.devicemanage_sure_icon);
			Holder.item_img
					.setImageResource(R.drawable.devicemanage_selected_icon);
		}
		notifyDataSetChanged();
		return convertView;
	}

	class DeviceHolder {
		private TextView channel_list_text;
		private ImageView channel_list_img;
		private EditText channel_list_edit;
		private LinearLayout channellist_pull;
		private ImageView item_img;
	}
}
