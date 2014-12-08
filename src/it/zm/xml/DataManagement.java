package it.zm.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class DataManagement {
	protected XPath xPath;
	
	// Retrived xml data
	protected Document xmlDocument;
	
	// Url base
	protected String url;
	
	// True if data has been fetched
	Boolean init;
	
	HttpClient client;
	
	public DataManagement(String baseUrl, HttpClient cl){
		xPath = XPathFactory.newInstance().newXPath();
		url = baseUrl; // Final URL will be base + suffix of the operation
		init = false;
		client = cl;
	}	
	
	protected abstract String getOperationSuffix();
	
	// Maybe this should be performed automatically when the object is created
	public void fetchData(){
		
		// Update url... just in case
		String u = url + getOperationSuffix();
		
		System.out.println(u);
		
		HttpGet request = new HttpGet(u);
		
		//System.out.println(u);
		 
		request.addHeader("User-Agent", "Mozilla");
		HttpResponse response;
		try {
			response = client.execute(request);
			System.out.println("Response Code : " 
	                + response.getStatusLine().getStatusCode());
	 
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			// Set fetched data
			String xml = result.toString();
			
			System.out.println(xml);
			
			// Create the xml document
			DocumentBuilderFactory builderFactory =
			        DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
			    builder = builderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
			    e.printStackTrace(); 
			}
			
			xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			init = true;
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
