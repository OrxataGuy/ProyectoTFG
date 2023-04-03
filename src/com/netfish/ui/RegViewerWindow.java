package com.netfish.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import javax.swing.JTextPane;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class RegViewerWindow {

	private JFrame frmVisorDeRegistros;
	public static JTextPane textPane;
	
	/**
	 * Create the application.
	 */
	public RegViewerWindow() {
		initialize();
	}
	
	public JFrame getFrame() {
		return this.frmVisorDeRegistros;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVisorDeRegistros = new JFrame();
		frmVisorDeRegistros.setTitle("Visor de registros");
		frmVisorDeRegistros.setBounds(100, 100, 565, 443);
		frmVisorDeRegistros.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		frmVisorDeRegistros.getContentPane().add(new JScrollPane(textPane));
	}

}
