package com.netfish;

import java.util.ArrayList;

import javax.swing.text.BadLocationException;

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
		try {
			RegViewerWindow.printNormalMessage(string);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printEx(String string) {
		try {
			RegViewerWindow.printBoldMessage(string);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
