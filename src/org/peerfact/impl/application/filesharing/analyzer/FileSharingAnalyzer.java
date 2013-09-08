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

package org.peerfact.impl.application.filesharing.analyzer;

import java.util.Set;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;


/**
 * FileSharingAnalyzer receive notifications about events in the file sharing
 * application. This way it is possible to collect data independent of the used
 * overlay.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public interface FileSharingAnalyzer extends Analyzer {

	/**
	 * A lookup has been started by the filesharing application.
	 * 
	 * @param initiator
	 *            : the initiator of the lookup
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	public void downloadStarted(OverlayContact<OverlayID<?>> initiator,
			Object queryUID);

	/**
	 * A lookup started by the filesharing application has succeeded.
	 * 
	 * @param initiator
	 *            : the initiator of the lookup
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	public void downloadSucceeded(OverlayContact<OverlayID<?>> initiator,
			Object queryUID, long filesize);

	/**
	 * A publish has been started by the filesharing application.
	 * 
	 * @param initiator
	 *            : the initiator of the publish
	 * @param keyToPublish
	 *            : the key that has to be published
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	public void publishStarted(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish, Object queryUID);

	/**
	 * A lookup has failed by the filesharing application.
	 * 
	 * @param initiator
	 *            : the initiator of the lookup
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	public void downloadFailed(OverlayContact<OverlayID<?>> initiator,
			Object queryUID);

	/**
	 * A publish started by the filesharing application has succeeded.
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
	public void publishSucceeded(OverlayContact<OverlayID<?>> initiator,
			Set<OverlayContact<OverlayID<?>>> holder, int keyPublished,
			Object queryUID, long filesize);

	/**
	 * A publish has failed by the filesharing application.
	 * 
	 * @param initiator
	 *            : the initiator of the publish
	 * @param keyToPublish
	 *            : the key that has to be published
	 * @param queryUID
	 *            : a query UID that is consistent with the equals() method.
	 */
	public void publishFailed(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish, Object queryUID);

}
