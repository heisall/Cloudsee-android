package com.jovision.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;
import com.jovision.commons.MyActivityManager;
import com.jovision.utils.ConfigUtil;

public class JVTimeZoneActivity extends BaseActivity {
	private Button back;
	private TextView currentMenu;
	private TextView currentTimeZone;
	private ListView selectListView;
	private SimpleAdapter adapter;
	private ArrayList<Map<String, Object>> content = new ArrayList<Map<String, Object>>();
	private int currentZone;
	private Button rightButton;
	private ProgressBar timezone_progress;
	private int window;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.CALL_TEXT_DATA: {// 文本回调
			switch (arg2) {
			case JVNetConst.JVN_RSP_TEXTDATA:// 文本数据
				String allStr = obj.toString();
				try {
					JSONObject dataObj = new JSONObject(allStr);
					switch (dataObj.getInt("flag")) {
					case JVNetConst.JVN_STREAM_INFO:// 3-- 码流配置请求
						// MyLog.i(TAG, "码流配置请求--" + obj.toString());
						String pnJSON = dataObj.getString("msg");
						HashMap<String, String> pnMap = ConfigUtil
								.genMsgMap(pnJSON);
						if (null != pnMap.get("timezone")) {
							timezone_progress.setVisibility(View.GONE);
							int index = Integer.valueOf(pnMap.get("timezone"));
							currentTimeZone
									.setText(getResources().getStringArray(
											R.array.time_zone)[12 - index]);
						}
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		MyActivityManager.getActivityManager().pushActivity(
				JVTimeZoneActivity.this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.time_zone_layout);
		Bundle extras = getIntent().getExtras();
		if (null != extras) {
			window = extras.getInt("window");
		}
		Jni.sendTextData(window, JVNetConst.JVN_RSP_TEXTDATA, 8,
				JVNetConst.JVN_STREAM_INFO);
	}

	@Override
	protected void initUi() {
		timezone_progress = (ProgressBar) findViewById(R.id.timezone_progress);
		rightButton = (Button) findViewById(R.id.btn_right);
		rightButton.setVisibility(View.GONE);
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(getResources().getString(R.string.time_zone));
		currentTimeZone = (TextView) findViewById(R.id.current_time_zone);
		selectListView = (ListView) findViewById(R.id.select_time_zone);
		String[] array = getResources().getStringArray(R.array.time_zone);
		currentTimeZone.setText(array[12 - currentZone]);
		for (int i = 0; i < 25; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", array[i]);
			content.add(map);
		}
		adapter = new SimpleAdapter(this,
				(ArrayList<Map<String, Object>>) content,
				R.layout.select_time_item, new String[] { "time" },
				new int[] { R.id.time_zone_item });
		selectListView.setAdapter(adapter);
		selectListView.setOnItemClickListener(onItemClickListener);
		back.setOnClickListener(myOnClickListener);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			default:
				break;
			}
		}
	};
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			timezone_progress.setVisibility(View.VISIBLE);
			final int itemIndex = arg2;
			new Thread() {
				public void run() {
					try {
						Thread.sleep(200);
						String zoneParam = "timezone=" + (12 - itemIndex)
								+ ";bSntp=1";
						Jni.sendString(window, JVNetConst.JVN_RSP_TEXTDATA,
								false, 0, JVNetConst.RC_SETPARAM, zoneParam);
						Thread.sleep(200);
						Jni.sendTextData(window, JVNetConst.JVN_RSP_TEXTDATA,
								8, JVNetConst.JVN_STREAM_INFO);
					} catch (Exception e) {
					}
				};
			}.start();
		};
	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	@Override
	protected void onDestroy() {
		MyActivityManager.getActivityManager().popActivity(
				JVTimeZoneActivity.this);
		super.onDestroy();
	}

}
