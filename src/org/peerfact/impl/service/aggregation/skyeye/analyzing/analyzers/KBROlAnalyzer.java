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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers;

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


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
 * This class keeps track of the important data of the KBR-Layer. The
 * SkyNet-nodes use these statistics to generate metrics that are then passed up
 * the SkyNet-Tree during metric collection.
 * 
 * Unlike the other existing analyzers, this one does not write out the global
 * view of this statistics for post processing. This was not needed so far. It
 * could be added at a later version.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 10.02.2009
 * 
 */
public class KBROlAnalyzer implements KBROverlayAnalyzer {

	public static class MessageHopTupel {
		private final Message msg;

		private final int hops;

		public MessageHopTupel(Message msg, int hops) {
			this.msg = msg;
			this.hops = hops;
		}

		public Message getMsg() {
			return msg;
		}

		public int getHops() {
			return hops;
		}
	}

	public static class QueryStat {
		private final Message msg;

		private final int hops;

		private final long duration;

		public QueryStat(Message msg, int hops, long duration) {
			this.msg = msg;
			this.hops = hops;
			this.duration = duration;
		}

		public Message getMsg() {
			return msg;
		}

		public int getHops() {
			return hops;
		}

		public long getDuration() {
			return duration;
		}
	}

	private static Logger log = SimLogger.getLogger(KBROlAnalyzer.class);

	private boolean runningAnalyzer;

	LinkedHashMap<OverlayID<?>, Vector<MessageHopTupel>> deltaDeliveredMsgs;

	LinkedHashMap<OverlayID<?>, Vector<MessageHopTupel>> deltaForwardedMsgs;

	LinkedHashMap<OverlayID<?>, Vector<Message>> deltaQueriesStarted;

	LinkedHashMap<OverlayID<?>, Vector<QueryStat>> deltaQueriesDelivered;

	LinkedHashMap<Message, Long> openQueries = new LinkedHashMap<Message, Long>();

	public KBROlAnalyzer() {
		runningAnalyzer = false;
	}

	@Override
	public void overlayMessageDelivered(OverlayContact<?> contact,
			Message olMsg,
			int hops) {
		log.debug("KBR Msg delivered");

		Message appMsg = olMsg.getPayload();
		if (runningAnalyzer) {
			if (openQueries.containsKey(appMsg)) {

				long queryStart = openQueries.remove(appMsg);
				long queryDuration = Simulator.getCurrentTime() - queryStart;

				Vector<QueryStat> v;
				if (deltaQueriesDelivered.containsKey(contact.getOverlayID())) {
					v = deltaQueriesDelivered.remove(contact.getOverlayID());
				} else {
					v = new Vector<QueryStat>();
				}
				v.add(new QueryStat(appMsg, hops, queryDuration));
				deltaQueriesDelivered.put(contact.getOverlayID(), v);
			}

			Vector<MessageHopTupel> v;
			if (deltaDeliveredMsgs.containsKey(contact.getOverlayID())) {
				v = deltaDeliveredMsgs.remove(contact.getOverlayID());
			} else {
				v = new Vector<MessageHopTupel>();
			}
			v.add(new MessageHopTupel(appMsg, hops));
			deltaDeliveredMsgs.put(contact.getOverlayID(), v);
		}
	}

	@Override
	public void overlayMessageForwarded(OverlayContact<?> sender,
			OverlayContact<?> receiver, Message msg, int hops) {
		log.debug("KBR Msg forwarded");
		if (runningAnalyzer) {
			Vector<MessageHopTupel> v;
			if (deltaForwardedMsgs.containsKey(sender.getOverlayID())) {
				v = deltaForwardedMsgs.remove(sender.getOverlayID());
			} else {
				v = new Vector<MessageHopTupel>();
			}
			v.add(new MessageHopTupel(msg, hops));
			deltaForwardedMsgs.put(sender.getOverlayID(), v);
		}
	}

	@Override
	public void queryStarted(OverlayContact<?> contact, Message appMsg) {
		log.debug("KBR Query started");
		if (runningAnalyzer) {
			openQueries.put(appMsg, Simulator.getCurrentTime());

			Vector<Message> v;
			if (deltaQueriesStarted.containsKey(contact.getOverlayID())) {
				v = deltaQueriesStarted.remove(contact.getOverlayID());
			} else {
				v = new Vector<Message>();
			}
			v.add(appMsg);
			deltaQueriesStarted.put(contact.getOverlayID(), v);
		}

	}

	@Override
	public void queryFailed(OverlayContact<?> failedHop, Message appMsg) {
		// Not considered yet
	}

	@Override
	public void start() {
		runningAnalyzer = true;
	}

	@Override
	public void stop(Writer output) {
		runningAnalyzer = false;
	}

	public void setSimulationSize(int size) {
		double capacity = Math.ceil(size / 0.75d);

		/**
		 * Initialize data structures
		 */
		deltaDeliveredMsgs = new LinkedHashMap<OverlayID<?>, Vector<MessageHopTupel>>(
				(int) capacity);
		deltaForwardedMsgs = new LinkedHashMap<OverlayID<?>, Vector<MessageHopTupel>>(
				(int) capacity);
		deltaQueriesStarted = new LinkedHashMap<OverlayID<?>, Vector<Message>>(
				(int) capacity);
		deltaQueriesDelivered = new LinkedHashMap<OverlayID<?>, Vector<QueryStat>>(
				(int) capacity);

	}

	public Vector<MessageHopTupel> getDeliveredMessages(OverlayID<?> id) {
		if (deltaDeliveredMsgs.get(id) != null) {
			return deltaDeliveredMsgs.remove(id);
		} else {
			return new Vector<MessageHopTupel>();
		}
	}

	public Vector<MessageHopTupel> getForwardedMessages(OverlayID<?> id) {
		if (deltaForwardedMsgs.get(id) != null) {
			return deltaForwardedMsgs.remove(id);
		} else {
			return new Vector<MessageHopTupel>();
		}
	}

	public Vector<Message> getQueriesStarted(OverlayID<?> id) {
		if (deltaQueriesStarted.get(id) != null) {
			return deltaQueriesStarted.remove(id);
		} else {
			return new Vector<Message>();
		}
	}

	public Vector<QueryStat> getQueriesDelivered(OverlayID<?> id) {
		if (deltaQueriesDelivered.get(id) != null) {
			return deltaQueriesDelivered.remove(id);
		} else {
			return new Vector<QueryStat>();
		}
	}

}
