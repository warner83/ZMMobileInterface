package it.zm.mobile;

import org.apache.http.impl.client.DefaultHttpClient;

import it.zm.auth.ZmHashAuth;
import it.zm.data.ConfigData;
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
		if(authenticator.checkAuthNeeded())
			auth = authenticator.getAuthHash();		
    }
    
    protected void runCameraActivity(){
		// Get number of cameras
		dc = new DataCameras(baseUrl, client);
		dc.fetchData();
    	
    	int numCameras = dc.getNumCameras();

		Log.d(TAG,"Got num cameras: "+numCameras);
		
		int monitor = 1;
		
		Intent nw_intent = new Intent(this, VideoActivity.class);
		nw_intent.putExtra("url", "http://"+confData.baseUrl+"/cgi-bin/zms?mode=jpeg&monitor="+monitor+"&scale=100&maxfps=5&buffer=1000&"+auth);
		nw_intent.putExtra("width", "1280");
		nw_intent.putExtra("height", "800");
		
		startActivity(nw_intent);
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        // TODO change that!
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
		
		// Create configuration data structure
		confData = new ConfigData();
		
		if(!confData.checkConfigFile()){
			
			// Open Settings Action
			Intent nw_intent = new Intent(this, SettingsActivity.class);
			
			startActivityForResult(nw_intent, REQUEST_NW_SETTING);
		} else {
			// We have a configuration file -> load data
			confData.load();
			
			// Auth and run CameraActivuty
			auth();
			runCameraActivity();
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
    			runCameraActivity();
    		}
    		break;
        }
    }
        
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
