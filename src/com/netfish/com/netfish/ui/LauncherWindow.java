package com.netfish.ui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JTextPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.regex.*;

import com.netfish.Netfish;
import com.netfish.core.TCPClient;
import com.netfish.net.NetObject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JTextField;

public class LauncherWindow {

	private JFrame frmFishnet;
	private JTable table;
	private JPanel reqPanel, netPanel;
	private ArrayList<NetObject> nets;
	public static final int TOOLS_PANEL_HEIGHT = 30, 
			TOOLS_PANEL_PREFERRED_WIDTH = 800, 
			REQ_PANEL_HEIGHT = 30, 
			REQ_PANEL_PREFERRED_WIDTH = 800, 
			METHOD_BOX_WIDTH = 100,
			OPTIONS_PANEL_PREFERRED_HEIGHT = 100;
	private JPanel visorPanel;
	private JSplitPane splitPane;
	
	public boolean running;
	public RegViewerWindow regViewer;

	public TCPClient tcpManager;
	private JTextPane headersText;
	private JMenuItem mntmNewMenuItem;
	
	public static JButton stopButton;
	private JPanel loadingPanel;
	private JProgressBar progressBar;
	private JTextField filterBox;
	private int id;
	private String url;
	

	public JFrame getFrame() {
		return this.frmFishnet;
	}
	/**
	 * Create the application.
	 */
	public LauncherWindow(String url, int id, int method, Map<String,String> requestHeaders, Map<String,String> requestBody) {
		this.url = url;
		this.id = id;
		nets = new ArrayList<NetObject>(); //DummyOps.getNetObjects();
		initialize();
		try {
			this.tcpManager = new TCPClient(this, id, url, this.table, requestHeaders, requestBody, true);
			tcpManager.getNetObjects(method);
		} catch (IOException | InterruptedException e1) {
			Netfish.print(e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	
	
	

	/**
	 * Initialize the contents of the frmFishnet.
	 */
	private void initialize() {
		frmFishnet = new JFrame();
		frmFishnet.setTitle("Analizador web de "+Netfish.APP_NAME+" - Petición "+id+" ("+url+")");
		frmFishnet.setBounds(100, 100, 800, 700);
		frmFishnet.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		
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
		
		
		reqPanel = new JPanel();
		reqPanel.setPreferredSize(new Dimension(REQ_PANEL_PREFERRED_WIDTH, REQ_PANEL_HEIGHT));
		reqPanel.setMaximumSize(new Dimension(9999, REQ_PANEL_HEIGHT));
		reqPanel.setMinimumSize(new Dimension(1, REQ_PANEL_HEIGHT));
		reqPanel.setLayout(new BoxLayout(reqPanel, BoxLayout.X_AXIS));
		
		
		frmFishnet.getContentPane().add(reqPanel);
		
		filterBox = new JTextField();
		reqPanel.add(filterBox);
		filterBox.setColumns(10);
		filterBox.addKeyListener((KeyListener) new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		          if (e.getKeyCode()==KeyEvent.VK_ENTER) {
		        	  filterTable();
		          }
		        }
		      });
		
		stopButton = new JButton("■"); // ▶ ■ 
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tcpManager.stopLoadingRegs();
			}
		});
		reqPanel.add(stopButton);
		
		
		
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
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
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
							if (net.getContent() != "") {
								doc.insertString(doc.getLength(), "\n", normal);
								doc.insertString(doc.getLength(), "Respuesta:" + "\n", bold);
								doc.insertString(doc.getLength(), net.getContent() + "\n", normal);
							}
							
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
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
		
		loadingPanel = new JPanel();
		frmFishnet.getContentPane().add(loadingPanel);
		loadingPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		loadingPanel.add(progressBar);
		
	}
	
	public void filterTable() {
		String completeFilter = filterBox.getText();
		String[] filters = completeFilter.split(";");
		TableModel model = table.getModel();
		
		if(completeFilter.length() > 0 && completeFilter.indexOf(":") > -1) 
		{
			ArrayList<RowFilter<TableModel, Object>> fs = new ArrayList<RowFilter<TableModel, Object>>();
			for(String s : filters) {
				String[] f = s.split(":");
				for(int i=0; i<model.getColumnCount(); i++) {
					if(model.getColumnName(i).equalsIgnoreCase(f[0].trim())) {
						try {
							fs.add(RowFilter.regexFilter(f[1].trim(), i));
						    } catch (PatternSyntaxException e) {
						    	e.printStackTrace();
						      return;
						    }
						break;
					}
				}
			}
			
			((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter(RowFilter.andFilter(fs));
		} else ((TableRowSorter<TableModel>)table.getRowSorter()).setRowFilter(null);		
	}
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
	public void setNets(ArrayList<NetObject> nets) {
		this.nets = nets;
	}
	
	public void addNet(NetObject net) {
		this.nets.add(net);
	}

}
