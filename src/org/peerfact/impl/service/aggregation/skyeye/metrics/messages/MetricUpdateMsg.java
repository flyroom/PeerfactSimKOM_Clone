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

package org.peerfact.impl.service.aggregation.skyeye.metrics.messages;

import org.peerfact.api.common.Message;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.service.aggregation.skyeye.AbstractSkyNetMessage;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsEntry;

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
 * This message contains the data for a <i>metric-update</i>, which a
 * Coordinator sends to its ParentCoordinator.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class MetricUpdateMsg extends AbstractSkyNetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6709999891314739989L;

	private MetricsEntry content;

	public MetricUpdateMsg(SkyNetNodeInfo senderNodeInfo,
			SkyNetNodeInfo receiverNodeInfo, MetricsEntry content,
			long skyNetMsgID) {
		super(senderNodeInfo, receiverNodeInfo, skyNetMsgID, false, false,
				false);
		this.content = content;
	}

	@Override
	public Message getPayload() {
		// Not needed
		return null;
	}

	@Override
	public long getSize() {
		return content.getSize() + super.getSize();
	}

	/**
	 * This method returns the content of the message, which comprises the sent
	 * <code>MetricsEntry</code> of a Coordinator.
	 * 
	 * @return the <code>MetricsEntry</code> of a Coordinator
	 */
	public MetricsEntry getContent() {
		return content;
	}

}
