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

package org.peerfact.impl.analyzer.visualization2d.ui.common.DetailsPane;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;


/**
 * Model of ObjectMetricsTable.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class ObjectMetricsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8805264033914855097L;

	Vector<BoundMetric> metrics;

	public ObjectMetricsTableModel(Vector<BoundMetric> metrics) {
		this.metrics = metrics;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return metrics.size();
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "Name";
		} else {
			return "Value";
		}
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (col == 0) {
			return metrics.elementAt(row).getName();
		} else {
			String mValue = metrics.elementAt(row).getValue();
			if (mValue == null) {
				return getNullView();
			}
			return mValue;
		}
	}

	/**
	 * Is returned to the cell to display, if the metric has no value.
	 * 
	 * @return
	 */
	private static Object getNullView() {
		return "<html><b>---null---</b></html>";
	}

	@Override
	public Class<String> getColumnClass(int c) {
		return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		// Nothing to do
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

}
