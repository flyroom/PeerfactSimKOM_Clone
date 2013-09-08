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

package org.peerfact.impl.analyzer.visualization2d.ui.common.gnuplot;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.api.metrics.Metric;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class MetricTableModel<TMetric extends Metric> extends
		AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7789895021882856840L;

	protected static final ImageIcon metric_icon = new ImageIcon(
			Constants.ICONS_DIR + "/misc/Metric16_16.png");

	List<TMetric> metrics;

	public MetricTableModel(List<TMetric> metrics) {
		this.metrics = metrics;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "";
		} else {
			return "Metric";
		}
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
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return metric_icon;
		} else {
			return metrics.get(row).getName();
		}
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 0) {
			return ImageIcon.class;
		} else {
			return String.class;
		}
	}

}
