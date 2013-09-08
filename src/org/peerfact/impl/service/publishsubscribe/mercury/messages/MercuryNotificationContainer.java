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

import org.peerfact.api.common.Message;


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
 * Container for MercuryNotifications, stores a bunch of Notifications for one
 * target and allows for better utilization of the assigned bandwith
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryNotificationContainer extends AbstractMercuryMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5569983003651797200L;

	private List<MercuryNotification> notifications;

	public MercuryNotificationContainer(List<MercuryNotification> notifications) {
		this.notifications = notifications;
	}

	/**
	 * Get all Notifications stored in this Container
	 * 
	 * @return
	 */
	public List<MercuryNotification> getNotifications() {
		return notifications;
	}

	@Override
	public String toString() {
		return "NotificationContainer [" + getSeqNr() + "]"
				+ notifications.toString();
	}

	@Override
	public long getSize() {
		long size = 0;
		for (MercuryNotification notify : notifications) {
			size += notify.getSize();
		}
		return super.getSize() + size;
	}

	@Override
	public Message getPayload() {
		return this;
	}

}
