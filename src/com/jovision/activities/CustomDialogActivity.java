
package com.jovision.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.test.JVACCOUNT;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.MainApplication;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVDeviceConst;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.PlayWindowManager;
import com.jovision.utils.AlarmUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.utils.FileUtils.onDownloadListener;
import com.jovision.utils.HttpDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

public class CustomDialogActivity extends BaseActivity implements
        android.view.View.OnClickListener, onDownloadListener {
    private static final int REPORT_LIMIT = 100 * 1024;
    private static final int TIPS_LIMIT_MB = 5;// M
    private static final String TAG = "CustomDialogActivity";
    /** 查看按钮 **/
    private Button lookVideoBtn;
    /** 报警图片 **/
    private ImageView alarmImage;
    /** 关闭按钮 **/
    private ImageView alarmClose;
    /** 报警时间 **/
    private TextView alarmTime;

    private String vod_uri_ = "";
    private String strImgUrl = "";

    private int msg_tag;

    // private String imgLoaderFilePath = "";
    private String localImgName = "", localImgPath = "";
    private String localVodName = "", localVodPath = "";
    private boolean bLocalFile = false;
    private boolean bConnectFlag = false;
    private int bDownLoadFileType = 0; // 0图片 1视频
    private String strYstNum = "";
    private int strChannelNum = 0;
    private ProgressDialog progressdialog;
    private PlayWindowManager manager;
    private int device_type = Consts.DEVICE_TYPE_IPC;
    private int audio_bit = 16;
    private MyHandler myHandler;
    private int dis_and_play_flag = 0;
    private String cloudSignVodUri, cloudSignImgUri;
    private int alarmSolution;
    private String cloudBucket, cloudResource, storageJson;
    private CustomDialogActivity mActivity;
    private int try_get_cloud_param_cnt = 1;
    private MainApplication mainApp;

    // private String url =
    // "http://jovetech.oss-cn-hangzhou.aliyuncs.com/S64983093/2015/3/27/M01165932.mp4?"
    // + "Expires=1431299984&OSSAccessKeyId=4fZazqCFmQTbbmcw&"
    // + "Signature=%2FuczSObE4Bl3b7auo6FM8AOweqc%3D";
    private String url = "http://jovetech.oss-cn-hangzhou.aliyuncs.com/S64983093/2015/3/31/M01170309.mp4?Expires=1428076891&OSSAccessKeyId=4fZazqCFmQTbbmcw&Signature=9zjoax27UQlWFkAKMkqSXbOHVUM%3D";
    /* 流量统计 */
    private long downLoadSize = 0L;
    private PushInfo pushInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alarm_dialog);
        InitViews();
        mActivity = this;
        mainApp = (MainApplication) getApplication();
        Intent intent = getIntent();
        bDownLoadFileType = 0;// 默认先下载图片
        pushInfo = (PushInfo) intent.getSerializableExtra("PUSH_INFO");
        if (pushInfo == null) {
            showTextToast(R.string.str_alarm_pushinfo_obj_null);
            return;
        }
        msg_tag = pushInfo.messageTag;
        myHandler = new MyHandler();

        strYstNum = pushInfo.ystNum;
        strChannelNum = pushInfo.coonNum;
        String strAlarmTime = new String(pushInfo.alarmTime);
        strImgUrl = new String(pushInfo.pic);
        vod_uri_ = new String(pushInfo.video);

        // strImgUrl =
        // "http://missiletcy.oss-cn-qingdao.aliyuncs.com/S224350962/2015/1/6/M01160227.jpg";;
        // vod_uri_ =
        // "http://missiletcy.oss-cn-qingdao.aliyuncs.com/S224350962/2015/1/6/M01160227.mp4";
        //
        // cloudBucket = "missiletcy";
        // String tempp[] = strImgUrl.split("com/");
        // cloudResource = String.format("/%s/%s", cloudBucket,tempp[1]);

        alarmTime.setText(strAlarmTime);
        if (vod_uri_.equals("") || vod_uri_ == null) {
            lookVideoBtn.setEnabled(false);
            lookVideoBtn.setText(getResources().getString(
                    R.string.str_alarm_no_video));
        }
        // strImgUrl = "./rec/00/20141017/A01185730.jpg";
        // vod_uri_ = "./rec/00/20141017/A01183434.mp4";

        if (!vod_uri_.equals("")) {
            String temp[] = vod_uri_.split("/");
            localVodName = temp[temp.length - 1];
            localVodPath = Consts.SD_CARD_PATH + "CSAlarmVOD/" + localVodName;

        }

        if (!strImgUrl.equals("")) {
            String temp[] = strImgUrl.split("/");
            localImgName = temp[temp.length - 1];
            localImgPath = Consts.SD_CARD_PATH + "CSAlarmIMG/" + localImgName;
            // imgLoaderFilePath = "file://"+localImgPath;
        }

        MyLog.d("New Alarm", "img_url:" + strImgUrl + ", vod_url:" + vod_uri_);
        MyLog.d("New Alarm", "localVodPath:" + localVodPath);
        MyLog.d("New Alarm", "localImgPath:" + localImgPath);

        alarmSolution = pushInfo.alarmSolution;
        /* 云存储测试变量----begin----正式发布屏蔽 */
        // alarmSolution = 1;
        // localVodName = "Cloud01Vod.mp4";
        // localVodPath = Consts.SD_CARD_PATH + "CSAlarmVOD/" + localVodName;
        // lookVideoBtn.setEnabled(true);
        // strYstNum = "S64983093";
        /* 云存储测试变量----end----正式发布屏蔽 */
        if (alarmSolution == 0)// 本地报警
        {
            if (msg_tag == JVAccountConst.MESSAGE_NEW_PUSH_TAG) {// 新报警

                if (!fileIsExists(localImgPath)) {
                    bLocalFile = false;
                    if (!strImgUrl.equals("")) {
                        Jni.setDownloadFileName(localImgPath);
                        // if (!AlarmUtil.OnlyConnect(strYstNum)) {
                        // showTextToast(R.string.str_alarm_connect_failed_1);
                        // if (!vod_uri_.equals("")) {
                        // lookVideoBtn.setEnabled(true);
                        // }
                        // } else {
                        // lookVideoBtn.setEnabled(false);
                        // }
                        lookVideoBtn.setEnabled(false);
                        new Thread(new ConnectProcess(0x9999)).start();

                    } else if (!vod_uri_.equals("")) {
                        lookVideoBtn.setEnabled(true);
                    }
                } else {
                    if (!vod_uri_.equals("")) {
                        lookVideoBtn.setEnabled(true);
                    }

                    Bitmap bmp = getLoacalBitmap(localImgPath);
                    if (null != bmp) {
                        alarmImage.setImageBitmap(bmp);
                    }
                    bLocalFile = true;
                    // showToast("文件已存在", Toast.LENGTH_SHORT);
                }
            }
        } else if (alarmSolution == 1) {// 云存储报警,图片下载，视频边下边播

            /* 初始化流量统计 */
            downLoadSize = MySharedPreference.getLong(Consts.KEY_CLOUD_VOD_SIZE, 0);
            Log.e("Down", "downLoadSize init :" + downLoadSize);
            lookVideoBtn.setEnabled(false);
            new Thread(new CloudCheckInfo()).start();
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub"
        super.onPause();

    }

    @Override
    public void onDestroy() {
        dis_and_play_flag = 0;
        // if (bConnectFlag) {
        Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
        // }
        bConnectFlag = false;
        mainApp.setAlarmConnectedFlag(false);
        super.onDestroy();
    }

    private void InitViews() {

        lookVideoBtn = (Button) findViewById(R.id.alarm_lookup_video_btn);
        lookVideoBtn.setOnClickListener(this);
        lookVideoBtn.setText(getResources().getString(
                R.string.str_alarm_check_video));
        lookVideoBtn.setEnabled(false);
        alarmImage = (ImageView) findViewById(R.id.alarm_img);
        alarmTime = (TextView) findViewById(R.id.alarm_datetime_tv);
        progressdialog = new ProgressDialog(CustomDialogActivity.this);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.setMessage(getResources().getString(
                R.string.str_downloading_vod));
        progressdialog.setIndeterminate(false);
        progressdialog.setCancelable(true);

        alarmClose = (ImageView) findViewById(R.id.dialog_cancle_img);
        alarmClose.setOnClickListener(this);
    }

    /**
     * 加载本地图片 http://bbs.3gstdy.com
     * 
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean fileIsExists(String strFilePath) {
        try {
            File f = new File(strFilePath);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.alarm_lookup_video_btn:// 查看录像

                if (alarmSolution == 0) {
                    if (null != vod_uri_ && !"".equalsIgnoreCase(vod_uri_)) {
                        progressdialog.show();
                        bDownLoadFileType = 1;
                        bConnectFlag = mainApp.getAlarmConnectedFlag();
                        if (!bConnectFlag) {
                            lookVideoBtn.setEnabled(false);
                            // if (!AlarmUtil.OnlyConnect(strYstNum)) {
                            // progressdialog.dismiss();
                            // showTextToast(R.string.str_alarm_connect_failed_1);
                            // lookVideoBtn.setEnabled(true);
                            // }
                            new Thread(new ConnectProcess(0x9999)).start();
                        } else {
                            bConnectFlag = mainApp.getAlarmConnectedFlag();
                            // 已经连接上走远程回放
                            if (bConnectFlag) {// 再判断一次
                                // dis_and_play_flag = 1;
                                // lookVideoBtn.setEnabled(false);
                                // Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
                                //
                                // new Thread(new TimeOutProcess(
                                // JVNetConst.JVN_RSP_DISCONN)).start();
                                handler.sendMessage(handler.obtainMessage(
                                        Consts.CALL_CONNECT_CHANGE, 0,
                                        JVNetConst.NO_RECONNECT, null));
                                // 不能接着就连接 ,需要等待断开连接后在连接
                                // 因此添加 dis_and_play标志，当为1时，在断开连接响应成功后，重新连接并播放
                            } else {
                                // 已经断开了
                                lookVideoBtn.setEnabled(false);
                                // if (!AlarmUtil.OnlyConnect(strYstNum)) {
                                // progressdialog.dismiss();
                                // showTextToast(R.string.str_alarm_connect_failed_1);
                                // lookVideoBtn.setEnabled(true);
                                // }
                                new Thread(new ConnectProcess(0x9999)).start();
                            }

                        }

                    }
                } else {
                    // 云存储
                    if (null != vod_uri_ && !"".equalsIgnoreCase(vod_uri_)) {
                        String temp[] = vod_uri_.split("com/");
                        if (temp.length == 2) {
                            cloudResource = String.format("/%s/%s", cloudBucket, temp[1]);
                            cloudSignVodUri = Jni.GenSignedCloudUri(cloudResource, storageJson);
                            lookVideoBtn.setEnabled(false);
                            bDownLoadFileType = 1;
                            // cloudSignVodUri = url;
                            new Thread(new HttpJudgeThread(cloudSignVodUri)).start();
                        }
                        else {
                            String strTips = getResources()
                                    .getString(R.string.str_cloud_url_error1);
                            showTextToast(strTips);
                        }
                    }

                }
                break;
            case R.id.dialog_cancle_img:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showToast(String text, int duration) {
        showTextToast(text);
    }

    /**
     * 复制单个文件
     * 
     * @param srcPath String 原文件路径 如：c:/fqf.txt
     * @param dstPath String 复制后路径 如：f:/fqf.txt
     * @return void
     */
    public void copyFile(String srcPath, String dstPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File srcfile = new File(srcPath);
            if (!srcfile.exists()) {
                MyLog.e("New Alarm", "文件不存在");
                return;
            }
            if (!srcfile.isFile()) {
                MyLog.e("New Alarm", "不是一个文件");
                return;
            }
            if (!srcfile.canRead()) {
                MyLog.e("New Alarm", "文件不能读");
                return;
            }

            InputStream inStream = new FileInputStream(srcPath); // 读入原文件
            FileOutputStream fs = new FileOutputStream(dstPath);
            int file_size = inStream.available();
            MyLog.e("New Alarm", "file size:" + file_size);
            byte[] buffer = new byte[file_size];

            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; // 字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        // TODO Auto-generated method stub
        MyLog.v(TAG, "onHandler--what=" + what + ";arg1=" + arg1 + ";arg2="
                + arg1);
        switch (what) {
        // 连接结果
            case Consts.CALL_CONNECT_CHANGE: {
                switch (arg2) {

                    case JVNetConst.NO_RECONNECT:// 1 -- 连接成功//3 不必重新连接
                    case JVNetConst.CONNECT_OK: {// 1 -- 连接成功
                        MyLog.e("New alarm", "连接成功");
                        myHandler.removeMessages(JVNetConst.JVN_RSP_DISCONN);
                        bConnectFlag = true;
                        mainApp.setAlarmConnectedFlag(true);
                        // showToast("连接成功", Toast.LENGTH_SHORT);
                        String strFilePath = "";
                        if (bDownLoadFileType == 0) {
                            strFilePath = strImgUrl;
                            MyLog.e("Alarm", "DownFile Path:" + strFilePath);
                            Jni.sendBytes(Consts.ONLY_CONNECT_INDEX,
                                    (byte) JVNetConst.JVN_CMD_DOWNLOADSTOP,
                                    new byte[0], 0);
                            byte[] dataByte = strFilePath.getBytes();
                            Jni.sendBytes(Consts.ONLY_CONNECT_INDEX,
                                    (byte) JVNetConst.JVN_REQ_DOWNLOAD, dataByte,
                                    dataByte.length);
                        } else if (bDownLoadFileType == 1) {
                            // if (progressdialog.isShowing()) {
                            // progressdialog.dismiss();
                            // }
                            // lookVideoBtn.setEnabled(true);
                            myHandler.sendEmptyMessageDelayed(0x9990, 1000);
                            strFilePath = vod_uri_;
                            // 走远程回放
                            Intent intent = new Intent();
                            intent.setClass(CustomDialogActivity.this,
                                    JVRemotePlayBackActivity.class);
                            intent.putExtra("IndexOfChannel", Consts.ONLY_CONNECT_INDEX);
                            intent.putExtra("acBuffStr", vod_uri_);
                            intent.putExtra("AudioBit", audio_bit);
                            intent.putExtra("DeviceType", device_type);
                            intent.putExtra("bFromAlarm", true);
                            intent.putExtra("is05", true);
                            this.startActivity(intent);

                        } else {

                        }
                    }
                        break;
                    // 2 -- 断开连接成功
                    case JVNetConst.DISCONNECT_OK: {
                        myHandler.removeMessages(JVNetConst.JVN_RSP_DISCONN);
                        bConnectFlag = false;
                        mainApp.setAlarmConnectedFlag(bConnectFlag);
                        // if (dis_and_play_flag == 1)// 断开连接重新连接并播放标志
                        // {
                        // if (!progressdialog.isShowing()) {
                        // progressdialog.show();
                        // }
                        // // if (!AlarmUtil.OnlyConnect(strYstNum)) {
                        // // progressdialog.dismiss();
                        // //
                        // showTextToast(R.string.str_alarm_connect_failed_1);
                        // // lookVideoBtn.setEnabled(true);
                        // // }
                        // new Thread(new ConnectProcess(0x9999)).start();
                        // } else {
                        // if (progressdialog.isShowing()) {
                        // progressdialog.dismiss();
                        // }
                        // if (!vod_uri_.equals("")) {
                        // lookVideoBtn.setEnabled(true);
                        // }
                        // }

                    }
                        break;
                    // 4 -- 连接失败
                    // 6 -- 连接异常断开
                    case JVNetConst.ABNORMAL_DISCONNECT:
                        // 7 -- 服务停止连接，连接断开
                    case JVNetConst.SERVICE_STOP:
                    case JVNetConst.CONNECT_FAILED:
                        bConnectFlag = false;
                        dis_and_play_flag = 0;
                        mainApp.setAlarmConnectedFlag(bConnectFlag);
                        if (progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        if (!vod_uri_.equals("")) {
                            lookVideoBtn.setEnabled(true);
                        }
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
                            } else {// "Connect failed!"
                                showTextToast(R.string.connect_failed);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Consts.BAD_NOT_CONNECT:
                        bConnectFlag = false;
                        mainApp.setAlarmConnectedFlag(bConnectFlag);
                        if (dis_and_play_flag == 1)// 断开连接重新连接并播放标志
                        {
                            if (!progressdialog.isShowing()) {
                                progressdialog.show();
                            }
                            // if (!AlarmUtil.OnlyConnect(strYstNum)) {
                            // progressdialog.dismiss();
                            // showTextToast(R.string.str_alarm_connect_failed_1);
                            // lookVideoBtn.setEnabled(true);
                            // }
                            new Thread(new ConnectProcess(0x9999)).start();
                        } else {
                            if (progressdialog.isShowing()) {
                                progressdialog.dismiss();
                            }
                            if (!vod_uri_.equals("")) {
                                lookVideoBtn.setEnabled(true);
                            }
                        }
                        break;
                    case JVNetConst.OHTER_ERROR:
                        if (progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        showTextToast(R.string.connect_failed);
                        if (!vod_uri_.equals("")) {
                            lookVideoBtn.setEnabled(true);
                        }
                        break;
                    default:
                        if (progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        // Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
                        showTextToast(R.string.connect_failed);
                        if (!vod_uri_.equals("")) {
                            lookVideoBtn.setEnabled(true);
                        }
                        break;
                }
            }
                break;
            case Consts.CALL_NORMAL_DATA: {
                if (obj == null) {
                    MyLog.i("ALARM NORMALDATA", "normal data obj is null");
                    device_type = Consts.DEVICE_TYPE_IPC;
                    audio_bit = 16;
                } else {
                    MyLog.i("ALARM NORMALDATA", obj.toString());
                    try {
                        JSONObject jobj;
                        jobj = new JSONObject(obj.toString());
                        device_type = jobj.optInt("device_type",
                                Consts.DEVICE_TYPE_IPC);
                        if (null != jobj) {
                            audio_bit = jobj.optInt("audio_bit", 16);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        device_type = Consts.DEVICE_TYPE_IPC;
                        audio_bit = 16;
                    }
                }

            }
                break;
            case Consts.CALL_DOWNLOAD: {
                switch (arg2) {
                    case JVNetConst.JVN_RSP_DOWNLOADOVER:// 文件下载完毕
                        // showToast("文件下载完毕", Toast.LENGTH_SHORT);
                        // JVSUDT.JVC_DisConnect(JVConst.ONLY_CONNECT);//断开连接,如果视频走远程回放

                        // Jni.disconnect(Consts.ONLY_CONNECT_INDEX);// by lkp

                        if (bDownLoadFileType == 0) {
                            // 下载图片
                            // if (!vod_uri_.equals("")) {
                            // lookVideoBtn.setEnabled(true);
                            // }

                            Bitmap bmp = getLoacalBitmap(localImgPath);
                            if (null != bmp) {
                                alarmImage.setImageBitmap(bmp);
                            }
                        } else if (bDownLoadFileType == 1) {
                            // 下载录像完毕
                            if (progressdialog.isShowing()) {
                                progressdialog.dismiss();
                            }
                            // 启动播放界面
                            Intent intent = new Intent();
                            intent.setClass(CustomDialogActivity.this,
                                    JVVideoActivity.class);
                            intent.putExtra("URL", localVodPath);
                            intent.putExtra("IS_LOCAL", true);
                            startActivity(intent);
                        } else {

                        }
                        if (progressdialog.isShowing()) {
                            progressdialog.dismiss();
                        }
                        break;
                    case JVNetConst.JVN_CMD_DOWNLOADSTOP:// 停止文件下载
                        showTextToast(R.string.str_alarm_download_alarmimg_stopped);
                        break;
                    case JVNetConst.JVN_RSP_DOWNLOADE:// 文件下载失败
                        showTextToast(R.string.str_alarm_download_alarmimg_failed);
                        break;
                    case JVNetConst.JVN_RSP_DLTIMEOUT:// 文件下载超时
                        showTextToast(R.string.str_alarm_download_alarmimg_timeout);
                        break;
                    default:
                        break;
                }
                ;
                if (!vod_uri_.equals("")) {
                    lookVideoBtn.setEnabled(true);
                }
            }
                break;
        // case JVNetConst.JVN_REQ_DOWNLOAD:
        // {
        // switch (arg1) { //二级: 结果代码
        //
        // }
        // break;
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        // TODO Auto-generated method stub
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    protected void initSettings() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initUi() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveSettings() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void freeMe() {
        // TODO Auto-generated method stub

    }

    class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    if (bDownLoadFileType == 0) {
                        if (msg.arg1 == 0) {// 下载成功
                            // 下载图片
                            if (!vod_uri_.equals("")) {
                                lookVideoBtn.setEnabled(true);
                            }

                            Bitmap bmp = getLoacalBitmap(localImgPath);
                            if (null != bmp) {
                                alarmImage.setImageBitmap(bmp);
                            }
                        } else if (msg.arg1 == 404) { // 文件不存在
                            if (try_get_cloud_param_cnt == 1) {
                                new Thread(new GetCloudInfoThread(strYstNum,
                                        strChannelNum)).start();
                            } else {
                                showTextToast(R.string.str_cloud_file_error_1);
                            }

                        } else {
                            showTextToast(R.string.str_query_account_failed1);
                        }
                    }
                    else {
                        // 下载录像
                        lookVideoBtn.setEnabled(true);
                        if (msg.arg1 == 0) {// 下载成功
                            // 直接播放
                            Intent intent = new Intent();
                            intent.setClass(mActivity, JVVideoActivity.class);
                            intent.putExtra("URL", localVodPath);
                            intent.putExtra("IS_LOCAL", true);
                            startActivity(intent);
                        } else if (msg.arg1 == 404) { // 文件不存在
                            if (try_get_cloud_param_cnt == 1) {
                                new Thread(new GetCloudInfoThread(strYstNum,
                                        strChannelNum)).start();
                            } else {
                                showTextToast(R.string.str_cloud_file_error_1);
                            }

                        } else {
                            showTextToast(R.string.str_query_account_failed1);
                        }
                    }
                    break;
                case 0x01:
                    try {
                        JSONObject storageObject = new JSONObject(storageJson);
                        int ret = storageObject.optInt("rt", -1);
                        if (ret != 0) {
                            if (ret == 6) {
                                showTextToast(R.string.str_cloud_file_error_2);
                                return;
                            } else {
                                showTextToast(R.string.str_cloud_file_error_2);
                                return;
                            }
                        }
                        String strSpKey = String.format(Consts.FORMATTER_CLOUD_DEV,
                                strYstNum, strChannelNum);
                        MySharedPreference.putString(strSpKey,
                                storageObject.toString());
                        cloudBucket = storageObject.optString("csspace");
                        
                        if(bDownLoadFileType == 0){
                            if(null!=strImgUrl && !strImgUrl.equals("")){
                                String temp1[] = strImgUrl.split("com/");                              
                                if(temp1.length == 2){
                                    cloudResource = String.format("/%s/%s", cloudBucket,
                                            temp1[1]);
                                    if (!fileIsExists(localImgPath)) {
                                        bLocalFile = false;
                                        lookVideoBtn.setEnabled(false);
                                        // 起线程下载图片
                                        // 首先计算签名
                                        cloudSignImgUri = Jni.GenSignedCloudUri(
                                                cloudResource, storageJson);
                                        new Thread(new DownThread(cloudSignImgUri,
                                                "CSAlarmIMG/", localImgName)).start();
                                    } else {
                                        Bitmap bmp = getLoacalBitmap(localImgPath);
                                        if (null != bmp) {
                                            alarmImage.setImageBitmap(bmp);
                                        }
                                        bLocalFile = true;
                                        if (!vod_uri_.equals("")) {
                                            lookVideoBtn.setEnabled(true);
                                        }                                        
                                    }
                                }
                                else{
                                    String strTips = getResources().getString(R.string.str_cloud_url_error1);
                                    showTextToast(strTips);                                    
                                }                                               
                            }
                            else{
                                if (!vod_uri_.equals("")) {
                                    lookVideoBtn.setEnabled(true);
                                }                                  
                            }
                        }
                        else{//下载录像
                            if(null != vod_uri_ && !vod_uri_.equals("")){
                                String temp2[] = vod_uri_.split("com/");                              
                                if(temp2.length == 2){
                                    cloudResource = String.format("/%s/%s", cloudBucket,
                                            temp2[1]);
                                    // 下载录像
                                    // TODO
                                    cloudSignVodUri = Jni.GenSignedCloudUri(cloudResource, storageJson);
                                    new Thread(new HttpJudgeThread(cloudSignVodUri)).start();
                                }
                                else{
                                    String strTips = getResources().getString(R.string.str_cloud_url_error1);
                                    showTextToast(strTips);
                                }                                 
                            }                                                        
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 0x02:
                    if (msg.arg1 == 200) {// 可以访问
                        // TODO 先下载,目前只有下载录像才会检测是否存在，因此这里只有下载录像时才会走这
                        HttpDownloadTask dlTask = new HttpDownloadTask();
                        String[] params = new String[3];
                        params[0] = cloudSignVodUri;
                        params[1] = "CSAlarmVOD/";
                        params[2] = localVodName;
                        dlTask.execute(params);
                        // Intent intent = new Intent();
                        // intent.setClass(mActivity, JVVideoActivity.class);
                        // intent.putExtra("URL", cloudSignVodUri);
                        // intent.putExtra("IS_LOCAL", false);
                        // startActivity(intent);
                    } else if (msg.arg1 == 404) { // 文件不存在
                        lookVideoBtn.setEnabled(true);
                        if (try_get_cloud_param_cnt == 1) {
                            new Thread(new GetCloudInfoThread(strYstNum,
                                    strChannelNum)).start();
                        } else {
                            showTextToast(R.string.str_cloud_file_error_1);
                        }
                    } else {
                        lookVideoBtn.setEnabled(true);
                        showTextToast(R.string.str_query_account_failed1);
                    }
                    break;
                case 0x03:// 上报流量
                    if (msg.arg1 == 0) {
                        Log.e("Down", "上报成功");
                        downLoadSize = 0;
                        //showTextToast(R.string.str_report_flow_ok);
                        MySharedPreference.putLong(Consts.KEY_CLOUD_VOD_SIZE, 0);// 清0
                    }
                    else {
                        Log.e("Down", "上报失败");
                        showTextToast(getResources().getString(R.string.str_report_flow_failed)
                                + msg.arg1);
                    }
                    break;
                case 0x9001:
                    {
                        String strTips = getResources().getString(R.string.str_check_flow_tips1);
                        showTextToast(strTips);
                        lookVideoBtn.setEnabled(false);
                    }
                    break;
                case 0x9002://流量低于指定大小，提示用户
                    {
                        String strTips = getResources().getString(R.string.str_check_flow_tips2);
                        strTips = strTips.replace("%%", String.valueOf(TIPS_LIMIT_MB));
                        showTextToast(strTips);
                    }
                    break;
                case 0x9003://查询流量失败
                    {
                        String strTips = getResources().getString(R.string.str_cloud_query_error_2);
                        showTextToast(strTips);
                    }
                    
                    break;
                case JVNetConst.JVN_RSP_DISCONN:
                    // new Thread(new ToastProcess(0x9999)).start();
                    if (progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    showTextToast(R.string.str_disconnect_resp_timeout);
                    break;
                case Consts.BAD_HAS_CONNECTED:
                    if (progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    String strDescString = getResources().getString(
                            R.string.connect_failed);
                    showTextToast(strDescString);
                    if (!vod_uri_.equals("")) {
                        lookVideoBtn.setEnabled(true);
                    }
                    break;
                case -99:
                    if (progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    String strDescString2 = getResources().getString(
                            R.string.connect_failed)
                            + ":-99";
                    showTextToast(strDescString2);
                    if (!vod_uri_.equals("")) {
                        lookVideoBtn.setEnabled(true);
                    }
                    break;
                case Consts.ARG2_STATUS_CONN_OVERFLOW:
                    if (progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    showTextToast(R.string.overflow);
                    if (!vod_uri_.equals("")) {
                        lookVideoBtn.setEnabled(true);
                    }
                    break;
                case 0x9999:
                    progressdialog.dismiss();
                    showTextToast(R.string.str_alarm_connect_failed_1);
                    if (!vod_uri_.equals("")) {
                        lookVideoBtn.setEnabled(true);
                    }
                    break;
                case 0x9990:
                    if (progressdialog.isShowing()) {
                        progressdialog.dismiss();
                    }
                    lookVideoBtn.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    }

    class TimeOutProcess implements Runnable {
        private int tag;

        public TimeOutProcess(int arg1) {
            tag = arg1;
        }

        @Override
        public void run() {
            myHandler.sendEmptyMessageDelayed(tag, 16000);
        }
    }

    class ConnectProcess implements Runnable {
        private int tag;

        public ConnectProcess(int arg1) {
            tag = arg1;
        }

        @Override
        public void run() {
            int try_cnt = 2;
            int conn_ret = -1;
            do {
                conn_ret = AlarmUtil.OnlyConnect(strYstNum);

                if (Consts.BAD_HAS_CONNECTED == conn_ret) {
                    try {
                        Jni.disconnect(Consts.ONLY_CONNECT_INDEX);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    tag = Consts.BAD_HAS_CONNECTED;
                    try_cnt--;
                } else if (Consts.BAD_CONN_OVERFLOW == conn_ret) {
                    // handler.sendMessage(handler.obtainMessage(
                    // Consts.WHAT_PLAY_STATUS, Consts.ONLY_CONNECT_INDEX,
                    // Consts.ARG2_STATUS_CONN_OVERFLOW));
                    tag = Consts.ARG2_STATUS_CONN_OVERFLOW;
                    myHandler.sendEmptyMessage(tag);
                    break;
                } else if (-99 == conn_ret) {
                    tag = -99;
                    myHandler.sendEmptyMessage(tag);
                    break;
                } else {
                    // handler.sendMessage(handler.obtainMessage(
                    // Consts.WHAT_PLAY_STATUS, Consts.ONLY_CONNECT_INDEX,
                    // Consts.ARG2_STATUS_CONNECTING));
                    MyLog.e("New Alarm", "调用connect返回成功");
                    break;
                }

            } while (try_cnt > 0);
            if (try_cnt == 0) {
                myHandler.sendEmptyMessageDelayed(tag, 0);
            }
        }
    }

    class DownThread implements Runnable {

        private String uri_, fileDir_, fileName_;

        public DownThread(String uri, String strPath, String fileName) {
            uri_ = uri;
            fileDir_ = strPath;
            fileName_ = fileName;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int ret = -1;
            Log.e("Down", "开始下载.............");
            HttpDownloader downloader = new HttpDownloader();
            ret = downloader.downFile(uri_, fileDir_, fileName_, null);// 下载图片先不统计流量
            Log.e("Down", "下载结束.............");
            Message msg = myHandler.obtainMessage(0x00, ret, 0x00);
            msg.sendToTarget();
        }

    }

    class HttpJudgeThread implements Runnable {

        private String uri_;

        public HttpJudgeThread(String uri) {
            uri_ = uri;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int ret = -1;
            HttpDownloader downloader = new HttpDownloader();
            ret = downloader.httpJudge(uri_);
            Message msg = myHandler.obtainMessage(0x02, ret, 0x00);
            msg.sendToTarget();
        }

    }

    class GetCloudInfoThread implements Runnable {

        private String ystGuid_;
        private int channelNo_;

        public GetCloudInfoThread(String ystGuid, int channelNo) {
            ystGuid_ = ystGuid;
            channelNo_ = channelNo;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            storageJson = DeviceUtil.getDevCloudStorageInfo(ystGuid_,
                    channelNo_);
            JSONObject storageObject = null;
            try {
                storageObject = new JSONObject(storageJson);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (storageObject == null) {
                try_get_cloud_param_cnt = 1;
                storageJson = "{\"rt\":0}";
            } else {
                int ret = storageObject.optInt("rt", -1);
                if (ret == 0) {
                    try_get_cloud_param_cnt = 0;
                }
            }

            myHandler.sendEmptyMessage(0x01);
        }

    }

    @Override
    public void onRealTimeDownloadSize(int RTSize) {
        // TODO Auto-generated method stub
        if (bDownLoadFileType == 1) {
            // 目前只统计录像的流量
            downLoadSize += RTSize;
            // Log.e("Down", "downLoadSize:"+downLoadSize);
        }
    }

    @Override
    public void onDownLoadFinished() {
        // TODO Auto-generated method stub
    }

    class HttpDownloadTask extends AsyncTask<String, Integer, Integer> {

        private String _uri, _fileDir, _fileName;

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (params.length < 3) {
                return -1;
            }
            int ret = -1;
            _uri = params[0];
            _fileDir = params[1];
            _fileName = params[2];
            // 先去查询剩余流量
            int check_ret = checkoutLeftFlow();
            if (check_ret < 0) {
                return check_ret;
            }
            else if (check_ret == 0) {
                // 剩余流量不足(充值+免费)
                return 0x9001;
            }
            else {
                if (check_ret <= TIPS_LIMIT_MB * 1024) {
                    Message msg = myHandler.obtainMessage(0x9002);
                    msg.sendToTarget();
                }
                Log.e("Down", "开始下载............." + _uri + "," + _fileDir + "," + _fileName);
                HttpDownloader downloader = new HttpDownloader();
                ret = downloader.downFile(_uri, _fileDir, _fileName, mActivity);
                Log.e("Down", "下载结束.............");
            }

            return ret;
        }

        @Override
        protected void onCancelled() {
            if (progressdialog.isShowing()) {
                progressdialog.dismiss();
            }
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            progressdialog.show();
            // Toast.makeText(mActivity, "开始下载", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (progressdialog.isShowing()) {
                progressdialog.dismiss();
            }
            if (result == 0x00) {
                // Toast.makeText(mActivity, "下载成功", Toast.LENGTH_SHORT).show();
            }
            else if (result == 0x9001) {
                String strTips = getResources().getString(R.string.str_check_flow_tips1);
                showTextToast(strTips);
                return;
            }
            else {
                showTextToast(R.string.video_download_failed);
            }
            if (downLoadSize >= REPORT_LIMIT) {
                Log.e("Down", "达到上限，开始上报");
                MySharedPreference.putLong(Consts.KEY_CLOUD_VOD_SIZE, downLoadSize);// 先保存
                new Thread(new ReportThread(downLoadSize)).start();
            }
            else {
                MySharedPreference.putLong(Consts.KEY_CLOUD_VOD_SIZE, downLoadSize);
            }
            Message msg = myHandler.obtainMessage(0x00, result, 0x00);
            msg.sendToTarget();
        }
    }

    class ReportThread implements Runnable {

        private int _flow;

        public ReportThread(long flow) {
            this._flow = (int) (flow / 1024);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String account = statusHashMap.get(Consts.KEY_USERNAME);
            int ret = JVACCOUNT.ReportUserFlow(account, strYstNum, strChannelNum, 15, _flow);
            Message msg = myHandler.obtainMessage(0x03, ret, 0x00);
            msg.sendToTarget();
        }

    }

    private int checkoutLeftFlow() {
        String resJson = DeviceUtil.getUserSurFlow();
        Log.e("cloud", "check flow res:" + resJson);
        JSONObject resObj;
        int charge_left = 0, free_left = 0;
        int total_left = 0;
        try {
            resObj = new JSONObject(resJson);

            int ret = resObj.optInt(JVDeviceConst.JK_RESULT);
            if (ret != 0) {
                return -1;
            }
            else {
                // 构造方法的字符格式这里如果小数不足2位,会以0补足.
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                int fee_type = resObj.optInt(JVDeviceConst.JK_CLOUD_FEE_TYPE, 0);
                if (fee_type == 0) {
                    // 单位流量
                    charge_left = resObj.optInt(JVDeviceConst.JK_CLOUD_STORAGE_FLOW, 0);

                }
                else if (fee_type == 1) {
                    // 单位元
                    charge_left = resObj.optInt(JVDeviceConst.JK_CLOUD_STORAGE_FLOW, 0);
                }
                if (charge_left < 0) {
                    charge_left = 0;
                }
                int free_total = resObj.optInt(JVDeviceConst.JK_CLOUD_STORAGE_FFREE, 0);
                if (free_total < 0) {
                    free_total = 0;
                }
                int free_used = resObj.optInt(JVDeviceConst.JK_CLOUD_STORAGE_FFREE_USE, 0);
                if (free_used < 0) {
                    free_used = 0;
                }
                free_left = (free_used <= free_total) ? (free_total - free_used) : 0;

                total_left = charge_left + free_left;
                return total_left;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return total_left;
    }
    
    class CloudCheckInfo implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int ret = checkoutLeftFlow();
            if(ret > 0){
                //流量足够，走正常的流程
                // 先调用接口获取计算签名参数
                String strSpKey = String.format(Consts.FORMATTER_CLOUD_DEV,
                        pushInfo.ystNum, pushInfo.coonNum);
                storageJson = MySharedPreference.getString(strSpKey);

                if (storageJson.equals("") || null == storageJson) {
                    // storageJson =
                    // DeviceUtil.getDevCloudStorageInfo(pushInfo.ystNum,
                    // pushInfo.coonNum);
                    storageJson = DeviceUtil.getDevCloudStorageInfo(pushInfo.ystNum,
                            pushInfo.coonNum);
                    JSONObject storageObject = null;
                    try {
                        storageObject = new JSONObject(storageJson);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (storageObject == null) {
                        try_get_cloud_param_cnt = 1;
                        storageJson = "{\"rt\":0}";
                    } else {
                        ret = storageObject.optInt("rt", -1);
                        if (ret == 0) {
                            try_get_cloud_param_cnt = 0;
                        }
                    }
                    myHandler.sendEmptyMessage(0x01);                        
                } else {
                    myHandler.sendEmptyMessage(0x01);
                }
            }
            else if(ret == 0){
                //流量不足
                myHandler.sendEmptyMessage(0x9001);
            }
            else{
                //失败
                myHandler.sendEmptyMessage(0x9003);
            }
        }
        
    }
}
