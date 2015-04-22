
package com.jovision.bean;

public class AlarmSettingsItemBean {
    private boolean _isTag;
    private String _title;
    private String _tips;
    private int _switch;
    private boolean _enabled;

    public boolean getIsTag() {
        return _isTag;
    }

    public String getTitle() {
        return _title == null ? "" : _title;
    }

    public String getTips() {
        return _tips == null ? "" : _tips;
    }

    public int getSwitch() {
        return _switch;
    }

    public boolean getEnabled() {
        return _enabled;
    }

    public void setIsTag(boolean isTag) {
        this._isTag = isTag;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public void setTips(String tips) {
        this._tips = tips;
    }

    public void setSwitch(int value) {
        this._switch = value;
    }

    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
    }

}
