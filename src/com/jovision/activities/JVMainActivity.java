package com.jovision.activities;

import android.R;
import android.widget.Button;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.adapters.DeviceListAdapter;
import com.jovision.views.RefreshableMainListView;

public class JVMainActivity extends BaseActivity {
	/** 显示隐藏左侧按钮 */
	private Button showLeft;
	/** 当前页面名称 */
	private TextView currentMenu;
	/** 增加设备 */
	private Button addDevice;
	/** 设备列表 */
	private RefreshableMainListView deviceListView = null;
	/** 设备列表adapter（第一项是广告位） */
	private DeviceListAdapter deviceAdapter = null;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.center_layout);
		showLeft = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.login_str_device);
		addDevice = (Button) findViewById(R.id.btn_right);
		deviceListView = (RefreshableMainListView) findViewById(R.id.devicelistview);
		deviceAdapter = new DeviceListAdapter(JVMainActivity.this,
				Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN)));
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

// // Sensor管理器
// private SensorManager mSensorManager = null;
// // 震动
// private Vibrator mVibrator = null;
//
// private ViewPager helpPager;// 帮助图
// private Timer mTimer = null;
// private TimerTask mTimerTask;
// private int dCount;// 倒计时数
//
// // 判断网络
// public Timer oneMinTimer = null;
// public NetCheckTask oneMinTask = null;
//
// private Dialog helpDialog = null;// 帮助dialog
//
// private String publicAccountName = "";// 公共账号名字
// private int picHeight = 0;// 新品高度
//
// public boolean changeFlag = true;// 图片切换动画执行flag
// public int screenWidth = 0;
// public int currentOpenPosition = -1;// 当前展开的设备
//
// private DeviceListAdapter deviceAdapter = null;// 设备列表Adapter
// private JVConfigManager dbManager = null;
//
// private ImageView deviceManage = null;// 设备管理按钮
// private ProgressDialog dialog = null;// 进度dialog
// private ImageView newTag = null;// 新品的new
// private FrameLayout newProLayout = null;// 新品布局
// private Button showLeft;// 显示隐藏左侧按钮
// private TextView currentMenu;// 当前页面名称
// private Button addDevice;// 增加设备
// private ImageView scanqrCode;// 二维码扫描添加设备
// private LinearLayout addDeviceLayout;// 增加设备布局
// private RefreshableMainListView deviceListView = null;// 设备列表view
// private boolean refreshFlag = false;// 列表刷新标志位
// private TextView showDemoPoint = null;// 演示点
//
// private DisplayMetrics dm = null;
// private LinearLayout indicatorlayout = null;// 小圆点
// private ArrayList<ImageView> mIndicatorList = new ArrayList<ImageView>();//
// 装小圆点的集合
//
// // 帮助图滑动圆点
// private LinearLayout indicatorlayout2 = null;
// private ArrayList<ImageView> mIndicatorList2 = new ArrayList<ImageView>();
//
// // 添加设备
// private EditText ystNum;
// private EditText account;
// private EditText passWord;
// private TableRow accoutLayout;
// private TableRow passLayout;
// private Button advance;
// private Button save;
// private boolean flag;// 标记列表初始有无内容
// // 帮助图
// private RelativeLayout help1;
// private RelativeLayout help10;
// private RelativeLayout help11;
// private boolean hasConnPoint = false;
//
// // 淘宝样式图片滑动
// private ArrayList<NewProduct> newProList = new ArrayList<NewProduct>();
// private ViewFlow viewFlow;
// private ImageAdapter imageAdapter;
//
// // 图片切换timer
// private Timer imageSwitchTimer = null;
// private TimerTask imageSwitchTask = null;
//
// private int TIME_SPAN = 2 * 60 * 1000;// 时间间隔
// public Timer fiveMinTimer = null;
// public CheckBroacastData fiveMinTask = null;
// private boolean isOnCreate = false;// 是否重建Activity
// private ImageView noDevice;
//
// @Override
// protected void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
// WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
// Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
// JVMainActivity.this.getApplicationContext()));
//
// // 获取手机分辨率
// dm = new DisplayMetrics();
// getWindowManager().getDefaultDisplay().getMetrics(dm);
// screenWidth = dm.widthPixels;
// dm = null;
// setContentView(R.layout.center_layout);
// isOnCreate = true;
// initViews();
//
// // 3G网络提示
// BaseApp.is3G(JVMainActivity.this, true);
//
// // 非本地登录检查更新
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// // 检查新版本
// UpdateThread updateThread = new UpdateThread(JVMainActivity.this,
// JVConst.JVMAIN_ACTIVITY_FLAG);
// updateThread.start();
// }
//
// // 调用初始化推送服务
// if (!BaseApp.isInitPushSdk) {
// // 进程序调用一次
// JVClient.JVInitWebcc();
// JVClient.JVRegisterCallBack("android/test/JVClient",
// "JVPushInfoCallBack", "JVPushChanngeCallBack");
// MyLog.e("进程序调用一次推送", "");
// BaseApp.isInitPushSdk = true;
// }
//
// try {
// if (null == dialog) {
// dialog = new ProgressDialog(JVMainActivity.this);
// dialog.setCancelable(false);
// }
// dialog.show();
// } catch (Exception e) {
// e.printStackTrace();
// }
// if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地登录
// BaseApp.LOADIMAGE = false;
//
// dbManager = BaseApp.getDbManager(this);//
// BaseApp.deviceList = getDataFromDB();// 从数据库中查询所有设备
//
// Message msg = BaseApp.mainHandler.obtainMessage();
// if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {//
// 获取设备成功,去广播设备列表
// if (BaseApp.is3G(JVMainActivity.this, false)) {// 3G直接加载设备
// msg.what = JVConst.DEVICE_GETDATA_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// } else {
// BaseApp.broadDeviceList();
//
// msg.what = JVConst.DEVICE_SEARCH_BROADCAST;//
// // 发广播搜索局域网设备（非3g搜索局域网）
// BaseApp.broadcastState = JVConst.UPDATE_LIST_BROADCAST;
// BaseApp.mainHandler.sendMessage(msg);
// }
// } else if (null != BaseApp.deviceList
// && 0 == BaseApp.deviceList.size()) {// 无数据
// msg.what = JVConst.DEVICE_NO_DEVICE;
// BaseApp.mainHandler.sendMessage(msg);
// }
//
// } else {// 非本地登录
// // BaseApp.LOADIMAGE =
// // BaseApp.getSP(getApplicationContext()).getBoolean("ScenePictures",
// // false);
// BaseApp.LOADIMAGE = false;
// // qq不允许qq账号登陆后立即提示绑定
// if (LoginUtil.userName.startsWith(LoginUtil.PUBLIC_ACCOUNT)) {// 公共账户
// publicAccountName = LoginUtil.userName;
//
// if (null != dialog && dialog.isShowing()) {
// dialog.dismiss();
// dialog = null;
// }
// } else {
// // 保持在线服务
// if (JVConst.KEEP_ONLINE_FLAG) {// 已经开启服务
// // do Nothing
// } else {
// Intent serviceIntent = new Intent();
// serviceIntent.setClass(this, OffLineService.class);
// startService(serviceIntent);
// JVConst.KEEP_ONLINE_FLAG = true;
// serviceIntent = null;
// }
//
// // 加载设备通道数据
// DataThread dataThread = new DataThread(JVMainActivity.this);
// dataThread.start();
//
// }
// }
//
// if (!BaseApp.LOCAL_LOGIN_FLAG) {// 非本地登录才接受推送
// // 首次登陆，提示是否接收推送
// if (null != BaseApp.getSP(getApplicationContext())
// && BaseApp.getSP(getApplicationContext()).getBoolean(
// "FirstLogin", true)) {
// // openPushDialog();
// BaseApp.getEditor(getApplicationContext()).putBoolean(
// "FirstLogin", false);
// BaseApp.getEditor(getApplicationContext()).commit();
// } else {// 非首次登陆获取本地保存的是否接收推送
// if (null != BaseApp.getSP(getApplicationContext())
// && BaseApp.getSP(getApplicationContext()).getBoolean(
// "PushMessage", false)) {
// // 能连上网，打开推送
// if (BaseApp.isConnected(JVMainActivity.this)) {
// if (!BaseApp.WEBCC_INTERFACE) {// 没有交互线程
// // 上线请求
// WebccThread wt = new WebccThread(
// JVMainActivity.this, 1,
// BaseApp.WEBCC_ONLINE);
// wt.start();
// }
// }
// } else {
// // 能连上网，webcc账号下线
// if (BaseApp.isConnected(JVMainActivity.this)) {
// if (!BaseApp.WEBCC_INTERFACE) {// 没有交互线程
// // 上线请求
// WebccThread wt = new WebccThread(
// JVMainActivity.this, 1,
// BaseApp.WEBCC_OFFLINE);
// wt.start();
// }
//
// }
//
// }
// }
// }
//
// }
//
// // 五分钟广播数据
// private static class CheckBroacastData extends TimerTask {
// private Context context;
//
// public CheckBroacastData(JVMainActivity jvMainActivity) {
// context = jvMainActivity;
// }
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// MyLog.e("五分钟时间到：", "清空数据，修改数据库！");
// if (null != JVSUDT.getInstance().broadCastData
// && 0 != JVSUDT.getInstance().broadCastData.size()) {
// JVSUDT.getInstance().broadCastData.clear();
// }
// Message msg = BaseApp.mainHandler.obtainMessage();
// if (!BaseApp.is3G(context, false)) {
// DeviceUtil.refreshDeviceState(LoginUtil.userName);
// BaseApp.broadDeviceList();
// msg.what = JVConst.DEVICE_SEARCH_BROADCAST;// 发广播搜索局域网设备（非3g搜索局域网）
// BaseApp.broadcastState = JVConst.UPDATE_STATE_BROADCAST;
// BaseApp.mainHandler.sendMessage(msg);
// } else {
// // LoginUtil.index = 0;
// boolean result = DeviceUtil
// .refreshDeviceState(LoginUtil.userName);
// if (result) {
// msg.what = JVConst.UPDATE_DEVICE_STATE_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// } else {
// msg.what = JVConst.UPDATE_DEVICE_STATE_FAILED;
// BaseApp.mainHandler.sendMessage(msg);
// }
// }
// }
//
// }
//
// // 打开是否接收推送消息dialog
// private void openPushDialog() {
// AlertDialog.Builder builder = new AlertDialog.Builder(
// JVMainActivity.this);
//
// builder.setTitle(R.string.tips);
// builder.setMessage("\"" + getResources().getString(R.string.app_name)
// + "\""
// + getResources().getString(R.string.str_push_message_tip));
//
// builder.setPositiveButton(R.string.str_sure,
// new DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// // MyLog.e("getIMEI", getIMEI());
//
// // 能连上网，打开推送
// if (BaseApp.isConnected(JVMainActivity.this)) {
// if (!BaseApp.WEBCC_INTERFACE) {// 没有交互线程
// // 上线请求
// WebccThread wt = new WebccThread(
// JVMainActivity.this, 1,
// BaseApp.WEBCC_ONLINE);
// wt.start();
// }
// }
//
// }
// });
// builder.setNegativeButton(R.string.str_cancel,
// new DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// // 能连上网，打开推送
// if (BaseApp.isConnected(JVMainActivity.this)) {
// if (!BaseApp.WEBCC_INTERFACE) {// 没有交互线程
// // 上线请求
// WebccThread wt = new WebccThread(
// JVMainActivity.this, 1,
// BaseApp.WEBCC_OFFLINE);
// wt.start();
// }
// }
// }
// });
// builder.create().show();
// }
//
// ArrayList<View> listViews = new ArrayList<View>();
//
// /**
// * ViewPager适配器
// */
// public class MyPagerAdapter extends PagerAdapter {
// public List<View> mListViews;
//
// public MyPagerAdapter(List<View> mListViews) {
// this.mListViews = mListViews;
// }
//
// @Override
// public void destroyItem(View arg0, int arg1, Object arg2) {
// ((ViewPager) arg0).removeView(mListViews.get(arg1));
// }
//
// @Override
// public void finishUpdate(View arg0) {
// }
//
// @Override
// public int getCount() {
// return mListViews.size();
// }
//
// @Override
// public Object instantiateItem(ViewGroup arg0, int arg1) {
// ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
// return mListViews.get(arg1);
// }
//
// @Override
// public boolean isViewFromObject(View arg0, Object arg1) {
// return arg0 == (arg1);
// }
//
// @Override
// public void restoreState(Parcelable arg0, ClassLoader arg1) {
// }
//
// @Override
// public Parcelable saveState() {
// return null;
// }
//
// @Override
// public void startUpdate(View arg0) {
// }
// }
//
// // 从数据库获取数据
// public ArrayList<Device> getDataFromDB() {
// ArrayList<Device> temList = new ArrayList<Device>();
// ArrayList<ConnPoint> tempList = new ArrayList<ConnPoint>();
// // 查询所有设备
// List<JVConnectInfo> deList = BaseApp.queryAllDataList(true);
// if (null != deList && 0 != deList.size()) {
// int size = deList.size();
// for (int i = 0; i < size; i++) {
// // 数据库存储类型转换成网络获取数据类型
// Device de = deList.get(i).toDevice();
// if (null != de) {
// temList.add(de);
// }
// }
// }
// // 查询所有通道
// List<JVConnectInfo> poList = BaseApp.queryAllDataList(false);
// if (null != poList && 0 != poList.size()) {
// int size = poList.size();
// for (int i = 0; i < size; i++) {
// ConnPoint cp = poList.get(i).toConnPoint();
// if (null != cp) {
// tempList.add(cp);
// }
// }
// }
//
// // 将通道关联到设备上
// if (null != temList && 0 != temList.size() && null != tempList
// && 0 != tempList.size()) {
// for (int i = 0; i < temList.size(); i++) {
// if (null == temList.get(i).pointList) {
// temList.get(i).pointList = new ArrayList<ConnPoint>();
// }
// for (int j = 0; j < tempList.size(); j++) {
// if (temList.get(i).yst == tempList.get(j).ystNum
// && temList.get(i).group.equalsIgnoreCase(tempList
// .get(j).group)) {
// temList.get(i).pointList.add(tempList.get(j));
// }
// }
//
// }
// }
// temList = BaseApp.orderDevice(temList);
// return temList;
// }
//
// @Override
// protected void onActivityResult(int requestCode, int resultCode, Intent data)
// {
// super.onActivityResult(requestCode, resultCode, data);
// if (requestCode == JVConst.ACCOUNT_BIND_REQUEST) {
// switch (resultCode) {
// case JVConst.ACCOUNT_BIND_RESULT:
// // 绑定登陆成功了才去获取数据
// if (!"".equalsIgnoreCase(LoginUtil.UNIQUECODE)
// && 32 == LoginUtil.UNIQUECODE.length()) {
// // 加载设备通道数据
// if (null == dialog) {
// dialog = new ProgressDialog(JVMainActivity.this);
// dialog.setCancelable(false);
// }
// dialog.setMessage(getResources().getString(
// R.string.str_loading_data));
// dialog.show();
// DataThread dataThread = new DataThread(JVMainActivity.this);
// dataThread.start();
// } else {// 绑定失败，用户名还用公共账号
// LoginUtil.userName = publicAccountName;
// }
//
// }
// } else if (resultCode == JVConst.BARCODE_RESULT) {
// Bundle bundle = data.getExtras();
// String scanResult = bundle.getString("result");
// ystNum.setText(scanResult);
//
// }
//
// }
//
// @Override
// protected void onPause() {
// if (null != dialog && dialog.isShowing()) {
// dialog.dismiss();
// dialog = null;
// }
// if (null != fiveMinTimer) {
// fiveMinTimer.cancel();
// fiveMinTimer = null;
// }
// if (null != fiveMinTask) {
// fiveMinTask.cancel();
// fiveMinTask = null;
// }
// super.onPause();
// }
//
// @Override
// public void onDestroy() {
// // TODO Auto-generated method stub
//
// if (null != oneMinTask) {
// oneMinTask.cancel();
// }
// oneMinTask = null;
//
// if (null != oneMinTimer) {
// oneMinTimer.cancel();
// }
// oneMinTimer = null;
//
// if (null != imageSwitchTimer) {
// imageSwitchTimer.cancel();
// imageSwitchTimer = null;
// }
//
// if (null != dialog && dialog.isShowing()) {
// dialog.dismiss();
// dialog = null;
// }
// LoginUtil.UNIQUECODE = "";
// if (null != BaseApp.deviceList && BaseApp.deviceList.size() > 0) {
// BaseApp.deviceList.clear();
// }
//
//
// if (null != BaseApp.mNotificationManager) {
// BaseApp.mNotificationManager.cancel(0);
// }
//
// super.onDestroy();
// }
//
// private Animation mAnimationRight;
//
// public void initViews() {
//
// helpPager = (ViewPager) findViewById(R.id.helppager);
// dialog = new ProgressDialog(JVMainActivity.this);
// dialog.setCancelable(false);
// dialog.setIndeterminate(true);
// dialog.setMessage(getResources().getString(R.string.str_loading_data));
// BaseApp.mainHandler = new MainHandler(JVMainActivity.this);
// mAnimationRight = AnimationUtils.loadAnimation(JVMainActivity.this,
// R.anim.rotate_right);
// mAnimationRight.setFillAfter(true);
//
// showLeft = (Button) findViewById(R.id.back);
// showLeft.setBackgroundResource(R.drawable.morefunc);
// currentMenu = (TextView) findViewById(R.id.currentmenu);
// currentMenu.setText(R.string.login_str_device);
// addDevice = (Button) findViewById(R.id.add_device);
// addDevice.setBackgroundDrawable(getResources().getDrawable(
// R.drawable.adddevice));
// noDevice = (ImageView) findViewById(R.id.no_device);
// scanqrCode = (ImageView) findViewById(R.id.scanqrcode);
//
// // 显示二维码扫描添加设备
// if (BaseApp.showQRHelp) {
// scanqrCode.setVisibility(View.VISIBLE);
// } else {
// scanqrCode.setVisibility(View.GONE);
// }
//
// addDeviceLayout = (LinearLayout) findViewById(R.id.adddevice);
//
// newTag = (ImageView) findViewById(R.id.gallerytag);
// if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地登录不显示new
// newTag.setVisibility(View.GONE);
// } else {
// newTag.setVisibility(View.VISIBLE);
// }
// deviceManage = (ImageView) findViewById(R.id.devicemanage);
// newProLayout = (FrameLayout) findViewById(R.id.newprolayout);
//
// deviceListView = (RefreshableMainListView) findViewById(R.id.devicelistview);
// // deviceListView
// // .setOnRefreshListener(new OnRefreshListener() {
// // @Override
// // public void onRefresh(RefreshableMainListView listView) {
// // if (null == BaseApp.deviceList
// // || 0 == BaseApp.deviceList.size()
// // || !hasConnPoint) {
// // refreshFlag = true;
// // DataThread dataThread = new DataThread(
// // JVMainActivity.this);
// // dataThread.start();
// // } else {
// // Thread thread = new Thread() {
// // @Override
// // public void run() {
// // super.run();
// // Message msg = BaseApp.mainHandler
// // .obtainMessage();
// // if (!BaseApp.is3G(JVMainActivity.this,
// // false)) {
// // DeviceUtil
// // .refreshDeviceState(LoginUtil.userName);
// // BaseApp.broadDeviceList();
// // msg.what = JVConst.DEVICE_SEARCH_BROADCAST;// 发广播搜索局域网设备（非3g搜索局域网）
// // BaseApp.broadcastState = JVConst.UPDATE_STATE_BROADCAST;
// // BaseApp.mainHandler.sendMessage(msg);
// // } else {
// // // LoginUtil.index = 0;
// // boolean result = DeviceUtil
// // .refreshDeviceState(LoginUtil.userName);
// // if (result) {
// // msg.what = JVConst.UPDATE_DEVICE_STATE_SUCCESS;
// // BaseApp.mainHandler
// // .sendMessage(msg);
// // } else {
// // msg.what = JVConst.UPDATE_DEVICE_STATE_FAILED;
// // BaseApp.mainHandler
// // .sendMessage(msg);
// // }
// // }
// // }
// // };
// // thread.start();
// // }
// // }
// // });
// indicatorlayout = (LinearLayout) findViewById(R.id.indicatorlayout);
// indicatorlayout2 = (LinearLayout) findViewById(R.id.indicatorlayout2);
// viewFlow = (ViewFlow) findViewById(R.id.viewflow);
// addDevice.setVisibility(View.VISIBLE);
// showDemoPoint = (TextView) findViewById(R.id.showpoint);
// showDemoPoint.setAnimation(mAnimationRight);
//
// deviceAdapter = new DeviceListAdapter(JVMainActivity.this);
// deviceManage.setOnClickListener(onClickListener);
// showLeft.setOnClickListener(onClickListener);
// addDevice.setOnClickListener(onClickListener);
// scanqrCode.setOnClickListener(onClickListener);
// showDemoPoint.setOnClickListener(onClickListener);
// imageAdapter = new ImageAdapter(JVMainActivity.this);
//
// // 添加设备界面
// ystNum = (EditText) addDeviceLayout.findViewById(R.id.ystnum);
//
// ystNum.addTextChangedListener(new TextWatcher() {
//
// @Override
// public void onTextChanged(CharSequence s, int start, int before,
// int count) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void beforeTextChanged(CharSequence s, int start, int count,
// int after) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void afterTextChanged(Editable s) {
// // TODO Auto-generated method stub
// if (s.length() > 0) {
// int pos = s.length() - 1;
// char c = s.charAt(pos);
// boolean isError = true;
// for (int i = 0; i < JVConst.str.length; i++) {
// if (JVConst.str[i] == c) {
// isError = false;
// }
// }
// if (isError) {// 这里限制在字串最后追加#
// s.delete(pos, pos + 1);
// // Toast.makeText(LoginActivity.this,
// // "Error letter.",Toast.LENGTH_SHORT).show();
// }
//
// if (null != ystNum.getText()
// && ystNum.getText().toString().trim().length() > 14) {
// s.delete(pos, pos + 1);
// }
// }
// }
// });
//
// account = (EditText) addDeviceLayout.findViewById(R.id.account);
// passWord = (EditText) addDeviceLayout.findViewById(R.id.password);
// accoutLayout = (TableRow) addDeviceLayout
// .findViewById(R.id.accounttablelayout);
// passLayout = (TableRow) addDeviceLayout
// .findViewById(R.id.passtablelayout);
// advance = (Button) addDeviceLayout.findViewById(R.id.advance);
// save = (Button) addDeviceLayout.findViewById(R.id.savedevice);
// // 高级
// advance.setOnClickListener(onClickListener);
// // 保存
// save.setOnClickListener(onClickListener);
// ystNum.setOnClickListener(onClickListener);
// }
//
// // 强制弹出键盘
// public static void showInputMethod(Context context, View view) {
// InputMethodManager im = (InputMethodManager) context
// .getSystemService(Context.INPUT_METHOD_SERVICE);
// im.showSoftInput(view, InputMethodManager.SHOW_FORCED);
//
// }
//
// // onclick事件
// OnClickListener onClickListener = new OnClickListener() {
// @Override
// public void onClick(View v) {
// switch (v.getId()) {
// case R.id.devicemanage:
// Intent manageIntent = new Intent();
// manageIntent.setClass(JVMainActivity.this,
// JVDeviceManageActivity.class);
// JVMainActivity.this.startActivity(manageIntent);
// break;
// case R.id.back:
//
// Intent intent = new Intent();
// intent.setClass(JVMainActivity.this,
// JVMoreFeatureActivity.class);
// intent.putExtra("DirectDisConn", true);
// JVMainActivity.this.startActivity(intent);
//
// break;
// case R.id.add_device:// 添加设备
// if (LoginUtil.userName.startsWith(LoginUtil.PUBLIC_ACCOUNT)
// && !BaseApp.LOCAL_LOGIN_FLAG) {// 公共账户且是在线登陆方式
// publicAccountName = LoginUtil.userName;
// // publicAccount();
// Intent bindIntent = new Intent();
// bindIntent.setClass(JVMainActivity.this,
// JVBindAccountActivity.class);
// bindIntent.putExtra("PublicAccount", publicAccountName);
// JVMainActivity.this.startActivityForResult(bindIntent,
// JVConst.ACCOUNT_BIND_REQUEST);
// } else {// 显示隐藏添加设备界面
// // addDeviceLayout = null;
// addDeviceLayout
// .setBackgroundResource(R.drawable.add_device_bg_small);
// if (addDeviceLayout.getVisibility() == View.GONE) {
//
// // 首次登陆
// // if (null != BaseApp.getSP(getApplicationContext()) &&
// // BaseApp.getSP(getApplicationContext()).getBoolean("ShowAddHelp",
// // true) && BaseApp.showQRHelp) { // 软件有二维码扫描才显示帮助
// // // 显示帮助图
// // if (null == helpDialog) {
// // helpDialog = new Dialog(JVMainActivity.this,
// // R.style.DialogFullScreen);
// // }
// // helpDialog.setContentView(R.layout.help_layout_6);
// // final RelativeLayout help = (RelativeLayout)
// // helpDialog.findViewById(R.id.help6);
// // help.setVisibility(View.VISIBLE);
// // help.setOnClickListener(new OnClickListener() {
// //
// // @Override
// // public void onClick(View v) {
// // try {
// // BaseApp.getEditor(getApplicationContext()).putBoolean("ShowAddHelp",
// // false);
// // BaseApp.getEditor(getApplicationContext()).commit();
// // help.setVisibility(View.GONE);
// // helpDialog.dismiss();
// // helpDialog = null;
// // } catch (Exception e) {
// // e.printStackTrace();
// // }
// //
// // }
// // });
// // helpDialog.show();
// // }
//
// addDeviceLayout.setVisibility(View.VISIBLE);
// addDevice.setBackgroundResource(R.drawable.close);
// accoutLayout.setVisibility(View.GONE);
// passLayout.setVisibility(View.GONE);
// account.setText(getResources().getString(
// R.string.str_default_user));
// passWord.setText(getResources().getString(
// R.string.str_default_pass));
// ystNum.setFocusable(true);
// ystNum.setFocusableInTouchMode(true);
// ystNum.requestFocus();
// showInputMethod(JVMainActivity.this, ystNum);
// } else {
// // 如果键盘显示,隐藏键盘
// try {
// InputMethodManager imm = (InputMethodManager)
// getSystemService(Context.INPUT_METHOD_SERVICE);
// if (null != JVMainActivity.this.getCurrentFocus()) {// 如果有焦点
// imm.hideSoftInputFromWindow(JVMainActivity.this
// .getCurrentFocus().getWindowToken(),
// InputMethodManager.HIDE_NOT_ALWAYS);
// imm.hideSoftInputFromWindow(
// ystNum.getApplicationWindowToken(), 0);
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// addDevice.setBackgroundResource(R.drawable.adddevice);
// addDeviceLayout.setVisibility(View.GONE);
// ystNum.setText("");
// }
// advance.setText(R.string.login_str_advance);// 高级
// save.setText(R.string.login_str_save);// 保存
// }
//
// deviceAdapter.changePos = -1;
// deviceAdapter.notifyDataSetChanged();
// break;
// case R.id.scanqrcode:
// // 扫瞄添加上设备
// Intent openCameraIntent = new Intent(JVMainActivity.this,
// MipcaActivityCapture.class);
// startActivityForResult(openCameraIntent, 0);
// break;
// case R.id.showpoint:
// if (!BaseApp.openMap) {// 还没打开map，则打开
// BaseApp.openMap = true;
// Intent pointIntent = new Intent();
// pointIntent.setClass(JVMainActivity.this,
// JVMarkerActivity.class);
// JVMainActivity.this.startActivity(pointIntent);
// }
//
// break;
// case R.id.advance:
// if (accoutLayout.getVisibility() == View.GONE) {
// accoutLayout.setVisibility(View.VISIBLE);
// passLayout.setVisibility(View.VISIBLE);
// advance.setText(R.string.login_str_save);// 点击高级后，高级变成保存，
// addDeviceLayout
// .setBackgroundResource(R.drawable.add_device_bg_big);
// save.setText(R.string.str_cancel);
// } else {// 现在点击后保存
// if (!BaseApp.LOCAL_LOGIN_FLAG
// && BaseApp.deviceList.size() >= 100) {
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.str_device_most_count);
// } else {
// addDeviceLayout.setVisibility(View.GONE);
// saveMethod();
// }
// }
// break;
// case R.id.savedevice:
// if (accoutLayout.getVisibility() == View.GONE) {
// if (!BaseApp.LOCAL_LOGIN_FLAG
// && BaseApp.deviceList.size() >= 100) {
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.str_device_most_count);
// } else {
// addDeviceLayout.setVisibility(View.GONE);
// saveMethod();
// }
// } else {
// // 如果键盘显示隐藏键盘
// InputMethodManager imm = (InputMethodManager)
// getSystemService(Context.INPUT_METHOD_SERVICE);
// if (null != JVMainActivity.this.getCurrentFocus()) {// 如果有焦点
// imm.hideSoftInputFromWindow(JVMainActivity.this
// .getCurrentFocus().getWindowToken(),
// InputMethodManager.HIDE_NOT_ALWAYS);
// }
// addDevice.setBackgroundResource(R.drawable.adddevice);
// addDeviceLayout.setVisibility(View.GONE);
// accoutLayout.setVisibility(View.GONE);
// passLayout.setVisibility(View.GONE);
// advance.setText(R.string.login_str_advance);// 点击高级后，高级变成保存，
// save.setText(R.string.login_str_save);
// ystNum.setText("");
// deviceAdapter.changePos = -1;
// deviceAdapter.notifyDataSetChanged();
// }
//
// break;
// case R.id.ystnum:
// ystNum.setFocusable(false);
// ystNum.setFocusableInTouchMode(false);
// ystNum.setFocusable(true);
// ystNum.setFocusableInTouchMode(true);
// ystNum.requestFocus();
// showInputMethod(JVMainActivity.this, ystNum);
// break;
// }
// }
// };
//
// // 保存设备信息
// public void saveMethod() {
// // 如果键盘显示隐藏键盘
// InputMethodManager imm = (InputMethodManager)
// getSystemService(Context.INPUT_METHOD_SERVICE);
// if (null != JVMainActivity.this.getCurrentFocus()) {// 如果有焦点
// imm.hideSoftInputFromWindow(JVMainActivity.this.getCurrentFocus()
// .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
// imm.hideSoftInputFromWindow(ystNum.getApplicationWindowToken(), 0);
// }
//
// if ("".equalsIgnoreCase(ystNum.getText().toString())) {
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.login_str_device_ytnum_notnull);
// } else if (!LoginUtil.checkYSTNum(ystNum.getText().toString())) {// 验证云视通号
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.increct_yst_tips);
// } else if ("".equalsIgnoreCase(account.getText().toString())) {//
// 用户名不可为空，其他不用验证
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.login_str_device_account_notnull);
// } else if (!LoginUtil.checkDeviceUsername(account.getText().toString())) {
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.login_str_device_account_error);
// } else if (!LoginUtil.checkDevicePwd(passWord.getText().toString())) {
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.login_str_device_pass_error);
// } else {
//
// // 判断一下是否已存在列表中
// boolean find = false;
// if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
// for (int i = 0; i < BaseApp.deviceList.size(); i++) {
// if (ystNum
// .getText()
// .toString()
// .equalsIgnoreCase(
// BaseApp.deviceList.get(i).deviceNum)) {
// find = true;
// break;
// }
// }
// }
//
// if (find) {
// ystNum.setText("");
// BaseApp.showTextToast(JVMainActivity.this,
// R.string.str_device_exsit);
// return;
// }
//
// LoginUtil.deviceName = ystNum.getText().toString().toUpperCase();//
// 云视通号将用户输入的小写均转成大写
// LoginUtil.deviceNum = ystNum.getText().toString().toUpperCase();//
// 云视通号将用户输入的小写均转成大写
// LoginUtil.deviceLoginUser = account.getText().toString();
// LoginUtil.deviceLoginPass = passWord.getText().toString();
// ystNum.setText("");
// account.setText(getResources().getString(R.string.str_default_user));
// passWord.setText(getResources()
// .getString(R.string.str_default_pass));
// // 添加设备线程
// // centerLoading.setVisibility(View.VISIBLE);
// if (null == dialog) {
// dialog = new ProgressDialog(JVMainActivity.this);
// dialog.setCancelable(false);
// }
//
// dialog.setMessage(getResources().getString(
// R.string.str_loading_adddevice));
// dialog.show();
// BaseApp.ADDDEVICE = new Device();
// BaseApp.ADDDEVICE.deviceNum = LoginUtil.deviceNum;
// BaseApp.ADDDEVICE.deviceLoginUser = LoginUtil.deviceLoginUser;
// BaseApp.ADDDEVICE.deviceLoginPwd = LoginUtil.deviceLoginPass;
//
// AddDeviceThread adThread1 = new AddDeviceThread(JVMainActivity.this);
// adThread1.start();
// // requested = false;
//
// // 3G添加设备不广播，走两个流程
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// // Thread thread = new Thread() {
// //
// // @Override
// // public void run() {
// // // TODO Auto-generated method stub
// // addDevice2Way();
// // super.run();
// // }
// //
// // };
// // thread.start();
// addDevice.setBackgroundResource(R.drawable.adddevice);
// addDeviceLayout.setVisibility(View.GONE);
// }
// // else {
// // BaseApp.ADDDEVICE.devicePointCount = 4;
// // }
//
// // if (BaseApp.is3G(JVMainActivity.this, false)) {
// // MyLog.v("3G不广播", "直接添加");
// // Thread thread = new Thread() {
// //
// // @Override
// // public void run() {
// // // TODO Auto-generated method stub
// // addDevice2Way();
// // super.run();
// // }
// //
// // };
// // thread.start();
// // addDevice.setBackgroundResource(R.drawable.adddevice);
// // addDeviceLayout.setVisibility(View.GONE);
// // } else {// wifi添加设备走三个流程
// // if (!JVSUDT.IS_BROADCASTING) {// 没有正在广播中
// // // requested = true;
// //
// // // 添加设备广播搜索通道数量
// // BaseApp.initBroadCast();
// // JVSUDT.ADD_DEVICE = true;// 添加设备广播
// // BaseApp.sendBroadCast();
// //
// // addDevice.setBackgroundResource(R.drawable.adddevice);
// // addDeviceLayout.setVisibility(View.GONE);
// // } else {// 如果有正在广播的，否则通过从服务器上获取通道和通过连接-1通道获取
// // addDevice.setBackgroundResource(R.drawable.adddevice);
// // addDeviceLayout.setVisibility(View.GONE);
// // Thread thread = new Thread() {
// //
// // @Override
// // public void run() {
// // // TODO Auto-generated method stub
// // addDevice2Way();
// // super.run();
// // }
// //
// // };
// // thread.start();
// // }
// //
// // }
//
// // mTimer = new Timer(true);
// // mTimerTask = new BroadCastTask(JVMainActivity.this);
// // mTimer.schedule(mTimerTask, 1000, 1000);
// }
// }
//
// // 从服务器获取或者连-1通道
// // public void addDevice2Way() {
// // // requested = true;
// // BaseApp.ADDDEVICE.getGroupYST();
// // String group = BaseApp.ADDDEVICE.group;
// // int deviceYSTNO = BaseApp.ADDDEVICE.yst;
// // int resultount = JVSUDT.JVC_WANGetChannelCount(group, deviceYSTNO, 5);
// // if (resultount > 0) {// 服务器返回通道数量
// // BaseApp.ADDDEVICE.devicePointCount = resultount;
// // } else {
// // MyLog.v("tags", "连接负一通道");
// // // JVMainActivity.ADDDEVICE.devicePointCount = 0;
// // BaseApp.ADDDEVICE.deviceLocalIp = "";
// // BaseApp.ADDDEVICE.deviceLocalPort = 9101;
// //
// // JVConnectInfo info = new JVConnectInfo();
// // info.setAction(false);
// // info.setByUDP(true);
// // info.setCsNumber(deviceYSTNO);
// // info.setChannel(-1);
// // info.setRemoteIp("");
// // info.setPort(9101);
// // info.setConnType(JVConst.JV_CONNECT_CS);
// // info.setGroup(group);
// // info.setLocalTry(true);
// // info.setSrcName("1");
// //
// // JVSUDT.JVC_Connect(JVConst.OTHER_CONNECT, info.getChannel(), "",
// // info.getPort(), info.getUserName(), info.getPasswd(),
// // info.getCsNumber(), info.getGroup(), info.isLocalTry(),
// // JVConst.JVN_TRYTURN, true, JVNetConst.TYPE_3GMO_UDP, null);
// //
// // info = null;
// // BaseApp.ADDDEVICE.devicePointCount = 4;
// // }
// // }
//
// // 广播搜索 线程
// // private static class BroadCastTask extends TimerTask {
// // private final WeakReference<JVMainActivity> mActivity;
// //
// // public BroadCastTask(JVMainActivity activity) {
// // mActivity = new WeakReference<JVMainActivity>(activity);
// // }
// //
// // @Override
// // public void run() {
// // // TODO Auto-generated method stub
// //
// // JVMainActivity activity = mActivity.get();
// // if (null != activity) {
// // if (BaseApp.ADDDEVICE.devicePointCount > 0) {
// // // obMsg.what=JVConst.MSG_SEARCH_DEVICE;
// //
// BaseApp.mainHandler.sendEmptyMessage(BaseApp.mainHandler.obtainMessage().what
// // = JVConst.MSG_SEARCH_DEVICE);
// // return;
// // }
// //
// // activity.dCount = activity.dCount + 1;
// // if (activity.dCount > 60) {
// //
// BaseApp.mainHandler.sendEmptyMessage(BaseApp.mainHandler.obtainMessage().what
// // = JVConst.MSG_SEARCH_DEVICE_END);
// // return;
// // }
// // }
// //
// // }
// // }
//
// // 添加设备线程
// private static class AddDeviceThread extends Thread {
//
// private final WeakReference<JVMainActivity> mActivity;
//
// public AddDeviceThread(JVMainActivity activity) {
// mActivity = new WeakReference<JVMainActivity>(activity);
// }
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// super.run();
// JVMainActivity activity = mActivity.get();
// if (null != activity) {
// BaseApp.ADDDEVICE.getGroupYST();
// String group = BaseApp.ADDDEVICE.group;
// int deviceYSTNO = BaseApp.ADDDEVICE.yst;
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// int resultount = JVSUDT.JVC_WANGetChannelCount(group,
// deviceYSTNO, 5);
// if (resultount > 0) {// 服务器返回通道数量
// BaseApp.ADDDEVICE.devicePointCount = resultount;
// } else {
// BaseApp.ADDDEVICE.devicePointCount = 4;
// }
// } else {
// if (BaseApp.isConnected(activity)) {
// int count = JVSUDT.JVC_WANGetChannelCount(group,
// deviceYSTNO, 5);
// if (count > 0) {
// BaseApp.ADDDEVICE.devicePointCount = count;
// } else {
// BaseApp.ADDDEVICE.devicePointCount = 4;
// }
// } else {
// BaseApp.ADDDEVICE.devicePointCount = 4;
// }
// }
//
// LoginUtil.deviceHasWifi = "";
//
// ArrayList<ConnPoint> connPointList = new ArrayList<ConnPoint>();
// if (BaseApp.ADDDEVICE.devicePointCount > 0) {
// for (int i = 0; i < BaseApp.ADDDEVICE.devicePointCount; i++) {
// ConnPoint connPoint = new ConnPoint();
// connPoint.deviceID = BaseApp.ADDDEVICE.deviceOID;//
// temObj.getInt("DeviceID");
// connPoint.pointNum = i + 1;// temObj.getInt("PointNum");
// connPoint.pointOwner = BaseApp.ADDDEVICE.deviceOID;// temObj.getInt("Owner");
// connPoint.pointName = BaseApp.ADDDEVICE.deviceNum + "_"
// + (i + 1);// temObj.getString("Name");
// connPoint.pointOID = BaseApp.ADDDEVICE.deviceOID;// temObj.getInt("OID");
// connPoint.isParent = false;
// connPoint.ystNum = deviceYSTNO;
// connPoint.group = group;
// connPointList.add(connPoint);
// }
// }
// BaseApp.ADDDEVICE.pointList = connPointList;
// Device device = BaseApp.addDeviceMethod(BaseApp.ADDDEVICE);
// // Device device =
// // DeviceUtil.addDeviceMethod(BaseApp.ADDDEVICE);
// if (null != device) {
// if (null == BaseApp.deviceList) {
// BaseApp.deviceList = new ArrayList<Device>();
// }
// // if (!BaseApp.LOCAL_LOGIN_FLAG &&
// // !BaseApp.getSP(activity).getBoolean("watchType", true)) {
// // device.onlineState = 1;
// // }
// BaseApp.deviceList.add(0, device);
// BaseApp.setHelpToDevice(device);
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// DeviceUtil.refreshDeviceState(LoginUtil.userName);
// }
// // deviceList.add(device);
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVConst.DEVICE_ADD_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// } else {
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVConst.DEVICE_ADD_FAILED;
// BaseApp.mainHandler.sendMessage(msg);
// }
// }
//
// }
//
// }
//
// // 设备列表点击事件
// OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
// @Override
// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
// long arg3) {
// // TODO Auto-generated method stub
// if (BaseApp.LOCAL_LOGIN_FLAG
// || (null != BaseApp.deviceList && arg2 < BaseApp.deviceList
// .size())) {
// if (currentOpenPosition == arg2) {// 已经展开了，要收缩回去
// currentOpenPosition = -1;
// deviceAdapter.openPos = -1;
// arg1.findViewById(R.id.deviceconnect)
// .setBackgroundResource(
// R.drawable.wifi_flag_close_bg);
// } else {// 展开
// currentOpenPosition = arg2;
// deviceAdapter.openPos = arg2;
// arg1.findViewById(R.id.deviceconnect)
// .setBackgroundResource(R.drawable.wifi_flag_open_bg);
// }
// deviceAdapter.changePos = -1;
// deviceAdapter.notifyDataSetChanged();
// deviceListView.setSelection(currentOpenPosition);
// }
// }
//
// };
//
// // 防止重复打开新品
// public static boolean openNewPro = false;
//
// @Override
// public void onConfigurationChanged(Configuration newConfig) {
// // TODO Auto-generated method stub
// super.onConfigurationChanged(newConfig);
// MyLog.e("tags", "jvmain onconfig");
// }
//
// // 进主界面自动获取设备及通道数据线程
// private static class UpdateData extends Thread {
//
// private final WeakReference<JVMainActivity> mActivity;
//
// public UpdateData(JVMainActivity activity) {
// mActivity = new WeakReference<JVMainActivity>(activity);
// }
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// super.run();
// JVMainActivity activity = mActivity.get();
// if (null != activity) {
// try {
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {
// for (int j = 0; j < BaseApp.deviceList.size(); j++) {
// Device device = LoginUtil
// .getConnDetails(BaseApp.deviceList.get(j));
// device.deviceImageList = new ArrayList<String>();
//
// for (int i = 0; i < device.pointList.size(); i++) {
// if (null != device.pointList.get(i).connImage
// && !"".equalsIgnoreCase(device.pointList
// .get(i).connImage.imageUrl)) {
// String imageUrl = Url.DOWNLOAD_IMAGE_URL
// + device.pointList.get(i).connImage.imageUrl;
// device.deviceImageList.add(imageUrl);// 一个通道就对应一张截图
// }
//
// }
//
// }
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVConst.DEVICE_DATA_REFRESH_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// }
// }
// }
//
// // 绑定账号
// class BindThread extends Thread {
// @Override
// public void run() {
// // TODO Auto-generated method stub
// super.run();
// String open_id = JVConst.OPEN_ID;
// // 绑定正确的账号
// LoginUtil.open_id = open_id;
// String userName = LoginUtil.userName;
// int res = LoginUtil.updateOpenID(open_id, LoginUtil.userName);
// // 解绑公共账号
// LoginUtil.open_id = "";
// LoginUtil.userName = publicAccountName;
// LoginUtil.updateOpenID(LoginUtil.open_id, publicAccountName);//
// 将公共账号绑定的open_id置空
//
// LoginUtil.userName = userName;
// if (1 == res) {
// // MyLog.v("账号绑定成功", "");
// // 绑定成功获取sessionkey
// String sessionKey = LoginUtil.loginRequest(LoginUtil.userName,
// LoginUtil.Md5(LoginUtil.passWord));
// Message msg = BaseApp.mainHandler.obtainMessage();
// if (null != sessionKey && 32 == sessionKey.length()) {
// msg.what = JVConst.MAIN_ACCOUNT_BIND_SUCCESS;
// // 绑定成功重新刷新一下数据
// BaseApp.deviceList = LoginUtil.getAllDevice();
// BaseApp.allConnPointList = LoginUtil.getAllConnPoint();
// addPointToDevice();
// // deviceList = JVMainActivity.deviceList;
// } else {
// msg.what = JVConst.MAIN_ACCOUNT_BIND_FAILED;
// }
// BaseApp.mainHandler.sendMessage(msg);
// } else {
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVConst.MAIN_ACCOUNT_BIND_FAILED;
// BaseApp.mainHandler.sendMessage(msg);
// }
//
// }
// };
//
// // 添加设备成功后
// // public void addDeviceSucc() {
// // // 设置小助手
// // // byte allByte[] = new byte[528];
// // // BaseApp.ADDDEVICE.getGroupYST();
// // // JVLittleTipsPacket packet = new JVLittleTipsPacket(256 * 2 + 16);
// // // packet.setChGroup(BaseApp.ADDDEVICE.group);
// // // packet.setnYSTNO(BaseApp.ADDDEVICE.yst);
// // // packet.setnChannel(1);
// // // packet.setChPName(BaseApp.ADDDEVICE.deviceLoginUser);
// // // packet.setChPWord(BaseApp.ADDDEVICE.deviceLoginPwd);
// // // packet.setnConnectStatus(0);
// // // System.arraycopy(packet.pack().data, 0, allByte, 0, packet.getLen());
// // // JVSUDT.JVC_SetHelpYSTNO(allByte, allByte.length);
// // // MyLog.e("添加完设备设置小助手--", BaseApp.ADDDEVICE.deviceNum);
// // // allByte = null;
// //
// // if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地登录,保存到本地数据库
// // if (null == BaseApp.ADDDEVICE.pointList) {
// // BaseApp.ADDDEVICE.pointList = new ArrayList<ConnPoint>();
// // } else {
// // BaseApp.ADDDEVICE.pointList.clear();
// // }
// //
// // JVConnectInfo jvc = BaseApp.ADDDEVICE.toJVConnectInfo();
// // JVConnectInfo resultInfo = BaseApp.queryChannelByYst(jvc);
// // if (null == resultInfo) {
// // jvc.setParent(true);
// // int res = BaseApp.addItem(jvc);
// //
// // MyLog.e("添加设备：", "" + res);
// // if (res > 0) {
// // ArrayList<JVConnectInfo> infoList = new ArrayList<JVConnectInfo>();
// // for (int i = 0; i < BaseApp.ADDDEVICE.devicePointCount; i++) {
// // ConnPoint connPoint = new ConnPoint();
// // connPoint.deviceID = 0;
// // connPoint.pointNum = i;
// // connPoint.pointOwner = 0;
// // connPoint.pointName = BaseApp.ADDDEVICE.deviceNum + "_" + (i + 1);
// // connPoint.pointOID = 0;
// // connPoint.ystNum = BaseApp.ADDDEVICE.yst;
// // BaseApp.ADDDEVICE.pointList.add(connPoint);
// //
// // JVConnectInfo pointInfo = (JVConnectInfo) jvc.clone();
// // pointInfo.setChannel(i + 1);
// // pointInfo.setNickName(connPoint.pointName);
// // pointInfo.setParent(false);
// // pointInfo.setPrimaryID(System.currentTimeMillis());
// // infoList.add(pointInfo);
// // // BaseApp.addItem(jvc); //添加通道
// // }
// // if (null != infoList && 0 != infoList.size()) {
// // BaseApp.addJVCInfos(infoList);
// // }
// // }
// // }
// // BaseApp.deviceList = getDataFromDB();// 从数据库中查询所有设备
// // }
// //
// // // JVMainActivity.this.leftFragement.refreshData();
// // if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
// // deviceAdapter.setData(BaseApp.deviceList);
// // deviceListView.setAdapter(deviceAdapter);
// // }
// // deviceListView.setOnItemClickListener(mOnItemClickListener);
// // deviceAdapter.changePos = -1;
// // deviceAdapter.notifyDataSetChanged();
// //
// // if (null != dialog && dialog.isShowing()) {
// // dialog.dismiss();
// // dialog = null;
// // }
// // // 如果设备有wifi，上传wifi信息
// // if (1 == BaseApp.deviceList.get(0).hasWifi && !BaseApp.LOCAL_LOGIN_FLAG)
// // {
// // ArrayList<Device> list = new ArrayList<Device>();
// // list.add(BaseApp.deviceList.get(0));
// // LoginUtil.wifiList = list;
// // new Thread() {
// // public void run() {
// // LoginUtil.setWifi(LoginUtil.wifiList);
// // };
// // }.start();
// // }
// // }
//
// @Override
// public void onResume() {
//
// // 正常播放开启五分钟广播
// if (BaseApp.is3G(JVMainActivity.this, false)) {
// MyLog.e("3G", "不广播");
// } else if (!BaseApp.LOCAL_LOGIN_FLAG) {
// fiveMinTimer = new Timer();
// fiveMinTask = new CheckBroacastData(this);
// fiveMinTimer.schedule(fiveMinTask, TIME_SPAN, TIME_SPAN);
// }
// if (BaseApp.LOADIMAGE) {
// changeFlag = true;
// if (null != imageSwitchTimer) {
// imageSwitchTimer.cancel();
// imageSwitchTimer = new Timer();
// }
// if (imageSwitchTask != null) {
// imageSwitchTask.cancel(); // 将原任务从队列中移除
// }
// imageSwitchTask = new TimerTask() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// // MyLog.e("五秒钟时间到", "--------------------");
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVConst.DEVICE_IMAGE_CHANGE;
// BaseApp.mainHandler.sendMessage(msg);
// }
//
// };
// if (null != imageSwitchTimer) {
// imageSwitchTimer.schedule(imageSwitchTask, 20000, 20000);
// }
// } else {
// changeFlag = false;
// }
// if (null != deviceAdapter) {
// deviceAdapter.changePos = -1;
// if (null != BaseApp.deviceList) {
// // if
// // (BaseApp.getSP(getApplicationContext()).getBoolean("watchType",
// // true)) {
// BaseApp.deviceList = BaseApp
// .orderByOnlineState(BaseApp.deviceList);
// // } else {
// // BaseApp.deviceList =
// // BaseApp.allDeviceOnline(BaseApp.deviceList);
// // }
// deviceAdapter.setData(BaseApp.deviceList);
// deviceListView.setAdapter(deviceAdapter);
// }
// deviceAdapter.notifyDataSetChanged();
// showNoDevice();
// }
// // JVMainActivity.this.leftFragement.refreshData();
// deviceListView.setSelection(currentOpenPosition);
// deviceListView.setOnItemClickListener(mOnItemClickListener);
// deviceAdapter.changePos = -1;
//
// // 回来之后广播一下设备列表
// if (BaseApp.finishSetting) {
// BaseApp.finishSetting = false;
// Message msg = BaseApp.mainHandler.obtainMessage();
// BaseApp.broadDeviceList();
// msg.what = JVConst.DEVICE_SEARCH_BROADCAST;// 发广播搜索局域网设备（非3g搜索局域网）
// BaseApp.broadcastState = JVConst.UPDATE_DEVICE_STATE_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// }
// if (!isOnCreate && helpPager.getVisibility() == View.GONE) {
// helpShow();
// }
//
// super.onResume();
// // MobclickAgent.onResume(this);
// }
//
// @Override
// public void onStop() {
// changeFlag = false;
// super.onStop();
// }
//
// public int NEW_PIC_SIZE = 0;
//
// // 获取新品图片
// private static class NewProThread extends Thread {
//
// private final WeakReference<JVMainActivity> mActivity;
//
// public NewProThread(JVMainActivity activity) {
// mActivity = new WeakReference<JVMainActivity>(activity);
// }
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// JVMainActivity activity = mActivity.get();
// if (null != activity) {
// if (!activity.refreshFlag) {
// try {
// if (activity.screenWidth < 480) {
// activity.NEW_PIC_SIZE = 1;
// // 防止重复
// if (null == activity.newProList) {
// activity.newProList = new ArrayList<NewProduct>();
// }
// activity.newProList.clear();
//
// activity.newProList = LoginUtil.getNewProducts(
// BaseApp.getLan(), 1);
// } else {
// activity.NEW_PIC_SIZE = 2;
// // 防止重复
// if (null == activity.newProList) {
// activity.newProList = new ArrayList<NewProduct>();
// }
// activity.newProList.clear();
// activity.newProList = LoginUtil.getNewProducts(
// BaseApp.getLan(), 2);
// }
//
// for (int i = 0; i < activity.newProList.size(); i++) {
// // 调用这个方法如果本地已存在不会重复下载
// Bitmap bitmap = ImageUtil
// .getbitmapAndwrite(
// activity,
// Url.DOWNLOAD_IMAGE_URL
// + activity.newProList
// .get(i).newProImgUrl);
//
// if (null != bitmap && 0 == activity.picHeight) {
// try {
// activity.picHeight = bitmap.getHeight()
// * activity.screenWidth
// / bitmap.getWidth();
// if (0 != activity.picHeight) {
// Message msg = BaseApp.mainHandler
// .obtainMessage();
// Bundle bundle = new Bundle();
// bundle.putInt("NewProHeight",
// activity.picHeight);
// msg.setData(bundle);
// msg.what = JVConst.NEW_PRO_REFRESH_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// }
// }
//
// } catch (Exception e) {
// // TODO: handle exception
// e.printStackTrace();
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVConst.NEW_PRO_REFRESH_FAILED;
// BaseApp.mainHandler.sendMessage(msg);
// }
// }
//
// }
// }
// }
//
// // 读取通道数据线程，读到数据后给左边和中间界面赋值
// private static class DataThread extends Thread {
//
// private final WeakReference<JVMainActivity> mActivity;
//
// public DataThread(JVMainActivity activity) {
// mActivity = new WeakReference<JVMainActivity>(activity);
// }
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// JVMainActivity activity = mActivity.get();
// if (null != activity) {
//
// try {
// if (!BaseApp.LOCAL_LOGIN_FLAG) {// 非本地登录，无论是否刷新都执行
// // 获取所有设备列表和通道列表
// activity.deviceAdapter.changePos = -1;
// // 如果设备请求失败，多请求一次
// ArrayList<Device> temlist = BaseApp.deviceList;
// BaseApp.deviceList = DeviceUtil
// .getUserDeviceList(LoginUtil.userName);
// if (null == BaseApp.deviceList
// || 0 == BaseApp.deviceList.size()) {
// BaseApp.deviceList = DeviceUtil
// .getUserDeviceList(LoginUtil.userName);
// }
// if (null == BaseApp.deviceList
// || 0 == BaseApp.deviceList.size()) {
// BaseApp.deviceList = temlist;
// }
// Message msg = BaseApp.mainHandler.obtainMessage();
// msg.what = JVAccountConst.REFERSH_DEVICE_LIST_ONLY;
// BaseApp.mainHandler.sendMessage(msg);
// // BaseApp.allConnPointList =
// // LoginUtil.getAllConnPoint();
// // if (null != BaseApp.allConnPointList
// // && 0 != BaseApp.allConnPointList.size()) {
// // activity.hasConnPoint = true;
// // }
// // activity.addPointToDevice();
// for (int i = 0; i < BaseApp.deviceList.size(); i++) {
// BaseApp.deviceList.get(i).pointList = DeviceUtil
// .getDevicePointList(BaseApp.deviceList
// .get(i).deviceNum);
// }
// } else if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地登录且是刷新
// if (activity.refreshFlag) {
// BaseApp.deviceList = activity.getDataFromDB();// 从数据库中查询所有设备
// }
// }
//
// Message msg = BaseApp.mainHandler.obtainMessage();
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {// 获取设备成功,去广播设备列表
// // msg.what
// // =
// // DEVICE_GETDATA_SUCCESS;
//
// if (BaseApp.is3G(activity, false)) {// 3G直接加载设备
// msg.what = JVConst.DEVICE_GETDATA_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg);
// } else {
// BaseApp.broadDeviceList();
// msg.what = JVConst.DEVICE_SEARCH_BROADCAST;// 发广播搜索局域网设备（非3g搜索局域网）
// BaseApp.broadcastState = JVConst.UPDATE_LIST_BROADCAST;
// BaseApp.mainHandler.sendMessage(msg);
// }
//
// } else if (null != BaseApp.deviceList
// && 0 == BaseApp.deviceList.size()) {// 无数据
// msg.what = JVConst.DEVICE_NO_DEVICE;
// BaseApp.mainHandler.sendMessage(msg);
// } else {// 获取设备失败
// msg.what = JVConst.DEVICE_GETDATA_FAILED;
// BaseApp.mainHandler.sendMessage(msg);
// }
// } catch (Exception e) {
// // TODO: handle exception
// e.printStackTrace();
// }
//
// super.run();
// }
//
// }
//
// }
//
// public static String findLetter(String str) {
// if (str == null || str.length() == 0) {
// return str;
// }
// char[] chs = str.toCharArray();
// int k = 0;
// for (int i = 0; i < chs.length; i++) {
// if (!isAsciiLetter(chs[i])) {
// break;
// }
// k++;
// }
// return new String(chs, 0, k);
// }
//
// private static boolean isAsciiLetter(char c) {
// return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
// }
//
// public static class MainHandler extends Handler {
//
// private final WeakReference<JVMainActivity> mActivity;
//
// public MainHandler(JVMainActivity activity) {
// mActivity = new WeakReference<JVMainActivity>(activity);
// }
//
// @Override
// public void handleMessage(Message msg) {
// // TODO Auto-generated method stub
// super.handleMessage(msg);
// final JVMainActivity activity = mActivity.get();
// if (null != activity && !activity.isFinishing()) {
// final String lang = String.valueOf(BaseApp.getLan());
//
// try {
// switch (msg.what) {
// case JVConst.CHECK_UPDATE_SUCCESS:// 检查到更新
// Bundle checkUpdateData = msg.getData();
// JVUpdate jvUpdate = new JVUpdate(activity);
// jvUpdate.checkUpdateInfo(checkUpdateData
// .getString("updateContent"));
// break;
// case JVConst.CHECK_UPDATE_FAILED:// 检查更新失败
// break;
// case JVConst.CHECK_NO_UPDATE:// 未检查到更新
// break;
// case JVConst.DEVICE_SEARCH_BROADCAST:
// for (int i = 0; i < BaseApp.deviceList.size(); i++) {
// BaseApp.deviceList.get(i).isBroadcast = false;
// }
// // 添加设备广播搜索通道数量
// BaseApp.initBroadCast();
// JVSUDT.BROADCAST_DEVICELIST_FLAG = true;// 广播设备列表
// BaseApp.sendBroadCast();
// break;
//
// case JVConst.DEVICE_GETDATA_SUCCESS:// 成功获取设备信息
//
// if (!activity.isOnCreate) {
// activity.deviceListView.completeRefreshing();
// activity.refreshFlag = false;
// } else {
// activity.isOnCreate = false;
// }
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// // if
// // (BaseApp.getSP(activity).getBoolean("watchType",
// // true)) {
// BaseApp.deviceList = BaseApp
// .orderByOnlineState(BaseApp.deviceList);
// // } else {
// // BaseApp.deviceList =
// // BaseApp.allDeviceOnline(BaseApp.deviceList);
// // }
// }
// activity.deviceAdapter.setData(BaseApp.deviceList);
// activity.deviceListView
// .setAdapter(activity.deviceAdapter);
// activity.deviceListView
// .setOnItemClickListener(activity.mOnItemClickListener);
//
// }
//
// // 非本地登录
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// UpdateData updateData = new UpdateData(activity);
// updateData.start();
// // 获取新品
// if (!activity.refreshFlag) {
// NewProThread npt = new NewProThread(activity);
// npt.start();
// }
//
// }
//
// activity.deviceAdapter.changePos = -1;
// activity.deviceAdapter.notifyDataSetChanged();
// activity.showNoDevice();
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// activity.helpShow();
// BaseApp.showTextToast(activity,
// R.string.str_data_load_success);
//
// break;
// case JVConst.DEVICE_NO_DEVICE:
// if (!activity.isOnCreate) {
// activity.deviceListView.completeRefreshing();
// activity.refreshFlag = false;
// } else {
// activity.isOnCreate = false;
// }
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// activity.helpShow();
// BaseApp.showTextToast(activity,
// R.string.str_data_load_nodevice);
// break;
// case JVConst.DEVICE_GETDATA_FAILED:
// if (!activity.isOnCreate) {
// activity.deviceListView.completeRefreshing();
// activity.refreshFlag = false;
// } else {
// activity.isOnCreate = false;
// }
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// BaseApp.showTextToast(activity,
// R.string.str_data_load_failed);
// activity.deviceAdapter.changePos = -1;
// activity.helpShow();
// break;
// case JVConst.DEVICE_DATA_REFRESH:
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {
// activity.deviceAdapter.setData(BaseApp.deviceList);
// activity.deviceListView
// .setAdapter(activity.deviceAdapter);
// }
// activity.deviceListView
// .setOnItemClickListener(activity.mOnItemClickListener);
// activity.deviceAdapter.openPos = 0;
// activity.currentOpenPosition = 0;
// activity.deviceAdapter.notifyDataSetChanged();
// activity.showNoDevice();
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// break;
//
// case JVConst.RELOGIN_FAILED:
// BaseApp.showTextToast(activity,
// R.string.login_str_login_failed);
// break;
//
// // case JVConst.MSG_SEARCH_DEVICE:// 成功搜索到设备
// // activity.dCount = 0;
// // if (activity.mTimer != null) {
// // activity.mTimer.cancel();
// // activity.mTimer = null;
// // }
// // if (activity.mTimerTask != null) {
// // activity.mTimerTask.cancel();
// // activity.mTimerTask = null;
// // }
// //
// // // JVSUDT.JVC_DisConnect(JVConst.OTHER_CONNECT);// -1通道
// //
// // if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地登录,保存到本地数据库
// // activity.addDeviceSucc();
// // } else {
// // AddDeviceThread adThread1 = new AddDeviceThread(activity,
// // BaseApp.ADDDEVICE.devicePointCount);
// // adThread1.start();
// // }
// // break;
// //
// // case JVConst.MSG_SEARCH_DEVICE_END:// 搜索设备超时，自动添加
// //
// // MyLog.e("搜索设备超时，自动添加", "搜索设备超时，自动添加");
// // // JVSUDT.ADD_DEVICE = false;
// // activity.dCount = 0;
// // if (activity.mTimer != null) {
// // activity.mTimer.cancel();
// // activity.mTimer = null;
// // }
// // if (activity.mTimerTask != null) {
// // activity.mTimerTask.cancel();
// // activity.mTimerTask = null;
// // }
// // // 断开-1通道
// // JVSUDT.JVC_DisConnect(JVConst.OTHER_CONNECT);// -1通道
// // BaseApp.ADDDEVICE.devicePointCount = 4;
// //
// // if (BaseApp.LOCAL_LOGIN_FLAG) {// 本地登录,保存到本地数据库
// // activity.addDeviceSucc();
// // } else {
// // AddDeviceThread adThread2 = new AddDeviceThread(activity,
// // BaseApp.ADDDEVICE.devicePointCount);
// // adThread2.start();
// // }
// //
// // break;
// case JVConst.DEVICE_DATA_REFRESH_SUCCESS:
// if (BaseApp.LOADIMAGE) {// 加载图片时才切换动画
// if (null != activity.imageSwitchTimer) {
// activity.imageSwitchTimer.cancel();
// }
// activity.imageSwitchTimer = new Timer();
// if (BaseApp.LOADIMAGE) {
// if (activity.imageSwitchTask != null) {
// activity.imageSwitchTask.cancel(); // 将原任务从队列中移除
// }
// activity.imageSwitchTask = new TimerTask() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// // MyLog.e("五秒钟时间到",
// // "--------------------");
// Message msg = BaseApp.mainHandler
// .obtainMessage();
// msg.what = JVConst.DEVICE_IMAGE_CHANGE;
// BaseApp.mainHandler.sendMessage(msg);
// }
//
// };
// if (null != activity.imageSwitchTimer) {
// activity.imageSwitchTimer.schedule(
// activity.imageSwitchTask, 20000,
// 20000);
// }
// }
// }
//
// break;
// case JVConst.DEVICE_IMAGE_CHANGE:
//
// if (activity.changeFlag) {
// Random ran = new Random();
// int randomPos = 0;
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {
// randomPos = ran.nextInt(BaseApp.deviceList
// .size()); // 获得随机数
// }
// activity.deviceAdapter.changePos = randomPos;
// activity.deviceAdapter.notifyDataSetChanged();
// activity.showNoDevice();
// }
//
// break;
// case JVConst.DEVICE_DATA_REFRESH_FAILED:
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// break;
// case JVConst.NEW_PRO_CHANGE_POSITION:
// Bundle data = msg.getData();
// int index = 0;
// if (null != data) {
// if (0 != activity.newProList.size()) {
// index = data.getInt("CurrentAdapterIndex", 0)
// % activity.newProList.size();
// }
//
// }
// if (null != activity.newProList
// && 0 != activity.newProList.size()) {
// for (int i = 0; i < activity.newProList.size(); i++) {
// activity.mIndicatorList.get(i)
// .setImageResource(
// R.drawable.feature_point1);
// }
// activity.mIndicatorList.get(index)
// .setImageResource(
// R.drawable.feature_point_cur1);
// }
//
// break;
// case JVConst.NEW_PRO_REFRESH_SUCCESS:
//
// Bundle bundle = msg.getData();
// if (null != bundle) {
// int height = bundle.getInt("NewProHeight");
// RelativeLayout.LayoutParams reParams = new RelativeLayout.LayoutParams(
// ViewGroup.LayoutParams.MATCH_PARENT, height);
// activity.newProLayout.setLayoutParams(reParams);
// }
// if (null != activity.newProList
// && 0 != activity.newProList.size()) {
// // 淘宝首页图片样式
// activity.imageAdapter.setData(activity.newProList);
// activity.viewFlow.setAdapter(activity.imageAdapter);
// activity.viewFlow.setmSideBuffer(1);
// activity.viewFlow.setTimeSpan(10000);
// activity.viewFlow.setSelection(0);// newProList.size()
// // * 1000); //
// // 设置初始位置
// activity.viewFlow.startAutoFlowTimer(); // 启动自动播放
// activity.viewFlow.activityTag = 0;
// // viewFlow.setOnItemClickListener(newProClickListener);
// // 加入小圆点
// if (null != activity.newProList
// && 0 != activity.newProList.size()) {
// for (int i = 0; i < activity.newProList.size(); i++) {
// ImageView indicator = new ImageView(
// activity);
// if (i == 0) {
// indicator
// .setImageResource(R.drawable.feature_point_cur1);
// } else {
// indicator
// .setImageResource(R.drawable.feature_point1);
// }
// LayoutParams param = new LayoutParams(
// LayoutParams.WRAP_CONTENT,
// LayoutParams.WRAP_CONTENT);
// indicator.setPadding(0, 0, 10, 0);
// indicator.setLayoutParams(param);
// activity.indicatorlayout.addView(indicator);
// // 把小圆点图片存入集合,好让切换图案片的时候动态改变小圆点背景
// activity.mIndicatorList.add(indicator);
// }
// }
// }
//
// break;
// case JVConst.NEW_PRO_DETAIL:
// // 非公共版本的新品可以点进去
// if (!BaseApp.PUBLIC_VERSION) {
// Bundle data1 = msg.getData();
// int arg2 = 0;
// if (null != data1) {
// if (0 != activity.newProList.size()) {
// arg2 = data1.getInt("CurrentAdapterIndex",
// 0) % activity.newProList.size();
// }
//
// }
//
// Intent intent = new Intent();
// intent.setClass(activity, JVNPDetailActivity.class);
// int pos = arg2;
// intent.putExtra("NEWLAN", BaseApp.getLan());
// intent.putExtra("NEWSIZE", activity.NEW_PIC_SIZE);
// intent.putExtra("NEWPOID",
// activity.newProList.get(pos).newProOID);
//
// activity.startActivity(intent);
// }
//
// break;
// case JVConst.NEW_PRO_REFRESH_FAILED:
// break;
// case JVConst.MAIN_ACCOUNT_BIND_SUCCESS:
// // 绑定成功重新刷新一下数据
// // setData(deviceList);
// Message msg1 = new Message();
// msg1.what = JVConst.DEVICE_GETDATA_SUCCESS;
// BaseApp.mainHandler.sendMessage(msg1);
//
// BaseApp.showTextToast(activity,
// R.string.login_str_bind_success);
//
// break;
// case JVConst.MAIN_ACCOUNT_BIND_FAILED:
// BaseApp.showTextToast(activity,
// R.string.login_str_bind_failed);
// break;
// case JVConst.DEVICE_ADD_SUCCESS:
// // activity.addDeviceSucc();
// if (!BaseApp.is3G(activity, false)) {// 非3G广播一下设备列表
//
// BaseApp.initBroadCast();
// JVSUDT.ADD_DEVICE = true;// 添加设备广播
// BaseApp.sendBroadCast();
// } else {
// activity.deviceAdapter.notifyDataSetChanged();
// activity.showNoDevice();
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// }
//
// // if (null != activity.dialog &&
// // activity.dialog.isShowing()) {
// // activity.dialog.dismiss();
// // activity.dialog = null;
// // }
// BaseApp.showTextToast(activity,
// R.string.login_str_device_add_success);
// //
// showTextToast(getResources().getString(R.string.login_str_device_add_success));
// break;
// case JVConst.DEVICE_ADD_FAILED:
// if (null != activity.dialog
// && activity.dialog.isShowing()) {
// activity.dialog.dismiss();
// activity.dialog = null;
// }
// BaseApp.showTextToast(activity,
// R.string.login_str_device_add_failed);
// break;
// case JVConst.RIGHT_SLIDE_SHOW_HELP:
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()) {
// // 首次登陆
// if (null != BaseApp.getSP(activity)
// && BaseApp.getSP(activity).getBoolean(
// "ShowLeftHelp", true)) {
// // 显示帮助图
// if (null == activity.helpDialog) {
// activity.helpDialog = new Dialog(activity,
// R.style.DialogFullScreen);
// }
// activity.helpDialog
// .setContentView(R.layout.help_layout_2);
// final RelativeLayout help = (RelativeLayout) activity.helpDialog
// .findViewById(R.id.help2);
// help.setVisibility(View.VISIBLE);
// help.setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View v) {
// help.setVisibility(View.GONE);
// Message msg1 = new Message();
// msg1.what = JVConst.INVISIBLE_HELP;
// BaseApp.mainHandler.sendMessage(msg1);
// }
// });
// activity.helpDialog.show();
// }
// }
// break;
// case JVConst.INVISIBLE_HELP:
// BaseApp.getEditor(activity).putBoolean("ShowLeftHelp",
// false);
// BaseApp.getEditor(activity).commit();
// activity.helpDialog.dismiss();
// activity.helpDialog = null;
// break;
// case JVConst.RECEVICE_PUSH_MSG:
//
// Bundle pushBundle = msg.getData();
// String pushMessage = "";
// if (null != pushBundle) {
// pushMessage = pushBundle.getString("PushMsg");
// }
//
// String ns = Context.NOTIFICATION_SERVICE;
// BaseApp.mNotificationManager = (NotificationManager) activity
// .getSystemService(ns);
// // 定义通知栏展现的内容信息
// int icon = R.drawable.notification_icon;
// CharSequence tickerText = activity.getResources()
// .getString(R.string.str_alarm);
// long when = System.currentTimeMillis();
// Notification notification = new Notification(icon,
// tickerText, when);
//
// notification.defaults |= Notification.DEFAULT_SOUND;// 声音
// // notification.defaults |=
// // Notification.DEFAULT_LIGHTS;//灯
// // notification.defaults |=
// // Notification.DEFAULT_VIBRATE;//震动
//
// // 定义下拉通知栏时要展现的内容信息
// Context context = activity.getApplicationContext();
// CharSequence contentTitle = activity.getResources()
// .getString(R.string.str_alarm_info);
// CharSequence contentText = pushMessage;
// Intent notificationIntent = new Intent(activity,
// JVNoticeActivity.class);
// PendingIntent contentIntent = PendingIntent
// .getActivity(activity, 0, notificationIntent, 0);
// notification.setLatestEventInfo(context, contentTitle,
// contentText, contentIntent);
//
// // 用mNotificationManager的notify方法通知用户生成标题栏消息通知
// BaseApp.mNotificationManager.notify(0, notification);
//
// // 刷新推送消息列表
// if (null != BaseApp.pushHandler) {
// Message msgRefresh = BaseApp.pushHandler
// .obtainMessage();
// msgRefresh.what = JVConst.REFRESH_PUSH_MSG;
// BaseApp.pushHandler.sendMessage(msgRefresh);
// }
//
// // 刷新纪录条数
// if (null != BaseApp.moreHandler) {
// Message msgCount = BaseApp.moreHandler
// .obtainMessage();
// msgCount.what = JVConst.REFRESH_PUSH_COUNT;
// BaseApp.moreHandler.sendMessage(msgCount);
// }
//
// break;
// case JVConst.PUSH_DISCONNECT:
//
// // 允许获取推送消息
// if (msg.arg1 == 1// 因断网导致的推送断开
// && null != BaseApp.getSP(activity)
// && BaseApp.getSP(activity).getBoolean(
// "PushMessage", false)
// && !BaseApp.LOCAL_LOGIN_FLAG) {
// // boolean closeFlag = JVClient.JVStopKeepOnline();
// //
// // // if(null != BaseApp.mNotificationManager){
// // // BaseApp.mNotificationManager.cancel(0);
// // // }
// //
// // MyLog.v("收到底层断开推送", closeFlag+"");
// // BaseApp.pushList.clear();
// // // JVClient.JVRelease();
// // // MyLog.v("注销8资源释放", closeFlag+"");
// // if(closeFlag){
// BaseApp.pushList.clear();
//
// Thread thread = new Thread() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// LoginUtil.loginSuccessToConfirm(
// activity.statusHashMap.get(Consts.IMEI), lang,
// LoginUtil.userName, "false");
// super.run();
// }
//
// };
// thread.start();
// // }
// }
//
// // 能连上网，再打开一次
// if (BaseApp.isConnected(activity)) {
// BaseApp.errorCount++;
//
// if (BaseApp.errorCount >= 3) {
// break;
// }
//
// boolean resOpen = JVClient.JVKeepOnline(
// Url.ALARM_PUSH_IP, Url.ALARM_PUSH_PORT,
// activity.statusHashMap.get(Consts.IMEI),
// Url.ALARM_PUSH_APPTYPE);
//
// if (resOpen) {
// Thread thread = new Thread() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// LoginUtil.loginSuccessToConfirm(
// activity.statusHashMap.get(Consts.IMEI),
// lang, LoginUtil.userName,
// "true");
// }
// };
// thread.start();
// }
//
// MyLog.v("能连上网，再打开一次", resOpen + "");
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", resOpen);
// BaseApp.getEditor(activity).commit();
//
// } else {// 连不上网
// MyLog.v("连不上网", "连不上网");
//
// Thread thread = new Thread() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// LoginUtil.loginSuccessToConfirm(
// activity.statusHashMap.get(Consts.IMEI), lang,
// LoginUtil.userName, "false");
// }
// };
// thread.start();
//
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", false);
// BaseApp.getEditor(activity).commit();
//
// BaseApp.showTextToast(activity,
// R.string.str_net_push_error);
// // 没网开线程判断网络
// activity.oneMinTask = new NetCheckTask(activity);
// activity.oneMinTimer = new Timer();
// activity.oneMinTimer.schedule(activity.oneMinTask,
// 10 * 1000, 10 * 1000);
// }
//
// if (null != BaseApp.moreHandler) {
// Message msgMore = BaseApp.moreHandler
// .obtainMessage();
// msgMore.what = JVConst.REFRESH_PUSH_COUNT;
// BaseApp.moreHandler.sendMessage(msgMore);// 刷新界面
// }
//
// break;
// case JVConst.PUSH_NET_RESTORE:// 网络恢复
//
// if (null != activity.oneMinTask) {
// activity.oneMinTask.cancel();
// }
// activity.oneMinTask = null;
//
// if (null != activity.oneMinTimer) {
// activity.oneMinTimer.cancel();
// }
// activity.oneMinTimer = null;
//
// // 没打开推送
// if (null != BaseApp.getSP(activity)
// && !BaseApp.getSP(activity).getBoolean(
// "PushMessage", false)
// && !BaseApp.LOCAL_LOGIN_FLAG) {
// boolean resOpen = JVClient.JVKeepOnline(
// Url.ALARM_PUSH_IP, Url.ALARM_PUSH_PORT,
// activity.statusHashMap.get(Consts.IMEI),
// Url.ALARM_PUSH_APPTYPE);
//
// if (resOpen) {
// Thread thread = new Thread() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// LoginUtil.loginSuccessToConfirm(
// activity.statusHashMap.get(Consts.IMEI),
// lang, LoginUtil.userName,
// "true");
// }
// };
// thread.start();
//
// }
// MyLog.v("非首次登陆获取本地保存的是否接收推送", resOpen + "");
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", resOpen);
// BaseApp.getEditor(activity).commit();
//
// if (null != BaseApp.moreHandler) {
// Message msgMore = BaseApp.moreHandler
// .obtainMessage();
// msgMore.what = JVConst.REFRESH_PUSH_COUNT;
// BaseApp.moreHandler.sendMessage(msgMore);// 刷新界面
// }
//
// BaseApp.showTextToast(activity,
// R.string.str_net_push_ok);
// }
//
// break;
// case JVConst.PUSH_NET_DISCONNECT:// 网络依然断开
// break;
// case JVConst.MAIN_OPEN_PUSH_WEBCC_SUCCESS:// 主界面交互成功
// BaseApp.WEBCC_INTERFACE = false;
// int onLineState = msg.arg1;
// if (onLineState == BaseApp.WEBCC_ONLINE) {// 上线成功
// boolean resOpen = JVClient.JVKeepOnline(
// Url.ALARM_PUSH_IP, Url.ALARM_PUSH_PORT,
// activity.statusHashMap.get(Consts.IMEI),
// Url.ALARM_PUSH_APPTYPE);
//
// MyLog.v("非首次登陆获取本地保存的是否接收推送", resOpen + "");
// if (resOpen) {
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", resOpen);
// BaseApp.getEditor(activity).commit();
// }
// } else if (onLineState == BaseApp.WEBCC_OFFLINE) {// 下线成功
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", false);
// BaseApp.getEditor(activity).commit();
// }
//
// // 更多界面
// if (null != BaseApp.moreHandler) {
// Message msgMore = BaseApp.moreHandler
// .obtainMessage();
// msgMore.what = JVConst.REFRESH_PUSH_COUNT;
// BaseApp.moreHandler.sendMessage(msgMore);
// }
// break;
// case JVConst.MAIN_OPEN_PUSH_WEBCC_FAILED:// 主界面交互失败
// BaseApp.WEBCC_INTERFACE = false;
// int onLineState1 = msg.arg1;
// if (onLineState1 == BaseApp.WEBCC_ONLINE) {// 上线
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", false);
// BaseApp.getEditor(activity).commit();
// } else if (onLineState1 == BaseApp.WEBCC_OFFLINE) {// 下线
// BaseApp.getEditor(activity).putBoolean(
// "PushMessage", false);
// BaseApp.getEditor(activity).commit();
// }
//
// // 更多界面
// if (null != BaseApp.moreHandler) {
// Message msgMore = BaseApp.moreHandler
// .obtainMessage();
// msgMore.what = JVConst.REFRESH_PUSH_COUNT;
// BaseApp.moreHandler.sendMessage(msgMore);
// }
// break;
// case JVConst.UPDATE_DEVICE_STATE_SUCCESS:
// if (!BaseApp.LOCAL_LOGIN_FLAG) {
// // if
// // (BaseApp.getSP(activity).getBoolean("watchType",
// // true)) {
// BaseApp.deviceList = BaseApp
// .orderByOnlineState(BaseApp.deviceList);
// // } else {
// // BaseApp.deviceList =
// // BaseApp.allDeviceOnline(BaseApp.deviceList);
// // }
// }
// activity.deviceAdapter.setData(BaseApp.deviceList);
// activity.deviceListView
// .setAdapter(activity.deviceAdapter);
// activity.deviceAdapter.changePos = -1;
// activity.deviceAdapter.notifyDataSetChanged();
// activity.showNoDevice();
// activity.deviceListView.completeRefreshing();
// activity.refreshFlag = false;
// break;
// case JVConst.UPDATE_DEVICE_STATE_FAILED:
// activity.deviceListView.completeRefreshing();
// activity.refreshFlag = false;
// break;
//
// case JVAccountConst.OFFLINE_CALL_BACK:// 保持在线失败，掉线
// Intent intent = new Intent();
// intent.putExtra("KEEP_ONLINE_ERROR_CODE", 1);// 提掉线
// intent.putExtra("message", 0);
// intent.setAction(activity.getResources().getString(
// R.string.str_action_flag));
// activity.sendBroadcast(intent);
//
// break;
// case JVAccountConst.OFFLINE_CALL_BACK2:// 保持在线失败，掉线
// Intent intent1 = new Intent();
// intent1.putExtra("KEEP_ONLINE_ERROR_CODE", 1);// 提掉线
// intent1.putExtra("message", 1);
// intent1.setAction(activity.getResources().getString(
// R.string.str_action_flag));
// activity.sendBroadcast(intent1);
//
// break;
// case JVAccountConst.TCP_ERROR_OFFLINE:// 网络或服务器异常，掉线
// Intent intentError = new Intent();
// intentError.putExtra("KEEP_ONLINE_ERROR_CODE", 2);// 提掉线
// intentError.setAction(activity.getResources()
// .getString(R.string.str_action_flag));
// activity.sendBroadcast(intentError);
// break;
//
// case JVAccountConst.REFERSH_DEVICE_LIST_ONLY:// 网络或服务器异常，掉线
// activity.deviceAdapter.setData(BaseApp.deviceList);
// activity.deviceListView
// .setAdapter(activity.deviceAdapter);
// activity.deviceAdapter.notifyDataSetChanged();
// break;
// // case DEVICE_ADD_BROADCAST_SUCCESS:
// // AddDeviceThread adThread = new AddDeviceThread(
// // ADDDEVICE.devicePointCount);
// // adThread.start();
// // break;
// // case DEVICE_ADD_BROADCAST_FAILED:
// //
// // break;
// }
// } catch (Exception e) {
// e.printStackTrace();
// // TODO: handle exception
// }
// }
//
// }
// }
//
// // 判断网络线程
// private static class NetCheckTask extends TimerTask {
//
// private final WeakReference<JVMainActivity> mActivity;
//
// public NetCheckTask(JVMainActivity activity) {
// mActivity = new WeakReference<JVMainActivity>(activity);
// }
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// // 能连上网，再打开一次
// JVMainActivity activity = mActivity.get();
//
// if (null != activity) {
// if (BaseApp.isConnected(activity)) {
// Message msgMore = BaseApp.mainHandler.obtainMessage();
// msgMore.what = JVConst.PUSH_NET_RESTORE;
// BaseApp.mainHandler.sendMessage(msgMore);// 网络恢复
// } else {
// Message msgMore = BaseApp.mainHandler.obtainMessage();
// msgMore.what = JVConst.PUSH_NET_DISCONNECT;
// BaseApp.mainHandler.sendMessage(msgMore);// 网络依然断开
// }
// }
//
// }
//
// }
//
// // 将获取的通道信息添加到设备信息上
// public void addPointToDevice() {
// try {
// if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()
// && null != BaseApp.allConnPointList
// && 0 != BaseApp.allConnPointList.size()) {
//
// for (int i = 0; i < BaseApp.deviceList.size(); i++) {
// if (null == BaseApp.deviceList.get(i).pointList) {
// BaseApp.deviceList.get(i).pointList = new ArrayList<ConnPoint>();
// }
// for (int j = 0; j < BaseApp.allConnPointList.size(); j++) {
// if (BaseApp.deviceList.get(i).deviceOID == BaseApp.allConnPointList
// .get(j).deviceID) {
// BaseApp.deviceList.get(i).pointList
// .add(BaseApp.allConnPointList.get(j));
// BaseApp.allConnPointList.remove(j);
// j--;
// }
// }
// }
// }
// BaseApp.deviceList = BaseApp.orderDevice(BaseApp.deviceList);
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
//
// // 返回按钮事件
// @Override
// public boolean onKeyDown(int keyCode, KeyEvent event) {
// // BaseApp.isAppKilled = true;
// // TODO Auto-generated method stub
// long time1 = System.currentTimeMillis();
// if (keyCode == KeyEvent.KEYCODE_BACK) {
// openExitDialog();
// // if (null != BaseApp.queryLoginUser()) {// 数据库中有免登录标志位的
// // openExitDialog();
// // } else {
// // BaseApp.saveDeviceToLocal(BaseApp.getSP(getApplicationContext()),
// // BaseApp.getEditor(getApplicationContext()));
// // if (null != imageSwitchTimer) {
// // imageSwitchTimer.cancel();
// // imageSwitchTimer = new Timer();
// // }
// // if (imageSwitchTask != null) {
// // imageSwitchTask.cancel(); // 将原任务从队列中移除
// // }
// // BaseApp.LOCAL_LOGIN_FLAG = false;
// // // 允许获取推送消息
// // if (null != BaseApp.getSP(getApplicationContext())
// // &&
// // BaseApp.getSP(getApplicationContext()).getBoolean("PushMessage",
// // false)
// // && !BaseApp.LOCAL_LOGIN_FLAG) {
// // boolean closeFlag = JVClient.JVStopKeepOnline();
// // if (closeFlag) {
// // String language = Locale.getDefault().getLanguage();
// // String lang = "";
// // if (language.equalsIgnoreCase("zh")) {// 中文
// // lang = "1";
// // } else {// 英文或其他
// // lang = "2";
// // }
// // LoginUtil.loginSuccessToConfirm(
// // BaseApp.getIMEI(JVMainActivity.this), lang,
// // LoginUtil.userName, "false");
// // }
// // // JVClient.JVRelease();
// // }
// // if (null != BaseApp.mNotificationManager) {
// // BaseApp.mNotificationManager.cancel(0);
// // }
// // JVMainActivity.this.finish();
// // }
// // long time2 = System.currentTimeMillis();
// // MyLog.e("关闭Main时间", (time2 - time1) + "");
// // return false;
//
// }
// return super.onKeyDown(keyCode, event);
// }
//
// private void openExitDialog() {
// AlertDialog.Builder builder = new AlertDialog.Builder(
// JVMainActivity.this);
//
// builder.setTitle(R.string.tips);
// builder.setMessage(R.string.str_sureExit);
//
// builder.setPositiveButton(R.string.str_sure,
// new DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// AsyncImageLoader.getInstance().imageCache.clear();
// BaseApp.saveDeviceToLocal(
// BaseApp.getSP(getApplicationContext()),
// BaseApp.getEditor(getApplicationContext()),
// BaseApp.LOCAL_LOGIN_FLAG);
// // 销毁保持在线服务
// if (null != imageSwitchTimer) {
// imageSwitchTimer.cancel();
// imageSwitchTimer = new Timer();
// }
// if (imageSwitchTask != null) {
// imageSwitchTask.cancel(); // 将原任务从队列中移除
// }
// Intent serviceIntent = new Intent(getResources()
// .getString(R.string.str_offline_class_name));
// stopService(serviceIntent);
// // 本地登录，将数据库中所有ip和断开都改成默认的
// // if (BaseApp.LOCAL_LOGIN_FLAG)
// // {
// // BaseApp.modifyAllIPPORT();
// // }
// // //关闭小助手
// // MyLog.e("tags----", "begin realse sdk");
// // JVSUDT.JVC_EnableHelp(false, 3);
// // MyLog.e("tags----", "begin realse sdk2");
// // 允许获取推送消息
// if (null != BaseApp.getSP(getApplicationContext())
// && BaseApp.getSP(getApplicationContext())
// .getBoolean("PushMessage", false)
// && !BaseApp.LOCAL_LOGIN_FLAG) {
// boolean closeFlag = JVClient.JVStopKeepOnline();
// if (closeFlag) {
//
// Thread thread = new Thread() {
//
// @Override
// public void run() {
// // TODO Auto-generated method stub
// LoginUtil.loginSuccessToConfirm(
// statusHashMap.get(Consts.IMEI),
// String.valueOf(BaseApp.getLan()),
// LoginUtil.userName, "false");
// }
// };
// thread.start();
//
// }
// JVClient.JVRelease();
// }
//
// if (null != BaseApp.mNotificationManager) {
// BaseApp.mNotificationManager.cancel(0);
// }
//
// android.os.Process.killProcess(android.os.Process
// .myPid());
// }
// });
// builder.setNegativeButton(R.string.str_cancel,
// new DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
//
// }
// });
// builder.create().show();
// }
//
// /**
// * 显示帮助图的方法
// *
// * @author suifupeng
// */
// private void helpShow() {
// // 首次登陆
// if (null != BaseApp.getSP(getApplicationContext())) {
// if (BaseApp.LOCAL_LOGIN_FLAG) {
// if (BaseApp.getSP(getApplicationContext()).getBoolean(
// "ShowMainHelp1", true)) {
// helpPager.setVisibility(View.VISIBLE);
// tempDevice();
// LayoutInflater mInflater = JVMainActivity.this
// .getLayoutInflater();
// help1 = (RelativeLayout) mInflater.inflate(
// R.layout.help_layout_1, null);
// listViews = new ArrayList<View>();
// listViews.add(help1);
// helpPager.setAdapter(new MyPagerAdapter(listViews));
// helpPager.setCurrentItem(0);
// final boolean flags = flag;
// help1.setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View v) {
// BaseApp.getEditor(getApplicationContext())
// .putBoolean("ShowMainHelp1", false);
// BaseApp.getEditor(getApplicationContext()).commit();
// if (!flags) {
// BaseApp.deviceList.clear();
// deviceAdapter.setData(BaseApp.deviceList);
// deviceAdapter.openPos = -1;
// deviceListView.setAdapter(deviceAdapter);
// deviceAdapter.notifyDataSetChanged();
// }
// helpPager.setVisibility(View.GONE);
// }
// });
// } else {
// helpPager.setVisibility(View.GONE);
// }
// } else {
// if (BaseApp.getSP(getApplicationContext()).getBoolean(
// "ShowMainHelp2", true)) {
// helpPager.setVisibility(View.VISIBLE);
// indicatorlayout2.setVisibility(View.VISIBLE);
// tempDevice();
// LayoutInflater mInflater = JVMainActivity.this
// .getLayoutInflater();
// help1 = (RelativeLayout) mInflater.inflate(
// R.layout.help_layout_1, null);
// help10 = (RelativeLayout) mInflater.inflate(
// R.layout.help_layout_10, null);
// help11 = (RelativeLayout) mInflater.inflate(
// R.layout.help_layout_11, null);
// listViews = new ArrayList<View>();
// listViews.add(help1);
// listViews.add(help10);
// listViews.add(help11);
// helpPager.setAdapter(new MyPagerAdapter(listViews));
// indicatorlayout2.removeAllViews();
// mIndicatorList2.clear();
// for (int i = 0; i < 3; i++) {
// ImageView indicator = new ImageView(this);
// if (i == 0) {
// indicator
// .setImageResource(R.drawable.feature_point_cur1);
// } else {
// indicator
// .setImageResource(R.drawable.feature_point1);
// }
// LayoutParams param = new LayoutParams(
// LayoutParams.WRAP_CONTENT,
// LayoutParams.WRAP_CONTENT);
// indicator.setPadding(0, 0, 10, 0);
// indicator.setLayoutParams(param);
// indicatorlayout2.addView(indicator);
// // 把小圆点图片存入集合,好让切换图案片的时候动态改变小圆点背景
// mIndicatorList2.add(indicator);
// }
// helpPager.setCurrentItem(0);
// helpPager
// .setOnPageChangeListener(new OnPageChangeListener() {
// @Override
// public void onPageSelected(int arg0) {
// for (int i = 0; i < 3; i++) {
// mIndicatorList2
// .get(i)
// .setImageResource(
// R.drawable.feature_point1);
// }
// mIndicatorList2.get(arg0).setImageResource(
// R.drawable.feature_point_cur1);
// }
//
// @Override
// public void onPageScrolled(int arg0,
// float arg1, int arg2) {
// }
//
// @Override
// public void onPageScrollStateChanged(int arg0) {
// }
// });
// final boolean flags = flag;
// help11.setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View v) {
// indicatorlayout2.setVisibility(View.GONE);
// BaseApp.getEditor(getApplicationContext())
// .putBoolean("ShowMainHelp2", false);
// BaseApp.getEditor(getApplicationContext()).commit();
// if (flags) {
// if (null != BaseApp.deviceList
// && 0 != BaseApp.deviceList.size()
// && BaseApp.deviceList.get(0).onlineState == 0) {
// deviceAdapter.openPos = -1;
// currentOpenPosition = -1;
// deviceListView
// .setSelection(currentOpenPosition);
// deviceAdapter.notifyDataSetChanged();
// }
// } else {
// BaseApp.deviceList.clear();
// deviceAdapter.setData(BaseApp.deviceList);
// deviceAdapter.openPos = -1;
// deviceListView.setAdapter(deviceAdapter);
// deviceAdapter.notifyDataSetChanged();
// }
// helpPager.setVisibility(View.GONE);
// }
// });
// } else {
// helpPager.setVisibility(View.GONE);
// }
// }
// }
// }
//
// private void tempDevice() {
// flag = true; // 列表是否有数据
// if (null == BaseApp.deviceList || 0 == BaseApp.deviceList.size()) {
// flag = false;
// Device device = new Device();
// device.deviceName = "A123456";
// device.deviceNum = "A123456";
// device.onlineState = 1;
// device.hasWifi = 1;
// device.useWifi = true;
// ConnPoint cp = new ConnPoint();
// cp.pointName = "A361_1";
// device.pointList = new ArrayList<ConnPoint>();
// device.pointList.add(cp);
// BaseApp.deviceList.add(device);
// deviceAdapter.setData(BaseApp.deviceList);
// deviceAdapter.openPos = 0;
// deviceListView.setAdapter(deviceAdapter);
// deviceAdapter.notifyDataSetChanged();
// } else {
// // hasWifi = BaseApp.deviceList.get(0).hasWifi;
// // useWifi = BaseApp.deviceList.get(0).useWifi;
// // BaseApp.deviceList.get(0).hasWifi = 1;
// // BaseApp.deviceList.get(0).useWifi = true;
// // deviceAdapter.setData(BaseApp.deviceList);
// deviceAdapter.openPos = 0;
// currentOpenPosition = 0;
// deviceListView.setSelection(currentOpenPosition);
// deviceAdapter.notifyDataSetChanged();
// }
// }
//
//
//
// private void showNoDevice() {
// if (null == BaseApp.deviceList || 0 == BaseApp.deviceList.size()) {
// noDevice.setVisibility(View.VISIBLE);
// } else {
// noDevice.setVisibility(View.GONE);
// }
// }
//
// @Override
// public void onHandler(int what, int arg1, int arg2, Object obj) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void onNotify(int what, int arg1, int arg2, Object obj) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// protected void initSettings() {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// protected void initUi() {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// protected void saveSettings() {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// protected void freeMe() {
// // TODO Auto-generated method stub
//
// }
// }
