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

package org.peerfact.impl.scenario;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.Host;
import org.peerfact.api.scenario.Composable;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;
import org.peerfact.api.scenario.ScenarioFactory;


/**
 * Abstract factory which provides common functionality for XML-based and
 * CSV-based action factories.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 14.12.2007
 * 
 */
public abstract class AbstractScenarioFactory implements ScenarioFactory,
		Composable {

	protected HostBuilder hostBuilder;

	// Map<String, List<Host>> allHosts;
	protected Class<? extends Component> defaultComponentClass;

	protected List<Class<? extends Component>> additionalClasses = new LinkedList<Class<? extends Component>>();

	protected List<Parser> paramParsers = new LinkedList<Parser>();

	ExtendedScenario scenario;

	AbstractScenarioFactory() {
		super();
	}

	@Override
	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);
	}

	/**
	 * Default target class to execute scenario actions.
	 * 
	 * @param componentClass
	 *            - class of the component that will be used.
	 */
	public void setComponentClass(Class<? extends Component> componentClass) {
		this.defaultComponentClass = componentClass;
	}

	/**
	 * Alternative target classes which can be specified for actions via
	 * "TargetClass:methodName" where "TargetClass" will be the name of the
	 * class without the package.
	 * 
	 * @param classes
	 *            - array of classes (in config file as strings separated by ";"
	 */
	public void setAdditionalClasses(Class<? extends Component>[] classes) {
		this.additionalClasses = Arrays.asList(classes);
	}

	/**
	 * Set parser classes which will be used to convert parameter values for
	 * action methods. Note that this is not necessary for primitive types,
	 * strings and Classes.
	 * 
	 * @param parser
	 *            - parser to parse method parameters
	 */
	public void setParamParser(Parser parser) {
		this.paramParsers.add(parser);
	}

	/**
	 * Create and pre-initialize a new scenario instance.
	 * 
	 * @return
	 */
	protected ExtendedScenario newScenario() {
		// if (scenario != null)
		// return scenario;
		assert scenario == null : "Scenario already exists!";
		scenario = new ExtendedScenario(defaultComponentClass,
				additionalClasses, paramParsers);

		if (hostBuilder == null) {
			throw new ConfigurationException(
					"HostBuilder must be specified for actions to be created");
		}

		Map<String, List<Host>> allHosts = hostBuilder
				.getAllHostsWithIDs();
		scenario.setHosts(allHosts);
		return scenario;
	}

}
