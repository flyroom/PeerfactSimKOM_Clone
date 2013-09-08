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

/**
 * 
 */
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import java.util.Set;

import org.peerfact.impl.common.Operations;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.ConnectionManager.ConnectionManagerListener;
import org.peerfact.impl.overlay.unstructured.heterogeneous.common.operations.UpdateDocumentsOperation;


/**
 * Component that manages the one-hop replication of documents to all neighbors
 * and stores replicated content from neighbors locally.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class OneHopReplicator implements
		ConnectionManagerListener<GiaOverlayContact, GiaConnectionMetadata> {

	private GiaNode component;

	private GiaConnectionManager mgr;

	public OneHopReplicator(GiaNode component, GiaConnectionManager mgr) {
		this.component = component;
		this.mgr = mgr;
	}

	@Override
	public void connectionEnded(GiaOverlayContact c,
			GiaConnectionMetadata metadata) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lostConnectivity() {
		// TODO Auto-generated method stub

	}

	public void documentsChanged() {
		Set<GiaOverlayContact> contacts = mgr.getConnectedContacts();

		for (GiaOverlayContact c : contacts) {
			new UpdateDocumentsOperation<GiaOverlayContact>(component, mgr, c,
					Operations.getEmptyCallback()).scheduleImmediately();
		}
	}

	@Override
	public void newConnectionEstablished(GiaOverlayContact c,
			GiaConnectionMetadata metadata) {
		new UpdateDocumentsOperation<GiaOverlayContact>(component, mgr, c,
				Operations.getEmptyCallback())
				.scheduleWithDelay(component.getConfig().getReplicationDelay());
	}

}
