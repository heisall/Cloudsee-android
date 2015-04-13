
package com.jovision.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.DeviceSettingsActivity.OnMainListener;
import com.jovision.bean.Device;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class DeviceSettingsMainFragment extends Fragment implements
        OnClickListener, OnMainListener {

    private View rootView;// 缓存Fragment view
    private ArrayList<Device> deviceList;
    private String devicename;
    private int channelIndex;// 窗口
    protected Toast toast;
    private int timeType;// 时间格式 0三种格式，1两种格式
    private int dialogflag; // 0表示重置设备，1表示重启设备;

    public interface OnFuncActionListener {
        public void OnFuncEnabled(int func_index, int enabled);

        public void OnFuncSelected(int func_index, String params);
    }

    private OnFuncActionListener mListener;
    private ImageView func_swalert;
    private ImageView func_swmotion;
    private ImageView funcAlaramSound;
    private int func_alert_enabled = -1;
    private int func_motion_enabled = -1;
    private int func_alarm_sound = -1;// 1：开启 0：关闭
    private int alarm_way_flag = -1;// ///////////判断的优先级最高
    private String strParam = "";
    private String alarmTime0 = "";
    private String startTime = "", endTime = "";
    private String startHour = "", startMin = "";
    private String endHour = "", endMin = "";
    private RelativeLayout functionlayout0, functionlayout1, functionlayout2, functionlayout3,
            functionlayout4, functionlayout5, functionlayout6, functionlayout7, functionlayout8,
            funclayout9;
    private RelativeLayout functiontips1, functiontips2, functiontips3,
            functiontips6, functiontips7;
    private TextView alarmTime0TextView, funtion_titile_71;
    private int power = 0;

    private Dialog initDialog;// 显示弹出框
    private TextView dialogCancel;// 取消按钮
    private TextView dialogCompleted;// 确定按钮

    private Dialog resetDialog;// 显示弹出框
    private TextView resetCancel;// 取消按钮
    private TextView resetCompleted;// 确定按钮
    private TextView retext;// 提示内容

    // 设备名称
    private TextView device_name;
    // 设备用户名
    private EditText device_nameet;
    // 设备密码
    private EditText device_passwordet;
    // 设备密码编辑键
    private ImageView device_password_cancleI;
    private ImageView dialog_cancle_img;

    // 云存储
    private ImageView func_cloudsw;
    private int func_cloud_enabled = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFuncActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnFuncEnabledListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        power = getArguments().getInt("power");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.dev_settings_main_fragment,
                    container, false);
        }
        deviceList = CacheUtil.getDevList();
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        // 云存储
        func_cloudsw = (ImageView) rootView.findViewById(R.id.function_switch_01);

        func_swalert = (ImageView) rootView
                .findViewById(R.id.function_switch_11);
        func_swmotion = (ImageView) rootView
                .findViewById(R.id.function_switch_21);
        funcAlaramSound = (ImageView) rootView
                .findViewById(R.id.function_switch_31);
        functionlayout0 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout0);
        functionlayout1 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout1);
        functionlayout2 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout2);
        functionlayout3 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout3);
        functionlayout4 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout4);
        functionlayout5 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout5);
        functionlayout6 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout6);
        functionlayout7 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout7);
        functionlayout8 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout8);
        funclayout9 = (RelativeLayout) rootView
                .findViewById(R.id.funclayout9);

        functiontips1 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_01);
        functiontips2 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_02);
        functiontips3 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_03);
        functiontips6 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_06);
        functiontips7 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_07);

        alarmTime0TextView = (TextView) rootView
                .findViewById(R.id.funtion_titile_32);
        funtion_titile_71 = (TextView) rootView
                .findViewById(R.id.funtion_titile_71);

        func_cloudsw.setOnClickListener(this);
        func_swalert.setOnClickListener(this);
        func_swmotion.setOnClickListener(this);
        funcAlaramSound.setOnClickListener(this);
        functionlayout3.setOnClickListener(this);
        functionlayout4.setOnClickListener(this);
        functionlayout5.setOnClickListener(this);
        functionlayout6.setOnClickListener(this);
        functionlayout7.setOnClickListener(this);
        functionlayout8.setOnClickListener(this);
        funclayout9.setOnClickListener(this);

        Bundle data = getArguments();// 获得从activity中传递过来的值

        strParam = data.getString("KEY_PARAM");

        try {
            JSONObject paramObject = new JSONObject(strParam);
            Log.e("Alarm", "paramObject:" + strParam);
            func_alarm_sound = paramObject.optInt("bAlarmSound", -1);
            if (0 == func_alarm_sound) {
                funcAlaramSound
                        .setBackgroundResource(R.drawable.morefragment_normal_icon);
            } else if (1 == func_alarm_sound) {
                funcAlaramSound
                        .setBackgroundResource(R.drawable.morefragment_selector_icon);
            } else {
                functionlayout6.setVisibility(View.GONE);
                functiontips6.setVisibility(View.GONE);
            }

            // 云存储服务
            // functionlayout0.setVisibility(View.GONE);
            func_cloud_enabled = paramObject.optInt("bCloudEnabled", -1);
            if (0 == func_cloud_enabled) {
                func_cloudsw
                        .setBackgroundResource(R.drawable.morefragment_normal_icon);
            } else if (1 == func_cloud_enabled) {
                func_cloudsw
                        .setBackgroundResource(R.drawable.morefragment_selector_icon);
            } else {
                functionlayout0.setVisibility(View.GONE);
            }

            alarm_way_flag = paramObject.optInt("alarmWay", -1);
            if (alarm_way_flag == 0) {
                // 走设备服务器
                func_alert_enabled = paramObject.optInt("bAlarmEnable", -1);
                functionlayout2.setVisibility(View.GONE);
                functiontips2.setVisibility(View.GONE);
                functionlayout3.setVisibility(View.GONE);
                functiontips3.setVisibility(View.GONE);
                switch (func_alert_enabled) {
                    case 0:
                        func_swalert
                                .setBackgroundResource(R.drawable.morefragment_normal_icon);
                        break;
                    case 1:
                        func_swalert
                                .setBackgroundResource(R.drawable.morefragment_selector_icon);
                        break;
                    case -1:
                        functionlayout1.setVisibility(View.GONE);
                        functiontips1.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            } else {
                // 走云视通
                func_alert_enabled = paramObject.optInt("bAlarmEnable", -1);
                func_motion_enabled = paramObject.optInt("bMDEnable", -1);
                func_alarm_sound = paramObject.optInt("bAlarmSound", -1);
                alarmTime0 = paramObject.optString("alarmTime0", "");
                Pattern pattern = Pattern.compile("-");
                String[] strs = pattern.split(alarmTime0);
                for (int i = 0; i < strs.length; i++) {
                    System.out.println(strs[i]);
                }
                if (strs.length == 2) {
                    startTime = strs[0];
                    endTime = strs[1];
                    Pattern pattern_s = Pattern.compile(":");
                    String[] strs_s = pattern_s.split(startTime);
                    for (int i = 0; i < strs_s.length; i++) {
                        System.out.println(strs_s[i]);
                    }
                    switch (strs_s.length) {
                        case 1:
                            startHour = strs_s[0];
                            startMin = "00";
                            break;
                        case 2:
                        case 3:
                            startHour = strs_s[0];
                            startMin = strs_s[1];
                            break;
                        default:
                            startHour = "00";
                            startMin = "00";
                            break;
                    }

                    Pattern pattern_e = Pattern.compile(":");
                    String[] strs_e = pattern_e.split(endTime);
                    for (int i = 0; i < strs_e.length; i++) {
                        System.out.println(strs_e[i]);
                    }
                    switch (strs_e.length) {
                        case 1:
                            endHour = strs_e[0];
                            endMin = "00";
                            break;
                        case 2:
                        case 3:
                            endHour = strs_e[0];
                            endMin = strs_e[1];
                            break;
                        default:
                            endHour = "23";
                            endMin = "59";
                            break;
                    }

                    startTime = String.format("%s:%s", startHour, startMin);
                    endTime = String.format("%s:%s", endHour, endMin);

                    alarmTime0TextView.setText(startTime + " - " + endTime);
                } else {
                    startTime = "00:00";
                    endTime = "23:59";
                }

                switch (func_alert_enabled) {
                    case 0:
                        func_swalert
                                .setBackgroundResource(R.drawable.morefragment_normal_icon);
                        functionlayout2.setVisibility(View.GONE);
                        functiontips2.setVisibility(View.GONE);
                        functionlayout3.setVisibility(View.GONE);
                        functiontips3.setVisibility(View.GONE);
                        break;
                    case 1:
                        func_swalert
                                .setBackgroundResource(R.drawable.morefragment_selector_icon);
                        break;
                    case -1:
                        functionlayout1.setVisibility(View.GONE);
                        functiontips1.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }

                switch (func_motion_enabled) {
                    case 0:
                        func_swmotion
                                .setBackgroundResource(R.drawable.morefragment_normal_icon);
                        break;
                    case 1:
                        func_swmotion
                                .setBackgroundResource(R.drawable.morefragment_selector_icon);
                        break;
                    case -1:
                        functionlayout2.setVisibility(View.GONE);
                        functiontips2.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }

                // switch (func_alarm_sound) {
                // case 0:
                // funcAlaramSound
                // .setBackgroundResource(R.drawable.morefragment_normal_icon);
                // break;
                // case 1:
                // funcAlaramSound
                // .setBackgroundResource(R.drawable.morefragment_selector_icon);
                // break;
                // case -1:
                // functionlayout5.setVisibility(View.GONE);
                // functiontips2.setVisibility(View.GONE);
                // break;
                // default:
                // break;
                // }

                if (alarmTime0.equals("")
                        || (startTime.equals("00:00") && endTime
                                .equals("23:59"))) {
                    // functionlayout3.setVisibility(View.GONE);
                    // functiontips3.setVisibility(View.GONE);
                    // functionlayout3.setVisibility(View.VISIBLE);
                    // functiontips3.setVisibility(View.VISIBLE);
                    // 如果没字段显示全天
                    MyLog.e("Alarm", "-------startTime:" + startTime
                            + "-----endTime:" + endTime);
                    alarmTime0TextView.setText(getActivity().getResources()
                            .getString(R.string.str_all_day));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.function_switch_01:
                if (func_cloud_enabled == 1) {
                    // 打开--->关闭
                    mListener.OnFuncEnabled(Consts.DEV_SETTINGS_CLOUD, 0);
                } else if (func_cloud_enabled == 0) {
                    // 关闭--->打开
                    mListener.OnFuncEnabled(Consts.DEV_SETTINGS_CLOUD, 1);
                } else {
                    // 隐藏
                }
                break;
            case R.id.function_switch_11:
                if (func_alert_enabled == 1) {
                    // 打开--->关闭
                    mListener.OnFuncEnabled(Consts.DEV_SETTINGS_ALARM, 0);
                } else if (func_alert_enabled == 0) {
                    // 关闭--->打开
                    mListener.OnFuncEnabled(Consts.DEV_SETTINGS_ALARM, 1);
                } else {
                    // 隐藏
                }
                break;
            case R.id.function_switch_21:
                if (func_motion_enabled == 1) {
                    // 打开--->关闭
                    mListener.OnFuncEnabled(Consts.DEV_SETTINGS_MD, 0);
                } else if (func_motion_enabled == 0) {
                    // 关闭--->打开
                    mListener.OnFuncEnabled(Consts.DEV_SETTINGS_MD, 1);
                } else {
                    // 隐藏
                }
                break;
            case R.id.function_switch_31: {// 报警声音
                if (func_alarm_sound == 1) {
                    // 打开--->关闭
                    mListener.OnFuncEnabled(Consts.DEV_ALARAM_SOUND, 0);
                } else if (func_alarm_sound == 0) {
                    // 关闭--->打开
                    mListener.OnFuncEnabled(Consts.DEV_ALARAM_SOUND, 1);
                } else {
                    // 隐藏
                }
                break;
            }
            case R.id.device_passwrodet_cancle:
                device_passwordet.setText("");
                break;
            case R.id.dialog_cancle_img:
                initDialog.dismiss();
                break;
            case R.id.reset_cancel:
                resetDialog.dismiss();
                break;
            case R.id.reset_completed:
                if (0 == dialogflag) {
                    mListener.OnFuncSelected(Consts.DEV_RESET_DEVICE, "");
                } else if (1 == dialogflag) {
                    mListener.OnFuncSelected(Consts.DEV_RESTART_DEVICE, "");
                }
                resetDialog.dismiss();
                break;
            case R.id.funclayout5:
                dialogflag = 0;
                ResetDialog();
                break;
            case R.id.funclayout6:
                break;
            case R.id.funclayout4:
                if (DeviceSettingsActivity.isadmin) {
                    initSummaryDialog();
                } else {
                    showTextToast(getActivity(), R.string.edit_pass_not);
                }
                break;
            case R.id.funclayout3:
                JSONObject paraObject = new JSONObject();
                try {
                    paraObject.put("st", startTime);
                    paraObject.put("et", endTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mListener.OnFuncSelected(Consts.DEV_SETTINGS_ALARMTIME,
                        paraObject.toString());
                break;
            case R.id.funclayout7:
                mListener.OnFuncSelected(JVNetConst.TIME_ZONE,
                        null);
                break;
            case R.id.funclayout8:
                dialogflag = 1;
                ResetDialog();
                break;
            case R.id.funclayout9:
                mListener.OnFuncSelected(JVNetConst.SET_TIME,
                        timeType + "");
                break;
            default:
                break;
        }
    }

    /** 弹出框初始化 */
    private void initSummaryDialog() {
        initDialog = new Dialog(getActivity(), R.style.mydialog);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_modify, null);
        initDialog.setContentView(view);
        dialog_cancle_img = (ImageView) view
                .findViewById(R.id.dialog_cancle_img);
        dialogCancel = (TextView) view.findViewById(R.id.dialog_cancel);
        dialogCompleted = (TextView) view.findViewById(R.id.dialog_completed);
        device_name = (TextView) view.findViewById(R.id.device_namew);
        device_nameet = (EditText) view.findViewById(R.id.device_nameet);
        device_nameet.setText("admin");
        device_nameet.setEnabled(false);
        device_passwordet = (EditText) view
                .findViewById(R.id.device_passwrodet);

        device_passwordet.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    selectionStart = device_passwordet.getSelectionStart();
                    selectionEnd = device_passwordet.getSelectionEnd();
                    if (!ConfigUtil.checkDevPwd(device_passwordet.getText()
                            .toString())) {
                        if (selectionStart > 0) {
                            showTextToast(getActivity(),
                                    R.string.device_pass_notzh);
                            s.delete(selectionStart - 1, selectionEnd);
                            int tempSelection = selectionStart;
                            device_passwordet.setText(s);
                            device_passwordet.setSelection(tempSelection);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        device_password_cancleI = (ImageView) view
                .findViewById(R.id.device_passwrodet_cancle);
        dialog_cancle_img.setOnClickListener(this);
        device_password_cancleI.setOnClickListener(this);
        initDialog.show();

        if (!"".equals(DeviceSettingsActivity.fullno)) {
            device_name.setText(DeviceSettingsActivity.fullno);
        }
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog.dismiss();
            }
        });
        dialogCompleted.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ("".equals(device_passwordet.getText().toString())) {
                    showTextToast(getActivity(),
                            R.string.login_str_device_pass_notnull);
                } else if (!ConfigUtil.checkDevPwd(device_passwordet.getText()
                        .toString())) {
                    showTextToast(getActivity(), R.string.device_pass_notzh);
                } else {
                    JSONObject paraObject = new JSONObject();
                    try {
                        paraObject.put("userName", device_nameet.getText()
                                .toString());
                        paraObject.put("userPwd", device_passwordet.getText()
                                .toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mListener.OnFuncSelected(JVNetConst.JVN_GET_USERINFO,
                            paraObject.toString());
                }
            }
        });
    }

    /** 是否确定重置设备 */
    private void ResetDialog() {
        resetDialog = new Dialog(getActivity(), R.style.mydialog);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_reset, null);
        resetDialog.setContentView(view);

        resetCancel = (TextView) view.findViewById(R.id.reset_cancel);
        resetCompleted = (TextView) view.findViewById(R.id.reset_completed);
        retext = (TextView) view.findViewById(R.id.retext);

        if (0 == dialogflag) {
            retext.setText(getResources().getString(R.string.str_reset_sure));
        } else if (1 == dialogflag) {
            retext.setText(getResources().getString(R.string.str_restart_sure));
        }
        resetCancel.setOnClickListener(this);
        resetCompleted.setOnClickListener(this);
        resetDialog.show();
    }

    @Override
    public void onMainAction(int packet_type, int packet_subtype, int ex_type,
            int func_index, int destFlag, String Content) {
        Log.e("Alarm", "----onMainAction---" + packet_type + "," + packet_type
                + "," + ex_type);
        switch (packet_type) {
            case JVNetConst.JVN_GET_USERINFO:
                // Toast.makeText(
                // getActivity(),
                // getActivity().getResources()
                // .getString(R.string.pwd_success),
                // Toast.LENGTH_SHORT).show();
                showTextToast(getActivity(), R.string.pwd_success);
                initDialog.dismiss();
                break;
            case JVNetConst.RC_EXTEND: {
                switch (packet_subtype) {
                    case JVNetConst.SET_TIME:
                        if (1 == ex_type) {
                            // TODO
                            timeType = 0;
                        } else {
                            timeType = 1;
                        }
                        break;
                    case JVNetConst.TIME_ZONE:
                        funtion_titile_71.setText(Content);
                        if (1 == ex_type) {
                            functionlayout7.setVisibility(View.GONE);
                            functiontips7.setVisibility(View.GONE);
                        }
                        break;
                    case JVNetConst.RC_EX_MD:
                        if (ex_type == JVNetConst.EX_MD_SUBMIT) {
                            // 移动侦测
                            if (func_motion_enabled == 1) {
                                // 打开--->关闭
                                // Log.e("Alarm",
                                // "before func_motion_enabled:"+func_motion_enabled+","+destFlag);
                                if (func_motion_enabled == destFlag) {
                                    Log.e("Alarm", "middle Don't need to change");
                                    return;
                                }
                                func_motion_enabled = 0;
                                func_swmotion
                                        .setBackgroundResource(R.drawable.morefragment_normal_icon);
                                // String text = getResources().getString(
                                // R.string.str_mdenabled_close_ok);
                                // Toast.makeText(getActivity(), text,
                                // Toast.LENGTH_SHORT)
                                // .show();
                                showTextToast(getActivity().getApplicationContext(),
                                        R.string.str_mdenabled_close_ok);
                                // Log.e("Alarm",
                                // "after func_motion_enabled:"+func_motion_enabled);
                            } else if (func_motion_enabled == 0) {
                                // 关闭--->打开
                                if (func_motion_enabled == destFlag) {
                                    Log.e("Alarm", "middle Don't need to change");
                                    return;
                                }
                                func_motion_enabled = 1;
                                func_swmotion
                                        .setBackgroundResource(R.drawable.morefragment_selector_icon);
                                // String text = getResources().getString(
                                // R.string.str_mdenabled_open_ok);
                                // Toast.makeText(getActivity(), text,
                                // Toast.LENGTH_SHORT)
                                // .show();
                                showTextToast(getActivity().getApplicationContext(),
                                        R.string.str_mdenabled_open_ok);
                            } else {
                                // 隐藏
                                // String text = getResources().getString(
                                // R.string.str_operation_failed);
                                // Toast.makeText(getActivity(), text,
                                // Toast.LENGTH_SHORT)
                                // .show();
                                showTextToast(getActivity(),
                                        R.string.str_operation_failed);
                            }
                        }
                        break;
                    case JVNetConst.RC_EX_ALARM:
                        // 安全防护与报警声音开关返回值是一样的，这里要做区分
                        if (ex_type == JVNetConst.EX_ALARM_SUBMIT) {
                            switch (func_index) {
                                case Consts.DEV_ALARAM_SOUND:// 报警声音开关
                                {
                                    if (func_alarm_sound == 1) {
                                        // 打开--->关闭
                                        // Log.e("Alarm",
                                        // "before func_motion_enabled:"+func_motion_enabled+","+destFlag);
                                        if (func_alarm_sound == destFlag) {
                                            Log.e("Alarm", "middle Don't need to change");
                                            return;
                                        }
                                        func_alarm_sound = 0;
                                        funcAlaramSound
                                                .setBackgroundResource(R.drawable.morefragment_normal_icon);
                                        // String text =
                                        // getResources().getString(
                                        // R.string.str_mdenabled_close_ok);
                                        // Toast.makeText(getActivity(), text,
                                        // Toast.LENGTH_SHORT)
                                        // .show();
                                        showTextToast(
                                                getActivity().getApplicationContext(),
                                                R.string.str_operation_success);
                                        // Log.e("Alarm",
                                        // "after func_motion_enabled:"+func_motion_enabled);
                                    } else if (func_alarm_sound == 0) {
                                        // 关闭--->打开
                                        if (func_alarm_sound == destFlag) {
                                            Log.e("Alarm", "middle Don't need to change");
                                            return;
                                        }
                                        func_alarm_sound = 1;
                                        funcAlaramSound
                                                .setBackgroundResource(R.drawable.morefragment_selector_icon);
                                        // String text =
                                        // getResources().getString(
                                        // R.string.str_mdenabled_open_ok);
                                        // Toast.makeText(getActivity(), text,
                                        // Toast.LENGTH_SHORT)
                                        // .show();
                                        showTextToast(
                                                getActivity().getApplicationContext(),
                                                R.string.str_operation_success);
                                    } else {
                                        // 隐藏
                                        // String text =
                                        // getResources().getString(
                                        // R.string.str_operation_failed);
                                        // Toast.makeText(getActivity(), text,
                                        // Toast.LENGTH_SHORT)
                                        // .show();
                                        showTextToast(getActivity(),
                                                R.string.str_operation_failed);
                                    }
                                }
                                    break;
                                default:
                                    if (func_alert_enabled == 1) {
                                        // 打开--->关闭
                                        // Log.e("Alarm",
                                        // "before func_alert_enabled:"+func_alert_enabled);
                                        if (func_alert_enabled == destFlag) {
                                            Log.e("Alarm", "middle Don't need to change");
                                            return;
                                        }
                                        if (alarm_way_flag == 1) {
                                            functionlayout2.setVisibility(View.GONE);
                                            functiontips2.setVisibility(View.GONE);
                                            functionlayout3.setVisibility(View.GONE);
                                            functiontips3.setVisibility(View.GONE);

                                            // String text =
                                            // getResources().getString(
                                            // R.string.protect_close_succ);
                                            // Toast.makeText(getActivity(),
                                            // text,
                                            // Toast.LENGTH_SHORT).show();
                                            showTextToast(getActivity()
                                                    .getApplicationContext(),
                                                    R.string.protect_close_succ);
                                        }
                                        func_alert_enabled = 0;
                                        func_swalert
                                                .setBackgroundResource(R.drawable.morefragment_normal_icon);
                                        // Log.e("Alarm",
                                        // "after func_alert_enabled:"+func_alert_enabled);
                                    } else if (func_alert_enabled == 0) {
                                        // 关闭--->打开
                                        if (func_alert_enabled == destFlag) {
                                            Log.e("Alarm", "middle Don't need to change");
                                            return;
                                        }
                                        if (alarm_way_flag == 1) {
                                            if (func_motion_enabled == -1) {
                                                functionlayout2.setVisibility(View.GONE);
                                                functiontips2.setVisibility(View.GONE);
                                            } else {
                                                functionlayout2.setVisibility(View.VISIBLE);
                                                functiontips2.setVisibility(View.VISIBLE);
                                                functionlayout3.setVisibility(View.VISIBLE);
                                                functiontips3.setVisibility(View.VISIBLE);
                                            }
                                            // String text =
                                            // getResources().getString(
                                            // R.string.protect_open_succ);
                                            // Toast.makeText(getActivity(),
                                            // text,
                                            // Toast.LENGTH_SHORT).show();
                                            showTextToast(getActivity()
                                                    .getApplicationContext(),
                                                    R.string.protect_open_succ);
                                        }
                                        func_alert_enabled = 1;
                                        func_swalert
                                                .setBackgroundResource(R.drawable.morefragment_selector_icon);
                                    } else {
                                        // 隐藏
                                        // String text =
                                        // getResources().getString(
                                        // R.string.str_operation_failed);
                                        // Toast.makeText(getActivity(), text,
                                        // Toast.LENGTH_SHORT)
                                        // .show();
                                        showTextToast(
                                                getActivity().getApplicationContext(),
                                                R.string.str_operation_failed);
                                    }
                                    break;
                            }

                        }
                        break;
                    default:
                        break;
                }
            }
                break;

            default:
                break;
        }
    }

    /**
     * 弹系统消息
     * 
     * @param context
     * @param id
     */
    public void showTextToast(Context context, int id) {
        String msg = context.getResources().getString(id);
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 弹系统消息
     * 
     * @param context
     * @param id
     */
    public void showTextToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    public void onFuncAction(int func_index, int nowFlag, int destFlag) {
        if (func_cloud_enabled == 1) {
            // 打开--->关闭
            // Log.e("Alarm",
            // "before func_motion_enabled:"+func_motion_enabled+","+destFlag);
            if (func_cloud_enabled == destFlag) {
                Log.e("Alarm", "middle Don't need to change");
                return;
            }
            func_cloud_enabled = 0;
            func_cloudsw
                    .setBackgroundResource(R.drawable.morefragment_normal_icon);
            // String text = getResources().getString(
            // R.string.str_mdenabled_close_ok);
            // Toast.makeText(getActivity(), text,
            // Toast.LENGTH_SHORT)
            // .show();
            showTextToast(
                    getActivity().getApplicationContext(),
                    R.string.cloud_close_succ);
            // Log.e("Alarm",
            // "after func_motion_enabled:"+func_motion_enabled);
        } else if (func_cloud_enabled == 0) {
            // 关闭--->打开
            if (func_cloud_enabled == destFlag) {
                Log.e("Alarm", "middle Don't need to change");
                return;
            }
            func_cloud_enabled = 1;
            func_cloudsw
                    .setBackgroundResource(R.drawable.morefragment_selector_icon);
            // String text = getResources().getString(
            // R.string.str_mdenabled_open_ok);
            // Toast.makeText(getActivity(), text,
            // Toast.LENGTH_SHORT)
            // .show();
            showTextToast(
                    getActivity().getApplicationContext(),
                    R.string.cloud_open_succ);
        } else {
            // 隐藏
            // String text = getResources().getString(
            // R.string.str_operation_failed);
            // Toast.makeText(getActivity(), text,
            // Toast.LENGTH_SHORT)
            // .show();
            showTextToast(getActivity(),
                    R.string.str_operation_failed);
        }
    }
}
