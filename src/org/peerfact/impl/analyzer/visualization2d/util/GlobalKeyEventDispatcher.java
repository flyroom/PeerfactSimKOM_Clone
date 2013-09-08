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

package org.peerfact.impl.analyzer.visualization2d.util;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures all AWT key events and pass it on to its KeyListener.
 * 
 * @author <info@peerfact.org>
 * @version 3.0, 13.11.2008
 * 
 */
public class GlobalKeyEventDispatcher {

	List<KeyListener> listeners = new ArrayList<KeyListener>();

	Window frame2BActive = null;

	/**
	 * Default constructor
	 */
	public GlobalKeyEventDispatcher() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof KeyEvent) {
					dispatch((KeyEvent) event);
				}
			}

		}, AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * GlobalKeyEventDispatcher, the events are only reported to the
	 * EventListener when the window frame2BActive is in the
	 * foreground(isFocused)
	 * 
	 * @param frame2BActive
	 */
	public GlobalKeyEventDispatcher(Window frame2BActive) {
		this();
		this.frame2BActive = frame2BActive;
	}

	/**
	 * Adds a KeyListener
	 * 
	 * @param l
	 */
	public void addKeyListener(KeyListener l) {
		listeners.add(l);
	}

	void dispatch(KeyEvent event) {
		if (shallDispatch()) {
			if (event.getID() == KeyEvent.KEY_PRESSED) {
				for (KeyListener listener : listeners) {
					listener.keyPressed(event);
				}
			} else if (event.getID() == KeyEvent.KEY_RELEASED) {
				for (KeyListener listener : listeners) {
					listener.keyReleased(event);
				}
			} else if (event.getID() == KeyEvent.KEY_TYPED) {
				for (KeyListener listener : listeners) {
					listener.keyTyped(event);
				}
			}
		}
	}

	private boolean shallDispatch() {
		return (frame2BActive == null || frame2BActive.isFocused());
	}

}
