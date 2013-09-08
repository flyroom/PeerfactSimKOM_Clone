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

package org.peerfact.api.service.skyeye;

/**
 * This interface defines the functionality of a SkyNet-node for sending
 * updates, regardless if they are updates of attributes or metrics.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 * @param <T>
 *            determines a sub-class of <code>Storage</code>, which will be used
 *            within an implementing class to store updates.
 */
public interface UpdateStrategy<T extends Storage> {

	/**
	 * This method returns the interval, which was calculated to determine the
	 * period of time between the actual and the last update.
	 * 
	 * @return the period of time between to updates
	 */
	public long getUpdateInterval();

	/**
	 * This method returns the number of retransmissions, which was calculated
	 * to define the amount of additional attempts for sending, if a
	 * transmission of an update fails.
	 * 
	 * @return the number of additional attempts for sending a message
	 */
	public int getNumberOfRetransmissions();

	/**
	 * This method returns the interval, which was calculated to determine the
	 * timeout of a sent message.
	 * 
	 * @return the period of time before a timeout occurs
	 */
	public long getTimeForACK();

	/**
	 * This method returns the receiver of the next update.<br>
	 * <li>For a <i>metric-update</i>, the receiver is calculated through the
	 * <code>TreeHandler</code>-class and is always denoted as
	 * Parent-Coordinator.<li>For an <i>attribute-update</i>, the receiver is
	 * the Parent-Coordinator, which was calculated during the last
	 * <i>metric-update</i>, or it is a Support Peer, which was introduced by
	 * the Parent-Coordinator.
	 * 
	 * @return the receiver for the next update
	 */
	public SkyNetNodeInfo getReceiverOfNextUpdate();

	/**
	 * This method returns a reference of the utilized storage.
	 * 
	 * @return the reference of the utilized storage
	 */
	public T getStorage();

	/**
	 * This method returns the point in time, which marks the start of the
	 * transmission of an update.
	 * 
	 * @return the point in time of sending an update
	 */
	public long getSendingTime();

	/**
	 * This method sets the point in time, which marks the start of the
	 * transmission of an update.
	 * 
	 * @param sendingTime
	 *            contains the point in time of sending an update
	 */
	public void setSendingTime(long sendingTime);

	/**
	 * This method determines the set of data, which will be sent with the next
	 * update and stores the set within this class for a fast reading access.
	 * The determination of the data utilizes the storage, which is accessed by
	 * <code>getStorage</code>.
	 */
	public void setDataToSend();

	/**
	 * This method calculates the period of time between two updates in
	 * accordance to the current context of the SkyNet-node and the whole
	 * P2P-system. The calculated value can be accessed by
	 * <code>getUpdateInterval()</code>.
	 */
	public void calculateUpdateInterval();

	/**
	 * This method calculates the amount of additional attempts for sending a
	 * message to a Parent-Coordinator in accordance to the current context of
	 * the SkyNet-node and the whole P2P-system. The calculated value can be
	 * accessed by <code>getNumberOfRetransmissions()</code>.
	 */
	public void calculateNumberOfRetransmissions();

	/**
	 * This method calculates the timeout of a sent message in accordance to the
	 * current context of the SkyNet-node, the whole P2P-system and in
	 * compliance with the submitted value <code>time</code>. The calculated
	 * value can be accessed by <code>getTimeForACK()</code>.
	 * 
	 * @param time
	 *            contains the value of the last timeout
	 */
	public void calculateTimeForACK(long time);

	/**
	 * This method calculates the receiver for the next update, which can be
	 * accessed by <code>getReceiverForNextUpdate()</code>.
	 */
	public void calculateReceiverForNextUpdate();

	/**
	 * To be informed about its children, a coordinator stores the references of
	 * all Sub-Coordinators, which send updates to him. To avoid, that old
	 * references are kept in the storage, this method is used to periodically
	 * refresh the references of the Sub-Coordinators and to delete old
	 * references.
	 */
	public void removeStaleSubCoordinators();

	/**
	 * Within this method, the data, which was stored by
	 * <code>setDataToSend()</code>, is sent in the context of an update to the
	 * calculated Parent-Coordinator.
	 */
	public void sendNextDataUpdate();

	/**
	 * After the transmission of an update, this method calculates the next
	 * point in time for the next update. As reference for the period of time,
	 * the value of <code>getUpdateInterval()</code> is used.
	 */
	public void scheduleNextUpdateEvent();

}
