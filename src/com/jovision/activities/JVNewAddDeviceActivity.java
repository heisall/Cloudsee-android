
package com.jovision.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.ConfigUtil;
import com.tencent.stat.StatService;

import java.util.HashMap;

@SuppressLint("SetJavaScriptEnabled")
public class JVNewAddDeviceActivity extends BaseActivity {
    EditText new_adddevice_et;
    ImageButton editimg_clearn;
    ImageView tab_icon, save_icon;
    TextView tab_title;
    private boolean isHasFocus;
    private Drawable imgEnable;
    WebView add_device_wv;
    String url = "http://test.cloudsee.net/mobile/";
    // String url ="https://www.baidu.com/";
    Boolean isLoadUrl = false;
    private Handler mHandler = new Handler();

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
        imgEnable = this.getResources().getDrawable(R.drawable.new_adddevice_close);
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
        add_device_wv.getSettings().setJavaScriptEnabled(true);
        add_device_wv.requestFocus(View.FOCUS_DOWN);
        add_device_wv.setWebViewClient(myWebviewClient);
        add_device_wv.loadUrl(url);

    }

    WebViewClient myWebviewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO 自动生成的方法存根
            // 会一直执行
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            // TODO 自动生成的方法存根
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String newurl) {
            view.loadUrl(newurl);
            String param_array[] = newurl.split("\\?");
            HashMap<String, String> resMap;
            resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);
            String rtmp_url = resMap.get("device_type");
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
