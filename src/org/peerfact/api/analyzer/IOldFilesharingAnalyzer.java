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

package org.peerfact.api.analyzer;

import org.peerfact.api.common.Transmitable;
import org.peerfact.api.overlay.OverlayContact;

/**
 * DHTOverlayAnalyzer receives information about various events occurring in a
 * DHT network like started transfers, failed transfers, served querys, created
 * mirrors, free bandwidth and so on.
 * 
 * @author wette
 * @version 1.0, 06/30/2011
 */
@Deprecated
public interface IOldFilesharingAnalyzer {

	/**
	 * called when a host receives responsibility for a mirror. A mirror may be
	 * placed because the origin of the document is not able to serve all the
	 * querys for the document.
	 * 
	 * @param host
	 * @param document
	 */
	@Deprecated
	public void mirrorAssigned(OverlayContact<?> host, Transmitable document);

	/**
	 * called if a document, that is assigned through the default document
	 * assigning process of the overlay, is served.
	 * 
	 * @param server
	 * @param document
	 * @param success
	 *            true if the document was present and could be served
	 */
	@Deprecated
	public void ownDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean success);

	/**
	 * called if a mirrored document has been served
	 * 
	 * @param server
	 * @param document
	 * @param source
	 *            mirror is predecessor or successor
	 */
	@Deprecated
	public void mirroredDocumentServed(OverlayContact<?> server,
			Transmitable document, boolean source);

	/**
	 * called if a mirror is dropped
	 * 
	 * @param server
	 * @param document
	 */
	@Deprecated
	public void mirrorDeleted(OverlayContact<?> server,
			Transmitable document);

}