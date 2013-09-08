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

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.peerfact.impl.analyzer.visualization2d.controller.commands.Forward;
import org.peerfact.impl.analyzer.visualization2d.controller.commands.PlayPause;
import org.peerfact.impl.analyzer.visualization2d.controller.commands.Reverse;


/**
 * Treated keyboard entries on the Vis-panel
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 13.11.2008
 * 
 */
public class DefaultKeyboardHandler implements KeyListener {

	/**
	 * Pixels, the image is shifted by press of the arrow button.
	 */
	public static final int SHIFT_AMOUNT = 10;

	Simple2DVisualization vis;

	/**
	 * Default constructor
	 * 
	 * @param vis
	 */
	public DefaultKeyboardHandler(Simple2DVisualization vis) {
		this.vis = vis;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			vis.zoom(true, null);
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			vis.zoom(false, null);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			vis.shiftView(new Point(0, -SHIFT_AMOUNT));
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			vis.shiftView(new Point(0, SHIFT_AMOUNT));
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			vis.shiftView(new Point(-SHIFT_AMOUNT, 0));
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			vis.shiftView(new Point(SHIFT_AMOUNT, 0));
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			new PlayPause().execute();
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			new Forward().execute();
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			new Reverse().execute();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// Nothing to do
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Nothing to do
	}

	/**
	 * Debug output
	 * 
	 * @param o
	 */
	public void dbg(Object o) {
		// System.err.println(this.getClass().getSimpleName() + ": " +
		// (o==null?"null":o.toString()));
	}

}
