package com.jovision.activities;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.TestAdapter;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class TestActivity extends Activity {

	private ListView listView;
	private TestAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new TestAdapter(TestActivity.this);
		listView.setAdapter(adapter);
		initView();
	}

	private void initView() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					Intent intent = new Intent(TestActivity.this,
							AddThirdDevActivity.class);
					startActivity(intent);
					break;
				case 1:
					Intent intent1 = new Intent(TestActivity.this,
							CustomDialogActivity.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(TestActivity.this,
							JVAddDeviceActivity.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(TestActivity.this,
							JVAddIpDeviceActivity.class);
					startActivity(intent3);
					break;
				case 4:
					Intent intent4 = new Intent(TestActivity.this,
							JVBoundEmailActivity.class);
					startActivity(intent4);
					break;
				case 5:
					Intent intent5 = new Intent(TestActivity.this,
							JVChannelListActivity.class);
					startActivity(intent5);
					break;
				case 6:
					Intent intent6 = new Intent(TestActivity.this,
							JVChannelsActivity.class);
					startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(TestActivity.this,
							JVDemoActivity.class);
					startActivity(intent7);
					break;
				case 8:
					Intent intent8 = new Intent(TestActivity.this,
							JVDeviceManageActivity.class);
					startActivity(intent8);
					break;
				case 9:
					Intent intent9 = new Intent(TestActivity.this,
							JVEditOldPassActivity.class);
					startActivity(intent9);
					break;
				case 10:
					Intent intent10 = new Intent(TestActivity.this,
							JVEditOldUserInfoActivity.class);
					startActivity(intent10);
					break;
				case 11:
					Intent intent11 = new Intent(TestActivity.this,
							JVEditPassActivity.class);
					startActivity(intent11);
					break;
				case 12:
					Intent intent12 = new Intent(TestActivity.this,
							JVFeedbackActivity.class);
					startActivity(intent12);
					break;
				case 13:
					Intent intent13 = new Intent(TestActivity.this,
							JVFindPassActivity.class);
					startActivity(intent13);
					break;
				case 14:
					Intent intent14 = new Intent(TestActivity.this,
							JVGuideActivity.class);
					startActivity(intent14);
					break;
				case 15:
					Intent intent15 = new Intent(TestActivity.this,
							JVIpconnectActivity.class);
					startActivity(intent15);
					break;
				case 16:
					Intent intent16 = new Intent(TestActivity.this,
							JVLoginActivity.class);
					startActivity(intent16);
					break;
				case 17:
					Intent intent17 = new Intent(TestActivity.this,
							JVOffLineDialogActivity.class);
					startActivity(intent17);
					break;
				case 18:
					Intent intent18 = new Intent(TestActivity.this,
							JVPlayActivity.class);
					startActivity(intent18);
					break;
				case 19:
					Intent intent19 = new Intent(TestActivity.this,
							JVQuickSettingActivity.class);
					startActivity(intent19);
					break;
				case 20:
					Intent intent20 = new Intent(TestActivity.this,
							JVQuickSettingActivity1.class);
					startActivity(intent20);
					break;
				case 21:
					Intent intent21 = new Intent(TestActivity.this,
							JVRegisterActivity.class);
					startActivity(intent21);
					break;
				case 22:
					Intent intent22 = new Intent(TestActivity.this,
							JVRemoteListActivity.class);
					startActivity(intent22);
					break;
				case 23:
					Intent intent23 = new Intent(TestActivity.this,
							JVRemotePlay.class);
					startActivity(intent23);
					break;
				case 24:
					Intent intent24 = new Intent(TestActivity.this,
							JVRemotePlayBackActivity.class);
					startActivity(intent24);
					break;
				case 25:
					Intent intent25 = new Intent(TestActivity.this,
							JVRemoteSettingActivity.class);
					startActivity(intent25);
					break;
				case 27:
					Intent intent27 = new Intent(TestActivity.this,
							JVVideoActivity.class);
					startActivity(intent27);
					break;
				case 28:
					Intent intent28 = new Intent(TestActivity.this,
							JVWelcomeActivity.class);
					startActivity(intent28);
					break;
				case 29:
					Intent intent29 = new Intent(TestActivity.this,
							PlayActivity.class);
					startActivity(intent29);
					break;
				case 30:
					Intent intent30 = new Intent(TestActivity.this,
							ThirdDevListActivity.class);
					startActivity(intent30);
					break;
				default:
					break;
				}
			}
		});
	}
}
