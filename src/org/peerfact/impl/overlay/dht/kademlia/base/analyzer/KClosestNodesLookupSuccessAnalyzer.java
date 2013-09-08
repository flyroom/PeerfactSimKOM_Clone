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
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.peerfact.impl.overlay.dht.kademlia.base.KademliaSetup;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.IKademliaAnalyzer.KClosestNodesLookupAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.AvgAccumulator;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KClosestNodesLookupOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.OperationState;
import org.peerfact.impl.util.toolkits.Comparators.KademliaOverlayIDXORMaxComparator;


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
 * Analyses the results of k closest nodes lookup operations and calculates the
 * average "success rate". In detail, k closest nodes lookup results are
 * evaluated using the following four metrics:
 * <ul>
 * <li>The fraction of contacts that are <i>contained in both</i> the lookup
 * result and the set of the "perfect" k closest nodes.</li>
 * <li>The fraction of contacts from the lookup result that are <i>offline</i>.</li>
 * <li>The fraction of <i>missed closer contacts</i>. That is the fraction of
 * contacts from the lookup result for which distinct contacts exist in the
 * "perfect" result that are closer to the lookup key and not contained in the
 * lookup result.</li>
 * <li>The average result size. This metric is only relevant if a lookup returns
 * less than K contacts.</li>
 * </ul>
 * The "perfect" k closest nodes are those that are closest to the lookup key
 * and currently online/going online. This set is determined using global
 * knowledge. Fractions are calculated by dividing by {@link KademliaConfig#K}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class KClosestNodesLookupSuccessAnalyzer extends FileAnalyzer
		implements KClosestNodesLookupAnalyzer {

	/**
	 * Enum to be used in {@link #getNumberOfMissedCloserContacts} to determine
	 * where a contact comes from.
	 */
	private enum ContactSource {
		LOOKUP, PERFECT;
	}

	/**
	 * An AvgAccumulator that stores the fraction of common contacts in lookup
	 * result and perfect result.
	 */
	private AvgAccumulator commonContactsRate;

	/**
	 * An AvgAccumulator that stores the fraction of offline contacts in lookup
	 * result.
	 */
	private AvgAccumulator offlineContactsRate;

	/**
	 * An AvgAccumulator that stores the fraction of missed closer contacts in
	 * lookup result.
	 */
	private AvgAccumulator missedCloserContactsRate;

	/**
	 * An AvgAccumulator that stores the average lookup result size.
	 */
	private AvgAccumulator resultSize;

	/**
	 * Called by the superclass to start the k closest nodes lookup success
	 * analysis. Only lookups that complete after start() has been called are
	 * taken into account. Calling this method starts a <i>fresh</i>
	 * measurement.
	 */
	@Override
	protected final void started() {
		commonContactsRate = new AvgAccumulator();
		offlineContactsRate = new AvgAccumulator();
		missedCloserContactsRate = new AvgAccumulator();
		resultSize = new AvgAccumulator();
		appendToFile("# OPERATION_ID PERFECT_CONTACTS OFFLINE_CONTACTS ");
		appendToFile("MISSED_CLOSER_CONTACTS NUM_OF_CONTACTS");
		appendNewLine();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void kClosestNodesLookupCompleted(
			final KademliaOverlayKey key,
			final Collection<? extends KademliaOverlayContact<?>> result,
			final KClosestNodesLookupOperation<?> op) {
		if (!isStarted() || op.getState() == OperationState.ABORTED) {
			return;
		}
		final Set<? extends KademliaOverlayID> perfectResult;
		perfectResult = KademliaSetup.getNodeFactory()
				.getKClosestOnlineIDs(key);
		final int offlineContacts = getNumberOfOfflineContacts(result);
		final int perfectContacts = getNumberOfPerfectContacts(result,
				perfectResult);
		final int missedCloserContacts = getNumberOfMissedCloserContacts(
				result, perfectResult, key);
		final double size = result.size();

		appendToFile(op.getOperationID());
		appendSeparator();
		appendToFile(perfectContacts);
		appendSeparator();
		appendToFile(offlineContacts);
		appendSeparator();
		appendToFile(missedCloserContacts);
		appendSeparator();
		appendToFile(result.size());
		appendNewLine();

		commonContactsRate.addToTotal(perfectContacts / size);
		offlineContactsRate.addToTotal(offlineContacts / size);
		missedCloserContactsRate.addToTotal(missedCloserContacts / size);
		resultSize.addToTotal(size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void stopped(final Writer output) throws IOException {
		output.write("\n******************** ");
		output.write("K Closest Nodes Lookup Operation Statistics ");
		output.write("********************\n");
		output.write("\t\t\t\tAVG\t\tMIN\t\tMAX\n");
		output
				.write(String
						.format(
								" Perfect lookup similarity: \t%1$6.5g%2$s \t%3$6.5g%2$s \t%4$6.5g%2$s\n",
								commonContactsRate.getAverage() * 100, "%",
								commonContactsRate.getMin() * 100,
								commonContactsRate.getMax() * 100));
		output
				.write(String
						.format(
								" Offline contacts: \t\t%1$6.5g%2$s \t%3$6.5g%2$s \t%4$6.5g%2$s\n",
								offlineContactsRate.getAverage() * 100, "%",
								offlineContactsRate.getMin() * 100,
								offlineContactsRate.getMax() * 100));
		output
				.write(String
						.format(
								" Missed closer contacts: \t%1$6.5g%2$s \t%3$6.5g%2$s \t%4$6.5g%2$s\n",
								missedCloserContactsRate.getAverage() * 100,
								"%", missedCloserContactsRate.getMin() * 100,
								missedCloserContactsRate.getMax() * 100));
		output.write(String.format(
				" Result size: \t\t\t%1$6.5g \t\t%2$6.5g \t\t%3$6.5g\n",
				resultSize.getAverage(), resultSize.getMin(), resultSize
						.getMax()));
		output
				.write(" ("
						+ commonContactsRate.getCount()
						+ " lookups analysed. Perfect lookup contains closest online nodes.)\n");
		output.write("****************** ");
		output.write("K Closest Nodes Lookup Operation Statistics End ");
		output.write("******************\n");
		output.flush();
	}

	/**
	 * Determines how many of the contacts in the result are currently ABSENT.
	 * 
	 * @param result
	 *            a Collection containing a lookup result.
	 * @return the number of Nodes that are ABSENT.
	 */
	protected static int getNumberOfOfflineContacts(
			final Collection<? extends KademliaOverlayContact<?>> result) {
		int offline = 0;
		for (final KademliaOverlayContact<?> con : result) {
			if (KademliaSetup.getNodeFactory().isOffline(con.getOverlayID())) {
				offline++;
			}
		}
		return offline;
	}

	/**
	 * Determines how many of the contacts in the result are also contained in
	 * what would be the perfect lookup result.
	 * 
	 * @param result
	 *            the result as returned by the lookup.
	 * @param perfectResult
	 *            the best possible lookup result.
	 * @return the number of contacts that the lookup result has in common with
	 *         the perfect lookup result.
	 */
	protected static int getNumberOfPerfectContacts(
			final Collection<? extends KademliaOverlayContact<?>> result,
			final Set<? extends KademliaOverlayID> perfectResult) {
		int common = 0;
		for (final KademliaOverlayContact<?> con : result) {
			if (perfectResult.contains(con.getOverlayID())) {
				common++;
			}
		}
		return common;
	}

	/**
	 * Determines how many closer ("better") contacts have been missed by
	 * comparing the actual lookup result with what would have been the perfect
	 * lookup result.
	 * <p>
	 * The number of missed closer contacts is the cardinality of the set of
	 * contacts from the lookup result that each have a distinct closer contact
	 * in the perfect lookup result that is not contained in the actual lookup
	 * result. In other words, there has to be an injective mapping from
	 * contacts from the actual lookup result to contacts from the perfect
	 * lookup result where a contact from the actual lookup result is mapped to
	 * a closer contact from the perfect lookup result that is not contained in
	 * the actual lookup result.
	 * 
	 * @param result
	 *            the lookup result to be analysed.
	 * @param perfectResult
	 *            the perfect lookup result.
	 * @param key
	 *            the key that has been looked up.
	 * @return the number of missed closer contacts in the lookup result.
	 */
	protected static int getNumberOfMissedCloserContacts(
			final Collection<? extends KademliaOverlayContact<?>> result,
			final Collection<? extends KademliaOverlayID> perfectResult,
			final KademliaOverlayKey key) {

		final SortedMap<KademliaOverlayID, ContactSource> contacts;
		final Comparator<KademliaOverlayID> xorToKey = new KademliaOverlayIDXORMaxComparator<KademliaOverlayID>(
				key.getBigInt());
		int seenPerfectIDs = 0;
		int seenActualIDsWithCloserPerfectID = 0;

		contacts = new TreeMap<KademliaOverlayID, ContactSource>(xorToKey);

		// first put perfect IDs
		for (final KademliaOverlayID perfect : perfectResult) {
			contacts.put(perfect, ContactSource.PERFECT);
		}

		// now put lookup result (overwrite common entries)
		for (final KademliaOverlayContact<?> actual : result) {
			contacts.put(actual.getOverlayID(), ContactSource.LOOKUP);
		}

		/*
		 * We iterate through the Map with all IDs in increasing distance to the
		 * lookup key. By construction, the Map contains no contacts from the
		 * perfect lookup result that are also contained in the actual lookup
		 * result (these have been overwritten due to the same ID/key). We count
		 * the number of perfect IDs we see (seenPerfectIDs). Each time we see
		 * an entry from the actual result, we may increase the counter of seen
		 * entries from the actual lookup result with a closer entry from the
		 * perfect lookup result (seenActualIDsWithCloserPerfectID) by one if it
		 * does not exceed the value of seenPerfectIDs.
		 */
		for (final Map.Entry<KademliaOverlayID, ContactSource> entry : contacts
				.entrySet()) {
			if (entry.getValue() == ContactSource.PERFECT) {
				seenPerfectIDs++;
			} else if (entry.getValue() == ContactSource.LOOKUP
					&& seenActualIDsWithCloserPerfectID < seenPerfectIDs) {
				seenActualIDsWithCloserPerfectID++;
			}
		}
		return seenActualIDsWithCloserPerfectID;
	}
}
