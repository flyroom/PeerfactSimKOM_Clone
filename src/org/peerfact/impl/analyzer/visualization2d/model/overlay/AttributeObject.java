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

package org.peerfact.impl.analyzer.visualization2d.model.overlay;

import java.io.Serializable;
import java.util.Map;

/**
 * An object with attributes, i.e. a Map<String, Serializable> for recording.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 27.10.2008
 * 
 */
public interface AttributeObject extends Serializable {

	/**
	 * Gives back a map of all attributes.
	 * 
	 * @return
	 */
	public Map<String, Serializable> getAttributes();

	/**
	 * Adds an attribute. <b>Attention! All attributes must be serializable!</b>
	 * 
	 * @param name
	 * @param value
	 */
	public void insertAttribute(String name, Serializable value);

	/**
	 * Returns the attribute with the key <code>name</code>.
	 * 
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name);

}
