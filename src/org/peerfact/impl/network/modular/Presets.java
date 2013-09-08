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

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.network.modular.st.fragmenting.IPv4Fragmenting;
import org.peerfact.impl.network.modular.st.fragmenting.NoFragmenting;
import org.peerfact.impl.network.modular.st.jitter.LognormalJitter;
import org.peerfact.impl.network.modular.st.jitter.NoJitter;
import org.peerfact.impl.network.modular.st.jitter.PingErJitter;
import org.peerfact.impl.network.modular.st.latency.GNPLatency;
import org.peerfact.impl.network.modular.st.latency.GeographicalLatency;
import org.peerfact.impl.network.modular.st.latency.PingErLatency;
import org.peerfact.impl.network.modular.st.latency.StaticLatency;
import org.peerfact.impl.network.modular.st.packetSizing.IPv4Header;
import org.peerfact.impl.network.modular.st.packetSizing.NoHeader;
import org.peerfact.impl.network.modular.st.ploss.NoPacketLoss;
import org.peerfact.impl.network.modular.st.ploss.PingErPacketLoss;
import org.peerfact.impl.network.modular.st.ploss.StaticPacketLoss;
import org.peerfact.impl.network.modular.st.positioning.GNPPositioning;
import org.peerfact.impl.network.modular.st.positioning.GeographicalPositioning;
import org.peerfact.impl.network.modular.st.positioning.TorusPositioning;
import org.peerfact.impl.network.modular.st.trafCtrl.BoundedTrafficQueue;
import org.peerfact.impl.network.modular.st.trafCtrl.NoTrafficControl;


/**
 * Different presets that speed up the usage of the Modular Network Layer in the
 * XML configuration file. Simply add 'preset="<Preset>"' to the XML element of
 * the ModularNetLayerFactory instead of defining every strategy by hand. Of
 * course, preset network layer strategies can be overridden by strategies
 * explicitly set.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Presets {

	static Map<String, Preset> presets = new LinkedHashMap<String, Preset>();

	private static Preset defaultPreset;

	public static void addPreset(String name, Preset preset) {
		presets.put(name.toLowerCase(), preset);
	}

	public static Preset getPreset(String name) {

		Preset p = presets.get(name.toLowerCase());
		if (p == null) {
			throw new IllegalArgumentException("A preset with the name '"
					+ name + "' is not registered.");
		}
		return p;

	}

	public static void setDefaultPreset(Preset p) {
		defaultPreset = p;
	}

	public static Preset getDefaultPreset() {
		return defaultPreset;
	}

	public static abstract class Preset {

		public abstract void set(ModularNetLayerFactory f);

	}

	static {

		addPreset("Easy", new Preset() {

			@Override
			public void set(ModularNetLayerFactory f) {
				f.setFragmenting(new NoFragmenting());
				f.setJitter(new NoJitter());
				f.setLatency(new StaticLatency());
				f.setPacketSizing(new NoHeader());
				f.setPLoss(new NoPacketLoss());
				f.setPositioning(new TorusPositioning());
				f.setTrafficControl(new NoTrafficControl());
			}

		});

		Preset fundamental = new Preset() {
			@Override
			public void set(ModularNetLayerFactory f) {
				f.setFragmenting(new IPv4Fragmenting());
				f.setJitter(new LognormalJitter());
				f.setLatency(new StaticLatency());
				f.setPacketSizing(new IPv4Header());
				f.setPLoss(new StaticPacketLoss());
				f.setPositioning(new TorusPositioning());
				f.setTrafficControl(new BoundedTrafficQueue());
			}

		};

		addPreset("Fundamental", fundamental);
		setDefaultPreset(fundamental);

		addPreset("PingEr", new Preset() {

			@Override
			public void set(ModularNetLayerFactory f) {
				f.setFragmenting(new IPv4Fragmenting());
				f.setJitter(new PingErJitter());
				f.setLatency(new PingErLatency());
				f.setPacketSizing(new IPv4Header());
				f.setPLoss(new PingErPacketLoss());
				f.setPositioning(new GeographicalPositioning());
				f.setTrafficControl(new BoundedTrafficQueue());
			}

		});

		addPreset("Geo", new Preset() {

			@Override
			public void set(ModularNetLayerFactory f) {
				f.setFragmenting(new IPv4Fragmenting());
				f.setJitter(new PingErJitter());
				f.setLatency(new GeographicalLatency());
				f.setPacketSizing(new IPv4Header());
				f.setPLoss(new PingErPacketLoss());
				f.setPositioning(new GeographicalPositioning());
				f.setTrafficControl(new BoundedTrafficQueue());
			}

		});

		addPreset("GNP", new Preset() {

			@Override
			public void set(ModularNetLayerFactory f) {
				f.setFragmenting(new IPv4Fragmenting());
				f.setJitter(new PingErJitter());
				f.setLatency(new GNPLatency());
				f.setPacketSizing(new IPv4Header());
				f.setPLoss(new PingErPacketLoss());
				f.setPositioning(new GNPPositioning());
				f.setTrafficControl(new BoundedTrafficQueue());
			}

		});

	}

}
