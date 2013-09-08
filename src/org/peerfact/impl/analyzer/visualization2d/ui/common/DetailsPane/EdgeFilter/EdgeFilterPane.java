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

import javax.swing.JTable;

import org.peerfact.impl.analyzer.visualization2d.ui.common.DetailsPane.ColorTableCellRenderer;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class EdgeFilterPane extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7724781197761597917L;

	public EdgeFilterPane() {
		this.setModel(new EdgeFilterModel());
		this.arrangeColumns();
		this.getColumnModel().getColumn(1).setCellRenderer(
				new ColorTableCellRenderer());

	}

	private void arrangeColumns() {
		this.getColumnModel().getColumn(0).setPreferredWidth(25);
		this.getColumnModel().getColumn(0).setMaxWidth(35);
		this.getColumnModel().getColumn(1).setPreferredWidth(25);
		this.getColumnModel().getColumn(1).setMaxWidth(35);
		this.getColumnModel().getColumn(3).setPreferredWidth(15);
	}

}
