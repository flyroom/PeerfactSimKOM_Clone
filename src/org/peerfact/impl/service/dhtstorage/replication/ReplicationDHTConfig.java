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

package org.peerfact.impl.service.dhtstorage.replication;

import org.peerfact.impl.simengine.Simulator;

/**
 * a repository class for all DHTService related config values, prepopulated
 * with their default values.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ReplicationDHTConfig {

	/**
	 * Number of Replictes for each file. The file will be stored on
	 * numberOfReplicates Nodes + 1 Root-Node.
	 */
	private int numberOfReplicates = 5;

	/**
	 * At least minimumNumberOfReplicates + root are supposed to store an
	 * object. If there are less active contacts the object will be republished
	 */
	private int minimumNumberOfReplicates = 3;

	/**
	 * Replication to a node will be tried numberOfReplicationTimes before
	 * target contact is given up.
	 */
	private int numberOfReplicationTries = 2;

	/**
	 * PING a contact n times before declaring him dead
	 */
	private int numberOfPingTries = 2;

	/**
	 * time between checks of all roots (replicates contact their roots). This
	 * is a heartbeat-Operation, eyery contact-Information will be refreshed. It
	 * issues a Ping/ACK-Message once for every contact.
	 */
	private long timeBetweenRootPings = 5 * Simulator.MINUTE_UNIT;

	/**
	 * minimum time between replications. A file is not replicated if a certain
	 * threshold of nodes had recent contact with the service
	 */
	private long timeBetweenReplicationChecks = 10 * Simulator.MINUTE_UNIT;

	/**
	 * Time to Live for a contact before it is considered offline
	 */
	private long timeToLiveForContacts = 8 * Simulator.MINUTE_UNIT;

	/**
	 * Number of Replictes for each file. The file will be stored on
	 * numberOfReplicates + 1 Nodes.
	 * 
	 * @return
	 */
	public int getNumberOfReplicates() {
		return Math.max(numberOfReplicates, minimumNumberOfReplicates);
	}

	/**
	 * At least minimumNumberOfReplicates + root are supposed to store an
	 * object. If there are less active contacts the object will be republished
	 * 
	 * @return
	 */
	public int getMinimumNumberOfReplicates() {
		return Math.min(minimumNumberOfReplicates, numberOfReplicates);
	}

	/**
	 * Replication will be tried numberOfReplicationTimes before target contact
	 * is given up.
	 * 
	 * @return
	 */
	public int getNumberOfReplicationTries() {
		return numberOfReplicationTries;
	}

	/**
	 * PING a contact n times before declaring him dead
	 * 
	 * @return
	 */
	public int getNumberOfPingTries() {
		return numberOfPingTries;
	}

	/**
	 * Check roots periodically
	 * 
	 * @return
	 */
	public long getTimeBetweenRootPings() {
		return timeBetweenRootPings;
	}

	/**
	 * minimum time between replications. A file is not replicated if a certain
	 * threshold of nodes had recent contact with the service
	 * 
	 * @return
	 */
	public long getTimeBetweenReplicationChecks() {
		return timeBetweenReplicationChecks;
	}

	/**
	 * Time to Live for a contact before it is considered offline
	 * 
	 * @param timeToLiveForContacts
	 */
	public long getTimeToLiveForContacts() {
		return Math.max(timeToLiveForContacts, timeBetweenRootPings);
	}

	/**
	 * @param numberOfReplicates
	 *            the numberOfReplicates to set
	 */
	public void setNumberOfReplicates(int numberOfReplicates) {
		this.numberOfReplicates = numberOfReplicates;
	}

	/**
	 * @param minimumNumberOfReplicates
	 *            the minimumNumberOfReplicates to set
	 */
	public void setMinimumNumberOfReplicates(int minimumNumberOfReplicates) {
		this.minimumNumberOfReplicates = minimumNumberOfReplicates;
	}

	/**
	 * @param numberOfReplicationTries
	 *            the numberOfReplicationTries to set
	 */
	public void setNumberOfReplicationTries(int numberOfReplicationTries) {
		this.numberOfReplicationTries = numberOfReplicationTries;
	}

	/**
	 * @param numberOfPingTries
	 *            the numberOfPingTries to set
	 */
	public void setNumberOfPingTries(int numberOfPingTries) {
		this.numberOfPingTries = numberOfPingTries;
	}

	/**
	 * @param timeBetweenRootPings
	 *            the timeBetweenRootPings to set
	 */
	public void setTimeBetweenRootPings(long timeBetweenRootPings) {
		this.timeBetweenRootPings = timeBetweenRootPings;
	}

	/**
	 * @param timeBetweenReplicationChecks
	 *            the timeBetweenReplicationChecks to set
	 */
	public void setTimeBetweenReplicationChecks(
			long timeBetweenReplicationChecks) {
		this.timeBetweenReplicationChecks = timeBetweenReplicationChecks;
	}

	/**
	 * Time to Live for a contact before it is considered offline
	 * 
	 * @param timeToLiveForContacts
	 */
	public void setTimeToLiveForContacts(long timeToLiveForContacts) {
		this.timeToLiveForContacts = timeToLiveForContacts;
	}

}
