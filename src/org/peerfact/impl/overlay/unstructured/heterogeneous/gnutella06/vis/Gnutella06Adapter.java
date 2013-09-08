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

package org.peerfact.impl.overlay.unstructured.heterogeneous.gnutella06.vis;

import java.awt.Color;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.analyzer.UnstructuredOverlayAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.OverlayAdapter;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Gnutella06OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.AbstractGnutellaLikeNode;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class Gnutella06Adapter extends OverlayAdapter implements
		UnstructuredOverlayAnalyzer {

	Map<OverlayContact<?>, NetID> ids = new LinkedHashMap<OverlayContact<?>, NetID>();

	Map<Connection, EdgeHandle> conns = new LinkedHashMap<Connection, EdgeHandle>();

	public Gnutella06Adapter() {
		addOverlayImpl(AbstractGnutellaLikeNode.class);
		addOverlayNodeMetric(Type.class);
	}

	@Override
	public Object getBootstrapManagerFor(OverlayNode<?, ?> nd) {
		if (nd instanceof AbstractGnutellaLikeNode) {
			AbstractGnutellaLikeNode<?, ?> node = (AbstractGnutellaLikeNode<?, ?>) nd;

			return node.getBootstrap();
		} else {
			return null;
		}
	}

	@Override
	public String getOverlayName() {
		return "Gnutella 0.6";
	}

	@Override
	public void handleLeavingHost(Host host) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {
		if (overlayNode instanceof AbstractGnutellaLikeNode) {
			AbstractGnutellaLikeNode<?, ?> nd = (AbstractGnutellaLikeNode<?, ?>) overlayNode;

			ids.put(nd.getOwnContact(), host.getNetLayer().getNetID());

		}
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {
		// not needed as the visualization of messages is done by the method
		// overlayMsgOccured(...) in VisAnalyzer since the flag
		// messageEdges="true" is set in the respective xml-config-file

	}

	@Override
	public void connectionStarted(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		conns.put(new Connection(invoker, receiver), this.addEdge(invoker
				.getTransInfo().getNetId(), receiver.getTransInfo().getNetId(),
				Color.YELLOW, "Connecting"));
		log.debug("Connection started: " + invoker + ", " + receiver);
	}

	@Override
	public void connectionSucceeded(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {

		boolean leafConnection = (receiver instanceof Gnutella06OverlayContact
				&& invoker instanceof Gnutella06OverlayContact && (!((Gnutella06OverlayContact) invoker)
				.isUltrapeer() || !((Gnutella06OverlayContact) invoker)
				.isUltrapeer()));

		EdgeHandle hdl = conns.get(new Connection(invoker, receiver));
		if (hdl != null) {
			hdl.remove();
		}

		if (leafConnection) {
			conns.put(new Connection(invoker, receiver), this.addEdge(invoker
					.getTransInfo().getNetId(), receiver.getTransInfo()
					.getNetId(), Color.GREEN, "leaf-2-up"));
		} else {
			conns.put(new Connection(invoker, receiver), this.addEdge(invoker
					.getTransInfo().getNetId(), receiver.getTransInfo()
					.getNetId(), Color.CYAN, "up-2-up"));
		}

		System.out
				.println("Connection succeeded: " + invoker + ", " + receiver);
	}

	@Override
	public void pingTimeouted(OverlayContact<?> invoker,
			OverlayContact<?> receiver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queryFailed(OverlayContact<?> initiator, Query query,
			int hits, double averageHops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queryMadeHop(int queryUID, OverlayContact<?> hopContact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queryStarted(OverlayContact<?> initiator, Query query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void querySucceeded(OverlayContact<?> initiator,
			Query query, int hits, double averageHops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reBootstrapped(OverlayContact<?> c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionBreakCancel(OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent) {
		EdgeHandle hdl = conns.get(new Connection(notifiedNode, opponent));
		if (hdl != null) {
			hdl.remove();
		} else {
			log.debug("CONNECTION NOT FOUND");
		}
		log.debug("Connection broke: " + notifiedNode + ", "
				+ opponent);
	}

	@Override
	public void connectionBreakTimeout(OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent) {
		EdgeHandle hdl = conns.get(new Connection(notifiedNode, opponent));
		if (hdl != null) {
			hdl.remove();
		} else {
			log.debug("CONNECTION NOT FOUND");
		}
		log.debug("Connection broke: " + notifiedNode + ", "
				+ opponent);
	}

	static class Connection {

		private OverlayContact<?> b;

		private OverlayContact<?> a;

		public Connection(OverlayContact<?> a,
				OverlayContact<?> b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			return a.hashCode() + b.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Connection)) {
				return false;
			}
			Connection other = (Connection) obj;
			return other.a.equals(this.a) && other.b.equals(this.b)
					|| other.a.equals(this.b) && other.b.equals(this.a);
		}

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(Writer output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionDenied(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionTimeout(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		// TODO Auto-generated method stub

	}

}
