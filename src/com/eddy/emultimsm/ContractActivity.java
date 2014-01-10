package com.eddy.emultimsm;

import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContractActivity extends Activity {

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;

	private ListView mListView;
	private CheckboxAdapter mAdapter;
	private List<Map<String, Object>> mList;

	private ProgressDialog progress;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contract);
		// 获取ListView对象
		mListView = (ListView) findViewById(R.id.contractList);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View view, int position, long id) {
				mAdapter.toggleCheck(view, position);
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progress.dismiss();

				// 创建适配器
				mAdapter = new CheckboxAdapter(getApplicationContext(), mList);
				mListView.setAdapter(mAdapter);
			}
		};

		final String preNumbers = getIntent().getStringExtra("preNumbers");
		progress = ProgressDialog.show(this, "提示", "正在加载联系人", true);
		new Thread() {
			public void run() {
				loadContact(preNumbers);
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void loadContact(String preNumbers) {
		String[] initNumbers = null;
		Map<String, Boolean> initMap = new HashMap<String, Boolean>();
		
		if(preNumbers != null && preNumbers != "") {
			initNumbers = preNumbers.split(",");
			for(int i = 0; i < initNumbers.length; i++) {
				String number = initNumbers[i].split("\\|")[0];
				initMap.put(number, true);
			}
		}
		
		mList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap = null;

		List<HashMap<String, Object>> contractList = fillMaps();
		Collections.sort(contractList, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> h1, HashMap<String, Object> h2) {
				Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
				return cmp.compare(h1.get("name").toString(), h2.get("name").toString());
			}
		});

		for (Map<String, Object> map : contractList) {
			mMap = new HashMap<String, Object>();
			mMap.put("contactPhoto", map.get("contactPhoto"));
			mMap.put("username", map.get("name"));
			mMap.put("phone", map.get("key"));
			mMap.put("py", map.get("py"));
			if(initMap.get(map.get("key")) != null) {
				mMap.put("checked", true);
			}
			else {
				mMap.put("checked", false);
			}
			mList.add(mMap);
		}
	}

	private List<HashMap<String, Object>> fillMaps() {
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
		Cursor phoneCursor = null;
		try {
			ContentResolver resolver = getContentResolver();
			phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
			if (phoneCursor == null) {
				return items;
			}
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (phoneNumber == null || phoneNumber.trim().equals(""))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
				}

				HashMap<String, Object> i = new HashMap<String, Object>();
				i.put("name", contactName);
				i.put("key", phoneNumber);
				i.put("contactPhoto", contactPhoto);
				i.put("py", contactName.charAt(0) + "");
				Set<String> pset = PYUtil.getPinYin(contactName);
				Iterator<String> itor = pset.iterator();
				if(itor.hasNext()) {
					String py = itor.next();
					String fc = py.substring(0, 1).toUpperCase();
					i.put("py", fc);
				}
				
				items.add(i);
			}
		} finally {
			if (phoneCursor != null)
				phoneCursor.close();
		}
		return items;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contract, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_selectContactOK:
			selectContractOK();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void selectContractOK() {
		String checkedContract = "";
		CheckboxAdapter mAdapter = (CheckboxAdapter) mListView.getAdapter();
		SparseBooleanArray state = mAdapter.state;
		for (int j = 0; j < mAdapter.getCount(); j++) {
			if (state.get(j)) {
				HashMap<String, Object> map = (HashMap<String, Object>) mAdapter.getItem(j);
				String username = map.get("username").toString();
				String phone = map.get("phone").toString();
				checkedContract += phone + "|" + username + ",";
			}
		}
		Intent data = new Intent(this, ContractActivity.class);
		Bundle extras = new Bundle();
		extras.putString("selectContact", checkedContract);
		data.putExtras(extras);
		setResult(RESULT_OK, data);
		finish();
	}

}
