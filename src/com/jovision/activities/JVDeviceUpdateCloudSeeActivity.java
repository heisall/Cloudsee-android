
package com.jovision.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.bean.Device;
import com.jovision.bean.OneKeyUpdate;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.PlayUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JVDeviceUpdateCloudSeeActivity extends BaseActivity {

    private static final String TAG = "JVDeviceUpdateCloudSeeActivity";

    private ArrayList<Device> deviceList = new ArrayList<Device>();
    private int devIndex;

    /** 一键升级功能 */
    private OneKeyUpdate updateObj;
    private ProgressDialog updateDialog;

    /** topBar **/

    private TextView devModel;
    private TextView devVersion;
    private Button updateBtn;

    boolean showConnectRes = true;// 显示连接结果

    private boolean downloadSuccess = false;// 下载完成标志
    private boolean writeSuccess = false;// 烧写完成标志

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case Consts.CALL_CONNECT_CHANGE: { // 连接回调
                MyLog.i(TAG, "CONNECT_CHANGE: " + what + ", " + arg1 + ", " + arg2
                        + ", " + obj);

                if (writeSuccess) {
                    return;
                }
                if (Consts.BAD_NOT_CONNECT == arg2) {
                    dismissDialog();
                } else if (JVNetConst.CONNECT_OK != arg2
                        && JVNetConst.DISCONNECT_OK != arg2) {// IPC连接失败才提示(非连接成功和断开连接)
                    dismissDialog();
                    try {
                        JSONObject connectObj = new JSONObject(obj.toString());
                        String errorMsg = connectObj.getString("msg");
                        if ("password is wrong!".equalsIgnoreCase(errorMsg)
                                || "pass word is wrong!".equalsIgnoreCase(errorMsg)) {// 密码错误时提示身份验证失败
                            showTextToast(R.string.connfailed_auth);
                        } else if ("channel is not open!"
                                .equalsIgnoreCase(errorMsg)) {// 无该通道服务
                            showTextToast(R.string.connfailed_channel_notopen);
                        } else if ("connect type invalid!"
                                .equalsIgnoreCase(errorMsg)) {// 连接类型无效
                            showTextToast(R.string.connfailed_type_invalid);
                        } else if ("client count limit!".equalsIgnoreCase(errorMsg)) {// 超过主控最大连接限制
                            showTextToast(R.string.connfailed_maxcount);
                        } else if ("connect timeout!".equalsIgnoreCase(errorMsg)) {//
                            showTextToast(R.string.connfailed_timeout);
                        } else {
                            showTextToast(R.string.connect_failed);
                        }

                        if (null != updateDialog && updateDialog.isShowing()) {
                            updateDialog.dismiss();
                            updateDialog = null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case Consts.CALL_NORMAL_DATA: {
                int devType = 0;
                try {
                    JSONObject jobj;
                    jobj = new JSONObject(obj.toString());
                    if (null != jobj) {
                        devType = jobj.optInt("device_type");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Consts.DEVICE_TYPE_IPC == devType) {// 是IPC
                    // // 暂停视频
                    // Jni.sendBytes(Consts.CHANNEL_JY,
                    // JVNetConst.JVN_CMD_VIDEOPAUSE,
                    // new byte[0], 8);
                    // 请求文本聊天
                    Jni.sendBytes(1, JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
                } else { // 不是IPC
                    showConnectRes = false;
                    PlayUtil.disconnectDevice();
                    dismissDialog();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

                    builder1.setTitle(R.string.tips);
                    builder1.setMessage(R.string.not_support_this_func);
                    builder1.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder1.create().show();
                }

                break;
            }

            case Consts.CALL_TEXT_DATA: {// 文本回调
                MyLog.i(TAG, "TEXT_DATA: " + what + ", " + arg1 + ", " + arg2
                        + ", " + obj);
                switch (arg2) {
                    case JVNetConst.JVN_RSP_TEXTACCEPT:// 同意文本聊天
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        MyLog.v(TAG, "同意文本聊天，发送取消升级命令--0");
                        // TODO 0.发送取消升级命令
                        Jni.sendSuperBytes(1, JVNetConst.JVN_RSP_TEXTDATA, true,
                                Consts.RC_EX_FIRMUP, Consts.EX_UPLOAD_CANCEL,
                                Consts.FIRMUP_HTTP, 0, 0, new byte[0], 0);
                        break;
                    case JVNetConst.JVN_CMD_TEXTSTOP:// 不同意文本聊天
                        showConnectRes = false;
                        PlayUtil.disconnectDevice();
                        showTextToast(R.string.str_only_administator_use_this_function);
                        break;

                    case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
                        String allStr = obj.toString();
                        try {
                            JSONObject dataObj = new JSONObject(allStr);
                            switch (arg1) {
                            // case 0: {// 1.发送升级命令回调
                            // int extend_type = dataObj.getInt("extend_type");
                            // switch (extend_type) {
                            // case Consts.EX_UPLOAD_DATA: {// 4-发送升级命令回调
                            // // 1.CALL_TEXT_DATA: 165, 0, 81,
                            // //
                            // {"extend_arg1":0,"extend_arg2":0,"extend_arg3":0,"extend_type":4,
                            // //
                            // "flag":0,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6}
                            // // TODO 2.创建计时器每隔一段时间获取下载进度：
                            //
                            // break;
                            // }
                            // }
                            // break;
                            // }

                                case 1: {// 2.创建计时器每隔一段时间获取下载进度回调
                                    // 2.TEXT_DATA: 165, 1, 81,
                                    // {"extend_arg1":0,"extend_arg2":100,"extend_arg3":0,"extend_type":4,
                                    // "flag":0,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6}
                                    int extend_type = dataObj.getInt("extend_type");
                                    switch (extend_type) {

                                        case Consts.EX_UPLOAD_CANCEL: {// 2-取消发送升级命令
                                            // TEXT_DATA: 165, 1, 81,
                                            // {"extend_arg1":0,"extend_arg2":0,"extend_arg3":0,"extend_type":2,
                                            // "flag":0,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6}
                                            MyLog.v(TAG, "同意文本聊天，发送升级命令--1");
                                            // TODO 1.发送升级命令
                                            Jni.sendSuperBytes(1, JVNetConst.JVN_RSP_TEXTDATA,
                                                    true, Consts.RC_EX_FIRMUP,
                                                    Consts.EX_UPLOAD_START, Consts.FIRMUP_HTTP,
                                                    0, 0, null, 0);
                                            dismissDialog();
                                            createDownloadProDialog(updateObj.getUfsize());
                                            break;
                                        }
                                        case Consts.EX_UPLOAD_DATA: {
                                            int pro = dataObj.getInt("extend_arg2");
                                            MyLog.v(TAG, "下载进度--" + pro + "%");
                                            if (downloadSuccess) {
                                                break;
                                            }
                                            // TODO 2.创建计时器每隔一段时间获取下载进度：
                                            if (100 > pro) {
                                                downloadSuccess = false;
                                                Thread.sleep(1000);
                                                Jni.sendSuperBytes(1,
                                                        JVNetConst.JVN_RSP_TEXTDATA, true,
                                                        Consts.RC_EX_FIRMUP,
                                                        Consts.EX_UPLOAD_DATA,
                                                        Consts.FIRMUP_HTTP, 0, 0, new byte[0],
                                                        0);
                                                handler.sendMessage(handler.obtainMessage(
                                                        Consts.WHAT_DOWNLOADING_KEY_UPDATE,
                                                        pro, 0));
                                            } else if (100 <= pro) {
                                                downloadSuccess = true;
                                                handler.sendMessage(handler
                                                        .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_SUCCESS));
                                                MyLog.v(TAG, "下完成，发送upload_ok命令--3");
                                                // TODO
                                                // 3.处理升级进度命令，进度为100时，表示下载完毕，并发送EX_UPLOAD_OK命令：
                                                Jni.sendSuperBytes(1,
                                                        JVNetConst.JVN_RSP_TEXTDATA, true,
                                                        Consts.RC_EX_FIRMUP,
                                                        Consts.EX_UPLOAD_OK,
                                                        Consts.FIRMUP_HTTP, 0, 0, new byte[0],
                                                        0);
                                            }
                                            break;
                                        }
                                        case Consts.EX_UPLOAD_OK: {// _UPLOAD_OK命令回调
                                            // 3.CALL_TEXT_DATA: 165, 0, 81,
                                            // {"extend_arg1":0,"extend_arg2":0,"extend_arg3":0,"extend_type":3,
                                            // "flag":2,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6,"wifi":null}
                                            // TODO 4.
                                            // 收到EX_UPLOAD_OK命令反馈，发送烧写命令：
                                            MyLog.v(TAG, "收到EX_UPLOAD_OK命令反馈，发送烧写命令--4");
                                            Jni.sendSuperBytes(1, JVNetConst.JVN_RSP_TEXTDATA,
                                                    true, Consts.RC_EX_FIRMUP,
                                                    Consts.EX_FIRMUP_START, Consts.FIRMUP_HTTP,
                                                    0, 0, new byte[0], 0);
                                            break;
                                        }
                                        case Consts.EX_FIRMUP_START: {// 烧写命令回调
                                            // CALL_TEXT_DATA: 165, 0, 81,
                                            // {"extend_arg1":0,"extend_arg2":100,"extend_arg3":0,"extend_type":5,
                                            // "flag":0,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6}
                                            // TODO 5.
                                            // 收到EX_FIRMUP_START命令反馈，发送获取烧写进度命令，创建计时器，一直发送获取烧写进度命令：
                                            MyLog.v(TAG, "获取烧写进度命令--5");
                                            createWriteDownloadProDialog();
                                            writeSuccess = false;
                                            Jni.sendSuperBytes(1, JVNetConst.JVN_RSP_TEXTDATA,
                                                    true, Consts.RC_EX_FIRMUP,
                                                    Consts.EX_FIRMUP_STEP, Consts.FIRMUP_HTTP,
                                                    0, 0, new byte[0], 0);
                                            break;
                                        }
                                        case Consts.EX_FIRMUP_STEP: {// 烧写进度回调
                                            // CALL_TEXT_DATA: 165, 0, 81,
                                            // {"extend_arg1":100,"extend_arg2":0,"extend_arg3":0,"extend_type":7,
                                            // "flag":0,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6}

                                            int pro = dataObj.getInt("extend_arg1");
                                            MyLog.v(TAG, "烧写进度--" + pro + "%");
                                            if (writeSuccess) {
                                                break;
                                            }
                                            if (pro < 100) {
                                                Thread.sleep(1000);
                                                writeSuccess = false;
                                                Jni.sendSuperBytes(1,
                                                        JVNetConst.JVN_RSP_TEXTDATA, true,
                                                        Consts.RC_EX_FIRMUP,
                                                        Consts.EX_FIRMUP_STEP,
                                                        Consts.FIRMUP_HTTP, 0, 0, new byte[0],
                                                        0);
                                                handler.sendMessage(handler.obtainMessage(
                                                        Consts.WHAT_DOWNLOADING_KEY_UPDATE,
                                                        pro, 0));
                                            } else {
                                                MyLog.v(TAG, "烧写完成");
                                                handler.sendMessage(handler.obtainMessage(
                                                        Consts.WHAT_DOWNLOADING_KEY_UPDATE,
                                                        pro, 0));
                                                writeSuccess = true;
                                                handler.sendMessage(handler
                                                        .obtainMessage(Consts.WHAT_WRITE_KEY_UPDATE_SUCCESS));
                                            }

                                            break;
                                        }
                                        case Consts.EX_FIRMUP_OK: {// 烧写完成回调
                                            // TEXT_DATA: 165, 1, 81,
                                            // {"extend_arg1":100,"extend_arg2":0,"extend_arg3":0,"extend_type":7,
                                            // "flag":0,"packet_count":1,"packet_id":0,"packet_length":0,"packet_type":6}

                                            int pro = dataObj.getInt("extend_arg1");
                                            MyLog.v(TAG, "烧写进度--" + pro + "%");
                                            if (writeSuccess) {
                                                break;
                                            }
                                            writeSuccess = true;
                                            // if (pro < 100) {
                                            // Thread.sleep(1000);
                                            // writeSuccess = false;
                                            // Jni.sendSuperBytes(1,
                                            // JVNetConst.JVN_RSP_TEXTDATA,
                                            // true,
                                            // Consts.RC_EX_FIRMUP,
                                            // Consts.EX_FIRMUP_STEP,
                                            // Consts.FIRMUP_HTTP, 0, 0, new
                                            // byte[0],
                                            // 0);
                                            // handler.sendMessage(handler.obtainMessage(
                                            // Consts.WHAT_DOWNLOADING_KEY_UPDATE,
                                            // pro, 0));
                                            // } else {
                                            MyLog.v(TAG, "烧写完成");
                                            handler.sendMessage(handler.obtainMessage(
                                                    Consts.WHAT_DOWNLOADING_KEY_UPDATE, pro, 0));
                                            writeSuccess = true;
                                            handler.sendMessage(handler
                                                    .obtainMessage(Consts.WHAT_WRITE_KEY_UPDATE_SUCCESS));
                                            // }
                                            break;
                                        }
                                        case Consts.EX_FIRMUP_RET: {
                                            int pro = dataObj.getInt("extend_arg1");
                                            showConnectRes = false;
                                            PlayUtil.disconnectDevice();
                                            if (null != updateDialog
                                                    && updateDialog.isShowing()) {
                                                updateDialog.dismiss();
                                                updateDialog = null;
                                            }
                                            JVDeviceUpdateCloudSeeActivity.this
                                                    .showTextToast(getResources().getString(
                                                            R.string.writing_update_error)
                                                            + pro);
                                            MyLog.v(TAG, "烧写出错，错误值--" + pro);
                                            break;
                                        }

                                    }

                                    break;
                                }
                                default:
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                }
                break;
            }

            case Consts.WHAT_DOWNLOAD_KEY_UPDATE_SUCCESS:
                if (null != updateDialog && updateDialog.isShowing()) {
                    updateDialog.dismiss();
                    updateDialog = null;
                }
                // updateDialog = null;
                // if (updateDialog == null) {
                // updateDialog = new ProgressDialog(
                // JVDeviceUpdateCloudSeeActivity.this);
                // updateDialog.setCancelable(false);
                // updateDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // updateDialog.setTitle(getResources().getString(
                // R.string.writing_update));
                // updateDialog.setIndeterminate(false);
                // updateDialog.setMax(100);
                // }
                // updateDialog.show();
                // new Thread() {
                // public void run() {
                // try {
                // boolean flag = true;
                // int time = 0;
                // while (flag) {
                // int pro = DeviceUtil
                // .getUpdateProgress(
                // JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                // .get(Consts.KEY_USERNAME),
                // deviceList.get(devIndex)
                // .getFullNo());
                // MyLog.v("DownPro", pro + "");
                // if (100 <= pro) {
                // flag = false;
                // handler.sendMessage(handler
                // .obtainMessage(Consts.WHAT_WRITE_KEY_UPDATE_SUCCESS));
                // } else if (-1 == pro) {
                // time++;
                // } else {
                // time = 0;
                // handler.sendMessage(handler.obtainMessage(
                // Consts.WHAT_DOWNLOADING_KEY_UPDATE,
                // pro, 0));
                // Thread.sleep(1000);
                // }
                // if (time >= 5) {
                // flag = false;
                // handler.sendMessage(handler
                // .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
                // }
                // }
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // handler.sendMessage(handler
                // .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
                // }
                // }
                // }.start();
                break;
            case Consts.WHAT_DOWNLOADING_KEY_UPDATE:
                if (null != updateDialog && updateDialog.isShowing()) {
                    MyLog.e("sss", arg1 + " arg1");
                    updateDialog.setProgress(arg1);
                }
                break;
            case Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR:
                if (null != updateDialog && updateDialog.isShowing()) {
                    updateDialog.dismiss();
                }
                JVDeviceUpdateCloudSeeActivity.this
                        .showTextToast(R.string.check_key_update_error);
                break;

            case Consts.WHAT_RESTART_DEVICE_SUCCESS:
                deviceList.get(devIndex).setDeviceVerName(updateObj.getUfver());
                devVersion.setText(updateObj.getUfver());
                // version.setText(deviceList.get(devIndex).deviceVersion);
                JVDeviceUpdateCloudSeeActivity.this
                        .showTextToast(R.string.update_reset_success);
                break;
            case Consts.WHAT_DOWNLOAD_KEY_UPDATE_CANCEL:
                if (null != updateDialog && updateDialog.isShowing()) {
                    updateDialog.dismiss();
                }
                break;
            // case Consts.WHAT_RESTART_DEVICE_FAILED:
            // JVDeviceUpdateCloudSeeActivity.this
            // .showTextToast(R.string.update_reset_failed);
            // break;
            case Consts.WHAT_WRITE_KEY_UPDATE_SUCCESS:
                if (null != updateDialog && updateDialog.isShowing()) {
                    updateDialog.dismiss();
                    updateDialog = null;
                }
                AlertDialog ads = new AlertDialog.Builder(
                        JVDeviceUpdateCloudSeeActivity.this)
                        .setTitle(R.string.key_update_reset)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.sure),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        showConnectRes = false;
                                        Jni.sendSuperBytes(1,
                                                JVNetConst.JVN_RSP_TEXTDATA, true,
                                                Consts.RC_EX_FIRMUP,
                                                Consts.EX_FIRMUP_REBOOT,
                                                Consts.FIRMUP_HTTP, 0, 0,
                                                new byte[0], 0);
                                        // new Thread() {
                                        // public void run() {
                                        // DeviceUtil
                                        // .cancelUpdate(
                                        // JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                        // .get(Consts.KEY_USERNAME),
                                        // deviceList
                                        // .get(devIndex)
                                        // .getFullNo());
                                        // if (0 == DeviceUtil
                                        // .pushRestart(
                                        // JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                        // .get(Consts.KEY_USERNAME),
                                        // deviceList
                                        // .get(devIndex)
                                        // .getFullNo())) {
                                        // handler.sendMessage(handler
                                        // .obtainMessage(Consts.WHAT_RESTART_DEVICE_SUCCESS));
                                        // } else {
                                        // handler.sendMessage(handler
                                        // .obtainMessage(Consts.WHAT_RESTART_DEVICE_FAILED));
                                        // }
                                        // };
                                        // }.start();
                                        handler.sendMessage(handler
                                                .obtainMessage(Consts.WHAT_RESTART_DEVICE_SUCCESS));
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(
                                getResources().getString(R.string.str_crash_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // new Thread() {
                                        // public void run() {
                                        // DeviceUtil
                                        // .cancelUpdate(
                                        // JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                        // .get(Consts.KEY_USERNAME),
                                        // deviceList
                                        // .get(devIndex)
                                        // .getFullNo());
                                        // };
                                        // }.start();
                                        dialog.dismiss();
                                    }
                                }).create();
                ads.show();
                break;
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    protected void initSettings() {
        deviceList = CacheUtil.getDevList();
        devIndex = getIntent().getIntExtra("deviceIndex", 0);
    }

    @Override
    protected void initUi() {
        setContentView(R.layout.deviceupdate_layout);
        /** topBar **/
        leftBtn = (Button) findViewById(R.id.btn_left);
        alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
        accountError = (TextView) findViewById(R.id.accounterror);
        currentMenu = (TextView) findViewById(R.id.currentmenu);
        currentMenu.setText(R.string.device_version_info);
        leftBtn.setOnClickListener(myOnClickListener);
        rightBtn = (Button) findViewById(R.id.btn_right);
        rightBtn.setVisibility(View.GONE);

        devModel = (TextView) findViewById(R.id.devmodel);
        devVersion = (TextView) findViewById(R.id.devversion);
        updateBtn = (Button) findViewById(R.id.update);
        updateBtn.setOnClickListener(myOnClickListener);

        if ("unknown".equalsIgnoreCase(deviceList.get(devIndex)
                .getDeviceModel())) {
            devModel.setText(R.string.unknown);
        } else {
            devModel.setText(deviceList.get(devIndex).getDeviceModel());
        }
        devVersion.setText(deviceList.get(devIndex).getDeviceVerName());
    }

    OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_left: {
                    backMethod();
                    break;
                }
                case R.id.update: {// 一键升级
                    createDialog("", true);
                    CheckUpdateTask task = new CheckUpdateTask();
                    String[] params = new String[3];
                    task.execute(params);
                    break;
                }
            }

        }

    };

    /**
     * 返回事件
     */
    private void backMethod() {
        showConnectRes = false;
        PlayUtil.disconnectDevice();
        if (null != updateDialog && updateDialog.isShowing()) {
            updateDialog.dismiss();
            updateDialog = null;
        }
        dismissDialog();
        JVDeviceUpdateCloudSeeActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        backMethod();
    }

    // 设置三种类型参数分别为String,Integer,String
    class CheckUpdateTask extends AsyncTask<String, Integer, Integer> {
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            int updateRes = -1;// 0成功， 14失败，其他出错
            try {
                updateObj = DeviceUtil.checkUpdate(
                        JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                .get(Consts.KEY_USERNAME),
                        deviceList.get(devIndex).getDeviceType(), deviceList
                                .get(devIndex).getDeviceVerNum(), deviceList
                                .get(devIndex).getDeviceVerName());
                updateRes = updateObj.getResultCode();
                // updateRes = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return updateRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            JVDeviceUpdateCloudSeeActivity.this.dismissDialog();
            if (0 == result) {
                AlertDialog ad = new AlertDialog.Builder(
                        JVDeviceUpdateCloudSeeActivity.this)
                        .setTitle(R.string.key_update_title)
                        .setMessage(
                                getResources().getString(
                                        R.string.device_new_version)
                                        + updateObj.getUfver())
                        .setCancelable(false)
                        .setPositiveButton(
                                getResources().getString(R.string.update),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        dismissDialog();
                                        createDialog("", false);
                                        showConnectRes = true;
                                        PlayUtil.connectDevice(deviceList
                                                .get(devIndex));

                                        // PushUpdateTask task = new
                                        // PushUpdateTask();
                                        // String[] params = new String[3];
                                        // task.execute(params);
                                        // JVDeviceUpdateCloudSeeActivity.this
                                        // .createDialog("", true);
                                        // dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(
                                getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                ad.show();
            } else if (14 == result) {
                JVDeviceUpdateCloudSeeActivity.this
                        .showTextToast(R.string.check_key_update_failed);
            } else {
                JVDeviceUpdateCloudSeeActivity.this
                        .showTextToast(R.string.check_key_update_error);
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
     * 创建下载进度dialog
     */
    @SuppressWarnings("deprecation")
    private void createDownloadProDialog(int max) {
        updateDialog = null;
        if (updateDialog == null) {
            updateDialog = new ProgressDialog(
                    JVDeviceUpdateCloudSeeActivity.this);
            updateDialog.setCancelable(false);
            updateDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            updateDialog.setTitle(getResources().getString(
                    R.string.downloading_update));
            updateDialog.setIndeterminate(false);
            updateDialog.setMax(max);
            updateDialog.setProgressNumberFormat("%1d B/%2d B");
            updateDialog.setButton(
                    getResources().getString(R.string.str_crash_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 取消下载
                            Jni.sendSuperBytes(1, JVNetConst.JVN_RSP_TEXTDATA,
                                    true, Consts.RC_EX_FIRMUP,
                                    Consts.EX_UPLOAD_CANCEL,
                                    Consts.FIRMUP_HTTP, 0, 0, new byte[0], 0);
                            showConnectRes = false;
                            PlayUtil.disconnectDevice();
                            dialog.dismiss();
                        }
                    });
        }
        updateDialog.show();
    }

    /**
     * 创建烧写进度dialog
     */
    @SuppressWarnings("deprecation")
    private void createWriteDownloadProDialog() {
        if (null != updateDialog && updateDialog.isShowing()) {
            updateDialog.dismiss();
            updateDialog = null;
        }
        updateDialog = null;
        if (updateDialog == null) {
            updateDialog = new ProgressDialog(
                    JVDeviceUpdateCloudSeeActivity.this);
            updateDialog.setCancelable(false);
            updateDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            updateDialog.setTitle(getResources().getString(
                    R.string.writing_update));
            updateDialog.setIndeterminate(false);
            updateDialog.setMax(100);
        }
        updateDialog.show();
    }

    // 设置三种类型参数分别为String,Integer,String
    class PushUpdateTask extends AsyncTask<String, Integer, Integer> {
        // 可变长的输入参数，与AsyncTask.exucute()对应
        @Override
        protected Integer doInBackground(String... params) {
            int pushRes = -1;// 0成功 1失败
            try {
                DeviceUtil.cancelUpdate(
                        JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                .get(Consts.KEY_USERNAME),
                        deviceList.get(devIndex).getFullNo());
                pushRes = DeviceUtil.pushUpdateCommand(
                        JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                .get(Consts.KEY_USERNAME),
                        deviceList.get(devIndex).getFullNo(), updateObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pushRes;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Integer result) {
            // 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
            JVDeviceUpdateCloudSeeActivity.this.dismissDialog();
            if (0 == result) {
                updateDialog = null;
                if (updateDialog == null) {
                    updateDialog = new ProgressDialog(
                            JVDeviceUpdateCloudSeeActivity.this);
                    updateDialog.setCancelable(false);
                    updateDialog
                            .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    updateDialog.setTitle(getResources().getString(
                            R.string.downloading_update));
                    updateDialog.setIndeterminate(false);
                    updateDialog.setMax(100);
                    updateDialog
                            .setButton(
                                    getResources().getString(
                                            R.string.str_crash_cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    DeviceUtil
                                                            .cancelUpdate(
                                                                    JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                                                            .get(Consts.KEY_USERNAME),
                                                                    deviceList
                                                                            .get(devIndex)
                                                                            .getFullNo());
                                                }
                                            }.start();
                                            dialog.dismiss();
                                        }
                                    });
                }
                updateDialog.show();
                new Thread() {
                    public void run() {
                        try {
                            boolean flag = true;
                            int time = 0;
                            int pro2 = 0;
                            while (flag) {
                                int pro = DeviceUtil
                                        .getDownloadProgress(
                                                JVDeviceUpdateCloudSeeActivity.this.statusHashMap
                                                        .get(Consts.KEY_USERNAME),
                                                deviceList.get(devIndex)
                                                        .getFullNo());
                                MyLog.v("sss", pro + " pro");
                                if (100 <= pro) {
                                    flag = false;
                                    handler.sendMessage(handler
                                            .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_SUCCESS));
                                } else if (-1 == pro && 0 == pro && pro2 == pro) {
                                    time++;
                                    Thread.sleep(1000);
                                } else if (pro2 > pro) {
                                    flag = false;
                                    handler.sendMessage(handler
                                            .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_CANCEL));
                                } else {
                                    time = 0;
                                    handler.sendMessage(handler.obtainMessage(
                                            Consts.WHAT_DOWNLOADING_KEY_UPDATE,
                                            pro, 0));
                                    Thread.sleep(1000);
                                }
                                if (time >= 5) {
                                    flag = false;
                                    handler.sendMessage(handler
                                            .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
                                }
                                pro2 = pro;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            handler.sendMessage(handler
                                    .obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
                        }
                    }
                }.start();
            } else {
                JVDeviceUpdateCloudSeeActivity.this
                        .showTextToast(R.string.check_key_update_error);
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
            // createDialog("", true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度,此方法在主线程执行，用于显示任务执行的进度。
        }
    }

    @Override
    protected void saveSettings() {

    }

    @Override
    protected void freeMe() {

    }

    @Override
    protected void onPause() {
        CacheUtil.saveDevList(deviceList);
        super.onPause();
    }

}
