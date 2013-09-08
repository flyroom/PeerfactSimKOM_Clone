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

package org.peerfact.impl.application.kbrlookupgenerator;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRLookupProvider;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.stats.distributions.Distribution;
import org.peerfact.impl.util.stats.distributions.UniformDistribution;

/**
 * A lookup generator for overlays which implement the KBR interface. It
 * generates random lookups.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 * @version 01/14/2012
 */
public class KBRLookupGenerator extends AbstractApplication implements
		KBRListener<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>>,
		ConnectivityListener {

	private static final double TIME_BETWEEN_RANDOM_LOOKUPS = 10 * Simulator.MINUTE_UNIT;

	private final KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> node;

	private PeriodicLookupOperation periodicOperation;

	private KBRLookupProvider<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> lookupProvider;

	/**
	 * @param node
	 * @param distribution
	 */
	public KBRLookupGenerator(
			KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> node,
			Distribution distribution) {
		this.node = node;

		// Set the application as KBRListener and ConnectivityListener
		node.setKBRListener(this);
		node.getHost().getNetLayer().addConnectivityListener(this);
		lookupProvider = node.getKbrLookupProvider();

		// create periodic lookup operation
		if (distribution == null) {
			periodicOperation = new PeriodicLookupOperation(this,
					new UniformDistribution(0,
							TIME_BETWEEN_RANDOM_LOOKUPS));

		} else {
			periodicOperation = new PeriodicLookupOperation(this, distribution);
		}
	}

	public void startLookups() {
		log.debug("start lookups");
		periodicOperation.start();
	}

	public void stopLookups() {
		log.debug("stop lookups");
		periodicOperation.stop();
	}

	@Override
	public void deliver(OverlayKey<?> key, Message msg) {
		// nothing to do here
	}

	@Override
	public void forward(
			KBRForwardInformation<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> information) {
		// Nothing to do here
	}

	@Override
	public void update(OverlayContact<OverlayID<?>> contact, boolean joined) {
		if (joined) {
			log.debug(contact.getOverlayID() + " - joined");
		} else {
			log.debug(contact.getOverlayID() + " - left");
		}
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {
			periodicOperation.start();
		}
		else {
			periodicOperation.stop();
		}

	}

	public void startRandomLookup() {
		OverlayKey<?> key = node.getRandomOverlayKey();
		lookupProvider.lookupKey(key, null);

		log.debug("started lookup request key = " + key + " from = "
				+ node.getLocalOverlayContact());
	}

}
