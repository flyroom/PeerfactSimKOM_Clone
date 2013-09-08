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

package org.peerfact.impl.analyzer.csvevaluation;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.impl.application.kbrapplication.KBRDummyApplication;
import org.peerfact.impl.application.kbrapplication.operations.QueryForDocumentOperation;
import org.peerfact.impl.application.kbrapplication.operations.RequestDocumentOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;


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
public class LookupSuccessAnalyzer implements OperationAnalyzer {

	String ops = "";

	Map<Query, Long> openQueries = new LinkedHashMap<Query, Long>();

	int succeededLookups = 0;

	AverageAccumulator avgLookupTime = new AverageAccumulator();

	@Override
	public void operationFinished(Operation<?> op) {
		// TODO Auto-generated method stub

	}

	@Override
	public void operationInitiated(Operation<?> op) {

		if (op instanceof QueryForDocumentOperation) {
			KBRNode<?, ?, ?> reqNode = ((KBRDummyApplication) op.getComponent())
					.getNode();
			OverlayKey<?> reqKey = ((QueryForDocumentOperation) op)
					.getKeyQueriedFor();

			openQueries.put(new Query(reqNode, reqKey), Simulator
					.getCurrentTime());
		} else if (op instanceof RequestDocumentOperation) {
			KBRNode<?, ?, ?> reqNode = ((KBRDummyApplication) op.getComponent())
					.getNode();
			OverlayKey<?> reqKey = ((RequestDocumentOperation) op)
					.getKeyOfDocument();

			Query query2lookup = new Query(reqNode, reqKey);

			if (openQueries.containsKey(query2lookup)) {
				long startTime = openQueries.get(query2lookup);
				long endTime = Simulator.getCurrentTime();
				avgLookupTime.accumulate(endTime - startTime);
				succeededLookups++;
				openQueries.remove(query2lookup);
			}
		}

		if (op.getComponent() instanceof KBRDummyApplication) {

			NetID host = ((KBRDummyApplication) op.getComponent()).getHost()
					.getNetLayer().getNetID();

			ops += Simulator.getCurrentTime() + "|" + op + "|" + host + "\n";

		}
	}

	@Override
	public void start() {
		// Nothing to do
	}

	@Override
	public void stop(Writer output) {
		try {
			output.write("========KBR Lookup Success=============\n");
			output.write("Closed Lookups:			" + succeededLookups + "\n");
			output.write("Open Lookups:			" + openQueries.size() + "\n");
			output.write("Succeeded Quota:		"
					+ NumberFormatToolkit
							.formatPercentage(getSuccessQuota(), 1) + "\n");
			output.write("Average Lookup Time: 		"
					+ NumberFormatToolkit.formatSecondsFromSimTime(
							avgLookupTime.returnAverage(), 3) + "\n");
			// output.write(ops);
			output.write("=======================================\n");
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getSuccessQuota() {
		int numOpenQueries = openQueries.size();

		return (double) succeededLookups / (numOpenQueries + succeededLookups);
	}

	public static class Query {

		public Query(KBRNode<?, ?, ?> queryingNode, OverlayKey<?> keyQueried) {
			super();
			this.queryingNode = queryingNode;
			this.keyQueried = keyQueried;
		}

		KBRNode<?, ?, ?> queryingNode;

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
	}

}
