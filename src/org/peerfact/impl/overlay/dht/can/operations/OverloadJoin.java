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

package org.peerfact.impl.overlay.dht.can.operations;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.impl.overlay.dht.can.components.CanArea;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanVID;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;

/**
 * handles the overload join operation. That means it takes a list of peers and
 * gives them new areas, neighbours, VID and VID neighbours. Therefore it takes
 * the information from the master node.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class OverloadJoin {

	private final static Logger log = SimLogger.getLogger(CanNode.class);

	private List<CanOverlayContact> newMasters;

	private List<CanArea> newAreas;

	private CanNode master;

	/**
	 * start the overload join operation which takes the contacts and gives them
	 * new areas.
	 * 
	 * @param master
	 *            old peer which shiould be changed
	 * @param overloadContacts
	 *            new peers
	 */
	public OverloadJoin(CanNode master, List<CanOverlayContact> overloadContacts) {
		log.debug(Simulator.getSimulatedRealtime()
				+ " New OverloadJoin Operation.");

		newMasters = new LinkedList<CanOverlayContact>();
		newAreas = new LinkedList<CanArea>();
		this.master = master;

		// not forget master
		overloadContacts.add(master.getLocalOverlayContact());

		for (int i = 0; i < overloadContacts.size(); i++) {
			for (int j = 0; j < overloadContacts.size(); j++) {
				if (i != j
						&& overloadContacts
								.get(i)
								.getOverlayID()
								.toString()
								.equals(overloadContacts.get(j).getOverlayID()
										.toString())) {
					overloadContacts.remove(j);
				}
			}
		}

		CanArea area = master.getLocalOverlayContact().getArea();
		newAreas.add(area);

		for (@SuppressWarnings("unused")
		int i = 0; newAreas.size() < overloadContacts.size(); i++) { // divide
																		// area
			CanArea biggestArea = new CanArea(0, 0, 0, 0);
			int removeFromListInt = 0;

			for (int j = 0; j < newAreas.size(); j++) { // largest area is
														// selected
				long squareBiggest = ((long) biggestArea.getArea()[1] - (long) biggestArea
						.getArea()[0])
						* ((long) biggestArea.getArea()[3] - (long) biggestArea
								.getArea()[2]);
				long squareAcutalNewArea = ((long) newAreas.get(j).getArea()[1] - (long) newAreas
						.get(j).getArea()[0])
						* ((long) newAreas.get(j).getArea()[3] - (long) newAreas
								.get(j).getArea()[2]);
				if (squareBiggest < squareAcutalNewArea) {
					removeFromListInt = j;
					biggestArea = newAreas.get(j);
				}
				log.debug("BiggestArea: " + biggestArea.toString() + " VID: "
						+ biggestArea.getVid().toString()
						+ " new area " + newAreas.get(j).toString());
			}
			newAreas.remove(removeFromListInt);

			if ((biggestArea.getArea()[1] - biggestArea.getArea()[0]) >= (biggestArea
					.getArea()[3] - biggestArea.getArea()[2])) { // largest area
																	// is
																	// divided,
																	// x edge is
																	// greater

				CanArea addToListArea = new CanArea(biggestArea.getArea()[0],
						(biggestArea.getArea()[1] - biggestArea.getArea()[0])
								/ 2 + biggestArea.getArea()[0],
						biggestArea.getArea()[2], biggestArea.getArea()[3],
						biggestArea.getVid().getVIDList());
				biggestArea.setX0((biggestArea.getArea()[1] - biggestArea
						.getArea()[0]) / 2 + biggestArea.getArea()[0]);

				List<String> newVIDList = new LinkedList<String>();
				newVIDList = addToListArea.getVid().getVIDList();
				newVIDList.add("0");
				addToListArea.setVID(newVIDList);

				newVIDList = biggestArea.getVid().getVIDList();
				newVIDList.add("1");
				biggestArea.setVID(newVIDList);
				newAreas.add(addToListArea);
				newAreas.add(biggestArea);
			}
			else if ((biggestArea.getArea()[1] - biggestArea.getArea()[0]) < (biggestArea
					.getArea()[3] - biggestArea.getArea()[2])) { // largest area
																	// is
																	// divided,
																	// y edge is
																	// greater
				CanArea addToListArea = new CanArea(biggestArea.getArea()[0],
						biggestArea.getArea()[1],
						biggestArea.getArea()[2],
						(biggestArea.getArea()[3] - biggestArea.getArea()[2])
								/ 2 + biggestArea.getArea()[2],
						biggestArea.getVid().getVIDList());
				biggestArea.setY0((biggestArea.getArea()[3] - biggestArea
						.getArea()[2]) / 2 + biggestArea.getArea()[2]);

				List<String> newVIDList = new LinkedList<String>();
				newVIDList = addToListArea.getVid().getVIDList();
				newVIDList.add("0");
				addToListArea.setVID(newVIDList);

				newVIDList = biggestArea.getVid().getVIDList();
				newVIDList.add("1");
				biggestArea.setVID(newVIDList);
				newAreas.add(addToListArea);
				newAreas.add(biggestArea);
			}
		}

		for (int i = 0; i < overloadContacts.size(); i++) { // every contact
															// gets an area list
			CanArea areaToContact = newAreas.get(i);
			overloadContacts.get(i).setArea(areaToContact);
			log.debug("Area " + overloadContacts.get(i).getArea().toString()
					+ " ID "
					+ overloadContacts.get(i).getOverlayID().toString());
		}

		newMasters.addAll(overloadContacts);

	}

	/**
	 * gives the new contacts the new neighbours. takes the contacts and checks
	 * if they are neighbours with the given area.
	 * 
	 * @param area
	 *            area to check
	 * @param neighbours
	 *            list of contacts
	 * 
	 * @return new neighbours
	 */
	public static List<CanOverlayContact> getNeighboursToNewArea(int[] area,
			List<CanOverlayContact> neighbours) {
		List<CanOverlayContact> newNeighbours = new LinkedList<CanOverlayContact>();

		for (int i = 0; i < neighbours.size(); i++) {
			CanArea neighbourArea = neighbours.get(i).getArea();

			if (!neighbourArea.getArea().equals(area)) {
				if (neighbourArea.commonCorner(area)) {
					newNeighbours.add(neighbours.get(i));
				}
			}
		}
		return newNeighbours;
	}

	/**
	 * creats a new List with VID neighbours. Therefore it takes the old VID
	 * neighbours and put them together with the new contacts from above.
	 * 
	 * @param oldVidNeighboursContacts
	 * @return
	 */
	public List<CanVID[]> newVidNeighbours(
			CanOverlayContact[] oldVidNeighboursContacts) {
		CanVID[] oldVidNeighbours = {
				oldVidNeighboursContacts[0].getArea().getVid(),
				oldVidNeighboursContacts[1].getArea().getVid() };
		List<CanVID> allVid = new LinkedList<CanVID>();
		CanVID nullValue = new CanVID("0");
		CanVID oneValue = new CanVID("1");

		List<CanVID[]> allNewVidNeighbours = new LinkedList<CanVID[]>();

		for (int i = 0; i < newMasters.size(); i++) {
			allVid.add(newMasters.get(i).getArea().getVid());
		}

		// first element
		if (master.getLocalOverlayContact().getArea().getVid().toString()
				.equals(oldVidNeighbours[0].toString())) {
			log.debug("First Element in VID list");
			int numberSmallest = -1, numberBiggest = -1;
			CanVID smallest = nullValue, biggest = nullValue;

			for (int i = 0; i < allVid.size(); i++) {
				CanVID[] newVidNeighbours = new CanVID[3]; // three values: 1.
															// current VID, 2.
															// smaller neighbor,
															// 3. greater
															// neighbor

				newVidNeighbours[0] = (allVid.get(i));
				newVidNeighbours[1] = (nullValue);
				newVidNeighbours[2] = (nullValue);

				for (int j = 0; j < allVid.size(); j++) {
					if (allVid.get(i).lower(allVid.get(j))
							&& allVid.get(j).lower(newVidNeighbours[2])) {
						newVidNeighbours[2] = allVid.get(j);
					}
					else if (allVid.get(i).higher(allVid.get(j))
							&& allVid.get(j).higher(newVidNeighbours[1])) {
						newVidNeighbours[1] = allVid.get(j);
					}
					else if (allVid.get(i).lower(allVid.get(j))
							&& newVidNeighbours[2].toString().equals(
									nullValue.toString())) {
						newVidNeighbours[2] = allVid.get(j);
					}
					else if (allVid.get(i).higher(allVid.get(j))
							&& newVidNeighbours[1].toString().equals(
									nullValue.toString())) {
						newVidNeighbours[1] = allVid.get(j);
					}

				}
				if (newVidNeighbours[0].lower(smallest) || numberSmallest == -1) {
					numberSmallest = i;
					smallest = newVidNeighbours[0];
				}
				if (newVidNeighbours[0].higher(biggest) || numberBiggest == -1) {
					numberBiggest = i;
					biggest = newVidNeighbours[0];
				}

				allNewVidNeighbours.add(newVidNeighbours);

			}
			allNewVidNeighbours.get(numberSmallest)[1] = allVid
					.get(numberBiggest);
			allNewVidNeighbours.get(numberBiggest)[2] = allVid
					.get(numberSmallest);
		}

		// already elements in the list
		else {
			log.debug("Not the first Element in VID list");
			allVid.add(oldVidNeighbours[0]);
			if (!oldVidNeighbours[1].toString().equals(
					oldVidNeighbours[0].toString())) {
				allVid.add(oldVidNeighbours[1]);
			}

			CanVID smallest = nullValue;
			CanVID biggest = nullValue;
			int smallestInt = 0, biggestInt = 0;

			for (int i = 0; i < allVid.size(); i++) {
				if (allVid.get(i).lower(smallest) || smallest.equals(nullValue)) {
					smallest = allVid.get(i);
					smallestInt = i;
				}

				if (allVid.get(i).higher(biggest) || biggest.equals(nullValue)) {
					biggest = allVid.get(i);
					biggestInt = i;
				}
				CanVID[] newVidNeighbours = new CanVID[3]; // three values: 1.
															// current VID, 2.
															// smaller neighbor,
															// 3. greater
															// neighbor

				newVidNeighbours[0] = (allVid.get(i));
				newVidNeighbours[1] = oneValue;
				newVidNeighbours[2] = (nullValue);
				for (int j = 0; j < allVid.size(); j++) {
					if (i != j) {
						if (allVid.get(i).lower(allVid.get(j))
								&& allVid.get(j).lower(newVidNeighbours[2])) {
							newVidNeighbours[2] = allVid.get(j);
						}
						else if (allVid.get(i).higher(allVid.get(j))
								&& allVid.get(j).higher(newVidNeighbours[1])) {
							newVidNeighbours[1] = allVid.get(j);
						}
						else if (allVid.get(i).lower(allVid.get(j))
								&& newVidNeighbours[2].toString().equals(
										nullValue.toString())) {
							newVidNeighbours[2] = allVid.get(j);
						}
						else if (allVid.get(i).higher(allVid.get(j))
								&& newVidNeighbours[1].toString().equals(
										oneValue.toString())) {
							newVidNeighbours[1] = allVid.get(j);
						}
					}
				}

				allNewVidNeighbours.add(newVidNeighbours);
			}

			allNewVidNeighbours.get(smallestInt)[1] = biggest;
			allNewVidNeighbours.get(biggestInt)[2] = smallest;
		}

		return allNewVidNeighbours;
	}

	public List<CanOverlayContact> getNewMasters() {
		return newMasters;
	}

}
