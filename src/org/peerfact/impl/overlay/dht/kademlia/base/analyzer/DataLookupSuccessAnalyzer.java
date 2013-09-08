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

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.IKademliaAnalyzer.DataLookupAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.AvgAccumulator;
import org.peerfact.impl.overlay.dht.kademlia.base.components.AbstractNodeFactory;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.DataLookupOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.OperationState;
import org.peerfact.impl.overlay.dht.kademlia.hkademlia.components.HKademliaOverlayID;
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
 * 
 * Analyses the results of data lookup operations and calculates the average
 * success rate. A data lookup operation is successful if the data item that
 * corresponds to the lookup key has been found (or if no such data item exists
 * and the lookup correctly does not find it).
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class DataLookupSuccessAnalyzer extends FileAnalyzer implements
		DataLookupAnalyzer {

	/**
	 * The initial capacity of the LinkedHashMap that contains the currently
	 * running (and analysed) operations.
	 */
	private static final int RUNNING_OPERATIONS_STARTING_CAPACITY = 50;

	/**
	 * A Map that maps the IDs of Operations to the point in time at which they
	 * have been started.
	 */
	private Map<Integer, Long> opBeginnings;

	/**
	 * A Map that contains for each operation-ID a Map with cluster depths as
	 * keys and the number of sent messages as values.
	 */
	private Map<Integer, Map<Integer, Integer>> opMsgClusterDepths;

	/**
	 * An AvgAccumulator that stores the rate of succeeded lookups. For each
	 * successful lookup, "1" is added and "0" for failed lookups respectively.
	 */
	private AvgAccumulator successRate;

	private AvgAccumulator successLatency;

	private AvgAccumulator failureLatency;

	/**
	 * The average number of peers that are closer to the lookup key than the
	 * peer that returned the item (only for successful lookups).
	 */
	private AvgAccumulator closer;

	/**
	 * The average number of peers that are online and closer to the lookup key
	 * than the peer that returned the item (only for successful lookups).
	 */
	private AvgAccumulator onlineCloser;

	/**
	 * The average number of peers that are online, closer to the lookup key
	 * than the peer that returned the item, and possess the data item (only for
	 * successful lookups).
	 */
	private AvgAccumulator onlineCloserWithItem;

	/**
	 * The average percentage of senders that are part of the k closest online
	 * nodes to the key.
	 */
	private AvgAccumulator senderPartOfKClosestOnline;

	/**
	 * The average number of peers among the k closest online IDs that have the
	 * data item (successful lookups).
	 */
	private AvgAccumulator onlineKClosestWithItemSucc;

	/**
	 * The average number of peers among the k closest online IDs that have the
	 * data item (failed lookups).
	 */
	private AvgAccumulator onlineKClosestWithItemFail;

	/**
	 * The average number of peers that have the data item (successful lookups).
	 */
	private AvgAccumulator allWithItemSucc;

	/**
	 * The average number of peers that have the data item (failed lookups).
	 */
	private AvgAccumulator allWithItemFail;

	/**
	 * The average number of online peers that have the data item (successful
	 * lookups).
	 */
	private AvgAccumulator onlineWithItemSucc;

	/**
	 * The average number of online peers that have the data item (failed
	 * lookups).
	 */
	private AvgAccumulator onlineWithItemFail;

	/**
	 * An AvgAccumulator that stores the fraction of common contacts in lookup
	 * result and perfect result (for failed data lookups).
	 */
	private AvgAccumulator commonContactsRate;

	/**
	 * An AvgAccumulator that stores the fraction of offline contacts in lookup
	 * result (for failed data lookups).
	 */
	private AvgAccumulator offlineContactsRate;

	/**
	 * An AvgAccumulator that stores the fraction of missed closer contacts in
	 * lookup result (for failed data lookups).
	 */
	private AvgAccumulator missedCloserContactsRate;

	/**
	 * Called by the superclass to start the data lookup success analysis. Only
	 * lookups that complete after start() has been called are taken into
	 * account. Calling this method starts a <i>fresh</i> measurement.
	 */
	@Override
	protected final void started() {
		opBeginnings = new LinkedHashMap<Integer, Long>(
				RUNNING_OPERATIONS_STARTING_CAPACITY);
		opMsgClusterDepths = new LinkedHashMap<Integer, Map<Integer, Integer>>(
				RUNNING_OPERATIONS_STARTING_CAPACITY);
		successRate = new AvgAccumulator();
		successLatency = new AvgAccumulator();
		failureLatency = new AvgAccumulator();
		closer = new AvgAccumulator();
		onlineCloser = new AvgAccumulator();
		onlineCloserWithItem = new AvgAccumulator();
		senderPartOfKClosestOnline = new AvgAccumulator();
		onlineKClosestWithItemSucc = new AvgAccumulator();
		onlineKClosestWithItemFail = new AvgAccumulator();
		allWithItemSucc = new AvgAccumulator();
		allWithItemFail = new AvgAccumulator();
		onlineWithItemSucc = new AvgAccumulator();
		onlineWithItemFail = new AvgAccumulator();
		commonContactsRate = new AvgAccumulator();
		offlineContactsRate = new AvgAccumulator();
		missedCloserContactsRate = new AvgAccumulator();
		appendToFile("# RESULT_CORRECT LATENCY DEPTH=MSGS_SENT ");
		appendToFile("ALL_DATA ONLINE_DATA KCN_DATA ");
		appendToFile("ALL_CLOSER ONLINE_CLOSER ONLINE_DATA_CLOSER SENDER_KCN ");
		appendToFile("PERFECT_CONTACTS OFFLINE_CONTACTS MISSED_CLOSER_CONTACTS");
		appendNewLine();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dataLookupInitiated(final DataLookupOperation<?> op) {
		if (!isStarted()) {
			return;
		}
		final int opID = op.getOperationID();
		opBeginnings.put(opID, Simulator.getCurrentTime());
		opMsgClusterDepths.put(opID, new LinkedHashMap<Integer, Integer>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dataLookupMsgSent(final KademliaMsg<?> msg,
			final DataLookupOperation<?> op) {
		if (!isStarted()) {
			return;
		}
		final Map<Integer, Integer> sentMsgs = opMsgClusterDepths.get(op
				.getOperationID());
		if (sentMsgs == null) {
			return; // operation unknown
		}
		final KademliaMsg<HKademliaOverlayID> m = (KademliaMsg<HKademliaOverlayID>) msg;
		final int clusterDepth = m.getSender().getCommonClusterDepth(
				m.getDestination());

		Integer sentSoFar = sentMsgs.get(clusterDepth);
		if (sentSoFar == null) {
			sentSoFar = 1;
		} else {
			sentSoFar++;
		}
		sentMsgs.put(clusterDepth, sentSoFar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dataLookupCompleted(final KademliaOverlayKey key,
			final DHTObject result, final KademliaOverlayID sender,
			final Collection<? extends KademliaOverlayContact<?>> closestNodes,
			final DataLookupOperation<?> op) {
		final boolean correct;
		final OperationState state;
		final AbstractNodeFactory factory;
		final Set<HKademliaOverlayID> kClosestOnlineIDs;
		final int opID = op.getOperationID();

		if (!isStarted() || !opBeginnings.containsKey(opID)) {
			return;
		}

		state = op.getState();
		if (state != OperationState.SUCCESS && state != OperationState.ERROR) {
			opBeginnings.remove(opID);
			opMsgClusterDepths.remove(opID);
			return; // do not measure ABORTED/TIMEOUT etc.
		}

		correct = KademliaSetup.getWorkloadGenerator()
				.isDataLookupResultCorrect(key, result);
		appendToFile(correct);
		appendSeparator();

		if (correct) {
			calcLatency(opID, successLatency);
			successRate.addToTotal(1);

		} else {
			calcLatency(opID, failureLatency);
			successRate.addToTotal(0);
		}

		calcMsgDepths(opID);

		factory = KademliaSetup.getNodeFactory();
		kClosestOnlineIDs = factory.getKClosestOnlineIDs(key);
		if (correct) {
			calcPeersWithData(key, kClosestOnlineIDs, factory, allWithItemSucc,
					onlineWithItemSucc, onlineKClosestWithItemSucc);
			calcCloserIDs(key, sender, kClosestOnlineIDs, factory);
			for (int i = 1; i <= 3; i++) {
				appendToFile('-');
				appendSeparator();
			}
		} else {
			calcPeersWithData(key, kClosestOnlineIDs, factory, allWithItemFail,
					onlineWithItemFail, onlineKClosestWithItemFail);
			for (int i = 1; i <= 4; i++) {
				appendToFile('-');
				appendSeparator();
			}
			calcKClosest(key, closestNodes, kClosestOnlineIDs);
		}
		appendNewLine();
	}

	private void calcLatency(final int opID, final AvgAccumulator whereToWrite) {
		final Long startingTime = opBeginnings.remove(opID);
		final long duration = Simulator.getCurrentTime() - startingTime;
		whereToWrite.addToTotal(duration);
		appendToFile(duration / (double) Simulator.SECOND_UNIT);
		appendSeparator();
	}

	private void calcMsgDepths(final int opID) {
		final Map<Integer, Integer> sentMsgs = opMsgClusterDepths.remove(opID);
		if (sentMsgs == null) {
			return; // operation unknown
		}
		appendToFile('[');
		for (final Map.Entry<Integer, Integer> entry : sentMsgs.entrySet()) {
			appendToFile(entry.getKey());
			appendToFile('=');
			appendToFile(entry.getValue());
			appendToFile(',');
		}
		appendToFile(']');
		appendSeparator();
	}

	private void calcPeersWithData(final KademliaOverlayKey key,
			final Set<HKademliaOverlayID> kClosestOnlineIDs,
			final AbstractNodeFactory factory, final AvgAccumulator all,
			final AvgAccumulator online, final AvgAccumulator kClosest) {
		int kClosestOnlineIDsWithItem = 0;
		final int[] peersWithData;

		peersWithData = factory.numberOfPeersWithData(key);
		for (final HKademliaOverlayID closestID : kClosestOnlineIDs) {
			if (factory.hasDataItem(closestID, key)) {
				kClosestOnlineIDsWithItem++;
			}
		}

		appendToFile(peersWithData[0]);
		appendSeparator();
		appendToFile(peersWithData[1]);
		appendSeparator();
		appendToFile(kClosestOnlineIDsWithItem);
		appendSeparator();

		all.addToTotal(peersWithData[0]);
		online.addToTotal(peersWithData[1]);
		kClosest.addToTotal(kClosestOnlineIDsWithItem);
	}

	private void calcCloserIDs(final KademliaOverlayKey key,
			final KademliaOverlayID sender,
			final Set<HKademliaOverlayID> kClosestOnlineIDs,
			final AbstractNodeFactory factory) {
		final Collection<HKademliaOverlayID> closerIDs;
		final HKademliaOverlayID sndr;
		int onlineCloserIDs = 0, onlineCloserIDsWithItem = 0;
		final boolean senderPartOfKClosest;

		sndr = (HKademliaOverlayID) sender;
		closerIDs = factory.getCloserIDs(sndr, key);
		senderPartOfKClosest = kClosestOnlineIDs.contains(sndr);

		for (final HKademliaOverlayID closerID : closerIDs) {
			if (factory.isOffline(closerID)) {
				continue;
			}
			onlineCloserIDs++;
			if (factory.hasDataItem(closerID, key)) {
				onlineCloserIDsWithItem++;
			}
		}

		appendToFile(closerIDs.size());
		appendSeparator();
		appendToFile(onlineCloserIDs);
		appendSeparator();
		appendToFile(onlineCloserIDsWithItem);
		appendSeparator();
		appendToFile(senderPartOfKClosest);
		appendSeparator();

		closer.addToTotal(closerIDs.size());
		onlineCloser.addToTotal(onlineCloserIDs);
		onlineCloserWithItem.addToTotal(onlineCloserIDsWithItem);
		senderPartOfKClosestOnline
				.addToTotal((senderPartOfKClosest ? 1.0 : 0.0));
	}

	private void calcKClosest(final KademliaOverlayKey key,
			final Collection<? extends KademliaOverlayContact<?>> closestNodes,
			final Set<HKademliaOverlayID> kClosestOnlineIDs) {
		final int offlineContacts = KClosestNodesLookupSuccessAnalyzer
				.getNumberOfOfflineContacts(closestNodes);
		final int perfectContacts = KClosestNodesLookupSuccessAnalyzer
				.getNumberOfPerfectContacts(closestNodes, kClosestOnlineIDs);
		final int missedCloserContacts = KClosestNodesLookupSuccessAnalyzer
				.getNumberOfMissedCloserContacts(closestNodes,
						kClosestOnlineIDs, key);
		final double size = closestNodes.size();

		appendToFile(perfectContacts);
		appendSeparator();
		appendToFile(offlineContacts);
		appendSeparator();
		appendToFile(missedCloserContacts);
		appendSeparator();

		commonContactsRate.addToTotal(perfectContacts / size);
		offlineContactsRate.addToTotal(offlineContacts / size);
		missedCloserContactsRate.addToTotal(missedCloserContacts / size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void stopped(final Writer output) throws IOException {
		output.write("\n******************** ");
		output.write("Data Lookup Operation Statistics ");
		output.write("********************\n");
		output.write("-- general --\n");
		output.write(String.format(
				" Success rate: \t\t\t%1$08.4f%2$s (%3$s of "
						+ "%4$s data lookups correct)\n", successRate
						.getAverage() * 100, "%", successRate.getCount()
						* successRate.getAverage(), successRate.getCount()));

		output.write("\n-- success --\t\t\tAVG\t\tMIN\t\tMAX\n");
		output.write(String.format(
				" Latency: \t\t\t%1$07.2f ms\t%2$07.2f ms\t%3$07.2f ms\n",
				successLatency.getAverage() / Simulator.MILLISECOND_UNIT,
				successLatency.getMin() / Simulator.MILLISECOND_UNIT,
				successLatency.getMax() / Simulator.MILLISECOND_UNIT));
		output.write(String.format(
				" All peers with data: \t\t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", allWithItemSucc.getAverage(), "%",
				allWithItemSucc.getMin(), allWithItemSucc.getMax()));
		output.write(String.format(
				" Online peers with data: \t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", onlineWithItemSucc.getAverage(), "%",
				onlineWithItemSucc.getMin(), onlineWithItemSucc.getMax()));
		output.write(String.format(
				" K closest nodes with data: \t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", onlineKClosestWithItemSucc
						.getAverage(), "%",
				onlineKClosestWithItemSucc.getMin(), onlineKClosestWithItemSucc
						.getMax()));
		output.write(String.format(
				" Closer IDs: \t\t\t%1$07.4f \t%3$07.4f \t%4$07.4f\n", closer
						.getAverage(), "%", closer.getMin(), closer.getMax()));
		output.write(String
				.format(" Closer online IDs: \t\t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", onlineCloser.getAverage(), "%",
						onlineCloser.getMin(), onlineCloser.getMax()));
		output.write(String.format(
				" Closer online IDs with data: \t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", onlineCloserWithItem.getAverage(),
				"%", onlineCloserWithItem.getMin(), onlineCloserWithItem
						.getMax()));
		output.write(String.format(
				" Sender among k closest nodes: \t%1$07.4f%2$s\n",
				senderPartOfKClosestOnline.getAverage() * 100, "%"));

		output.write("\n-- failure --\t\t\tAVG\t\tMIN\t\tMAX\n");
		output.write(String.format(
				" Latency: \t\t\t%1$07.2f ms\t%2$07.2f ms\t%3$07.2f ms\n",
				failureLatency.getAverage() / Simulator.MILLISECOND_UNIT,
				failureLatency.getMin() / Simulator.MILLISECOND_UNIT,
				failureLatency.getMax() / Simulator.MILLISECOND_UNIT));
		output.write(String.format(
				" All peers with data: \t\t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", allWithItemFail.getAverage(), "%",
				allWithItemFail.getMin(), allWithItemFail.getMax()));
		output.write(String.format(
				" Online peers with data: \t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", onlineWithItemFail.getAverage(), "%",
				onlineWithItemFail.getMin(), onlineWithItemFail.getMax()));
		output.write(String.format(
				" K closest nodes with data: \t%1$07.4f \t%3$07.4f "
						+ "\t%4$07.4f\n", onlineKClosestWithItemFail
						.getAverage(), "%",
				onlineKClosestWithItemFail.getMin(), onlineKClosestWithItemFail
						.getMax()));
		output.write(String.format(" Perfect lookup similarity: \t%1$6.5g%2$s "
				+ "\t%3$6.5g%2$s \t%4$6.5g%2$s\n", commonContactsRate
				.getAverage() * 100, "%", commonContactsRate.getMin() * 100,
				commonContactsRate.getMax() * 100));
		output.write(String.format(" Offline contacts: \t\t%1$6.5g%2$s "
				+ "\t%3$6.5g%2$s \t%4$6.5g%2$s\n", offlineContactsRate
				.getAverage() * 100, "%", offlineContactsRate.getMin() * 100,
				offlineContactsRate.getMax() * 100));
		output.write(String.format(" Missed closer contacts: \t%1$6.5g%2$s "
				+ "\t%3$6.5g%2$s \t%4$6.5g%2$s\n", missedCloserContactsRate
				.getAverage() * 100, "%",
				missedCloserContactsRate.getMin() * 100,
				missedCloserContactsRate.getMax() * 100));

		output.write(" ('K closest nodes' refers to online peers. 'Closer' ");
		output.write("means closer than the sender of the data.\n");
		output.write("****************** ");
		output.write("Data Lookup Operation Statistics End ");
		output.write("******************\n");
		output.flush();
	}
}
