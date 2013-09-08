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

package org.peerfact.impl.overlay.dht.chord.base.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayRoutingTable;


/**
 * The extension functionalities will be implemented in this class
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractChordRoutingTable implements Serializable,
		NeighborDeterminator<AbstractChordContact>,
		OverlayRoutingTable<ChordID, AbstractChordContact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2656094163621590645L;

	/**
	 * Returns the closest finger preceding id n.closest_preceding_finger(id)
	 * for i= m-1 downto 0 if(finger[i].node in (n,id) return finger[i].node
	 * return n
	 * 
	 * @param id
	 * @return the point in finger table, which is closest by the input id
	 * 
	 */
	public abstract AbstractChordContact getClosestPrecedingFinger(ChordID id);

	/**
	 * Returns the num closest preceding fingers
	 * 
	 * @param id
	 * @param num
	 * @return
	 */
	public abstract List<AbstractChordContact> getClosestPrecedingFingers(
			ChordID id, int num);

	public abstract boolean responsibleFor(ChordID key);

	/**
	 * @param index
	 * @return start address of i-th finger point
	 */
	public abstract ChordID getPointAddress(int index);

	/**
	 * refresh finger table when receive leaving event of node
	 * 
	 * @param offlineNode
	 */
	public abstract void receiveOfflineEvent(AbstractChordContact offlineNode);

	public abstract AbstractChordContact[] copyFingerTable();

	public abstract AbstractChordContact getFingerEntry(int index);

	public abstract AbstractChordNode getMasterNode();

	protected abstract void sendAndWait(Message msg,
			AbstractChordContact receiver);

	public abstract AbstractChordContact getPredecessor();

	public abstract List<AbstractChordContact> getPredecessors();

	public abstract AbstractChordContact getSuccessor();

	public abstract List<AbstractChordContact> getSuccessors();

	public abstract void setInactive();

	public abstract boolean isActive();

	@Override
	public abstract Collection<AbstractChordContact> getNeighbors();

	public abstract List<AbstractChordContact> getAllDistantPredecessor();

	public abstract List<AbstractChordContact> getAllDistantSuccessor();

	public abstract AbstractChordContact getDistantPredecessor(int index);

	public abstract AbstractChordContact getDistantSuccessor(int index);

}
