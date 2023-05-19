package com.netfish.ui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import com.netfish.Netfish;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

/**
 * 
 * @author Manel Andreu Pérez
 *
 */
public class AppWindow {

	private JFrame frmFishnet;
	private JPanel toolsPanel;
	private JMenuItem mntmNewMenuItem;
	private JButton addNewRequest;
	private int requestCount;
	public RegViewerWindow regViewer;

	public static final int TOOLS_PANEL_HEIGHT = 30, 
			TOOLS_PANEL_PREFERRED_WIDTH = 800, 
			REQ_PANEL_HEIGHT = 30, 
			REQ_PANEL_PREFERRED_WIDTH = 800, 
			METHOD_BOX_WIDTH = 100,
			OPTIONS_PANEL_PREFERRED_HEIGHT = 100;
	private JTabbedPane requestsPanel;
	/**
	 * Create the application.
	 */
	public AppWindow() {	
		this.requestCount = 1;
		regViewer = new RegViewerWindow();
		initialize();
		addRequest();
	}
	
	public JFrame getFrame() {
		return this.frmFishnet;
	}
			
	private void addRequest() {
		String title = "Petición "+this.requestCount;
		RequestPanel body =  new RequestPanel(this.requestCount++);
		requestsPanel.addTab(title, null, body, null);
		frmFishnet.revalidate();

		int index = requestsPanel.indexOfTab(title);
		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		
		JLabel lblTitle = new JLabel(title);
		lblTitle.setBorder(new EmptyBorder(0,0,0,10));//top,left,bottom,right
		JButton btnClose = new JButton("x");
		btnClose.setMaximumSize(new Dimension(10,10));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;

		pnlTab.add(lblTitle, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		pnlTab.add(btnClose, gbc);

		requestsPanel.setTabComponentAt(index, pnlTab);

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component selected = requestsPanel.getSelectedComponent();
				 if (selected != null) requestsPanel.remove(selected);
			}
		});
				
				
				
				/*new ActionListener() {

		    public void actionPerformed(ActionEvent evt) {

		        Component selected = requestsPanel.getSelectedComponent();
		        if (selected != null) 
		        	requestsPanel.remove(selected);
		    });*/
		}
	
	/**
	 * Initialize the contents of the frame.
	 */	
	private void initialize() {
		frmFishnet = new JFrame();
		frmFishnet.setTitle("Analizador web de "+Netfish.APP_NAME);
		frmFishnet.setBounds(100, 100, 800, 332);
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
		
		addNewRequest = new JButton("Añadir Petición");
		addNewRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRequest();
			}
		});
		toolsPanel.add(addNewRequest);
		
		requestsPanel = new JTabbedPane(JTabbedPane.TOP);
		frmFishnet.getContentPane().add(requestsPanel);
		
		
	}

}
