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

package org.peerfact.impl.service.publishsubscribe.mercury.filter;

import org.peerfact.impl.service.publishsubscribe.mercury.attribute.AttributeType;
import org.peerfact.impl.service.publishsubscribe.mercury.attribute.IMercuryAttribute;

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
 * Abstract Base-Class for IMercuryFilter. An implementation of
 * AbstractMercuryFilter should overwrite all supported operator-Methods
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class AbstractMercuryFilter implements IMercuryFilter {

	/**
	 * The name of the attribute
	 */
	private String name;

	/**
	 * The type of the attribute
	 */
	private AttributeType type;

	/**
	 * The operator type for the filter.
	 */
	private OPERATOR_TYPE operator;

	public AbstractMercuryFilter(String name, AttributeType type,
			OPERATOR_TYPE operator) {
		this.name = name;
		this.type = type;
		this.operator = operator;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AttributeType getType() {
		return type;
	}

	@Override
	public OPERATOR_TYPE getOperator() {
		return operator;
	}

	@Override
	public boolean match(IMercuryAttribute attribute) {

		if (!attribute.getName().equals(this.getName())
				|| !attribute.getType().equals(this.getType())) {
			// Only match, if Attribute and Type are the same - should be
			// implemented by MercurySubscription
			return true;
		}

		switch (this.getOperator()) {
		case equals:
			return this.operatorEquals(attribute);
		case greater:
			return this.operatorGreater(attribute);
		case greaterEquals:
			return this.operatorGreaterEquals(attribute);
		case smaller:
			return this.operatorSmaller(attribute);
		case smallerEquals:
			return this.operatorSmallerEquals(attribute);
		case postfix:
			return this.operatorPostfix(attribute);
		case prefix:
			return this.operatorPrefix(attribute);
		default:
			throw new RuntimeException("Operator " + this.getOperator()
					+ " is not defined in " + this.getClass().getName());
		}
	}

	/*
	 * Operators should be overwritten by implementing Classes
	 */

	public boolean operatorEquals(IMercuryAttribute attribute) {
		return this.getValue().equals(attribute.getValue());
	}

	public boolean operatorGreater(IMercuryAttribute attribute) {
		this.operatorNotDefined();
		return false;
	}

	public boolean operatorGreaterEquals(IMercuryAttribute attribute) {
		this.operatorNotDefined();
		return false;
	}

	public boolean operatorSmaller(IMercuryAttribute attribute) {
		this.operatorNotDefined();
		return false;
	}

	public boolean operatorSmallerEquals(IMercuryAttribute attribute) {
		this.operatorNotDefined();
		return false;
	}

	public boolean operatorPostfix(IMercuryAttribute attribute) {
		this.operatorNotDefined();
		return false;
	}

	public boolean operatorPrefix(IMercuryAttribute attribute) {
		this.operatorNotDefined();
		return false;
	}

	private void operatorNotDefined() {
		throw new RuntimeException("Operator " + this.getOperator()
				+ " is not defined for AttributeType "
				+ this.getType().toString());
	}

	@Override
	public String toString() {
		return this.getName() + this.getOperator().toString() + this.getValue();
	}

	@Override
	public long getTransmissionSize() {
		return name.getBytes().length + 1; // +1 for operator
	}

}
