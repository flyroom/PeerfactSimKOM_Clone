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

package org.peerfact.impl.overlay.informationdissemination.von.voronoi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import org.peerfact.impl.overlay.informationdissemination.von.VonContact;
import org.peerfact.impl.overlay.informationdissemination.von.VonID;
import org.peerfact.impl.overlay.informationdissemination.von.VonNodeInfo;


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
 * This class was used to test the functions provided by the voronoi library.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TestVoronoi extends JFrame implements MouseInputListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1049917848210953286L;

	private static int numberOfPeers = 100;

	private static Point dimensionOfWorld = new Point(800, 800);

	private static Point initalPosOfMovingPeer = new Point(250, 180);

	private static int AOI_Radius = 150;

	private static boolean Pause = false;

	private static final int borderTolerance = 50;

	private static final int maxStepWidth = 30;

	private static long seed = 42;

	private static long sleepTimeBetweenRefreshs = 200; // given in ms

	public static void main(String[] args) {

		Thread.currentThread();

		Random r = new Random(seed);

		Voronoi v = new Voronoi(VonID.EMPTY_ID);

		/*
		 * Insert moving peer
		 */
		v.insert(new VonNodeInfo(new VonContact(new VonID(0), null),
				initalPosOfMovingPeer, AOI_Radius), 0l);

		/*
		 * Create sites
		 */
		for (int i = 1; i < numberOfPeers; i++) {
			Point pos = new Point(r.nextInt(dimensionOfWorld.x),
					r.nextInt(dimensionOfWorld.y));

			v.insert(new VonNodeInfo(new VonContact(new VonID(i), null), pos,
					AOI_Radius), 0l);
		}

		TestVoronoi window = new TestVoronoi(v);

		boolean xDirection = true;
		boolean yDirection = true;

		int xStepWidth = r.nextInt(maxStepWidth);
		int yStepWidth = r.nextInt(maxStepWidth);

		while (true) {
			try {
				Thread.sleep(sleepTimeBetweenRefreshs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (Pause) {
				continue;
			}

			/*
			 * Make changes in direction of movement
			 */
			Point oldPos = v.get(0);
			if (oldPos.x + borderTolerance >= dimensionOfWorld.x
					|| oldPos.x - borderTolerance <= 0) {
				xDirection = !xDirection;
				xStepWidth = r.nextInt(maxStepWidth);
			} else if (r.nextInt(100) < 5) {
				xDirection = !xDirection;
			}
			if (oldPos.y + borderTolerance >= dimensionOfWorld.y
					|| oldPos.y - borderTolerance <= 0) {
				yDirection = !yDirection;
				yStepWidth = r.nextInt(maxStepWidth);
			} else if (r.nextInt(100) < 1) {
				yDirection = !yDirection;
			}

			/*
			 * Compute new position
			 */
			Point newPos = new Point(xDirection ? (oldPos.x + xStepWidth)
					: (oldPos.x - xStepWidth),
					yDirection ? (oldPos.y + yStepWidth)
							: (oldPos.y - yStepWidth));

			v.update(0, newPos);
			v.recompute();

			window.repaint();
		}

	}

	private final Voronoi voronoi;

	public TestVoronoi(Voronoi v) {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});

		this.addMouseListener(this);

		voronoi = v;

		this.setSize(dimensionOfWorld.x, dimensionOfWorld.y);
		this.setTitle("VON - Test Voronoi Library");
		this.setBackground(Color.WHITE);
		this.setResizable(false);
		this.setVisible(true);

	}

	@Override
	public void paint(Graphics g) {

		/*
		 * Anti-Aliasing
		 * 
		 * Does not work properly. There are to many repaints and this makes the
		 * whole thing very slow. The result is ugly flickering.
		 */
		// Graphics2D g2 = (Graphics2D) g;
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);

		g.clearRect(0, 0, dimensionOfWorld.x, dimensionOfWorld.y);

		Point site = voronoi.get(0);

		/*
		 * Paint the edges of the voronoi
		 */
		g.setColor(Color.BLACK);
		Vector<Line2d> edges = voronoi.getmEdges();

		for (Line2d e : edges) {
			g.drawLine((int) e.seg.p1.x, (int) e.seg.p1.y, (int) e.seg.p2.x,
					(int) e.seg.p2.y);
		}

		/*
		 * Draw points for other nodes.
		 */
		for (VonNodeInfo info : voronoi.getAllNodeInfo()) {
			g.setColor(Color.CYAN);
			Point pos = info.getPosition();
			g.drawOval(pos.x - 1, pos.y - 1, 2, 2);
		}

		/*
		 * Paint connection to not enclosing boundary neighbors
		 */
		VonNodeInfo[] nodeInfo = voronoi.getBoundingOrEnclosing(0, AOI_Radius);
		for (int i = 0; i < nodeInfo.length; i++) {
			g.setColor(Color.BLUE);
			Point pos = nodeInfo[i].getPosition();
			g.drawLine(site.x, site.y, pos.x, pos.y);
			g.fillRect(pos.x - 3, pos.y - 3, 7, 7);
		}

		/*
		 * Paint connection to enclosing neighbors
		 */
		Vector<Integer> env = voronoi.getEnclosingNeighbors(0);
		for (int i = 0; i < env.size(); i++) {
			Point envSite = voronoi.get(env.get(i));

			g.setColor(Color.GREEN);
			g.drawLine(site.x, site.y, envSite.x, envSite.y);
			g.fillRect(envSite.x - 3, envSite.y - 3, 7, 7);
		}

		/*
		 * Paint the moving node itself
		 */
		g.setColor(Color.RED);
		g.fillOval(site.x - 5, site.y - 5, 11, 11);

		/*
		 * Paint the AOI circle
		 */
		g.setColor(Color.MAGENTA);
		g.drawOval(site.x - AOI_Radius, site.y - AOI_Radius, 2 * AOI_Radius,
				2 * AOI_Radius);

		Point2d siteP = voronoi.getSites().get(Integer.valueOf(0));

		Site s = voronoi.getmSites().get(siteP);

		for (Integer eIndex : s.edge_idxlist) {
			Line2d line = voronoi.getmEdges().get(eIndex);

			int id1 = line.vertexIndex[0];
			int id2 = line.vertexIndex[1];

			g.setColor(Color.GREEN);

			if (id1 != -1) {
				Point2d p = voronoi.getmVertices().get(id1);
				g.drawRect((int) p.x - 3, (int) p.y - 3, 5, 5);
			}
			if (id2 != -1) {
				Point2d p = voronoi.getmVertices().get(id2);
				g.drawRect((int) p.x - 3, (int) p.y - 3, 5, 5);
			}

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Pause = !Pause;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

}
