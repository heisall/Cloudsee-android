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
import com.jovision.bean.ChannellistBean;
import com.jovision.utils.ConfigUtil;

public class ChannelListAdapter extends BaseAdapter {

	private BaseActivity activity;
	private LayoutInflater inflater;
	private ArrayList<ChannellistBean> dataList;

	public ChannelListAdapter(BaseActivity activitys) {
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
			Holder.parent_relative = (RelativeLayout) convertView
					.findViewById(R.id.parent_relative);
			convertView.setTag(Holder);
		} else {
			Holder = (DeviceHolder) convertView.getTag();
		}

		Holder.parent_relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int i = 0; i < dataList.size(); i++) {
					if (position == i) {
						if (dataList.get(i).isIspull()) {
							if ("".equalsIgnoreCase(Holder.channel_list_edit
									.getText().toString())) {
								activity.showTextToast(R.string.str_nikename_notnull);
							} else if (!ConfigUtil
									.checkNickName(Holder.channel_list_edit
											.getText().toString())) {
								activity.showTextToast(R.string.login_str_nike_name_order);
							} else {
								activity.onNotify(1, position, 0,
										Holder.channel_list_edit.getText()
												.toString());
								dataList.get(position).setChannelName(
										Holder.channel_list_edit.getText()
												.toString());
								dataList.get(i).setIspull(false);
							}
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
		Holder.parent_relative
				.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						activity.onNotify(2, position, 0,
								Holder.channel_list_edit.getText().toString());
						return false;
					}
				});
		if (!dataList.get(position).isIspull()) {
			Holder.channellist_pull.setVisibility(View.GONE);
			Holder.channel_list_img
					.setImageResource(R.drawable.devicemanage_edit_icon);
			Holder.item_img
					.setImageResource(R.drawable.devicemanage_normal_icon);
			Holder.channel_list_text.setTextColor(activity.getResources()
					.getColor(R.color.more_fragment_color2));
		} else {
			Holder.channellist_pull.setVisibility(View.VISIBLE);
			Holder.channel_list_edit.setFocusable(true);
			Holder.channel_list_edit.setFocusableInTouchMode(true);
			Holder.channel_list_edit.requestFocus();
			Holder.channel_list_img
					.setImageResource(R.drawable.devicemanage_sure_icon);
			Holder.channel_list_text.setTextColor(activity.getResources()
					.getColor(R.color.dialogchannaltext));
			Holder.item_img
					.setImageResource(R.drawable.devicemanage_selected_icon);
		}
		Holder.channel_list_text.setText(dataList.get(position)
				.getChannelName());
		Holder.channel_list_edit.setText(dataList.get(position)
				.getChannelName());

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
