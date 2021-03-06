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

package org.peerfact.impl.service.aggregation.skyeye.overlay2skynet;

import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.OPAnalyzerEntry;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.util.AbstractMetricsCollectorDelegator;
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
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class KademliaMetricsCollectorDelegator extends
		AbstractMetricsCollectorDelegator {

	private static Logger log = SimLogger
			.getLogger(KademliaMetricsCollectorDelegator.class);

	@Override
	public LinkedHashMap<String, MetricsAggregate> getStatisticsOfMsgs(
			Vector<NetMessage> msgVector, double interval, boolean sent) {
		LinkedHashMap<String, MetricsAggregate> map = new LinkedHashMap<String, MetricsAggregate>();
		String prefix;
		if (sent) {
			prefix = "Sent";
		} else {
			prefix = "Rec";
		}

		// count all instantiated messages in the simulation
		int completeMsgCounter = 0;
		double completeTraffic = 0;

		// count the different messages of the overlay
		int overlayMsgCounter = 0;
		double overlayTraffic = 0;

		if (msgVector != null) {
			completeMsgCounter = msgVector.size();
			Message msg = null;
			// KademliaMsg<?> overlayMsg;
			for (int i = 0; i < msgVector.size(); i++) {
				// determine the statistics for the complete traffic
				msg = msgVector.get(i).getPayload().getPayload();
				completeTraffic = completeTraffic + msgVector.get(i).getSize();
				// check for Overlay-message
				if (msg instanceof KademliaMsg) {
					// determine the statistics for the overlay traffic
					overlayMsgCounter = overlayMsgCounter + 1;
					overlayTraffic = overlayTraffic
							+ msgVector.get(i).getSize();
					// overlayMsg = (KademliaMsg<?>) msg;
					// TODO implement further type-checking for the statistics
					// of Kademlia
				} else {
					log.debug(msg.toString()
							+ "is not a MessageType of the current overlay");
				}
			}// for()

		}

		// amount of all messages
		MetricsAggregate ag = createAggregate(prefix + "CompleteMessages",
				completeMsgCounter, interval);
		map.put(ag.getAggregateName(), ag);

		// size of all messages
		ag = createAggregate(prefix + "SizeCompleteMessages", completeTraffic,
				interval);
		map.put(ag.getAggregateName(), ag);

		// bandwidth-consumption
		double bandwidth;
		double bandwidthConsumption;
		if (sent) {
			bandwidth = skyNetNode.getHost().getNetLayer()
					.getMaxBandwidth().getUpBW();

		} else {
			bandwidth = skyNetNode.getHost().getNetLayer()
					.getMaxBandwidth().getDownBW();
		}
		bandwidthConsumption = completeTraffic / bandwidth;
		ag = createAggregate("Average" + prefix + "BandwidthConsumption",
				bandwidthConsumption, interval);
		map.put(ag.getAggregateName(), ag);

		// amount of overlay-messages
		ag = createAggregate(prefix + "OverlayMessages", overlayMsgCounter,
				interval);
		map.put(ag.getAggregateName(), ag);

		// size of overlay-messages
		ag = createAggregate(prefix + "SizeOverlayMessages", overlayTraffic,
				interval);
		map.put(ag.getAggregateName(), ag);

		return map;
	}

	@Override
	public LinkedHashMap<String, MetricsAggregate> getStatisticsOfOperations(
			Vector<OPAnalyzerEntry> opVector, double interval) {
		return new LinkedHashMap<String, MetricsAggregate>();
	}

}
