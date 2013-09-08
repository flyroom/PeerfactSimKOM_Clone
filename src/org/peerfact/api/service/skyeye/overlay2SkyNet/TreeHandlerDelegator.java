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

package org.peerfact.api.service.skyeye.overlay2SkyNet;

import java.math.BigDecimal;

import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.LookupResult;
import org.peerfact.api.service.skyeye.overlay2SkyNet.util.ProcessNextLevelResult;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetID;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public interface TreeHandlerDelegator {

	public void setSkyNetNode(SkyNetNodeInterface skyNetNode);

	public void setOwnOverlayNode(OverlayNode<?, ?> ownOverlayNode);

	// //////////////////////////////////////////////////////////////
	// Methods for looking up the parent-coordinator of a coordinator by the
	// calculated coordinatorKey
	// //////////////////////////////////////////////////////////////

	public void lookupParentCoordinator(SkyNetID coordinatorKey,
			OperationCallback<?> callback);

	public void lookupOperationFailed(Operation<?> op);

	public LookupResult lookupOperationSucceeded(Operation<?> op);

	// //////////////////////////////////////////////////////////////
	// Methods for processing the next level of the SkyNet-Tree
	// //////////////////////////////////////////////////////////////

	public void processNextLevel(SkyNetID ownID, SkyNetID skyNetCoKey,
			BigDecimal left, BigDecimal right, int iter,
			OperationCallback<?> callback);

	public void processNextLevelOperationFailed(Operation<?> op);

	public ProcessNextLevelResult processNextLevelOperationSucceeded(
			Operation<?> op);

	// //////////////////////////////////////////////////////////////
	// Methods for determining the responsibility-interval of a node on the
	// current overlay and its mapping to the ID-space of SkyNet
	// //////////////////////////////////////////////////////////////

	public void calculateResponsibilityInterval(SkyNetID id,
			OperationCallback<OverlayContact<OverlayID<?>>> callback);

	public void calculateResponsibilityIntervalOperationFailed(Operation<?> op);

	public SkyNetID calculateResponsibilityIntervalOperationSucceeded(
			Operation<?> op);
}
