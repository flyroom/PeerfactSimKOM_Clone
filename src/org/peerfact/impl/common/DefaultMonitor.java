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

package org.peerfact.impl.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.AggregationAnalyzer;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.analyzer.ChurnAnalyzer;
import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.analyzer.DHTOverlayAnalyzer;
import org.peerfact.api.analyzer.IOldFilesharingAnalyzer;
import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.analyzer.UnstructuredOverlayAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Monitor;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.Transmitable;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Default monitor implementation which is called by components whenever an
 * action occurs that is important to trace. In particular, upon calling a
 * specific monitor method, the monitor delegates notifications to all installed
 * analyzers.
 * 
 * @author Philip Wette <info@peerfact.org>
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 */
public class DefaultMonitor implements Monitor {
	private static Logger log = SimLogger.getLogger(DefaultMonitor.class);

	private BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
			System.out));

	protected boolean isMonitoring;

	/** Monitor list for base monitor. */
	private final List<Analyzer> baseAnalyzers;

	/** List of analyzers monitoring at the network layer. */
	protected final List<NetAnalyzer> netAnalyzers;

	/** List of analyzers monitoring at the transport layer. */
	protected final List<TransAnalyzer> transAnalyzers;

	/** List of analyzers monitoring the executed operation. */
	protected final List<OperationAnalyzer> opAnalyzers;

	/**
	 * List of analyzers monitoring the session times of peers as well as their
	 * arrival and depature.
	 */
	protected final List<ChurnAnalyzer> churnAnalyzers;

	/**
	 * List of analyzers monitoring at the overlay layer overlays that implement
	 * the KBR-interface.
	 */
	protected final List<KBROverlayAnalyzer> kbrOverlayAnalyzers;

	/**
	 * List of analyzers monitoring at the overlay layer overlays that implement
	 * the DHT-interface.
	 */
	protected final List<DHTOverlayAnalyzer> dhtOverlayAnalyzers;

	/**
	 * List of analyzers monitoring at the overlay layer overlays that implement
	 * unstructured overlay interfaces.
	 */
	protected final List<UnstructuredOverlayAnalyzer> unstructuredOverlayAnalyzers;

	@Deprecated
	protected final List<IOldFilesharingAnalyzer> iOldFilesharingAnalyzers;

	/**
	 * List of connectivity analyzers.
	 */
	protected final List<ConnectivityAnalyzer> connAnalyzers;

	/**
	 * List for aggregation analyzers.
	 */
	protected final List<AggregationAnalyzer> aggregationAnalyzers;

	public DefaultMonitor() {
		this.baseAnalyzers = new LinkedList<Analyzer>();
		this.netAnalyzers = new LinkedList<NetAnalyzer>();
		this.transAnalyzers = new LinkedList<TransAnalyzer>();
		this.churnAnalyzers = new LinkedList<ChurnAnalyzer>();
		this.opAnalyzers = new LinkedList<OperationAnalyzer>();
		this.connAnalyzers = new LinkedList<ConnectivityAnalyzer>();
		this.kbrOverlayAnalyzers = new LinkedList<KBROverlayAnalyzer>();
		this.dhtOverlayAnalyzers = new LinkedList<DHTOverlayAnalyzer>();
		this.unstructuredOverlayAnalyzers = new LinkedList<UnstructuredOverlayAnalyzer>();
		this.iOldFilesharingAnalyzers = new LinkedList<IOldFilesharingAnalyzer>();
		this.aggregationAnalyzers = new LinkedList<AggregationAnalyzer>();
		Simulator.getInstance().setMonitor(this);
		this.isMonitoring = false;
	}

	@Override
	public void setAnalyzer(Analyzer analyzer) {
		if (!this.baseAnalyzers.contains(analyzer)) {
			this.baseAnalyzers.add(analyzer);
			if (analyzer instanceof NetAnalyzer) {
				this.netAnalyzers.add((NetAnalyzer) analyzer);
			}
			if (analyzer instanceof TransAnalyzer) {
				this.transAnalyzers.add((TransAnalyzer) analyzer);
			}
			if (analyzer instanceof OperationAnalyzer) {
				this.opAnalyzers.add((OperationAnalyzer) analyzer);
			}
			if (analyzer instanceof ChurnAnalyzer) {
				this.churnAnalyzers.add((ChurnAnalyzer) analyzer);
			}
			if (analyzer instanceof ConnectivityAnalyzer) {
				this.connAnalyzers.add((ConnectivityAnalyzer) analyzer);
			}
			if (analyzer instanceof IOldFilesharingAnalyzer) {
				this.iOldFilesharingAnalyzers
						.add((IOldFilesharingAnalyzer) analyzer);
			}
			if (analyzer instanceof KBROverlayAnalyzer) {
				this.kbrOverlayAnalyzers.add((KBROverlayAnalyzer) analyzer);
			}
			if (analyzer instanceof DHTOverlayAnalyzer) {
				this.dhtOverlayAnalyzers.add((DHTOverlayAnalyzer) analyzer);
			}
			if (analyzer instanceof UnstructuredOverlayAnalyzer) {
				this.unstructuredOverlayAnalyzers
						.add((UnstructuredOverlayAnalyzer) analyzer);
			}
			if (analyzer instanceof AggregationAnalyzer) {
				this.aggregationAnalyzers.add((AggregationAnalyzer) analyzer);
			}
		}
	}

	@Override
	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_START);
	}

	@Override
	public void setStop(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_STOP);
	}

	public void close() {
		if (this.baseAnalyzers.size() != 0) {
			try {
				output.write("*******************************************************\n");
				output.write("# Monitoring results \n");
				output.newLine();
				for (Analyzer analyzer : this.baseAnalyzers) {
					analyzer.stop(output);
				}
				output.write("*******************************************************\n");
				// output.close();

			} catch (IOException e) {
				log.error("Failed to print monitoring results.", e);
			}
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.MONITOR_START)) {
			this.isMonitoring = true;
			for (Analyzer analyzer : this.baseAnalyzers) {
				analyzer.start();
			}
		} else {
			this.close();
			this.isMonitoring = false;
		}
	}

	/**
	 * Specifies where to write the monitoring results to.
	 * 
	 * @param output
	 *            writer (e.g. FileWriter, StringWriter, ...)
	 */
	public void setResultWriter(Writer output) {
		this.output = new BufferedWriter(output);
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		if (isMonitoring) {
			for (OperationAnalyzer opAna : this.opAnalyzers) {
				opAna.operationInitiated(op);
			}
		}
	}

	@Override
	public void operationFinished(Operation<?> op) {
		if (isMonitoring) {
			for (OperationAnalyzer opAna : this.opAnalyzers) {
				opAna.operationFinished(op);
			}
		}
	}

	@Override
	public void nextInterSessionTime(long time) {
		if (isMonitoring) {
			for (ChurnAnalyzer churnAna : this.churnAnalyzers) {
				churnAna.nextInterSessionTime(time);
			}
		}
	}

	@Override
	public void nextSessionTime(long time) {
		if (isMonitoring) {
			for (ChurnAnalyzer churnAna : this.churnAnalyzers) {
				churnAna.nextSessionTime(time);
			}
		}
	}

	@Override
	public void churnEvent(Host host, Reason reason) {
		if (isMonitoring) {
			switch (reason) {
			case ONLINE:
				for (ConnectivityAnalyzer connAna : this.connAnalyzers) {
					connAna.onlineEvent(host);
				}
				break;
			case OFFLINE:
				for (ConnectivityAnalyzer connAna : this.connAnalyzers) {
					connAna.offlineEvent(host);
				}
				break;
			default:
				throw new RuntimeException("error: reason (" + reason + ")");
			}
		}
	}

	@Override
	public void netMsgEvent(NetMessage msg, NetID id, Reason reason) {
		if (isMonitoring) {
			switch (reason) {
			case SEND:
				for (NetAnalyzer monitor : this.netAnalyzers) {
					monitor.netMsgSend(msg, id);
				}
				break;
			case RECEIVE:
				for (NetAnalyzer monitor : this.netAnalyzers) {
					monitor.netMsgReceive(msg, id);
				}
				break;
			case DROP:
				for (NetAnalyzer monitor : this.netAnalyzers) {
					monitor.netMsgDrop(msg, id);
				}
				break;
			default:
				throw new RuntimeException("error: reason (" + reason + ")");
			}
		}
	}

	@Override
	public void transMsgReceived(final AbstractTransMessage msg) {
		if (isMonitoring) {
			for (final TransAnalyzer monitor : this.transAnalyzers) {
				monitor.transMsgReceived(msg);
			}
		}
	}

	@Override
	public void transMsgSent(final AbstractTransMessage msg) {
		if (isMonitoring) {
			for (final TransAnalyzer monitor : this.transAnalyzers) {
				monitor.transMsgSent(msg);
			}
		}
	}

	@Override
	public void kbrOverlayMessageDelivered(OverlayContact<?> contact,
			Message msg,
			int hops) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer analyzer : this.kbrOverlayAnalyzers) {
				analyzer.overlayMessageDelivered(contact, msg, hops);
			}
		}
	}

	@Override
	public void kbrOverlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int hops) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer analyzer : this.kbrOverlayAnalyzers) {
				analyzer.overlayMessageForwarded(sender, receiver, msg, hops);
			}
		}
	}

	@Override
	public void kbrQueryStarted(OverlayContact<?> contact, Message appMsg) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer analyzer : this.kbrOverlayAnalyzers) {
				analyzer.queryStarted(contact, appMsg);
			}
		}
	}

	@Override
	public void kbrQueryFailed(OverlayContact<?> failedHop, Message appMsg) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer analyzer : this.kbrOverlayAnalyzers) {
				analyzer.queryFailed(failedHop, appMsg);
			}
		}
	}

	@Override
	public void dhtStoreInitiated(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.storeInitiated(contact, key, object);
			}
		}
	}

	@Override
	public void dhtStoreFailed(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.storeFailed(contact, key, object);
			}
		}
	}

	@Override
	public void dhtStoreFinished(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object, List<OverlayContact<?>> responsibleContacts) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.storeFinished(contact, key, object,
						responsibleContacts);
			}
		}
	}

	@Override
	public void dhtLookupInitiated(OverlayContact<?> contact, DHTKey<?> key) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.lookupInitiated(contact, key);
			}
		}
	}

	@Override
	public void dhtLookupForwarded(OverlayContact<?> contact, DHTKey<?> key,
			OverlayContact<?> currentHop, int hops) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.lookupForwarded(contact, key, currentHop, hops);
			}
		}
	}

	@Override
	public void dhtLookupFailed(OverlayContact<?> contact, DHTKey<?> key) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.lookupFailed(contact, key);
			}
		}
	}

	@Override
	public void dhtLookupFinished(OverlayContact<?> contact, DHTKey<?> key,
			List<OverlayContact<?>> responsibleContacts, int hops) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.lookupFinished(contact, key, responsibleContacts, hops);
			}
		}
	}

	@Override
	public void dhtLookupFinished(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object, int hops) {
		if (isMonitoring) {
			for (DHTOverlayAnalyzer analyzer : this.dhtOverlayAnalyzers) {
				analyzer.lookupFinished(contact, key, object, hops);
			}
		}
	}

	@Override
	public void dhtMirrorAssigned(OverlayContact<?> host, Transmitable document) {
		if (isMonitoring) {
			for (IOldFilesharingAnalyzer a : this.iOldFilesharingAnalyzers) {
				a.mirrorAssigned(host, document);
			}
		}

	}

	@Override
	public void dhtOwnDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean success) {
		if (isMonitoring) {
			for (IOldFilesharingAnalyzer a : this.iOldFilesharingAnalyzers) {
				a.ownDocumentServed(server, document, success);
			}
		}

	}

	@Override
	public void dhtMirroredDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean source) {
		if (isMonitoring) {
			for (IOldFilesharingAnalyzer a : this.iOldFilesharingAnalyzers) {
				a.mirroredDocumentServed(server, document, source);
			}
		}

	}

	@Override
	public void dhtMirrorDeleted(OverlayContact<?> server, Transmitable document) {
		if (isMonitoring) {
			for (IOldFilesharingAnalyzer a : this.iOldFilesharingAnalyzers) {
				a.mirrorDeleted(server, document);
			}
		}
	}

	@Override
	public void unstructuredConnectionStarted(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.connectionStarted(invoker, receiver, connectionUID);
			}
		}
	}

	@Override
	public void unstructuredConnectionSucceeded(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.connectionSucceeded(invoker, receiver, connectionUID);
			}
		}
	}

	@Override
	public void unstructuredConnectionDenied(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.connectionDenied(invoker, receiver, connectionUID);
			}
		}
	}

	@Override
	public void unstructuredConnectionTimeout(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.connectionTimeout(invoker, receiver, connectionUID);
			}
		}
	}

	@Override
	public void unstructuredConnectionBreakCancel(
			OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.connectionBreakCancel(notifiedNode, opponent);
			}
		}
	}

	@Override
	public void unstructuredConnectionBreakTimeout(
			OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.connectionBreakTimeout(notifiedNode, opponent);
			}
		}
	}

	@Override
	public void unstructuredPingTimeouted(OverlayContact<?> invoker,
			OverlayContact<?> receiver) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.pingTimeouted(invoker, receiver);
			}
		}
	}

	@Override
	public void unstructuredQueryStarted(OverlayContact<?> initiator,
			Query query) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.queryStarted(initiator, query);
			}
		}
	}

	@Override
	public void unstructuredQuerySucceeded(OverlayContact<?> initiator,
			Query query, int hits, double averageHops) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.querySucceeded(initiator, query, hits, averageHops);
			}
		}
	}

	@Override
	public void unstructuredQueryFailed(OverlayContact<?> initiator,
			Query query, int hits, double averageHops) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.queryFailed(initiator, query, hits, averageHops);
			}
		}
	}

	@Override
	public void unstructuredQueryMadeHop(int queryUID,
			OverlayContact<?> hopContact) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.queryMadeHop(queryUID, hopContact);
			}
		}
	}

	@Override
	public void unstructuredReBootstrapped(OverlayContact<?> c) {
		if (isMonitoring) {
			for (UnstructuredOverlayAnalyzer analyzer : this.unstructuredOverlayAnalyzers) {
				analyzer.reBootstrapped(c);
			}
		}
	}

	@Override
	public void aggregationQueryStarted(Host host, Object identifier, Object UID) {
		if (isMonitoring) {
			for (AggregationAnalyzer aggrAna : this.aggregationAnalyzers) {
				aggrAna.aggregationQueryStarted(host, identifier, UID);
			}
		}
	}

	@Override
	public void aggregationQuerySucceeded(Host host, Object identifier,
			Object UID, AggregationResult result) {
		if (isMonitoring) {
			for (AggregationAnalyzer aggrAna : this.aggregationAnalyzers) {
				aggrAna.aggregationQuerySucceeded(host, identifier, UID, result);
			}
		}

	}

	@Override
	public void aggregationQueryFailed(Host host, Object identifier, Object UID) {
		if (isMonitoring) {
			for (AggregationAnalyzer aggrAna : this.aggregationAnalyzers) {
				aggrAna.aggregationQueryFailed(host, identifier, UID);
			}
		}
	}

}
