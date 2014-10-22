package com.jovision.activities;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.ThirdDevAdapter;
import com.jovision.bean.Device;
import com.jovision.bean.ThirdAlarmDev;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.AlarmUtil;
import com.jovision.utils.CacheUtil;
import com.jovision.views.XListView;

public class ThirdDevListActivity extends BaseActivity implements
		OnClickListener {
	private Button backBtn;
	private Button bindBtn;
	private Button topClickAddBtn;
	private TextView titleTv;
	private XListView thirdDevListView = null;// 设备列表view
	private ThirdDevAdapter thirdDevAdapter;
	private int selected_dev_index = -1;// 保存当前修改防护开关的设备索引
	private int saved_third_safe_flag = 1;// 保存第三方设备防护开关的要被设置成的标志，等返回成功后再设置，默认开
	private RelativeLayout topAddLayout;
	private boolean btopaddvisible = true;
	private String strYstNum;
	private ProgressDialog dialog;
	private boolean bConnectFlag = false;
	private ThirdDevListActivity mActivity;
	private PlayWindowManager manager;
	private int saved_index = -1; // 保存开关index，等响应结果后修改对应的状态
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private Device device;
	private ArrayList<ThirdAlarmDev> thirdList = new ArrayList<ThirdAlarmDev>();
	private boolean bNeedSendTextReq = true;
	private MyHandler myHandler;

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
		topAddLayout = (RelativeLayout) findViewById(R.id.top_add_layout);
		backBtn = (Button) findViewById(R.id.btn_left);
		bindBtn = (Button) findViewById(R.id.btn_right);
		titleTv = (TextView) findViewById(R.id.currentmenu);
		thirdDevListView = (XListView) findViewById(R.id.third_dev_listview);
		thirdDevListView.setOnItemLongClickListener(mOnLongClickListener);
		thirdDevListView.setPullLoadEnable(false);
		thirdDevListView.setPullRefreshEnable(false);
		topClickAddBtn = (Button) findViewById(R.id.add_third_dev_top);
		topClickAddBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
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
		device = deviceList.get(selected_dev_index);
		thirdList = deviceList.get(selected_dev_index).getThirdDevList();
		strYstNum = device.getFullNo();
		thirdDevAdapter = new ThirdDevAdapter(this, thirdList, device);
		thirdDevListView.setAdapter(thirdDevAdapter);
		if (thirdList.size() > 0) {
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

		if (!bConnectFlag) {
			if (!AlarmUtil.OnlyConnect(strYstNum)) {
				showTextToast("连接失败，已经连接或者超过最大连接数");
				dialog.dismiss();
			}
		}
		myHandler = new MyHandler();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		CacheUtil.saveDevList(deviceList);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (bConnectFlag) {
			// JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接
			Jni.disconnect(0);
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
			intent.putExtra("text_req_flag", bNeedSendTextReq);
			startActivityForResult(intent, 10);
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (10 == resultCode) {

			String nickname = data.getExtras().getString("nickname");
			int dev_type_mark = data.getExtras().getInt("dev_type_mark");
			ThirdAlarmDev testThirdDev = new ThirdAlarmDev();
			testThirdDev.dev_uid = data.getExtras().getInt("dev_uid");
			testThirdDev.dev_nick_name = nickname;
			testThirdDev.dev_type_mark = dev_type_mark;
			testThirdDev.dev_safeguard_flag = 1;
			testThirdDev.dev_belong_yst = strYstNum;
			thirdList.add(testThirdDev);
			thirdDevAdapter.setDataList(thirdList);
			thirdDevListView.setAdapter(thirdDevAdapter);
			thirdDevAdapter.notifyDataSetChanged();
			device.setThirdDevList(thirdList);
			if (btopaddvisible) {
				topAddLayout.setVisibility(View.GONE);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showToast(String text, int duration) {
		Toast.makeText(this, "[DEBUG] " + text, duration).show();
	}

	// [lkp]
	// 推送长按显示删除按钮
	OnItemLongClickListener mOnLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub

			String[] choices = { "删除" };
			// 包含多个选项的对话框
			onSelect onSelect = new onSelect(mActivity, arg2 - 1);
			AlertDialog dialog = new AlertDialog.Builder(mActivity)
					.setTitle("删除设备")
					.setItems(choices, onSelect)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// 这里点击取消之后可以进行的操作
									dialog.cancel();
								}
							}).create();
			dialog.show();
			return true;
		}
	};

	class onSelect implements DialogInterface.OnClickListener {

		private int index_;
		private Context context_;

		public onSelect(Context context, int index) {
			index_ = index;
			context_ = context;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub

			ThirdAlarmDev thirdDev = device.getThirdDevList().get(index_);

			StringBuffer bb = new StringBuffer();
			String arg1 = "type=" + thirdDev.dev_type_mark + ";";
			String arg2 = "guid=" + thirdDev.dev_uid + ";";
			bb.append(arg1).append(arg2);
			Jni.sendString(0, (byte) JVNetConst.JVN_RSP_TEXTDATA, false, 0,
					(byte) Consts.RC_GPIN_DEL, bb.toString().trim());
			dialog.dismiss();
			mActivity.dialog.show();
		}
	}

	private boolean RemoveItemWithGuid(int guid, int dev_type) {
		MyLog.e("Delete Third Dev", "delete guid:" + guid + ", type:"
				+ dev_type);
		ArrayList<ThirdAlarmDev> tmpthirdList = device.getThirdDevList();
		for (int i = 0; i < tmpthirdList.size(); i++) {
			if (guid == tmpthirdList.get(i).dev_uid
					&& dev_type == tmpthirdList.get(i).dev_type_mark) {
				tmpthirdList.remove(i);
				device.setThirdDevList(tmpthirdList);
				return true;
			}
		}
		return false;
	}

	class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case JVNetConst.JVN_REQ_TEXT:
			case JVNetConst.JVN_RSP_TEXTDATA:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				showTextToast("获取主控绑定设备超时");
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		MyLog.e(TAG, "onHandler--what=" + what + ";arg1=" + arg1 + ";arg2="
				+ arg2 + "; obj = " + (obj == null ? "" : obj.toString()));
		switch (what) {
		// 连接结果
		case Consts.CALL_CONNECT_CHANGE:
			switch (arg2) {

			case JVNetConst.NO_RECONNECT:// 1 -- 连接成功//3 不必重新连接
			case JVNetConst.CONNECT_OK: {// 1 -- 连接成功
				MyLog.e("New alarm", "连接成功");
				bConnectFlag = true;
				showToast("连接成功", Toast.LENGTH_SHORT);
				// 首先需要发送文本聊天请求
				Jni.sendBytes(0, (byte) JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
				myHandler.sendEmptyMessageDelayed(JVNetConst.JVN_REQ_TEXT,
						10000);// 10秒获取不到就取消Dialog
			}
				break;
			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK: {
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
			}
				break;
			}
			;
			break;
		case Consts.CALL_TEXT_DATA: {
			myHandler.removeMessages(JVNetConst.JVN_REQ_TEXT);
			myHandler.removeMessages(JVNetConst.JVN_RSP_TEXTDATA);
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本请求后才发送请求,这里要区分出是添加还是最后的绑定昵称
				bNeedSendTextReq = false;
				StringBuffer videoBuffer = new StringBuffer();
				Jni.sendString(0, (byte) JVNetConst.JVN_RSP_TEXTDATA, false, 0,
						(byte) Consts.RC_GPIN_SECLECT, videoBuffer.toString()
								.trim());
				myHandler.sendEmptyMessageDelayed(JVNetConst.JVN_RSP_TEXTDATA,
						10000);// 10秒获取不到就取消Dialog

				break;
			case JVNetConst.JVN_CMD_TEXTSTOP:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				bNeedSendTextReq = true;
				showTextToast("文本请求聊天终止");
				break;
			case JVNetConst.JVN_RSP_TEXTDATA: {
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();

				JSONObject respObject = null;
				if (obj != null) {
					try {
						respObject = new JSONObject(obj.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						showTextToast("TextData回调obj参数转Json异常");
						return;
					}
				} else {
					showTextToast("TextData回调obj参数is null");
					MyLog.e("Third Dev", "TextData回调obj参数is null");
					return;
				}
				int flag = respObject.optInt("flag");
				switch (flag) {
				case Consts.RC_GPIN_SECLECT:
					if (obj != null) {
						String selectStr = respObject.optString("msg");

						if (!selectStr.equals("")) {
							// 成功
							String[] selectStrArray = selectStr.split("\\$");

							ArrayList<ThirdAlarmDev> myThirdList = device
									.getThirdDevList();
							myThirdList.clear();
							if (selectStrArray.length > 0) {
								topAddLayout.setVisibility(View.GONE);
							} else {
								topAddLayout.setVisibility(View.VISIBLE);
							}
							for (int i = 0; i < selectStrArray.length; i++) {
								String inArray[] = selectStrArray[i].split(";");
								// type=1;guid=1;enable=1;name=south;$
								// type=1;guid=2;enable=1;name=south;$
								ThirdAlarmDev selectalarm = new ThirdAlarmDev();
								for (int j = 0; j < inArray.length; j++) {
									String[] split = inArray[j].split("=");

									if ("guid".equals(split[0])) {
										selectalarm.dev_uid = Integer
												.parseInt(split[1]);
									} else if ("type".equals(split[0])) {
										selectalarm.dev_type_mark = Integer
												.parseInt(split[1]);
									} else if ("enable".equals(split[0])) {
										selectalarm.dev_safeguard_flag = Integer
												.parseInt(split[1]);
									} else if ("name".equals(split[0])) {
										if (split.length > 1) {
											selectalarm.dev_nick_name = split[1];
										} else {
											selectalarm.dev_nick_name = "未命名";
										}
									}
								}
								selectalarm.dev_belong_yst = strYstNum;
								myThirdList.add(selectalarm);
							}
							thirdDevAdapter.setDataList(myThirdList);
							thirdDevListView.setAdapter(thirdDevAdapter);
							device.setThirdDevList(myThirdList);
							thirdDevAdapter.notifyDataSetChanged();
						} else {
							// 失败
							showToast("该设备暂无绑定第三方设备或者查询失败", Toast.LENGTH_SHORT);
						}
					} else {
						showTextToast("TextData回调obj参数is null");
					}
					break;
				case Consts.RC_GPIN_SET:// 设置开关
					if (obj != null) {
						String setStr = respObject.optString("msg");
						String setStrArray[] = setStr.split(";");
						ThirdAlarmDev setalarm = new ThirdAlarmDev();
						int setResult = -1;
						for (int i = 0; i < setStrArray.length; i++) {
							String[] split = setStrArray[i].split("=");
							if ("res".equals(split[0])) {
								setResult = Integer.parseInt(split[1]);
								break;
							}
						}

						if (setResult == 1) {// SET success
							for (int i = 0; i < setStrArray.length; i++) {
								String[] split = setStrArray[i].split("=");
								if ("guid".equals(split[0])) {
									setalarm.dev_uid = Integer
											.parseInt(split[1]);
								} else if ("type".equals(split[0])) {
									setalarm.dev_type_mark = Integer
											.parseInt(split[1]);
								}
							}
							// ok
							thirdList.get(saved_index).dev_safeguard_flag = saved_third_safe_flag;
							thirdDevAdapter.setDataList(thirdList);
							thirdDevListView.setAdapter(thirdDevAdapter);
							thirdDevAdapter.notifyDataSetChanged();
							device.setThirdDevList(thirdList);
							break;

						} else {
							showToast("设置属性失败", Toast.LENGTH_SHORT);
						}
					} else {
						showTextToast("TextData回调obj参数is null");
					}
					break;
				case Consts.RC_GPIN_DEL:// 删除
					if (obj != null) {
						String delStr = respObject.optString("msg");
						String delStrArray[] = delStr.split(";");
						ThirdAlarmDev delalarm = new ThirdAlarmDev();
						int delResult = -1;
						for (int i = 0; i < delStrArray.length; i++) {
							String[] split = delStrArray[i].split("=");
							if ("res".equals(split[0])) {
								delResult = Integer.parseInt(split[1]);
								break;
							}
						}

						if (delResult == 1) {// SET success
							for (int i = 0; i < delStrArray.length; i++) {
								String[] split = delStrArray[i].split("=");
								if ("guid".equals(split[0])) {
									delalarm.dev_uid = Integer
											.parseInt(split[1]);
								} else if ("type".equals(split[0])) {
									delalarm.dev_type_mark = Integer
											.parseInt(split[1]);
								}
							}
							// OK
							if (RemoveItemWithGuid(delalarm.dev_uid,
									delalarm.dev_type_mark)) {
								thirdDevAdapter.setDataList(device
										.getThirdDevList());
								thirdDevListView.setAdapter(thirdDevAdapter);
								thirdDevAdapter.notifyDataSetChanged();

								if (device.getThirdDevList().size() == 0) {
									topAddLayout.setVisibility(View.VISIBLE);
									btopaddvisible = true;
								}
							}
						} else {
							// Failed
							showToast("删除设备失败", Toast.LENGTH_SHORT);
						}
					} else {
						showTextToast("TextData回调obj参数is null");
					}
					break;
				default:
					break;
				}
			}
				break;
			default:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				showTextToast("文本请求其他回调");
				break;
			}
		}
			break;
		// case 9009://开关
		// //arg1 开关标志 arg2 索引 obj 请求参数
		// if(obj != null){
		// saved_index = arg2;
		// Jni.sendString(0, (byte)JVNetConst.JVN_RSP_TEXTDATA, false, 0,
		// (byte)Consts.RC_GPIN_SET, obj.toString());
		// // JVSUDT.JVC_GPINAlarm(JVConst.ONLY_CONNECT,
		// (byte)JVNetConst.JVN_RSP_TEXTDATA, (byte)JVConst.RC_GPIN_SET,
		// obj.toString());
		// }
		// break;
		case Consts.RC_GPIN_SET_SWITCH:// 设置开关，内部使用
			saved_index = arg2;
			saved_third_safe_flag = arg1;
			MyLog.e("RC_GPIN_SET_SWITCH", "arg1 : " + arg1 + ", arg2:" + arg2);
			Jni.sendString(0, (byte) JVNetConst.JVN_RSP_TEXTDATA, false, 0,
					(byte) Consts.RC_GPIN_SET, obj.toString().trim());
			break;

		// default:
		// if (dialog != null && dialog.isShowing())
		// dialog.dismiss();
		// showTextToast("其他回调");
		// break;
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
		deviceList = CacheUtil.getDevList();
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
