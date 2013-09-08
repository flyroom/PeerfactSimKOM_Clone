/*
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

import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.overlay.dht.can.components.CanArea;
import org.peerfact.impl.overlay.dht.can.components.CanConfig;
import org.peerfact.impl.overlay.dht.can.components.CanNode;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayContact;
import org.peerfact.impl.overlay.dht.can.components.CanOverlayKey;
import org.peerfact.impl.overlay.dht.can.components.CanVID;
import org.peerfact.impl.overlay.dht.can.messages.LeaveLeftMsg;
import org.peerfact.impl.overlay.dht.can.messages.NewNeighbourMsg;
import org.peerfact.impl.overlay.dht.can.messages.NewVIDNeighbourMsg;
import org.peerfact.impl.overlay.dht.can.messages.StartTakeoverMsg;
import org.peerfact.impl.simengine.Simulator;

/**
 * This operation starts the rebuild of a section if one peer left the CAN. It
 * takes all the information from the peers with common parents and use the
 * overloadJoin class to give each of this peer a new area, neighbours, VID and
 * VID neighbours.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class TakeoverRebuildOperation extends
		AbstractOperation<CanNode, Object> {

	private CanOverlayContact missingNode;

	private boolean done;

	private CanNode master = getComponent();

	private List<CanOverlayContact> newContacts = new LinkedList<CanOverlayContact>();

	/**
	 * starts the TakeoverRebuildOperation after a peer left
	 * 
	 * @param component
	 *            closest neighbour of the missing node start this operation
	 */
	public TakeoverRebuildOperation(CanNode component) {
		super(component);
		done = false;
	}

	@Override
	public void execute() {
		if (done == false) {
			missingNode = master.getMissingNode();
			log.debug(Simulator.getSimulatedRealtime()
					+ " TakeoverRebuildOperation started, left node: "
					+ missingNode.getOverlayID().toString()
					+ " in node "
					+ master.getLocalOverlayContact().getOverlayID().toString());
			master.stopTakeoverOperation();

			List<CanOverlayContact> neighboursOfMissing = master
					.getNeighboursOfCertainNeighbour(missingNode);
			CanOverlayContact[] vidNeighbourOfMissing = master
					.getVidNeighboursOfCertainNeighbour(missingNode);

			for (int x = 0; x < master.getVidNeighboursOfNeighbours().size(); x++) {
				log.debug("vidNeighbours of Neighbours "
						+ master.getVidNeighboursOfNeighbours().get(x)[0]
								.getOverlayID().toString()
						+ " "
						+ master.getVidNeighboursOfNeighbours().get(x)[0]
								.getArea().getVid().toString());
				log.debug(" "
						+ master.getVidNeighboursOfNeighbours().get(x)[1]
								.getArea().getVid().toString()
						+ " "
						+ master.getVidNeighboursOfNeighbours().get(x)[2]
								.getArea().getVid().toString());
			}

			// if just direct neighbour is missing
			if (missingNode
					.getArea()
					.getVid()
					.closestNeighbour(
							master.getLocalOverlayContact().getArea().getVid())) {
				log.debug("direct Vid neighbour is missing");

				List<CanOverlayContact> newNeighbours = new LinkedList<CanOverlayContact>();
				newNeighbours.addAll(master.getNeighbours());
				newNeighbours.addAll(neighboursOfMissing);
				List<CanOverlayContact> oldNeighbours = new LinkedList<CanOverlayContact>();
				oldNeighbours.add(missingNode);
				oldNeighbours.add(master.getLocalOverlayContact().clone());
				List<Object[]> newHashs = new LinkedList<Object[]>();

				if (master.getStoredHashs() != null) {
					newHashs.addAll(master.getStoredHashs());
				}
				if (master.getLeavingHash() != null) {
					newHashs.addAll(master.getLeavingHash());
				}

				if (newHashs != null) {
					for (int i = 0; i < newHashs.size(); i++) {
						if (((CanOverlayContact) newHashs.get(i)[1]) != null) {
							if (((CanOverlayContact) newHashs.get(i)[1])
									.getOverlayID()
									.toString()
									.equals(missingNode.getOverlayID()
											.toString())) {
								newHashs.remove(i);
							}
						}
					}
				}

				for (int x = 0; x < newNeighbours.size(); x++) {
					log.debug("new Neighbours: "
							+ newNeighbours.get(x).getOverlayID().toString());
				}

				// delete doubleNeighbours, own node and missing node
				for (int i = 0; i < newNeighbours.size(); i++) {
					if (newNeighbours.get(i).getOverlayID().toString()
							.equals(missingNode.getOverlayID().toString())
							|| newNeighbours
									.get(i)
									.getOverlayID()
									.toString()
									.equals(master.getLocalOverlayContact()
											.getOverlayID().toString())) {
						newNeighbours.remove(i);
						i = -1;
						break;
					}

					for (int j = 0; j < newNeighbours.size(); j++) {
						if (i != j
								&& (newNeighbours.get(i).getOverlayID()
										.toString().equals(newNeighbours.get(j)
										.getOverlayID().toString()))) {
							newNeighbours.remove(j);
						}
					}
				}

				// delete hashs of the left node
				for (int j = 0; j < newHashs.size(); j++) {
					if (((CanOverlayContact) newHashs.get(j)[1]) != null) {
						if (((CanOverlayContact) newHashs.get(j)[1])
								.getOverlayID().toString()
								.equals(missingNode.getOverlayID().toString())) {
							newHashs.remove(j);
						}
					}
				}

				CanVID oldVid = master.getLocalOverlayContact().getArea()
						.getVid();
				CanOverlayContact[] vidNeighboursToSave = { null, null };
				if (oldVid.toString().equals(
						master.getVIDNeighbours()[0].getArea().getVid()
								.toString())
						|| oldVid.toString().equals(
								vidNeighbourOfMissing[1].getArea().getVid()
										.toString())) {
					vidNeighboursToSave[0] = vidNeighbourOfMissing[0];
					vidNeighboursToSave[1] = master.getVIDNeighbours()[1];
				}
				else {
					vidNeighboursToSave[0] = master.getVIDNeighbours()[0];
					vidNeighboursToSave[1] = vidNeighbourOfMissing[1];

				}

				List<CanArea> oldAreas = new LinkedList<CanArea>();
				oldAreas.add(missingNode.getArea());
				oldAreas.add(master.getLocalOverlayContact().getArea());

				for (int x = 0; x < newNeighbours.size(); x++) {
					log.debug("new Neighbours: "
							+ newNeighbours.get(x).getOverlayID().toString());
				}

				master.setArea(getNewArea(oldAreas));
				CanVID toSave = new CanVID(master.getLocalOverlayContact()
						.getArea()
						.getVid().listCommon(missingNode.getArea().getVid()));
				master.getLocalOverlayContact().getArea().setVid(toSave);
				master.setNeighbours(newNeighbours);

				master.removeVidNeighboursOfNeighbours(newNeighbours);
				master.removeNeighboursOfNeighbours(newNeighbours);

				master.setVIDNeigbours(vidNeighboursToSave);
				master.setStoredHashs(newHashs);
				master.setMissingNode(null);

				List<CanOverlayContact> sendNewNeighbours = new LinkedList<CanOverlayContact>();
				sendNewNeighbours.add(master.getLocalOverlayContact().clone());
				// send msg to all neighbours
				for (int i = 0; i < newNeighbours.size(); i++) {
					CanOverlayContact receiver = newNeighbours.get(i);
					NewNeighbourMsg newNeighbourMsg = new NewNeighbourMsg(
							master.getLocalOverlayContact().getOverlayID(),
							receiver.getOverlayID(),
							master.getLocalOverlayContact().clone(),
							oldNeighbours,
							sendNewNeighbours);
					master.getTransLayer().send(newNeighbourMsg,
							receiver.getTransInfo(), master.getPort(),
							TransProtocol.TCP);
				}

				sendNewVIDNeighbours(master.getLocalOverlayContact(),
						master.getVIDNeighbours()[0],
						master.getVIDNeighbours()[1]);

				// master.resumeTakeoverOperation();
				master.getBootstrap().unregisterNode(missingNode);
				master.emptyLeavingNeighbours();
				master.emptyLeavingReplyContacts();
				master.emptyLeavingReplyVIDNeighbours();
				master.emptyLeavingArea();
				master.resumeTakeoverOperation();
			}
			else {
				List<CanArea> area = master.getLeavingArea();
				area.add(missingNode.getArea());
				List<CanOverlayContact> replyContacts = master
						.getLeavingReplyContacts();

				CanArea newArea = new CanArea();
				area.add(master.getLocalOverlayContact().getArea());
				List<CanOverlayContact> neighbours = master
						.getLeavingNeighbours();

				List<Object[]> newHashs = new LinkedList<Object[]>();

				if (master.getStoredHashs() != null) {
					newHashs.addAll(master.getStoredHashs());
				}
				if (master.getLeavingHash() != null) {
					newHashs.addAll(master.getLeavingHash());
				}

				if (newHashs != null) {
					for (int i = 0; i < newHashs.size(); i++) {
						if (((CanOverlayContact) newHashs.get(i)[1]) != null) {
							if (((CanOverlayContact) newHashs.get(i)[1])
									.getOverlayID()
									.toString()
									.equals(missingNode.getOverlayID()
											.toString())) {
								newHashs.remove(i);
							}
						}
					}
				}

				List<CanOverlayContact[]> leavingReplyVIDNeighbours = master
						.getLeavingReplyVIDNeighbours();

				List<CanOverlayContact> findVID = new LinkedList<CanOverlayContact>();

				List<CanOverlayContact> findBiggestSmallestVID = new LinkedList<CanOverlayContact>();
				findBiggestSmallestVID.add(master.getLocalOverlayContact()
						.clone());

				for (int i = 0; i < neighbours.size(); i++) {
					if (neighbours
							.get(i)
							.getOverlayID()
							.toString()
							.equals(master.getLocalOverlayContact()
									.getOverlayID()
									.toString())
							|| neighbours
									.get(i)
									.getOverlayID()
									.toString()
									.equals(missingNode.getOverlayID()
											.toString())) {
						neighbours.remove(i);
					}
				}

				for (int i = 0; i < neighbours.size(); i++) {
					for (int j = 0; j < neighbours.size(); j++) {
						if (i != j
								&& neighbours
										.get(i)
										.getOverlayID()
										.toString()
										.equals(neighbours.get(j)
												.getOverlayID().toString())) {
							neighbours.remove(j);
							i = -1;
							break;
						}
					}
				}

				log.debug(Simulator.getSimulatedRealtime()
						+ " takingOver node: "
						+ missingNode.getOverlayID().toString()
						+ " "
						+ missingNode.getArea().toString()
						+ " own id "
						+ master.getLocalOverlayContact().getOverlayID()
								.toString()
						+ " areaList:");

				newArea = getNewArea(area);

				log.debug("newArea " + newArea.toString());

				for (int x = 0; x < replyContacts.size(); x++) {
					log.debug((replyContacts.get(x)).getOverlayID().toString());
				}

				for (int x = 0; x < neighbours.size(); x++) {
					log.debug((neighbours.get(x)).getOverlayID().toString());
				}

				neighbours.addAll(master.getNeighbours());
				neighbours.addAll(master
						.getNeighboursOfCertainNeighbour(missingNode));

				for (int i = 0; i < neighbours.size(); i++) {
					if (neighbours
							.get(i)
							.getOverlayID()
							.toString()
							.equals(master.getLocalOverlayContact()
									.getOverlayID()
									.toString())
							|| neighbours
									.get(i)
									.getOverlayID()
									.toString()
									.equals(missingNode.getOverlayID()
											.toString())) {
						neighbours.remove(i);
					}

					for (int j = 0; j < replyContacts.size(); j++) {
						if (neighbours
								.get(i)
								.getOverlayID()
								.toString()
								.equals(replyContacts.get(j).getOverlayID()
										.toString())) {
							neighbours.remove(i);
							i = -1;
							break;
						}
					}
				}

				List<String> vidToSave = master.getLocalOverlayContact()
						.getArea()
						.getVid().listCommon(missingNode.getArea().getVid());

				master.setNeighbours(neighbours);
				master.setArea(newArea);

				master.getLocalOverlayContact().getArea().setVID(vidToSave);

				log.debug("newVID "
						+ master.getLocalOverlayContact().getArea().getVid()
								.toString());

				log.debug("NewNeighbours: ");
				for (int x = 0; x < master.getNeighbours().size(); x++) {
					log.debug(master.getNeighbours().get(x).getOverlayID()
							.toString());
				}

				leavingReplyVIDNeighbours.add(master.getVIDNeighbours());
				leavingReplyVIDNeighbours.add(master
						.getVidNeighboursOfCertainNeighbour(missingNode));

				for (int x = 0; x < leavingReplyVIDNeighbours.size(); x++) {
					log.debug("receveived vid: "
							+ leavingReplyVIDNeighbours.get(x)[0].getArea()
									.getVid().toString()
							+ " "
							+ leavingReplyVIDNeighbours.get(x)[1].getArea()
									.getVid().toString());
				}

				CanOverlayContact smallest = null;
				CanOverlayContact biggest = null;
				// Set new VIDNEighbours depending on the VID Neighbours of the
				// reply Contacts, the leaving and the own vid-neighbours
				CanOverlayContact[] vidNeighboursToSave = new CanOverlayContact[2];
				findBiggestSmallestVID.addAll(replyContacts);
				findBiggestSmallestVID.add(missingNode);
				for (int i = 0; i < findBiggestSmallestVID.size(); i++) {
					if (smallest == null) {
						smallest = findBiggestSmallestVID.get(i);
					} else if (findBiggestSmallestVID.get(i).getArea().getVid()
							.lower(smallest.getArea().getVid())) {
						smallest = findBiggestSmallestVID.get(i);
					}
					if (biggest == null) {
						biggest = findBiggestSmallestVID.get(i);
					} else if (findBiggestSmallestVID.get(i).getArea().getVid()
							.higher(biggest.getArea().getVid())) {
						biggest = findBiggestSmallestVID.get(i);
					}
				}
				vidNeighboursToSave[0] = master
						.getVidNeighboursOfCertainNeighbour(smallest)[0];
				vidNeighboursToSave[1] = master
						.getVidNeighboursOfCertainNeighbour(biggest)[1];

				if (master.getLocalOverlayContact().getArea().getVid()
						.toString()
						.equals("0")) {
					vidNeighboursToSave[0] = master.getLocalOverlayContact();
					vidNeighboursToSave[1] = master.getLocalOverlayContact();

				}

				master.setVIDNeigbours(vidNeighboursToSave);
				findVID.add(vidNeighboursToSave[0]);
				findVID.add(vidNeighboursToSave[1]);

				// create new object
				OverloadJoin overloadJoin = new OverloadJoin(master,
						replyContacts);
				newContacts = overloadJoin.getNewMasters();

				for (int x = 0; x < newContacts.size(); x++) {
					log.debug("newContacts: " + newContacts.get(x));
				}

				// create new neighbor list
				List<CanVID[]> allNewVidNeighbours = new LinkedList<CanVID[]>();
				allNewVidNeighbours.addAll(overloadJoin.newVidNeighbours(master
						.getVIDNeighbours()));

				for (int i = 0; i < newContacts.size(); i++) {
					List<CanOverlayContact> newNeighbour = new LinkedList<CanOverlayContact>();
					newNeighbour.addAll(master.getNeighbours());
					newNeighbour.addAll(neighboursOfMissing);

					for (int j = 0; j < master.getNeighboursOfNeighbours()
							.size(); j++) {
						newNeighbour.addAll(master.getNeighboursOfNeighbours()
								.get(j));
					}
					for (int j = 0; j < newNeighbour.size(); j++) {
						for (int h = 0; h < newContacts.size(); h++) {
							if (newNeighbour
									.get(j)
									.getOverlayID()
									.toString()
									.equals(newContacts.get(h).getOverlayID()
											.toString())) {
								newNeighbour.remove(j);
								break;
							}
						}
					}

					newNeighbour = OverloadJoin.getNeighboursToNewArea(
							newContacts.get(i).getArea().getArea(),
							newNeighbour);
					for (int j = 0; j < newContacts.size(); j++) {
						boolean allreadySaved = false;
						int savedPosition = 0;
						for (int h = 0; h < newNeighbour.size(); h++) {
							if (newContacts
									.get(j)
									.getOverlayID()
									.toString()
									.equals(newNeighbour.get(h).getOverlayID()
											.toString())) {
								allreadySaved = true;
								savedPosition = h;
								break;
							}
						}
						if (allreadySaved == true) {
							newNeighbour.remove(savedPosition);
							newNeighbour.add(newContacts.get(j));
						} else {
							newNeighbour.add(newContacts.get(j));
						}

					}
					for (int j = 0; j < newNeighbour.size(); j++) {
						if (newNeighbour
								.get(j)
								.getOverlayID()
								.toString()
								.equals(newContacts.get(i).getOverlayID()
										.toString())
								|| newNeighbour
										.get(j)
										.getOverlayID()
										.toString()
										.equals(missingNode.getOverlayID()
												.toString())) {
							newNeighbour.remove(j);
							j = 0;
						}
						for (int h = 0; h < newNeighbour.size(); h++) {
							if (j != h
									&& newNeighbour
											.get(j)
											.getOverlayID()
											.toString()
											.equals(newNeighbour.get(h)
													.getOverlayID().toString())) {
								newNeighbour.remove(j);
								j = -1;
								break;
							}
						}

					}

					// divide vid neighbors
					CanOverlayContact[] sendVidNeighbours = new CanOverlayContact[2];
					CanVID[] newVidNeighbours = new CanVID[3];
					newVidNeighbours = allNewVidNeighbours.get(i);

					findVID.addAll(newContacts);

					for (int j = 0; j < findVID.size(); j++) {
						if (findVID.get(j).getArea().getVid().toString()
								.equals(newVidNeighbours[0].toString())) {
							// find contacts for vid neighbors
							for (int h = 0; h < findVID.size(); h++) {
								if (findVID.get(h).getArea().getVid()
										.toString()
										.equals(newVidNeighbours[1].toString())) {
									sendVidNeighbours[0] = findVID.get(h);
								}

								if (findVID.get(h).getArea().getVid()
										.toString()
										.equals(newVidNeighbours[2].toString())) {
									sendVidNeighbours[1] = findVID.get(h);
								}
							}
						}
					}

					for (int x = 0; x < newNeighbour.size(); x++) {
						log.debug("new Neighbour "
								+ (newNeighbour.get(x)).getOverlayID()
										.toString());
					}

					if (!newContacts
							.get(i)
							.getOverlayID()
							.toString()
							.equals(
									master.getLocalOverlayContact()
											.getOverlayID().toString())) {
						CanArea areaToSend = new CanArea(newContacts.get(i)
								.getArea().getArea(),
								newContacts.get(i).getArea().getVid()
										.getVIDList());
						CanOverlayContact contactToSend = new CanOverlayContact(
								newContacts.get(i).getOverlayID(),
								newContacts.get(i).getTransInfo(), areaToSend,
								true);

						List<Object[]> hashToSend = new LinkedList<Object[]>();
						if (master.getStoredHashs() != null) {
							newHashs.addAll(master.getStoredHashs());
						}
						if (master.getLeavingHash() != null) {
							newHashs.addAll(master.getLeavingHash());
						}
						for (int j = 0; j < newHashs.size(); j++) {
							if (((CanOverlayKey) newHashs.get(j)[0])
									.includedInArea(areaToSend)) {
								hashToSend.add(newHashs.get(j));
							}
						}

						LeaveLeftMsg leaveLeft = new LeaveLeftMsg(master
								.getLocalOverlayContact().getOverlayID(),
								newContacts.get(i).getOverlayID(),
								contactToSend,
								newNeighbour, sendVidNeighbours,
								newContacts.get(i).getArea().getVid(),
								hashToSend);
						master.getTransLayer().send(leaveLeft,
								newContacts.get(i).getTransInfo(),
								master.getPort(), TransProtocol.TCP);

						sendNewVIDNeighbours(newContacts.get(i),
								sendVidNeighbours[0], sendVidNeighbours[1]);

					}
					else {
						CanArea areaToSend = new CanArea(newContacts.get(i)
								.getArea().getArea(), newContacts.get(i)
								.getArea().getVid().getVIDList());
						master.setArea(areaToSend);
						newNeighbour = OverloadJoin.getNeighboursToNewArea(
								master.getLocalOverlayContact().getArea()
										.getArea(),
								newNeighbour);

						master.removeVidNeighboursOfNeighbours(newNeighbour);
						master.removeNeighboursOfNeighbours(newNeighbour);

						master.setNeighbours(newNeighbour);
						master.setVIDNeigbours(sendVidNeighbours);

						List<Object[]> hashToSend = new LinkedList<Object[]>();
						if (master.getStoredHashs() != null) {
							newHashs.addAll(master.getStoredHashs());
						}
						if (master.getLeavingHash() != null) {
							newHashs.addAll(master.getLeavingHash());
						}
						for (int j = 0; j < newHashs.size(); j++) {
							if (((CanOverlayKey) newHashs.get(j)[0])
									.includedInArea(areaToSend)) {
								hashToSend.add(newHashs.get(j));
							}
						}
						master.setStoredHashs(hashToSend);

						sendNewVIDNeighbours(newContacts.get(i),
								sendVidNeighbours[0], sendVidNeighbours[1]);
					}

					List<CanOverlayContact> sendNewNeighboursTo = new LinkedList<CanOverlayContact>();
					sendNewNeighboursTo.addAll(newNeighbour);

					List<CanOverlayContact> newNeighboursToSendList = new LinkedList<CanOverlayContact>();

					newNeighboursToSendList.addAll(newContacts);

					List<CanOverlayContact> deleteNeighbours = new LinkedList<CanOverlayContact>();
					deleteNeighbours.addAll(newContacts);
					deleteNeighbours.add(missingNode);
					deleteNeighbours.add(master.getLocalOverlayContact()
							.clone());

					for (int j = 0; j < sendNewNeighboursTo.size(); j++) {
						NewNeighbourMsg newNeighboursMsg = new NewNeighbourMsg(
								master.getCanOverlayID(), sendNewNeighboursTo
										.get(j).getOverlayID(),
								master.getLocalOverlayContact().clone(),
								deleteNeighbours, newNeighboursToSendList);
						master.getTransLayer().send(newNeighboursMsg,
								sendNewNeighboursTo.get(j).getTransInfo(),
								master.getPort(), TransProtocol.TCP);
					}

					log.debug("masterNode: "
							+ master.getLocalOverlayContact().getOverlayID()
									.toString()
							+ " "
							+ master.getLocalOverlayContact().getArea()
									.toString()
							+ " "
							+ master.getLocalOverlayContact().getArea()
									.getVid()
									.toString()
							+ " vid neighbours "
							+ master.getVIDNeighbours()[0].getArea().getVid()
									.toString()
							+ " "
							+ master.getVIDNeighbours()[1].getArea().getVid()
									.toString());
					for (int x = 0; x < master.getNeighbours().size(); x++) {
						log.debug("neighbour " + master.getNeighbours().get(x));
					}

				}

				log.debug("Changed node: "
						+ master.getLocalOverlayContact().getOverlayID()
								.toString()
						+ " area "
						+ master.getLocalOverlayContact().getArea().toString()
						+ " alive " + master.getLocalOverlayContact().isAlive());

				master.getBootstrap().unregisterNode(missingNode);
				master.removeNeighbour(missingNode);
				master.emptyLeavingNeighbours();
				master.emptyLeavingReplyContacts();
				master.emptyLeavingReplyVIDNeighbours();
				master.emptyLeavingArea();
				master.setMissingNode(null);

				done = true;
				this.scheduleWithDelay(CanConfig.waitTimeBetweenPing);

				boolean masterIncluded = false;
				for (CanOverlayContact contacts : newContacts) {
					if (contacts
							.getOverlayID()
							.toString()
							.equals(master.getLocalOverlayContact()
									.getOverlayID()
									.toString())) {
						masterIncluded = true;
					}
				}
				if (masterIncluded == false) {
					newContacts.add(master.getLocalOverlayContact());
				}
			}
		}
		else {
			for (int i = 0; i < newContacts.size(); i++) {
				log.debug(Simulator.getSimulatedRealtime() + "startTakeover"
						+ newContacts.get(i).getOverlayID().toString());
				if (newContacts
						.get(i)
						.getOverlayID()
						.toString()
						.equals(master.getLocalOverlayContact().getOverlayID()
								.toString())) {
					master.resumeTakeoverOperation();
				}
				else {
					StartTakeoverMsg startTakeover = new StartTakeoverMsg(
							master.getLocalOverlayContact().getOverlayID(),
							newContacts.get(i).getOverlayID());
					master.getTransLayer().send(startTakeover,
							newContacts.get(i).getTransInfo(),
							master.getPort(), TransProtocol.TCP);
				}

			}
		}

	}

	@Override
	public Object getResult() {
		return null;
	}

	/**
	 * takes the old areas and put them back together
	 * 
	 * @param area
	 *            list of old area
	 * @return new area
	 */
	public static CanArea getNewArea(List<CanArea> area) {
		CanArea newArea = new CanArea(0, 0, 0, 0);
		for (int j = 0; j < area.size(); j++) {
			for (int i = 0; i < area.size(); i++) {
				CanArea jArea = area.get(j);
				CanArea iArea = area.get(i);
				if (j != i
						&& (jArea.getVid().numberCommon(iArea.getVid()) == jArea
								.getVid().getVIDList().size() - 1)) {
					newArea = new CanArea(0, 0, 0, 0);

					if (jArea.getArea()[0] == iArea.getArea()[0]
							&& jArea.getArea()[1] == iArea.getArea()[1]
							&& (jArea.getArea()[2] == iArea.getArea()[3])) {
						newArea.setX0(jArea.getArea()[0]);
						newArea.setX1(jArea.getArea()[1]);
						newArea.setY0(iArea.getArea()[2]);
						newArea.setY1(jArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid()
								.listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);
						area.remove(jArea);
						area.remove(iArea);
						area.add(newArea);
						i = -1;
						j = -1;
						break;
					}
					else if (jArea.getArea()[0] == iArea.getArea()[0]
							&& jArea.getArea()[1] == iArea.getArea()[1]
							&& (jArea.getArea()[3] == iArea.getArea()[2])) {
						newArea.setX0(jArea.getArea()[0]);
						newArea.setX1(jArea.getArea()[1]);
						newArea.setY0(jArea.getArea()[2]);
						newArea.setY1(iArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid()
								.listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);
						area.remove(iArea);
						area.remove(jArea);
						area.add(newArea);
						i = -1;
						j = -1;
						break;
					}
					else if (jArea.getArea()[2] == iArea.getArea()[2]
							&& jArea.getArea()[3] == iArea.getArea()[3]
							&& (jArea.getArea()[0] == iArea.getArea()[1])) {
						newArea.setX0(iArea.getArea()[0]);
						newArea.setX1(jArea.getArea()[1]);
						newArea.setY0(jArea.getArea()[2]);
						newArea.setY1(jArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid()
								.listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);

						area.remove(iArea);
						area.remove(jArea);
						area.add(newArea);
						i = -1;
						j = -1;
						break;
					}
					else if (jArea.getArea()[2] == iArea.getArea()[2]
							&& jArea.getArea()[3] == iArea.getArea()[3]
							&& (jArea.getArea()[1] == iArea.getArea()[0])) {
						newArea.setX0(jArea.getArea()[0]);
						newArea.setX1(iArea.getArea()[1]);
						newArea.setY0(jArea.getArea()[2]);
						newArea.setY1(jArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid()
								.listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);

						area.remove(iArea);
						area.remove(jArea);
						area.add(newArea);
						i = -1;
						j = -1;
						break;
					}
				}
			}
		}
		log.debug("new Area: " + newArea.toString());
		return newArea;
	}

	public void sendNewVIDNeighbours(CanOverlayContact sendTo,
			CanOverlayContact vidNeighbour1, CanOverlayContact vidNeighbour2) {
		log.debug("send newVIDNeighbours " + sendTo.getOverlayID().toString()
				+ " " + sendTo.getArea().getVid().toString()
				+ " " + vidNeighbour1.getArea().getVid().toString()
				+ " " + vidNeighbour2.getArea().getVid().toString());
		CanOverlayContact[] sendVID1 = { null, sendTo.clone() };
		CanOverlayContact[] sendVID2 = { sendTo.clone(), null };
		NewVIDNeighbourMsg newVIDNeighboursMsg1 = new NewVIDNeighbourMsg(
				sendTo.getOverlayID(),
				vidNeighbour1.getOverlayID(), sendVID1);

		master.getTransLayer().send(newVIDNeighboursMsg1,
				vidNeighbour1.getTransInfo(),
				master.getPort(), TransProtocol.TCP);

		NewVIDNeighbourMsg newVIDNeighboursMsg2 = new NewVIDNeighbourMsg(
				sendTo.getOverlayID(),
				vidNeighbour2.getOverlayID(), sendVID2);
		master.getTransLayer().send(newVIDNeighboursMsg2,
				vidNeighbour2.getTransInfo(),
				master.getPort(), TransProtocol.TCP);
	}

}
