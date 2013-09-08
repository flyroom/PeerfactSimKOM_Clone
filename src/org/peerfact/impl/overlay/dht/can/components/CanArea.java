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

package org.peerfact.impl.overlay.dht.can.components;

import java.io.Serializable;
import java.util.List;

import org.peerfact.Constants;
import org.peerfact.api.common.Transmitable;

/**
 * The class for the areas of every peer. The area is saved as short array[4],
 * this class gives some classes to work with the area. In the CanArea is as
 * well the CanVID address saved.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanArea implements Serializable, Transmitable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8390059062144742164L;

	private short[] area;

	private CanVID vid;

	/**
	 * A empty CanArea is created.
	 */
	public CanArea() {
		area = new short[4];
		vid = new CanVID();
	}

	/**
	 * A CanArea is created with the array of the parameters
	 * 
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 */
	public CanArea(int x0, int x1, int y0, int y1) {
		this.area = new short[4];
		this.area[0] = (short) x0;
		this.area[1] = (short) x1;
		this.area[2] = (short) y0;
		this.area[3] = (short) y1;
		vid = new CanVID();

	}

	/**
	 * A CanArea is created with the array of the parameters and the VID address
	 * is added to the CanArea
	 * 
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 * @param vid
	 */
	public CanArea(int x0, int x1, int y0, int y1, List<String> vid) {
		this.area = new short[4];
		this.area[0] = (short) x0;
		this.area[1] = (short) x1;
		this.area[2] = (short) y0;
		this.area[3] = (short) y1;
		this.vid = new CanVID(vid);
	}

	/**
	 * A CanArea is created with the array of the parameters and the VID address
	 * is added to the CanArea
	 * 
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 * @param vid
	 */
	public CanArea(int[] area, List<String> vid) {
		this.area = new short[4];
		this.area[0] = (short) area[0];
		this.area[1] = (short) area[1];
		this.area[2] = (short) area[2];
		this.area[3] = (short) area[3];
		this.vid = new CanVID(vid);

	}

	public void setArea(int[] area) {
		this.area = new short[4];
		this.area[0] = (short) area[0];
		this.area[1] = (short) area[1];
		this.area[2] = (short) area[2];
		this.area[3] = (short) area[3];
	}

	public int[] getArea() {
		int[] areaOut = { area[0], area[1], area[2], area[3] };
		return areaOut;
	}

	public void setX0(int x0) {
		area[0] = (short) x0;
	}

	public void setX1(int x1) {
		area[1] = (short) x1;
	}

	public void setY0(int y0) {
		area[2] = (short) y0;
	}

	public void setY1(int y1) {
		area[3] = (short) y1;
	}

	public void setVid(CanVID vid) {
		this.vid = vid;
	}

	public CanVID getVid() {
		return vid;
	}

	/**
	 * The VID address is extended.
	 * 
	 * @param VID
	 *            String 0 or 1 are added
	 */
	public void addToVid(String VID) {
		this.vid.addToVID(VID);
	}

	public void setVID(List<String> VID) {
		this.vid.setVIDList(VID);
	}

	/**
	 * checks if either the x0 or the x1 value of the actual area is located
	 * between the x values of the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean betweenX(CanArea betweenArea) {
		if ((betweenArea.getArea()[0] >= area[0] && betweenArea.getArea()[0] < area[1])
				|| (betweenArea.getArea()[1] > area[0] && betweenArea.getArea()[1] <= area[1])) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if either the y0 or the y1 value of the actual area is located
	 * between the y values of the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean betweenY(CanArea betweenArea) {
		if ((betweenArea.getArea()[2] >= area[2] && betweenArea.getArea()[2] < area[3])
				|| (betweenArea.getArea()[3] > area[2] && betweenArea.getArea()[3] <= area[3])) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if both x values of the actual area is located between the x
	 * values of the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean betweenXboth(short[] betweenArea) {
		if ((betweenArea[0] >= area[0] && betweenArea[0] < area[1])
				|| (betweenArea[1] > area[0] && betweenArea[1] <= area[1])
				|| (betweenArea[0] <= area[0] && betweenArea[0] > area[1])
				|| (betweenArea[1] < area[0] && betweenArea[1] >= area[1])
				|| (betweenArea[0] <= area[0] && betweenArea[1] >= area[1])
				|| (betweenArea[0] >= area[0] && betweenArea[1] <= area[1])) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if both y values of the actual area is located between the y
	 * values of the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean betweenYboth(short[] betweenArea) {
		if ((betweenArea[2] >= area[2] && betweenArea[2] < area[3])
				|| (betweenArea[3] > area[2] && betweenArea[3] <= area[3])
				|| (betweenArea[2] <= area[2] && betweenArea[2] > area[3])
				|| (betweenArea[3] < area[2] && betweenArea[3] >= area[3])
				|| (betweenArea[2] <= area[2] && betweenArea[3] >= area[3])
				|| (betweenArea[2] >= area[2] && betweenArea[3] <= area[3])) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the actual area has a common X0 corner with the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean atX0Corner(CanArea betweenArea) {
		if (betweenArea.getArea()[1] == area[0]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the actual area has a common X1 corner with the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean atX1Corner(CanArea betweenArea) {
		if (betweenArea.getArea()[0] == area[1]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the actual area has a common Y0 corner with the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean atY0Corner(CanArea betweenArea) {
		if (betweenArea.getArea()[3] == area[2]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the actual area has a common Y0 corner with the betweenArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean atY1Corner(CanArea betweenArea) {
		if (betweenArea.getArea()[3] == area[2]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the actual area has at least on common corner with the testArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean commonCorner(CanArea testArea) {
		if ((testArea.getArea()[1] == area[0] && (testArea.betweenYboth(area)))
				|| (testArea.getArea()[0] == area[1] && (testArea
						.betweenYboth(area)))
				|| (testArea.getArea()[3] == area[2] && (testArea
						.betweenXboth(area)))
				|| (testArea.getArea()[2] == area[3] && (testArea
						.betweenXboth(area)))) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * checks if the actual area has at least on common corner with the testArea
	 * 
	 * @param betweenArea
	 * @return
	 */
	public boolean commonCorner(int[] testArea) {
		if ((testArea[1] == area[0] && (betweenYboth(areaToShort(testArea))))
				|| (testArea[0] == area[1] && (betweenYboth(areaToShort((testArea)))))
				|| (testArea[3] == area[2] && (betweenXboth(areaToShort((testArea)))))
				|| (testArea[2] == area[3] && (betweenXboth(areaToShort((testArea)))))) {
			return true;
		} else {
			return false;
		}
	}

	public static short[] areaToShort(int[] area) {
		short[] areaOut = { (short) area[0], (short) area[1], (short) area[2],
				(short) area[3] };
		return areaOut;
	}

	@Override
	public String toString() {
		return (String.valueOf(area[0]) + " " + String.valueOf(area[1])
				+ " " + String.valueOf(area[2]) + " " + String.valueOf(area[3]));
	}

	@Override
	public long getTransmissionSize() {
		return 4 * Constants.SHORT_SIZE + vid.getTransmissionSize();
	}

}
