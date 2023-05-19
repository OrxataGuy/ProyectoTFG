package com.netfish.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// import org.littleshoot.proxy.HttpProxyServer;
// import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.netfish.Netfish;
import com.netfish.net.NetObject;
import com.netfish.ui.AppWindow;
import com.netfish.ui.LauncherWindow;

public class TCPClient {

	private boolean stop=false, useProxy, hasBody;
	public int loadedObjects, objectCount;
	public int[] attempts;
	public final int MAX_ATTEMPTS =2;
	private LauncherWindow context;
	private JTable table;
	private String primaryURL;
	private int id;
	private Map<String, String> requestHeaders, requestBody;
	private int currentNum;
	
	
	
	private ArrayList<String> urls = new ArrayList<String>();
	private URL url;
	
	public TCPClient(String url) throws MalformedURLException {
		
	}
	
	public TCPClient(LauncherWindow context, int id, String url, JTable table, Map<String, String> requestHeaders, Map<String, String> requestBody) throws MalformedURLException {
		this.id = id;
		this.context = context;
		this.table = table;
		this.requestHeaders = requestHeaders;
		this.requestBody = requestBody;
		this.stop = false;
		this.primaryURL = url;
		this.url = new URL(this.primaryURL);
		this.currentNum = 1;
		this.useProxy = false;
		this.hasBody = false;
		for(Entry<String, String> entry : requestBody.entrySet())
			if(entry.getKey() != "") this.hasBody = true;
	}
	
	public TCPClient(LauncherWindow context, int id, String url, JTable table, Map<String, String> requestHeaders, Map<String, String> requestBody, boolean useProxy) throws MalformedURLException {
		this.id = id;
		this.context = context;
		this.table = table;
		this.requestHeaders = requestHeaders;
		this.requestBody = requestBody;
		this.stop = false;
		this.primaryURL = url;
		this.url = new URL(this.primaryURL);
		this.currentNum = 1;
		this.useProxy = useProxy;
		this.hasBody = false;
		for(Entry<String, String> entry : requestBody.entrySet())
			if(entry.getKey() != "") this.hasBody = true;
	}
    
	
	public void getNetObjects(int type) throws IOException, InterruptedException {
		initializeTable();
		/*HttpProxyServer server;
		if(this.useProxy)
			server = DefaultHttpProxyServer.bootstrap().withPort(8080).start();*/
		
		ArrayList<NetObject> result = new ArrayList<NetObject>();
		String host = url.getHost();
	    loadedObjects = 0;
	    Netfish.print("---------------------------------------");
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Cargando contenido de "+host);
		
		InetAddress ia = InetAddress.getByName(host);
		String ip = ia.getHostAddress();			
		String path = url.getPath();
		
		this.context.getProgressBar().setValue(loadedObjects);
		
		Netfish.print("[PETICIÓN "+id+" ("+url+")] Direción: "+url.getHost());
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Direción canónica: "+ia.getCanonicalHostName());
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] IP: "+ia.getHostAddress());
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Puerto: "+ (url.getPort() == -1 ? String.valueOf(443) : String.valueOf(url.getPort())));
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Directorio: "+ url.getPath());
		Netfish.print("");

		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Creando socket de conexión por el puerto 443");
		
		Request req = new Request(this.primaryURL, ip, path, Request.GET, this.requestHeaders, this.useProxy);
		ArrayList<String> endpoints = req.explore();
		
		if(this.hasBody)
			addNet(req.call(type, requestBody).toNetObject());
		else
			addNet(req.call(type).toNetObject());
		
		this.context.getProgressBar().setMaximum(endpoints.size()+1);
		this.context.getProgressBar().setValue(++loadedObjects);
		
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Objeto base cargado.");
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Cargando objetos del sitio web.");
		
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		for(String endpoint : endpoints)
			if(!stop)
			{
				threadPool.submit(new Runnable() {
					public void run() {
						String ep = endpoint.lastIndexOf("http") == -1 && endpoint.lastIndexOf("www.") == -1 ? primaryURL+endpoint : endpoint;
						if(ep.lastIndexOf("//")>10)
							ep = primaryURL+endpoint.replaceFirst("/", "");
						
						if(ep.indexOf("ytimg") != -1) 
							ep = "https:"+endpoint;

						try {
						//	System.out.println(ep);
							URL url = new URL(ep);
							String host = url.getHost();
							InetAddress ia = InetAddress.getByName(host);
							String ip = ia.getHostAddress();	
							String path = url.getPath();
							
							Netfish.print("[PETICIÓN "+id+" ("+ep+")] Direción: "+url.getHost());
							Netfish.print("[PETICIÓN "+id+" ("+ep+")] Direción canónica: "+ia.getCanonicalHostName());
							Netfish.print("[PETICIÓN "+id+" ("+ep+")] IP: "+ia.getHostAddress());
							Netfish.print("[PETICIÓN "+id+" ("+ep+")] Puerto: "+ (url.getPort() == -1 ? String.valueOf(443) : String.valueOf(url.getPort())));
							Netfish.print("[PETICIÓN "+id+" ("+ep+")] Directorio: "+ url.getPath());
							Netfish.print("");
							Request req = new Request(ep, ip, path, Request.GET, useProxy);
							addNet(req.call(Request.GET).toNetObject());
							context.getProgressBar().setValue(++loadedObjects);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				});
			}
			else Netfish.print("Carga interrumpida por el usuario");		
	}
	
	public void initializeTable() {
		this.context.setNets(new ArrayList<NetObject>());
		DefaultTableModel model = new DefaultTableModel(new String[] {"", "Estado", "URL", "Método", "Dominio", "IP", "Archivo", "Tipo", "Tiempo (ms)"}, 0);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		this.table.setModel(model);
		this.table.setRowSorter(sorter);
		this.table.getColumnModel().getColumn(0).setMaxWidth(50);
		this.table.getColumnModel().getColumn(1).setMaxWidth(50);
		this.table.getColumnModel().getColumn(3).setMaxWidth(AppWindow.METHOD_BOX_WIDTH);
	}
	
	public void addNet(NetObject net) {
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		model.addRow(net.toArray(currentNum++));
		this.context.addNet(net);
	}
	
	

	public void stopLoadingRegs() {
		this.stop = true;
	}
}
