/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.
 * 
 * PeerfactSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.overlay.dht.chord.carechord.vis;

import java.awt.Color;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.metrics.overlay.DocCountM;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.PositionInfo;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.messages.AckMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.LeaveMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyOfflineMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrievePredecessorReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.RetrieveSuccessorReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.StoreMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.StoreReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.ValueLookupReplyMessage;
import org.peerfact.impl.overlay.dht.chord.base.vis.AbstractChordAdapter;
import org.peerfact.impl.overlay.dht.chord.base.vis.HostsInFT;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordContact;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.carechord.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.carechord.operations.StabilisationOperation;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.ForwardMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;

/**
 * @author Markus Benter (original author)
 * @author Thim Strothmann (Adaptions)
 * 
 */
public class ChordAdapter extends AbstractChordAdapter {

	// protected Map<NetID, AbstractChordNode> overlayImpls = new
	// LinkedHashMap<NetID, AbstractChordNode>();

	protected Map<ChordID, NetID> virtualNodes = new LinkedHashMap<ChordID, NetID>();

	protected Map<ChordID, EdgeHandle> virtualNodeEdges = new LinkedHashMap<ChordID, EdgeHandle>();

	protected Map<Host, EdgeHandle> successors = new LinkedHashMap<Host, EdgeHandle>();

	// protected Map<Host, EdgeHandle> predecessors = new LinkedHashMap<Host,
	// EdgeHandle>();

	// protected Map<NetID, Set<NetID>> fingertables = new LinkedHashMap<NetID,
	// Set<NetID>>();

	protected Map<NetID, Set<EdgeHandle>> unmarkedEdges = new LinkedHashMap<NetID, Set<EdgeHandle>>();

	protected Map<NetID, Set<EdgeHandle>> ringEdges = new LinkedHashMap<NetID, Set<EdgeHandle>>();

	protected Map<NetID, Map<NetID, EdgeHandle>> fingerEdges = new LinkedHashMap<NetID, Map<NetID, EdgeHandle>>();

	// protected Map<AbstractChordNode, Integer> docCount = new
	// LinkedHashMap<AbstractChordNode, Integer>();

	// protected Set<Integer> lookupRequestsOpIDs = new
	// LinkedHashSet<Integer>();

	public ChordAdapter() {
		addOverlayImpl(AbstractChordNode.class);

		addOverlayImpl(JoinMessage.class); // done
		addOverlayImpl(JoinReply.class); // done
		addOverlayImpl(LeaveMessage.class); // done

		addOverlayImpl(LookupMessage.class); // done
		addOverlayImpl(LookupReply.class); // done
		addOverlayImpl(ValueLookupMessage.class); // done
		addOverlayImpl(ValueLookupReplyMessage.class); // done

		addOverlayImpl(NotifyOfflineMsg.class); // done
		addOverlayImpl(AckMessage.class); // done

		addOverlayImpl(RetrievePredecessorMsg.class); // done
		addOverlayImpl(RetrievePredecessorReply.class); // done
		addOverlayImpl(RetrieveSuccessorMsg.class); // done
		addOverlayImpl(RetrieveSuccessorReply.class); // done

		addOverlayImpl(StoreMessage.class); //
		addOverlayImpl(StoreReplyMessage.class); //

		addOverlayImpl(ForwardMsg.class); //

		addOverlayNodeMetric(HostsInFT.class);
		addOverlayNodeMetric(HostsInUE.class);
		addOverlayNodeMetric(ChordIDM.class);
		addOverlayNodeMetric(DocCountM.class);

		addOverlayNodeMetric(VirtualNodeCount.class);

	}

