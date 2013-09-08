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

package org.peerfact.impl.service.publishsubscribe.mercury.messages;

import java.util.List;

import org.peerfact.impl.service.publishsubscribe.mercury.MercuryContact;
import org.peerfact.impl.service.publishsubscribe.mercury.attribute.IMercuryAttribute;


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
 * Response to a subscription
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryNotification extends AbstractMercuryMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4243820314254736286L;

	private List<IMercuryAttribute> attributes = null;

	private MercuryContact origin = null;

	private MercuryContact rendevouzPoint = null;

	public MercuryNotification(List<IMercuryAttribute> attributes,
			MercuryContact origin, MercuryContact rendevouzPoint) {
		super();
		this.attributes = attributes;
		this.rendevouzPoint = rendevouzPoint;
		this.origin = origin;
	}

	public List<IMercuryAttribute> getAttributes() {
		return this.attributes;
	}

	public MercuryContact getOrigin() {
		return this.origin;
	}

	public MercuryContact getRendevouzPoint() {
		return this.rendevouzPoint;
	}

	@Override
	public String toString() {
		return "Notification [" + getSeqNr() + "] Origin: " + origin.toString()
				+ " attr: "
				+ attributes.toString();
	}

	@Override
	public long getSize() {
		int size = 0;
		for (IMercuryAttribute attr : attributes) {
			size += attr.getTransmissionSize();
		}
		return super.getSize() + size + origin.getTransmissionSize()
				+ rendevouzPoint.getTransmissionSize();
	}

}
