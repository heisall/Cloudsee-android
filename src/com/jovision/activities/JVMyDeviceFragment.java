
package com.jovision.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.MainApplication;
import com.jovision.activities.JVTabActivity.OnMainListener;
import com.jovision.adapters.LanAdapter;
import com.jovision.adapters.MyDeviceListAdapter;
import com.jovision.adapters.PopWindowAdapter;
import com.jovision.bean.AD;
import com.jovision.bean.APPImage;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.commons.GetDemoTask;
import com.jovision.commons.JVDeviceConst;
import com.jovision.commons.LoginTask;
import com.jovision.commons.MyList;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.BitmapCache;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.PlayUtil;
import com.jovision.views.AlarmDialog;
import com.jovision.views.ImageViewPager;
import com.tencent.stat.StatService;

import neo.droid.p2r.PullToRefreshBase;
import neo.droid.p2r.PullToRefreshBase.OnLastItemVisibleListener;
import neo.droid.p2r.PullToRefreshBase.OnRefreshListener;
import neo.droid.p2r.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 我的设备
 */
public class JVMyDeviceFragment extends BaseFragment implements OnMainListener {

    private String TAG = "MyDeviceFragment";

    // private RefreshableView refreshableView;
    private PullToRefreshListView mPullRefreshListView;

    /** 叠加三个 布局 */
    private LinearLayout deviceLayout; // 设备列表界面
    private RelativeLayout refreshLayout; // 设备加载失败界面
    private LinearLayout quickSetSV; // 快速配置界面
    /** 新添添加设备引导界面 nihy */

    // private Button quickSet;// 快速无限安装
    // private ImageView quickinstall_img_bg;
    // private Button addDevice;// 有线设备添加
    // private ImageView unwire_device_img_bg;

    /** 广告位 */
    private ArrayList<AD> adList = new ArrayList<AD>();
    private LayoutInflater inflater;
    private View adView;
    private ImageViewPager imageScroll; // 图片容器
    private LinearLayout ovalLayout; // 圆点容器
    private List<View> listViews; // 图片组
    // private int[] imageResId = new int[] { R.drawable.a, R.drawable.b};
    // private int[] imageEnResId = new int[] { R.drawable.aen, R.drawable.ben};
    // private int[] image = new int[] {};
    /** 弹出框 */
    private Dialog initDialog;// 显示弹出框
    private TextView dialogCancel;// 取消按钮
    private TextView dialogCompleted;// 确定按钮
    /** 广播弹出框 */
    private Dialog LanDialog;// 显示弹出框
    private ListView lanlistview;// 广播到的设备列表
    private TextView lan_completed;// 确定按钮
    private TextView lan_cancel;// 取消按钮
    private LanAdapter lanAdapter;
    private LinearLayout lanselect;
    private ImageView lanall;
    private boolean isselectall;
    private TextView selectnum;
    private TextView number;
    private String Selectnumberl;
    private String numString;
    public static boolean isshow;
    int Sum;
    private ArrayList<Device> addLanList = new ArrayList<Device>();// 广播到的设备列表
    // 设备名称
    private TextView device_name;
    // 设备昵称
    private EditText device_nicket;
    // 设备昵称编辑键
    private ImageView device_niceet_cancle;
    // 设备用户名
    private EditText device_nameet;
    // 设备用户名编辑键
    private ImageView device_nameet_cancle;
    // 设备密码
    private EditText device_passwordet;
    // 设备密码编辑键
    private ImageView device_password_cancleI;
    private ImageView dialog_cancle_img;
    /** 设备列表 */
    private ListView myDeviceListView;
    private ArrayList<Device> myDeviceList = null;
    public static MyDeviceListAdapter myDLAdapter;
    /** 自动刷新 */
    private Timer updateTimer = null;
    private AutoUpdateTask updateTask;
    /** 没有设备时的提示语 */
    private TextView no_device_lead;
    private TextView no_device_lead_text2;

    // /** 3分钟广播 */
    // private Timer broadTimer;
    // private TimerTask broadTimerTask;

    // public boolean localFlag = false;// 本地登陆标志位
    public String devicename;

    public int broadTag = 0;
    private ArrayList<Device> broadList = new ArrayList<Device>();// 广播到的设备列表
    private HashMap<String, Integer> broadChanCoutMap = new HashMap<String, Integer>();// 广播到的设备和通道数量

    private PopupWindow popupWindow; // 声明PopupWindow对象；

    private ListView popListView;

    private String[] popFunArray;

    private PopWindowAdapter popWindowAdapter;

    private int[] popDrawarray = new int[] {
            R.drawable.mydevice_popwindowonse_icon,
            R.drawable.mydevice_popwindowtwo_icon,
            // R.drawable.mydevice_popwindowthree_icon,
            R.drawable.mydevice_popwindowfour_icon,
            R.drawable.mydevice_popwindowsix_icon
    };
    private int[] popDrawarrayno = new int[] {
            R.drawable.mydevice_popwindowonse_icon,
            R.drawable.mydevice_popwindowtwo_icon,
            // R.drawable.mydevice_popwindowthree_icon,
            R.drawable.mydevice_popwindowfour_icon,
            R.drawable.mydevice_popwindowsix_icon,
            R.drawable.mydevice_popwindowfive_icon
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mydevice, container,
                false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            boolean firstLogin = MySharedPreference.getBoolean(
                    Consts.FIRST_LOGIN, false);
            if (firstLogin) {
                mActivity.createDialog("", false);
                LoginTask loginTask = new LoginTask(true, mActivity,
                        (MainApplication) mActivity.getApplication(),
                        mActivity.statusHashMap, alarmnet);
                String[] params = new String[3];
                loginTask.execute(params);
            }

            if (null == ((BaseActivity) mActivity).statusHashMap.get("DEMOURL")) {
                fragHandler.sendEmptyMessage(Consts.GETDEMOURL);
            }
            boolean hasGot = Boolean.parseBoolean(mActivity.statusHashMap
                    .get(Consts.HAG_GOT_DEVICE));
            if (!hasGot) {
                fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
            }
            // getActivity() = (BaseActivity) getActivity();
            mParent = getView();
            if (!Boolean.valueOf(mActivity.statusHashMap
                    .get(Consts.LOCAL_LOGIN))) {
                popFunArray = mActivity.getResources().getStringArray(
                        R.array.array_popno);
            } else {
                popFunArray = mActivity.getResources().getStringArray(
                        R.array.array_pop);
            }
            currentMenu.setText(mActivity.getResources().getString(
                    R.string.my_device));
            currentMenu.setText(R.string.my_device);
            leftBtn.setVisibility(View.GONE);
            leftBtn.setOnClickListener(myOnClickListener);
            devicename = mActivity.statusHashMap.get(Consts.KEY_USERNAME);
            inflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // refreshableView = (RefreshableView) mParent
            // .findViewById(R.id.device_refreshable_view);

            mPullRefreshListView = (PullToRefreshListView) mActivity
                    .findViewById(R.id.device_refreshable_view);

            mPullRefreshListView
                    .setOnRefreshListener(new OnRefreshListener<ListView>() {
                        @Override
                        public void onRefresh(
                                PullToRefreshBase<ListView> refreshView) {
                            String label = DateUtils.formatDateTime(mActivity,
                                    System.currentTimeMillis(),
                                    DateUtils.FORMAT_SHOW_TIME
                                            | DateUtils.FORMAT_SHOW_DATE
                                            | DateUtils.FORMAT_ABBREV_ALL);

                            // Update the LastUpdatedLabel
                            refreshView.getLoadingLayoutProxy()
                                    .setLastUpdatedLabel(label);

                            fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);

                            GetDevTask task = new GetDevTask();
                            String[] strParams = new String[3];
                            strParams[0] = "1";
                            task.execute(strParams);
                        }
                    });

            mPullRefreshListView
                    .setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

