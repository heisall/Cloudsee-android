
package com.jovision.bean;

public class OneKeyUpdate {
    // "ufver":"v10.2.1",
    // "ufurl":"http:\/\/60.216.75.26:9999\/jenkins\/job\/IPC\/ws\/H200a_up.bin",
    // "ufsize":2161088,
    // "ufc":"",
    // "ufdes":"10.2.1"}}
    private String ufver = "";
    private String ufurl = "";
    private int ufsize = 0;
    private String ufc = "";
    private String ufdes = "";
    private int resultCode = -1;

    public String getUfver() {
        return ufver;
    }

    public void setUfver(String ufver) {
        this.ufver = ufver;
    }

    public String getUfurl() {
        return ufurl;
    }

    public void setUfurl(String ufurl) {
        this.ufurl = ufurl;
    }

    // public String getUfsize() {
    // return ufsize;
    // }
    //
    // public void setUfsize(String ufsize) {
    // this.ufsize = ufsize;
    // }

    public String getUfc() {
        return ufc;
    }

    public void setUfc(String ufc) {
        this.ufc = ufc;
    }

    public String getUfdes() {
        return ufdes;
    }

    public void setUfdes(String ufdes) {
        this.ufdes = ufdes;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getUfsize() {
        return ufsize;
    }

    public void setUfsize(int ufsize) {
        this.ufsize = ufsize;
    }

}
