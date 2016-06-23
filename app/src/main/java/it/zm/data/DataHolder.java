package it.zm.data;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import it.zm.auth.ZmHashAuth;
import it.zm.xml.DataCameras;

public class DataHolder {
	private ConfigData confData;
	
	private DataCameras dc;
	
	private String auth;
	
	private Context context;
	
    private DefaultHttpClient client;
    
    private String baseUrl;
    
	public DataHolder(){
		confData = null;
		dc = null;
		auth = new String("");
		context = null;
		client = null;
	}
    
    public void setAuth(String a){
    	auth = a;
    }
    
    public String getAuth(){
    	return auth;
    }
	
	public void setContext(Context c){
		context = c;
	}
	
	public Context getContext(){
		return context;
	}
	
	public ConfigData getConfigData(){
		if( confData == null ){
			confData = new ConfigData(context);
		}
		
		return confData;
	}

	public DefaultHttpClient getHttpClient(){
		if( client == null ) {
			client = new DefaultHttpClient();
		}
		
		return client;
	}
	
	public String getBaseUrl(){
		if(baseUrl == null && confData.baseUrl != ""){
			baseUrl = new String("http://"+confData.baseUrl+"/zm/index.php");   
		}
		
		return baseUrl;
	}
	
	public DataCameras getDataCameras(){
		if( dc == null ){
			dc = new DataCameras(getBaseUrl(), getHttpClient());
		}
		
		return dc;
	}
	
	private static final DataHolder holder = new DataHolder();
	public static DataHolder getDataHolder() {return holder;}
}
