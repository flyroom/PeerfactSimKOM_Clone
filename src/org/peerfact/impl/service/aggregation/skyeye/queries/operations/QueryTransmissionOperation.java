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

package org.peerfact.impl.service.aggregation.skyeye.queries.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.queries.Query;

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
 * This class implements the operation of a transmission of a query. Within this
 * operation a originated query is injected in the over-overlay of SkyNet and
 * forwarded towards the root until the query is solved or the root is reached.
 * In both cases the solved or unsolved query is returned to the originator and
 * this operation is finished. As this operation is not finished by receiving an
 * acknowledgment, the operation is not terminated within this class, but
 * provides the method <code>setFinishOfOperation(boolean success)</code>, which
 * allows for terminating the operation from any class of a SkyNet-node. If the
 * named method is not called within a predefined period of time, a timeout
 * occurs.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class QueryTransmissionOperation extends
		AbstractOperation<SkyNetNodeInterface, Object> {

	private SkyNetNodeInfo senderInfo;

	private SkyNetNodeInfo receiverInfo;

	private Query query;

	private boolean receiverSP;

	private long timeout;

	public QueryTransmissionOperation(SkyNetNodeInterface component,
			SkyNetNodeInfo senderInfo, SkyNetNodeInfo receiverInfo,
			Query query, boolean receiverSP, long timeout,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.senderInfo = senderInfo;
		this.receiverInfo = receiverInfo;
		this.query = query;
		this.receiverSP = receiverSP;
		this.timeout = timeout;
	}

	@Override
	protected void execute() {
		log
				.debug(SkyNetUtilities.getTimeAndNetID(senderInfo)
						+ "starts the QueryTransmission of query "
						+ query.getQueryID());
		scheduleOperationTimeout(timeout);
		((SkyNetNode) getComponent()).getQueryHandler().sendQuery(senderInfo,
				receiverInfo, query, false, -1, receiverSP);
	}

	@Override
	public Object getResult() {
		// not needed
		return null;
	}

	/**
	 * This method allows for terminating the operation from any class of the
	 * SkyNet-node. The given parameter provides the state of the finished
	 * operation (success or failure).
	 * 
	 * @param success
	 *            contains the state of the finished operation
	 */
	public void setFinishOfOperation(boolean success) {
		operationFinished(success);
	}

}
