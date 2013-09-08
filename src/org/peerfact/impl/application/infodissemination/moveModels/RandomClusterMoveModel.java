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
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.peerfact.impl.application.infodissemination.IDOApplication;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;


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
 * The random cluster move model, moves the player in a virtual world. The
 * virtual world should be set over the setter. This move model create random
 * points in the world. The players go to one point (clusterpoint) and move
 * around this point. With a probability of
 * {@link RandomClusterMoveModel.PROBABILITY_FOR_CHANGE_CLUSTER} can a player
 * changes the cluster.
 * 
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 01/06/2011
 */
public class RandomClusterMoveModel implements IMoveModel {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(RandomClusterMoveModel.class);

	/**
	 * The average distance to the center of the cluster
	 */
	private int MULTIPLICATOR_FOR_DISTANCE = 100;

	/**
	 * The probability to change the cluster.
	 */
	private double PROBABILITY_FOR_CHANGE_CLUSTER = 0.05;

	/**
	 * The move speed limit of new nodes
	 */
	public double moveSpeedLimit;

	/**
	 * The maximal world dimension in Y direction
	 */
	public int worldDimensionX;

	/**
	 * The maximal world dimension in Y direction
	 */
	public int worldDimensionY;

	/**
	 * The rate to change speed. It will only used, if it is activated the
	 * changing of the speed in the application. For every call of
	 * getNextPosition(...), the speed will be changed with this rate for the
	 * given player.
	 */
	public double speedChangingRate;

	/**
	 * The portal component.
	 */
	public IPortalComponent portal;

	/**
	 * A storage of IDOApplication to assign the needed move information. The
	 * move information stores target, last position and speed of a node. It is
	 * needed to derive the next position.
	 */
	public Map<IDOApplication, MoveInformation> moveStorage = new Hashtable<IDOApplication, MoveInformation>();

	/**
	 * The positions of the cluster centers
	 */
	public Point[] clusterCenterPositions;

	/**
	 * The cluster assignment of an IDOApplication
	 */
	public Map<IDOApplication, Integer> clusterAssignment = new Hashtable<IDOApplication, Integer>();

	/**
	 * The number of clusters on the world
	 */
	public int numberOfClusters;

	/**
	 * Create a random cluster move model with following properties:
	 * moveSpeedLimit = 10;<br>
	 * worldDimensionX = 200; <br>
	 * worldDimensionY = 200; <br>
	 * speedChaining Rate = 1/3; <br>
	 * numberOfClusters = 10;
	 * <p>
	 * If you need other parameter, then you can set this over the setters in
	 * this class.
	 */
	public RandomClusterMoveModel() {
		this(10, 200, 200, 1 / 30, 10);
	}

	/**
	 * The constructor of this class.
	 * 
	 * @param moveSpeedLimit
	 *            The standard speedLimit to which is started for all players.
	 *            Can changed for every player, if it is activate the changing
	 *            of the speed. The rates for the changing can set in
	 *            {@link #speedChangingRate}.
	 * @param worldDimensionX
	 *            The dimension in X direction of the world.
	 * @param worldDimensionY
	 *            The dimension in Y direction of the world.
	 * @param speedChaningRate
	 *            The rate to change speed. It will only used, if it is activate
	 *            the changing of the speed. For every call of
	 *            getNextPosition(...), the speed will be changed with this rate
	 *            for the given player.
	 * @param numberOfClusters
	 *            The number of clusters, that should be exist for this movement
	 *            model.
	 */
	public RandomClusterMoveModel(int moveSpeedLimit, int worldDimensionX,
			int worldDimensionY, double speedChaningRate, int numberOfClusters) {
		if (numberOfClusters <= 0) {
			throw new IllegalArgumentException(
					"numberOfClusters is to small. The number must be greater then 0.");
		}
		this.moveSpeedLimit = moveSpeedLimit;
		this.worldDimensionX = worldDimensionX;
		this.worldDimensionY = worldDimensionY;
		this.speedChangingRate = speedChaningRate;
		this.numberOfClusters = numberOfClusters;
		this.clusterCenterPositions = new Point[numberOfClusters];

		determineClusterPositions();

		if (moveSpeedLimit <= 0) {
			log.warn("Bad value for move speed limit. It is equals or smaller as 0.");
		}
	}

	private void determineClusterPositions() {
		for (int i = 0; i < numberOfClusters; i++) {
			int x = Simulator.getRandom().nextInt(worldDimensionX);
			int y = Simulator.getRandom().nextInt(worldDimensionY);
			clusterCenterPositions[i] = new Point(x, y);
		}
	}

