
package com.jovision.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class TestAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;

    public TestAdapter(Activity activitys) {
        activity = activitys;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 40;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TestHolser testHolser;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.textitem, null);
            testHolser = new TestHolser();
            testHolser.item = (TextView) convertView.findViewById(R.id.item);
            convertView.setTag(testHolser);
        } else {
            testHolser = (TestHolser) convertView.getTag();
        }
        testHolser.item.setText(position + "");
        return convertView;
    }

    class TestHolser {
        TextView item;
    }
}
