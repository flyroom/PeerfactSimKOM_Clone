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

package org.peerfact.impl.service.aggregation.skyeye.attributes;

import java.util.LinkedHashMap;

import org.peerfact.api.common.Host;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetHostProperties;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.simengine.Simulator;


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
 * This class implements the collection of attributes at a single SkyNet-node.
 * Within this class all attributes, which are defined by SkyNet and of which a
 * node disposes, are read from {@link SkyNetHostProperties} and written in an
 * {@link AttributeEntry}, that incloses all attributes of one SkyNet-node.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class AttributeCollector {

	private SkyNetNodeInterface skyNetNode;

	private float cpuCoefficient;

	private float downBandwidthCoefficient;

	private float ramCoefficient;

	private float storageCoefficient;

	private float onlineCoefficient;

	private SkyNetPropertiesReader propReader;

	public AttributeCollector(SkyNetNodeInterface skyNetNode) {
		this.skyNetNode = skyNetNode;
		propReader = SkyNetPropertiesReader.getInstance();
		cpuCoefficient = propReader.getFloatProperty("CPUCoefficient");
		downBandwidthCoefficient = propReader
				.getFloatProperty("DownBandwidthCoefficient");
		ramCoefficient = propReader.getFloatProperty("RAMCoefficient");
		storageCoefficient = propReader.getFloatProperty("STORAGECoefficient");
		onlineCoefficient = propReader.getFloatProperty("ONLINECoefficient");
	}

	/**
	 * This method is responsible for reading all attributes from
	 * {@link SkyNetHostProperties} and for calculating the quality of the
	 * attributes, that are stored within {@link AttributeEntry}. The single
	 * attributes are multiplied with their corresponding weights. As this
	 * method actually multiplies these weights with the absolute values of the
	 * attributes instead of the relative values (normalized to the interval
	 * between 0 and 1), they are currently obsolete and therefore do not differ
	 * from each other, since the weighting derives from the absolute values of
	 * the attributes. As already mentioned, all attributes of a node as well as
	 * the quality of the attributes are stored within an
	 * <code>AttributeEntry</code>, which is returned as a result of this
	 * method.
	 * 
	 * @return the created <code>AttributeEntry</code> for this SkyNet-node
	 */
	public AttributeEntry collectOwnData() {
		LinkedHashMap<String, Attribute<?>> list = new LinkedHashMap<String, Attribute<?>>();
		Host host = skyNetNode.getHost();
		SkyNetHostProperties properties = (SkyNetHostProperties) host
				.getProperties();
		Attribute<?> temp;
		double rank = 0;
		for (int i = 0; i < properties.getHostProperties().size(); i++) {
			temp = properties.getHostProperties().get(i);
			if (temp.getName().equals("DownBandwidth")) {
				rank = rank
						+ normalizeValue(
								propReader
										.getIntProperty("LowerBoundDownBandwidth"),
								propReader
										.getIntProperty("UpperBoundDownBandwidth"),
								(Double) temp.getValue())
						* downBandwidthCoefficient;
			} else if (temp.getName().equals("CPU")) {
				rank = rank
						+ normalizeValue(propReader
								.getIntProperty("LowerBoundCPU"), propReader
								.getIntProperty("UpperBoundCPU"),
								(Integer) temp.getValue()) * cpuCoefficient;
			} else if (temp.getName().equals("RAM")) {
				rank = rank
						+ normalizeValue(propReader
								.getIntProperty("LowerBoundRAM"), propReader
								.getIntProperty("UpperBoundRAM"),
								(Integer) temp.getValue()) * ramCoefficient;
			} else if (temp.getName().equals("Storage")) {
				rank = rank
						+ normalizeValue(propReader
								.getIntProperty("LowerBoundStorage"),
								propReader.getIntProperty("UpperBoundStorage"),
								(Integer) temp.getValue()) * storageCoefficient;
			}
			list.put(temp.getName(), temp);
		}
		Attribute<Double> online = new Attribute<Double>("AvgOnline",
				((SkyNetNode) skyNetNode).getAverageOnlineTime());
		rank = rank
				+ normalizeValue(propReader
						.getTimeProperty("LowerBoundOnlineTime"), propReader
						.getTimeProperty("UpperBoundOnlineTime"), online
						.getValue()) * onlineCoefficient;

		list.put(online.getName(), online);
		Attribute<String> time = new Attribute<String>("CreatingTime",
				Simulator.getFormattedTime(Simulator.getCurrentTime()));
		list.put(time.getName(), time);
		return new AttributeEntry(skyNetNode.getSkyNetNodeInfo(), list, rank,
				Simulator.getCurrentTime());
	}

	private static double normalizeValue(long lowerBound, long upperBound,
			double currentValue) {
		if (currentValue < lowerBound) {
			return 0d;
		} else if (currentValue > upperBound) {
			return 1d;
		} else {
			return (currentValue - lowerBound) / (upperBound - lowerBound);
		}
	}
}
