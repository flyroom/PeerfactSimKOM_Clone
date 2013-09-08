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

package org.peerfact.impl.application.infodissemination.moveModels;

import java.awt.Point;

import org.peerfact.impl.application.infodissemination.IDOApplication;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * An interface for the portal component. This can be implemented by a class of
 * {@link IMoveModel}. This interface provide a jump to a wide position, which
 * represent a portal. It should be call every calculation of a new position.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04/05/2011
 */
public interface IPortalComponent {

	/**
	 * This methode execute a portal with a certain probability. Additionally it
	 * sets the moveVector in the {@link IDOApplication}, to that the speed to
	 * 0.
	 * 
	 * @param actuallyPos
	 *            The actually position
	 * @param app
	 *            The IDO-Application to set the moveVector
	 * @param worldDimensionX
	 *            The world dimension in X
	 * @param worldDimensionY
	 *            The world dimension in Y
	 * @return A new position, if a portal is executed or null.
	 */
	public Point portal(Point actuallyPos, IDOApplication app,
			int worldDimensionX, int worldDimensionY);

	/**
	 * Sets the probability for a the using of a portal.
	 * 
	 * @param probability
	 */
	public void setProbability(double probability);
}
