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

package org.peerfact.impl.application.filesharing.operations;

import java.util.Set;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.application.filesharing.FileSharingApplication;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.application.filesharing.overlayhandler.IOverlayHandler;
import org.peerfact.impl.common.AbstractOperation;


/**
 * A node makes documents available with defined keys.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class FSPublishMultipleResourcesOperation extends
		AbstractOperation<FileSharingApplication, Object> implements
		IFilesharingOperation {

	private final IOverlayHandler ol;

	private final Set<FileSharingDocument> documents;

	static final int distributionDelay = 100000; // 10ms

	public FSPublishMultipleResourcesOperation(IOverlayHandler ol,
			Set<FileSharingDocument> documents,
			FileSharingApplication component,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.ol = ol;
		this.documents = documents;
	}

	@Override
	protected void execute() {
		if (getComponent().getHost().getNetLayer().isOnline()) {
			this.publishResources(documents);
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

	public Set<FileSharingDocument> getDocuments() {
		return documents;
	}

	// Allows to "smoothly" announce new documents to the network.

	public void publishResources(Set<FileSharingDocument> resources) {
		int delayInc = 0;
		for (FileSharingDocument resource : resources) {
			FSPublishSingleRessurceOperation publishOp = new FSPublishSingleRessurceOperation(
					getComponent(),
					new OperationCallback<Object>() {

						@Override
						public void calledOperationFailed(Operation<Object> op) {
							// TODO ANALYZER CALL

						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							// TODO ANALYZER CALL

						}

					}, this.ol, resource);
			publishOp.scheduleWithDelay(delayInc);
			delayInc += distributionDelay;
		}
	}

}
