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

package org.peerfact.impl.util.functiongenerator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.functiongenerator.exceptions.FunctionNotLoadedException;
import org.peerfact.impl.util.functiongenerator.functions.Function;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This function generator acts as a container for multiple functions that are
 * loaded as specified in the configuration file of a simulation.
 * 
 * It provides the functionality to produce values for each loaded function
 * based on the current or given simulation time.
 * 
 * @author Fabio Zöllner <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class FunctionGenerator {
	private static Logger log = SimLogger.getLogger(FunctionGenerator.class);

	private static FunctionGenerator instance;

	private LinkedHashMap<Class<? extends Function>, Function> functions = new LinkedHashMap<Class<? extends Function>, Function>();

	private FunctionGenerator() {
		/* Required to be private for the use as a singleton */
	}

	public static FunctionGenerator getInstance() {
		if (FunctionGenerator.instance == null) {
			log.debug("Instantiating new FunctionGenerator");
			FunctionGenerator.instance = new FunctionGenerator();
		}

		return FunctionGenerator.instance;
	}

	/**
	 * This method loads and initializes a new function for the later use
	 * throughout the simulation.
	 * 
	 * @param function
	 *            The function to be loaded
	 */
	public final void setFunction(Function function) {
		if (!this.functions.values().contains(function)) {
			log.debug("Added " + function.toString());
			this.functions.put(function.getClass(), function);
		}
	}

	/**
	 * This method returns a collection of currently loaded function classes.
	 * 
	 * @return A collection of function classes
	 */
	public Collection<Class<? extends Function>> getLoadedFunctionClasses() {
		return this.functions.keySet();
	}

	/**
	 * This method returns the value calculated by the given function for the
	 * given simulation time.
	 * 
	 * @param simTime
	 *            The simulation time
	 * @param function
	 *            The function that shall be executed
	 * @return The value calculated by the function
	 */
	public double getValueAt(long simTime, Class<? extends Function> function) {
		Function func = functions.get(function);

		if (func == null) {
			StringBuilder message = new StringBuilder();
			message.append("The function ").append(function.getSimpleName());
			message.append(" was not loaded in the current scenario.");
			throw new FunctionNotLoadedException(message.toString());
		}

		return func.execute(simTime);
	}

	/**
	 * This method returns the derivative for the given function at the given
	 * simulation time.
	 * 
	 * @param simTime
	 *            The simulation time
	 * @param function
	 *            The function that shall be executed
	 * @return The derivative calculated by the function
	 */
	public double getDerivativeAt(long simTime,
			Class<? extends Function> function) {
		Function func = functions.get(function);

		if (func == null) {
			StringBuilder message = new StringBuilder();
			message.append("The function ").append(function.getSimpleName());
			message.append(" was not loaded in the current scenario.");
			throw new FunctionNotLoadedException(message.toString());
		}

		return func.getDerivativeAt(simTime);
	}

	/**
	 * This method returns the values calculated by all loaded functions for the
	 * given simulation time.
	 * 
	 * @param simTime
	 *            The simulation time
	 * @return The values calculated by the functions
	 */
	public Map<Class<? extends Function>, Double> getValuesAt(long simTime) {
		LinkedHashMap<Class<? extends Function>, Double> valueMap = new LinkedHashMap<Class<? extends Function>, Double>();

		for (Function func : this.functions.values()) {
			double value = func.execute(simTime);
			valueMap.put(func.getClass(), value);
		}

		return valueMap;
	}

	/**
	 * This method returns the value calculated by the given function for the
	 * current simulation time.
	 * 
	 * @param function
	 *            The function that shall be executed
	 * @return The value calculated by the function
	 */
	public double getValue(Class<? extends Function> function) {
		Simulator.getInstance();
		return getValueAt(Simulator.getCurrentTime(), function);
	}

	/**
	 * This method returns the derivative for the given function at the current
	 * simulation time.
	 * 
	 * @param function
	 *            The function that shall be executed
	 * @return The derivative calculated by the function
	 */
	public double getDerivative(Class<? extends Function> function) {
		Simulator.getInstance();
		return getDerivativeAt(Simulator.getCurrentTime(),
				function);
	}

	/**
	 * This method returns the values calculated by all loaded functions for the
	 * current simulation time.
	 * 
	 * @return The values calculated by the functions
	 */
	public Map<Class<? extends Function>, Double> getValues() {
		Simulator.getInstance();
		return getValuesAt(Simulator.getCurrentTime());
	}
}
