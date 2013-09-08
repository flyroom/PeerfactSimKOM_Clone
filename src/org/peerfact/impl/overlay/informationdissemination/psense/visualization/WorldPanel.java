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

package org.peerfact.impl.overlay.informationdissemination.psense.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.JPanel;

import org.peerfact.api.common.Host;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseID;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseNode;
import org.peerfact.impl.overlay.informationdissemination.psense.PSenseNodeInfo;
import org.peerfact.impl.overlay.informationdissemination.psense.util.Configuration;
import org.peerfact.impl.util.oracle.GlobalOracle;


//TODO: Message Senden anzeigen
//TODO: Nur sensorKnoten anzeigen (statt alle bekannten)
//TODO: sector name in die mitte der sektoren
//TODO: kreise kräftiger zeichnen (Sensorknoten, localnode ...)
//TODO: Auswählen und Abwählen mit Klick! Und nicht irgendwo ein Klick um alle weg zu machen.
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
 * This class paint the game field. Additionally paints the nodes and many
 * characteristics of the nodes.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @version 10/15/2010
 * 
 */
public class WorldPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6024443929261980956L;

	/**
	 * The radius used to paint nodes.
	 */
	private static final int NODE_PAINT_RADIUS = 4;

	/**
	 * This radius is used to determine if a node was clicked.
	 */
	private static final int NODE_CLICK_RADIUS = NODE_PAINT_RADIUS + 1;

	private static final int DESISTANCE_FROM_NODE_FOR_CIRCLE = 3;

	private static final Color MILD_GREEN = new Color(0x41b027);

	private VisWindow mainWindow = null;

	private final LinkedHashSet<PSenseNode> registeredNodes = new LinkedHashSet<PSenseNode>();

	private final LinkedHashSet<PSenseNode> selectedNodes = new LinkedHashSet<PSenseNode>();

	public WorldPanel(VisWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.setPreferredSize(new Dimension(mainWindow.WORLD_DIMENSION_X
				+ NODE_PAINT_RADIUS, mainWindow.WORLD_DIMENSION_Y
				+ NODE_PAINT_RADIUS));

		this.addMouseListener(new MouseAdapter() {

			/**
			 * Stores the mouse position, if the mouse button is pressed
			 */
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				boolean CtrlWasPressedAtClick = e.isControlDown()
						|| e.isShiftDown();
				if (!CtrlWasPressedAtClick) {
					selectedNodes.clear();
				}
				Point lastClickedPoint = e.getPoint();
				PSenseNode node = findSelectedNode(lastClickedPoint);
				if (node != null) {
					selectedNodes.add(node);
				}
				WorldPanel.this.mainWindow.updateVariablesTree(selectedNodes);
				repaint();
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				boolean hover = false;
				for (PSenseNode node : registeredNodes) {
					if (node.getPosition() != null
							&& node.getPeerStatus() != PeerStatus.ABSENT) {
						if (e.getPoint().distance(node.getPosition()) <= NODE_CLICK_RADIUS) {
							hover = true;
						}
					}
				}
				if (hover) {
					WorldPanel.this.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					WorldPanel.this.setCursor(Cursor.getDefaultCursor());
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// nothing
			}
		});

	}

	private PSenseNode findSelectedNode(Point lastClickedPoint) {
		if (lastClickedPoint != null) {
			for (PSenseNode node : registeredNodes) {
				if (node.getPosition() != null
						&& node.getPeerStatus() != PeerStatus.ABSENT) {
					if (lastClickedPoint.distance(node.getPosition()) <= NODE_CLICK_RADIUS) {
						return node;
					}
				}

			}
		}
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		updateRegisteredHost();

		super.paintComponent(g);

		/*
		 * Anti aliasing
		 */
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		paintPlayingFieldBorder(g2);

		// paint only all Nodes! It must be, because the order of the painting.
		// It could be overpaint information.
		for (PSenseNode node : registeredNodes) {
			if (node.getPosition() != null
					&& node.getPeerStatus() != PeerStatus.ABSENT) {

				if ((mainWindow.getShowOnlySelected().isSelected() && selectedNodes
						.contains(node))
						|| !mainWindow.getShowOnlySelected().isSelected()) {
					paintNode(node.getPosition(), g2);
				}
			}
		}

		g2.setColor(Color.BLACK);
		for (PSenseNode node : registeredNodes) {
			if (node.getPosition() != null
					&& node.getPeerStatus() != PeerStatus.ABSENT) {

				if ((mainWindow.getShowOnlySelected().isSelected() && selectedNodes
						.contains(node))
						|| !mainWindow.getShowOnlySelected().isSelected()) {

					if (mainWindow.getShowOverlayID().isSelected()) {
						paintOverlayID(node.getOverlayID().toString(),
								node.getPosition(), g2);
					}

					if (mainWindow.getShowNearNodes().isSelected()
							&& isNearNode(node)) {
						paintNearNode(node.getPosition(), g2);
					}
					if (mainWindow.getShowSensorNodes().isSelected()
							&& isSensorNode(node)) {
						paintSensorNode(node.getPosition(), g2);
					}

				}

				if (selectedNodes.contains(node)) {
					paintLocalNode(node.getPosition(), g2);
					if (mainWindow.getShowVisionRange().isSelected()) {
						paintVisionRange(node.getPosition(), g2);
					}
					if (mainWindow.getShowSectorLines().isSelected()) {
						paintSectorLines(node.getPosition(),
								Configuration.NUMBER_SECTORS,
								Configuration.VISION_RANGE_RADIUS, g2);
					}
					if (mainWindow.getShowEnlargedSectorLines().isSelected()) {
						paintEnlargedSectorLines(node.getPosition(),
								Configuration.NUMBER_SECTORS,
								Configuration.VISION_RANGE_RADIUS,
								Configuration.ENLARGING_SECTOR_FACTOR, g2);
					}

					if (mainWindow.getShowStoredPositions().isSelected()) {
						paintStoredPositions(node, g2);
					}
				}

			}

		}
	}

	private static void paintStoredPositions(PSenseNode node, Graphics2D g2) {
		g2.setColor(new Color(15, 107, 214, 200));
		for (PSenseNodeInfo nodeInfo : node.getLocalPSense()
				.getAllKnownNodeInfos()) {
			g2.fillOval(nodeInfo.getPosition().x - NODE_PAINT_RADIUS,
					nodeInfo.getPosition().y - NODE_PAINT_RADIUS,
					NODE_PAINT_RADIUS * 2, NODE_PAINT_RADIUS * 2);
		}
	}

	private static void paintSensorNode(Point position, Graphics2D g2) {
		g2.setColor(Color.MAGENTA);
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0));
		int radius = NODE_PAINT_RADIUS + DESISTANCE_FROM_NODE_FOR_CIRCLE;
		g2.drawOval(position.x - radius, position.y - radius, radius * 2,
				radius * 2);
		g2.setStroke(oldStroke);
	}

	private static void paintNearNode(Point position, Graphics2D g2) {
		g2.setColor(Color.BLUE);
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2));
		int radius = NODE_PAINT_RADIUS + DESISTANCE_FROM_NODE_FOR_CIRCLE;
		g2.drawOval(position.x - radius, position.y - radius, radius * 2,
				radius * 2);
		g2.setStroke(oldStroke);
	}

	private boolean isSensorNode(PSenseNode node) {
		for (PSenseNode selectedNode : selectedNodes) {
			PSenseID[] sensorNodes = selectedNode.getLocalPSense()
					.getSensorNodes();
			for (int i = 0; i < sensorNodes.length; i++) {
				if (sensorNodes[i] != null
						&& sensorNodes[i].equals(node.getOverlayID())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isNearNode(PSenseNode node) {
		for (PSenseNode selectedNode : selectedNodes) {
			if (selectedNode.getLocalPSense().getNearNodes()
					.contains(node.getOverlayID())) {
				return true;
			}
		}
		return false;
	}

	private void paintEnlargedSectorLines(Point position, int numberSectors,
			int visionRangeRadius, double enlargingSectorFactor, Graphics2D g2) {
		double sectorSize = 360.0 / numberSectors;
		double enlargeSize = sectorSize * enlargingSectorFactor;
		double startAngle = 0 - enlargeSize / 2;
		double endAngle = sectorSize + enlargeSize / 2;
		Color color[] = { Color.BLACK, Color.MAGENTA, Color.BLUE, Color.GREEN };
		for (int i = 0; i < numberSectors; i++, startAngle += sectorSize, endAngle += sectorSize) {
			g2.setColor(color[i % color.length]);

			// startLine
			int x1 = (int) (Math.cos(Math.toRadians(startAngle)) * visionRangeRadius)
					+ position.x;
			int y1 = (int) (Math.sin(Math.toRadians(startAngle)) * visionRangeRadius)
					+ position.y;
			int x2 = (int) (Math.cos(Math.toRadians(startAngle)) * mainWindow.WORLD_DIMENSION_X)
					+ position.x;
			int y2 = (int) (Math.sin(Math.toRadians(startAngle)) * mainWindow.WORLD_DIMENSION_Y)
					+ position.y;
			g2.drawLine(x1, y1, x2, y2);
			g2.drawString("start " + Integer.valueOf(i).toString(), x1, y1);

			// endLine
			x1 = (int) (Math.cos(Math.toRadians(endAngle)) * visionRangeRadius)
					+ position.x;
			y1 = (int) (Math.sin(Math.toRadians(endAngle)) * visionRangeRadius)
					+ position.y;
			x2 = (int) (Math.cos(Math.toRadians(endAngle)) * mainWindow.WORLD_DIMENSION_X)
					+ position.x;
			y2 = (int) (Math.sin(Math.toRadians(endAngle)) * mainWindow.WORLD_DIMENSION_Y)
					+ position.y;
			g2.drawLine(x1, y1, x2, y2);
			g2.drawString("end " + Integer.valueOf(i).toString(), x1, y1);
		}

	}

	private void paintSectorLines(Point position, int numberSectors,
			int visionRangeRadius, Graphics2D g2) {
		g2.setColor(MILD_GREEN);
		double sectorSize = 360.0 / numberSectors;
		double angle = 0;
		for (int i = 0; i < numberSectors; i++, angle += sectorSize) {
			int x1 = (int) (Math.cos(Math.toRadians(angle)) * visionRangeRadius)
					+ position.x;
			int y1 = (int) (Math.sin(Math.toRadians(angle)) * visionRangeRadius)
					+ position.y;
			int x2 = (int) (Math.cos(Math.toRadians(angle)) * mainWindow.WORLD_DIMENSION_X)
					+ position.x;
			int y2 = (int) (Math.sin(Math.toRadians(angle)) * mainWindow.WORLD_DIMENSION_X)
					+ position.y;
			g2.drawLine(x1, y1, x2, y2);
			g2.drawString(Integer.valueOf(i).toString(), x1, y1);
		}

	}

	private static void paintLocalNode(Point position, Graphics2D g2) {
		int radius = NODE_PAINT_RADIUS + DESISTANCE_FROM_NODE_FOR_CIRCLE;
		g2.setColor(Color.red);
		g2.drawOval(position.x - radius, position.y - radius, radius * 2,
				radius * 2);
	}

	private static void paintOverlayID(String overlayID, Point position,
			Graphics2D g2) {
		g2.setColor(Color.black);
		g2.drawString(overlayID, position.x + 4, position.y - 4);
	}

	private static void paintVisionRange(Point position, Graphics2D g2) {
		g2.setColor(MILD_GREEN);
		g2.drawOval(position.x - Configuration.VISION_RANGE_RADIUS, position.y
				- Configuration.VISION_RANGE_RADIUS,
				Configuration.VISION_RANGE_RADIUS * 2,
				Configuration.VISION_RANGE_RADIUS * 2);

	}

	private static void paintNode(Point position, Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.drawOval(position.x - NODE_PAINT_RADIUS, position.y
				- NODE_PAINT_RADIUS, NODE_PAINT_RADIUS * 2,
				NODE_PAINT_RADIUS * 2);
		g2.fillOval(position.x - NODE_PAINT_RADIUS, position.y
				- NODE_PAINT_RADIUS, NODE_PAINT_RADIUS * 2,
				NODE_PAINT_RADIUS * 2);
	}

	private void paintPlayingFieldBorder(Graphics2D g2) {
		g2.drawRect(0, 0, mainWindow.WORLD_DIMENSION_X,
				mainWindow.WORLD_DIMENSION_Y);
		g2.setPaint(Color.WHITE);

		g2.fillRect(0, 0, mainWindow.WORLD_DIMENSION_X,
				mainWindow.WORLD_DIMENSION_Y);
		g2.setPaint(Color.BLACK);
	}

	private void updateRegisteredHost() {
		List<Host> hosts = GlobalOracle.getHosts();
		for (Host host : hosts) {
			OverlayNode<?, ?> olNode = host.getOverlay(PSenseNode.class);
			if (olNode != null) {
				PSenseNode node = (PSenseNode) olNode;
				if (!registeredNodes.contains(node)) {
					registeredNodes.add(node);
				}
			}
		}
	}
}
