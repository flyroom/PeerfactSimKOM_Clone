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

package org.peerfact.impl.service.dhtstorage.past;

import org.peerfact.impl.simengine.Simulator;

/**
 * Config values for the PAST replication service.
 */
public class PASTConfig {

	/**
	 * Minimum number of replicas.
	 */
	private int numberOfReplicates = 5;
	
	/**
	 * Maximum number of replicas.
	 */
	private int maxNumberOfReplicates = 10;
	
	/**
	 * If true a peer will drop all values upon rejoining the network.
	 */
	private boolean dropFiles = false;

	/**
	 * Replication to a node will be tried numberOfReplicationTimes before
	 * target contact is given up.
	 */
	private int numberOfReplicationTries = 4;
	
	/**
	 * PING a contact n times before declaring him dead
	 */
	private int numberOfPingTries = 4;

	/**
	 * Declares how much time should past before a node is pinged again.
	 */
	private long timeBetweenRootPings = 10 * Simulator.MINUTE_UNIT;

	public int getNumberOfReplicates() {
		return numberOfReplicates;
	}


	public int getNumberOfReplicationTries() {
		return numberOfReplicationTries;
	}


	public int getNumberOfPingTries() {
		return numberOfPingTries;
	}


	public long getTimeBetweenRootPings() {
		return timeBetweenRootPings;
	}



	public void setNumberOfReplicates(int numberOfReplicates) {
		this.numberOfReplicates = numberOfReplicates;
	}



	public void setNumberOfReplicationTries(int numberOfReplicationTries) {
		this.numberOfReplicationTries = numberOfReplicationTries;
	}


	public void setNumberOfPingTries(int numberOfPingTries) {
		this.numberOfPingTries = numberOfPingTries;
	}


	public void setTimeBetweenRootPings(long timeBetweenRootPings) {
		this.timeBetweenRootPings = timeBetweenRootPings;
	}

	public int getMaxNumberOfReplicates() {
		return maxNumberOfReplicates;
	}

	public void setMaxNumberOfReplicates(int maxNumberOfReplicates) {
		this.maxNumberOfReplicates = maxNumberOfReplicates;
	}

	public boolean isDropFiles() {
		return dropFiles;
	}

	public void setDropFiles(boolean dropFiles) {
		this.dropFiles = dropFiles;
	}
	
}
