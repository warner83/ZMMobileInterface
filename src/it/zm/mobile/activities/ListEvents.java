package it.zm.mobile.activities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.zm.adapter.EventListAdapter;
import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
import it.zm.mobile.R;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.xml.DataCameras;
import it.zm.xml.DataEvents;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
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
	
	ProgressDialog pDialog;
	
	List<String> names;
	
	EventListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_list_events);
		super.onCreate(savedInstanceState);
	
		ListView listview = (ListView) findViewById(R.id.listview);
		
		eventList = new ArrayList<MonitorEvent>();
		adapter = new EventListAdapter(this, eventList);
		listview.setAdapter(adapter);
		
		// Get monitor
        Intent intent = getIntent();
        m_id = intent.getExtras().getString("monitor");
		
        // Get DataCameras structure
		dc = DataHolder.getDataHolder().getDataCameras();
	    
		// Fetch events
	    page = 0;
		de = new DataEvents(DataHolder.getDataHolder().getBaseUrl(), DataHolder.getDataHolder().getHttpClient(), m_id);
        
        // Add load more button
        Button btnLoadMore = new Button(this);
        btnLoadMore.setText("Load More");
         
        // Listening to Load More button click event
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	// Load more
            	new loadMoreListView().execute();
            }
        });
        
        // Adding button to listview at footer
        listview.addFooterView(btnLoadMore);
        
        registerForContextMenu(listview);
        
        listview.setOnItemClickListener( new AdapterView.OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
               int position, long id) {
            	
	            	ListView listview = (ListView) findViewById(R.id.listview);
	              
	            	// ListView Clicked item index
	            	int itemPosition     = position;
	             
	            	// ListView Clicked item value
	            	MonitorEvent  item    = (MonitorEvent) listview.getItemAtPosition(position);
	                
	            	// Show Alert 
	            	Toast.makeText(getApplicationContext(),
	            			"Position :"+itemPosition+"  ListItem : " +item.id , Toast.LENGTH_LONG)
	            			.show();
              
	                MonitorEvent e = (MonitorEvent) eventList.get(position);
	              
	      		    String e_id = e.id;
	              
	        		  // Evaluate size
	                double max_width = listview.getWidth();
	                double max_height = listview.getHeight();
	                double width = dc.getWidth(m_id);
	                double height = dc.getHeight(m_id);
	                
	                int perc = 0;
	                
	                perc = (int) Math.round(width / max_width * 100);
	                if( perc > ( height / max_height * 100 ) )
	              	  perc = (int) Math.round(height / max_height * 100);
	               
	                if(perc > 100)
	              	  perc = 100;
	      		    
	                int act_width = (int) Math.round(width * (width / max_width));
	                int act_height = (int) Math.round(height * (height / max_height));  
	                
	      		    Log.d("LIST EVENTS", "Event ID " + e_id + " width " + dc.getWidth(m_id) + " height " + dc.getHeight(m_id)+ " perc " + perc );
	      		        		  
	                Intent nw_intent = new Intent(ListEvents.this, VideoActivity.class);
	      		    nw_intent.putExtra("url", "http://"+DataHolder.getDataHolder().getConfigData().baseUrl+"/cgi-bin/zms?source=event&mode=jpeg&event="+e_id+"&monitor="+(m_id)+"&frame=1&scale="+perc+"&maxfps=5&buffer=1000&replay=single&"+DataHolder.getDataHolder().getAuth());
	      		    nw_intent.putExtra("width", Integer.toString(act_width));
	      		    nw_intent.putExtra("height", Integer.toString(act_height));
	      		        		
	      		    startActivity(nw_intent);	    
           
            }

       }); 
        
        // Create structure for incoming data
        names = new ArrayList<String>();
        
        
        // Get first set of data
        new loadMoreListView().execute();
		
	}
	
	// Async task to load more events
    private class loadMoreListView extends AsyncTask<Void, Void, Void> {
    	 
        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending the request
            pDialog = new ProgressDialog(ListEvents.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected Void doInBackground(Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    
                	ListView listview = (ListView) findViewById(R.id.listview);
                	
                    // Get data
            		de.setPage(Integer.toString(page));
            		de.fetchData();
            		List events = de.getAllEvents();
            		
            		// Parse information and copy data to local structure 
					for(int i =0; i < events.size(); ++i ){
            			
            			MonitorEvent e = (MonitorEvent) events.get(i);
            			e.monitor = m_id;
            			
            			Log.d("LIST EVENTS", "Event ID " + e.id + " time " + e.time + " duration " + e.duration);
            			
            			String name = new String(e.time + " " + e.duration + " seconds");
            			names.add(name);

            		}
            		
					eventList.addAll(events); 
					
					// Show new entries and restore position
            		int currentPosition = listview.getFirstVisiblePosition();
            		
            		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, names);
            		listview.setAdapter(adapter);
            		*/
            		
            		// Restore position
            		if(page > 0)
            			listview.setSelectionFromTop(currentPosition + 1, 0);
            		
            		page++;
            		
            		adapter.notifyDataSetChanged();
                }
            });
 
            return (null);
        }
 
        protected void onPostExecute(Void unused) {
            // closing progress dialog
            pDialog.dismiss();
        }
    }
}
