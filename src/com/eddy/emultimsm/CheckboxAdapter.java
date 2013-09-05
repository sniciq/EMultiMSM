package com.eddy.emultimsm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckboxAdapter extends BaseAdapter {
	
	Context context;
	List<Map<String, Object>> listData;
	//记录checkbox的状态  
	HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
	
	public CheckboxAdapter(Context context, List<Map<String, Object>> listData) {
		this.context = context;
		this.listData = listData;
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int positionId, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.contract_item, null);
		ImageView image = (ImageView) convertView.findViewById(R.id.img);
		TextView username = (TextView) convertView.findViewById(R.id.username);
		username.setText(listData.get(positionId).get("username").toString());
		
		TextView phone = (TextView) convertView.findViewById(R.id.phone);
		phone.setText(listData.get(positionId).get("phone").toString());
		
		image.setImageBitmap((Bitmap) listData.get(positionId).get("contactPhoto"));
		
		CheckBox check = (CheckBox) convertView.findViewById(R.id.checked);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					state.put(positionId, isChecked);
				}
				else {
					state.remove(positionId);
				}
			}
		});
		check.setChecked((state.get(positionId) == null ? false : true));  
		return convertView;
	}

}
