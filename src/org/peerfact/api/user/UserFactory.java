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

package org.peerfact.api.user;

import org.peerfact.api.application.Application;
import org.peerfact.api.scenario.Configurable;

/**
 * Generates new users
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface UserFactory extends Configurable {
	/**
	 * Create new user. Note, that setApplication has to be called before.
	 * 
	 * @return User instance
	 */
	public User newUser();

	/**
	 * Set the application for new user. Concrete user factories should check
	 * whether the application implements the more specific application
	 * interface.
	 * <p/>
	 * Example: <code>setApplication("FileSharing", fsa)</code> here the user
	 * factory implementation should check whether fsa implements the required
	 * specific interface
	 * 
	 * @param name
	 *            name of the application, may identify applications type or
	 *            whatever
	 * @param application
	 *            application which will be used by new users
	 */
	public void setApplication(String name, Application application);
}
