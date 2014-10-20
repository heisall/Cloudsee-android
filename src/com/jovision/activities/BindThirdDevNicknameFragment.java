package com.jovision.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.ActionReceiver;
import com.jovision.activities.AddThirdDeviceMenuFragment.OnDeviceClassSelectedListener;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.RegularUtil;

public class BindThirdDevNicknameFragment extends Fragment implements
		OnClickListener, ActionReceiver.EventHandler {

	private View rootView;// 缓存Fragment view
	private Button completeBtn;
	private EditText nickNameEdt;
	private String nickName;
	private int dev_type_mark;
	private String dev_num;
	private int dev_uid;
	private boolean bConnectedFlag;
	private ProgressDialog dialog;
	
	public interface OnSetNickNameListener {
		public void OnSetNickName(int index);
	}

	private OnSetNickNameListener mListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.bind_thirddev_nick_fragment,
					container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		completeBtn = (Button) rootView.findViewById(R.id.complete_btn);
		completeBtn.setOnClickListener(this);
		nickNameEdt = (EditText) rootView.findViewById(R.id.third_dev_nick_edt);
		nickNameEdt.setFocusable(true);
		dev_type_mark = getArguments().getInt("dev_type_mark");
		dev_uid = getArguments().getInt("dev_uid");
		dev_num = getArguments().getString("dev_num");
		bConnectedFlag = getArguments().getBoolean("conn_flag");
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
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.complete_btn:
			nickName = nickNameEdt.getText().toString().trim();
			if (nickName.equals("")) {
				Toast.makeText(getActivity(), "请输入昵称", Toast.LENGTH_SHORT)
						.show();
			} else {	
				if(!RegularUtil.checkNickName(nickName)){
					showToast("输入的昵称不合法，请重新输入", Toast.LENGTH_SHORT);
					return;
				}
				dialog.show();
				if(bConnectedFlag){
					SendBingNickName();
				}
				else{
					AlarmUtil.OnlyConnect(dev_num);
				}
			}
			break;

		default:
			break;
		}
	}
	@Override
	public void onHandlerNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();		
		switch (what) {
		case 9006://设置属性
			if(arg1 == 1){
				//成功
				// 返回第三方设备列表activity
				Intent data = new Intent();
				data.putExtra("nickname", nickName);
				data.putExtra("dev_type_mark", dev_type_mark);
				data.putExtra("dev_uid", dev_uid);
				// 请求代码可以自己设置，这里设置成20
				getActivity().setResult(10, data);
				// 关闭掉这个Activity
				getActivity().finish();				
			}else{
				//失败
				showToast("设置昵称失败:"+arg1, Toast.LENGTH_SHORT);
			}
			break;
		case JVNetConst.JVN_RSP_TEXTACCEPT://同意文本请求后才发送请求
			SendBingNickName();
			break;
		default:
			break;
		}
	}
	@Override
	public void onHandleConnectRes(int ret, Object obj) {
		// TODO Auto-generated method stub
		switch (ret) {
		case 1://连接成功
		case 3://已经连上，如正在播放视频界面
			MyLog.e("New alarm", "连接成功");
			bConnectedFlag = true;
			showToast("连接成功", Toast.LENGTH_SHORT);
			JVSUDT.JVC_SendData(JVConst.ONLY_CONNECT,
					(byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);			
			SendBingNickName();	
			break;
		case 2://断开连接成功
			bConnectedFlag = false;
			break;
		case 4://连接失败
			Log.e("New alarm", "连接失败");
			bConnectedFlag = false;
			showToast("连接失败", Toast.LENGTH_SHORT);
			break;
		default:
			bConnectedFlag = false;
			showToast("连接:"+ret, Toast.LENGTH_SHORT);
			break;
		}		
	}
	private void showToast(String text, int duration){
		Toast.makeText(getActivity(), "[DEBUG] "+text, duration).show();
	}
	private void SendBingNickName(){
		String strType = "type="+dev_type_mark+";";
		String strGuid = "guid="+dev_uid+";";
		String strNickName = "name="+nickName+";";
		String strSwitch = "enable=1;";
		String reqData = strType+strGuid+strNickName+strSwitch;	
		Log.e("Third Dev", "bing nick name req:"+reqData);
		JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_SET, reqData.trim());				
	}
}
