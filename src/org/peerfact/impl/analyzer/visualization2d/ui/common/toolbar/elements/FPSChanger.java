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
 * Allows you to set the quantization.
 * 
 * Unit: the refresh interval in real milliseconds
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class FPSChanger extends JPanel implements ChangeListener,
		PlayerEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2980517499160605264L;

	static final double MIN_Q = 0.025d;

	static final double MAX_Q = 2d;

	static final double EXPONENT = 0.5d;

	protected JLabel label = new JLabel();

	protected JSlider slider = new JSlider();

	static final int SLIDER_UNITS = 1000;

	public FPSChanger() {
		label.setPreferredSize(new Dimension(50, 20));
		label.setBackground(SystemColor.text);
		label.setOpaque(true);

		slider.setMinimum(0);
		slider.setMaximum(SLIDER_UNITS);
		slider.addChangeListener(this);

		this.setLayout(new FlowLayout());
		this.add(label);
		this.add(slider);
		this.setToolTipText("Quantization settings (refresh interval in real milliseconds)");

		double initialQuantization = Controller.getPlayer().getQuantization();

		Controller.getPlayer().addEventListener(this);

		setSliderValueFromQuantization(initialQuantization);
		this.setQuantizationText(initialQuantization);

	}

	private double getQuantizationFromSliderValue() {

		return MIN_Q
				+ (MAX_Q - MIN_Q)
				* (1f - Math.pow(
						1f - ((double) slider.getValue() / SLIDER_UNITS),
						EXPONENT));
	}

	private void setSliderValueFromQuantization(double q) {
		slider.setValue((int) Math
				.rint((1f - Math.pow(1f - (q - MIN_Q)
						/ (MAX_Q - MIN_Q), 1f / EXPONENT)) * SLIDER_UNITS));
	}

	private void setQuantizationText(double q) {
		label.setText((int) (q * 1000) + "ms");
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		Controller.getPlayer()
				.setQuantization(getQuantizationFromSliderValue());
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
		setSliderValueFromQuantization(quantization);
		this.setQuantizationText(quantization);
	}

	@Override
	public void speedChange(double speed) {
		// Nothing to do
	}

}
