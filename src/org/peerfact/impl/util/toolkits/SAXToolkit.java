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

package org.peerfact.impl.util.toolkits;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peerfact.impl.util.Tuple;
import org.xml.sax.Attributes;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class SAXToolkit {

	/**
	 * Creates a new tuple list from given SAX attributes. The first element of
	 * each tuple is the attribute local name, the second one is the value. If
	 * qname is set to true, the qualified name is used instead of the local
	 * name.
	 * 
	 * @param atts
	 * @param qname
	 * @return
	 */
	public static List<Tuple<String, String>> attributesToTupleArray(
			Attributes atts, boolean qname) {
		int len = atts.getLength();
		List<Tuple<String, String>> l = new ArrayList<Tuple<String, String>>(
				len);
		for (int i = 0; i < len; i++) {
			String name = qname ? atts.getQName(i) : atts.getLocalName(i);
			l.add(new Tuple<String, String>(name, atts.getValue(i)));
		}
		return l;
	}

	/**
	 * Creates a new map from given SAX attributes. The key is the attribute
	 * local name, If qname is set to true, the qualified name is used instead
	 * of the local name.
	 * 
	 * @param atts
	 * @param qname
	 * @return
	 */
	public static Map<String, String> attributesToMap(Attributes atts,
			boolean qname) {
		int len = atts.getLength();
		Map<String, String> m = new LinkedHashMap<String, String>();
		for (int i = 0; i < len; i++) {
			String name = qname ? atts.getQName(i) : atts.getLocalName(i);
			m.put(name, atts.getValue(i));
		}
		return m;
	}

}
