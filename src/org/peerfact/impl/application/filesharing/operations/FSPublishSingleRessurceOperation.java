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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.application.filesharing.FileSharingApplication;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.application.filesharing.overlayhandler.IOverlayHandler;
import org.peerfact.impl.common.AbstractOperation;

/**
 * A structured overlay publishes a single document.
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class FSPublishSingleRessurceOperation extends
		AbstractOperation<FileSharingApplication, Object> implements
		IFilesharingOperation {

	private FileSharingDocument resource;

	private IOverlayHandler olHandler;

	public FSPublishSingleRessurceOperation(FileSharingApplication component,
			OperationCallback<Object> callback,
			IOverlayHandler olHandler,
			FileSharingDocument resource) {
		super(component, callback);
		this.resource = resource;
		this.olHandler = olHandler;
	}

	@Override
	protected void execute() {
		if (getComponent().getHost().getNetLayer().isOnline()) {
			olHandler.publishResource(resource);
		}
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
