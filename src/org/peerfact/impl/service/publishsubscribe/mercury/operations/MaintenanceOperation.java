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
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.publishsubscribe.mercury.MercuryService;
import org.peerfact.impl.service.publishsubscribe.mercury.messages.MercurySubscription;


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
 * periodically executed Maintenance Operation. Used to remove outdated
 * subscriptions
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MaintenanceOperation extends
		AbstractOperation<MercuryService, Object> {

	private MercuryService service = null;

	public MaintenanceOperation(MercuryService component,
			OperationCallback<Object> callback) {
		super(component, callback);

		this.service = component;
	}

	@Override
	protected void execute() {
		List<MercurySubscription> subs = service.getSubscriptions();
		List<MercurySubscription> remove = new Vector<MercurySubscription>();
		for (MercurySubscription sub : subs) {
			if (!sub.isValid()) {
				remove.add(sub);
			}
		}
		subs.removeAll(remove);
		operationFinished(true);
	}

	@Override
	public Object getResult() {
		// do nothing
		return null;
	}

	public void stop() {
		operationFinished(false);
	}

}
