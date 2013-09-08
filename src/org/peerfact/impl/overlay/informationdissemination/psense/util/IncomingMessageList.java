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

package org.peerfact.impl.overlay.informationdissemination.psense.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.informationdissemination.psense.IncomingMessageBean;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.AbstractPSenseMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.AbstractPositionUpdateMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorRequestMsg;
import org.peerfact.impl.overlay.informationdissemination.psense.messages.SensorResponseMsg;
import org.peerfact.impl.util.logging.SimLogger;


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
 * This class stores the incoming messages. Additionally it has the task to
 * store only the newest messages.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 */
public class IncomingMessageList {

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(IncomingMessageList.class);

	private LinkedList<IncomingMessageBean> positionUpdateMsgList = null;

	private LinkedList<IncomingMessageBean> sensorRequestMsgList = null;

	private LinkedList<IncomingMessageBean> sensorResponseMsgList = null;

	public IncomingMessageList() {
		positionUpdateMsgList = new LinkedList<IncomingMessageBean>();
		sensorRequestMsgList = new LinkedList<IncomingMessageBean>();
		sensorResponseMsgList = new LinkedList<IncomingMessageBean>();
	}

	public boolean add(IncomingMessageBean e) {
		if (log.isDebugEnabled()) {
			log.debug("Add the messageBean: " + e);
			log.debug("Before adding of the messageBean: " + this);
		}

		boolean successful = false;
		AbstractPSenseMsg msg = e.getMessage();

		if (msg instanceof SensorRequestMsg) {
			successful = addSensorRequestMsg(e);
		} else if (msg instanceof SensorResponseMsg) {
			successful = addSensorResponseMsg(e);
		} else if (msg instanceof AbstractPositionUpdateMsg) {
			successful = addPositionMsg(e);
		}

		if (log.isDebugEnabled()) {
			log.debug("After adding of the messageBean: " + this);
		}

		return successful;
	}

	public boolean addSensorResponseMsg(IncomingMessageBean e) {
		if (isNewerAsAStoredPositionUpdateMsg(e)) {
			removeOldPositionUpdateMsg(e);
		}
		if (isNewerAsStoredSensorResponseMsg(e)) {
			removeOldSensorResponses(e);
			sensorResponseMsgList.add(e);
			return true;
		}
		return false;
	}

	public boolean addSensorRequestMsg(IncomingMessageBean e) {
		if (isNewerAsAStoredPositionUpdateMsg(e)) {
			removeOldPositionUpdateMsg(e);
		}
		if (isNewerAsAStoredSensorRequestMsg(e)) {
			removeOldSensorRequests(e);
			sensorRequestMsgList.add(e);
			return true;
		}
		return false;
	}

	public boolean addPositionMsg(IncomingMessageBean e) {
		if (isNewerAsAStoredPositionUpdateMsg(e)) {
			removeOldPositionUpdateMsg(e);
			positionUpdateMsgList.add(e);
			return true;
		}
		return false;
	}

	private void removeOldPositionUpdateMsg(IncomingMessageBean e) {
		List<IncomingMessageBean> toDelete = new Vector<IncomingMessageBean>();
		for (IncomingMessageBean msgBean : positionUpdateMsgList) {
			if (e.getContact().equals(msgBean.getContact())) {
				toDelete.add(msgBean);
			}
		}
		positionUpdateMsgList.removeAll(toDelete);
	}

	private void removeOldSensorRequests(IncomingMessageBean e) {
		List<IncomingMessageBean> toDelete = new Vector<IncomingMessageBean>();
		for (IncomingMessageBean msgBean : sensorRequestMsgList) {
			SensorRequestMsg msg = (SensorRequestMsg) e.getMessage();
			SensorRequestMsg oldMsg = (SensorRequestMsg) msgBean.getMessage();
			if (e.getContact().equals(msgBean.getContact())) {
				if (msg.getSectorID() == oldMsg.getSectorID()) {
					if (e.getSeqNr().isNewerAs(msgBean.getSeqNr())) {
						toDelete.add(msgBean);
					}
				}
			}
		}
		sensorRequestMsgList.removeAll(toDelete);
	}

