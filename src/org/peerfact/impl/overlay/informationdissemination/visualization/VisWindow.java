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

package org.peerfact.impl.overlay.informationdissemination.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Operation;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * This class create the view of the visualisization. Additionally it contains a
 * little bit control. (see VariablesTree)
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VisWindow extends JFrame implements OperationAnalyzer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7172148964895162133L;

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(VisWindow.class);

	// TODO: change to private ...
	public int WORLD_DIMENSION_X = 500;

	public int WORLD_DIMENSION_Y = 500;

	public int aoi = 200;

	private WorldPanel worldPanel = null;

	private CheckBoxList checkBoxList = null;

	private JTextPane outputField = null;

	private JScrollPane mapScrollPanel = null;

	private JSplitPane rightSplitPane = null;

	private JScrollPane rightTopScrollPanel = null;

	private JScrollPane rightBottomScrollPanel = null;

	private JSplitPane centerHorizontalSplitPane = null;

	private JSplitPane centerVerticalSplitPane = null;

	private JScrollPane southPanel = null;

	private VariablesPanel variablesPanel;

	private final JCheckBox showVisionRange = new JCheckBox(
			"show vision range radius for selected", true);

	private final JCheckBox showOverlayID = new JCheckBox(
			"show Overlay ID for all Nodes", false);

	private final JCheckBox showSectorLines = new JCheckBox(
			"show sector lines for selected", true);

	private final JCheckBox showEnlargedSectorLines = new JCheckBox(
			"show enlarged sector lines for selected", false);

	private final JCheckBox showStoredPositions = new JCheckBox(
			"show stored positions of known nodes for selected", true);

	private final JCheckBox showOnlySelected = new JCheckBox(
			"show only selected", false);

	private final JCheckBox showSensorNodes = new JCheckBox(
			"show sensor nodes for selected", true);

	private final JCheckBox showNearNodes = new JCheckBox(
			"show near nodes for selected", true);

	private void initWindow() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setSize(800, 800);
		this.setTitle("pSense Visualization");

		this.variablesPanel = new VariablesPanel(this);

		// getter generates the Components!
		getCenterVerticalSplitPane().setTopComponent(
				getCenterHorizontalSplitPane());
		getCenterVerticalSplitPane().setBottomComponent(getSouthPanel());
		getCenterHorizontalSplitPane().setLeftComponent(getMapScrollPanel());
		getCenterHorizontalSplitPane().setRightComponent(getRightSplitPane());
		getMapScrollPanel().setViewportView(getWorldPanel());

		getRightTopScrollPanel().setViewportView(getCheckBoxList());
		getRightBottomScrollPanel().setViewportView(
				variablesPanel.getVariablesTree());

		getRightSplitPane().setTopComponent(getRightTopScrollPanel());
		getRightSplitPane().setBottomComponent(getRightBottomScrollPanel());
		getSouthPanel().setViewportView(getOutputField());

		this.getContentPane().add(BorderLayout.CENTER,
				getCenterVerticalSplitPane());
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				repaint();
			}
		});

		this.setVisible(true);
	}

	public WorldPanel getWorldPanel() {
		if (worldPanel == null) {
			worldPanel = new WorldPanel(this);
		}
		return worldPanel;
	}

	public JTextPane getOutputField() {
		if (outputField == null) {
			outputField = new JTextPane();
			outputField.getAutoscrolls();
			// outputField.setEditable(false);
		}
		return outputField;
	}

	public CheckBoxList getCheckBoxList() {
		if (checkBoxList == null) {
			checkBoxList = new CheckBoxList();
			checkBoxList.setModel(createCheckBoxListData());
			checkBoxList.setAutoscrolls(true);

			checkBoxList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					repaint();
				}
			});
		}
		return checkBoxList;
	}

	public JScrollPane getMapScrollPanel() {
		if (mapScrollPanel == null) {
			mapScrollPanel = new JScrollPane(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			mapScrollPanel.getVerticalScrollBar().setUnitIncrement(50);
			mapScrollPanel.getHorizontalScrollBar().setUnitIncrement(50);
		}
		return mapScrollPanel;
	}

	public JSplitPane getRightSplitPane() {
		if (rightSplitPane == null) {
			rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			rightSplitPane.setDividerLocation(180);
		}
		return rightSplitPane;
	}

	public JScrollPane getSouthPanel() {
		if (southPanel == null) {
			southPanel = new JScrollPane(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			southPanel.setPreferredSize(new Dimension(0, 200));
			southPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder("Output"),
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					southPanel.getBorder()));

		}
		return southPanel;
	}

	public JSplitPane getCenterHorizontalSplitPane() {
		if (centerHorizontalSplitPane == null) {
			centerHorizontalSplitPane = new JSplitPane(
					JSplitPane.HORIZONTAL_SPLIT);
			centerHorizontalSplitPane.setResizeWeight(1);
			centerHorizontalSplitPane.setDividerLocation((int) this.getSize()
					.getWidth() - 250);
		}
		return centerHorizontalSplitPane;
	}

	public JSplitPane getCenterVerticalSplitPane() {
		if (centerVerticalSplitPane == null) {
			centerVerticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			centerVerticalSplitPane.setResizeWeight(1);
			centerVerticalSplitPane.setDividerLocation((int) this.getSize()
					.getHeight() - 200);
		}
		return centerVerticalSplitPane;
	}

	public JScrollPane getRightTopScrollPanel() {
		if (rightTopScrollPanel == null) {
			rightTopScrollPanel = new JScrollPane(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			rightTopScrollPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder("Options"),
							BorderFactory.createEmptyBorder(3, 3, 3, 3)),
					rightTopScrollPanel.getBorder()));
		}
		return rightTopScrollPanel;
	}

	public JScrollPane getRightBottomScrollPanel() {
		if (rightBottomScrollPanel == null) {
			rightBottomScrollPanel = new JScrollPane(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			rightBottomScrollPanel.setBorder(BorderFactory
					.createCompoundBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder("Variables"),
							BorderFactory.createEmptyBorder(3, 3, 3, 3)),
							rightBottomScrollPanel.getBorder()));
		}
		return rightBottomScrollPanel;
	}

	private ListModel createCheckBoxListData() {
		DefaultListModel model = new DefaultListModel();
		model.addElement(getShowVisionRange());
		model.addElement(getShowOverlayID());
		model.addElement(getShowSectorLines());
		model.addElement(getShowStoredPositions());
		model.addElement(getShowNearNodes());
		model.addElement(getShowSensorNodes());
		model.addElement(getShowOnlySelected());
		model.addElement(getShowEnlargedSectorLines());
		return model;
	}

	public JCheckBox getShowVisionRange() {
		return showVisionRange;
	}

	public JCheckBox getShowOverlayID() {
		return showOverlayID;
	}

	public JCheckBox getShowSectorLines() {
		return showSectorLines;
	}

	public JCheckBox getShowEnlargedSectorLines() {
		return showEnlargedSectorLines;
	}

	public JCheckBox getShowStoredPositions() {
		return showStoredPositions;
	}

	public JCheckBox getShowOnlySelected() {
		return showOnlySelected;
	}

	public JCheckBox getShowSensorNodes() {
		return showSensorNodes;
	}

	public JCheckBox getShowNearNodes() {
		return showNearNodes;
	}

	public VariablesPanel getVariablesPanel() {
		return variablesPanel;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		WORLD_DIMENSION_X = worldDimensionX;
	}

	public void setWorldDimensionY(int worldDimensionY) {
		WORLD_DIMENSION_Y = worldDimensionY;
	}

	public void setAOI(int aoi) {
		this.aoi = aoi;
	}

	@Override
	public void start() {
		initWindow();
	}

	@Override
	public void stop(Writer output) {
		// Do nothing --> the window is kept open after the simulation finished
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		this.repaint();
		// TODO: Update Variables in output
	}

	@Override
	public void operationFinished(Operation<?> op) {
		this.repaint();
		// TODO: Update Variables in output
	}

}
