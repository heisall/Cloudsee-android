
package com.jovision.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.Device;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;
import com.tencent.stat.StatService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("SetJavaScriptEnabled")
public class JVNewAddDeviceActivity extends ShakeActivity {
    private static final String TAG = "JVNewAddDeviceActivity";

    private EditText devNumET;
    private ImageButton editimg_clearn;
    private ImageView tab_icon, save_icon;
    private TextView tab_title;
    private WebView add_device_wv;
    private String url = "http://www.cloudsee.net/mobile/nineBlock.action";
    private ArrayList<Device> deviceList = new ArrayList<Device>();
    private Device addDevice;
    private boolean hasBroadIP = false;// 是否广播完IP
    private int onLine = 0;
    private String ip = "";
    private int port = 0;
    private int channelCount = -1;

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
        // 广播回调
            case Consts.CALL_LAN_SEARCH: {
                JSONObject broadObj;
                try {
                    broadObj = new JSONObject(obj.toString());
                    if (0 == broadObj.optInt("timeout")) {
                        String broadDevNum = broadObj.optString("gid")
                                + broadObj.optInt("no");
                        int netmod = broadObj.optInt("netmod");
                        if (broadDevNum.equalsIgnoreCase(devNumET.getText()
                                .toString())) {// 同一个设备
                            // addDevice.setOnlineState(1);
                            // addDevice.setIp(broadObj.optString("ip"));
                            // addDevice.setPort(broadObj.optInt("port"));
                            onLine = 1;
                            ip = broadObj.optString("ip");
                            port = broadObj.optInt("port");
                            channelCount = broadObj.optInt("count");
                        }

                    } else if (1 == broadObj.optInt("timeout")) {
                        hasBroadIP = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    protected void initSettings() {
        deviceList = CacheUtil.getDevList();
    }

    @Override
    protected void initUi() {
        super.initUi();
        setContentView(R.layout.new_adddevice_layout);
        leftBtn = (Button) findViewById(R.id.btn_left);
        alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
        accountError = (TextView) findViewById(R.id.accounterror);
        rightBtn = (Button) findViewById(R.id.btn_right);
        rightBtn.setVisibility(View.GONE);
        currentMenu = (TextView) findViewById(R.id.currentmenu);

        currentMenu.setText(R.string.str_new_add_device);
        leftBtn.setOnClickListener(myOnClickListener);
        devNumET = (EditText) findViewById(R.id.new_adddevice_et);
        editimg_clearn = (ImageButton) findViewById(R.id.editimg_clearn);
        editimg_clearn.setOnClickListener(myOnClickListener);
        tab_title = (TextView) findViewById(R.id.tab_title);
        tab_icon = (ImageView) findViewById(R.id.tab_icon);
        tab_icon.setOnClickListener(myOnClickListener);
        // tab_title.setOnClickListener(myOnClickListener);
        save_icon = (ImageView) findViewById(R.id.save_icon);
        save_icon.setOnClickListener(myOnClickListener);
        devNumET.addTextChangedListener(new TextWatcherImpl());
        devNumET.setOnFocusChangeListener(new FocusChangeListenerImpl());
        // webview显示设备内容
        add_device_wv = (WebView) findViewById(R.id.add_device_wv);
        add_device_wv.getSettings().setJavaScriptEnabled(true);
        add_device_wv.requestFocus(View.FOCUS_DOWN);
        add_device_wv.setWebViewClient(myWebviewClient);
        add_device_wv.loadUrl(url);

    }

    WebViewClient myWebviewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String newurl) {
            // view.loadUrl(newurl);
            String param_array[] = newurl.split("\\?");
            HashMap<String, String> resMap;
            resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);
            String deviceType = resMap.get("device_type");
            if (null != deviceType) {
                int devType = Integer.parseInt(deviceType);

                switch (devType) {
                    case Consts.NET_DEVICE_TYPE_OTHER:
                    case Consts.NET_DEVICE_TYPE_YST_NUMBER: {// 云视通号添加
                        StatService.trackCustomEvent(JVNewAddDeviceActivity.this,
                                "Add by CloudSEE ID", JVNewAddDeviceActivity.this.getResources()
                                        .getString(R.string.census_addcloudseeid));
                        Intent addIntent = new Intent();
                        addIntent.setClass(JVNewAddDeviceActivity.this, JVAddDeviceActivity.class);
                        addIntent.putExtra("QR", false);
                        JVNewAddDeviceActivity.this.startActivityForResult(addIntent,
                                Consts.DEVICE_ADD_REQUEST);
                        break;
                    }
                    case Consts.NET_DEVICE_TYPE_SOUND_WAVE: {// 声波配置添加
                        StatService.trackCustomEvent(
                                JVNewAddDeviceActivity.this,
                                "SoundWave",
                                JVNewAddDeviceActivity.this.getResources().getString(
                                        R.string.census_soundwave));
                        Intent intent = new Intent();
                        intent.setClass(JVNewAddDeviceActivity.this, JVWaveSetActivity.class);
                        JVNewAddDeviceActivity.this.startActivity(intent);
                        break;
                    }
                    case Consts.NET_DEVICE_TYPE_AP_SET: {// AP设置添加
                        StatService.trackCustomEvent(
                                JVNewAddDeviceActivity.this,
                                "Add Wi_Fi Device",
                                JVNewAddDeviceActivity.this.getResources().getString(
                                        R.string.census_addwifidev));
                        JVNewAddDeviceActivity.this.startSearch(false);

                        break;
                    }
                    case Consts.NET_DEVICE_TYPE_IPDOMAIN: {// IP 域名添加
                        StatService.trackCustomEvent(JVNewAddDeviceActivity.this, "IP/DNS",
                                JVNewAddDeviceActivity.this
                                        .getResources().getString(R.string.census_ipdns));
                        Intent intent = new Intent();
                        intent.setClass(JVNewAddDeviceActivity.this, JVAddIpDeviceActivity.class);
                        JVNewAddDeviceActivity.this.startActivityForResult(intent,
                                Consts.DEVICE_ADD_REQUEST);
                        break;
                    }
                }
            }

            return true;

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Consts.DEVICE_ADD_REQUEST) {
            if (resultCode == Consts.DEVICE_ADD_SUCCESS_RESULT) {// 设备添加成功
                JVNewAddDeviceActivity.this.finish();
            }
        }
    }

    OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_left:
                    JVNewAddDeviceActivity.this.finish();
                    break;
                case R.id.tab_icon:
                    StatService.trackCustomEvent(
                            JVNewAddDeviceActivity.this,
                            "Scan QR Code",
                            JVNewAddDeviceActivity.this.getResources().getString(
                                    R.string.census_scanqrcod));
                    Intent addIntent = new Intent();
                    addIntent.setClass(JVNewAddDeviceActivity.this, JVAddDeviceActivity.class);
                    addIntent.putExtra("QR", true);
                    JVNewAddDeviceActivity.this.startActivity(addIntent);

                    break;
                case R.id.editimg_clearn:
                    devNumET.setText("");
                    break;
                case R.id.save_icon:
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(JVNewAddDeviceActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    saveMethod(devNumET.getText().toString(), Consts.DEFAULT_USERNAME
                            , Consts.DEFAULT_PASSWORD, devNumET.getText().toString());
                    break;
            // case R.id.tab_title:
            // StatService.trackCustomEvent(
            // JVNewAddDeviceActivity.this,
            // "Scan QR Code",
            // JVNewAddDeviceActivity.this.getResources().getString(
            // R.string.census_scanqrcod));
            // Intent addIntent1 = new Intent();
            // addIntent1.setClass(JVNewAddDeviceActivity.this,
            // JVAddDeviceActivity.class);
            // addIntent1.putExtra("QR", true);
            // JVNewAddDeviceActivity.this.startActivity(addIntent1);
            //
            // break;
            }
        }
    };

    private class FocusChangeListenerImpl implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (devNumET.getText().toString().length() >= 1) {
                editimg_clearn.setVisibility(View.VISIBLE);
                save_icon.setVisibility(View.VISIBLE);
            } else {
                editimg_clearn.setVisibility(View.GONE);
                save_icon.setVisibility(View.GONE);
            }
        }

    }

    // 当输入结束后判断是否显示右边clean的图标
    private class TextWatcherImpl implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            if (devNumET.getText().toString().length() >= 1) {
                editimg_clearn.setVisibility(View.VISIBLE);
                save_icon.setVisibility(View.VISIBLE);
                tab_title.setVisibility(View.GONE);
                tab_icon.setVisibility(View.GONE);
            } else {
                editimg_clearn.setVisibility(View.GONE);
                save_icon.setVisibility(View.GONE);
                tab_icon.setVisibility(View.VISIBLE);
                tab_title.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    }

    @Override
    protected void saveSettings() {

    }

    @Override
    protected void freeMe() {

    }

    /**
     * 保存设备信息
     * 
     * @param devNum
     * @param userName
     * @param userPwd
     */
    public void saveMethod(String devNum, String userName, String userPwd,
            String nickName) {
        if (null == deviceList) {
            deviceList = new ArrayList<Device>();
        }
        int size = deviceList.size();
        if ("".equalsIgnoreCase(devNum)) {// 云视通号不可为空
            showTextToast(R.string.login_str_device_ytnum_notnull);
            return;
        } else if (!ConfigUtil.checkYSTNum(devNum)) {// 验证云视通号是否合法
            showTextToast(R.string.increct_yst_tips);
            return;
        } else if (!"".equals(nickName) && !ConfigUtil.checkNickName(nickName)) {// 昵称不合法
            showTextToast(R.string.login_str_nike_name_order);
            return;
        } else if ("".equalsIgnoreCase(userName)) {// 用户名不可为空
            showTextToast(R.string.login_str_device_account_notnull);
            return;
        } else if (!ConfigUtil.checkDeviceUsername(userName)) {// 用户名是否合法
            showTextToast(R.string.login_str_device_account_error);
            return;
        } else if (!ConfigUtil.checkDevicePwd(userPwd)) {
            showTextToast(R.string.login_str_device_pass_error);
            return;
        } else if (size >= 100
                && !Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地多于100个设备不让再添加
            showTextToast(R.string.str_device_most_count);
            return;
        } else {
            // 判断一下是否已存在列表中
            boolean find = false;
            if (null != deviceList && 0 != deviceList.size()) {
                for (Device dev : deviceList) {
                    if (devNum.equalsIgnoreCase(dev.getFullNo())) {
                        find = true;
                        break;
                    }
                }
            }

            if (find) {
                devNumET.setText("");
                showTextToast(R.string.str_device_exsit);
                return;
            }
        }

        AddDevTask task = new AddDevTask();
        String[] strParams = new String[4];
        strParams[0] = ConfigUtil.getGroup(devNum);
        strParams[1] = String.valueOf(ConfigUtil.getYST(devNum));
        strParams[2] = String.valueOf(2);
        strParams[3] = nickName;
        task.execute(strParams);
    }

    // 设置三种类型参数分别为String,Integer,String
    class AddDevTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {

            String nickName = params[3];
            int addRes = -1;
            boolean localFlag = Boolean.valueOf(statusHashMap
                    .get(Consts.LOCAL_LOGIN));
            try {
                MyLog.e(TAG, "getChannelCount E = ");

                // 非3G广播获取通道数量
                if (PlayUtil.broadCast(JVNewAddDeviceActivity.this)) {
                    int errorCount = 0;
                    while (!hasBroadIP) {
                        Thread.sleep(1000);
                        errorCount++;
                        if (errorCount >= 10) {
                            break;
                        }
                    }
                }

                MyLog.e(TAG, "getChannelCount C1 = " + channelCount);
                if (channelCount <= 0) {
                    channelCount = Jni.getChannelCount(params[0],
                            Integer.parseInt(params[1]),
                            Integer.parseInt(params[2]));
                }

                MyLog.e(TAG, "getChannelCount C2 = " + channelCount);
                if (channelCount <= 0) {
                    channelCount = 4;
                }

                addDevice = new Device("", 0, params[0],
                        Integer.parseInt(params[1]), Consts.DEFAULT_USERNAME,
                        Consts.DEFAULT_PASSWORD, false,
                        channelCount, 0, nickName);
                // MyLog.v(TAG, "dev = " + addDev.toString());
                if (null != addDevice) {
                    if (localFlag) {// 本地添加
                        addRes = 0;
                        if (!"".equals(nickName)) {
                            addDevice.setNickName(nickName);
                        }
                    } else {
                        addDevice = DeviceUtil.addDevice2(addDevice,
                                statusHashMap.get(Consts.KEY_USERNAME),
                                nickName);
                        if (null != addDevice) {
                            addRes = 0;
                        }

                    }
                }
                MyLog.e(TAG, "addRes X = " + addRes);
                if (0 == addRes) {
                    addDevice.setOnlineStateLan(onLine);
                    addDevice.setIp(ip);
                    addDevice.setPort(port);
                    deviceList.add(0, addDevice);
                    CacheUtil.saveDevList(deviceList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return addRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            dismissDialog();
            if (0 == result) {
                showTextToast(R.string.add_device_succ);
                JVNewAddDeviceActivity.this.finish();

            } else {
                showTextToast(R.string.add_device_failed);
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
            createDialog("", true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度,此方法在主线程执行，用于显示任务执行的进度。
        }
    }

    @Override
    public void onBackPressed() {
        CacheUtil.saveDevList(deviceList);
        this.finish();
        super.onBackPressed();
    }

}
