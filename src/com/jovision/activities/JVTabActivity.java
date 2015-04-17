
package com.jovision.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.igexin.sdk.PushManager;
import com.jovetech.CloudSee.temp.R;
import com.jovetech.product.ConfigFragmentFactory;
import com.jovetech.product.CustomizeFragmentFactory;
import com.jovetech.product.IFragmentFactory;
import com.jovetech.product.ITabItem;
import com.jovetech.product.MoreFragmentFactory;
import com.jovetech.product.MyDeviceFragmentFactory;
import com.jovetech.product.VideoFragmentFactory;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.MainApplication;
import com.jovision.activities.JVMoreFragment.OnFuncActionListener;
import com.jovision.adapters.MyPagerAdp;
import com.jovision.bean.BackRunPushInfoStack;
import com.jovision.bean.Device;
import com.jovision.bean.PushInfo;
import com.jovision.commons.CheckUpdateTask;
import com.jovision.commons.GetDemoTask;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.TPushTips;
import com.jovision.customize.CustomizeBoard;
import com.jovision.customize.CustomizeIconTabIndicator;
import com.jovision.customize.CustomizeIconTabIndicator.OnTabSelectedListener;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DefaultExceptionHandler;
import com.jovision.utils.JSONUtil;
import com.jovision.utils.PlayUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
//-------------customize start-----------
//import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
//-------------customize end-------------