	@Override
	public void handleLeavingHost(Host host) {
		try {
			successors.remove(host).remove();
			predecessors.remove(host).remove();
			NetID nID = host.getNetLayer().getNetID();

			List<Host> toDelete = new LinkedList<Host>();

			for (Map.Entry<Host, EdgeHandle> e : successors.entrySet()) {
				if (e.getValue() == null || e.getValue().getTo().equals(nID)
						|| e.getValue().getFrom().equals(nID)) {
					toDelete.add(e.getKey());
				}
			}
			for (Map.Entry<Host, EdgeHandle> e : predecessors.entrySet()) {
				if (e.getValue() == null || e.getValue().getTo().equals(nID)
						|| e.getValue().getFrom().equals(nID)) {
					toDelete.add(e.getKey());
				}
			}
			for (Host h : toDelete) {
				try {
					if (successors.containsKey(h)) {
						successors.remove(h).remove();
					}
					if (predecessors.containsKey(h)) {
						predecessors.remove(h).remove();
					}
				} catch (Exception e) {
					System.out.print("Exception: " + e);
				}
			}
			fingertables.remove(host.getNetLayer().getNetID());

			getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {

		AbstractChordNode chordNode = (AbstractChordNode) overlayNode;

		attributes.put("ChordID", chordNode.getOverlayID());

		// Initialisiere Dokumenten-Count
		docCount.put(chordNode, chordNode.getDHT().getNumberOfDHTEntries());
		attributes.put("doc_count", chordNode.getDHT().getNumberOfDHTEntries());

		overlayImpls.put(host.getNetLayer().getNetID(), chordNode);

		updateSuccessorFor(host);
		updatePredecessorsFor(host);

	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		// currently not needed

	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {

		if (op.getClass() == StabilisationOperation.class && host != null) {
			refreshVirtualNodeCount(host);
			refreshVirtualNodes(host,
					((StabilisationOperation) op).getDeletedVirtualNodes());
			refreshUnmarkedEdges(host); // TODO: das gehört hier eigentlich
										// nicht hin!
			refreshRingEdges(host); // TODO: das gehört hier eigentlich nicht
									// hin!
			updateSuccessorFor(host); // TODO: das gehört hier eigentlich nicht
										// hin!
			updatePredecessorsFor(host); // TODO: das gehört hier eigentlich
											// nicht hin!
			refreshFingertable(host); // TODO: das gehört hier eigentlich nicht
										// hin!
		}
	}

	private void refreshVirtualNodeCount(Host host) {
		if (host != null) {
			getTranslator().nodeAttributeChanged(
					host.getNetLayer().getNetID(),
					"v_nodes",
					((ChordNode) host.getOverlay(AbstractChordNode.class))
							.getVirtualNodes().size());
		}
	}

	private void refreshVirtualNodes(Host host,
			LinkedList<ChordRoutingTable> deleted) {
		if (host != null) {
			for (AbstractChordRoutingTable rt : ((ChordNode) host
					.getOverlay(AbstractChordNode.class)).getVirtualNodes()) {
				// check if this virtual node does not exist:
				if (!virtualNodes.containsKey(((ChordRoutingTable) rt)
						.getChordId())) {
					// create node!

					// TODO: this is quite sick hacked together!

					NetID n = new NetID() {
						@Override
						public long getTransmissionSize() {
							return 0;
						}
					};

					// create attributes
					Map<String, Serializable> attributes = new LinkedHashMap<String, Serializable>();
					attributes.put("ChordID",
							((ChordRoutingTable) rt).getChordId());

					// compute position on ring:
					double div = 0.0;
					BigInteger MAX_KEY_SIZE = new BigInteger(
							"1461501637330902918203684832716283019655932542975");

					div = ((ChordRoutingTable) rt).getChordId().getValue()
							.doubleValue()
							/ MAX_KEY_SIZE.doubleValue();

					float x = 0.5f + 0.4f * (float) Math.sin(2 * Math.PI * div);
					float y = 0.5f + 0.4f * (float) Math.cos(2 * Math.PI * div);

					// add virtual node to visualisation
					getTranslator().overlayNodeAdded(n, "vNode",
							new PositionInfo(new Coords(x, y)), attributes);
					virtualNodes.put(((ChordRoutingTable) rt).getChordId(), n);

					// add edge from real node to virtual node:
					EdgeHandle h = this.addEdge(rt.getMasterNode().getHost()
							.getNetLayer().getNetID(), n, Color.BLUE,
							"virtualNodeEdge");

					virtualNodeEdges.put(((ChordRoutingTable) rt).getChordId(),
							h);
				}
			}

			// remove thoses who have been deleted:
			if (deleted != null) {
				for (AbstractChordRoutingTable rt : deleted) {
					if (virtualNodeEdges.containsKey(((ChordRoutingTable) rt)
							.getChordId())) {
						virtualNodeEdges.get(
								((ChordRoutingTable) rt).getChordId()).remove();
					}

					if (virtualNodes.containsKey(((ChordRoutingTable) rt)
							.getChordId())) {
						getTranslator().overlayNodeRemoved(
								virtualNodes.get(((ChordRoutingTable) rt)
										.getChordId()));
					}

				}
			}
		}
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {

		if (from != null) {
			refreshFingertable(from);
		}
		if (to != null)
		{
			refreshFingertable(to);
			// if (from != null)
			// updatePredecessorFor(from);
			// if (to != null)
			// updatePredecessorFor(to);
		}

		if (from != null) {
			refreshUnmarkedEdges(from);
		}
		if (to != null) {
			refreshUnmarkedEdges(to);
		}

		updateDocCount(fromID);
		updateDocCount(toID);

		// With != null is checked whether a corresponding host exists.
		// Sometimes, hosts send messages without that they have joined the
		// scenario. Strange.

		// if (omsg instanceof LookupReply) {
		// LookupReply lookupReply = (LookupReply) omsg;
		// if (lookupRequestsOpIDs.remove(lookupReply)) {
		// Log.warn("Removed LookupReply with hash "+lookupReply.hashCode());
		// this.flashEdge(fromID, toID, Color.BLUE,
		// "LookupReply ForValue", omsg.getClass());
		// }
		// } else if (omsg instanceof LookupRequest) {
		// LookupRequest lookupRequest = (LookupRequest) omsg;
		// if (lookupRequest.forValue()) {
		// lookupRequestsOpIDs.add(lookupRequest.hashCode());
		// this.flashEdge(fromID, toID, Color.RED,
		// "LookupRequest ForValue", omsg.getClass());
		// }
		// } else if (omsg instanceof GetInfoRequest) {
		// GetInfoRequest getInfoRequest = (GetInfoRequest) omsg;
		// if (getInfoRequest.getCmdID() == -2) {
		// // getInfoRequestMap.put(currentOpID, getInfoRequest);
		// this.flashEdge(fromID, toID, Color.BLACK, "GetInfoRequest",
		// omsg.getClass());
		// }
		// } else if (omsg instanceof GetInfoReply) {
		// GetInfoReply getInfoReply = (GetInfoReply) omsg;
		// if (getInfoReply.getCmdID() == -2) {
		// // getInfoReplyMap.put(currentOpID, getInfoReply);
		// this.flashEdge(fromID, toID, Color.ORANGE, "GetInfoReply", omsg
		// .getClass());
		// }
		// }
		// messages for node lookup
		if (omsg instanceof LookupReply) {
			this.flashEdge(fromID, toID, Color.BLUE, "LookupReply ForNode",
					omsg.getClass());
		} else if (omsg instanceof LookupMessage) {
			this.flashEdge(fromID, toID, Color.RED, "LookupRequest ForNode",
					omsg.getClass());
		}
		// messages of join and leave
		else if (omsg instanceof JoinMessage) {
			this.flashEdge(fromID, toID, Color.GREEN, "JoinMsg",
					omsg.getClass());
		} else if (omsg instanceof JoinReply) {
			this.flashEdge(fromID, toID, Color.BLUE, "JoinReply",
					omsg.getClass());
		} else if (omsg instanceof LeaveMessage) {
			this.flashEdge(fromID, toID, Color.PINK, "LeaveMsg",
					omsg.getClass());
		}
		// messages for value lookup
		if (omsg instanceof ValueLookupReplyMessage) {
			this.flashEdge(fromID, toID, Color.GREEN, "LookupReply ForValue",
					omsg.getClass());
		} else if (omsg instanceof ValueLookupMessage) {
			this.flashEdge(fromID, toID, Color.YELLOW,
					"LookupRequest ForValue", omsg.getClass());
		}
		// messages for predecessor and successor
		else if (omsg instanceof RetrievePredecessorMsg) {
			this.flashEdge(fromID, toID, Color.BLACK,
					"RetrievePredecessorRequest", omsg.getClass());
		} else if (omsg instanceof RetrievePredecessorReply) {
			this.flashEdge(fromID, toID, Color.ORANGE,
					"RetrievePredecessorReply", omsg.getClass());
		} else if (omsg instanceof RetrieveSuccessorMsg) {
			this.flashEdge(fromID, toID, Color.BLACK,
					"RetrieveSuccessorRequest", omsg.getClass());
		} else if (omsg instanceof RetrieveSuccessorReply) {
			this.flashEdge(fromID, toID, Color.ORANGE,
					"RetrieveSuccessorReply", omsg.getClass());
		}
		// messages for notification
		else if (omsg instanceof NotifyOfflineMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "NotifyOfflineMsg",
					omsg.getClass());
		} else if (omsg instanceof AckMessage) {
			this.flashEdge(fromID, toID, Color.YELLOW, "AckMessage",
					omsg.getClass());
		}
		// messages for store
		else if (omsg instanceof StoreMessage) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "StoreMessage",
					omsg.getClass());
		} else if (omsg instanceof StoreReplyMessage) {
			this.flashEdge(fromID, toID, Color.BLUE, "StoreReplyMessage",
					omsg.getClass());
		}

		else if (omsg instanceof KBRForwardMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "ForwardMessage",
					omsg.getClass());
		}

	}

