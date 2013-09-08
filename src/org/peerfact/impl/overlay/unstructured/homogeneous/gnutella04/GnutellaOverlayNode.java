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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.unstructured.HomogeneousOverlayNode;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.overlay.AbstractOverlayMessage;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing.FilesharingDocument;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.filesharing.FilesharingKey;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.BaseMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.ConnectMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.OkMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PingMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PongMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PushMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryHitMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.ConnectOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.PingOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.PongOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.PushOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.QueryHitOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.QueryOperation;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.operations.ScheduleConnectOperation;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnutellaOverlayNode extends
		AbstractOverlayNode<GnutellaOverlayID, GnutellaAccessOverlayContact>
		implements
		HomogeneousOverlayNode<GnutellaOverlayID, GnutellaAccessOverlayContact>,
		TransMessageCallback, TransMessageListener {

	private TransLayer transLayer;

	private Map<FilesharingKey, FilesharingDocument> documents = new LinkedHashMap<FilesharingKey, FilesharingDocument>();

	// TODO change OverlayID to Integer
	// TODO initiated timeout for ping
	// ping initiated (Descriptor)
	private Set<BigInteger> pingInitiated = new LinkedHashSet<BigInteger>();

	// queries initiated by application layer (Descriptor, FilesharingKey)
	private Map<BigInteger, FilesharingKey> queryInitiatedWithKey = new LinkedHashMap<BigInteger, FilesharingKey>();

	// results, ready to get for application layer (Descriptor, Contacts)
	private Map<BigInteger, List<OverlayContact<GnutellaOverlayID>>> queryResults = new LinkedHashMap<BigInteger, List<OverlayContact<GnutellaOverlayID>>>();

	// TODO low priority: Timeouts
	private long delayAcceptConnection;

	private long lastAcceptedConnection = 0;

	boolean active = false;

	public GnutellaOverlayNode(TransLayer transLayer, GnutellaOverlayID peerId,
			int numConn, long delayAcceptConnection, long refresh,
			long contactTimeout, long descriptorTimeout, short port) {
		super(peerId, port);
		active = true;
		this.transLayer = transLayer;
		transLayer.addTransMsgListener(this, this.getPort());
		this.delayAcceptConnection = delayAcceptConnection;
		this.routingTable = new GnutellaOverlayRoutingTable(peerId);
		GnutellaBootstrapManager.getInstance().registerPeer(this);
		this.getGnutellaRoutingTable()
				.setNumConn(numConn);
		this.getGnutellaRoutingTable()
				.setRefresh(refresh);
		this.getGnutellaRoutingTable()
				.setContactTimeout(contactTimeout);
		this.getGnutellaRoutingTable()
				.setDescriptorTimeout(descriptorTimeout);
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		//
	}

	@Override
	public void messageTimeoutOccured(int commId) {
		//
	}

	@Override
	public void receive(Message msg, TransInfo senderInfo, int commId) {
		//
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message message = receivingEvent.getPayload();

		if (isActive()) {
			// accept messages only if connection to peer exists
			if (message instanceof BaseMessage
					&& getGnutellaRoutingTable()
							.getContact(
									((AbstractOverlayMessage<GnutellaOverlayID>) message
											.getPayload()).getSender()) != null) {

				if (message instanceof PingMessage) {
					processPing(receivingEvent);
				} else if (message instanceof PongMessage) {
					processPong(receivingEvent);
				} else if (message instanceof QueryMessage) {
					processQuery(receivingEvent);
				} else if (message instanceof QueryHitMessage) {
					processQueryHit(receivingEvent);
				} else if (message instanceof PushMessage) {
					processPush(receivingEvent);
				}
			}

			if (message instanceof ConnectMessage) {
				processConnect(receivingEvent);
			} else if (message instanceof OkMessage) {
				processOk(receivingEvent);
			}
		}
	}

	private void processConnect(TransMsgEvent receivingEvent) {
		int size = this.getGnutellaRoutingTable()
				.numberOfActiveContacts();
		if (size < this.getGnutellaRoutingTable()
				.getNumConn()
				|| this.lastAcceptedConnection < Simulator.getCurrentTime()) {
			// set time for next connections
			if (size >= this.getGnutellaRoutingTable()
					.getNumConn()) {
				this.lastAcceptedConnection = this.lastAcceptedConnection
						+ this.delayAcceptConnection;
				if (Simulator.getCurrentTime() - this.lastAcceptedConnection > this.delayAcceptConnection
						* Math.ceil(1.0
								* this
										.getGnutellaRoutingTable().getNumConn()
								* GnutellaConfiguration.RELATIVE_CONNECT_SLOTS)) {
					this.lastAcceptedConnection = (Simulator.getCurrentTime() - ((long) (this.delayAcceptConnection * Math
							.ceil(1.0
									* this
											.getGnutellaRoutingTable()
											.getNumConn()
									* GnutellaConfiguration.RELATIVE_CONNECT_SLOTS))));
				}
			}

			ConnectMessage connectMessage = (ConnectMessage) receivingEvent
					.getPayload();
			this.getGnutellaRoutingTable().addContact(
					(GnutellaAccessOverlayContact) connectMessage.getContact());
			OverlayContact<GnutellaOverlayID> contact = new GnutellaAccessOverlayContact(
					this.getOverlayID(), this
							.getTransLayer().getLocalTransInfo(this.getPort()));

			OkMessage okMessage = new OkMessage(
					this.getOverlayID(), connectMessage
							.getContact().getOverlayID(), contact);
			this.getTransLayer().send(okMessage,
					connectMessage.getContact().getTransInfo(), this.getPort(),
					TransProtocol.UDP);
		}
	}

	private void processOk(TransMsgEvent receivingEvent) {

		OkMessage message = (OkMessage) receivingEvent.getPayload();
		this.getGnutellaRoutingTable().addContact(
				(GnutellaAccessOverlayContact) message.getContact());
	}

	private void processPing(TransMsgEvent receivingEvent) {
		PingMessage message = (PingMessage) receivingEvent.getPayload();
		if (this.getGnutellaRoutingTable()
				.incomingPing(message.getSender(), message.getDescriptor())
				&& !this.pingInitiated.contains(message.getDescriptor())) {
			// route ping
			if (message.getTTL() - 1 > 0) {
				PingOperation pingOperation = new PingOperation(this,
						message.getTTL() - 1, message.getHops() + 1,
						message.getDescriptor(), message.getSender(),
						new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				pingOperation.scheduleImmediately();
			}
			// reply with pong
			PongOperation pongOperation = new PongOperation(this,
					message.getDescriptor(), new OperationCallback<Object>() {
						@Override
						public void calledOperationFailed(Operation<Object> op) {
							//
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					});
			pongOperation.scheduleImmediately();

		}
	}

	private void processPong(TransMsgEvent receivingEvent) {
		PongMessage message = (PongMessage) receivingEvent.getPayload();
		boolean acceptPong = this
				.getGnutellaRoutingTable().incomingPong(message.getSender(),
						message.getDescriptor());
		// route pong
		if (acceptPong) {
			if (message.getTTL() - 1 > 0) {
				PongOperation pongOperation = new PongOperation(this,
						message.getTTL() - 1, message.getHops() + 1,
						message.getDescriptor(), message.getContact(),
						new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				pongOperation.scheduleImmediately();
			}
		}
		// add contact
		this.getGnutellaRoutingTable()
				.addInactiveContact((GnutellaAccessOverlayContact) message
						.getContact());
	}

	private void processQuery(TransMsgEvent receivingEvent) {
		QueryMessage message = (QueryMessage) receivingEvent.getPayload();
		// prevent accepting the same query two times or a query sent from here
		if (this.getGnutellaRoutingTable()
				.incomingQuery(message.getSender(), message.getDescriptor())
				&& !this.queryInitiatedWithKey.containsKey(message
						.getDescriptor())) {
			// route query
			if (message.getTTL() - 1 > 0) {
				QueryOperation queryOperation = new QueryOperation(this,
						message.getTTL() - 1, message.getHops() + 1,
						message.getDescriptor(), message.getSender(),
						message.getKey(), new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				queryOperation.scheduleImmediately();
			}
			OverlayContact<GnutellaOverlayID> contact = new GnutellaAccessOverlayContact(
					this.getOverlayID(), this
							.getTransLayer().getLocalTransInfo(this.getPort()));
			List<FilesharingKey> keys = new LinkedList<FilesharingKey>();
			// send query hits (same Key with prop. = 1, different keys with
			// prop. < 1)
			int messageKeyRank = message.getKey().getRank();
			for (FilesharingDocument document : documents.values()) {
				int documentKeyRank = document.getKey()
						.getRank();
				if ((messageKeyRank & GnutellaConfiguration.QUERY_KEY_MASK) == (documentKeyRank & GnutellaConfiguration.QUERY_KEY_MASK)) {
					keys.add(document.getKey());
				}
			}
			if (!keys.isEmpty()) {
				QueryHitOperation queryHitOperation = new QueryHitOperation(
						this, message.getDescriptor(), contact, keys,
						new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				queryHitOperation.scheduleImmediately();
			}
		}
	}

	private void processQueryHit(TransMsgEvent receivingEvent) {
		QueryHitMessage message = (QueryHitMessage) receivingEvent.getPayload();
		boolean acceptQuery = this
				.getGnutellaRoutingTable().incomingQueryHit(
						message.getSender(),
						message.getDescriptor(), message.getContact());
		// route query hit
		if (acceptQuery) {
			if (message.getTTL() - 1 > 0) {
				QueryHitOperation queryHitOperation = new QueryHitOperation(
						this, message.getTTL() - 1, message.getHops() + 1,
						message.getDescriptor(), message.getContact(),
						message.getKeys(), new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				queryHitOperation.scheduleImmediately();
			}
		}
		// addContact
		this.getGnutellaRoutingTable()
				.addInactiveContact((GnutellaAccessOverlayContact) message
						.getContact());
		// check if query was send from here and filter unwanted results
		if (message.getKeys().contains(
				queryInitiatedWithKey.get(message.getDescriptor()))) {
			// add contact to result list
			if (queryResults.get(message.getDescriptor()) == null) {
				queryResults.put(message.getDescriptor(),
						new LinkedList<OverlayContact<GnutellaOverlayID>>());
			}
			queryResults.get(message.getDescriptor()).add(
					new GnutellaAccessOverlayContact(message.getContact()));
		}
	}

	private void processPush(TransMsgEvent receivingEvent) {
		PushMessage message = (PushMessage) receivingEvent.getPayload();
		// check push and send file
		if (this.getOverlayID().equals(message.getPushReceiver())) {
			FilesharingDocument document = documents.get(message.getKey());
			if (document != null) {
				// TODO Datei senden
			}
		}
		// route push
		else if (message.getTTL() - 1 > 0) {
			PushOperation pushOperation = new PushOperation(this,
					message.getTTL() - 1, message.getHops() + 1,
					message.getDescriptor(), message.getPushSender(),
					message.getPushReceiver(), message.getKey(),
					new OperationCallback<Object>() {
						@Override
						public void calledOperationFailed(Operation<Object> op) {
							//
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					});
			pushOperation.scheduleImmediately();
		}
	}

	public boolean addDocument(FilesharingDocument arg0) {
		return (documents.put(arg0.getKey(), arg0) != null);
	}

	public void clearDocuments() {
		documents.clear();
	}

	public boolean containsKey(Object arg0) {
		return documents.containsKey(arg0);
	}

	public boolean containsDocument(Object arg0) {
		return documents.containsValue(arg0);
	}

	public boolean removeDocument(FilesharingDocument arg0) {
		FilesharingKey key = arg0.getKey();
		return (documents.remove(key) != null);
	}

	public FilesharingDocument removeKey(FilesharingKey arg0) {
		return documents.remove(arg0);
	}

	public Set<FilesharingKey> keySet() {
		return documents.keySet();
	}

	public Collection<FilesharingDocument> getDocuments() {
		return documents.values();
	}

	public void registerQuery(BigInteger descriptor, FilesharingKey key) {
		this.queryResults.put(descriptor,
				new LinkedList<OverlayContact<GnutellaOverlayID>>());
		this.queryInitiatedWithKey.put(descriptor, key);
	}

	public List<OverlayContact<GnutellaOverlayID>> getQueryResults(
			BigInteger descriptor) {
		List<OverlayContact<GnutellaOverlayID>> ret = queryResults
				.get(descriptor);
		queryResults.remove(descriptor);
		queryInitiatedWithKey.remove(descriptor);
		return ret;
	}

	public void scheduleConnect(
			ScheduleConnectOperation scheduleOverlayOperation) {
		// Join to bootstrap_nodes, if no contacts available
		if (this.getGnutellaRoutingTable()
				.numberOfActiveContacts() == 0) {
			List<TransInfo> bootstrapInfos = GnutellaBootstrapManager
					.getInstance().getBootstrapInfo();
			for (TransInfo bootstrapInfo : bootstrapInfos) {
				ConnectOperation connectOperation = new ConnectOperation(this,
						bootstrapInfo, new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				connectOperation.scheduleImmediately();
			}
		}
		// sent ping, if not enough peers known
		else if (this.getGnutellaRoutingTable()
				.numberOfActiveContacts()
				+ this.getGnutellaRoutingTable()
						.inactiveContacts().size() < this
				.getGnutellaRoutingTable().getNumConn()) {
			PingOperation pingOperation = new PingOperation(this,
					new OperationCallback<Object>() {
						@Override
						public void calledOperationFailed(Operation<Object> op) {
							//
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					});
			pingOperation.scheduleImmediately();
			this.pingInitiated.add(pingOperation.getDescriptor());
		}
		// sent ping if refresh needed
		List<GnutellaAccessOverlayContact> refreshContacts = this
				.getGnutellaRoutingTable().getRefreshContacts();
		for (GnutellaAccessOverlayContact contact : refreshContacts) {
			PingOperation pingOperation = new PingOperation(this, contact,
					new OperationCallback<Object>() {
						@Override
						public void calledOperationFailed(Operation<Object> op) {
							//
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					});
			pingOperation.scheduleImmediately();
		}
		// remove dead descriptors
		List<BigInteger> deadDescriptors = this
				.getGnutellaRoutingTable().getDeadContacts();
		pingInitiated.removeAll(deadDescriptors);
		// connect to inactive nodes until all available connections are used
		for (int i = 0; i < this
				.getGnutellaRoutingTable().getNumConn()
				- this.getGnutellaRoutingTable()
						.numberOfActiveContacts(); i++) {
			OverlayContact<GnutellaOverlayID> contact = this
					.getGnutellaRoutingTable().removeInactiveContact();
			if (contact != null) {

				ConnectOperation connectOperation = new ConnectOperation(this,
						contact.getTransInfo(),
						new OperationCallback<Object>() {
							@Override
							public void calledOperationFailed(
									Operation<Object> op) {
								//
							}

							@Override
							public void calledOperationSucceeded(
									Operation<Object> op) {
								//
							}
						});
				connectOperation.scheduleImmediately();
			}
		}
	}

	public void scheduleReconnect(
			ScheduleConnectOperation scheduleOverlayOperation) {
		OverlayContact<GnutellaOverlayID> contact = this
				.getGnutellaRoutingTable().removeInactiveContact();
		if (contact != null) {
			ConnectOperation connectOperation = new ConnectOperation(this,
					contact.getTransInfo(), new OperationCallback<Object>() {
						@Override
						public void calledOperationFailed(Operation<Object> op) {
							//
						}

						@Override
						public void calledOperationSucceeded(
								Operation<Object> op) {
							//
						}
					});
			connectOperation.scheduleImmediately();
		}
	}

	@Override
	public String toString() {
		return this.getOverlayID().toString();
	}

	public void sendQuery(FilesharingKey key) {
		QueryOperation query = new QueryOperation(this, key,
				new OperationCallback<Object>() {
					@Override
					public void calledOperationFailed(Operation<Object> op) {
						//
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						//
					}
				});
	}

	public void fail() {
		active = false;
		routingTable.clearContacts();
		GnutellaBootstrapManager.getInstance().unregisterNode(this);
		GnutellaBootstrapManager.getInstance().unregisterPeer(this);
	}

	public void leave() {
		active = false;
		routingTable.clearContacts();
		GnutellaBootstrapManager.getInstance().unregisterNode(this);
		GnutellaBootstrapManager.getInstance().unregisterPeer(this);
	}

	public boolean isActive() {
		return active;
	}

	public GnutellaOverlayRoutingTable getGnutellaRoutingTable() {
		return (GnutellaOverlayRoutingTable) routingTable;
	}

	@Override
	public NeighborDeterminator<GnutellaAccessOverlayContact> getNeighbors() {
		return new NeighborDeterminator() {

			@Override
			public List<OverlayContact<GnutellaOverlayID>> getNeighbors() {
				return Collections.unmodifiableList(routingTable.allContacts());
			}
		};
	}

	@Override
	public int join(OperationCallback<Object> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int leave(OperationCallback<Object> callback) {
		// TODO Auto-generated method stub
		return 0;
	}
}
