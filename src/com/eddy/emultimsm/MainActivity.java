package com.eddy.emultimsm;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ProgressDialog progress;
	private Button sendBtn;
	private EditText mobileText;
	private EditText messageText;
	
	private Handler handler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBtn = (Button) findViewById(R.id.send);
        mobileText = (EditText) findViewById(R.id.mobile);
        messageText = (EditText) findViewById(R.id.message);
        
        handler = new Handler(){
        	@Override  
            public void handleMessage(Message msg) {
        		progress.dismiss();
        		sendBtn.setEnabled(true);
        		Toast.makeText(getApplicationContext(), "发送完毕", Toast.LENGTH_SHORT).show();
        		messageText.setText("");
        		mobileText.setText("");
        	}
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_selectContact:
			Intent intent = new Intent(MainActivity.this, ContractActivity.class);
			intent.putExtra("preNumbers", mobileText.getText().toString());
	    	startActivityForResult(intent, 1);
			return true;
		}
		
		return false;
	}

    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_CANCELED) {
    	}
		else if (resultCode == Activity.RESULT_OK)  {
			String result = data.getExtras().getString("selectContact");
			mobileText.setText(result);
		}
	}
    
    public void sendMessage(View view) {
		progress = ProgressDialog.show(this, "提示", "正在发送信息", true);
		
		sendBtn.setEnabled(false);
		new Thread() {  
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				doSendMessage();
				handler.sendEmptyMessage(0);
			}
		}.start();
    }
    
    private void doSendMessage() {
    	String mobiles = mobileText.getText().toString();//逗号分隔
    	String message = messageText.getText().toString();
    	if(message != null) {
    		SmsManager sms = SmsManager.getDefault();
    		List<String> texts = sms.divideMessage(message);
    		
    		String[] mobileArr = mobiles.split(",");
    		for(int i = 0; i < mobileArr.length; i++) {
    			String mobile = mobileArr[i];
    			if(mobile == null || mobile.trim().equals(""))
    				continue;
    			
    			String[] contactInfo = mobile.split("\\|");
    			String no = contactInfo[0];
    			
    			for(String text : texts) {
        			sms.sendTextMessage(no, null, text, null, null);
        			Log.i("sms", "send a message");
        		}
    		}
    		
    	}
    }
}
