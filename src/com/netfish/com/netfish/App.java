package com.netfish;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.netfish.ui.AppWindow;

public class App {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Netfish.usedHosts = new ArrayList<String>();
					Netfish.usedHosts.add("");
					Netfish.usedHosts.add("https://www.upv.es");
					Netfish.usedHosts.add("https://www.uv.es");
					AppWindow window = new AppWindow();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

}
