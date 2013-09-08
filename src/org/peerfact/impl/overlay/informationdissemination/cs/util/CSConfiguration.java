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

package org.peerfact.impl.overlay.informationdissemination.cs.util;

import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.simengine.Simulator;

//TODO: split in ServerConfig and ClientConfig
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
 * Configuration of the Client and Server
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 * 
 */
public class CSConfiguration {

	/**
	 * The Area Of Interest radius
	 */
	public static final int AOI = 200;

	/**
	 * The transport protocol, which should be used.
	 */
	public static final TransProtocol TRANSPORT_PROTOCOL = TransProtocol.UDP;

	/**
	 * The time between the operation of a dissemination from the server.
	 */
	public static final long TIME_BETWEEN_DISSEMINATION_SERVER = 200 * Simulator.MILLISECOND_UNIT;

	/**
	 * The time out from a join message.
	 */
	public static final long JOIN_TIME_OUT = 10 * Simulator.SECOND_UNIT;

	/**
	 * Time to the node is declared as offline
	 */
	public static final long TIME_OUT_CLIENT = 5 * Simulator.SECOND_UNIT;

	/**
	 * Time to the node think, the server is down or the server has reseted the
	 * connection.
	 */
	public static final long TIME_OUT_SERVER = 5 * Simulator.SECOND_UNIT;

	/**
	 * Time between the maintenance operation on the server side
	 */
	public static final long TIME_BETWEEN_MAINTENANCE_SERVER = 1 * Simulator.SECOND_UNIT;

	/**
	 * Time between the maintenance operation on the client side
	 */
	public static final long TIME_BETWEEN_MAINTENANCE_CLIENT = 2 * Simulator.SECOND_UNIT;

	/**
	 * The interval time between a heartbeat (update message) from client.
	 */
	public static final long INTERVAL_BETWEEN_HEARTBEATS = 2 * Simulator.SECOND_UNIT;

	/**
	 * Time between the heartbeat operation for a client.
	 */
	public static final long TIME_BETWEEN_HEARTBEAT_OPERATION = 1 * Simulator.SECOND_UNIT;

}
