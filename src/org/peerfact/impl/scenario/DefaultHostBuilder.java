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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.scenario.Composable;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.api.scenario.HostBuilder;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.common.DefaultHostProperties;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * This builder will parse an XML subtree and create hosts as specified there.
 * It expects a tree which looks as follows: <code>
 * &lt;HostBuilder&gt;
 * 	  &lt;Host id="..." groupID="..."&gt;...
 *   &lt;Group size="..." id="..." groupID="..."&gt;...
 * &lt;HostBuilder/&gt;
 * </code>
 * 
 * The exact values for XML tags are specified as constants in this class (see
 * below).
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @author Matthias Feldotto <info@peerfact.org>
 * @version 3.0, 29.11.2007
 * 
 */
public class DefaultHostBuilder implements HostBuilder, Composable {
	/**
	 * XML attribute with this name specifies the size of the group.
	 */
	public static final String GROUP_SIZE_TAG = "size";

	/**
	 * XML element with this name specifies a group of hosts.
	 */
	public static final String GROUP_TAG = "Group";

	/**
	 * XML element with this name specifies a single host and behaves equivalent
	 * to an element with the name = GROUP_TAG value and group size of 1.
	 */
	public static final String HOST_TAG = "Host";

	/**
	 * XML attribute with this name specifies the id of the group, which is used
	 * to refer to this group lateron, e.g. when you specify scenario actions.
	 */
	public static final String ID_TAG = "id";

	/**
	 * XML attribute with this name specifies the id group id which is used for
	 * gnp mapping.
	 */
	public static final String GROUP_ID_TAG = "groupID";

	private static final Logger log = SimLogger
			.getLogger(DefaultHostBuilder.class);

	/**
	 * Groups of hosts indexed by group ids.
	 */
	protected Map<String, List<Host>> groups;

	protected int experimentSize;

	/**
	 * Flat list of all hosts.
	 */
	protected final List<Host> hosts = new LinkedList<Host>();

	/**
	 * Will be called by the configurator.
	 * 
	 * @param size
	 *            total number of hosts in the simulator TODO we could remove
	 *            this or force its correctness...
	 */
	public void setExperimentSize(int size) {
		groups = new LinkedHashMap<String, List<Host>>(size);
		this.experimentSize = size;
	}

	@Override
	public void compose(Configurator config) {
		// unused
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.scenario.HostBuilder#getAllHostsWithIDs()
	 */
	@Override
	public Map<String, List<Host>> getAllHostsWithIDs() {
		Map<String, List<Host>> hostsMap = new LinkedHashMap<String, List<Host>>(
				groups);
		return hostsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.scenario.HostBuilder#getAllHosts()
	 */
	@Override
	public List<Host> getAllHosts() {
		return hosts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.peerfact.api.scenario.HostBuilder#getHosts(java.lang.String)
	 */
	@Override
	public List<Host> getHosts(String id) {
		return groups.get(id);
	}

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
			String id = groupElem.attributeValue(ID_TAG);
			if (id == null) {
				// FIXME: remove, only for compatibility
				id = groupID;
			}
			if (id == null) {
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

				DefaultHost host = new DefaultHost();

				// initialize properties
				DefaultHostProperties hostProperties = new DefaultHostProperties();
				host.setProperties(hostProperties);
				// minimal information for host properties is the id and the
				// group id
				hostProperties.setId(id);
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
						Component comp = layer.createComponent(host);
						host.setComponent(comp);
					}
				}
				group.add(host);
			}
			log.debug("Created a group with " + group.size() + " hosts");
			hosts.addAll(group);
			groups.put(id, group);
		}
		log.info("CREATED " + hosts.size() + " hosts");
		if (hosts.size() != experimentSize) {
			log
					.warn("Only "
							+ hosts.size()
							+ " hosts were specified, though the experiment size was set to "
							+ experimentSize);
		}
	}

}
