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

package org.peerfact.impl.analyzer.csvevaluation.metrics;

import org.peerfact.api.common.Message;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.LiveMonitoring;
import org.peerfact.impl.util.LiveMonitoring.ProgressValue;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MessagesCounter implements Metric {

	public MessagesCounter() {
		LiveMonitoring.addProgressValue(this.new MessagesValue());
	}

	int messages = 0;

	long measurementStart = -1;

	double lastMeasurementResult = 0;

	@Override
	public String getMeasurementFor(long time) {
		long currentTime = Simulator.getCurrentTime();
		if (measurementStart == -1) {
			measurementStart = currentTime;
			return "NaN";
		}

		double result = (messages / (double) (currentTime - measurementStart) * Simulator.SECOND_UNIT);
		measurementStart = currentTime;
		lastMeasurementResult = result;
		messages = 0;

		return NumberFormatToolkit.floorToDecimalsString(result, 1);

	}

	public void messageSent(Message msg) {
		messages++;
	}

	@Override
	public String getName() {
		return "Messages/sec";
	}

	/**
	 * A field in the progress window displaying the result of this operation
	 * 
	 * @author
	 * 
	 */
	public class MessagesValue implements ProgressValue {

		@Override
		public String getName() {
			return "Messages per second";
		}

		@Override
		public String getValue() {
			return NumberFormatToolkit.floorToDecimalsString(
					lastMeasurementResult, 2);
		}

	}

}
