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

package org.peerfact.impl.overlay.kbr;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupMessage;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardInformationImpl;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupMsg;
import org.peerfact.impl.overlay.kbr.messages.KBRLookupReplyMsg;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This is the message listener for KBR layers
 * 
 * @author Yue Sheng (improved by Julius Rueckert)
 *         <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * 
 * @version 05/06/2011
 */
public class KBRMsgHandler<T extends OverlayID<?>, S extends OverlayContact<T>, K extends OverlayKey<?>>
		implements TransMessageListener {

	private final static Logger log = SimLogger.getLogger(KBRMsgHandler.class);

	private final KBRNode<T, S, K> masterAsKBR;

	private final AbstractOverlayNode<T, S> masterAsOverlayNode;

	private final List<KBRListener<T, S, K>> listeners = new LinkedList<KBRListener<T, S, K>>();

	private KBRLookupProvider<T, S, K> lookupProvider;

	/**
	 * Info: If you want to create an instance of this class inside an overlay
	 * node which inherits from AbstractOverlayNode and realizes the interface
	 * KBR you have to pass for the first and second parameter "this".
	 * 
	 * @param kbr
	 * @param overlayNode
	 * @param listener
	 */
	public KBRMsgHandler(KBRNode<T, S, K> kbr,
			AbstractOverlayNode<T, S> overlayNode,
			KBRListener<T, S, K> listener) {
		this.masterAsKBR = kbr;
		this.masterAsOverlayNode = overlayNode;
		listeners.add(listener);

		/*
		 * Register the handler at the transport layer
		 */
		overlayNode.getHost().getTransLayer()
				.addTransMsgListener(this, overlayNode.getPort());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof KBRForwardMsg) {
			KBRForwardMsg<T, K> fm = (KBRForwardMsg<T, K>) msg;
			K key = fm.getKey();
			Message appMsg = fm.getPayload();
			OverlayContact<?> nextHop = null;
			int numberOfHops = fm.getHops();
			numberOfHops++;

			/*
			 * Inform the KBR node about the direct contact to another node
			 */
			masterAsKBR.hadContactTo(masterAsKBR.getOverlayContact(
					fm.getSender(), receivingEvent.getSenderTransInfo()));

			if (key == null || masterAsKBR.isRootOf(key)) {

				if (appMsg instanceof KBRLookupMessage) {
					/*
					 * In this case the message contains to a lookup operation
					 * for the node responsible for a key. This message is
					 * delivered to the <code>KBRLookupProvider</code> that
					 * handles the lookup procedure.
					 */

					if (lookupProvider == null) {
						lookupProvider = new KBRLookupProviderImpl<T, S, K>(
								masterAsKBR);
					}

					if (appMsg instanceof KBRLookupMsg<?, ?>) {
						lookupProvider
								.lookupRequestArrived((KBRLookupMsg<T, S>) appMsg);
					} else if (appMsg instanceof KBRLookupReplyMsg) {
						lookupProvider
								.lookupReplyArrived((KBRLookupReplyMsg<T, S>) appMsg);
					}

					/*
					 * Deliver the message to the application if the key is null
					 * (it is a direct message to that node) or the node is the
					 * root of the key
					 */
					for (KBRListener<T, S, K> listener : listeners) {
						listener.deliver(key, appMsg);
					}

					if (key != null) {
						log.debug("Delivered a query that was routed towards a key.");
					}
					/*
					 * Inform monitors about delivery
					 */
					Simulator.getMonitor().kbrOverlayMessageDelivered(
							masterAsKBR.getLocalOverlayContact(), msg,
							numberOfHops);
				}
				return;
			} else {
				List<S> posNextHops = masterAsKBR.local_lookup(key, 1);
				if (posNextHops.size() > 0) {
					nextHop = posNextHops.get(0);

					if (nextHop != null) {
						KBRForwardInformation<T, S, K> info = new KBRForwardInformationImpl<T, S, K>(
								key, appMsg, (S) nextHop);
						for (KBRListener<T, S, K> listener : listeners) {
							listener.forward(info);
						}
						key = info.getKey();
						appMsg = info.getMessage();
						nextHop = info.getNextHopAgent();

						if (nextHop != null) {
							fm = new KBRForwardMsg<T, K>(
									masterAsOverlayNode.getOverlayID(),
									(T) nextHop.getOverlayID(), key, appMsg,
									numberOfHops);
							masterAsOverlayNode.getTransLayer().send(fm,
									nextHop.getTransInfo(),
									masterAsOverlayNode.getPort(),
									TransProtocol.UDP);

							/*
							 * Inform monitors about forwarding
							 */
							Simulator.getMonitor().kbrOverlayMessageForwarded(
									masterAsKBR.getLocalOverlayContact(),
									nextHop,
									msg, numberOfHops);
						}
					}
				} else {
					/*
					 * No next hop could be determined. The message is dropped.
					 */
					Simulator.getMonitor().kbrQueryFailed(
							masterAsKBR.getLocalOverlayContact(), appMsg);
				}
			}
		}
	}

	/**
	 * Add a <code>KBRListener</code> to the list of listeners.
	 * 
	 * @param listener
	 */
	public void addKBRListener(KBRListener<T, S, K> listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a <code>KBRListener</code> to the list of listeners.
	 * 
	 * @param listener
	 */
	public void removeKBRListener(KBRListener<T, S, K> listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the lookup provider, to perform lookups of keys
	 */
	public KBRLookupProvider<T, S, K> getLookupProvider() {
		if (lookupProvider == null) {
			lookupProvider = new KBRLookupProviderImpl<T, S, K>(masterAsKBR);
		}

		return lookupProvider;
	}

}
