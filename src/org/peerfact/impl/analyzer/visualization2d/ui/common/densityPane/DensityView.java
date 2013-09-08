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

package org.peerfact.impl.analyzer.visualization2d.ui.common.densityPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;
import org.peerfact.impl.analyzer.visualization2d.util.ColorToolkit;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class DensityView extends Component implements ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6701830897732027013L;

	EventTimeline tl = null;

	DensityInfo dens = null;

	static final Color MINCOLOR = new Color(255, 255, 255);

	static final Color MAXCOLOR = new Color(170, 0, 0);

	int widthBuiltFor = -1;

	private int padLeft = 0;

	private int padRight = 0;

	public DensityView() {
		this.addComponentListener(this);
		this.setPreferredSize(new Dimension(100, 6));
	}

	public void setEventTimeline(EventTimeline tl) {
		this.tl = tl;
		if (this.isVisible()) {
			rebuild();
		}

	}

	private void rebuild() {

		if (this.getWidth() < padLeft + padRight) {
			return;
		}

		if (tl != null) {

			dbg("DensityView: Reconstruction");

			int pxWidth = this.getWidth() - padLeft - padRight;
			long tlWidth = tl.getMaxTime();
			double tlPartSize = (double) tlWidth / (double) pxWidth;

			int peak = calculatePeak(pxWidth, tlWidth, tlPartSize);

			dens = new DensityInfo(pxWidth);

			for (int i = 0; i < pxWidth; i++) {
				long startTime = (long) (i * tlPartSize);
				long endTime = (long) ((i + 1) * tlPartSize);

				int density = tl.getEventsBetween(startTime, endTime).size();

				dens.setAt(i, (byte) ((double) density / (double) peak * 255d));
			}

		} else {
			dens = null;
		}
	}

	private int calculatePeak(int pxWidth, long tlWidth, double tlPartSize) {
		int actualPeak = 1;

		for (int i = 0; i < pxWidth; i++) {
			long startTime = (long) (i * tlPartSize);
			long endTime = (long) ((i + 1) * tlPartSize);

			int density = tl.getEventsBetween(startTime, endTime).size();

			if (density > actualPeak) {
				actualPeak = density;
			}
		}
		return actualPeak;
	}

	@Override
	public void paint(Graphics g) {
		if (dens == null) {
			return;
		}

		if (this.getWidth() < padLeft + padRight) {
			return;
		}

		if (this.getWidth() - padLeft - padRight != dens.getSize()) {
			rebuild();
		}

		int height = this.getHeight();
		int width = this.getWidth();

		for (int i = 0; i < width - padLeft - padRight; i++) {

			int density = convertByteUnsigned(dens.getAt(i));
			g.setColor(ColorToolkit.weighColor(MINCOLOR, MAXCOLOR,
					(double) density / 255));
			g.drawLine(i + padLeft, 0, i + padLeft, height);
		}

	}

	public static class DensityInfo {

		byte[] pixels;

		public DensityInfo(int width) {
			pixels = new byte[width];
		}

		public int getSize() {
			return pixels.length;
		}

		public void setAt(int i, byte value) {
			pixels[i] = value;
		}

		public byte getAt(int i) {
			return pixels[i];
		}

	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// Nothing to do
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		rebuild();
	}

	protected static int convertByteUnsigned(byte b) {
		if (b < 0) {
			return b + 256;
		} else {
			return b;
		}
	}

	public void setPadding(int padLeft, int padRight) {
		this.padLeft = padLeft;
		this.padRight = padRight;
	}

	public void dbg(String msg) {
		// System.err.println(msg);
	}
}
