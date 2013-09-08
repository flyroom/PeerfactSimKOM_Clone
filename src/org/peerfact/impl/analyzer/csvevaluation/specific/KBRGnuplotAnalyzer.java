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

package org.peerfact.impl.analyzer.csvevaluation.specific;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.impl.analyzer.csvevaluation.DefaultGnuplotAnalyzer;
import org.peerfact.impl.analyzer.csvevaluation.metrics.AverageResponseTime;
import org.peerfact.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops;
import org.peerfact.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops.QueryTimeoutListener;
import org.peerfact.impl.application.kbrapplication.KBRDummyApplication;
import org.peerfact.impl.application.kbrapplication.messages.QueryForDocumentMessage;
import org.peerfact.impl.application.kbrapplication.operations.QueryForDocumentOperation;
import org.peerfact.impl.application.kbrapplication.operations.RequestDocumentOperation;
import org.peerfact.impl.overlay.kbr.messages.KBRForwardMsg;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class KBRGnuplotAnalyzer extends DefaultGnuplotAnalyzer implements
		OperationAnalyzer, QueryTimeoutListener,
		KBROverlayAnalyzer {

	AverageResponseTime avgRespTime = new AverageResponseTime();

	QuerySuccessAndNHops qMetrics = new QuerySuccessAndNHops();

	Map<Query, Long> openQueries = new LinkedHashMap<Query, Long>();

	int queriesFailed = 0;

	int queriesSucceeded = 0;

	int queriesInitiated = 0;

	@Override
	protected void declareMetrics() {
		super.declareMetrics();
		addMetric(avgRespTime);
		addMetric(qMetrics.getQuerySuccess());
		addMetric(qMetrics.getQueryNHops());
		qMetrics.addListener(this);
	}

	protected QuerySuccessAndNHops getQMetrics() {
		return qMetrics;
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		this.checkTimeProgress();

		if (op instanceof QueryForDocumentOperation) {
			OverlayID<?> reqNode = ((KBRDummyApplication) op.getComponent())
					.getNode().getLocalOverlayContact().getOverlayID();
			OverlayKey<?> reqKey = ((QueryForDocumentOperation) op)
					.getKeyQueriedFor();
			Query q = new Query(reqNode, reqKey);
			long currentTime = Simulator.getCurrentTime();
			openQueries.put(q, currentTime);
			qMetrics.queryStarted(q, currentTime);
			queriesInitiated++;

		} else if (op instanceof RequestDocumentOperation) {
			OverlayID<?> reqNode = ((KBRDummyApplication) op.getComponent())
					.getNode().getLocalOverlayContact().getOverlayID();
			OverlayKey<?> reqKey = ((RequestDocumentOperation) op)
					.getKeyOfDocument();

			Query query2lookup = new Query(reqNode, reqKey);

			if (openQueries.containsKey(query2lookup)) {
				long startTime = openQueries.get(query2lookup);
				long endTime = Simulator.getCurrentTime();
				avgRespTime.gotResponse(endTime - startTime);
				openQueries.remove(query2lookup);
				qMetrics.querySucceeded(query2lookup, 0);
				queriesSucceeded++;
			}
		}
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		super.netMsgReceive(msg, id);

		Message olMsg = msg.getPayload().getPayload();

		/*
		 * if (olMsg instanceof LookupRequest) { LookupRequest req =
		 * (LookupRequest)olMsg; OverlayKey key = req.getKey(); OverlayID
		 * initiator = req.getStarter().getOverlayID();
		 * 
		 * Query q = new Query(initiator, key);
		 * 
		 * if (!this.getQMetrics().addHopToQuery(q)) {
		 * 
		 * if (Simulator.getCurrentTime() > 12000000000l)
		 * //this.getQMetrics().printDbg(); } else }
		 */

		if (olMsg instanceof KBRForwardMsg) {
			KBRForwardMsg<?, ?> req = (KBRForwardMsg<?, ?>) olMsg;
			OverlayKey<?> key = req.getKey();

			if (req.getPayload() instanceof QueryForDocumentMessage) {
				QueryForDocumentMessage qmsg = (QueryForDocumentMessage) req
						.getPayload();
				OverlayID<?> initiator = qmsg.getSenderContact().getOverlayID();

				Query q = new Query(initiator, key);

				if (!this.getQMetrics().addHopToQuery(q)) {
					log.debug("Query was not initiated, but handled through messages: "
							+ q);

				}

			} /*
			 * else if (req.getPayload() instanceof AnnounceNewDocumentMessage
			 * && INCLUDE_ANNOUNCEMENTS) { AnnounceNewDocumentMessage amsg =
			 * (AnnounceNewDocumentMessage)req.getPayload(); OverlayID initiator
			 * = amsg.getSenderContact().getOverlayID();
			 * 
			 * Query q = new Query(initiator, key);
			 * 
			 * if (!this.getQMetrics().addHopToQuery(q)) {
			 * 
			 * log.debug("Announce Query was not found for " + q);
			 * //this.getQMetrics().printDbg(); } else
			 * log.debug("Announce Query was found for: " + q);
			 */
		}
	}

	public static class Query {

		public Query(OverlayID<?> queryingNode, OverlayKey<?> keyQueried) {
			super();
			this.queryingNode = queryingNode;
			this.keyQueried = keyQueried;
		}

		OverlayID<?> queryingNode;

		OverlayKey<?> keyQueried;

		@Override
		public int hashCode() {
			return queryingNode.hashCode() + keyQueried.hashCode() * 95651;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Query)) {
				return false;
			}
			Query other = (Query) o;
			return queryingNode.equals(other.queryingNode)
					&& keyQueried.equals(other.keyQueried);
		}

		@Override
		public String toString() {
			String qnodeStr = String.valueOf(queryingNode);
			String keyQueriedStr = String.valueOf(keyQueried);
			return "Q from "
					+ ((qnodeStr.length() > 10) ? qnodeStr.substring(0, 10)
							+ "..." : qnodeStr)
					+ " for "
					+ ((keyQueriedStr.length() > 10) ? qnodeStr
							.substring(0, 10)
							+ "..." : keyQueriedStr);
		}
	}

	@Override
	public void operationFinished(Operation<?> op) {
		// Right now there is nothing to do here.
	}

	@Override
	public void queryTimeouted(Object queryIdentifier) {
		openQueries.remove(queryIdentifier);
		this.queriesFailed++;
	}

	@Override
	public void stop(Writer w) {
		super.stop(w);
		try {
			w.write("=========KBR Query Summary Report=========");

			w.write("Total Queries failed: " + this.queriesFailed
					+ ", succeeded: " + this.queriesSucceeded + ", initiated: "
					+ this.queriesInitiated);
			w.write("==========================================");
		} catch (IOException e) { // No output
		}
		log.debug("=========KBR Query Summary Report=========");
		log.debug("Total Queries failed: " + this.queriesFailed
				+ ", succeeded: " + this.queriesSucceeded + ", initiated: "
				+ this.queriesInitiated);
		log.debug("==========================================");
	}

	@Override
	public void overlayMessageDelivered(OverlayContact<?> contact, Message msg,
			int hops) {
		qMetrics.querySucceeded(msg, 0);
	}

	@Override
	public void overlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int hops) {
		qMetrics.addHopToQuery(msg);
	}

	@Override
	public void queryFailed(OverlayContact<?> failedHop, Message appMsg) {
		// Timeout...
	}

	@Override
	public void queryStarted(OverlayContact<?> contact, Message appMsg) {
		qMetrics.queryStarted(appMsg, Simulator.getCurrentTime());
	}

}
