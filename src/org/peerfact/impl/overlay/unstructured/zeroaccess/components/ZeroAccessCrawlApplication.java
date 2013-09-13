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

package org.peerfact.impl.overlay.unstructured.zeroaccess.components;

import java.math.BigInteger;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.overlay.unstructured.zeroaccess.operation.ScheduleGetLOperation;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ZeroAccessCrawlApplication extends AbstractApplication {

	private ZeroAccessOverlayNode node;

	// propability for a upload, if updateRessource is called
	private double propUp;

	// propability for a ressource to be deleted, if updateRessource is called
	private double propDel;

	// propability for a download, if updateRessource is called
	private double propDown;

	// time between query and download
	private long downloadDelay;

	public ZeroAccessCrawlApplication(ZeroAccessOverlayNode node, double propUp,
			double propDel, double propDown, long downloadDelay) {
		this.node = node;
		this.propUp = propUp;
		this.propDel = propDel;
		this.propDown = propDown;
		this.downloadDelay = downloadDelay;
	}

	public void registerBootstrap() {
		ZeroAccessBootstrapManager.getInstance().registerNode(node);
	}

	public void startScheduleGetL(long delay) {
		ScheduleGetLOperation scheduleGetLOperation = new ScheduleGetLOperation(
				node, delay, new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(Operation<Object> op) {
						//
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						//
					}
				});
		scheduleGetLOperation.scheduleWithDelay((long) (Simulator
				.getRandom().nextDouble() * BigInteger.valueOf(delay)
				.doubleValue()));
	}
}
