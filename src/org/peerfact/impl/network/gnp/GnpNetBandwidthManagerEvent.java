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

package org.peerfact.impl.network.gnp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetLayer;


/**
 * 
 * @author Gerald Klunker <peerfact@kom.tu-darmstadt.de>
 * @version 0.01, 07/12/12
 */
public class GnpNetBandwidthManagerEvent extends AbstractGnpNetBandwidthManager {

	private Set<GnpNetBandwidthAllocation> tempConnections;

	private Map<NetLayer, Set<GnpNetBandwidthAllocation>> tempSenderConnections;

	private Map<NetLayer, Set<GnpNetBandwidthAllocation>> tempReceiverConnections;

	private Set<GnpNetBandwidthAllocation> changedAllocations;

	private long step = 0;

	public GnpNetBandwidthManagerEvent() {
		tempSenderConnections = new LinkedHashMap<NetLayer, Set<GnpNetBandwidthAllocation>>(
				connectionsSenderToReceiver.size());
		tempReceiverConnections = new LinkedHashMap<NetLayer, Set<GnpNetBandwidthAllocation>>(
				connectionsReceiverToSender.size());
		tempConnections = new LinkedHashSet<GnpNetBandwidthAllocation>();
		changedAllocations = new LinkedHashSet<GnpNetBandwidthAllocation>();
	}

	@Override
	public void allocateBandwidth() {

		changedAllocations.clear();

		// Initialize Bids and Allocations

		tempSenderConnections.clear();
		tempReceiverConnections.clear();

		for (NetLayer nl : connectionsSenderToReceiver.keySet()) {
			tempSenderConnections.put(nl,
					new LinkedHashSet<GnpNetBandwidthAllocation>(
							connectionsSenderToReceiver.get(nl).size()));
			tempSenderConnections
					.get(nl)
					.addAll(
							connectionsSenderToReceiver
									.get(nl).values());
			tempConnections
					.addAll(connectionsSenderToReceiver.get(nl).values());
		}
		for (NetLayer nl : connectionsReceiverToSender.keySet()) {
			tempReceiverConnections.put(nl,
					new LinkedHashSet<GnpNetBandwidthAllocation>(
							connectionsReceiverToSender.get(nl).size()));
			tempReceiverConnections.get(nl).addAll(
					connectionsReceiverToSender.get(nl).values());
		}
		for (GnpNetBandwidthAllocation c : tempConnections) {
			c.initConnection();
		}

		Set<NetLayer> senders = tempSenderConnections.keySet();
		Set<NetLayer> receivers = tempReceiverConnections.keySet();

		LinkedHashSet<NetLayer> deleteSenders = new LinkedHashSet<NetLayer>();
		LinkedHashSet<NetLayer> deleteReceivers = new LinkedHashSet<NetLayer>();
		LinkedHashSet<GnpNetBandwidthAllocation> deleteConnections = new LinkedHashSet<GnpNetBandwidthAllocation>();

		while (!tempConnections.isEmpty()) {

			for (NetLayer s : senders) {
				Set<GnpNetBandwidthAllocation> temp = new LinkedHashSet<GnpNetBandwidthAllocation>();
				temp.addAll(tempSenderConnections.get(s));
				generateMeanBids(s.getCurrentBandwidth().getUpBW(), temp, true);
			}
			for (NetLayer r : receivers) {
				Set<GnpNetBandwidthAllocation> temp = new LinkedHashSet<GnpNetBandwidthAllocation>();
				temp.addAll(tempReceiverConnections.get(r));
				generateMeanBids(r.getCurrentBandwidth().getDownBW(), temp,
						false);
			}

			step++;

			/*
			 * log.debug("Loop " + step +
			 * " - Remaining Connections / Senders / Receivers: " +
			 * tempConnections.size() + " / " + senders.size() + " / " +
			 * receivers.size());
			 */

			for (NetLayer p : senders) {
				if (generateMinimumBids(p, true)) {
					deleteSenders.add(p);
				}
			}
			for (NetLayer p : receivers) {
				if (generateMinimumBids(p, false)) {
					deleteReceivers.add(p);
				}
			}

			for (GnpNetBandwidthAllocation c : tempConnections) {
				if (assignAllocation(c)) {
					deleteConnections.add(c);
				}
			}

			senders.removeAll(deleteSenders);
			receivers.removeAll(deleteReceivers);
			tempConnections.removeAll(deleteConnections);
			for (GnpNetBandwidthAllocation c : deleteConnections) {
				NetLayer sender = c.getSender();
				NetLayer receiver = c.getReceiver();
				if (tempSenderConnections.containsKey(sender)) {
					tempSenderConnections.get(sender).remove(c);
				}
				if (tempReceiverConnections.containsKey(receiver)) {
					tempReceiverConnections.get(receiver).remove(c);
				}
			}

			deleteSenders.clear();
			deleteReceivers.clear();
			deleteConnections.clear();

		}
		step = 0;
		tempConnections.clear();
		tempSenderConnections.clear();
		tempReceiverConnections.clear();
	}

