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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * A tree view that shows available and recently opened configuration files.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 25.11.2008
 * 
 */
public class DirView extends JTree implements TreeSelectionListener,
MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -504444531274951942L;

	private RunnerController ctrl;

	private ConfigTreeNode selectedConfigNode = null;

	public DirView(RunnerController ctrl, LastOpened lastOpened) {
		super();

		this.ctrl = ctrl;

		NodeModel mdl = new NodeModel(lastOpened);

		this.setModel(new DefaultTreeModel(mdl.getRoot()));
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addTreeSelectionListener(this);

		this.expandRow(2);
		this.expandRow(1);

		this.addMouseListener(this);

		this.setCellRenderer(new NodeRenderer());

		initialSelect();

	}

	/**
	 * Selects the row that should be selected on startup.
	 */
	private void initialSelect() {
		this.setSelectionRow(2);
	}

	/**
	 * Expands all entries in the tree view.
	 * 
	 * @param tree
	 */
	public static void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object selectedNode = this.getLastSelectedPathComponent();

		if (selectedNode instanceof ConfigTreeNode) {
			selectedConfigNode = (ConfigTreeNode) selectedNode;
		} else {
			selectedConfigNode = null;
		}

		ctrl.selectFile(selectedConfigNode == null ? null : selectedConfigNode
				.getConfigFile());

		// log.debug("Ausgewählte ConfigNode: " + selectedConfigNode);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() == 2 && selectedConfigNode != null) {
			ctrl.invokeRunSimulator();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
