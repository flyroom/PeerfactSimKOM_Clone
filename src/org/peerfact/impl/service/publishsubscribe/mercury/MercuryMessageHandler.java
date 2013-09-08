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

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryMessage;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryNotification;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryNotificationContainer;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryPublication;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryPublicationInterHub;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercurySendRange;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercurySubscription;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercurySubscriptionDirect;
import org.peerfact.impl.transport.TransMsgEvent;

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
 * Message Handler for Mercury
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MercuryMessageHandler implements TransMessageListener {

	MercuryService service = null;

	public MercuryMessageHandler(MercuryService service) {
		this.service = service;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();

		// Mercury Messages inside of KBR Forward-Messages are not recognized
		// here (correct behaviour)
		if (msg instanceof MercuryMessage) {
			/*
			 * received a Notification, inform listeners (applications)
			 */
			if (msg instanceof MercuryNotification) {
				MercuryNotification notification = (MercuryNotification) msg;
				service.receivedNotification(notification);
			}

			/*
			 * received a Notification-Container, containing multiple
			 * Notifications for one Node
			 */
			if (msg instanceof MercuryNotificationContainer) {
				MercuryNotificationContainer container = (MercuryNotificationContainer) msg;

				for (MercuryNotification notify : container.getNotifications()) {
					service.receivedNotification(notify);
				}
			}

			/*
			 * received a MercuryRange-Message, store contact information and
			 * reply with own info, if needed
			 */
			if (msg instanceof MercurySendRange) {
				// Store Information about sender
				MercurySendRange srange = (MercurySendRange) msg;
				service.receivedSendRange(srange);
			}

			if (msg instanceof MercurySubscription) {
				MercurySubscription sub = (MercurySubscription) msg;
				if (sub.getOrigin().getAttribute()
						.equals(service.getOwnAttribute().getName())) {
					service.receivedSubscription(sub, true);
				} else {
					// TODO this is still untested! Use with care!
					service.receivedSubscriptionFromOtherHub(sub);
				}
			}

			if (msg instanceof MercurySubscriptionDirect) {
				MercurySubscriptionDirect subd = (MercurySubscriptionDirect) msg;
				service.receivedSubscription(subd.getSubscription(), false);
			}

			if (msg instanceof MercuryPublication) {
				MercuryPublication pub = (MercuryPublication) msg;
				service.receivedPublication(pub);
			}

			if (msg instanceof MercuryPublicationInterHub) {
				MercuryPublicationInterHub pubInterHub = (MercuryPublicationInterHub) msg;
				service.receivedPublicationFromOtherHub(
						pubInterHub
								.getPublication());
			}

		}
	}

}
