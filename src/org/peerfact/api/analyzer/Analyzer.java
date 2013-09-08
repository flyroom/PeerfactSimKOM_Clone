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

package org.peerfact.api.analyzer;

import java.io.Writer;

import org.peerfact.api.common.Monitor;


/**
 * In general, analyzers are used to receive notifications about actions that
 * took place on specific components, for instance the sending or receiving of
 * messages. In particular, analyzers are able to collect data during a
 * simulation run and prepare the results at the end of a simulation.
 * 
 * Note that analyzers must be registered by an implementation of the
 * {@link Monitor} interface by using the xml configuration file before the
 * simulation starts.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 4.0, 03/10/2011
 * 
 */
public interface Analyzer {

	/**
	 * Invoking this method denotes start running analyzer
	 * 
	 */
	public void start();

	/**
	 * Invoking this method denotes stop running analyzer. Furthermore, all
	 * results have to be prepared and printed out using the given writer
	 * 
	 * @param output
	 *            the given output writer
	 */
	public void stop(Writer output);
}
