package com.jovision.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.ActionReceiver;

public class AddThirdDeviceMenuFragment extends Fragment implements
		OnClickListener,ActionReceiver.EventHandler {
	private View rootView;// 缓存Fragment view
	private Button doorBtn; // 门磁
	private Button braceletBtn; // 手环
	private ProgressDialog dialog;
	private ThirdDevHandler myHandler;
	private boolean bConnectFlag = false;
	private String strYstNum;
	private int dev_type_mark;
	private int dev_uid; //设备绑定成功后返回的 唯一标示
	public interface OnDeviceClassSelectedListener {
		public void OnDeviceClassSelected(int index);
	}

	private OnDeviceClassSelectedListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDeviceClassSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnDeviceClassSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.add_thirddev_menu_fragment,
					container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		doorBtn = (Button) rootView.findViewById(R.id.add_door_btn);
		doorBtn.setOnClickListener(this);
		braceletBtn = (Button) rootView.findViewById(R.id.add_bracelet_btn);
		braceletBtn.setOnClickListener(this);
		Bundle bundle = getArguments();  
		strYstNum = bundle.getString("yst_num");
		bConnectFlag = getArguments().getBoolean("conn_flag");
		myHandler = new ThirdDevHandler();

		if (null == dialog) {
			dialog = new ProgressDialog(getActivity());
			dialog.setCancelable(false);
		}

		return rootView;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ActionReceiver.ehList.add(this);
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ActionReceiver.ehList.remove(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_door_btn:// 门磁设备
			dev_type_mark = 1;
			dialog.setMessage(getResources().getString(
					R.string.str_loading_data));
			dialog.show();
//			Message msg = myHandler.obtainMessage(1, 0, 0);
//			myHandler.sendMessageDelayed(msg, 2000);
			if(!bConnectFlag){
				AlarmUtil.OnlyConnect(strYstNum);
			}
			else{
//				JVSUDT.JVC_SendData(JVConst.ONLY_CONNECT,
//						(byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);				
				String req_data = "type=1;";
				JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA,
						 (byte)JVConst.RC_GPIN_ADD, req_data.trim());				
			}
			// 实际应该起个线程，然后开始播放动画
			// TODO
			break;
		case R.id.add_bracelet_btn:// 手环设备
			dev_type_mark = 2;
			dialog.setMessage(getResources().getString(
					R.string.str_loading_data));
			dialog.show();
//			Message msg1 = myHandler.obtainMessage(1, 1, 0);
//			myHandler.sendMessageDelayed(msg1, 2000);
			if(!bConnectFlag){
				AlarmUtil.OnlyConnect(strYstNum);
			}
			else{
//				JVSUDT.JVC_SendData(JVConst.ONLY_CONNECT,
//						(byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);				
				String req_data = "type=2;";
				JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA,
						 (byte)JVConst.RC_GPIN_ADD, req_data.trim());				
			}			
			// 实际应该起个线程，然后开始播放动画
			// TODO
			break;
		default:
			break;
		}
	}

	class ThirdDevHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();			
			switch (msg.what) {
			case 1:
				BindThirdDevNicknameFragment nicknameFragment = new BindThirdDevNicknameFragment();
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				Bundle bundle = new Bundle();
				bundle.putInt("dev_type_mark", msg.arg1);
				bundle.putInt("dev_uid", msg.arg2);
				bundle.putBoolean("conn_flag", bConnectFlag);
				bundle.putString("dev_num", strYstNum);
				nicknameFragment.setArguments(bundle);
				// Replace whatever is in the fragment_container view with this
				// fragment,
				// and add the transaction to the back stack so the user can
				// navigate back
				transaction.replace(R.id.fragment_container, nicknameFragment);
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();
				mListener.OnDeviceClassSelected(msg.arg1);
				break;
			default:
				break;
			}
		}
	}


	@Override
	public void onHandlerNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		switch (what) {
		case 9005://绑定第三方设备
			if(arg1 == 0){//failed
				
				showToast("绑定设备失败", Toast.LENGTH_SHORT);
				myHandler.sendEmptyMessage(-1);
			}
			else if(arg1 == 1){//OK
				showToast("绑定设备成功", Toast.LENGTH_SHORT);
				dev_uid = arg2;
				Message msg = myHandler.obtainMessage();
				msg.what = 1;
				msg.arg1 = dev_type_mark;
				msg.arg2 = arg2;
				myHandler.sendMessage(msg);
			}
			else if(arg1 == 2){//超过最大数
				showToast("超过最大数", Toast.LENGTH_SHORT);
				myHandler.sendEmptyMessage(-1);
			}
			else{
				myHandler.sendEmptyMessage(-1);
			}
			break;
		case JVNetConst.JVN_RSP_TEXTACCEPT://同意文本请求后才发送请求
			String req_data = "type="+dev_type_mark+";";
			JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA,
					 (byte)JVConst.RC_GPIN_ADD, req_data.trim());
			break;
		default:
			myHandler.sendEmptyMessage(-1);
			break;
		}
	}

	@Override
	public void onHandleConnectRes(int ret, Object obj) {
		// TODO Auto-generated method stub
//		switch (ret) {
//		case 1://连接成功
//		case 3://已经连上，如正在播放视频界面
//			Log.e("New alarm", "连接成功");
//			bConnectFlag = true;
//			showToast("连接成功", Toast.LENGTH_SHORT);
//			//首先需要发送文本聊天请求
//			JVSUDT.JVC_SendData(JVConst.ONLY_CONNECT,
//					(byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
//		
//			break;
//		case 2://断开连接成功
//			bConnectFlag = false;
//			break;
//		case 4://连接失败
//			Log.e("New alarm", "连接失败");
//			bConnectFlag = false;
//			showToast("连接失败", Toast.LENGTH_SHORT);
//			myHandler.sendEmptyMessage(-1);
//			break;
//		default:
//			bConnectFlag = false;
//			showToast("连接:"+ret, Toast.LENGTH_SHORT);
//			myHandler.sendEmptyMessage(-1);
//			break;
//		}		
	}
	private void showToast(String text, int duration){
		Toast.makeText(getActivity(), "[DEBUG] "+text, duration).show();
	}	
}
