package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.ThirdDevAdapter;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.bean.ThirdAlarmDev;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.CacheUtil;
import com.jovision.views.XListView;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jovision.ActionReceiver;
import com.jovision.Consts;
import com.jovision.Jni;

public class ThirdDevListActivity extends BaseActivity implements
	OnClickListener,ActionReceiver.EventHandler {
	private Button backBtn;
	private Button bindBtn;
	private Button topClickAddBtn;
	private TextView titleTv;
	private XListView thirdDevListView = null;// 设备列表view
	private ThirdDevAdapter thirdDevAdapter;
	private int selected_dev_index = -1;
	private RelativeLayout topAddLayout;
	private boolean btopaddvisible = true;
	private String strYstNum;
	private ProgressDialog dialog;
	private boolean bConnectFlag = false;
	private ThirdDevListActivity mActivity;
	private PlayWindowManager manager;
	private int saved_index = -1; //保存开关index，等响应结果后修改对应的状态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mActivity = this;

		setContentView(R.layout.third_alarm_device_main);
		InitViews();
		InitData();
	}

	private void InitViews() {
		topAddLayout = (RelativeLayout)findViewById(R.id.top_add_layout);
		backBtn = (Button) findViewById(R.id.btn_left);
		bindBtn = (Button) findViewById(R.id.btn_right);
		titleTv = (TextView) findViewById(R.id.currentmenu);
		thirdDevListView = (XListView) findViewById(R.id.third_dev_listview);
		thirdDevListView.setOnItemLongClickListener(mOnLongClickListener);
		topClickAddBtn = (Button) findViewById(R.id.add_third_dev_top);
		topClickAddBtn.setOnClickListener(this);
		backBtn.setBackgroundResource(R.drawable.back);
		backBtn.setOnClickListener(this);
		bindBtn.setBackgroundResource(R.drawable.adddevice);
		bindBtn.setVisibility(View.VISIBLE);
		bindBtn.setOnClickListener(this);
		titleTv.setText(R.string.str_alarm_manage);
	}

	private void InitData() {

		Bundle extras = getIntent().getExtras();
		selected_dev_index = extras.getInt("dev_index");
		if (selected_dev_index < 0) {
			selected_dev_index = 0;
		}
		Device device_itemDevice = CacheUtil.getDevList().get(selected_dev_index);
		// 测试数据
		// ThirdAlarmDev testThirdDev = new ThirdAlarmDev();
		// testThirdDev.dev_id = "0";
		// testThirdDev.dev_nick_name = "测试南区北门";
		// testThirdDev.dev_type_mark = 0;
		// device_itemDevice.thirdDevMap.put(testThirdDev.dev_id, testThirdDev);
		//
		// ThirdAlarmDev testThirdDev2 = new ThirdAlarmDev();
		// testThirdDev2.dev_id = "1";
		// testThirdDev2.dev_nick_name = "城管大队队长的手环";
		// testThirdDev.dev_type_mark = 1;
		// testThirdDev2.dev_safeguard_flag = 1;
		// device_itemDevice.thirdDevMap.put(testThirdDev2.dev_id,
		// testThirdDev2);
		strYstNum = device_itemDevice.getFullNo();
		thirdDevAdapter = new ThirdDevAdapter(this,
				device_itemDevice.getThirdDevList(), device_itemDevice);
		thirdDevListView.setAdapter(thirdDevAdapter);
		if(device_itemDevice.getThirdDevList().size() > 0){
			topAddLayout.setVisibility(View.GONE);
			btopaddvisible = false;
		}
		if (null == dialog) {
			dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(
					R.string.str_loading_data));			
		}		
		dialog.show();
		
		if(!bConnectFlag){
			AlarmUtil.OnlyConnect(strYstNum);
		}		
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bConnectFlag){
//			JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			this.finish();
			break;
		case R.id.btn_right:
		case R.id.add_third_dev_top:
			Intent intent = new Intent(ThirdDevListActivity.this,
					AddThirdDevActivity.class);
			intent.putExtra("dev_num", strYstNum);
			intent.putExtra("conn_flag", bConnectFlag);
			startActivityForResult(intent, 10);
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (10 == resultCode) {
			Device device_itemDevice = CacheUtil.getDevList()
					.get(selected_dev_index);

			String nickname = data.getExtras().getString("nickname");
			int dev_type_mark = data.getExtras().getInt("dev_type_mark");
			ThirdAlarmDev testThirdDev = new ThirdAlarmDev();
			testThirdDev.dev_uid = data.getExtras().getInt("dev_uid");
			testThirdDev.dev_nick_name = nickname;
			testThirdDev.dev_type_mark = dev_type_mark;
			device_itemDevice.getThirdDevList().add(testThirdDev);
			thirdDevAdapter.notifyDataSetChanged();
			if(btopaddvisible){
				topAddLayout.setVisibility(View.GONE);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onHandlerNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
//		if (dialog != null && dialog.isShowing())
//			dialog.dismiss();		
//		switch (what) {
//		case 9006://设置属性
//			if(arg1 == 1){
//				//ok
//				Device device_itemDevice = CacheUtil.getDevList()
//						.get(selected_dev_index);	
//				device_itemDevice.getThirdDevList().get(saved_index).dev_safeguard_flag = arg2;//
//				thirdDevAdapter.notifyDataSetChanged();
//			}
//			else{
//				showToast("设置属性失败", Toast.LENGTH_SHORT);
//			}
//			break;
//		case 9007://查询第三方设备
//			if(arg1 == 1){
//				//成功
//				
//				String selectStr = obj.toString();
//				String []selectStrArray = selectStr.split("\\$");
//				Device device_itemDevice = CacheUtil.getDevList()
//						.get(selected_dev_index);
//				device_itemDevice.getThirdDevList().clear();
//				if(selectStrArray.length > 0){
//					topAddLayout.setVisibility(View.GONE);					
//				}
//				else{
//					topAddLayout.setVisibility(View.VISIBLE);
//				}
//				for(int i=0;i<selectStrArray.length;i++){
//					String inArray[] = selectStrArray[i].split(";");
//					//type=1;guid=1;enable=1;name=south;$
//					//type=1;guid=2;enable=1;name=south;$
//					ThirdAlarmDev selectalarm = new ThirdAlarmDev();
//					for(int j=0;j<inArray.length;j++){
//						String[] split = inArray[j].split("=");
//
//						if("guid".equals(split[0])){
//							selectalarm.dev_uid = Integer.parseInt(split[1]);
//						}else if("type".equals(split[0])){
//							selectalarm.dev_type_mark = Integer.parseInt(split[1]);
//						}else if("enable".equals(split[0])){
//							selectalarm.dev_safeguard_flag = Integer.parseInt(split[1]);
//						}else if("name".equals(split[0])){
//							if(split.length > 1){
//								selectalarm.dev_nick_name = split[1];
//							}
//							else{
//								selectalarm.dev_nick_name = "No Name";
//							}
//						}						
//					}
//					device_itemDevice.getThirdDevList()
//					.add(selectalarm);																	
//				}	
//				thirdDevAdapter.notifyDataSetChanged();
//			}else{
//				//失败
//				showToast("查询设备失败:"+arg1, Toast.LENGTH_SHORT);
//			}
//			break;
//		case JVNetConst.JVN_RSP_TEXTACCEPT://同意文本请求后才发送请求
//			StringBuffer videoBuffer = new StringBuffer();
//			JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_SECLECT, videoBuffer.toString().trim());
//			break;
//		case 9008://删除
//			if(arg1 == 1){ //arg1 结果  arg2 guid
//				//OK
//				Device device_itemDevice = CacheUtil.getDevList()
//						.get(selected_dev_index);	
//				if(RemoveItemWithGuid(device_itemDevice.getThirdDevList(), arg2, Integer.parseInt(obj.toString()))){
//					thirdDevAdapter.notifyDataSetChanged();
//				}
//				
//			}else{
//				//Failed
//				showToast("删除设备失败", Toast.LENGTH_SHORT);
//			}
//		case 9009://开关
//			//arg1 开关标志  arg2 索引 obj 请求参数
//			if(obj != null){
//				saved_index = arg2;
//				JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_SET, obj.toString());
//			}	
//			break;
//		default:
//			break;
//		}		
	}
	@Override
	public void onHandleConnectRes(int ret, Object obj) {
		// TODO Auto-generated method stub
//		if (dialog != null && dialog.isShowing())
//			dialog.dismiss();			
//		switch (ret) {
//		case 1://连接成功
//		case 3://已经连上，如正在播放视频界面
//			MyLog.e("New alarm", "连接成功");
//			bConnectFlag = true;
//			showToast("连接成功", Toast.LENGTH_SHORT);
//			//首先需要发送文本聊天请求
//			JVSUDT.JVC_SendData(JVConst.ONLY_CONNECT,
//					(byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
//		
//			break;
//		case 2://断开连接成功
//			bConnectFlag = false;
//			if (dialog != null && dialog.isShowing())
//				dialog.dismiss();			
//			break;
//		case 4://连接失败
//			if (dialog != null && dialog.isShowing())
//				dialog.dismiss();			
//			MyLog.e("New alarm", "连接失败");
//			bConnectFlag = false;
//			showToast("连接失败", Toast.LENGTH_SHORT);
//			break;
//		default:
//			if (dialog != null && dialog.isShowing())
//				dialog.dismiss();			
//			bConnectFlag = false;
//			showToast("连接:"+ret, Toast.LENGTH_SHORT);
//			break;
//		}		
	}
	private void showToast(String text, int duration){
		Toast.makeText(this, "[DEBUG] "+text, duration).show();
	}
	
	// [lkp]
	 // 推送长按显示删除按钮
	 OnItemLongClickListener mOnLongClickListener = new
		 OnItemLongClickListener() {
		
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub

			String[] choices={"删除"};  
	        //包含多个选项的对话框  
			onSelect onSelect = new onSelect(mActivity, arg2-1);
			AlertDialog dialog = new AlertDialog.Builder(mActivity) 
	           .setTitle("删除设备")  
	           .setItems(choices, onSelect)
	           .setNegativeButton(
                   "取消",
                   new DialogInterface.OnClickListener() {

                       public void onClick(
                               DialogInterface dialog,
                               int which) {
                           // 这里点击取消之后可以进行的操作
                    	   dialog.cancel();
                       }
                   }).create();  
			dialog.show(); 
			return true;
		}	
	 };

	class onSelect implements DialogInterface.OnClickListener{

		private int index_;
		private Context context_;
		public onSelect(Context context, int index){
			index_ = index;
			context_ = context;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Device device_itemDevice = CacheUtil.getDevList().get(selected_dev_index);
			ThirdAlarmDev thirdDev = device_itemDevice.getThirdDevList().get(index_);
			
			StringBuffer bb  = new StringBuffer();
			String arg1 = "type="+thirdDev.dev_type_mark+";";
			String arg2 = "guid="+thirdDev.dev_uid+";";
			bb.append(arg1).append(arg2);
			Jni.sendString(0, (byte)JVNetConst.JVN_RSP_TEXTDATA, false, 0, (byte)Consts.RC_GPIN_DEL, bb.toString().trim());
//			JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_DEL, bb.toString().trim());
			dialog.dismiss();
			mActivity.dialog.show();
		}
	}
	
	private boolean RemoveItemWithGuid(ArrayList<ThirdAlarmDev> list, int guid, int dev_type){
		MyLog.e("Delete Third Dev", "delete guid:"+guid+", type:"+dev_type);
		for(int i=0; i<list.size(); i++){
			if(guid == list.get(i).dev_uid && dev_type == list.get(i).dev_type_mark){
				list.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		MyLog.v(TAG, "onHandler--what=" + what + ";arg1=" + arg1 + ";arg2="
				+ arg2);		
		switch (what) {
		// 连接结果
		case Consts.CALL_CONNECT_CHANGE:
			Channel channel = manager.getChannel(arg2);
			if (null == channel) {
				MyLog.e("CustomDialogActivity onHandler", "the channel "+arg2+" is null");
				return;
			}
			switch (arg1) {
				
				case JVNetConst.NO_RECONNECT:// 1 -- 连接成功//3 不必重新连接
				case JVNetConst.CONNECT_OK: {// 1 -- 连接成功
					channel.setConnecting(false);
					channel.setConnected(true);
					
					MyLog.e("New alarm", "连接成功");
					bConnectFlag = true;
					showToast("连接成功", Toast.LENGTH_SHORT);		
					//首先需要发送文本聊天请求
					Jni.sendBytes(0,
							(byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);					
				}
				break;
				// 2 -- 断开连接成功
				case JVNetConst.DISCONNECT_OK: {
					channel.setConnecting(false);
					channel.setConnected(false);
					bConnectFlag = false;
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();						
				}
				break;
				// 4 -- 连接失败
				case JVNetConst.CONNECT_FAILED: {
					bConnectFlag = false;
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();					
					try {
						JSONObject connectObj = new JSONObject(obj.toString());
						String errorMsg = connectObj.getString("msg");
						if ("password is wrong!".equalsIgnoreCase(errorMsg)
								|| "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
							showTextToast(R.string.connfailed_auth);
						} else if ("channel is not open!"
								.equalsIgnoreCase(errorMsg)) {// 无该通道服务
							showTextToast(R.string.connfailed_channel_notopen);
						} else if ("connect type invalid!"
								.equalsIgnoreCase(errorMsg)) {// 连接类型无效
							showTextToast(R.string.connfailed_type_invalid);
						} else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
							showTextToast(R.string.connfailed_maxcount);
						} else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
							showTextToast(R.string.connfailed_timeout);
						} else {// "Connect failed!"
							showTextToast(R.string.connect_failed);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					channel.setConnecting(false);
					channel.setConnected(false);
				}	
				break;
			};
			break;
		case 9006://设置属性
			if(arg1 == 1){
				//ok
				Device device_itemDevice = CacheUtil.getDevList()
						.get(selected_dev_index);	
				device_itemDevice.getThirdDevList().get(saved_index).dev_safeguard_flag = arg2;//
				thirdDevAdapter.notifyDataSetChanged();
			}
			else{
				showToast("设置属性失败", Toast.LENGTH_SHORT);
			}
			break;			
		case JVNetConst.JVN_RSP_TEXTACCEPT://同意文本请求后才发送请求
			StringBuffer videoBuffer = new StringBuffer();
			Jni.sendString(0, (byte)JVNetConst.JVN_RSP_TEXTDATA, false, 0, (byte)Consts.RC_GPIN_SECLECT, videoBuffer.toString().trim());
//			JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_SECLECT, videoBuffer.toString().trim());
			break;
		case 9007://查询第三方设备
			if(arg1 == 1){
				//成功
				
				String selectStr = obj.toString();
				String []selectStrArray = selectStr.split("\\$");
				Device device_itemDevice = CacheUtil.getDevList()
						.get(selected_dev_index);
				device_itemDevice.getThirdDevList().clear();
				if(selectStrArray.length > 0){
					topAddLayout.setVisibility(View.GONE);					
				}
				else{
					topAddLayout.setVisibility(View.VISIBLE);
				}
				for(int i=0;i<selectStrArray.length;i++){
					String inArray[] = selectStrArray[i].split(";");
					//type=1;guid=1;enable=1;name=south;$
					//type=1;guid=2;enable=1;name=south;$
					ThirdAlarmDev selectalarm = new ThirdAlarmDev();
					for(int j=0;j<inArray.length;j++){
						String[] split = inArray[j].split("=");

						if("guid".equals(split[0])){
							selectalarm.dev_uid = Integer.parseInt(split[1]);
						}else if("type".equals(split[0])){
							selectalarm.dev_type_mark = Integer.parseInt(split[1]);
						}else if("enable".equals(split[0])){
							selectalarm.dev_safeguard_flag = Integer.parseInt(split[1]);
						}else if("name".equals(split[0])){
							if(split.length > 1){
								selectalarm.dev_nick_name = split[1];
							}
							else{
								selectalarm.dev_nick_name = "No Name";
							}
						}						
					}
					device_itemDevice.getThirdDevList()
					.add(selectalarm);																	
				}	
				thirdDevAdapter.notifyDataSetChanged();
			}else{
				//失败
				showToast("查询设备失败:"+arg1, Toast.LENGTH_SHORT);
			}
			break; 
			
		case 9008://删除
			if(arg1 == 1){ //arg1 结果  arg2 guid
				//OK
				Device device_itemDevice = CacheUtil.getDevList()
						.get(selected_dev_index);	
				if(RemoveItemWithGuid(device_itemDevice.getThirdDevList(), arg2, Integer.parseInt(obj.toString()))){
					thirdDevAdapter.notifyDataSetChanged();
				}
				
			}else{
				//Failed
				showToast("删除设备失败", Toast.LENGTH_SHORT);
			}
			break;
		case 9009://开关
			//arg1 开关标志  arg2 索引 obj 请求参数
			if(obj != null){
				saved_index = arg2;
				Jni.sendString(0, (byte)JVNetConst.JVN_RSP_TEXTDATA, false, 0, (byte)Consts.RC_GPIN_SET, obj.toString());
//				JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT, (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_SET, obj.toString());
			}	
			break;			
		}		
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub
		
	}
}
