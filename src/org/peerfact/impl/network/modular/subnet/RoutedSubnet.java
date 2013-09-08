/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.network.modular.subnet;

import org.peerfact.impl.network.modular.st.JitterStrategy;
import org.peerfact.impl.network.modular.st.LatencyStrategy;
import org.peerfact.impl.network.modular.st.PLossStrategy;
import org.peerfact.impl.network.modular.subnet.topology.NetworkTopology;

/**
 * An interface for subnets that use routing and allow for topology-scenarios.
 * They must provide some strategy methods which will be called if the
 * Routed...Strategy is used.
 * 
 * <b>Important:</b> The Strategies defined by the subnet are only used, if the
 * corresponding <code>Routed...</code>-Strategy is selected in the
 * Network-Layer configuration! Each routed subnet may also be used with
 * "unrouted" strategies or a mixture.
 * 
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/13/2011
 */
public interface RoutedSubnet extends LatencyStrategy, JitterStrategy,
		PLossStrategy {

	/**
	 * Topology this subnet has to operate on. This allows an unified creation
	 * and processing of routing-based szenarios.
	 * 
	 * @param graph
	 *            One of the provided NetworkTopologies
	 */
	public void setTopology(NetworkTopology topology);

	/**
	 * For Visualizations etc
	 * 
	 * @return
	 */
	public NetworkTopology getTopology();

	/*
	 * forces Methods for "dummy" Routing...Strategy as defined in extended
	 * Interfaces
	 */

}
