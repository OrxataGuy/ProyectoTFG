package com.netfish.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.netfish.net.NetObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Request {
	
	public String url;
	private String ip, ep;
	private boolean verbose, usesProxy;
	public static final int GET=0, POST=1, PUT=2, PATCH=3, DELETE=4, HEAD=5, OPTIONS=6;
	private int type;
	private Map<String, String> headers, body;
	
	public URL host;
	
	public Request(String url, String ip, String path, int type, Map<String, String> headers, boolean usesProxy) throws IOException {
		this.type = type;
		this.url = url;
		this.host = new URL(this.url);
		this.ip = ip;
		this.ep = this.host.getHost();
		this.verbose = false;
		this.headers = headers;
		this.body = new HashMap<String, String>();
		this.usesProxy = usesProxy;
	}
	
	public Request(String url, String ip, String path, int type, boolean usesProxy) throws IOException {
		this.type = type;
		this.url = url;
		this.host = new URL(this.url);
		this.ip = ip;
		this.ep = this.host.getHost();		
		this.verbose = false;
		this.headers = new HashMap<String, String>();
		this.body = new HashMap<String, String>();
		this.usesProxy = usesProxy;
	}
	
	public Request(String url, String ip, String path, int type, Map<String, String> headers) throws IOException {
		this.type = type;
		this.url = url;
		this.host = new URL(this.url);
		this.ip = ip;
		this.ep = this.host.getHost();
		this.verbose = false;
		this.headers = headers;
		this.body = new HashMap<String, String>();
		this.usesProxy = false;
	}
	
	
	
	public Request(String url, String ip, String path, int type) throws IOException {
		this.type = type;
		this.url = url;
		this.host = new URL(this.url);
		this.ip = ip;
		this.ep = this.host.getHost();		
		this.verbose = false;
		this.headers = new HashMap<String, String>();
		this.body = new HashMap<String, String>();
		this.usesProxy = false;
	}
	
	public Response call(int type,  Map<String, String> body) throws IOException {	
		HttpURLConnection con = (HttpURLConnection) this.host.openConnection();
		String method;
		switch(type) {
			case POST:
			default:
				method = "POST";
				break;
			case PUT:
				method = "PUT";
				break;
			case PATCH:
				method = "PATCH";
				break;
			case OPTIONS:
				method = "OPTIONS";
				break;
		}
		
		con.setRequestMethod(method);
		
	
		con.addRequestProperty("User-Agent", "Java/13.0.2");
		con.addRequestProperty("Accept-Language", "en,es-ES;q=0.8,es;q=0.5,en-US;q=0.3");
		con.addRequestProperty("Accept", "*/*");
		con.addRequestProperty("Connection", "keep-alive");
		con.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
		if(this.usesProxy)
		{
			con.setRequestProperty("Proxy-Connection", "keep-alive");
	        con.setRequestProperty("X-Forwarded-For", "127.0.0.1");
	        con.setRequestProperty("Via", "1.1 "+this.ep);
		}
		
		for(Entry<String, String> entry : this.headers.entrySet())
			con.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
		
		String data = "";
		for(Entry<String, String> entry : this.body.entrySet())
			data += entry.getKey()+":"+entry.getValue()+";";
		con.setDoOutput(true);
		 // Write to the connection
	    OutputStream output = con.getOutputStream();
	    output.write(data.getBytes("UTF-8"));
	    output.close();
		
	    return new Response(method, this.url, this.host, this.ip, System.currentTimeMillis(), con);
	}
	
	public Response call(int type) throws IOException {	
		HttpURLConnection con = (HttpURLConnection) this.host.openConnection();
		String method;
		switch(type) {
			case GET:
			default:
				method = "GET";
				break;
			case DELETE:
				method = "DELETE";
				break;
			case OPTIONS:
				method = "OPTIONS";
				break;
		}
		
		con.setRequestMethod(method);
		
	
		con.addRequestProperty("User-Agent", "Java/13.0.2");
		con.addRequestProperty("Accept-Language", "en,es-ES;q=0.8,es;q=0.5,en-US;q=0.3");
		con.addRequestProperty("Accept", "*/*");
		con.addRequestProperty("Connection", "keep-alive");
		con.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
		if(this.usesProxy)
		{
			con.setRequestProperty("Proxy-Connection", "keep-alive");
	        con.setRequestProperty("X-Forwarded-For", "127.0.0.1");
	        con.setRequestProperty("Via", "1.1 "+this.ep);
		}
		
		for(Entry<String, String> entry : this.headers.entrySet())
			con.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
		for(Entry<String, String> entry : this.body.entrySet())
			con.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
		
		con.setDoOutput(true);
		
	    return new Response(method, this.url, this.host, this.ip, System.currentTimeMillis(), con);
	}
	
	
	public ArrayList<String> explore() throws IOException {
		ArrayList<String> endpoints = new ArrayList<String>();
		Document doc = Jsoup.connect(this.url).get();

	    for (Element link : doc.select("link[href],script[src],img[src]")) 
	        endpoints.add(link.attr("href") != "" ? link.attr("href") : link.attr("src"));
	    
	    return endpoints;
	}
	
	
}
