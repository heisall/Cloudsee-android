package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.WifiAdapter;
import com.jovision.bean.Device;
import com.jovision.bean.Wifi;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.RefreshableListView;
import com.jovision.views.RefreshableListView.OnRefreshListener;

public class JVRemoteSettingActivity extends BaseActivity {

	private static final String TAG = "JVRemoteSettingActivity";

	private HashMap<String, String> settingMap = new HashMap<String, String>();
	private HashMap<String, String> allStreamMap = new HashMap<String, String>();
	private String[] array = null;
	private String[] array1 = null;
	private String[] array2 = null;
	private ArrayList<Wifi> wifiList = new ArrayList<Wifi>();// wifi数据列表
	private int deviceIndex = 0;
	private ArrayList<Device> deviceList;
	private Device device;
	protected RelativeLayout.LayoutParams reParamstop2;
	/** bottom */
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	// private ImageView cursor;// 动画图片
	private TextView t1, t2;// 页卡头标, t3;
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ArrayList<TextView> tabMenuList = null;

	// 有线连接
	private LinearLayout firstLayout;
	private RelativeLayout obtainAuto;
	private ImageView autoImage;
	private RelativeLayout obtainManu;
	private ImageView manuImage;
	private LinearLayout secondLayout;
	private int bdhcpTag = 0;

	private EditText ipET;// IP地址
	private EditText netmaskET;// 子网掩码
	private EditText gateWayET;// 默认网关
	private EditText dnsET;// 域名服务器
	private EditText macET;// 网卡地址
	private EditText cloudseeidET;// 设备号
	private ImageView stateET;// 设备状态
	private TextView saveChange;// 保存

	private String ipStr = "";// IP地址
	private String netmaskStr = "";// 子网掩码
	private String gateWayStr = "";// 默认网关
	private String dnsStr = "";// 域名服务器

	// 无线连接
	private Button wifiDetail;
	private LinearLayout wifiFirstLayout;
	private LinearLayout wifiSecondLayout;

	private LinearLayout wifiSelect;// wifi选择layout
	private ProgressBar loadingWifi;// 进度条
	private RefreshableListView wifiListView; // wifi列表
	private LinearLayout wifiListBG;// wifi列表背景
	private WifiAdapter wifiAdapter;

	private EditText wifiipET;// IP地址
	private EditText wifinetmaskET;// 子网掩码
	private EditText wifigateWayET;// 默认网关
	private EditText wifidnsET;// 域名服务器
	private EditText wifimacET;// 网卡地址

	private EditText wifiName;// 无线网名
	private EditText wifiPwd;// 无线网密码

	// // 码流设置
	// private LinearLayout imageQualitySetting;// 图片质量
	// private LinearLayout clearSetting;// 帧率
	// private LinearLayout fluentSetting;// 码流质量
	// private TextView imageQuality;
	// private TextView imageClear;
	// private TextView imageFluent;
	//
	// private int arrayIndex0;
	// private int arrayIndex1;
	// private int arrayIndex2;

	private int wifiIndex = -1;

