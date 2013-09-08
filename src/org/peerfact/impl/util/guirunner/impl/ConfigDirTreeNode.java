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

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A tree node that represents a directory.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class ConfigDirTreeNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2248644055706219180L;

	public ConfigDirTreeNode(File configDir) {
		super(configDir.getName());
	}

	/**
	 * A tree node that is the root of all nodes
	 * 
	 * @author Leo Nobach
	 * @version 3.0, 25.11.2008
	 * 
	 */
	public static class Root extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7738958362644721084L;

		public Root() {
			super("PeerfactSim.KOM Configurations");
		}
	}

	/**
	 * A tree node that is the root of the last opened files
	 * 
	 * @author Leo Nobach
	 * @version 3.0, 25.11.2008
	 * 
	 */
	public static class LastOpenedRoot extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8933832462501264621L;

		public LastOpenedRoot() {
			super("Recently opened");
		}
	}

	/**
	 * A tree node that is the root of all available files
	 * 
	 * @author Leo Nobach
	 * @version 3.0, 25.11.2008
	 * 
	 */
	public static class AvailableRoot extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3224732124938616138L;

		public AvailableRoot() {
			super("Available configurations");
		}
	}
}
