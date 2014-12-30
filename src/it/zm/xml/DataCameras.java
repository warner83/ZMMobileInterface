package it.zm.xml;

import it.zm.data.CameraDesc;
import it.zm.data.MonitorEvent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.client.HttpClient;
import org.w3c.dom.NodeList;

public class DataCameras extends DataManagement {
	
	public DataCameras(String baseUrl, HttpClient cl) {
		super(baseUrl, cl);
	}

	@Override
	protected String getOperationSuffix() {
		return "?skin=xml";
	}

	public int getNumCameras(){
		if( !init )
			return 0; // I still have to fetch data
		
		String expression = "/ZM_XML/MONITOR_LIST/MONITOR[ENABLED=1]/ID	";
		
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
						
			return nodeList.getLength();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return 0;
		}
		
	}
	
	public List getAllCameras(){
		List<CameraDesc> ret = new ArrayList<CameraDesc>();
		
		String expression = "/ZM_XML/MONITOR_LIST/MONITOR[ENABLED=1]";
		
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
						
			for(int i = 0; i < nodeList.getLength(); ++i ){
				
				try{
					ret.add(new CameraDesc(nodeList.item(i)));
				} catch (Exception e){
					
					// Just skip and go on parsing
					
				}
				
			}
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
		return ret;
	}
	
	public List getIDs(){
		List<String> ret = new ArrayList<String>();
		
		String expression = "/ZM_XML/MONITOR_LIST/MONITOR[ENABLED=1]/ID	";
		
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
						
			for(int i = 0; i < nodeList.getLength(); ++i ){
				ret.add(new String(nodeList.item(i).getFirstChild().getNodeValue()));
			}
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
		return ret;
	} 
	
	public List getNames(){
		List<String> ret = new ArrayList<String>();
		
		String expression = "/ZM_XML/MONITOR_LIST/MONITOR[ENABLED=1]/NAME";
		
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
						
			for(int i = 0; i < nodeList.getLength(); ++i ){
				ret.add(new String(nodeList.item(i).getFirstChild().getNodeValue()));
			}
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
		return ret;
	}
	
	public int getWidth(String id){
		int ret=0;
		
		String expression = "/ZM_XML/MONITOR_LIST/MONITOR[ID="+id+"]/WIDTH";
		
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
						
			ret = Integer.valueOf(new String(nodeList.item(0).getFirstChild().getNodeValue()));
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return 0;
		}
		
		return ret;
	}
	
	public int getHeight(String id){
		int ret=0;
		
		String expression = "/ZM_XML/MONITOR_LIST/MONITOR[ID="+id+"]/HEIGHT";
		
		try {
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
						
			ret = Integer.valueOf(new String(nodeList.item(0).getFirstChild().getNodeValue()));
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return 0;
		}
		
		return ret;
	}
	
	// This function evaluates the percentage of the image to be requested in order to optimize bandwidth
	public int getPerc(String id, int width, int height){
		int ret = 0;
		
		int imagew = getWidth(id);
		int imageh = getHeight(id);
				
		double ratiow = (double) width / imagew * 100;
		double ratioh = (double) height / imageh * 100;
		
		ret = (int) Math.ceil( Math.max(ratiow, ratioh) );
	
                if(ret > 100)
                    // 100% maximum scale
                    ret = 100;

		return ret;
	}
}
