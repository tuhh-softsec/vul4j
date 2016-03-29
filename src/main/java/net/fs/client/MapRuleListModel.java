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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


public class MapRuleListModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 2267856423317178816L;

	private List<MapRule> mapRuleList;
	
	String titles[] ;

	Class<?> types[] = new Class[] {String.class,  String.class, String.class,String.class,  String.class, String.class};
	
	MapRuleListModel(){
		mapRuleList=new ArrayList<MapRule> ();
		titles = new String[] {""};
	}
	
	public void setMapRuleList(List<MapRule> list){
		mapRuleList.clear();
		if(list!=null){
			mapRuleList.addAll(list);
		}
		fireTableDataChanged();
	}
	
	public int getMapRuleIndex(String name){
		int index=-1;
		int i=0;
		for(MapRule r:mapRuleList){
			if(name.equals(r.getName())){
				index=i;
				break;
			}
			i++;
		}
		return index;
	}
	
	List<MapRule> getMapRuleList(){
		return mapRuleList;
	}
	
	public MapRule getMapRuleAt(int row){
		if(row>-1&row<mapRuleList.size()){
			return mapRuleList.get(row);
		}else{
			return null;
		}
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		MapRule node=mapRuleList.get(rowIndex);
		return node;
	}
	
	public void setValueAt(Object value, int row, int col) {
	      fireTableCellUpdated(row, col);
	 }

	public int getRowCount() {
		return mapRuleList.size();
	}

	public int getColumnCount() {
		return titles.length;
	}

	public String getColumnName(int c) {
		return titles[c];
	}

	public Class<?> getColumnClass(int c) {
		return types[c];
	}


	public boolean isCellEditable(int row, int col) {
		boolean b=false;
		if(col==0){
			b=true;
		}
		return false;
	}
}
