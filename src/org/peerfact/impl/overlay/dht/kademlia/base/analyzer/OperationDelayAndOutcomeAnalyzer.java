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
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.api.common.Operation;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.IKademliaAnalyzer.KademliaOperationAnalyzer;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.util.AvgAccumulator;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation.OperationState;
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
 * Collects data about how long operations take to execute. The analysis can be
 * restricted by indicating a superclass for the operations to consider. The
 * average delay is computed for each implementation (=class) and outcome
 * (=final state) of an operation separately.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class OperationDelayAndOutcomeAnalyzer extends FileAnalyzer implements
		KademliaOperationAnalyzer {

	// /** The expected number of characters per output file line. */
	// private static final int APPROX_CHARS_PER_LINE = 60;
	//
	// /** The expected number of lines. */
	// private static final int APPROX_NUM_OF_LINES = 150000;

	/**
	 * The initial capacity of the LinkedHashMap that contains the currently
	 * running (and analysed) operations.
	 */
	private static final int RUNNING_OPERATIONS_STARTING_CAPACITY = 50;

	/** The supertype of all measured Operations. */
	private Class<? extends Operation<?>> superOperation;

	/**
	 * A Map that maps the IDs of Operations to the point in time at which they
	 * have been started.
	 */
	private Map<Integer, Long> startedOperationIDs;

	/**
	 * A Map that stores the current average for each class/type and outcome of
	 * Operation.
	 */
	private Map<OperationState, Map<Class<? extends Operation<?>>, AvgAccumulator>> runningAverages;

	/**
	 * Constructs a new OperationDelayAnalyser.
	 */
	public OperationDelayAndOutcomeAnalyzer() {
		// super(APPROX_CHARS_PER_LINE * APPROX_NUM_OF_LINES);
		setOperationSupertype(Operation.class);
	}

	/**
	 * Sets a supertype for Operations that are considered in this measurement.
	 * Data will be collected only about Operations that fulfill the
	 * <code>"instanceof superOperationClass"</code> test. Events triggered by
	 * other operations (those that are not a subtype of
	 * <code>superOperationClass</code>) are ignored. If no specific type is
	 * set, the default value (any subtype of Operation will be analysed) will
	 * be used. This method has no effect if start() has already been called.
	 * 
	 * @param <C>
	 *            the type of the <code>superOperation</code> class object.
	 * @param superOperationClass
	 *            a Class object, parameterised with the type of Operation that
	 *            all Operations that will be analysed have to be a subtype of.
	 */
	public final <C extends Operation<?>> void setOperationSupertype(
			final Class<C> superOperationClass) {
		if (!isStarted()) {
			superOperation = superOperationClass;
		}
	}

	/**
	 * Sets the supertype of all Operations that will be analysed in this
	 * OperationDelayAnalyser. This method is a convenience method that permits
	 * to set the class name as a String as used in input XML-files. It is
	 * equivalent to {@link #setOperationSupertype(Class)} except that this
	 * method allows to pass the supertype as a String. If this method is not
	 * called, the default of "org.peerfact.api.common.Operation" will be
	 * used.
	 * <p>
	 * If the given class does not exist or is not a subtype of Operation, an
	 * exception will be thrown.
	 * 
	 * @param superOperationClassName
	 *            the class name of the Operation subtype that will be a
	 *            supertype of all Operations analysed here.
	 * @throws ClassNotFoundException
	 *             if the given class does not exist.
	 * @throws ClassCastException
	 *             if the given class name is not a subtype of Operation.
	 */
	public final void setRestrictToSubtypesOf(
			final String superOperationClassName) throws ClassNotFoundException {
		Class<? extends Operation<?>> clazz = (Class<? extends Operation<?>>) Class
				.forName(superOperationClassName);
		setOperationSupertype(clazz);
	}

	/**
	 * Called by the superclass to start the operation latency analysis.
	 * Operations that have been started prior to invoking this method are not
	 * taken into account for analysis (even if they complete after start() has
	 * been invoked). Calling this method starts a <i>fresh</i> measurement. The
	 * supertype of Operations to consider in the measurement has to be set
	 * before invoking this method.
	 */
	@Override
	protected final void started() {
		startedOperationIDs = new LinkedHashMap<Integer, Long>(
				RUNNING_OPERATIONS_STARTING_CAPACITY);
		runningAverages = new LinkedHashMap<OperationState, Map<Class<? extends Operation<?>>, AvgAccumulator>>();
		appendToFile("# OPERATION_ID CLASS FINAL_STATE LATENCY(SECONDS) ");
		appendToFile("PEER_ID START(SIM_TIME) STOP(SIM_TIME)");
		appendNewLine();
	}

	/**
	 * This method should be called whenever an operation has been triggered.
	 * (It is assumed that an operation is not triggered twice and that no two
	 * operations have the same operationID.)
	 * 
	 * @param op
	 *            the Operation that has been triggered.
	 */
	@Override
	public final void operationInitiated(
			final AbstractKademliaOperation<?, ?> op) {
		if (!isStarted()) {
			return;
		}
		// check type of Operation
		if (superOperation.isInstance(op)) {
			startedOperationIDs.put(op.getOperationID(),
					Simulator.getCurrentTime());
		}
	}

	/**
	 * This method should be called whenever an operation has completed. (It is
	 * assumed that an operation is not triggered twice and that no two
	 * operations have the same operationID.)
	 * 
	 * @param op
	 *            the Operation that has completed.
	 */
	@Override
	public final void operationFinished(final AbstractKademliaOperation<?, ?> op) {
		if (!isStarted()) {
			return;
		}
		final Long startingTime = startedOperationIDs.remove(op
				.getOperationID());
		if (startingTime == null) {
			// operation unknown
			return;
		}
		final long duration = Simulator.getCurrentTime() - startingTime;
		final AvgAccumulator avg = getAvgAcc(op);
		avg.addToTotal(duration);
		appendToFile(op.getOperationID());
		appendSeparator();
		appendToFile(op.getClass().getName());
		appendSeparator();
		appendToFile(op.getState().toString());
		appendSeparator();
		appendToFile(duration / (double) Simulator.SECOND_UNIT);
		// TODO: remove?
		appendSeparator();
		appendToFile(op.getComponent().getTypedOverlayID().toString());
		appendSeparator();
		appendToFile(startingTime);
		appendSeparator();
		appendToFile(Simulator.getCurrentTime());
		appendNewLine();
	}

	/**
	 * Called by the superclass to stop the operation latency analysis.
	 * 
	 * @param output
	 *            a Writer on which a textual representation of the measurements
	 *            will be written.
	 */
	@Override
	protected final void stopped(final Writer output) throws IOException {
		long sum = 0;

		output.write("\n******************** ");
		output.write("Operation Duration Statistics ");
		output.write("********************\n");
		output.write(" \t\tLATENCY (sec)\t\t(sim. units)\t\tCOUNT\tOPERATION TYPE\n");

		for (Map.Entry<OperationState, Map<Class<? extends Operation<?>>, AvgAccumulator>> outcomeEntry : runningAverages
				.entrySet()) {
			output.write(" -- " + outcomeEntry.getKey() + " -- \n");
			for (Map.Entry<Class<? extends Operation<?>>, AvgAccumulator> entry : outcomeEntry
					.getValue().entrySet()) {
				output.write(String.format(
						" AVERAGE: \t%1$15.14g \t%2$15.14g \t%3$d "
								+ "\t%4$s\n", entry.getValue().getAverage()
								/ Simulator.SECOND_UNIT, entry.getValue()
								.getAverage(), entry.getValue().getCount(),
						entry.getKey().getName()));
				output.write(String.format(" MIN: \t\t%1$15.14g \t%2$15.14g\n",
						entry.getValue().getMin() / Simulator.SECOND_UNIT,
						entry.getValue().getMin()));
				output.write(String.format(" MAX: \t\t%1$15.14g \t%2$15.14g\n",
						entry.getValue().getMax() / Simulator.SECOND_UNIT,
						entry.getValue().getMax()));
				sum += entry.getValue().getCount();
			}
			output.write("\n");
		}

		output.write(" (" + sum + " operations analysed; further "
				+ startedOperationIDs.size()
				+ " operations are still running)\n");
		output.write("****************** Operation Duration Statistics End ******************\n");
		output.flush();
	}

	/**
	 * Commented out, because it is never used
	 * 
	 * @return a Map that contains all encountered Operation types as keys and
	 *         the average duration of their respective execution as values.
	 */
	// private final Map<OperationState, Map<Class<? extends Operation<?>>,
	// Double>> getAvgOperationLatencies() {
	// final Map<OperationState, Map<Class<? extends Operation<?>>, Double>>
	// result;
	// result = new LinkedHashMap<OperationState, Map<Class<? extends
	// Operation<?>>, Double>>(
	// runningAverages.size(), 1.0f);
	// if (runningAverages == null) {
	// return result; // empty
	// }
	//
	// Map<Class<? extends Operation<?>>, Double> oneOutcomeResult;
	//
	// for (final Map.Entry<OperationState, Map<Class<? extends Operation<?>>,
	// AvgAccumulator>> outcomeEntry : runningAverages
	// .entrySet()) {
	// oneOutcomeResult = new LinkedHashMap<Class<? extends Operation<?>>,
	// Double>(
	// outcomeEntry.getValue().size(), 1.0f);
	// result.put(outcomeEntry.getKey(), oneOutcomeResult);
	// for (final Map.Entry<Class<? extends Operation<?>>, AvgAccumulator> entry
	// : outcomeEntry
	// .getValue().entrySet()) {
	// oneOutcomeResult.put(entry.getKey(), entry.getValue()
	// .getAverage());
	// }
	// }
	//
	// return result;
	// }

	private AvgAccumulator getAvgAcc(final AbstractKademliaOperation<?, ?> op) {
		Map<Class<? extends Operation<?>>, AvgAccumulator> outcomeMap;
		outcomeMap = runningAverages.get(op.getState());
		if (outcomeMap == null) {
			outcomeMap = new LinkedHashMap<Class<? extends Operation<?>>, AvgAccumulator>();
			runningAverages.put(op.getState(), outcomeMap);
		}
		AvgAccumulator avg = outcomeMap.get(op.getClass());
		if (avg == null) {
			avg = new AvgAccumulator();
			putAvgAcc(outcomeMap, op.getClass(), avg);
		}
		return avg;
	}

	// necessary because of generics (?)
	private static <C extends Operation<?>> void putAvgAcc(
			final Map<Class<? extends Operation<?>>, AvgAccumulator> outcomeMap,
			final Class<C> cl, AvgAccumulator acc) {
		outcomeMap.put(cl, acc);
	}
}
