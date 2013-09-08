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

package org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.controller.player.PlayerEventListener;


/**
 * Allows you to set the speed.
 * 
 * Speed ​​= length of a virtual unit of time in ms.
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class SpeedChanger extends JPanel implements ChangeListener,
		PlayerEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3966024292754025010L;

	static final double MAX_SPEED = 100d;

	static final double MIN_SPEED = 0.005d;

	static final double BASE = 10d;

	protected JLabel label = new JLabel();

	protected JSlider slider = new JSlider();

	static final int SLIDER_UNITS = 1000;

	public SpeedChanger() {
		max_speed_log = Math.log(MAX_SPEED);
		min_speed_log = Math.log(MIN_SPEED);

		label.setPreferredSize(new Dimension(70, 20));
		label.setBackground(SystemColor.text);
		label.setOpaque(true);

		slider.setMinimum(0);
		slider.setMaximum(SLIDER_UNITS);
		slider.addChangeListener(this);

		this.setLayout(new FlowLayout());
		this.add(label);
		this.add(slider);
		this.setToolTipText("Speed settings (virtual milliseconds per real second)");

		double initialSpeed = Controller.getPlayer().getSpeed();

		Controller.getPlayer().addEventListener(this);

		setSliderValueFromSpeed(initialSpeed);
		this.setSpeedText(initialSpeed);

	}

	final double max_speed_log;

	final double min_speed_log;

	private double getSpeedFromSliderValue() {

		return Math
				.exp(min_speed_log
						+ ((double) slider.getValue() / SLIDER_UNITS * (max_speed_log - min_speed_log)));

	}

	private void setSliderValueFromSpeed(double speed) {

		int result = (int) ((Math.log(speed) - min_speed_log)
				/ (max_speed_log - min_speed_log) * SLIDER_UNITS);

		slider.setValue(result);

		// log.debug("Result: SV=" + result + " speed=" + speed);
	}

	private void setSpeedText(double speed) {
		label.setText((int) (speed * 1000) + "ms");
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		Controller.getPlayer().setSpeed(getSpeedFromSliderValue());
	}

	@Override
	public void speedChange(double speed) {
		setSliderValueFromSpeed(speed);
		this.setSpeedText(speed);
	}

	@Override
	public void forward() {
		// Nothing to do
	}

	@Override
	public void pause() {
		// Nothing to do
	}

	@Override
	public void play() {
		// Nothing to do
	}

	@Override
	public void reverse() {
		// Nothing to do
	}

	@Override
	public void stop() {
		// Nothing to do
	}

	@Override
	public void quantizationChange(double quantization) {
		// TODO Auto-generated method stub

	}

}
