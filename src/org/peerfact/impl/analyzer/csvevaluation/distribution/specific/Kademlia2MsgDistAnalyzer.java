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

package org.peerfact.impl.analyzer.csvevaluation.distribution.specific;

import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.impl.analyzer.csvevaluation.distribution.MessageCategory;
import org.peerfact.impl.analyzer.csvevaluation.distribution.SpecificMsgDistAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.DataMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KClosestNodesLookupMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.NodeListMsg;

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
public class Kademlia2MsgDistAnalyzer extends SpecificMsgDistAnalyzer {

	/*
	 * Chord-Messages:
	 * 
	 * class org.peerfact.impl.overlay.dht.chord.messages.StabilizeMsg
	 * class org.peerfact.impl.overlay.dht.chord.messages.LookupReply class
	 * org.peerfact.impl.overlay.dht.chord.messages.PongMsg class
	 * org.peerfact.impl.overlay.dht.chord.messages.JoinMsg class
	 * org.peerfact.impl.overlay.dht.chord.messages.GetInfoReply class
	 * org.peerfact.impl.overlay.dht.chord.messages.PingMsg class
	 * org.peerfact.impl.overlay.dht.chord.messages.LookupRequest class
	 * org.peerfact.impl.overlay.dht.ForwardMsg class
	 * org.peerfact.impl.overlay.dht.chord.messages.GetInfoRequest class
	 * org.peerfact.impl.overlay.dht.chord.messages.NotifyMsg
	 * 
	 * join, leave, maintenance, userMsg, result, other
	 */

	@Override
	protected MessageCategory getMessageCategory(Message overlayMsg, NetID id) {

		if (overlayMsg instanceof DataMsg) {
			return MessageCategory.userMsg;
		}
		if (overlayMsg instanceof DataLookupMsg) {
			return MessageCategory.userMsg;
		}

		if (overlayMsg instanceof KClosestNodesLookupMsg) {
			return MessageCategory.maintenance;
		}
		if (overlayMsg instanceof NodeListMsg) {
			return MessageCategory.maintenance;
		} else {
			return MessageCategory.other;
		}
	}

}
