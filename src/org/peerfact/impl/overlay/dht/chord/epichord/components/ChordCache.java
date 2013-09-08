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

package org.peerfact.impl.overlay.dht.chord.epichord.components;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * This class implements the cache functionality as described in the original
 * paper of EpiChord. It provides cache entry management methods and handles
 * automatic expiration of entries as well as the calculation and automatic
 * updates of slices (so that entries similar to Chord fingers are always
 * present).
 * <p>
 * Internally, it uses a {@link TreeMap} for storage of the entries, which
 * provides sorting and fast access. It does not impose an explicit entry limit,
 * which corresponds to the description in the original paper. It does, however,
 * clear expired entries automatically, so that the cache should not 'overflow'
 * in usual scenarios.
 * <p>
 * This class is completely is thread-safe.
 * 
 * @see #lookup(ChordID, int)
 * @see #update(AbstractChordContact)
 * @see #update()
 * 
 * @see <a href="http://dl.acm.org/citation.cfm?id=1646651.1646839">EpiChord:
 *      Parallelizing the Chord lookup algorithm with reactive routing state
 *      management</a>
 */
public final class ChordCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1643074968708702165L;

	/**
	 * Log.
	 */
	private final Logger log = SimLogger.getLogger(getClass());

	/**
	 * Wrapper class for a cache entry, consisting of the routing information
	 * and the timestamp when the entry was created/updated.
	 */
	private static final class CacheEntry implements Cloneable {

		/**
		 * Convenience access to cache lifetime configuration parameter.
		 * Parameter might change during runtime, therefore we don't cache it in
		 * a variable.
		 * 
		 * @return the cache lifetime (in simulation time units)
		 */
		static long lifetime() {
			return EpiChordConfiguration.CHORD_CACHE_ENTRY_MAX_LIFETIME;
		}

		/** Routing information. */
		final AbstractChordContact contact;

		/** Timestamp (in simulation time) when the entry was created/updated. */
		final long time;

		/**
		 * Constructor used for cloning (used during the join process of a
		 * node).
		 * 
		 * @param contact
		 *            routing information of entry
		 * @param time
		 *            entry timestamp
		 */
		CacheEntry(AbstractChordContact contact, long time) {
			this.contact = contact;
			this.time = time;
		}

		/**
		 * Constructor used when creating/updating an entry. Uses the current
		 * simulation time as timestamp.
		 * 
		 * @param contact
		 *            routing information of entry
		 */
		CacheEntry(AbstractChordContact contact) {
			this(contact, Simulator.getCurrentTime());
		}

		/**
		 * Convenience method to check whether the entry is expired (according
		 * to the configured cache lifetime). It expects an explicit timestamp
		 * instead of using the current simulation time in order to guarantee
		 * that during one iteration tour the same timestamp is used for the
		 * expiration check (so that long running iterations don't affect
		 * consistency).
		 * 
		 * @param compareTime
		 *            timestamp (in simulation time) at which the expiration is
		 *            to be checked
		 * @return <code>true</code> if expired, <code>false</code> otherwise
		 */
		boolean isExpired(long compareTime) {
			return compareTime - time > lifetime();
		}

		@Override
		public CacheEntry clone() {
			return new CacheEntry(contact, time);
		}
	}

	/** The node to which this cache belongs. */
	final transient ChordNode node;

	/**
	 * The internal entry map, using {@link ChordID}s as keys so quick access to
	 * the correct area in the cache based on a lookup target ID is possible.
	 */
	final transient TreeMap<ChordID, CacheEntry> entries = new TreeMap<ChordID, CacheEntry>();

	/**
	 * Constructor, used both for the construction of the very first node's
	 * cache, as well as for the construction of joining nodes which copy the
	 * cache of their join 'accomplice' node (entries to 'ourself' are removed
	 * to ensure consistency).
	 * 
	 * @param node
	 *            the node to which this cache belongs
	 * @param initialEntries
	 *            (optional) cache of the 'accomplice' node during a join
	 *            operation
	 */
	public ChordCache(ChordNode node, ChordCache initialEntries) {
		this.node = node;
		if (initialEntries != null) {
			synchronized (initialEntries) {
				for (CacheEntry ce : initialEntries.entries.values()) {
					if (!ce.contact.equals(node.getOverlayID())) {
						this.entries.put(ce.contact.getOverlayID(), ce.clone());
					}
				}
			}
		}
	}

	/**
	 * Creates or updates an entry in the cache. For example, should be called
	 * whenever an arbitrary node is sending us a message, or during the
	 * retrieval of best matches sent to 'us' by other nodes during a lookup
	 * operation.
	 * 
	 * @param contact
	 *            the routing information which should be cached
	 */
	public synchronized void update(AbstractChordContact contact) {
		if (!contact.getOverlayID().equals(node.getOverlayID())) {
			entries.put(contact.getOverlayID(), new CacheEntry(contact));
		}
	}

	/**
	 * Returns the <code>count</code> best matches for a given lookup target ID,
	 * as defined in the original paper:<br>
	 * The entry, which immediately follows (or is equal to) the lookup target
	 * ID, and <code>count-1</code> preceeding entries.
	 * 
	 * @param target
	 *            lookup target ID
	 * @param count
	 *            number of the expected best matches
	 * @return the <code>count</code> best matches (is never <code>null</code>,
	 *         but might be an empty array if the cache is empty)
	 */
	public synchronized AbstractChordContact[] lookup(ChordID target, int count) {
		long now = Simulator.getCurrentTime();
		try {
			if (entries.isEmpty()) {
				return new AbstractChordContact[0];
			}

			RingIter iterFirst = new RingIter(now, target, +1);

			if (!iterFirst.hasNext()) {
				return new AbstractChordContact[0];
			}

			// our best match
			AbstractChordContact best = iterFirst.next();

			// our best match + (count-1) previous matches
			List<AbstractChordContact> result = new ArrayList<AbstractChordContact>(
					count);
			RingIter iterResult = new RingIter(now, best.getOverlayID(), -1);

			for (int i = 0; i < count && iterResult.hasNext(); i++) {
				result.add(iterResult.next());
			}

			iterFirst = iterResult = null;

			// debug output: let's see how often we can't return enough best
			// matches
			if (result.size() != count) {
				log.warn("Cache lookup count is wrong!");
			}

			return result.toArray(new AbstractChordContact[result.size()]);
		} finally {
			flushExpired(now);
		}
	}

	/**
	 * Removes the entry for a given node. This should happen if a message to
	 * this node times out.
	 * 
	 * @param contact
	 *            routing information of the entry which should be removed
	 */
	public synchronized void flush(AbstractChordContact contact) {
		entries.remove(contact.getOverlayID());
	}

	/**
	 * Removes all entries which expired at the given timestamp
	 * 
	 * @param now
	 *            current simulation time, cached before (potentially) long
	 *            operation was executed
	 */
	private synchronized void flushExpired(long now) {
		List<ChordID> expired = new ArrayList<ChordID>();
		for (CacheEntry ce : entries.values()) {
			if (ce.isExpired(now)) {
				expired.add(ce.contact.getOverlayID());
			}
		}
		for (ChordID id : expired) {
			entries.remove(id);
		}
	}

	/**
	 * Returns the count of valid entries in the cache.
	 * 
	 * @return valid entry count
	 */
	public synchronized long size() {
		flushExpired(Simulator.getCurrentTime());
		return (Constants.LONG_SIZE + node.getLocalOverlayContact()
				.getTransmissionSize()) * entries.size();
	}

	/**
	 * Convenience method which returns all valid entries. This method is useful
	 * for debugging or visualization purposes.
	 * 
	 * @return all valid cache entries
	 */
	public synchronized Set<AbstractChordContact> entries() {
		if (entries.isEmpty()) {
			return Collections.emptySet();
		}

		long now = Simulator.getCurrentTime();
		try {
			TreeSet<AbstractChordContact> result = new TreeSet<AbstractChordContact>();
			Iter iter = new Iter(now, +1);
			while (iter.hasNext()) {
				result.add(iter.next());
			}
			return result;
		} finally {
			flushExpired(now);
		}
	}

	/**
	 * Iterator over the cache entries, according to their {@link ChordID}
	 * order, which does not jump over 0 (i.e. ends with the last entry in the
	 * map), but takes into account possible expiration of entries. This means
	 * that {@link #hasNext()} and {@link #next()} will only return valid
	 * entries, regardless of whether the internal map contains invalid entries
	 * or not.
	 * <p>
	 * Both directions (forward, backward) are supported. It can be used with an
	 * approximate starting point for the iteration.
	 * <p>
	 * As the expiration of entries should be consistent during one iteration
	 * tour (regardless of the time the actual iteration takes), the
	 * constructors of the iterator expect a cached simulation timestamp against
	 * which expiration will be checked.
	 * <p>
	 * <i>The iterator does not <b>remove</b> expired entries, it simply skips
	 * them. Removal is handled by {@link ChordCache#flushExpired(long)}.</i>
	 */
	class Iter implements Iterator<AbstractChordContact> {

		/** Cached simulation time. */
		private final long now;

		/** Internal map iterator. */
		private final Iterator<CacheEntry> iter;

		/** Current entry of the iteration. */
		private AbstractChordContact cur;

		/**
		 * Iterator constructor which expects an approximate starting point for
		 * the operation.
		 * 
		 * @param now
		 *            cached simulation timestamp against which the entry
		 *            expiration is checked
		 * @param target
		 *            approximate starting point (if an entry with the exact ID
		 *            is present/valid, it is used; otherwise the next
		 *            smaller/greater valid entry is used, depending on the
		 *            iteration direction)
		 * @param direction
		 *            <code>+1</code> for forward iteration, <code>-1</code> for
		 *            backward iteration
		 */
		Iter(long now, ChordID target, int direction) {
			this.now = now;
			if (direction > 0) {
				iter = entries.tailMap(target, true).values().iterator();
			} else if (direction < 0) {
				iter = entries.headMap(target, true).descendingMap().values()
						.iterator();
			} else {
				throw new IllegalArgumentException("'direction' must not be 0!");
			}
		}

		/**
		 * Iterator constructor which takes the first/last valid entry as its
		 * iteration starting point, according to the direction.
		 * 
		 * @param now
		 *            cached simulation timestamp against which the entry
		 *            expiration is checked
		 * @param direction
		 *            <code>+1</code> for forward iteration, <code>-1</code> for
		 *            backward iteration
		 */
		Iter(long now, int direction) {
			this(now,
					((direction > 0) ? entries.firstKey() : entries.lastKey()),
					direction);
		}

		// post-condition: if there is still an un-expired entry, #cur will be
		// set accordingly after this call
		@Override
		public boolean hasNext() {
			while (iter.hasNext()) {
				CacheEntry ce = iter.next();
				if (!ce.isExpired(now)) {
					cur = ce.contact;
					return true;
				}
			}
			return false;
		}

		@Override
		public AbstractChordContact next() {
			try {

				if (cur != null) {
					return cur;
				} else if (hasNext()) {
					return cur;
				} else {
					throw new NoSuchElementException();
				}

			} finally {
				cur = null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Cache entry iterator which implements the 0-jump. Only this iterator
	 * should be used in the various cache methods.
	 * <p>
	 * It uses {@link Iter} internally, which provides expiration-aware
	 * iteration.
	 * <p>
	 * As is the case with {@link Iter}, both directions (forward/backward) are
	 * supported. An approximate starting point must be provided (as we always
	 * know our general area of interest within the various cache methods). It
	 * is guaranteed that the iterator stops after all entries were returned.
	 * <p>
	 * <i>The iterator does not <b>remove</b> expired entries, it simply skips
	 * them. Removal is handled by {@link ChordCache#flushExpired(long)}.</i>
	 */
	class RingIter implements Iterator<AbstractChordContact> {

		/**
		 * Iterator over entries in [target,0).
		 */
		private final Iter iter1;

		/**
		 * Iterator over entries in [0,target).
		 */
		private final Iter iter2;

		/**
		 * Currently used iterator.
		 */
		private Iter iterCur;

		/**
		 * Flag which indicates whether we already iterated over all entries.
		 */
		private boolean roundTrip = false;

		/**
		 * Entry which was returned as the very first, during the iteration.
		 * Used for the check whether we iterated over all entries already.
		 */
		private AbstractChordContact first;

		private AbstractChordContact cur;

		/**
		 * Iterator constructor.
		 * 
		 * @param now
		 *            cached simulation timestamp against which the entry
		 *            expiration is checked
		 * @param target
		 *            approximate starting point (if an entry with the exact ID
		 *            is present/valid, it is used; otherwise the next
		 *            smaller/greater valid entry is used, depending on the
		 *            iteration direction)
		 * @param direction
		 *            <code>+1</code> for forward iteration, <code>-1</code> for
		 *            backward iteration
		 */
		public RingIter(long now, ChordID target, int direction) {
			this.iter1 = this.iterCur = new Iter(now, target, direction);
			this.iter2 = new Iter(now, direction);
		}

		// post-condition: if there is still an un-expired entry which was not
		// returned yet, #cur will be set after this call
		@Override
		public boolean hasNext() {
			if (roundTrip) {
				return false;
			}

			// we have reached the end of [target,0) --> start with [0,target)
			boolean hasNext = iterCur.hasNext();
			if (iterCur == iter1 && !hasNext) {
				iterCur = iter2;
				hasNext = iterCur.hasNext();
			}

			if (hasNext) {
				cur = iterCur.next();
				// remember when to stop iteration
				if (first == null) {
					first = cur;
				} else if (first == cur) {
					cur = null;
					roundTrip = true;
					return false;
				}
				return true;
			}
			return false;
		}

		@Override
		public AbstractChordContact next() {
			try {
				if (cur != null) {
					return cur;
				} else if (hasNext()) {
					return cur;
				} else {
					throw new NoSuchElementException();
				}
			} finally {
				cur = null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * This method is responsible for updating entries within each slice, as
	 * defined by the original EpiChord paper.
	 * <p>
	 * <dl>
	 * <dt>Slice calculation
	 * <dd>Slices are calculated using a configuration hint which defines the
	 * number of slices and should be equal to <code>log(N)-1</code> (with
	 * <code>N</code> being the expected network size). More precisely, it
	 * defines the number of slices within one half of the whole ring.
	 * <p>
	 * The first two slices are assumed to be to the left and right of the
	 * opposite ID (that is, the opposite ID of our own node's ID on the ring),
	 * respectively, and cover <code>1/4</code> of the ring. The following
	 * slices (in both directions) are calculated iteratively, using the
	 * appropriate left/right bounds of the previous slices and the previous
	 * slice size divided by two in order to calculate the new slice bounds.
	 * This step is repeated until enough slices (i.e. according to the
	 * configuration hint) are calculated.
	 * <dt>Slice updates
	 * <dd>Using the previously calculated slice bounds, it is checked how many
	 * valid entries are present within each slice. If there are
	 * <code>&gt;=J</code> valid entries in a slice, no slice update is
	 * necessary. Otherwise, <code>lookups = J - valid_entries</code> lookups
	 * are started, with lookup IDs uniformly distributed over the slice range.
	 * <p>
	 * The lookup mechanism of EpiChord then ensures that enough entries are
	 * present in this slice.
	 * </dl>
	 * <p>
	 * It should be called by an appropriate scheduler using the configured
	 * update interval.
	 * 
	 * @see EpiChordConfiguration#CHORD_CACHE_SLICE_COUNT_HINT
	 * @see EpiChordConfiguration#J
	 * @see EpiChordConfiguration#CHORD_CACHE_UPDATE_INTERVAL
	 */
	public void update() {
		long now = Simulator.getCurrentTime();
		List<ChordID> slices = getSlices();

		for (int i = 0; i < slices.size(); i++) {
			ChordID lbound = slices.get(i);
			ChordID rbound = slices.get((i == slices.size() - 1) ? 0 : i + 1);

			int lookups = EpiChordConfiguration.J
					- getCacheEntriesInSlice(lbound, rbound, now);
			if (lookups <= 0) {
				return;
			}

			BigInteger sliceFraction = rbound.getValue()
					.subtract(lbound.getValue()).divide(num(lookups + 1));

			for (int j = 1; j <= lookups; j++) {
				ChordID lookupId = new ChordID(ringMod(lbound.getValue().add(
						sliceFraction.multiply(num(j)))));
				node.overlayNodeLookup(lookupId, null);
			}
		}
	}

	// slices

	/** The actual slice calculation, as described in {@link #update()}. */
	private List<ChordID> getSlices() {

		BigInteger half = ChordID.getMaxValue().divide(num(2));
		BigInteger mine = node.getOverlayID().getValue();
		BigInteger opposite = ringMod(mine.add(half));

		TreeSet<ChordID> slices = new TreeSet<ChordID>();
		BigInteger sliceSize = half;

		for (int i = 0; i < countHint() - 1; i++) {
			sliceSize = sliceSize.divide(num(2));
			BigInteger left = ringMod(mine.add(sliceSize));
			BigInteger right = ringMod(mine.subtract(sliceSize).add(
					ChordID.getMaxValue()));
			slices.add(new ChordID(left));
			slices.add(new ChordID(right));
		}

		slices.add(node.getOverlayID());
		slices.add(new ChordID(opposite));

		return new ArrayList<ChordID>(slices);
	}

	/**
	 * Returns the count of valid entries within a slice.
	 * 
	 * @param lbound
	 *            left bound of the slice (inclusive)
	 * @param rbound
	 *            right bound of the slice (exclusive)
	 * @param now
	 *            simulation timestamp against which the expiration should be
	 *            checked
	 * @return the count of valid entries in the slice
	 */
	private int getCacheEntriesInSlice(ChordID lbound, ChordID rbound, long now) {
		if (entries.isEmpty()) {
			return 0;
		}

		RingIter sliceIter = new RingIter(now, lbound, +1);
		ChordID cur;
		int count = 0;
		while (sliceIter.hasNext()) {
			cur = sliceIter.next().getOverlayID();
			if (cur.compareTo(rbound) >= 0) {
				break;
			}
			count++;
		}
		return count;
	}

	/**
	 * Convenience method which returns the configuration hint for the slices
	 * count.
	 */
	private static int countHint() {
		return EpiChordConfiguration.CHORD_CACHE_SLICE_COUNT_HINT;
	}

	// BigInteger convenience methods

	/**
	 * Convenience method which returns the <code>BigInteger</code> value of the
	 * passed <code>long</code> value.
	 * 
	 * @param val
	 *            the <code>long</code> value
	 * @return the <code>BigInteger</code> value
	 */
	private static BigInteger num(long val) {
		return BigInteger.valueOf(val);
	}

	/**
	 * Convenience methods which takes a <code>BigInteger</code> value modulo
	 * the maximum <code>ChordID</code> value.
	 */
	private static BigInteger ringMod(BigInteger val) {
		return val.mod(ChordID.getMaxValue());
	}
}