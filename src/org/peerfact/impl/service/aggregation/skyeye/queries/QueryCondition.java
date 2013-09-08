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

package org.peerfact.impl.service.aggregation.skyeye.queries;

import java.io.Serializable;

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
 * This class defines the representation of a condition of a query within
 * SkyNet. A condition consists of an operand, which compares the provided value
 * with the value of the attribute from a peer. Therefore,
 * <code>QueryCondition</code> comprises the defined operand and the comparing
 * value as well as the name and the type of the specified attribute.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 * @param <T>
 *            specifies the data-type of the value of the attribute
 */
public class QueryCondition<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5115180367686010430L;

	private String name;

	private T value;

	private String operand;

	private String type;

	public QueryCondition(String name, T value, String operand) {
		this.name = name;
		this.value = value;
		this.operand = operand;
		this.type = value.getClass().getSimpleName();
	}

	/**
	 * This method returns the name of the attribute, which is compared within
	 * this condition.
	 * 
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method returns the value for the comparison with the attribute from
	 * a peer.
	 * 
	 * @return the defined value of the attribute
	 */
	public T getValue() {
		return value;
	}

	/**
	 * This method returns the operand, which specifies the type of comparison
	 * between the defined value of this condition and the one of the attribute
	 * from a peer.
	 * 
	 * @return the defined operand
	 */
	public String getOperand() {
		return operand;
	}

	/**
	 * This method returns the type of the attribute and its value
	 * 
	 * @return the type of the attribute and its value
	 */
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return name + operand + value + " of " + type;
	}

}
