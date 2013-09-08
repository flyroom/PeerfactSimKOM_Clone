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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.peerfact.Constants;
import org.peerfact.impl.application.infodissemination.IDOApplication;
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
 * This class provides the testing of the move models. It execute the the
 * defined move model in getMoveModel, and write a gnuPlot data with the derived
 * position out.
 * 
 * @author christoph <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05/04/2011
 */
public class MoveModelMainTester {

	private static String outputName = "gnuMoveModel.dat";

	private static boolean withIncreasingSpeed = false;

	private static int NUMBER_OF_EXECUTIONS = 10000;

	private static int worldDimensionX = 1200;

	private static int worldDimensionY = 1200;

	private static IMoveModel getMoveModel() {

		IPortalComponent portal = new RandomPortal();
		portal.setProbability(1.0 / NUMBER_OF_EXECUTIONS);

		// ----------- RANDOM CLUSTER ---------------//
		// outputName = "RandomClusterMoveModel.dat";
		// RandomClusterMoveModel model = new RandomClusterMoveModel();
		// model.setAverageDistanceFromCenter(75);
		// model.setMoveSpeedLimit(4);
		// model.setNumberOfClusters(4);
		// model.setProbabilityForChangeCluster(0.15);
		// model.setSpeedChangingRate(0.025);
		// model.setWorldDimensionX(worldDimensionX);
		// model.setWorldDimensionY(worldDimensionY);

		// ----------- RANDOM MOVE ---------------//
		// outputName = "RandomMoveModel.dat";
		// RandomMoveModel model = new RandomMoveModel();
		// model.setMoveSpeedLimit(4);
		// model.setWorldDimensionX(worldDimensionX);
		// model.setWorldDimensionY(worldDimensionY);

		// ----------- RANDOM PATH MOVE ---------------//
		// outputName = "RandomPathMoveModel.dat";
		// RandomPathMoveModel model = new RandomPathMoveModel();
		// model.setMoveSpeedLimit(4);
		// model.setWorldDimensionX(worldDimensionX);
		// model.setWorldDimensionY(worldDimensionY);
		// model.setSpeedChangingRate(0.025);

		// ----------- VARIABLE SPEED MOVE MODEL ---------------//
		// outputName = "vectorModel2.dat";
		// VariableSpeedMoveModel model = new VariableSpeedMoveModel();
		// model.setMoveSpeedLimit(50);
		// withIncreasingSpeed(true);
		// model.setWorldDimensionX(worldDimensionX);
		// model.setWorldDimensionY(worldDimensionY);

		// ----------- RANDOM PORTAL MOVE ---------------//
		// outputName = "RandomMovePortalModel.dat";
		// RandomMoveModel model = new RandomMoveModel();
		// model.setMoveSpeedLimit(4);
		// model.setPortal(portal);
		// model.setWorldDimensionX(worldDimensionX);
		// model.setWorldDimensionY(worldDimensionY);

		// ----------- RANDOM PATH MOVE ---------------//
		outputName = "RandomPathPortalMode.dat";
		RandomPathMoveModel model = new RandomPathMoveModel();
		model.setMoveSpeedLimit(4);
		model.setPortal(portal);
		model.setWorldDimensionX(worldDimensionX);
		model.setWorldDimensionY(worldDimensionY);
		model.setSpeedChangingRate(0.025);

		return model;
	}

	public static void main(String[] args) throws IOException {
		IMoveModel model = getMoveModel();

		IDOApplication app = new IDOApplicationTest();
		app.setPlayerPosition(new Point(worldDimensionX / 2,
				worldDimensionY / 2));
		app.setCurrentMoveVector(Simulator.getRandom().nextInt(5), Simulator
				.getRandom().nextInt(5));

		File output = new File(Constants.OUTPUTS_DIR + File.separator
				+ outputName);
		FileWriter writer = new FileWriter(output);
		writer.write("# counter\n");
		writer.write("# x\n");
		writer.write("# y\n");
		for (int i = 0; i < NUMBER_OF_EXECUTIONS; i++) {
			Point position = model.getNextPosition(app);
			app.setPlayerPosition(position);
			if (withIncreasingSpeed && i == NUMBER_OF_EXECUTIONS / 2) {
				app.startIncreaseSpeed();
			}
			writer.write(i + "\t" + position.x + "\t" + position.y + "\n");
		}
		writer.close();
	}
}

class IDOApplicationTest extends IDOApplication {

	public IDOApplicationTest() {
		super(null, 0);
	}

}
