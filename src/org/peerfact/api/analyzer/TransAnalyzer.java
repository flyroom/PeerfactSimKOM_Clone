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

package org.peerfact.api.analyzer;

import org.peerfact.impl.transport.AbstractTransMessage;

/**
 * TransAnalyzers receive notifications when a network message is sent, or
 * received at the transport layer.
 * 
 */
public interface TransAnalyzer extends Analyzer {

	/**
	 * Invoking this method denotes that the given message is sent at the
	 * transport layer (from the application towards the network layer).
	 * 
	 * @param msg
	 *            the AbstractTransMessage which is sent out.
	 */
	public void transMsgSent(AbstractTransMessage msg);

	/**
	 * Invoking this method denotes that the given message is received at
	 * the transport layer (from the network layer towards the application
	 * layer).
	 * 
	 * @param msg
	 *            the received AbstractTransMessage.
	 */
	public void transMsgReceived(AbstractTransMessage msg);

}