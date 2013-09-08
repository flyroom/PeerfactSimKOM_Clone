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

package org.peerfact.impl.util.db.relational;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public interface XMLSerializable {

	/**
	 * Writes the object to the file. The XML stream writer MUST always be in
	 * the state AFTER writing an elements name, so that this method has no
	 * influence anymore on the name of the element, but can continue to write
	 * attributes to it.
	 * 
	 * @param wr
	 * @throws XMLStreamException
	 */
	public void writeToXML(XMLStreamWriter wr) throws XMLStreamException;

}
