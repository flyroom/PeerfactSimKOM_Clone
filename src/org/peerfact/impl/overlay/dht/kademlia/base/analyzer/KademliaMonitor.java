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

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer;

import java.util.Collection;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.common.DefaultMonitor;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.IKademliaAnalyzer.DataLookupAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.IKademliaAnalyzer.KClosestNodesLookupAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.DataLookupOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KClosestNodesLookupOperation;


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
 * A Monitor that has extended support for Kademlia Analyzers.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaMonitor extends DefaultMonitor {

	/**
	 * A reference to the DataLookupAnalyzer used in Kademlia.
	 */
	private DataLookupAnalyzer dataLookupAnalyzer;

	/**
	 * A reference to the KClosestNodesLookupAnalyzer used in Kademlia.
	 */
	private KClosestNodesLookupAnalyzer kClosestNodesAnalyzer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setAnalyzer(final Analyzer analyzer) {
		// add to list of all analyzers in super class (for output etc.)
		super.setAnalyzer(analyzer);
		if (analyzer instanceof DataLookupAnalyzer) {
			// we currently support only one Analyzer - to be extended if
			// needed.
			dataLookupAnalyzer = (DataLookupAnalyzer) analyzer;
		} else if (analyzer instanceof KClosestNodesLookupSuccessAnalyzer) {
			kClosestNodesAnalyzer = (KClosestNodesLookupSuccessAnalyzer) analyzer;
		}
	}

	/**
	 * The given data lookup operation has been initiated.
	 * 
	 * @param op
	 *            the DataLookupOperation that has begun to execute.
	 */
	public final void dataLookupInitiated(final DataLookupOperation<?> op) {
		if (dataLookupAnalyzer != null) {
			dataLookupAnalyzer.dataLookupInitiated(op);
		}
	}

	/**
	 * The given operation sent the given message.
	 * 
	 * @param msg
	 *            the KademliaMsg that has been sent.
	 * @param op
	 *            the DataLookupOperation that sent the message.
	 */
	public final void dataLookupMsgSent(final KademliaMsg<?> msg,
			final DataLookupOperation<?> op) {
		if (dataLookupAnalyzer != null) {
			// http: // en.wikipedia.org/wiki/Zipf's_law
			dataLookupAnalyzer.dataLookupMsgSent(msg, op);
		}
	}

	/**
	 * A data lookup has completed.
	 * 
	 * @param key
	 *            the KademliaOverlayKey of the data item that has been looked
	 *            up.
	 * @param result
	 *            the DHTObject that has been found (or <code>null</code> if
	 *            nothing has been found).
	 * @param sender
	 *            the KademliaOverlayID of the peer that sent the data item (or
	 *            <code>null</code> if no peer sent it).
	 * @param closestNodes
	 *            the Set of the K closest nodes that the lookup has seen (only
	 *            relevant if lookup did not return a data item).
	 * @param op
	 *            the DataLookupOperation that has completed.
	 */
	public void dataLookupCompleted(final KademliaOverlayKey key,
			final DHTObject result, final KademliaOverlayID sender,
			final Collection<? extends KademliaOverlayContact<?>> closestNodes,
			final DataLookupOperation<?> op) {
		if (dataLookupAnalyzer != null) {
			dataLookupAnalyzer.dataLookupCompleted(key, result, sender,
					closestNodes, op);
		}
	}

	/**
	 * A k closest nodes lookup has completed.
	 * 
	 * @param key
	 *            the KademliaOverlayKey of the data item that has been looked
	 *            up.
	 * @param result
	 *            a Collection containing the k closest nodes that have been
	 *            found.
	 * @param op
	 *            the KClosestNodesLookupOperation that has completed.
	 */
	public final void kClosestNodesLookupCompleted(
			final KademliaOverlayKey key,
			final Collection<? extends KademliaOverlayContact<?>> result,
			final KClosestNodesLookupOperation<?> op) {
		if (kClosestNodesAnalyzer != null) {
			kClosestNodesAnalyzer.kClosestNodesLookupCompleted(key, result, op);
		}
	}

	/**
	 * This method should be called whenever an operation has been triggered.
	 * (It is assumed that an operation is not triggered twice and that no two
	 * operations have the same operationID.)
	 * 
	 * @param op
	 *            the Operation that has been triggered.
	 */
	// public final void operationInitiated(final AbstractKademliaOperation<?>
	// op) {
	// if (operationAnalyzer != null) {
	// operationAnalyzer.operationInitiated(op);
	// }
	// if (msgTrafficAnalyzer != null) {
	// msgTrafficAnalyzer.operationInitiated(op);
	// }
	// } // TODO uncomment
	/**
	 * This method should be called whenever an operation has completed. (It is
	 * assumed that an operation is not triggered twice and that no two
	 * operations have the same operationID.)
	 * 
	 * @param op
	 *            the Operation that has completed.
	 */
	// public final void operationFinished(final AbstractKademliaOperation<?>
	// op) {
	// if (operationAnalyzer != null) {
	// operationAnalyzer.operationFinished(op);
	// }
	// if (msgTrafficAnalyzer != null) {
	// msgTrafficAnalyzer.operationFinished(op);
	// }
	// } // TODO uncomment
	/**
	 * The given message has been sent by the given operation.
	 * 
	 * @param msg
	 *            the KademliaMsg that has been sent.
	 * @param op
	 *            the AbstractKademliaOperation that sent the message.
	 */
	// public final void messageSent(final KademliaMsg<?> msg,
	// final AbstractKademliaOperation<?> op) {
	// if (msgTrafficAnalyzer != null) {
	// msgTrafficAnalyzer.messageSent(msg, op);
	// }
	// } // TODO uncomment
}
