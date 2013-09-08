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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.vis;

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
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaAccessOverlayContact;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayNode;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.GnutellaOverlayRoutingTable;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.ConnectMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.OkMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PingMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PongMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PushMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryHitMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryMessage;


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
 * adapter for Gnutella
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 03.11.2008
 * 
 */
public class Gnutella04Adapter extends OverlayAdapter {

	public Gnutella04Adapter() {
		addOverlayImpl(GnutellaOverlayNode.class);

		addOverlayImpl(PingMessage.class);
		addOverlayImpl(PongMessage.class);
		addOverlayImpl(QueryMessage.class);
		addOverlayImpl(PushMessage.class);
		addOverlayImpl(OkMessage.class);
		addOverlayImpl(ConnectMessage.class);

		addOverlayNodeMetric(DocCountM.class);
	}

	private Map<NetID, Set<NetID>> routingTables = new LinkedHashMap<NetID, Set<NetID>>();

	private Map<GnutellaOverlayNode, Integer> docCounts = new LinkedHashMap<GnutellaOverlayNode, Integer>();

	@Override
	public String getOverlayName() {
		return "Gnutella";
	}

	@Override
	public void handleLeavingHost(Host host) {
		routingTables.remove(host.getNetLayer().getNetID());
		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {

		if (overlayNode instanceof GnutellaOverlayNode) {
			GnutellaOverlayNode gn_node = ((GnutellaOverlayNode) overlayNode);

			attributes.putAll(analyzeAttributes(gn_node));

		}

	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {
		// it's defined here how the appearance of messages is visualized

		if (omsg instanceof PingMessage) {
			// flashEdge(fromID, toID, Color.darkGray, "Ping", omsg.getClass());
		} else if (omsg instanceof PongMessage) {
			// flashEdge(fromID, toID, Color.darkGray, "Pong", omsg.getClass());
		} else if (omsg instanceof QueryMessage) {
			flashEdge(fromID, toID, Color.blue, "Query", omsg.getClass());
		} else if (omsg instanceof QueryHitMessage) {
			flashEdge(fromID, toID, Color.green, "QueryHit", omsg.getClass());
		} else if (omsg instanceof PushMessage) {
			flashEdge(fromID, toID, Color.red, "Push", omsg.getClass());
		} else if (omsg instanceof OkMessage) {
			flashEdge(fromID, toID, Color.magenta, "OK", omsg.getClass());
		} else if (omsg instanceof ConnectMessage) {
			flashEdge(fromID, toID, Color.GRAY, "Connect", omsg.getClass());
		}

		refreshAttributesFor(from);
		refreshAttributesFor(to);
		updateRoutingtable(from);
		updateRoutingtable(to);
	}

	/**
	 * Check whether all attributes for the host are still up-to-date and change
	 * them if they need to be
	 * 
	 * @param host
	 */
	protected void refreshAttributesFor(Host host) {
		if (host == null) {
			return;
		}
		OverlayNode<?, ?> ov_node = host.getOverlay(GnutellaOverlayNode.class);

		if (ov_node instanceof GnutellaOverlayNode) {
			Map<String, Serializable> attrsToRefresh = analyzeAttributes((GnutellaOverlayNode) ov_node);

			if (!attrsToRefresh.isEmpty()) {
				getTranslator().nodeAttributesChanged(
						host.getNetLayer().getNetID(), attrsToRefresh);
			}

		}
	}

	/**
	 * Return all attributes, which have changed. For newly created nodes return
	 * all.
	 * 
	 * @param gn_node
	 * @return
	 */
	protected Map<String, Serializable> analyzeAttributes(
			GnutellaOverlayNode gn_node) {
		Map<String, Serializable> attrs = new LinkedHashMap<String, Serializable>();

		refreshDocCount(gn_node, attrs);

		return attrs;
	}

	/**
	 * refreshes the number of documents if
	 * 
	 * @param gn_node
	 * @param attrs
	 */
	private void refreshDocCount(GnutellaOverlayNode gn_node,
			Map<String, Serializable> attrs) {
		int docCount = gn_node.getDocuments().size();
		if (docCounts.get(gn_node) == null
				|| docCount != docCounts.get(gn_node)) {
			attrs.put("doc_count", docCount);
			docCounts.put(gn_node, docCount);
		}
	}

	@Override
	public BootstrapManager<GnutellaOverlayNode> getBootstrapManagerFor(
			OverlayNode<?, ?> nd) {
		return null;

		// Not implemented for Gnutella
	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// TODO Auto-generated method stub

	}

	private void updateRoutingtable(Host host) {
		if (host != null) {
			GnutellaOverlayNode gnutellaNode = (GnutellaOverlayNode) host
					.getOverlay(GnutellaOverlayNode.class);

			GnutellaOverlayRoutingTable rt = (GnutellaOverlayRoutingTable) gnutellaNode
					.getRoutingTable();
			LinkedHashSet<GnutellaAccessOverlayContact> rt_set = new LinkedHashSet<GnutellaAccessOverlayContact>(
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
				for (GnutellaAccessOverlayContact con : rt_set) {
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
