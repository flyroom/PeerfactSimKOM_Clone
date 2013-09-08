package org.peerfact.impl.service.aggregation.centralizedmonitoring;

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
public class Configuration {
	public static final short SERVER_PORT = 4242;

	public static final short PEER_PORT = 4242;

	// TODO: Improvement: implement adaptive timeout (RFC1122)
	public static final long ACK_TIMEOUT = 3 * Simulator.SECOND_UNIT;

	public static final long REQUEST_TIMEOUT = 3 * Simulator.SECOND_UNIT;

	public static final int RESEND_THRESHOLD = 3;

	public static final long PUSH_DELAY = 60 * Simulator.SECOND_UNIT;

	public static final long SERVER_REFRESH_DELAY = 60 * Simulator.SECOND_UNIT;

	public static final long SERVER_SEND_INTERVAL = 60 * Simulator.SECOND_UNIT;
}
