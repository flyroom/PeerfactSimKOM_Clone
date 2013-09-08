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

package org.peerfact.api.service.skyeye;

/**
 * This interface defines the functionality, which a class for post-processing
 * must implement. Therefore, we divide the routine of post-processing in three
 * parts:<br>
 * <li>Reading the data of files <li>Processing the data <li>Storing the data in
 * the desired data-format
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetPostProcessing {

	/**
	 * This method is responsible for reading the data of stored files, which
	 * were created during a simulation.
	 */
	public void extractDataOfFiles();

	/**
	 * This method is responsible for processing the data, which is actually
	 * kept in the implementing class of <code>SkyNetPostProcessing</code>.
	 */
	public void processData();

	/**
	 * If it is necessary to store the processed data in further files, this
	 * method is used to write the data in a set of predefined files. The data
	 * can be written in the desired format.
	 */
	public void writeDataFile();
}
