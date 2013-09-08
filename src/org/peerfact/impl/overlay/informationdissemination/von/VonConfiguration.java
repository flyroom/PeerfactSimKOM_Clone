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

package org.peerfact.impl.overlay.informationdissemination.von;

import org.apache.log4j.Logger;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.application.infodissemination.moveModels.IMoveModel;
import org.peerfact.impl.application.infodissemination.moveModels.IPositionDistribution;
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
 * This class contains all configuration parameters of VON.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VonConfiguration {
	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(VonConfiguration.class);

	/*
	 * General parameters
	 */

	public static long GENERAL_MSG_TIMEOUT = 5 * Simulator.SECOND_UNIT;

	public static int GENERAL_MSG_RETRANSMISSIONS = 5;

	public static long OP_TIMEOUT_OBTAIN_ID = 10 * Simulator.SECOND_UNIT;

	public static long OP_WAIT_BEFORE_RETRY_OBTAIN_ID = 60 * Simulator.SECOND_UNIT;

	public static long OP_TIMEOUT_HELLO = 10 * Simulator.SECOND_UNIT;

	public static long OP_TIMEOUT_MOVE = 10 * Simulator.SECOND_UNIT;

	public static long OP_TIMEOUT_JOIN = 120 * Simulator.SECOND_UNIT;

	public static long OP_WAIT_BEFORE_RETRY_JOIN = 60 * Simulator.SECOND_UNIT;

	public static int WORLD_DIMENSION_X = 1200;

	public static int WORLD_DIMENSION_Y = 1200;

	public static int DEFAULT_AOI_RADIUS = 120;

	public static int MOVE_SPEED_LIMIT = 10;

	public static TransProtocol TRANSPORT_PROTOCOL = TransProtocol.UDP;

	public static IPositionDistribution POSITION_DISTRIBUTION = null;

	public static IMoveModel MOVE_MODEL = null;

	public static long MOVE_TIME_BETWEEN_STEPS = 333 * Simulator.MILLISECOND_UNIT;

	public static long INTERVAL_BETWEEN_HEARTBEATS = 3 * MOVE_TIME_BETWEEN_STEPS;

	public static long STALE_NEIGHBOR_INTERVAL = 3 * INTERVAL_BETWEEN_HEARTBEATS;

	/******************************************************************************************
	 * 
	 * Setter for the values, because it is needed to set the properties from
	 * the config file!
	 * 
	 ********************************************************************************************/

	public static void setGeneralMsgTimeout(long generalMsgTimeout) {
		GENERAL_MSG_TIMEOUT = generalMsgTimeout;
	}

	public static void setGeneralMsgRetransmissions(
			int generalMsgRetransmissions) {
		GENERAL_MSG_RETRANSMISSIONS = generalMsgRetransmissions;
	}

	public static void setOpTimeoutObtainID(long opTimeoutObtainID) {
		OP_TIMEOUT_OBTAIN_ID = opTimeoutObtainID;
	}

	public static void setOpWaitBeforeRetryObtainID(
			long opWaitBeforeRetryObtainID) {
		OP_WAIT_BEFORE_RETRY_OBTAIN_ID = opWaitBeforeRetryObtainID;
	}

	public static void setOpTimeoutHello(long opTimeoutHello) {
		OP_TIMEOUT_HELLO = opTimeoutHello;
	}

	public static void setOpTimeoutMove(long opTimeoutMove) {
		OP_TIMEOUT_MOVE = opTimeoutMove;
	}

	public static void setOpTimeoutJoin(long opTimeoutJoin) {
		OP_TIMEOUT_JOIN = opTimeoutJoin;
	}

	public static void setOpWaitBeforeRetryJoin(long opWaitBeforeRetryJoin) {
		OP_WAIT_BEFORE_RETRY_JOIN = opWaitBeforeRetryJoin;
	}

	public static void setWorldDimensionX(int worldDimensionX) {
		WORLD_DIMENSION_X = worldDimensionX;
	}

	public static void setWorldDimensionY(int worldDimensionY) {
		WORLD_DIMENSION_Y = worldDimensionY;
	}

	public static void setDefaultAOIRadius(int defaultAOIRadius) {
		DEFAULT_AOI_RADIUS = defaultAOIRadius;
	}

	public static void setMoveSpeedLimit(int moveSpeedLimit) {
		MOVE_SPEED_LIMIT = moveSpeedLimit;
	}

	public static void setTransportProtocol(String transportProtocol) {
		TransProtocol result = null;
		try {
			result = Enum.valueOf(TransProtocol.class, transportProtocol);
		} catch (IllegalArgumentException e) {
			log.fatal("Wrong configuration! The transport protocol \""
					+ transportProtocol
					+ "\" doesn't exist in enum TransProtocol.", e);
			log.fatal("The program will be closed...");
			System.exit(-1);
		}
		TRANSPORT_PROTOCOL = result;
	}

	public static void setPositionDistribution(
			IPositionDistribution positionDistribution) {
		POSITION_DISTRIBUTION = positionDistribution;
	}

	public static void setMoveModel(IMoveModel moveModel) {
		MOVE_MODEL = moveModel;
	}

	public static void setMoveTimeBetweenSteps(long moveTimeBetweenSteps) {
		MOVE_TIME_BETWEEN_STEPS = moveTimeBetweenSteps;

		INTERVAL_BETWEEN_HEARTBEATS = 3 * MOVE_TIME_BETWEEN_STEPS;
		STALE_NEIGHBOR_INTERVAL = 3 * INTERVAL_BETWEEN_HEARTBEATS;
	}
}
