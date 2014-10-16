package com.jovision.adapters;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.Device;

public class MyDeviceListAdapter extends BaseAdapter {

	public static final int DEVICE_ITEM_CLICK = 0x10;// 设备单击事件--
	public static final int DEVICE_ITEM_LONG_CLICK = 0x11;// 设备长按事件--
	public static final int DEVICE_ITEM_DEL_CLICK = 0x12;// 设备删除按钮事件--
	public static final int DEVICE_EDIT_CLICK = 0x13;// 设备编辑按钮事件--

	private ArrayList<Device> deviceList;
	private BaseFragment mfragment;
	private LayoutInflater inflater;
	private boolean showDelete = false;

	private int[] devResArray = { R.drawable.device_bg_1,
			R.drawable.device_bg_2, R.drawable.device_bg_3,
			R.drawable.device_bg_4 };

	public MyDeviceListAdapter(Context con, BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> dataList) {
		deviceList = dataList;
	}

	// 控制是否显示删除按钮
	public boolean setShowDelete(boolean flag) {
		boolean changeSucc;
		if (showDelete == flag) {
			changeSucc = false;
		} else {
			showDelete = flag;
			changeSucc = true;
		}
		return changeSucc;
	}

	@Override
	public int getCount() {
		int count = 0;
		try {
			int last = deviceList.size() % 2;
			if (0 == last) {
				count = deviceList.size() / 2;
			} else {
				count = deviceList.size() / 2 + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return deviceList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		DeviceHolder deviceHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.mydevice_list_item, null);
			deviceHolder = new DeviceHolder();
			deviceHolder.devLayoutL = (RelativeLayout) convertView
					.findViewById(R.id.dev_layout_l);
			deviceHolder.devNameL = (TextView) convertView
					.findViewById(R.id.dev_name_l);
			deviceHolder.onLineStateL = (TextView) convertView
					.findViewById(R.id.dev_online_l);
			deviceHolder.wifiStateL = (TextView) convertView
					.findViewById(R.id.dev_wifi_l);
			deviceHolder.dev_online_img_l = (ImageView) convertView
					.findViewById(R.id.dev_online_img_l);
			deviceHolder.dev_wifi_img_l = (ImageView) convertView
					.findViewById(R.id.wifi_online_img_l);
			deviceHolder.devImgL = (ImageView) convertView
					.findViewById(R.id.dev_image_l);
			deviceHolder.devDeleteL = (ImageView) convertView
					.findViewById(R.id.mydevice_cancle_l);
			deviceHolder.editDevL = (RelativeLayout) convertView
					.findViewById(R.id.dev_edit_l);
			deviceHolder.editDevIVL = (ImageView) convertView
					.findViewById(R.id.mydevice_edit_l);

			deviceHolder.devLayoutR = (RelativeLayout) convertView
					.findViewById(R.id.dev_layout_r);
			deviceHolder.devNameR = (TextView) convertView
					.findViewById(R.id.dev_name_r);
			deviceHolder.onLineStateR = (TextView) convertView
					.findViewById(R.id.dev_online_r);
			deviceHolder.wifiStateR = (TextView) convertView
					.findViewById(R.id.dev_wifi_r);
			deviceHolder.dev_online_img_r = (ImageView) convertView
					.findViewById(R.id.dev_online_img_r);
			deviceHolder.dev_wifi_img_r = (ImageView) convertView
					.findViewById(R.id.wifi_online_img_r);
			deviceHolder.devImgR = (ImageView) convertView
					.findViewById(R.id.dev_image_r);
			deviceHolder.devDeleteR = (ImageView) convertView
					.findViewById(R.id.mydevice_cancle_r);
			deviceHolder.editDevR = (RelativeLayout) convertView
					.findViewById(R.id.dev_edit_r);
			deviceHolder.editDevIVR = (ImageView) convertView
					.findViewById(R.id.mydevice_edit_r);

			convertView.setTag(deviceHolder);
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}
		deviceHolder.devNameL.setText(deviceList.get(position * 2).getFullNo());
		if (Boolean
				.valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
						.get(Consts.LOCAL_LOGIN))) {
			deviceHolder.onLineStateL.setVisibility(View.GONE);
			deviceHolder.wifiStateL.setVisibility(View.GONE);
			deviceHolder.dev_online_img_l.setVisibility(View.GONE);
			deviceHolder.dev_wifi_img_l.setVisibility(View.GONE);
		} else {
			if (deviceList.get(position * 2).getHasWifi() == 1) {
				deviceHolder.dev_wifi_img_l
						.setImageResource(R.drawable.wifionline);
				deviceHolder.wifiStateL.setTextColor(0xffE8793F);
			} else {
				deviceHolder.dev_wifi_img_l
						.setImageResource(R.drawable.wifioffline);
				deviceHolder.wifiStateL.setTextColor(0xffB4B3B3);
			}

			if (deviceList.get(position * 2).getOnlineState() == 1) {
				deviceHolder.onLineStateL.setText(R.string.str_device_online);
				deviceHolder.onLineStateL.setTextColor(0xffE8793F);
				deviceHolder.dev_online_img_l
						.setImageResource(R.drawable.deviceonline);
			} else {
				deviceHolder.onLineStateL.setText(R.string.str_device_offline);
				deviceHolder.onLineStateL.setTextColor(0xffB4B3B3);
				deviceHolder.dev_online_img_l
						.setImageResource(R.drawable.deviceoffline);
			}
		}
		int lastL = (position * 2) % 4;
		int lastR = (position * 2 + 1) % 4;
		// 按规律设置背景色
		if (0 == lastL || 2 == lastL) {
			deviceHolder.devLayoutL.setBackgroundResource(devResArray[lastL]);
		}
		if (1 == lastR || 3 == lastR) {
			deviceHolder.devLayoutR.setBackgroundResource(devResArray[lastR]);
		}

