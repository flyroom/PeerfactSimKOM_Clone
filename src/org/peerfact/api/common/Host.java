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

import java.util.Iterator;

import org.peerfact.api.application.Application;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.user.User;


/**
 * Host represents an end-system in a (p2p) network. A host contains several
 * layers (network, transport, overlay and application) while one layer may
 * contain several components (e.g. two overlays in the same host). Further, a
 * host stores additional properties in a HostProperites object and the user
 * object which may represent .
 * <p>
 * Before a simulation can be started a host may receive some basic actions
 * (=operations) to be performed during the simulation. These actions are
 * specified outside of the host and the host schedule them when the
 * <code>scheduleEvents()</code> method is called.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 3.0, 03.12.2007
 */
public interface Host {

	/**
	 * Returns a User (behavior) object
	 * 
	 * @return User
	 */
	public User getUser();

	/**
	 * Returns the current NetworkLayer
	 * 
	 * @return a general Network Interface
	 */
	public NetLayer getNetLayer();

	/**
	 * Returns a List of Overlays
	 * 
	 * @return List of Overlays
	 */
	public Iterator<OverlayNode<?, ?>> getOverlays();

	/**
	 * Returns the Application
	 * 
	 * @return Application
	 */
	public Application getApplication();

	/**
	 * Returns host properties
	 * 
	 * @return Properties of an Host
	 */
	public HostProperties getProperties();

	/**
	 * Returns a registered overlay
	 * 
	 * @param api
	 *            interface class which should be implemented by the required
	 *            overlay (e.g. DHTNode)
	 * @return overlay
	 */
	public OverlayNode<?, ?> getOverlay(Class<?> api);

	/**
	 * Returns the transport layer
	 * 
	 * @return transport layer
	 */
	public TransLayer getTransLayer();

	/**
	 * Get the component which is instance of the specified class or interface.
	 * 
	 * @param componentClass
	 *            - class or interface which must be implemented by the desired
	 *            component
	 * @return component which is instance of the specified interface or class.
	 */
	public <T extends Component> T getComponent(
			Class<? extends Component> componentClass);

}
