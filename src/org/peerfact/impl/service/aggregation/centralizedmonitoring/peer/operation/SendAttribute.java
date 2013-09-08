package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.operation;

import java.util.Collection;

import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonInfoMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.CMonPeerOverlayNode;
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
 */
public class SendAttribute<ID extends Object> extends
		AbstractPeerOperation<ID, CMonPeerOverlayNode<ID>, Boolean> implements
		TransMessageCallback {

	private Collection<ID> identifiers;

	private boolean success;

	protected SendAttribute(CMonPeerOverlayNode<ID> component) {
		super(component);
	}

	@Override
	protected void doExecute() {

		CMonInfoMessage<ID> infos = new CMonInfoMessage<ID>(this.getComponent()
				.getAttributeManager().getNew());
		infos.setDummyMetricsCounter(getComponent().getDummyMetricsCounter());
		log.debug(Simulator.getSimulatedRealtime() + " - "
				+ getComponent().getHost().getNetLayer().getNetID() + " sends "
				+ (infos.getInfos().size() + infos.getDummyMetricsCounter())
				+ " infos to the server with size " + infos.getSize());
		this.getComponent()
				.getHost()
				.getTransLayer()
				.sendAndWait(infos, this.getComponent().getServer(),
						this.getComponent().getPort(), TransProtocol.UDP, this,
						Configuration.ACK_TIMEOUT);
		this.identifiers = infos.getIdentifiers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getResult() {
		return this.success ? this.success : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageTimeoutOccured(int commId) {
		this.getComponent().getAttributeManager().reset(this.identifiers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		this.operationFinished(true);
	}

}
