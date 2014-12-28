package it.zm.mobile.activities;

import org.apache.http.impl.client.DefaultHttpClient;

import it.zm.auth.ZmHashAuth;
import it.zm.data.ConfigData;
import it.zm.data.DataHolder;
import it.zm.mobile.R;
import it.zm.mobile.R.id;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.xml.DataCameras;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BasicActivity {
	
	private static final String TAG = "MAIN_ACTIVITY";

    protected void runOnMenuRelease(){
		// Get number of cameras and create common DataCamera structure
		DataHolder.getDataHolder().getDataCameras().fetchData();
		
		Intent nw_intent = new Intent(this, ListCameras.class);
		startActivity(nw_intent);
    }
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		runOnMenuRelease();
	}
        
}