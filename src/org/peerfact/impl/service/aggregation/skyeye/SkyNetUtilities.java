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

package org.peerfact.impl.service.aggregation.skyeye;

import org.peerfact.api.network.NetID;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;

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
 * This class contains the so-called utility-methods, which are needed by every
 * SkyNet-node during a simulation. Therefore, all methods are declared as
 * static.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetUtilities {

	/**
	 * This method determines the type of the provided object and returns the
	 * {@link NetID} of this object in a printable format, if possible.
	 * 
	 * @param instance
	 *            contains the object, from which the {@link NetID} will be
	 *            retrieved
	 * @return the {@link NetID} of the provided object in a printable format
	 */
	public static String getNetID(Object instance) {
		if (instance == null) {
			return "[null]";
		} else if (instance instanceof SkyNetNode) {
			return ((SkyNetNode) instance).getSkyNetNodeInfo().getTransInfo()
					.getNetId().toString()
					+ " ";
		} else if (instance instanceof AbstractAliasInfo) {
			return ((AbstractAliasInfo) instance).getNodeInfo().getTransInfo()
					.getNetId().toString()
					+ " ";
		} else if (instance instanceof SkyNetNodeInfo) {
			return ((SkyNetNodeInfo) instance).getTransInfo().getNetId()
					.toString()
					+ " ";
		} else {
			return " [unknown object] ";
		}
	}

	/**
	 * This method determines the type of the provided object and returns the
	 * {@link NetID} of this object in a printable format, if possible. Besides
	 * the ID, the simulated time is added to the <code>String</code>.
	 * 
	 * @param instance
	 *            contains the object, from which the {@link NetID} will be
	 *            retrieved
	 * @return the ID of the provided object in a printable format
	 */
	public static String getTimeAndNetID(Object instance) {
		if (instance == null) {
			return "[null]";
		} else if (instance instanceof SkyNetNode) {
			return Simulator.getFormattedTime(Simulator.getCurrentTime())
					+ " "
					+ ((SkyNetNode) instance).getSkyNetNodeInfo()
							.getTransInfo().getNetId().toString() + " ";
		} else if (instance instanceof SkyNetNodeInfo) {
			return Simulator.getFormattedTime(Simulator.getCurrentTime())
					+ " "
					+ ((SkyNetNodeInfo) instance).getTransInfo().getNetId()
							.toString() + " ";
		} else if (instance instanceof AbstractAliasInfo) {
			return Simulator.getFormattedTime(Simulator.getCurrentTime())
					+ " "
					+ ((AbstractAliasInfo) instance).getNodeInfo()
							.getTransInfo().getNetId().toString() + " ";
		} else {
			return Simulator.getFormattedTime(Simulator.getCurrentTime())
					+ " unknown object ";
		}
	}

}
