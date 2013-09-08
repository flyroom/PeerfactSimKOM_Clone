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

package org.peerfact.impl.analyzer.visualization2d.controller.player;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Plays the recording as well as performing related operations.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class Player {

	private static Logger log = SimLogger.getLogger(Player.class);

	/**
	 * Maximum frame rate per ms (optimized processing power)
	 */
	public static final int FPS = 25;

	/**
	 * Number of time units which are one second.
	 */
	public static final long TIME_UNIT_MULTIPLICATOR = 1000000;

	/**
	 * Step in fast forward and rewind.
	 */
	public static final int FWD_STEP_SIZE = 50;

	ArrayList<PlayerEventListener> listeners = new ArrayList<PlayerEventListener>();

	EventTimeline timeline;

	PlayThread playthread = new PlayThread(this);

	boolean isPlaying;

	boolean loop = false;

	/**
	 * Speed ​​in milliseconds per frame
	 */
	double speed = Config.getValue("Player/Speed", 10000) / 10000d;

	/**
	 * Step in seconds when playing.
	 */
	protected double quantization = Config.getValue("Player/Quantization", 500) / 10000d;

	public EventTimeline getTimeline() {
		return timeline;
	}

	public void setTimeline(EventTimeline timeline) {
		this.timeline = timeline;
	}

	public static void setSpeed(float speed) {
		log.debug("Speed set to:" + speed);
	}

	public void play() {
		if (timeline.getMaxTime() > 0) {
			if (!isPlaying) {
				if (timeline.getActualTime() >= timeline.getMaxTime()) {
					Controller.resetView();
				}
				playthread = new PlayThread(this);
				playthread.setOpen(true);
				playthread.start();
			}
			this.notifyPlaying();
		}
	}

	public void pause() {
		playthread.setOpen(false);
		this.notifyPause();
	}

	public void stop() {
		playthread.setOpen(false);
		Controller.resetView();
		this.notifyStopping();
	}

	public void rev() {
		long jumptime = timeline.getActualTime() - FWD_STEP_SIZE
				* TIME_UNIT_MULTIPLICATOR;

		if (jumptime <= 0) {
			timeline.jumpToTime(0);
		} else {
			timeline.jumpToTime(jumptime);
		}

		this.notifyReverse();
	}

	public void fwd() {

		long jumptime = timeline.getActualTime() + FWD_STEP_SIZE
				* TIME_UNIT_MULTIPLICATOR;

		if (jumptime >= timeline.getMaxTime()) {
			timeline.jumpToTime(timeline.getMaxTime());
		} else {
			timeline.jumpToTime(jumptime);
		}

		this.notifyForward();
	}

	public void reset() {
		// Nothing to do
	}

	/**
	 * Returns the speed of the player in virtual seconds per real seconds
	 * 
	 * @param speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed of the player in virtual seconds per real seconds
	 * 
	 * @param speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
		this.notifySpeedChange(speed);
	}

	/**
	 * Returns the quantization (frequency of refresh rate) of the player in
	 * real seconds
	 * 
	 * @param speed
	 */
	public double getQuantization() {
		return quantization;
	}

	/**
	 * Sets the quantization (frequency of refresh rate) of the player in real
	 * seconds
	 * 
	 * @param speed
	 */
	public void setQuantization(double quantization) {
		this.quantization = quantization;
		this.notifyQuantizationChange(quantization);
	}

	public void showTime() {
		log.debug(timeline.getActualTime());
	}

	/**
	 * Returns whether the player is plays at the moment.
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	protected void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	public void addEventListener(PlayerEventListener l) {
		this.listeners.add(l);
	}

	protected void notifySpeedChange(double speed1) {
		for (PlayerEventListener l : listeners) {
			l.speedChange(speed1);
		}
	}

	protected void notifyPlaying() {
		for (PlayerEventListener l : listeners) {
			l.play();
		}
	}

	protected void notifyStopping() {
		for (PlayerEventListener l : listeners) {
			l.stop();
		}
	}

	protected void notifyPause() {
		for (PlayerEventListener l : listeners) {
			l.pause();
		}
	}

	protected void notifyForward() {
		for (PlayerEventListener l : listeners) {
			l.forward();
		}
	}

	protected void notifyReverse() {
		for (PlayerEventListener l : listeners) {
			l.reverse();
		}
	}

	private void notifyQuantizationChange(double q) {
		for (PlayerEventListener l : listeners) {
			l.quantizationChange(q);
		}
	}

	public void saveSettings() {
		Config.setValue("Player/Speed", (int) (speed * 10000d));
		Config.setValue("Player/Quantization", (int) (quantization * 10000d));

	}

	public boolean isLooping() {
		return loop;
	}

	public void setLooping(boolean loop) {
		this.loop = loop;
	}

}
