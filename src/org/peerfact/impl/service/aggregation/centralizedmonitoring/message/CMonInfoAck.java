package org.peerfact.impl.service.aggregation.centralizedmonitoring.message;

import org.peerfact.api.common.Message;
import org.peerfact.impl.transport.AbstractTransMessage;

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
 * Acknowledge of received Info Message
 * 
 * @author Alexander Nigl
 * 
 */
public class CMonInfoAck extends AbstractTransMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5234698466837511677L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getPayload() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getSize() {
		// calculate the size for AbstractTransMessage
		return (Integer.SIZE + 2 * Short.SIZE) / 8;
	}

}
