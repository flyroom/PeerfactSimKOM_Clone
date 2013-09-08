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

package org.peerfact.impl.application.filesharing.overlayhandler;

import org.peerfact.impl.application.filesharing.FileSharingApplication;
import org.peerfact.impl.application.filesharing.documents.FileSharingDocument;

/**
 * Common set of operations every overlay has to implement for filesharing
 * purposes.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface IOverlayHandler {

	/**
	 * Looks up the resource with the given key.
	 * 
	 * @param key
	 */
	public void downloadResource(int key);

	/**
	 * Joins the overlay network.
	 */
	public void join();

	/**
	 * Leaves the overlay network.
	 */
	public void leave();

	/**
	 * Publishes the given set of resources represented by their integer rank.
	 * 
	 * @param resources
	 */
	public void publishResource(FileSharingDocument resource);

	/**
	 * Sets the filesharing application used by this node. Called in the
	 * beginning before any other operation.
	 * 
	 * @param app
	 */
	public void setFSApplication(FileSharingApplication app);

}
