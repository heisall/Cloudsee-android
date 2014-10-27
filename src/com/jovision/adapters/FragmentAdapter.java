package com.jovision.adapters;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.MoreFragmentBean;
import com.jovision.commons.MySharedPreference;

public class FragmentAdapter extends BaseAdapter {
	private BaseFragment mfragment;
	private List<MoreFragmentBean> dataList;
	private boolean localFlag;

	public FragmentAdapter(BaseFragment mfragment,
			ArrayList<MoreFragmentBean> dataList) {
		this.mfragment = mfragment;
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
			convertView = LayoutInflater.from(mfragment.getActivity()).inflate(
					R.layout.fragment_more_item, null);
			holder = new ViewHolder();
			holder.more_relative = (RelativeLayout) convertView
					.findViewById(R.id.more_relative);
			holder.item_img = (ImageView) convertView
					.findViewById(R.id.item_img);
			holder.item_next = (ImageView) convertView
					.findViewById(R.id.item_next);
			holder.name = (TextView) convertView.findViewById(R.id.item_name);
			holder.item_version = (TextView) convertView
					.findViewById(R.id.item_version);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		localFlag = Boolean
				.valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
						.get(Consts.LOCAL_LOGIN));
		holder.item_img.setBackgroundResource(dataList.get(position)
				.getItem_img());
		holder.name.setText(dataList.get(position).getName());
		if (position == 1 && localFlag) {
			holder.more_relative.setVisibility(View.VISIBLE);
			holder.more_relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
		}
		if (position == 5) {
			holder.item_next.setVisibility(View.GONE);
			holder.item_version.setVisibility(View.VISIBLE);
			holder.item_version.setText(mfragment.getActivity().getResources()
					.getString(R.string.str_current_version));
		}
		if (position > -1 && position < 3) {
			holder.item_next
					.setBackgroundResource(R.drawable.morefragment_normal_icon);
			switch (position) {
			case 0:
				if (MySharedPreference.getBoolean("HELP")) {
					holder.item_next
							.setBackgroundResource(R.drawable.morefragment_selector_icon);
				}
				break;
			case 1:
				if (!localFlag) {
					if (MySharedPreference.getBoolean("AlarmSwitch")) {
						holder.item_next
								.setBackgroundResource(R.drawable.morefragment_selector_icon);
					}
				} else {
					holder.item_next
							.setBackgroundResource(R.drawable.morefragment_normal_icon);
				}
				break;
			case 2:
				if (MySharedPreference.getBoolean("PlayDeviceMode")) {
					holder.item_next
							.setBackgroundResource(R.drawable.morefragment_selector_icon);

				}
				break;
			default:
				break;
			}
		}
		return convertView;
	}

	private static class ViewHolder {
		private ImageView item_img;
		private TextView name;
		private ImageView item_next;
		private TextView item_version;
		private RelativeLayout more_relative;
	}
}
