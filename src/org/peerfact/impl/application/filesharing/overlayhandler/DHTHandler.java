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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.api.overlay.cd.Document;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.application.filesharing.Torrent;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;

/**
 * Filesharing2 overlay handler for the Chord2 overlay implementation by Minh
 * Hoang Nguyen. For the generation of Chord overlay keys, the rank of the
 * resource is hashed, with a salt that is uniquely generated for each
 * simulation. This ensures hash consistency, but ensures the usage of different
 * keys for every simulation made.
 * 
 * @author Leo Nobach, adapted for chord2 by Julius Rueckert
 *         <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class DHTHandler extends AbstractOverlayHandler {

	DHTNode<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>> node;

	public DHTHandler(
			DHTNode<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>> node) {
		this.node = node;
	}

	@Override
	public void join() {
		node.join(Operations.EMPTY_CALLBACK);
	}

	@Override
	public void leave() {
		node.leave(Operations.EMPTY_CALLBACK);
	}

	@Override
	public void downloadResource(int key) {
		final DHTKey<?> valueKey = node.getNewOverlayKey(key);
		final long queryUID = Simulator.getRandom().nextLong();
		AbstractOverlayHandler.downloadStarted(node.getLocalOverlayContact(),
				queryUID);
		node.nodeLookup(valueKey,
				new OperationCallback<List<OverlayContact<OverlayID<?>>>>() {

					@Override
					public void calledOperationFailed(
							Operation<List<OverlayContact<OverlayID<?>>>> op) {
						AbstractOverlayHandler.downloadFailed(
								node.getLocalOverlayContact(), queryUID);
					}

					@Override
					public void calledOperationSucceeded(
							Operation<List<OverlayContact<OverlayID<?>>>> op) {
						processLookupResult(op.getResult(), valueKey,
								queryUID);
					}

				}, false);
	}

	void processLookupResult(List<OverlayContact<OverlayID<?>>> list,
			DHTKey<?> key, final long queryUID) {
		ContentDistribution<OverlayKey<?>> strategy = this.getFSApplication()
				.getContentDistribution();

		// create transinfo for download
		LinkedList<TransInfo> peers = new LinkedList<TransInfo>();
		for (OverlayContact<OverlayID<?>> contact : list) {
			peers.add(DefaultTransInfo.getTransInfo(contact.getTransInfo()
					.getNetId(), strategy.getPort()));
		}

		// download
		strategy.downloadDocument(key, peers,
				new OperationCallback<Document<OverlayKey<?>>>() {

					@Override
					public void calledOperationFailed(
							Operation<Document<OverlayKey<?>>> op) {
						AbstractOverlayHandler.downloadFailed(
								node.getLocalOverlayContact(),
								queryUID);
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Document<OverlayKey<?>>> op) {
						AbstractOverlayHandler.downloadSucceeded(
								node.getLocalOverlayContact(),
								queryUID, op.getResult().getSize());
					}

				});
	}

	@Override
	public void publishResource(final FileSharingDocument resource) {
		final long queryUID = Simulator.getRandom().nextLong();
		AbstractOverlayHandler.publishStarted(node.getLocalOverlayContact(),
				resource.getId(), queryUID);
		DHTKey<?> valueKey = node.getNewOverlayKey(resource.getId());
		resource.setKey(valueKey);
		List<OverlayContact<?>> ownerList = new ArrayList<OverlayContact<?>>();
		ownerList.add(node.getLocalOverlayContact());
		Torrent obj = new Torrent(valueKey, ownerList);

		node.store(valueKey, obj,
				new OperationCallback<Set<OverlayContact<OverlayID<?>>>>() {
					@Override
					public void calledOperationFailed(
							Operation<Set<OverlayContact<OverlayID<?>>>> op) {
						AbstractOverlayHandler.publishFailed(
								node.getLocalOverlayContact(),
								resource.getId(),
								queryUID);

					}

					@Override
					public void calledOperationSucceeded(
							Operation<Set<OverlayContact<OverlayID<?>>>> op) {
						processPublishResult(op.getResult(), resource, queryUID);
					}
				});
	}

	void processPublishResult(final Set<OverlayContact<OverlayID<?>>> set,
			final FileSharingDocument document, final long queryUID) {
		ContentDistribution<OverlayKey<?>> strategy = this
				.getFSApplication().getContentDistribution();

		// create transinfo for upload
		List<TransInfo> peers = new LinkedList<TransInfo>();
		if (set != null) {
			for (OverlayContact<OverlayID<?>> con : set) {
				peers.add(DefaultTransInfo.getTransInfo(con.getTransInfo()
						.getNetId(), strategy.getPort()));
			}
		}

		// upload
		getFSApplication().getContentDistribution().uploadDocument(document,
				peers,
				new OperationCallback<List<TransInfo>>() {
					@Override
					public void calledOperationFailed(
							Operation<List<TransInfo>> opList) {
						AbstractOverlayHandler.publishFailed(
								node.getLocalOverlayContact(),
								document.getId(), queryUID);

					}

					@Override
					public void calledOperationSucceeded(
							Operation<List<TransInfo>> opList) {
						AbstractOverlayHandler.publishSucceeded(
								node.getLocalOverlayContact(),
								set, document.getId(), queryUID,
								document.getSize());
					}

				});

	}

}
