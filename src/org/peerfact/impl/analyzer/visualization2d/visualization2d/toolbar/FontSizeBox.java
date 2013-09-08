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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class FontSizeBox extends JPanel implements ActionListener {

	public static final String CONF_PATH = "Visualization/FontSize";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2981360948327157441L;

	private static final String TOOL_TIP = "Font size";

	protected JComboBox box;

	public FontSizeBox() {
		box = new JComboBox(new Integer[] { 8, 10, 12, 15, 18, 20 });
		box.setEditable(true);
		box.addActionListener(this);
		box.setSelectedItem(Config.getValue(CONF_PATH, "10"));

		this.setLayout(new BorderLayout());

		this.add(box, BorderLayout.CENTER);
		this.add(new JLabel(new ImageIcon(
				Constants.ICONS_DIR + "/misc/font_size16_16.png")),
				BorderLayout.WEST);

		this.setToolTipText(TOOL_TIP);
		box.setToolTipText(TOOL_TIP);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Integer.valueOf(box.getSelectedItem().toString());
			Config.setValue(CONF_PATH, box.getSelectedItem().toString());
			VisDataModel.needsRefresh();
		} catch (NumberFormatException ex) {
			box.setSelectedItem(Config.getValue(CONF_PATH, "10"));
		}
	}

}
