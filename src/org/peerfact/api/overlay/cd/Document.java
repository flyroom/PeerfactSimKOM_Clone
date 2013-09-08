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

import java.io.Serializable;

import org.peerfact.api.overlay.OverlayKey;


/**
 * All specific documents in the application layer and overlay layer should be a
 * subtype of this interface.
 * 
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 1.0, 11/25/2007
 * 
 */
public interface Document<T extends OverlayKey<?>> extends Serializable {

	/**
	 * Documents can have three different states: empty, partial and complete
	 * 
	 * @author Sebastian Kaune
	 * 
	 */
	public enum State {
		/**
		 * The document is empty if only its OverlayKey is known.
		 */
		EMPTY,
		/**
		 * The document state becomes partial if some chunks have been
		 * downloaded
		 */
		PARTIAL,
		/**
		 * The document is complete if all chunks are available
		 */
		COMPLETE
	}

	/**
	 * Assigns the OverlayKey <code>key</code> to a given document
	 * 
	 * @param key
	 */
	public void setKey(T key);

	/**
	 * Returns the OverlayKey of a given document
	 * 
	 * @return the appropriate OverlayKey
	 */
	public T getKey();

	/**
	 * Sets the size of a given document
	 * 
	 * @param newSize
	 *            the size in bytes
	 */
	public void setSize(long newSize);

	/**
	 * Returns the size in bytes of a given document
	 * 
	 * @return the document size in bytes
	 */
	public long getSize();

	/**
	 * Returns the popularity of a given document
	 * 
	 * @return the popularity of the document
	 */
	public int getPopularity();

	/**
	 * Assigns a popularity to a given document
	 * 
	 * @param popularity
	 *            the popularity of the document
	 */
	public void setPopularity(int popularity);

	/**
	 * Changes the state of a given document
	 * 
	 * @param state
	 *            the new state
	 */
	public void setState(State state);

	/**
	 * Returns the state of a given document
	 * 
	 * @return the state of the document
	 */
	public State getState();

	/**
	 * Returns a copy of the current document.
	 * 
	 * @return a copy
	 */
	public Document<T> copy();
}
