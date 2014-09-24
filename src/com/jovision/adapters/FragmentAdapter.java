package com.jovision.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.MoreFragmentBean;

public class FragmentAdapter extends BaseAdapter {
	private Context activity;
	private List<MoreFragmentBean> dataList;

	public FragmentAdapter(Context activity,
			ArrayList<MoreFragmentBean> dataList) {
		this.activity = activity;
		this.dataList = dataList;
	}

	public void updateListView(List<MoreFragmentBean> list) {
		this.dataList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.fragment_more_item, null);
			holder = new ViewHolder();
			holder.item_img = (ImageView) convertView
					.findViewById(R.id.item_img);
			holder.item_new = (ImageView) convertView
					.findViewById(R.id.item_new);
			holder.name = (TextView) convertView.findViewById(R.id.item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_img.setBackgroundResource(dataList.get(position)
				.getItem_img());
		holder.name.setText(dataList.get(position).getName());
		if (dataList.get(position).isIsnew()) {
			if (position == 2) {
				holder.item_new.setBackgroundResource(R.drawable.more_red_new);
			}
			holder.item_new.setVisibility(View.VISIBLE);
		} else {
			holder.item_new.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class ViewHolder {
		private ImageView item_img;
		private TextView name;
		private ImageView item_new;
	}
}
