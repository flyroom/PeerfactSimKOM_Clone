/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.application.filesharing;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.application.filesharing.documents.DocumentSpace;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;
import org.peerfact.impl.application.filesharing.documents.IDocumentSet;
import org.peerfact.impl.application.filesharing.operations.AbstractPeriodicFilesharingOperation;
import org.peerfact.impl.application.filesharing.operations.FSDownloadResourceFromSetOperation;
import org.peerfact.impl.application.filesharing.operations.FSDownloadResourceOperation;
import org.peerfact.impl.application.filesharing.operations.FSJoinOperation;
import org.peerfact.impl.application.filesharing.operations.FSLeaveOperation;
import org.peerfact.impl.application.filesharing.operations.FSPublishMultipleResourcesOperation;
import org.peerfact.impl.application.filesharing.operations.intervalModels.ExponentialIntervalModel;
import org.peerfact.impl.application.filesharing.operations.intervalModels.LinearIntervalModel;
import org.peerfact.impl.application.filesharing.overlayhandler.IOverlayHandler;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.util.stats.distributions.Distribution;
import org.peerfact.impl.util.stats.distributions.ExponentialDistribution;


/**
 * Client for a <i>napster-like</i> application able to advertise shared
 * documents in any DHT-like overlay. The functionality provided to the user
 * includes:
 * <ul>
 * <li>publish document</li>
 * <li>download document</li>
 * </ul>
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 3.0, 10.12.2007
 * 
 */
public class FileSharingApplication extends AbstractApplication {

	private IOverlayHandler overlay;

	private ContentDistribution<OverlayKey<?>> cd;

	@Override
	public String toString() {
		return "FileSharingApplication";
	}

	public IOverlayHandler getOverlay() {
		return overlay;
	}

	public ContentDistribution<OverlayKey<?>> getContentDistribution() {
		return this.cd;
	}

	private Set<FileSharingDocument> publishedDocuments = new LinkedHashSet<FileSharingDocument>();

	private List<AbstractPeriodicFilesharingOperation<?, ?>> periodicLookupOperations = new Vector<AbstractPeriodicFilesharingOperation<?, ?>>();

	/**
	 * Default Constructor
	 * 
	 * @param overlay
	 */
	FileSharingApplication(IOverlayHandler overlay,
			ContentDistribution<OverlayKey<?>> distStrategy) {
		overlay.setFSApplication(this);
		this.overlay = overlay;
		this.cd = distStrategy;
	}

