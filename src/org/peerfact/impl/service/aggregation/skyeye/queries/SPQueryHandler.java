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

package org.peerfact.impl.service.aggregation.skyeye.queries;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeStorage;
import org.peerfact.impl.service.aggregation.skyeye.queries.messages.QueryForwardMsg;
import org.peerfact.impl.service.aggregation.skyeye.queries.operations.QueryForwardOperation;
import org.peerfact.impl.util.logging.SimLogger;


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
 * This class implements the component at a SkyNet-node, which is responsible
 * for the handling of a query in case that a Support Peer receives the queries.
 * The treatment of the queries within this class just comprises the handling
 * and resolution of foreign queries, that arrive at the Support Peer. For this
 * reasons, <code>QueryHandler</code> only consists of one method, that starts
 * one operation in terms of the transmission of queries (e.g.
 * {@link QueryHandler} consists of two operations). <code>sendQuery()</code>
 * forwards a query from one node to another by initiating a
 * {@link QueryForwardOperation}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class SPQueryHandler {

	private static Logger log = SimLogger.getLogger(SPQueryHandler.class);

	private SupportPeer supportPeer;

	private AttributeStorage storage;

	private long queryForwardOpTimeout;

	public SPQueryHandler(SupportPeer supportPeer, AttributeStorage storage) {
		this.supportPeer = supportPeer;
		this.storage = storage;

		// set the Properties from skynet.properties
		SkyNetPropertiesReader propReader = SkyNetPropertiesReader
				.getInstance();
		queryForwardOpTimeout = propReader
				.getTimeProperty("QueryForwardOperationTimeForAck");
	}

	// ----------------------------------------------------------------------
	// methods for sending a query to the next peer
	// ----------------------------------------------------------------------

	/**
	 * This method starts a <code>QueryForwardOperation</code>, which forwards a
	 * query to the next node in the SkyNet-tree, and provides the operation
	 * with the needed values.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param query
	 *            represents the query, which will be forwarded
	 * @param isSolved
	 *            specifies if the transmitted query is already solved
	 * @param receiverSP
	 *            specifies, if the receiver of the query is a Support Peer
	 * @param senderSP
	 *            specifies, if the sender of the query is a Support Peer
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	private int queryForwardOperation(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, Query query, boolean isSolved,
			boolean receiverSP, boolean senderSP,
			OperationCallback<Object> callback) {
		QueryForwardOperation op = new QueryForwardOperation(supportPeer,
				senderInfo, receiverInfo, query, isSolved, supportPeer
						.getMessageCounter().assignmentOfMessageNumber(),
				receiverSP, senderSP, queryForwardOpTimeout, callback);
		op.scheduleImmediately();
		return op.getOperationID();
	}

	/**
	 * This method is executed to start the operation for the forwarding of a
	 * query. Within this method, <code>queryForwardOperation()</code> is called
	 * to start the corresponding operation.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param query
	 *            represents the query, which will be forwarded
	 * @param isSolved
	 *            specifies if the transmitted query is already solved
	 * @param receiverSP
	 *            specifies, if the receiver of the query is a Support Peer
	 * @param senderSP
	 *            specifies, if the sender of the query is a Support Peer
	 */
	public void sendQuery(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, Query query, boolean isSolved,
			boolean receiverSP, boolean senderSP) {
		final boolean solved = isSolved;
		queryForwardOperation(senderInfo.clone(), receiverInfo.clone(), query,
				isSolved, receiverSP, senderSP,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						queryForwardOperationFailed(op, solved);
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						queryForwardOperationSucceeded(op);
					}

				});
	}

	void queryForwardOperationFailed(Operation<Object> op, boolean solved) {
		QueryForwardMsg msg = ((QueryForwardOperation) op).getRequest();
		if (solved) {
			log.error(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "----NO CHANCE TO SEND SOLVED QUERY "
					+ msg.getQuery().getQueryID() + " TO ORIGINATOR "
					+ SkyNetUtilities.getNetID(msg.getReceiverNodeInfo())
					+ "----");
		} else {
			log.error(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "----NO CHANCE TO SEND QUERY "
					+ msg.getQuery().getQueryID() + " TO PARENTCOORDINATOR "
					+ SkyNetUtilities.getNetID(msg.getReceiverNodeInfo())
					+ "----");
		}
	}

	void queryForwardOperationSucceeded(Operation<Object> op) {
		log.debug(SkyNetUtilities.getTimeAndNetID(supportPeer)
				+ "QueryForwardOperation with id " + op.getOperationID()
				+ " succeeded");
	}

	// ----------------------------------------------------------------------
	//
	// ----------------------------------------------------------------------

	/**
	 * This method tries to solve the provided query with the knowledge of the
	 * attribute-entries, that are stored in the <code>AttributeStorage</code>
	 * for the Support Peer.
	 * 
	 * @param query
	 *            contains the query, whose clauses are delivered to the
	 *            <code>AttributeStorage</code> for resolution
	 * @return the query, that includes the matches from the
	 *         <code>AttributeStorage</code> of this SkyNet-node as Support Peer
	 */
	public Query localLookupAtAttributeStorage(Query query) {
		for (int i = 0; i < query.getNumberOfAddends(); i++) {
			QueryAddend ad = storage.processQueryAddendOfSP(query
					.removeAddend(i), query.getQueryOriginator());
			query.insertAddend(ad, i);
			if (ad.getSearchedElements() == 0) {
				break;
			}
		}
		return query;
	}

	/**
	 * This method is responsible for resolving received queries, which are
	 * delivered by the <code>SkyNetMessageHandler</code>. Within this method,
	 * <code>QueryHandler</code> tries to solve the received query with its
	 * knowledge. Depending of the outcome of this resolution, the query is
	 * forwarded to the next node in the over-overlay or sent back to the
	 * originator, if the query is solved or the root of the tree is reached.
	 * 
	 * @param request
	 *            contains the received message, which includes the forwarded
	 *            query
	 */
	public void processForeignQuery(QueryForwardMsg request) {
		Query actualQuery = localLookupAtAttributeStorage(request.getQuery());
		actualQuery.incrementHops();
		// check if the query can be solved with the local knowledge.
		// Otherwise transmit the query
		if (actualQuery.getIndexOfSolvedAddend() != -1) {
			SkyNetNodeInfo solutionReceiver = actualQuery.getQueryOriginator();
			log.info(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ " could solve the rest of the query."
					+ " Sends it back to originator "
					+ SkyNetUtilities.getNetID(solutionReceiver));
			sendQuery(supportPeer.getSkyNetNodeInfo(), solutionReceiver,
					actualQuery, true, false, true);
		} else if (supportPeer.getSPAttributeUpdateStrategy()
				.getBrotherCoordinator().getLevel() > 0) {
			SkyNetNodeInfo parentCo = supportPeer
					.getSPAttributeUpdateStrategy().getParentCoordinator();
			if (parentCo != null) {
				log.info(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ " could not solve the rest of the query."
						+ " Sends it to ParentCoordinator "
						+ SkyNetUtilities.getNetID(parentCo));
				sendQuery(supportPeer.getSkyNetNodeInfo(), parentCo,
						actualQuery, false, false, true);
			} else {
				log.error(SkyNetUtilities.getTimeAndNetID(supportPeer)
						+ "Cannot send query"
						+ ", since no parentCoordinator is known");
			}
		} else {
			SkyNetNodeInfo solutionReceiver = actualQuery.getQueryOriginator();
			log.info(SkyNetUtilities.getTimeAndNetID(supportPeer)
					+ "as SP of the root,"
					+ " could not solve the rest of the query."
					+ " So sends it back to originator "
					+ SkyNetUtilities.getNetID(solutionReceiver));
			sendQuery(supportPeer.getSkyNetNodeInfo(), solutionReceiver,
					actualQuery, true, false, true);
		}

	}

}
