/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.analyzer.dbevaluation;

import java.io.Writer;
import java.util.List;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.Bandwidth;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.oracle.GlobalOracle;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class PeerPropertiesAnalyzer implements Analyzer, IOutputWriterDelegator {

	private IAnalyzerOutputWriter outputWriter;

	private static String TABLE_NAME = "PeerProperties";

	private static String UP_BANDWIDTH = "UpBandwidth";

	private static String DOWN_BANDWIDTH = "DownBandwidth";

	@Override
	public void setAnalyzerOutputWriter(
			IAnalyzerOutputWriter analyzerOutputWriter) {
		this.outputWriter = analyzerOutputWriter;
	}

	@Override
	public void start() {
		outputWriter.initialize(TABLE_NAME);
	}

	@Override
	public void stop(Writer output) {
		List<Host> hosts = GlobalOracle.getHosts();
		Bandwidth b = null;
		long time = Simulator.getCurrentTime();
		for (Host host : hosts) {
			b = host.getNetLayer().getMaxBandwidth();
			outputWriter.persist(TABLE_NAME, new AnalyzerOutputEntry(
					((IPv4NetID) host.getNetLayer().getNetID()).getID()
							.longValue(), time, UP_BANDWIDTH, b.getUpBW()));
			outputWriter.persist(TABLE_NAME, new AnalyzerOutputEntry(
					((IPv4NetID) host.getNetLayer().getNetID()).getID()
							.longValue(), time, DOWN_BANDWIDTH, b.getDownBW()));
		}
	}

}
