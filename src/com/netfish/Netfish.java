package com.netfish;

import java.util.ArrayList;

import com.netfish.ui.*;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class Netfish {
	public static final String APP_NAME="Netfish";
	public static ArrayList<String> usedHosts;

	public static void print(String string) {
		String text = RegViewerWindow.textPane.getText();
		RegViewerWindow.textPane.setText(text+string+"\n");
	}
}
