package com.jovision.views;

import com.jovetech.CloudSee.temp.R;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

public class CustomDialog extends Dialog {
	private ImageView img_guide;
	private int img_res_id;

	public CustomDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.guide_custom_dialog_layout);
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void onStart() {
		img_guide = (ImageView) findViewById(R.id.guide_img);
		img_guide.setImageResource(img_res_id);
	}

	public void Show(int img_id) {
		img_res_id = img_id;
		this.show();
	}
}
