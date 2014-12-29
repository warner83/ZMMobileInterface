package it.zm.data;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

// Class representing an event
// <EVENT><ID>2493079</ID><NAME>Event-2493079</NAME><TIME>10/19 17:18:48</TIME><DURATION>32.25</DURATION><FRAMES>32</FRAMES><FPS>1</FPS><TOTSCORE>26</TOTSCORE><AVGSCORE>8</AVGSCORE><MAXSCORE>10</MAXSCORE><ALARMFRAMES>3</ALARMFRAMES><MAXFRAMEID>15</MAXFRAMEID></EVENT>

public class MonitorEvent {
	public String id;
	public String name;
	public String time;
	public String duration;
	public String frames;
	public String fps;
	public String totscore;
	public String avgscore;
	public String maxscore;
	public String alarmframes;
	public String maxframeid;
	
	public String monitor;
	
	public MonitorEvent(){}
	
	public MonitorEvent(Node node){
		NodeList list = node.getChildNodes();	
		
        
		for(int i =0; i < list.getLength(); ++i){	
			
			Log.d("XML", "Name " + list.item(i).getNodeName() + " value " + list.item(i).getFirstChild().getNodeValue());
			
			if( list.item(i).getNodeName().equals("ID") )				
				id = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("TIME") )
				time = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("DURATION") )
				duration = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("FRAMES") )
				frames = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("FPS") )
				fps = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("TOTSCORE") )
				totscore = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("AVGSCORE") )
				avgscore = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("MAXSCORE") )
				maxscore = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("ALARMFRAMES") )
				alarmframes = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("MAXFRAMEID") )
				maxframeid = list.item(i).getFirstChild().getNodeValue();
		}
	}
}
