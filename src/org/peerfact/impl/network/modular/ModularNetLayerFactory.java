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

package org.peerfact.impl.network.modular;

import org.apache.log4j.Logger;
import org.peerfact.api.common.Host;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.network.AbstractNetLayerFactory;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.network.modular.db.NetMeasurementDB;
import org.peerfact.impl.network.modular.device.HostDevice;
import org.peerfact.impl.network.modular.livemon.NetLayerLiveMonitoring;
import org.peerfact.impl.network.modular.st.FragmentingStrategy;
import org.peerfact.impl.network.modular.st.JitterStrategy;
import org.peerfact.impl.network.modular.st.LatencyStrategy;
import org.peerfact.impl.network.modular.st.PLossStrategy;
import org.peerfact.impl.network.modular.st.PacketSizingStrategy;
import org.peerfact.impl.network.modular.st.PositioningStrategy;
import org.peerfact.impl.network.modular.st.TrafficControlStrategy;
import org.peerfact.impl.network.modular.subnet.SimpleModularSubnet;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.BackToXMLWritable;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Component factory for the Modular Network Layer. In this component, strategy
 * modules are selected that are used by the modular network layer class.
 * 
 * You can configure different strategies in the PeerfactSim.KOM XML
 * configuration file. An example:
 * 
 * <pre>
 * &lt;NetLayer class="org.peerfact.impl.network.modular.ModularNetLayerFactory" downBandwidth="5000.0" upBandwidth="5000.0" useRegionGroups="false" useInOrderDelivery="true" preset="PingER"&gt;
 *   &lt;BandwidthDetermination class="org.peerfact.impl.network.bandwidthdetermination.OECDReportBandwidthDetermination"/&gt;
 *   &lt;MeasurementDB class="org.peerfact.impl.network.modular.db.NetMeasurementDB" file="data/mod_measured_data.xml"/&gt;
 *   &lt;Subnet class="org.peerfact.impl.network.modular.subnet.SimpleModularSubnet"&gt;
 *   &lt;PacketSizing class="org.peerfact.impl.network.modular.st.packetSizing.IPv4Header"/&gt;
 *   &lt;Fragmenting class="org.peerfact.impl.network.modular.st.fragmenting.IPv4Fragmenting"/&gt;
 *   &lt;TrafficControl class="org.peerfact.impl.network.modular.st.trafCtrl.BoundedTrafficQueue" maxTimeSend="3s" maxTimeReceive="3s"/&gt;
 *   &lt;PLoss class="org.peerfact.impl.network.modular.st.ploss.PingERPacketLoss"/&gt;
 *   &lt;Latency class="org.peerfact.impl.network.modular.st.latency.PingErLatency"/&gt;
 *   &lt;Jitter class="org.peerfact.impl.network.modular.st.jitter.PingErJitter"/&gt;
 *   &lt;Positioning class="org.peerfact.impl.network.modular.st.positioning.GeographicalPositioning"/&gt;
 * &lt;/NetLayer&gt;
 * </pre>
 * 
 * @see org.peerfact.impl.network.modular.ModularNetLayer
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ModularNetLayerFactory extends AbstractNetLayerFactory {

	private static Logger log = SimLogger
			.getLogger(ModularNetLayerFactory.class);

	private NetMeasurementDB db = null;

	private StrategiesImpl strategies = new StrategiesImpl();

	private AbstractModularSubnet subnet = new SimpleModularSubnet(strategies);

	private HostDevice device = null;

	public boolean useRegionGroups = false;

	/**
	 * Only used when useRegionGroups is set to true
	 */
	public DBHostListManager dbHostList = null;

	private String presetName;

	public ModularNetLayerFactory() {
		NetLayerLiveMonitoring.register();
		Presets.getDefaultPreset().set(this);
		Simulator.scheduleEvent(null, 0, new SimulationEventHandler() {

			@Override
			public void eventOccurred(SimulationEvent se) {
				onSimulationStart();
			}

		}, SimulationEvent.Type.START_SIMULATION);
	}

	@Override
	public ModularNetLayer createComponent(Host host) {
		String groupStr = host.getProperties().getGroupID();
		NetMeasurementDB.Host hostMeta;
		if (db != null) {
			subnet.setDB(db);
			if (useRegionGroups) {
				// In case of a DB presence, look up the host's specific
				// metadata there
				NetMeasurementDB.Group g = db.getStringAddrObjFromStr(
						NetMeasurementDB.Group.class, groupStr);
				if (g == null) {
					throw new IllegalArgumentException(
							"There is no group named '" + groupStr + "'");
				}
				hostMeta = g.tGetNextMember();
			} else {
				// The hosts are not grouped by their region name, we will
				// return random hosts in the world for each group.
				if (dbHostList == null) {
					dbHostList = new DBHostListManager(db);
				}
				hostMeta = dbHostList.getNextHost();
			}
		} else {
			hostMeta = null;
		}

		IPv4NetID id;
		if (hostMeta != null) {
			id = new IPv4NetID(IPv4NetID.intToLong(hostMeta.getId()));
		} else {
			id = new IPv4NetID(IPv4NetID.intToLong(Simulator.getRandom()
					.nextInt()));
		}

		if (device == null) {
			device = new HostDevice();
		}

		return new ModularNetLayer(subnet, getBandwidth(id), hostMeta,
				getStrategies().getPositioningStrategy().getPosition(host, db,
						hostMeta), id, device.clone());
	}

	/**
	 * Executed at the start of the simulation via a simulation event at time 0.
	 */
	void onSimulationStart() {
		if (db != null) {
			log.debug(db.getStats());
			dbHostList = null;
			db.release();
		}
		log.info("Fully written out, the modular net layer configuration would look like this:\n"
				+ getWrittenBackNetLayer());
	}

	/**
	 * Returns the network layer configuration written back to the XML
	 * configuration format.
	 * 
	 * @return
	 */
	public String getWrittenBackNetLayer() {
		return BackToXMLWritable.BackWriter.getWrittenBackDoc("NetLayer", this);
	}

	public void setMeasurementDB(NetMeasurementDB db) {
		this.db = db;
		// subnet.setDB(db); bjoernr: moved to createComponent to be independent
		// of order in config.xml
	}

	public void setSubnet(AbstractModularSubnet subnet) {
		this.subnet = subnet;
		this.subnet.setStrategies(getStrategies());
	}

	public void setDevice(HostDevice device) {
		this.device = device;
	}

	public IStrategies getStrategies() {
		return strategies;
	}

	public static class StrategiesImpl implements IStrategies {

		// Defaults
		PacketSizingStrategy packetSizing = null;

		TrafficControlStrategy trafCtrl = null;

		PLossStrategy pLoss = null;

		LatencyStrategy latency = null;

		PositioningStrategy positioning = null;

		FragmentingStrategy frag = null;

		JitterStrategy jit = null;

		@Override
		public PacketSizingStrategy getPacketSizingStrategy() {
			return packetSizing;
		}

		@Override
		public TrafficControlStrategy getTrafficControlStrategy() {
			return trafCtrl;
		}

		@Override
		public PLossStrategy getPLossStrategy() {
			return pLoss;
		}

		@Override
		public LatencyStrategy getLatencyStrategy() {
			return latency;
		}

		@Override
		public PositioningStrategy getPositioningStrategy() {
			return positioning;
		}

		@Override
		public FragmentingStrategy getFragmentingStrategy() {
			return frag;
		}

		@Override
		public JitterStrategy getJitterStrategy() {
			return jit;
		}

	}

	public void setUseRegionGroups(boolean useRegionGroups) {
		this.useRegionGroups = useRegionGroups;
	}

	public void setUseInOrderDelivery(boolean useInOrderDelivery) {
		subnet.setUseInOrderDelivery(useInOrderDelivery);
	}

	public void setPreset(String presetName) {
		Presets.getPreset(presetName).set(this);
		this.presetName = presetName;
	}

	public void setPacketSizing(PacketSizingStrategy packetSizing) {
		strategies.packetSizing = packetSizing;
	}

	public void setTrafficControl(TrafficControlStrategy trafCtrl) {
		strategies.trafCtrl = trafCtrl;
	}

	public void setPLoss(PLossStrategy pLoss) {
		strategies.pLoss = pLoss;
	}

	public void setLatency(LatencyStrategy latency) {
		strategies.latency = latency;
	}

	public void setPositioning(PositioningStrategy positioning) {
		strategies.positioning = positioning;
	}

	public void setFragmenting(FragmentingStrategy frag) {
		strategies.frag = frag;
	}

	public void setJitter(JitterStrategy jit) {
		strategies.jit = jit;
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		super.writeBackToXML(bw);
		bw.writeSimpleType("useRegionGroups", useRegionGroups);
		bw.writeSimpleType("useInOrderDelivery", subnet.getUseInOrderDelivery());
		bw.writeSimpleType("preset", presetName);
		bw.writeComplexType("MeasurementDB", db);
		bw.writeComplexType("Subnet", subnet);
		bw.writeComplexType("PacketSizing", strategies.packetSizing);
		bw.writeComplexType("Fragmenting", strategies.frag);
		bw.writeComplexType("TrafficControl", strategies.trafCtrl);
		bw.writeComplexType("PLoss", strategies.pLoss);
		bw.writeComplexType("Latency", strategies.latency);
		bw.writeComplexType("Jitter", strategies.jit);
		bw.writeComplexType("Positioning", strategies.positioning);
		bw.writeComplexType("Device", device);
	}

}