		// 控制删除按钮显示隐藏
		if (showDelete) {
			deviceHolder.devDeleteL.setVisibility(View.VISIBLE);
			deviceHolder.devDeleteR.setVisibility(View.VISIBLE);
			deviceHolder.editDevL.setVisibility(View.VISIBLE);
			deviceHolder.editDevR.setVisibility(View.VISIBLE);
		} else {
			deviceHolder.devDeleteL.setVisibility(View.GONE);
			deviceHolder.devDeleteR.setVisibility(View.GONE);
			deviceHolder.editDevL.setVisibility(View.GONE);
			deviceHolder.editDevR.setVisibility(View.GONE);
		}

		if (position * 2 + 1 < deviceList.size()) {
			deviceHolder.devLayoutR.setVisibility(View.VISIBLE);
			deviceHolder.devNameR.setText(deviceList.get(position * 2 + 1)
					.getFullNo());
			if (Boolean
					.valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
							.get(Consts.LOCAL_LOGIN))) {
				deviceHolder.onLineStateR.setVisibility(View.GONE);
				deviceHolder.wifiStateR.setVisibility(View.GONE);
				deviceHolder.dev_online_img_r.setVisibility(View.GONE);
				deviceHolder.dev_wifi_img_r.setVisibility(View.GONE);
			} else {
				if (deviceList.get(position * 2 + 1).getHasWifi() == 1) {
					deviceHolder.dev_wifi_img_r
							.setImageResource(R.drawable.wifionline);
					deviceHolder.wifiStateR.setTextColor(0xffE8793F);
				} else {
					deviceHolder.dev_wifi_img_r
							.setImageResource(R.drawable.wifioffline);
					deviceHolder.wifiStateR.setTextColor(0xffB4B3B3);
				}
				if (deviceList.get(position * 2 + 1).getOnlineState() == 1) {
					deviceHolder.onLineStateR
							.setText(R.string.str_device_online);
					deviceHolder.onLineStateR.setTextColor(0xffE8793F);
					deviceHolder.dev_online_img_r
							.setImageResource(R.drawable.deviceonline);
				} else {
					deviceHolder.onLineStateR
							.setText(R.string.str_device_offline);
					deviceHolder.onLineStateR.setTextColor(0xffB4B3B3);
					deviceHolder.dev_online_img_r
							.setImageResource(R.drawable.deviceoffline);
				}
			}
		} else {
			deviceHolder.devLayoutR.setVisibility(View.GONE);
		}

		// 左侧按钮事件
		deviceHolder.devLayoutL.setOnClickListener(new DevOnClickListener(1, 1,
				position));
		deviceHolder.devDeleteL.setOnClickListener(new DevOnClickListener(1, 3,
				position));
		deviceHolder.devLayoutL
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View arg0) {
						mfragment.onNotify(DEVICE_ITEM_LONG_CLICK, position, 0,
								null);
						return false;
					}
				});
		deviceHolder.editDevIVL.setOnClickListener(new DevOnClickListener(1, 4,
				position));
		// 右侧按钮事件
		deviceHolder.devLayoutR.setOnClickListener(new DevOnClickListener(2, 1,
				position));
		deviceHolder.devDeleteR.setOnClickListener(new DevOnClickListener(2, 3,
				position));
		deviceHolder.devLayoutR
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View arg0) {
						mfragment.onNotify(DEVICE_ITEM_LONG_CLICK, position, 0,
								null);
						return false;
					}
				});
		deviceHolder.editDevIVR.setOnClickListener(new DevOnClickListener(2, 4,
				position));
		return convertView;
	}

	// 设备单击事件
	class DevOnClickListener implements OnClickListener {
		private int tag = 0;// 左右标志 1：左 2：右
		private int operate = 1;// 1：点击播放 2：点击查看设备通道 3.点击删除设备 4.编辑设备
		private int line = 0;// 行号
		private int position = 0;// 列表中的位置

		public DevOnClickListener(int leftOrRight, int method, int linePos) {
			tag = leftOrRight;
			operate = method;
			line = linePos;
			if (1 == tag) {
				position = line * 2;
			} else if (2 == tag) {
				position = line * 2 + 1;
			}

		}

		@Override
		public void onClick(View arg0) {
			if (1 == operate || 2 == operate) {
				mfragment.onNotify(DEVICE_ITEM_CLICK, position, 0, null);
			} else if (3 == operate) {
				dialog(position);
			} else if (4 == operate) {
				mfragment.onNotify(DEVICE_EDIT_CLICK, position, 0, null);
			}
		}

	}

	class DeviceHolder {
		RelativeLayout devLayoutL;
		TextView devNameL;
		TextView onLineStateL;
		TextView wifiStateL;
		ImageView devImgL;
		ImageView devDeleteL;
		RelativeLayout editDevL;
		ImageView editDevIVL;

		RelativeLayout devLayoutR;
		TextView devNameR;
		TextView onLineStateR;
		TextView wifiStateR;
		ImageView devImgR;
		ImageView devDeleteR;
		RelativeLayout editDevR;
		ImageView editDevIVR;

		ImageView dev_online_img_l;
		ImageView dev_wifi_img_l;
		ImageView dev_online_img_r;
		ImageView dev_wifi_img_r;
	}

	protected void dialog(final int position) {
		AlertDialog.Builder builder = new Builder(mfragment.getActivity());
		builder.setMessage("确认删除该设备吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mfragment.onNotify(DEVICE_ITEM_DEL_CLICK, position, 0, null);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
