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

package org.peerfact.impl.overlay.unstructured.zeroaccess.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.metrics.overlay.DocCountM;
import org.peerfact.impl.overlay.unstructured.zeroaccess.ZeroAccessOverlayContact;
import org.peerfact.impl.overlay.unstructured.zeroaccess.ZeroAccessOverlayNode;
import org.peerfact.impl.overlay.unstructured.zeroaccess.ZeroAccessOverlayRoutingTable;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.GetLMessage;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.RetLMessage;

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
 * adapter for ZeroAccess
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 * 
 */
public class ZeroAccessAdapter extends OverlayAdapter {

	public ZeroAccessAdapter() {
		addOverlayImpl(ZeroAccessOverlayNode.class);

		addOverlayImpl(GetLMessage.class);
		addOverlayImpl(RetLMessage.class);

		addOverlayNodeMetric(DocCountM.class);
	}

	private Map<NetID, Set<NetID>> routingTables = new LinkedHashMap<NetID, Set<NetID>>();

	@Override
	public String getOverlayName() {
		return "ZeroAccess";
	}

	@Override
	public void handleLeavingHost(Host host) {
		routingTables.remove(host.getNetLayer().getNetID());
		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {

	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {
		// it's defined here how the appearance of messages is visualized

		if (omsg instanceof GetLMessage) {
			// flashEdge(fromID, toID, Color.darkGray, "Ping", omsg.getClass());
		} else if (omsg instanceof RetLMessage) {
			// flashEdge(fromID, toID, Color.darkGray, "Pong", omsg.getClass());
		}

		updateRoutingtable(from);
		updateRoutingtable(to);
	}

	@Override
	public BootstrapManager<ZeroAccessOverlayNode> getBootstrapManagerFor(
			OverlayNode<?, ?> nd) {
		return null;

		// Not implemented for ZeroAccess
	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// TODO Auto-generated method stub

	}

	private void updateRoutingtable(Host host) {
		if (host != null) {
			ZeroAccessOverlayNode ZeroAccessNode = (ZeroAccessOverlayNode) host
					.getOverlay(ZeroAccessOverlayNode.class);

			ZeroAccessOverlayRoutingTable rt = (ZeroAccessOverlayRoutingTable) ZeroAccessNode
					.getRoutingTable();
			LinkedHashSet<ZeroAccessOverlayContact> rt_set = new LinkedHashSet<ZeroAccessOverlayContact>(
					rt.allContacts());
			if (rt_set.contains(null)) {
				rt_set.remove(null);
			}

			// log.debug(fromNode + " Größe FT:" + ft_set.size());
			// log.debug(fromNode + " FT:" + ft_set);

			if (rt != null) {

				Set<NetID> vis_rt = routingTables.get(host.getNetLayer()
						.getNetID());
				if (vis_rt == null) {
					vis_rt = new LinkedHashSet<NetID>();
					routingTables.put(host.getNetLayer().getNetID(), vis_rt);
				}
				boolean rt_changed = false;
				for (ZeroAccessOverlayContact con : rt_set) {
					NetID newNetID = con.getTransInfo().getNetId();
					if (con != null && !vis_rt.contains(newNetID)) {
						this.addEdge(host.getNetLayer().getNetID(), newNetID,
								Color.LIGHT_GRAY, "Routingtable-Eintrag");
						vis_rt.add(newNetID);
						rt_changed = true;

					}
				}
				if (rt_changed) {
					this.getTranslator().nodeAttributeChanged(
							host.getNetLayer().getNetID(),
							"routingtable_hosts", rt_set.size());
				}
			}
		}
	}

}
