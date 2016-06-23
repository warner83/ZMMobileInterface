package it.zm.mobile.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.zm.adapter.CameraAdapter;
import it.zm.adapter.EventListAdapter;
import it.zm.adapter.NavDrawerListAdapter;
import it.zm.auth.ZmHashAuth;
import it.zm.data.CameraDesc;
import it.zm.data.ConfigData;
import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
import it.zm.data.NavDrawerItem;
import it.zm.mobile.R;
import it.zm.mobile.R.layout;
import it.zm.mobile.R.menu;
import it.zm.mobile.activities.MainActivity;
import it.zm.xml.DataCameras;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class GridCameras extends Activity {

	private static final int REQUEST_NW_SETTING = 0;
	
	DataCameras dc;
	
	CameraAdapter adapter;
	
	// Context menu data
	String[] menuItems = {"Events"};
	String selectedId;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    
    private ArrayList<NavDrawerItem> navDrawerItems;
    private TypedArray navMenuIcons;
    private ActionBarDrawerToggle mDrawerToggle;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_grid_cameras);
		super.onCreate(savedInstanceState);
		
		// Navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        
        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem("Camera", navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem("Settings", navMenuIcons.getResourceId(1, -1)));
        
        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("Camera ID"); // TODO show server ID
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Select one server");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
		initGridView();
	}
	
	
	
	protected void initGridView(){
		
		dc = DataHolder.getDataHolder().getDataCameras();
		
		final GridView gridview = (GridView) findViewById(R.id.gridview);
		
		List<CameraDesc> cameras = dc.getAllCameras();
		adapter = new CameraAdapter(this, cameras);
		gridview.setAdapter(adapter);
        
        registerForContextMenu(gridview);
        
        gridview.setOnItemClickListener( new AdapterView.OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
               int position, long id) {
              
             // ListView Clicked item index
             int itemPosition     = position;
             
             // ListView Clicked item value
             CameraDesc  item    = (CameraDesc) gridview.getItemAtPosition(position);
                
              // Show Alert 
              Toast.makeText(getApplicationContext(),
                "Position :"+itemPosition+"  ListItem : " +item.name , Toast.LENGTH_LONG)
                .show();
                    		 
              List<String> IDs = dc.getIDs();
              
      		  String m_id = IDs.get(position);
              
      		  // Evaluate size
              double max_width = gridview.getWidth();
              double max_height = gridview.getHeight();
              double width = dc.getWidth(m_id);
              double height = dc.getHeight(m_id);
              int act_width;
              int act_height;
              
              int perc = 0;
              
              perc = (int) Math.round(max_width / width * 100);
              if( perc > ( max_height / height * 100 ) )
            	  perc = (int) Math.round(max_height / height * 100);
              
              if(perc > 100)
            	  perc = 100;
              
              //perc /= 3; // Do that for bandwidth saving
                            
              act_width = (int) Math.round(width * (max_width / width));
              act_height = (int) Math.round(height * (max_height / height));  
              
      		  Log.d("LIST CAMERAS", "Selected monitor " + position + " ID " + m_id + " width " + dc.getWidth(m_id) + " height " + dc.getHeight(m_id) + " perc " + (int)perc );
      		  
      		  Log.d("LIST CAMERAS", "Screen res " + max_width + " x " + max_height + " width " + dc.getWidth(m_id) + " height " + dc.getHeight(m_id) + " perc " + (int)perc );
    		  
      		  
      		  // Create new activity
              Intent nw_intent = new Intent(GridCameras.this, VideoActivity.class);
      		  nw_intent.putExtra("url", "http://"+DataHolder.getDataHolder().getConfigData().baseUrl+"/cgi-bin/zms?mode=jpeg&monitor="+(m_id)+"&scale="+(int)perc+"&maxfps=1&buffer=1000&"+DataHolder.getDataHolder().getAuth());
      		  nw_intent.putExtra("width", Integer.toString(act_width));
      		  nw_intent.putExtra("height", Integer.toString(act_height));
      		        		
      		  startActivity(nw_intent);	    
           
            }

       }); 
        
        getActionBar().setTitle("Server ID"); // TODO Show server name
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
				
		if (v.getId()==R.id.gridview) {
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
            
            Intent nw_intent = new Intent(GridCameras.this, ListEvents.class);
    		nw_intent.putExtra("monitor", selectedId);
    		        		
    		startActivity(nw_intent);	    
		}
		
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
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
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    	case REQUEST_NW_SETTING:
    		if (resultCode == Activity.RESULT_OK) {
    			ConfigData confData = DataHolder.getDataHolder().getConfigData();
    			
    			// Recover data
    			String baseUrl = data.getExtras().getString("hostText");
    			String username = data.getExtras().getString("userText");
    			String password = data.getExtras().getString("passwordText");

    			confData.addServer(baseUrl, username, password);
    			confData.selectServer(0);
    			
    			Log.d("LIST CAMERAS", "Base URL: "+confData.baseUrl);
    			
    			// Got configuration data -> save them
    			confData.save();
    			
				Intent nw_intent = new Intent(this, MainActivity.class);
				startActivity(nw_intent);

    		}
    		break;
        }
    }
}