	@Override
	public Point getNextPosition(IDOApplication app) {
		Integer clusterID = clusterAssignment.get(app);
		MoveInformation moveInfo = moveStorage.get(app);

		// if not known, then it adds to the storages
		if (clusterID == null || moveInfo == null) {
			if (moveInfo == null) {
				moveInfo = new MoveInformation(moveSpeedLimit,
						app.getPlayerPosition(), app.getPlayerPosition());
				moveStorage.put(app, moveInfo);
			}
			clusterID = Simulator.getRandom().nextInt(numberOfClusters);
			clusterAssignment.put(app, clusterID);
		}

		if (portal != null) {
			Point position = portal.portal(app.getPlayerPosition(), app,
					worldDimensionX, worldDimensionY);
			// if a portal is used.
			if (position != null) {
				moveInfo.setLastPosition(position);
				return position;
			}
		}

		if (app.speedChanging() != 0) {
			double newMaxSpeed = moveInfo.getMaxSpeed() + speedChangingRate
					* app.speedChanging();
			moveInfo.setMaxSpeed(newMaxSpeed);
		}

		if (moveInfo.getTarget().equals(app.getPlayerPosition())) {
			moveInfo.setTarget(nextTarget(app, clusterID));
		}

		Point nextPosition;
		double distance = app.getPlayerPosition()
				.distance(moveInfo.getTarget());
		if (distance > moveInfo.getMaxSpeed()) {
			nextPosition = deriveNextPosition(app, moveInfo, distance);
		} else {
			nextPosition = moveInfo.getTarget();
		}

		moveInfo.setLastPosition(app.getPlayerPosition());
		setMoveVector(app, nextPosition);
		return nextPosition;
	}

	private static void setMoveVector(IDOApplication app, Point nextPosition) {
		app.setCurrentMoveVector(
				Math.abs(app.getPlayerPosition().x - nextPosition.x),
				Math.abs(app.getPlayerPosition().y - nextPosition.y));
	}

	private static Point deriveNextPosition(IDOApplication app,
			MoveInformation moveInfo, double distance) {
		double normX = (moveInfo.getTarget().x - app.getPlayerPosition().x)
				/ distance;
		double normY = (moveInfo.getTarget().y - app.getPlayerPosition().y)
				/ distance;
		double stepX = normX * moveInfo.getMaxSpeed();
		double stepY = normY * moveInfo.getMaxSpeed();

		if (Math.abs(stepX) < 1 && Math.abs(stepY) < 1) {
			if (Math.abs(stepX) < Math.abs(stepY)) {
				if (stepY < 0) {
					stepY = -1;
				} else {
					stepY = 1;
				}
			} else {
				if (stepX < 0) {
					stepX = -1;
				} else {
					stepX = 1;
				}
			}

		}
		int newX = app.getPlayerPosition().x + (int) stepX;
		int newY = app.getPlayerPosition().y + (int) stepY;

		return new Point(newX, newY);
	}

	private Point nextTarget(IDOApplication app, int clusterID) {
		// change Cluster
		int newClusterID = clusterID;
		if (Simulator.getRandom().nextDouble() <= PROBABILITY_FOR_CHANGE_CLUSTER) {
			newClusterID = Simulator.getRandom().nextInt(numberOfClusters);
			clusterAssignment.put(app, newClusterID);
		}

		// determine the offset to the center
		int offsetX = (int) (Simulator.getRandom().nextGaussian() * MULTIPLICATOR_FOR_DISTANCE);
		int offsetY = (int) (Simulator.getRandom().nextGaussian() * MULTIPLICATOR_FOR_DISTANCE);
		Point clusterCenter = clusterCenterPositions[newClusterID];
		// determine the next target on the map.
		int x = Math.min(Math.max(clusterCenter.x + offsetX, 0),
				worldDimensionX);
		int y = Math.min(Math.max(clusterCenter.y + offsetY, 0),
				worldDimensionY);
		return new Point(x, y);
	}

	public void setMoveSpeedLimit(double moveSpeedLimit) {
		this.moveSpeedLimit = moveSpeedLimit;
	}

	public void setWorldDimensionX(int worldDimensionX) {
		this.worldDimensionX = worldDimensionX;
		determineClusterPositions();
	}

	public void setWorldDimensionY(int worldDimensionY) {
		this.worldDimensionY = worldDimensionY;
		determineClusterPositions();
	}

	public void setSpeedChangingRate(double speedChangingRate) {
		this.speedChangingRate = speedChangingRate;
	}

	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
		this.clusterCenterPositions = new Point[numberOfClusters];
		determineClusterPositions();
	}

	public void setAverageDistanceFromCenter(int distance) {
		this.MULTIPLICATOR_FOR_DISTANCE = distance;
	}

	public void setProbabilityForChangeCluster(double probability) {
		this.PROBABILITY_FOR_CHANGE_CLUSTER = probability;
	}

	public void setPortal(IPortalComponent portal) {
		this.portal = portal;
	}
}
