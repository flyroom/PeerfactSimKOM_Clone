package org.peerfact.impl.util.toolkits;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.CollectionHelpers;


public class CollectionHelpersTest {
	private static Logger log = SimLogger
			.getLogger(CollectionHelpersTest.class);

	public static void main(String[] args) {
		log.debug("===================== This should look random.");
		log.debug("===================== List");
		List<String> list = Arrays.asList("Das", "ist", "das", "Haus", "vom",
				"Nikolaus");
		for (int i = 0; i < 100; i++) {
			log.debug(CollectionHelpers.getRandomEntry(list));
		}

		log.debug("===================== Not List");
		Set<String> set = new LinkedHashSet<String>(list);
		for (int i = 0; i < 100; i++) {
			log.debug(CollectionHelpers.getRandomEntry(set));
		}
	}

}
