package it.zm.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigData {
	public String baseUrl;
	public String username;
	public String password;
	public Boolean fullOnActive;
	public Boolean bandwidthSaverActive;
	
	// Save and handle properties
	private Properties prop;
	
	public ConfigData(){
		prop = new Properties();
		 
		//set the default properties value
		prop.setProperty("baseUrl", "localhost");
		prop.setProperty("username", "admin");
		prop.setProperty("password", "admin");
		prop.setProperty("fullOnActive", "false");
		prop.setProperty("bandwidthSaverActive", "false");
		
		baseUrl = "localhost";
		username = "admin";
		password = "admin";
		fullOnActive = false;
		bandwidthSaverActive = false;
	}
	
	public Boolean save(){
		
		prop.setProperty("baseUrl", baseUrl);
		prop.setProperty("username", username);
		prop.setProperty("password", password);
		prop.setProperty("fullOnActive", fullOnActive.toString());
		prop.setProperty("bandwidthSaverActive", bandwidthSaverActive.toString());
		
    	try {	
    		//save properties to project root folder
    		prop.store(new FileOutputStream("config.properties"), null);
    	} catch (IOException ex) {
    		ex.printStackTrace();
    		return false;
        }
    	return true;
	}
	
	public Boolean load(){
		
    	try {	
    		//load properties to project root folder
    		prop.load(new FileInputStream("config.properties"));
    	} catch (IOException ex) {
    		ex.printStackTrace();
    		return false;
        }
    	
    	baseUrl = prop.getProperty("baseUrl");
    	username = prop.getProperty("username");
    	password = prop.getProperty("password");
    	fullOnActive = Boolean.valueOf(prop.getProperty("fullOnActive"));
    	bandwidthSaverActive = Boolean.valueOf(prop.getProperty("bandwidthSaverActive"));
    	
    	return true;
	}
	
	// Return true if a configuration file is available
	public Boolean checkConfigFile(){
		File f = new File("config.properties");
		return f.exists();
	}
}
