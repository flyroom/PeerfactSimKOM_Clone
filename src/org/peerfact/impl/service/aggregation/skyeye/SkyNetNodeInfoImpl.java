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

import java.math.BigDecimal;

import org.peerfact.Constants;
import org.peerfact.api.network.NetPosition;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.transport.TransInfo;

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
 * This class implements {@link SkyNetNodeInfo}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SkyNetNodeInfoImpl implements SkyNetNodeInfo, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8060410507750180368L;

	private SkyNetID skyNetID;

	private SkyNetID coordinatorKey;

	private TransInfo transInfo;

	private int level;

	private int observedLevel = -1;

	@Override
	public TransInfo getTransInfo() {
		return transInfo;
	}

	@Override
	public void setTransInfo(TransInfo transInfo) {
		this.transInfo = transInfo;
	}

	public SkyNetNodeInfoImpl(SkyNetID id, SkyNetID key, TransInfo transInfo,
			int level) {
		skyNetID = id;
		coordinatorKey = key;
		this.transInfo = transInfo;
		this.level = level;
	}

	@Override
	public SkyNetID getSkyNetID() {
		return skyNetID;
	}

	@Override
	public void setSkyNetID(SkyNetID skyNetID) {
		this.skyNetID = skyNetID;
	}

	@Override
	public SkyNetID getCoordinatorKey() {
		return coordinatorKey;
	}

	@Override
	public void setCoordinatorKey(SkyNetID coordinatorKey) {
		this.coordinatorKey = coordinatorKey;
	}

	@Override
	public boolean isComplete() {
		if (skyNetID != null && coordinatorKey != null) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String ret = "SkyNetNodeInfo[";
		if (skyNetID != null) {
			ret += "SkyNetID = " + skyNetID.getPlainSkyNetID() + "; ";
		}
		if (coordinatorKey != null) {
			ret += "C-Key = " + coordinatorKey.getPlainSkyNetID() + "; ";
		}
		if (transInfo != null) {
			ret += transInfo.toString();
		}
		return ret + "; level = " + level + "]";
	}

	@Override
	public double getDistance(NetPosition netPosition) {
		BigDecimal foreignID = ((SkyNetNodeInfoImpl) netPosition).getSkyNetID()
				.getID();
		return this.getSkyNetID().getID().subtract(foreignID).abs()
				.doubleValue();
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public SkyNetNodeInfoImpl clone() {
		return new SkyNetNodeInfoImpl(skyNetID, coordinatorKey, transInfo,
				level);
	}

	@Override
	public SkyNetID getOverlayID() {
		return getSkyNetID();
	}

	@Override
	public int getObservedLevelFromRoot() {
		return observedLevel;
	}

	@Override
	public void setObservedLevelFromRoot(int level) {
		observedLevel = level;
	}

	@Override
	public long getTransmissionSize() {
		return skyNetID.getTransmissionSize()
				+ coordinatorKey.getTransmissionSize()
				+ transInfo.getTransmissionSize() + Constants.INT_SIZE
				+ Constants.INT_SIZE;
	}

}
