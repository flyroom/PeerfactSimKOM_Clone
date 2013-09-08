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

package org.peerfact.impl.service.aggregation.skyeye.analyzing;

import java.util.Iterator;

import org.peerfact.api.analyzer.ChurnAnalyzer;
import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.analyzer.KBROverlayAnalyzer;
import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.impl.common.DefaultMonitor;
import org.peerfact.impl.overlay.dht.kademlia.base.analyzer.KademliaMonitor;


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
 * This class extends the currently used monitor-class with the functionality,
 * defined by the <code>SkyNetMonitor</code>-interface. The definition of the
 * new methods is given in {@link org.peerfact.api.skynet.SkyNetMonitor},
 * while {@link DefaultMonitor} introduces the general mode of operation of a
 * monitor.
 * 
 * @author Dominik Stingl, Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SkyNetMonitor extends KademliaMonitor implements
		org.peerfact.api.service.skyeye.ISkyNetMonitor {

	@Override
	public ChurnAnalyzer getChurnAnalyzers(Class<?> clazz) {
		Iterator<ChurnAnalyzer> iter = churnAnalyzers.iterator();
		while (iter.hasNext()) {
			ChurnAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public ConnectivityAnalyzer getConnectivityAnalyzer(Class<?> clazz) {
		Iterator<ConnectivityAnalyzer> iter = connAnalyzers.iterator();
		while (iter.hasNext()) {
			ConnectivityAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public NetAnalyzer getNetAnalyzer(Class<?> clazz) {
		Iterator<NetAnalyzer> iter = netAnalyzers.iterator();
		while (iter.hasNext()) {
			NetAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public OperationAnalyzer getOperationAnalyzer(Class<?> clazz) {
		Iterator<OperationAnalyzer> iter = opAnalyzers.iterator();
		while (iter.hasNext()) {
			OperationAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public TransAnalyzer getTransAnalyzers(Class<?> clazz) {
		return null;
	}

	@Override
	public KBROverlayAnalyzer getKBROverlayAnalyzer(Class<?> clazz) {
		Iterator<KBROverlayAnalyzer> iter = kbrOverlayAnalyzers.iterator();
		while (iter.hasNext()) {
			KBROverlayAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

}
