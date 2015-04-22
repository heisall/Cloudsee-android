
package com.jovision.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;
import com.jovision.views.CustomDialog;

//import com.jovision.adapters.PeripheralManageAdapter;

public class AddThirdDeviceMenuFragment extends Fragment implements
        AddThirdDevActivity.OnMainListener {
    private View rootView;// 缓存Fragment view
    private GridView manageGridView;
    // private PeripheralManageAdapter manageAdapter;
    DisplayMetrics disMetrics;
    private WebView mWebView;
    private MyHandler myHandler;
    private String mDevType = "";
    private LinearLayout loadinglayout;
    private ImageView loadingBar;
    private boolean loadFailed = false;
    private CustomDialog learnDialg;
    private RelativeLayout loadFailedLayout;
    private ImageView reloadImgView;

    public interface OnDeviceClassSelectedListener {
        public void OnDeviceClassSelected(int index, String paras);
    }

    private String webUrlZH = "http://182.92.242.230:8081/device.html?lan=ch";
    private String webUrlTW = "http://182.92.242.230:8081/device.html?lan=tw";
    private String webUrlEN = "http://182.92.242.230:8081/device.html?lan=en";

    private OnDeviceClassSelectedListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDeviceClassSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnDeviceClassSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.web_add_exdev_menu_layout,
                    container, false);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        myHandler = new MyHandler();
        mWebView = (WebView) rootView.findViewById(R.id.webview);
        // learnDialg = new CustomDialog(getActivity(), mListener);
        loadingBar = (ImageView) rootView.findViewById(R.id.loadingbar);
        loadinglayout = (LinearLayout) rootView
                .findViewById(R.id.loadinglayout);

        loadFailedLayout = (RelativeLayout) rootView
                .findViewById(R.id.loadfailedlayout);
        loadFailedLayout.setVisibility(View.GONE);
        reloadImgView = (ImageView) rootView.findViewById(R.id.refreshimg);
        reloadImgView.setOnClickListener(myOnClickListener);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // webSettings.setBuiltInZoomControls(true);
        // webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // webSettings.setUseWideViewPort(true);
        // webSettings.setLoadWithOverviewMode(true);
        // webSettings.setSaveFormData(true);
        // webSettings.setGeolocationEnabled(true);
        mWebView.requestFocus();
        mWebView.setScrollBarStyle(0);
        mWebView.setWebChromeClient(m_chromeClient);

        if (ConfigUtil.getLanguage2(getActivity()) == Consts.LANGUAGE_ZH) {
            mWebView.loadUrl(webUrlZH);
        } else if (ConfigUtil.getLanguage2(getActivity()) == Consts.LANGUAGE_ZHTW) {
            mWebView.loadUrl(webUrlTW);
        } else {
            mWebView.loadUrl(webUrlEN);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                Log.e("webv", "webView load failed");
                loadFailed = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
                // 重载

                Log.e("webv", "newUrl:" + newUrl);
                if (newUrl.contains("device=")) {
                    if (ConfigUtil.getLanguage2(getActivity()) == Consts.LANGUAGE_ZH) {
                        view.loadUrl(webUrlZH);
                    } else if (ConfigUtil.getLanguage2(getActivity()) == Consts.LANGUAGE_ZHTW) {
                        view.loadUrl(webUrlTW);
                    } else {
                        view.loadUrl(webUrlEN);
                    }
                    learnDialg = new CustomDialog(getActivity(), mListener);
                    learnDialg.Show(0, 0, newUrl);
                    return false;
                } else {
                    return true;
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("device=")) {
                    return;
                } else {
                    loadinglayout.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(
                            getActivity(), R.anim.rotate);
                    loadingBar.setAnimation(anim);
                    Log.e("webv", "webView start load");
                }

                // mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("device=")) {
                    return;
                } else {
                    if (loadFailed) {
                        loadFailedLayout.setVisibility(View.VISIBLE);
                        loadinglayout.setVisibility(View.GONE);
                        Log.e("webv", "url:" + url + " load failed");
                    } else {
                        loadinglayout.setVisibility(View.GONE);
                        loadFailedLayout.setVisibility(View.GONE);
                    }
                    Log.e("webv", "webView finish load");
                }

                // else {
                // if (url.contains("device=")) {
                //
                // String param_array[] = url.split("\\?");
                // HashMap<String, String> resMap;
                // resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);
                //
                // mDevType = resMap.get("device");
                // if (mDevType != null && !mDevType.equals("")) {
                // mListener.OnDeviceClassSelected(Integer
                // .parseInt(mDevType), "");
                // }
                // }
                // }

            }
        });

        // manageGridView = (GridView) rootView
        // .findViewById(R.id.third_alarm_gridview);
        // disMetrics = new DisplayMetrics();
        // getActivity().getWindowManager().getDefaultDisplay()
        // .getMetrics(disMetrics);
        // manageAdapter = new PeripheralManageAdapter(this);
        // manageAdapter.SetData(disMetrics.widthPixels);
        // manageGridView.setAdapter(manageAdapter);
        // manageAdapter.notifyDataSetChanged();

        return rootView;
    }

    private WebChromeClient m_chromeClient = new WebChromeClient() {
        // 扩充缓存的容量
        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded,
                long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(spaceNeeded * 2);
        }
    };

    OnClickListener myOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.refreshimg: {
                    loadFailedLayout.setVisibility(View.GONE);
                    loadinglayout.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.rotate);
                    loadingBar.setAnimation(anim);
                    loadFailed = false;
                    if (mWebView.getUrl().contains("device=")) {
                        if (ConfigUtil.getLanguage2(getActivity()) == Consts.LANGUAGE_ZH) {
                            mWebView.loadUrl(webUrlZH);
                        } else if (ConfigUtil.getLanguage2(getActivity()) == Consts.LANGUAGE_ZHTW) {
                            mWebView.loadUrl(webUrlTW);
                        } else {
                            mWebView.loadUrl(webUrlEN);
                        }
                    } else {
                        mWebView.reload();
                    }

                    break;
                }
                default:
                    break;
            }
        }
    };

    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // if (rootView == null) {
    // rootView = inflater.inflate(
    // R.layout.new_add_thirddev_menu_fragment, container, false);
    // }
    // ViewGroup parent = (ViewGroup) rootView.getParent();
    // if (parent != null) {
    // parent.removeView(rootView);
    // }
    // myHandler = new MyHandler();
    // manageGridView = (GridView) rootView
    // .findViewById(R.id.third_alarm_gridview);
    // disMetrics = new DisplayMetrics();
    // getActivity().getWindowManager().getDefaultDisplay()
    // .getMetrics(disMetrics);
    // manageAdapter = new PeripheralManageAdapter(this);
    // manageAdapter.SetData(disMetrics.widthPixels);
    // manageGridView.setAdapter(manageAdapter);
    // manageAdapter.notifyDataSetChanged();
    // // doorBtn = (Button) rootView.findViewById(R.id.add_door_btn);
    // // doorBtn.setOnClickListener(this);
    // // braceletBtn = (Button) rootView.findViewById(R.id.add_bracelet_btn);
    // // braceletBtn.setOnClickListener(this);
    // // telecontrolBtn = (Button) rootView
    // // .findViewById(R.id.add_telecontrol_btn);
    // // telecontrolBtn.setOnClickListener(this);
    //
    // return rootView;
    // }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            MyLog.e("PERIP", "what:" + msg.what + ", arg1:" + msg.arg1);
            switch (msg.what) {
                case Consts.WHAT_PERI_ITEM_CLICK: {
                    // arg1:外设功能编号从1开始
                    // 1:门磁设备 2:手环设备 3:遥控 4:烟感 5:幕帘 6:红外探测器 7:燃气泄露
                    mListener.OnDeviceClassSelected(msg.arg1, "");
                }
                    break;
                default:
                    break;
            }
        }
    }

    public void MyOnNotify(int what, int arg1, int arg2, Object obj) {
        Message msg = myHandler.obtainMessage(what, arg1, arg2, obj);
        myHandler.sendMessage(msg);
    }

    @Override
    public void onBindResult(int ret, String paras) {
        // TODO Auto-generated method stub
        if (null != learnDialg && learnDialg.isShowing()) {
            learnDialg.dismiss();
        }
    }
}
