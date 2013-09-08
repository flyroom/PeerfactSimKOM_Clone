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

package org.peerfact.impl.overlay.informationdissemination.von.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.informationdissemination.von.VonConfiguration;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNode;
import org.peerfact.impl.overlay.informationdissemination.von.voronoi.Line2d;
import org.peerfact.impl.overlay.informationdissemination.von.voronoi.Point2d;
import org.peerfact.impl.util.oracle.GlobalOracle;


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
 * This class is used to visualize a scenario. To use it, simply add it to the
 * monitors in the configuration file.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class VisWindow extends JFrame implements OperationAnalyzer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2405164286453696163L;

	public static class WorldPane extends JPanel implements MouseInputListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1836054423204097526L;

		/**
		 * The radius used to paint nodes. This radius is also used to determine
		 * if a node was clicked.
		 */
		private static final int NODE_PAINT_RADIUS = 3;

		private static final int NODE_CLICK_RADIUS = NODE_PAINT_RADIUS + 1;

		private final LinkedHashSet<VonNode> registeredNodes = new LinkedHashSet<VonNode>();

		private final LinkedHashSet<VonID> selectedNodeID = new LinkedHashSet<VonID>();

		private Point lastClickedPoint = null;

		private boolean CtrlWasPressedAtClick = false;

		public WorldPane() {
			this.addMouseListener(this);
			this.setSize(VonConfiguration.WORLD_DIMENSION_X,
					VonConfiguration.WORLD_DIMENSION_Y);
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

			for (VonNode node : registeredNodes) {
				if (node.getPosition() == null) {
					continue;
				}

				if (node.getPeerStatus() == PeerStatus.PRESENT) {
					g.setColor(Color.GREEN);
				} else if (node.getPeerStatus() == PeerStatus.TO_JOIN) {
					g.setColor(Color.ORANGE);
				} else {
					g.setColor(Color.LIGHT_GRAY);
				}

				Point pos = node.getPosition();
				g.fillOval(pos.x - NODE_PAINT_RADIUS,
						pos.y - NODE_PAINT_RADIUS, 2 * NODE_PAINT_RADIUS + 1,
						2 * NODE_PAINT_RADIUS + 1);

				if (lastClickedPoint != null
						&& (lastClickedPoint.distance(node.getPosition().x,
								node.getPosition().y) <= NODE_CLICK_RADIUS)
						&& node.getPeerStatus() != PeerStatus.ABSENT) {

					if (selectedNodeID.contains(node.getVonID())) {
						int selSize = selectedNodeID.size();
						selectedNodeID.remove(node.getVonID());

						if (!CtrlWasPressedAtClick && selSize != 1) {
							selectedNodeID.clear();
							selectedNodeID.add(node.getVonID());
						}
					} else {
						if (!CtrlWasPressedAtClick) {
							selectedNodeID.clear();
						}

						selectedNodeID.add(node.getVonID());
					}

					lastClickedPoint = null;
					CtrlWasPressedAtClick = false;
				}

				if (selectedNodeID != null
						&& selectedNodeID.contains(node.getVonID())) {

					if (node.getPeerStatus() == PeerStatus.PRESENT) {
						/*
						 * Paint the edges of the voronoi
						 */
						g.setColor(Color.BLACK);

						// Clone edges to avoid concurrent modification
						// exception
						Vector<Line2d> edges = (Vector<Line2d>) node
								.getLocalVoronoi().getmEdges().clone();

						for (Line2d e : edges) {

							g.setColor(Color.BLACK);
							g.drawLine((int) e.seg.p1.x, (int) e.seg.p1.y,
									(int) e.seg.p2.x, (int) e.seg.p2.y);

						}

						/*
						 * Mark all nodes that are part of the local voronoi
						 */

						// Clone sites to avoid concurrent modification
						// exception?
						ConcurrentHashMap<Integer, Point2d> sites = node
								.getLocalVoronoi().getSites();

						for (Entry<Integer, Point2d> site : sites.entrySet()) {

							boolean isBoundaryNeighbor = node.getLocalVoronoi()
									.isBoundaryNeighborOf(
											node.getVonID().getUniqueValue(),
											site.getKey(), node.getAOI());

							boolean isEnclosingNeighbor = node
									.getLocalVoronoi().isEnclosingNeighbor(
											node.getVonID().getUniqueValue(),
											site.getKey());

							boolean isAoiNeighbor = node.getLocalVoronoi()
									.isAoiNeighbor(
											node.getVonID().getUniqueValue(),
											site.getKey(), node.getAOI());

							if (selectedNodeID
									.contains(new VonID(site.getKey()))) {
								g.setColor(Color.DARK_GRAY); // the selected
							} else if (isBoundaryNeighbor
									&& isEnclosingNeighbor) {
								g.setColor(Color.RED); // boundary and enclosing
							} else if (isBoundaryNeighbor) {
								g.setColor(Color.ORANGE); // only boundary
							} else if (isEnclosingNeighbor) {
								g.setColor(Color.BLUE); // only enclosing
							} else if (isAoiNeighbor)
							{
								g.setColor(Color.GREEN); // only in AOI
							}

							g.drawOval((int) site.getValue().x - 2
									* NODE_PAINT_RADIUS,
									(int) site.getValue().y - 2
											* NODE_PAINT_RADIUS,
									4 * NODE_PAINT_RADIUS,
									4 * NODE_PAINT_RADIUS);
						}

						g.setColor(Color.BLUE);

						/*
						 * Paint the AOI boundary
						 */
						g.drawOval(pos.x - node.getAOI(),
								pos.y - node.getAOI(), node.getAOI() * 2,
								node.getAOI() * 2);
					}
					/*
					 * Paint the node itself
					 */
					g.fillOval(pos.x - NODE_PAINT_RADIUS, pos.y
							- NODE_PAINT_RADIUS, 2 * NODE_PAINT_RADIUS + 1,
							2 * NODE_PAINT_RADIUS + 1);

					g.drawString(node.getVonID() + "", pos.x + 2
							* NODE_PAINT_RADIUS, pos.y + 2 * NODE_PAINT_RADIUS);

				}

			}

		}

		private void updateRegisteredHost() {
			// registeredNodes.clear();

			List<Host> hosts = GlobalOracle.getHosts();
			for (Host host : hosts) {
				OverlayNode<?, ?> olNode = host.getOverlay(VonNode.class);
				if (olNode != null) {
					VonNode vonNode = (VonNode) olNode;

					if (!registeredNodes.contains(olNode)) {
						registeredNodes.add(vonNode);
					}
				}
			}
		}

		/**
		 * @return the id of the selected node
		 */
		public LinkedHashSet<VonID> getSelectedNodeID() {
			return selectedNodeID;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			this.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// No implementation here (not sure why) by Thim
			// This text is here so that later on someone will find this.

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// No implementation here (not sure why) by Thim
			// This text is here so that later on someone will find this.

		}

		@Override
		public void mousePressed(MouseEvent mE) {
			CtrlWasPressedAtClick = mE.isControlDown() || mE.isShiftDown();
			lastClickedPoint = mE.getPoint();
			this.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// No implementation here (not sure why) by Thim
			// This text is here so that later on someone will find this.
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// No implementation here (not sure why) by Thim
			// This text is here so that later on someone will find this.

		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// No implementation here (not sure why) by Thim
			// This text is here so that later on someone will find this.

		}

	}

	private final WorldPane worldPane = new WorldPane();

	private void initWindow() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setSize(VonConfiguration.WORLD_DIMENSION_X,
				VonConfiguration.WORLD_DIMENSION_Y);
		this.setTitle("VON Visualization");

		this.getContentPane().add(BorderLayout.CENTER, worldPane);

		this.setVisible(true);
	}

	@Override
	public void operationFinished(Operation<?> op) {
		this.repaint();

	}

	@Override
	public void operationInitiated(Operation<?> op) {
		this.repaint();

	}

	@Override
	public void start() {
		initWindow();

	}

	@Override
	public void stop(Writer output) {
		// Do nothing --> the window is kept open after the simulation finished
	}

}
