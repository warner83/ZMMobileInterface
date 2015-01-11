package it.zm.mobile.activities;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import it.zm.auth.ZmHashAuth;
import it.zm.data.ConfigData;
import it.zm.data.DataHolder;
import it.zm.mobile.R;
import it.zm.mobile.R.id;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.util.Util;
import it.zm.xml.DataCameras;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	private static final String TAG = "MAIN_ACTIVITY";
    
	private static final int REQUEST_NW_SETTING = 0;
		
    private ConfigData confData;
    private String auth;
    private String baseUrl;
    
    private DefaultHttpClient client;
    
    private ZmHashAuth authenticator;
    
    private DataCameras dc;
    
    private ProgressBar progressBar;
            
    // Async operations 
    
    // Autenticate
    protected boolean auth(){

		// Get auth token only if needed
		try {
			if(authenticator.checkAuthNeeded()){
				
				auth = authenticator.getAuthHash();
				DataHolder.getDataHolder().setAuth(auth);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
			Util.handleException(e, MainActivity.this);
			
			return false;			
		} catch (IOException e) {
			e.printStackTrace();
			
			Util.handleException(e, MainActivity.this);
			
			return false;
		}
		
		return true;
		
    }
    
    // Fetch data
    protected boolean getData(){
		// Get number of cameras and create common DataCamera structure
		try {
			DataHolder.getDataHolder().getDataCameras().fetchData();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			
			Util.handleException(e, MainActivity.this);
			
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			
			Util.handleException(e, MainActivity.this);
			
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			
			Util.handleException(e, MainActivity.this);
			
			return false;
		}
		
		return true;
    }
    
    // Go to camera activity
    protected void moveNext(){
		//Intent nw_intent = new Intent(this, ListCameras.class);
		Intent nw_intent = new Intent(this, GridCameras.class);
		startActivity(nw_intent);
    }
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		Log.d("MainActivity","App started");
		
        // Change that?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
		
		// Create shared configuration data structure
        DataHolder.getDataHolder().setContext(getBaseContext());
		confData = DataHolder.getDataHolder().getConfigData();
		
		// Create Http connection for everyone
		client = DataHolder.getDataHolder().getHttpClient();
		
		if(!confData.checkConfigFile()){
			
			Log.d("MainActivity","No settings -> start setting dialog");
			
			// Open Settings Action
			Intent nw_intent = new Intent(this, SettingsActivity.class);
			
			startActivityForResult(nw_intent, REQUEST_NW_SETTING);
		} else {
			// We have a configuration file -> load data
			confData.load();
			
	        auth = null;
	        baseUrl = DataHolder.getDataHolder().getBaseUrl();       
			
			// Authenticate
			authenticator = new ZmHashAuth(baseUrl, confData.username, confData.password, client);
			
			// Authenticate
			new AsyncThread(this).execute();
		}
	}
	
	// Handle settings input (Only first installation
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    	case REQUEST_NW_SETTING:
    		if (resultCode == Activity.RESULT_OK) {
    			ConfigData confData = DataHolder.getDataHolder().getConfigData();
    			
    			// Recover data
    			confData.baseUrl = data.getExtras().getString("hostText");
    			confData.username = data.getExtras().getString("userText");
    			confData.password = data.getExtras().getString("passwordText");

    			Log.d("MainActivity", "Base URL: "+confData.baseUrl);
    			
    			// Got configuration data -> save them
    			confData.save();
    			
    	        auth = null;
    	        baseUrl = DataHolder.getDataHolder().getBaseUrl();       
    			
    			// Authenticate
    			authenticator = new ZmHashAuth(baseUrl, confData.username, confData.password, client);
    			
    			// Authenticate
    			new AsyncThread(this).execute();

    		}
    		break;
        }
    }
	
	// Async task to load more events
    private class AsyncThread extends AsyncTask<Void, Void, Void> {
    	 
    	MainActivity act;
    	
    	ProgressDialog pDialog;
    	
    	boolean result;
    	
    	public AsyncThread( MainActivity a ){
    		act = a;
    	}
    	
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(result = act.auth())
                    	result = act.getData();
                }
            });
 
            return (null);
        }
 
        protected void onPostExecute(Void unused) {
        	pDialog.dismiss();
        	
        	if(result)
        		act.moveNext();
        }
    }
        
}