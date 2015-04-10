
package com.jovision.activities;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.MyLog;

@SuppressLint("SetJavaScriptEnabled")
public class JVNewAddDeviceActivity extends BaseActivity {
    EditText new_adddevice_et;
    ImageButton editimg_clearn;
    private boolean isHasFocus;
    private Drawable imgEnable;
    WebView add_device_wv;
    String url = "http://test.cloudsee.net/mobile/";
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
        new_adddevice_et.addTextChangedListener(new TextWatcherImpl());
        new_adddevice_et.setOnFocusChangeListener(new FocusChangeListenerImpl());
        // webview显示设备内容
        add_device_wv = (WebView) findViewById(R.id.add_device_wv);
        add_device_wv.loadUrl(url);
        add_device_wv.getSettings().setJavaScriptEnabled(true);
        add_device_wv.requestFocus(View.FOCUS_DOWN);
        add_device_wv.setWebViewClient(myWebviewClient);

    }

    WebViewClient myWebviewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO 自动生成的方法存根
            // 会一直执行
            if (url != null && url.contains("demo")) {
                isLoadUrl = true;
                MyLog.e("new_url新的3333", url);
            } else {
                MyLog.e("new_url新的44444", url);
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            // TODO 自动生成的方法存根
            MyLog.e("new_url新的2222", url);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isLoadUrl) {
                isLoadUrl = true;
                view.loadUrl(url);
                MyLog.e("new_url新的1111", url);
            }
            // return true;
            return super.shouldOverrideUrlLoading(view, url);

        }

    };

    final class InJavaScriptLocalObj {

        public void showSource(String html) {

            System.out.println("====>html=" + html);

        }

    }

    OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_left:
                    JVNewAddDeviceActivity.this.finish();
                    break;
            }
        }
    };

    private class FocusChangeListenerImpl implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (new_adddevice_et.getText().toString().length() >= 1) {
                editimg_clearn.setVisibility(View.VISIBLE);
            } else {
                editimg_clearn.setVisibility(View.GONE);
            }
        }

    }

    // 当输入结束后判断是否显示右边clean的图标
    private class TextWatcherImpl implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            if (new_adddevice_et.getText().toString().length() >= 1) {
                editimg_clearn.setVisibility(View.VISIBLE);
            } else {
                editimg_clearn.setVisibility(View.GONE);
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
