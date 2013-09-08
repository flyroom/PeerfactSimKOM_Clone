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

package org.peerfact.api.analyzer;

/**
 * ChurnAnalyzers receive notifications about the session/inter-session
 * times of the hosts. Thus, it is possible to estimate the mean/median
 * session/inter-session lengths of the applied churn model.
 * 
 */
public interface ChurnAnalyzer extends Analyzer {

	/**
	 * Informs the correspondent churn analyzer about the next session time
	 * calculated by the applied churn model
	 * 
	 * @param time
	 *            the next session time in minutes (time = calculatedTime *
	 *            Simulator.MINUTE_UNIT);
	 */
	public void nextSessionTime(long time);

	/**
	 * Informs the correspondent churn analyzer about the next inter-session
	 * time calculated by the applied churn model
	 * 
	 * @param time
	 *            the next inter-session time in minutes (time =
	 *            calculatedTime * Simulator.MINUTE_UNIT);
	 */
	public void nextInterSessionTime(long time);
}