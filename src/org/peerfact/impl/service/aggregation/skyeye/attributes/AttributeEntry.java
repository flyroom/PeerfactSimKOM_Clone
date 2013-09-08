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

import org.peerfact.api.service.skyeye.SkyNetNodeInfo;


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
 * This class embodies all attributes of a SkyNet-node and stores the calculated
 * quality of the attributes as well as the ID of the SkyNet-node to relate this
 * <code>AttributeEntry</code> to the corresponding SkyNet-node.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 */
public class AttributeEntry {

	private LinkedHashMap<String, Attribute<?>> listOfAttributes;

	private SkyNetNodeInfo nodeInfo;

	private double rank;

	private long timestamp;

	public AttributeEntry(SkyNetNodeInfo nodeInfo,
			LinkedHashMap<String, Attribute<?>> listOfAttributes, double rank,
			long timestamp) {
		this.nodeInfo = nodeInfo;
		this.listOfAttributes = listOfAttributes;
		this.rank = rank;
		this.timestamp = timestamp;
	}

	/**
	 * This method returns the {@link Attribute}-object of a single attribute,
	 * which is specified by the provided parameter of the method.
	 * 
	 * @param name
	 *            contains the name of the attribute, which shall be returned
	 * @return the <code>Attribute</code>-object of the specified attribute
	 */
	public Attribute<?> getAttribute(String name) {
		return listOfAttributes.get(name);
	}

	/**
	 * This method stores the provided {@link Attribute}-object in
	 * <code>AttributeEntry</code>. If the attribute with the corresponding name
	 * already exists within the entry, the new attribute overwrites the old
	 * one, otherwise, the new attribute is appended.
	 * 
	 * @param name
	 *            contains the name of the new attribute and is used as key
	 * @param attribute
	 *            contains the <code>Attribute</code>-object and is used as
	 *            value
	 */
	public void addAttribute(String name, Attribute<?> attribute) {
		listOfAttributes.put(name, attribute);
	}

	/**
	 * This method returns all attributes, of which this
	 * <code>AttributeEntry</code> currently exists.
	 * 
	 * @return the <code>HashMap</code> with all currently contained attributes
	 */
	public LinkedHashMap<String, Attribute<?>> getListOfAttributes() {
		return listOfAttributes;
	}

	/**
	 * This method returns the ID of the SkyNet-node, to which this
	 * <code>AttributeEntry</code> belongs.
	 * 
	 * @return the ID of a SkyNet-node as <code>SkyNetNodeInfo</code>-object
	 */
	public SkyNetNodeInfo getNodeInfo() {
		return nodeInfo;
	}

	/**
	 * This method returns the quality of the attributes, which are currently
	 * stored within this entry.
	 * 
	 * @return the quality of the <code>AttributeEntry</code>
	 */
	public double getRank() {
		return rank;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
