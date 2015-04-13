
package com.jovision.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.commons.GetDemoTask;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;
import com.tencent.stat.StatService;
import com.umeng.common.Log;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class JVNewAddDeviceActivity extends BaseActivity {
    EditText new_adddevice_et;
    ImageButton editimg_clearn;
    ImageView tab_icon, save_icon;
    TextView tab_title;
    WebView add_device_wv;
    // String url = "http://test.cloudsee.net/mobile/";
    String url = "http://www.cloudsee.net/mobile/nineBlock.action";
    // String url ="https://www.baidu.com/";
    Boolean isLoadUrlfail = false;
    Boolean isLoadUrlFinish = false;
    private RelativeLayout loadFailedLayout;
    private LinearLayout loadinglayout;
    private ImageView reloadImgView, loadingBar;

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
        // TODO Auto-generated method stub
        setContentView(R.layout.new_adddevice_layout);
        leftBtn = (Button) findViewById(R.id.btn_left);
        alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
        accountError = (TextView) findViewById(R.id.accounterror);
        rightBtn = (Button) findViewById(R.id.btn_right);
        rightBtn.setVisibility(View.GONE);
        currentMenu = (TextView) findViewById(R.id.currentmenu);

        currentMenu.setText(R.string.str_new_add_device);
        leftBtn.setOnClickListener(myOnClickListener);
        new_adddevice_et = (EditText) findViewById(R.id.new_adddevice_et);
        editimg_clearn = (ImageButton) findViewById(R.id.editimg_clearn);
        editimg_clearn.setOnClickListener(myOnClickListener);
        tab_title = (TextView) findViewById(R.id.tab_title);
        tab_icon = (ImageView) findViewById(R.id.tab_icon);
        tab_icon.setOnClickListener(myOnClickListener);
        // tab_title.setOnClickListener(myOnClickListener);
        save_icon = (ImageView) findViewById(R.id.save_icon);
        save_icon.setOnClickListener(myOnClickListener);
        new_adddevice_et.addTextChangedListener(new TextWatcherImpl());
        new_adddevice_et.setOnFocusChangeListener(new FocusChangeListenerImpl());
        // webview显示设备内容
        add_device_wv = (WebView) findViewById(R.id.add_device_wv);
        add_device_wv.requestFocus(View.FOCUS_DOWN);
        add_device_wv.setWebViewClient(myWebviewClient);
        loadingBar = (ImageView) findViewById(R.id.loadingbars);
        loadinglayout = (LinearLayout) findViewById(R.id.loadinglayout);
        loadinglayout.setVisibility(View.VISIBLE);
        add_device_wv.loadUrl(url);
        // 网络加载不成功状态
        loadFailedLayout = (RelativeLayout)
                findViewById(R.id.loadfailedlayout);
        loadFailedLayout.setVisibility(View.GONE);
        reloadImgView = (ImageView) findViewById(R.id.refreshimg);
        reloadImgView.setOnClickListener(myOnClickListener);

    }

    WebViewClient myWebviewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO 自动生成的方法存根
            // 会一直执行
            // MyLog.e("添加设备", "页面开始加载");
            super.onPageStarted(view, url, favicon);
            if (!MySharedPreference.getBoolean("webfirst")) {
                MySharedPreference.putBoolean("webfirst", true);
                loadinglayout.setVisibility(View.VISIBLE);
                add_device_wv.setVisibility(View.GONE);
                loadingBar.setAnimation(AnimationUtils.loadAnimation(
                        JVNewAddDeviceActivity.this, R.anim.rotate));
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            // TODO 自动生成的方法存根
            // MyLog.e("添加设备", "页面加载失败");
            isLoadUrlfail = true;
            loadFailedLayout.setVisibility(View.VISIBLE);
            add_device_wv.setVisibility(View.GONE);
            loadinglayout.setVisibility(View.GONE);
            JVNewAddDeviceActivity.this.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO 自动生成的方法存根
            // MyLog.e("添加设备", "页面开始完成");
            if (isLoadUrlfail) {// 加载失败
                // MyLog.e("添加设备", "页面开始完成失败");
                loadFailedLayout.setVisibility(View.VISIBLE);
                add_device_wv.setVisibility(View.GONE);
                loadinglayout.setVisibility(View.GONE);
            } else {
                // MyLog.e("添加设备", "页面开始完成成功");
                // add_device_wv.loadUrl(url);
                loadinglayout.setVisibility(View.GONE);
                add_device_wv.setVisibility(View.VISIBLE);
                loadFailedLayout.setVisibility(View.GONE);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String newurl) {
            MyLog.e("添加设备", "点击了!!!!!!!!!");
            view.loadUrl(newurl);
            String param_array[] = newurl.split("\\?");
            HashMap<String, String> resMap;
            resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);
            String rtmp_url = resMap.get("device_type");
            Toast.makeText(JVNewAddDeviceActivity.this, "点击了" + rtmp_url, Toast.LENGTH_SHORT)
                    .show();
            MyLog.e("添加设备", "点击了" + rtmp_url);
            return true;

        }

    };
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
                    new_adddevice_et.setText("");
                    break;
                case R.id.save_icon:
                    Toast.makeText(JVNewAddDeviceActivity.this, "保存设备成功", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.refreshimg:
                    if (ConfigUtil.isConnected(JVNewAddDeviceActivity.this)) {
                        MyLog.e("添加设备", "判断手机是否联网成功");
                        loadFailedLayout.setVisibility(View.GONE);
                        JVNewAddDeviceActivity.this.statusHashMap
                                .put(Consts.HAS_LOAD_DEMO, "false");
                        loadinglayout.setVisibility(View.VISIBLE);
                        loadingBar.setAnimation(AnimationUtils.loadAnimation(
                                JVNewAddDeviceActivity.this, R.anim.rotate));
                        isLoadUrlfail = false;
                        if (null == url || "".equalsIgnoreCase(url)) {
                            if ("false".equals(JVNewAddDeviceActivity.this.statusHashMap
                                    .get(Consts.KEY_INIT_ACCOUNT_SDK))) {
                                MyLog.e("Login", "初始化账号SDK失败");
                                ConfigUtil
                                        .initAccountSDK(((MainApplication) JVNewAddDeviceActivity.this
                                                .getApplication()));// 初始化账号SDK
                            }
                        }
                        add_device_wv.loadUrl(url);
                    } else {
                        MyLog.e("添加设备", "判断手机是否联网失败");
                        JVNewAddDeviceActivity.this.alertNetDialog();
                    }
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
            if (new_adddevice_et.getText().toString().length() >= 1) {
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
            if (new_adddevice_et.getText().toString().length() >= 1) {
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
        // TODO Auto-generated method stub

    }

    @Override
    protected void freeMe() {
        // TODO Auto-generated method stub

    }

}
