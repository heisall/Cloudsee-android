
package com.jovision.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class JVNewAddDeviceActivity extends BaseActivity {
    EditText new_adddevice_et;
    private Drawable imgEnable;
    WebView add_device_wv;
    String url = "http://test.cloudsee.net/mobile/";
    // String url="http://baidu.com";

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
        // webview显示设备内容
        add_device_wv = (WebView) findViewById(R.id.add_device_wv);
        add_device_wv.loadUrl(url);
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

    @Override
    protected void saveSettings() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void freeMe() {
        // TODO Auto-generated method stub

    }

}
