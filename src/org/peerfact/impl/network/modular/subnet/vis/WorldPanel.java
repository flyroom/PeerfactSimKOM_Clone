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
 */
package org.peerfact.impl.network.modular.subnet.vis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.peerfact.impl.network.modular.ModularNetLayer;
import org.peerfact.impl.network.modular.device.Device;
import org.peerfact.impl.network.modular.device.MovingHostDevice;
import org.peerfact.impl.network.modular.subnet.RoutedSubnet;
import org.peerfact.impl.network.modular.subnet.routed.Channel;
import org.peerfact.impl.network.modular.subnet.routed.WirelessChannel;
import org.peerfact.impl.network.modular.subnet.topology.NetworkTopology;
import org.peerfact.impl.util.oracle.GlobalOracle;
import org.peerfact.impl.util.positioning.PositionVector;


public class WorldPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1645379072940532789L;

	private RoutedSubnet subnet;

	/**
	 * The radius used to paint Devices.
	 */
	private static final int DEVICE_PAINT_RADIUS = 4;

	private VisWindow mainWindow = null;

	public WorldPanel(VisWindow mainWindow) {

		this.subnet = ((RoutedSubnet) ((ModularNetLayer) GlobalOracle
				.getHosts().get(0).getNetLayer()).getSubnet());

		this.mainWindow = mainWindow;

		this.setPreferredSize(new Dimension(VisWindow.WORLD_DIMENSION_X
				+ DEVICE_PAINT_RADIUS, VisWindow.WORLD_DIMENSION_Y
				+ DEVICE_PAINT_RADIUS));

	}

	@Override
	protected void paintComponent(Graphics g) {
		// Anti aliasing
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		paintPlayingFieldBorder(g2);

		// get Devices
		NetworkTopology topo = subnet.getTopology();
		Device[] devices = new Device[1];
		devices = topo.getAllDevices().toArray(devices);
		for (int i = 0; i < devices.length; i++) {
			paintDevice(devices[i], g2);
			Channel[] channels = new Channel[1];
			channels = topo.getChannelsForDevice(devices[i]).toArray(channels);
			for (int j = 0; j < channels.length; j++) {
				if (channels[j] == null) {
					continue;
				}
				paintChannel(channels[j], g2);
			}
		}

	}

	private static Point getPosition(Device d) {
		PositionVector pVec = (PositionVector) d.getNetPosition();
		return pVec.asPoint();
	}

	private static void paintDevice(Device d, Graphics2D g2) {
		Point position = getPosition(d);
		if (d instanceof MovingHostDevice) {
			g2.setColor(Color.MAGENTA);
		} else {
			g2.setColor(Color.GRAY);
		}
		g2.drawOval(position.x - DEVICE_PAINT_RADIUS, position.y
				- DEVICE_PAINT_RADIUS, DEVICE_PAINT_RADIUS * 2,
				DEVICE_PAINT_RADIUS * 2);
		g2.fillOval(position.x - DEVICE_PAINT_RADIUS, position.y
				- DEVICE_PAINT_RADIUS, DEVICE_PAINT_RADIUS * 2,
				DEVICE_PAINT_RADIUS * 2);
	}

	private static void paintChannel(Channel ch, Graphics2D g2) {
		Point p1 = ch.getSourcePosition().asPoint();
		Point p2 = ch.getTargetPosition().asPoint();
		if (ch instanceof WirelessChannel) {
			g2.setColor(Color.MAGENTA);
			g2.setStroke(new BasicStroke(1f));
		} else {
			g2.setColor(Color.GRAY);
			g2.setStroke(new BasicStroke(0.1f));
			/*
			 * g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE,
			 * BasicStroke.JOIN_MITER, 10.0f, new float[] { 25.0f, 25.0f },
			 * 0.0f));
			 */
		}
		g2.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	private static void paintPlayingFieldBorder(Graphics2D g2) {
		g2.drawRect(0, 0, VisWindow.WORLD_DIMENSION_X,
				VisWindow.WORLD_DIMENSION_Y);
		g2.setPaint(Color.WHITE);

		g2.fillRect(0, 0, VisWindow.WORLD_DIMENSION_X,
				VisWindow.WORLD_DIMENSION_Y);
		g2.setPaint(Color.BLACK);
	}
}