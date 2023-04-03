package com.netfish.net;

import java.util.*;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class NetObject {
	public String url, host, method, file, type, status, ip, reference, body;
	private Map<String, String> requestHeaders, responseHeaders;
	
	public NetObject(String url, String host, String file, String type, String method, String status, String ip, String reference) {
		this.url = url;
		this.host = host;
		this.method = method;
		this.status = status;
		this.file = file;
		this.type = type;
		this.ip = ip;
		this.reference = reference;
		this.body = "";
		this.responseHeaders = new HashMap<String, String>();
		this.requestHeaders = new HashMap<String, String>();
	}
	
	/**
	 * GENERAL
	 * Método, Host, Código de estado, Dirección remota, "Política de referencia"
	 * 
	 * ENCABEZADOS DE RESPUESTA
	 * "accept-ranges", "access-control-allow-origin", "age", "alt-svc", "cache-control", "content-encoding", "content-length"
	 * 
	 */
	
	public void setResponseHeaders(Map<String, String> headers) {
		this.responseHeaders = headers;
	}
	
	public void setRequestHeaders(Map<String, String> headers) {
		this.responseHeaders = headers;
	}
	
	public void printHeaders() {
		Set<Map.Entry<String, String>> requestSet = getRequestHeaders().entrySet();
		Set<Map.Entry<String, String>> responseSet = getResponseHeaders().entrySet();
		
		ArrayList<Map.Entry<String,String>> rows = new ArrayList<Map.Entry<String,String>>(requestSet);
		rows.addAll(responseSet);
		
		System.out.println(rows);
		
		//return headers;
	}
	
	public String[][] getHeaders() {
		Set<Map.Entry<String, String>> responseSet = getResponseHeaders().entrySet();
		ArrayList<Map.Entry<String,String>> rows = new ArrayList<Map.Entry<String,String>>(responseSet);
		String[][] result = new String[rows.size()][2];
		for(int i=0; i<rows.size(); i++) {
			Map.Entry<String, String> row = rows.get(i);
			result[i][0] = row.getKey();
			result[i][1] = row.getValue();
		}
			
		return result;
	}
	

	public Map<String,String> getRequestHeaders () {
		if(this.requestHeaders.size()== 0)
		{
			this.requestHeaders.put("accept-ranges", "byte");	
			this.requestHeaders.put("content-type", "text/html; charset=utf-8");
			this.requestHeaders.put("date", "Sat, 01 Apr 2023 21:04:08 GMT");
			this.requestHeaders.put("age", "45634");
			this.requestHeaders.put("cache-control", "private");
			this.requestHeaders.put("content-encoding", "gzip");
			this.requestHeaders.put("content-length", "345675");
		}
		return this.requestHeaders;
	}
	
	public Map<String,String> getResponseHeaders () {
		if(this.responseHeaders.size()== 0)
		{
			this.responseHeaders.put("accept-ranges", "byte");	
			this.responseHeaders.put("content-type", "text/html; charset=utf-8");
			this.responseHeaders.put("date", "Sat, 01 Apr 2023 21:04:08 GMT");
			this.responseHeaders.put("age", "45634");
			this.responseHeaders.put("cache-control", "private");
			this.responseHeaders.put("content-encoding", "gzip");
			this.responseHeaders.put("content-length", "345675");
		}
		return this.responseHeaders;
	}
	
	//  String url, host, method, file, type, status, ip,
	public Object[] toArray() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(this.status);
		result.add(this.url);
		result.add(this.method);
		result.add(this.host);
		result.add(this.ip);
		result.add(this.file);
		result.add(this.type);
		return result.toArray();
	}
	
	public Object[] toArray(int number) {
		ArrayList<String> result = new ArrayList<String>();
		result.add(String.valueOf(number));
		result.add(this.status);
		result.add(this.url);
		result.add(this.method);
		result.add(this.host);
		result.add(this.ip);
		result.add(this.file);
		result.add(this.type);
		return result.toArray();
	}
}
