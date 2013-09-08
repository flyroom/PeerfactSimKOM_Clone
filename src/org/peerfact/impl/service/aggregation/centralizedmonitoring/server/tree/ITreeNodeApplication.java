package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import org.peerfact.api.application.Application;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;

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
public interface ITreeNodeApplication extends Application,
		TransMessageListener {
	public void setParent(TransInfo transInfo);

	public void setNumberOfChildren(double numberOfChildren);
}
