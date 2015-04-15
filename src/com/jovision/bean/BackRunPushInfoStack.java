
package com.jovision.bean;

import java.util.ArrayList;

public class BackRunPushInfoStack {
    private static BackRunPushInfoStack instance_ = new BackRunPushInfoStack();
    private ArrayList<PushInfo> backPList_ = null;

    public static BackRunPushInfoStack getInstance() {
        return instance_;
    }

    /**
     * private的构造函数用于避免外界直接使用new来实例化对象
     */
    private BackRunPushInfoStack() {
        backPList_ = new ArrayList<PushInfo>();
    }

    /* 获取链表尾部的对象，并移除 */
    public PushInfo pop() {
        synchronized (backPList_) {
            if (backPList_.size() == 0) {
                return null;
            }
            int index = backPList_.size() - 1;
            PushInfo popInfo = backPList_.get(index);
            backPList_.remove(index);
            return popInfo;
        }
    }

    /* 将对象压入链表 */
    public void push(PushInfo item) {
        synchronized (backPList_) {
            backPList_.add(item);
        }
    }

    public int size() {
        synchronized (backPList_) {
            return backPList_.size();
        }
    }

    public void clear() {
        synchronized (backPList_) {
            backPList_.clear();
        }
    }
}
