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

import java.util.LinkedHashSet;
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
import org.peerfact.api.overlay.unstructured.HeterogeneousOverlayNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaOverlayKey;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IResource;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.RankResource;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.DefaultTransInfo;

/**
 * Filesharing2 overlay handler for my Gnutella06 implementation.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GnutellaHandler extends AbstractOverlayHandler {

	private static final int HITS_WANTED = 1;

	private HeterogeneousOverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>> node;

	public GnutellaHandler(
			HeterogeneousOverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>> node) {
		this.node = node;

	}

	@Override
	public void join() {
		node.join(Operations.getEmptyCallback());
	}

	@Override
	public void leave() {
		node.leave(Operations.getEmptyCallback());
	}

	@Override
	public void downloadResource(int key) {
		final long queryUID = Simulator.getRandom().nextLong();
		AbstractOverlayHandler.downloadStarted(node.getOwnContact(), queryUID);
		final GnutellaOverlayKey<Integer> overlayKey = new GnutellaOverlayKey<Integer>(
				key);
		node.queryRank(
				key,
				HITS_WANTED,
				new OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>>() {

					@Override
					public void calledOperationFailed(
							Operation<List<QueryHit<GnutellaLikeOverlayContact>>> op) {
						AbstractOverlayHandler.downloadFailed(
								node.getOwnContact(), queryUID);
					}

					@Override
					public void calledOperationSucceeded(
							Operation<List<QueryHit<GnutellaLikeOverlayContact>>> op) {
						processLookupResult(op.getResult(), overlayKey,
								queryUID);
					}

				});
	}

	void processLookupResult(List<QueryHit<GnutellaLikeOverlayContact>> list,
			GnutellaOverlayKey<Integer> key, final long queryUID) {
		ContentDistribution<OverlayKey<?>> strategy = this.getFSApplication()
				.getContentDistribution();

		// create transinfo for download
		LinkedList<TransInfo> peers = new LinkedList<TransInfo>();
		for (QueryHit<GnutellaLikeOverlayContact> qhit : list) {
			peers.add(DefaultTransInfo.getTransInfo(qhit.getContact()
					.getTransInfo()
					.getNetId(), strategy.getPort()));
		}

		// download
		strategy.downloadDocument(key, peers,
				new OperationCallback<Document<OverlayKey<?>>>() {

					@Override
					public void calledOperationFailed(
							Operation<Document<OverlayKey<?>>> op) {
						AbstractOverlayHandler.downloadFailed(
								node.getOwnContact(),
								queryUID);
					}

					@Override
					public void calledOperationSucceeded(
							Operation<Document<OverlayKey<?>>> op) {
						AbstractOverlayHandler.downloadSucceeded(
								node.getOwnContact(),
								queryUID, op.getResult().getSize());
					}

				});
	}

	@Override
	public void publishResource(final FileSharingDocument resource) {
		final long queryUID = Simulator.getRandom().nextLong();
		AbstractOverlayHandler.publishStarted(node.getOwnContact(),
				resource.getId(), queryUID);
		RankResource rankResource = new RankResource(resource.getId());
		resource.setKey(rankResource.getKey());
		Set<IResource> res = new LinkedHashSet<IResource>();
		res.add(rankResource);

		node.publishSet(
				res,
				new OperationCallback<Set<OverlayContact<OverlayID<?>>>>() {
					@Override
					public void calledOperationFailed(
							Operation<Set<OverlayContact<OverlayID<?>>>> op) {
						AbstractOverlayHandler.publishFailed(
								node.getOwnContact(),
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

	void processPublishResult(
			final Set<OverlayContact<OverlayID<?>>> set,
			final FileSharingDocument document, final long queryUID) {
		ContentDistribution<OverlayKey<?>> strategy = this
				.getFSApplication().getContentDistribution();

		// create transinfo for upload
		List<TransInfo> peers = new LinkedList<TransInfo>();
		for (OverlayContact<OverlayID<?>> con : set) {
			peers.add(DefaultTransInfo.getTransInfo(con.getTransInfo()
					.getNetId(), strategy.getPort()));
		}

		// upload
		getFSApplication().getContentDistribution().uploadDocument(document,
				peers,
				new OperationCallback<List<TransInfo>>() {
					@Override
					public void calledOperationFailed(
							Operation<List<TransInfo>> opList) {
						AbstractOverlayHandler.publishFailed(
								node.getOwnContact(),
								document.getId(), queryUID);

					}

					@Override
					public void calledOperationSucceeded(
							Operation<List<TransInfo>> opList) {
						AbstractOverlayHandler.publishSucceeded(
								node.getOwnContact(),
								set, document.getId(), queryUID,
								document.getSize());
					}

				});

	}

}
