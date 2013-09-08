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

package org.peerfact.api.scenario;

/**
 * This is a marker interface for so-called configurables - classes which can be
 * configured via the configuration mechanism. The way how the configurables are
 * configured is the following: For each configurable property <code>foo</code>
 * there should be the setter method <code>setFoo(Type value);</code>. Then in
 * the XML configuration file the configurable can be associated with the
 * attribute <code>foo="abc123"</code> or there can a child element
 * <code>foo</code>. In general any type of parameters are supported, but only
 * primitive types (int, short, long etc.) and strings can be configured via XML
 * attributes while complex types has to be specified via nested XML elements.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 03.12.2007
 * 
 */
public interface Configurable {
	// marker interface

}
