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

package org.peerfact.impl.analyzer.visualization2d.ui.common.DetailsPane.EdgeFilter;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.ModelFilter;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class EdgeFilterModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7493119663427021910L;

	public static final ModelFilter NULL_FILTER = new ModelFilter();

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return getFilter().getAllTypes().size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return getFilter().getAllTypes().get(row).typeIcon;
		} else if (col == 1) {
			return getFilter().getAllTypes().get(row).typeColor;
		} else if (col == 2) {
			return getFilter().getAllTypes().get(row).typeName;
		} else {
			return getFilter().getAllTypes().get(row).isTypeActivated();
		}
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "";
		} else if (col == 1) {
			return "";
		} else if (col == 2) {
			return "Name";
		} else {
			return "Show";
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 3;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 3) {
			getFilter().getAllTypes().get(row)
			.setTypeActivated((Boolean) value);
			VisDataModel.needsRefresh();
		}

	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 0) {
			return ImageIcon.class;
		} else if (c == 1) {
			return Color.class;
		} else if (c == 2) {
			return String.class;
		} else {
			return Boolean.class;
		}
	}

	public static ModelFilter getFilter() {
		VisDataModel model = Controller.getModel();
		if (model != null) {
			return model.getFilter();
		} else {
			return NULL_FILTER;
		}
	}

}
