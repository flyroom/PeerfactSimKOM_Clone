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

package org.peerfact.impl.service.publishsubscribe.mercury.operations;

import java.util.List;
import java.util.Vector;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryContact;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryService;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryNotification;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercuryNotificationContainer;


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
 * Operation for one Target, collects Notifications and sends them after a
 * specified ammount of time as bulk Message
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class NotificationOperation extends
		AbstractOperation<MercuryService, MercuryContact> {

	private MercuryService service = null;

	private MercuryContact target = null;

	private List<MercuryNotification> notifications = new Vector<MercuryNotification>();

	public NotificationOperation(MercuryService component,
			MercuryContact target,
			OperationCallback<MercuryContact> callback) {
		super(component, callback);
		this.target = target;
		this.service = component;
	}

	@Override
	protected void execute() {
		if (notifications.isEmpty()) {
			operationFinished(false);
		} else {
			MercuryNotificationContainer container = new MercuryNotificationContainer(
					new Vector<MercuryNotification>(notifications));
			service.getHost()
					.getTransLayer()
					.send(container, target.getTransInfo(), service.getPort(),
							TransProtocol.UDP);
			// log.debug(Simulator.getFormattedTime(Simulator
			// .getCurrentTime())
			// + " NotifyContainer sent to "
			// + target.toString()
			// + container.toString());
			notifications.clear();
			operationFinished(true);
		}
	}

	@Override
	public MercuryContact getResult() {
		return target;
	}

	public void stop() {
		operationFinished(false);
	}

	/**
	 * Add a notification which gets sent in next Bulk
	 * 
	 * @param notify
	 */
	public void addNotification(MercuryNotification notify) {
		notifications.add(notify);
	}
}
