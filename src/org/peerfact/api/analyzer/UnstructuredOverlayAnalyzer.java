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

package org.peerfact.api.analyzer;

import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.Query;

/**
 * UnstructuredOverlayAnalyzer receives notifications about events on
 * unstructured overlay layers. This way it is possible to collect data
 * independent of the used overlay. To use this kind of analyzer in a meaningful
 * way you have to use an application that uses the methods of the unstructured
 * overlays.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 */
public interface UnstructuredOverlayAnalyzer extends Analyzer {

	/**
	 * The node invoker started a connection attempt to the node receiver. This
	 * event should be followed by connectionSucceeded(...) or
	 * connectionFailed(...). connectionUID can be used to identify the whole
	 * connection operation and is equal to the UIDs of the reply.
	 * 
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 */
	public void connectionStarted(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID);

	/**
	 * The node receiver has accepted the connection from the node invoker.
	 * 
	 * @see connectionStarted
	 * @param invoker
	 * @param receiver
	 * @param connectionUID
	 */
	public void connectionSucceeded(OverlayContact<?> invoker,
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
	public void connectionDenied(OverlayContact<?> invoker,
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
	public void connectionTimeout(OverlayContact<?> invoker,
			OverlayContact<?> receiver, int connectionUID);

	/**
	 * Called whenever a connection has broken and a peer was notified about it.
	 * 
	 * @param notifiedNode
	 * @param opponent
	 * @param cause
	 */
	public void connectionBreakCancel(OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent);

	/**
	 * Called whenever a connection has broken and a peer was notified about it.
	 * 
	 * @param notifiedNode
	 * @param opponent
	 * @param cause
	 */
	public void connectionBreakTimeout(OverlayContact<?> notifiedNode,
			OverlayContact<?> opponent);

	/**
	 * The ping attempt for receiver has timed out. invoker has waited too long
	 * for a reply.
	 * 
	 * @param invoker
	 * @param receiver
	 */
	public void pingTimeouted(OverlayContact<?> invoker,
			OverlayContact<?> receiver);

	/**
	 * A query was started at initiator.
	 * 
	 * @param initiator
	 * @param query
	 */
	public void queryStarted(OverlayContact<?> initiator, Query query);

	/**
	 * A query has received correct and enough replies (according to the
	 * configuration). hits returns the amount of hits that were received.
	 * 
	 * @param initiator
	 * @param query
	 * @param hits
	 * @param averageHops
	 */
	public void querySucceeded(OverlayContact<?> initiator, Query query,
			int hits, double averageHops);

	/**
	 * A query has not received enough replies or incorrect ones. hits returns
	 * the amount of hits that were received.
	 * 
	 * @param initiator
	 * @param query
	 * @param hits
	 * @param averageHops
	 */
	public void queryFailed(OverlayContact<?> initiator, Query query,
			int hits, double averageHops);

	/**
	 * A query was received by a node. If a query is received by a node multiple
	 * times, this method is called multiple times, too.
	 * 
	 * @param queryUID
	 * @param hopContact
	 */
	public void queryMadeHop(int queryUID, OverlayContact<?> hopContact);

	/**
	 * A peer is bootstrapping again, because it has lost any connectivity to
	 * other peers. Does NOT include bootstraps that were explicitly invoked by
	 * join() or by a connectivityChanged event.
	 * 
	 * @param c
	 */
	public void reBootstrapped(OverlayContact<?> c);
}