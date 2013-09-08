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

package org.peerfact.impl.overlay.dht.chord.adaptivechord.capacityRing.interfaces;

import java.util.LinkedList;

import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.overlay.dht.chord.adaptivechord.objectRing.components.ChordNodeType;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordContact;
import org.peerfact.impl.overlay.dht.chord.base.components.AbstractChordNode;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordBootstrapManager;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordKey;
import org.peerfact.impl.util.Tuple;


/**
 * 
 * @author wette
 * @version 1.0, 06/20/2011
 * 
 *          abstract class to remote control a DHT Node.
 * 
 *          This is used by the ZHChordNode to control its ChordNode.
 */

public abstract class AdaptiveRemoteControl extends AbstractChordNode {

	public AdaptiveRemoteControl(TransLayer transLayer, short port,
			ChordBootstrapManager bootstrap) {
		super(transLayer, port, bootstrap);
	}

	public final static double MAXIMUM_UPLOAD_BANDWIDTH = 50.0 * 1000.0 * 1000.0; // 50

	// MBit
	// /
	// sec

	public final double OVERLOAD_FACTOR = 0.8; // a node is overloaded if the

	// consumed upload exceeds 0.8
	// times the avaiable upload
	// bandwidth

	/**
	 * get a value between 0 and 1. 1 meaning the node has very much resources
	 * free and 0 meaning no free resources are left at all.
	 * 
	 * @return measure of free resources
	 */
	public abstract double getPerformanceIndex();

	/**
	 * get the aggregated bandwidth consumed by all documents hold by the node.
	 * 
	 * @return used bandwidth
	 */
	public abstract double getLoad();

	/**
	 * get all documents of a node.
	 * 
	 * @return a list of all offered objects together with the load each object
	 *         produces
	 */
	public abstract LinkedList<Tuple<ChordKey, Double>> getOfferedObjects();

	/**
	 * add a redirection for a document. if a redirection is installed, all
	 * querys for that document are redirected to the mirror.
	 * 
	 * @param documentId
	 *            the document to outsource
	 * @param mirrorNode
	 *            the new home of the document
	 */
	public abstract void addRedirection(ChordKey documentId,
			AbstractChordContact mirrorNode);

	/**
	 * show the mirror of a document that belongs to this node.
	 * 
	 * @param documentId
	 *            the id of the document
	 * @return the node that serves the questioned document
	 */
	public abstract AbstractChordContact getMirrorForObject(ChordKey documentId);

	/**
	 * check if a node runs over its capacitys
	 * 
	 * @return true if the node is overloaded
	 */
	public abstract boolean isOverloaded();

	/**
	 * remove all mirrors for the document.
	 * 
	 * @param documentId
	 *            id of the document.
	 */
	public abstract void removeRedirection(ChordKey documentId);

	public abstract void setNodeType(ChordNodeType loadbalancingnetnode);

	public abstract void initReoccuringOperations();

}
