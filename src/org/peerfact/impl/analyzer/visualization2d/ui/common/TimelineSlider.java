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

package org.peerfact.impl.analyzer.visualization2d.ui.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;
import org.peerfact.impl.analyzer.visualization2d.model.ModelRefreshListener;
import org.peerfact.impl.analyzer.visualization2d.model.TimelineEventListener;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.ui.common.densityPane.DensityView;


/**
 * Slider, which makes it possible to change the position of the timeline.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TimelineSlider extends JPanel implements TimelineEventListener,
MouseListener, ModelRefreshListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8975112526022783477L;

	private static final int SLIDER_SIZE = 1000;

	JSlider slider = new JSlider(0, SLIDER_SIZE);

	DensityView density = new DensityView();

	public TimelineSlider() {

		slider.addMouseListener(this);
		EventTimeline.addEventListener(this);
		// slider.setPreferredSize(new Dimension(800, 20));

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		// c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(slider, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		// 1c.weighty = 1/3;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(density, c);

		if (Controller.getModel() != null) {
			density.setEventTimeline(Controller.getTimeline());
		}
		density.setPadding(6, 6);

		VisDataModel.addRefreshListener(this);
		enableProperly();

	}

	private void sliderChanged(int sliderValue) {

		long timeline_time = (long) (Controller.getTimeline().getMaxTime() * ((double) slider
				.getValue() / (double) SLIDER_SIZE));
		TimelineSlider.changeTimeline(timeline_time);
	}

	private static void changeTimeline(long time) {
		Controller.getTimeline().jumpToTime(time);
	}

	private void refreshSlider() {

		EventTimeline tl = Controller.getTimeline();
		int value = (int) Math.rint(SLIDER_SIZE
				* ((double) tl.getActualTime() / (double) tl.getMaxTime()));
		this.slider.setValue(value);
	}

	@Override
	public void actualTimeChanged(EventTimeline invoker) {
		if (invoker == Controller.getTimeline()) {
			this.refreshSlider();
		}
	}

	@Override
	public void maxTimeChanged(EventTimeline invoker) {
		if (invoker == Controller.getTimeline()) {
			this.refreshSlider();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (slider.isEnabled()) {
			this.sliderChanged(slider.getValue());
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modelNeedsRefresh(VisDataModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newModelLoaded(VisDataModel model) {
		enableProperly();
		if (model != null) {
			density.setEventTimeline(model.getTimeline());
		}
	}

	private void enableProperly() {
		slider.setEnabled(Controller.getModel() != null);
	}

	@Override
	public void simulationFinished(VisDataModel model) {
		// TODO Auto-generated method stub

	}

}
