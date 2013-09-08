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

package org.peerfact.api.scenario;

/**
 * An implementation of this interface is used to access and probably parse the
 * file describing the configuration of the simulator. The simulator is
 * configured from XML files with a flexible but still constrained structure.
 * Few features of this files are
 * <ul>
 * <li>Support for variables which can be passed to the configurator from
 * outside (e.g. command line)
 * <li>Arbitrary new components can be configured via this configurator without
 * to change the configurator
 * </ul>
 * TODO describe an example config file and the rules (or point to the
 * documentation)
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 03.12.2007
 * 
 */
public interface Configurator {

	/**
	 * This tag denotes the root element of the XML-file that embodies the
	 * configuration
	 */
	public static final String CONFIGURATION_ROOT_TAG = "Configuration";

	/**
	 * Tag to be used in the config file in order to define some default values.
	 */
	public final static String DEFAULT_TAG = "Default";

	/**
	 * This tag is used in the config-file in order to access the xml-element,
	 * that specifies the component, which implements and represents the basis
	 * for the simulator
	 */
	public static final String CORE = "SimulatorCore";

	/**
	 * This tag is used in the config-file in order to access the xml-element,
	 * that embodies the logic for creating the defined amount of peers
	 */
	public static final String HOST_BUILDER = "HostBuilder";

	/**
	 * This tag is used in the config-file in order to access the xml-element,
	 * that comprises the functionality to extract the action out of different
	 * types of action files
	 */
	public static final String SCENARIO_TAG = "Scenario";

	/**
	 * Tag to be used in the config file in order to define default values for a
	 * single variable.
	 */
	public final static String VARIABLE_TAG = "Variable";

	/**
	 * Predefined name of the attribute within the <code>Variable</code>
	 * -element, that contains the name of the variable as value
	 */
	public static final String VARIABLE_NAME_TAG = "name";

	/**
	 * Predefined name of the attribute within the <code>Variable</code>
	 * -element, that contains the value of the variable as value
	 */
	public static final String VARIABLE_VALUE_TAG = "value";

	/**
	 * Tag for the nested element within a <code>host</code>- oder
	 * <code>group</code>-element, that defines the values of the given
	 * properties for a single host or a group of hosts
	 */
	public static final String HOST_PROPERTIES_TAG = "Properties";

	/**
	 * Tag for the nested element within the <code>Scenario</code>-element,
	 * which is used to create a new action method inside config-file
	 */
	public static final String ACTION_TAG = "Action";

	/**
	 * Get a configurable which was already parsed and configured. This is the
	 * common way how configurables can access each other during the
	 * configuration process or how the simulation engine can obtain the
	 * configuration result.
	 * 
	 * @param name
	 *            - configurable's name (which is same as in the configuration
	 *            file).
	 * 
	 * @return configured configurable or null if there is no configurable with
	 *         the given name
	 */
	public Configurable getConfigurable(String name);

	/**
	 * Parse value. If the argument starts with a <b>$</b> sign then it will be
	 * interpreted as a variable name and the variable value will be returned.
	 * If the variable was not set the result will be <code>null</code>. If the
	 * argument contains no <b>$</b> sign, the <code>valueString</code>
	 * parameter will be just forwarded as the return value.
	 * 
	 * @param valueString
	 *            - string to be parsed, either plain value or variable name
	 *            with preceding $
	 * @return the value of the variable named <code>valueString</code>, null,
	 *         or the valueString
	 */
	public String parseValue(String valueString);
}
