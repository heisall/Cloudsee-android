package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;

public class StreamAdapter extends BaseAdapter {

	public static final int STREAM_ITEM_CLICK = 0x60;// 码流单击事件--

	private Context context;
	private String[] dataArray;
	private LayoutInflater mInflater;
	public int selectStream = -1;// 选中码流
	public int kinds = 3;// 几种切换

	/**
	 * 自定义构造方法
	 * 
	 * @param activity
	 * @param handler
	 * @param list
	 */
	public StreamAdapter(Context c) {
		context = c;
		mInflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String[] array) {
		this.dataArray = array;
	}

	public void setChangeCounts(int counts) {
		this.kinds = counts;
	}

	@Override
	public int getCount() {
		return dataArray.length;
	}

	@Override
	public Object getItem(int position) {
		return dataArray[position];
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

		holder.screenNum.setBackgroundDrawable(context.getResources()
				.getDrawable(R.drawable.transparent_icon));
		holder.screenNum.setTextColor(context.getResources().getColor(
				R.color.white));

		// 选择的码流等于当前码流
		if (selectStream == position) {
			if (0 == position) {
				holder.screenNum.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.stream_selected));
			} else if (position == (dataArray.length - 1)) {
				holder.screenNum.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.stream_selected));
			} else {
				holder.screenNum.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.stream_selected));
			}
		}

		holder.screenNum.setText(dataArray[position]);

		holder.screenNum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((BaseActivity) context).onNotify(STREAM_ITEM_CLICK, position,
						0, null);
			}
		});

		if (0 == position && 2 == kinds) {// 老的IPC连接手机码流不让切换成高清
			holder.screenNum.setVisibility(View.GONE);
		} else {
			holder.screenNum.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		Button screenNum;
	}
}
