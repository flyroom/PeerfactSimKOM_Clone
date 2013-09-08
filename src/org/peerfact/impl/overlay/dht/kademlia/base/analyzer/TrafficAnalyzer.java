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

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.transport.AbstractTransMessage;
import org.peerfact.impl.util.logging.SimLogger;


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
 * Accumulates the average amount of bytes sent and received at the application
 * layer of a Kademlia overlay network per host and hour. These bytes are
 * classified into bytes sent for maintenance purposes and user-initiated
 * respectively.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class TrafficAnalyzer implements TransAnalyzer {
	private final static Logger log = SimLogger
			.getLogger(TrafficAnalyzer.class);

	/** Whether the analysis has begun. */
	private boolean started = false;

	/**
	 * The running average of #bytes sent/received per host and hour with
	 * MAINTENANCE/USER_INITIATED.
	 */
	private double runningAvgRcvdMaintenance = 0,
			runningAvgRcvdUserInitiated = 0, runningAvgSentMaintenance = 0,
			runningAvgSentUserInitiated = 0;

	/**
	 * The duration of the measurement in hours.
	 */
	private double duration = 1;

	/**
	 * The number of hosts in the overlay network.
	 */
	private int numHosts = 1;

	/**
	 * The divisor used in this calculation (saved for performance reasons:
	 * divisor = duration * numHosts).
	 */
	private double divisor;

	/**
	 * Sets the measurement duration in hours. If not set, the default value of
	 * 1 hour is assumed. Has to be set before analysis is started and will be
	 * ignored else.
	 * 
	 * @param measurementHours
	 *            the duration of the measurement in hours.
	 */
	public final void setDuration(final double measurementHours) {
		if (!started) {
			duration = measurementHours;
		}
	}

	/**
	 * Sets the number of hosts that take part in the overlay network. If not
	 * set, the default value of 1 host is assumed. Has to be set before
	 * analysis is started and will be ignored else.
	 * 
	 * @param numberOfHosts
	 *            the number of hosts that take part in the overlay network.
	 */
	public final void setNumberOfHosts(final int numberOfHosts) {
		if (!started) {
			numHosts = numberOfHosts;
		}
	}

	/**
	 * Called to start the traffic analysis. Messages that are sent and received
	 * prior to invoking this method are not taken into account for analysis.
	 * Calling this method starts a <i>fresh</i> measurement. The number of the
	 * participating hosts and the duration of the simulation have to be set
	 * before calling this method.
	 */
	@Override
	public final void start() {
		if (!started) {
			started = true;
			divisor = duration * numHosts;
			runningAvgRcvdMaintenance = 0;
			runningAvgRcvdUserInitiated = 0;
			runningAvgSentMaintenance = 0;
			runningAvgSentUserInitiated = 0;
		}
	}

	/**
	 * A Kademlia overlay message has been received at a host.
	 * 
	 * @param msg
	 *            the KademliaMsg that has been received.
	 */
	public final void messageReceived(final KademliaMsg<?> msg) {
		if (started) {
			switch (msg.getReason()) {
			case MAINTENANCE:
				runningAvgRcvdMaintenance = runningAvgRcvdMaintenance
						+ (msg.getSize() / divisor);
				break;
			case USER_INITIATED:
				runningAvgRcvdUserInitiated = runningAvgRcvdUserInitiated
						+ (msg.getSize() / divisor);
				break;
			}
		}
	}

	/**
	 * A Kademlia overlay message has been sent at a host.
	 * 
	 * @param msg
	 *            the KademliaMsg that has been sent.
	 */
	public final void messageSent(final KademliaMsg<?> msg) {
		if (started) {
			switch (msg.getReason()) {
			case MAINTENANCE:
				runningAvgSentMaintenance = runningAvgSentMaintenance
						+ (msg.getSize() / divisor);
				break;
			case USER_INITIATED:
				runningAvgSentUserInitiated = runningAvgSentUserInitiated
						+ (msg.getSize() / divisor);
				break;
			}
		}
	}

	/**
	 * Called to stop the traffic analysis.
	 * 
	 * @param output
	 *            a Writer on which a textual representation of the measurements
	 *            will be written.
	 */
	@Override
	public final void stop(final Writer output) {
		if (!started) {
			return;
		}
		started = false;

		try {
			output
					.write("\n**************** Application Message Statistics ****************\n");
			output.write(" Reason:\t\t\tUSER_INITIATED\tMAINTENANCE\n");
			output.write(" Average #bytes/hour sent:\t"
					+ String.format("%7.6f", runningAvgSentUserInitiated)
					+ "\t" + String.format("%7.6f", runningAvgSentMaintenance)
					+ "\n");
			output.write(" Average #bytes/hour received:\t"
					+ String.format("%7.6f", runningAvgRcvdUserInitiated)
					+ "\t" + String.format("%7.6f", runningAvgRcvdMaintenance)
					+ "\n");
			output.write(" (assumed " + numHosts + " hosts and measurement of "
					+ duration + " hours)\n");
			output
					.write("************** Application Message Statistics End **************\n");
			output.flush();
		} catch (IOException ex) {
			log
					.error(
							"Overlay traffic measurement could not be written to output.",
							ex);
		}
	}

	/**
	 * @return the average number of bytes sent for maintenance purposes per
	 *         host and hour. (Only bytes sent at the application layer are
	 *         taken into account.)
	 */
	public final double getAvgMaintenanceSent() {
		return runningAvgSentMaintenance;
	}

	/**
	 * @return the average number of bytes received for maintenance purposes per
	 *         host and hour. (Only bytes sent at the application layer are
	 *         taken into account.)
	 */
	public final double getAvgMaintenanceReceived() {
		return runningAvgRcvdMaintenance;
	}

	/**
	 * @return the average number of bytes sent and initiated by the user per
	 *         host and hour. (Only bytes sent at the application layer are
	 *         taken into account.)
	 */
	public final double getAvgUserInitiatedSent() {
		return runningAvgSentUserInitiated;
	}

	/**
	 * @return the average number of bytes received and initiated by the user
	 *         per host and hour. (Only bytes sent at the application layer are
	 *         taken into account.)
	 */
	public final double getAvgUserInitiatedReceived() {
		return runningAvgRcvdUserInitiated;
	}

	/*
	 * Methods used to "adapt" this class to the TransAnalyzer interface:
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void transMsgReceived(final AbstractTransMessage msg) {
		final Message payload = msg.getPayload();
		if (payload != null && payload instanceof KademliaMsg<?>) {
			messageReceived((KademliaMsg<?>) payload);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void transMsgSent(final AbstractTransMessage msg) {
		final Message payload = msg.getPayload();
		if (payload != null && payload instanceof KademliaMsg<?>) {
			messageSent((KademliaMsg<?>) payload);
		}
	}

}
