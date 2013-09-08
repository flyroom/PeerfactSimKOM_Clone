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

package org.peerfact.impl.application.aggregationsimple;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.service.aggr.AggregationService;
import org.peerfact.api.service.aggr.NoSuchValueException;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.service.aggregation.AggregationToolkit;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.stats.distributions.Distribution;


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
public class SimpleAggregationApp extends AbstractApplication {

	private AggregationService<?> srvc = null;

	private Map<String, Distribution> distributions;

	BufferedWriter dumpStr;

	private AggregationService<?> service2compare;

	public SimpleAggregationApp(Host host,
			Map<String, Distribution> distributions,
			BufferedWriter dumpStr, AggregationService<?> service2compare) {
		this.setHost(host);
		this.distributions = distributions;
		this.dumpStr = dumpStr;
		this.service2compare = service2compare;
	}

	public void setLocalValueDist(String identifier, String distribution) {
		Distribution dist = distributions.get(distribution);
		if (dist == null) {
			throw new IllegalArgumentException(
					"The distribution is not registered in the config: "
							+ distribution);
		}
		setLocalValue(identifier, dist.returnValue());
	}

	public void setLocalValue(String identifier, double value) {
		try {
			getService().setLocalValue(identifier, value);
			service2compare.setLocalValue(identifier, value);
		} catch (NoSuchValueException e) {
			throw new RuntimeException(e);
		}
	}

	public void dumpAggregationResult(final String identifier) {
		final long startTime = Simulator.getCurrentTime();

		try {
			service2compare.getAggregationResult(identifier,
					new OperationCallback<AggregationResult>() {

						@Override
						public void calledOperationFailed(
								Operation<AggregationResult> op) {
							log.error("Failed to gather result of the service to compare to.");
						}

						@Override
						public void calledOperationSucceeded(
								Operation<AggregationResult> op) {

							final AggregationResult result2compare = op
									.getResult();

							try {
								getService()
										.getAggregationResult(
												identifier,
												new OperationCallback<AggregationResult>() {

													@Override
													public void calledOperationFailed(
															Operation<AggregationResult> operation) {
														long currentTime = Simulator
																.getCurrentTime();
														try {
															dumpStr.write(AggregationToolkit
																	.printResultFailedCSV(
																			getHost(),
																			startTime,
																			currentTime
																					- startTime)
																	+ "\t"
																	+ AggregationToolkit
																			.printResultFailedCSV(
																					getHost(),
																					startTime,
																					currentTime
																							- startTime)
																	+ "\n");
															System.out
																	.println("Getting the result failed.");
															dumpStr.flush();
														} catch (IOException e) {
															throw new RuntimeException(
																	e);
														}
													}

													@Override
													public void calledOperationSucceeded(
															Operation<AggregationResult> operation) {
														AggregationResult res = operation
																.getResult();
														long currentTime = Simulator
																.getCurrentTime();
														try {
															String result = AggregationToolkit
																	.printResultCSV(
																			getHost(),
																			startTime,
																			currentTime
																					- startTime,
																			res)
																	+ "\t"
																	+ AggregationToolkit
																			.printResultCSV(
																					getHost(),
																					startTime,
																					currentTime
																							- startTime,
																					result2compare)
																	+ "\n";
															System.out
																	.println("Result: "
																			+ result);
															dumpStr.write(result);
															dumpStr.flush();
														} catch (IOException e) {
															throw new RuntimeException(
																	e);
														}
													}

												});
							} catch (NoSuchValueException e) {
								throw new RuntimeException(e);
							}
						}

					});
		} catch (NoSuchValueException e) {
			throw new RuntimeException(e);
		}

	}

	protected AggregationService<?> getService() {
		if (srvc == null) {
			srvc = determineCorrectAggregationService(getHost());
		}
		return srvc;

	}

	private static AggregationService<?> determineCorrectAggregationService(
			Host host) {
		AggregationService<?> comp = host
				.getComponent(AggregationService.class);
		if (comp == null) {
			throw new ConfigurationException(
					"There is no aggregation service (IAggregationService) registered at the current node that could be used by the application.");
		}
		return comp;
	}

	public void join() {
		getService().join(Operations.getEmptyCallback());
	}

	public void leave() {
		getService().leave(Operations.getEmptyCallback());
	}

}
