package com.jovision.adapters;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.jovision.MainApplication;
import com.jovision.commons.JVConst;

public class ScreenAdapter extends BaseAdapter {
	Context mContext;
	List<Integer> list;
	private LayoutInflater mInflater;
	public int selectIndex = 0;

	/**
	 * 自定义构造方法
	 * 
	 * @param activity
	 * @param handler
	 * @param list
	 */
	public ScreenAdapter(Context c, List<Integer> aList) {
		mContext = c;
		mInflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = aList;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 下拉项布局
			convertView = mInflater.inflate(R.layout.screen_item, null);
			holder.screenNum = (Button) convertView
					.findViewById(R.id.screennum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (0 == position) {
			holder.screenNum.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.screen_selector_top));
		} else if (position == (list.size() - 1)) {
			holder.screenNum.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.screen_selector_bottom));
		} else {
			holder.screenNum.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.screen_selector_center));
		}
		holder.screenNum.setTextColor(mContext.getResources().getColor(
				R.color.black));

		// 选择的分屏等于当前分屏
		if (selectIndex == position) {
			if (0 == position) {
				holder.screenNum.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.screen_selector_top_2));
				holder.screenNum.setTextColor(mContext.getResources().getColor(
						R.color.white));
			} else if (position == (list.size() - 1)) {
				holder.screenNum.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.screen_selector_bottom_2));
				holder.screenNum.setTextColor(mContext.getResources().getColor(
						R.color.white));
			} else {
				holder.screenNum.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.screen_selector_center_2));
				holder.screenNum.setTextColor(mContext.getResources().getColor(
						R.color.white));
			}
		}

		holder.screenNum.setText(list.get(position)
				+ mContext.getResources().getString(R.string.str_screen));

		holder.screenNum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				((MainApplication) mContext.getApplicationContext()).onNotify(
						JVConst.WHAT_SELECT_SCREEN, position, 0, null);

			}
		});

		return convertView;
	}

	class ViewHolder {
		Button screenNum;
	}
}
