package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jovision.bean.ConnPoint;
import com.jovision.bean.Device;

public class PointListAdapter2 extends BaseAdapter {

	// 获取通道选中位置
	public int currentConnPos = -1;
	public Context mContext;
	public LayoutInflater inflater;
	public Device editDevice;
	public int deviceIndex;
	public ArrayList<ConnPoint> pointList;

	private boolean localLogin = false;

	public PointListAdapter2(Context con, Boolean local) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		localLogin = local;

	}

	public void setData(Device device, int index) {
		editDevice = device;
		deviceIndex = index;
		pointList = editDevice.pointList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null == pointList) {
			return 0;
		}
		return pointList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		Object obj = null;
		if (null != pointList && arg0 < pointList.size()) {
			obj = pointList.get(arg0);
		}
		return obj;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ConnPointHolder connHolder;
		final int pointIndex = arg0;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.connpoint_left_item, null);
			connHolder = new ConnPointHolder();
			connHolder.connItemLayout = (RelativeLayout) convertView
					.findViewById(R.id.connitemlayout);
			connHolder.connPointImg = (ImageView) convertView
					.findViewById(R.id.connpointimg);
			connHolder.connPointName = (TextView) convertView
					.findViewById(R.id.connpointname);
			connHolder.connDetailLayout = (TableLayout) convertView
					.findViewById(R.id.conndetail);
			connHolder.connAccountNikeName = (EditText) convertView
					.findViewById(R.id.connaccountnike);
			connHolder.saveConn = (Button) convertView
					.findViewById(R.id.saveconn);
			connHolder.closeConn = (Button) convertView
					.findViewById(R.id.closeconn);
			connHolder.deleteConn = (Button) convertView
					.findViewById(R.id.deleteconnpoint);
			connHolder.closeBtn = (Button) convertView
					.findViewById(R.id.closebtn);
			convertView.setTag(connHolder);
		} else {
			connHolder = (ConnPointHolder) convertView.getTag();
		}
		if (null != pointList && pointIndex < pointList.size()) {
			connHolder.connPointName.setText(// editDevice.deviceNum + "_"
					pointList.get(pointIndex).pointName);
			connHolder.connAccountNikeName
					.setText(pointList.get(pointIndex).pointName);
		}
		//
		// // 保存
		// if (null != pointList && pointIndex < pointList.size()) {
		// final ConnPoint connPoint = pointList.get(pointIndex);
		// connHolder.saveConn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// LoginUtil.pointId = connPoint.pointNum;
		// // 通道昵称不能为空
		// if ("".equalsIgnoreCase(connHolder.connAccountNikeName
		// .getText().toString())) {
		// Toast.makeText(
		// mContext,
		// mContext.getResources().getString(
		// R.string.str_nikename_notnull),
		// Toast.LENGTH_LONG).show();
		// }
		// // 通道昵称验证
		// else if (!LoginUtil
		// .checkNickName(connHolder.connAccountNikeName
		// .getText().toString())) {
		// Toast.makeText(
		// mContext,
		// mContext.getResources().getString(
		// R.string.login_str_nike_name_order),
		// Toast.LENGTH_LONG).show();
		// } else {
		// LoginUtil.connName = connHolder.connAccountNikeName
		// .getText().toString();// 修改后的名称
		// connPoint.pointName = LoginUtil.connName;
		// LoginUtil.deviceId = connPoint.deviceID;
		// LoginUtil.pointNum = connPoint.pointNum;
		//
		// currentConnPos = -1;
		// if (localLogin) {
		// JVConnectInfo jvc = connPoint.toJVConnectInfo();
		// int res = BaseApp.modifyChannelInfoPort(jvc);
		// Message msg = deviceHandler.obtainMessage();
		// if (res > 0) {
		// connPoint.pointName = LoginUtil.connName;
		// msg.what = JVConst.EDIT_POINT_SUCCESS;
		// msg.arg1 = pointIndex;
		// } else {
		// msg.what = JVConst.EDIT_POINT_FAILED;
		// msg.arg1 = pointIndex;
		// }
		// deviceHandler.sendMessage(msg);
		// } else {
		// EditPointThread epThread = new EditPointThread(
		// connPoint, pointIndex);
		// epThread.start();
		// }
		// }
		// }
		//
		// });
		// }
		//
		// // 关闭
		// connHolder.closeConn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// currentConnPos = -1;
		// ((MainApplication)
		// mContext.getApplicationContext()).onNotify(JVConst.DEVICE_POS_DATA_REFRESH,pointIndex,0,null);
		// }
		//
		// });
		//
		// // 单击展开修改界面
		// connHolder.connItemLayout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (currentConnPos == pointIndex) {// 关闭修改界面
		// currentConnPos = -1;
		// ((MainApplication)
		// mContext.getApplicationContext()).onNotify(JVConst.DEVICE_POS_DATA_REFRESH,pointIndex,0,null);
		// } else {// 打开修改界面
		// currentConnPos = pointIndex;
		// ((MainApplication)
		// mContext.getApplicationContext()).onNotify(JVConst.DEVICE_POS_DATA_REFRESH,pointIndex,0,null);
		// }
		//
		// }
		//
		// });
		//
		// if (currentConnPos == pointIndex) {
		// connHolder.connDetailLayout.setVisibility(View.VISIBLE);
		//
		// } else {
		// connHolder.connDetailLayout.setVisibility(View.GONE);
		// }
		//
		// // 删除通道
		// connHolder.connItemLayout
		// .setOnLongClickListener(new OnLongClickListener() {
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// AlertDialog.Builder builder = new Builder(
		// (JVEditDeviceActivity) mContext);
		// builder.setMessage(R.string.str_delete_sure);
		// builder.setTitle(R.string.str_delete_tip);
		// builder.setPositiveButton(R.string.str_sure,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// dialog.dismiss();
		// if (null != proDialog
		// && !proDialog.isShowing()) {
		// proDialog.show();
		// }
		// ((BaseActivity) mContext).cre
		//
		// // 普通删除，删除通道
		// if (null != pointList
		// && pointList.size() > 1) {
		//
		// if (localLogin) {// 本地登录操作数据库
		// JVConnectInfo jvInfo = pointList
		// .get(pointIndex)
		// .toJVConnectInfo();
		// jvInfo.setParent(false);
		// int res = BaseApp
		// .completeDeleteChannel(jvInfo);
		// Message msg = deviceHandler
		// .obtainMessage();
		// if (res > 0) {
		// BaseApp.deviceList
		// .get(deviceIndex).pointList
		// .remove(pointIndex);
		// msg.what = JVConst.DELETE_POINT_SUCCESS;
		// msg.arg1 = pointIndex + 1;
		// } else {
		// msg.what = JVConst.DELETE_POINT_FAILED;
		// msg.arg1 = pointIndex;
		// }
		// deviceHandler.sendMessage(msg);
		// } else {
		// DeletePointThread dpt = new DeletePointThread(
		// pointIndex);
		// dpt.start();
		// }
		//
		// } else {// 如果设备只剩下一个通道，则将该设备删除
		// if (localLogin) {// 本地登录操作数据库
		// JVConnectInfo jvInfo = editDevice
		// .toJVConnectInfo();
		// jvInfo.setParent(true);
		// int res = BaseApp
		// .completeDeleteChannel(jvInfo);
		// Message msg = deviceHandler
		// .obtainMessage();
		// if (res > 0) {
		// BaseApp.deviceList
		// .remove(deviceIndex);
		// msg.what = JVConst.DELETE_DEVICE_SUCCESS;
		// } else {
		// msg.what = JVConst.DELETE_DEVICE_FAILED;
		// }
		// deviceHandler.sendMessage(msg);
		//
		// } else {// 非本地操作，提交到服务器上
		// DeleteDeviceThread ddt = new DeleteDeviceThread(
		// deviceIndex);
		// ddt.start();
		// }
		// }
		// }
		// });
		//
		// builder.setNegativeButton(R.string.str_cancel,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// dialog.dismiss();
		// }
		// });
		//
		// builder.create().show();
		// return false;
		// }
		//
		// });
		// connHolder.closeBtn.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// connHolder.connAccountNikeName.setText("");
		// connHolder.connAccountNikeName.setFocusable(true);
		// connHolder.connAccountNikeName.requestFocus();
		// }
		// });
		return convertView;
	}

	//
	// // 删除设备线程
	// class DeleteDeviceThread extends Thread {
	// int index;
	//
	// DeleteDeviceThread(int arg) {
	// index = arg;
	// }
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// super.run();
	// if (index < BaseApp.deviceList.size()) {
	// LoginUtil.deviceId = BaseApp.deviceList.get(index).deviceOID;
	// int flag = DeviceUtil.unbindDevice(LoginUtil.userName,
	// BaseApp.deviceList.get(index).deviceNum);
	// Message msg = deviceHandler.obtainMessage();
	// if (0 == flag) {
	// if (index < BaseApp.deviceList.size()) {
	// BaseApp.deviceList.remove(index);
	// msg.what = JVConst.DELETE_DEVICE_SUCCESS;
	// }
	//
	// } else {
	// msg.what = JVConst.DELETE_DEVICE_FAILED;
	// }
	// deviceHandler.sendMessage(msg);
	// }
	// }
	//
	// }
	//
	// // 删除通道线程
	// class DeletePointThread extends Thread {
	// int child = 0;
	//
	// DeletePointThread(int arg1) {
	// child = arg1;
	// }
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// super.run();
	// if (null != pointList && child < pointList.size()) {
	// LoginUtil.pointId = pointList.get(child).pointNum;
	// int flag = DeviceUtil.deletePoint(editDevice.deviceNum,
	// LoginUtil.pointId);
	// Message msg = deviceHandler.obtainMessage();
	// if (0 == flag) {
	// if (child < pointList.size()) {
	//
	// // try{
	// // if(null ==
	// // BaseApp.deviceList.get(group).pointList.get(child).connImage
	// // ||
	// //
	// "".equalsIgnoreCase(BaseApp.deviceList.get(group).pointList.get(child).connImage.imageUrl)){
	// //
	// // }else{
	// // String imageUrl = Url.DOWNLOAD_IMAGE_URL +
	// // deviceList.get(group).pointList.get(child).connImage.imageUrl;
	// //
	// // MyLog.e("删除图片地址", imageUrl+"");
	// // AsyncImageLoader.getInstance().imageCache.remove(imageUrl);
	// // BaseApp.deviceList.get(group).deviceImageList.remove(imageUrl);
	// // }
	// //
	// // }catch(Exception e){
	// // e.printStackTrace();
	// // }
	// pointList.remove(child);
	// msg.what = JVConst.DELETE_POINT_SUCCESS;
	// msg.arg1 = child + 1;
	// }
	//
	// } else {
	// msg.what = JVConst.DELETE_POINT_FAILED;
	// msg.arg1 = child;
	// }
	// deviceHandler.sendMessage(msg);
	// }
	//
	// }
	//
	// }
	//
	// // 添加通道线程
	// class AddPointThread extends Thread {
	// int addCount;
	// Device device;
	//
	// AddPointThread(int arg0, Device arg1) {
	// addCount = arg0;
	// device = arg1;
	// }
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// super.run();
	// Message msg = deviceHandler.obtainMessage();
	// int size = 0;
	// size = device.pointList.size();
	// ArrayList<Integer> addList = new ArrayList<Integer>();
	//
	// try {
	// if (0 == size) {// 原来没通道，直接添加，无需考虑顺序问题
	// for (int i = 0; i < addCount; i++) {
	// addList.add(i);
	// }
	// } else {
	// int[] temArray = new int[64];// 临时数组，用于判断哪个通道没了需要添加
	// for (int i = 0; i < size; i++) {
	// temArray[device.pointList.get(i).pointNum - 1] = device.pointList
	// .get(i).pointNum;
	// }
	// for (int i = 0; i < temArray.length; i++) {
	// if (temArray[i] == 0) {
	// addList.add(i);
	// if (addList.size() == addCount) {
	// break;
	// }
	// }
	// }
	// }
	//
	// for (int i = 0; i < addList.size(); i++) {
	// // MyLog.e("addList"+i, addList.get(i)+"");
	// LoginUtil.deviceId = device.deviceOID;
	// LoginUtil.connName = device.deviceNum + "_"
	// + (addList.get(i) + 1);
	// LoginUtil.pointNum = addList.get(i) + 1;
	//
	// MyLog.e("需要添加的通道号：", LoginUtil.pointNum + "");
	// ConnPoint connPoint = new ConnPoint();
	//
	// if (localLogin) {// 本地登录,修改数据库
	// connPoint.deviceID = LoginUtil.deviceId;
	// connPoint.pointNum = LoginUtil.pointNum;
	// connPoint.pointName = LoginUtil.connName;
	// } else {// 在线
	// connPoint = LoginUtil.createConnPoint(
	// LoginUtil.connName, LoginUtil.deviceId,
	// LoginUtil.pointNum);
	// if (null == connPoint) {// 第一次请求失败后，发送二次请求
	// connPoint = LoginUtil.createConnPoint(
	// LoginUtil.connName, LoginUtil.deviceId,
	// LoginUtil.pointNum);
	// }
	// }
	//
	// if (null != connPoint) {
	// if (localLogin) {// 本地登录,修改数据库
	// JVConnectInfo jvc = device.toJVConnectInfo();
	// jvc.setParent(false);
	// jvc.setChannel(LoginUtil.pointNum);
	// jvc.setNickName(LoginUtil.connName);
	// int res = BaseApp.addItem(jvc);
	// if (res > 0) {
	// device.pointList.add(connPoint);
	// }
	// } else {
	// device.pointList.add(connPoint);
	// }
	//
	// }
	// }
	//
	// MyLog.e("通道数量", device.pointList.size() + "");
	// MyLog.e("size + addCount", (size + addCount) + "");
	// if (device.pointList.size() == (size + addCount)) {
	// msg.what = JVConst.ADD_POINT_SUCCESS;
	// } else {
	// msg.what = JVConst.ADD_POINT_FAILED;
	// }
	//
	// deviceHandler.sendMessage(msg);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }
	//
	// // 修改通道线程
	// class EditPointThread extends Thread {
	// ConnPoint connPoint = null;
	// int index;
	//
	// EditPointThread(ConnPoint obj, int pointIndex) {
	// connPoint = obj;
	// index = pointIndex;
	// }
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// super.run();
	// int flag = DeviceUtil.modifyPointName(editDevice.deviceNum,
	// LoginUtil.pointNum, LoginUtil.connName);
	// // LoginUtil.editConnPoint(LoginUtil.pointId,
	// // LoginUtil.connName, LoginUtil.deviceId, LoginUtil.pointNum);
	//
	// Message msg = deviceHandler.obtainMessage();
	// if (0 == flag) {
	// connPoint.pointName = LoginUtil.connName;
	// msg.what = JVConst.EDIT_POINT_SUCCESS;
	// msg.arg1 = index;
	// } else {
	// msg.what = JVConst.EDIT_POINT_FAILED;
	// msg.arg1 = index;
	// }
	// deviceHandler.sendMessage(msg);
	// }
	//
	// }
	//
	// class DeviceHandler extends Handler {
	// DeviceHandler(Looper looper) {
	// super(looper);
	// }
	//
	// public DeviceHandler() {
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// switch (msg.what) {
	// // 编辑
	// case JVConst.EDIT_POINT_SUCCESS:
	// ((BaseActivity)
	// mContext).showTextToast(R.string.login_str_point_edit_success);
	// break;
	// case JVConst.EDIT_POINT_FAILED:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_point_edit_failed);
	// break;
	// case JVConst.EDIT_DEVICE_SUCCESS:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_device_edit_success);
	// break;
	// case JVConst.EDIT_DEVICE_FAILED:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_device_edit_failed);
	// break;
	//
	// // 删除
	// case JVConst.DELETE_POINT_SUCCESS:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_point_delete_success);
	// break;
	// case JVConst.DELETE_POINT_FAILED:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_point_delete_failed);
	// break;
	// case JVConst.DELETE_DEVICE_SUCCESS:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_device_delete_success);
	// ((MainApplication)
	// mContext.getApplicationContext()).onNotify(JVConst.DELETE_DEVICE_SUCCESS,msg.arg1,0,null);
	// break;
	// case JVConst.DELETE_DEVICE_FAILED:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_device_delete_failed);
	// break;
	//
	// // 添加通道
	// case JVConst.ADD_POINT_SUCCESS:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_point_add_success);
	// break;
	// case JVConst.ADD_POINT_FAILED:
	// ((BaseActivity) mContext).showTextToast(
	// R.string.login_str_point_add_failed);
	// break;
	//
	// }
	// ((MainApplication)
	// mContext.getApplicationContext()).onNotify(JVConst.DEVICE_POS_DATA_REFRESH,msg.arg1,0,null);
	// }
	//
	// }

	// 通道(child)
	class ConnPointHolder {
		RelativeLayout connItemLayout;
		ImageView connPointImg;
		TextView connPointName;
		TableLayout connDetailLayout;
		EditText connAccountNikeName;
		Button saveConn;
		Button closeConn;
		Button deleteConn;
		Button closeBtn; // 删除输入的文本内容
	}

}
