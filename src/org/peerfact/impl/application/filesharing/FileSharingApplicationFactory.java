/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.application.filesharing;

import java.util.Iterator;

import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.api.overlay.dht.DHTKey;
import org.peerfact.api.overlay.dht.DHTNode;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.overlay.unstructured.HeterogeneousOverlayNode;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.impl.application.filesharing.overlayhandler.DHTHandler;
import org.peerfact.impl.application.filesharing.overlayhandler.GnutellaHandler;
import org.peerfact.impl.application.filesharing.overlayhandler.KBRHandler;
import org.peerfact.impl.application.filesharing.overlayhandler.TestOracle;


/**
 * Factory to create FileSharingApplication on nodes.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public class FileSharingApplicationFactory implements ComponentFactory {

	private boolean oracleTest = false;

	@Override
	public FileSharingApplication createComponent(Host host) {

		ContentDistribution<OverlayKey<?>> cd = (ContentDistribution<OverlayKey<?>>) host
				.getOverlay(ContentDistribution.class);
		if (cd != null) {
			if (oracleTest) {
				return new FileSharingApplication(new TestOracle(), cd);
			} else if (host.getOverlay(HeterogeneousOverlayNode.class) != null) {
				return new FileSharingApplication(
						new GnutellaHandler(
								(HeterogeneousOverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>>) host
										.getOverlay(HeterogeneousOverlayNode.class)), cd);
			} else if (host.getOverlay(DHTNode.class) != null) {
				return new FileSharingApplication(
						new DHTHandler(
								(DHTNode<OverlayID<?>, OverlayContact<OverlayID<?>>, DHTKey<?>>) host
										.getOverlay(DHTNode.class)), cd);
			} else if (host.getOverlay(KBRNode.class) != null) {
				return new FileSharingApplication(
						new KBRHandler(
								(KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>>) host
										.getOverlay(KBRNode.class)), cd);
			}
		}

		Iterator<OverlayNode<?, ?>> olays = host.getOverlays();

		StringBuffer buf = new StringBuffer();
		while (olays.hasNext()) {
			buf.append(olays.next().getClass().getSimpleName() + ", ");
		}

		String olaysStr = buf.toString();
		throw new ConfigurationException("The host " + host + ", " + olaysStr
				+ " is not supported by filesharing");
	}

	@Override
	public String toString() {
		return "FileSharing factory";
	}

	/**
	 * Puts this instance into "oracle" test mode, a mode that automatically
	 * locates a document if it was shared and the sharing host is online. Made
	 * for debugging purposes. Has to be set before the filesharing component is
	 * created, e.g. in the XML config file.
	 * 
	 * @param test
	 */
	public void setOracleTest(boolean test) {
		this.oracleTest = test;
	}
}
