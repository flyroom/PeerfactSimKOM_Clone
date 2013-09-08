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

package org.peerfact.impl.application.filesharing.documents;

/**
 * Behaves like a set of documents where keys can be drawn for lookup and
 * publishing.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Philip Wette
 * 
 * @version 05/06/2011
 */
public interface IDocumentSet {

	/**
	 * Returns the number of documents this set consists of.
	 * 
	 * @return
	 */
	public abstract int getSize();

	/**
	 * Returns an document id that can be looked up by the application layer.
	 * 
	 * @return
	 */
	public abstract int getIdForLookup();

	/**
	 * Returns the string name of the document set.
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * The ranks of different document sets must not overlap. Sets the first
	 * rank of this document set. Further ranks of this set are numbers
	 * following this one.
	 * 
	 * @param rank
	 */
	public abstract void setBeginRank(int rank);

	/**
	 * Returns a document that can be published by the application layer.
	 * 
	 * @return a FileSharingDocument
	 */
	public abstract FileSharingDocument getDocumentForPublish();

	/**
	 * Returns whether this set contains at least one resource of the given set.
	 * 
	 * @param set
	 * @return
	 */
	public abstract boolean containsResourcesOf(Iterable<Integer> set);

}
