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

package org.peerfact.impl.overlay.dht.can.vis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.overlay.AbstractOverlayNode.PeerStatus;
import org.peerfact.impl.overlay.dht.can.components.CanConfig;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayKey;
import org.peerfact.impl.util.oracle.GlobalOracle;

/**
 * This class is used to show a visualization of the CAN. Good for debugging,
 * but only useful for a few peers.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanGraphic extends JFrame implements OperationAnalyzer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7377672484934376596L;

	public static class Graphic extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6445838217544400155L;

		private final List<CanNode> registeredNodes = new LinkedList<CanNode>();

		public Graphic() {
			this.setSize((CanConfig.CanSize / CanConfig.VisSize),
					CanConfig.CanSize / CanConfig.VisSize);
		}

		@Override
		public void paint(Graphics g)
		{
			updateRegisteredHost();

			super.paintComponent(g);

			/*
			 * Anti aliasing
			 */
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			for (int i = 0; i < registeredNodes.size(); i++) {
				CanNode node = registeredNodes.get(i);

				if (node.getPeerStatus() == PeerStatus.PRESENT) {
					int[] pos = node.getLocalOverlayContact().getArea()
							.getArea();
					int spaceBetweenLines = 10;

					g.setColor(Color.red);
					g.drawRect(pos[0] / CanConfig.VisSize, pos[2]
							/ CanConfig.VisSize,
							(pos[1] - pos[0]) / CanConfig.VisSize,
							(pos[3] - pos[2]) / CanConfig.VisSize);

					Font font = new Font(g.getFont().getFontName(), Font.PLAIN,
							Math.min(g.getFont().getSize(),
									9));// (pos[3]-pos[2])/(CanConfig.VisSize)/4));
					g.setFont(font);

					String output = node.getCanOverlayID().getValue()
							.toString().subSequence(0, 4).toString();

					g.setColor(Color.blue);
					g.drawString(output + "", (pos[0]) / CanConfig.VisSize + 5,
							(pos[2]) / CanConfig.VisSize + 10);
					g.setColor(Color.black);

					g.drawString(node.getLocalOverlayContact().getArea()
							.getVid()
							.toString()
							+ "",
							(pos[0]) / CanConfig.VisSize + 5,
							(pos[2]) / CanConfig.VisSize + spaceBetweenLines
									* 2);
					g.drawString(node.getVIDNeighbours()[0].getArea().getVid()
							.toString()
							+ "",
							(pos[0]) / CanConfig.VisSize + 5,
							(pos[2]) / CanConfig.VisSize + spaceBetweenLines
									* 3);
					g.drawString(node.getVIDNeighbours()[1].getArea().getVid()
							.toString()
							+ "",
							(pos[0]) / CanConfig.VisSize + 5,
							(pos[2]) / CanConfig.VisSize + spaceBetweenLines
									* 4);

					// for(int j=0;j<node.getNeighbours().size();j++){
					// if(((CanOverlayContact)node.getNeighbours().get(j)).getOverlayID().toString().equals("191238049301810595734908489859115168821139990602"))
					// g.setColor(Color.red);
					// else

					// g.setColor(Color.black);
					// g.drawString(((CanOverlayContact)node.getNeighbours().get(j)).getOverlayID().toString().substring(0,4),
					// (pos[0])/CanConfig.VisSize+5,
					// (pos[2])/CanConfig.VisSize + spaceBetweenLines*6
					// +j*spaceBetweenLines);
					// }

					for (int j = 0; j < node.getStoredHashs().size(); j++) {
						g.setColor(Color.green);
						Object[] drawObject = node.getStoredHashs().get(j);
						CanOverlayKey drawID = (CanOverlayKey) (drawObject[0]);
						g.drawOval(drawID.getXValue().intValue()
								/ CanConfig.VisSize - 3,
								drawID.getYValue().intValue()
										/ CanConfig.VisSize - 3, 6, 6);
					}
				}

			}
		}

		/**
		 * updates all register nodes. (not sure if it is still needed.)
		 */
		private void updateRegisteredHost() {

			List<Host> hosts = GlobalOracle.getHosts();
			for (Host host : hosts) {
				OverlayNode<?, ?> olNode = host.getOverlay(CanNode.class);
				if (olNode != null) {
					CanNode canNode = (CanNode) olNode;

					if (!registeredNodes.contains(olNode)) {
						registeredNodes.add(canNode);
					}
				}
			}
		}
	}

	private final Graphic grafik = new Graphic();

	public void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setSize((CanConfig.CanSize / CanConfig.VisSize) + 10,
				(CanConfig.CanSize / CanConfig.VisSize) + 10);
		this.setTitle("CAN Visualization");

		this.getContentPane().add(BorderLayout.CENTER, grafik);

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
		init();

	}

	public void updateRepaint() {
		this.repaint();
	}

	@Override
	public void stop(Writer output) {
		// Nothing to do here
	}

	class MyAdjustmentListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent arg0) {
			repaint();
		}
	}

}
