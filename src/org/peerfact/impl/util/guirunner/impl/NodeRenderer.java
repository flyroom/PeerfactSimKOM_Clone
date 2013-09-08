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

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peerfact.Constants;


/**
 * Decides how to render a particular node.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 25.11.2008
 * 
 */
public class NodeRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6968205085443692522L;

	static final ImageIcon DEFAULT_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/guiRunner/ScenarioConfigBlue.png");

	static final ImageIcon LAST_OPENED_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/guiRunner/ScenarioConfigRed.png");

	static final ImageIcon LAST_OPENED_DIR_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/guiRunner/recentlyOpened.png");

	static final ImageIcon AVAILABLE_DIR_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/guiRunner/availableConfigs.png");

	static final ImageIcon ROOT_ICON = new ImageIcon(
			Constants.ICONS_DIR + "/guiRunner/RootNode.png");

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean focus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, focus);

		if (value instanceof ConfigTreeNode.LastOpened) {
			setIcon(LAST_OPENED_ICON);
		} else if (value instanceof ConfigDirTreeNode.LastOpenedRoot) {
			setIcon(LAST_OPENED_DIR_ICON);
		} else if (value instanceof ConfigDirTreeNode.AvailableRoot) {
			setIcon(AVAILABLE_DIR_ICON);
		} else if (value instanceof ConfigDirTreeNode.Root) {
			setIcon(ROOT_ICON);
		} else if (value instanceof ConfigDirTreeNode) {
			// Standard-Knoten-Icon
		} else {
			setIcon(DEFAULT_ICON);
		}

		return this;
	}

}