	/**
	 * Joins the overlay network initially. Connects to the overlay network
	 * using the method defined by the specific overlay.
	 */
	public void join() {
		// this.getHost().getNetLayer().addConnectivityListener(this);
		FSJoinOperation op = new FSJoinOperation(overlay, this,
				Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
	}

	/**
	 * Leaves the overlay network. Disconnects from the overlay network as
	 * defined by the overlay used.
	 */
	public void leave() {
		// this.getHost().getNetLayer().removeConnectivityListener(this);
		FSLeaveOperation op = new FSLeaveOperation(overlay, this,
				Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
		stopAllLookupOperations();
	}

	/**
	 * Publishes random resources from the given set. The number of resources
	 * drawn from this set and published is randomly taken from an exponential
	 * distribution with the given mean amount
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 * @param meanAmount
	 *            : the mean amount of documents published (amount is
	 *            exponentially distributed).
	 */
	public void publishResourcesFromSet(String setName, int meanAmount) {
		Distribution meanDocAmount = new ExponentialDistribution(meanAmount);
		int docAmount = (int) meanDocAmount.returnValue();

		Set<FileSharingDocument> documents = DocumentSpace.getInstance()
				.getSomeDocumentsForPublish(
						DocumentSpace.getInstance().getResourceSet(setName),
						docAmount);

		log.debug("Publishing the documents " + documents + "...");

		publishResourcesSet(documents);

	}

	/**
	 * Draws exactly one resource from the given set and publishes it.
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 */
	public void publishOneResourceFromSet(String setName) {
		Set<FileSharingDocument> documents = DocumentSpace.getInstance()
				.getSomeDocumentsForPublish(
						DocumentSpace.getInstance().getResourceSet(setName), 1);

		log.debug("Publishing the document " + documents + "...");

		publishResourcesSet(documents);

	}

	/**
	 * Looks up a resource from a given set in the overlay network. <b>Only
	 * looks up documents from a set that were already published</b>, and anyone
	 * of the publishers <b>is currently online</b>. If no document of a set is
	 * published or no publisher of a document of this set is currently online,
	 * a runtime error is thrown.
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 */
	public void lookupResourceFromSet(String setName) {
		lookupResourceFromSetDirect(DocumentSpace.getInstance().getResourceSet(
				setName));
	}

	/**
	 * Looks up a resource from a given set in the overlay network. <b>Only
	 * looks up documents from a set that were already published</b>, and anyone
	 * of the publishers <b>is currently online</b>. If no document of a set is
	 * published or no publisher of a document of this set is currently online,
	 * a runtime error is thrown.
	 * 
	 * @param set
	 *            : the given document set object to use.
	 */
	public void lookupResourceFromSetDirect(IDocumentSet set) {
		lookupResource(getDocumentForLookup(set));
	}

	/**
	 * <b>Periodically</b> looks up a resource from a given set in the overlay
	 * network. <b>Only looks up documents from a set that were already
	 * published</b>, and anyone of the publishers <b>is currently online</b>.
	 * If no document of a set is published or no publisher of a document of
	 * this set is currently online, a runtime error is thrown.
	 * 
	 * The lookups are equally distributed over time, thus the interval between
	 * two lookups <b>is exponentially distributed</b>.
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 * @param meanPeriod
	 *            : The mean lookup interval
	 */
	public void lookupResourceFromSetPeriodically(String setName,
			long meanPeriod) {
		lookupResourceFromSetPeriodicallyDirect(DocumentSpace.getInstance()
				.getResourceSet(setName), meanPeriod);
	}

	public void lookupResourceFromSetIncreasingRequests(String setName,
			double startRequestsPerHour, double endRequestsPerHour,
			long interval) {
		IDocumentSet set = DocumentSpace.getInstance().getResourceSet(setName);
		FSDownloadResourceFromSetOperation op = new FSDownloadResourceFromSetOperation(
				set, this, Operations.EMPTY_CALLBACK);
		this.periodicLookupOperations.add(op);
		op.schedulePeriodically(new LinearIntervalModel(startRequestsPerHour,
				endRequestsPerHour, interval));
	}

	/**
	 * <b>Periodically</b> looks up a resource from a given set in the overlay
	 * network. <b>Only looks up documents from a set that were already
	 * published</b>, and anyone of the publishers <b>is currently online</b>.
	 * If no document of a set is published or no publisher of a document of
	 * this set is currently online, a runtime error is thrown.
	 * 
	 * The lookups are equally distributed over time, thus the interval between
	 * two lookups <b>is exponentially distributed</b>.
	 * 
	 * @param set
	 *            : the given document set object to use
	 * @param meanPeriod
	 *            : The mean lookup interval
	 */
	public void lookupResourceFromSetPeriodicallyDirect(IDocumentSet set,
			long meanPeriod) {
		FSDownloadResourceFromSetOperation op = new FSDownloadResourceFromSetOperation(
				set, this, Operations.EMPTY_CALLBACK);
		this.periodicLookupOperations.add(op);
		op.schedulePeriodically(new ExponentialIntervalModel(meanPeriod));
	}

	protected static int getDocumentForLookup(IDocumentSet set) {
		int res = DocumentSpace.getInstance().getIdForLookup(set);
		// log.debug(this.getHost().getNetLayer() + "Retrieved" + res +
		// "for lookup.");
		return res;
	}

	/**
	 * <b>Periodically</b> looks up a document with a given document ID.
	 * 
	 * @param documentID
	 *            : the integer ID of the document
	 * @param meanPeriod
	 *            : the mean period in which the document shall be looked up.
	 */
	public void lookupResourcePeriodically(int documentID, long meanPeriod) {
		FSDownloadResourceOperation op = new FSDownloadResourceOperation(
				overlay,
				documentID, this, Operations.EMPTY_CALLBACK);
		op.schedulePeriodically(new ExponentialIntervalModel(meanPeriod));
		this.periodicLookupOperations.add(op);
	}

	/**
	 * Publishes a set of resources.
	 * 
	 * @param docs
	 *            : a set of integer document identifiers.
	 */
	public void publishResourcesSet(Set<FileSharingDocument> docs) {
		publishedDocuments.addAll(docs);
		if (this.getHost().getNetLayer().isOnline()) {
			// log.debug(this.getHost().getNetLayer() +
			// "Beginning sharing, adding " + publishedRanks +
			// "to shared set.");
			// DocumentSpace.getInstance().activateMyPublishedKeys(docs);
		}
		FSPublishMultipleResourcesOperation op = new FSPublishMultipleResourcesOperation(
				overlay, docs, this, Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
	}

	/**
	 * Looks up a resource given its document ID.
	 * 
	 * @param documentID
	 */
	public void lookupResource(int documentID) {
		if (this.getHost().getNetLayer().isOffline()) {
			log.warn("Host is offline, suspending lookup.");
			return;
		}
		FSDownloadResourceOperation op = new FSDownloadResourceOperation(
				overlay,
				documentID, this, Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
	}

	public void stopAllLookupOperations() {
		for (AbstractPeriodicFilesharingOperation<?, ?> pco : periodicLookupOperations) {
			pco.stop();
			periodicLookupOperations.remove(pco);
		}
	}

}
