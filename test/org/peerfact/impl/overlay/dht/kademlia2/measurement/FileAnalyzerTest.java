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

package org.peerfact.impl.overlay.dht.kademlia2.measurement;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.FileAnalyzer;


/**
 * Test cases for FileAnalyzer.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class FileAnalyzerTest {

	private static final String TEST_FILE = "test/kademlia2/measurement/fileAnalyzerTest.txt";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public static void setUp() throws Exception {
		File testFile = new File(TEST_FILE);
		testFile.delete();
	}

	private static String getFileContents() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(TEST_FILE));
		StringBuilder actual = new StringBuilder();
		String tmp;
		while ((tmp = in.readLine()) != null) {
			actual.append(tmp);
			actual.append('\n');
		}
		return actual.substring(0, actual.length() - 1);
	}

	/**
	 * Basic test for FileAnalyzer.
	 */
	@Test
	public static void testFileAnalyzer() throws Exception {
		final String expected = "This is a test for FileAnalyzer.\nSecond line.";
		FileAnalyzer testObj = new FileAnalyzer();
		testObj.setOutputFile(TEST_FILE);
		testObj.start();
		testObj.appendToFile("This is a test for FileAnalyzer.");
		testObj.appendNewLine();
		testObj.appendToFile("Second line.");
		testObj.stop(null);
		assertEquals("Output should be as expected.", expected,
				getFileContents());
	}
}
