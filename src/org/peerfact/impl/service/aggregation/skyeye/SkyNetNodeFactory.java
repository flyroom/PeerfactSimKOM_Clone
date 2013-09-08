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

package org.peerfact.impl.service.aggregation.skyeye;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetSimulationType;
import org.peerfact.api.service.skyeye.SkyNetSimulationType.SimulationType;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIClientNode;
import org.peerfact.impl.overlay.dht.chord.chord.components.ChordNode;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.kademlia.components.KademliaNodeGlobalKnowledge;
import org.peerfact.impl.service.aggregation.skyeye.addressresolution.Chord2AddressResolutionImpl;
import org.peerfact.impl.service.aggregation.skyeye.addressresolution.KademliaAddressResolutionImpl;
import org.peerfact.impl.service.aggregation.skyeye.addressresolution.NapsterAddressResolutionImpl;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.Chord2MetricsCollectorDelegator;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.Chord2TreeHandlerDelegator;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.KademliaMetricsCollectorDelegator;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.KademliaTreeHandlerDelegator;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.NapsterMetricsCollectorDelegator;
import org.peerfact.impl.service.aggregation.skyeye.overlay2skynet.NapsterTreeHandlerDelegator;
import org.peerfact.impl.transport.DefaultTransInfo;

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
 * This class implements the interface {@link ComponentFactory} and is used to
 * initialize the SkyNet-component and to add it to a host. For further details,
 * we refer to {@link ComponentFactory}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetNodeFactory implements ComponentFactory {

	private short commonPort;

	private int simulationSize;

	@Override
	public Component createComponent(Host host) {
		TransLayer transLayer = host.getTransLayer();
		CIClientNode napsterNode = (CIClientNode) host
				.getOverlay(CIClientNode.class);
		if (napsterNode != null) {
			return skyNetNodeOnNapster(napsterNode, transLayer);
		}
		ChordNode chordNode = (ChordNode) host.getOverlay(ChordNode.class);
		if (chordNode != null) {
			return skyNetNodeOnChord(chordNode, transLayer);
		}
		KademliaNodeGlobalKnowledge<KademliaOverlayID> kademliaNode = (KademliaNodeGlobalKnowledge<KademliaOverlayID>) host
				.getOverlay(KademliaNodeGlobalKnowledge.class);
		if (kademliaNode != null) {
			return skyNetNodeOnKademlia(kademliaNode, transLayer);
		}
		System.exit(1);
		return null;
	}

	/**
	 * This private method creates the SkyNet-component for a host and defines
	 * the type of the simulation, depending on the utilized overlay. If this
	 * method is called, SkyNet is simulated on top of Napster and
	 * <code>SkyNetSimulationType</code> is set to
	 * <code>NAPSTER_SIMULATION</code>.
	 * 
	 * @param napsterNode
	 *            the overlay-component, on which SkyNet is set up
	 * @param transLayer
	 *            the TransportLayer-component of the host
	 * @return the completely initialized SkyNet-component for the host.
	 */
	private Component skyNetNodeOnNapster(CIClientNode napsterNode,
			TransLayer transLayer) {
		// Determine the type of the simulation
		SkyNetSimulationType.createInstance(SimulationType.NAPSTER_SIMULATION);

		// creating all needed addresses for the own nodeInfo
		NetID ip = napsterNode.getOwnOverlayContact().getTransInfo().getNetId();
		SkyNetID id = NapsterAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				napsterNode.getOwnOverlayContact().getOverlayID());
		SkyNetNodeInfoImpl nodeInfo = new SkyNetNodeInfoImpl(id, null,
				DefaultTransInfo.getTransInfo(ip, commonPort), -1);

		// create and return the SkyNet-node
		return new SkyNetNode(nodeInfo, commonPort, transLayer, napsterNode,
				simulationSize - 1, new NapsterTreeHandlerDelegator(),
				new NapsterMetricsCollectorDelegator());
	}

	/**
	 * This private method creates the SkyNet-component for a host and defines
	 * the type of the simulation, depending on the utilized overlay. If this
	 * method is called, SkyNet is simulated on top of Chord and
	 * <code>SkyNetSimulationType</code> is set to <code>CHORD_SIMULATION</code>
	 * .
	 * 
	 * @param napsterNode
	 *            the overlay-component, on which SkyNet is set up
	 * @param transLayer
	 *            the TransportLayer-component of the host
	 * @return the completely initialized SkyNet-component for the host.
	 */
	private Component skyNetNodeOnChord(ChordNode chordNode,
			TransLayer transLayer) {
		// Determine the type of the simulation
		SkyNetSimulationType.createInstance(SimulationType.CHORD_SIMULATION);

		// creating all needed addresses for the own nodeInfo
		NetID ip = chordNode.getLocalOverlayContact().getTransInfo().getNetId();
		SkyNetID id = Chord2AddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				chordNode.getOverlayID());
		SkyNetNodeInfoImpl nodeInfo = new SkyNetNodeInfoImpl(id, null,
				DefaultTransInfo.getTransInfo(ip, commonPort), -1);

		// create and return the SkyNet-node
		return new SkyNetNode(nodeInfo, commonPort, transLayer, chordNode,
				simulationSize, new Chord2TreeHandlerDelegator(),
				new Chord2MetricsCollectorDelegator());
	}

	private Component skyNetNodeOnKademlia(
			KademliaNodeGlobalKnowledge<KademliaOverlayID> kademliaNode,
			TransLayer transLayer) {
		// Determine the type of the simulation
		SkyNetSimulationType.createInstance(SimulationType.KADEMLIA_SIMULATION);

		// creating all needed addresses for the own nodeInfo
		NetID ip = kademliaNode.getLocalContact().getTransInfo().getNetId();
		SkyNetID id = KademliaAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				kademliaNode.getLocalContact().getOverlayID());
		SkyNetNodeInfoImpl nodeInfo = new SkyNetNodeInfoImpl(id, null,
				DefaultTransInfo.getTransInfo(ip, commonPort), -1);

		// create and return the SkyNet-node
		return new SkyNetNode(nodeInfo, commonPort, transLayer, kademliaNode,
				simulationSize, new KademliaTreeHandlerDelegator(),
				new KademliaMetricsCollectorDelegator());
	}

	public void setPort(long port) {
		this.commonPort = (short) port;
	}

	public void setSimulationSize(long size) {
		this.simulationSize = (int) size;
	}

}
