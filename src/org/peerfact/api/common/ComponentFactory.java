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

package org.peerfact.api.common;

import org.peerfact.api.scenario.Configurable;

/**
 * Common interface for factories that is used to create components. Typically,
 * each layer implementation will have its own factory implementation. The
 * parameters of the component should be provided via <code>setXY</code>
 * methods. This way a concrete factory can be configured from config files. The
 * simulator's configurator will call <code>setFoo(value)</code> method for each
 * attribute or element <code>foo</code> found in the configuration file (inside
 * of the factory specification element).
 * <p>
 * Note: This is an instantiation of the <i>Abstract Factory</i> design pattern.
 * 
 * @see org.peerfact.api.common#Component
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 03.12.2007
 * 
 */
public interface ComponentFactory extends Configurable {
	/**
	 * Create and return a new component specific to this factory. The factory
	 * may use the host parameter to obtain references to other components
	 * required (e.g. overlay factory will typically need a transport manager).
	 * 
	 * @param host
	 *            - host where this component will run.
	 * @return new component instance
	 */
	public Component createComponent(Host host);
}
