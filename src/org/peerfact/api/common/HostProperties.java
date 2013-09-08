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

package org.peerfact.api.common;

import org.peerfact.api.network.NetPosition;

/**
 * Provides Methods to get some <i>cross-layer</i> information about a host.
 * There are many possible properties like the geographical position, maximum
 * and current upload rate, maximum and current download rate. Note that
 * depending on the actual components of a host some properties may be empty,
 * e.g. the euclidian point may be unknown/unset.
 * <p>
 * Additionally, the host properties can be used to track the connectivity
 * status of a host by registering a <code>ConnectivityListener</code> with it.
 * <p>
 * Despite the cross-layer information host properties is used to enable the
 * churn per host.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 03.12.2007 TODO revise the description
 * @see ConnectivityListener
 */
public interface HostProperties extends ConnectivityListener {

	/**
	 * Returns a NetPosition on a map. This information is obtained from the
	 * network layer (if supported by the implementation of the network layer).
	 * 
	 * @return NetPosition
	 */
	public NetPosition getNetPosition();

	/**
	 * Returns the maximum upload bandwidth
	 * 
	 * @return positive bandwidth
	 */
	public double getMaxUploadBandwidth();

	/**
	 * Returns the available upload bandwidth
	 * 
	 * @return positive bandwidth
	 */
	public double getCurrentUploadBandwidth();

	/**
	 * Returns the maximum download bandwidth
	 * 
	 * @return positive bandwidth
	 */
	public double getMaxDownloadBandwidth();

	/**
	 * Returns the available download bandwidth
	 * 
	 * @return positive bandwidth
	 */
	public double getCurrentDownloadBandwidth();

	/**
	 * The host this properties belong to.
	 * 
	 * @param host
	 *            host
	 */
	public void setHost(Host host);

	/**
	 * Whether this host should be affected by churn.
	 * 
	 * @param churn
	 *            - churn on/off flag
	 */
	public void setEnableChurn(boolean churn);

	/**
	 * Churn status of this host (churn means that a host goes on and offline
	 * during the simulation)
	 * 
	 * @return whether churn is enabled for this host
	 */
	public boolean isChurnAffected();

	/**
	 * Whether this host should be affected by isolation.
	 * 
	 * @param isolation
	 *            - isolation on/off flag
	 */
	public void setEnableIsolation(boolean isolation);

	/**
	 * Isolation status of this host (isolation means that a host can go only
	 * communicate with its group during simulation)
	 * 
	 * @return whether isolation is enabled for this host
	 */
	public boolean isIsolationAffected();

	/**
	 * Registers a connectivity listener which can track the on/off line status
	 * of this host.
	 * 
	 * @param listener
	 *            - connectivity listener
	 */
	public void addConnectivityListener(ConnectivityListener listener);

	/**
	 * Unregister connectivity listener.
	 * 
	 * @param listener
	 *            - connectivity listener.
	 */
	public void removeConnectivityListener(ConnectivityListener listener);

	/**
	 * The same IDs as used in the configuration files.
	 * 
	 * @return ID of the group this host belongs to
	 */
	public String getId();

	/**
	 * The same groupIDs as used in the configuration files.
	 * 
	 * @return groupID of the group this host belongs to
	 */
	public String getGroupID();
}
