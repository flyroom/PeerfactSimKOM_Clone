package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.operation;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonRequestMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonResultMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.CMonPeerOverlayNode;


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
public class RequestValues<ID extends Object>
		extends
		AbstractPeerOperation<ID, CMonPeerOverlayNode<ID>, LinkedHashMap<ID, Aggregate<ID>>>
		implements TransMessageCallback {

	Collection<ID> ids;

	LinkedHashMap<ID, Aggregate<ID>> result;

	public RequestValues(CMonPeerOverlayNode<ID> cMonPeerOverlayNode,
			Collection<ID> ids,
			OperationCallback<LinkedHashMap<ID, Aggregate<ID>>> callback) {
		super(cMonPeerOverlayNode, callback);
		this.ids = ids;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.getComponent().getHost().getTransLayer().sendAndWait(
				new CMonRequestMessage<ID>(this.ids),
				this.getComponent().getServer(), this.getComponent().getPort(),
				TransProtocol.TCP, this, Configuration.REQUEST_TIMEOUT);
	}

	@Override
	public LinkedHashMap<ID, Aggregate<ID>> getResult() {
		return this.result;
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		this.operationFinished(false);
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		if (msg instanceof CMonResultMessage) {
			this.result = ((CMonResultMessage<ID>) msg).getInfos();
			this.operationFinished(true);
		}
	}

}
