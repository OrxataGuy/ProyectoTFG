package com.netfish.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class RegViewerWindow {

	private JFrame frmVisorDeRegistros;
	public static JTextPane textPane;
	private static StyledDocument doc;
	private static SimpleAttributeSet normal, bold;
	
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
		
		doc = (StyledDocument) textPane.getDocument();

        normal = new SimpleAttributeSet();
        StyleConstants.setFontFamily(normal, "SansSerif");
        StyleConstants.setFontSize(normal, 16);

        bold = new SimpleAttributeSet(normal);
        StyleConstants.setBold(bold, true);
		
		frmVisorDeRegistros.getContentPane().add(new JScrollPane(textPane));
	}
	
	public static void printNormalMessage(String text) throws BadLocationException {
		doc.insertString(doc.getLength(), text + "\n", normal);
	}
	
	public static void printBoldMessage(String text) throws BadLocationException {
		doc.insertString(doc.getLength(), text + "\n", bold);
	}

}
