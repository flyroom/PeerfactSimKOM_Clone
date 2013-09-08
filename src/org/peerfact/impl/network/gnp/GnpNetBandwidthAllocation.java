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

import org.peerfact.impl.network.AbstractNetLayer;

/**
 * 
 * @author Gerald Klunker <peerfact@kom.tu-darmstadt.de>
 * @version 0.01, 07/12/12
 */
public class GnpNetBandwidthAllocation {

	private double allocatedBandwidth = 0.0;

	private double bandwidthNeeds = 0.0;

	private AbstractNetLayer receiver;

	private AbstractNetLayer sender;

	private double bidSender[] = new double[2];

	private double bidReciever[] = new double[2];

	private boolean minBidSender = false;

	private boolean minBidReceiver = false;

	public GnpNetBandwidthAllocation(AbstractNetLayer sender,
			AbstractNetLayer receiver) {
		this.sender = sender;
		this.receiver = receiver;
		bidSender[0] = 0;
		bidSender[1] = 0;
		bidReciever[0] = 0;
		bidReciever[1] = 0;
	}

	public double getAllocatedBandwidth() {
		return allocatedBandwidth;
	}

	public void setAllocatedBandwidth(double allocatedBandwidth) {
		this.allocatedBandwidth = allocatedBandwidth;
	}

	public double getBandwidthNeeds() {
		return bandwidthNeeds;
	}

	public void setBandwidthNeeds(double bandwidthNeeds) {
		this.bandwidthNeeds = bandwidthNeeds;
	}

	public AbstractNetLayer getReceiver() {
		return receiver;
	}

	public AbstractNetLayer getSender() {
		return sender;
	}

	/*
	 * Eventbased Allocation only
	 */

	public void initConnection() {
		bidSender[0] = 0;
		bidSender[1] = 0;
		bidReciever[0] = 0;
		bidReciever[1] = 0;
		minBidSender = false;
		minBidReceiver = false;
		sender.getCurrentBandwidth()
				.setUpBW(sender.getMaxBandwidth().getUpBW());
		receiver.getCurrentBandwidth().setDownBW(
				sender.getMaxBandwidth().getDownBW());
	}

	public void setBid(double bid, boolean isMinimal, boolean sender, long step) {
		if (sender) {
			int posC = (int) (step % 2);
			bidSender[posC] = bid;
			minBidSender = isMinimal;
		} else {
			int posC = (int) (step % 2);
			bidReciever[posC] = bid;
			minBidReceiver = isMinimal;
		}
	}

	public double getCurrentBid(boolean forSender, long step) {
		if (forSender) {
			return bidSender[(int) (step % 2)];
		} else {
			return bidReciever[(int) (step % 2)];
		}
	}

	public double getPreviousBid(boolean forSender, long step) {
		if (forSender) {
			return bidSender[(int) ((step + 1) % 2)];
		} else {
			return bidReciever[(int) ((step + 1) % 2)];
		}
	}

	public boolean isBidRepeated(boolean forSender) {
		if (forSender) {
			return Math.abs(bidSender[0] - bidSender[1]) <= 0.0001;
		} else {
			return Math.abs(bidReciever[0] - bidReciever[1]) <= 0.0001;
		}
	}

	public boolean isMinBid(boolean forSender) {
		if (forSender) {
			return minBidSender;
		} else {
			return minBidReceiver;
		}
	}

}
