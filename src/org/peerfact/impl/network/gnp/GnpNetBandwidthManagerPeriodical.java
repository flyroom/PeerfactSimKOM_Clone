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
import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.api.network.Bandwidth;
import org.peerfact.api.network.NetLayer;
import org.peerfact.impl.network.AbstractNetLayer;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnpNetBandwidthManagerPeriodical extends
		AbstractGnpNetBandwidthManager {

	private LinkedHashSet<NetLayer> changedSenders;

	private LinkedHashSet<NetLayer> changedReceivers;

	private Set<GnpNetBandwidthAllocation> changedAllocations; // within last

	// realocation

	public GnpNetBandwidthManagerPeriodical() {
		super();
		changedSenders = new LinkedHashSet<NetLayer>();
		changedReceivers = new LinkedHashSet<NetLayer>();
		changedAllocations = new LinkedHashSet<GnpNetBandwidthAllocation>();
	}

	@Override
	public GnpNetBandwidthAllocation addConnection(AbstractNetLayer sender,
			AbstractNetLayer receiver, double bandwidth) {
		changedSenders.add(sender);
		changedReceivers.add(receiver);
		// sender.setCurrentUpBandwidth(sender.getMaxUploadBandwidth());
		// receiver.setCurrentDownBandwidth(receiver.getMaxDownloadBandwidth());
		GnpNetBandwidthAllocation ba = super.addConnection(sender, receiver,
				bandwidth);
		return ba;
	}

	@Override
	public GnpNetBandwidthAllocation removeConnection(AbstractNetLayer sender,
			AbstractNetLayer receiver, double bandwidth) {
		GnpNetBandwidthAllocation ba = super.removeConnection(sender, receiver,
				bandwidth);
		if (connectionsSenderToReceiver.containsKey(sender)) {
			changedSenders.add(sender);
		} else {
			changedSenders.remove(sender);
		}
		if (connectionsReceiverToSender.containsKey(receiver)) {
			changedReceivers.add(receiver);
		} else {
			changedReceivers.remove(receiver);
		}
		return ba;
	}

	@Override
	public void allocateBandwidth() {
		LinkedHashSet<AbstractNetLayer> chSenders = (LinkedHashSet<AbstractNetLayer>) changedSenders
				.clone();
		LinkedHashSet<AbstractNetLayer> chReceivers = (LinkedHashSet<AbstractNetLayer>) changedReceivers
				.clone();
		changedSenders.clear();
		changedReceivers.clear();
		changedAllocations.clear();
		for (AbstractNetLayer host : chSenders) {
			host.getCurrentBandwidth()
					.setUpBW(host.getMaxBandwidth().getUpBW());
			Set<GnpNetBandwidthAllocation> temp = new LinkedHashSet<GnpNetBandwidthAllocation>();
			temp.addAll(connectionsSenderToReceiver.get(host).values());
			fairShare(temp, true);
		}
		for (AbstractNetLayer host : chReceivers) {
			host.getCurrentBandwidth().setDownBW(
					host.getMaxBandwidth().getDownBW());
			Set<GnpNetBandwidthAllocation> temp = new LinkedHashSet<GnpNetBandwidthAllocation>();
			temp.addAll(connectionsReceiverToSender.get(host).values());
			fairShare(temp, false);
		}
	}

	private void fairShare(Collection<GnpNetBandwidthAllocation> unassigned,
			boolean isSender) {

		if (unassigned.isEmpty()) {
			return;
		}

		double x = Double.POSITIVE_INFINITY;

		GnpNetBandwidthAllocation min = null;
		for (GnpNetBandwidthAllocation ba : unassigned) {

			double bandwidth = 0;
			if (isSender) {
				bandwidth = Math.min(ba.getReceiver()
						.getCurrentBandwidth().getDownBW()
						+ ba.getAllocatedBandwidth(), ba.getBandwidthNeeds());
			} else {
				bandwidth = Math.min(ba.getSender().getCurrentBandwidth()
						.getUpBW()
						+ ba.getAllocatedBandwidth(), ba.getBandwidthNeeds());
			}

			if (bandwidth < x) {
				x = bandwidth;
				min = ba;
			}
		}

		double bw = 0;
		if (isSender) {
			bw = min.getSender().getCurrentBandwidth().getUpBW()
					/ unassigned.size();
		} else {
			bw = min.getReceiver().getCurrentBandwidth().getDownBW()
					/ unassigned.size();
		}

		if (x < bw) {
			if (min.getAllocatedBandwidth() != x) {
				changedAllocations.add(min);
				min.setAllocatedBandwidth(x);
				if (isSender) {
					changedReceivers.add(min.getReceiver());
				} else {
					changedSenders.add(min.getSender());
				}
			}

			if (isSender) {
				Bandwidth curbw = min.getSender().getCurrentBandwidth();
				curbw.setUpBW(curbw.getUpBW() - min.getAllocatedBandwidth());
			} else {
				Bandwidth curbw = min.getReceiver().getCurrentBandwidth();
				curbw.setDownBW(curbw.getDownBW() - min.getAllocatedBandwidth());
			}
			unassigned.remove(min);
			fairShare(unassigned, isSender);
		} else {
			for (GnpNetBandwidthAllocation ba : unassigned) {
				if (Math.abs(ba.getAllocatedBandwidth() - bw) < 0.0000001) {
					changedAllocations.add(ba);
					ba.setAllocatedBandwidth(bw);
					if (isSender) {
						changedReceivers.add(ba.getReceiver());
					} else {
						changedSenders.add(ba.getSender());
					}
				}
				if (isSender) {
					ba.getSender().getCurrentBandwidth().setUpBW(0d);
				} else {
					ba.getReceiver().getCurrentBandwidth().setDownBW(0d);
				}
			}

		}
	}

	@Override
	public BandwidthAllocation getBandwidthAllocationType() {
		return AbstractGnpNetBandwidthManager.BandwidthAllocation.PERIODICAL;
	}

	@Override
	public Set<GnpNetBandwidthAllocation> getChangedAllocations() {
		return changedAllocations;
	}
}
