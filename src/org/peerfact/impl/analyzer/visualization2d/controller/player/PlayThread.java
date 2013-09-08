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

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.EventTimeline;

/**
 * Thread of the Player class uses to control the playback.
 * 
 * Note: This is not the only thread that draws. The AWT-EventQueue draws most
 * of the time!.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class PlayThread extends Thread {

	boolean open = false;

	Player invokingPlayer;

	long lastFrameBegun = 0;

	long lastFrameDone = 0;

	/**
	 * Default constructor
	 * 
	 * @param p
	 */
	public PlayThread(Player p) {
		this.invokingPlayer = p;
	}

	@Override
	public void run() {
		this.setName("PlayerThread");
		EventTimeline tl = invokingPlayer.getTimeline();

		while (true) {
			invokingPlayer.setPlaying(true);

			while (tl.getActualTime() < tl.getMaxTime() && open) {
				this.doFrame(tl);
				try {

					long timeToSleep = (long) (invokingPlayer.getQuantization() * 1000d);

					if (timeToSleep > 0) {
						Thread.sleep(timeToSleep);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			invokingPlayer.setPlaying(false);

			if (!invokingPlayer.isLooping() || !open) {
				break;
			}
			Controller.resetView();
		}

		if (tl.getActualTime() >= tl.getMaxTime()) {
			invokingPlayer.notifyStopping();
		}

	}

	/**
	 * Draws a frame, or places it in the AWT-EventQueue.
	 * 
	 * @param tl
	 */
	public void doFrame(EventTimeline tl) {

		this.lastFrameBegun = System.currentTimeMillis();

		try {

			double speed = invokingPlayer.getSpeed();
			double q = invokingPlayer.getQuantization();

			long timeDiff = (long) (speed * q * Player.TIME_UNIT_MULTIPLICATOR);

			// log.debug("Time diff: " + speed + "*" + q + "*" +
			// Player.TIME_UNIT_MULTIPLICATOR + "=" + timeDiff);

			tl.jumpToTime(tl.getActualTime() + timeDiff);

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		this.lastFrameDone = System.currentTimeMillis();
	}

	/**
	 * Closes the thread when open=false.
	 * 
	 * @param open
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * Debug output
	 * 
	 * @param o
	 */
	public void dbg(Object o) {
		// System.err.println(this.getClass().getSimpleName() + ": " +
		// (o==null?"null":o.toString()));
	}

}
