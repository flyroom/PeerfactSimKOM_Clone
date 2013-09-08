package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import java.util.LinkedHashMap;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonResultMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;


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
public class RootNodeApplication<ID extends Object> extends
		AbstractInnerNodeApplication<ID> {

	public RootNodeApplication(Host host) {
		super(host);
	}

	@Override
	public void setParent(TransInfo parent) {
		// Do Nothing
	}

	@Override
	public int close(OperationCallback<?> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int start(OperationCallback<?> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Sends update to all children
	 */
	@Override
	protected void sendUpdate(LinkedHashMap<ID, Aggregate<ID>> update) {
		for (TransInfo child : this.status.keySet()) {
			this.getHost().getTransLayer().send(
					new CMonResultMessage<ID>(update), child,
					Configuration.SERVER_PORT, TransProtocol.UDP);
		}
	}

}
