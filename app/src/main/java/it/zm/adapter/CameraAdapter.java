package it.zm.adapter;

import it.zm.mobile.R;
import it.zm.app.AppController;
import it.zm.data.CameraDesc;
import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
 
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
 
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
 
public class CameraAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CameraDesc> cameraItems;
    private GridView gridview;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
     
    public CameraAdapter(Activity activity, List<CameraDesc> cameraItems) {
        this.activity = activity;
        this.cameraItems = cameraItems;
    }
 
    @Override
    public int getCount() {
        return cameraItems.size();
    }
 
    @Override
    public Object getItem(int location) {
        return cameraItems.get(location);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.grid_cell, null);
        
        gridview =  (GridView) parent.findViewById(R.id.gridview);
 
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView status = (TextView) convertView.findViewById(R.id.status);
 
        // getting movie data for the row
        CameraDesc m = cameraItems.get(position);
        
        // thumbnail image
        
        // Get boundaries of grid and apply
        int local_position = position % gridview.getNumColumns();
        int first = position - local_position;
        int last = position - local_position + gridview.getNumColumns() - 1;
        Log.d("CAMERA ADAPTER", "Position " + position + "First  " + first + " last " + last);
        int max_width = gridview.getWidth() / gridview.getNumColumns();
        // Get the maximum height of a raw
        CameraDesc maxC = DataHolder.getDataHolder().getDataCameras().getMaxHeight(Integer.toString(first), Integer.toString(last));
        int max_height = (int) (( (double) max_width / Integer.parseInt(maxC.width) * Integer.parseInt(maxC.height)));
        Log.d("CAMERA ADAPTER", "Max width " + max_width);
        Log.d("CAMERA ADAPTER", "Max height " + max_height);
        
        LayoutParams params = (LayoutParams) thumbNail.getLayoutParams();
        params.width = max_width; 
        params.height = max_height;
        thumbNail.setLayoutParams(params);
        
        
        
        // Get actual image size
        int act_width = max_width;
        int act_height = Integer.parseInt(m.height) * ( act_width / Integer.parseInt(m.width)); 
        int perc = DataHolder.getDataHolder().getDataCameras().getPerc(m.id, act_width, act_height);
        Log.d("CAMERA ADAPTER", "Perc " + perc); 
        
        // Set url
     	String baseUrl = "http://"+DataHolder.getDataHolder().getConfigData().baseUrl;
     		
     	String u =	baseUrl + "/cgi-bin/zms?mode=single&monitor="+(m.id)+"&scale="+perc+"&"+DataHolder.getDataHolder().getAuth();
     		
     	Log.d("CAMERA ADAPTER", "Preview URL  " + u);
        thumbNail.setImageUrl(u, imageLoader);
        
        // camera name
        name.setText(m.name);
         
        // status
        status.setText("Status: " + m.state);
 
        return convertView;
    }
 
}