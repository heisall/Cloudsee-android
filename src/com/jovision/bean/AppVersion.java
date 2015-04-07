
package com.jovision.bean;

public class AppVersion {
    // appver":88,"appfullver":"V4.5.9",
    // "appverurl":"http://www.baidu.com","appverdesc":"升级信息"}
    private int versionCode = 0;
    private String versionName = "";
    private String downloadUrl = "";
    private String versionInfo = "";

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

}
