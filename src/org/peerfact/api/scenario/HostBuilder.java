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

import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;


/**
 * HostBuilder is used to parse a portion of the configuration file and to build
 * hosts based on it. HostBuilder will use other modules already configured by
 * the <code>Configurator</code>, mainly the {@linkplain ComponentFactory
 * component factories} to create single components for this host.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 03.12.2007
 * 
 * @see Host
 * @see Configurator
 */
public interface HostBuilder extends Configurable, Builder {

	/**
	 * Return a map with all hosts indexed by ids.
	 * 
	 * @return map with id -> hosts mappings
	 */
	public Map<String, List<Host>> getAllHostsWithIDs();

	/**
	 * Return all hosts regardless group ids.
	 * 
	 * @return all hosts created
	 */
	public List<Host> getAllHosts();

	/*
	 * @see org.peerfact.api.scenario.Builder#parse(org.dom4j.Element,
	 * org.peerfact.api.scenario.Configurator)
	 */
	@Override
	public void parse(Element elem, Configurator config);

	/**
	 * Return the group of hosts with the given ID. This must be the same id as
	 * specified in the configuration file. Single hosts with an own id will be
	 * returned by this method too (as a list with only one entry).
	 * 
	 * @param id
	 *            same group or host id as in the configuration file
	 * @return list of hosts (or single host packed in a list).
	 */
	public List<Host> getHosts(String id);
}
