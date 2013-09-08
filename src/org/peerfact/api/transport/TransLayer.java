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

package org.peerfact.api.transport;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.Message;
import org.peerfact.impl.transport.TransMsgEvent;

/**
 * The TransLayer acts as an <code>NetMessageListener</code> and listens for
 * incoming <code>NetMsgEvents</code> passed from the <code>NetLayer</code>. It
 * provides a framework for upper layers (i.e. the overlay layer or application
 * layer) in order to send messages or respond to received messages. Further,
 * message receivers which implements the <code>TransMessageListener</code>
 * interface can be registered for a specific port. Incoming messages are
 * dispatched either through port information or communication identifiers.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 12/03/2007
 * 
 */
public interface TransLayer extends Component {

	/**
	 * Registers a TransMessageListener listening for incoming messages on a
	 * specific port
	 * 
	 * @param receiver
	 *            the given TransMessageListener
	 * @param port
	 *            the port on which to listen for incoming messages
	 */
	public void addTransMsgListener(TransMessageListener receiver, short port);

	/**
	 * Removes a TransMessageListener listening for incoming messages on a
	 * specific port
	 * 
	 * @param receiver
	 *            the receiver to be removed
	 * @param port
	 *            the listening port
	 */
	public void removeTransMsgListener(TransMessageListener receiver, short port);

	/**
	 * Sends a message to a remote host by using the given
	 * <code>TransInfo</code> information of the receiver. Further to this, it
	 * is necessary to specify the port of the sender of the given message in
	 * order to support future replies or queries initiated by the remote
	 * receiver. Lastly, the transport protocol has to be chosen such as UDP or
	 * TCP.
	 * 
	 * @param msg
	 *            the message to be send
	 * @param receiverInfo
	 *            the remote receiver which should receive the given message
	 * @param senderPort
	 *            the port of the sender
	 * @param protocol
	 *            the used transport protocol
	 * @return the unique communication identifier
	 */
	public int send(Message msg, TransInfo receiverInfo, short senderPort,
			TransProtocol protocol);

	/**
	 * This method is used to implement a request-reply scenario. It sends the
	 * given message to a remote host by using the given <code>TransInfo</code>
	 * information of the receiver and calls the given TransMessageCallback when
	 * a reply for the given message is received by using
	 * {@link TransMessageCallback#receive(Message, TransInfo, int)} method.
	 * 
	 * In particular, the sendAndWait method returns a unique communication
	 * identifier which can be used to identify the above mentioned reply when
	 * implementing the TransMessageCallback interface. In addition to this, a
	 * timeout event occurs at the TransMessageCallback after
	 * <code>timeout</code> simulation units.
	 * 
	 * Note that the timeout interval must be adapted to the time units of the
	 * simulation framework. For instance, a real time time of two milliseconds
	 * is specified by 2 * Simulator.MILLISECOND_UNIT;
	 * 
	 * @param msg
	 *            the message to be send
	 * @param receiverInfo
	 *            the remote receiver which should receive the given message
	 * @param senderPort
	 *            the port of the sender
	 * @param protocol
	 *            the used transport protocol
	 * @param senderCallback
	 *            the TransMessageCallback which is called when receiving a
	 *            reply to the given message
	 * @param timeout
	 *            the timeout interval which has to be adapted to the time units
	 *            of the simulation framework
	 * @return the unique communication identifier
	 */
	public int sendAndWait(Message msg, TransInfo receiverInfo,
			short senderPort, TransProtocol protocol,
			TransMessageCallback senderCallback, long timeout);

	/**
	 * Sends a reply to a specific message which has been received within a
	 * TransMsgEvent. It is recommended to use this method in order to implement
	 * a request-reply scenario.
	 * 
	 * @param msg
	 *            the given reply message
	 * @param receivingEvent
	 *            the message receive event passed from the TransLayer that
	 *            triggers indirectly the sending of the given reply message
	 * @param senderPort
	 *            the port of the sender
	 * @param protocol
	 *            the used transport protocol
	 * 
	 * @return the unique communication identifier of the reply msg
	 */
	public int sendReply(Message msg, TransMsgEvent receivingEvent,
			short senderPort, TransProtocol protocol);

	/**
	 * Returns the local transport information which comprises the network
	 * identifier of the connected <code>NetLayer</code> and the given port
	 * 
	 * @param port
	 *            the local port
	 * 
	 * @return the TransInfo which comprises the network identifier of the
	 *         connected <code>NetLayer</code> and the given port
	 */
	public TransInfo getLocalTransInfo(short port);

	/**
	 * @return the unique id of the last communication
	 */
	public int getLastCommunicationId();

	/**
	 * @param commId
	 *            the unique id of the transmission to be canceled
	 */
	public void cancelTransmission(int commId);

}
