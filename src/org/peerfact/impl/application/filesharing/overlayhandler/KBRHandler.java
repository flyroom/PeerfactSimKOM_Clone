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

package org.peerfact.impl.application.filesharing.overlayhandler;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.application.filesharing.overlayhandler.kbr.HavingResourceMessage;
import org.peerfact.impl.application.filesharing.overlayhandler.kbr.LookupResourceMessage;
import org.peerfact.impl.application.filesharing.overlayhandler.kbr.PublishResourceMessage;
import org.peerfact.impl.application.filesharing.overlayhandler.kbr.PublishSucceededMessage;
import org.peerfact.impl.common.Operations;


/**
 * Filesharing2 overlay handler wrapper for all KBR-enabled overlays. This
 * component was not used in my studies, so is not well debugged although it
 * seems to work pretty good.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KBRHandler extends AbstractOverlayHandler implements
		KBRListener<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> {

	private final KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> overlay;

	private final Set<OverlayKey<?>> keysStored = new LinkedHashSet<OverlayKey<?>>();

	public KBRHandler(
			KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> overlay) {
		this.overlay = overlay;
		overlay.setKBRListener(this);
	}

	@Override
	public void join() {

		if (overlay instanceof DHTNode) {
			((DHTNode<?, ?, ?>) overlay).join(Operations.EMPTY_CALLBACK);
			return;
		}
		throw new ConfigurationException(
				"Join: KBR node type was not recognized automatically. "
						+ "Implement it or join manually.");
	}

	@Override
	public void leave() {
		if (overlay instanceof DHTNode) {
			((DHTNode<?, ?, ?>) overlay).leave(Operations.EMPTY_CALLBACK);
			return;
		}
		throw new ConfigurationException(
				"Leave: KBR node type was not recognized automatically. "
						+ "Implement it or leave manually.");
	}

	@Override
	public void downloadResource(int key) {
		OverlayKey<?> key2lookup = overlay.getNewOverlayKey(key);
		LookupResourceMessage msg2forward = new LookupResourceMessage(
				key2lookup, overlay.getLocalOverlayContact());
		AbstractOverlayHandler
				.downloadStarted(
						overlay
								.getLocalOverlayContact(),
						msg2forward.getQueryUID());
		if (overlay.isRootOf(key2lookup)) {
			deliver(key2lookup, msg2forward);
		} else {
			overlay.route(key2lookup, msg2forward, null);
		}
	}

	@Override
	public void publishResource(FileSharingDocument resource) {
		OverlayKey<?> key2publish = overlay.getNewOverlayKey(resource.getId());
		PublishResourceMessage msg2forward = new PublishResourceMessage(
				key2publish, overlay.getLocalOverlayContact());
		AbstractOverlayHandler
				.publishStarted(overlay
						.getLocalOverlayContact(),
						resource.getId(),
						msg2forward.getQueryUID());
		if (overlay.isRootOf(key2publish)) {
			deliver(key2publish, msg2forward);
		} else {
			overlay.route(key2publish, msg2forward, null);
		}
	}

	@Override
	public void deliver(OverlayKey<?> key, Message msg) {

		Message olmsg = msg;

		if (olmsg instanceof LookupResourceMessage) {
			LookupResourceMessage lookupMsg = (LookupResourceMessage) olmsg;
			if (keysStored.contains(lookupMsg.getKeyLookedUp())) {
				sendQuerySuccess(
						lookupMsg.getKeyLookedUp(),
						(OverlayContact<OverlayID<?>>) lookupMsg.getInitiator(),
						lookupMsg.getQueryUID());
			} else {
				log.debug("Key looked up is not stored.");
			}
		} else if (olmsg instanceof PublishResourceMessage) {
			PublishResourceMessage pubMsg = (PublishResourceMessage) olmsg;

			OverlayKey<?> key2publish = pubMsg.getKey2publish();

			keysStored.add(key2publish);

			List<? extends OverlayContact<OverlayID<?>>> replNodes = overlay
					.replicaSet(
							pubMsg.getKey2publish(),
							KBRHandler.getReplicaCount());

			if (!pubMsg.hasReplicationFlag()) {
				// Send replicated key/values to all neighbors.
				for (OverlayContact<OverlayID<?>> contact : replNodes) {
					PublishResourceMessage repMsg = new PublishResourceMessage(
							key2publish, overlay.getLocalOverlayContact());
					repMsg.setReplicationFlag(true);
					overlay.route(null, repMsg,
							contact);
				}

				sendStoreSuccess(key2publish,
						(OverlayContact<OverlayID<?>>) pubMsg.getInitiator(),
						pubMsg.getQueryUID());
			}

		} else if (olmsg instanceof HavingResourceMessage) {
			HavingResourceMessage hvMsg = (HavingResourceMessage) olmsg;
			if (hvMsg.getRequestor().equals(overlay.getLocalOverlayContact())) {
				AbstractOverlayHandler.downloadSucceeded(
						overlay
								.getLocalOverlayContact(),
						hvMsg.getQueryUID(), 0);
				// TODO: Ranks aus OverlayKeys extrapolieren
			}

		} else if (olmsg instanceof PublishSucceededMessage) {
			PublishSucceededMessage succMsg = (PublishSucceededMessage) olmsg;
			Set<OverlayContact<OverlayID<?>>> responsibles = new HashSet<OverlayContact<OverlayID<?>>>();
			responsibles.add(succMsg.getPublishResponsibleNode());
			if (succMsg.getRequestor().equals(overlay.getLocalOverlayContact())) {
				AbstractOverlayHandler.publishSucceeded(
						overlay
								.getLocalOverlayContact(), responsibles
						, 0,
						succMsg.getQueryUID(), 0);
				// TODO: Ranks aus OverlayKeys extrapolieren
			}
		}
	}

	protected void sendStoreSuccess(OverlayKey<?> keyStored,
			OverlayContact<OverlayID<?>> contact, long queryUID) {
		Message msg2forward = new PublishSucceededMessage(queryUID, keyStored,
				contact,
				overlay.getLocalOverlayContact());
		if (overlay.getLocalOverlayContact().equals(contact)) {
			deliver(keyStored, msg2forward);
		} else {
			overlay.route(null, msg2forward, contact);
		}
	}

	protected void sendQuerySuccess(OverlayKey<?> keyLookedUp,
			OverlayContact<OverlayID<?>> contact, long queryUID) {
		Message msg2forward = new HavingResourceMessage(queryUID, keyLookedUp,
				contact, overlay.getLocalOverlayContact());
		if (overlay.getLocalOverlayContact().equals(contact)) {
			deliver(keyLookedUp, msg2forward);
		} else {
			overlay.route(null, msg2forward, contact);
		}
	}

	@Override
	public void forward(
			KBRForwardInformation<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> information) {
		// Do nothing - This was improtant due to an old overlay hop

	}

	@Override
	public void update(OverlayContact<OverlayID<?>> contact, boolean joined) {
		// TODO Auto-generated method stub

	}

	public static int getReplicaCount() {
		return 20; // TODO nur zum Test!
	}

}
