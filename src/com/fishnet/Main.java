package com.fishnet;

import java.awt.EventQueue;
import java.util.ArrayList;

import com.fishnet.ui.AppWindow;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class Main {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Fishnet.usedHosts = new ArrayList<String>();
					Fishnet.usedHosts.add("");
					Fishnet.usedHosts.add("https://www.upv.es");
					Fishnet.usedHosts.add("https://www.uv.es");
					AppWindow window = new AppWindow();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
