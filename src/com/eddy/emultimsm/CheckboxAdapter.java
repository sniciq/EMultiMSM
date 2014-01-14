package com.eddy.emultimsm;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
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
	SparseBooleanArray state = new SparseBooleanArray();
	
	public CheckboxAdapter(Context context, List<Map<String, Object>> listData) {
		this.context = context;
		this.listData = listData;
		for(int i = 0; i < listData.size(); i++) {
			Map<String, Object> item = listData.get(i);
			boolean checked = (Boolean) item.get("checked");
			state.put(i, checked);
		}
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
		
		TextView userPY = (TextView) convertView.findViewById(R.id.userPY);
		userPY.setText(listData.get(positionId).get("py").toString());
		
		CheckBox check = (CheckBox) convertView.findViewById(R.id.checked);
		check.setChecked(state.get(positionId));  
		
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				state.put(positionId, isChecked);
			}
		});
		
		return convertView;
	}

	public void toggleCheck(View convertView, int positionId) {
		boolean checked = false;
		if(state.get(positionId)) {
			checked = state.get(positionId);
		}
		checked = !checked;
		CheckBox check = (CheckBox) convertView.findViewById(R.id.checked);
		check.setChecked(checked);
		state.put(positionId, checked);
	}
}
