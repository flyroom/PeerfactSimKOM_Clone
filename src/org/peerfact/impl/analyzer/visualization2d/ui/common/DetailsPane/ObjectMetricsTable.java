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

import javax.swing.JTable;

import org.peerfact.impl.analyzer.visualization2d.api.metrics.BoundMetric;
import org.peerfact.impl.analyzer.visualization2d.model.ModelRefreshListener;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;


/**
 * SWING-table, that displays all the given metrics with their values​​.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ObjectMetricsTable extends JTable implements ModelRefreshListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1071754568206817344L;

	public ObjectMetricsTable() {
		VisDataModel.addRefreshListener(this);
	}

	private void arrangeColumns() {
		this.getColumnModel().getColumn(1).setPreferredWidth(10);
	}

	/**
	 * Passes the metric table a new set of bonded metrics, which are then
	 * displayed.
	 * 
	 * @param boundMetrics
	 */
	public void setMetrics(Vector<BoundMetric> boundMetrics) {
		this.setModel(new ObjectMetricsTableModel(boundMetrics));
		this.arrangeColumns();
	}

	@Override
	public void modelNeedsRefresh(VisDataModel model) {
		this.repaint();
	}

	@Override
	public void newModelLoaded(VisDataModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationFinished(VisDataModel model) {
		// TODO Auto-generated method stub

	}
}
