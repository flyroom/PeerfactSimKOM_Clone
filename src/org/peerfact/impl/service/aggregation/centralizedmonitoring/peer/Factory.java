package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.AttributeIdentifier;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.analyzer.CMonNetAnalyzer;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.BootstrapInfo;

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
public class Factory implements ComponentFactory {

	private int i = 0;

	private long delay;

	private CMonNetAnalyzer netAnalyzer;

	/**
	 * Set Aggregation Delay
	 * 
	 * @param delay
	 *            of aggregation cycle
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	public Factory() {
		this.netAnalyzer = new CMonNetAnalyzer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component createComponent(Host host) {
		CMonPeerOverlayNode<AttributeIdentifier> peer = new CMonPeerOverlayNode<AttributeIdentifier>(
				new DefaultOverlayID(this.i++), Configuration.PEER_PORT);
		peer.setDelay(this.delay);
		peer.setServer(BootstrapInfo.getServer());
		peer.setAttributeFactory(new RealAttributeFactory(host,
				this.netAnalyzer));
		host.getTransLayer().addTransMsgListener(peer, Configuration.PEER_PORT);
		return peer;
	}

}
