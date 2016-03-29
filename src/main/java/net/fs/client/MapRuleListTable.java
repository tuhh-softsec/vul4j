/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.fs.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.*;

public class MapRuleListTable extends JTable{
	
	private static final long serialVersionUID = -547936371303904463L;
	
	MapRuleListModel model;
	
	MapRuleListTable table;
	
	ClientUI ui;
	
	MapRuleListTable(ClientUI ui,final MapRuleListModel model){
		super();
		this.model=model;
		this.ui=ui;
		table=this;
		setModel(model);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSorter(null);
		
		getColumnModel().getColumn(0).setMinWidth(30);
				
		MapRuleRender rr=new MapRuleRender();
		getColumnModel().getColumn(0).setCellRenderer(rr);
		setRowHeight(50);
		
		new Thread(){
			public void run() {
				while(true){
					try {
						Thread.sleep(1000);
						refresh();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON3&&e.getClickCount()==1){
				int index=rowAtPoint(e.getPoint());
				int modelIndex=convertRowIndexToModel(index);
				getSelectionModel().setSelectionInterval(modelIndex, modelIndex);
			}
				}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==2){
					editRule();
				}
			}
		});
		
	}
	
	void editRule(){
		int index=getSelectedRow();
		int modelIndex=convertRowIndexToModel(index);
		MapRule mapRule=getModel().getMapRuleAt(modelIndex);
		//AddMapFrame sf=new AddMapFrame(ui,ui.mainFrame,mapRule,true);
		//sf.setVisible(true);
	}
	
	void refresh(){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateUI();
			}
		});
	}
	
	public void setMapRuleList(List<MapRule> list){
		model.setMapRuleList(list);
	}

	public MapRuleListModel getModel() {
		return model;
	}

	public void setModel(MapRuleListModel model) {
		super.setModel(model);
		this.model = model;
	}
	
}
