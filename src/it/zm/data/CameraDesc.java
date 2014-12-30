package it.zm.data;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

// Class representing a class
// <MONITOR><ID>1</ID><NAME>Ingresso</NAME><FUNCTION>Modect</FUNCTION><NUMEVENTS>390</NUMEVENTS><ENABLED>1</ENABLED><ZMC>1</ZMC><ZMA>1</ZMA><STATE>OK</STATE><WIDTH>1280</WIDTH><HEIGHT>800</HEIGHT><PAGEOFF>0</PAGEOFF><EVENTS/></MONITOR>

public class CameraDesc {
	public String id;
	public String name;
	public String function;
	public String numevents;
	public String state;
	public String width;
	public String height;
		
	public CameraDesc(){}
	
	public CameraDesc(Node node){
		NodeList list = node.getChildNodes();	
		
        
		for(int i =0; i < list.getLength(); ++i){	
			
			
			if( list.item(i).getNodeName().equals("ID") )				
				id = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("NAME") )
				name = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("FUNCTION") )
				function = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("NUMEVENTS") )
				numevents = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("STATE") )
				state = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("WIDTH") )
				width = list.item(i).getFirstChild().getNodeValue();
			else if( list.item(i).getNodeName().equals("HEIGHT") )
				height = list.item(i).getFirstChild().getNodeValue();
		}
	}
}
