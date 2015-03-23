package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.channels.AlreadyConnectedException;
import java.util.List;  
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.AlarmSettingsItemBean;

public class AlarmSettingsAdapter extends BaseAdapter {

	private Context _context;
	private LayoutInflater _layoutInflater; 
	private List<AlarmSettingsItemBean> _listData = null;
	
	public AlarmSettingsAdapter(Context context, List<AlarmSettingsItemBean> listData){
		this._context = context;
		this._listData = listData;
		this._layoutInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _listData==null ? 0 : _listData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return _listData==null ? null : _listData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	@Override  
	public boolean isEnabled(int position) {  

	   if (_listData.get(position).getIsTag()) {  	  
		   return false;  
	   }  	  
	   return super.isEnabled(position);  	  
	}  
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//istag:标示是否是tag; title：标题; tips:标题下的提示; switch:功能开关(-1:无;0:关闭;1:开启); enabled:功能能否使用
	   AlarmSettingsItemBean dataObj = _listData.get(position);
	   boolean enabled = true;
	   TextView tv_title = null;
	   try {
		   if (dataObj.getIsTag()) {  	  
			   convertView = _layoutInflater.inflate(R.layout.alarm_settings_list_tag, null);
			   tv_title = (TextView)convertView.findViewById(R.id.tv_title);
			   View tv_divider = (View)convertView.findViewById(R.id.group_divider);
			   if(position == 0){			   
				   tv_divider.setVisibility(View.GONE);
			   }
			   else{
				   tv_divider.setVisibility(View.VISIBLE);
			   }
		   } 		
		   else{
			   convertView = _layoutInflater.inflate(R.layout.alarm_settings_list_item, null);
			   tv_title = (TextView)convertView.findViewById(R.id.tv_title);
			   enabled = dataObj.getEnabled();
			   RelativeLayout rl_item = (RelativeLayout)convertView.findViewById(R.id.rl_item);
			   TextView tv_tips = (TextView)convertView.findViewById(R.id.tv_tips);	
			   ImageView img_func_sw = (ImageView)convertView.findViewById(R.id.item_switch);
			   if(!enabled){
				   rl_item.setEnabled(false);
				   rl_item.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					});
				   tv_title.setTextColor(_context.getResources().getColor(R.color.more_fragment_color7));
				   tv_tips.setTextColor(_context.getResources().getColor(R.color.more_fragment_color7));
				   int func_switch = dataObj.getSwitch();
				   if(0 == func_switch){
					   //关闭
					   img_func_sw.setBackgroundResource(R.drawable.morefragment_normal_disabled_con);
				   }
				   else if(1 == func_switch){
					   //打开
					   img_func_sw.setBackgroundResource(R.drawable.morefragment_selected_disabled_icon);				   
				   }
				   else{
					   img_func_sw.setVisibility(View.INVISIBLE);
				   }
			   }
			   else{
				   rl_item.setEnabled(true);
				   convertView.setEnabled(true);
				   tv_title.setTextColor(_context.getResources().getColor(R.color.more_fragment_color5));
				   tv_tips.setTextColor(_context.getResources().getColor(R.color.more_fragment_color5));
					  
				   int func_switch = dataObj.getSwitch();
				   if(0 == func_switch){
					   //关闭
					   img_func_sw.setBackgroundResource(R.drawable.morefragment_normal_icon);
				   }
				   else if(1 == func_switch){
					   //打开
					   img_func_sw.setBackgroundResource(R.drawable.morefragment_selector_icon);				   
				   }
				   else{
					   img_func_sw.setVisibility(View.INVISIBLE);
				   }
			   }
			   tv_tips.setText(dataObj.getTips());					   					  
		   }
		   
		   tv_title.setText(dataObj.getTitle());

		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	   return convertView;
	}

}
