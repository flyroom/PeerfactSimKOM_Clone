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

package org.peerfact.impl.service.aggregation.skyeye;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.scenario.DefaultConfigurator;
import org.peerfact.impl.scenario.DefaultHostBuilder;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.ChurnStatisticsAnalyzer;
import org.peerfact.impl.util.logging.SimLogger;


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
 * This builder will parse an XML subtree and create hosts as specified there.
 * It expects a tree which looks as follows: <code>
 * &lt;HostBuilder&gt;
 * 	  &lt;Host groupID="..."&gt;...
 *   &lt;Group size="..." groupID="..."&gt;...
 * &lt;HostBuilder/&gt;
 * </code>
 * 
 * The exact values for XML tags are specified as constants in this class (see
 * below).
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 29.11.2007
 * 
 */
public class SkyNetHostBuilder extends DefaultHostBuilder {

	private static final Logger log = SimLogger
			.getLogger(SkyNetHostBuilder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.scenario.HostBuilder#parse(org.dom4j.Element,
	 * org.peerfact.api.scenario.Configurator)
	 */
	@Override
	public void parse(Element elem, Configurator config) {
		DefaultConfigurator defaultConfigurator = (DefaultConfigurator) config;

		// create groups
		for (Iterator<?> iter = elem.elementIterator(); iter.hasNext();) {
			Element groupElem = (Element) iter.next();
			String groupID = groupElem.attributeValue(GROUP_ID_TAG);
			if (groupID == null) {
				throw new IllegalArgumentException("Id of host/group "
						+ groupElem.asXML() + " must not be null");
			}

			// either a group of hosts or a single host (=group with size 1)
			int groupSize;
			if (groupElem.getName().equals(HOST_TAG)) {
				groupSize = 1;
			} else if (groupElem.getName().equals(GROUP_TAG)) {
				String attributeValue = config.parseValue(groupElem
						.attributeValue(GROUP_SIZE_TAG));
				groupSize = Integer.parseInt(attributeValue);
			} else {
				throw new IllegalArgumentException("Unexpected tag "
						+ groupElem.getName());
			}
			List<Host> group = new ArrayList<Host>(groupSize);

			// create hosts and instances of specified components for each host
			for (int i = 0; i < groupSize; i++) {
				log
						.info((i + 1) + ". host of Group " + groupID
								+ " is created");
				DefaultHost host = new DefaultHost();

				// initialize properties
				// Changed the constructor to SkyNetHostProperties to add some
				// additional properties
				SkyNetHostProperties hostProperties = new SkyNetHostProperties();
				host.setProperties(hostProperties);
				// minimal information for host properties is the group id
				hostProperties.setGroupID(groupID);

				// initialize layers and properties
				for (Iterator<?> layers = groupElem.elementIterator(); layers
						.hasNext();) {
					Element layerElem = (Element) layers.next();
					if (layerElem.getName().equals(
							Configurator.HOST_PROPERTIES_TAG)) {
						defaultConfigurator.configureAttributes(hostProperties,
								layerElem);
					} else {
						// layer component factory
						ComponentFactory layer = (ComponentFactory) defaultConfigurator
								.configureComponent(layerElem);
						log.debug("Factory is " + layer + " for wanted elem "
								+ layerElem.asXML());
						Component comp = layer.createComponent(host);

						host.setComponent(comp);
					}
				}
				group.add(host);
				log.info("->" + host.toString());
			}
			log.info("------Created group " + groupID + " with " + group.size()
					+ " hosts------");
			hosts.addAll(group);
			groups.put(groupID, group);
		}
		log.info("******CREATION OF HOSTS IS FINISHED. CREATED " + hosts.size()
				+ " HOSTS******");
		if (ChurnStatisticsAnalyzer.isActivated()) {
			ChurnStatisticsAnalyzer.setCreatedHost(getAllHosts());
		}
		if (hosts.size() != experimentSize) {
			log
					.warn("Only "
							+ hosts.size()
							+ " hosts were specified, though the experiment size was set to "
							+ experimentSize);
		}
	}

}
