/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.util.movement;

import org.peerfact.impl.util.positioning.PositionVector;

/**
 * Identifier for Components or Applications that support movement. If for
 * instance a Device should be moved it has to extend this class and will be
 * notified upon each change of its position.
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/25/2011
 */
public interface MovementSupported {

	public void positionChanged();

	public void positionUnchanged();

	/**
	 * A Movement-Model will work with this PositionVector-Instance and call
	 * positionChanged or positionUnchanged when its finished
	 * 
	 * @return
	 */
	public PositionVector getPosition();

	/**
	 * Has to return true, if movement is possible (for example Net Layer is
	 * online).
	 * 
	 * @return
	 */
	public boolean movementActive();

}
