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

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetUtilities;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.QueryAnalyzer;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeStorage;
import org.peerfact.impl.service.aggregation.skyeye.queries.messages.QueryForwardMsg;
import org.peerfact.impl.service.aggregation.skyeye.queries.operations.QueryForwardOperation;
import org.peerfact.impl.service.aggregation.skyeye.queries.operations.QueryTransmissionOperation;
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
 * for the handling of the queries. This treatment of the queries comprises the
 * injection of a new query into the over-overlay as well as the handling and
 * resolution of foreign queries, that arrive at a SkyNet-node. For this
 * reasons, <code>QueryHandler</code> consists of two methods, which start two
 * operations in terms of the transmission of queries. While
 * <code>startQuery()</code> injects the query into the over-overlay and starts
 * a {@link QueryTransmissionOperation}, <code>sendQuery()</code> forwards a
 * query from one node to another by initiating a {@link QueryForwardOperation}.<br>
 * As a SkyNet-node may receive a forwarded Query as Coordinator or Support
 * Peer, there also exists a query-handler, which handles the queries, that a
 * SkyNet-node receives in its role as Support Peer.<br>
 * <code>QueryHandler</code> is responsible for receiving forwarded queries as
 * Coordinator, while {@link SPQueryHandler} does the same for the SkyNet-node
 * in its role as Support Peer
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class QueryHandler {

	private static Logger log = SimLogger.getLogger(QueryHandler.class);

	private SkyNetNodeInterface skyNetNode;

	private AttributeStorage storage;

	private LinkedHashMap<Integer, QueryTransmissionOperation> openQueries;

	private long queryTransmissionOpTimeout;

	private long queryForwardOpTimeout;

	public QueryHandler(SkyNetNodeInterface skyNetNode, AttributeStorage storage) {
		this.skyNetNode = skyNetNode;
		this.storage = storage;
		openQueries = new LinkedHashMap<Integer, QueryTransmissionOperation>();

		// set the Properties from skynet.properties
		SkyNetPropertiesReader propReader = SkyNetPropertiesReader
				.getInstance();
		queryTransmissionOpTimeout = propReader
				.getTimeProperty("QueryTransmissionOperationTimeout");
		queryForwardOpTimeout = propReader
				.getTimeProperty("QueryForwardOperationTimeForAck");
	}

	// ----------------------------------------------------------------------
	// methods for creating a queryTransmissionOperation
	// ----------------------------------------------------------------------

	/**
	 * This method starts a <code>QueryTransmissionOperation</code>, which
	 * comprises the injection of a query in the over-overlay and terminates
	 * with the reception of the solved or unsolved query or with a timeout, if
	 * the query got lost in the over-overlay.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param query
	 *            contains the query
	 * @param receiverSP
	 *            specifies, if the receiver is a Support Peer
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	private int queryTransmissionOperation(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, Query query, boolean receiverSP,
			OperationCallback<Object> callback) {
		QueryTransmissionOperation op = new QueryTransmissionOperation(
				skyNetNode, senderInfo, receiverInfo, query, receiverSP,
				queryTransmissionOpTimeout, callback);
		op.scheduleImmediately();
		openQueries.put(query.getQueryID(), op);
		return op.getOperationID();
	}

	/**
	 * This method is called by the other components of the SkyNet-node to start
	 * the operation for the resolution of a query. Within this method,
	 * <code>queryTransmissionOperation()</code> is called to start the
	 * corresponding operation.
	 * 
	 * @param senderInfo
	 *            contains the ID of the sender
	 * @param receiverInfo
	 *            contains the ID of the receiver
	 * @param query
	 *            contains the query
	 * @param receiverSP
	 *            specifies, if the receiver is a Support Peer
	 */
	public void startQuery(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, Query query, boolean receiverSP) {
		final Query q = query;
		queryTransmissionOperation(senderInfo, receiverInfo, query, receiverSP,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						queryTransmissionOperationFailed(op, q);
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						queryTransmissionOperationSucceeded(op);
					}

				});
	}

	void queryTransmissionOperationFailed(Operation<Object> op, Query query) {
		QueryTransmissionOperation queryOP = openQueries.remove(query
				.getQueryID());
		if (queryOP == null) {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "found no corresping operation to the query-ID "
					+ query.getQueryID());
		} else {
			QueryAnalyzer.getInstance().queryLost(query);
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "QueryOperation with ID " + op.getOperationID()
					+ " failed");
		}
	}

	void queryTransmissionOperationSucceeded(Operation<Object> op) {
		log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "QueryOperation with ID " + op.getOperationID()
				+ " succeeded");
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
	 * @param msgID
	 *            specifies the ID of the message
	 * @param receiverSP
	 *            specifies, if the receiver of the query is a Support Peer
	 * @param callback
	 *            contains the object, which receives the corresponding ACK
	 * @return the ID of the executed operation
	 */
	private int queryForwardOperation(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, Query query, boolean isSolved,
			boolean receiverSP, OperationCallback<Object> callback) {
		QueryForwardOperation op = new QueryForwardOperation(skyNetNode,
				senderInfo, receiverInfo, query, isSolved, skyNetNode
						.getMessageCounter().assignmentOfMessageNumber(),
				receiverSP, false, queryForwardOpTimeout, callback);
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
	 * @param msgID
	 *            specifies the ID of the message
	 * @param receiverSP
	 *            specifies, if the receiver of the query is a Support Peer
	 */
	public void sendQuery(SkyNetNodeInfo senderInfo,
			SkyNetNodeInfo receiverInfo, Query query, boolean isSolved,
			long msgID, boolean receiverSP) {
		final boolean solved = isSolved;
		queryForwardOperation(senderInfo.clone(), receiverInfo.clone(), query,
				isSolved, receiverSP, new OperationCallback<Object>() {

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
		if (!solved) {
			if (msg.isReceiverSP()) {
				log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "----NO CHANCE TO SEND QUERY "
						+ msg.getQuery().getQueryID() + " TO SUPPORTPEER "
						+ SkyNetUtilities.getNetID(msg.getReceiverNodeInfo())
						+ "----");
				if (!skyNetNode.getTreeHandler().isRoot()) {
					sendQuery(skyNetNode.getSkyNetNodeInfo(), skyNetNode
							.getTreeHandler().getParentCoordinator(), msg
							.getQuery(), msg.isSolved(), msg.getSkyNetMsgID(),
							false);
				} else {
					SkyNetNodeInfo solutionReceiver = msg.getQuery()
							.getQueryOriginator();
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "as root could not solve the rest of the query."
							+ " So sends it back to originator "
							+ SkyNetUtilities.getNetID(solutionReceiver));
					sendQuery(skyNetNode.getSkyNetNodeInfo(), solutionReceiver,
							msg.getQuery(), true, msg.getSkyNetMsgID(), false);
				}
			} else {
				log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "----NO CHANCE TO SEND QUERY "
						+ msg.getQuery().getQueryID()
						+ " TO PARENTCOORDINATOR "
						+ SkyNetUtilities.getNetID(msg.getReceiverNodeInfo())
						+ "----");
			}
		} else {
			log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ "----NO CHANCE TO SEND SOLVED QUERY "
					+ msg.getQuery().getQueryID() + " TO ORIGINATOR "
					+ SkyNetUtilities.getNetID(msg.getReceiverNodeInfo())
					+ "----");
		}
	}

	void queryForwardOperationSucceeded(Operation<Object> op) {
		log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
				+ "QueryForwardOperation with id " + op.getOperationID()
				+ " succeeded");
	}

	// ----------------------------------------------------------------------
	//
	// ----------------------------------------------------------------------

	/**
	 * This method tries to solve the provided query with the knowledge of the
	 * attribute-entries, that are stored in the <code>AttributeStorage</code>
	 * for the Coordinator.
	 * 
	 * @param query
	 *            contains the query, whose clauses are delivered to the
	 *            <code>AttributeStorage</code> for resolution
	 * @return the query, that includes the matches from the
	 *         <code>AttributeStorage</code> of this SkyNet-node as Coordinator
	 */
	public Query localLookupAtAttributeStorage(Query query) {
		for (int i = 0; i < query.getNumberOfAddends(); i++) {
			QueryAddend ad = storage.processQueryAddendOfCo(query
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
			log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ " could solve the rest of the query."
					+ " Sends it back to originator "
					+ SkyNetUtilities.getNetID(solutionReceiver));
			sendQuery(skyNetNode.getSkyNetNodeInfo(), solutionReceiver,
					actualQuery, true, -1, false);
		} else {
			// check first if a SupportPeer is in use, which could probably
			// resolve the query. If no SupportPeer is in use, send the query to
			// the ParentCoordinator
			if (skyNetNode.getAttributeInputStrategy().isSupportPeerInUse()) {
				SkyNetNodeInfo supportPeer = skyNetNode
						.getAttributeInputStrategy().getSpHandler()
						.getActiveSupportPeer().getNodeInfo();
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ " could not solve the rest of the query."
						+ " Sends it to SupportPeer "
						+ SkyNetUtilities.getNetID(supportPeer));
				sendQuery(skyNetNode.getSkyNetNodeInfo(), supportPeer,
						actualQuery, false, -1, true);
			} else if (!skyNetNode.getTreeHandler().isRoot()) {
				SkyNetNodeInfo parentCo = skyNetNode.getTreeHandler()
						.getParentCoordinator();
				if (parentCo != null) {
					log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ " could not solve the rest of the query."
							+ " Sends it to ParentCoordinator "
							+ SkyNetUtilities.getNetID(parentCo));
					sendQuery(skyNetNode.getSkyNetNodeInfo(), parentCo,
							actualQuery, false, -1, false);
				} else {
					log.error(SkyNetUtilities.getTimeAndNetID(skyNetNode)
							+ "Cannot send query "
							+ actualQuery.getQueryID()
							+ " from originator "
							+ SkyNetUtilities.getNetID(actualQuery
									.getQueryOriginator())
							+ ", since no parentCoordinator is known");
				}
			} else {
				SkyNetNodeInfo solutionReceiver = actualQuery
						.getQueryOriginator();
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "as root could not solve the rest of the query."
						+ " So sends it back to originator "
						+ SkyNetUtilities.getNetID(solutionReceiver));
				sendQuery(skyNetNode.getSkyNetNodeInfo(), solutionReceiver,
						actualQuery, true, -1, false);
			}
		}
	}

	/**
	 * This method is executed, if the originator of a query receives the solved
	 * or unsolved query. First of all the
	 * <code>QueryTransmissionOperation</code> is terminated, while afterwards
	 * the received query is processed.
	 * 
	 * @param request
	 *            contains the message, that includes the solved or unsolved
	 *            query
	 */
	public void processQueryResult(QueryForwardMsg request) {
		Query query = request.getQuery();
		QueryTransmissionOperation op = openQueries.remove(query.getQueryID());
		if (op != null) {
			op.setFinishOfOperation(true);
			if (query.getIndexOfSolvedAddend() != -1) {
				QueryAnalyzer.getInstance().solvedQueryReceived(query);
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "received solved query " + query.getQueryID()
						+ " of peer "
						+ SkyNetUtilities.getNetID(request.getSenderNodeInfo())
						+ "with msgID " + request.getSkyNetMsgID());
			} else {
				QueryAnalyzer.getInstance().unsolvedQueryReceived(query);
				log.debug(SkyNetUtilities.getTimeAndNetID(skyNetNode)
						+ "received unsolved query " + query.getQueryID()
						+ " of peer "
						+ SkyNetUtilities.getNetID(request.getSenderNodeInfo())
						+ "with msgID " + request.getSkyNetMsgID());
			}
		} else {
			log.warn(SkyNetUtilities.getTimeAndNetID(skyNetNode)
					+ " already received answer for the query "
					+ query.getQueryID() + " of peer "
					+ SkyNetUtilities.getNetID(request.getSenderNodeInfo())
					+ "with msgID " + request.getSkyNetMsgID());
		}
	}
}
