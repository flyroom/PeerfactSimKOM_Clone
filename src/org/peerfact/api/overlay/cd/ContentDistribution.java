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

package org.peerfact.api.overlay.cd;

import java.util.Collection;
import java.util.List;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransInfo;


/**
 * 
 * Represents a distribution and storage of documents inside of a host, which
 * can be used by applications and overlays to store documents locally and fetch
 * them again later. Furthermore documents can transferred through different
 * peers.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 11/25/2007
 * 
 */
public interface ContentDistribution<T extends OverlayKey<?>> extends
		OverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>> {

	/**
	 * Fetch doc from the local storage.
	 * 
	 * @param key
	 *            document's key
	 * @return the requested document if present, null otherwise
	 */
	public Document<T> loadDocument(T key);

	/**
	 * Store the document in the local storage.
	 * 
	 * @param doc
	 *            document to stores
	 */
	public void storeDocument(Document<T> doc);

	/**
	 * List the keys of all locally stored documents
	 * 
	 * @return documents stored locally.
	 */
	public Collection<T> listDocumentKeys();

	/**
	 * The way to find out whether a document with the given key is inside of
	 * this storage.
	 * 
	 * @param key
	 *            document key
	 * @return whether the document with the <code>key</code> is inside
	 */
	public boolean containsDocument(T key);

	/**
	 * 
	 * @return all documents stored inside of this storage
	 */
	public Collection<Document<T>> listDocuments();

	/**
	 * Calling this method will download the document identified by the given
	 * key from the given peers. Depending on the strategy implementation one,
	 * some or all of the provided peers will be involved.
	 * 
	 * @param key
	 *            identifies the requested document
	 * @param peers
	 *            addresses of hosts which should have copies of the requested
	 *            document
	 * @param callback
	 *            callback for this operation
	 * @return operation id
	 */
	public int downloadDocument(T key, List<TransInfo> peers,
			OperationCallback<Document<T>> callback);

	/**
	 * Calling this method will upload the document identified by the given key
	 * to the given peers.
	 * 
	 * @param document
	 *            the document
	 * @param peers
	 *            addresses of hosts which should receive copies of the
	 *            requested document
	 * @param callback
	 *            callback for this operation
	 * @return operation id
	 */
	public int uploadDocument(Document<T> document, List<TransInfo> peers,
			OperationCallback<List<TransInfo>> callback);

	/**
	 * The address at which this strategy waits for incoming download requests.
	 * 
	 * @return trans info address of this component.
	 */
	public TransInfo getTransInfo();
}
