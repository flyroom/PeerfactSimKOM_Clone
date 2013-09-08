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

import java.awt.Color;

/**
 * Manipulation tools for color
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 24.11.2008
 * 
 */
public class ColorToolkit {

	/**
	 * Returns a lighter version of the passed color, for example, can be used
	 * as background.
	 * 
	 * @param color
	 * @return
	 */
	public static Color getLightColorFor(Color color) {
		float weaknessFactor = 0.1f;

		float[] compArray = color.getRGBColorComponents(null);

		// invert Colors
		for (int i = 0; i < 3; i++) {
			compArray[i] = 1 - compArray[i];
		}

		// Makes the color whiter with a factor.
		for (int i = 0; i < 3; i++) {
			compArray[i] = compArray[i] * weaknessFactor;
		}

		// Drag the gray hue of the color.
		float grayPart = Math.min(compArray[0], Math.min(compArray[1],
				compArray[2]));
		for (int i = 0; i < 3; i++) {
			compArray[i] -= grayPart;
		}

		// reinvert the colors
		for (int i = 0; i < 3; i++) {
			compArray[i] = 1 - compArray[i];
		}

		return new Color(compArray[0], compArray[1], compArray[2]);
	}

	public static Color weighColor(Color cl1, Color cl2, double weight) {
		if (weight <= 0) {
			return cl1;
		}
		if (weight >= 1) {
			return cl2;
		}

		double invWeight = 1 - weight;

		Color result = new Color((int) (cl1.getRed() * weight + cl2.getRed()
				* invWeight), (int) (cl1.getGreen() * weight + cl2.getGreen()
				* invWeight), (int) (cl1.getBlue() * weight + cl2.getBlue()
				* invWeight));

		return result;
	}

}
