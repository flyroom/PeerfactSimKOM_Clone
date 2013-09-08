package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import org.peerfact.api.common.Host;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;

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
public abstract class AbstractTreeApplication implements ITreeNodeApplication {

	protected Host host;

	protected TransInfo parent;

	protected double numberOfChildren;

	public AbstractTreeApplication(Host host) {
		this.host = host;
	}

	@Override
	public Host getHost() {
		return this.host;
	}

	@Override
	public void setParent(TransInfo parent) {
		this.parent = parent;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public void setNumberOfChildren(double numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	public void init() {
		this.host.getTransLayer().addTransMsgListener(this,
				Configuration.SERVER_PORT);
	}

}
