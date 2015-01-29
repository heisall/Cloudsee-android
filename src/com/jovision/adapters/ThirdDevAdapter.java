package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.bean.Device;
import com.jovision.bean.ThirdAlarmDev;
import com.jovision.commons.MyLog;

//因为删除信息走的回调在Activity中，因此把该Apapter放在Actvity中实现
public class ThirdDevAdapter extends BaseAdapter {

	private Context context_;
	private ArrayList<ThirdAlarmDev> third_dev_list_;
	public LayoutInflater inflater;
	private Device device_;
	private int[] device_types_array = { R.drawable.third_door_default,
			R.drawable.third_bracelet_default,
			R.drawable.third_telecontrol_default,
			R.drawable.third_smoke_default, R.drawable.third_curtain_default,
			R.drawable.third_infrared_default, R.drawable.third_gas_default };

	public ThirdDevAdapter(Context context, ArrayList<ThirdAlarmDev> devList,
			Device device) {
		context_ = context;
		third_dev_list_ = devList;
		device_ = device;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return third_dev_list_.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return third_dev_list_.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setDataList(ArrayList<ThirdAlarmDev> devList) {
		third_dev_list_ = devList;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		try {
			final ItemViewHolder viewHolder;
			final ThirdAlarmDev devItem = third_dev_list_.get(position);
			if (null == convertView) {
				convertView = inflater.inflate(R.layout.third_dev_item, null);
				viewHolder = new ItemViewHolder();
				viewHolder.dev_nick_name = (TextView) convertView
						.findViewById(R.id.third_dev_nick);
				viewHolder.switch_btn = (Button) convertView
						.findViewById(R.id.alarm_switch);
				viewHolder.dev_type_img = (ImageView) convertView
						.findViewById(R.id.dev_type_icon);
				viewHolder.dev_yst = (TextView) convertView
						.findViewById(R.id.third_dev_yst);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ItemViewHolder) convertView.getTag();
			}

			class ListHandler extends Handler {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {

					case 0:
						// viewHolder.switch_btn
						// .setBackgroundResource(R.drawable.sw_button);
						// third_dev_list_.get(msg.arg1).dev_safeguard_flag = 0;
						//
						String strType = "type="
								+ third_dev_list_.get(msg.arg1).dev_type_mark
								+ ";";
						String strGuid = "guid="
								+ third_dev_list_.get(msg.arg1).dev_uid + ";";
						String strNickName = "name="
								+ third_dev_list_.get(msg.arg1).dev_nick_name
								+ ";";
						String strSwitch = "enable=0;";
						String reqData = strType + strGuid + strNickName
								+ strSwitch;
						MyLog.i("Third Dev", "set switch [off] req:" + reqData);
						// JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT,
						// (byte)JVNetConst.JVN_RSP_TEXTDATA,
						// (byte)JVConst.RC_GPIN_SET, reqData.trim());

						// BaseApp.getInstance().onNotify(9009, 0, msg.arg1,
						// reqData.trim());
						((MainApplication) context_.getApplicationContext())
								.onNotify(Consts.RC_GPIN_SET_SWITCH, 0,
										msg.arg1, reqData.trim());
						break;
					case 1:
						// viewHolder.switch_btn
						// .setBackgroundResource(R.drawable.sw_button_on);
						// third_dev_list_.get(msg.arg1).dev_safeguard_flag = 1;

						String strType1 = "type="
								+ third_dev_list_.get(msg.arg1).dev_type_mark
								+ ";";
						String strGuid1 = "guid="
								+ third_dev_list_.get(msg.arg1).dev_uid + ";";
						String strNickName1 = "name="
								+ third_dev_list_.get(msg.arg1).dev_nick_name
								+ ";";
						String strSwitch1 = "enable=1;";
						String reqData1 = strType1 + strGuid1 + strNickName1
								+ strSwitch1;
						MyLog.i("Third Dev", "set switch [on] req:" + reqData1);
						// JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT,
						// (byte)JVNetConst.JVN_RSP_TEXTDATA,
						// (byte)JVConst.RC_GPIN_SET, reqData1.trim());

						// BaseApp.getInstance().onNotify(9009, 1, msg.arg1,
						// reqData1.trim());
						((MainApplication) context_.getApplicationContext())
								.onNotify(Consts.RC_GPIN_SET_SWITCH, 1,
										msg.arg1, reqData1.trim());
						break;
					default:

						break;
					}
				}
			}
			viewHolder.dev_nick_name.setText(devItem.dev_nick_name);
			viewHolder.dev_yst.setText(devItem.dev_belong_yst);
			if (third_dev_list_.get(position).dev_safeguard_flag == 0) {
				viewHolder.switch_btn
						.setBackgroundResource(R.drawable.morefragment_normal_icon);
			} else {
				viewHolder.switch_btn
						.setBackgroundResource(R.drawable.morefragment_selector_icon);
			}
			viewHolder.dev_type_img
					.setBackgroundResource(device_types_array[devItem.dev_type_mark - 1]);
			// if (devItem.dev_type_mark == 1) {// 门磁设备
			// viewHolder.dev_type_img
			// .setBackgroundResource(R.drawable.third_door_default);
			// } else if (devItem.dev_type_mark == 2) {// 手环设备
			// viewHolder.dev_type_img
			// .setBackgroundResource(R.drawable.third_bracelet_default);
			// } else if (devItem.dev_type_mark == 3) {// 遥控设备
			// viewHolder.dev_type_img
			// .setBackgroundResource(R.drawable.third_telecontrol_default);
			// }
			final ListHandler handler = new ListHandler();
			viewHolder.switch_btn
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (third_dev_list_.get(position).dev_safeguard_flag == 0) {
								new Thread() {
									public void run() {
										Message msg = handler.obtainMessage(1,
												position, 0); // 1 打开防护
										handler.sendMessage(msg);
									};
								}.start();

							} else {
								new Thread() {
									public void run() {
										Message msg = handler.obtainMessage(0,
												position, 0); // 0 关闭防护
										handler.sendMessage(msg);
									};
								}.start();
							}
						}
					});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OutOfMemoryError ooe) {
			ooe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ItemViewHolder {
		public TextView dev_nick_name;
		public Button switch_btn;
		public ImageView dev_type_img;
		public TextView dev_yst;
	}

	// class Onlongclick implements OnLongClickListener{
	// public boolean onLongClick(View v) {
	// String[] choices={"删除"};
	// //包含多个选项的对话框
	// AlertDialog dialog = new AlertDialog.Builder(context_)
	// .setIcon(android.R.drawable.btn_star)
	// .setTitle("分享")
	// .setItems(choices, onselect).create();
	// dialog.show();
	// // TODO Auto-generated method stub
	// }
	// }
}
