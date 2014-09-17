//package com.jovision.activities;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Parcelable;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.test.JVSUDT;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.jovetech.CloudSee.temp.R;
//import com.jovision.adapters.WifiAdapter;
//import com.jovision.commons.BaseApp;
//import com.jovision.commons.JVConst;
//import com.jovision.commons.JVNetConst;
//import com.jovision.commons.MyLog;
//import com.jovision.utils.LoginUtil;
//import com.jovision.views.RefreshableListView;
//import com.jovision.views.RefreshableListView.OnRefreshListener;
//
//public class JVRemoteSettingActivity extends Activity {
//	// ViewPager是google SDk中自带的一个附加包的一个类，可以用来实现屏幕间的切换。
//	// android-support-v4.jar
//	private ViewPager mPager;// 页卡内容
//	private List<View> listViews; // Tab页面列表
//	// private ImageView cursor;// 动画图片
//	private TextView t1, t2, t3;// 页卡头标
//	private int offset = 0;// 动画图片偏移量
//	private int currIndex = 0;// 当前页卡编号
//	private int bmpW;// 动画图片宽度
//	private ArrayList<TextView> tabMenuList = null;
//
//	private Button back;// 返回按钮
//	private Button saveChange;// 保存按钮
//	private TextView currentMenu;// 当前页面名称
//
//	// 有线连接
//	private LinearLayout firstLayout;
//	private RelativeLayout obtainAuto;
//	private ImageView autoImage;
//	private RelativeLayout obtainManu;
//	private ImageView manuImage;
//	private LinearLayout secondLayout;
//	private int bdhcpTag = 0;
//
//	private EditText ipET;// IP地址
//	private EditText netmaskET;// 子网掩码
//	private EditText gateWayET;// 默认网关
//	private EditText dnsET;// 域名服务器
//	private EditText macET;// 网卡地址
//	private EditText cloudseeidET;// 设备号
//	private EditText stateET;// 设备状态
//
//	private String ipStr = "";// IP地址
//	private String netmaskStr = "";// 子网掩码
//	private String gateWayStr = "";// 默认网关
//	private String dnsStr = "";// 域名服务器
//
//	private ProgressDialog dialog = null;// 进度dialog
//
//	// 无线连接
//	private Button wifiDetail;
//	private LinearLayout wifiFirstLayout;
//	private LinearLayout wifiSecondLayout;
//
//	private LinearLayout wifiSelect;// wifi选择layout
//	private ProgressBar loadingWifi;// 进度条
//	private RefreshableListView wifiListView; // wifi列表
//	private LinearLayout wifiListBG;// wifi列表背景
//	private WifiAdapter wifiAdapter;
//
//	private EditText wifiipET;// IP地址
//	private EditText wifinetmaskET;// 子网掩码
//	private EditText wifigateWayET;// 默认网关
//	private EditText wifidnsET;// 域名服务器
//	private EditText wifimacET;// 网卡地址
//
//	private EditText wifiName;// 无线网名
//	private EditText wifiPwd;// 无线网密码
//
//	// 码流设置
//	private LinearLayout imageQualitySetting;// 图片质量
//	private LinearLayout clearSetting;// 帧率
//	private LinearLayout fluentSetting;// 码流质量
//	private TextView imageQuality;
//	private TextView imageClear;
//	private TextView imageFluent;
//
//	private int arrayIndex0;
//	private int arrayIndex1;
//	private int arrayIndex2;
//
//	private String deviceNum = "";// 设备名
//	private int deviceIndex = -1;// 设备index
//	private int wifiIndex = -1;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		LoginUtil.activityList.add(JVRemoteSettingActivity.this);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.remotesetting_layout);
//		// getWindow().setLayout(LayoutParams.MATCH_PARENT,
//		// LayoutParams.MATCH_PARENT);
//
//		JVSUDT.DEVICE_MANAGE_FLAG = false;
//		JVSUDT.QUICK_SETTING_FLAG = false;
//
//		Intent intent = getIntent();
//		if (null != intent) {
//			deviceIndex = intent.getIntExtra("Index", -1);
//			deviceNum = intent.getStringExtra("deviceNum");
//		}
//
//		BaseApp.settingHandler = new SettingHandler(
//				JVRemoteSettingActivity.this);
//
//		initViews();
//
//		if (null != BaseApp.settingMap
//				&& BaseApp.settingMap.get("ACTIVED").equalsIgnoreCase("0")) {// 有线
//			currIndex = 0;
//
//			// 值为2双码流是家庭安防产品，显示加载wifi动画
//			if (null != BaseApp.settingMap.get("MobileCH")
//					&& "2".equalsIgnoreCase(BaseApp.settingMap.get("MobileCH"))) {
//				// 可选取无线网络进行配置
//				wifiSelect.setVisibility(View.VISIBLE);
//				wifiListView.setVisibility(View.VISIBLE);
//				wifiListBG.setVisibility(View.VISIBLE);
//				loadingWifi.setVisibility(View.VISIBLE);
//				wifiName.setEnabled(true);
//				wifiPwd.setEnabled(true);
//			} else {// 非家庭安防不能修改,也不显示加载wifi动画
//					// 可选取无线网络进行配置
//				wifiSelect.setVisibility(View.GONE);
//				wifiListView.setVisibility(View.GONE);
//				wifiListBG.setVisibility(View.GONE);
//				loadingWifi.setVisibility(View.GONE);
//				wifiName.setEnabled(false);
//				wifiPwd.setEnabled(false);
//			}
//		} else if (null != BaseApp.settingMap
//				&& BaseApp.settingMap.get("ACTIVED").equalsIgnoreCase("2")) {// 无线
//			wifiDetail.setVisibility(View.VISIBLE);
//			wifiSelect.setVisibility(View.GONE);
//			loadingWifi.setVisibility(View.GONE);
//			wifiListView.setVisibility(View.GONE);
//			wifiListBG.setVisibility(View.GONE);
//			currIndex = 1;
//		}
//		tabMenuList.get(currIndex).setBackgroundDrawable(
//				getResources().getDrawable(R.drawable.tab_menu_hover));
//		tabMenuList.get(currIndex).setTextColor(Color.WHITE);
//		mPager.setCurrentItem(currIndex);
//		JVSUDT.DEVICE_MANAGE_FLAG = true;
//		MyLog.e("tags", "-------------------------------------------------");
//		// 获取主控码流信息请求
//		JVSUDT.JVC_SendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
//				JVNetConst.JVN_STREAM_INFO);
//
//		// 值为2双码流是家庭安防产品
//		if (null != BaseApp.settingMap.get("MobileCH")
//				&& "2".equalsIgnoreCase(BaseApp.settingMap.get("MobileCH"))) {// 家庭安防
//			// 获取主控配置信息成功后，才发其他请求
//			if (null != BaseApp.settingMap
//					&& BaseApp.settingMap.get("ACTIVED").equalsIgnoreCase("2")) {// 无线状态不能发
//																					// 无线请求
//
//			} else {// 有线时发送
//					// 获取主控AP信息请求
//				JVSUDT.JVC_SendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA,
//						8, JVNetConst.JVN_WIFI_INFO);
//			}
//		}
//
//	}
//
//	/**
//	 * 初始化头标
//	 */
//	private void initViews() {
//		back = (Button) findViewById(R.id.back);
//		currentMenu = (TextView) findViewById(R.id.currentmenu);
//		currentMenu.setText(R.string.str_remote_setting);
//		back.setOnClickListener(onClickListener);
//		saveChange = (Button) findViewById(R.id.add_device);
//		saveChange.setTextColor(Color.WHITE);
//		saveChange.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.setting_save));
//		saveChange.setVisibility(View.VISIBLE);
//		saveChange.setText(getResources().getString(R.string.login_str_save));
//		saveChange.setOnClickListener(onClickListener);
//
//		t1 = (TextView) findViewById(R.id.text1);
//		t2 = (TextView) findViewById(R.id.text2);
//		t3 = (TextView) findViewById(R.id.text3);
//
//		mPager = (ViewPager) findViewById(R.id.vPager);
//		listViews = new ArrayList<View>();
//		LayoutInflater mInflater = getLayoutInflater();
//		listViews.add(mInflater.inflate(R.layout.remote_lay_1, null));
//		listViews.add(mInflater.inflate(R.layout.remote_lay_2, null));
//		listViews.add(mInflater.inflate(R.layout.remote_lay_3, null));
//		mPager.setAdapter(new MyPagerAdapter(listViews));
//		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
//
//		tabMenuList = new ArrayList<TextView>();
//		tabMenuList.add(t1);
//		tabMenuList.add(t2);
//		tabMenuList.add(t3);
//
//		for (int i = 0; i < tabMenuList.size(); i++) {
//			tabMenuList.get(i).setOnClickListener(new MyOnClickListener(i));
//		}
//
//		// 有线连接
//		firstLayout = (LinearLayout) listViews.get(0).findViewById(
//				R.id.firstlayout);
//		obtainAuto = (RelativeLayout) listViews.get(0).findViewById(
//				R.id.obtainauto);
//		obtainManu = (RelativeLayout) listViews.get(0).findViewById(
//				R.id.obtainmanu);
//		secondLayout = (LinearLayout) listViews.get(0).findViewById(
//				R.id.secondlayout);
//
//		autoImage = (ImageView) listViews.get(0).findViewById(R.id.autoselect);
//		manuImage = (ImageView) listViews.get(0).findViewById(R.id.manuselect);
//
//		// if(null!=BaseApp.settingMap &&
//		// BaseApp.settingMap.get("ACTIVED").equalsIgnoreCase("0")){//有线连接
//		if (null != BaseApp.settingMap
//				&& BaseApp.settingMap.get("bDHCP").equalsIgnoreCase("0")) {// 手动
//			autoImage.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.obtain_unselected_icon));
//			manuImage.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.obtain_selected_icon));
//			bdhcpTag = Integer.parseInt(BaseApp.settingMap.get("bDHCP"));
//		} else {// 自动
//			autoImage.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.obtain_selected_icon));
//			manuImage.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.obtain_unselected_icon));
//			bdhcpTag = Integer.parseInt(BaseApp.settingMap.get("bDHCP"));
//		}
//		// }
//
//		ipET = (EditText) listViews.get(0).findViewById(R.id.ipet);// IP地址
//		netmaskET = (EditText) listViews.get(0).findViewById(R.id.netmasket);// 子网掩码
//		gateWayET = (EditText) listViews.get(0).findViewById(R.id.gatewayet);// 默认网关
//		dnsET = (EditText) listViews.get(0).findViewById(R.id.dnset);// 域名服务器
//		macET = (EditText) listViews.get(0).findViewById(R.id.macet);// 网卡地址
//		cloudseeidET = (EditText) listViews.get(0).findViewById(
//				R.id.cloudseeidet);// 设备号
//		stateET = (EditText) listViews.get(0).findViewById(R.id.statuset);// 设备状态
//
//		// 网卡地址，云视通号，状态三个不能改
//		macET.setEnabled(false);
//		macET.setTextColor(getResources().getColor(R.color.userinfocolor));
//		cloudseeidET.setEnabled(false);
//		cloudseeidET.setTextColor(getResources()
//				.getColor(R.color.userinfocolor));
//		stateET.setEnabled(false);
//		stateET.setTextColor(getResources().getColor(R.color.userinfocolor));
//
//		if (null != BaseApp.settingMap
//				&& BaseApp.settingMap.get("bDHCP").equalsIgnoreCase("1")) {// 自动
//			ipET.setEnabled(false);
//			netmaskET.setEnabled(false);
//			gateWayET.setEnabled(false);
//			dnsET.setEnabled(false);
//
//			ipET.setTextColor(getResources().getColor(R.color.userinfocolor));
//			netmaskET.setTextColor(getResources().getColor(
//					R.color.userinfocolor));
//			gateWayET.setTextColor(getResources().getColor(
//					R.color.userinfocolor));
//			dnsET.setTextColor(getResources().getColor(R.color.userinfocolor));
//		}
//
//		if (null != BaseApp.settingMap) {
//			ipStr = BaseApp.settingMap.get("ETH_IP");// IP地址
//			netmaskStr = BaseApp.settingMap.get("ETH_NM");// 子网掩码
//			ipET.setText(ipStr);// IP地址
//			netmaskET.setText(netmaskStr);// 子网掩码
//
//			// 网关为空
//			if (null != BaseApp.settingMap
//					&& !"".equalsIgnoreCase(BaseApp.settingMap.get("ETH_GW"))) {
//				gateWayStr = BaseApp.settingMap.get("ETH_GW");
//			} else {
//				gateWayStr = "0.0.0.0";
//			}
//			gateWayET.setText(gateWayStr);// 默认网关
//
//			dnsStr = BaseApp.settingMap.get("ETH_DNS");
//			dnsET.setText(dnsStr);// 域名服务器
//
//			macET.setText(BaseApp.settingMap.get("ETH_MAC"));// 网卡地址
//			cloudseeidET.setText(deviceNum);// 设备号
//		}
//
//		if (null != BaseApp.settingMap
//				&& BaseApp.settingMap.get("YSTSTATUS").equalsIgnoreCase("1")) {// 在线
//			stateET.setText(getResources()
//					.getString(R.string.str_device_online));// 设备状态
//		} else {// 不在线
//			stateET.setText(getResources().getString(
//					R.string.str_device_offline));// 设备状态
//		}
//
//		obtainAuto.setOnClickListener(onClickListener);
//		obtainManu.setOnClickListener(onClickListener);
//
//		// 无线连接
//		wifiDetail = (Button) listViews.get(1).findViewById(R.id.wifidetail);
//		wifiFirstLayout = (LinearLayout) listViews.get(1).findViewById(
//				R.id.wififirstlayout);
//		wifiSecondLayout = (LinearLayout) listViews.get(1).findViewById(
//				R.id.wifisecondlayout);
//		wifiDetail.setOnClickListener(onClickListener);
//
//		wifiSelect = (LinearLayout) listViews.get(1).findViewById(
//				R.id.selectwifi);// wifi选择layout
//		loadingWifi = (ProgressBar) listViews.get(1).findViewById(
//				R.id.loadingwifi);// wifi loading;
//
//		wifiListView = (RefreshableListView) listViews.get(1).findViewById(
//				R.id.wifilistview); // wifi列表
//		wifiListBG = (LinearLayout) listViews.get(1).findViewById(
//				R.id.wifilistbg);// wifi列表背景
//
//		wifiipET = (EditText) listViews.get(1).findViewById(R.id.wifiipet);// IP地址
//		wifinetmaskET = (EditText) listViews.get(1).findViewById(
//				R.id.wifinetmasket);// 子网掩码
//		wifigateWayET = (EditText) listViews.get(1).findViewById(
//				R.id.wifigatewayet);// 默认网关
//		wifidnsET = (EditText) listViews.get(1).findViewById(R.id.wifidnset);// 域名服务器
//		wifimacET = (EditText) listViews.get(1).findViewById(R.id.wifimacet);// 网卡地址
//
//		wifiName = (EditText) listViews.get(1).findViewById(R.id.wifiname);// 无线网名
//		wifiPwd = (EditText) listViews.get(1).findViewById(R.id.wifipwd);// 无线网密码
//
//		if (null != BaseApp.settingMap
//				&& BaseApp.settingMap.get("ACTIVED").equalsIgnoreCase("2")) {// 无线连接
//			if (null != BaseApp.settingMap) {
//				wifiipET.setText(BaseApp.settingMap.get("WIFI_IP"));// IP地址
//				wifinetmaskET.setText(BaseApp.settingMap.get("WIFI_NM"));// 子网掩码
//				wifigateWayET.setText(BaseApp.settingMap.get("WIFI_GW"));// 默认网关
//				wifidnsET.setText(BaseApp.settingMap.get("WIFI_DNS"));// 域名服务器
//				wifimacET.setText(BaseApp.settingMap.get("WIFI_MAC"));// 网卡地址
//
//				wifiName.setText(BaseApp.settingMap.get("WIFI_ID"));
//				wifiPwd.setText(BaseApp.settingMap.get("WIFI_PW"));
//			}
//		}
//
//		// 码流质量
//		imageQualitySetting = (LinearLayout) listViews.get(2).findViewById(
//				R.id.imagequsetting);// 图片质量
//		clearSetting = (LinearLayout) listViews.get(2).findViewById(
//				R.id.clearsetting);// 帧率
//		fluentSetting = (LinearLayout) listViews.get(2).findViewById(
//				R.id.fluentsetting);// 码流质量
//
//		imageQuality = (TextView) listViews.get(2).findViewById(
//				R.id.image_quality);
//		imageClear = (TextView) listViews.get(2).findViewById(R.id.image_clear);
//		imageFluent = (TextView) listViews.get(2).findViewById(
//				R.id.image_fluent);
//
//		// imageQuality.setText(array[0]);
//		// imageClear.setText(BaseApp.array1[0]);;
//		// imageFluent.setText(BaseApp.array2[0]);;
//		//
//		// imageQualitySetting.setOnClickListener(onClickListener);
//		// clearSetting.setOnClickListener(onClickListener);
//		// fluentSetting.setOnClickListener(onClickListener);
//
//		wifiListView.setOnRefreshListener(new OnRefreshListener() {
//			@Override
//			public void onRefresh(RefreshableListView listView) {
//				// 获取主控AP信息请求
//				JVSUDT.JVC_SendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA,
//						8, JVNetConst.JVN_WIFI_INFO);
//			}
//
//		});
//	}
//
//	// wifi列表点击事件
//	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//
//			wifiIndex = arg2;
//			wifiAdapter.wifiIndex = wifiIndex;
//			wifiName.setText(BaseApp.wifiList.get(arg2).wifiUserName);
//			wifiAdapter.notifyDataSetChanged();
//		}
//
//	};
//
//	OnClickListener onClickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			switch (v.getId()) {
//			case R.id.add_device:
//				JVSUDT.DEVICE_MANAGE_FLAG = false;
//				JVSUDT.QUICK_SETTING_FLAG = false;
//				// BaseApp.isAppKilled = true;
//				// //完成
//				// if(saveChange.getText().toString().equalsIgnoreCase(getResources().getString(R.string.str_finish))){
//				//
//				// }else{//保存
//				if (0 == currIndex) {// 有线
//					// if(null == dialog){
//					// dialog = new
//					// ProgressDialog(JVRemoteSettingActivity.this);
//					// }
//					// dialog.setMessage(getResources().getString(R.string.str_editing_wifi));
//					// dialog.setCancelable(false);
//					// dialog.show();
//
//					// JVSUDT.DEVICE_MANAGE_FLAG = true;//用于判断修改ip后主控是否断开
//					// 非家庭安防且有ClientPower字段
//
//					if (null != BaseApp.settingMap
//							&& null != BaseApp.settingMap.get("ClientPower")
//							&& (JVNetConst.POWER_USER & Integer
//									.parseInt(BaseApp.settingMap
//											.get("ClientPower"))) > 0) {// 大于0是普通用户
//						Message msg = BaseApp.settingHandler.obtainMessage();
//						msg.what = JVConst.DEVICE_MAN_HOST_NO_POWER_EDIT;
//						BaseApp.settingHandler.sendMessage(msg);
//					} else {
//						// 设备处于STA模式，应该不能修改有线的IP地址
//						if (null != BaseApp.settingMap
//								&& BaseApp.settingMap.get("ACTIVED")
//										.equalsIgnoreCase("2")) {// 无线不允许改手动输入的ip，改了不起作用
//
//						} else {
//							int bdhcp = bdhcpTag;
//							int oldbdhcp = Integer.parseInt(BaseApp.settingMap
//									.get("bDHCP"));
//							String str1 = ipET.getText().toString();
//							String str2 = netmaskET.getText().toString();
//							String str3 = gateWayET.getText().toString();
//							String str4 = dnsET.getText().toString();
//							// 都一样根本没改
//							if (oldbdhcp == bdhcp
//									&& str1.equalsIgnoreCase(BaseApp.settingMap
//											.get("ETH_IP"))
//									&& str2.equalsIgnoreCase(BaseApp.settingMap
//											.get("ETH_NM"))
//									&& str3.equalsIgnoreCase(BaseApp.settingMap
//											.get("ETH_GW"))
//									&& str4.equalsIgnoreCase(BaseApp.settingMap
//											.get("ETH_DNS"))) {
//							} else {
//								JVSUDT.JVC_SetDHCP(1,
//										(byte) JVNetConst.JVN_RSP_TEXTDATA,
//										bdhcp, str1, str2, str3, str4);
//							}
//
//						}
//						Message msg = BaseApp.settingHandler.obtainMessage();
//						msg.what = JVConst.DEVICE_MAN_HOST_EDIT_WIRED_SUCC;
//						BaseApp.settingHandler.sendMessage(msg);
//					}
//				} else if (1 == currIndex) { // 无线
//					// 非家庭安防且有ClientPower字段
//					if (null != BaseApp.settingMap
//							&& null != BaseApp.settingMap.get("ClientPower")
//							&& (JVNetConst.POWER_USER & Integer
//									.parseInt(BaseApp.settingMap
//											.get("ClientPower"))) > 0) {// 大于0是普通用户
//						Message msg = BaseApp.settingHandler.obtainMessage();
//						msg.what = JVConst.DEVICE_MAN_HOST_NO_POWER_EDIT;
//						BaseApp.settingHandler.sendMessage(msg);
//					} else {
//						// 无线状态不能发请求
//						if (null != BaseApp.settingMap
//								&& BaseApp.settingMap.get("ACTIVED")
//										.equalsIgnoreCase("2")) {// 无线
//							// 断开视频连接
//							disConnectVideo();
//							try {
//								Thread.sleep(200);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							Message msg = BaseApp.settingHandler
//									.obtainMessage();
//							msg.what = JVConst.DEVICE_MAN_HOST_EDIT_WIRED_SUCC;
//							BaseApp.settingHandler.sendMessage(msg);
//						} else {
//
//							// 值为2双码流是家庭安防产品
//							if (null != BaseApp.settingMap
//									&& null != BaseApp.settingMap
//											.get("MobileCH")
//									&& "2".equalsIgnoreCase(BaseApp.settingMap
//											.get("MobileCH"))) {
//								String wifiname = wifiName.getText().toString();
//								String wifipwd = wifiPwd.getText().toString();
//
//								if ("".equalsIgnoreCase(wifiname)) {
//									BaseApp.showTextToast(
//											JVRemoteSettingActivity.this,
//											R.string.str_wifiname_not_null);
//								} else {
//									if (null == dialog) {
//										dialog = new ProgressDialog(
//												JVRemoteSettingActivity.this);
//									}
//									dialog.setMessage(getResources().getString(
//											R.string.str_editing_wifi));
//									dialog.setCancelable(false);
//									dialog.show();
//
//									String auth = "";
//									String enc = "";
//
//									if (null != BaseApp.wifiList
//											&& 0 != BaseApp.wifiList.size()
//											&& wifiIndex < BaseApp.wifiList
//													.size()) {
//										auth = String.valueOf(BaseApp.wifiList
//												.get(wifiIndex).wifiAuth);
//										enc = String.valueOf(BaseApp.wifiList
//												.get(wifiIndex).wifiEnc);
//									}
//
//									JVSUDT.DEVICE_MANAGE_FLAG = true;
//									JVSUDT.QUICK_SETTING_FLAG = false;
//									JVSUDT.JVC_SaveWifi(1,
//											(byte) JVNetConst.JVN_RSP_TEXTDATA,
//											wifiname, wifipwd, 2, 9, auth, enc);
//
//								}
//							} else {// 非家庭安防不能修改wifi
//								try {
//									Thread.sleep(200);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								Message msg = BaseApp.settingHandler
//										.obtainMessage();
//								msg.what = JVConst.DEVICE_MAN_HOST_EDIT_WIRED_SUCC;
//								BaseApp.settingHandler.sendMessage(msg);
//							}
//
//						}
//					}
//
//				} else if (2 == currIndex) { // 码流
//
//					int mobilech = 0;
//					int width = 0;
//					int height = 0;
//					String proTag = BaseApp.settingMap.get("MobileCH");
//
//					if ("2".equalsIgnoreCase(proTag)) {
//						mobilech = 2;
//						if (0 == arrayIndex0) {
//							width = 352;
//							height = 288;
//						} else {
//							width = 720;
//							height = 480;
//						}
//					} else {
//						mobilech = 3;
//						width = 352;
//						height = 288;
//					}
//					try {
//						// 还没获取到数据
//						if (imageFluent
//								.getText()
//								.toString()
//								.equalsIgnoreCase(
//										getResources().getString(
//												R.string.str_fluent))
//								|| imageClear
//										.getText()
//										.toString()
//										.equalsIgnoreCase(
//												getResources().getString(
//														R.string.str_clear))) {
//
//						} else {
//							int arg1 = Integer.parseInt(imageFluent.getText()
//									.toString());// nMBPH
//							int arg2 = Integer.parseInt(imageClear.getText()
//									.toString());// framerate
//							JVSUDT.JVC_SetStreams(1,
//									(byte) JVNetConst.JVN_RSP_TEXTDATA,
//									mobilech, width, height, arg1, arg2);
//						}
//						try {
//							Thread.sleep(200);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					Message msg = BaseApp.settingHandler.obtainMessage();
//					msg.what = JVConst.DEVICE_MAN_HOST_EDIT_WIRED_SUCC;
//					BaseApp.settingHandler.sendMessage(msg);
//				}
//
//				// }
//
//				break;
//			case R.id.back:
//				// saveChange.setText(getResources().getString(R.string.str_finish));
//				goToBack();
//				break;
//			case R.id.obtainauto:// 自动获取
//				bdhcpTag = 1;// 1为自动获取
//				ipStr = ipET.getText().toString();// IP地址
//				netmaskStr = netmaskET.getText().toString();// 子网掩码
//				gateWayStr = gateWayET.getText().toString();// 默认网关
//				dnsStr = dnsET.getText().toString();// 域名服务器
//
//				ipET.setEnabled(false);
//				netmaskET.setEnabled(false);
//				gateWayET.setEnabled(false);
//				dnsET.setEnabled(false);
//
//				ipET.setTextColor(getResources()
//						.getColor(R.color.userinfocolor));
//				netmaskET.setTextColor(getResources().getColor(
//						R.color.userinfocolor));
//				gateWayET.setTextColor(getResources().getColor(
//						R.color.userinfocolor));
//				dnsET.setTextColor(getResources().getColor(
//						R.color.userinfocolor));
//
//				if (null != BaseApp.settingMap) {
//					ipET.setText(BaseApp.settingMap.get("ETH_IP"));// IP地址
//					netmaskET.setText(BaseApp.settingMap.get("ETH_NM"));// 子网掩码
//
//					// 网关为空
//					if (null != BaseApp.settingMap
//							&& !"".equalsIgnoreCase(BaseApp.settingMap
//									.get("ETH_GW"))) {
//						gateWayET.setText(BaseApp.settingMap.get("ETH_GW"));// 默认网关
//					} else {
//						gateWayET.setText("0.0.0.0");// 默认网关
//					}
//
//					dnsET.setText(BaseApp.settingMap.get("ETH_DNS"));// 域名服务器
//					macET.setText(BaseApp.settingMap.get("ETH_MAC"));// 网卡地址
//					cloudseeidET.setText(deviceNum);// 设备号
//				}
//
//				autoImage.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.obtain_selected_icon));
//				manuImage.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.obtain_unselected_icon));
//
//				currentMenu.setText(R.string.str_obtain_automatically);
//				secondLayout.setVisibility(View.VISIBLE);
//				firstLayout.setVisibility(View.GONE);
//				break;
//			case R.id.obtainmanu:// 手动输入
//				bdhcpTag = 0;// 0为手动输入
//				ipET.setText(ipStr);// IP地址
//				netmaskET.setText(netmaskStr);// 子网掩码
//				gateWayET.setText(gateWayStr);// 默认网关
//				dnsET.setText(dnsStr);// 域名服务器
//
//				ipET.setEnabled(true);
//				netmaskET.setEnabled(true);
//				gateWayET.setEnabled(true);
//				dnsET.setEnabled(true);
//
//				ipET.setTextColor(getResources().getColor(R.color.black));
//				netmaskET.setTextColor(getResources().getColor(R.color.black));
//				gateWayET.setTextColor(getResources().getColor(R.color.black));
//				dnsET.setTextColor(getResources().getColor(R.color.black));
//
//				autoImage.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.obtain_unselected_icon));
//				manuImage.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.obtain_selected_icon));
//
//				currentMenu.setText(R.string.str_input_manually);
//				secondLayout.setVisibility(View.VISIBLE);
//				firstLayout.setVisibility(View.GONE);
//				break;
//
//			case R.id.wifidetail:
//				currentMenu.setText(R.string.str_wifi_info);
//				wifiSecondLayout.setVisibility(View.VISIBLE);
//				wifiFirstLayout.setVisibility(View.GONE);
//				break;
//			case R.id.imagequsetting:
//				new AlertDialog.Builder(JVRemoteSettingActivity.this)
//						.setTitle(
//								getResources().getString(
//										R.string.str_image_quality))
//						.setSingleChoiceItems(BaseApp.array, arrayIndex0,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										imageQuality
//												.setText(BaseApp.array[which]);
//										arrayIndex0 = which;
//										dialog.dismiss();
//									}
//								})
//						.setNegativeButton(
//								getResources().getString(R.string.str_cancel),
//								null).show();
//				break;
//			case R.id.clearsetting:
//
//				new AlertDialog.Builder(JVRemoteSettingActivity.this)
//						.setTitle(getResources().getString(R.string.str_clear))
//						.setSingleChoiceItems(BaseApp.array1, arrayIndex1,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										imageClear
//												.setText(BaseApp.array1[which]);
//										arrayIndex1 = which;
//										dialog.dismiss();
//									}
//								})
//						.setNegativeButton(
//								getResources().getString(R.string.str_cancel),
//								null).show();
//				break;
//			case R.id.fluentsetting:
//				new AlertDialog.Builder(JVRemoteSettingActivity.this)
//						.setTitle(getResources().getString(R.string.str_fluent))
//						.setSingleChoiceItems(BaseApp.array2, arrayIndex2,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										imageFluent
//												.setText(BaseApp.array2[which]);
//										arrayIndex2 = which;
//										dialog.dismiss();
//									}
//								})
//						.setNegativeButton(
//								getResources().getString(R.string.str_cancel),
//								null).show();
//				break;
//			}
//		}
//
//	};
//
//	// 返回事件
//	public void goToBack() {
//		if (currIndex == 0) {// 有线连接
//			if (secondLayout.getVisibility() == View.VISIBLE) {
//				currentMenu.setText(R.string.str_remote_setting);
//				secondLayout.setVisibility(View.GONE);
//				firstLayout.setVisibility(View.VISIBLE);
//			} else {
//				JVRemoteSettingActivity.this.finish();
//			}
//		} else if (currIndex == 1) {// 无线连接
//			if (wifiSecondLayout.getVisibility() == View.VISIBLE) {
//				wifiSecondLayout.setVisibility(View.GONE);
//				wifiFirstLayout.setVisibility(View.VISIBLE);
//			} else {
//				JVRemoteSettingActivity.this.finish();
//			}
//		} else {// 码流设置
//			JVRemoteSettingActivity.this.finish();
//		}
//	}
//
//	/**
//	 * ViewPager适配器
//	 */
//	public class MyPagerAdapter extends PagerAdapter {
//		public List<View> mListViews;
//
//		public MyPagerAdapter(List<View> mListViews) {
//			this.mListViews = mListViews;
//		}
//
//		@Override
//		public void destroyItem(View arg0, int arg1, Object arg2) {
//			((ViewPager) arg0).removeView(mListViews.get(arg1));
//		}
//
//		@Override
//		public void finishUpdate(View arg0) {
//		}
//
//		@Override
//		public int getCount() {
//			return mListViews.size();
//		}
//
//		@Override
//		public Object instantiateItem(View arg0, int arg1) {
//			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
//			return mListViews.get(arg1);
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return arg0 == (arg1);
//		}
//
//		@Override
//		public void restoreState(Parcelable arg0, ClassLoader arg1) {
//		}
//
//		@Override
//		public Parcelable saveState() {
//			return null;
//		}
//
//		@Override
//		public void startUpdate(View arg0) {
//		}
//	}
//
//	/**
//	 * 头标点击监听
//	 */
//	public class MyOnClickListener implements View.OnClickListener {
//		private int index = 0;
//
//		public MyOnClickListener(int i) {
//			index = i;
//		}
//
//		@Override
//		public void onClick(View v) {
//
//			// 有线连接
//			if (index == 0) {
//				if (secondLayout.getVisibility() == View.VISIBLE) {
//					secondLayout.setVisibility(View.GONE);
//				}
//				firstLayout.setVisibility(View.VISIBLE);
//				// wifiFirstLayout.setVisibility(View.GONE);
//			} else if (index == 1) {// 无线连接
//				if (wifiSecondLayout.getVisibility() == View.VISIBLE) {
//					wifiSecondLayout.setVisibility(View.GONE);
//				}
//				// firstLayout.setVisibility(View.GONE);
//				wifiFirstLayout.setVisibility(View.VISIBLE);
//			}
//			for (int i = 0; i < tabMenuList.size(); i++) {
//				if (i == index) {
//					tabMenuList.get(index).setBackgroundDrawable(
//							getResources().getDrawable(
//									R.drawable.tab_menu_hover));
//					tabMenuList.get(index).setTextColor(Color.WHITE);
//				} else {
//					tabMenuList.get(i).setBackgroundDrawable(null);
//					tabMenuList.get(i)
//							.setTextColor(
//									getResources().getColor(
//											R.color.setting_text_color));
//				}
//			}
//
//			mPager.setCurrentItem(index);
//			currentMenu.setText(R.string.str_remote_setting);
//			// saveChange.setText(getResources().getString(R.string.str_finish));
//		}
//	};
//
//	/**
//	 * 页卡切换监听
//	 */
//	public class MyOnPageChangeListener implements OnPageChangeListener {
//
//		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
//		int two = one * 2;// 页卡1 -> 页卡3 偏移量
//
//		@Override
//		public void onPageSelected(int arg0) {
//			Animation animation = null;
//			switch (arg0) {
//			case 0:
//				if (currIndex == 1) {
//					animation = new TranslateAnimation(one, 0, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, 0, 0, 0);
//				}
//				break;
//			case 1:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, one, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, one, 0, 0);
//				}
//				break;
//			case 2:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, two, 0, 0);
//				} else if (currIndex == 1) {
//					animation = new TranslateAnimation(one, two, 0, 0);
//				}
//				break;
//			}
//			currIndex = arg0;
//			for (int i = 0; i < tabMenuList.size(); i++) {
//				if (i == currIndex) {
//					tabMenuList.get(currIndex).setBackgroundDrawable(
//							getResources().getDrawable(
//									R.drawable.tab_menu_hover));
//					tabMenuList.get(currIndex).setTextColor(Color.WHITE);
//				} else {
//					tabMenuList.get(i).setBackgroundDrawable(null);
//					tabMenuList.get(i)
//							.setTextColor(
//									getResources().getColor(
//											R.color.setting_text_color));
//				}
//			}
//
//			currentMenu.setText(R.string.str_remote_setting);
//			// 有线连接
//			if (currIndex == 0) {
//				if (secondLayout.getVisibility() == View.VISIBLE) {
//					secondLayout.setVisibility(View.GONE);
//				}
//				firstLayout.setVisibility(View.VISIBLE);
//			} else if (currIndex == 1) {// 无线连接
//				if (wifiSecondLayout.getVisibility() == View.VISIBLE) {
//					wifiSecondLayout.setVisibility(View.GONE);
//				}
//				wifiFirstLayout.setVisibility(View.VISIBLE);
//			}
//
//			if (null != animation) {
//				animation.setFillAfter(true);// True:图片停在动画结束位置
//				animation.setDuration(300);
//			}
//		}
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//		}
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		// 断开视频连接
//		disConnectVideo();
//		if (null != LoginUtil.activityList
//				&& 0 != LoginUtil.activityList.size()) {
//			LoginUtil.activityList.remove(JVRemoteSettingActivity.this);
//		}
//
//		if (null != BaseApp.wifiList) {
//			BaseApp.wifiList.clear();
//		}
//
//		if (null != BaseApp.settingMap) {
//			BaseApp.settingMap.clear();
//		}
//		BaseApp.settingMap = null;
//		super.onDestroy();
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {// 返回按钮
//			goToBack();
//			return true;
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}
//
//	public static class SettingHandler extends Handler {
//
//		private final WeakReference<JVRemoteSettingActivity> mActivity;
//
//		public SettingHandler(JVRemoteSettingActivity activity) {
//			mActivity = new WeakReference<JVRemoteSettingActivity>(activity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			JVRemoteSettingActivity activity = mActivity.get();
//			if (null != activity && !activity.isFinishing()) {
//				switch (msg.what) {
//				case JVConst.DEVICE_SETTING_WIFI_DET:
//					if (msg.arg1 == 0) {
//						if (null != BaseApp.settingMap) {
//							activity.wifiipET.setText("");// IP地址
//							activity.wifinetmaskET.setText("");// 子网掩码
//							activity.wifigateWayET.setText("");// 默认网关
//							activity.wifidnsET.setText("");// 域名服务器
//							activity.wifimacET.setText("");// 网卡地址
//						}
//					} else {
//						if (null != BaseApp.settingMap) {
//							activity.wifiipET.setText(BaseApp.settingMap
//									.get("WIFI_IP"));// IP地址
//							activity.wifinetmaskET.setText(BaseApp.settingMap
//									.get("WIFI_NM"));// 子网掩码
//							activity.wifigateWayET.setText(BaseApp.settingMap
//									.get("WIFI_GW"));// 默认网关
//							activity.wifidnsET.setText(BaseApp.settingMap
//									.get("WIFI_DNS"));// 域名服务器
//							activity.wifimacET.setText(BaseApp.settingMap
//									.get("WIFI_MAC"));// 网卡地址
//						}
//					}
//					activity.currentMenu.setText(R.string.str_wifi_info);
//					activity.wifiSecondLayout.setVisibility(View.VISIBLE);
//					activity.wifiFirstLayout.setVisibility(View.GONE);
//
//					break;
//				case JVConst.DEVICE_MAN_HOST_WIFI_TEXT:// 获取到WIFI配置文本数据
//					activity.wifiListView.completeRefreshing();
//					activity.loadingWifi.setVisibility(View.GONE);
//
//					activity.wifiAdapter = new WifiAdapter(activity);
//					activity.wifiAdapter.setData1(BaseApp.wifiList, 1, true);
//					activity.wifiListView.setAdapter(activity.wifiAdapter);
//					activity.wifiListView
//							.setOnItemClickListener(activity.mOnItemClickListener);
//
//					// //获取主控码流信息请求
//					// JVSUDT.JVC_SendTextData(1,
//					// (byte)JVNetConst.JVN_RSP_TEXTDATA,
//					// 8,JVNetConst.JVN_STREAM_INFO);
//
//					break;
//				case JVConst.DEVICE_MAN_HOST_STREAM_TEXT:// 获取到码流配置数据
//					if (null != BaseApp.settingMap) {
//						// 值为2双码流是家庭安防产品
//						if (null != BaseApp.settingMap.get("MobileCH")
//								&& "2".equalsIgnoreCase(BaseApp.settingMap
//										.get("MobileCH"))) {
//							BaseApp.array = activity
//									.getResources()
//									.getStringArray(R.array.array_image_quality);
//							BaseApp.array1 = activity.getResources()
//									.getStringArray(R.array.array_clear);
//							BaseApp.array2 = activity.getResources()
//									.getStringArray(R.array.array_fluent);
//						} else {
//							BaseApp.array = activity.getResources()
//									.getStringArray(
//											R.array.array_image_quality_not);
//							BaseApp.array1 = activity.getResources()
//									.getStringArray(R.array.array_clear_not);
//							BaseApp.array2 = activity.getResources()
//									.getStringArray(R.array.array_fluent_not);
//						}
//
//						// 图像质量
//						if (null != BaseApp.settingMap.get("MobileCH")
//								&& "2".equalsIgnoreCase(BaseApp.settingMap
//										.get("MobileCH"))) {
//							if (Integer.parseInt(BaseApp.settingMap
//									.get("width")) == 720) {
//								activity.arrayIndex0 = 1;
//								activity.imageQuality
//										.setText(BaseApp.array[activity.arrayIndex0]);
//							} else if (Integer.parseInt(BaseApp.settingMap
//									.get("width")) == 352) {
//								activity.arrayIndex0 = 0;
//								activity.imageQuality
//										.setText(BaseApp.array[activity.arrayIndex0]);
//							}
//						} else {
//							activity.arrayIndex0 = 0;
//							activity.imageQuality
//									.setText(BaseApp.array[activity.arrayIndex0]);
//						}
//
//						// 码率
//						for (int i = 0; i < BaseApp.array1.length; i++) {
//							if (BaseApp.array1[i]
//									.equalsIgnoreCase(BaseApp.settingMap
//											.get("framerate"))) {
//								activity.arrayIndex1 = i;
//								activity.imageClear
//										.setText(BaseApp.array1[activity.arrayIndex1]);
//							}
//						}
//						if (Integer.parseInt(BaseApp.settingMap
//								.get("framerate")) < Integer
//								.parseInt(BaseApp.array1[0])) {
//							activity.arrayIndex1 = 0;
//							activity.imageClear
//									.setText(BaseApp.array1[activity.arrayIndex1]);
//						} else if (Integer.parseInt(BaseApp.settingMap
//								.get("framerate")) > Integer
//								.parseInt(BaseApp.array1[BaseApp.array1.length - 1])) {
//							activity.arrayIndex1 = BaseApp.array1.length - 1;
//							activity.imageClear
//									.setText(BaseApp.array1[activity.arrayIndex1]);
//						}
//
//						int temMBPH = Integer.parseInt(BaseApp.settingMap
//								.get("nMBPH"));
//						// 码流质量
//						for (int i = 1; i < BaseApp.array2.length; i++) {
//							// if(BaseApp.array2[i].equalsIgnoreCase(BaseApp.settingMap.get("nMBPH"))){
//							// arrayIndex2 = i;
//							// imageFluent.setText(BaseApp.array2[arrayIndex2]);
//							// }
//
//							if (temMBPH >= Integer
//									.parseInt(BaseApp.array2[i - 1])
//									&& temMBPH < Integer
//											.parseInt(BaseApp.array2[i])) {
//								activity.arrayIndex2 = i - 1;
//								activity.imageFluent
//										.setText(BaseApp.array2[activity.arrayIndex2]);
//							}
//
//							// 最后一个
//							if (i == BaseApp.array2.length - 1
//									&& temMBPH >= Integer
//											.parseInt(BaseApp.array2[i])) {
//								activity.arrayIndex2 = BaseApp.array2.length - 1;
//								activity.imageFluent
//										.setText(BaseApp.array2[activity.arrayIndex2]);
//							}
//
//						}
//
//						if (Integer.parseInt(BaseApp.settingMap.get("nMBPH")) < Integer
//								.parseInt(BaseApp.array2[0])) {
//							activity.arrayIndex2 = 0;
//							activity.imageFluent
//									.setText(BaseApp.array2[activity.arrayIndex2]);
//						} else if (Integer.parseInt(BaseApp.settingMap
//								.get("nMBPH")) > Integer
//								.parseInt(BaseApp.array2[BaseApp.array2.length - 1])) {
//							activity.arrayIndex2 = BaseApp.array2.length - 1;
//							activity.imageFluent
//									.setText(BaseApp.array2[activity.arrayIndex2]);
//						}
//
//					}
//
//					activity.imageQualitySetting
//							.setOnClickListener(activity.onClickListener);
//					activity.clearSetting
//							.setOnClickListener(activity.onClickListener);
//					activity.fluentSetting
//							.setOnClickListener(activity.onClickListener);
//					break;
//
//				case JVConst.DEVICE_MAN_HOST_CONNECT_WIFI_SUCC:// 主控wifi连接成功
//					if (null != activity.dialog && activity.dialog.isShowing()) {
//						activity.dialog.dismiss();
//					}
//					activity.dialog = null;
//
//					if (-1 != activity.deviceIndex) {
//						BaseApp.deviceList.get(activity.deviceIndex).deviceLocalIp = activity.ipET
//								.getText().toString();
//					}
//					JVSUDT.DEVICE_MANAGE_FLAG = false;
//					BaseApp.showTextToast(activity, R.string.str_connect_succ);
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					activity.finish();
//					break;
//				case JVConst.DEVICE_MAN_HOST_CONNECT_WIFI_FAIL:// 主控wifi连接失败
//
//					if (null != activity.dialog && activity.dialog.isShowing()) {
//						activity.dialog.dismiss();
//					}
//					activity.dialog = null;
//
//					BaseApp.showTextToast(activity, R.string.str_connect_fail);
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					activity.finish();
//					break;
//				case JVConst.DEVICE_MAN_HOST_EDIT_WIRED_SUCC:// 主控修改有线信息成功
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					activity.finish();
//					break;
//				case JVConst.DEVICE_MAN_HOST_NO_POWER_EDIT:// 没有权限修改网络信息
//					BaseApp.showTextToast(activity, R.string.str_no_permission);
//					break;
//				case JVConst.DEVICE_MAN_HOST_EDIT_WIRED_FAIL:// 主控修改有线信息失败
//
//					if (null != activity.dialog && activity.dialog.isShowing()) {
//						activity.dialog.dismiss();
//					}
//					activity.dialog = null;
//
//					BaseApp.showTextToast(activity, R.string.str_save_fail);
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					activity.finish();
//					break;
//				}
//
//			}
//		}
//
//	}
//
//	// 断开视频连接
//	public void disConnectVideo() {
//		if (null != BaseApp.channelMap && BaseApp.channelMap.size() > 0) {
//			BaseApp.channelMap.get(0).stopPrimaryChannel(0);
//		}
//	}
// }