	private void removeOldSensorResponses(IncomingMessageBean e) {
		List<IncomingMessageBean> toDelete = new Vector<IncomingMessageBean>();
		for (IncomingMessageBean msgBean : sensorResponseMsgList) {
			SensorResponseMsg msg = (SensorResponseMsg) e.getMessage();
			SensorResponseMsg oldMsg = (SensorResponseMsg) msgBean.getMessage();
			if (e.getContact().equals(msgBean.getContact())) {
				if (msg.getSectorID() == oldMsg.getSectorID()) {
					if (msg.getSequenceNrRequest().isNewerAs(
							oldMsg.getSequenceNrRequest())) {
						toDelete.add(msgBean);
					}
				}

			}
		}
		sensorResponseMsgList.removeAll(toDelete);
	}

	private boolean isNewerAsAStoredPositionUpdateMsg(IncomingMessageBean e) {
		boolean containsANewerMsg = false;
		for (IncomingMessageBean msgBean : positionUpdateMsgList) {
			if (e.getContact().equals(msgBean.getContact())) {
				if (e.getSeqNr().isNewerAs(msgBean.getSeqNr())) {
					return true;
				}
				containsANewerMsg = true;
			}
		}
		if (containsANewerMsg) {
			return false;
		}
		// then contain no newer msg
		return true;
	}

	private boolean isNewerAsAStoredSensorRequestMsg(IncomingMessageBean e) {
		boolean containsANewerMsg = false;
		for (IncomingMessageBean msgBean : sensorRequestMsgList) {
			SensorRequestMsg msg = (SensorRequestMsg) e.getMessage();
			SensorRequestMsg oldMsg = (SensorRequestMsg) msgBean.getMessage();
			if (e.getContact().equals(msgBean.getContact())) {
				if (msg.getSectorID() == oldMsg.getSectorID()) {
					if (e.getSeqNr().isNewerAs(msgBean.getSeqNr())) {
						return true;
					}
					containsANewerMsg = true;

				}
			}
		}
		if (containsANewerMsg) {
			return false;
		}
		// then contain no newer msg
		return true;
	}

	private boolean isNewerAsStoredSensorResponseMsg(IncomingMessageBean e) {
		boolean containsANewerMsg = false;
		for (IncomingMessageBean msgBean : sensorResponseMsgList) {
			SensorResponseMsg msg = (SensorResponseMsg) e.getMessage();
			SensorResponseMsg oldMsg = (SensorResponseMsg) msgBean.getMessage();
			if (e.getContact().equals(msgBean.getContact())) {
				if (msg.getSectorID() == oldMsg.getSectorID()) {
					if (msg.getSequenceNrRequest().isNewerAs(
							oldMsg.getSequenceNrRequest())) {
						return true;
					}
					containsANewerMsg = true;
				}

			}
		}
		if (containsANewerMsg) {
			return false;
		}
		// then contain no newer msg
		return true;
	}

	public boolean addAll(Collection<? extends IncomingMessageBean> c) {
		boolean change = true;
		for (IncomingMessageBean msgBean : c) {
			change &= add(msgBean);
		}
		return change;
	}

	public boolean isEmpty() {
		return positionUpdateMsgList.isEmpty()
				&& sensorRequestMsgList.isEmpty()
				&& sensorResponseMsgList.isEmpty();
	}

	public void clear() {
		positionUpdateMsgList.clear();
		sensorRequestMsgList.clear();
		sensorResponseMsgList.clear();
	}

	public int size() {
		return positionUpdateMsgList.size() + sensorRequestMsgList.size()
				+ sensorResponseMsgList.size();
	}

	public LinkedList<IncomingMessageBean> getAll() {
		LinkedList<IncomingMessageBean> all = new LinkedList<IncomingMessageBean>();
		all.addAll(positionUpdateMsgList);
		all.addAll(sensorRequestMsgList);
		all.addAll(sensorResponseMsgList);
		return all;
	}

	public LinkedList<IncomingMessageBean> getPositionUpdateMsgs() {
		return positionUpdateMsgList;
	}

	public LinkedList<IncomingMessageBean> getSensorRequestsMsgs() {
		return sensorRequestMsgList;
	}

	public LinkedList<IncomingMessageBean> getSensorResponseMsgs() {
		return sensorResponseMsgList;
	}
}
