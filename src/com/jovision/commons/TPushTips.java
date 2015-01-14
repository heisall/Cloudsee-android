package com.jovision.commons;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class TPushTips {

	private Context mContext;
	private Dialog noticeDialog;

	public TPushTips(Context context) {
		this.mContext = context;
	}

	public void showNoticeDialog(int id) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(mContext.getString(R.string.tips));

		builder.setMessage(mContext.getString(id));
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(20, 0, 0, 0);
		layout.setLayoutParams(layoutParams);
		CheckBox box = new CheckBox(mContext);
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					MySharedPreference.putBoolean("TP_AUTO_TIPS", true);
				} else {
					MySharedPreference.putBoolean("TP_AUTO_TIPS", false);
				}
			}
		});
		box.setLayoutParams(layoutParams);
		layout.addView(box);
		TextView view = new TextView(mContext);
		view.setTextSize(18);
		view.setLayoutParams(layoutParams);
		view.setTextColor(mContext.getResources().getColor(
				R.color.more_fragment_color3));
		view.setText(mContext.getResources().getString(R.string.no_more_tips));
		layout.addView(view);
		builder.setView(layout);
		builder.setPositiveButton(mContext.getString(R.string.sure),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		// builder.setNegativeButton(mContext.getString(R.string.str_updatelater),
		// new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
		noticeDialog = builder.create();
		noticeDialog.show();
	}
}
