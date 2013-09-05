package com.eddy.emultimsm;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void getContract(View view) {
    	Intent intent = new Intent(MainActivity.this, ContractActivity.class);
    	startActivityForResult(intent, 1);
    }
    
    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_CANCELED) 
			System.out.println(1111);
		else if (resultCode == Activity.RESULT_OK)  {
			System.out.println(222);
			String result = data.getExtras().getString("selectContact");
			EditText mobileText = (EditText) findViewById(R.id.mobile);
			mobileText.setText(result);
		}
	}
    
    public void sendMessage(View view) {
    	Button sendBtn = (Button) findViewById(R.id.send);
    	sendBtn.setEnabled(false);
    	EditText mobileText = (EditText) findViewById(R.id.mobile);
    	String mobiles = mobileText.getText().toString();//逗号分隔
    	EditText messageText = (EditText) findViewById(R.id.message);
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
    			String username = contactInfo[0];
    			String no = contactInfo[1];
    			
    			sendBtn.setText("正在发送: " + username + ", " + i);
    			for(String text : texts) {
        			sms.sendTextMessage(no, null, text, null, null);
        			Log.i("sms", "send a message");
        		}
    		}
    		
    	}
    	
    	sendBtn.setEnabled(true);
    	sendBtn.setText("发送完毕 ");
    }
    
}
