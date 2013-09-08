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

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.impl.analyzer.csvevaluation.DefaultGnuplotAnalyzer;
import org.peerfact.impl.analyzer.csvevaluation.metrics.QuerySuccessAndNHops;
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
public class KBRGnuplotAnalyzer2 extends DefaultGnuplotAnalyzer implements
		KBROverlayAnalyzer {

	// AverageResponseTime avgRespTime = new AverageResponseTime();
	QuerySuccessAndNHops qMetrics = new QuerySuccessAndNHops();

	Map<Message, Long> openQueries = new LinkedHashMap<Message, Long>();

	@Override
	protected void declareMetrics() {
		super.declareMetrics();
		// addMetric(avgRespTime);
		addMetric(qMetrics.getAvgRespTime());
		addMetric(qMetrics.getQuerySuccess());
		addMetric(qMetrics.getQueryNHops());
	}

	@Override
	public void overlayMessageDelivered(OverlayContact<?> contact, Message msg,
			int hops) {
		log.debug("Message delivered " + msg.getPayload());

		qMetrics.querySucceeded(msg.getPayload(), 0);
		msgsDelivered++;
	}

	@Override
	public void overlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int hops) {
		log.debug("Message forwarded " + msg.getPayload());

		qMetrics.addHopToQuery(msg.getPayload());
		msgsForwarded++;
	}

	@Override
	public void queryFailed(OverlayContact<?> failedHop, Message appMsg) {
		queriesFailed++;
	}

	@Override
	public void queryStarted(OverlayContact<?> contact, Message appMsg) {
		log.debug("Query started" + appMsg);
		qMetrics.queryStarted(appMsg, Simulator.getCurrentTime());
		queriesStarted++;
	}

	int queriesStarted = 0;

	int msgsForwarded = 0;

	int msgsDelivered = 0;

	int queriesFailed = 0;

	@Override
	public void stop(Writer w) {
		super.stop(w);
		log.debug("Q's started: " + queriesStarted);
		log.debug("Q's failed: " + queriesFailed);
		log.debug("Msgs forwarded " + msgsForwarded);
		log.debug("Msgs delivered " + msgsDelivered);
	}

}
