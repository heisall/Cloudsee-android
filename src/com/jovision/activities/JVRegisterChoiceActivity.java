
package com.jovision.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.jovetech.CloudSee.temp.R;

public class JVRegisterChoiceActivity extends Activity implements
        OnClickListener {
    private RelativeLayout rlyMailWay, rlyPhoneWay, rlyCance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.register_choice);

        rlyMailWay = (RelativeLayout) findViewById(R.id.rly_mail_verify);
        rlyPhoneWay = (RelativeLayout) findViewById(R.id.rly_phone_verify);
        rlyCance = (RelativeLayout) findViewById(R.id.rly_cancel);

        rlyMailWay.setOnClickListener(this);
        rlyPhoneWay.setOnClickListener(this);
        rlyCance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.rly_phone_verify:// 手机
                Intent registIntent = new Intent();
                registIntent.setClass(JVRegisterChoiceActivity.this,
                        JVRegisterActivity.class);
                startActivity(registIntent);
                finish();
                break;

            case R.id.rly_mail_verify:// 邮箱
                Intent registmailIntent = new Intent();
                registmailIntent.setClass(JVRegisterChoiceActivity.this,
                        JVRegisterByEmailActivity.class);
                startActivity(registmailIntent);
                finish();
                break;
            case R.id.rly_cancel:// 取消
                finish();
                return;
            default:
                break;
        }
        finish();
    }
}
