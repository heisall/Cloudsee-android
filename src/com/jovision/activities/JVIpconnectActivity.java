package com.jovision.activities;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.Rotate3dUtil;

public class JVIpconnectActivity extends BaseActivity {
	// ip连接形式的RadioButton
	private RadioButton ipconnnect_ip;
	// 云视通号连接形式的RadioButton
	private RadioButton ipconnnect_cloud;
	// 输入ip地址的布局
	private LinearLayout addressLayout;
	// 输入云视通号的布局
	private LinearLayout couldnumLayout;
	// 输入接口的布局
	private LinearLayout portLayout;
	// 输入ip地址的edittext
	private EditText ipconnect_address;
	// 输入端口号的edittext
	private EditText ipconnect_port;
	// 输入用户名的edittext
	private EditText ipconnect_user;
	// 输入密码的edittext
	private EditText ipconnect_pwd;
	// 保存按钮
	private Button editsave;
	// 旋转的外层包裹布局
	private LinearLayout mContainer;
	// 更改形式的标志位
	private boolean isturn = false;
	// 返回按钮
	private Button back;
	// 添加按钮
	private Button plus_btu;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.ipconnect_layout);

		mContainer = (LinearLayout) findViewById(R.id.mContainer);
		ipconnnect_ip = (RadioButton) findViewById(R.id.ipconnect_ip);
		ipconnnect_cloud = (RadioButton) findViewById(R.id.ipconnect_cloud);
		addressLayout = (LinearLayout) findViewById(R.id.Addresslayout);
		couldnumLayout = (LinearLayout) findViewById(R.id.NumberLayout);
		portLayout = (LinearLayout) findViewById(R.id.portlayout);
		ipconnect_address = (EditText) findViewById(R.id.ipconnnect_address);
		ipconnect_port = (EditText) findViewById(R.id.ipconnect_port);
		ipconnect_user = (EditText) findViewById(R.id.ipconnect_username);
		ipconnect_pwd = (EditText) findViewById(R.id.ipconnect_pwd);
		editsave = (Button) findViewById(R.id.editsave);
		back = (Button) findViewById(R.id.btn_left);
		plus_btu = (Button) findViewById(R.id.btn_right);

		back.setOnClickListener(myOnClickListener);
		plus_btu.setOnClickListener(myOnClickListener);
		ipconnnect_ip.setOnClickListener(myOnClickListener);
		ipconnnect_cloud.setOnClickListener(myOnClickListener);
		editsave.setOnClickListener(myOnClickListener);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.ipconnect_ip:
				if (isturn) {
					applyRotation(0, 0, 90);
					isturn = false;
				}
				break;
			case R.id.ipconnect_cloud:
				if (!isturn) {
					applyRotation(0, 0, 90);
					isturn = true;
				}
				break;
			case R.id.editsave:

				break;
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	// 设置旋转中心点，开始和结束位置
	private void applyRotation(int position, float start, float end) {
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;
		final Rotate3dUtil rotation = new Rotate3dUtil(start, end, centerX,
				centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(position));
		mContainer.startAnimation(rotation);
	}

	// 动画 监听
	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mContainer.post(new SwapViews(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	// 动画旋转显示或隐藏的布局
	private final class SwapViews implements Runnable {
		public SwapViews(int position) {
		}

		public void run() {
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			Rotate3dUtil rotation;
			rotation = new Rotate3dUtil(270, 360, centerX, centerY, 310.0f,
					false);
			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			mContainer.startAnimation(rotation);
			if (!isturn) {
				addressLayout.setVisibility(View.VISIBLE);
				couldnumLayout.setVisibility(View.GONE);
				portLayout.setVisibility(View.VISIBLE);
			} else {
				addressLayout.setVisibility(View.GONE);
				couldnumLayout.setVisibility(View.VISIBLE);
				portLayout.setVisibility(View.GONE);
			}
		}
	}
}
