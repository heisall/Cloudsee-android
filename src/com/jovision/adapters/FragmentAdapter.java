package com.jovision.adapters;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.MoreFragmentBean;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;

public class FragmentAdapter extends BaseAdapter {
	private BaseFragment mfragment;
	private List<MoreFragmentBean> dataList;
	private boolean localFlag;
	private ImageView item_img;
	private TextView name;
	private ImageView item_next;
	private TextView item_version;
	private RelativeLayout more_relative;
	private FrameLayout more_item;
	private ImageView divider_img;

	public FragmentAdapter(BaseFragment mfragment,
			ArrayList<MoreFragmentBean> dataList) {
		this.mfragment = mfragment;
		this.dataList = dataList;
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
		convertView = LayoutInflater.from(mfragment.getActivity()).inflate(
				R.layout.fragment_more_item, null);
		more_relative = (RelativeLayout) convertView
				.findViewById(R.id.more_relative);
		more_item = (FrameLayout) convertView.findViewById(R.id.item);
		divider_img = (ImageView) convertView.findViewById(R.id.divider_img);
		item_img = (ImageView) convertView.findViewById(R.id.item_img);
		item_next = (ImageView) convertView.findViewById(R.id.item_next);
		name = (TextView) convertView.findViewById(R.id.item_name);
		item_version = (TextView) convertView.findViewById(R.id.item_version);
		localFlag = Boolean
				.valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
						.get(Consts.LOCAL_LOGIN));
		item_img.setBackgroundResource(dataList.get(position).getItem_img());
		name.setText(dataList.get(position).getName());
		if (position == 1 && localFlag) {
			more_relative.setVisibility(View.VISIBLE);
			more_relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
		}
		if (position == 3) {
			if (!MySharedPreference.getBoolean("LITTLE")) {
				more_item.setVisibility(View.GONE);
				divider_img.setVisibility(View.GONE);
			} else {
				more_item.setVisibility(View.VISIBLE);
				divider_img.setVisibility(View.VISIBLE);
			}
		}
		if (position == 5
				&& "true".equalsIgnoreCase(((BaseActivity) mfragment
						.getActivity()).statusHashMap
						.get(Consts.NEUTRAL_VERSION))) {
			// 中性版本的隐藏注册协议
			more_item.setVisibility(View.GONE);
			divider_img.setVisibility(View.GONE);
		}
		if (position == 7) {
			item_next.setVisibility(View.GONE);
			item_version.setVisibility(View.VISIBLE);
			item_version
					.setText(ConfigUtil.getVersion(mfragment.getActivity()));

			// mfragment.getActivity().getResources()
			// .getString(R.string.str_current_version));
		}
		if (position > -1 && position < 4) {
			item_next
					.setBackgroundResource(R.drawable.morefragment_normal_icon);
			switch (position) {
			case 0:
				if (MySharedPreference.getBoolean("HELP")) {
					item_next
							.setBackgroundResource(R.drawable.morefragment_selector_icon);
				}
				break;
			case 1:
				if (!localFlag) {
					if (MySharedPreference.getBoolean("AlarmSwitch")) {
						item_next
								.setBackgroundResource(R.drawable.morefragment_selector_icon);
					}
				} else {
					item_next
							.setBackgroundResource(R.drawable.morefragment_normal_icon);
				}
				break;
			case 2:
				if (MySharedPreference.getBoolean("PlayDeviceMode")) {
					item_next
							.setBackgroundResource(R.drawable.morefragment_selector_icon);

				}
				break;
			case 3:
				if (MySharedPreference.getBoolean("LITTLEHELP")) {
					item_next
							.setBackgroundResource(R.drawable.morefragment_selector_icon);
				}
				break;
			default:
				break;
			}
		}
		return convertView;
	}
}
