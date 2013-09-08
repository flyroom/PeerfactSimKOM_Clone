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
package org.peerfact.impl.network.modular.device;

import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.subnet.RoutedSubnet;
import org.peerfact.impl.util.movement.MovementModel;
import org.peerfact.impl.util.movement.MovementSupported;
import org.peerfact.impl.util.positioning.PositionVector;

/**
 * A Host which is moving and therefore changing its NetPosition. Use this in
 * conjunction with the RoutedPositioning-Strategy. A MovingHosts needs a
 * MovementStrategy, this should be passed upon configuration.
 * 
 * This kind of Hosts has a connection-radius.
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/16/2011
 */
public class MovingHostDevice extends HostDevice implements MovementSupported {

	private MovementModel movement;

	public MovingHostDevice() {
		// nothing to do here
	}

	/**
	 * Copyconstructor
	 * 
	 * @param device
	 */
	protected MovingHostDevice(MovingHostDevice device) {
		super(device);
		setMovementModel(device.movement);
	}

	public void setMovementModel(MovementModel movement) {
		this.movement = movement;
		this.movement.addComponent(this);
	}

	@Override
	public void positionChanged() {
		// Notify Subnet, if it is Routed
		if (getNetLayer().getSubnet() instanceof RoutedSubnet) {
			((RoutedSubnet) getNetLayer().getSubnet()).getTopology()
					.updateDevice(this);
		}
	}

	@Override
	public void positionUnchanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean movementActive() {
		return (getNetLayer() != null && getNetLayer().isOnline());
	}

	@Override
	public PositionVector getPosition() {
		NetPosition pos = getNetPosition();
		if (pos instanceof PositionVector) {
			return (PositionVector) getNetPosition();
		} else {
			throw new AssertionError("MovementModel needs a PositionVector!");
		}
	}

	@Override
	public MovingHostDevice clone() {
		super.clone();
		return new MovingHostDevice(this);
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		super.writeBackToXML(bw);
		// nothing to write back here
	}

	@Override
	public String toString() {
		return "Moving" + super.toString();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null && this.movement == ((MovingHostDevice) obj).movement
				&& super.equals(obj))
		{
			return true;
		} else {
			return false;
		}
	}

}
