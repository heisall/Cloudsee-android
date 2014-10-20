package com.jovision.activities;

import com.jovetech.CloudSee.temp.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AddThirdDevActivity extends FragmentActivity implements
		OnClickListener, OnDeviceClassSelectedListener {
	private AddThirdDeviceMenuFragment third_dev_menu_fragment;
	private Button backBtn;
	public TextView titleTv;
	private String strYstNum;
	private boolean bConnectedFlag;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.add_third_device_main);
		Bundle extras = getIntent().getExtras();
		strYstNum = extras.getString("dev_num");	
		bConnectedFlag = extras.getBoolean("conn_flag");
		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}
			third_dev_menu_fragment = new AddThirdDeviceMenuFragment();
			Bundle bundle1 = new Bundle();  
	        bundle1.putString("yst_num", strYstNum);  
	        bundle1.putBoolean("conn_flag", bConnectedFlag);
	        third_dev_menu_fragment.setArguments(bundle1);  			
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.add(R.id.fragment_container, third_dev_menu_fragment)
					.commitAllowingStateLoss();
		}
		InitViews();
	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void InitViews() {
		backBtn = (Button) findViewById(R.id.back);
		titleTv = (TextView) findViewById(R.id.currentmenu);

		backBtn.setBackgroundResource(R.drawable.back);
		backBtn.setOnClickListener(this);
		titleTv.setText(R.string.str_help1_1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void OnDeviceClassSelected(int index) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0:
			titleTv.setText(R.string.str_door_device);
			break;
		case 1:
			titleTv.setText(R.string.str_bracelet_device);
			break;
		default:
			break;
		}
	}
}
