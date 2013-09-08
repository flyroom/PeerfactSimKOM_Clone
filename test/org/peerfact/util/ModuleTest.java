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

package org.peerfact.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.simengine.SimulatorTest;


/**
 * Check RESI compatibility ...
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 30.11.2007
 * 
 */
@Deprecated
public class ModuleTest extends SimulatorTest {
	private List<Network> netModules;

	private List<Overlay> onetModules;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		netModules = new LinkedList<Network>();
		onetModules = new LinkedList<Overlay>();
		for (int i = 0; i < 5; i++) {
			Network net = new Network();
			netModules.add(net);
			Overlay overlay = new Overlay();
			onetModules.add(overlay);
			net.setOnet(overlay);
			overlay.setNet(net);
		}
	}

	@Test
	public void testSend() {
		assertEquals(5, Network.nets.size());
		assertEquals(5, netModules.size());
		scheduleEvent(1, onetModules.get(0));
		runSimulation(100l);
		// for (Iterator iter = netModules.iterator(); iter.hasNext();) {
		// Network net = (Network) iter.next();
		// net.send
		// }
	}

	public static class Network implements SimulationEventHandler {
		static List<Network> nets = new LinkedList<Network>();

		Overlay onet;

		public Network() {
			nets.add(this);
		}

		public void setOnet(Overlay overlay) {
			this.onet = overlay;

		}

		@Override
		public void eventOccurred(SimulationEvent se) {
			Object msgType = se.getData();
			if (msgType instanceof NetworkSendMsg) {
				NetworkReceiveMsg msg = new NetworkReceiveMsg();
				msg.data = ((NetworkSendMsg) msgType).data;
				long time = Simulator.getCurrentTime();
				for (Iterator<Network> iter = nets.iterator(); iter.hasNext();) {
					Network net = iter.next();
					Simulator.scheduleEvent(msg, ++time, net, null);
				}
			} else if (msgType instanceof NetworkReceiveMsg) {
				OverlayReceiveMsg msg = new OverlayReceiveMsg(
						((NetworkReceiveMsg) msgType).data);
				long time = Simulator.getCurrentTime();
				Simulator.scheduleEvent(msg, ++time, onet, null);
			}
			else {
				System.err.println("Unknown net msg " + msgType);
				// Scheduler.getScheduler().scheduleEvent(content,
				// simulationTime,
				// handler, eventType);
			}
		}

	}

	public static class NetworkSendMsg {
		Object data;

		public NetworkSendMsg(Object data) {
			super();
			this.data = data;
		}
	}

	public static class NetworkReceiveMsg {
		Object data;
	}

	public static class Overlay implements SimulationEventHandler {
		static List<Overlay> onets = new LinkedList<Overlay>();

		private Network net;

		@Override
		public void eventOccurred(SimulationEvent se) {
			Object msgType = se.getData();
			if (msgType instanceof OverlaySendMsg) {
				Simulator.scheduleEvent(new NetworkSendMsg(
						((OverlaySendMsg) msgType).data), Simulator
						.getCurrentTime() + 10, net, null);
			} else if (msgType instanceof OverlayReceiveMsg) {
				System.err.println("Received onet msg " + msgType);
			} else {
				System.err.println("Unknown onet msg " + msgType);
			}
		}

		public void setNet(Network net) {
			this.net = net;

		}

	}

	public static class OverlaySendMsg {
		Object data;

		public OverlaySendMsg(Object data) {
			super();
			this.data = data;
		}
	}

	public static class OverlayReceiveMsg {
		Object data;

		public OverlayReceiveMsg(Object data) {
			super();
			this.data = data;
		}
	}
}
