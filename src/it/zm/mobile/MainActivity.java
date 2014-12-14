package it.zm.mobile;

import org.apache.http.impl.client.DefaultHttpClient;

import it.zm.auth.ZmHashAuth;
import it.zm.data.ConfigData;
import it.zm.data.DataHolder;
import it.zm.mobile.activities.ListCameras;
import it.zm.mobile.activities.SettingsActivity;
import it.zm.mobile.activities.VideoActivity;
import it.zm.mobile.activities.VideoActivity.RestartApp;
import it.zm.xml.DataCameras;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final int REQUEST_NW_SETTING = 0;
	
	private static final String TAG = "MAIN_ACTIVITY";
	
    ConfigData confData;
    String auth;
    String baseUrl;
    
    DefaultHttpClient client;
    
    ZmHashAuth authenticator;
    
    DataCameras dc;
    
    protected void auth(){
        auth = null;
        baseUrl = new String("http://"+confData.baseUrl+"/zm/index.php");        
        
		// Create Http connection for everyone
		client = new DefaultHttpClient();
		
		// Authenticate
		authenticator = new ZmHashAuth(baseUrl, confData.username, confData.password, client);

		// Get auth token only if needed
		if(authenticator.checkAuthNeeded()){
			auth = authenticator.getAuthHash();
			DataHolder.getDataHolder().auth = auth;
		}
    }
    
    protected void runListCameraActivity(){
		// Get number of cameras and create common DataCamera structure
		dc = new DataCameras(baseUrl, client);
		dc.fetchData();
		DataHolder.getDataHolder().dc = dc;
		
		Intent nw_intent = new Intent(this, ListCameras.class);
		startActivity(nw_intent);
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        // TODO change that!
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
		
		// Create shared configuration data structure
		confData = new ConfigData(getBaseContext());
		DataHolder.getDataHolder().confData = confData;
		
		if(!confData.checkConfigFile()){
			
			// Open Settings Action
			Intent nw_intent = new Intent(this, SettingsActivity.class);
			
			startActivityForResult(nw_intent, REQUEST_NW_SETTING);
		} else {
			// We have a configuration file -> load data
			confData.load();
			
			// Auth and run CameraActivuty
			auth();
			runListCameraActivity();
		}
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    	case REQUEST_NW_SETTING:
    		if (resultCode == Activity.RESULT_OK) {
    			// Recover data
    			confData.baseUrl = data.getExtras().getString("hostText");
    			confData.username = data.getExtras().getString("userText");
    			confData.password = data.getExtras().getString("passwordText");

    			Log.d(TAG, "Base URL: "+confData.baseUrl);
    			
    			// Got configuration data -> save them
    			confData.save();
    			
    			// Auth and run CameraActivuty
    			auth();
    			runListCameraActivity();
    		}
    		break;
        }
    }
        
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
		    case R.id.action_settings:
		    	// Open Settings Action
				Intent nw_intent = new Intent(this, SettingsActivity.class);
				startActivityForResult(nw_intent, REQUEST_NW_SETTING);
				
				return true;
				
		    default:
		    	return super.onOptionsItemSelected(item);
		  }
		}

}
