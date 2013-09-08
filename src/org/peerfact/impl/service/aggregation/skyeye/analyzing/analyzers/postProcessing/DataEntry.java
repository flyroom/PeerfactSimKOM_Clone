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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing;

import org.peerfact.api.network.NetID;

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
 * This class is used to represent the amount of all messages, of the
 * overlay-messages and of the SkyNet-messages as well as of their sizes for
 * every single host. <code>DataEntry</code> obtains its information from
 * {@link NetLayerPostProcessor}, which de-serializes the files of the exchanged
 * messages. An instance of this class is initialized for sent, received and
 * dropped messages for every single host.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class DataEntry {

	private int allMessage;

	private long allMessageSize;

	private int overlayMessage;

	private long overlayMessageSize;

	private int skyNetMessage;

	private long skyNetMessageSize;

	private int metricUpdateMessage;

	private long metricUpdateMessageSize;

	private int metricUpdateACKMessage;

	private long metricUpdateACKMessageSize;

	private int attributeUpdateMessage;

	private long attributeUpdateMessageSize;

	private NetID id;

	public DataEntry(NetID id) {
		allMessage = 0;
		allMessageSize = 0;
		overlayMessage = 0;
		overlayMessageSize = 0;
		skyNetMessage = 0;
		skyNetMessageSize = 0;
		metricUpdateMessage = 0;
		metricUpdateMessageSize = 0;
		metricUpdateACKMessage = 0;
		metricUpdateACKMessageSize = 0;
		attributeUpdateMessage = 0;
		attributeUpdateMessageSize = 0;
		this.id = id;
	}

	/**
	 * This method is responsible for returning the {@link NetID} of the host,
	 * for which this <code>DataEntry</code> manages the information about the
	 * messages and their sizes.
	 * 
	 * @return the ID of the host, for which this instance of
	 *         <code>DataEntry</code> is managed
	 */
	public NetID getId() {
		return id;
	}

	/**
	 * This method is responsible for returning the amount of all messages of a
	 * host.
	 * 
	 * @return the amount of all messages
	 */
	public int getAllMessage() {
		return allMessage;
	}

	/**
	 * This method is responsible for returning the complete size of all
	 * messages of a host.
	 * 
	 * @return the size of all messages
	 */
	public long getAllMessageSize() {
		return allMessageSize;
	}

	/**
	 * This method is responsible for returning the amount of all
	 * overlay-messages of a host.
	 * 
	 * @return the amount of overlay-messages
	 */
	public int getOverlayMessage() {
		return overlayMessage;
	}

	/**
	 * This method is responsible for returning the complete size of all
	 * overlay-messages of a host.
	 * 
	 * @return the size of all overlay-messages
	 */
	public long getOverlayMessageSize() {
		return overlayMessageSize;
	}

	/**
	 * This method is responsible for returning the amount of all
	 * SkyNet-messages of a host.
	 * 
	 * @return the amount of SkyNet-messages
	 */
	public int getSkyNetMessage() {
		return skyNetMessage;
	}

	/**
	 * This method is responsible for returning the complete size of all
	 * SkyNet-messages of a host.
	 * 
	 * @return the size of all SkyNet-messages
	 */
	public long getSkyNetMessageSize() {
		return skyNetMessageSize;
	}

	public int getMetricUpdateMessage() {
		return metricUpdateMessage;
	}

	public long getMetricUpdateMessageSize() {
		return metricUpdateMessageSize;
	}

	public int getMetricUpdateACKMessage() {
		return metricUpdateACKMessage;
	}

	public long getMetricUpdateACKMessageSize() {
		return metricUpdateACKMessageSize;
	}

	public int getAttributeUpdateMessage() {
		return attributeUpdateMessage;
	}

	public long getAttributeUpdateMessageSize() {
		return attributeUpdateMessageSize;
	}

	/**
	 * This method increments the amount of all messages by the value, which is
	 * provided by the parameter.
	 * 
	 * @param messageAmount
	 *            contains the additional amount of all messages, which are
	 *            added to the existing amount
	 */
	public void incrementAllMessage(int messageAmount) {
		this.allMessage = allMessage + messageAmount;
	}

	/**
	 * This method enhances the size of all messages by the value, which is
	 * provided by the parameter.
	 * 
	 * @param size
	 *            contains the additional size of all messages, which is added
	 *            to the existing size
	 */
	public void enhanceAllMessageSize(long size) {
		this.allMessageSize = allMessageSize + size;
	}

	/**
	 * This method increments the amount of all overlay-messages by the value,
	 * which is provided by the parameter.
	 * 
	 * @param messageAmount
	 *            contains the additional amount of all overlay-messages, which
	 *            are added to the existing amount
	 */
	public void incrementOverlayMessage(int messageAmount) {
		this.overlayMessage = overlayMessage + messageAmount;
	}

	/**
	 * This method enhances the size of all overlay-messages by the value, which
	 * is provided by the parameter.
	 * 
	 * @param size
	 *            contains the additional size of all overlay-messages, which is
	 *            added to the existing size
	 */
	public void enhanceOverlayMessageSize(long size) {
		this.overlayMessageSize = overlayMessageSize + size;
	}

	/**
	 * This method increments the amount of all SkyNet-messages by the value,
	 * which is provided by the parameter.
	 * 
	 * @param messageAmount
	 *            contains the additional amount of all SkyNet-messages, which
	 *            are added to the existing amount
	 */
	public void incrementSkyNetMessage(int messageAmount) {
		this.skyNetMessage = skyNetMessage + messageAmount;
	}

	/**
	 * This method enhances the size of all SkyNet-messages by the value, which
	 * is provided by the parameter.
	 * 
	 * @param size
	 *            contains the additional size of all SkyNet-messages, which is
	 *            added to the existing size
	 */
	public void enhanceSkyNetMessageSize(long size) {
		this.skyNetMessageSize = skyNetMessageSize + size;
	}

	public void incrementMetricUpdateMessage(int messageAmount) {
		this.metricUpdateMessage = metricUpdateMessage + messageAmount;
	}

	public void enhanceMetricUpdateMessageSize(long size) {
		this.metricUpdateMessageSize = metricUpdateMessageSize + size;
	}

	public void incrementMetricUpdateACKMessage(int messageAmount) {
		this.metricUpdateACKMessage = metricUpdateACKMessage + messageAmount;
	}

	public void enhanceMetricUpdateACKMessageSize(long size) {
		this.metricUpdateACKMessageSize = metricUpdateACKMessageSize + size;
	}

	public void incrementAttributeUpdateMessage(int messageAmount) {
		this.attributeUpdateMessage = attributeUpdateMessage + messageAmount;
	}

	public void enhanceAttributeUpdateMessageSize(long size) {
		this.attributeUpdateMessageSize = attributeUpdateMessageSize + size;
	}
}