	/**
	 * Method shares "bandwidth" to all attached "unsigned" allocations.
	 * Normally each allocation gets the same part excluding the allocations
	 * with an upper bound of need bandwidth that is smaller than the mean
	 * fraction (caused by TCP Throughput).
	 * 
	 * Example: sender s has an bandwith of 30bytes/second and 3 receivers
	 * r1-r3. Normally each connection will be assigned with 10 bytes/s
	 * 
	 * If the throughput of connection s -> r1 is limited to 6 bytes/s => s ->
	 * r1 : 6 bytes/s s -> r1 : 12 bytes/s s -> r1 : 12 bytes/s
	 * 
	 * @param bandwidth
	 *            available for bids
	 * @param unassigned
	 *            Allocations will be empty after run
	 * @param isSender
	 *            true if allocations relates to one sender, false if
	 *            allocations relates to one reciever
	 * @return number of smaller opposite bids
	 */
	private int generateMeanBids(double bandwidth,
			Collection<GnpNetBandwidthAllocation> unassigned, boolean isSender) {
		if (unassigned.isEmpty()) {
			return 0;
		}

		// find connections with an upper bound of throughput that is below fair
		// fraction of bandwidth
		double minBW = Double.POSITIVE_INFINITY;
		double share = bandwidth / unassigned.size();
		GnpNetBandwidthAllocation min = null;
		for (GnpNetBandwidthAllocation ba : unassigned) {
			double bw = 0;
			bw = Math.min(share, ba.getBandwidthNeeds());
			if (bw < minBW) {
				minBW = bw;
				min = ba;
			}
		}

		if (minBW < share) {
			min.setBid(minBW, false, isSender, step);
			unassigned.remove(min);
			double newBandwidth = bandwidth - minBW;
			int counter = generateMeanBids(newBandwidth, unassigned, isSender);
			if (minBW > min.getPreviousBid(!isSender, step)) {
				return counter + 1;
			} else {
				return counter;
			}
		} else {
			int counter = 0;
			for (GnpNetBandwidthAllocation ba : unassigned) {
				ba.setBid(share, false, isSender, step);
				if (share > ba.getPreviousBid(!isSender, step)) {
					counter++;
				}
			}
			return counter;
		}
	}

	private boolean generateMinimumBids(NetLayer p, boolean sender) {

		// get unassigned connections related to p
		Set<GnpNetBandwidthAllocation> connections = (sender) ? tempSenderConnections
				.get(p)
				: tempReceiverConnections.get(p);

		// if one connection left repeat bid and set as minimum
		if (connections.size() == 1) {
			GnpNetBandwidthAllocation c = connections.iterator().next();
			c.setBid(c.getPreviousBid(sender, step), true, sender, step);
			return true;
		}

		// Fair Share bandwidth
		double bandwidth = (sender) ? p.getCurrentBandwidth().getUpBW() : p
				.getCurrentBandwidth().getDownBW();
		Set<GnpNetBandwidthAllocation> temp = new LinkedHashSet<GnpNetBandwidthAllocation>();
		temp.addAll(connections);
		int smallerBids = generateMeanBids(bandwidth, temp, sender);

		// no opposite Bid is smaller than own bid
		// mark as minimum Bid
		if (smallerBids == 0) {
			for (GnpNetBandwidthAllocation c : connections) {
				c.setBid(c.getCurrentBid(sender, step), true, sender, step);
			}
			return true;
		}

		// some opposite Bids are smaller than own bid:
		else {

			// get Connections with slowest Bids
			Set<GnpNetBandwidthAllocation> slowestConnections = new LinkedHashSet<GnpNetBandwidthAllocation>();
			double slowestBid = Double.MAX_VALUE;
			for (GnpNetBandwidthAllocation c : connections) {
				double currentBid = c.getPreviousBid(!sender, step);
				if (currentBid < slowestBid) {
					slowestBid = currentBid;
				}
			}
			for (GnpNetBandwidthAllocation c : connections) {
				double currentBid = c.getPreviousBid(!sender, step);
				if (currentBid == slowestBid) {
					slowestConnections.add(c);
				}
			}
			// double bandwidth = (sender) ? p.getCurrentUploadBandwidth() :
			// p.getCurrentDownloadBandwidth();
			double min = slowestConnections.iterator().next().getPreviousBid(
					!sender, step);
			for (GnpNetBandwidthAllocation c : slowestConnections) {
				c.setBid(min, true, sender, step);
				bandwidth -= min;
			}

			Set<GnpNetBandwidthAllocation> fasterConnections = new LinkedHashSet<GnpNetBandwidthAllocation>();
			fasterConnections.addAll(connections);
			fasterConnections.removeAll(slowestConnections);
			generateMeanBids(bandwidth, fasterConnections, sender);

			return false;
		}
	}

	private boolean assignAllocation(GnpNetBandwidthAllocation c) {
		double bidSender = c.getCurrentBid(true, step);
		double bidReceiver = c.getCurrentBid(false, step);

		if (bidSender <= bidReceiver && c.isMinBid(true)
				&& c.isBidRepeated(true)) {
			if (bidSender != c.getAllocatedBandwidth()) {
				changedAllocations.add(c);
			}
			c.setAllocatedBandwidth(bidSender);
			Bandwidth curBWSnd = c.getSender().getCurrentBandwidth();
			curBWSnd.setUpBW(curBWSnd.getUpBW() - bidSender);
			Bandwidth curBWRcv = c.getReceiver().getCurrentBandwidth();
			curBWRcv.setDownBW(curBWRcv.getDownBW() - bidSender);
			return true;
		} else if (bidSender >= bidReceiver && c.isMinBid(false)
				&& c.isBidRepeated(false)) {
			if (bidReceiver != c.getAllocatedBandwidth()) {
				changedAllocations.add(c);
			}
			c.setAllocatedBandwidth(bidReceiver);
			Bandwidth curBWSnd = c.getSender().getCurrentBandwidth();
			curBWSnd.setUpBW(curBWSnd.getUpBW() - bidReceiver);
			Bandwidth curBWRcv = c.getReceiver().getCurrentBandwidth();
			curBWRcv.setDownBW(curBWRcv.getDownBW() - bidReceiver);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public BandwidthAllocation getBandwidthAllocationType() {
		return AbstractGnpNetBandwidthManager.BandwidthAllocation.EVENT;
	}

	@Override
	public Set<GnpNetBandwidthAllocation> getChangedAllocations() {
		return changedAllocations;
	}
}
