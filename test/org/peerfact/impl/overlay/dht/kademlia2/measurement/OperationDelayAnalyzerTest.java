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

import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.SupportOperations;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.OperationDelayAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KademliaOperation;
import org.peerfact.util.helpers.TestHelper;


/**
 * Tests for OperationDelayAnalyser.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 */
public class OperationDelayAnalyzerTest {

	Operation<Object> op1, op2, op3;

	KademliaOperation<KademliaOverlayID> kadOp1, kadOp2, kadOp3;

	OperationDelayAnalyzer testObj;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestHelper.initSimulator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		TestHelper.setSimulationTime(0);
		testObj = new OperationDelayAnalyzer();

		op1 = new OperationStub(1);
		op2 = new OperationStub(2);
		op3 = new OperationStub(3);

		kadOp1 = new KademliaOperationStub(4);
		kadOp2 = new KademliaOperationStub(5);
		kadOp3 = new KademliaOperationStub(6);
	}

	/**
	 * Operations started before the analysis is started should be ignored. Part
	 * A: Operations that both start and finish before analyser is started.
	 */
	@Test
	public void testMsgBeforeStartA() throws Exception {
		testObj.operationInitiated(op1);
		TestHelper.setSimulationTime(2);
		testObj.operationFinished(op1);
		testObj.start();
		assertEquals(
				"Message started & stopped before start of analysis should be ignored",
				Collections.emptyMap(), testObj.getAvgOperationLatencies());
	}

	/**
	 * Operations started before the analysis is started should be ignored. Part
	 * B: Operations that are started before but finished after the analyser is
	 * started.
	 */
	@Test
	public void testMsgBeforeStartB() throws Exception {
		testObj.operationInitiated(op1);
		testObj.start();
		TestHelper.setSimulationTime(2);
		testObj.operationFinished(op1);
		assertEquals(
				"Message started before start of analysis (and finished afterwards) should be ignored",
				Collections.emptyMap(), testObj.getAvgOperationLatencies());
	}

	/**
	 * Tests whether the restriction to a subtype of Operation is applied
	 * correctly.
	 */
	@Test
	public void testSubtypeRestriction() throws Exception {
		testObj.setOperationSupertype(KademliaOperation.class);
		testObj.start();
		testObj.operationInitiated(op1);
		TestHelper.setSimulationTime(29);
		testObj.operationFinished(op1);
		TestHelper.setSimulationTime(40);
		testObj.operationInitiated(kadOp1);
		TestHelper.setSimulationTime(49);
		testObj.operationFinished(kadOp1);

		Map<Class<?>, Double> expected;
		expected = new LinkedHashMap<Class<?>, Double>();
		expected.put(KademliaOperationStub.class, 9d);
		assertEquals("Only operation of correct type should be counted",
				expected, testObj.getAvgOperationLatencies());
	}

	/**
	 * Tests whether the average of the execution duration is computed correctly
	 * for each type. Also makes sure that the default supertype is Operation.
	 */
	@Test
	public void testAverage() throws Exception {
		testObj.start();
		testObj.operationInitiated(op1);
		TestHelper.setSimulationTime(19);
		testObj.operationFinished(op1);
		TestHelper.setSimulationTime(33);
		testObj.operationInitiated(kadOp1);
		TestHelper.setSimulationTime(34);
		testObj.operationFinished(kadOp1);
		TestHelper.setSimulationTime(40);
		testObj.operationInitiated(kadOp2);
		TestHelper.setSimulationTime(52);
		testObj.operationInitiated(op2);
		testObj.operationInitiated(kadOp3); // never finished
		TestHelper.setSimulationTime(54);
		testObj.operationFinished(kadOp2);
		TestHelper.setSimulationTime(57);
		testObj.operationFinished(op2);
		TestHelper.setSimulationTime(89);
		testObj.operationFinished(op3); // never started

		Map<Class<?>, Double> expected;
		expected = new LinkedHashMap<Class<?>, Double>();
		expected.put(OperationStub.class, 12d);
		expected.put(KademliaOperationStub.class, 7.5d);
		assertEquals("Correct average should be computed for each type",
				expected, testObj.getAvgOperationLatencies());
	}

	/**
	 * Tests the output generated by stop(). Has to be verified manually.
	 */
	@Ignore
	@Test
	public void testStopOutput() throws Exception {
		testObj.start();
		testObj.operationInitiated(op1);
		TestHelper.setSimulationTime(19);
		testObj.operationFinished(op1); // took 19
		TestHelper.setSimulationTime(33);
		testObj.operationInitiated(kadOp1); // took 1
		TestHelper.setSimulationTime(34);
		testObj.operationFinished(kadOp1);
		testObj.stop(new OutputStreamWriter(System.out));
	}

	/**
	 * Test stub of Operation.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	protected class OperationStub implements Operation<Object> {

		private int operationID;

		public OperationStub(int opId) {
			operationID = opId;
		}

		@Override
		public SupportOperations getComponent() {
			return null;
		}

		@Override
		public int getOperationID() {
			return operationID;
		}

		@Override
		public Object getResult() {
			return null;
		}

		@Override
		public boolean isFinished() {
			return true;
		}

		@Override
		public boolean isSuccessful() {
			return false;
		}

		@Override
		public void scheduleAtTime(long executionTime) {
			// stub
		}

		@Override
		public void scheduleImmediately() {
			// stub
		}

		@Override
		public void scheduleWithDelay(long delay) {
			// stub
		}

		@Override
		public long getDuration() {
			// stub
			return 0;
		}
	}

	/**
	 * Test stub for KademliaOperation.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	protected class KademliaOperationStub implements
	KademliaOperation<KademliaOverlayID> {

		private int operationID;

		public KademliaOperationStub(int opId) {
			operationID = opId;
		}

		@Override
		public SupportOperations getComponent() {
			return null;
		}

		@Override
		public int getOperationID() {
			return operationID;
		}

		@Override
		public KademliaOverlayID getResult() {
			return null;
		}

		@Override
		public boolean isFinished() {
			return true;
		}

		@Override
		public boolean isSuccessful() {
			return false;
		}

		@Override
		public void scheduleAtTime(long executionTime) {
			// stub
		}

		@Override
		public void scheduleImmediately() {
			// stub
		}

		@Override
		public void scheduleWithDelay(long delay) {
			// stub
		}

		@Override
		public void abort() {
			// stub
		}

		@Override
		public long getDuration() {
			// stub
			return 0;
		}
	}

}
