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

package org.peerfact.impl.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Global unit for managing runtime information of the simulator that may be
 * displayed to the user.
 * 
 * Manages a list of ProgressValue objects. These objects are a basic name/value
 * scheme: They all have an arbitrary, human-readable name and a value that may
 * (and should) change during runtime.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LiveMonitoring {

	static List<ProgressValue> progValues = new LinkedList<ProgressValue>();

	/**
	 * Returns the list of ProgressValues that are currently registered to the
	 * LiveMonitoring unit.
	 * 
	 * @return
	 */
	public static List<ProgressValue> getProgressValues() {
		return progValues;
	}

	/**
	 * Adds a ProgressValue to the LiveMonitoring unit.
	 * 
	 * @param val
	 */
	public static void addProgressValue(ProgressValue val) {
		progValues.add(val);
	}

	/**
	 * Value that can be monitored at runtime. Simple name/value data structure.
	 * May be inserted in the LiveMonitoring unit that allows global dispatching
	 * of this value (e.g. the GUIRunner or other UIs) BEFORE simulation start
	 * (in the configuration period or before).
	 * 
	 */
	public interface ProgressValue {

		/**
		 * Returns an arbitrary fancy human-readable name describing this value.
		 * 
		 * @return
		 */
		public String getName();

		/**
		 * Returns the value how it shall be displayed. Mind that this method
		 * may be called from another thread than the simulation thread.
		 * Therefore, you should synchronize it when large data structures are
		 * crawled.
		 * 
		 * @return
		 */
		public String getValue();
	}

	/**
	 * Adds a ProgressValue to the LiveMonitoring unit. Does nothing, if an
	 * object equal to this one is already in the live monitoring field list.
	 * 
	 * @param val
	 */
	public static void addProgressValueIfNotThere(ProgressValue val) {
		if (!progValues.contains(val)) {
			addProgressValue(val);
		}
	}

}
