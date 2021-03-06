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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing;

import org.peerfact.Constants;
import org.peerfact.api.overlay.cd.Document;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class FilesharingDocument implements Document<FilesharingKey> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4699219916176815454L;

	private FilesharingKey key;

	private Integer rank;

	public FilesharingDocument(Integer rank) {
		this.rank = rank;
		key = new FilesharingKey(rank);
	}

	@Override
	public FilesharingKey getKey() {
		return this.key;
	}

	@Override
	public int getPopularity() {
		return rank;
	}

	@Override
	public long getSize() {
		return key.getTransmissionSize() + Constants.INT_SIZE;
	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setKey(FilesharingKey key) {
		this.key = key;

	}

	@Override
	public void setPopularity(int popularity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSize(long newSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(State state) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return rank.toString();
	}

	@Override
	public Document<FilesharingKey> copy() {
		return new FilesharingDocument(this.rank);
	}

}