public class JVTabActivity extends ShakeActivity implements

        OnPageChangeListener, OnFuncActionListener {
    private static final String TAG = "JVTabActivity";
    int flag = 0;
    private int currentIndex = 0;// 当前页卡index

    protected NotificationManager mNotifyer;
    protected int timer = 16;
    protected RelativeLayout helpbg_relative;
    protected Timer offlineTimer = new Timer();
    private int countshow;
    private int countbbs;
    private OnMainListener mainListener;
    // -------------customize start-----------
    // private BaseFragment mFragments[] = new BaseFragment[4];
    // -------------customize end-------------
    private String showGcsStr;

    private ViewPager viewpager;

    private MyPagerAdp adp;

    private List<View> pics;

    private boolean page1;

    private boolean page2;

    private boolean localFlag;

    private ArrayList<Device> myDeviceList = new ArrayList<Device>();
    // 当前页面索引
    private int currentImage = 0;
    // 前一个页面索引
    private int oldImage = 0;
    // 点集合
    private List<ImageView> dots;
    private LinearLayout ll_dot;

    /** 3分钟广播 */
    private Timer broadTimer;
    private TimerTask broadTimerTask;

    private ImageView local_gone;

    // -------------customize start---------------
    // JVFragmentIndicator mIndicator;
    // -------------customize end---------------
    private MainApplication mApp;

    // -------------customize start---------------
    private static final CharSequence EMPTY = "";
    private CustomizeIconTabIndicator mIndicator;
    private Map<Character, IFragmentFactory> mFactoryMap;
    private Map<Character, BaseFragment> mFragmentsMap;
    private Map<Character, ITabItem> mTabsMap;

    private char mIndicatorSequence[];
    // 最后一个Tab的位置(也就是更多的位置)
    private int mLastTabPosition;
    // 最后位置的Tag(也就是更多的Tag)
    private char mLastTabTag;

    // -------------customize end----------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (null != intent) {
            boolean autoLogin = intent.getBooleanExtra("AutoLogin", false);
            if (autoLogin) {
                String userName = intent.getStringExtra("UserName");
                String userPass = intent.getStringExtra("UserPass");
                statusHashMap.put(Consts.ACCOUNT_ERROR,
                        String.valueOf(Consts.WHAT_SESSION_AUTOLOGIN));
            }
        }

        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
                this));
        // // 如果savedInstanceState!=null，说明在应用在后台被干掉，或者应用崩掉需要重新create
        // if (savedInstanceState != null) {
        // Intent newApp = new Intent(JVTabActivity.this,
        // JVWelcomeActivity.class);
        // startActivity(newApp);
        // finish();
        // }
        MyLog.v(TAG, "onCreate----E");

        mApp = (MainApplication) getApplication();
        MyActivityManager.getActivityManager().pushAlarmActivity(this);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 开启logcat输出，方便debug，发布时请关闭
        if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地登录才有离线推送
            Log.d("GPush", "initializing sdk...");
            PushManager.getInstance().initialize(this.getApplicationContext());
            String userName = statusHashMap.get(Consts.KEY_USERNAME);
            MySharedPreference.putString(Consts.KEY_USERNAME, userName);
            mApp.initNewPushCnt(userName);
            Log.e("GPush", "android.os.Build.MODEL:" + android.os.Build.MODEL);
        }
        MyLog.v(TAG, "onCreate----X");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // 绑定接口
    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            mainListener = (OnMainListener) fragment;
        } catch (Exception e) {
            throw new ClassCastException(this.toString()
                    + " must implement OnMainListener");
        }
        super.onAttachFragment(fragment);
    }

    public interface OnMainListener {
        public void onMainAction(int count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // int cnt = mApp.getNewPushCnt();
        BackRunPushInfoStack.getInstance().clear();
        MyLog.e(TAG, "onDestroy,invoke~~~~~ ");
        // mApp.setNewPushCnt(0);
        // MySharedPreference.putInt(Consts.NEW_PUSH_CNT_KEY, 0);
    }

    private void getPic() {
        flag = 0;
        pics = new ArrayList<View>();
        View view1 = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item1, null);
        View view2 = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item2, null);
        View view3 = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item3, null);
        View view4 = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item4, null);
        View view6 = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item6, null);
        if (localFlag) {
            pics.add(view1);
            pics.add(view2);
            pics.add(view3);
            pics.add(view6);
            initDot(3);
            local_gone.setVisibility(View.GONE);
        } else {
            pics.add(view1);
            pics.add(view2);
            pics.add(view3);
            pics.add(view4);
            pics.add(view6);
            local_gone.setVisibility(View.VISIBLE);
            initDot(4);
        }
    }

    private void getPicone() {
        flag = 1;
        pics = new ArrayList<View>();
        View view = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item, null);
        View view6 = LayoutInflater.from(JVTabActivity.this).inflate(
                R.layout.help_item6, null);
        pics.add(view);
        pics.add(view6);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewpager.setVisibility(View.GONE);
                ll_dot.setVisibility(View.GONE);
            }
        });

    }

    private void initDot(int dotnum) {
        if (dotnum == 3) {

        }
        dots = new ArrayList<ImageView>();
        // 得到点的父布局
        for (int i = 0; i < dotnum; i++) {
            ll_dot.getChildAt(i);// 得到点
            dots.add((ImageView) ll_dot.getChildAt(i));
            dots.get(i).setEnabled(false);// 将点设置为白色
        }
        dots.get(currentImage).setEnabled(true); // 因为默认显示第一张图片，将第一个点设置为黑色
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取后台运行时推送过来的报警，by lkp
        PushInfo pi = BackRunPushInfoStack.getInstance().pop2clear();
        if (null != pi) {
            onNotify(Consts.WHAT_PUSH_MESSAGE,
                    pi.alarmType, 0, pi);
        }

        if (null == (statusHashMap.get(Consts.MORE_BBSNUM))
                || "".equals((statusHashMap.get(Consts.MORE_BBSNUM)))) {
            GetDemoTask taskdemo = new GetDemoTask(JVTabActivity.this);
            String[] params = new String[3];
            params[1] = "4";
            taskdemo.execute(params);
        }
        if (null != (statusHashMap.get(Consts.MORE_BBSNUM))
                && !"".equals((statusHashMap.get(Consts.MORE_BBSNUM)))
                && Consts.LANGUAGE_ZH == ConfigUtil
                        .getLanguage2(JVTabActivity.this)
                && !"hasget".equals(statusHashMap.get("GETNUM"))
                && !Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
            GetnoMessageTask task = new GetnoMessageTask();
            String[] param = new String[3];
            param[0] = statusHashMap.get(Consts.MORE_BBSNUM);
            task.execute(param);
        }
        showGcsStr = statusHashMap.get(Consts.MORE_GCS_SWITCH);

        countshow = 0;
        MyLog.v(TAG, "onResume----E");
        if ("hasget".equals(statusHashMap.get("GETNUM"))) {
            countbbs = 0;
        }
        if (null != mIndicator && null != showGcsStr) {
            if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
                int cnt = mApp.getNewPushCnt();
                countshow = cnt;
                if (!MySharedPreference
                        .getBoolean(Consts.MORE_CLOUD_SHOP)) {
                    countshow = countshow + 1;
                }
                Log.e("TPush", "JVTab onResume cnt mApp.getNewPushCnt():" + cnt);
            }
            int lan = ConfigUtil.getLanguage2(JVTabActivity.this);
            if (lan == Consts.LANGUAGE_ZH) {
                if (!MySharedPreference.getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                    countshow = countshow + 1;
                }
                if ((!MySharedPreference.getBoolean(Consts.MORE_GCSURL))) {
                    if (null != statusHashMap.get(Consts.MORE_GCS_SWITCH)
                            && !"".equalsIgnoreCase(statusHashMap
                                    .get(Consts.MORE_GCS_SWITCH))) {
                        if (1 == Integer.parseInt(statusHashMap
                                .get(Consts.MORE_GCS_SWITCH))) {
                            countshow = countshow + 1;
                        }
                    }
                }
                // if ((!MySharedPreference.getBoolean(Consts.MORE_STATURL))) {
                // countshow = countshow + 1;
                // }
            } else {
                if (!MySharedPreference.getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                    countshow = countshow + 1;
                }
                // if ((!MySharedPreference.getBoolean(Consts.MORE_STATURL))) {
                // countshow = countshow + 1;
                // }
            }
            if (countshow + countbbs > 0) {
                // -------------customize start--------------
                // mIndicator.updateIndicator(3, 0, true, countshow + countbbs);
                mIndicator.updateIndicator(mLastTabPosition, 0, true, countshow + countbbs);
                // -------------customize end --------------
            } else {
                // -------------customize start--------------
                // mIndicator.updateIndicator(3, 0, false, countshow +
                // countbbs);
                mIndicator.updateIndicator(mLastTabPosition, 0, false, countshow + countbbs);
                // -------------customize end --------------
            }
        }

        // -------------customize start--------------
        // Intent intent = getIntent();
        // int index = intent.getIntExtra("tabIndex", -1);
        //
        // if (-1 != index) {
        // currentIndex = index;
        // android.support.v4.app.FragmentManager manager =
        // getSupportFragmentManager();
        // if (null != manager) {
        // getSupportFragmentManager().beginTransaction()
        // .replace(R.id.tab_fragment, mFragments[currentIndex])
        // .commit();
        // } else {
        // MyLog.e(TAG, "TAB_onresume_manager null" + currentIndex);
        // this.finish();
        // }
        // -------------customize end--------------

        // if (currentIndex == 1) {
        // int cnt = mApp.getNewPushCnt();
        // if (cnt > 0) {
        // mApp.setNewPushCnt(0);
        // mIndicator.updateIndicator(1, 0, false);
        // }
        // } else {
        // int cnt = mApp.getNewPushCnt();
        // if (cnt > 0) {
        // mIndicator.updateIndicator(1, cnt, true);
        // } else {
        // mIndicator.updateIndicator(1, 0, false);
        // }
        // }
        // -------------customize start--------------
        // MyLog.v(TAG, "onResume----X");
        // }
        // -------------customize end----------------
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // -------------customize start--------------
        // BaseFragment currentFrag = mFragments[currentIndex];
        BaseFragment currentFrag = getFragmentByTag(currentIndex);
        // -------------customize end----------------
        if (null != currentFrag) {
            ((IHandlerLikeNotify) currentFrag).onNotify(Consts.WHAT_TAB_BACK,
                    0, 0, null);
        }
        openExitDialog();
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case 0:
                viewpager.setCurrentItem(arg1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
        // TODO 增加过滤
        switch (what) {
            case Consts.MORE_BBSNUMNOTY:
                countshow = 0;
                if ("hasget".equals(statusHashMap.get("GETNUM"))) {
                    countbbs = 0;
                }
                if (null != mIndicator
                        && !"hasget".equals(statusHashMap.get("GETNUM"))) {
                    if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
                        int cnt = mApp.getNewPushCnt();
                        countshow = cnt;
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_CLOUD_SHOP)) {
                            countshow = countshow + 1;
                        }
                        Log.e("TPush", "JVTab onResume cnt mApp.getNewPushCnt():"
                                + cnt);
                    }
                    int lan = ConfigUtil.getLanguage2(JVTabActivity.this);
                    if (lan == Consts.LANGUAGE_ZH) {
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                            countshow = countshow + 1;
                        }
                        if ((!MySharedPreference.getBoolean(Consts.MORE_GCSURL))) {
                            if (null != statusHashMap.get(Consts.MORE_GCS_SWITCH)
                                    && !"".equalsIgnoreCase(statusHashMap
                                            .get(Consts.MORE_GCS_SWITCH))) {
                                if (1 == Integer.parseInt(statusHashMap
                                        .get(Consts.MORE_GCS_SWITCH))) {
                                    countshow = countshow + 1;
                                }
                            }
                        }
                        // if
                        // ((!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // countshow = countshow + 1;
                        // }
                    } else {
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                            countshow = countshow + 1;
                        }
                        // if
                        // ((!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // countshow = countshow + 1;
                        // }
                    }
                    if (countshow + countbbs > 0) {
                        // -------------customize start--------------
                        // mIndicator
                        // .updateIndicator(3, 0, true, countshow + countbbs);
                        mIndicator
                                .updateIndicator(mLastTabPosition, 0, true, countshow + countbbs);
                        // -------------customize end----------------
                    } else {
                        // -------------customize start--------------
                        // mIndicator.updateIndicator(3, 0, false, countshow
                        // + countbbs);
                        mIndicator.updateIndicator(mLastTabPosition, 0, false, countshow
                                + countbbs);
                        // -------------customize end----------------
                    }
                }

                break;
            case Consts.CALL_LAN_SEARCH: {
                // PlayUtil.broadIp(obj,JVTabActivity.this);
                // myDeviceList = CacheUtil.getDevList();
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
                        String broadDevNum = gid + no;
                        int netmod = broadObj.optInt("netmod");
                        PlayUtil.hasDev(myDeviceList, broadDevNum, ip, port, netmod);

                    } else if (1 == broadObj.optInt("timeout")) {
                        PlayUtil.sortList(myDeviceList, JVTabActivity.this);
                        CacheUtil.saveDevList(myDeviceList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // -------------customize start--------------
                // BaseFragment currentFrag = mFragments[currentIndex];
                BaseFragment currentFrag = getFragmentByTag(currentIndex);
                // -------------customize end----------------
                if (null != currentFrag) {
                    ((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
                            obj);
                }
                break;
            }
            case Consts.NEW_PUSH_MSG_TAG: {
                // if (currentIndex == 1)// 在信息列表界面
                // {
                // mApp.setNewPushCnt(0);
                // mIndicator.updateIndicator(1, 0, false);
                // } else {
                // boolean show = false;
                // int cnt = mApp.getNewPushCnt();
                // if (cnt > 0) {
                // show = true;
                // }
                // mIndicator.updateIndicator(1, cnt, show);
                // }
                // mIndicator.updateIndicator(3, 0, true);
                // -------------customize start--------------
                // BaseFragment currentFrag = mFragments[currentIndex];
                BaseFragment currentFrag = getFragmentByTag(currentIndex);
                // -------------customize end----------------
                if (null != currentFrag) {
                    ((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
                            obj);
                }
            }
                break;
            case Consts.NEW_PUSH_MSG_TAG_PRIVATE:
                countshow = 0;
                if (null != mIndicator) {
                    if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
                        int cnt = mApp.getNewPushCnt();
                        countshow = cnt;
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_CLOUD_SHOP)) {
                            countshow = countshow + 1;
                        }
                    }
                    int lan = ConfigUtil.getLanguage2(JVTabActivity.this);
                    if (lan == Consts.LANGUAGE_ZH) {
                        // if (cnt > 0
                        // || !MySharedPreference
                        // .getBoolean(Consts.MORE_SYSTEMMESSAGE)
                        // ||
                        // (!MySharedPreference.getBoolean(Consts.MORE_CUSTURL))
                        // ||
                        // (!MySharedPreference.getBoolean(Consts.MORE_STATURL))
                        // || (!MySharedPreference.getBoolean(Consts.MORE_BBS)))
                        // {
                        //
                        // }
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                            countshow = countshow + 1;
                        }
                        if ((!MySharedPreference.getBoolean(Consts.MORE_GCSURL))) {
                            if (null != showGcsStr
                                    && !"".equalsIgnoreCase(showGcsStr)) {
                                if (1 == Integer.parseInt(showGcsStr)) {
                                    countshow = countshow + 1;
                                }
                            }
                        }
                        // if
                        // ((!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // countshow = countshow + 1;
                        // }
                    } else {
                        // if (cnt > 0
                        // || !MySharedPreference
                        // .getBoolean(Consts.MORE_SYSTEMMESSAGE)
                        // ||
                        // (!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // }
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                            countshow = countshow + 1;
                        }
                        // if
                        // ((!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // countshow = countshow + 1;
                        // }
                    }
                    if (countshow + countbbs > 0) {
                        // -------------customize start--------------
                        // mIndicator
                        // .updateIndicator(3, 0, true, countshow + countbbs);
                        mIndicator
                                .updateIndicator(mLastTabPosition, 0, true, countshow + countbbs);
                        // -------------customize end --------------
                    } else {
                        // -------------customize start--------------
                        // mIndicator.updateIndicator(3, 0, false, countshow
                        // + countbbs);
                        mIndicator
                                .updateIndicator(mLastTabPosition, 0, false, countshow + countbbs);
                        // -------------customize end --------------
                    }
                }
                break;
            default:
                // -------------customize start--------------
                // BaseFragment currentFrag = mFragments[currentIndex];
                BaseFragment currentFrag = getFragmentByTag(currentIndex);
                // -------------customize end----------------
                if (null != currentFrag) {
                    ((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
                            obj);
                }
                break;
        }
    }

    @Override
    protected void initSettings() {
        MySharedPreference.init(getApplication());
        ConfigUtil.getJNIVersion();
        Intent intent = getIntent();
        // -------------customize start--------------
        // currentIndex = intent.getIntExtra("tabIndex", 0);
        currentIndex = intent.getIntExtra("tabIndex", 'a');
        // -------------customize end----------------
        localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
    }

    @Override
    protected void initUi() {
        super.initUi();
        MyLog.v(TAG, "initUi----E");
        setContentView(R.layout.tab_layout);
        startBroadTimer();
        local_gone = (ImageView) findViewById(R.id.local_gone);
        viewpager = (ViewPager) findViewById(R.id.tab_viewpager);
        viewpager.setOnPageChangeListener(JVTabActivity.this);
        // -------------customize start--------------
        // mIndicator = (JVFragmentIndicator) findViewById(R.id.indicator);
        // JVFragmentIndicator.setIndicator(currentIndex);
        // -------------customize end----------------

        Boolean local = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
        if (local) {
            Consts.DEVICE_LIST = Consts.LOCAL_DEVICE_LIST;
        } else {
            Consts.DEVICE_LIST = Consts.CACHE_DEVICE_LIST;
            // 在线登陆,检查更新
            CheckUpdateTask task = new CheckUpdateTask(JVTabActivity.this);
            String[] strParams = new String[3];
            strParams[0] = "0";// 0,自动检查更新
            task.execute(strParams);
        }
        // String strModel = android.os.Build.MODEL;
        // if(strModel.toUpperCase().substring(0, 1+1).equals("MI")){
        // new TPushTips(this).showNoticeDialog();
        // }
        if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
            String model = android.os.Build.MODEL;
            Log.e("OS", "model:" + model);
            if (model.startsWith("MI") || model.startsWith("HM")) {// 对于刷机的手机好像有影响
                String strRom = ConfigUtil
                        .getSystemProperty("ro.miui.ui.version.name");
                if (strRom == null || strRom.equals("")) {
                    // showTextToast("不是MIUI");
                } else {
                    if (!MySharedPreference.getBoolean("TP_AUTO_TIPS", false)) {
                        if (strRom.equals("V6")) {
                            new TPushTips(this)
                                    .showNoticeDialog(R.string.str_tpush_autostart_tips_v6);
                        } else {
                            new TPushTips(this)
                                    .showNoticeDialog(R.string.str_tpush_autostart_tips_v5);
                        }
                    }

                }
            } else {
                Log.e("OS", "不是小米或者红米系列");
            }
        }

        // -------------customize start------------
        // mFragments[0] = new JVMyDeviceFragment();
        // mFragments[1] = new JVVideoFragment();
        // mFragments[2] = new JVDeviceManageFragment();
        // mFragments[3] = new JVMoreFragment();
        // -------------customize end--------------

        if (!MySharedPreference.getBoolean(Consts.MORE_PAGETWO)) {
            ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
            ll_dot.setVisibility(View.GONE);
            viewpager.setCurrentItem(0);
            viewpager.setVisibility(View.VISIBLE);
            getPicone();
            adp = new MyPagerAdp(pics);
            viewpager.setAdapter(adp);
            MySharedPreference.putBoolean(Consts.MORE_PAGETWO, true);
        }
        // -------------customize start------------
        // ## 此处原有的indicator操作移动到了最下方
        // 初始化自定义的tabs view
        initCustomizeViews();
        // -------------customize end--------------

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        if (null != manager) {
            // -------------customize start------------
            // manager.beginTransaction()
            // .replace(R.id.tab_fragment, mFragments[0]).commit();
            manager.beginTransaction()
                    .replace(R.id.tab_fragment, getFragmentByTag(mIndicatorSequence[0])).commit();
            // -------------customize end--------------
        } else {
            MyLog.e(TAG, "TAB_initUI_manager null" + currentIndex);
            this.finish();
        }
        MyLog.v(TAG, "initUi----X");
    }

    @Override
    protected void saveSettings() {

    }

    @Override
    protected void freeMe() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            MyLog.v("notifyer",
                    ((MainApplication) this.getApplication()).currentNotifyer
                            + "");
            String notifer = ((MainApplication) this.getApplication()).currentNotifyer
                    + "";
            if (notifer.startsWith("JVMyDeviceFragment")) {
                if (JVMyDeviceFragment.isshow) {
                    JVMyDeviceFragment.isshow = false;
                    JVMyDeviceFragment.myDLAdapter.setShowDelete(false);
                    JVMyDeviceFragment.myDLAdapter.notifyDataSetChanged();
                } else {
                    exit();
                }
            } else if (notifer.startsWith("JVVideoFragment")) {
                if (JVVideoFragment.webView.canGoBack()) {
                    ((MainApplication) this.getApplication()).currentNotifyer
                            .onNotify(Consts.TAB_WEBVIEW_BACK, 0, 0, null);
                } else {
                    exit();
                }
            } else {
                exit();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        currentImage = arg0; // 获取当前页面索引
        if (flag == 0) {
            if (!localFlag) {
                if (arg0 != 4) {
                    dots.get(oldImage).setEnabled(false); // 前一个点设置为白色
                    dots.get(currentImage).setEnabled(true); // 当前点设置为黑色
                    oldImage = currentImage; // 改变前一个索引
                    currentImage = (currentImage) % 4; // 有几张就对几求余
                    onNotify(0, currentImage, 0, null);
                }
                if (arg0 == 4) {
                    viewpager.setVisibility(View.GONE);
                    ll_dot.setVisibility(View.GONE);
                }
            } else {
                if (arg0 != 3) {
                    dots.get(oldImage).setEnabled(false); // 前一个点设置为白色
                    dots.get(currentImage).setEnabled(true); // 当前点设置为黑色
                    oldImage = currentImage; // 改变前一个索引
                    currentImage = (currentImage) % 3; // 有几张就对几求余
                    onNotify(0, currentImage, 0, null);
                }
                if (arg0 == 3) {
                    viewpager.setVisibility(View.GONE);
                    ll_dot.setVisibility(View.GONE);
                }
            }
        } else if (flag == 1) {
            if (arg0 == 1) {
                viewpager.setVisibility(View.GONE);
                ll_dot.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 3分钟广播
     */
    public void startBroadTimer() {
        // 本地 非3G加广播设备
        if (!ConfigUtil.is3G(JVTabActivity.this, false) && localFlag) {
            if (null != broadTimer) {
                broadTimer.cancel();
            }
            broadTimer = new Timer();
            broadTimerTask = new TimerTask() {
                @Override
                public void run() {
                    MyLog.e(TAG, "startBroadTimer--E");
                    myDeviceList = CacheUtil.getDevList();
                    PlayUtil.deleteDevIp(myDeviceList);
                    PlayUtil.broadCast(JVTabActivity.this);
                    MyLog.e(TAG, "startBroadTimer--X");
                }
            };
            broadTimer.schedule(broadTimerTask, 3 * 60 * 1000, 3 * 60 * 1000);
        }
    }

    public void stopBroadTimer() {
        if (null != broadTimer) {
            MyLog.e("注销停止broadTimer", "stop--broadTimer");
            broadTimer.cancel();
            broadTimer = null;
        }
        if (null != broadTimerTask) {
            MyLog.e("注销停止broadTimerTask", "stop--broadTimerTask");
            broadTimerTask.cancel();
            broadTimerTask = null;
        }
    }

    @Override
    public void OnFuncEnabled(int func_index, int enabled) {
        // TODO Auto-generated method stub
        switch (func_index) {
            case 0:
                countshow = 0;
                if ("hasget".equals(statusHashMap.get("GETNUM"))) {
                    countbbs = 0;
                }
                if (null != mIndicator) {
                    if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
                        int cnt = mApp.getNewPushCnt();
                        countshow = cnt;
                        if (!MySharedPreference
                                .getBoolean(Consts.MORE_CLOUD_SHOP)) {
                            countshow = countshow + 1;
                        }
                        Log.e("TPush", "JVTab onResume cnt mApp.getNewPushCnt():" + cnt);
                    }
                    int lan = ConfigUtil.getLanguage2(JVTabActivity.this);
                    if (lan == Consts.LANGUAGE_ZH) {
                        if (!MySharedPreference.getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                            countshow = countshow + 1;
                        }
                        if ((!MySharedPreference.getBoolean(Consts.MORE_GCSURL))) {
                            if (null != statusHashMap.get(Consts.MORE_GCS_SWITCH)
                                    && !"".equalsIgnoreCase(statusHashMap
                                            .get(Consts.MORE_GCS_SWITCH))) {
                                if (1 == Integer.parseInt(statusHashMap
                                        .get(Consts.MORE_GCS_SWITCH))) {
                                    countshow = countshow + 1;
                                }
                            }
                        }
                        // if
                        // ((!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // countshow = countshow + 1;
                        // }
                    } else {
                        if (!MySharedPreference.getBoolean(Consts.MORE_SYSTEMMESSAGE)) {
                            countshow = countshow + 1;
                        }
                        // if
                        // ((!MySharedPreference.getBoolean(Consts.MORE_STATURL)))
                        // {
                        // countshow = countshow + 1;
                        // }
                    }
                    if (countshow + countbbs > 0) {
                        // -------------customize start--------------
                        // mIndicator.updateIndicator(3, 0, true, countshow +
                        // countbbs);
                        mIndicator.updateIndicator(mLastTabPosition, 0, true, countshow + countbbs);
                        // -------------customize end --------------
                    } else {
                        // -------------customize start--------------
                        // mIndicator.updateIndicator(3, 0, false, countshow +
                        // countbbs);
                        mIndicator
                                .updateIndicator(mLastTabPosition, 0, false, countshow + countbbs);
                        // -------------customize end --------------
                    }

                }
                break;

            default:
                break;
        }
    }

    @Override
    public void OnFuncSelected(int func_index, String params) {
        // TODO Auto-generated method stub
    }

    // 获取论坛未读消息
    private class GetnoMessageTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            countbbs = 0;
            String gcsString = "";
            // String gcsurlString =
            // "http://bbs.cloudsee.net/v.php?mod=api&act=user_pm"+"&sid ="+JVACCOUNT.GetSession()
            String result = JSONUtil.httpGet(params[0]);
            MyLog.v("BBS_notread", "request=" + params[0] + ";result=" + result);
            // request=http://bbs.cloudsee.net/v.php?mod=auth&act=sid_login&next=/&sid=c01ed43499478b62f4cb233112a41fe8
            // request=http://bbs.cloudsee.net/v.php?mod=api&act=user_pm&sid=1dad46caaa92eb0ea59a4c348fd5de81;result={"msg":"ok","errCode":1,"data":[{"url":"","count":0}]}
            try {
                JSONObject responseObject = new JSONObject(result);
                JSONArray dataArray = new JSONArray(
                        responseObject.optString("data"));

                countbbs = dataArray.getJSONObject(0).optInt("count");
                gcsString = dataArray.getJSONObject(0).optString("url");
                statusHashMap.put(Consts.MORE_BBSNUMURL, gcsString);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return countbbs;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            mainListener.onMainAction(result);
            statusHashMap.put("GETNUM", "hasget");
            onNotify(Consts.NEW_BBS, result, 0, null);
            if (countshow + result > 0) {
                // -------------customize start--------------
                // mIndicator.updateIndicator(3, 0, true, countshow + result);
                mIndicator.updateIndicator(mLastTabPosition, 0, true, countshow + result);
                // -------------customize end --------------
            } else {
                // -------------customize start--------------
                // mIndicator.updateIndicator(3, 0, false, countshow + result);
                mIndicator.updateIndicator(mLastTabPosition, 0, false, countshow + result);
                // -------------customize end --------------
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        if (Consts.SCAN_IN_LINE_REQUEST == requestCode && Consts.SCAN_IN_LINE_RESULT == resultCode) {
            BaseFragment currentFrag = getFragmentByTag(currentIndex);
            if (null != currentFrag) {
                ((IHandlerLikeNotify) currentFrag).onNotify(Consts.SCAN_IN_LINE_REQUEST,
                        Consts.SCAN_IN_LINE_RESULT, 0,
                        data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    // @Override
    // public void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // onNotify(Consts.TAB_ON_ACTIVITY_RESULT, requestCode, resultCode, data);
    // super.onActivityResult(requestCode, resultCode, data);
    // }

    // ---------------------------------------------------------------------
    // ## customize
    // ---------------------------------------------------------------------
    private void initCustomizeViews() {
        mFactoryMap = new HashMap<Character, IFragmentFactory>();
        mFragmentsMap = new HashMap<Character, BaseFragment>();
        mTabsMap = new HashMap<Character, ITabItem>();

        mIndicator = (CustomizeIconTabIndicator) findViewById(R.id.indicator);

        List<ITabItem> tabItems = initTabs();
        mIndicator.setIndicator(tabItems);

        initIndicatorListener();
    }

    /**
     * 初始化Tabs
     * 
     * @return tabs
     */
    private List<ITabItem> initTabs() {
        List<ITabItem> tabItems = new ArrayList<ITabItem>();

        mIndicatorSequence = getIndicatorSequence();
        mLastTabPosition = mIndicatorSequence.length - 1;
        mLastTabTag = mIndicatorSequence[mLastTabPosition];
        for (int i = 0; i < mIndicatorSequence.length; i++) {
            initFramentsAndTabs(mIndicatorSequence[i]);
            tabItems.add(getTabByTag(mIndicatorSequence[i]));
        }

        return tabItems;
    }

    /**
     * Tab选项卡的监听器
     */
    private void initIndicatorListener() {

        mIndicator.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int oldPosition, char tag,
                    boolean isCustomize) {
                MyLog.v(TAG, "--------onTabSelected start-------");
                MyLog.v(TAG, "position=>" + position);
                MyLog.v(TAG, "oldPosition=>" + oldPosition);
                MyLog.v(TAG, "tag=>" + tag);
                MyLog.v(TAG, "isCustomize=>" + isCustomize);
                MyLog.v(TAG, "--------onTabSelected end  -------");

                if (isCustomize) {
                    CustomizeBoard shareBoard = new CustomizeBoard(
                            JVTabActivity.this);
                    shareBoard.showAtLocation(getWindow().getDecorView(),
                            Gravity.BOTTOM, 0, 0);

                } else {
                    // 保存当前的fragment标记
                    currentIndex = tag;
                    MyLog.v(TAG, "--fragment replace--");
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tab_fragment,
                                    getFragmentByTag(tag)).commit();
                    // 执行旧版的indicator中的其它操作
                    oldTabIndicate(null, currentIndex);
                }
            }

            @Override
            public void onTabReselected(int position, char tag,
                    boolean isCustomize) {
                MyLog.v(TAG, "--------onTabReselected start-------");
                MyLog.v(TAG, "position=>" + position);
                MyLog.v(TAG, "tag=>" + tag);
                MyLog.v(TAG, "isCustomize=>" + isCustomize);
                MyLog.v(TAG, "--------onTabReselected end-------");

            }

        });
    }

    /**
     * 获取保存Tab选项卡配置的数组
     * 
     * @return tab indicator sequence's array
     */
    public char[] getIndicatorSequence() {
        String indicatorSeq = MySharedPreference
                .getString("indicator_sequence");
        // 检测是否使用保存起来的tabs顺序来显示tabs
        boolean isUseSavedTabsSeq = getResources().getBoolean(R.bool.use_saved_tabs_seq);
        if (EMPTY.equals(indicatorSeq) || !isUseSavedTabsSeq) {
            StringBuffer sbstr = new StringBuffer();
            // 默认的Tabs的顺序
            String tabsDefSeq[] = getResources().getStringArray(
                    R.array.array_tabs_sequence);
            for (int i = 0, length = tabsDefSeq.length; i < length; i++) {
                sbstr.append(tabsDefSeq[i]);
            }
            indicatorSeq = sbstr.toString();
            // 保存选项卡配置
            MySharedPreference.putString("indicator_sequence", indicatorSeq);
        }
        MyLog.v(TAG, "--indicator Sequence--" + indicatorSeq);
        return indicatorSeq.toCharArray();
    }

    /**
     * 初始化工厂,保存tabs和fragments
     * 
     * @param tag 标记
     * @return fragment factory
     */
    private void initFramentsAndTabs(char tag) {
        IFragmentFactory fragmentFactory = null;

        if (!mFactoryMap.containsKey(tag)) {
            switch (tag) {
                case 'a':
                    fragmentFactory = new MyDeviceFragmentFactory();
                    break;
                case 'b':
                    fragmentFactory = new VideoFragmentFactory();
                    break;
                case 'c':
                    fragmentFactory = new CustomizeFragmentFactory();
                    break;
                case 'd':
                    fragmentFactory = new ConfigFragmentFactory();
                    break;
                case 'e':
                    fragmentFactory = new MoreFragmentFactory();
                    break;
                case 'g':
                    break;
                case 'h':
                    break;
                default:
                    MyLog.v(TAG, "--default tab--");
            }
            // 保存工厂
            mFactoryMap.put(tag, fragmentFactory);
        } else {
            fragmentFactory = mFactoryMap.get(tag);
        }

        // 保存tab
        if (!mTabsMap.containsKey(tag)) {
            mTabsMap.put(tag, fragmentFactory.getTab());
        }
        // 保存fragment
        if (!mFragmentsMap.containsKey(tag)) {
            mFragmentsMap.put(tag, (BaseFragment) fragmentFactory.newInstance());
        }
    }

    /**
     * 通过标记获取Fragment
     * 
     * @param mark fragment对应的标记
     */
    private BaseFragment getFragmentByTag(int mark) {
        char charTag = (char) mark;
        return mFragmentsMap.get(charTag);
    }

    /**
     * 通过标记获取Tab
     */
    private ITabItem getTabByTag(int mark) {
        char charTag = (char) mark;
        return mTabsMap.get(charTag);
    }

    /**
     * 获取最后一个Tab的对应的fragment(其实就是获取更多对应的fragment) 这个功能在中间扩展功能中使用
     */
    public BaseFragment getLastFragment() {
        return mFragmentsMap.get(mLastTabTag);
    }

    /**
     * 跳转到某个fragment
     * 
     * @param tag
     */
    public void jumpFragmentByTag(char tag) {
        int position = 0;
        for (int i = 0; i < mIndicatorSequence.length; i++) {
            if (tag == mIndicatorSequence[i]) {
                position = i;
            }
        }
        MyLog.v(TAG, "--jump fragment's position--" + position);
        // 跳转到某个fragment
        mIndicator.jumpFragment(position);
    }

    /**
     * 当前Tab的索引
     * 
     * @return
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * 获取当前的fragment
     * 
     * @return
     */
    public BaseFragment getCurrentFragment() {
        return getFragmentByTag(currentIndex);
    }

    /**
     * 小维商城
     */
    public void jumpShop() {
        if (!ConfigUtil.isConnected(this)) {
            this.alertNetDialog();
        } else {
            // 商城地址存在,直接跳转
            if (null != statusHashMap
                    .get(Consts.MORE_SHOPURL)) {
                Intent intentAD0 = new Intent(this,
                        JVWebViewActivity.class);
                intentAD0
                        .putExtra(
                                "URL", statusHashMap
                                        .get(Consts.MORE_SHOPURL));
                intentAD0.putExtra("title", -2);
                startActivity(intentAD0);
            } else {
                if ("false".equals(statusHashMap
                        .get(Consts.KEY_INIT_ACCOUNT_SDK))) {
                    MyLog.e("Login", "初始化账号SDK失败");
                    ConfigUtil
                            .initAccountSDK(((MainApplication) this
                                    .getApplication()));// 初始化账号SDK
                }
                // 重新请求url地址,然后进行跳转
                GetDemoTask UrlTask = new GetDemoTask(
                        this);
                String[] demoParams = new String[3];
                demoParams[1] = "7";
                UrlTask.execute(demoParams);
            }
        }
    }

    /**
     * 原有的indicator选择操作. 因为牵扯到的一些其它的业务,所以保留.在initIndicatorListener会调用
     * 原有代表位置的which,现在作用代表具体fragment的tag来使用
     * 
     * @param v
     * @param which
     */
    public void oldTabIndicate(View v, int which) {
        char tag = (char) which;
        try {
            // currentIndex = which;
            // getSupportFragmentManager().beginTransaction()
            // .replace(R.id.tab_fragment, mFragments[which])
            // .commit();
            switch (tag) {
                case 'a':
                    if (!page2
                            && !MySharedPreference
                                    .getBoolean(Consts.MORE_PAGETWO)) {
                        ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
                        ll_dot.setVisibility(View.GONE);
                        viewpager.setCurrentItem(0);
                        viewpager.setVisibility(View.VISIBLE);
                        getPicone();
                        adp = new MyPagerAdp(pics);
                        viewpager.setAdapter(adp);
                        MySharedPreference.putBoolean(Consts.MORE_PAGETWO,
                                true);
                    } else {
                        if (MySharedPreference.getBoolean(Consts.MORE_HELP)
                                && !MySharedPreference
                                        .getBoolean(Consts.MORE_PAGETWO)) {
                            ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
                            ll_dot.setVisibility(View.GONE);
                            viewpager.setCurrentItem(0);
                            viewpager.setVisibility(View.VISIBLE);
                            getPicone();
                            adp = new MyPagerAdp(pics);
                            viewpager.setAdapter(adp);
                            MySharedPreference.putBoolean(
                                    Consts.MORE_PAGETWO, false);
                            page2 = true;
                        }
                    }
                    break;
                case 'b':
                    // int cnt = mApp.getNewPushCnt();
                    // if (cnt > 0) {
                    // mApp.setNewPushCnt(0);
                    // mIndicator.updateIndicator(1, 0, false);
                    // }
                    break;
                case 'd':
                    myDeviceList = CacheUtil.getDevList();
                    if (0 != myDeviceList.size()) {
                        if (!page1
                                && !MySharedPreference
                                        .getBoolean(Consts.MORE_PAGEONE)) {
                            ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
                            ll_dot.setVisibility(View.VISIBLE);
                            viewpager.setCurrentItem(0);
                            viewpager.setVisibility(View.VISIBLE);
                            getPic();
                            adp = new MyPagerAdp(pics);
                            viewpager.setAdapter(adp);
                            MySharedPreference.putBoolean(
                                    Consts.MORE_PAGEONE, true);
                        } else {
                            if (MySharedPreference
                                    .getBoolean(Consts.MORE_HELP)
                                    && !MySharedPreference
                                            .getBoolean(Consts.MORE_PAGEONE)) {
                                ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
                                ll_dot.setVisibility(View.VISIBLE);
                                viewpager.setCurrentItem(0);
                                viewpager.setVisibility(View.VISIBLE);
                                getPic();
                                adp = new MyPagerAdp(pics);
                                viewpager.setAdapter(adp);
                                MySharedPreference.putBoolean(
                                        Consts.MORE_PAGEONE, false);
                                page1 = true;
                            }
                        }
                    }
                    break;
                case 'e':
                    if (Consts.LANGUAGE_ZH == ConfigUtil
                            .getLanguage2(JVTabActivity.this)) {
                        if (null == (statusHashMap.get(Consts.MORE_BBSNUM))
                                || "".equals((statusHashMap
                                        .get(Consts.MORE_BBSNUM)))) {
                            GetDemoTask taskdemo = new GetDemoTask(
                                    JVTabActivity.this);
                            String params[] = new String[3];
                            params[1] = "4";
                            taskdemo.execute(params);
                        }

                        if (null != (statusHashMap.get(Consts.MORE_BBSNUM))
                                && !"".equals((statusHashMap
                                        .get(Consts.MORE_BBSNUM)))
                                && Consts.LANGUAGE_ZH == ConfigUtil
                                        .getLanguage2(JVTabActivity.this)
                                && !"hasget".equals(statusHashMap
                                        .get("GETNUM"))
                                && !Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
                            GetnoMessageTask task = new GetnoMessageTask();
                            String[] param = new String[3];
                            param[0] = statusHashMap
                                    .get(Consts.MORE_BBSNUM);
                            task.execute(param);
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MySharedPreference.getBoolean(Consts.MORE_PAGETWO)
                && MySharedPreference.getBoolean(Consts.MORE_PAGEONE)) {
            MySharedPreference.putBoolean(Consts.MORE_HELP, false);
        }
    }
}
