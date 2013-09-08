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

package org.peerfact.impl.overlay.informationdissemination.psense;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.impl.overlay.informationdissemination.psense.util.SequenceNumber;
import org.peerfact.impl.simengine.Simulator;


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
 * This class stores the information to a node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 09/15/2010
 * 
 */
public class PSenseNodeInfo implements IDONodeInfo {

	/**
	 * Describe the AOI radius
	 */
	private final int visionRangeRadius;

	private final PSenseContact contact;

	private final long lastUpdate;

	private final SequenceNumber sequenceNr;

	private List<PSenseID> receiversList;

	private byte hops;

	private final Point position;

	public PSenseNodeInfo(int visionRangeRadius, Point position,
			PSenseContact contact, SequenceNumber sequenceNr,
			List<PSenseID> receiversList, byte hops) {
		this.visionRangeRadius = visionRangeRadius;
		this.position = position;
		this.contact = contact;
		this.lastUpdate = Simulator.getCurrentTime();
		this.sequenceNr = sequenceNr;
		this.hops = hops;
		if (receiversList == null) {
			this.receiversList = new Vector<PSenseID>();
		} else {
			this.receiversList = receiversList;
		}

	}

	/**
	 * The same methode, how getAoiRadius()
	 * 
	 * @return Return the vision range radius of the node or rather the AOI
	 */
	public int getVisionRangeRadius() {
		return visionRangeRadius;
	}

	public PSenseContact getContact() {
		return contact;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public SequenceNumber getSequenceNr() {
		return sequenceNr;
	}

	public List<PSenseID> getReceiversList() {
		return receiversList;
	}

	public byte getHops() {
		return hops;
	}

	public void updateReceiversList(List<PSenseID> moreReceivers) {
		for (PSenseID id : moreReceivers) {
			if (!receiversList.contains(id)) {
				receiversList.add(id);
			}
		}
	}

	@Override
	public Point getPosition() {
		return position;
	}

	@Override
	public OverlayID<?> getID() {
		return contact.getOverlayID();
	}

	/**
	 * The same method, like getVisionRangeRadius()
	 * 
	 * @return the AOI radius
	 */
	@Override
	public int getAoiRadius() {
		return visionRangeRadius;
	}

	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("[ contact: ");
		temp.append(getContact());
		temp.append(", hops: ");
		temp.append(getHops());
		temp.append(", lastUpdate: ");
		temp.append(getLastUpdate());
		temp.append(", Position: ");
		temp.append(getPosition());
		temp.append(", receiversList: ");
		temp.append(getReceiversList());
		temp.append(", sequenceNr: ");
		temp.append(getSequenceNr());
		temp.append(", VisionRangeRadius: ");
		temp.append(getVisionRangeRadius());
		temp.append(" ]");
		return temp.toString();
	}

}
