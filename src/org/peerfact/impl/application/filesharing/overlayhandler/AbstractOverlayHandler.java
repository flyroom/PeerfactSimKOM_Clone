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

import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.application.filesharing.FileSharingApplication;
import org.peerfact.impl.application.filesharing.analyzer.FileSharingMonitor;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * An overlay handler implements the common overlay filesharing operations like
 * defined in IOverlayHandler. It uses a specific overlay implementation to
 * fulfill these tasks. The overlay handlers decouple the filesharing
 * application and the overlay implementations.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractOverlayHandler implements IOverlayHandler {

	private FileSharingApplication app;

	protected static Logger log = SimLogger
			.getLogger(AbstractOverlayHandler.class);

	@Override
	public void setFSApplication(FileSharingApplication app) {
		this.app = app;
	}

	/**
	 * Returns the filesharing application that calls the operations on
	 * IOverlayHandler
	 * 
	 * @return
	 */
	protected FileSharingApplication getFSApplication() {
		return app;
	}

	/**
	 * Called when a lookup was started.
	 * 
	 * @param initiator
	 *            : the initiator of the lookup
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	protected static void downloadStarted(
			OverlayContact<OverlayID<?>> initiator,
			Object queryUID) {
		if (Simulator.getMonitor() instanceof FileSharingMonitor) {
			((FileSharingMonitor) Simulator.getMonitor()).downloadStarted(
					initiator,
					queryUID);
		}
	}

	/**
	 * Called when a lookup previously started succeeded.
	 * 
	 * @param initiator
	 *            : the initiator of the lookup
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 * @param hops
	 *            : the number of hops that were done to make the lookup
	 */
	protected static void downloadSucceeded(
			OverlayContact<OverlayID<?>> initiator,
			Object queryUID,
			long filesize) {
		if (Simulator.getMonitor() instanceof FileSharingMonitor) {
			((FileSharingMonitor) Simulator.getMonitor()).downloadSucceeded(
					initiator, queryUID, filesize);
		}
	}

	/**
	 * Called when a lookup was started.
	 * 
	 * @param initiator
	 *            : the initiator of the lookup
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	protected static void downloadFailed(
			OverlayContact<OverlayID<?>> initiator,
			Object queryUID) {
		if (Simulator.getMonitor() instanceof FileSharingMonitor) {
			((FileSharingMonitor) Simulator.getMonitor()).downloadFailed(
					initiator,
					queryUID);
		}
	}

	/**
	 * Called when a publish was started.
	 * 
	 * @param initiator
	 *            : the initiator of the publish
	 * @param keyToPublish
	 *            : the key that has to be published
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	protected static void publishStarted(
			OverlayContact<OverlayID<?>> initiator,
			int keyToPublish,
			Object queryUID) {
		if (Simulator.getMonitor() instanceof FileSharingMonitor) {
			((FileSharingMonitor) Simulator.getMonitor()).publishStarted(
					initiator, keyToPublish, queryUID);
		}
	}

	/**
	 * Called when a publish succeeded.
	 * 
	 * @param initiator
	 *            : the initiator of the publish
	 * @param holder
	 *            : the holder of the resource that has been published.
	 * @param keyPublished
	 *            : the key that has been published
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	protected static void publishSucceeded(
			OverlayContact<OverlayID<?>> initiator,
			Set<OverlayContact<OverlayID<?>>> holder, int keyPublished,
			Object queryUID,
			long filesize) {
		if (Simulator.getMonitor() instanceof FileSharingMonitor) {
			((FileSharingMonitor) Simulator.getMonitor()).publishSucceeded(
					initiator, holder,
					keyPublished, queryUID, filesize);
		}
	}

	/**
	 * Called when a publish has failed.
	 * 
	 * @param initiator
	 *            : the initiator of the publish
	 * @param keyToPublish
	 *            : the key that has to be published
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	protected static void publishFailed(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish,
			Object queryUID) {
		if (Simulator.getMonitor() instanceof FileSharingMonitor) {
			((FileSharingMonitor) Simulator.getMonitor()).publishFailed(
					initiator,
					keyToPublish,
					queryUID);
		}
	}

}
