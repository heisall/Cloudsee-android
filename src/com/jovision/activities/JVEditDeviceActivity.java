package com.jovision.activities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.PointListAdapter2;
import com.jovision.bean.ConnPoint;
import com.jovision.bean.Device;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.LoginUtil;

@SuppressLint("InlinedApi")
public class JVEditDeviceActivity extends BaseActivity {

	private Button back;// 显示隐藏左侧按钮
	private TextView currentMenu;// 当前页面名称
	// 设备详细信息
	private TextView deviceNumber;// 云视通号
	private Button addPoint;// 添加通道
	private RelativeLayout deviceItemLayout;
	private LinearLayout deviceDetail;
	private EditText deviceNickName;
	private EditText deviceUserName;
	private EditText devicePassWord;
	private Button saveDevice;
	private Button closeEdit;
	private Button closeNikeName;
	private Button closeUserName;
	private Button closePwd;

	private PointListAdapter2 pointAdapter;
	private ListView pointListView;

	private Device editDevice;// 待修改的设备
	private int listIndex;// 设备在列表中的index

	private int openPointIndex = -1;// 展开的通道位置
	private String whichActivity;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case JVConst.DEVICE_POS_DATA_REFRESH:// 刷新滑动到指定位置
			deviceDetail.setVisibility(View.GONE);
			pointListView.setAdapter(pointAdapter);
			pointListView.setSelection(arg1);
			pointAdapter.notifyDataSetChanged();
			break;
		case JVConst.ADD_POINT_SUCCESS:// 添加通道
			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
				BaseApp.deviceList = BaseApp.orderDevice(BaseApp.deviceList);
				if (listIndex < BaseApp.deviceList.size()) {
					editDevice = BaseApp.deviceList.get(listIndex);
				}
			}
			pointAdapter.setData(editDevice, listIndex);
			pointListView.setAdapter(pointAdapter);
			pointAdapter.notifyDataSetChanged();
			showTextToast(R.string.login_str_point_add_success);
			break;
		case JVConst.ADD_POINT_FAILED:
			showTextToast(R.string.login_str_point_add_failed);
			break;

		case JVConst.EDIT_DEVICE_SUCCESS:// 修改设备
			editDevice.deviceName = deviceNickName.getText().toString();
			editDevice.deviceLoginUser = deviceUserName.getText().toString();
			editDevice.deviceLoginPwd = devicePassWord.getText().toString();
			showTextToast(R.string.login_str_device_edit_success);
			break;
		case JVConst.EDIT_DEVICE_FAILED:
			showTextToast(R.string.login_str_device_edit_failed);

			showTextToast(R.string.login_str_device_edit_failed);
			break;

		case JVConst.DELETE_DEVICE_SUCCESS:// 删除设备
			showTextToast(R.string.login_str_device_delete_success);
			finish();
			break;
		case JVConst.DELETE_DEVICE_FAILED:
			showTextToast(R.string.login_str_device_delete_failed);
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.device_left_item);
		addPoint = (Button) findViewById(R.id.addpoint);// 添加通道
		deviceDetail = (LinearLayout) findViewById(R.id.devicedetail);
		deviceDetail.setVisibility(View.GONE);
		Intent intent = getIntent();
		if (null != intent) {
			listIndex = intent.getIntExtra("listIndex", 0);
			whichActivity = intent.getStringExtra("Activity");
			if ("play".equals(whichActivity)) {
				setResult(JVConst.MODIFY_DEVICE_INFO_RESP);
				addPoint.setVisibility(View.INVISIBLE);
				deviceDetail.setVisibility(View.VISIBLE);
			} else {
				addPoint.setVisibility(View.VISIBLE);
			}
			MyLog.v("sss", listIndex + " listIndex");
			MyLog.v("sss", BaseApp.deviceList.size()
					+ " BaseApp.deviceList.size()");
			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()
					&& listIndex < BaseApp.deviceList.size()) {
				editDevice = BaseApp.deviceList.get(listIndex);
				MyLog.v("sss", editDevice.deviceNum);
			}
		}

		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_device_manage);
		back.setOnClickListener(onClickListener);
		deviceNumber = (TextView) findViewById(R.id.devicenumber);// 云视通号
		deviceItemLayout = (RelativeLayout) findViewById(R.id.deviceitemlayout);
		deviceNickName = (EditText) findViewById(R.id.deviceaccountnike);
		deviceUserName = (EditText) findViewById(R.id.deviceaccountname);
		devicePassWord = (EditText) findViewById(R.id.deviceaccountpass);
		closeNikeName = (Button) findViewById(R.id.closenikename);
		closeUserName = (Button) findViewById(R.id.closeusername);
		closePwd = (Button) findViewById(R.id.closepwd);
		closeNikeName.setOnClickListener(onClickListener);
		closeUserName.setOnClickListener(onClickListener);
		closePwd.setOnClickListener(onClickListener);
		pointListView = (ListView) findViewById(R.id.coonlistview);
		deviceItemLayout.setOnClickListener(onClickListener);
		deviceItemLayout.setOnLongClickListener(onLongClickListener);
		addPoint.setOnClickListener(onClickListener);
		saveDevice = (Button) findViewById(R.id.savedevice);
		closeEdit = (Button) findViewById(R.id.closedevice);
		saveDevice.setOnClickListener(onClickListener);
		closeEdit.setOnClickListener(onClickListener);

		if (null != editDevice) {
			deviceNumber.setText(editDevice.deviceNum);
			deviceNickName.setText(editDevice.deviceName);
			deviceUserName.setText(editDevice.deviceLoginUser);
			devicePassWord.setText(editDevice.deviceLoginPwd);

			pointAdapter = new PointListAdapter2(JVEditDeviceActivity.this,
					Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN)));
			pointAdapter.setData(editDevice, listIndex);
			pointListView.setAdapter(pointAdapter);
		}

	}

	// 按钮单击事件
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				JVEditDeviceActivity.this.finish();
				break;
			case R.id.deviceitemlayout:
				openPointIndex = -1;
				pointAdapter.currentConnPos = openPointIndex;
				pointAdapter.notifyDataSetChanged();
				if (View.VISIBLE == deviceDetail.getVisibility()) {
					deviceDetail.setVisibility(View.GONE);
				} else {
					deviceDetail.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.addpoint:
				try {
					if (null == editDevice.pointList) {
						editDevice.pointList = new ArrayList<ConnPoint>();
					}
					int addCount = 0;// 需要添加的通道数量
					if (editDevice.pointList.size() < 61) {
						addCount = 4;
					} else if (editDevice.pointList.size() >= 61
							&& editDevice.pointList.size() <= 64) {
						addCount = 64 - editDevice.pointList.size();
					} else if (editDevice.pointList.size() == 64) {
						showTextToast(R.string.login_str_point_add_full);
					}
					if (0 != addCount) {
						createDialog(R.string.str_deleting);

						AddPointThread apThread = new AddPointThread(
								JVEditDeviceActivity.this, addCount, editDevice);
						apThread.start();

					} else {
						showTextToast(R.string.login_str_point_add_full);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case R.id.savedevice:
				// 设备昵称不为空
				if ("".equalsIgnoreCase(deviceNickName.getText().toString())) {
					showTextToast(R.string.str_nikename_notnull);
				}
				// 设备昵称验证
				else if (!LoginUtil.checkNickName(deviceNickName.getText()
						.toString())) {
					showTextToast(R.string.login_str_nike_name_order);
				}
				// 设备用户名不为空
				else if ("".equalsIgnoreCase(deviceUserName.getText()
						.toString())) {
					showTextToast(R.string.login_str_device_account_notnull);
				} else if (!LoginUtil.checkDeviceUsername(deviceUserName
						.getText().toString())) {
					showTextToast(R.string.login_str_device_account_error);
				} else if (!LoginUtil.checkDevicePwd(devicePassWord.getText()
						.toString())) {
					showTextToast(R.string.login_str_device_pass_error);
				} else {
					if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
						// JVConnectInfo jvc = editDevice.toJVConnectInfo();
						// jvc.setNickName(deviceNickName.getText().toString());
						// jvc.setUserName(deviceUserName.getText().toString());
						// jvc.setPasswd(devicePassWord.getText().toString());
						// int res = BaseApp.modifyChannelInfoPort(jvc);
						// if (res > 0) {
						// handler.sendMessage(handler
						// .obtainMessage(JVConst.EDIT_DEVICE_SUCCESS));
						// } else {
						// handler.sendMessage(handler
						// .obtainMessage(JVConst.EDIT_DEVICE_FAILED));
						// }
					} else {
						createDialog(R.string.str_deleting);
						editDevice.deviceName = deviceNickName.getText()
								.toString();
						editDevice.deviceLoginUser = deviceUserName.getText()
								.toString();
						editDevice.deviceLoginPwd = devicePassWord.getText()
								.toString();
						editDevice.deviceLocalIp = "";
						editDevice.deviceLocalPort = 0;
						EditDeviceThread edThread = new EditDeviceThread(
								JVEditDeviceActivity.this, editDevice);
						edThread.start();
					}
				}
				break;
			case R.id.closedevice:
				deviceDetail.setVisibility(View.GONE);
				break;
			case R.id.closenikename:
				deviceNickName.setText("");
				break;
			case R.id.closeusername:
				deviceUserName.setText("");
				break;
			case R.id.closepwd:
				devicePassWord.setText("");
				break;
			}
		}
	};

	OnLongClickListener onLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub

			AlertDialog.Builder builder = new Builder(JVEditDeviceActivity.this);
			builder.setMessage(R.string.str_delete_sure);
			builder.setTitle(R.string.str_delete_tip);
			builder.setPositiveButton(R.string.str_sure,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Boolean.valueOf(statusHashMap
									.get(Consts.LOCAL_LOGIN))) {// 本地登录操作数据库
								// JVConnectInfo jvInfo = editDevice
								// .toJVConnectInfo();
								// jvInfo.setParent(true);
								// int res =
								// BaseApp.completeDeleteChannel(jvInfo);
								//
								// if (res > 0) {
								// BaseApp.deviceList.remove(listIndex);
								// handler.sendMessage(handler
								// .obtainMessage(JVConst.DELETE_DEVICE_SUCCESS));
								// } else {
								// handler.sendMessage(handler
								// .obtainMessage(JVConst.DELETE_DEVICE_FAILED));
								// }

							} else {// 非本地操作，提交到服务器上
								createDialog(R.string.str_deleting);
								DeleteDeviceThread ddt = new DeleteDeviceThread(
										JVEditDeviceActivity.this, editDevice);
								ddt.start();
							}
						}
					});

			builder.setNegativeButton(R.string.str_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();

			return false;
		}

	};

	// 修改设备线程
	private static class EditDeviceThread extends Thread {
		private Device device = null;
		private final WeakReference<JVEditDeviceActivity> mActivity;

		public EditDeviceThread(JVEditDeviceActivity activity, Device obj) {
			mActivity = new WeakReference<JVEditDeviceActivity>(activity);
			device = obj;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			JVEditDeviceActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				int flag = DeviceUtil.modifyDevice(
						activity.statusHashMap.get("KEY_USERNAME"),
						device.deviceNum, device.deviceName,
						device.deviceLoginUser, device.deviceLoginPwd);
				if (0 == flag) {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVConst.EDIT_DEVICE_SUCCESS));
				} else {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVConst.EDIT_DEVICE_FAILED));
				}
			}
		}

	}

	// 添加通道线程
	private static class AddPointThread extends Thread {
		private int addCount;
		private Device device;
		private final WeakReference<JVEditDeviceActivity> mActivity;

		public AddPointThread(JVEditDeviceActivity activity, int arg0,
				Device arg1) {
			mActivity = new WeakReference<JVEditDeviceActivity>(activity);
			addCount = arg0;
			device = arg1;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			JVEditDeviceActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {

				int size = 0;
				size = device.pointList.size();
				ArrayList<Integer> addList = new ArrayList<Integer>();

				try {
					if (0 == size) {// 原来没通道，直接添加，无需考虑顺序问题
						for (int i = 0; i < addCount; i++) {
							addList.add(i);
						}
					} else {
						int[] temArray = new int[64];// 临时数组，用于判断哪个通道没了需要添加
						for (int i = 0; i < size; i++) {
							temArray[device.pointList.get(i).pointNum - 1] = device.pointList
									.get(i).pointNum;
						}
						for (int i = 0; i < temArray.length; i++) {
							if (temArray[i] == 0) {
								addList.add(i);
								if (addList.size() == addCount) {
									break;
								}
							}
						}
					}

					if (Boolean.valueOf(activity.statusHashMap
							.get(Consts.LOCAL_LOGIN))) {
						for (int i = 0; i < addList.size(); i++) {
							// connPoint.deviceID = device.deviceOID;
							//
							// connPoint.pointNum = device.deviceNum + "_"
							// + (addList.get(i) + 1);
							// connPoint.pointName = addList.get(i) + 1;

							ConnPoint connPoint = new ConnPoint();
							connPoint.deviceID = device.deviceOID;
							connPoint.pointNum = addList.get(i) + 1;// A361_1
							connPoint.pointName = device.deviceNum + "_"
									+ (addList.get(i) + 1);

							MyLog.e("需要添加的通道号：", connPoint.pointNum + "");
							if (null != connPoint) {
								// connPoint.group = device.group;
								// connPoint.ystNum = device.yst;
								// JVConnectInfo jvc = device.toJVConnectInfo();
								// jvc.setParent(false);
								// jvc.setChannel(connPoint.pointNum);
								// jvc.setNickName(connPoint.pointName);
								// int res = BaseApp.addItem(jvc);
								// if (res > 0) {
								// device.pointList.add(connPoint);
								// }

							}
						}
					} else {
						if (0 == DeviceUtil
								.addPoint(device.deviceNum, addCount)) {
							// device.pointList = DeviceUtil
							// .getDevicePointList(device.deviceNum);
						}

					}
					MyLog.e("通道数量", device.pointList.size() + "");
					MyLog.e("size + addCount", (size + addCount) + "");
					if (device.pointList.size() == (size + addCount)) {
						activity.handler.sendMessage(activity.handler
								.obtainMessage(JVConst.ADD_POINT_SUCCESS));
					} else {
						activity.handler.sendMessage(activity.handler
								.obtainMessage(JVConst.ADD_POINT_FAILED));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	// 删除设备线程
	private static class DeleteDeviceThread extends Thread {
		private Device device;
		private final WeakReference<JVEditDeviceActivity> mActivity;

		public DeleteDeviceThread(JVEditDeviceActivity activity, Device dev) {
			mActivity = new WeakReference<JVEditDeviceActivity>(activity);
			device = dev;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			JVEditDeviceActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				// LoginUtil.deviceId = device.deviceOID;
				int flag = DeviceUtil.unbindDevice(
						activity.statusHashMap.get("KEY_USERNAME"),
						device.deviceNum);

				if (0 == flag) {
					BaseApp.deviceList.remove(device);
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVConst.DELETE_DEVICE_SUCCESS));

				} else {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVConst.DELETE_DEVICE_FAILED));
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVEditDeviceActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
