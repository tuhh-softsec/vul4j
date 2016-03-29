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

import net.miginfocom.swing.MigLayout;

import sun.swing.DefaultLookup;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class MapRuleRender extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -3260748459008436510L;

	JPanel pleft,pright,p1;

	JLabel label_wan_address;
	JLabel label2;

	MapRule rule;

	{
		setOpaque(true);
		setLayout(new MigLayout("insets 8 10 0 0"));
		label_wan_address=new JLabel();
		add(label_wan_address,"width :500:,wrap");
		label_wan_address.setBackground(new Color(0f,0f,0f,0f));
		label_wan_address.setOpaque(true);
		label2=new JLabel();
		add(label2,"width :500:,wrap");
		label2.setBackground(new Color(0f,0f,0f,0f));
		label2.setOpaque(true);
	}


	void update(MapRule rule,JTable table,int row){
		this.rule=rule;
		int rowHeight=50;
		int h=table.getRowHeight(row);
		if(h!=rowHeight){
			table.setRowHeight(row, rowHeight);
		}
		String name=rule.getName();
		if(name==null){
			name="无";
		}else if(name.trim().equals("")){
			name="无";
		}
		label_wan_address.setText("名称: "+rule.name+"  加速端口: "+rule.dst_port);
		label2.setText("本地端口: "+rule.getListen_port());

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Color fg = null;
		Color bg = null;
		JTable.DropLocation dropLocation = table.getDropLocation();
		if (dropLocation != null
				&& !dropLocation.isInsertRow()
				&& !dropLocation.isInsertColumn()
				&& dropLocation.getRow() == row
				&& dropLocation.getColumn() == column) {

			fg = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
			bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");
			isSelected = true;
		}
		if (isSelected) {
			setBackground(DefaultLookup.getColor(this, ui, "Table.dropCellBackground"));
		} else {
			setBackground( DefaultLookup.getColor(this, ui, "Table.alternateRowColor"));
		}
		MapRule rule=(MapRule)value;
		update(rule,table,row);
		return this;
	}

}
