package com.fishnet.core;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fishnet.Fishnet;
import com.fishnet.net.*;

import javax.net.ssl.SSLSocket;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class Request {

	public String url;
	public static final int GET=0, POST=1, PUT=2, PATCH=3, DELETE=4, HEAD=5, OPTIONS=6;
	private boolean ssl, base, verbose;
	private String host, ip;
	private int type;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private SSLSocket sslsocket;
	private Map<String, String> headers;
	
	public Request(String url, Socket socket, String ip, String path, int type) throws IOException {
		this.ssl=false;
		this.type = type;
		this.url = url;
		this.host = new URL(this.url).getHost();
		this.base = this.url==this.host;
		this.ip = ip;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.verbose = false;
		this.socket = socket;
		this.headers = new HashMap<String, String>();

	}
	
	public Request(String url, SSLSocket socket, String ip, String path, int type) throws IOException {
		this.ssl=true;
		this.type = type;
		this.url = url;
		this.host = new URL(this.url).getHost();
		this.base = this.url==this.host;
		this.ip = ip;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.verbose = false;
		this.sslsocket = socket;
		this.headers = new HashMap<String, String>();
	}
	
	public Request(String url, Socket socket, String ip, String path, int type, boolean verbose) throws IOException {
		this.ssl=false;
		this.type = type;
		this.url = url;
		this.host = new URL(this.url).getHost();
		this.base = this.url==this.host;
		this.ip = ip;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.verbose = verbose;
		this.socket = socket;
		this.headers = new HashMap<String, String>();

	}
	
	public Request(String url, SSLSocket socket, String ip, String path, int type, boolean verbose) throws IOException {
		this.ssl=true;
		this.type = type;
		this.url = url;
		this.host = new URL(this.url).getHost();
		this.base = this.url==this.host;
		this.ip = ip;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.verbose = verbose;
		this.sslsocket = socket;
		this.headers = new HashMap<String, String>();

	}
	
	public Response send() {
		return send(false);
	}
	
	public Response send(boolean closeAtFinish) {
		String target = this.base ? "/" : this.url;
		switch(this.type)
		{
			case Request.GET:
				this.out.println("GET "+target+" HTTP/1.0");
				logReq("GET "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: GET "+target+" HTTP/1.0");

				break;
			case Request.POST:
				this.out.println("POST "+target+" HTTP/1.0");
				logReq("POST "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: POST "+target+" HTTP/1.0");
				break;
			case Request.PUT:
				this.out.println("PUT "+target+" HTTP/1.0");
				logReq("PUT "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: PUT "+target+" HTTP/1.0");
				break;
			case Request.PATCH:
				this.out.println("PATCH "+target+" HTTP/1.0");
				logReq("PATCH "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: PATCH "+target+" HTTP/1.0");
				break;
			case Request.DELETE:
				this.out.println("DELETE "+target+" HTTP/1.0");
				logReq("DELETE "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: DELETE "+target+" HTTP/1.0");
				break;
			case Request.HEAD:
				this.out.println("HEAD "+target+" HTTP/1.0");
				logReq("HEAD "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: HEAD "+target+" HTTP/1.0");
				break;
			case Request.OPTIONS:
				this.out.println("OPTIONS "+target+" HTTP/1.0");
				logReq("OPTIONS "+target+" HTTP/1.0");
				Fishnet.print("[REQUEST]: OPTIONS "+target+" HTTP/1.0");
				break;
		}
		
		this.out.println("Host: " + this.host);
		logReq("Host: " + this.host);
        // Cabeceras
		this.out.println("User-Agent: Java/13.0.2");
		logReq("User-Agent: Java/13.0.2");
		this.out.println("Accept-Language: en,es-ES;q=0.8,es;q=0.5,en-US;q=0.3");
		logReq("Accept-Language: en,es-ES;q=0.8,es;q=0.5,en-US;q=0.3");
		this.out.println("Accept: */*");
		logReq("Accept: */*");
		this.out.println("Connection: keep-alive");
		logReq("Connection: keep-alive");
		this.out.println("Accept-Encoding: gzip, deflate, br");
		logReq("Accept-Encoding: gzip, deflate, br");
		log("");
        // Blanco
		this.out.println();

				
		return new Response(this.host, this.in, this.out, this.ssl, closeAtFinish, this.verbose);
	}
	
	public NetObject toNetObject() throws IOException {
		Response res = send();
		String file, type;
		String[] urlList = this.url.split(this.host);

		if(urlList.length==1) {
			file = "/";
			type = "document";
		}else {
			file = urlList[1];
			type = file.substring(file.lastIndexOf('.')+1, file.length());
		}
		
		NetObject result = new NetObject(this.url, this.host, file, type, "GET", "200", this.ip, "");
		result.setRequestHeaders(this.headers);
		result.setResponseHeaders(res.getHeaders());
		return result;
	}
		
	public void close() throws IOException {
		this.in.close();
		this.out.close();
		if(this.ssl) this.sslsocket.close();
		else this.socket.close();
	}
	
	private void log(String str) {
		if(this.verbose) System.out.println(str);
	}
	
	private void logReq(String str) {
		String[] req = str.split(":");
		if(req.length==1) this.headers.put("Request", str);
		else this.headers.put(req[0], req[1]);
		if(this.verbose) System.out.println(str);
	}
}
