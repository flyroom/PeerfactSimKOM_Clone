/*
 * Copyright (c) 2005-2010 KOM – Multimedia Communications Lab
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

package org.peerfact.impl.service.dhtstorage.past.analyzer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.api.overlay.dht.DHTEntry;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.impl.analyzer.csvevaluation.metrics.AverageMetric;
import org.peerfact.impl.analyzer.csvevaluation.metrics.BinaryRatioMetric;
import org.peerfact.impl.analyzer.csvevaluation.metrics.Metric;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.service.dhtstorage.past.PASTObject;
import org.peerfact.impl.util.oracle.GlobalOracle;
import org.peerfact.impl.util.toolkits.AverageAccumulator;
import org.peerfact.impl.util.toolkits.NumberFormatToolkit;


public class ReplicationAnalyzerMetric {

	private long lastMeasurement;

	protected AverageAccumulator avgHopsAcc = new AverageAccumulator();

	private AvgHopsMetric avgHops = new AvgHopsMetric();

	private AvgReplicaCount avgReplica = new AvgReplicaCount();

	private MinReplicaCount minReplica = new MinReplicaCount();

	private MaxReplicaCount maxReplica = new MaxReplicaCount();

	private MaxReplicaListCount maxReplicaList = new MaxReplicaListCount();

	private MinReplicaListCount minReplicaList = new MinReplicaListCount();

	private ResponsibleNodeMetric responsibleNode = new ResponsibleNodeMetric();

	private FileCountMetric fileCount = new FileCountMetric();

	protected Map<DHTKey<?>, Integer> documentCount = new LinkedHashMap<DHTKey<?>, Integer>();

	/**
	 * Average numer of hops before a successful value lookup reached its
	 * destination.
	 * 
	 * @author dbn
	 * @version 1.0, mm/dd/2012
	 */
	public class AvgHopsMetric implements Metric {

		@Override
		public String getName() {
			return "AvgHops";
		}

		@Override
		public String getMeasurementFor(long time) {
			return NumberFormatToolkit.floorToDecimalsString(
					avgHopsAcc.returnAverage(), 3);
		}
	}

	/**
	 * Average number of replications per available file (includes "original").
	 * 
	 * @author dbn
	 * @version 1.0, mm/dd/2012
	 */
	public class AvgReplicaCount extends AverageMetric {

		@Override
		public String getName() {
			return "AvgReplicaCount";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			return super.getMeasurementFor(time);
		}

		@Override
		public void considerNewValue(double val) {
			super.considerNewValue(val);
		}

	}

	/**
	 * Minimum number of replications per available file (includes "original").
	 */
	public class MinReplicaCount implements Metric {

		int min = Integer.MAX_VALUE;

		@Override
		public String getName() {
			return "MinReplicaCount";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			return String.valueOf(min);
		}

	}

	/**
	 * Maximum number of replications per available file (includes "original").
	 */
	public class MaxReplicaCount implements Metric {

		int max = Integer.MAX_VALUE;

		@Override
		public String getName() {
			return "MaxReplicaCount";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			return String.valueOf(max);
		}

	}

	/**
	 * Maximum length of the list of replica holders per file.
	 */
	public class MaxReplicaListCount implements Metric {

		int max = Integer.MIN_VALUE;

		@Override
		public String getName() {
			return "MaxReplicaListCount";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			return String.valueOf(max);
		}

	}

	/**
	 * Minimum length of the list of replica holders per file.
	 */
	public class MinReplicaListCount implements Metric {

		int min = Integer.MAX_VALUE;

		@Override
		public String getName() {
			return "MinReplicaListCount";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			return String.valueOf(min);
		}

	}

	/**
	 * Percentage of how many list of replica holders include the node
	 * responsible for the file.
	 */
	public class ResponsibleNodeMetric extends BinaryRatioMetric {

		private Map<DHTKey<?>, Boolean> respExist = new LinkedHashMap<DHTKey<?>, Boolean>();

		@Override
		public String getName() {
			return "ResponsibleNodeInReplicaList";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			for (boolean found : respExist.values()) {
				if (found) {
					addPositive();
				} else {
					addNegative();
				}
			}
			return super.getMeasurementFor(time);
		}

		public void considerObj(Host host, PASTObject obj) {
			boolean found = false;
			for (TransInfo info : obj.getReplicationHolders()) {
				found = found
						|| isResponsible((ChordKey) obj.getKey(),
								info.getNetId());
			}
			if (!found && !respExist.containsKey(obj.getKey())) {
				respExist.put(obj.getKey(), found);
			} else if (found) {
				respExist.put(obj.getKey(), found);
			}
		}

		public boolean isResponsible(ChordKey key, NetID netId) {
			Host h = GlobalOracle.getHostForNetID(netId);
			return h.<AbstractChordNode> getComponent(AbstractChordNode.class)
					.isRootOf(key);
		}

		@Override
		public void reset() {
			respExist.clear();
		}

	}

	/**
	 * Number of files available.
	 */
	public class FileCountMetric implements Metric {

		protected Set<DHTKey<?>> fileMetricdocumentCount = new LinkedHashSet<DHTKey<?>>();

		@Override
		public String getName() {
			return "FileCount";
		}

		@Override
		public String getMeasurementFor(long time) {
			calcMeasurementFor(time);
			int size = fileMetricdocumentCount.size();
			fileMetricdocumentCount.clear();
			return String.valueOf(size);
		}

		public void inc(DHTKey<?> entry) {
			if (!fileMetricdocumentCount.contains(entry)) {
				fileMetricdocumentCount.add(entry);
			}
		}
	}

	public void calcMeasurementFor(long time) {
		if (lastMeasurement != time) {
			lastMeasurement = time;
			documentCount.clear();
			responsibleNode.reset();
			maxReplicaList.max = Integer.MIN_VALUE;
			minReplicaList.min = Integer.MAX_VALUE;
			List<Host> hosts = GlobalOracle.getHosts();
			for (Host host : hosts) {
				if (host.getNetLayer().isOnline()) {
					AbstractChordNode node = host
							.getComponent(AbstractChordNode.class);
					Set<DHTEntry<ChordKey>> entries = node.getDHT()
							.getDHTEntries();
					for (DHTEntry<?> entry : entries) {
						inc(entry.getKey());
						fileCount.inc(entry.getKey());
						if (entry instanceof PASTObject) {
							PASTObject obj = (PASTObject) entry;
							if (((PASTObject) entry).getReplicationHolders()
									.size() > maxReplicaList.max) {
								maxReplicaList.max = ((PASTObject) entry)
										.getReplicationHolders().size();
							}
							if (((PASTObject) entry).getReplicationHolders()
									.size() < minReplicaList.min) {
								minReplicaList.min = ((PASTObject) entry)
										.getReplicationHolders().size();
							}
							responsibleNode.considerObj(host, obj);
						}
					}
				}
			}
			minReplica.min = Integer.MAX_VALUE;
			maxReplica.max = Integer.MIN_VALUE;
			for (Integer count : documentCount.values()) {
				avgReplica.considerNewValue(count);
				if (count > maxReplica.max) {
					maxReplica.max = count;
				}
				if (count < minReplica.min) {
					minReplica.min = count;
				}
			}
		}
	}

	public void addHopsValue(int value) {
		avgHopsAcc.accumulate(value);
	}

	public void inc(DHTKey<?> entry) {
		if (!documentCount.containsKey(entry)) {
			documentCount.put(entry, 1);
		} else {
			documentCount.put(entry, documentCount.get(entry) + 1);
		}

	}

	public AvgHopsMetric getAvgHops() {
		return avgHops;
	}

	public AvgReplicaCount getAvgReplica() {
		return avgReplica;
	}

	public ResponsibleNodeMetric getResponsibleNode() {
		return responsibleNode;
	}

	public FileCountMetric getFileCount() {
		return fileCount;
	}

	public MinReplicaCount getMinReplica() {
		return minReplica;
	}

	public MaxReplicaCount getMaxReplica() {
		return maxReplica;
	}

	public MaxReplicaListCount getMaxReplicaList() {
		return maxReplicaList;
	}

	public MinReplicaListCount getMinReplicaList() {
		return minReplicaList;
	}

}
