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

package org.peerfact.impl.churn.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.apache.log4j.Logger;
import org.peerfact.api.churn.ChurnModel;
import org.peerfact.api.common.Host;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ExponentialChurnModel implements ChurnModel {

	final static Logger log = SimLogger.getLogger(ExponentialChurnModel.class);

	public enum UserType {

		TRANSIENT(MEAN_SESSION_LENGTH), NORMAL(10 * MEAN_SESSION_LENGTH), LONG_LASTING(
				100 * MEAN_SESSION_LENGTH);

		public final ExponentialDistributionImpl expDist;

		private UserType(long length) {
			double meanLength = Double.parseDouble(Long.toString(length));
			expDist = new ExponentialDistributionImpl(meanLength);
			Long.toString(length);
		}

	}

	static long MEAN_SESSION_LENGTH;

	// ----------------------------------
	// Exponential settings for churn with a NON-Chord-Overlay:
	// set churn factor to 0.1
	// ----------------------------------

	// double longLastingFraction = 0.10d;
	//
	// double normalFraction = 0.40d;
	//
	// double transientFraction = 0.50d;
	//
	// double longLastingCf = 0.75d;
	//
	// double normalCf = 0.5d;
	//
	// double transientCf;

	// ----------------------------------
	// Exponential settings for churn with a Chord-Overlay:
	// set churn factor to 0.1
	// ----------------------------------

	double longLastingFraction = 0.10d;

	double normalFraction = 0.40d;

	double transientFraction = 0.50d;

	double longLastingCf = 0.95d;

	double normalCf = 0.85d;

	double transientCf; // 0.85

	Map<Host, ChurnData> hosts;

	HostBuilder hostBuilder;

	double churnFactor;

	public int online;

	public ExponentialChurnModel() {
		this.hosts = new LinkedHashMap<Host, ChurnData>();
	}

	@Override
	public long getNextDowntime(Host host) {
		ChurnData data = this.hosts.get(host);
		try {
			data.setOnlineTime(Math.round(data.type.expDist
					.inverseCumulativeProbability(Simulator.getRandom()
							.nextDouble())));
			// REFREX
			/*
			 * double value = data.type.expDist
			 * .inverseCumulativeProbability(Simulator.getRandom()
			 * .nextDouble()); data.setOnlineTime((long) value);
			 */
		} catch (MathException e) {
			e.printStackTrace();
		}
		return data.onlineTime;
	}

	@Override
	public long getNextUptime(Host host) {
		ChurnData data = this.hosts.get(host);
		return data.offlineTime;
	}

	@Override
	public void prepare(List<Host> churnHosts) {
		this.calculateTransientCf();
		assert (this.longLastingFraction + this.normalFraction
				+ this.transientFraction <= 1d) : "Wrong fraction distribution";
		double random;
		for (Host host : churnHosts) {
			Simulator.getInstance();
			random = Simulator.getRandom().nextDouble();
			if (random < this.longLastingFraction) {
				hosts.put(host, new ChurnData(UserType.LONG_LASTING,
						this.longLastingCf));
			} else if ((random >= this.longLastingFraction)
					&& (random < (this.normalFraction + this.longLastingFraction))) {
				hosts.put(host, new ChurnData(UserType.NORMAL, this.normalCf));
			} else {
				hosts.put(host, new ChurnData(UserType.TRANSIENT,
						this.transientCf));
			}
		}
	}

	private void calculateTransientCf() {
		this.transientCf = (1 - this.churnFactor
				- (this.longLastingCf * this.longLastingFraction) - (this.normalCf * this.normalFraction))
				/ this.transientFraction;
		if (transientCf < 0d || transientCf > 1d) {
			log
					.error("Cannot use desired churn factor ("
							+ churnFactor
							+ "). "
							+ "Transient connection factor would be "
							+ transientCf
							+ " then, which is out of bounds. Modify the fraction instead.");
			throw new AssertionError(
					"Cannot use desired churn factor. "
							+ "Transient connection factor would be "
							+ transientCf
							+ " then, which is out of bounds. Modify the fraction instead.");
		}
	}

	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);
	}

	public static void setMeanSessionLength(long length) {
		MEAN_SESSION_LENGTH = length;
	}

	public void setChurnFactor(double factor) {
		this.churnFactor = factor;
	}

	static class ChurnData {

		UserType type;

		double cf;

		long onlineTime;

		long offlineTime;

		public ChurnData(UserType type, double cf) {
			this.type = type;
			this.cf = cf;
		}

		void setOnlineTime(long time) {
			this.onlineTime = time;
			this.offlineTime = Math.round((time - cf * time) / cf);
		}
	}

	@Override
	public String toString() {
		return "ExponentialChurnGenerator";
	}

}
