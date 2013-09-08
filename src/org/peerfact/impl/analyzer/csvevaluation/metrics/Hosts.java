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

package org.peerfact.impl.analyzer.csvevaluation.metrics;

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.api.network.NetID;
import org.peerfact.impl.util.LiveMonitoring;
import org.peerfact.impl.util.LiveMonitoring.ProgressValue;


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
public class Hosts implements Metric {

	public Hosts() {
		LiveMonitoring.addProgressValue(this.new HostsProgress());
	}

	Set<NetID> hostsOnline = new LinkedHashSet<NetID>();

	Set<NetID> hostsOffline = new LinkedHashSet<NetID>();

	@Override
	public String getMeasurementFor(long time) {
		return String.valueOf(hostsOnline.size());
	}

	@Override
	public String getName() {
		return "Hosts";
	}

	public boolean hostIsOnline(NetID id) {
		return hostsOnline.contains(id);
	}

	public void hostSeen(NetID id) {
		if (!hostsOffline.contains(id)) {
			hostsOnline.add(id);
		}
	}

	public void hostRemoved(NetID id) {
		hostsOffline.remove(id);
		hostsOnline.remove(id);
	}

	public void goneOffline(NetID id) {
		hostsOnline.remove(id);
		hostsOffline.add(id);
	}

	public void goneOnline(NetID id) {
		hostsOffline.remove(id);
		hostsOnline.add(id);
	}

	/**
	 * A field in the progress window displaying the result of this operation
	 * 
	 * @author
	 * 
	 */
	public class HostsProgress implements ProgressValue {

		@Override
		public String getName() {
			return "Hosts online: ";
		}

		@Override
		public String getValue() {
			return String.valueOf(hostsOnline.size() + ", off: "
					+ hostsOffline.size());
		}

	}

}
