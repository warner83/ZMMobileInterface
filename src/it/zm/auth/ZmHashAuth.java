package it.zm.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class ZmHashAuth {
	private String user;
	private String password;
	private String url;
	private HttpClient client;

	public ZmHashAuth(String ur,String u, String p, HttpClient cl){
		user = u;
		password = p;
		url = ur;
		client = cl;
	}
	
	public Boolean checkAuthNeeded(){
		
		HttpGet request = new HttpGet(url);
	 
		request.addHeader("User-Agent", "Mozilla");
		HttpResponse response;

		try {
			Log.d("ZMAUTH","request "+url);
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
			
			System.out.println(result.toString());
			
			int ini = result.toString().indexOf("Password");
			
			if( ini == -1 )
				return false;
			else 
				return true;
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String getAuthHash(){
		String auth = "";
				 
		// Perform login through an HTTP POST
		HttpPost post = new HttpPost(url+"?skin=classic");
	 
		// add header
		post.setHeader("User-Agent", "Mozilla");
	 
		// Set in the header the username and password
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", user));
		urlParameters.add(new BasicNameValuePair("password", password));
		urlParameters.add(new BasicNameValuePair("action", "login"));
		urlParameters.add(new BasicNameValuePair("view", "postlogin"));
		
		// Do the job
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));	
			post.setHeader("Pragma","no-cache");
			post.setHeader("Cache-Control","no-cache");
	        post.setHeader("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
	 
			HttpResponse response = client.execute(post);
			
			System.out.println("Response Code : " 
		                + response.getStatusLine().getStatusCode());
		 
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));
		 
			// Consume the return, can I trash it directly?
			// TODO perform some check that the page returned is the one expected
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			System.out.println(result.toString());
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get the auth token
		
		// That is done opening the fist video stream and get the auth token
		
		// We assume that there is at least one webcam...
		// TODO perform a check
		url += "?view=watch&mid=1";
		HttpGet request = new HttpGet(url);
	 
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
			
			System.out.println(result.toString());
			
			int ini = result.toString().indexOf("auth");
			int end = result.toString().indexOf('"', ini);
			auth = result.toString().substring(ini, end);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	 
		return auth;
	}
}
