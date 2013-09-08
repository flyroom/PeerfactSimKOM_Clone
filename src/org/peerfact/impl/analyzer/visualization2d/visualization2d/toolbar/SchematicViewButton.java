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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.visualization2d.Simple2DVisualization;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class SchematicViewButton extends JToggleButton implements
		ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5186748047384510370L;

	static final ImageIcon icon = new ImageIcon(
			Constants.ICONS_DIR + "/schematicView.png");

	static final String tooltip = "Scematic View";

	static final String CONF_PATH = "Visualization/SchematicView";

	private Simple2DVisualization vis;

	public SchematicViewButton(Simple2DVisualization vis) {
		super();
		this.setIcon(icon);
		this.setToolTipText(tooltip);
		this.addActionListener(this);
		this.vis = vis;

		this.getModel().setSelected((Config.getValue(CONF_PATH, 0)) != 0);

		vis.setSchematic(this.getModel().isSelected());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		vis.setSchematic(this.getModel().isSelected());

		if (this.getModel().isSelected()) {
			Config.setValue(CONF_PATH, 1);
		} else {
			Config.setValue(CONF_PATH, 0);
		}
	}

}
