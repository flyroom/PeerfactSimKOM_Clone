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

package org.peerfact.impl.common;

import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.HostProperties;
import org.peerfact.api.network.NetPosition;

/**
 * Default implementation of host properties.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 */
public class DefaultHostProperties implements HostProperties {

	private DefaultHost host;

	private boolean churnAffected = true;

	private boolean isolationAffected = true;

	private String id;

	private String groupID;

	private List<ConnectivityListener> conListeners;

	/**
	 * Create new and empty default host properties.
	 */
	public DefaultHostProperties() {
		this.conListeners = new LinkedList<ConnectivityListener>();
	}

	@Override
	public double getCurrentDownloadBandwidth() {
		return host.getNetLayer().getCurrentBandwidth().getDownBW();
	}

	@Override
	public double getCurrentUploadBandwidth() {
		return host.getNetLayer().getCurrentBandwidth().getUpBW();
	}

	@Override
	public NetPosition getNetPosition() {
		return host.getNetLayer().getNetPosition();
	}

	@Override
	public double getMaxDownloadBandwidth() {
		return host.getNetLayer().getMaxBandwidth().getDownBW();
	}

	@Override
	public double getMaxUploadBandwidth() {
		return host.getNetLayer().getMaxBandwidth().getUpBW();
	}

	@Override
	public void setHost(Host host) {
		this.host = (DefaultHost) host;
	}

	public DefaultHost getHost() {
		return host;
	}

	@Override
	public void setEnableChurn(boolean churn) {
		this.churnAffected = churn;
	}

	@Override
	public void setEnableIsolation(boolean isolation) {
		this.isolationAffected = isolation;
	}

	@Override
	public boolean isChurnAffected() {
		return this.churnAffected;
	}

	@Override
	public boolean isIsolationAffected() {
		return this.isolationAffected;
	}

	@Override
	public void addConnectivityListener(ConnectivityListener listener) {
		conListeners.add(listener);
	}

	@Override
	public void removeConnectivityListener(ConnectivityListener listener) {
		conListeners.remove(listener);
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		for (ConnectivityListener listener : conListeners) {
			listener.connectivityChanged(ce);
		}
	}
}
