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

import java.util.Arrays;
import java.util.Vector;

import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class LookAndFeel {

	private static Logger log = SimLogger.getLogger(LookAndFeel.class);

	public static Vector<UIManager.LookAndFeelInfo> getAllLookAndFeels() {
		Vector<UIManager.LookAndFeelInfo> v = new Vector<UIManager.LookAndFeelInfo>();
		v.addAll(Arrays.asList(UIManager.getInstalledLookAndFeels()));
		return v;
	}

	public static void setLookAndFeel() {
		setLookAndFeel(Config.getValue("UI/LookAndFeel", getDefaultLaF()));
	}

	private static String getDefaultLaF() {
		return UIManager.getSystemLookAndFeelClassName();
	}

	public static String getActivatedLookAndFeel() {
		return UIManager.getLookAndFeel().getClass().getName();
	}

	/**
	 * Changes the LookAndFeel according to the settings.
	 * 
	 */
	public static void setLookAndFeel(String laf) {

		try {
			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			log.debug("Could not load Look-and-Feel " + laf + " . "
					+ "Probably it is not installed.");
		}
	}

	/*
	 * private static boolean setWindowsLookAndFeel() { try {
	 * UIManager.setLookAndFeel
	 * ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); return true; }
	 * catch (Exception e) { log.debug("Cannot load Windows LaF, try GTK" );
	 * return false; } }
	 * 
	 * private static boolean setGTKLookAndFeel() { try {
	 * UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	 * return true; } catch (Exception e) {
	 * log.debug("Cannot load GTK LaF, try Default" ); return false; } }
	 */
}
