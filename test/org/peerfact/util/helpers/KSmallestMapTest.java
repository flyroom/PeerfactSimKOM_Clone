/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.util.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.peerfact.impl.util.toolkits.KSmallestMap;
import org.peerfact.impl.util.toolkits.KSortedLookupList;
import org.peerfact.impl.util.toolkits.Predicate;
import org.peerfact.impl.util.toolkits.Predicates;


/**
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * 
 */
public class KSmallestMapTest {

	final Comparator<Integer> intNaturalComp = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}

	};

	final Predicate<Integer> even = new Predicate<Integer>() {

		@Override
		public boolean isTrue(Integer object) {
			return object % 2 == 0;
		}

	};

	final Predicate<Boolean> notNull = new Predicate<Boolean>() {

		@Override
		public boolean isTrue(Boolean object) {
			return (object != null);
		}

	};

	final Predicate<Boolean> isTrue = new Predicate<Boolean>() {

		@Override
		public boolean isTrue(Boolean object) {
			return object;
		}

	};

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#put(java.lang.Object, java.lang.Object)}
	 * .
	 */
	@Test
	public void testPutCorrectnessNaturalOrder() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);
		assertTrue("4 can be put into empty map", testObj.put(4, false));
		assertTrue("3 can be put into map", testObj.put(3, true));
		assertTrue("6 can be put into map", testObj.put(6, true));
		assertTrue("2 can be put into map", testObj.put(2, false));
		assertTrue("1 can be put into map, replace 6", testObj.put(1, true));
		assertFalse("7 cannot be put into map (too large)", testObj
				.put(7, true));
		assertTrue("1 can be updated", testObj.put(1, false));

		assertEquals("Result should have size 4", 4, testObj.size());
		assertEquals("Value for key=1 should be false", false, testObj.get(1));
		assertEquals("Value for key=2 should be false", false, testObj.get(2));
		assertEquals("Value for key=3 should be true", true, testObj.get(3));
		assertEquals("Value for key=4 should be false", false, testObj.get(4));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#put(Object, Object, Predicate)}
	 * .
	 */
	@Test
	public void testPutWithPredicate() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);

		assertTrue("4 can be put into empty map", testObj.put(4, false, even,
				notNull));
		assertFalse("3 cannot be put into map (odd)", testObj.put(3, true,
				even, notNull));
		assertTrue("6 can be put into map", testObj.put(6, true, even, notNull));
		assertTrue("2 can be put into map", testObj.put(2, true, even, notNull));
		assertTrue("10 can be put into map", testObj.put(10, true, even,
				notNull));
		assertTrue("8 can be put into map (replace 10)", testObj.put(8, true,
				even, notNull));
		assertFalse("12 cannot be put into map (too large)", testObj.put(12,
				true, even, notNull));
		assertTrue("2 can be updated", testObj.put(2, false, even, notNull));
		assertFalse("6 cannot be updated (null)", testObj.put(6, null, even,
				notNull));
		assertTrue("3 can now be added", testObj.put(3, true, null, notNull));
		assertFalse("3 cannot be updated (null)", testObj.put(3, null, null,
				notNull));
		assertTrue("3 can now be updated", testObj.put(3, null, null, null));

		assertEquals("Result should have size 4", 4, testObj.size());
		assertEquals("Value for key=2 should be false", false, testObj.get(2));
		assertEquals("Value for key=3 should be null", null, testObj.get(8));
		assertEquals("Value for key=4 should be false", false, testObj.get(4));
		assertEquals("Value for key=6 should be true", true, testObj.get(6));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#putAll(java.lang.Object)}
	 * .
	 */
	@Test
	public void testPutAll() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);
		List<Integer> items = new ArrayList<Integer>(5);
		items.add(4);
		items.add(3);
		items.add(6);
		items.add(2);
		items.add(1);

		testObj.putAll(items);

		assertEquals("Result should have size 4", 4, testObj.size());
		assertEquals("Value for key=1 should be null", null, testObj.get(1));
		assertEquals("Value for key=2 should be null", null, testObj.get(2));
		assertEquals("Value for key=3 should be null", null, testObj.get(3));
		assertEquals("Value for key=4 should be null", null, testObj.get(4));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#putAll(java.util.Collection, Object, boolean)}
	 * .
	 */
	@Test
	public void testPutAllNoUpdate() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);
		List<Integer> items = new ArrayList<Integer>(5);
		items.add(4);
		items.add(3);
		items.add(6);
		items.add(2);
		items.add(1);

		// preset 2=true and prevent update when adding items with value=false
		testObj.put(2, true);
		testObj.putAll(items, false, false);

		assertEquals("Result should have size 4", 4, testObj.size());
		assertEquals("Value for key=1 should be false", false, testObj.get(1));
		assertEquals("Value for key=2 should be true", true, testObj.get(2));
		assertEquals("Value for key=3 should be false", false, testObj.get(3));
		assertEquals("Value for key=4 should be false", false, testObj.get(4));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#putAll(java.lang.Object)}
	 * .
	 */
	@Test
	public void testPutAllWithPredicate() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);

		List<Integer> items = new ArrayList<Integer>(5);
		items.add(4);
		items.add(3);
		items.add(6);
		items.add(2);
		items.add(1);

		testObj.putAll(items, even);

		assertEquals("Result should have size 3", 3, testObj.size());
		assertEquals("Value for key=2 should be null", null, testObj.get(2));
		assertEquals("Value for key=4 should be null", null, testObj.get(4));
		assertEquals("Value for key=6 should be null", null, testObj.get(6));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#putAll(Map, Predicate, Predicate)}
	 * .
	 */
	@Test
	public void testPutAllMapWithPredicates() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(2, intNaturalComp);

		Map<Integer, Boolean> mappings = new LinkedHashMap<Integer, Boolean>();
		mappings.put(4, true);
		mappings.put(6, false);
		mappings.put(3, null);
		mappings.put(1, true);
		mappings.put(0, null);
		mappings.put(2, false);

		testObj.putAll(mappings, even, notNull);

		assertEquals("Result should have size 2", 2, testObj.size());
		assertEquals("Value for key=2 should be false", false, testObj.get(2));
		assertEquals("Value for key=4 should be true", true, testObj.get(4));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#isInRange(Object)}
	 * .
	 */
	@Test
	public void testIsInRange() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);
		testObj.put(4, false);
		testObj.put(3, true);
		testObj.put(6, true);
		testObj.put(2, false);

		assertFalse("1 is not in range of map", testObj.isInRange(1));
		assertTrue("2 is in the range of the map", testObj.isInRange(2));
		assertTrue("5 is in the range of the map", testObj.isInRange(5));
		assertFalse("7 is not in the range of the map", testObj.isInRange(7));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#getMinKey(Predicate)}
	 * .
	 */
	@Test
	public void testGetMinKey() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);

		testObj.put(4, false);
		testObj.put(3, true);
		testObj.put(6, true);
		testObj.put(2, false);

		assertEquals("The minimal key with value=true is 3",
				Integer.valueOf(3),
				testObj
				.getMinKey(isTrue));
		testObj.remove(3);
		assertEquals("The minimal key with value=true is 6",
				Integer.valueOf(6),
				testObj
				.getMinKey(isTrue));
		testObj.remove(6);
		assertEquals("The minimal key with value=true is null", null, testObj
				.getMinKey(isTrue));
		testObj.put(3984, true);
		assertEquals("The minimal key with value=true is 3984",
				Integer.valueOf(3984), testObj
				.getMinKey(isTrue));
		testObj.put(-3, true);
		assertEquals("The minimal key with value=true is -3",
				Integer.valueOf(-3),
				testObj
				.getMinKey(isTrue));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#getMaxKey(Comparator)}
	 * .
	 */
	@Test
	public void testGetMaxKey() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);
		Comparator<Integer> oddSmaller = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 % 2 != 0 && o2 % 2 == 0) {
					return -1;
				} else if (o1 % 2 == o2 % 2) {
					return 0;
				}
				return +1;
			}

		};

		testObj.put(4, false);
		testObj.put(3, true);
		testObj.put(6, true);
		testObj.put(2, false);
		testObj.put(7, true);

		assertEquals("The minimal even key is 2", Integer.valueOf(2),
				testObj.getMaxKey(
						oddSmaller, Predicates.getFilterNothing()));
		assertEquals("The minimal even key with value=true is 6",
				Integer.valueOf(6), testObj
				.getMaxKey(oddSmaller, isTrue));
		testObj.remove(2);
		assertEquals("The minimal even key is 4", Integer.valueOf(4),
				testObj.getMaxKey(
						oddSmaller, Predicates.getFilterNothing()));
		testObj.remove(4);
		assertEquals("The minimal even key is 6", Integer.valueOf(6),
				testObj.getMaxKey(
						oddSmaller, Predicates.getFilterNothing()));
		testObj.remove(6);
		assertEquals("The minimal odd key is 3", Integer.valueOf(3),
				testObj.getMaxKey(
						oddSmaller, Predicates.getFilterNothing()));
		testObj.put(98454, false);
		assertEquals("The minimal even key is 98454", Integer.valueOf(98454),
				testObj.getMaxKey(
						oddSmaller, Predicates.getFilterNothing()));
		testObj.remove(3);
		testObj.remove(7);
		assertEquals("There is no key with value=true", null, testObj
				.getMaxKey(oddSmaller, isTrue));
		testObj.put(-234973, true);
		assertEquals("The minimal even key is still 98454",
				Integer.valueOf(98454),
				testObj
				.getMaxKey(oddSmaller, Predicates.getFilterNothing()));
		assertEquals("The minimal key with value=true is -234973",
				Integer.valueOf(-234973),
				testObj.getMaxKey(oddSmaller, isTrue));
		testObj.clear();
		assertEquals("There is no key.", null, testObj.getMaxKey(oddSmaller,
				Predicates.getFilterNothing()));
	}

	/**
	 * Test method for
	 * {@link org.peerfact.impl.util.toolkits.KSmallestMap#setAllValues(Object)}
	 * .
	 */
	@Test
	public void testSetAllValues() {
		KSortedLookupList<Integer, Boolean> testObj;
		testObj = new KSmallestMap<Integer, Boolean>(4, intNaturalComp);
		testObj.put(2, true);
		testObj.put(99, null);
		testObj.put(-23498, false);
		testObj.setAllValues(false);
		assertEquals("Size is still 3", 3, testObj.size());
		assertEquals("Value of 2 is false", false, testObj.get(2));
		assertEquals("Value of 99 is false", false, testObj.get(99));
		assertEquals("Value of -23498 is false", false, testObj.get(-23498));
		assertEquals("First is still -23498", Integer.valueOf(-23498),
				testObj.firstKey());
		assertEquals("Last is still 99", Integer.valueOf(99), testObj.lastKey());
	}

}
