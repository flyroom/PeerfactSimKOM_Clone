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
import java.util.Set;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.util.MultiSet;


/**
 * This class is for debugging purposes only. It fakes a perfect overlay with
 * global knowledge. Every lookup succeeds iff
 * <ul>
 * <li>the key was published
 * <li>the peer that published the document is online.
 * </ul>
 * Query success should always be 100% when using this handler.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TestOracle extends AbstractOverlayHandler implements
		ConnectivityListener {

	static final MultiSet<Integer> ranksShared = new MultiSet<Integer>();

	final Set<Integer> localRanksShared = new LinkedHashSet<Integer>();

	static long queryUID = 0;

	// private FilesharingApplication app;

	@Override
	public void join() {
		this.getFSApplication().getHost().getNetLayer()
				.addConnectivityListener(this);
	}

	@Override
	public void leave() {
		// Nothing to do
	}

	@Override
	public void downloadResource(int key) {
		if (this.getFSApplication().getHost().getNetLayer().isOffline()) {
			throw new IllegalStateException(
					"Host is offline and wants to lookup.");
		}
		AbstractOverlayHandler.downloadStarted(null, queryUID);
		if (ranksShared.containsOccurrence(key)) {
			downloadSucceeded(null, queryUID, 0);
		}
		queryUID++;
	}

	@Override
	public void publishResource(FileSharingDocument resource) {

		AbstractOverlayHandler.publishStarted(null, resource.getId(), queryUID);
		localRanksShared.add(resource.getId());
		ranksShared.addOccurrence(resource.getId());
		publishSucceeded(null, null, resource.getId(), queryUID, 0);
		queryUID++;

	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			// log.debug("Gone offline.");
			ranksShared.removeOccurrences(localRanksShared);
		} else {
			// log.debug("Gone online.");
			ranksShared.addOccurrences(localRanksShared);
		}
	}

}
