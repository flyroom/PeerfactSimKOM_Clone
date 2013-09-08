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

package org.peerfact.impl.analyzer.visualization2d.visualization2d;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Treated mouse events and performs commands in Simple2DVisualization if
 * necessary.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 13.11.2008
 * 
 */

public class DefaultMouseHandler implements MouseListener, MouseMotionListener,
		MouseWheelListener {

	Simple2DVisualization vis;

	/**
	 * Position of last click. Needed for dragging of the scenario.
	 */
	private Point lastClickPosition = new Point(0, 0);

	/**
	 * Default constructor
	 * 
	 * @param vis
	 */
	public DefaultMouseHandler(Simple2DVisualization vis) {
		super();
		this.vis = vis;
	}

	/**
	 * Called when the mouse was clicked in the visualization window.
	 */
	@Override
	public void mouseClicked(MouseEvent event) {
		// Nothing to do
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Nothing to do
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Nothing to do
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!isActive()) {
			return;
		}
		lastClickPosition = event.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (!isActive()) {
			return;
		}
		if (event.getButton() == MouseEvent.BUTTON1) {
			vis.handleClick(event.getPoint());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!isActive()) {
			return;
		}
		if (vis.clickHndlr.clickedOnABox(e.getPoint())) {
			vis.setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			vis.setCursor(Cursor.getDefaultCursor());
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!isActive()) {
			return;
		}
		vis.zoom(e.getWheelRotation() < 0, e.getPoint());

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!isActive()) {
			return;
		}
		Point pNew = e.getPoint();
		Point pDiff = new Point(pNew.x - lastClickPosition.x, pNew.y
				- lastClickPosition.y);

		vis.shiftView(pDiff);

		this.lastClickPosition = pNew;
	}

	public boolean isActive() {
		return vis.clickHndlr != null;
	}

}
