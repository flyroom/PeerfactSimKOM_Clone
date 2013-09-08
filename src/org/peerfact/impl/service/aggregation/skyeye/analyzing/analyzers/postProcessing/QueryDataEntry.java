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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing;

import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;

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
 * This class represents the information, which is de-serialized within
 * {@link QueryPostProcessor} and used for the <code>QueryMatrix.dat</code>
 * -file. <code>QueryDataEntry</code> contains the amount of queries, which are
 * originated at a certain level and solved at another level, as well as the
 * average complexity of queries, which are solved at a certain level in the
 * SkyNet-tree.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class QueryDataEntry {

	private int counter;

	private MetricsAggregate peerVariation;

	private MetricsAggregate conditionVarition;

	public QueryDataEntry() {
		this.counter = 0;
		this.peerVariation = new MetricsAggregate("emptyPeerVariation",
				0l);
		this.conditionVarition = new MetricsAggregate(
				"emptyConditionVariation", 0l);
	}

	public QueryDataEntry(int counter, MetricsAggregate variation, boolean peer) {
		this.counter = counter;
		if (peer) {
			peerVariation = variation;
			conditionVarition = new MetricsAggregate("emptyConditionVariation",
					0l);
		} else {
			peerVariation = new MetricsAggregate("emptyPeerVariation",
					0l);
			conditionVarition = variation;
		}

	}

	public QueryDataEntry(int counter) {
		this.counter = counter;
		this.peerVariation = new MetricsAggregate("emptyPeerVariation",
				0l);
		this.conditionVarition = new MetricsAggregate(
				"emptyConditionVariation", 0l);
	}

	/**
	 * This method returns the amount of queries, which were originated at a
	 * certain level and solved at another level in the SkyNet-tree.
	 * 
	 * @return the amount of queries for a certain level combination
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * This method sets the amount of queries, which were originated at a
	 * certain level and solved at another level in the SkyNet-tree.
	 * 
	 * @param counter
	 *            contains the amount of queries for a certain level combination
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * This method returns an aggregate of the complexity of the solved queries
	 * at a certain level. The method returns the complexity, which is attained
	 * by varying the amount of searched peers.
	 * 
	 * @return an aggregate of the complexity of the solved queries
	 */
	public MetricsAggregate getPeerVariation() {
		return peerVariation;
	}

	/**
	 * This method sets a new aggregate of the complexity of the solved queries
	 * at a certain level. The complexity is attained by varying the amount of
	 * searched peers.
	 * 
	 * @param peerVariation
	 *            contains an aggregate of the complexity of the solved queries
	 */
	public void setPeerVariation(MetricsAggregate peerVariation) {
		this.peerVariation = peerVariation;
	}

	/**
	 * This method returns an aggregate of the complexity of the solved queries
	 * at a certain level. The method returns the complexity, which is attained
	 * by varying the conditions of a query.
	 * 
	 * @return an aggregate of the complexity of the solved queries
	 */
	public MetricsAggregate getConditionVarition() {
		return conditionVarition;
	}

	/**
	 * This method sets a new aggregate of the complexity of the solved queries
	 * at a certain level. The complexity is attained by varying the conditions
	 * of a query.
	 * 
	 * @param conditionVarition
	 *            contains an aggregate of the complexity of the solved queries
	 */
	public void setConditionVarition(MetricsAggregate conditionVarition) {
		this.conditionVarition = conditionVarition;
	}

}
