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
 * This class defines the structure of an attribute, which is disseminated
 * within SkyNet and describe a capacity of a SkyNet-node. It consists of the
 * name and the type of the attribute as well as of its value.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 06.12.2008
 * 
 * @param <T>
 *            Specifies the type of the Attribute
 */
public class Attribute<T> {

	private String name;

	private String type;

	private T value;

	public Attribute(String name, T value) {
		this.name = name;
		this.type = value.getClass().getSimpleName();
		this.value = value;
	}

	/**
	 * This method returns the name of the attribute as <code>String</code>.
	 * 
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method returns the type of the Attribute as <code>String</code>.
	 * 
	 * @return the type of the attribute
	 */
	public String getType() {
		return type;
	}

	/**
	 * This method returns the value of the Attribute, whose type is specified
	 * by the parameter <code>T</code> of the class.
	 * 
	 * @return the value of the attribute
	 */
	public T getValue() {
		return value;
	}

}
