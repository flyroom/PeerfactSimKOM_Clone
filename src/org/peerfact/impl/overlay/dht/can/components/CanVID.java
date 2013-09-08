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
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.common.Transmitable;

/**
 * Creates the VID for a CanOverlayContact.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class CanVID implements Serializable, Transmitable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -170216833261575543L;

	private List<String> VID;

	/**
	 * creates an empty VID
	 */
	public CanVID() {
		this.VID = new LinkedList<String>();
	}

	/**
	 * Creates a VID and adds the value as last value.
	 * 
	 * @param value
	 *            one String value, is used as last value
	 */
	public CanVID(String value) {
		this.VID = new LinkedList<String>();
		this.VID.add(value);
	}

	/**
	 * Creates a VID and adds the whole list.
	 * 
	 * @param vid
	 *            List is set as VID
	 */
	public CanVID(List<String> vid) {
		this.VID = new LinkedList<String>();
		this.VID.addAll(vid);
	}

	public List<String> getVIDList() {
		return this.VID;
	}

	public void setVIDList(List<String> VID) {
		this.VID = VID;
	}

	/**
	 * adds the parameter as last value.
	 * 
	 * @param newVID
	 *            String as last value
	 * 
	 */
	public void addToVID(String newVID) {
		String addTo = null;
		addTo = String.valueOf(newVID);
		this.VID.add(addTo);
	}

	public void removeFromList(int removeIndex) {
		this.VID.remove(removeIndex);
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < VID.size(); i++) {
			output.append(VID.get(i));
		}
		return output.toString();
	}

	public String singleToString() {
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < VID.size(); i++) {
			output.append(" " + VID.get(i));
		}
		return output.toString();
	}

	/**
	 * Compares two CanVID and if the actual values is higher as the parameter
	 * the return value is true.
	 * 
	 * @param compare
	 *            CanVID to compare
	 * @return true if actual value is higher
	 */
	public boolean higher(CanVID compare) {
		String out = VID.get(0).toString();
		String out2 = compare.getVIDList().get(0).toString();
		for (int i = 1; i < VID.size(); i++) {
			out = out + VID.get(i);
		}
		for (int i = 1; i < compare.getVIDList().size(); i++) {
			out2 = out2 + compare.getVIDList().get(i);
		}

		while (out2.length() > out.length()) {
			out = out + "0";
		}
		while (out.length() > out2.length()) {
			out2 = out2 + "0";
		}

		if (out.compareTo(out2) == 1) { // VID is smaller
			return true;
		}
		else {
			return false; // VID is greater or equal
		}
	}

	/**
	 * Compares two CanVID and if the actual values is lower as the parameter
	 * the return value is true.
	 * 
	 * @param compare
	 *            CanVID to compare
	 * @return true if actual value is lower
	 */
	public boolean lower(CanVID compare) {
		String out = VID.get(0).toString();
		String out2 = compare.getVIDList().get(0).toString();
		for (int i = 1; i < VID.size(); i++) {
			out = out + VID.get(i);
		}
		for (int i = 1; i < compare.getVIDList().size(); i++) {
			out2 = out2 + compare.getVIDList().get(i);
		}

		while (out2.length() > out.length()) {
			out = out + "0";
		}
		while (out.length() > out2.length()) {
			out2 = out2 + "0";
		}

		if (out.compareTo(out2) == -1) { // VID is smaller
			return true;
		}
		else {
			return false; // VID is greater or equal
		}
	}

	/**
	 * Compares three CanVID and if the actual values is in between of the
	 * parameter the return value is true.
	 * 
	 * @param compare
	 *            CanVID to compare
	 * @return true if actual value is in between
	 */
	public boolean inBetween(CanVID lower, CanVID higher) {
		if (higher(lower) && lower(higher)) {
			return true;
		}
		return false;
	}

	/**
	 * Compares two CanVID and gives the number of common values back. So it
	 * gives the number of common parents
	 * 
	 * @param compare
	 *            CanVID to compare
	 * @return number of common parents
	 */
	public int numberCommon(CanVID compare) {
		int number = 0;

		for (int i = 0; i < Math.min(VID.size(), compare.getVIDList().size()); i++) {
			if (!VID.get(i).toString()
					.equals(compare.getVIDList().get(i).toString())) {
				break;
			}
			number++;
		}
		return number;
	}

	/**
	 * Compares two CanVID and if all value until the n-1 number are common the
	 * return value is true. So it is true, if all parents are common
	 * 
	 * @param compare
	 *            CanVID to compare
	 * @return true if actual value and compare have the same parents
	 */
	public boolean closestNeighbour(CanVID compare) {
		if (VID.size() != compare.getVIDList().size()) {
			return false;
		}

		for (int i = 0; i < VID.size() - 1; i++) {
			if (!VID.get(i).toString()
					.equals(compare.getVIDList().get(i).toString())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * gives a List of all common values.
	 * 
	 * @param compare
	 * @return
	 */
	public List<String> listCommon(CanVID compare) {
		int number = 0;

		for (int i = 0; i < Math.min(VID.size(), compare.getVIDList().size()); i++) {
			if (VID.get(i).toString()
					.equals(compare.getVIDList().get(i).toString())) {
				number++;
			}
		}

		List<String> listCommon = new LinkedList<String>();
		for (int i = 0; i < number; i++) {
			listCommon.add(VID.get(i));
		}
		return listCommon;
	}

	public boolean allZero() {
		for (int i = 0; i < VID.size(); i++) {
			if (VID.get(i).toString().equals("1")) {
				return false;
			}
		}
		return true;
	}

	public boolean allOne() {
		for (int i = 1; i < VID.size(); i++) {
			if (VID.get(i).toString().equals("0")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public long getTransmissionSize() {
		return 10;
	}

}
