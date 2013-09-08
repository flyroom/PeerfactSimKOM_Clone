/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.peerfact.impl.analyzer.csvevaluation.distribution;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;


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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MsgsRcvDistAnalyzer extends AbstractGnuplotDistAnalyzer implements
		NetAnalyzer, OperationAnalyzer, ConnectivityAnalyzer {

	Map<NetID, Integer> msgs = new LinkedHashMap<NetID, Integer>();

	@Override
	protected void resetDistributions() {
		msgs = new LinkedHashMap<NetID, Integer>(); // Reset message count at
													// each interval.
		super.resetDistributions();
	}

	@Override
	protected String modifyResultValue(long result) {
		return NumberFormatToolkit
				.floorToDecimalsString(result / (double) this.getInterval()
						* this.TIME_UNIT_OUTPUT, 3); // Messages per sec.
	}

	@Override
	public void operationFinished(Operation<?> op) {
		hostSeen(op.getComponent().getHost().getNetLayer().getNetID());
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		hostSeen(op.getComponent().getHost().getNetLayer().getNetID());
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		hostSeen(id);
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		hostGotMsg(id, msg);
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		hostSeen(id);
	}

	protected void hostSeen(NetID id) {
		this.checkTimeProgress();
		if (!msgs.containsKey(id)) {
			msgs.put(id, 0);
			this.addHostOrUpdateAll(id, new long[] { 0 });
		}
	}

	protected void hostGotMsg(NetID id, NetMessage msg) {
		this.checkTimeProgress();
		if (!msgs.containsKey(id)) {
			msgs.put(id, 1);
			this.addHostOrUpdateAll(id, new long[] { 1 });
		} else {
			int oldAmount = msgs.get(id);
			msgs.put(id, oldAmount + 1);
			this.updateHost(id, 0, oldAmount + 1);
		}
	}

	protected void hostLeft(NetID id) {
		this.checkTimeProgress();
		msgs.remove(id);
		this.removeHost(id);
	}

	@Override
	public void offlineEvent(Host host) {
		hostLeft(host.getNetLayer().getNetID());
	}

	@Override
	public void onlineEvent(Host host) {
		hostSeen(host.getNetLayer().getNetID());
	}

	@Override
	protected void declareDistributions() {
		this.addDistribution("msgs/sec");
	}

}
