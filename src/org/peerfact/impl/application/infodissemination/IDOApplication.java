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

package org.peerfact.impl.application.infodissemination;

import java.awt.Point;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.ido.IDONode;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.application.infodissemination.moveModels.IMoveModel;
import org.peerfact.impl.application.infodissemination.moveModels.IPositionDistribution;
import org.peerfact.impl.application.infodissemination.operations.MoveOperation;
import org.peerfact.impl.simengine.Simulator;


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
 * This is an application for the IDO-Overlays. Over this class can be control
 * the players actions. The actions are:
 * <ul>
 * <li>startGame - The player connect to the overlay</li>
 * <li>leaveGame - The player leave the overlay</li>
 * <li>startMovingPlayer - The player start to move</li>
 * <li>stopMovingPlayer - The player stop to move
 * <li>startDescreaseSpeed - Decrease the speed for a move of the player</li>
 * <li>startIncreaseSpeed - Increase the speed for a move of the player</li>
 * <li>stopSpeedChanging - Stop the speed changing</li>
 * </ul>
 * 
 * In this class is stored the move models as static attribute. One instance of
 * a move model is used for all players/applications. The move models can be set
 * over the {@link IDOApplicationFactory}. See at
 * org.peerfact.impl.application.infodissemination.moveModels, for the moveModels.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class IDOApplication extends AbstractApplication {
	/**
	 * Logger for this class
	 */

	/**
	 * The stored move model
	 */
	private static IMoveModel moveModel;

	/**
	 * The model to distribute the player by starting the game.
	 */
	private static IPositionDistribution positionDistribution;

	/**
	 * The change speed. <br>
	 * 0 for no change.<br>
	 * positive for increase speed.<br>
	 * negative for decrease speed.
	 */
	private int changeSpeed = 0;

	/**
	 * The IDO node to this application.
	 */
	private IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node;

	/**
	 * The players position in the world.
	 */
	private Point playerPosition;

	/**
	 * The operation fpr the move.
	 */
	protected MoveOperation moveOp = null;

	/**
	 * The waiting time between two move computations.
	 */
	private long intervalBetweenMove;

	/**
	 * The current move vector. Calculate from the last position and the
	 * actually position.
	 */
	private final int[] currentMoveVector = {
			Simulator.getRandom().nextInt(3) - 1,
			Simulator.getRandom().nextInt(3) - 1 };

	/**
	 * The constructor. It sets the associated node to this application and the
	 * time between two moves.
	 * 
	 * @param node
	 *            The associated node for this application.
	 * @param intervallBetweenMove
	 *            The time between two moves.
	 */
	public IDOApplication(
			IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> node,
			long intervallBetweenMove) {
		this.node = node;
		setIntervalBetweenMove(intervallBetweenMove);
	}

	/**
	 * The player starts to move.
	 */
	public void startMovingPlayer() {
		if (moveModel != null && moveOp == null) {
			// FIXED prevent multiple move operations running in parallel
			moveOp();
		} else if (moveModel == null) {
			log.error("MoveModel not set!");
		}
	}

	/**
	 * The player stops to move.
	 */
	public void stopMovingPlayer() {
		if (moveOp != null) {
			moveOp.stopOperation();
			moveOp = null;
		}
	}

	/**
	 * This class adds an event for the movement.
	 */
	protected void moveOp() {
		moveOp = new MoveOperation(this, new OperationCallback<Object>() {

			@Override
			public void calledOperationFailed(Operation<Object> op) {
				moveOp = null;
			}

			@Override
			public void calledOperationSucceeded(Operation<Object> op) {
				moveOp();
			}
		});
		moveOp.scheduleWithDelay(intervalBetweenMove);
	}

	/**
	 * Start the game. The player will be connected to the overlay.
	 * 
	 * @param startMoving
	 *            start moving, if the peer has started the game.
	 */
	public void startGame(boolean startMoving) {
		if (positionDistribution == null) {
			log.error("No PositionDistribution set");
		}

		playerPosition = getPositionDistribution().getNextPosition();
		node.join(playerPosition);
		if (startMoving) {
			startMovingPlayer();
		}
	}

	/**
	 * The player leave the game. The player will be disconnected from the
	 * overlay. The parameter is used to identifier a crash or a normal leaving
	 * of the player.
	 * 
	 * @param crash
	 *            A crash or a normal leaving of the player from the game.
	 */
	public void leaveGame(boolean crash) {
		stopMovingPlayer();
		node.leave(crash);
	}

	/**
	 * 0 for no change.<br>
	 * positive for increase speed.<br>
	 * negative for decrease speed.
	 * 
	 * @return Gets the rate of the speed changing.
	 */
	public int speedChanging() {
		return changeSpeed;
	}

	/**
	 * start the increasing of the speed. Every computation of a move, the speed
	 * will be increase.
	 */
	public void startIncreaseSpeed() {
		this.changeSpeed = 1;
	}

	/**
	 * start the decreasing of the speed. Every computation of a move, the speed
	 * will be decrease.
	 */
	public void startDecreaseSpeed() {
		this.changeSpeed = -1;
	}

	/**
	 * stop the changing of the speed.
	 */
	public void stopSpeedChanging() {
		this.changeSpeed = 0;
	}

	/**
	 * Gets the current move vector
	 * 
	 * @return the {@link IDOApplication.currentMoveVector}
	 */
	public int[] getCurrentMoveVector() {
		return currentMoveVector;
	}

	/**
	 * Sets the current move vector.
	 * 
	 * @param x
	 *            The direction in x of the vector
	 * @param y
	 *            The direction in y of the vector
	 */
	public void setCurrentMoveVector(int x, int y) {
		this.currentMoveVector[0] = x;
		this.currentMoveVector[1] = y;
	}

	/**
	 * Gets the associated node to the application back.
	 * 
	 * @return The associated node to this application.
	 */
	public IDONode<OverlayID<?>, OverlayContact<OverlayID<?>>> getNode() {
		return node;
	}

	/**
	 * Sets the players Position.
	 * 
	 * @param position
	 *            The position of the player.
	 */
	public void setPlayerPosition(Point position) {
		this.playerPosition = position;
	}

	/**
	 * Gets the players Position.
	 * 
	 * @return The position of the player.
	 */
	public Point getPlayerPosition() {
		return playerPosition;
	}

	/**
	 * Sets the interval between two moves.
	 * 
	 * @param time
	 *            The time between two moves.
	 */
	public void setIntervalBetweenMove(long time) {
		this.intervalBetweenMove = time;
	}

	/**
	 * Sets the move model for the players moves.
	 * 
	 * @param model
	 *            The move model, which should be use.
	 */
	public static void setMoveModel(IMoveModel model) {
		moveModel = model;
	}

	/**
	 * Sets the position distribution for the players.
	 * 
	 * @param posDistribution
	 *            The position distribution model, which should be use.
	 */
	public static void setPositionDistribution(
			IPositionDistribution posDistribution) {
		positionDistribution = posDistribution;
	}

	/**
	 * Gets the move model for this class
	 * 
	 * @return The move model
	 */
	public static IMoveModel getMoveModel() {
		return moveModel;
	}

	/**
	 * Gets the position distribution for this class
	 * 
	 * @return The position distribution
	 */
	public static IPositionDistribution getPositionDistribution() {
		return positionDistribution;
	}

}
