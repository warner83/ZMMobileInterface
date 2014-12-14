package it.zm.mobile.activities;

import it.zm.data.DataHolder;
import it.zm.mobile.R;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		TextView hostText = (TextView)findViewById(R.id.hostText);
		hostText.setText(DataHolder.getDataHolder().confData.baseUrl);
		
		TextView userText = (TextView)findViewById(R.id.userText);
		userText.setText(DataHolder.getDataHolder().confData.username);
		
		TextView passwordText = (TextView)findViewById(R.id.passwordText);
		passwordText.setText(DataHolder.getDataHolder().confData.password);
		
		Button buttonOK = (Button) findViewById(R.id.buttonConfirm);
		buttonOK.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View arg0) {
		        TextView hostText = (TextView)findViewById(R.id.hostText);
		        TextView userText = (TextView)findViewById(R.id.userText);
		        TextView passwordText = (TextView)findViewById(R.id.passwordText);
		        
		        Intent extras = new Intent();
		        extras.putExtra("hostText", hostText.getText().toString());
		        extras.putExtra("userText", userText.getText().toString());
		        extras.putExtra("passwordText", (String) passwordText.getText().toString());
				
		        setResult(RESULT_OK, extras);
				finish();
			}
			
		});
		
		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	

}
