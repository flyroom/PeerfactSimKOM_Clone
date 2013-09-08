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

package org.peerfact.impl.service.publishsubscribe.mercury;

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
 * Class provides additional Information for each stored MercuryContact, for
 * example timestamp of last action. This information is not part of a
 * transmitted Mercury Contact.
 * 
 * TODO Bjoern: set lifetimeRange and maxTimeForRangeUpdate via config.xml
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryContactListEntry {

	private MercuryContact contact;

	private long lifetimeRange = 20 * Simulator.SECOND_UNIT;

	private long rangeValidUntil;

	private long maxTimeForRangeUpdate = 7 * Simulator.SECOND_UNIT;

	private boolean updateStarted = false;

	public MercuryContactListEntry(MercuryContact contact) {
		this.contact = contact;
		this.rangeValidUntil = Simulator.getCurrentTime() + 4
				* Simulator.SECOND_UNIT;
	}

	/**
	 * Copy, in order to prevent unwanted updates due to OOP
	 * 
	 * @return
	 */
	public MercuryContact getContact() {
		return new MercuryContact(contact.getAttribute(),
				contact.getTransInfo(),
				contact.getRange());
	}

	/**
	 * udpate MercuryContact for this Entry
	 * 
	 * @param contact
	 */
	public void setContact(MercuryContact contact) {
		this.contact = contact;
		rangeValidUntil = Simulator.getCurrentTime() + lifetimeRange;
		updateStarted = false;
	}

	/**
	 * This Contacts' range info is up-to-date and can be used
	 * 
	 * @return
	 */
	public boolean hasValidRange() {
		return Simulator.getCurrentTime() < rangeValidUntil;
	}

	/**
	 * This contact should be updated using a SendRange-Message
	 * 
	 * @return
	 */
	public boolean shouldUpdate() {
		return !updateStarted && !hasValidRange();
	}

	/**
	 * An update for this contacts' range has been initialized. Waiting for the
	 * reply.
	 */
	public void updateStarted() {
		updateStarted = true;
	}

	/**
	 * A reply did not arrive within the specified time, so this contact is
	 * assumed to be dead
	 * 
	 * @return
	 */
	public boolean isDead() {
		return Simulator.getCurrentTime() > rangeValidUntil
				+ maxTimeForRangeUpdate;
	}

	@Override
	public String toString() {
		return contact.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result
				+ (int) (lifetimeRange ^ (lifetimeRange >>> 32));
		result = prime
				* result
				+ (int) (maxTimeForRangeUpdate ^ (maxTimeForRangeUpdate >>> 32));
		result = prime * result
				+ (int) (rangeValidUntil ^ (rangeValidUntil >>> 32));
		result = prime * result + (updateStarted ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MercuryContactListEntry other = (MercuryContactListEntry) obj;
		if (contact == null) {
			if (other.contact != null) {
				return false;
			}
		} else if (!contact.equals(other.contact)) {
			return false;
		}
		if (lifetimeRange != other.lifetimeRange) {
			return false;
		}
		if (maxTimeForRangeUpdate != other.maxTimeForRangeUpdate) {
			return false;
		}
		if (rangeValidUntil != other.rangeValidUntil) {
			return false;
		}
		if (updateStarted != other.updateStarted) {
			return false;
		}
		return true;
	}

}
