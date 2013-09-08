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

package org.peerfact.impl.analyzer.visualization2d.api.metrics;

import java.awt.Color;

import javax.swing.ImageIcon;

import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * A metric in this sense is a value expressed in a string variable which can be
 * measured at any time at a node, edge, or the whole graph. It consists of a
 * <b> Name </ b> and an object-dependent <b> value </ b>. For a better
 * understanding of it helps to look closer on some examples of code.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class Metric {

	public Metric() {
		this.loadSettings();
	}

	protected final String base_path = "Metrics/"
			+ this.getClass().getSimpleName();

	protected final String activated_path = base_path + "/Activated";

	protected final String color_path = base_path + "/Color";

	/**
	 * Standard color for a metric.
	 */
	protected Color color = Color.BLACK;

	protected boolean isActivated = false;

	/**
	 * Returns the name of the metric
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Returns the name of the unit, if any. Otherwise an empty string. The
	 * string has a maximum of about 5 characters.
	 * 
	 * @return
	 */
	public abstract String getUnit();

	/**
	 * Indicates whether the metric is numeric, so for example Gnuplot can be
	 * used for. The metric returns in this case with getValue a value that
	 * gnuplot understands, ie a number or similar
	 * 
	 * @return
	 */
	public abstract boolean isNumeric();

	public boolean isActivated() {
		return this.isActivated;
	}

	/**
	 * Enables the metric for display in the graphic. If the metric is
	 * activated, it will be shown in the graphic, if it is not activated, it
	 * will not be displayed.
	 * 
	 * @param true if it should be enabled, false if it is to be disabled
	 */
	public void setActivated(boolean activated) {
		this.isActivated = activated;
	}

	/**
	 * Returns the characteristic color for the representation of the metric.
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the characteristic color of the metric. Is ignored if a value is in
	 * the XML-Config.
	 * 
	 * @param color
	 */
	public void setColor(Color color) {

		int red = Config.getValue(color_path + "/Red", color.getRed());
		int green = Config.getValue(color_path + "/Green", color.getGreen());
		int blue = Config.getValue(color_path + "/Blue", color.getBlue());

		this.color = new Color(red, green, blue);
	}

	public void loadSettings() {
		if (Config.getValue(activated_path, 0) == 0) {
			isActivated = false;
		} else {
			isActivated = true;
		}
	}

	/**
	 * Saves all settings that were made ​​for the metric
	 */
	public void saveSettings() {
		if (isActivated == true) {
			Config.setValue(activated_path, 1);
		} else {
			Config.setValue(activated_path, 0);
		}
	}

	/**
	 * Returns an icon representing. Can be null!
	 * 
	 * @return
	 */
	@SuppressWarnings("static-method")
	public ImageIcon getRepresentingIcon() {
		return null;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
