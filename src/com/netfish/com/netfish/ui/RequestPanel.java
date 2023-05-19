package com.netfish.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.netfish.Netfish;
import javax.swing.JCheckBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;

public class RequestPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel reqPanel, headersPanel, bodyPanel, optionsPanel;
	private JTabbedPane tabbedPane;
	private JTable headersTable, bodyTable;
	private JComboBox<Object> urlBox, methodSelectorBox;
	private JButton goButton;
	private String[] methods;
	private Map<String,String> requestBody, requestHeaders;
	private final int REQ_PANEL_HEIGHT = 30, 
			REQ_PANEL_PREFERRED_WIDTH = 800, 
			METHOD_BOX_WIDTH = 100;
	private int id;
	private JCheckBox keepHeadersOption;
	private JCheckBox showAllContentOption;
	
	public RequestPanel(int id) {
		this.id = id;
		requestBody = new HashMap<String, String>();
		requestHeaders = new HashMap<String, String>();	
		initialize();
		populateURLBox();
		populateBodyTable();
		populateHeadersTable();
	}
	
	public RequestPanel() {
		this.id = (int) (Math.random()*100);
		requestBody = new HashMap<String, String>();
		requestHeaders = new HashMap<String, String>();	
		initialize();
		populateURLBox();
		populateBodyTable();
		populateHeadersTable();
	}
	
	private void initialize() 
	{
		this.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		reqPanel = new JPanel();
		reqPanel.setPreferredSize(new Dimension(REQ_PANEL_PREFERRED_WIDTH, REQ_PANEL_HEIGHT));
		reqPanel.setMaximumSize(new Dimension(9999, REQ_PANEL_HEIGHT));
		reqPanel.setMinimumSize(new Dimension(1, REQ_PANEL_HEIGHT));
		
		this.add(reqPanel);

		
		
		

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				
		headersPanel = new JPanel();
		tabbedPane.addTab("Cabeceras de la petición", null, headersPanel, null);
		headersPanel.setLayout(new BoxLayout(headersPanel, BoxLayout.X_AXIS));
				
		headersTable = new JTable();
		headersPanel.add(new JScrollPane(headersTable));
				
				
		bodyPanel = new JPanel();
		tabbedPane.addTab("Cuerpo de la petición", null, bodyPanel, null);
				
				
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
				
		bodyTable = new JTable();
		bodyPanel.add(new JScrollPane(bodyTable));
		
		optionsPanel = new JPanel();	
		GridBagLayout gbl_optionsPanel = new GridBagLayout();
		gbl_optionsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_optionsPanel.rowHeights = new int[]{0, 0, 0};
		gbl_optionsPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_optionsPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};

		optionsPanel.setLayout(gbl_optionsPanel);
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsPanel, tabbedPane);
		this.add(splitPane);
		
		keepHeadersOption = new JCheckBox("Mantener las cabeceras en todas las subconsultas");
		GridBagConstraints gbc_keepHeadersOption = new GridBagConstraints();
		gbc_keepHeadersOption.anchor = GridBagConstraints.WEST;
		gbc_keepHeadersOption.insets = new Insets(0, 0, 5, 5);
		gbc_keepHeadersOption.gridx = 0;
		gbc_keepHeadersOption.gridy = 0;
		optionsPanel.add(keepHeadersOption, gbc_keepHeadersOption);
		
		showAllContentOption = new JCheckBox("Mostrar contenido entero de las respuestas");
		showAllContentOption.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_showAllContentOption = new GridBagConstraints();
		gbc_showAllContentOption.anchor = GridBagConstraints.WEST;
		gbc_showAllContentOption.insets = new Insets(0, 0, 0, 5);
		gbc_showAllContentOption.gridx = 0;
		gbc_showAllContentOption.gridy = 1;
		optionsPanel.add(showAllContentOption, gbc_showAllContentOption);
		
		reqPanel.setLayout(new BoxLayout(reqPanel, BoxLayout.X_AXIS));
		urlBox = new JComboBox<Object>(Netfish.usedHosts.toArray());
		urlBox.setEditable(true);
		urlBox.setMaximumSize(new Dimension(9999, REQ_PANEL_HEIGHT));
		reqPanel.add(urlBox);
		
		goButton = new JButton("▶");
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String url = urlBox.getSelectedItem().toString();
				LauncherWindow launcher = new LauncherWindow(url, id, methodSelectorBox.getSelectedIndex(), requestHeaders, requestBody);
				for(Entry<String, String> entry : requestHeaders.entrySet())
					System.out.println(entry.getKey()+": "+entry.getValue());
				launcher.getFrame().setVisible(true);
				
			}
		});
		reqPanel.add(goButton);
		
		methods = new String[] {"GET","POST","PUT","DELETE", "PATCH", "HEAD", "OPTIONS"};
		methodSelectorBox = new JComboBox<Object>(methods);
		
		methodSelectorBox.setPreferredSize(new Dimension(METHOD_BOX_WIDTH, REQ_PANEL_HEIGHT));
		methodSelectorBox.setMaximumSize(new Dimension(METHOD_BOX_WIDTH, REQ_PANEL_HEIGHT));
		reqPanel.add(methodSelectorBox);
	}
			
	private void populateURLBox() {
		DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(Netfish.usedHosts.toArray());
		urlBox.setModel(model);
	}
	
	private void populateBodyTable() {
		int length = this.requestBody.size();
		Object[][] objects = new Object[++length][2];
		int index = 0;
		if(length>0)
		for(Entry<String, String> ent : this.requestBody.entrySet())
		{
			objects[index][0] = ent.getKey();
			objects[index++][1] = ent.getValue();
		}
		objects[index] = new String[] {"", ""};
		
		
		
		DefaultTableModel model = new DefaultTableModel(
				objects,
				new String[] {
					"Nombre", "Valor"
				}
			);
		
		model.addTableModelListener((TableModelListener) new TableModelListener() 
		{
		    public void tableChanged(TableModelEvent evt) 
		    {
		    	String key = (String) model.getValueAt(evt.getLastRow(), 0);
		    	String value = (String) model.getValueAt(evt.getLastRow(), 1);
		    	String k, v;
		    	if(
		    			(key.trim().length()>0 && value.trim().length()>0) 
		    			|| (key.trim().length()==0 && value.trim().length()==0)
		    		) {
		    		requestBody.clear();
		    		for(int i = 0; i < model.getRowCount(); i++)
		    			if(
		    					(k = (String) model.getValueAt(i, 0)).trim().length()>0 
		    					&& (v = (String) model.getValueAt(i, 1)).trim().length()>0 
		    			)
		    			requestBody.put(k,v);
		    		populateBodyTable();
		    	} 
		    	if(evt.getColumn() == 0) {
		    		bodyTable.changeSelection(evt.getLastRow(), 1, false, false);
		    		bodyTable.requestFocus();
		    	}
		    	
		    }
		});
		
		this.bodyTable.setModel(model);

	}

	private void populateHeadersTable() {
		int length = this.requestHeaders.size();
		Object[][] objects = new Object[++length][2];
		int index = 0;
		if(length>0)
		for(Entry<String, String> ent : this.requestHeaders.entrySet())
		{
			objects[index][0] = ent.getKey();
			objects[index++][1] = ent.getValue();
		}
		objects[index] = new String[] {"", ""};
		
		
		
		DefaultTableModel model = new DefaultTableModel(
				objects,
				new String[] {
					"Nombre", "Valor"
				}
			);
		
		model.addTableModelListener((TableModelListener) new TableModelListener() 
		{
		    public void tableChanged(TableModelEvent evt) 
		    {
		    	String key = (String) model.getValueAt(evt.getLastRow(), 0);
		    	String value = (String) model.getValueAt(evt.getLastRow(), 1);
		    	String k, v;
		    	if(
		    			(key.trim().length()>0 && value.trim().length()>0) 
		    			|| (key.trim().length()==0 && value.trim().length()==0)
		    		) {
		    		requestHeaders.clear();
		    		for(int i = 0; i < model.getRowCount(); i++)
		    			if(
		    					(k = (String) model.getValueAt(i, 0)).trim().length()>0 
		    					&& (v = (String) model.getValueAt(i, 1)).trim().length()>0 
		    			)
		    			requestHeaders.put(k,v);
		    		populateHeadersTable();
		    	} 
		    	if(evt.getColumn() == 0) {
		    		headersTable.changeSelection(evt.getLastRow(), 1, false, false);
		    		headersTable.requestFocus();
		    	}
		    	
		    }
		});
		
		this.headersTable.setModel(model);

	}
}
