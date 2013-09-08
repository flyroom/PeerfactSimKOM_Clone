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

package org.peerfact.api.common;

import java.util.List;

import org.peerfact.api.analyzer.AggregationAnalyzer;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;
import org.peerfact.impl.transport.AbstractTransMessage;


/**
 * Monitor defines a central instance which is called by components whenever an
 * action occurs that is important to trace. In particular, upon calling a
 * specific monitor method, the monitor delegates notifications to all installed
 * analyzers.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @author Philip Wette
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 3.0, 12/03/2007
 * 
 */
public interface Monitor extends Configurable, SimulationEventHandler {

	/**
	 * 
	 * Provides additional information why an action occurs at a specific
	 * component
	 * 
	 */
	public enum Reason {
		/**
		 * Sending of a message
		 */
		SEND,
		/**
		 * Receiving of a message
		 */
		RECEIVE,
		/**
		 * Dropping of a message
		 */
		DROP,
		/**
		 * Host does not have physical network connectivity
		 */
		OFFLINE,
		/**
		 * Host does have physical network connectivity
		 */
		ONLINE

	}

	/**
	 * Sets the point in time at which the simulation framework will activate
	 * the monitoring of actions
	 * 
	 * @param time
	 *            time at which the monitoring starts
	 */
	public void setStart(long time);

	/**
	 * Sets the point in time at which the simulation framework will deactivate
	 * the monitoring of actions
	 * 
	 * @param time
	 *            time at which the monitoring ends
	 */
	public void setStop(long time);

	/**
	 * Registers an analyzer to the monitor which will be notified about
	 * specific actions
	 * 
	 * @param analyzer
	 *            the given analyzer
	 */
	public void setAnalyzer(Analyzer analyzer);

	/**
	 * This method is called whenever an operation has been triggered.
	 * 
	 * @param op
	 *            the Operation that has been triggered.
	 */
	public void operationInitiated(Operation<?> op);

	/**
	 * Invoking this method denotes that an operation is finished
	 * 
	 * @param op
	 *            the finished operation
	 */
	public void operationFinished(Operation<?> op);

	/**
	 * Informs the installed churn analyzers about the next session time
	 * calculated by the applied churn model
	 * 
	 * @param time
	 *            next session time in minutes (time = calculatedTime *
	 *            Simulator.MINUTE_UNIT);
	 */
	public void nextSessionTime(long time);

	/**
	 * Informs the installed churn analyzers about the next inter-session time
	 * calculated by the applied churn model
	 * 
	 * @param time
	 *            next inter-session time in minutes (time = calculatedTime *
	 *            Simulator.MINUTE_UNIT);
	 */
	public void nextInterSessionTime(long time);

	/**
	 * Invoking this method denotes that the physical network connectivity of
	 * the given host has been changed cause by churn.
	 * 
	 * @param host
	 *            the churn affected host
	 * @param reason
	 *            the given reason
	 */
	public void churnEvent(Host host, Reason reason);

	/**
	 * Invoking this method denotes that an action related to a network message
	 * is occurred on the network layer with the given NetID. The reason why
	 * this action is occurred is specified by the given reason such as send,
	 * receive or drop.
	 * 
	 * @param msg
	 *            the related network message
	 * @param id
	 *            the NetId of the network layer of the occurring event
	 * @param reason
	 *            the reason why this event happened
	 */
	public void netMsgEvent(NetMessage msg, NetID id, Reason reason);

	/**
	 * Invoking this method denotes that the given message is sent at the
	 * transport layer (from the application towards the network layer).
	 * 
	 * @param msg
	 *            the AbstractTransMessage which is sent out.
	 */
	public void transMsgSent(AbstractTransMessage msg);

	/**
	 * Invoking this method denotes that the given message is received at the
	 * transport layer (from the network layer towards the application layer).
	 * 
	 * @param msg
	 *            the received AbstractTransMessage.
	 */
	public void transMsgReceived(AbstractTransMessage msg);

