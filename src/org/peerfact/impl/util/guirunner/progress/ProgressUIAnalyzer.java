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

package org.peerfact.impl.util.guirunner.progress;

import java.io.File;
import java.io.Writer;

import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ProgressUIAnalyzer implements NetAnalyzer, OperationAnalyzer {

	SimulationProgressView view;

	public ProgressUIAnalyzer(File configFile) {

		view = SimulationProgressView.getInstance();
		view.setConfigurationName(configFile.getName());

		view.rebuildProgressValues();

		Thread.setDefaultUncaughtExceptionHandler(view);
		Thread.currentThread().setUncaughtExceptionHandler(view);

	}

	@Override
	public void start() {
		view.notifySimulationRunning();
		view.update();
	}

	@Override
	public void stop(Writer output) {
		view.notifySimulationFinished();
		view.update();
	}

	@Override
	public void operationFinished(Operation<?> op) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		view.notifySimulationRunning();
		view.updateIfNecessary();
	}

}
