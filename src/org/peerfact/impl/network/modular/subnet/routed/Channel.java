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
package org.peerfact.impl.network.modular.subnet.routed;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.peerfact.api.network.NetPosition;
import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.util.positioning.PositionVector;


/**
 * This class is the base class for a routing channel - a connection between two
 * systems. It specifies the message delay, loss rate and energy loss
 * parameters.
 * 
 * As this class can be used in conjunction with a network graph it extends
 * <code>DefaultWeightedEdge</code>, but each NetworkGraph may decide what to
 * specify as the weight of an edge!
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/16/2011
 */
public abstract class Channel extends DefaultWeightedEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6853172301223483626L;

	public Channel() {
		// nothing to do
	}

	/**
	 * Time a message needs for this Channel, in Simulator-Units. This might be
	 * calculated dynamically based on the position of the devices
	 * 
	 * @return
	 */
	public abstract long getPropagationDelay();

	/**
	 * If a NetworkTopology uses Channels and changes, the graph periodically
	 * (or based on some kind of strategy) needs to update edges and their
	 * weights. This method is called in the process and is supposed to return
	 * the new weight of the edge. <b>The weight of an edge is used to determine
	 * the shortest Path for routing!</b>
	 * 
	 * The basic representation of a weight should be the propagationDelay to
	 * find fastest routes, but on some channels like wireless communication
	 * between mobile hosts with limited energy it might be a good idea to add
	 * some kind of penalty (a factor).
	 * 
	 * Due to limitations in JgraphT it is not possible to make getWeight() in
	 * DefaultWeightedEdge dynamic!
	 * 
	 * @return
	 */
	public abstract double calculateWeight();

	/**
	 * Length of this connection (distance between both Devices)
	 * 
	 * @return
	 */
	protected double getLength() {
		NetPosition posSource = ((Device) getSource()).getNetPosition();
		NetPosition posTarget = ((Device) getTarget()).getNetPosition();
		return posSource.getDistance(posTarget);
	}

	/*
	 * Used for drawing
	 */
	public PositionVector getSourcePosition() {
		return getPosition((Device) getSource());
	}

	public PositionVector getTargetPosition() {
		return getPosition((Device) getTarget());
	}

	private static PositionVector getPosition(Device d) {
		NetPosition pos = d.getNetPosition();
		if (pos instanceof PositionVector) {
			return (PositionVector) pos;
		} else {
			throw new AssertionError(
					"NetPosition is not of type PositionVector!");
		}
	}

}
