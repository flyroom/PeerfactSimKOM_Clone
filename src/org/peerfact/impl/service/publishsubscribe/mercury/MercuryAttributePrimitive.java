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

package org.peerfact.impl.service.publishsubscribe.mercury;

import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.service.publishsubscribe.mercury.attribute.AttributeType;

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
 * A Mercury-Attribute, not to be confused with the usage of the term
 * <code>Attribute</code> in conjunction with Filters (in package
 * <code>mercury.attribute</code> and <code>mercury.filter</code>). This is
 * simply a class to store attribute names and types used by the application and
 * their ranges
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryAttributePrimitive {

	private String name = null;

	private AttributeType type = null;

	private Comparable<Integer> min = null;

	private Comparable<Integer> max = null;

	private long expiresAfter = 0;

	public MercuryAttributePrimitive() {
		// Intentionally left blank. Needed by config.xml
	}

	public String getName() {
		return this.name;
	}

	public AttributeType getType() {
		return this.type;
	}

	public Comparable<Integer> getMin() {
		return this.min;
	}

	public Comparable<Integer> getMax() {
		return this.max;
	}

	public long getExpirationTime() {
		return this.expiresAfter;
	}

	public boolean doesExpire() {
		return this.expiresAfter > 0;
	}

	/*
	 * Setters only for one-time usage through config.xml
	 */
	private boolean nameSet = false;

	private boolean typeSet = false;

	private boolean minSet = false;

	private boolean maxSet = false;

	public void setName(String name) {
		if (!nameSet) {
			this.name = name;
		}
		nameSet = true;
	}

	public void setType(String type) {
		if (!typeSet) {
			this.type = AttributeType.valueOf(type);
			if (this.type == null) {
				throw new ConfigurationException("Attribute Type " + type
						+ " is not specified for Mercury");
			}
		}
		typeSet = true;
	}

	public void setRangemin(String min) {
		if (!minSet) {
			if (this.type == AttributeType.Integer) {
				this.min = Integer.parseInt(min);
			} else {
				System.err
						.println("Other Types than Integer are not yet specified by MercuryAttributePrimitive");
			}
		}
		minSet = true;
	}

	public void setRangemax(String max) {
		if (!maxSet) {
			if (this.type == AttributeType.Integer) {
				this.max = Integer.parseInt(max);
			} else {
				System.err
						.println("Other Types than Integer are not yet specified by MercuryAttributePrimitive");
			}
		}
		maxSet = true;
	}

	public void setSubscriptionExpiresAfter(long expiresAfter) {
		this.expiresAfter = expiresAfter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (expiresAfter ^ (expiresAfter >>> 32));
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + (maxSet ? 1231 : 1237);
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		result = prime * result + (minSet ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (nameSet ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (typeSet ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MercuryAttributePrimitive other = (MercuryAttributePrimitive) obj;
		if (expiresAfter != other.expiresAfter) {
			return false;
		}
		if (max == null) {
			if (other.max != null) {
				return false;
			}
		} else if (!max.equals(other.max)) {
			return false;
		}
		if (maxSet != other.maxSet) {
			return false;
		}
		if (min == null) {
			if (other.min != null) {
				return false;
			}
		} else if (!min.equals(other.min)) {
			return false;
		}
		if (minSet != other.minSet) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nameSet != other.nameSet) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (typeSet != other.typeSet) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
