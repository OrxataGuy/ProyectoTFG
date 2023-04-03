package com.netfish.core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.netfish.Netfish;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class Response {
	private String host;
	private BufferedReader in;
	private PrintWriter out;
	private String content;
	private boolean ssl, verbose, closeAtFinish;
	
	public Response(String host, BufferedReader in, PrintWriter out, boolean ssl, boolean claf, boolean verbose) {
		this.in = in;
		this.out = out;
		this.content = "";
		this.host = host;
		this.ssl = ssl;
		this.verbose = verbose;
		this.closeAtFinish = claf;
	}
	
	public Map<String, String> getHeaders() throws IOException {
		String line = this.in.readLine();
		boolean endHeaders=false;
		Map<String, String> headers = new HashMap<String, String>();	
		 while (line != null) {
			 if(!endHeaders) {
				 if(!(endHeaders = (line.length() < 12 || !Character.isUpperCase(line.charAt(0))))) {
					 if(line.length() > 5 && line.substring(0,4) == "HTTP") {
						 headers.put("Response", line);
						 Netfish.print("[RESPONSE]: "+line);
					 }else {
						 String[] header = line.split(":");
						 if(header.length > 1)
							 headers.put(header[0], header[1]);
						 else Netfish.print("[RESPONSE]: "+header[0]);
					 }
				 }
			   line = in.readLine();
			 }else break;
		   
         }
		 if(this.closeAtFinish) {
			 this.in.close();
		     this.out.close();
		 }

		 return headers;
	}
	
	public String getContent() throws IOException {
		String line = this.in.readLine();
		boolean endHeaders=false;
		while (line != null) {
			 if(!endHeaders) {
				 if(!(endHeaders = line.length() == 0)) {
					 if(line.substring(0,4) == "HTTP") {
					 }else {
						 
					 }
				 }
			 }
				 
			 if(!endHeaders) System.out.println(line.length()+ ": "+line);

         	this.content += line;
         	log(line);
         	line = in.readLine();
         }
		 if(this.closeAtFinish) {
			 this.in.close();
		     this.out.close();
		 }
		return this.content;
	}
	
	public ArrayList<String> explore() throws IOException {
		Netfish.print("Procesado del objeto base para listar todos los objetos a cargar");
		String line = this.in.readLine();
		ArrayList<String> contents = new ArrayList<String>();
		String[] aux;
        while (line != null) {
            if(stringContainsFile(line))
            	if((aux = line.split("src=\"")).length > 1)
            		contents.add(aux[1].split("\"")[0].replace("//", ""));
            	else if ((aux = line.split("href=\"")).length > 1)
            		contents.add(aux[1].split("\"")[0].replace("//", ""));
        	log(line);
            line = this.in.readLine();
        }
        prepare(this.host, contents, this.ssl);
        if(this.closeAtFinish) {
			 this.in.close();
		     this.out.close();
		 }
        Netfish.print("Se cargarán: "+contents.size()+" objetos");
		return contents;
	}
	
	private void prepare(String host, ArrayList<String> contents, boolean ssl) {
		for(int i=0; i<contents.size(); i++) {
			if(contents.get(i).charAt(0) == '/')
				contents.set(i, host+contents.get(i));
			if(contents.get(i).charAt(0) == 'h') {
				String[] aux = contents.get(i).split(":");
				if(ssl && aux[0] == "http") contents.set(i, contents.get(i).replace("http:", "https:"));
				else if (!ssl && aux[0] == "https") contents.set(i, contents.get(i).replace("https:", "http:"));
			} else {
				if(ssl) contents.set(i, "https://"+contents.get(i));
				else  contents.set(i, "http://"+contents.get(i).replace("https:", "http:"));
			}
		}
		log(contents);
	}
	
	private boolean stringContainsFile(String input) {
		String[] items = new String[] {
				".png",".jpg",".jpeg",".gif",".mp4",".mpeg",".css",".js"
		};
	    return Arrays.stream(items).anyMatch(input::contains);
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
