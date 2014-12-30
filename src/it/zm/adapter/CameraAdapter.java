package it.zm.adapter;

import it.zm.mobile.R;
import it.zm.app.AppController;
import it.zm.data.CameraDesc;
import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
 
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
 
public class CameraAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CameraDesc> cameraItems;
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
 
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView status = (TextView) convertView.findViewById(R.id.status);
 
        // getting movie data for the row
        CameraDesc m = cameraItems.get(position);
 
        // thumbnail image
		String baseUrl = "http://"+DataHolder.getDataHolder().getConfigData().baseUrl;
		
		String u =	baseUrl + "/cgi-bin/zms?mode=single&monitor="+(m.id)+"&scale=100&"+DataHolder.getDataHolder().getAuth();
		
		Log.d("CAMERA ADAPTER", "Preview URL  " + u);
		
        thumbNail.setImageUrl(u, imageLoader);
         
        // camera name
        name.setText(m.name);
         
        // status
        status.setText("Status: " + m.state);
 
        return convertView;
    }
 
}