package org.peerfact.impl.service.aggregation.centralizedmonitoring.analyzer;

import org.peerfact.impl.analyzer.csvevaluation.DefaultGnuplotAnalyzer;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class CMonAnalyzer extends DefaultGnuplotAnalyzer {

	protected OnlinePeers peers = new OnlinePeers();

	@Override
	protected void declareMetrics() {
		super.declareMetrics();
		this.addMetric(this.peers);
	}

}
