package it.zm.mobile.activities;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import it.zm.mobile.R;
import it.zm.mobile.mjpeg.MjpegInputStream;
import it.zm.mobile.mjpeg.MjpegView;
import it.zm.xml.DataCameras;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import it.zm.auth.ZmHashAuth;
import it.zm.data.ConfigData;

public class VideoActivity extends Activity {
	private static final boolean DEBUG=false;
    private static final String TAG = "MJPEG";

    private int width = 1280;
    private int height = 800;

	String URL;
	
	private boolean suspending = false;
	
    private MjpegView mv;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        /*SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        width = preferences.getInt("width", width);
        height = preferences.getInt("height", height);*/
        
        Intent intent = getIntent();
        URL = intent.getExtras().getString("url");
        width = Integer.parseInt(intent.getExtras().getString("width"));
        height = Integer.parseInt(intent.getExtras().getString("height"));
        
        Log.d(TAG, "Starting URL " + URL);
		        
        mv = (MjpegView) findViewById(R.id.mv);
        
		if(mv!=null){
			mv.setResolution(width, height);
		}
        
        new DoRead().execute(URL);
    }

    
    @Override
	public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        if(mv!=null){
        	if(suspending){
        		suspending = false;
        		new RestartApp().execute();
        	}
        }

    }

    @Override
	public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    @Override
	public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        if(mv!=null){
        	if(mv.isStreaming()){
		        mv.stopPlayback();
		        suspending = true;
        	}
        }
    }
    @Override
	public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    @Override
	public void onDestroy() {
    	if(DEBUG) Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }
    
    /*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    	case 0:
    		if (resultCode == Activity.RESULT_OK) {
    			width = data.getIntExtra("width", width);
    			height = data.getIntExtra("height", height);
	    			    		
	    		SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				
				if(mv!=null){
					mv.setResolution(width, height);
				}
				
				editor.putInt("width", width);
				editor.putInt("height", height);
				
				editor.commit();

				new RestartApp().execute();
    		}
    		break;
        }
    }*/
    
    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        @Override
		protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();  
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());  
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }
            return null;
        }

        @Override
		protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if(result!=null) result.setSkip(1);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }
    
	public class RestartApp extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... v) {
			VideoActivity.this.finish();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			startActivity((new Intent(VideoActivity.this, VideoActivity.class)));
		}
	}
}