package com.netfish.core;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.netfish.Netfish;
import com.netfish.net.*;
import com.netfish.ui.AppWindow;
import com.netfish.ui.LauncherWindow;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class TCPClient {
		private boolean stop=false;
		public int loadedObjects, objectCount;
		public int[] attempts;
		public final int MAX_ATTEMPTS =2;
		private LauncherWindow context;
		private JTable table;
		private String primaryURL;
		private int id;
		
		public TCPClient(LauncherWindow context, JTable table) {
			this.context = context;
			this.table = table;
			this.stop = false;
		}
		
		public void stopLoadingRegs() {
			this.stop = true;
		}

	public void getNetObjects(String url, int id) throws IOException {
		this.id = id;
		this.primaryURL = url;
		URL URL = new URL(url);
		String host = URL.getHost();
		loadedObjects = 0;
		Netfish.print("---------------------------------------");
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Cargando contenido de "+host);
		
		InetAddress ia = InetAddress.getByName(host);
		String ip = ia.getHostAddress();			
		String path = URL.getPath();
		this.context.getProgressBar().setValue(0);
		Netfish.print("[PETICIÓN "+id+" ("+url+")] Direción: "+URL.getHost());
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Direción canónica: "+ia.getCanonicalHostName());
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] IP: "+ia.getHostAddress());
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Puerto: "+ (URL.getPort() == -1 ? String.valueOf(443) : String.valueOf(URL.getPort())));
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Directorio: "+ URL.getPath());

		Netfish.print("");
		
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket  socket = (SSLSocket ) factory.createSocket(ia, 443);
		
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Creando socket de conexión por el puerto 443");
		
		
		Request req = new Request(url, socket, ip, path, Request.GET);
		
		ArrayList<NetObject> result = new ArrayList<NetObject>();
		ArrayList<String> objects = req.send().explore();
		result.add(req.toNetObject());
		req.close();
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Objeto base cargado.");
		objectCount = objects.size();
		attempts = new int[objectCount];
		for(int i=0; i<attempts.length; i++)
			attempts[i] = 0;
		
		this.context.getProgressBar().setMaximum(objectCount);
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Cargando objetos del sitio web:");
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		for(String u : objects)
			if(!stop)
			{
				threadPool.submit(new Runnable() {
					public void run() {
						threadableNetObject(factory, u, result);
						
					}
				});
			}
			else Netfish.print("Carga interrumpida por el usuario");
		Netfish.print("");
		Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Carga finalizada");
		Netfish.print("---------------------------------------");
		
		/*IntStream.range(0, objects.size()).parallel().forEach(n->{
			threadableNetObject(factory, objects.get(n), result);
		});*/
			
	}
	
	public void threadableNetObject(SSLSocketFactory factory, String url, ArrayList<NetObject> result) {
		if(!stop)
				try {
				
	
						Netfish.print("");
						URL URL = new URL(url);
						String host = URL.getHost();
						InetAddress ia = InetAddress.getByName(host);
						String ip = ia.getHostAddress();			
						String path = URL.getPath();
						
						Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Direción: "+URL.getHost());
						Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Direción canónica: "+ia.getCanonicalHostName());
						Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] IP: "+ia.getHostAddress());
						Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Puerto: "+ (URL.getPort() == -1 ? String.valueOf(443) : String.valueOf(URL.getPort())));
						Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Directorio: "+ URL.getPath());
						SSLSocket s = (SSLSocket ) factory.createSocket(ia, 443);
						Request r = new Request(url, s, ip, path, Request.GET);
						if ((attempts[loadedObjects] = attempts[loadedObjects]+1) < MAX_ATTEMPTS) {
							result.add(r.toNetObject());
							Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] Objeto cargado");
							r.close();
							this.context.getProgressBar().setValue(this.context.getProgressBar().getValue()+1);
							if(++loadedObjects == objectCount)
							{
								stop = true;
							}
							populateTable(result);
						} else {
							result.add(new NetObject());
							Netfish.print("[PETICIÓN "+id+" ("+primaryURL+")] El objeto no se ha podido cargar correctamente");
							r.close();
							this.context.getProgressBar().setValue(this.context.getProgressBar().getValue()+1);
							if(++loadedObjects == objectCount)
							{
								stop = true;
							}
							populateTable(result);
						}
				} catch (java.net.ConnectException cex) {
					//System.out.println("Falla la conexión para "+url+" por lo que se vuelve a pedir el archivo.");
					if ((attempts[loadedObjects] = attempts[loadedObjects]+1) < MAX_ATTEMPTS)
						threadableNetObject(factory, url, result);
					else {
						Netfish.printEx(cex.getCause().getMessage()+", "+cex.getMessage());
						for(StackTraceElement ste : cex.getStackTrace())
							Netfish.printEx(ste.getClassName()+"("+ste.getMethodName()+":"+ste.getLineNumber()+")");
					}
				} catch(Exception ex) {
					 Netfish.printEx(ex.getCause().getMessage()+", "+ex.getMessage());
					 for(StackTraceElement ste : ex.getStackTrace())
							Netfish.printEx(ste.getClassName()+"("+ste.getMethodName()+":"+ste.getLineNumber()+")");					/*for(StackTraceElement ste : ex.getStackTrace()) {
						System.out.println(ste.getFileName()+"->"+ste.getMethodName());
					}*/
				}	
	}
	
	public void populateTable(ArrayList<NetObject> nets) {
		DefaultTableModel model = new DefaultTableModel(new String[] {"", "Estado", "URL", "Método", "Dominio", "IP", "Archivo", "Tipo"}, 0);
		this.context.setNets(nets);
		int num = 1;
		for(NetObject row : nets)
			model.addRow(row.toArray(num++));;
		this.table.setModel(model);
		this.table.getColumnModel().getColumn(0).setMaxWidth(50);
		this.table.getColumnModel().getColumn(1).setMaxWidth(50);
		this.table.getColumnModel().getColumn(3).setMaxWidth(AppWindow.METHOD_BOX_WIDTH);

	}
	
	/*
	public ArrayList<NetObject> getNetObjects(String url) throws IOException {
		
		URL URL = new URL(url);
		String host = URL.getHost();
		Netfish.print("---------------------------------------");
		Netfish.print("Cargando contenido de "+host);
		
		InetAddress ia = InetAddress.getByName(host);
		String ip = ia.getHostAddress();			
		String path = URL.getPath();
		
		Netfish.print("Direción: "+URL.getHost());
		Netfish.print("Direción canónica: "+ia.getCanonicalHostName());
		Netfish.print("IP: "+ia.getHostAddress());
		Netfish.print("Puerto: "+ (URL.getPort() == -1 ? String.valueOf(443) : String.valueOf(URL.getPort())));
		Netfish.print("Directorio: "+ URL.getPath());

		Netfish.print("");
		
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket  socket = (SSLSocket ) factory.createSocket(ia, 443);
		
		Netfish.print("Creando socket de conexión por el puerto 443");
		
		Request req = new Request(url, socket, ip, path, Request.GET);
		
		ArrayList<NetObject> result = new ArrayList<NetObject>();
		ArrayList<String> objects = req.send().explore();
		result.add(req.toNetObject());
		req.close();
		Netfish.print("Objeto base cargado.");

		Netfish.print("Cargando objetos del sitio web:");
		for(String u : objects) {
			URL uu = new URL(u);
			InetAddress i = InetAddress.getByName(host);
			String p = i.getHostAddress();			
			String ph = uu.getPath();
			Netfish.print("");
			Netfish.print("Direción: "+uu.getHost());
			Netfish.print("Direción canónica: "+i.getCanonicalHostName());
			Netfish.print("IP: "+i.getHostAddress());
			Netfish.print("Puerto: "+ (uu.getPort() == -1 ? String.valueOf(443) : String.valueOf(uu.getPort())));
			Netfish.print("Directorio: "+ uu.getPath());
			
			SSLSocket s = (SSLSocket ) factory.createSocket(i, 443);
			Request r = new Request(u, s, p, ph, Request.GET);
			result.add(r.toNetObject());
			r.close();
		}
		
		Netfish.print("Carga finalizada");
		Netfish.print("---------------------------------------");

		
		return result;
	}*/
	
	public String tcp(String url) {
		try {
			String host = new URL(url).getHost();
			System.out.println("");
			System.out.println("Cargando contenido de "+host);
			
			InetAddress ia = InetAddress.getByName(host);
			String ip = ia.getHostAddress();			
			String path = new URL(url).getPath();
			
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket  socket = (SSLSocket ) factory.createSocket(ia, 443);
			
			Request req = new Request(url, socket, ip, path, Request.GET, true);

			String content = req.send().getContent();
			req.send().explore();
            System.out.println("Termino");
            req.close();
			socket.close();
            
            return content;
                        
		}catch(Exception e) {
			System.err.println("Algo ha ido mal");
			e.printStackTrace();
			return null;
		}
	}
}