	private boolean disConnected = false;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {

		case Consts.CALL_CONNECT_CHANGE:
			switch (arg2) {
			case Consts.BAD_NOT_CONNECT: {
				dismissDialog();
				JVRemoteSettingActivity.this.finish();
				break;
			}
			}
		case Consts.CALL_TEXT_DATA: {// 文本回调
			MyLog.i(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
					+ ", " + obj);
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				String allStr = obj.toString();
				MyLog.i("AP,WIFI热点请求--", "AP,WIFI热点请求--" + allStr);
				try {
					JSONObject dataObj = new JSONObject(allStr);
					switch (dataObj.getInt("flag")) {
					case JVNetConst.JVN_WIFI_INFO:// 2-- AP,WIFI热点请求
						MyLog.i("AP,WIFI热点请求--",
								"AP,WIFI热点请求--" + obj.toString());
						if (null != allStr && !allStr.equalsIgnoreCase("")) {
							String wifiStrArray = dataObj.optString("wifi");
							wifiList = ConfigUtil.genWifiList(wifiStrArray);
						}

						wifiListView.completeRefreshing();
						loadingWifi.setVisibility(View.GONE);

						wifiAdapter = new WifiAdapter(
								JVRemoteSettingActivity.this);
						wifiAdapter.setData1(wifiList, 1, true);
						wifiListView.setAdapter(wifiAdapter);
						wifiListView
								.setOnItemClickListener(mOnItemClickListener);
						break;
					case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求
						MyLog.i(TAG, "码流配置请求--" + obj.toString());
						// setStreamData(allStr);

						// 值为2双码流是家庭安防产品
						if (null != settingMap.get("MobileCH")
								&& "2".equalsIgnoreCase(settingMap
										.get("MobileCH"))) {// 家庭安防
							// 获取主控配置信息成功后，才发其他请求
							if (null != settingMap
									&& settingMap.get("ACTIVED")
											.equalsIgnoreCase("2")) {// 无线状态不能发
																		// 无线请求

							} else {// 有线时发送
									// 获取主控AP信息请求
								Jni.sendTextData(1,
										(byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
										JVNetConst.JVN_WIFI_INFO);
							}
						}
						break;
					case JVNetConst.EX_WIFI_AP_CONFIG:// 11 ---新wifi配置流程
						break;
					case JVNetConst.JVN_WIFI_SETTING_SUCCESS:// 4-- wifi配置成功
						break;
					case JVNetConst.JVN_WIFI_SETTING_FAILED:// 5--WIFI配置失败
						break;
					case JVNetConst.JVN_WIFI_IS_SETTING:// -- 6 正在配置wifi

						break;

					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}

			break;
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.CALL_CONNECT_CHANGE: {
			switch (arg2) {
			// 2 -- 断开连接成功
			case JVNetConst.DISCONNECT_OK:
				// 4 -- 连接失败
			case JVNetConst.CONNECT_FAILED:
				// 6 -- 连接异常断开
			case JVNetConst.ABNORMAL_DISCONNECT:
				// 7 -- 服务停止连接，连接断开
			case JVNetConst.SERVICE_STOP:
				break;
			case Consts.BAD_NOT_CONNECT: {
				disConnected = true;
				MyLog.e(TAG, "线程断开成功");
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
				break;
			}
			}

		}
		case Consts.CALL_TEXT_DATA:
			handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
			break;
		}

	}

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		String jsonStr = intent.getStringExtra("SettingJSON");
		settingMap = ConfigUtil.genMsgMap(jsonStr);
		if (null == intent.getStringExtra("Device")) {
			this.finish();
			return;
		}
		deviceIndex = intent.getIntExtra("DeviceIndex", 0);
		deviceList = CacheUtil.getDevList();
		device = deviceList.get(deviceIndex);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.remotesetting_layout);
		if (null == settingMap) {
			this.finish();
			return;
		}

		/** top bar */
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		rightBtn = (Button) findViewById(R.id.btn_right);
		currentMenu.setText(R.string.str_help1_0);
		leftBtn.setOnClickListener(mOnClickListener);
		rightBtn.setVisibility(View.VISIBLE);
		reParamstop2 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		reParamstop2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		reParamstop2.addRule(RelativeLayout.CENTER_VERTICAL);
		reParamstop2.setMargins(0, 0, 30, 0);
		rightBtn.setLayoutParams(reParamstop2);

		rightBtn.setTextColor(Color.WHITE);
		rightBtn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_save));
		rightBtn.setText(getResources().getString(R.string.login_str_save));
		rightBtn.setOnClickListener(mOnClickListener);

		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		// t3 = (TextView) findViewById(R.id.text3);

		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.remote_lay_1, null));
		listViews.add(mInflater.inflate(R.layout.remote_lay_2, null));
		// listViews.add(mInflater.inflate(R.layout.remote_lay_3, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		tabMenuList = new ArrayList<TextView>();
		tabMenuList.add(t1);
		tabMenuList.add(t2);
		// tabMenuList.add(t3);

		for (int i = 0; i < tabMenuList.size(); i++) {
			tabMenuList.get(i).setOnClickListener(new MyOnClickListener(i));
		}

		// 有线连接
		firstLayout = (LinearLayout) listViews.get(0).findViewById(
				R.id.firstlayout);
		obtainAuto = (RelativeLayout) listViews.get(0).findViewById(
				R.id.obtainauto);
		obtainManu = (RelativeLayout) listViews.get(0).findViewById(
				R.id.obtainmanu);
		secondLayout = (LinearLayout) listViews.get(0).findViewById(
				R.id.secondlayout);

		autoImage = (ImageView) listViews.get(0).findViewById(R.id.autoselect);
		manuImage = (ImageView) listViews.get(0).findViewById(R.id.manuselect);

		if (null != settingMap && settingMap.get("bDHCP").equalsIgnoreCase("0")) {// 手动
			autoImage.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.morefragment_normal_icon));
			manuImage.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.morefragment_selector_icon));
			obtainAuto.setBackgroundResource(R.drawable.obtain_bg);
			obtainManu.setBackgroundResource(R.drawable.obtain_select_bg);
			bdhcpTag = Integer.parseInt(settingMap.get("bDHCP"));
		} else {// 自动
			autoImage.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.morefragment_selector_icon));
			manuImage.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.morefragment_normal_icon));
			obtainAuto.setBackgroundResource(R.drawable.obtain_select_bg);
			obtainManu.setBackgroundResource(R.drawable.obtain_bg);
			bdhcpTag = Integer.parseInt(settingMap.get("bDHCP"));
		}

		ipET = (EditText) listViews.get(0).findViewById(R.id.ipet);// IP地址
		netmaskET = (EditText) listViews.get(0).findViewById(R.id.netmasket);// 子网掩码
		gateWayET = (EditText) listViews.get(0).findViewById(R.id.gatewayet);// 默认网关
		dnsET = (EditText) listViews.get(0).findViewById(R.id.dnset);// 域名服务器
		macET = (EditText) listViews.get(0).findViewById(R.id.macet);// 网卡地址
		cloudseeidET = (EditText) listViews.get(0).findViewById(
				R.id.cloudseeidet);// 设备号
		stateET = (ImageView) listViews.get(0).findViewById(R.id.statuset);// 设备状态
		saveChange = (TextView) listViews.get(0).findViewById(R.id.saveChange);
		saveChange.setOnClickListener(mOnClickListener);

		// 网卡地址，云视通号，状态三个不能改
		macET.setEnabled(false);
		macET.setTextColor(getResources().getColor(R.color.userinfocolor));
		cloudseeidET.setEnabled(false);
		cloudseeidET.setTextColor(getResources()
				.getColor(R.color.userinfocolor));
		stateET.setBackgroundResource(R.drawable.state_offline);

		if (null != settingMap && settingMap.get("bDHCP").equalsIgnoreCase("1")) {// 自动
			ipET.setEnabled(false);
			netmaskET.setEnabled(false);
			gateWayET.setEnabled(false);
			dnsET.setEnabled(false);

			ipET.setTextColor(getResources().getColor(R.color.userinfocolor));
			netmaskET.setTextColor(getResources().getColor(
					R.color.userinfocolor));
			gateWayET.setTextColor(getResources().getColor(
					R.color.userinfocolor));
			dnsET.setTextColor(getResources().getColor(R.color.userinfocolor));
		}

		if (null != settingMap) {
			ipStr = settingMap.get("ETH_IP");// IP地址
			netmaskStr = settingMap.get("ETH_NM");// 子网掩码
			ipET.setText(ipStr);// IP地址
			netmaskET.setText(netmaskStr);// 子网掩码

			// 网关为空
			if (null != settingMap
					&& !"".equalsIgnoreCase(settingMap.get("ETH_GW"))) {
				gateWayStr = settingMap.get("ETH_GW");
			} else {
				gateWayStr = "0.0.0.0";
			}
			gateWayET.setText(gateWayStr);// 默认网关

			dnsStr = settingMap.get("ETH_DNS");
			dnsET.setText(dnsStr);// 域名服务器

			macET.setText(settingMap.get("ETH_MAC"));// 网卡地址
			cloudseeidET.setText(device.getFullNo());// 设备号
		}

		if (null != settingMap
				&& settingMap.get("YSTSTATUS").equalsIgnoreCase("1")) {// 在线
			stateET.setBackgroundResource(R.drawable.state_online);// 设备状态
		} else {// 不在线
			stateET.setBackgroundResource(R.drawable.state_offline);// 设备状态
		}

		obtainAuto.setOnClickListener(mOnClickListener);
		obtainManu.setOnClickListener(mOnClickListener);

		// 无线连接
		wifiDetail = (Button) listViews.get(1).findViewById(R.id.wifidetail);
		wifiFirstLayout = (LinearLayout) listViews.get(1).findViewById(
				R.id.wififirstlayout);
		wifiSecondLayout = (LinearLayout) listViews.get(1).findViewById(
				R.id.wifisecondlayout);
		wifiDetail.setOnClickListener(mOnClickListener);

		wifiSelect = (LinearLayout) listViews.get(1).findViewById(
				R.id.selectwifi);// wifi选择layout
		loadingWifi = (ProgressBar) listViews.get(1).findViewById(
				R.id.loadingwifi);// wifi loading;

		wifiListView = (RefreshableListView) listViews.get(1).findViewById(
				R.id.wifilistview); // wifi列表
		wifiListBG = (LinearLayout) listViews.get(1).findViewById(
				R.id.wifilistbg);// wifi列表背景

		wifiipET = (EditText) listViews.get(1).findViewById(R.id.wifiipet);// IP地址
		wifinetmaskET = (EditText) listViews.get(1).findViewById(
				R.id.wifinetmasket);// 子网掩码
		wifigateWayET = (EditText) listViews.get(1).findViewById(
				R.id.wifigatewayet);// 默认网关
		wifidnsET = (EditText) listViews.get(1).findViewById(R.id.wifidnset);// 域名服务器
		wifimacET = (EditText) listViews.get(1).findViewById(R.id.wifimacet);// 网卡地址

		wifiName = (EditText) listViews.get(1).findViewById(R.id.wifiname);// 无线网名
		wifiPwd = (EditText) listViews.get(1).findViewById(R.id.wifipwd);// 无线网密码

		if (null != settingMap
				&& settingMap.get("ACTIVED").equalsIgnoreCase("2")) {// 无线连接
			if (null != settingMap) {
				wifiipET.setText(settingMap.get("WIFI_IP"));// IP地址
				wifinetmaskET.setText(settingMap.get("WIFI_NM"));// 子网掩码
				wifigateWayET.setText(settingMap.get("WIFI_GW"));// 默认网关
				wifidnsET.setText(settingMap.get("WIFI_DNS"));// 域名服务器
				wifimacET.setText(settingMap.get("WIFI_MAC"));// 网卡地址

				wifiName.setText(settingMap.get("WIFI_ID"));
				wifiPwd.setText(settingMap.get("WIFI_PW"));
			}
		}

		// // 码流质量
		// imageQualitySetting = (LinearLayout) listViews.get(2).findViewById(
		// R.id.imagequsetting);// 图片质量
		// clearSetting = (LinearLayout) listViews.get(2).findViewById(
		// R.id.clearsetting);// 帧率
		// fluentSetting = (LinearLayout) listViews.get(2).findViewById(
		// R.id.fluentsetting);// 码流质量
		//
		// imageQuality = (TextView) listViews.get(2).findViewById(
		// R.id.image_quality);
		// imageClear = (TextView)
		// listViews.get(2).findViewById(R.id.image_clear);
		// imageFluent = (TextView) listViews.get(2).findViewById(
		// R.id.image_fluent);

		wifiListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshableListView listView) {
				// 获取主控AP信息请求
				Jni.sendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
						JVNetConst.JVN_WIFI_INFO);
			}

		});

		if (null != settingMap
				&& settingMap.get("ACTIVED").equalsIgnoreCase("0")) {// 有线
			currIndex = 0;

			// 值为2双码流是家庭安防产品，显示加载wifi动画
			// if (null != settingMap.get("MobileCH")
			// && "2".equalsIgnoreCase(settingMap.get("MobileCH"))) {
			// 可选取无线网络进行配置
			wifiSelect.setVisibility(View.VISIBLE);
			wifiListView.setVisibility(View.VISIBLE);
			wifiListBG.setVisibility(View.VISIBLE);
			loadingWifi.setVisibility(View.VISIBLE);
			wifiName.setEnabled(true);
			wifiPwd.setEnabled(true);
			// } else {// 非家庭安防不能修改,也不显示加载wifi动画
			// // 可选取无线网络进行配置
			// wifiSelect.setVisibility(View.GONE);
			// wifiListView.setVisibility(View.GONE);
			// wifiListBG.setVisibility(View.GONE);
			// loadingWifi.setVisibility(View.GONE);
			// wifiName.setEnabled(false);
			// wifiPwd.setEnabled(false);
			// }
		} else if (null != settingMap
				&& settingMap.get("ACTIVED").equalsIgnoreCase("2")) {// 无线
			wifiDetail.setVisibility(View.VISIBLE);
			wifiSelect.setVisibility(View.GONE);
			loadingWifi.setVisibility(View.GONE);
			wifiListView.setVisibility(View.GONE);
			wifiListBG.setVisibility(View.GONE);
			currIndex = 1;
		}
		tabMenuList.get(currIndex).setBackgroundDrawable(
				getResources().getDrawable(R.drawable.tab_menu_hover));
		tabMenuList.get(currIndex).setTextColor(Color.WHITE);
		mPager.setCurrentItem(currIndex);
		// // 获取主控码流信息请求
		// Jni.sendTextData(1, JVNetConst.JVN_RSP_TEXTDATA, 8,
		// JVNetConst.JVN_STREAM_INFO);

		// 获取主控AP信息请求
		Jni.sendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
				JVNetConst.JVN_WIFI_INFO);
	}

	// wifi列表点击事件
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			wifiIndex = arg2;
			wifiAdapter.wifiIndex = wifiIndex;
			wifiName.setText(wifiList.get(arg2).wifiUserName);
			wifiAdapter.notifyDataSetChanged();
		}

	};

	OnClickListener mOnClickListener = new OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left: {
				goToBack();
				break;
			}
			case R.id.btn_right:
				if (0 == currIndex) {// 有线

					if (null != settingMap
							&& null != settingMap.get("ClientPower")
							&& (JVNetConst.POWER_USER & Integer
									.parseInt(settingMap.get("ClientPower"))) > 0) {// 大于0是普通用户
						showTextToast(R.string.str_no_permission);
					} else {
						// 设备处于STA模式，应该不能修改有线的IP地址
						if (null != settingMap
								&& settingMap.get("ACTIVED").equalsIgnoreCase(
										"2")) {// 无线不允许改手动输入的ip，改了不起作用

						} else {
							int bdhcp = bdhcpTag;
							int oldbdhcp = Integer.parseInt(settingMap
									.get("bDHCP"));
							String str1 = ipET.getText().toString();
							String str2 = netmaskET.getText().toString();
							String str3 = gateWayET.getText().toString();
							String str4 = dnsET.getText().toString();
							// 都一样根本没改
							if (oldbdhcp == bdhcp
									&& str1.equalsIgnoreCase(settingMap
											.get("ETH_IP"))
									&& str2.equalsIgnoreCase(settingMap
											.get("ETH_NM"))
									&& str3.equalsIgnoreCase(settingMap
											.get("ETH_GW"))
									&& str4.equalsIgnoreCase(settingMap
											.get("ETH_DNS"))) {
							} else {
								Jni.setDhcp(1,
										(byte) JVNetConst.JVN_RSP_TEXTDATA,
										bdhcp, str1, str2, str3, str4);
							}

						}
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (0 == deviceList.get(deviceIndex).getIsDevice()) {
							deviceList.get(deviceIndex).setIp("");
							deviceList.get(deviceIndex).setPort(0);
							CacheUtil.saveDevList(deviceList);
						}
						back();
					}
				} else if (1 == currIndex) { // 无线
					// 非家庭安防且有ClientPower字段
					if (null != settingMap
							&& null != settingMap.get("ClientPower")
							&& (JVNetConst.POWER_USER & Integer
									.parseInt(settingMap.get("ClientPower"))) > 0) {// 大于0是普通用户
						showTextToast(R.string.str_no_permission);
					} else {
						// 无线状态不能发请求
						if (null != settingMap
								&& settingMap.get("ACTIVED").equalsIgnoreCase(
										"2")) {// 无线
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							back();
						} else {

//							// 值为2双码流是家庭安防产品
//							if (null != settingMap
//									&& null != settingMap.get("MobileCH")
//									&& "2".equalsIgnoreCase(settingMap
//											.get("MobileCH"))) {
								String wifiname = wifiName.getText().toString();
								String wifipwd = wifiPwd.getText().toString();

								if ("".equalsIgnoreCase(wifiname)) {
									showTextToast(R.string.str_wifiname_not_null);
								} else {

									String auth = "";
									String enc = "";

									if (null != wifiList
											&& 0 != wifiList.size()
											&& wifiIndex >= 0
											&& wifiIndex < wifiList.size()) {
										auth = String.valueOf(wifiList
												.get(wifiIndex).wifiAuth);
										enc = String.valueOf(wifiList
												.get(wifiIndex).wifiEnc);
									}

									Jni.saveWifi(1,
											(byte) JVNetConst.JVN_RSP_TEXTDATA,
											wifiname, wifipwd, 2, 9, auth, enc);

									try {
										Thread.sleep(20);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									back();

								}

//							}

						}
					}
				}
				// } else if (2 == currIndex) { // 码流
				//
				// int mobilech = 0;
				// int width = 0;
				// int height = 0;
				// String proTag = settingMap.get("MobileCH");
				//
				// if ("2".equalsIgnoreCase(proTag)) {
				// mobilech = 2;
				// if (0 == arrayIndex0) {
				// width = 352;
				// height = 288;
				// } else {
				// width = 720;
				// height = 480;
				// }
				// } else {
				// if (null != settingMap.get("*CH*")) {
				// mobilech = Integer.parseInt(settingMap.get("*CH*"));
				// }
				//
				// width = 352;
				// height = 288;
				// }
				// try {
				// // 还没获取到数据
				// if (imageFluent
				// .getText()
				// .toString()
				// .equalsIgnoreCase(
				// getResources().getString(
				// R.string.str_fluent))
				// || imageClear
				// .getText()
				// .toString()
				// .equalsIgnoreCase(
				// getResources().getString(
				// R.string.str_clear))) {
				//
				// } else {
				// int arg1 = Integer.parseInt(imageFluent.getText()
				// .toString());// nMBPH
				// int arg2 = Integer.parseInt(imageClear.getText()
				// .toString());// framerate
				// // Jni.setBpsAndFps(1,
				// // (byte) JVNetConst.JVN_RSP_TEXTDATA,
				// // mobilech, width, height, arg1, arg2);
				// Jni.sendString(1, JVNetConst.JVN_RSP_TEXTDATA,
				// false, 0, Consts.TYPE_SET_PARAM,
				// String.format(Consts.FORMATTER_SET_BPS_FPS,
				// mobilech, width, height, arg1,
				// arg2, 1));
				// }
				// try {
				// Thread.sleep(200);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// back();
				// }

				break;
			case R.id.obtainauto:// 自动获取
				bdhcpTag = 1;// 1为自动获取
				ipStr = ipET.getText().toString();// IP地址
				netmaskStr = netmaskET.getText().toString();// 子网掩码
				gateWayStr = gateWayET.getText().toString();// 默认网关
				dnsStr = dnsET.getText().toString();// 域名服务器

				ipET.setEnabled(false);
				netmaskET.setEnabled(false);
				gateWayET.setEnabled(false);
				dnsET.setEnabled(false);

				ipET.setTextColor(getResources()
						.getColor(R.color.userinfocolor));
				netmaskET.setTextColor(getResources().getColor(
						R.color.userinfocolor));
				gateWayET.setTextColor(getResources().getColor(
						R.color.userinfocolor));
				dnsET.setTextColor(getResources().getColor(
						R.color.userinfocolor));

				if (null != settingMap) {
					ipET.setText(settingMap.get("ETH_IP"));// IP地址
					netmaskET.setText(settingMap.get("ETH_NM"));// 子网掩码

					// 网关为空
					if (null != settingMap
							&& !"".equalsIgnoreCase(settingMap.get("ETH_GW"))) {
						gateWayET.setText(settingMap.get("ETH_GW"));// 默认网关
					} else {
						gateWayET.setText("0.0.0.0");// 默认网关
					}

					dnsET.setText(settingMap.get("ETH_DNS"));// 域名服务器
					macET.setText(settingMap.get("ETH_MAC"));// 网卡地址
					cloudseeidET.setText(device.getFullNo());// 设备号
				}

				autoImage.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.morefragment_selector_icon));
				manuImage.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.morefragment_normal_icon));
				obtainAuto.setBackgroundResource(R.drawable.obtain_select_bg);
				obtainManu.setBackgroundResource(R.drawable.obtain_bg);
				currentMenu.setText(R.string.str_obtain_automatically);
				secondLayout.setVisibility(View.VISIBLE);
				firstLayout.setVisibility(View.GONE);
				break;
			case R.id.obtainmanu:// 手动输入
				bdhcpTag = 0;// 0为手动输入
				ipET.setText(ipStr);// IP地址
				netmaskET.setText(netmaskStr);// 子网掩码
				gateWayET.setText(gateWayStr);// 默认网关
				dnsET.setText(dnsStr);// 域名服务器

				ipET.setEnabled(true);
				netmaskET.setEnabled(true);
				gateWayET.setEnabled(true);
				dnsET.setEnabled(true);

				ipET.setTextColor(getResources().getColor(R.color.black));
				netmaskET.setTextColor(getResources().getColor(R.color.black));
				gateWayET.setTextColor(getResources().getColor(R.color.black));
				dnsET.setTextColor(getResources().getColor(R.color.black));

				autoImage.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.morefragment_normal_icon));
				manuImage.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.morefragment_selector_icon));
				obtainAuto.setBackgroundResource(R.drawable.obtain_bg);
				obtainManu.setBackgroundResource(R.drawable.obtain_select_bg);
				currentMenu.setText(R.string.str_input_manually);
				secondLayout.setVisibility(View.VISIBLE);
				firstLayout.setVisibility(View.GONE);
				break;

			case R.id.wifidetail:
				currentMenu.setText(R.string.str_wifi_info);
				wifiSecondLayout.setVisibility(View.VISIBLE);
				wifiFirstLayout.setVisibility(View.GONE);
				break;
			// case R.id.imagequsetting:
			// new AlertDialog.Builder(JVRemoteSettingActivity.this)
			// .setTitle(
			// getResources().getString(
			// R.string.str_image_quality))
			// .setSingleChoiceItems(array, arrayIndex0,
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// imageQuality.setText(array[which]);
			// arrayIndex0 = which;
			// dialog.dismiss();
			// }
			// })
			// .setNegativeButton(
			// getResources().getString(R.string.cancel), null)
			// .show();
			// break;
			// case R.id.clearsetting:
			//
			// new AlertDialog.Builder(JVRemoteSettingActivity.this)
			// .setTitle(getResources().getString(R.string.str_clear))
			// .setSingleChoiceItems(array1, arrayIndex1,
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// imageClear.setText(array1[which]);
			// arrayIndex1 = which;
			// dialog.dismiss();
			// }
			// })
			// .setNegativeButton(
			// getResources().getString(R.string.cancel), null)
			// .show();
			// break;
			// case R.id.fluentsetting:
			// new AlertDialog.Builder(JVRemoteSettingActivity.this)
			// .setTitle(getResources().getString(R.string.str_fluent))
			// .setSingleChoiceItems(array2, arrayIndex2,
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// imageFluent.setText(array2[which]);
			// arrayIndex2 = which;
			// dialog.dismiss();
			// }
			// })
			// .setNegativeButton(
			// getResources().getString(R.string.cancel), null)
			// .show();
			// break;
			}
		}

	};

	// 返回事件
	public void goToBack() {
		if (currIndex == 0) {// 有线连接
			if (secondLayout.getVisibility() == View.VISIBLE) {
				currentMenu.setText(R.string.str_help1_0);
				secondLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
			} else {
				back();
			}
		} else if (currIndex == 1) {// 无线连接
			if (wifiSecondLayout.getVisibility() == View.VISIBLE) {
				wifiSecondLayout.setVisibility(View.GONE);
				wifiFirstLayout.setVisibility(View.VISIBLE);
			} else {
				back();
			}
		} else {// 码流设置
			back();
		}
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {

			// 有线连接
			if (index == 0) {
				if (secondLayout.getVisibility() == View.VISIBLE) {
					secondLayout.setVisibility(View.GONE);
				}
				firstLayout.setVisibility(View.VISIBLE);
				// wifiFirstLayout.setVisibility(View.GONE);
			} else if (index == 1) {// 无线连接
				if (wifiSecondLayout.getVisibility() == View.VISIBLE) {
					wifiSecondLayout.setVisibility(View.GONE);
				}
				// firstLayout.setVisibility(View.GONE);
				wifiFirstLayout.setVisibility(View.VISIBLE);
			}
			for (int i = 0; i < tabMenuList.size(); i++) {
				if (i == index) {
					tabMenuList.get(index).setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.tab_menu_hover));
					tabMenuList.get(index).setTextColor(Color.WHITE);
				} else {
					tabMenuList.get(i).setBackgroundDrawable(null);
					tabMenuList.get(i)
							.setTextColor(
									getResources().getColor(
											R.color.setting_text_color));
				}
			}

			mPager.setCurrentItem(index);
			currentMenu.setText(R.string.str_help1_0);
			// saveChange.setText(getResources().getString(R.string.str_finish));
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@SuppressWarnings("deprecation")
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			for (int i = 0; i < tabMenuList.size(); i++) {
				if (i == currIndex) {
					tabMenuList.get(currIndex).setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.tab_menu_hover));
					tabMenuList.get(currIndex).setTextColor(Color.WHITE);
				} else {
					tabMenuList.get(i).setBackgroundDrawable(null);
					tabMenuList.get(i)
							.setTextColor(
									getResources().getColor(
											R.color.setting_text_color));
				}
			}

			currentMenu.setText(R.string.str_help1_0);
			// 有线连接
			if (currIndex == 0) {
				if (secondLayout.getVisibility() == View.VISIBLE) {
					secondLayout.setVisibility(View.GONE);
				}
				firstLayout.setVisibility(View.VISIBLE);
			} else if (currIndex == 1) {// 无线连接
				if (wifiSecondLayout.getVisibility() == View.VISIBLE) {
					wifiSecondLayout.setVisibility(View.GONE);
				}
				wifiFirstLayout.setVisibility(View.VISIBLE);
			}

			if (null != animation) {
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(300);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goToBack();
	}

	// /**
	// * 设置码流数据
	// *
	// * @param allStr
	// */
	// public void setStreamData(String allStr) {
	// allStreamMap = ConfigUtil.genMsgMap(allStr);
	//
	// if (!allStr.equalsIgnoreCase("")) {
	// String textString4 = "";
	//
	// if (null != allStreamMap.get("MobileCH")
	// && "2".equalsIgnoreCase(allStreamMap.get("MobileCH"))) {// 家用取第二码流
	// if (allStr.contains("[CH2];")) {
	// textString4 = allStr.substring(
	// allStr.lastIndexOf("[CH2];") + 6, allStr.length());
	// settingMap.put("*CH*", "2");
	// }
	// } else {// 非家用取第三码流
	// if (allStr.contains("[CH3];")) {
	// textString4 = allStr.substring(
	// allStr.lastIndexOf("[CH3];") + 6, allStr.length());
	// settingMap.put("*CH*", "3");
	// } else {
	// if (allStr.contains("[CH2];")) {
	// textString4 = allStr.substring(
	// allStr.lastIndexOf("[CH2];") + 6,
	// allStr.length());
	// settingMap.put("*CH*", "2");
	// } else {
	// textString4 = allStr.substring(
	// allStr.lastIndexOf("[CH1];") + 6,
	// allStr.length());
	// settingMap.put("*CH*", "1");
	// }
	// }
	// }
	// String[] arrayStr = textString4.split(";");
	// if (null != arrayStr) {
	// for (int i = 0; i < arrayStr.length; i++) {
	// if (arrayStr[i].contains("=")) {
	// String[] arrayStr1 = arrayStr[i].split("=");
	// if (null == settingMap) {
	// settingMap = new HashMap<String, String>();
	// }
	// if (arrayStr1.length == 1) {
	// if (!settingMap.containsKey(arrayStr1[0])) {
	// settingMap.put(arrayStr1[0], "");
	// }
	//
	// } else {
	// if (!settingMap.containsKey(arrayStr1[0])) {
	// settingMap.put(arrayStr1[0], arrayStr1[1]);
	// }
	// }
	//
	// }
	// }
	// }
	// }
	//
	// if (null != settingMap) {
	// // 值为2双码流是家庭安防产品
	// if (null != settingMap.get("MobileCH")
	// && "2".equalsIgnoreCase(settingMap.get("MobileCH"))) {
	// array = getResources().getStringArray(
	// R.array.array_image_quality);
	// array1 = getResources().getStringArray(R.array.array_clear);
	// array2 = getResources().getStringArray(R.array.array_fluent);
	// } else {
	// array = getResources().getStringArray(
	// R.array.array_image_quality_not);
	// array1 = getResources().getStringArray(R.array.array_clear_not);
	// array2 = getResources()
	// .getStringArray(R.array.array_fluent_not);
	// }
	//
	// // 图像质量
	// if (null != settingMap.get("MobileCH")
	// && "2".equalsIgnoreCase(settingMap.get("MobileCH"))) {
	// if (Integer.parseInt(settingMap.get("width")) == 720) {
	// arrayIndex0 = 1;
	// imageQuality.setText(array[arrayIndex0]);
	// } else if (Integer.parseInt(settingMap.get("width")) == 352) {
	// arrayIndex0 = 0;
	// imageQuality.setText(array[arrayIndex0]);
	// }
	// } else {
	// arrayIndex0 = 0;
	// imageQuality.setText(array[arrayIndex0]);
	// }
	//
	// // 码率
	// for (int i = 0; i < array1.length; i++) {
	// if (array1[i].equalsIgnoreCase(settingMap.get("framerate"))) {
	// arrayIndex1 = i;
	// imageClear.setText(array1[arrayIndex1]);
	// }
	// }
	// if (Integer.parseInt(settingMap.get("framerate")) < Integer
	// .parseInt(array1[0])) {
	// arrayIndex1 = 0;
	// imageClear.setText(array1[arrayIndex1]);
	// } else if (Integer.parseInt(settingMap.get("framerate")) > Integer
	// .parseInt(array1[array1.length - 1])) {
	// arrayIndex1 = array1.length - 1;
	// imageClear.setText(array1[arrayIndex1]);
	// }
	// int temMBPH = Integer.parseInt(settingMap.get("nMBPH"));
	// // 码流质量
	// for (int i = 1; i < array2.length; i++) {
	// // if(array2[i].equalsIgnoreCase(settingMap.get("nMBPH"))){
	// // arrayIndex2 = i;
	// // imageFluent.setText(array2[arrayIndex2]);
	// // }
	//
	// if (temMBPH >= Integer.parseInt(array2[i - 1])
	// && temMBPH < Integer.parseInt(array2[i])) {
	// arrayIndex2 = i - 1;
	// imageFluent.setText(array2[arrayIndex2]);
	// }
	//
	// // 最后一个
	// if (i == array2.length - 1
	// && temMBPH >= Integer.parseInt(array2[i])) {
	// arrayIndex2 = array2.length - 1;
	// imageFluent.setText(array2[arrayIndex2]);
	// }
	//
	// }
	//
	// if (Integer.parseInt(settingMap.get("nMBPH")) < Integer
	// .parseInt(array2[0])) {
	// arrayIndex2 = 0;
	// imageFluent.setText(array2[arrayIndex2]);
	// } else if (Integer.parseInt(settingMap.get("nMBPH")) > Integer
	// .parseInt(array2[array2.length - 1])) {
	// arrayIndex2 = array2.length - 1;
	// imageFluent.setText(array2[arrayIndex2]);
	// }
	//
	// }
	//
	// imageQualitySetting.setOnClickListener(mOnClickListener);
	// clearSetting.setOnClickListener(mOnClickListener);
	// fluentSetting.setOnClickListener(mOnClickListener);
	//
	// }

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
		if (null != settingMap) {
			settingMap.clear();
		}
		if (null != allStreamMap) {
			allStreamMap.clear();
		}

		array = null;
		array1 = null;
		array2 = null;
		wifiList.clear();
		device = null;

	}

	public void back() {
		if (PlayUtil.disconnectDevice()) {
			createDialog("", false);
			while (!disConnected) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			dismissDialog();
		}
		JVRemoteSettingActivity.this.finish();
	}

}
