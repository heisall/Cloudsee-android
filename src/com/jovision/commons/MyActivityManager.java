package com.jovision.commons;

import java.util.Stack;

import android.app.Activity;

public class MyActivityManager {
	private static Stack<Activity> activityStack;
	private static MyActivityManager instance;

	private MyActivityManager() {

	}

	public static MyActivityManager getActivityManager() {
		if (instance == null) {
			instance = new MyActivityManager();
		}
		return instance;
	}

	// 退出栈顶Activity
	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	// 获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = null;
		if (null != activityStack && 0 != activityStack.size()) {
			activity = activityStack.lastElement();
		}
		return activity;
	}

	// 将当前Activity推入栈中
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	// 退出栈中所有Activity
	public void popAllActivityExceptOne(Class cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (null != cls && activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	// 退出栈中所有Activity
	public void popThisActivity(Class cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (null != cls && activity.getClass().equals(cls)) {
				popActivity(activity);
				break;
			}

		}
	}
}