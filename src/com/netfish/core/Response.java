package com.netfish.core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netfish.Netfish;
import com.netfish.net.NetObject;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class Response {
	private Map<String, String> reqHeaders, resHeaders;
	private String url, ip, method;
	private URL host;
	private boolean verbose;
	private Object content;
	private int status;
	private long timeout;

	
	public Response(String method, String url, URL host, String ip, long timeMillis, HttpURLConnection request) throws IOException {
		this.reqHeaders = new HashMap<String, String>();
		this.resHeaders = new HashMap<String, String>();
		
		for(Map.Entry<String, List<String>> entry : request.getRequestProperties().entrySet()) 
			this.reqHeaders.put(entry.getKey(), entry.getValue().get(0));
		
		for(Map.Entry<String, List<String>> entry : request.getHeaderFields().entrySet()) 
			this.resHeaders.put(entry.getKey(), entry.getValue().get(0));
		
		this.method = method;
		this.url = url;
		this.ip = ip;
		this.host = host;
		this.status = request.getResponseCode();
	//	this.content = request.getContent();
		this.timeout = System.currentTimeMillis() - timeMillis;
	}
		
	public NetObject toNetObject() {
		return new NetObject(this.url, this.host.getHost(), this.host.getFile(), this.resHeaders.get("Content-Type"), this.method, this.status, this.ip, "", this.timeout, this.reqHeaders, this.resHeaders);
	}


	private void log(String str) {
		if(this.verbose) System.out.println(str);
	}
	
	private void log(ArrayList<String> array) {
		System.out.println(array.size()+" elementos:");
		if(this.verbose) 
			for(String s : array)
				System.out.println(s);
	}

}
