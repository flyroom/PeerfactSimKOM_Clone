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

package org.peerfact.impl.application.aggregationsimple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.scenario.XMLConfigurableConstructor;
import org.peerfact.impl.service.aggregation.AggregationToolkit;
import org.peerfact.impl.service.aggregation.oracle.AggregationServiceOracle;
import org.peerfact.impl.service.aggregation.oracle.OracleUniverse;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.stats.distributions.Distribution;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SimpleAggregationAppFactory implements ComponentFactory {

	File outputFile = null;

	BufferedWriter output = null;

	Map<String, Distribution> distributions = null;

	static final Logger log = SimLogger
			.getLogger(SimpleAggregationAppFactory.class);

	OracleUniverse orUniv = new OracleUniverse();

	@XMLConfigurableConstructor({ "outputFile" })
	public SimpleAggregationAppFactory(String outputFile) {
		this.outputFile = new File(Simulator.getOuputDir(), outputFile);
		distributions = new LinkedHashMap<String, Distribution>();
	}

	@Override
	public Component createComponent(Host host) {
		if (output == null) {
			try {
				output = prepareOutputStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return new SimpleAggregationApp(host, distributions, output,
				new AggregationServiceOracle(orUniv));
	}

	private BufferedWriter prepareOutputStream() throws IOException {
		outputFile.getParentFile().mkdirs();
		FileWriter wr = new FileWriter(outputFile);
		BufferedWriter buf = new BufferedWriter(wr);
		buf.write(AggregationToolkit.printDescLineCSV() + "\t"
				+ AggregationToolkit.printDescLineCSV() + "\n");
		buf.flush();
		return buf;
	}

	public void setDistribution(NamedDistribution dist) {
		distributions.put(dist.getName(), dist.getValue());
		log.debug("Distribution set " + dist);
	}

}
