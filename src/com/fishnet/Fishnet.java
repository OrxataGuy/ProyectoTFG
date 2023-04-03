package com.fishnet;

import java.util.ArrayList;

import com.fishnet.ui.*;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class Fishnet {
	public static final String APP_NAME="Fishnet";
	public static ArrayList<String> usedHosts;

	public static void print(String string) {
		String text = RegViewerWindow.textPane.getText();
		RegViewerWindow.textPane.setText(text+string+"\n");
	}
}
