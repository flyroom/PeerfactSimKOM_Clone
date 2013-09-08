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

package org.peerfact.impl.analyzer.visualization2d.visualization2d.toolbar;

import javax.swing.JToolBar;

import org.peerfact.impl.analyzer.visualization2d.visualization2d.Simple2DVisualization;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class VisToolBar extends JToolBar {

	public VisToolBar(Simple2DVisualization vis) {
		this.add(new ZoomButton(false, vis));
		this.add(new ZoomButton(true, vis));
		this.add(new FontSizeBox());
		this.add(new StrokeMetricBox(vis));
		this.add(new NodeSizeMetricBox(vis));
		this.add(new SchematicViewButton(vis));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 45375913454649305L;

}
