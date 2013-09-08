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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.simengine.Simulator;
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
 * Accumulates the average amount of bytes sent at the application layer of a
 * Kademlia overlay network. These bytes are classified into bytes sent for
 * maintenance purposes and user-initiated respectively. A measurement is made
 * for each hour of simulated realtime (this analyzer assumes that each
 * measurement is started at the first minute of the hour, and takes an integral
 * number of hours).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class HourlyTrafficAnalyzer implements TransAnalyzer {
	private final static Logger log = SimLogger
			.getLogger(HourlyTrafficAnalyzer.class);

	/** Whether the analysis has begun. */
	private boolean started = false;

	/**
	 * A Map with the hour of measurement as keys, and the total number of bytes
	 * sent as values.
	 */
	private Map<Long, Long> sentUser, sentMaintenance;

	/**
	 * The number of hosts in the overlay network.
	 */
	private double numHosts = 1;

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
	 * Called to start the traffic analysis. Messages that are sent prior to
	 * invoking this method are not taken into account for analysis. Calling
	 * this method starts a <i>fresh</i> measurement. The number of the
	 * participating hosts has to be set before calling this method.
	 */
	@Override
	public final void start() {
		if (!started) {
			started = true;
			sentMaintenance = new LinkedHashMap<Long, Long>();
			sentUser = new LinkedHashMap<Long, Long>();
		}
	}

	/**
	 * A Kademlia overlay message has been sent at a host.
	 * 
	 * @param msg
	 *            the KademliaMsg that has been sent.
	 */
	private void messageSent(final KademliaMsg<?> msg) {
		Map<Long, Long> bytes = null;
		Long currBytes = null;
		final Long currentHour;
		if (!started) {
			return;
		}

		switch (msg.getReason()) {
		case MAINTENANCE:
			bytes = sentMaintenance;
			break;
		case USER_INITIATED:
			bytes = sentUser;
			break;
		}
		if (bytes == null) {
			return;
		}
		currentHour = Simulator.getCurrentTime() / Simulator.HOUR_UNIT;
		currBytes = bytes.get(currentHour);
		if (currBytes == null) {
			currBytes = msg.getSize();
		} else {
			currBytes += msg.getSize();
		}
		bytes.put(currentHour, currBytes);
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
			output.write("\n**************** Application Message Statistics "
					+ "****************\n");
			output.write(" Hour\tTotal Bytes Sent\tBytes Sent Per Host\n");
			output.write("-- user-initiated --\n");
			writeTraffic(output, sentUser);
			output.write("\n-- maintenance --\n");
			writeTraffic(output, sentMaintenance);
			output.write(" (Assumed " + numHosts
					+ " hosts and measurement of entire ");
			output.write("hours starting at minute 0.)\n");
			output.write("************** Application Message Statistics End "
					+ "**************\n");
			output.flush();
		} catch (IOException ex) {
			log.error("Overlay traffic measurement could not be written to "
					+ "output.", ex);
		}
	}

	private void writeTraffic(final Writer output, final Map<Long, Long> bytes)
			throws IOException {
		for (final Map.Entry<Long, Long> entry : bytes.entrySet()) {
			output.write(String.format(" %1$02d \t%2$016d \t%3$09.2f\n", entry
					.getKey(), entry.getValue(), entry.getValue() / numHosts));
		}
	}

	/*
	 * Methods used to "adapt" this class to the TransAnalyzer interface:
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void transMsgReceived(final AbstractTransMessage msg) {
		// ignore
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
