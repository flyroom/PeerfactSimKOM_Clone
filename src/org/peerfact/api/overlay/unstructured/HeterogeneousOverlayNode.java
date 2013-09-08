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

package org.peerfact.api.overlay.unstructured;

import java.util.List;
import java.util.Set;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.GnutellaLikeOverlayContact;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IQueryInfo;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.IResource;
import org.peerfact.impl.overlay.unstructured.heterogeneous.api.QueryHit;


/**
 * API of heterogeneous overlay nodes (such as Gnutella06 and Gia).
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface HeterogeneousOverlayNode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends UnstructuredOverlayNode<T, S> {

	public abstract S getOwnContact();

	/**
	 * Publishes the given ranks that are given in a comma-separated string
	 * representation of integers, e.g.: "1,2,3,4,5"
	 */
	public abstract void publishRanks(String docs,
			OperationCallback<Set<S>> callback);

	/**
	 * Publishes the documents given in the set.
	 */
	public abstract void publishSet(Set<IResource> res,
			OperationCallback<Set<S>> callback);

	/**
	 * Queries a document, represented by a rank.
	 */
	public abstract void queryRank(
			int rank,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback);

	/**
	 * Queries a document given query information (for "advanced" users and
	 * custom query semantics)
	 */
	public abstract void query(
			IQueryInfo info,
			int hitsWanted,
			OperationCallback<List<QueryHit<GnutellaLikeOverlayContact>>> callback);

	/**
	 * Returns the set of resources this node has currently published.
	 */
	public abstract Set<IResource> getResources();

}
