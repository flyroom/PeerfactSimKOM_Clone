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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.peerfact.impl.util.toolkits.CollectionHelpers;
import org.peerfact.impl.util.toolkits.Predicate;


/**
 * Tests for CollectionHelpers.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class CollectionHelpersTest {

	private static Predicate<Integer> oddFilter;

	private static Comparator<Integer> natOrder;

	/**
	 * Setup common used objects.
	 */
	@BeforeClass
	public static void setUp() {
		oddFilter = new Predicate<Integer>() {
			@Override
			public boolean isTrue(Integer object) {
				return object % 2 != 0;
			}
		};

		natOrder = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		};
	}

	/**
	 * Tests filterMap.
	 */
	@Test
	public void testFilterMap() {
		Map<String, Integer> zahlen = new LinkedHashMap<String, Integer>();
		zahlen.put("eins", 1);
		zahlen.put("zwei", 2);
		zahlen.put("drei", 3);

		Map<String, Integer> ungeradeZahlen = new LinkedHashMap<String, Integer>();
		ungeradeZahlen.put("eins", 1);
		ungeradeZahlen.put("drei", 3);

		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		CollectionHelpers.filterMap(zahlen, result, oddFilter);

		assertEquals("Result map should only contain odd numbers",
				ungeradeZahlen, result);
	}

	/**
	 * Test filter.
	 */
	@Test
	public void testFilter() {
		List<Integer> zahlen = new ArrayList<Integer>();
		zahlen.add(1);
		zahlen.add(2);
		zahlen.add(3);

		List<Integer> ungeradeZahlen = new ArrayList<Integer>();
		ungeradeZahlen.add(1);
		ungeradeZahlen.add(3);

		List<Integer> result = new ArrayList<Integer>();
		CollectionHelpers.filter(zahlen, result, oddFilter);

		assertEquals("Result list should contain only odd numbers",
				ungeradeZahlen, result);
	}

	/**
	 * Test copyNSorted.
	 */
	@Test
	public void testCopyNSorted() {
		List<Integer> numberSrc = new ArrayList<Integer>();
		numberSrc.add(1);
		numberSrc.add(3);
		numberSrc.add(9);
		numberSrc.add(2);

		Set<Integer> numberDst = new LinkedHashSet<Integer>();
		numberDst.add(2);

		Set<Integer> expected1 = new LinkedHashSet<Integer>();
		expected1.add(2);
		expected1.add(1);
		expected1.add(3);

		CollectionHelpers.copyNSorted(numberSrc, numberDst, natOrder, 2);
		assertEquals(
				"Copying 2 new lowest entries from {1, 3, 9, 2} to {2} should result in {2, 1, 3}",
				expected1, numberDst);

		numberDst.clear();
		numberDst.add(2);

		Set<Integer> expected2 = new LinkedHashSet<Integer>();
		expected2.add(2);
		expected2.add(1);
		expected2.add(3);
		expected2.add(9);

		CollectionHelpers.copyNSorted(numberSrc, numberDst, natOrder, 4);
		assertEquals(
				"Copying 4 new lowest entries from {1, 3, 9, 2} to {2} should result in {2, 1, 3, 9}",
				expected2, numberDst);
	}

	/**
	 * Test copyNSortedAndFiltered.
	 */
	@Test
	public void testCopyNSortedAndFiltered() {
		List<Integer> numberSrc = new ArrayList<Integer>();
		numberSrc.add(1);
		numberSrc.add(3);
		numberSrc.add(9);
		numberSrc.add(2);

		Set<Integer> numberDst = new LinkedHashSet<Integer>();
		numberDst.add(3);

		Set<Integer> expected1 = new LinkedHashSet<Integer>();
		expected1.add(3);
		expected1.add(1);
		expected1.add(9);

		CollectionHelpers.copyNSortedAndFiltered(numberSrc, numberDst,
				oddFilter, natOrder, 2);
		assertEquals(
				"Copying 2 new lowest odd entries from {1, 3, 9, 2} to {3} should result in {3, 1, 9}",
				expected1, numberDst);

		List<Integer> dstList = new ArrayList<Integer>();
		dstList.add(3);

		List<Integer> expected2 = new ArrayList<Integer>();
		expected2.add(3);
		expected2.add(1);
		expected2.add(3);
		expected2.add(9);

		CollectionHelpers.copyNSortedAndFiltered(numberSrc, dstList, oddFilter,
				natOrder, 4);
		assertEquals(
				"Copying 4 new lowest odd entries from {1, 3, 9, 2} to list (3) should result in (3, 1, 3, 9)",
				expected2, dstList);
	}

	// public static <T, U> void copyUntilFull(Map<T, U> source, Map<T, U>
	// dest1,
	// Map<T, U> overflow, int dest1Capacity) {
	/**
	 * Test copyUntilFull.
	 */
	@Test
	public void testCopyUntilFull() {
		Map<String, Integer> max4 = new LinkedHashMap<String, Integer>();
		max4.put("eins", 1);
		max4.put("zwei", 2);

		Map<String, Integer> threeMore = new LinkedHashMap<String, Integer>();
		threeMore.put("drei", 3);
		threeMore.put("vier", 4);
		threeMore.put("fuenf", 5);

		Map<String, Integer> rest = new LinkedHashMap<String, Integer>();

		Map<String, Integer> allTogether = new LinkedHashMap<String, Integer>();
		allTogether.put("eins", 1);
		allTogether.put("zwei", 2);
		allTogether.put("drei", 3);
		allTogether.put("vier", 4);
		allTogether.put("fuenf", 5);

		CollectionHelpers.copyUntilFull(threeMore, max4, rest, 4);
		assertEquals("max4 should now contain 4 entries", 4, max4.size());
		assertEquals("rest should now contain 1 entry", 1, rest.size());
		max4.putAll(rest);
		assertEquals("max4 merged with rest should contain all entries (1-5)",
				allTogether, max4);
	}

	/**
	 * Test splitMap.
	 */
	@Test
	public void testSplitMap() {
		Map<String, Integer> zahlen = new LinkedHashMap<String, Integer>();
		zahlen.put("eins", 1);
		zahlen.put("zwei", 2);
		zahlen.put("drei", 3);

		Map<String, Integer> ungeradeZahlen = new LinkedHashMap<String, Integer>();
		ungeradeZahlen.put("eins", 1);
		ungeradeZahlen.put("drei", 3);

		Map<String, Integer> geradeZahlen = new LinkedHashMap<String, Integer>();
		geradeZahlen.put("zwei", 2);

		Map<String, Integer> result1 = new LinkedHashMap<String, Integer>();
		Map<String, Integer> result2 = new LinkedHashMap<String, Integer>();
		CollectionHelpers.splitMap(zahlen, result1, result2, oddFilter);

		assertEquals("Result map 1 should only contain odd numbers",
				ungeradeZahlen, result1);
		assertEquals("Result map 2 should only contain even numbers",
				geradeZahlen, result2);
	}
}