	private void refreshUnmarkedEdges(Host fromNode) {
		AbstractChordRoutingTable crt = ((AbstractChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getChordRoutingTable();

		refreshUnmarkedEdgesForNetId(netID(fromNode), crt);

		// now update vNodes:
		for (AbstractChordRoutingTable v : ((ChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getVirtualNodes()) {
			refreshUnmarkedEdgesForNetId(
					virtualNodes.get(((ChordRoutingTable) v).getChordId()), v);
		}

	}

	private void refreshUnmarkedEdgesForNetId(NetID netid,
			AbstractChordRoutingTable crt) {
		if (crt == null) {
			return;
		}
		AbstractChordContact[] ue = ((ChordRoutingTable) crt)
				.getUnmarkedEdgesAsArray();
		LinkedHashSet<AbstractChordContact> ue_set = new LinkedHashSet<AbstractChordContact>(
				Arrays.asList(ue));
		if (ue_set.contains(null)) {
			ue_set.remove(null);
		}

		if (ue != null) {

			Set<EdgeHandle> vis_ue = unmarkedEdges.get(netid);
			if (vis_ue == null) {
				vis_ue = new LinkedHashSet<EdgeHandle>();
				unmarkedEdges.put(netid, vis_ue);
			}

			// delete all edges that do not exist nomore.
			for (EdgeHandle h : vis_ue) {
				if (h == null) {
					continue;
				}

				boolean stillInUse = false;

				for (AbstractChordContact con : ue_set) {
					NetID newNetID;
					if (((ChordContact) con).isRealNode()) {
						newNetID = con.getTransInfo().getNetId();
					} else {
						newNetID = virtualNodes.get(con.getOverlayID());
					}

					if (h.getTo() == newNetID) {
						stillInUse = true;
					}
				}

				if (!stillInUse) {
					h.remove();
				}
			}

			// add all edges that are new
			for (AbstractChordContact con : ue_set) {
				NetID newNetID;
				if (((ChordContact) con).isRealNode()) {
					newNetID = con.getTransInfo().getNetId();
				} else {
					newNetID = virtualNodes.get(con.getOverlayID());
				}

				boolean inSet = false;
				for (EdgeHandle h : vis_ue) {
					if (h == null) {
						continue;
					}
					if (newNetID == h.getTo()) {
						inSet = true;
					}
				}

				if (!inSet) {
					EdgeHandle e = this.addEdge(netid, newNetID, Color.ORANGE,
							"unmarkedEdges");
					vis_ue.add(e);
				}
			}

			this.getTranslator().nodeAttributeChanged(netid, "ue_hosts",
					ue_set.size());
		}
	}

	private void refreshRingEdges(Host fromNode) {
		AbstractChordRoutingTable crt = ((AbstractChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getChordRoutingTable();

		refreshRingEdgesForNetId(netID(fromNode), crt);

		// now update vNodes:
		for (AbstractChordRoutingTable v : ((ChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getVirtualNodes()) {
			refreshRingEdgesForNetId(
					virtualNodes.get(((ChordRoutingTable) v).getChordId()), v);
		}

	}

	private void refreshRingEdgesForNetId(NetID netid,
			AbstractChordRoutingTable crt) {
		if (crt == null) {
			return;
		}
		AbstractChordContact[] ue = ((ChordRoutingTable) crt)
				.getRingEdgesAsArray();
		LinkedHashSet<AbstractChordContact> ue_set = new LinkedHashSet<AbstractChordContact>(
				Arrays.asList(ue));
		if (ue_set.contains(null)) {
			ue_set.remove(null);
		}

		if (ue != null) {

			Set<EdgeHandle> vis_ue = ringEdges.get(netid);
			if (vis_ue == null) {
				vis_ue = new LinkedHashSet<EdgeHandle>();
				ringEdges.put(netid, vis_ue);
			}

			// delete all edges that do not exist nomore.
			for (EdgeHandle h : vis_ue) {
				if (h == null) {
					continue;
				}

				boolean stillInUse = false;

				for (AbstractChordContact con : ue_set) {
					NetID newNetID;
					if (((ChordContact) con).isRealNode()) {
						newNetID = con.getTransInfo().getNetId();
					} else {
						newNetID = virtualNodes.get(con.getOverlayID());
					}

					if (h.getTo() == newNetID) {
						stillInUse = true;
					}
				}

				if (!stillInUse) {
					h.remove();
				}
			}

			// add all edges that are new
			for (AbstractChordContact con : ue_set) {
				NetID newNetID;
				if (((ChordContact) con).isRealNode()) {
					newNetID = con.getTransInfo().getNetId();
				} else {
					newNetID = virtualNodes.get(con.getOverlayID());
				}

				boolean inSet = false;
				for (EdgeHandle h : vis_ue) {
					if (h == null) {
						continue;
					}
					if (newNetID == h.getTo()) {
						inSet = true;
					}
				}

				if (!inSet) {
					EdgeHandle e = this.addEdge(netid, newNetID, Color.MAGENTA,
							"ringEdges");
					vis_ue.add(e);
				}
			}

		}
	}

	protected void updateSuccessorFor(Host host) {
		NetID pre = getOverlaySuccessor(host);
		EdgeHandle old_pre = successors.get(host);
		// log.debug("PREDECESSORS: " + pre + "|" + ((old_pre !=
		// null)?old_pre.getTo():"null"));

		if (pre == null) {
			if (old_pre != null) {
				old_pre.remove();
			}
		} else if (old_pre == null || !pre.equals(old_pre.getTo())) {
			if (old_pre != null) {
				old_pre.remove();
			}
			EdgeHandle newEdge = this.addEdge(netID(host), pre, Color.GREEN,
					"successor");
			successors.put(host, newEdge);
		}

	}

	protected void updatePredecessorsFor(Host host) {
		NetID pre = getOverlayPredecessor(host);
		EdgeHandle old_pre = predecessors.get(host);
		// log.debug("PREDECESSORS: " + pre + "|" + ((old_pre !=
		// null)?old_pre.getTo():"null"));

		if (pre == null) {
			if (old_pre != null) {
				old_pre.remove();
			}
		} else if (old_pre == null || !pre.equals(old_pre.getTo())) {
			if (old_pre != null) {
				old_pre.remove();
			}
			EdgeHandle newEdge = this.addEdge(netID(host), pre, Color.CYAN,
					"predecessor");
			predecessors.put(host, newEdge);
		}

	}

	@Override
	protected void refreshFingertable(Host fromNode) {
		AbstractChordRoutingTable crt = ((AbstractChordNode) fromNode
				.getOverlay(AbstractChordNode.class)).getChordRoutingTable();
		if (crt == null) {
			return;
		}
		AbstractChordContact[] ft = crt.copyFingerTable();
		LinkedHashSet<AbstractChordContact> ft_set = new LinkedHashSet<AbstractChordContact>(
				Arrays.asList(ft));
		if (ft_set.contains(null)) {
			ft_set.remove(null);
		}

		if (ft != null) {

			Map<NetID, EdgeHandle> edgeHandles = fingerEdges
					.get(netID(fromNode));
			if (edgeHandles == null) {
				edgeHandles = new LinkedHashMap<NetID, EdgeHandle>();
				fingerEdges.put(netID(fromNode), edgeHandles);
			}

			Set<NetID> vis_ft = fingertables.get(netID(fromNode));
			if (vis_ft == null) {
				vis_ft = new LinkedHashSet<NetID>();
				fingertables.put(netID(fromNode), vis_ft);
			}
			boolean ft_changed = false;
			// add new edges
			for (AbstractChordContact con : ft_set) {
				NetID newNetID = con.getTransInfo().getNetId();
				if (con != null && !vis_ft.contains(newNetID)) {
					EdgeHandle h = this.addEdge(netID(fromNode), newNetID,
							Color.LIGHT_GRAY, "finger");
					edgeHandles.put(newNetID, h);
					vis_ft.add(newNetID);
					ft_changed = true;

				}
			}
			// remove unused edges:
			if (vis_ft != null) {
				for (NetID nId : vis_ft) {
					boolean inUse = false;
					for (AbstractChordContact con : ft_set) {
						if (con.getTransInfo().getNetId().toString()
								.equals(nId.toString())) {
							inUse = true;
							break;
						}
					}
					if (!inUse && edgeHandles.containsKey(nId)) {
						// remove edge.
						edgeHandles.get(nId).remove();
						edgeHandles.remove(nId);
					}

				}
			}
			if (ft_changed) {
				this.getTranslator().nodeAttributeChanged(
						fromNode.getNetLayer().getNetID(), "ft_hosts",
						ft_set.size());
			}
		}
	}

}
