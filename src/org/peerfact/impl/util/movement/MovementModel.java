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

import java.util.LinkedHashSet;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.common.SupportOperations;
import org.peerfact.impl.common.Operations;


/**
 * Unified movement Models. Can be used inside an Application (virtual Position)
 * or Device (physical Position) or anything else that implements
 * MovementSupported. They support automatical triggering using an Operation or
 * you may trigger them directly from within your Application by calling move()
 * 
 * @author Bjoern Richerzhagen
 * @version 1.0, 04/25/2011
 */
public abstract class MovementModel implements SupportOperations {

	private Set<MovementSupported> components = new LinkedHashSet<MovementSupported>();

	private long timeBetweenMoveOperations = 0;

	private int[] worldDimensions;

	private int moveSpeedLimit;

	public MovementModel() {
		worldDimensions = new int[] { 800, 600, 100 };
	}

	/**
	 * Gets called periodically (after timeBetweenMoveOperations) or by an
	 * application and should be used to recalculate positions
	 */
	public abstract void move();

	/**
	 * Get all participating Components
	 * 
	 * @return
	 */
	protected Set<MovementSupported> getComponents() {
		return components;
	}

	/**
	 * Add a component to this movement-Model (used to keep global information
	 * about all participants in a movement model). Each Component acts as a
	 * callback upon movement of this component
	 * 
	 * @param component
	 */
	public void addComponent(MovementSupported component) {
		components.add(component);
	}

	/**
	 * Move models can periodically calculate new positions and notify
	 * listeners, if a position changed. Here you can specify the interval
	 * between these notifications/calculations. If this is set to zero, there
	 * is no periodical execution, which may be useful if you want to call
	 * move() from within an application.
	 * 
	 * @param timeBetweenMoveOperations
	 */
	public void setTimeBetweenMoveOperations(long timeBetweenMoveOperations) {
		this.timeBetweenMoveOperations = timeBetweenMoveOperations;
		periodicMove();
	}

	protected void periodicMove() {
		Operations.createEmptyOperation(this, new OperationCallback<Object>() {

			@Override
			public void calledOperationFailed(Operation<Object> op) {
				// nothing to do here
			}

			@Override
			public void calledOperationSucceeded(Operation<Object> op) {
				periodicMove();
			}
		}).scheduleWithDelay(timeBetweenMoveOperations);
		move();
	}

	@Override
	public Host getHost() {
		return null;
	}

	@Override
	public void setHost(Host host) {
		// nothing
	}

	public void setMoveSpeedLimit(int moveSpeedLimit) {
		this.moveSpeedLimit = moveSpeedLimit;
	}

	public int getMoveSpeedLimit() {
		return moveSpeedLimit;
	}

	public void setWorldDimensionX(int dimension) {
		this.worldDimensions[0] = dimension;
	}

	public void setWorldDimensionY(int dimension) {
		this.worldDimensions[1] = dimension;
	}

	public int getWorldDimension(int dim) {
		return worldDimensions[dim];
	}

	public void setWorldDimensionZ(int dimension) {
		this.worldDimensions[2] = dimension;
	}

}
