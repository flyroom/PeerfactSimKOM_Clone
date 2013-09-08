package org.peerfact.impl.service.aggregation.centralizedmonitoring.analyzer;

import java.io.Writer;
import java.util.LinkedHashMap;

import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;


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
public class CMonNetAnalyzer implements NetAnalyzer {

	protected LinkedHashMap<NetID, Long> sentMsg;

	protected LinkedHashMap<NetID, Long> receivedMsg;

	protected LinkedHashMap<NetID, Long> droppedMsg;

	public CMonNetAnalyzer() {
		this.sentMsg = new LinkedHashMap<NetID, Long>();
		this.receivedMsg = new LinkedHashMap<NetID, Long>();
		this.droppedMsg = new LinkedHashMap<NetID, Long>();
	}

	@Override
	public void start() {
		// nothing to do
	}

	@Override
	public void stop(Writer output) {
		// nothing to do
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		Long c = this.sentMsg.get(id);
		this.sentMsg.put(id, c == null ? 1 : c + 1);
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		Long c = this.receivedMsg.get(id);
		this.receivedMsg.put(id, c == null ? 1 : c + 1);
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		Long c = this.droppedMsg.get(id);
		this.droppedMsg.put(id, c == null ? 1 : c + 1);
	}

	public void reset() {
		this.sentMsg = new LinkedHashMap<NetID, Long>();
		this.receivedMsg = new LinkedHashMap<NetID, Long>();
		this.droppedMsg = new LinkedHashMap<NetID, Long>();
	}

	public Long getSentMsg(NetID id) {
		return this.sentMsg.containsKey(id) ? this.sentMsg.remove(id) : 0;
	}

	public Long getReceivedMsg(NetID id) {
		return this.receivedMsg.containsKey(id) ? this.receivedMsg.remove(id)
				: 0;
	}

	public Long getDroppedMsg(NetID id) {
		return this.droppedMsg.containsKey(id) ? this.droppedMsg.remove(id) : 0;
	}

}
