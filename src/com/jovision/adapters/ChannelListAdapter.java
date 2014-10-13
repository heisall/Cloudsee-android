package com.jovision.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.ChannellistBean;
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;

public class ChannelListAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<ChannellistBean> dataList;
	private Boolean localFlag;
	private ArrayList<Device> manageDeviceList;

	public ChannelListAdapter(Activity activitys) {
		activity = activitys;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		manageDeviceList = CacheUtil.getDevList();
	}

	public void setData(ArrayList<ChannellistBean> dataList, Boolean localFlag) {
		this.dataList = dataList;
		this.localFlag = localFlag;
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
		DeviceHolder Holder;
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
			Holder.item_img = (ImageView)convertView.findViewById(R.id.item_img);
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
						notifyDataSetChanged();
					} else {
						dataList.get(i).setIspull(false);
						notifyDataSetChanged();
					}
				}

			}
		});
		if (!dataList.get(position).isIspull()) {
			Holder.channellist_pull.setVisibility(View.GONE);
			Holder.channel_list_img
			.setImageResource(R.drawable.devicemanage_edit_icon);
			Holder.item_img.setImageResource(R.drawable.devicemanage_normal_icon);
			ModifyDevTask task = new ModifyDevTask();
			String[] strParams = new String[3];
			strParams[0] = dataList.get(position).getCloudnum();
			strParams[1] = dataList.get(position).getChannelnum() + "";
			strParams[2] = Holder.channel_list_edit.getText().toString();
			task.execute(strParams);
		} else {
			Holder.channellist_pull.setVisibility(View.VISIBLE);
			Holder.channel_list_img.setImageResource(R.drawable.devicemanage_selected_icon);
			Holder.item_img.setImageResource(R.drawable.devicemanage_sure_icon);
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

	// 保存更改设备信息线程
	class ModifyDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int delRes = -1;
			try {
				if (localFlag) {// 本地保存修改信息
					delRes = 0;
				} else {
					int num = Integer.valueOf(params[1]);
					delRes = DeviceUtil.modifyPointName(params[0], num,
							params[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return delRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			CacheUtil.saveDevList(manageDeviceList);
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}
}
