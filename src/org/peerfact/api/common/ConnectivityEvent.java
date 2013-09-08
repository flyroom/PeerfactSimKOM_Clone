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

import org.peerfact.api.network.NetLayer;

/**
 * Event which notifies that the connectivity of a host changed, i.e. that the
 * host went online or offline. Components interested in the online status
 * should register themselves at host's <code>HostProperty</code> as
 * <code>ConnectivityListener</code>s.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 03.12.2007
 * 
 * @see ConnectivityListener
 * @see HostProperties#addConnectivityListener(ConnectivityListener)
 * @see NetLayer#addConnectivityListener(ConnectivityListener)
 */
public class ConnectivityEvent extends ComponentEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1007991469837564924L;

	private boolean isOnline;

	/**
	 * Creates new immutable connectivity event.
	 * 
	 * @param source
	 *            - source of the connectivity event
	 * @param isOnline
	 *            - what happened: true => went online, false => went offline
	 */
	public ConnectivityEvent(Object source, boolean isOnline) {
		super(source);
		this.isOnline = isOnline;
	}

	/**
	 * @return whether this peer went online
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * @return whether this peer went offline
	 */
	public boolean isOffline() {
		return !isOnline;
	}
}
