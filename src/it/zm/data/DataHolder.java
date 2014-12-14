package it.zm.data;

import it.zm.xml.DataCameras;

public class DataHolder {
	public ConfigData confData;
	
	public DataCameras dc;
	
	public String auth;
	
	private static final DataHolder holder = new DataHolder();
	public static DataHolder getDataHolder() {return holder;}
}