	/**
	 * Informs the installed KBROverlayAnalyzers about the forward of a routed
	 * message
	 * 
	 * @param sender
	 * @param receiver
	 * @param olMsg
	 *            the forwarded overlay Message
	 * @param hops
	 *            the hop count at message send
	 */
	public void kbrOverlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message olMsg, int hops);

	/**
	 * Informs the installed KBROverlayAnalyzers about the delivery of a routed
	 * message
	 * 
	 * @param contact
	 *            the contact of the host the message is delivered at
	 * @param olMsg
	 *            the delivered overlay message
	 * @param hops
	 *            the hop count at message delivery
	 */
	public void kbrOverlayMessageDelivered(OverlayContact<?> contact,
			Message olMsg,
			int hops);

	/**
	 * Informs the installed KBROverlayAnalyzers about the start of a new query.
	 * A Query is defined as the procedure of routing a message through an
	 * overlay towards a key. The start of a query is then determined by the
	 * call of the route method of a KBR overlay node.
	 * 
	 * @param contact
	 *            the contact of the host that initiates the routing
	 * @param appMsg
	 *            the application message that is routed
	 */
	public void kbrQueryStarted(OverlayContact<?> contact, Message appMsg);

	/**
	 * Informs the installed KBROverlayAnalyzers about the fail of a query. A
	 * Query is defined as the procedure of routing a message through an overlay
	 * towards a key. The fail of a query happens if a host that is part of the
	 * routing process can not determine a next hop.
	 * 
	 * @param failedHop
	 *            the contact of the host that could not determine a next hop
	 * @param appMsg
	 *            the application message of the query that failed
	 */
	public void kbrQueryFailed(OverlayContact<?> failedHop, Message appMsg);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the start of a store of
	 * an object.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the store
	 * @param key
	 *            the key to store
	 * @param object
	 *            the object to store
	 */
	public void dhtStoreInitiated(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the fail of a store of an
	 * object.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the store
	 * @param key
	 *            the key to store
	 * @param object
	 *            the object to store
	 * @param failedHop
	 *            the contact of the peer that let the store failed
	 */
	public void dhtStoreFailed(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the finish of a store of
	 * an object.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the store
	 * @param key
	 *            the key to store
	 * @param object
	 *            the object to store
	 * @param responsibleContacts
	 *            the contacts of the peers who are responsible for the object
	 * @param hops
	 *            the number of hops needed for the store
	 */
	public void dhtStoreFinished(OverlayContact<?> contact, DHTKey<?> key,
			DHTObject object, List<OverlayContact<?>> responsibleContacts);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the start of a lookup for
	 * a specific key.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to find
	 */
	public void dhtLookupInitiated(OverlayContact<?> contact, DHTKey<?> key);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the fail of a lookup.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param currentHop
	 *            the contact of the peer that let the lookup failed
	 * @param hops
	 *            the number of hops until now
	 */
	public void dhtLookupForwarded(OverlayContact<?> contact, DHTKey<?> key,
			OverlayContact<?> currentHop, int hops);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the fail of a lookup.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param failedHop
	 *            the contact of the peer that let the lookup failed
	 */
	public void dhtLookupFailed(OverlayContact<?> contact, DHTKey<?> key);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the finish of a lookup
	 * with the responsible contact.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param responsibleContact
	 *            the contact of the peer who is responsible for the key
	 * @param hops
	 *            the number of hops needed for the lookup
	 */
	public void dhtLookupFinished(OverlayContact<?> contact,
			DHTKey<?> key, List<OverlayContact<?>> responsibleContact, int hops);

	/**
	 * Informs the installed DHTOverlayAnalyzers about the finish of a lookup
	 * with the result.
	 * 
	 * @param contact
	 *            the contact of the peer who has initiated the lookup
	 * @param key
	 *            the key to lookup
	 * @param hops
	 *            the number of hops needed for the lookup
	 */
	public void dhtLookupFinished(OverlayContact<?> contact,
			DHTKey<?> key, DHTObject object, int hops);

	@Deprecated
	public void dhtMirrorAssigned(OverlayContact<?> host, Transmitable document);

	@Deprecated
	public void dhtOwnDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean success);

	@Deprecated
	public void dhtMirroredDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean source);

	@Deprecated
	public void dhtMirrorDeleted(OverlayContact<?> server, Transmitable document);

	/**
	 * The node invoker started a connection attempt to the node receiver. This
	 * event should be followed by connectionSucceeded(...) or
	 * connectionFailed(...). connectionUID can be used to identify the whole
	 * connection operation and is equal to the UIDs of the reply.
	 * 
	 * @param invoker
	 *            the contact of the new connection
	 * @param receiver
	 *            the receiving contact
	 * @param connectionUID
	 *            the unique identifier of the connection
	 */
	public void unstructuredConnectionStarted(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID);

	/**
	 * The node receiver has accepted the connection from the node invoker.
	 * 
	 * @param invoker
	 *            the contact of the new connection
	 * @param receiver
	 *            the receiving contact
	 * @param connectionUID
	 *            the unique identifier of the connection
	 */
	public void unstructuredConnectionSucceeded(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID);

	/**
	 * The node receiver has denied the connection from the node invoker. The
	 * cause for the denial is given in cause.
	 * 
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 * @param cause
	 */
	public void unstructuredConnectionDenied(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID);

	/**
	 * The node receiver has denied the connection from the node invoker. The
	 * cause for the denial is given in cause.
	 * 
	 * @see connectionStarted
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 * @param cause
	 */
	public void unstructuredConnectionTimeout(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID);

	/**
	 * Called whenever a connection has broken and a peer was notified about it.
	 * 
	 * @param notifiedNode
	 * @param opponent
	 * @param cause
	 */
	public void unstructuredConnectionBreakCancel(
			OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent);

	/**
	 * Called whenever a connection has broken and a peer was notified about it.
	 * 
	 * @param notifiedNode
	 * @param opponent
	 * @param cause
	 */
	public void unstructuredConnectionBreakTimeout(
			OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent);

	/**
	 * The ping attempt for receiver has timed out. invoker has waited too long
	 * for a reply.
	 * 
	 * @param invoker
	 * @param receiver
	 */
	public void unstructuredPingTimeouted(OverlayContact<?> invoker,
			OverlayContact<?> receiver);

	/**
	 * A query was started at initiator.
	 * 
	 * @param initiator
	 * @param query
	 */
	public void unstructuredQueryStarted(OverlayContact<?> initiator,
			Query query);

	/**
	 * A query has received correct and enough replies (according to the
	 * configuration). hits returns the amount of hits that were received.
	 * 
	 * @param initiator
	 * @param query
	 * @param hits
	 * @param averageHops
	 */
	public void unstructuredQuerySucceeded(OverlayContact<?> initiator,
			Query query, int hits, double averageHops);

	/**
	 * A query has not received enough replies or incorrect ones. hits returns
	 * the amount of hits that were received.
	 * 
	 * @param initiator
	 * @param query
	 * @param hits
	 * @param averageHops
	 */
	public void unstructuredQueryFailed(OverlayContact<?> initiator,
			Query query, int hits, double averageHops);

	/**
	 * A query was received by a node. If a query is received by a node multiple
	 * times, this method is called multiple times, too.
	 * 
	 * @param queryUID
	 * @param hopContact
	 */
	public void unstructuredQueryMadeHop(int queryUID,
			OverlayContact<?> hopContact);

	/**
	 * A peer is bootstrapping again, because it has lost any connectivity to
	 * other peers. Does NOT include bootstraps that were explicitly invoked by
	 * join() or by a connectivityChanged event.
	 * 
	 * @param c
	 */
	public void unstructuredReBootstrapped(OverlayContact<?> c);

	/**
	 * Informs the installed {@link AggregationAnalyzer} about the start of an
	 * aggregation query.
	 * 
	 * @param host
	 *            The host, that starts the query
	 * @param identifier
	 *            the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param UID
	 *            An unique identifier for this query.
	 */
	public void aggregationQueryStarted(Host host, Object identifier, Object UID);

	/**
	 * Informs the installed {@link AggregationAnalyzer} about the success of an
	 * aggregation query.
	 * 
	 * @param host
	 *            The host, that starts the query
	 * @param identifier
	 *            the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param UID
	 *            The unique identifier for this query
	 * @param result
	 *            The result of the aggregation query
	 */
	public void aggregationQuerySucceeded(Host host, Object identifier,
			Object UID, AggregationResult result);

	/**
	 * Informs the installed {@link AggregationAnalyzer} about the fail of an
	 * aggregation query.
	 * 
	 * @param host
	 *            The host, that starts the query
	 * @param identifier
	 *            the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param UID
	 *            The unique identifier for this query
	 */
	public void aggregationQueryFailed(Host host, Object identifier, Object UID);

}
