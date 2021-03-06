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

package org.peerfact.impl.analyzer.visualization2d.util.gui;

import javax.swing.JCheckBox;

import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * How JCheckBox, except that the value of this CheckBox are saved in the XML
 * config beneath configPath
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class JConfigCheckBox extends JCheckBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4395710891110224842L;

	public String configPath;

	public JConfigCheckBox(String caption, String configPath) {
		super(caption);
		create(configPath);
	}

	public JConfigCheckBox(String configPath) {
		create(configPath);
	}

	/**
	 * pseudo constructor
	 * 
	 * @param path
	 *            to the config
	 */
	public void create(String passedConfigPath) {
		this.configPath = passedConfigPath;
		if (Config.getValue(passedConfigPath, 0) == 1) {
			this.setSelected(true);
		}
	}

	public void saveSettings() {
		int value = 0;
		if (this.isSelected()) {
			value = 1;
		}

		Config.setValue(configPath, value);
	}
}
