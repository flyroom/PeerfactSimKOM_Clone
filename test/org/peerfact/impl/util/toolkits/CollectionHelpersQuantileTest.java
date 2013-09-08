package org.peerfact.impl.util.toolkits;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.peerfact.impl.util.toolkits.CollectionHelpers;

public class CollectionHelpersQuantileTest {

	@Test
	public static void testSimpleValues() {

		Assert.assertEquals(Double.NaN, CollectionHelpers.getQuantile(
				Collections.<Double> emptyList(), 0.5d), 0.1);
		Assert.assertEquals(5d, CollectionHelpers.getQuantile(
				Collections.singletonList(5d), 0.5d), 0.1);

	}

	@Test
	public static void testHarderValues() {

		Assert.assertEquals(
				16d,
				CollectionHelpers.getQuantile(
						Arrays.asList(new Double[] { 10d, 20d }), 0.6d), 0.1);
		Assert.assertEquals(25d, CollectionHelpers.getQuantile(
				Arrays.asList(new Double[] { 10d, 20d, 30d, 60d }), 0.5d), 0.1);

	}

}
