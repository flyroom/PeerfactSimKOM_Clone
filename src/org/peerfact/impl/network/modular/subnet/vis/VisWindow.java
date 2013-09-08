/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.network.modular.subnet.vis;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Writer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Operation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Simple Subnet-Level Visualization of Devices and Routing-Connections
 * 
 * @author Bjoern Richerzhagen, based on work from Christoph Muenker
 * 
 */
public class VisWindow extends JFrame implements OperationAnalyzer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7496575577710460946L;

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(VisWindow.class);

	public static int WORLD_DIMENSION_X = 500;

	public static int WORLD_DIMENSION_Y = 500;

	private WorldPanel worldPanel = null;

	private JScrollPane mapScrollPanel = null;

	private void initWindow() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setSize(815, 630);
		this.setTitle("Subnet Visualization");

		getMapScrollPanel().setViewportView(getWorldPanel());

		this.getContentPane().add(BorderLayout.CENTER, getMapScrollPanel());
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

	public static void setWorldDimensionX(int worldDimensionX) {
		WORLD_DIMENSION_X = worldDimensionX;
	}

	public static void setWorldDimensionY(int worldDimensionY) {
		WORLD_DIMENSION_Y = worldDimensionY;
	}

	@Override
	public void start() {
		initWindow();
	}

	@Override
	public void stop(Writer output) {
		// Do nothing --> the window is kept open after the simulation finished
	}

	long lastTimePainted = 0;

	@Override
	public void operationInitiated(Operation<?> op) {
		// ntd
	}

	@Override
	public void operationFinished(Operation<?> op) {
		if (Simulator.getCurrentTime() > lastTimePainted + 10
				* Simulator.SECOND_UNIT) {
			lastTimePainted = Simulator.getCurrentTime();
			this.repaint();
		}
	}

}
