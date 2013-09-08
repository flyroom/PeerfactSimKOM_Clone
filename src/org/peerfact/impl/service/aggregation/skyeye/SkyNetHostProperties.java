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

import java.util.List;
import java.util.Vector;

import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.impl.common.DefaultHostProperties;
import org.peerfact.impl.network.gnp.topology.GnpPosition;
import org.peerfact.impl.network.modular.st.positioning.GNPPositioning.GNPPosition;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.AttributeWriter;
import org.peerfact.impl.service.aggregation.skyeye.attributes.Attribute;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
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
 * This class extends <code>DefaultHostProperties</code>, which is normally used
 * within PeerfactSim.KOM for the definition of the host-properties. As SkyNet
 * needs more properties than provided by <code>DefaultHostProperties</code>,
 * this class defines the additional ones. In SkyNet, the properties represent
 * the attributes of a peer, which are transmitted within the attribute-updates.
 * To have different peers with different attributes, this class defines a range
 * for each attribute, from which a value is randomly chosen. While the lower
 * bound of the range for an attribute is defined within this class, the upper
 * bound is provided by the config-file. E.g. <code>MIN_CPU</code> is defined
 * within this class as lower bound, while the method
 * <code>setCpu(int cpu)</code> contains the upper bound, which is specified by
 * the config-file. In code>setCpu(int cpu)</code> the final value of the
 * attribute <code>cpu</code> is set.<br>
 * The other attributes, which possess a setter-methods, are initialized by the
 * same procedure, while the rest is initialized with the private method
 * <code>calculateBorders()</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetHostProperties extends DefaultHostProperties {

	private Vector<Attribute<?>> hostProperties;

	// Attribute CPU is denoted in MHz
	private int currentCpu;

	private int minCPU = 900;

	private int maxCPU;

	// Attribute storage is denoted in GigaByte
	private int currentStorage;

	private int minStorage = 8;

	private int maxStorage;

	// Attribute RAM is denoted in MegaByte
	private int currentRam;

	private int minRam = 256;

	private int maxRam;

	// Definition of the bounds, for specifying how much information of the
	// SubCoordinators is saved and how much information will be send
	private int tresholdPercentage;

	private int downBandwidthPercentage;

	private int upBandwidthPercentage;

	private int tMin = 5;

	// the values for the peer as Coordinator
	private int tMaxCo;

	private int tTresholdCo;

	private int sendMaxCo;

	// the values for the peer as SupportPeer
	private int tMaxSP;

	private int tTresholdSP;

	private int sendMaxSP;

	private boolean initDone;

	public SkyNetHostProperties() {
		super();
		hostProperties = new Vector<Attribute<?>>();
		initDone = false;
	}

	public void setTresholdPercentage(int tresholdPercentage) {
		this.tresholdPercentage = tresholdPercentage;
	}

	public void setDownBandwidthPercentage(int downBandwidthPercentage) {
		this.downBandwidthPercentage = downBandwidthPercentage;
	}

	public void setUpBandwidthPercentage(int upBandwidthPercentage) {
		this.upBandwidthPercentage = upBandwidthPercentage;
	}

	public void setCpu(int cpu) {
		this.maxCPU = cpu;
	}

	public void setStorage(int storage) {
		this.maxStorage = storage;
	}

	public void setRam(int ram) {
		this.maxRam = ram;
	}

	private void calculateCurrentCpu() {
		this.currentCpu = minCPU + Simulator.getRandom().nextInt(maxCPU);
	}

	private void calculateCurrentStorage() {
		this.currentStorage = minStorage
				+ Simulator.getRandom().nextInt(maxStorage);
	}

	private void calculateCurrentRam() {
		this.currentRam = minRam + Simulator.getRandom().nextInt(maxRam);
	}

	public int getTMin() {
		return tMin;
	}

	public int getTMaxCo() {
		return tMaxCo;
	}

	public int getTTresholdCo() {
		return tTresholdCo;
	}

	public int getSendMaxCo() {
		return sendMaxCo;
	}

	public int getTMaxSP() {
		return tMaxSP;
	}

	public int getTTresholdSP() {
		return tTresholdSP;
	}

	public int getSendMaxSP() {
		return sendMaxSP;
	}

	/**
	 * This method initializes the attributes of the super-class
	 * <code>DefaultHostProperties</code> as well as the attributes, which are
	 * defined within this class. All initialized attributes are stored in the
	 * <code>Vector hostProperties</code>, which can be accessed by a host.
	 */
	public void init() {
		if (!initDone) {
			initDone = true;
			fillHostProperties();
			calculateBorders();

			// calculating the values for CPU, RAM and Storage out of the given
			// min- and max-values
			calculateCurrentCpu();
			Attribute<Integer> cpuAtt = new Attribute<Integer>("CPU",
					currentCpu);
			hostProperties.add(cpuAtt);
			calculateCurrentRam();
			Attribute<Integer> ramAtt = new Attribute<Integer>("RAM",
					currentRam);
			hostProperties.add(ramAtt);
			calculateCurrentStorage();
			Attribute<Integer> storAtt = new Attribute<Integer>("Storage",
					currentStorage);
			hostProperties.add(storAtt);

			// for Coordinator
			Attribute<Integer> tMaxCoAtt = new Attribute<Integer>("tMaxCo",
					tMaxCo);
			hostProperties.add(tMaxCoAtt);
			Attribute<Integer> tTreshCoAtt = new Attribute<Integer>(
					"tTresholdCo", tTresholdCo);
			hostProperties.add(tTreshCoAtt);
			Attribute<Integer> sendMaxCoAtt = new Attribute<Integer>(
					"sendMaxCo", sendMaxCo);
			hostProperties.add(sendMaxCoAtt);
			// for SupportPeer
			Attribute<Integer> tMaxSPAtt = new Attribute<Integer>("tMaxSP",
					tMaxSP);
			hostProperties.add(tMaxSPAtt);
			Attribute<Integer> tTreshSPAtt = new Attribute<Integer>(
					"tTresholdSP", tTresholdSP);
			hostProperties.add(tTreshSPAtt);
			Attribute<Integer> sendMaxSPAtt = new Attribute<Integer>(
					"sendMaxSP", sendMaxSP);
			hostProperties.add(sendMaxSPAtt);

			Attribute<Integer> tMinAtt = new Attribute<Integer>("tMin", tMin);
			hostProperties.add(tMinAtt);
			AttributeWriter.getInstance().writeAttributesOfPeer(hostProperties,
					getHost().getNetLayer().getNetID().toString());
		}
	}

	public Vector<Attribute<?>> getHostProperties() {
		return hostProperties;
	}

	/**
	 * This method initializes all attributes, which cannot be defined by the
	 * setter-methods, but need special handling.
	 */
	private void calculateBorders() {
		SkyNetNode node = (SkyNetNode) getHost().getOverlay(SkyNetNode.class);
		double updateIntervall = node.getAttributeUpdateStrategy()
				.getUpdateInterval() / SkyNetConstants.DIVISOR_FOR_SECOND;
		// calculate the borders for the coordinator
		tMaxCo = (int) Math.floor((getMaxDownloadBandwidth() * updateIntervall)
				* ((downBandwidthPercentage / 2) / 100d));
		tMaxCo = (int) (tMaxCo / SkyNetConstants.ATTRIBUTE_ENTRY_SIZE_ESTIMATE);
		tTresholdCo = (int) Math.floor(tMaxCo * (tresholdPercentage / 100d));
		sendMaxCo = (int) Math
				.floor((getMaxUploadBandwidth() * updateIntervall)
						* ((upBandwidthPercentage / 2) / 100d));
		sendMaxCo = (int) (sendMaxCo / SkyNetConstants.ATTRIBUTE_ENTRY_SIZE_ESTIMATE);

		// calculate the borders for the SupportPeer
		tMaxSP = (int) Math.floor((getMaxDownloadBandwidth() * updateIntervall)
				* ((downBandwidthPercentage / 2) / 100d));
		tMaxSP = (int) (tMaxSP / SkyNetConstants.ATTRIBUTE_ENTRY_SIZE_ESTIMATE);
		tTresholdSP = (int) Math.floor(tMaxSP * (tresholdPercentage / 100d));
		sendMaxSP = (int) Math
				.floor((getMaxUploadBandwidth() * updateIntervall)
						* ((upBandwidthPercentage / 2) / 100d));
		sendMaxSP = (int) (sendMaxSP / SkyNetConstants.ATTRIBUTE_ENTRY_SIZE_ESTIMATE);
	}

	/**
	 * This method initializes the attributes of the super-class
	 * <code>DefaultHostProperties</code> and adds them to
	 * <code>hostProperties</code>.
	 */
	private void fillHostProperties() {
		Attribute<Double> down = new Attribute<Double>("DownBandwidth",
				getMaxDownloadBandwidth());
		hostProperties.add(down);
		Attribute<Double> up = new Attribute<Double>("UpBandwidth",
				getMaxUploadBandwidth());
		hostProperties.add(up);
		Attribute<String> groupID = new Attribute<String>("GroupID",
				getGroupID());
		hostProperties.add(groupID);

		// handle the different types of netpositioning originating from the two
		// different gnp-implementations
		StringBuffer buf = new StringBuffer();
		if (getNetPosition() instanceof GNPPosition) {
			List<Double> coordinatesList = ((GNPPosition) getNetPosition())
					.getCoords();
			for (int i = 0; i < coordinatesList.size(); i++) {
				if (i != 0) {
					buf.append("," + coordinatesList.get(i));
				} else {
					buf.append(coordinatesList.get(i));
				}

			}
		} else {
			buf.append(((GnpPosition) getNetPosition())
					.getCoordinateString());
		}

		Attribute<String> position = new Attribute<String>("Position",
				buf.toString());
		hostProperties.add(position);
		Attribute<Double> online = new Attribute<Double>("AvgOnline",
				((SkyNetNode) getHost().getOverlay(SkyNetNode.class))
						.getAverageOnlineTime());
		hostProperties.add(online);
	}
}
