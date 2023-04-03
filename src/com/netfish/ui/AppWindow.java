package com.netfish.ui;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.netfish.Netfish;
import com.netfish.core.*;
import com.netfish.net.NetObject;

import javax.swing.JSplitPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
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
public class AppWindow {

	private JFrame frmFishnet;
	private JTable table;
	private JPanel toolsPanel, reqPanel, netPanel;
	public static ArrayList<NetObject> nets;
	public static final int TOOLS_PANEL_HEIGHT = 30, 
			TOOLS_PANEL_PREFERRED_WIDTH = 800, 
			REQ_PANEL_HEIGHT = 30, 
			REQ_PANEL_PREFERRED_WIDTH = 800, 
			METHOD_BOX_WIDTH = 100,
			OPTIONS_PANEL_PREFERRED_HEIGHT = 100;
	private JPanel visorPanel;
	private JComboBox<Object> urlBox;
	private JSplitPane splitPane;
	
	public boolean running;
	public RegViewerWindow regViewer;

	public TCPClient tcpManager;
	private JTextPane headersText;
	private JMenuItem mntmNewMenuItem;
	
	/**
	 * Create the application.
	 */
	public AppWindow() {
		this.running = false;
		this.tcpManager = new TCPClient();
		nets = new ArrayList<NetObject>(); //DummyOps.getNetObjects();
		initialize();
		populateTable();
		populateURLBox();
		regViewer = new RegViewerWindow();
	}
	
	public JFrame getFrame() {
		return this.frmFishnet;
	}
		
	public void populateURLBox() {
		DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(Netfish.usedHosts.toArray());
		urlBox.setModel(model);
	}
	
	public void populateTable() {
		DefaultTableModel model = new DefaultTableModel(new String[] {"", "Estado", "URL", "Método", "Dominio", "IP", "Archivo", "Tipo"}, 0);
		int num = 1;
		for(NetObject row : nets)
			model.addRow(row.toArray(num++));
		this.table.setModel(model);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		table.getColumnModel().getColumn(3).setMaxWidth(METHOD_BOX_WIDTH);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFishnet = new JFrame();
		frmFishnet.setTitle("Analizador web de "+Netfish.APP_NAME);
		frmFishnet.setBounds(100, 100, 800, 700);
		frmFishnet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmFishnet.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Opciones");
		menuBar.add(mnNewMenu);
		
		mntmNewMenuItem = new JMenuItem("Abrir visor de registros");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regViewer.getFrame().setVisible(true);
			}
		});
		mnNewMenu.add(mntmNewMenuItem);
		
	
		frmFishnet.getContentPane().setLayout(new BoxLayout(frmFishnet.getContentPane(), BoxLayout.Y_AXIS));
		
		toolsPanel = new JPanel();
		toolsPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		toolsPanel.setPreferredSize(new Dimension(TOOLS_PANEL_PREFERRED_WIDTH, TOOLS_PANEL_HEIGHT));
		toolsPanel.setMaximumSize(new Dimension(9999, TOOLS_PANEL_HEIGHT));
		toolsPanel.setMinimumSize(new Dimension(1, TOOLS_PANEL_HEIGHT));
		frmFishnet.getContentPane().add(toolsPanel);
		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
		
		
		reqPanel = new JPanel();
		reqPanel.setPreferredSize(new Dimension(REQ_PANEL_PREFERRED_WIDTH, REQ_PANEL_HEIGHT));
		reqPanel.setMaximumSize(new Dimension(9999, REQ_PANEL_HEIGHT));
		reqPanel.setMinimumSize(new Dimension(1, REQ_PANEL_HEIGHT));
		reqPanel.setLayout(new BoxLayout(reqPanel, BoxLayout.X_AXIS));
		urlBox = new JComboBox<Object>(Netfish.usedHosts.toArray());
		urlBox.setEditable(true);
		reqPanel.add(urlBox);
		
		JButton goButton = new JButton("▶"); // ▶ ■ 
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(running) {
					TCPClient.stop = true;
					goButton.setText("▶");
				}else {
					TCPClient.stop = false;
					goButton.setText("■");
					try {
						String url = urlBox.getSelectedItem().toString();
						tcpManager.getNetObjects(url, table);
					} catch (IOException e1) {
						Netfish.print(e1.getMessage());
						e1.printStackTrace();
					}
				}
				running = !running;
				
			}
		});
		reqPanel.add(goButton);
		
		
		String[] options = new String[] {"GET","POST","PUT","DELETE", "PATCH", "HEAD", "OPTIONS"};
		JComboBox<String> methodSelectorBox = new JComboBox<String>(options);
		methodSelectorBox.setPreferredSize(new Dimension(METHOD_BOX_WIDTH, REQ_PANEL_HEIGHT));
		methodSelectorBox.setMaximumSize(new Dimension(METHOD_BOX_WIDTH, REQ_PANEL_HEIGHT));
		reqPanel.add(methodSelectorBox);
		frmFishnet.getContentPane().add(reqPanel);
		
		
		
		netPanel = new JPanel();
		netPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		netPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		table = new JTable();
		table.getTableHeader().setResizingAllowed(true);
		table.setRowHeight(30);
		netPanel.add(new JScrollPane(table));
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				headersText.setText("");
				int index = table.getSelectedRow();
				NetObject net = nets.get(index);
				StyledDocument doc = (StyledDocument) headersText.getDocument();

		        SimpleAttributeSet normal = new SimpleAttributeSet();
		        StyleConstants.setFontFamily(normal, "SansSerif");
		        StyleConstants.setFontSize(normal, 16);

		        SimpleAttributeSet bold = new SimpleAttributeSet(normal);
		        StyleConstants.setBold(bold, true);

		        try {
		        	doc.insertString(doc.getLength(), "Cabeceras de la petición:" + "\n", bold);
					for(Map.Entry <String,String> entry : net.getRequestHeaders().entrySet()) 
						doc.insertString(doc.getLength(), entry.getKey()+": "+entry.getValue() + "\n", normal);
					doc.insertString(doc.getLength(), "\n", normal);
					doc.insertString(doc.getLength(), "Cabeceras de la respuesta:" + "\n", bold);
					for(Map.Entry <String,String> entry : net.getResponseHeaders().entrySet()) 
						doc.insertString(doc.getLength(), entry.getKey()+": "+entry.getValue() + "\n", normal);
					
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		       
				
			}
		});

		visorPanel = new JPanel();
		visorPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		// frame.getContentPane().add(visorPanel);
		//visorPanel.setPreferredSize(new Dimension(800, 240));
		visorPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmFishnet.getContentPane().add(splitPane);
		splitPane.setLeftComponent(netPanel);
		splitPane.setRightComponent(visorPanel);
		
		headersText = new JTextPane();
		headersText.setEditable(false);
		visorPanel.add(new JScrollPane(headersText));
		splitPane.getLeftComponent().setPreferredSize(new Dimension(800, 100));
		
		
		
		


	}

}