                        @Override
                        public void onLastItemVisible() {
                            mActivity.showTextToast(R.string.end_list);
                        }
                    });

            adView = inflater.inflate(R.layout.ad_layout, null);

            deviceLayout = (LinearLayout) mParent
                    .findViewById(R.id.devicelayout);
            refreshLayout = (RelativeLayout) mParent
                    .findViewById(R.id.refreshlayout);
            quickSetSV = (LinearLayout) mParent
                    .findViewById(R.id.quickinstalllayout);
            // quickSet = (Button) mParent.findViewById(R.id.quickinstall);
            // quickinstall_img_bg = (ImageView) mParent
            // .findViewById(R.id.quickinstall_img_bg);
            // addDevice = (Button) mParent.findViewById(R.id.adddevice);
            // unwire_device_img_bg = (ImageView) mParent
            // .findViewById(R.id.unwire_device_img_bg);

            refreshLayout.setOnClickListener(myOnClickListener);
            // quickSet.setOnClickListener(myOnClickListener);
            // quickinstall_img_bg.setOnClickListener(myOnClickListener);
            // addDevice.setOnClickListener(myOnClickListener);
            // unwire_device_img_bg.setOnClickListener(myOnClickListener);
            //
            // if (mActivity.statusHashMap.get(Consts.NEUTRAL_VERSION).equals(
            // "false")) {// CloudSEE
            // quickinstall_img_bg.setImageDrawable(mActivity.getResources()
            // .getDrawable(R.drawable.wire_device_img));
            // unwire_device_img_bg.setImageDrawable(mActivity.getResources()
            // .getDrawable(R.drawable.unwire_device_img));
            // } else {
            // quickinstall_img_bg.setImageDrawable(mActivity.getResources()
            // .getDrawable(R.drawable.wire_devicen_img));
            // unwire_device_img_bg.setImageDrawable(mActivity.getResources()
            // .getDrawable(R.drawable.unwire_devicen_img));
            // }
            /** 广告条 */
            imageScroll = (ImageViewPager) adView
                    .findViewById(R.id.imagescroll);
            // 防止广告图片变形
            RelativeLayout.LayoutParams reParams = new RelativeLayout.LayoutParams(
                    mActivity.disMetrics.widthPixels,
                    (int) (0.45 * mActivity.disMetrics.widthPixels));
            imageScroll.setLayoutParams(reParams);
            ovalLayout = (LinearLayout) adView.findViewById(R.id.dot_layout);
            initADViewPager();
            myDLAdapter = new MyDeviceListAdapter(mActivity, this);
            myDeviceListView = mPullRefreshListView.getRefreshableView();
            myDeviceListView.addHeaderView(adView);

            /** 没有设备时的提示语 */
            no_device_lead = (TextView) mParent
                    .findViewById(R.id.no_device_lead);
            no_device_lead_text2 = (TextView) mParent
                    .findViewById(R.id.no_device_lead_text2);
            no_device_lead.setText(myGettext());
            rightBtn.setOnClickListener(myOnClickListener);

            if (null != mActivity.statusHashMap && null != mActivity.statusHashMap
                    .get(Consts.MORE_SHOP_SWITCH)) {
                if (1 == Integer.parseInt(mActivity.statusHashMap
                        .get(Consts.MORE_SHOP_SWITCH))) {
                    no_device_lead_text2.setVisibility(View.VISIBLE);
                }
            }
            no_device_lead_text2.setOnClickListener(myOnClickListener);
            // if (0 == adUrlList.size()) {
            // adUrlList
            // .add("http://xx.53shop.com/uploads/allimg/c090325/123O60E4530-2V016.jpg");
            // adUrlList
            // .add("http://img4.imgtn.bdimg.com/it/u=1147331110,3253839708&fm=201&gp=0.jpg");
            // adUrlList
            // .add("http://img2.imgtn.bdimg.com/it/u=3597069752,2844048456&fm=201&gp=0.jpg");
            // }
            if (hasGot) {
                myDeviceList = CacheUtil.getDevList();
                refreshList();
            } else {
                fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
                GetDevTask task = new GetDevTask();
                String[] strParams = new String[3];
                strParams[0] = "0";
                task.execute(strParams);
            }

            if (!Boolean.valueOf(mActivity.statusHashMap
                    .get(Consts.LOCAL_LOGIN))) {
                startAutoRefreshTimer();
            } else {
                // // 非3G加广播设备
                // startBroadTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.lan_cancel:
                    LanDialog.dismiss();
                    break;
                case R.id.dialog_cancle_img:
                    initDialog.dismiss();
                    break;
                case R.id.btn_left:
                    if (isshow) {
                        myDLAdapter.setShowDelete(false);
                        myDLAdapter.notifyDataSetChanged();
                        isshow = false;
                    } else {
                        mActivity.openExitDialog();
                    }
                    break;
                case R.id.btn_right:

                    // DeviceUtil.modifyChannalNum("A456","10");

                    Intent newAddDevIntent = new Intent();
                    newAddDevIntent.setClass(mActivity,
                            JVNewAddDeviceActivity.class);
                    mActivity.startActivityForResult(newAddDevIntent, Consts.SCAN_IN_LINE_REQUEST);

                    // initPop();
                    // // 点击按钮时，pop显示状态，显示中就消失，否则显示
                    // if (popupWindow.isShowing()) {
                    // popupWindow.dismiss();
                    // } else {
                    // // 显示在below正下方
                    // popupWindow.showAsDropDown(view,
                    // (mActivity.disMetrics.widthPixels / 2), 10);
                    // }
                    // myDLAdapter.setShowDelete(false);
                    // myDLAdapter.notifyDataSetChanged();
                    break;
                case R.id.device_nameet_cancle:
                    device_nameet.setText("");
                    break;
                case R.id.device_passwrodet_cancle:
                    device_passwordet.setText("");
                    break;
                case R.id.device_nicket_cancle:
                    device_nicket.setText("");
                    break;
                // case R.id.quickinstall:
                // ((ShakeActivity) getActivity()).startSearch(false);
                // break;
                // case R.id.quickinstall_img_bg:// 快速安装无线设备
                // ((ShakeActivity) getActivity()).startSearch(false);
                // break;
                // case R.id.adddevice:
                // Intent addIntent = new Intent();
                // addIntent.setClass(mActivity, JVAddDeviceActivity.class);
                // addIntent.putExtra("QR", false);
                // mActivity.startActivity(addIntent);
                // break;
                // case R.id.unwire_device_img_bg:
                // Intent addIntents = new Intent();
                // addIntents.setClass(mActivity, JVAddDeviceActivity.class);
                // addIntents.putExtra("QR", false);
                // mActivity.startActivity(addIntents);
                // break;
                case R.id.refreshlayout: {
                    fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);

                    GetDevTask task = new GetDevTask();
                    String[] strParams = new String[3];
                    strParams[0] = "1";
                    task.execute(strParams);
                    break;
                }
                case R.id.no_device_lead_text2:
                    ((JVTabActivity) mActivity).jumpShop();
                    break;
                default:
                    break;
            }

        }

    };

    // 点击加号弹出的popWindow
    private void initPop() {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.popview, null); // 将布局转化为view
        popListView = (ListView) v.findViewById(R.id.popwindowlist);
        popWindowAdapter = new PopWindowAdapter(JVMyDeviceFragment.this);
        if (!Boolean.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN))) {
            popWindowAdapter.setData(popFunArray, popDrawarray);
        } else {
            popWindowAdapter.setData(popFunArray, popDrawarrayno);
        }
        popListView.setAdapter(popWindowAdapter);

        if (popupWindow == null) {
            /**
             * public PopupWindow (View contentView, int width, int height)
             * contentView:布局view width：布局的宽 height：布局的高
             */
            if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mActivity)) {
                popupWindow = new PopupWindow(v,
                        mActivity.disMetrics.widthPixels / 2,
                        LayoutParams.WRAP_CONTENT);
            } else {
                popupWindow = new PopupWindow(v,
                        mActivity.disMetrics.widthPixels / 2 + 60,
                        LayoutParams.WRAP_CONTENT);
            }
        }
        popupWindow.setFocusable(true); // 获得焦点
        popupWindow.setOutsideTouchable(true);// 是否可点击

        popupWindow.getContentView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.setFocusable(false); // 失去焦点
                popupWindow.dismiss(); // pop消失
                return false;
            }
        });
        popListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                System.gc();
                switch (position) {
                    case 0: {// 云视通号
                        StatService.trackCustomEvent(mActivity,
                                "Add by CloudSEE ID", mActivity.getResources()
                                        .getString(R.string.census_addcloudseeid));
                        Intent addIntent = new Intent();
                        addIntent.setClass(mActivity, JVAddDeviceActivity.class);
                        addIntent.putExtra("QR", false);
                        mActivity.startActivity(addIntent);
                        break;
                    }

                    case 1: {// 声波配置
                        StatService.trackCustomEvent(
                                mActivity,
                                "SoundWave",
                                mActivity.getResources().getString(
                                        R.string.census_soundwave));
                        Intent intent = new Intent();
                        intent.setClass(mActivity, JVWaveSetActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }

                    // // TODO
                    // case 2: {// 局域网设备-->即将改成智联路由...
                    // // StatService.trackCustomEvent(mActivity,
                    // // "Scan devices in LAN", mActivity.getResources()
                    // // .getString(R.string.str_scanlandevice));
                    // //
                    // // if
                    // (!MySharedPreference.getBoolean(Consts.MORE_BROADCAST,
                    // // true)) {
                    // // MyLog.v(Consts.TAG_APP, "not broad = " + false);
                    // // break;
                    // // }
                    // //
                    // // if (!ConfigUtil.is3G(mActivity, false)) {// 3G网提示不支持
                    // // fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
                    // // broadTag = Consts.TAG_BROAD_ADD_DEVICE;
                    // // broadList.clear();
                    // // PlayUtil.deleteDevIp(myDeviceList);
                    // // PlayUtil.broadCast(mActivity);
                    // // } else {
                    // // mActivity.showTextToast(R.string.notwifi_forbid_func);
                    // // }
                    // /************ 智联路由 begin ***********/
                    // StatService.trackCustomEvent(
                    // mActivity,
                    // "Smart Connection",
                    // mActivity.getResources().getString(
                    // R.string.str_scanlandevice));
                    //
                    // if (!MySharedPreference.getBoolean(Consts.MORE_BROADCAST,
                    // true)) {
                    // MyLog.v(Consts.TAG_APP, "not broad = " + false);
                    // break;
                    // }
                    //
                    // if (!ConfigUtil.is3G(mActivity, false)) {// 3G网提示不支持
                    // Intent intent = new Intent();
                    // intent.setClass(mActivity,
                    // SmartConnectionConfigActivity.class);
                    // mActivity.startActivity(intent);
                    // } else {
                    // mActivity.showTextToast(R.string.notwifi_forbid_func);
                    // }
                    // /************ 智联路由 end ***********/
                    // break;
                    // }

                    case 2: {// 无线设备
                        StatService.trackCustomEvent(
                                mActivity,
                                "Add Wi_Fi Device",
                                mActivity.getResources().getString(
                                        R.string.census_addwifidev));
                        ((ShakeActivity) getActivity()).startSearch(false, -1);
                        break;
                    }
                    case 3: {// 二维码扫描
                        StatService.trackCustomEvent(
                                mActivity,
                                "Scan QR Code",
                                mActivity.getResources().getString(
                                        R.string.census_scanqrcod));
                        Intent addIntent = new Intent();
                        addIntent.setClass(mActivity, JVAddDeviceActivity.class);
                        addIntent.putExtra("QR", true);
                        mActivity.startActivity(addIntent);
                        break;
                    }
                    case 4: {// IP/域名设备
                        StatService.trackCustomEvent(mActivity, "IP/DNS", mActivity
                                .getResources().getString(R.string.census_ipdns));
                        Intent intent = new Intent();
                        intent.setClass(mActivity, JVAddIpDeviceActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    }

                }
                popupWindow.dismiss(); // pop消失
            }
        });
    }

    // -----------------customize start--------------------
    // 局域网扫描设备
    public void searchLocalNetworkDevice() {
        MyLog.v(TAG, "--searchLocalNetworkDevice--");
        StatService.trackCustomEvent(mActivity,
                "Scan devices in LAN", mActivity.getResources()
                        .getString(R.string.str_scanlandevice));

        if (!MySharedPreference.getBoolean(Consts.MORE_BROADCAST,
                true)) {
            MyLog.v(Consts.TAG_APP, "not broad = " + false);
            return;
        }

        if (!ConfigUtil.is3G(mActivity, false)) {// 3G网提示不支持
            fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
            broadTag = Consts.TAG_BROAD_ADD_DEVICE;
            broadList.clear();
            PlayUtil.deleteDevIp(myDeviceList);
            PlayUtil.broadCast(mActivity);
        } else {
            mActivity.showTextToast(R.string.notwifi_forbid_func);
        }
    }

    // -----------------customize end----------------------

    @Override
    public void onResume() {
        super.onResume();
        BitmapCache.getInstance().clearCache();
        // startBroadTimer();
        // startAutoRefreshTimer();
        boolean hasGot = Boolean.parseBoolean(mActivity.statusHashMap
                .get(Consts.HAG_GOT_DEVICE));
        if (hasGot) {
            myDeviceList = CacheUtil.getDevList();
            MyLog.v("aaaaaaa4", myDeviceList.toString());
            refreshList();
        }
    }

    /**
     * 刷新列表
     */
    public void refreshList() {
        PlayUtil.sortList(myDeviceList, mActivity);
        CacheUtil.saveDevList(myDeviceList);
        String stateStr = mActivity.statusHashMap.get(Consts.DATA_LOADED_STATE);
        if (null != stateStr) {
            refreshLayout.setVisibility(View.VISIBLE);
            deviceLayout.setVisibility(View.GONE);
            quickSetSV.setVisibility(View.GONE);
        } else {
            if (null == myDeviceList) {
                refreshLayout.setVisibility(View.VISIBLE);
                deviceLayout.setVisibility(View.GONE);
                quickSetSV.setVisibility(View.GONE);
            } else if (0 == myDeviceList.size()) {
                refreshLayout.setVisibility(View.GONE);
                deviceLayout.setVisibility(View.GONE);
                quickSetSV.setVisibility(View.VISIBLE);
            } else if (myDeviceList.size() > 0) {
                refreshLayout.setVisibility(View.GONE);
                deviceLayout.setVisibility(View.VISIBLE);
                quickSetSV.setVisibility(View.GONE);
                myDLAdapter.setData(myDeviceList);
                myDeviceListView.setAdapter(myDLAdapter);
                myDLAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        BitmapCache.getInstance().clearCache();
        stopRefreshWifiTimer();
        isshow = false;
        // stopBroadTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        myDLAdapter.setShowDelete(false);
        myDLAdapter.notifyDataSetChanged();
        BitmapCache.getInstance().clearCache();
        // stopRefreshWifiTimer();
        // stopBroadTimer();
        if (null == mActivity) {
            mActivity = (BaseActivity) getActivity();
        }
        PlayUtil.sortList(myDeviceList, mActivity);
        CacheUtil.saveDevList(myDeviceList);
        imageScroll.stopTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        imageScroll.stopTimer();
    }

    /**
     * 初始化图片
     */
    private void initADViewPager() {
        listViews = new ArrayList<View>();
        if (mActivity.statusHashMap.get(Consts.NEUTRAL_VERSION).equals("false")) {
            if (MySharedPreference.getBoolean(Consts.AD_UPDATE)) {
                try {
                    adList = AD.fromJsonArray(MySharedPreference
                            .getString(Consts.AD_LIST));
                    if (null == listViews || 0 == listViews.size()) {// 还没加载过广告，先添加上广告
                        for (int i = 0; i < adList.size(); i++) {
                            final ImageView imageView = new ImageView(mActivity);
                            imageView.setTag(i);
                            imageView.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {// 设置图片点击事件
                                    StatService.trackCustomEvent(
                                            mActivity,
                                            "Ad",
                                            mActivity.getResources().getString(
                                                    R.string.census_ad));
                                    Intent intentAD = new Intent(mActivity,
                                            JVWebViewActivity.class);
                                    int index = (Integer) imageView.getTag();

                                    String adUrl = "";

                                    if (Consts.LANGUAGE_ZH == ConfigUtil
                                            .getLanguage2(mActivity)) {
                                        adUrl = adList.get(index).getAdLinkCh();
                                    } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
                                            .getLanguage2(mActivity)) {
                                        adUrl = adList.get(index)
                                                .getAdLinkZht();
                                    } else {
                                        adUrl = adList.get(index).getAdLinkEn();
                                    }

                                    String adDes = adList.get(index)
                                            .getAdDesp();
                                    int type = -1;
                                    int action = -1;
                                    if (null != adDes
                                            && !"".equalsIgnoreCase(adDes)) {
                                        try {
                                            JSONObject adObj = new JSONObject(
                                                    adDes);
                                            type = adObj.optInt("type");
                                            action = adObj.optInt("action");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    if (null != adUrl
                                            && !"".equalsIgnoreCase(adUrl
                                                    .trim())) {

                                        String lan = "";
                                        if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mActivity
                                                .getApplicationContext())) {
                                            lan = "zh_cn";
                                        } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
                                                .getLanguage2(mActivity
                                                        .getApplicationContext())) {
                                            lan = "zh_tw";
                                        } else {
                                            lan = "en_us";
                                        }
                                        String sid = "";
                                        if (!Boolean
                                                .valueOf(mActivity.statusHashMap
                                                        .get(Consts.LOCAL_LOGIN))) {
                                            sid = ConfigUtil.getSession();
                                        }

                                        if (adUrl.contains("platv=9999")
                                                || Consts.AD_TYPE_1 == type) {// 视频广场特殊标识

                                            if (adUrl.contains("?")) {
                                                adUrl = adUrl.substring(0,
                                                        adUrl.lastIndexOf("?"));
                                            }

                                            adUrl = adUrl + ConfigUtil.getDemoParamsStr(mActivity);
                                            ;
                                            MyLog.v("adUrl", adUrl);
                                            intentAD.putExtra("title", -2);
                                            intentAD.putExtra("URL", adUrl);
                                            mActivity.startActivity(intentAD);
                                        } else if (adUrl.contains("bbs")
                                                || Consts.AD_TYPE_3 == type) {// 小维知道特殊标识{

                                            adUrl = adUrl + "&sid=" + sid;
                                            MyLog.v("zhidaoUrl", adUrl);
                                            intentAD.putExtra("title", -2);
                                            intentAD.putExtra("URL", adUrl);
                                            mActivity.startActivity(intentAD);

                                            // Intent zhidaoIntent = mActivity
                                            // .getPackageManager()
                                            // .getLaunchIntentForPackage(
                                            // "com.jovision.zhidao");//
                                            // com.jovision.zhidao.SplashActivity
                                            // if (null == zhidaoIntent) {//
                                            // 提示下载小维知道
                                            // try {
                                            // if (mActivity.hasSDCard(20)) {
                                            // Uri uri = Uri
                                            // .parse(adList
                                            // .get(index)
                                            // .getAdDesp());
                                            // Intent it = new Intent(
                                            // Intent.ACTION_VIEW,
                                            // uri);
                                            // mActivity
                                            // .startActivity(it);
                                            // }
                                            // } catch (Exception e) {
                                            // e.printStackTrace();
                                            // }
                                            //
                                            // } else {
                                            // startActivity(zhidaoIntent);
                                            // }
                                        } else {
                                            MyLog.v("adUrl", adUrl);
                                            intentAD.putExtra("title", -2);
                                            intentAD.putExtra("URL", adUrl);
                                            mActivity.startActivity(intentAD);
                                        }

                                    }
                                }
                            });

                            Bitmap bmp = null;

                            if (Consts.LANGUAGE_ZH == ConfigUtil
                                    .getLanguage2(mActivity)) {
                                bmp = BitmapCache
                                        .getInstance()
                                        .getBitmap(
                                                adList.get(i).getAdImgUrlCh(),
                                                "net",
                                                String.valueOf(adList.get(i)
                                                        .getIndex())
                                                        + ConfigUtil
                                                                .getLanguage2(mActivity));
                            } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
                                    .getLanguage2(mActivity)) {
                                bmp = BitmapCache
                                        .getInstance()
                                        .getBitmap(
                                                adList.get(i).getAdImgUrlZht(),
                                                "net",
                                                String.valueOf(adList.get(i)
                                                        .getIndex())
                                                        + ConfigUtil
                                                                .getLanguage2(mActivity));
                                // adList.get(i).getAdImgUrlZht());
                            } else {
                                bmp = BitmapCache
                                        .getInstance()
                                        .getBitmap(
                                                adList.get(i).getAdImgUrlEn(),
                                                "net",
                                                String.valueOf(adList.get(i)
                                                        .getIndex())
                                                        + ConfigUtil
                                                                .getLanguage2(mActivity));
                                // adList.get(i).getAdImgUrlEn());
                            }
                            if (null != bmp) {
                                imageView.setImageBitmap(bmp);
                            } else {
                                imageView
                                        .setImageResource(R.drawable.ad_default);
                            }

                            imageView.setScaleType(ScaleType.FIT_CENTER);
                            listViews.add(imageView);
                        }

                    } else {
                        refreshAD();
                        // for (int i = 0; i < listViews.size(); i++) {
                        // ImageView imageView = (ImageView) listViews.get(i);
                        // Bitmap bmp = null;
                        //
                        // if (Consts.LANGUAGE_ZH == ConfigUtil
                        // .getLanguage2(mActivity)) {
                        // bmp = BitmapCache
                        // .getInstance()
                        // .getBitmap(
                        // adList.get(i).getAdImgUrlCh(),
                        // "net",
                        // String.valueOf(adList.get(i)
                        // .getIndex())
                        // + ConfigUtil
                        // .getLanguage2(mActivity));
                        // // adList.get(i).getAdImgUrlCh());
                        // } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
                        // .getLanguage2(mActivity)) {
                        // bmp = BitmapCache
                        // .getInstance()
                        // .getBitmap(
                        // adList.get(i).getAdImgUrlZht(),
                        // "net",
                        // String.valueOf(adList.get(i)
                        // .getIndex())
                        // + ConfigUtil
                        // .getLanguage2(mActivity));
                        // // adList.get(i).getAdImgUrlZht());
                        // } else {
                        // bmp = BitmapCache
                        // .getInstance()
                        // .getBitmap(
                        // adList.get(i).getAdImgUrlEn(),
                        // "net",
                        // String.valueOf(adList.get(i)
                        // .getIndex())
                        // + ConfigUtil
                        // .getLanguage2(mActivity));
                        // // adList.get(i).getAdImgUrlEn());
                        // }
                        //
                        // if (null != bmp) {
                        // imageView.setImageBitmap(bmp);
                        // } else {
                        // imageView
                        // .setImageResource(R.drawable.ad_default);
                        // }
                        // imageView.setScaleType(ScaleType.FIT_CENTER);
                        // }
                    }

                    if (null != listViews && 0 != listViews.size()) {
                        imageScroll.stopTimer();
                        // 开始滚动
                        imageScroll.start(mActivity, listViews, 4 * 1000,
                                ovalLayout, R.layout.dot_item, R.id.ad_item_v,
                                R.drawable.dot_focused, R.drawable.dot_normal);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 刷新广告
     */
    private void refreshAD() {
        if (null != listViews && 0 != listViews.size()) {
            for (int i = 0; i < listViews.size(); i++) {
                ImageView imageView = (ImageView) listViews.get(i);
                Bitmap bmp = null;

                if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mActivity)) {
                    bmp = BitmapCache.getInstance().getBitmap(
                            adList.get(i).getAdImgUrlCh(),
                            "net",
                            String.valueOf(adList.get(i).getIndex())
                                    + ConfigUtil.getLanguage2(mActivity));
                    // adList.get(i).getAdImgUrlCh());
                } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
                        .getLanguage2(mActivity)) {
                    bmp = BitmapCache.getInstance().getBitmap(
                            adList.get(i).getAdImgUrlZht(),
                            "net",
                            String.valueOf(adList.get(i).getIndex())
                                    + ConfigUtil.getLanguage2(mActivity));
                    // adList.get(i).getAdImgUrlZht());
                } else {
                    bmp = BitmapCache.getInstance().getBitmap(
                            adList.get(i).getAdImgUrlEn(),
                            "net",
                            String.valueOf(adList.get(i).getIndex())
                                    + ConfigUtil.getLanguage2(mActivity));
                    // adList.get(i).getAdImgUrlEn());
                }

                if (null != bmp) {
                    imageView.setImageBitmap(bmp);
                } else {
                    imageView.setImageResource(R.drawable.ad_default);
                }
                imageView.setScaleType(ScaleType.FIT_CENTER);
            }
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        if (null != fragHandler) {
            fragHandler.sendMessage(fragHandler.obtainMessage(what, arg1, arg2,
                    obj));
        }
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case Consts.SCAN_IN_LINE_REQUEST: {// 局域网添加设备
                if (Consts.SCAN_IN_LINE_RESULT == arg1) {
                    StatService.trackCustomEvent(mActivity,
                            "Scan devices in LAN", mActivity.getResources()
                                    .getString(R.string.str_scanlandevice));

                    if (!MySharedPreference.getBoolean(Consts.MORE_BROADCAST,
                            true)) {
                        MyLog.v(Consts.TAG_APP, "not broad = " + false);
                    } else {

                        if (!ConfigUtil.is3G(mActivity, false)) {// 3G网提示不支持
                            fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
                            broadTag = Consts.TAG_BROAD_ADD_DEVICE;
                            broadList.clear();
                            PlayUtil.deleteDevIp(myDeviceList);
                            PlayUtil.broadCast(mActivity);
                        } else {
                            mActivity.showTextToast(R.string.notwifi_forbid_func);
                        }
                    }

                }

                break;
            }
            case Consts.NET_CHANGE_CLEAR_CACHE: {// 网络切换清缓存
                MyLog.i("MyRecevier", "网络变化了--正常网络");
                Jni.getVersion();
                Jni.clearCache();

                if (null == myDeviceList || 0 == myDeviceList.size()) {
                    myDeviceList = CacheUtil.getDevList();
                }

                if (null != myDeviceList && 0 != myDeviceList.size()) {
                    PlayUtil.setHelperToList(myDeviceList);
                }
                break;
            }
            case Consts.GETDEMOURL: {
                fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);

                if ("false".equals(mActivity.statusHashMap
                        .get(Consts.KEY_INIT_ACCOUNT_SDK))) {
                    MyLog.e("Login", "初始化账号SDK失败");
                    ConfigUtil.initAccountSDK(((MainApplication) mActivity
                            .getApplication()));// 初始化账号SDK
                }

                GetDemoTask demoTask = new GetDemoTask(mActivity);
                String[] demoParams = new String[3];
                demoParams[1] = "1";
                demoParams[2] = "fragmentString";
                demoTask.execute(demoParams);
                break;
            }
            case Consts.WHAT_HEART_ERROR: {// 心跳异常
                if (this.isAdded()) {
                    if (null != alarmnet
                            && !Boolean.valueOf(mActivity.statusHashMap
                                    .get(Consts.LOCAL_LOGIN))) {
                        alarmnet.setVisibility(View.VISIBLE);
                        if (null != accountError) {
                            accountError.setText(mActivity.getResources()
                                    .getString(R.string.network_error_tips));
                        }
                    }
                }
                break;
            }
            case Consts.WHAT_HEART_TCP_ERROR:
            case Consts.WHAT_HEART_TCP_CLOSED:
                if (this.isAdded()) {
                    if (null != alarmnet
                            && !Boolean.valueOf(mActivity.statusHashMap
                                    .get(Consts.LOCAL_LOGIN))) {
                        alarmnet.setVisibility(View.VISIBLE);
                        if (null != accountError) {
                            accountError.setText(mActivity.getResources()
                                    .getString(R.string.network_error_tips));
                        }
                    }
                }
                break;
            case Consts.WHAT_HEART_NORMAL:// 心跳恢复正常
                if (null != alarmnet) {
                    alarmnet.setVisibility(View.GONE);
                }
                break;

            case Consts.WHAT_HAS_NOT_LOGIN:// 账号未登录
                if (this.isAdded()) {
                    if (null != alarmnet
                            && !Boolean.valueOf(mActivity.statusHashMap
                                    .get(Consts.LOCAL_LOGIN))) {
                        alarmnet.setVisibility(View.VISIBLE);
                        if (null != accountError) {
                            accountError.setText(mActivity.getResources()
                                    .getString(R.string.account_error_tips));
                        }
                    }
                }
                break;
            case Consts.WHAT_HAS_LOGIN_SUCCESS:// 账号正常登陆
                if (null != alarmnet) {
                    alarmnet.setVisibility(View.GONE);
                }
                break;
            case Consts.WHAT_SESSION_FAILURE:// session失效
                if (null != alarmnet
                        && !Boolean.valueOf(mActivity.statusHashMap
                                .get(Consts.LOCAL_LOGIN))) {
                    alarmnet.setVisibility(View.VISIBLE);
                    if (null != accountError) {
                        accountError.setText(R.string.account_error_tips);
                    }
                }
                mActivity.createDialog("", false);
                LoginTask loginTask = new LoginTask(false, mActivity,
                        (MainApplication) mActivity.getApplication(),
                        mActivity.statusHashMap, alarmnet);
                String[] params = new String[3];
                loginTask.execute(params);
                break;
            case Consts.WHAT_AD_UPDATE: {
                initADViewPager();
                break;
            }
            case Consts.WHAT_MYDEVICE_POINT_FAILED: {// 通道加载失败
                mActivity.showTextToast(R.string.no_channel_error);
                break;
            }
            case Consts.WHAT_DEV_GETFINISHED: {
                // TODO
                // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
                mActivity.dismissDialog();
                mActivity.createDialog(
                        mActivity.getResources().getString(R.string.waiting)
                                + "...", false);
                refreshList();
                initADViewPager();
                switch (arg1) {
                // 从服务器端获取设备成功
                    case Consts.WHAT_DEVICE_GETDATA_SUCCESS: {
                        broadTag = Consts.TAG_BROAD_DEVICE_LIST;
                        PlayUtil.deleteDevIp(myDeviceList);
                        boolean canBroad = PlayUtil.broadCast(mActivity);
                        mPullRefreshListView.onRefreshComplete();
                        // if (!canBroad) {// 3G不可以广播
                        // new ChannelCountThread().start();
                        // }
                        break;
                    }
                    // 云视通检索服务器通信异常
                    case Consts.WHAT_DEVICE_GETDATA_SEARCH_FAILED: {
                        mActivity.showTextToast(R.string.search_device_failed);
                        break;
                    }
                    // 从服务器端获取设备成功，但是没有设备
                    case Consts.WHAT_DEVICE_NO_DEVICE: {
                        break;
                    }
                    // 从服务器端获取设备失败
                    case Consts.WHAT_DEVICE_GETDATA_FAILED: {
                        mActivity.showTextToast(R.string.get_device_failed);
                        break;
                    }
                }
                break;
            }
            case Consts.WHAT_AUTO_UPDATE: {
                GetDevTask task = new GetDevTask();
                String[] strParams = new String[3];
                strParams[0] = "1";
                task.execute(strParams);
                break;
            }
            case Consts.WHAT_SHOW_PRO: {
                mActivity.createDialog("", false);
                break;
            }
            case Consts.WHAT_TAB_BACK: {// tab 返回事件，保存数据
                if (null == mActivity) {
                    mActivity = (BaseActivity) getActivity();
                }
                PlayUtil.sortList(myDeviceList, mActivity);
                // CacheUtil.saveDevList(myDeviceList);
                break;
            }

            // 广播回调
            case Consts.CALL_LAN_SEARCH: {

                MyLog.v(TAG, "CALL_LAN_SEARCH = what=" + what + ";arg1=" + arg1
                        + ";arg2=" + arg1 + ";obj=" + obj.toString());
                // MyLog.v("广播回调", "onTabAction2:what=" + what + ";arg1=" + arg1
                // + ";arg2=" + arg1 + ";obj=" + obj.toString());
                // onTabAction:what=168;arg1=0;arg2=0;obj={"count":1,"curmod":0,"gid":"A","ip":"192.168.21.238","netmod":0,"no":283827713,"port":9101,"timeout":0,"type":59162,"variety":3}
                if (broadTag == Consts.TAG_BROAD_DEVICE_LIST
                        || broadTag == Consts.TAG_BROAD_THREE_MINITE) {// 三分钟广播或广播设备列表
                    JSONObject broadObj;
                    try {
                        broadObj = new JSONObject(obj.toString());
                        if (0 == broadObj.optInt("timeout")) {

                            String gid = broadObj.optString("gid");
                            int no = broadObj.optInt("no");

                            if (0 == no) {
                                return;
                            }
                            String ip = broadObj.optString("ip");
                            int port = broadObj.optInt("port");
                            int netmod = broadObj.optInt("netmod");
                            int channelCount = broadObj.optInt("count");
                            String broadDevNum = gid + no;

                            if (channelCount > 0) {// 广播获取到设备的通道数量
                                if (null == broadChanCoutMap) {
                                    broadChanCoutMap = new HashMap<String, Integer>();// 广播到的设备和通道数量
                                }
                                broadChanCoutMap.put(broadDevNum, channelCount);
                            }

                            PlayUtil.hasDev(myDeviceList, broadDevNum, ip, port,
                                    netmod);

                        } else if (1 == broadObj.optInt("timeout")) {
                            broadTag = 0;
                            if (null == mActivity) {
                                mActivity = (BaseActivity) getActivity();
                            }
                            PlayUtil.sortList(myDeviceList, mActivity);
                            // if (broadTag == Consts.TAG_BROAD_DEVICE_LIST) {
                            // new ChannelCountThread().start();
                            // }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (broadTag == Consts.TAG_BROAD_ADD_DEVICE) {// 广播添加设备
                    JSONObject broadObj;
                    try {
                        broadObj = new JSONObject(obj.toString());
                        if (0 == broadObj.optInt("timeout")) {
                            String gid = broadObj.optString("gid");
                            int no = broadObj.optInt("no");
                            if (no <= 0) {
                                return;
                            }

                            String ip = broadObj.optString("ip");
                            int port = broadObj.optInt("port");
                            int count = broadObj.optInt("count");
                            int netmod = broadObj.optInt("netmod");
                            String broadDevNum = gid + no;
                            if (count <= 0) {
                                count = 4;
                            }

                            // 广播列表和设备列表里面都没有这个设备
                            if (!PlayUtil.hasDev(broadList, broadDevNum, ip, port,
                                    netmod)
                                    && !PlayUtil.hasDev(myDeviceList, broadDevNum,
                                            ip, port, netmod)) {
                                Device broadDev = new Device(ip, port, gid, no,
                                        Consts.DEFAULT_USERNAME,
                                        Consts.DEFAULT_PASSWORD, false, count, 0,
                                        null);
                                broadDev.setHasWifi(netmod);
                                broadDev.setOnlineStateLan(1);// 广播都在线
                                broadList.add(broadDev);
                            }

                        } else if (1 == broadObj.optInt("timeout")) {
                            broadTag = 0;
                            mActivity.dismissDialog();

                            if (null != broadList && 0 != broadList.size()) {
                                Sum = broadList.size();
                                initLanDialog();
                            } else {
                                mActivity.showTextToast(R.string.broad_zero);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
            case Consts.WHAT_DEVICE_ITEM_CLICK: {// 设备单击事件
                myDLAdapter.setShowDelete(false);
                myDLAdapter.notifyDataSetChanged();
                Device dev = myDeviceList.get(arg1);
                if (0 == dev.getChannelList().size()) {// 0个通道直接播放
                    mActivity.showTextToast(R.string.selectone_to_connect);
                } else if (1 == dev.getChannelList().size()) {// 1个通道直接播放

                    // if (0 == myDeviceList.get(arg1).getOnlineState()
                    // && !Boolean
                    // .valueOf(((BaseActivity) getActivity()).statusHashMap
                    // .get(Consts.LOCAL_LOGIN))) {
                    // getActivity().showTextToast(R.string.offline_not_play);
                    // } else {
                    // sortList(myDeviceList);
                    ArrayList<Device> playList = PlayUtil.prepareConnect(
                            myDeviceList, arg1);

                    if (null == playList || 0 == playList.size()) {
                        mActivity.showTextToast(R.string.selectone_to_connect);
                    } else {
                        Intent intentPlay = new Intent(mActivity,
                                JVPlayActivity.class);
                        intentPlay.putExtra(Consts.KEY_PLAY_NORMAL,
                                playList.toString());
                        intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);
                        intentPlay.putExtra("DeviceIndex", arg1);
                        intentPlay.putExtra("ChannelofChannel", dev
                                .getChannelList().toList().get(0).getChannel());
                        // 设备分组
                        intentPlay.putExtra("DeviceGroup", dev.getGid());
                        mActivity.startActivity(intentPlay);
                    }

                    // }

                } else {// 多个通道查看通道列表
                    Intent intentPlay = new Intent(mActivity,
                            JVChannelsActivity.class);
                    intentPlay.putExtra("DeviceIndex", arg1);
                    mActivity.startActivity(intentPlay);
                }

                break;
            }
            case Consts.WHAT_DEVICE_ITEM_LONG_CLICK: {// 设备长按事件
                isshow = true;
                myDLAdapter.setShowDelete(true);
                myDLAdapter.notifyDataSetChanged();
                break;
            }
            case Consts.WHAT_DEVICE_ITEM_DEL_CLICK: {// 设备删除事件
                DelDevTask task = new DelDevTask();
                String[] strParams = new String[3];
                strParams[0] = String.valueOf(arg1);
                task.execute(strParams);
                break;
            }
            case Consts.WHAT_DEVICE_EDIT_CLICK: {// 设备编辑事件
                myDLAdapter.setShowDelete(false);
                initSummaryDialog(myDeviceList, arg1);
            }
                break;
            case Consts.WHAT_PUSH_MESSAGE:
                // 弹出对话框
                //
                // ArrayList<Device> deviceList = CacheUtil.getDevList();
                // MyLog.v("Alarm", "prepareConnect 00--" +
                // deviceList.toString());
                if (null != mActivity) {
                    mActivity.onNotify(Consts.NEW_PUSH_MSG_TAG_PRIVATE, 0, 0, null);// 通知显示报警信息条数
                    new AlarmDialog(mActivity).Show(obj);
                } else {
                    MyLog.e("Alarm",
                            "onHandler mActivity is null ,so dont show the alarm dialog");
                }
                break;
        }
    }

    /** 弹出框初始化 */
    private void initSummaryDialog(ArrayList<Device> myDeviceList,
            final int agr1) {
        initDialog = new Dialog(mActivity, R.style.mydialog);
        View view = LayoutInflater.from(mActivity).inflate(
                R.layout.dialog_summary, null);
        initDialog.setContentView(view);
        dialog_cancle_img = (ImageView) view
                .findViewById(R.id.dialog_cancle_img);
        dialogCancel = (TextView) view.findViewById(R.id.dialog_cancel);
        dialogCompleted = (TextView) view.findViewById(R.id.dialog_completed);
        device_name = (TextView) view.findViewById(R.id.device_namew);
        device_nicket = (EditText) view.findViewById(R.id.device_nicket);
        device_niceet_cancle = (ImageView) view
                .findViewById(R.id.device_nicket_cancle);
        device_nameet = (EditText) view.findViewById(R.id.device_nameet);
        device_nameet_cancle = (ImageView) view
                .findViewById(R.id.device_nameet_cancle);
        device_passwordet = (EditText) view
                .findViewById(R.id.device_passwrodet);
        device_password_cancleI = (ImageView) view
                .findViewById(R.id.device_passwrodet_cancle);
        dialog_cancle_img.setOnClickListener(myOnClickListener);
        device_nameet_cancle.setOnClickListener(myOnClickListener);
        device_niceet_cancle.setOnClickListener(myOnClickListener);
        device_password_cancleI.setOnClickListener(myOnClickListener);
        device_name.setText(myDeviceList.get(agr1).getFullNo());
        device_nameet.setText(myDeviceList.get(agr1).getUser());
        device_passwordet.setText(myDeviceList.get(agr1).getPwd());
        if (!("").equals(myDeviceList.get(agr1).getNickName())) {
            device_nicket.setText(myDeviceList.get(agr1).getNickName());
        }
        initDialog.show();
        device_name.setFocusable(true);
        device_name.setFocusableInTouchMode(true);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDLAdapter.notifyDataSetChanged();
                initDialog.dismiss();
            }
        });
        dialogCompleted.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 设备昵称不为空
                if ("".equalsIgnoreCase(device_nicket.getText().toString())) {
                    mActivity.showTextToast(mActivity.getResources().getString(
                            R.string.str_nikename_notnull));
                }
                // 设备昵称验证
                else if (!ConfigUtil.checkNickName(device_nicket.getText()
                        .toString())) {
                    mActivity.showTextToast(mActivity.getResources().getString(
                            R.string.login_str_nike_name_order));
                }
                // 设备用户名不为空
                else if (""
                        .equalsIgnoreCase(device_nameet.getText().toString())) {
                    mActivity
                            .showTextToast(R.string.login_str_device_account_notnull);
                }
                // 设备用户名验证
                else if (!ConfigUtil.checkDeviceUsername(device_nameet
                        .getText().toString())) {
                    mActivity.showTextToast(mActivity.getResources().getString(
                            R.string.login_str_device_account_error));
                } else if (!ConfigUtil.checkDevicePwd(device_passwordet
                        .getText().toString())) {
                    mActivity.showTextToast(mActivity.getResources().getString(
                            R.string.login_str_device_pass_error));
                } else {
                    ModifyDevTask task = new ModifyDevTask();
                    String[] strParams = new String[5];
                    strParams[0] = agr1 + "";
                    strParams[1] = device_name.getText().toString();
                    strParams[2] = device_nameet.getText().toString();
                    strParams[3] = device_passwordet.getText().toString();
                    strParams[4] = device_nicket.getText().toString();
                    task.execute(strParams);
                }
            }
        });
    }

    /** 局域网扫描弹出框初始化 */
    private void initLanDialog() {
        addLanList.clear();
        LanDialog = new Dialog(mActivity, R.style.mydialog);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_lan,
                null);
        LanDialog.setContentView(view);
        Selectnumberl = mActivity.getResources().getString(R.string.selectnum);
        numString = mActivity.getResources().getString(R.string.number);
        selectnum = (TextView) view.findViewById(R.id.selectnum);
        number = (TextView) view.findViewById(R.id.number);
        lan_completed = (TextView) view.findViewById(R.id.lan_completed);
        lan_cancel = (TextView) view.findViewById(R.id.lan_cancel);
        lanlistview = (ListView) view.findViewById(R.id.lanlistview);
        lanselect = (LinearLayout) view.findViewById(R.id.select);
        lanall = (ImageView) view.findViewById(R.id.Lanall);
        lanAdapter = new LanAdapter(mActivity);
        lanAdapter.setData(broadList);
        lanlistview.setAdapter(lanAdapter);
        selectnum.setText(Selectnumberl.replace("?",
                String.valueOf(broadList.size())));
        number.setText(numString.replace("?", String.valueOf(broadList.size())));
        lanlistview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        if (broadList.get(position).isIslanselect()) {
                            broadList.get(position).setIslanselect(false);
                            Sum = Sum - 1;
                            selectnum.setText(Selectnumberl.replace("?",
                                    String.valueOf(Sum)));
                        } else {
                            Sum = Sum + 1;
                            selectnum.setText(Selectnumberl.replace("?",
                                    String.valueOf(Sum)));
                            broadList.get(position).setIslanselect(true);
                        }
                        lanAdapter.notifyDataSetChanged();
                    }
                });
        lan_completed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < broadList.size(); i++) {
                    if (broadList.get(i).isIslanselect()) {
                        addLanList.add(broadList.get(i));
                    }
                }
                AddDevTask task = new AddDevTask();
                String[] strParams = new String[3];
                task.execute(strParams);
            }
        });
        lanselect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isselectall) {
                    for (int i = 0; i < broadList.size(); i++) {
                        broadList.get(i).setIslanselect(true);
                    }
                    Sum = broadList.size();
                    selectnum.setText(Selectnumberl.replace("?",
                            String.valueOf(Sum)));
                    lanall.setBackgroundResource(R.drawable.morefragment_selector_icon);
                    isselectall = false;
                } else {
                    for (int i = 0; i < broadList.size(); i++) {
                        broadList.get(i).setIslanselect(false);
                    }
                    Sum = 0;
                    selectnum.setText(Selectnumberl.replace("?",
                            String.valueOf(Sum)));
                    lanall.setBackgroundResource(R.drawable.morefragment_normal_icon);
                    isselectall = true;
                }
                lanAdapter.notifyDataSetChanged();
            }
        });
        lan_cancel.setOnClickListener(myOnClickListener);
        LanDialog.show();
    }

    // 保存更改设备信息线程
    class ModifyDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            int delRes = -1;
            try {
                int delIndex = Integer.parseInt(params[0]);
                if (Boolean.valueOf(mActivity.statusHashMap
                        .get(Consts.LOCAL_LOGIN))) {// 本地保存修改信息
                    delRes = 0;
                } else {
                    String name = mActivity.statusHashMap
                            .get(Consts.KEY_USERNAME);
                    delRes = DeviceUtil.modifyDevice(name, params[1],
                            params[4], params[2], params[3]);
                }
                if (0 == delRes) {
                    myDeviceList.get(delIndex).setFullNo(params[1]);
                    myDeviceList.get(delIndex).setUser(params[2]);
                    myDeviceList.get(delIndex).setPwd(params[3]);
                    myDeviceList.get(delIndex).setNickName(params[4]);

                    // 同步map bylkp
                    CacheUtil.setNickNameWithYstfn(params[1], params[4]);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return delRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            mActivity.dismissDialog();
            if (0 == result) {
                mActivity.showTextToast(R.string.login_str_device_edit_success);
                myDLAdapter.setShowDelete(false);
                refreshList();
            } else {
                mActivity.showTextToast(R.string.login_str_device_edit_failed);
            }
            initDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
            mActivity.createDialog("", true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度,此方法在主线程执行，用于显示任务执行的进度。
        }
    }

    // 删除设备线程
    class DelDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            int delRes = -1;
            boolean localFlag = Boolean.valueOf(mActivity.statusHashMap
                    .get(Consts.LOCAL_LOGIN));
            try {
                int delIndex = Integer.parseInt(params[0]);
                if (localFlag) {// 本地删除
                    delRes = 0;
                } else {
                    delRes = DeviceUtil.unbindDevice(
                            mActivity.statusHashMap.get("KEY_USERNAME"),
                            myDeviceList.get(delIndex).getFullNo());
                }

                if (0 == delRes) {
                    ConfigUtil.deleteSceneFolder(myDeviceList.get(delIndex)
                            .getFullNo());
                    myDeviceList.remove(delIndex);
                    // 删除对应缓存的nicknamemap中的值 by lkp
                    CacheUtil.removeDevFromNcikMap(myDeviceList.get(delIndex)
                            .getFullNo());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return delRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            mActivity.dismissDialog();
            if (null == mActivity) {
                mActivity = (BaseActivity) getActivity();
            }
            PlayUtil.sortList(myDeviceList, mActivity);
            CacheUtil.saveDevList(myDeviceList);
            if (0 == result) {
                mActivity.showTextToast(R.string.del_device_succ);
                myDLAdapter.setShowDelete(false);
                refreshList();
            } else {
                mActivity.showTextToast(R.string.del_device_failed);
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
            mActivity.createDialog("", true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度,此方法在主线程执行，用于显示任务执行的进度。
        }
    }

    /**
     * 获取广告列表
     */
    public void getADList() {
        int adVersion = 0;
        // 本地获取广告数据
        adList = AD.fromJsonArray(MySharedPreference.getString(Consts.AD_LIST));

        if (!Boolean.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN))) {

            if (null == adList || 0 == adList.size()) {// 本地没有广告去网上获取
                adVersion = MySharedPreference.getInt(Consts.AD_VERSION);
            } else {
                adVersion = adList.get(0).getVersion();
            }
            adList = DeviceUtil.getADList(adVersion);

            if (null == adList) {// 获取广告出错
                adList = AD.fromJsonArray(MySharedPreference
                        .getString(Consts.AD_LIST));
            } else if (0 == adList.size()) {// 未检查到更新
                adList = AD.fromJsonArray(MySharedPreference
                        .getString(Consts.AD_LIST));
            } else if (adList.size() > 0) {// 有新广告
                // 删除老广告
                File adFolder = new File(Consts.AD_PATH);
                MobileUtil.deleteFile(adFolder);
            }
        }
        MySharedPreference.putString(Consts.AD_LIST, adList.toString());
        if (null != adList && 0 != adList.size()) {
            // 从网上获取广告图片
            for (AD ad : adList) {
                if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mActivity)) {
                    BitmapCache.getInstance().getBitmap(
                            ad.getAdImgUrlCh(),
                            "net",
                            String.valueOf(ad.getIndex())
                                    + ConfigUtil.getLanguage2(mActivity));
                } else if (Consts.LANGUAGE_ZHTW == ConfigUtil
                        .getLanguage2(mActivity)) {
                    BitmapCache.getInstance().getBitmap(
                            ad.getAdImgUrlZht(),
                            "net",
                            String.valueOf(ad.getIndex())
                                    + ConfigUtil.getLanguage2(mActivity));
                } else {
                    BitmapCache.getInstance().getBitmap(
                            ad.getAdImgUrlEn(),
                            "net",
                            String.valueOf(ad.getIndex())
                                    + ConfigUtil.getLanguage2(mActivity));
                }
            }
        }

        MySharedPreference.putBoolean(Consts.AD_UPDATE, true);

    }

    // 获取设备列表线程
    class GetDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {// 0：获取，1：刷新
            int getRes = 0;
            fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
            try {
                int errorCode = 0;
                if (null != mActivity.statusHashMap.get(Consts.ACCOUNT_ERROR)) {
                    errorCode = Integer.parseInt(mActivity.statusHashMap
                            .get(Consts.ACCOUNT_ERROR));
                }

                if (errorCode == Consts.WHAT_HAS_NOT_LOGIN
                        || errorCode == Consts.WHAT_SESSION_AUTOLOGIN) {// 未登录，离线登陆
                    myDeviceList = CacheUtil.getOfflineDevList();
                    MyLog.v("LoginState", "offline-" + errorCode);
                } else {
                    MyLog.v("LoginState", "online-" + errorCode);
                    if (!Boolean.valueOf(mActivity.statusHashMap
                            .get(Consts.LOCAL_LOGIN))) {// 非本地登录，无论是否刷新都执行
                        // 获取所有设备列表和通道列表 ,如果设备请求失败，多请求一次
                        if (null == myDeviceList || 0 == myDeviceList.size()) {
                            myDeviceList = DeviceUtil
                                    .getUserDeviceList(mActivity.statusHashMap
                                            .get(Consts.KEY_USERNAME));
                        } else {
                            DeviceUtil.refreshDeviceState(
                                    mActivity.statusHashMap
                                            .get(Consts.KEY_USERNAME),
                                    myDeviceList);
                        }
                        if (null == myDeviceList || 0 == myDeviceList.size()) {
                            myDeviceList = DeviceUtil
                                    .getUserDeviceList(mActivity.statusHashMap
                                            .get(Consts.KEY_USERNAME));
                        }

                        if (null != myDeviceList && 0 != myDeviceList.size()) {
                            boolean hasChannel = true;
                            for (Device dev : myDeviceList) {
                                if (null == dev.getChannelList()
                                        || 0 == dev.getChannelList().size()) {
                                    hasChannel = false;
                                    break;
                                }
                            }

                            if (!hasChannel) {
                                ArrayList<Channel> channelList = DeviceUtil
                                        .getUserPointList();

                                if (null == channelList
                                        || 0 == channelList.size()) {
                                    channelList = DeviceUtil.getUserPointList();
                                }

                                if (null != channelList
                                        && 0 != channelList.size()) {
                                    for (Device dev : myDeviceList) {
                                        MyList<Channel> chanList = new MyList<Channel>(
                                                1);
                                        for (Channel channel : channelList) {
                                            if (channel.isHasFind()) {
                                                continue;
                                            }
                                            if (channel.getDguid()
                                                    .equalsIgnoreCase(
                                                            dev.getFullNo())) {
                                                chanList.add(channel);
                                                channel.setHasFind(true);
                                            }
                                        }
                                        dev.setChannelList(chanList);
                                    }
                                } else {
                                    fragHandler
                                            .sendMessage(fragHandler
                                                    .obtainMessage(Consts.WHAT_MYDEVICE_POINT_FAILED));
                                }

                            }

                        }
                        if (null != myDeviceList && 0 != myDeviceList.size()) {
                            if (null == mActivity) {
                                mActivity = (BaseActivity) getActivity();
                            }
                            PlayUtil.sortList(myDeviceList, mActivity);
                        }
                        CacheUtil.saveDevList(myDeviceList);
                    } else if (Boolean.valueOf(mActivity.statusHashMap
                            .get(Consts.LOCAL_LOGIN))) {// 本地登录
                        myDeviceList = CacheUtil.getDevList();
                    }
                }

                if (null == myDeviceList) {
                    mActivity.statusHashMap.put(Consts.DATA_LOADED_STATE, "-1");
                } else {
                    mActivity.statusHashMap.put(Consts.DATA_LOADED_STATE, null);
                }

                mActivity.statusHashMap.put(Consts.HAG_GOT_DEVICE, "true");
                if (null != myDeviceList && 0 != myDeviceList.size()) {// 获取设备成功,去广播设备列表
                    mActivity.statusHashMap.put(Consts.HAG_GOT_DEVICE, "true");
                    // 给设备列表设置小助手
                    PlayUtil.setHelperToList(myDeviceList);

                    if (JVDeviceConst.YST_INDEX_SEND_ERROR == myDeviceList.get(
                            0).getShortConnRes()) {
                        getRes = Consts.WHAT_DEVICE_GETDATA_SEARCH_FAILED;
                    } else {
                        getRes = Consts.WHAT_DEVICE_GETDATA_SUCCESS;
                    }
                } else if (null != myDeviceList && 0 == myDeviceList.size()) {// 无数据
                    getRes = Consts.WHAT_DEVICE_NO_DEVICE;
                } else {// 获取设备失败
                    getRes = Consts.WHAT_DEVICE_GETDATA_FAILED;
                }
                fragHandler.sendMessage(fragHandler.obtainMessage(
                        Consts.WHAT_DEV_GETFINISHED, getRes, 0));
                if (mActivity.statusHashMap.get(Consts.NEUTRAL_VERSION).equals(
                        "false")) {
                    if (errorCode == Consts.WHAT_HAS_NOT_LOGIN) {// 未登录，离线登陆

                    } else {
                        if ("0".equalsIgnoreCase(params[0])) {
                            if (!Boolean.valueOf(mActivity.statusHashMap
                                    .get(Consts.LOCAL_LOGIN))) {// 在线登陆
                                downloadAppImage();
                            }
                            // TODO 获取广告
                            getADList();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if ("0".equalsIgnoreCase(params[0])) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return getRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            refreshList();
            mPullRefreshListView.onRefreshComplete();
            initADViewPager();
            mActivity.dismissDialog();
            switch (result) {
            // 从服务器端获取设备成功
                case Consts.WHAT_DEVICE_GETDATA_SUCCESS: {
                    broadTag = Consts.TAG_BROAD_DEVICE_LIST;
                    break;
                }
                // 在线服务器检索失败
                case Consts.WHAT_DEVICE_GETDATA_SEARCH_FAILED: {
                    break;
                }
                // 从服务器端获取设备成功，但是没有设备
                case Consts.WHAT_DEVICE_NO_DEVICE: {
                    break;
                }
                // 从服务器端获取设备失败
                case Consts.WHAT_DEVICE_GETDATA_FAILED: {
                    break;
                }
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
            // ((BaseActivity) getActivity()).createDialog("");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度,此方法在主线程执行，用于显示任务执行的进度。
        }
    }

    // 设置三种类型参数分别为String,Integer,String
    class AddDevTask extends AsyncTask<String, Integer, Integer> {
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            LanDialog.dismiss();
            fragHandler.sendEmptyMessage(Consts.WHAT_SHOW_PRO);
            int addRes = -1;
            int addCount = 0;
            try {
                for (Device addDev : addLanList) {
                    String ip = addDev.getIp();
                    int port = addDev.getPort();
                    if (myDeviceList.size() >= 100
                            && !Boolean.valueOf(mActivity.statusHashMap
                                    .get(Consts.LOCAL_LOGIN))) {// 非本地多于100个设备不让再添加
                        addRes = 100;
                        addCount = -100;
                        break;
                    }
                    if (null != addDev) {
                        if (Boolean.valueOf(mActivity.statusHashMap
                                .get(Consts.LOCAL_LOGIN))) {// 本地添加
                            addRes = 0;
                            addCount++;
                        } else {

                            addDev = DeviceUtil.addDevice2(addDev,
                                    mActivity.statusHashMap
                                            .get(Consts.KEY_USERNAME), addDev
                                            .getNickName());
                            if (null != addDev) {
                                addCount++;
                                addRes = 0;
                            }
                        }
                    }
                    MyLog.v("addDevice2----", addRes + "");

                    if (0 == addRes) {
                        addDev.setIp(ip);
                        addDev.setPort(port);
                        myDeviceList.add(0, addDev);
                    }
                }

                for (Device dev1 : addLanList) {
                    for (Device dev2 : myDeviceList) {
                        if (dev1.getFullNo().equalsIgnoreCase(dev2.getFullNo())) {
                            dev2.setOnlineStateLan(1);
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return addCount;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            if (null == mActivity) {
                mActivity = (BaseActivity) getActivity();
            }
            PlayUtil.sortList(myDeviceList, mActivity);
            CacheUtil.saveDevList(myDeviceList);
            mActivity.dismissDialog();
            if (result > 0) {
                refreshList();
                mActivity.dismissDialog();
                mActivity.showTextToast(R.string.add_device_succ);
            } else if (result == -100) {
                mActivity.showTextToast(R.string.str_device_most_count);
            } else {
                refreshList();
                myDLAdapter.setData(myDeviceList);
                myDeviceListView.setAdapter(myDLAdapter);
                mActivity.showTextToast(R.string.add_device_failed);
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度,此方法在主线程执行，用于显示任务执行的进度。
        }
    }

    /**
     * 弹搜出来几个设备界面
     */
    public void alertAddDialog() {
        // 提示对话框
        AlertDialog.Builder builder = new Builder(mActivity);
        builder.setTitle(R.string.tips)
                .setMessage(
                        getResources().getString(R.string.add_broad_dev)
                                .replaceFirst("!",
                                        String.valueOf(broadList.size())))
                .setPositiveButton(R.string.sure,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                mActivity.createDialog("", false);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    /**
     * 自动刷新
     * 
     * @author Administrator
     */
    class AutoUpdateTask extends TimerTask {

        @Override
        public void run() {
            // CacheUtil.saveDevList(myDeviceList);
            MyLog.e(TAG, "AutoUpdateTask--E");
            myDeviceList = CacheUtil.getDevList();
            fragHandler.sendMessage(fragHandler
                    .obtainMessage(Consts.WHAT_AUTO_UPDATE));
            MyLog.e(TAG, "AutoUpdateTask--X");
        }

    }

    // /**
    // * 3分钟广播
    // */
    // public void startBroadTimer() {
    // // 非3G加广播设备
    // if (!getActivity().is3G(false)) {
    // if (null != broadTimer) {
    // broadTimer.cancel();
    // }
    // broadTimer = new Timer();
    //
    // broadTimerTask = new TimerTask() {
    // @Override
    // public void run() {
    // MyLog.e(TAG, "startBroadTimer--E");
    // broadTag = BROAD_THREE_MINITE;
    // PlayUtil.deleteDevIp(myDeviceList);
    // PlayUtil.broadCast(getActivity());
    // MyLog.e(TAG, "startBroadTimer--X");
    // }
    // };
    // broadTimer.schedule(broadTimerTask, 3 * 60 * 1000, 3 * 60 * 1000);
    // }
    // }
    //
    // public void stopBroadTimer() {
    // if (null != broadTimer) {
    // MyLog.e("注销停止broadTimer", "stop--broadTimer");
    // broadTimer.cancel();
    // broadTimer = null;
    // }
    // if (null != broadTimerTask) {
    // MyLog.e("注销停止broadTimerTask", "stop--broadTimerTask");
    // broadTimerTask.cancel();
    // broadTimerTask = null;
    // }
    // }

    /**
     * 3分钟自动刷新
     */
    public void startAutoRefreshTimer() {
        // 3分钟自动刷新设备列表
        updateTask = new AutoUpdateTask();
        if (null != updateTimer) {
            updateTimer.cancel();
        }

        updateTimer = new Timer();
        if (null != updateTimer) {
            updateTimer.schedule(updateTask, 3 * 60 * 1000, 3 * 60 * 1000);
        }
    }

    public void stopRefreshWifiTimer() {
        if (null != updateTimer) {
            MyLog.e("注销停止updateTimer", "stop--updateTimer");
            updateTimer.cancel();
            updateTimer = null;
        }
        if (null != updateTask) {
            MyLog.e("注销停止updateTask", "stop--updateTask");
            updateTask.cancel();
            updateTask = null;
        }
    }

    /**
     * 下载欢迎界面图
     */
    public void downloadAppImage() {
        // 欢迎界面图片数据
        APPImage app = APPImage.fromJson(MySharedPreference
                .getString(Consts.APP_IMAGE));
        int appVersion = 0;
        if (null != app) {
            appVersion = app.getVersion();
        }
        APPImage newAppImage = DeviceUtil.getAPPImage(appVersion);

        if (null != newAppImage && 0 == newAppImage.getResult()) {// 有更新
            // 删除老欢迎界面图
            File appFolder = new File(Consts.WELCOME_IMG_PATH);
            MobileUtil.deleteFile(appFolder);
            MySharedPreference.putString(Consts.APP_IMAGE,
                    newAppImage.toString());
        }
        app = APPImage.fromJson(MySharedPreference.getString(Consts.APP_IMAGE));

        getImage(app, "welcome_zh");
        getImage(app, "welcome_zht");
        getImage(app, "welcome_en");
    }

    /**
     * 本地没有图片才去下载
     * 
     * @param app
     * @param fileName
     */
    public void getImage(APPImage app, String fileName) {
        String welcomePath = Consts.WELCOME_IMG_PATH + fileName
                + Consts.IMAGE_JPG_KIND;
        File imgFile = new File(welcomePath);
        if (!imgFile.exists()) {
            BitmapCache.getInstance().getBitmap(app.getAppImageUrlZh(),
                    "welcome", fileName);
        }
    }

    @Override
    public void onMainAction(int packet_type) {
        // TODO Auto-generated method stub

    }

    public SpannableStringBuilder myGettext() {
        CharSequence text = getResources().getString(R.string.str_no_device_lead);
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        String rexgString = "@";
        Pattern pattern = Pattern.compile(rexgString);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            Drawable drawable = this.getResources().getDrawable(R.drawable.bjd);
            drawable.setBounds(0, 0, 60, 60);// 这里设置图片的大小
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            builder.setSpan(imageSpan, matcher.start(),
                    matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return builder;
    }

    /**
     * 2015.4.18 去云视通获取设备的真正通道数量
     * 
     * @author Administrator
     */
    class ChannelCountThread extends Thread {

        @Override
        public void run() {
            MyLog.v(TAG, "ChannelCountThread:E");
            try {
                Thread.sleep(200);
                if (null != myDeviceList && 0 != myDeviceList.size()) {
                    for (Device dev : myDeviceList) {
                        if (1 == dev.getChannelBindFlag()) {// 通道数量不对，重新获取
                            MyLog.v(TAG, "通道不正确:dev=" + dev.getFullNo());
                            boolean hasBroadChannelCount = false;// true：广播到通道数量
                                                                 // false：未广播到通道数量
                            if (null != broadChanCoutMap) {// 有广播通道信息、
                                MyLog.e(TAG, "E:有广播通道信息，从广播里取");
                                if (broadChanCoutMap.containsKey(dev.getFullNo())) {
                                    int channelCount = broadChanCoutMap.get(dev.getFullNo());
                                    if (channelCount > 0) {
                                        MyLog.v(TAG, "X：广播--通道获取正确了，汇报给服务器:dev=" + dev.getFullNo()
                                                + ",count="
                                                + channelCount);
                                        int modRes = DeviceUtil.modifyChannalNum(dev.getFullNo(),
                                                channelCount);
                                        if (0 == modRes) {
                                            editDeviceChannel(channelCount, dev);
                                        }
                                    }
                                } else {
                                    MyLog.e(TAG, "X：广播没取到");
                                }
                            }

                            if (!hasBroadChannelCount) {// 没广播到通道，从服务器过去
                                MyLog.e(TAG, "E:没广播到通道，从服务器过去");
                                int channelCount = Jni
                                        .getChannelCount(dev.getGid(), dev.getNo(), 2);
                                if (channelCount > 0) {// 通道获取正确了，汇报给服务器
                                    MyLog.v(TAG, "X:云视通--通道获取正确了，汇报给服务器:dev=" + dev.getFullNo()
                                            + ",count="
                                            + channelCount);
                                    int modRes = DeviceUtil.modifyChannalNum(dev.getFullNo(),
                                            channelCount);
                                    if (0 == modRes) {
                                        editDeviceChannel(channelCount, dev);
                                    }
                                } else {
                                    MyLog.e(TAG, "X：云视通没取到");
                                }
                            }

                        }
                    }
                }
                MyLog.v(TAG, "ChannelCountThread:X");
                super.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 2015.4.20修改设备的通道
     * 
     * @param rightChannel
     * @param errorChannel
     * @param dev
     */
    public Device editDeviceChannel(int rightChannel, Device dev) {
        int errorChannel = dev.getChannelList().size();
        if (rightChannel > errorChannel) {// 本地通道少，需要增加通道
            MyLog.v(TAG, "本地通道少，需要增加通道");
            for (int i = errorChannel; errorChannel < rightChannel; i++) {
                Channel channel = new Channel(dev, i, i + 1, false, false, dev.getFullNo() + "_"
                        + (i + 1));
                dev.getChannelList().add(channel);
                MyLog.v(TAG, "index=" + i);
            }
        } else if (rightChannel < errorChannel) {// 本地通道多，需要删通道
            MyLog.v(TAG, "本地通道多，需要删通道");
            for (int i = rightChannel; i < errorChannel; i++) {
                dev.getChannelList().remove(rightChannel);
                errorChannel = dev.getChannelList().size();
                i--;
                MyLog.v(TAG, "index=" + i);
            }

        }

        return dev;
    }
}
