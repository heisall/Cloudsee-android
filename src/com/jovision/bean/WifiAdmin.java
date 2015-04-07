
package com.jovision.bean;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;

import com.jovision.Consts;
import com.jovision.commons.MyLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiAdmin {
    private final String TAG = "WifiAdmin";

    private ConnectivityManager connectivityManager;

    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private ArrayList<ScanResult> mWifiList;
    // 配置过的网络连接列表
    private ArrayList<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiLock mWifiLock;

    Context mContext;

    // 构造器
    public WifiAdmin(Context context) {
        mContext = context;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 获取wifi状态
    public boolean getWifiState() {
        if (null != mWifiManager) {
            return mWifiManager.isWifiEnabled();
        }
        return false;
    }

    // 打开WIFI
    public boolean openWifi() {
        boolean flag = false;
        if (!mWifiManager.isWifiEnabled()) {
            flag = mWifiManager.setWifiEnabled(true);
        } else {
            flag = true;
        }
        return flag;
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // // 得到配置好的网络
    // public List<WifiConfiguration> getConfiguration() {
    // return mWifiConfiguration;
    // }
    //
    // // 指定配置好的网络进行连接
    // public boolean connectConfiguration(String wifiName, String wifipwd) {
    // boolean flag = false;
    //
    // if (null != mWifiConfiguration && mWifiConfiguration.size() > 0) {
    // for (int i = 0; i < mWifiConfiguration.size(); i++) {
    // if (wifiName.equalsIgnoreCase(mWifiConfiguration.get(i).SSID)) {
    // // mWifiConfiguration.get(i).preSharedKey
    // flag = mWifiManager.enableNetwork(
    // mWifiConfiguration.get(i).networkId, true);
    // }
    // }
    // }
    // return flag;
    // }

    public boolean ConnectWifiByConfig(WifiConfiguration wifiConfiguration) {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return false;
        }
        boolean flag = false;
        WifiConfiguration wc = isExsits(wifiConfiguration.SSID);

        if (null == wc) {// 未配置过的网络
            // Log.v("添加连接的新网络", wifiConfiguration.SSID + "");
            flag = addNetwork(wifiConfiguration);
        } else {// 已配置过的网络,使连接可用
            // Log.v(" 已配置过的网络,使连接可用", wifiConfiguration.SSID + "");
            flag = connNetwork(wc);
        }

        // Log.v("连接结果", flag + "");
        return flag;
    }

    public ArrayList<ScanResult> startScanIPC() {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return null;
        }
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = (ArrayList<ScanResult>) mWifiManager.getScanResults();
        // // 得到配置好的网络连接
        // mWifiConfiguration = (ArrayList<WifiConfiguration>) mWifiManager
        // .getConfiguredNetworks();

        // 只取出IPC路由
        if (null != mWifiList) {
            int size = mWifiList.size();
            for (int i = 0; i < size; i++) {
                String name = mWifiList.get(i).SSID;
                // .replace("\"", "");

                if (!name.startsWith(Consts.IPC_FLAG)) {
                    mWifiList.remove(i);
                    i--;
                    size = mWifiList.size();
                }
            }
        }

        return mWifiList;
    }

    public ArrayList<ScanResult> startScanWifi() {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return null;
        }
        try {
            mWifiManager.startScan();
            // 得到扫描结果
            mWifiList = (ArrayList<ScanResult>) mWifiManager.getScanResults();
            // // 得到配置好的网络连接
            // mWifiConfiguration = (ArrayList<WifiConfiguration>) mWifiManager
            // .getConfiguredNetworks();
            // 只取出非IPC路由
            if (null != mWifiList) {
                int size = mWifiList.size();
                for (int i = 0; i < size; i++) {
                    // MyLog.v("wifi--Name--" + size, mWifiList.get(i).SSID);

                    String name = mWifiList.get(i).SSID;

                    if (name.startsWith(Consts.IPC_FLAG)
                            || name.equalsIgnoreCase("")) {
                        mWifiList.remove(i);
                        i--;
                        size = mWifiList.size();
                    }

                    if (name.equalsIgnoreCase(Consts.IPC_FLAG)) {
                        ScanResult sr = mWifiList.get(i);
                        mWifiList.remove(i);
                        mWifiList.add(0, sr);
                    }
                }
            }
            mWifiList = removeDup(mWifiList);

            // int size = mWifiList.size();
            // for (int i = 0; i < size; i++) {
            // MyLog.v("wifi--Name--" + size, mWifiList.get(i).SSID);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mWifiList;
    }

    // public ArrayList<ScanResult>
    // removeDuplicateWithOrder(ArrayList<ScanResult> sourceList) {
    // ArrayList<ScanResult> resultList = new ArrayList<ScanResult>();
    // for(ScanResult sr: sourceList){
    // if(Collections.frequency(resultList, sr) < 1)
    // resultList.add(sr);
    // }
    // return resultList;
    // }

    public ArrayList<ScanResult> removeDup(ArrayList<ScanResult> sourceList) {

        for (int i = 0; i < sourceList.size() - 1; i++) {
            for (int j = sourceList.size() - 1; j > i; j--) {
                if (sourceList.get(j).SSID
                        .equalsIgnoreCase(sourceList.get(i).SSID)) {
                    sourceList.remove(j);
                }
            }
        }
        return sourceList; // 返回集合
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到接入点的SSID
    public String getSSID() {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return null;
        }
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return false;
        }

        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        return b;
    }

    // 连接一个网络
    public boolean connNetwork(WifiConfiguration wcg) {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return false;
        }
        int wcgID = wcg.networkId;
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        return b;
    }

    // 断开指定ID的网络
    public void disconnectWifi(WifiConfiguration wifiConfiguration,
            boolean remove) {
        if (!getWifiState()) {// 如果wifi为关闭状态
            return;
        }

        int netId = -1;

        WifiConfiguration wc = isExsits(wifiConfiguration.SSID);
        if (null == wc) {
            return;
        } else {
            netId = wc.networkId;
        }

        // MyLog.v("断开的网络SSID---", "SSID--" + wifiConfiguration.SSID);
        // MyLog.v("断开的网络ID---", "netId--" + netId);
        try {
            if (null == mWifiManager) {
                // 取得WifiManager对象
                mWifiManager = (WifiManager) mContext
                        .getSystemService(Context.WIFI_SERVICE);
                // 取得WifiInfo对象
                mWifiInfo = mWifiManager.getConnectionInfo();
            }
            mWifiManager.disableNetwork(netId);
            // mWifiManager.disconnect();
            if (remove) {
                mWifiManager.removeNetwork(netId);// 移除网络
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 分为三种情况：1没有密码2用wep加密3用wpa加密
    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
            int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        WifiConfiguration tempConfig = isExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) {
            config.wepKeys[0] = "\"" + "" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            config.hiddenSSID = true;

        }
        if (Type == 2) {

            // WifiConfiguration wifiConfig = new WifiConfiguration();
            // wifiConfig.SSID = String.format("\"%s\"", ssid);
            // wifiConfig.preSharedKey = String.format("\"%s\"", key);
            // WifiManager wifiManager =
            // (WifiManager)this.getSystemService(WIFI_SERVICE);
            // int netId = wifiManager.addNetwork(wifiConfig)
            // wifiManager.disconnect();
            // wifiManager.enableNetwork(netId, true);
            // wifiManager.reconnect();

            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.preSharedKey = String.format("\"%s\"", Password);
            // config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            // config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

        }
        if (Type == 3) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    public WifiConfiguration isExsits(String SSID) {
        try {
            if (null == SSID) {
                return null;
            }
            if (!getWifiState()) {// 如果wifi为关闭状态
                return null;
            }

            // MyLog.v("连接的网络：", SSID + "");

            if (null == mWifiManager) {
                // 取得WifiManager对象
                mWifiManager = (WifiManager) mContext
                        .getSystemService(Context.WIFI_SERVICE);
                // 取得WifiInfo对象
                mWifiInfo = mWifiManager.getConnectionInfo();
            }

            List<WifiConfiguration> existingConfigs = mWifiManager
                    .getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                // Log.v("已配置过的网络SSID：", existingConfig.SSID + "");
                // Log.v("已配置过的网络netID：", existingConfig.networkId + "");

                String str1 = existingConfig.SSID.replace("\"", "");
                String str2 = SSID.replace("\"", "");
                if (str1.equals(str2)) {
                    // MyLog.v("找到了：", existingConfig.SSID + "");
                    return existingConfig;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) {
            mWifiManager.setWifiEnabled(false);
        }

        try {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "NETGEAR49-2G";
            config.preSharedKey = "37DD5A0741";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);

            return (Boolean) method.invoke(mWifiManager, config, enabled);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }

    }

    public int[] getWifiAuthEnc(String targetSSID) {
        int result[] = null;

        if (TextUtils.isEmpty(targetSSID)) {
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (null != info) {
                targetSSID = info.getSSID();
            }
        }

        if (false == TextUtils.isEmpty(targetSSID)) {
            result = new int[2];
            result[0] = 0;
            result[1] = 0;

            // [Neo] Open?
            final String[] auth = {
                    "NONE", "NEO", "NEO", "WPA-PSK",
                    "WPA2-PSK", "NEO"
            };
            final String[] enc = {
                    "NONE", "WEP", "TKIP", "CCMP"
            };

            ArrayList<ScanResult> results = (ArrayList<ScanResult>) mWifiManager
                    .getScanResults();
            for (ScanResult item : results) {
                if (item.SSID.equals(targetSSID)) {
                    boolean isFound = false;
                    String combines[] = item.capabilities.replace("[", "")
                            .split("]");

                    int size = combines.length;
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < 6; j++) {
                            // System.out.println("auth: " + j);
                            if (combines[i].contains(auth[j])) {
                                result[0] = j;
                                isFound = true;
                                break;
                            }
                        }

                        for (int j = 0; j < 4; j++) {
                            // System.out.println("enc: " + j);
                            if (combines[i].contains(enc[j])) {
                                result[1] = j;
                                break;
                            }
                        }

                        // System.out.println("combine: " + combines[i]);

                        if (isFound) {
                            // break;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

    // /**
    // * 切换到原来网络
    // *
    // * @param disWifi
    // * @param connWifi
    // * @return
    // */
    // public boolean changeWifi(String disWifi, String connWifi,
    // boolean oldWifiState) {
    //
    // boolean changeRes = false;
    // try {
    // // 断开跟连接的wifi 一样不做任何处理
    // if (null != disWifi && !"".equalsIgnoreCase(disWifi)
    // && null != connWifi && !"".equalsIgnoreCase(connWifi)
    // && disWifi.equalsIgnoreCase(connWifi)) {
    // changeRes = true;
    // return changeRes;
    // } else {
    // if (oldWifiState) {// 原wifi开着的，恢复到原来的网络
    // // 断开现在的wifi
    // if (null != disWifi && !"".equalsIgnoreCase(disWifi)) {
    // WifiConfiguration currWifi = isExsits(disWifi);
    // if (null != currWifi) {
    // disconnectWifi(currWifi, true);
    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // // 完成配置连上原来的wifi
    // if (null != connWifi) {
    // WifiConfiguration oldWifi = isExsits(connWifi);
    // if (null != oldWifi) {
    // boolean connRes = false;
    // int count = 0;
    // while (!connRes) {// 没连接调用连接方法
    // if (count < 10) {
    // count++;
    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // connRes = connNetwork(oldWifi);
    // } else {
    // connRes = true;
    // break;
    // }
    // }
    //
    // count = 0;
    // if (connRes) {// 已连接
    // while (!changeRes) {// 没连接调用连接方法
    // if (count < 20) {
    // count++;
    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // changeRes = getWifiState(oldWifi.SSID);
    // } else {
    // changeRes = false;
    // break;
    // }
    //
    // }
    // } else {
    // changeRes = false;
    // }
    //
    // }
    // } else {
    // changeRes = true;
    // }
    // } else {// 原wifi关闭状态，关闭wifi
    // closeWifi();
    // changeRes = true;
    // }
    // }
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    //
    // return changeRes;
    //
    // }

    /**
     * 切换到原来网络
     * 
     * @param disWifi
     * @param connWifi
     * @return
     */
    public boolean changeWifi(String disWifi, String connWifi,
            boolean oldWifiState) {
        oldWifiState = true;
        boolean state = getWifiState(connWifi);

        if (state) {
            return state;
        }

        MyLog.v(TAG, "changeWifi-E:" + disWifi + "-" + connWifi);
        boolean changeRes = false;
        try {
            // 断开跟连接的wifi 一样不做任何处理
            if (null != disWifi && !"".equalsIgnoreCase(disWifi)
                    && null != connWifi && !"".equalsIgnoreCase(connWifi)
                    && disWifi.equalsIgnoreCase(connWifi)) {
                changeRes = true;
                return changeRes;
            } else {
                if (oldWifiState) {// 原wifi开着的，恢复到原来的网络
                    // 断开现在的wifi
                    if (null != disWifi && !"".equalsIgnoreCase(disWifi)) {
                        WifiConfiguration currWifi = isExsits(disWifi);
                        if (null != currWifi) {
                            MyLog.v("完成配置断开", disWifi);
                            disconnectWifi(currWifi, true);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    // 完成配置连上原来的wifi
                    if (null != connWifi) {
                        WifiConfiguration oldWifi = isExsits(connWifi);
                        if (null != oldWifi) {
                            MyLog.v("完成配置连接", connWifi);
                            boolean connRes = false;
                            int count = 0;
                            while (!connRes) {// 没连接调用连接方法
                                if (count < 10) {
                                    count++;
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    connRes = connNetwork(oldWifi);
                                    MyLog.v("完成配置", connWifi + "----" + count
                                            + "-----调用连接-----" + connRes);
                                } else {
                                    connRes = true;
                                    break;
                                }
                            }

                            count = 0;
                            if (connRes) {// 已连接
                                while (!changeRes) {// 没连接调用连接方法
                                    if (count < 20) {
                                        count++;
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        changeRes = getWifiState(oldWifi.SSID);
                                        MyLog.v("完成配置", connWifi + "----"
                                                + count + "-----连接结果-----"
                                                + changeRes);
                                    } else {
                                        changeRes = false;
                                        break;
                                    }

                                }
                            } else {
                                changeRes = false;
                            }

                        }
                    } else {
                        changeRes = true;
                    }
                } else {// 原wifi关闭状态，关闭wifi
                    closeWifi();
                    changeRes = true;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        MyLog.v(TAG, "changeWifi-X:" + changeRes);
        return changeRes;

    }

    /**
     * 判断网络连接状态，wifiadmin不准确
     * 
     * @param wifiName
     * @return
     */
    public boolean getWifiState(String wifiName) {
        boolean flag = false;
        try {
            if (null == connectivityManager) {
                connectivityManager = (ConnectivityManager) mContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            NetworkInfo net = connectivityManager.getActiveNetworkInfo();
            if (net == null) {
            } else {
                if ("CONNECTED".equalsIgnoreCase(net.getState().name())) {
                    if (null != this.getSSID()
                            && !"".equalsIgnoreCase(this.getSSID().trim())) {
                        String str1 = this.getSSID().replace("\"", "");
                        String str2 = wifiName.replace("\"", "");

                        if (str1.equalsIgnoreCase(str2)) {
                            flag = true;
                        } else {
                            WifiConfiguration oldWifi = isExsits(str1);
                            if (null != oldWifi) {
                                disconnectWifi(oldWifi, false);// 关掉，不移除
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
