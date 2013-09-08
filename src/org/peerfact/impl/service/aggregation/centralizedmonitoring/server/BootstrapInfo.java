package org.peerfact.impl.service.aggregation.centralizedmonitoring.server;

import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.transport.TransInfo;


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
public class BootstrapInfo {

	private static final List<TransInfo> server = new LinkedList<TransInfo>();

	private static Integer lastServer = 0;

	/**
	 * Returns Server for Client
	 * 
	 * @return TransInfo of Server
	 */
	public static TransInfo getServer() {
		return server.get(lastServer++ % server.size());
	}

	public static void addServer(TransInfo transInfo) {
		server.add(transInfo);
	}

	public static void delServer(TransInfo transInfo) {
		server.remove(transInfo);
	}

}
