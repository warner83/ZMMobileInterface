package it.zm.mobile.deprecated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.zm.data.DataHolder;
import it.zm.mobile.R;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.mobile.activities.ListEvents;
import it.zm.mobile.activities.VideoActivity;
import it.zm.xml.DataCameras;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Toast;

public class ListCameras extends Activity {

	private static final int REQUEST_NW_SETTING = 0;
	
	DataCameras dc;
	
	// Context menu data
	String[] menuItems = {"Events"};
	String selectedId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_list_cameras);
		super.onCreate(savedInstanceState);
		
		runOnMenuRelease();
	}
	
	protected void runOnMenuRelease(){
		
		dc = DataHolder.getDataHolder().getDataCameras();
		
	    final ListView listview = (ListView) findViewById(R.id.listview);
	    
    	int numCameras = dc.getNumCameras();
    	
		List<String> names = dc.getNames();
    	
		Log.d("LIST CAMERAS", "Got num cameras: "+numCameras);
		
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, names);
        listview.setAdapter(adapter); 
        
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
                    		 
              List<String> IDs = dc.getIDs();
              
      		  String m_id = IDs.get(position);
              
      		  Log.d("LIST CAMERAS", "Selected monitor " + position + " ID " + m_id + " width " + dc.getWidth(m_id) + " height " + dc.getHeight(m_id));
      		        		  
              Intent nw_intent = new Intent(ListCameras.this, VideoActivity.class);
      		  nw_intent.putExtra("url", "http://"+DataHolder.getDataHolder().getConfigData().baseUrl+"/cgi-bin/zms?mode=jpeg&monitor="+(m_id)+"&scale=100&maxfps=5&buffer=1000&"+DataHolder.getDataHolder().getAuth());
      		  nw_intent.putExtra("width", Integer.toString(dc.getWidth(m_id)));
      		  nw_intent.putExtra("height", Integer.toString(dc.getHeight(m_id)));
      		        		
      		  startActivity(nw_intent);	    
           
            }

       }); 
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
				
		if (v.getId()==R.id.listview) {
		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		    
		    List<String> IDs = dc.getIDs();
		    
		    selectedId = IDs.get(info.position);
		    
		    List<String> names = dc.getNames();
		    
			menu.setHeaderTitle(names.get(info.position));
		    
		    for (int i = 0; i<menuItems.length; i++) {
		      menu.add(Menu.NONE, i, i, menuItems[i]);
		    }
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String menuItemName = menuItems[menuItemIndex];

		if(menuItemName.equals("Events")){
			// Launch event list action with selectedId as camera
            Toast.makeText(getApplicationContext(),
              "Showing events :"+selectedId, Toast.LENGTH_LONG)
              .show();
            
            Intent nw_intent = new Intent(ListCameras.this, ListEvents.class);
    		nw_intent.putExtra("monitor", selectedId);
    		        		
    		startActivity(nw_intent);	    
		}
		
		return true;
	}

}
