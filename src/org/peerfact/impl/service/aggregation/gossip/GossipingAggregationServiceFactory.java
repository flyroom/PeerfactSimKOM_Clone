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

package org.peerfact.impl.service.aggregation.gossip;

import java.util.Iterator;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.JoinLeaveOverlayNode;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.simengine.Simulator;


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
 * Default factory of the Gossiping Aggregation Service.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GossipingAggregationServiceFactory implements ComponentFactory {

	short port = 4000;

	public GossipingAggregationServiceFactory() {
		Monitoring.register();
	}

	@Override
	public Component createComponent(Host host) {
		JoinLeaveOverlayNode<?, ?> nd = getOverlay(host);
		return new GossipingAggregationService(host, nd,
				getNeighborDeterminationStrategyFor(nd), port, getConfig(),
				Simulator.getRandom().nextInt());
	}

	private static JoinLeaveOverlayNode<?, ?> getOverlay(Host host) {
		Iterator<OverlayNode<?, ?>> ols = host.getOverlays();
		OverlayNode<?, ?> nd;
		while (ols.hasNext()) {
			nd = ols.next();
			if (nd instanceof JoinLeaveOverlayNode) {
				return (JoinLeaveOverlayNode<?, ?>) nd;
			}
		}
		throw new ConfigurationException(
				"There are no overlays registered at host " + host
						+ " of type JoinLeaveOverlayNode");
	}

	private static IConfiguration getConfig() {
		return new DefaultConfig();
	}

	public static NeighborDeterminator<?> getNeighborDeterminationStrategyFor(
			JoinLeaveOverlayNode<?, ?> nd) {
		NeighborDeterminator<?> str = nd.getNeighbors();
		if (str == null) {
			throw new ConfigurationException(
					"The overlay "
							+ nd
							+ " is not able to return its neighbors (returned null on request via getNeighbors())");
		}
		return str;
	}

	public void setPort(short port) {
		this.port = port;
	}

}
