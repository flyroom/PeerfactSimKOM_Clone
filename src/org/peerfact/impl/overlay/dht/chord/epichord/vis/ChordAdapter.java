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

package org.peerfact.impl.overlay.dht.chord.epichord.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.Translator.EdgeHandle;
import org.peerfact.impl.analyzer.visualization2d.metrics.overlay.DocCountM;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.base.messages.AckMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.HandshakeMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.JoinReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.LeaveMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupMessage;
import org.peerfact.impl.overlay.dht.chord.base.messages.LookupReply;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyOfflineMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifyPredecessorMsg;
import org.peerfact.impl.overlay.dht.chord.base.messages.NotifySuccessorMsg;
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
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordCache;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordNode;
import org.peerfact.impl.overlay.dht.chord.epichord.components.ChordRoutingTable;
import org.peerfact.impl.overlay.dht.chord.epichord.messages.HandshakeReply;
import org.peerfact.impl.overlay.dht.chord.epichord.messages.LookupInfoReply;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.ForwardMsg;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ChordAdapter extends AbstractChordAdapter {

	// protected Set<Integer> lookupRequestsOpIDs = new
	// LinkedHashSet<Integer>();

	protected Map<NetID, Map<NetID, EdgeHandle>> caches = new LinkedHashMap<NetID, Map<NetID, EdgeHandle>>();

	public ChordAdapter() {
		addOverlayImpl(AbstractChordNode.class);

		addOverlayImpl(HandshakeMsg.class); // done
		addOverlayImpl(HandshakeReply.class); // done

		addOverlayImpl(JoinMessage.class); // done
		addOverlayImpl(JoinReply.class); // done
		addOverlayImpl(LeaveMessage.class); // done

		addOverlayImpl(LookupMessage.class); // done
		addOverlayImpl(LookupInfoReply.class); // done
		addOverlayImpl(LookupReply.class); // done
		addOverlayImpl(ValueLookupMessage.class); // done
		addOverlayImpl(ValueLookupReplyMessage.class); // done

		addOverlayImpl(NotifyOfflineMsg.class); // done
		addOverlayImpl(NotifyPredecessorMsg.class); // done
		addOverlayImpl(NotifySuccessorMsg.class); // done
		addOverlayImpl(AckMessage.class); // done

		addOverlayImpl(RetrievePredecessorMsg.class); // done
		addOverlayImpl(RetrievePredecessorReply.class); // done
		addOverlayImpl(RetrieveSuccessorMsg.class); // done
		addOverlayImpl(RetrieveSuccessorReply.class); // done

		addOverlayImpl(StoreMessage.class); //
		addOverlayImpl(StoreReplyMessage.class); //

		addOverlayImpl(ForwardMsg.class); //

		addOverlayNodeMetric(HostsInFT.class);
		addOverlayNodeMetric(ChordIDM.class);
		addOverlayNodeMetric(DocCountM.class);
	}

	@Override
	public void handleLeavingHost(Host host) {
		EdgeHandle removeEdge = predecessors.remove(host);

		if (removeEdge != null) {
			removeEdge.remove();
		}

		NetID nID = host.getNetLayer().getNetID();

		List<Host> toDelete = new LinkedList<Host>();

		for (Map.Entry<Host, EdgeHandle> e : predecessors.entrySet()) {
			if (e.getValue() == null || e.getValue().getTo().equals(nID)
					|| e.getValue().getFrom().equals(nID)) {
				toDelete.add(e.getKey());
			}
		}
		for (Host h : toDelete) {
			predecessors.remove(h).remove();
		}
		fingertables.remove(host.getNetLayer().getNetID());
		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode<?, ?> overlayNode) {

		AbstractChordNode chordNode = (AbstractChordNode) overlayNode;

		attributes.put("ChordID", chordNode.getOverlayID());

		// initialize document count
		docCount.put(chordNode, chordNode.getDHT().getNumberOfDHTEntries());
		attributes.put("doc_count", chordNode.getDHT().getNumberOfDHTEntries());

		overlayImpls.put(host.getNetLayer().getNetID(), chordNode);

		updatePredecessorFor(host);
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode<?, ?> overlayNode) {
		// currently not needed

	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// updatePredecessorFor(host);
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {

		if (from != null) {
			refreshFingertable(from);
			refreshCache(from);
		}
		if (to != null) {
			refreshFingertable(to);
			refreshCache(to);
		}
		if (from != null) {
			updatePredecessorFor(from);
		}
		if (to != null) {
			updatePredecessorFor(to);
		}

		updateDocCount(fromID);
		updateDocCount(toID);

		// Mit dem != null wird überprüft, ob ein entsprechender Host auch
		// existiert.
		// Manchmal versenden Hosts Nachrichten, ohne dass sie zum Szenario
		// dazugekommen
		// sind. Komisch.

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
		} else if (omsg instanceof LookupInfoReply) {
			this.flashEdge(fromID, toID, Color.CYAN, "LookupInfoReply ForNode",
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
		// messages for a handshake
		else if (omsg instanceof HandshakeMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "HandshakeMsg",
					omsg.getClass());
		} else if (omsg instanceof HandshakeReply) {
			this.flashEdge(fromID, toID, Color.BLUE, "HandshakeReply",
					omsg.getClass());
		}
		// messages for notification
		else if (omsg instanceof NotifyOfflineMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "NotifyOfflineMsg",
					omsg.getClass());
		} else if (omsg instanceof NotifyPredecessorMsg) {
			this.flashEdge(fromID, toID, Color.BLUE, "NotifyPredecessorMsg",
					omsg.getClass());
		} else if (omsg instanceof NotifySuccessorMsg) {
			this.flashEdge(fromID, toID, Color.orange, "NotifySuccessorMsg",
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

		else if (omsg instanceof ForwardMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "ForwardMessage",
					omsg.getClass());
		}

	}

	private void updatePredecessorFor(Host host) {
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
			EdgeHandle newEdge = this.addEdge(super.netID(host), pre,
					Color.GREEN, "succ/pre");
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

		// log.debug(fromNode + " Größe FT:" + ft_set.size());
		// log.debug(fromNode + " FT:" + ft_set);

		if (ft != null) {

			Set<NetID> vis_ft = fingertables.get(netID(fromNode));
			if (vis_ft == null) {
				vis_ft = new LinkedHashSet<NetID>();
				fingertables.put(netID(fromNode), vis_ft);
			}
			boolean ft_changed = false;
			for (AbstractChordContact con : ft_set) {
				NetID newNetID = con.getTransInfo().getNetId();
				if (con != null && !vis_ft.contains(newNetID)) {
					this.addEdge(netID(fromNode), newNetID, Color.LIGHT_GRAY,
							"finger");
					vis_ft.add(newNetID);
					ft_changed = true;

				}
			}
			if (ft_changed) {
				this.getTranslator().nodeAttributeChanged(
						fromNode.getNetLayer().getNetID(), "ft_hosts",
						ft_set.size());
			}
		}
	}

	protected void refreshCache(Host fromNode) {
		if (fromNode.getOverlay(AbstractChordNode.class) instanceof ChordNode) {
			ChordRoutingTable crt = (ChordRoutingTable) ((AbstractChordNode) fromNode
					.getOverlay(AbstractChordNode.class))
					.getChordRoutingTable();
			if (crt == null) {
				return;
			}
			ChordCache cache = crt.getChordCache();
			if (cache == null) {
				return;
			}

			Map<NetID, EdgeHandle> vis_cache = caches.get(netID(fromNode));
			if (vis_cache == null) {
				vis_cache = new LinkedHashMap<NetID, EdgeHandle>();
				caches.put(netID(fromNode), vis_cache);
			}

			// remove all cache edges
			for (Entry<NetID, EdgeHandle> entry : vis_cache.entrySet()) {
				EdgeHandle edge = entry.getValue();
				if (edge != null) {
					edge.remove();
				}
			}
			vis_cache.clear();

			// add cache edges
			Set<AbstractChordContact> entries = cache.entries();
			if (!entries.isEmpty()) {
				boolean cache_changed = false;
				for (AbstractChordContact con : entries) {
					NetID newNetID = con.getTransInfo().getNetId();
					if (!vis_cache.containsKey(newNetID)) {
						EdgeHandle edge = this.addEdge(netID(fromNode),
								newNetID, Color.GRAY, "cache");
						vis_cache.put(newNetID, edge);
						cache_changed = true;
					}
				}

				if (cache_changed) {
					this.getTranslator().nodeAttributeChanged(
							fromNode.getNetLayer().getNetID(), "cache_hosts",
							entries.size());
				}
			}
		}
	}

}
