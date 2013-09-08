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

package org.peerfact.impl.overlay.unstructured.heterogeneous.api;

import java.util.Set;

/**
 * Abstract information that is sent with a query. Needed to decide if a
 * document matches a given query or not.
 * 
 * Can be just an identifier or something more advanced, like a set of keywords
 * or an SQL-like query.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface IQueryInfo {

	/**
	 * Returns the number of matches of this query in the given resource set.
	 * E.g. if this value is > 0, the node that received the query sends a Query
	 * Hit.
	 * 
	 * @param resourceSet
	 * @return
	 */
	public int getNumberOfMatchesIn(Set<IResource> resourceSet);

	/**
	 * Returns the size of this query information in the transport
	 * representation.
	 * 
	 * @return
	 */
	public int getSize();

}
