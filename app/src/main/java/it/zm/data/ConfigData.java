package it.zm.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

public class ConfigData {
	
	public class ZmServer {
		
		ZmServer (String bu, String u, String p){
			baseUrl = bu;
			username = u;
			password = p;
		}
		
		public String baseUrl;
		public String username;
		public String password;
	}

	// JSON management functions to save/load data to/from file 
	
	 public void readSettings(JsonReader reader) throws IOException {

		 ZmServer server = null;

		     reader.beginObject();
		     while (reader.hasNext()) {
		       String name = reader.nextName();
		       if (name.equals("selectedServer")) {
		         selectedServer = reader.nextInt();
		         Log.d("CONFIG", "Loaded selected server: "+selectedServer);
		       } else if (name.equals("bwSavingRatio")) {
		    	   bwSavingRatio = reader.nextDouble();
		       } else if (name.equals("ZmServer")) {
		         server = readZmServer(reader);
		         servers.add(server);
		       } else {
		         reader.skipValue();
		       }
		     }
		     reader.endObject();
		   }


		   public ZmServer readZmServer(JsonReader reader) throws IOException {
		     String username = null;
		     String password = null;
		     String baseUrl = null;

		     reader.beginObject();
		     while (reader.hasNext()) {
		       String name = reader.nextName();
		       if (name.equals("username")) {
		         username = reader.nextString();
		       } else if (name.equals("password")) {
		         password = reader.nextString();
		       } else if (name.equals("baseUrl")) {
		    	   baseUrl = reader.nextString();
		    	   Log.d("CONFIG", "Loaded server: "+baseUrl);
		       } else {
		         reader.skipValue();
		       }
		     }
		     reader.endObject();
		     return new ZmServer(baseUrl, username, password);
		   }
	
		   public void readJsonStream(InputStream in) throws IOException {
			     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			     try {
			       readSettings(reader);
			     }
			      finally {
			       reader.close();
			     }
			   }
		   
		   public void writeJsonStream(OutputStream out) throws IOException {
			     JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			     writer.setIndent("  ");
			     writeSettings(writer);
			     writer.close();
		   }
		   
		   public void writeSettings(JsonWriter writer) throws IOException {
			     writer.beginObject();
			     writer.name("selectedServer").value(selectedServer);
			     writer.name("bwSavingRatio").value(bwSavingRatio);

			     for(int i = 0 ; i < servers.size(); ++i ){
				     writer.name("ZmServer");
				     writeServer(writer, servers.get(i));
			     }
			     writer.endObject();
			   }

			   public void writeServer(JsonWriter writer, ZmServer s) throws IOException {
			     writer.beginObject();
			     writer.name("baseUrl").value(s.baseUrl);
			     writer.name("username").value(s.username);
			     writer.name("password").value(s.password);
			     writer.endObject();
			   }


	// Configuration data
		   
	List<ZmServer> servers;
	
	int selectedServer;
	
	double bwSavingRatio;
		
	// Data of selected server
	public String baseUrl;
	public String username;
	public String password;
	
	Context context;
	
	public ConfigData(Context c){
				
		context = c;
		
		selectedServer = -1;
		servers = new ArrayList<ZmServer>();
		
		baseUrl = "localhost";
		username = "admin";
		password = "admin";

	}
	
	public void addServer(String baseUrl, String username, String password){
		servers.add(new ZmServer(baseUrl, username, password));
		
		Log.d("CONFIG", "Server added: "+baseUrl);
	}
	
	public void removeServer(int ID){
		servers.remove(ID);
		
		Log.d("CONFIG", "Server removed ID: "+ID);
		
		if(ID == selectedServer)
			selectServer(0); // Select default server
	}
	
	public void selectServer(int ID){
		
		if(ID < 0 || ID > servers.size())
			return;
		
		if(servers.size() > 0){
			selectedServer = ID;
			
			baseUrl = servers.get(selectedServer).baseUrl;
	    	username = servers.get(selectedServer).username;
	    	password = servers.get(selectedServer).password;
	    	
	    	Log.d("CONFIG", "Server selected: "+baseUrl);
			
		} else {
			selectedServer = -1;
			
			baseUrl = "localhost";
			username = "admin";
			password = "admin";
			
			Log.d("CONFIG", "Server selected, DEFAULT");
		}
	}
	
	public Boolean save(){
		
    	try {	
    		//save properties to project root folder
    		FileOutputStream fileOut = context.openFileOutput("config.properties", Context.MODE_PRIVATE);
    		
    		writeJsonStream(fileOut);
    		
    		Log.d("CONFIG", "Saving servers no: "+servers.size());
    		
    		fileOut.close();
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
    		return false;
        }
    	return true;
	}
	
	public Boolean load(){
		
    	try {	
    		//load properties to project root folder
    		FileInputStream fileIn = context.openFileInput("config.properties");
    		
    		readJsonStream(fileIn);
    		
    		Log.d("CONFIG", "Loaded servers no: "+servers.size());
    		
    		fileIn.close();
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
    		return false;
        }
    	
    	selectServer(selectedServer);
    	
    	return true;
	}
	
	// Return true if a configuration file is available
	public Boolean checkConfigFile(){
		File f = new File( context.getFilesDir(), "config.properties");
		return f.exists();
	}
}
