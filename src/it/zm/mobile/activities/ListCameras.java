package it.zm.mobile.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.zm.data.DataHolder;
import it.zm.mobile.R;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.xml.DataCameras;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListCameras extends Activity {

	DataCameras dc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_cameras);
		
		dc = DataHolder.getDataHolder().dc;
		
	    final ListView listview = (ListView) findViewById(R.id.listview);
	    
    	int numCameras = dc.getNumCameras();
    	
		List<String> names = dc.getNames();
    	
		Log.d("LIST CAMERAS", "Got num cameras: "+numCameras);
		
	    //String[] values = new String[] { };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, names);
        listview.setAdapter(adapter); 
        
        listview.setOnItemClickListener( new AdapterView.OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
               int position, long id) {
              
             // ListView Clicked item index
             int itemPosition     = position;
             int monitor = position + 1;
             
             // ListView Clicked item value
             String  itemValue    = (String) listview.getItemAtPosition(position);
                
              // Show Alert 
              Toast.makeText(getApplicationContext(),
                "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                .show();
              
      		  List<String> IDs = dc.getIDs();
      		  
      		  String m_id = IDs.get(monitor);
              
      		  Log.d("LIST CAMERAS", "Selected " + m_id + " width " + dc.getWidth(m_id) + " height " + dc.getHeight(m_id));
      		        		  
              Intent nw_intent = new Intent(ListCameras.this, VideoActivity.class);
      		  nw_intent.putExtra("url", "http://"+DataHolder.getDataHolder().confData.baseUrl+"/cgi-bin/zms?mode=jpeg&monitor="+(monitor)+"&scale=100&maxfps=5&buffer=1000&"+DataHolder.getDataHolder().auth);
      		  nw_intent.putExtra("width", Integer.toString(dc.getWidth(m_id)));
      		  nw_intent.putExtra("height", Integer.toString(dc.getHeight(m_id)));
      		        		
      		  startActivity(nw_intent);	    
           
            }

       }); 
		
	}
	

	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_cameras, menu);
		return true;
	}

}
