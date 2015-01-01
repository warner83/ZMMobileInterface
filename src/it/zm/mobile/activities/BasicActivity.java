package it.zm.mobile.activities;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import it.zm.auth.ZmHashAuth;
import it.zm.data.ConfigData;
import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
import it.zm.mobile.R;
import it.zm.mobile.R.id;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.xml.DataCameras;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public abstract class BasicActivity extends Activity {

	private static final int REQUEST_NW_SETTING = 0;
	
	private static final String TAG = "BASIC_ACTIVITY";
	
    private ConfigData confData;
    private String auth;
    private String baseUrl;
    
    private DefaultHttpClient client;
    
    private ZmHashAuth authenticator;
    
    private DataCameras dc;
            
    protected void auth(){
    	
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
              
            }
          };
          new Thread(runnable).start();
    	
        auth = null;
        baseUrl = DataHolder.getDataHolder().getBaseUrl();        
        
		// Create Http connection for everyone
		client = DataHolder.getDataHolder().getHttpClient();
		
		// Authenticate
		authenticator = new ZmHashAuth(baseUrl, confData.username, confData.password, client);

		// Get auth token only if needed
		if(authenticator.checkAuthNeeded()){
			
			auth = authenticator.getAuthHash();
			DataHolder.getDataHolder().setAuth(auth);
		}
		
    }
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
        // Change that?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
		
		// Create shared configuration data structure
        DataHolder.getDataHolder().setContext(getBaseContext());
		confData = DataHolder.getDataHolder().getConfigData();
		
		if(!confData.checkConfigFile()){
			
			// Open Settings Action
			Intent nw_intent = new Intent(this, SettingsActivity.class);
			
			startActivityForResult(nw_intent, REQUEST_NW_SETTING);
		} else {
			// We have a configuration file -> load data
			confData.load();
			
			// Authenticate
			auth();
			runOnMenuRelease();
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
    			
    			// Authenticate
    			auth();
    			runOnMenuRelease();
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

	// Function used to be called when menu returns
	protected abstract void runOnMenuRelease();
	
}

