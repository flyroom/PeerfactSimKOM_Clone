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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * A tree node that represents a configuration file
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 25.11.2008
 * 
 */
public class ConfigTreeNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6455121549220135193L;

	private ConfigFile configFile;

	public ConfigTreeNode(ConfigFile configFile) {
		super(configFile.getFile().getName());

		this.configFile = configFile;

	}

	/**
	 * Returns the config file this node is associated to.
	 * 
	 * @return
	 */
	public ConfigFile getConfigFile() {
		return configFile;
	}

	/**
	 * A node to represent a last-opened entry
	 * 
	 * @author Leo Nobach
	 * @version 3.0, 25.11.2008
	 * 
	 */
	public static class LastOpened extends ConfigTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3774709165293556027L;

		public LastOpened(ConfigFile configFile) {
			super(configFile);
		}
	}
}
