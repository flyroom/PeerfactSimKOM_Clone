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

package org.peerfact.impl.overlay.informationdissemination;

import java.awt.Point;
import java.util.List;

import org.peerfact.api.overlay.BootstrapManager;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONode;
import org.peerfact.api.overlay.ido.IDONodeInfo;
import org.peerfact.impl.application.infodissemination.IDOApplication;
import org.peerfact.impl.overlay.AbstractOverlayNode;


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
 * Abstract class for IDO implementation. It provides a set of functions which
 * are helpfully for a implementation of an IDO node.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 * 
 * @param <T extens OverlayID> Instances of a {@link OverlayID} can be used in
 *        this abstract class.
 */
public abstract class AbstractIDONode<T extends OverlayID<?>, S extends OverlayContact<T>>
		extends AbstractOverlayNode<T, S> implements IDONode<T, S> {

	/**
	 * The position of the node in the virtual world
	 */
	private Point position;

	/**
	 * The AOI radius of the node
	 */
	private int aoi;

	/**
	 * @param peerId
	 *            An OverlayID for this node.
	 * @param port
	 *            The port for the incoming messages.
	 * @param aoi
	 *            The AOI radius of this node.
	 */
	protected AbstractIDONode(T peerId, short port, int aoi) {
		super(peerId, port);
		setAOI(aoi);
	}

	/**
	 * Gets the position of this node return.
	 * 
	 * @return The actually position of this node.
	 */
	@Override
	public Point getPosition() {
		return position;
	}

	/**
	 * Sets the position of the node.
	 * 
	 * @param position
	 *            The new position for this node.
	 */
	protected void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * Sets the AOI radius for this node
	 * 
	 * @param aoi
	 *            the AOI radius
	 */
	@Override
	public void setAOI(int aoi) {
		this.aoi = aoi;
	}

	/**
	 * Gets the AOI radius for this node
	 * 
	 * @return The AOI radius
	 */
	@Override
	public int getAOI() {
		return aoi;
	}

	/**
	 * Gets the associated application for this node.
	 * 
	 * @return The associated application for this node.
	 */
	public IDOApplication getApplication() {
		if (getHost() != null && getHost().getApplication() != null
				&& getHost().getApplication() instanceof IDOApplication) {
			return (IDOApplication) getHost().getApplication();
		}
		return null;
	}

	/**
	 * Node leave the overlay.
	 * 
	 * @param crash
	 *            If <code>true</code> then, the node goes offline without to
	 *            execute a routine. Otherwise it can execute a routine.
	 */
	@Override
	public abstract void leave(boolean crash);

	/**
	 * Join with the given position to the overlay.
	 * 
	 * @param determinedPosition
	 *            The position on the map, where the node join.
	 */
	@Override
	public abstract void join(Point determinedPosition);

	/**
	 * Disseminate the position to the nodes, that are interested of this
	 * information. Additionally sets the position for this node.
	 * 
	 * @param determinedPosition
	 *            The position, which should be disseminated
	 */
	@Override
	public abstract void disseminatePosition(Point determinedPosition);

	/**
	 * Gets a list of nodes back, that the node knows. Thats are nodes, which
	 * are in the AOI and Nodes, which are used for the connectivity of the
	 * overlay.
	 * 
	 * @return A list of {@link IDONodeInfo}.
	 */
	@Override
	public abstract List<IDONodeInfo> getNeighborsNodeInfo();

	/**
	 * Gets the {@link BootstrapManager} back.
	 * 
	 * @return The {@link BootstrapManager} of this node.
	 */
	public abstract BootstrapManager<?> getBootstrapManager();

	/**
	 * Sets the {@link BootstrapManager} for the node.
	 * 
	 * @param bootstrapManager
	 *            The {@link BootstrapManager} for the node.
	 */
	public abstract void setBootstrapManager(
			BootstrapManager<?> bootstrapManager);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.api.overlay.OverlayNode#getNeighbors()
	 */
	@Override
	public NeighborDeterminator<S> getNeighbors() {
		return null;
	}
}
