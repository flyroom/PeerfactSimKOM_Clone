/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.util.guirunner.impl;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.guirunner.impl.RunnerController.IRunnerCtrlListener;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class VariableFieldEditor {

	private static Logger log = SimLogger
			.getLogger(VariableFieldEditor.class);

	ConfigFile selectedFile;

	JTable t;

	public VariableFieldEditor(RunnerController ctrl) {
		selectedFile = ctrl.getSelectedFile();
		ctrl.addListener(this.new RunnerCtrlListenerImpl());
		t = new JTable(new TableModelImpl());
	}

	public JComponent getComponent() {
		JScrollPane p = new JScrollPane(t);
		p.setPreferredSize(new Dimension(230, 230));
		return p;
	}

	class RunnerCtrlListenerImpl implements IRunnerCtrlListener {

		@Override
		public void newFileSelected(ConfigFile f) {
			selectedFile = f;
			t.revalidate();
			t.repaint();
		}

	}

	class TableModelImpl extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3813864959892321891L;

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return "Name";
			} else {
				return "Value";
			}
		}

		@Override
		public int getRowCount() {
			if (selectedFile == null) {
				return 0;
			}
			return selectedFile.getVariables().size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Tuple<String, String> tuple = selectedFile.getVariables().get(
					rowIndex);
			if (columnIndex == 0) {
				return tuple.getA();
			}
			return tuple.getB();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex > 0;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Tuple<String, String> tuple = selectedFile.getVariables().get(
					rowIndex);
			log.debug("Changed value of " + tuple.getA() + " from "
					+ tuple.getB() + " to " + aValue);
			tuple.setB((String) aValue);

		}

	}

}
