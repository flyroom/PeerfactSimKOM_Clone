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

package org.peerfact.impl.analyzer.visualization2d.model.events;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.analyzer.visualization2d.model.overlay.AttributeObject;


/**
 * Changes the attributes in an object
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 27.10.2008
 * 
 */
public class AttributesChanged extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6124586429312315638L;

	/**
	 * Attributes which should be changed, and their new values​​.
	 */
	private Map<String, Serializable> newAttributes;

	/**
	 * Keys and values ​​of the changed attributes, as it was immediately before
	 * calling the event.
	 */
	private Map<String, Serializable> pastAttributes;

	private AttributeObject obj;

	public AttributesChanged(AttributeObject obj,
			Map<String, Serializable> changedAttributes) {
		this.newAttributes = changedAttributes;
		this.obj = obj;
	}

	@Override
	public void makeHappen() {
		Map<String, Serializable> objAttrs = obj.getAttributes();
		if (objAttrs == null) {
			System.out
					.println("Warning: Trying to change attributes from an object "
							+ "that does not exist or has no attributes.");
			return;
		}
		pastAttributes = new LinkedHashMap<String, Serializable>();
		for (String key : newAttributes.keySet()) {
			pastAttributes.put(key, objAttrs.get(key)); // Saves the old value
														// to enable undo if
														// necessary
		}
		objAttrs.putAll(newAttributes);

	}

	@Override
	public void undoMakeHappen() {

		for (String key : pastAttributes.keySet()) {
			Serializable value = pastAttributes.get(key);
			if (value != null) {
				pastAttributes.put(key, value);
			} else {
				pastAttributes.remove(key);
			}
		}

		obj.getAttributes().putAll(pastAttributes);
	}

	@Override
	public String toString() {
		return "AttributesChanged: " + newAttributes.toString();
	}
}
