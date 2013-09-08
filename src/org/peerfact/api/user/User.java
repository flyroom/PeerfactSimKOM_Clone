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

/**
 * User interface provides methods which allow an user to communicate with an
 * application
 * 
 * @author Vasilios Darlagiannis <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface User {

	/**
	 * Starts an application which joins the p2p network
	 */
	public void join();

	/**
	 * User leaves regularly
	 */
	public void leaveRegularly();

	/**
	 * User makes an hard abort
	 */
	public void leaveImmediately();

	/**
	 * Returns user id
	 * 
	 * @return User identity
	 */
	public UserID getUserID();
}
