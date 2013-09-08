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

package org.peerfact.impl.network.modular.testapp;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.application.AbstractApplication;
import org.peerfact.impl.network.modular.testapp.ModularNetLayerTestAppFactory.HostPool;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;


public class ModularNetLayerTestApp extends AbstractApplication implements
ConnectivityListener {

	private HostPool hostPool;

	TransInfo myTransInfo;

	private short port;

	TransInfo otherHostAddr = null;

	int c = 0;


	public ModularNetLayerTestApp(Host host, HostPool hostPool, short port) {
		setHost(host);
		this.hostPool = hostPool;
		this.port = port;
		myTransInfo = host.getTransLayer().getLocalTransInfo(port);
		hostPool.addHost(myTransInfo);
		host.getTransLayer().addTransMsgListener(new TransMessageListener() {

			@Override
			public void messageArrived(TransMsgEvent receivingEvent) {
				log.info("### Received message " + receivingEvent.getPayload()
						+ " by " + myTransInfo + " at simulation time "
						+ Simulator.getCurrentTime());
			}

		}, port);
		host.getNetLayer().addConnectivityListener(this);
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			log.info("Connectivity changed to offline");
		} else {
			log.info("Connectivity changed to online");
		}
	}

	public void sendUDPMsgToSomeHost(String flag, int size) {

		if (otherHostAddr == null) {
			otherHostAddr = hostPool.getHost(myTransInfo);
		}

		Message msg = new DummyTestMessage(flag, size);

		log.info("### Sending UDP dummy message " + msg
				+ " to transport address " + otherHostAddr
				+ " at simulation time " + Simulator.getCurrentTime());
		getHost().getTransLayer().send(msg, otherHostAddr, port,
				TransProtocol.UDP);
	}

	static class DummyTestMessage implements Message {

		/**
		 * 
		 */
		private static final long serialVersionUID = 9124297879136736767L;

		private long size;

		private String flag;

		public DummyTestMessage(String flag, long size) {
			this.size = size;
			this.flag = flag;
		}

		@Override
		public long getSize() {
			return size;
		}

		@Override
		public Message getPayload() {
			return this;
		}

		@Override
		public String toString() {
			return "DummyTestMessage(flag=" + flag + ", size=" + size + ")";
		}

	}

}
