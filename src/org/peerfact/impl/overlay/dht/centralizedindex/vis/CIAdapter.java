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
 *
 */

package org.peerfact.impl.overlay.dht.centralizedindex.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.SchematicPositioner;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.contentdistribution.DefaultContentDistribution;
import org.peerfact.impl.overlay.contentdistribution.messages.DownloadRequestMsg;
import org.peerfact.impl.overlay.contentdistribution.messages.DownloadResultMsg;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSClientNode;
import org.peerfact.impl.overlay.dht.centralizedstorage.components.CSServerNode;


/**
 * Dieser Overlay-Adapter visualisiert spezielle Eigenschaften des
 * Napster-Protokolls.
 * 
 * @author Julius Rueckert
 * @author Leo Nobach
 * 
 */
public class CIAdapter extends OverlayAdapter {

	/**
	 * Current peer statuses of all hosts.
	 */
	public Map<NetID, PeerStatus> peerStatuses = new LinkedHashMap<NetID, PeerStatus>();

	public Map<DownloadTransaction, EdgeHandle> dl_edges = new LinkedHashMap<DownloadTransaction, EdgeHandle>();

	public CIAdapter() {

		addOverlayImpl(CSClientNode.class);
		addOverlayImpl(CSServerNode.class);
		addOverlayImpl(DownloadRequestMsg.class);
		addOverlayImpl(DownloadResultMsg.class);

		addOverlayNodeMetric(PeerStatusM.class);

	}

	@Override
	public String getOverlayName() {
		return "Napster";
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {
		if (overlayNode instanceof CSClientNode) {
			CSClientNode cSClientNode = (CSClientNode) overlayNode;
			attributes
					.put("peer_status", cSClientNode.getPeerStatus());
		} else if (overlayNode instanceof CSServerNode) {
			CSServerNode cSServerNode = (CSServerNode) overlayNode;
			attributes
					.put("peer_status", cSServerNode.getPeerStatus());
		}
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {

		if (overlayNode instanceof CSClientNode) {

			CSClientNode cSClientNode = (CSClientNode) overlayNode;

			NetID ServerID = cSClientNode.getServerAddress()
					.getNetId();

			addEdge(host.getNetLayer().getNetID(), ServerID, Color.GREEN,
					"server");

		} else if (overlayNode instanceof DefaultContentDistribution) {
			// nothing 2 do

		} else if (overlayNode instanceof CSServerNode) {
			// TODO: produce edge to all connected clients?
		}
	}

	@Override
	public void handleLeavingHost(Host host) {
		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {

		if (omsg instanceof DownloadRequestMsg) {
			// request for download

			// new edge between client and server
			EdgeHandle edge = addEdge(fromID, toID, Color.RED, "download");
			OverlayKey<?> key = ((DownloadRequestMsg) omsg).getKey();
			dl_edges.put(new DownloadTransaction(fromID, toID, key), edge);

		} else if (omsg instanceof DownloadResultMsg) {
			// document received

			OverlayKey<?> key = ((DownloadResultMsg) omsg).getDoc().getKey();
			EdgeHandle edge = dl_edges.remove(new DownloadTransaction(toID,
					fromID, key));
			// remove edge
			if (edge != null) {
				edge.remove();
			}
		}

		checkPeerStatus(from);
		checkPeerStatus(to);
	}

	private void checkPeerStatus(Host from) {
		OverlayNode<?, ?> onode = from.getOverlay(CSClientNode.class);

		if (onode instanceof CSClientNode) {
			PeerStatus pst = ((CSClientNode) onode).getPeerStatus();
			if (peerStatuses.get(from.getNetLayer().getNetID()) != pst) {
				getTranslator().nodeAttributeChanged(
						from.getNetLayer().getNetID(), "peer_status", pst);
				peerStatuses.put(from.getNetLayer().getNetID(), pst);
			}
		}
	}

	@Override
	public BootstrapManager<OverlayNode<?, ?>> getBootstrapManagerFor(
			OverlayNode<?, ?> nd) {
		return null;

		// Not implemented / Needed for Centralized Index
	}

	@Override
	public SchematicPositioner getNewPositioner() {
		return new CIPositioner();
	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// TODO Auto-generated method stub

	}

	public static class DownloadTransaction {

		private NetID requestor;

		private NetID responder;

		private OverlayKey<?> key;

		public DownloadTransaction(NetID requestor, NetID responder,
				OverlayKey<?> key) {
			this.requestor = requestor;
			this.responder = responder;
			this.key = key;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof DownloadTransaction)) {
				return false;
			}
			DownloadTransaction other = (DownloadTransaction) o;
			return other.requestor.equals(this.requestor)
					&& other.responder.equals(this.responder)
					&& other.key.equals(this.key);
		}

		@Override
		public int hashCode() {
			return requestor.hashCode() * 23 + responder.hashCode() * 13
					+ key.hashCode();
		}
	}

}