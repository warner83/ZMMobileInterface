package it.zm.adapter;

import it.zm.mobile.R;
import it.zm.app.AppController;
import it.zm.data.DataHolder;
import it.zm.data.MonitorEvent;
 
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
 
public class EventListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MonitorEvent> eventItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public EventListAdapter(Activity activity, List<MonitorEvent> eventItems) {
        this.activity = activity;
        this.eventItems = eventItems;
    }
 
    @Override
    public int getCount() {
        return eventItems.size();
    }
 
    @Override
    public Object getItem(int location) {
        return eventItems.get(location);
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
            convertView = inflater.inflate(R.layout.list_row, null);
 
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
 
        // getting movie data for the row
        MonitorEvent m = eventItems.get(position);
 
        // thumbnail image
		Integer numFrame = Integer.parseInt(m.maxframeid);
		String frame = String.format("%03d", numFrame);
        String baseUrl = DataHolder.getDataHolder().getBaseUrl();
		String base = new String(baseUrl.replaceAll("index.php", ""));
		String u =	base+"events/"+m.monitor+"/"+m.id+"/"+frame+"-capture.jpg";
        thumbNail.setImageUrl(u, imageLoader);
         
        // title
        date.setText(m.time);
         
        // rating
        duration.setText("Duration: " + m.duration);
 
        return convertView;
    }
 
}