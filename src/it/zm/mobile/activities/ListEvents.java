package it.zm.mobile.activities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
import it.zm.mobile.R;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.xml.DataCameras;
import it.zm.xml.DataEvents;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListEvents extends Activity {
	
	DataCameras dc;
	
	List eventList;
	
	DataEvents de;
	
	String m_id;
	
	int page;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_list_events);
		super.onCreate(savedInstanceState);
	
		// Get monitor
		
        Intent intent = getIntent();
        m_id = intent.getExtras().getString("monitor");
		
		dc = DataHolder.getDataHolder().getDataCameras();
		
	    final ListView listview = (ListView) findViewById(R.id.listview);
	    
		// Fetch events
	    page = 0;
		de = new DataEvents(DataHolder.getDataHolder().getBaseUrl(), DataHolder.getDataHolder().getHttpClient(), m_id);
		de.setPage(Integer.toString(page));
		de.fetchData();
		
		eventList = de.getAllEvents();
		
		Log.d("LIST EVENTS", "Monitor ID " + m_id + " got " + eventList.size() + " events");
		
		final List<String> names = new ArrayList<String>();
		
		for(int i =0; i < eventList.size(); ++i ){
			
			MonitorEvent e = (MonitorEvent) eventList.get(i);
			
			Log.d("LIST EVENTS", "Event ID " + e.id + " time " + e.time + " duration " + e.duration);
			
			String name = new String(e.time + " " + e.duration + " seconds");
			
			names.add(name);

		}
		
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, names);
        listview.setAdapter(adapter); 
        
        // Add load more button
        // Creating a button - Load More
        Button btnLoadMore = new Button(this);
        btnLoadMore.setText("Load More");
         
        // Listening to Load More button click event
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                page++;
                
                // TODO make a function for this
        		de.setPage(Integer.toString(page));
        		de.fetchData();
        		
        		eventList = de.getAllEvents();
        		
        		for(int i =0; i < eventList.size(); ++i ){
        			
        			MonitorEvent e = (MonitorEvent) eventList.get(i);
        			
        			Log.d("LIST EVENTS", "Event ID " + e.id + " time " + e.time + " duration " + e.duration);
        			
        			String name = new String(e.time + " " + e.duration + " seconds");
        			
        			names.add(name);

        		}
        		
        		int currentPosition = listview.getFirstVisiblePosition();
        		
        		ArrayAdapter<String> adapter = new ArrayAdapter<String>(arg0.getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, names);
        		listview.setAdapter(adapter);
        		
        		// Restore position
        		listview.setSelectionFromTop(currentPosition + 1, 0);
        		
            }
        });
        
        // Adding button to listview at footer
        listview.addFooterView(btnLoadMore);
        
        registerForContextMenu(listview);
        
        listview.setOnItemClickListener( new AdapterView.OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
               int position, long id) {
              
             // ListView Clicked item index
             int itemPosition     = position;
             
             // ListView Clicked item value
             String  itemValue    = (String) listview.getItemAtPosition(position);
                
              // Show Alert 
              Toast.makeText(getApplicationContext(),
                "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                .show();
              
              MonitorEvent e = (MonitorEvent) eventList.get(position);
              
      		  String e_id = e.id;
              
      		  Log.d("LIST EVENTS", "Event ID " + e_id + " width " + dc.getWidth(m_id) + " height " + dc.getHeight(m_id));
      		        		  
              Intent nw_intent = new Intent(ListEvents.this, VideoActivity.class);
      		  nw_intent.putExtra("url", "http://"+DataHolder.getDataHolder().getConfigData().baseUrl+"/cgi-bin/zms?source=event&mode=jpeg&event="+e_id+"&monitor="+(m_id)+"&frame=1&scale=100&maxfps=5&buffer=1000&replay=single&"+DataHolder.getDataHolder().getAuth());
      		  nw_intent.putExtra("width", Integer.toString(dc.getWidth(m_id)));
      		  nw_intent.putExtra("height", Integer.toString(dc.getHeight(m_id)));
      		        		
      		  startActivity(nw_intent);	    
           
            }

       }); 
		
	}
}
