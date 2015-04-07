
package com.jovision.activities;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.GetPhoneNumber;

public class JVReBoundEmailActivity extends BaseActivity {
    private Button finish;
    private EditText reBindMail;
    private EditText reBindPhone;
    private int bindPhone = -1;
    private GetPhoneNumber phone;

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
        setContentView(R.layout.rebound_email_layout);
    }

    @Override
    protected void initUi() {
        // TODO Auto-generated method stub
        leftBtn = (Button) findViewById(R.id.btn_left);
        rightBtn = (Button) findViewById(R.id.btn_right);
        alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
        accountError = (TextView) findViewById(R.id.accounterror);
        finish = (Button) findViewById(R.id.finish);
        currentMenu = (TextView) findViewById(R.id.currentmenu);
        reBindMail = (EditText) findViewById(R.id.rebindmail);
        reBindPhone = (EditText) findViewById(R.id.rebindphone);
        currentMenu.setVisibility(View.VISIBLE);
        rightBtn.setVisibility(View.GONE);
        currentMenu.setText(getResources().getString(R.string.str_bound_email));

        leftBtn.setOnClickListener(myOnClickListener);
        finish.setOnClickListener(myOnClickListener);
    }

    OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_left:
                    finish();
                    break;
                case R.id.finish:
                    if ("".equalsIgnoreCase(reBindMail.getText().toString())
                            && "".equalsIgnoreCase(reBindPhone.getText().toString())) {
                        showTextToast(R.string.login_str_loginemail_notnull);
                    } else if (!"".equalsIgnoreCase(reBindPhone.getText()
                            .toString())) {
                        phone = new GetPhoneNumber(reBindPhone.getText().toString());
                        if (phone.matchNum() == 4 || phone.matchNum() == 5) {
                            showTextToast(R.string.login_str_loginephone_tips);
                        } else if (!"".equalsIgnoreCase(reBindMail.getText()
                                .toString())
                                && !AccountUtil.verifyEmail(reBindMail.getText()
                                        .toString())) {
                            showTextToast(R.string.login_str_loginemail_tips);
                        } else {
                            createDialog("", true);
                            BindTask task = new BindTask();
                            String[] params = new String[3];
                            task.execute(params);
                        }
                    } else if (!""
                            .equalsIgnoreCase(reBindMail.getText().toString())
                            && !AccountUtil.verifyEmail(reBindMail.getText()
                                    .toString())) {
                        showTextToast(R.string.login_str_loginemail_tips);
                    } else {
                        createDialog("", true);
                        BindTask task = new BindTask();
                        String[] params = new String[3];
                        task.execute(params);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    // 设置三种类型参数分别为String,Integer,String
    class BindTask extends AsyncTask<String, Integer, Integer> {
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            int bindRes = -1;// 0成功 2邮箱已被绑定，绑定失败 其他失败
            try {
                bindRes = AccountUtil.bindMailOrPhone(reBindMail.getText()
                        .toString());
                bindPhone = AccountUtil.bindMailOrPhone(reBindPhone.getText()
                        .toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bindRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            dismissDialog();
            if (0 == result || bindPhone == 0) {
                showTextToast(R.string.str_bound_email_success);
                finish();
            } else if (2 == result || bindPhone == 2) {
                if (bindPhone == 2) {
                    showTextToast(R.string.str_bound_phone_exist);
                } else {
                    showTextToast(R.string.str_bound_email_exist);
                }
            } else {
                showTextToast(R.string.str_bound_email_failed);
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
    protected void saveSettings() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void freeMe() {
        // TODO Auto-generated method stub

    }

}
