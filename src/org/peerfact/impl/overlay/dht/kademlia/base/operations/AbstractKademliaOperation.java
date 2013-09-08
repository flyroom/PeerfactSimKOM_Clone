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

package org.peerfact.impl.overlay.dht.kademlia.base.operations;

import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageCallback;
import org.peerfact.api.transport.TransProtocol;
import org.peerfact.impl.common.AbstractOperation;
import org.peerfact.impl.common.AbstractOperationCounter;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;


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
 * An abstract operation with very basic methods for Kademlia. An
 * AbstractKademliaOperation can be scheduled multiple times, but only if it has
 * not already been in a finished state. If it has been finished, it intercepts
 * all incoming events (such as message reception or completion of triggered
 * operations) and prevents them from being treated in subclasses.
 * <p>
 * This AbstractKademliaOperation is based on code from
 * {@link AbstractOperation}.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @author Sebastian Kaune <kaune@kom.tu-darmstadt.de>
 * @author Konstantin Pussep <pussep@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class AbstractKademliaOperation<T, S extends KademliaOverlayID>
		extends AbstractOperationCounter<T> implements KademliaOperation<T>,
		TransMessageCallback,
		OperationCallback<List<KademliaOverlayContact<S>>>,
		SimulationEventHandler {
	// FIXME check type parameter "Object"

	/**
	 * Constants for different reasons why an operation can be run (or why a
	 * message is sent).
	 */
	public enum Reason {
		MAINTENANCE, USER_INITIATED
	}

	/**
	 * Details about the state of an Operation.
	 */
	public enum OperationState {
		/** The operation has been constructed but not yet started. */
		READY {
			@Override
			public final boolean isFinished() {
				return false;
			}
		},

		/** The operation is still running. */
		RUNNING {
			@Override
			public final boolean isFinished() {
				return false;
			}
		},

		/** The operation has completed successfully. */
		SUCCESS {
			@Override
			public final boolean isFinished() {
				return true;
			}

			@Override
			public final boolean isSuccessful() {
				return true;
			}
		},

		/** The operation has completed with an error. */
		ERROR {
			@Override
			public final boolean isFinished() {
				return true;
			}
		},

		/** The operation has taken too long to complete. */
		TIMEOUT {
			@Override
			public final boolean isFinished() {
				return true;
			}
		},

		/** The operation has been aborted. */
		ABORTED {
			@Override
			public final boolean isFinished() {
				return true;
			}
		};

		/**
		 * @return whether this state is a finished state.
		 */
		public abstract boolean isFinished();

		/**
		 * @return whether this state denotes a state that is both finished and
		 *         successful.
		 */
		public boolean isSuccessful() {
			return false;
		}
	}

	/**
	 * The current state of this Operation.
	 */
	private OperationState myState;

	/**
	 * The caller of this Operation.
	 */
	private final OperationCallback caller;

	/**
	 * The owner component of this operation.
	 */
	private final Node<S> owner;

	/**
	 * The reason why this operation is being executed.
	 */
	protected final Reason reason;

	/**
	 * Configuration values ("constants").
	 */
	protected final OperationsConfig config;

	private long startTime;

	private long endTime;

	/**
	 * Constructs a new AbstractKademliaOperation.
	 * 
	 * @param node
	 *            the Node that runs this operation.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param reason
	 *            the reason why this operation is being run.
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public AbstractKademliaOperation(final Node<S> node,
			final OperationCallback<?> opCallback, final Reason reason,
			final OperationsConfig conf) {
		super();
		this.caller = opCallback;
		this.config = conf;
		this.reason = reason;
		this.owner = node;
		this.myState = OperationState.READY;
		node.getOperationFactory().operationConstructed(this);
	}

	/*
	 * Getters
	 */

	/**
	 * @return the reason why this operation is being executed (user-initiated
	 *         or for maintenance purposes).
	 */
	public final Reason getReason() {
		return this.reason;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getResult() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(id=" + getOperationID()
				+ ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSuccessful() {
		return myState.isSuccessful();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isFinished() {
		return myState.isFinished();
	}

	@Override
	public final long getDuration() {
		if (this.startTime != 0 && this.endTime != 0) {
			return this.endTime - this.startTime;
		} else {
			return 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Node<S> getComponent() {
		return owner;
	}

	/**
	 * @return the OperationState of this Operation.
	 */
	public final OperationState getState() {
		return myState;
	}

	/*
	 * Operation scheduling methods
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void scheduleImmediately() {
		scheduleWithDelay(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void scheduleWithDelay(final long delay) {
		final long time = Simulator.getCurrentTime() + delay;
		scheduleAtTime(time);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void scheduleAtTime(final long time) {
		final long nowOrLater = Math.max(time, Simulator.getCurrentTime());
		Simulator.scheduleEvent(this, nowOrLater, this,
				SimulationEvent.Type.OPERATION_EXECUTE);
	}

	/**
	 * Schedules an operation timeout in <code>timeout</code> simulation time
	 * units. The timeout event will be scheduled relative to the current
	 * simulation time.
	 * 
	 * @param timeout
	 */
	protected final void scheduleOperationTimeout(final long timeout) {
		final long time = Simulator.getCurrentTime() + timeout;
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.TIMEOUT_EXPIRED);
	}

	/*
	 * Methods for Operation execution & termination
	 */

	/**
	 * Changes the state of this Operation to <code>newState</code> and executes
	 * related actions. Possible state transitions are from READY to RUNNING,
	 * from RUNNING to RUNNING and from a non-finished state to a state with
	 * <code>isFinished()==true</code>.
	 * <p>
	 * When an Operation changes to the RUNNING state, {@link #execute()} is
	 * called. When changing to a finished state, the callback for subclasses
	 * and the (external) caller are notified (in this order).
	 * <p>
	 * Any state except RUNNING cannot be reentered. (These calls will be
	 * ignored.) For instance, if this Operation has already finished, it is not
	 * possible to change the finished state from ABORTED to SUCCESSFUL. In
	 * contrast, if the state of this Operation is RUNNING and this method is
	 * called with <code>newState==RUNNING</code>, the <code>execute()</code>
	 * method is called again.
	 * 
	 * @param newState
	 *            the new OperationState of this Operation.
	 */
	private void changeState(final OperationState newState) {
		if (myState == OperationState.READY
				&& newState == OperationState.RUNNING) {
			// start operation, call monitor
			myState = OperationState.RUNNING;
			this.startTime = Simulator.getCurrentTime();
			Simulator.getMonitor().operationInitiated(this);
			execute();
		} else if (myState == OperationState.RUNNING
				&& newState == OperationState.RUNNING) {
			// restart operation, do not call monitor
			execute();
		} else if (!myState.isFinished() && newState.isFinished()) {
			// stop operation
			myState = newState;
			owner.getOperationFactory().operationFinished(this);
			operationJustFinished();
			this.endTime = Simulator.getCurrentTime();
			Simulator.getMonitor().operationFinished(this);
			if (isSuccessful()) {
				caller.calledOperationSucceeded(this);
			} else {
				caller.calledOperationFailed(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void abort() {
		changeState(OperationState.ABORTED);
	}

	/**
	 * Finishes this Operation for the given reason, notifies listeners in
	 * subclasses and the caller (in this order). If this Operation has been
	 * finished before, this method has no effect. If <code>newState</code> is
	 * no finish state, this method has no effect either.
	 * 
	 * @param newState
	 *            the new OperationState of this Operation.
	 *            <code>newState.isFinished()==true</code> is required.
	 */
	protected final void finishOperation(final OperationState newState) {
		// ignore if newState is not a finished state
		if (newState.isFinished()) {
			changeState(newState);
		}
	}

	/**
	 * This Operation has just finished. That is, it has just moved from state
	 * "not finished" to "finished". Note that the caller (
	 * {@link OperationCallback} of this Operation will be notified <i>after</i>
	 * this method has returned. While this method executes,
	 * {@link #isFinished()} will always return <code>true</code>.
	 * <p>
	 * This method is intended to be overwritten by subclasses if these need to
	 * be informed about this kind of events. It should not be called from
	 * classes other than AbstractKademliaOperation!
	 */
	protected void operationJustFinished() {
		// ignore by default - overwrite in subclasses if needed
	}

	/**
	 * Starts this Operation. This method is called if this Operation is
	 * ready/scheduled to execute.
	 * 
	 * @see AbstractOperation#execute()
	 */
	protected abstract void execute();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void eventOccurred(final SimulationEvent se) {
		if (se.getType() == SimulationEvent.Type.TIMEOUT_EXPIRED) {
			changeState(OperationState.TIMEOUT);
		} else if (se.getType() == SimulationEvent.Type.OPERATION_EXECUTE
				&& se.getData() == this) {
			changeState(OperationState.RUNNING);
		}
	}

	/*
	 * Methods for message sending & receiving
	 */

	/**
	 * Sends the given request message to its destination from the port and via
	 * the transport layer of the Node that owns this Operation. The protocol is
	 * UDP and the message timeout is
	 * {@link OperationsConfig#getLookupMessageTimeout()}. Replies will be
	 * received by this class and after sending the message this class' callback
	 * {@link #messageSent(int, KademliaOverlayID)} is called.
	 * 
	 * @see #messageSent(int, KademliaOverlayID)
	 * @see #messageReceived(KademliaMsg, TransInfo, int)
	 * @see #messageTimedOut(int)
	 * @see TransLayer#sendAndWait(Message, TransInfo, short, TransProtocol,
	 *      TransMessageCallback, long)
	 * 
	 * @param message
	 *            the KademliaMsg to be sent.
	 * @param destination
	 *            the KademliaOverlayContact of the receiver of the message.
	 */
	protected final void sendAndWait(final KademliaMsg<S> message,
			final KademliaOverlayContact<S> destination) {
		final TransLayer transport = getComponent().getMessageManager();
		final short myPort = getComponent().getPort();
		final TransInfo rcvrAddress = destination.getTransInfo();
		final long timeout = config.getLookupMessageTimeout();
		final TransProtocol protocol = TransProtocol.UDP;
		final TransMessageCallback replyCallback = this;
		final int commId;
		commId = transport.sendAndWait(message, rcvrAddress, myPort, protocol,
				replyCallback, timeout);
		messageSent(commId, destination.getOverlayID());
	}

	/**
	 * A request message has been sent (via
	 * {@link #sendAndWait(KademliaMsg, KademliaOverlayContact)} with the given
	 * information.
	 * <p>
	 * This method is intended to be overwritten by subclasses if these need to
	 * be informed about this kind of events. It should not be called from
	 * classes other than AbstractKademliaOperation!
	 * 
	 * @param commId
	 *            the communication identifier of the message that has been
	 *            sent.
	 * @param destination
	 *            the KademliaOverlayID of the receiver of the message that has
	 *            been sent.
	 */
	protected void messageSent(final int commId, final S destination) {
		// ignore by default - overwrite in subclasses if needed
	}

	/**
	 * Sends the given one-to-one message to the given destination from the port
	 * and via the transport layer of the Node that owns this Operation. The
	 * protocol is UDP. The sender (this Operation) does not expect to receive a
	 * reply and in contrast to
	 * {@link #sendAndWait(KademliaMsg, KademliaOverlayContact)}, there is
	 * <i>no</i> callback that will be called if the message has been sent.
	 * 
	 * @see TransLayer#send(Message, TransInfo, short, TransProtocol)
	 * 
	 * @param message
	 *            the KademliaMsg to be sent.
	 * @param destination
	 *            the KademliaOverlayContact of the receiver of the message.
	 */
	protected final void send(final KademliaMsg<S> message,
			final KademliaOverlayContact<S> destination) {
		final TransLayer transport = getComponent().getMessageManager();
		final short myPort = getComponent().getPort();
		final TransInfo rcvrAddress = destination.getTransInfo();
		final TransProtocol protocol = TransProtocol.UDP;
		transport.send(message, rcvrAddress, myPort, protocol);
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Intercept event, process it in this class, and inform callbacks in
	 * subclasses.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void receive(final Message msg, final TransInfo senderInfo,
			final int commId) {
		if (!isFinished()) {
			messageReceived((KademliaMsg<S>) msg, senderInfo, commId);
		}
	}

	/**
	 * A reply to a message sent by this Operation has been received. Note that
	 * incoming messages are discarded (and this callback is not called) if the
	 * Node that owns this Operation is ABSENT. Instead, this Operation will be
	 * aborted.
	 * <p>
	 * This method is intended to be overwritten by subclasses if these need to
	 * be informed about this kind of events. It should not be called from
	 * classes other than AbstractKademliaOperation!
	 * 
	 * @see TransMessageCallback#receive(Message, TransInfo, int)
	 * @param msg
	 *            the incoming message
	 * @param senderInfo
	 *            the TransInfo of the sender
	 * @param commId
	 *            the unique communication id
	 */
	protected void messageReceived(final KademliaMsg<S> msg,
			final TransInfo senderInfo, final int commId) {
		// ignore by default - overwrite in subclasses if needed
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Intercept event, process it in this class, and inform callbacks in
	 * subclasses.
	 */
	@Override
	public final void messageTimeoutOccured(final int commId) {
		if (!isFinished()) {
			messageTimedOut(commId);
		}
	}

	/**
	 * A reply expected for a message sent by this Operation has not arrived in
	 * time (a timeout occurred). Note that a timeout event is discarded by
	 * AbstractKademliaOperation (and this callback is not called) if the Node
	 * that owns this Operation is ABSENT. Instead, when the timeout event
	 * occurs, this Operation will be aborted.
	 * <p>
	 * This method is intended to be overwritten by subclasses if these need to
	 * be informed about this kind of events. It should not be called from
	 * classes other than AbstractKademliaOperation!
	 * 
	 * @see TransMessageCallback#messageTimeoutOccured(int)
	 * @param commId
	 *            the unique communication id
	 */
	protected void messageTimedOut(final int commId) {
		// ignore by default - overwrite in subclasses if needed
	}

	/*
	 * Methods for awaiting other Operations
	 */

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Intercept event, process it in this class, and inform callbacks in
	 * subclasses.
	 */
	@Override
	public final void calledOperationFailed(final Operation op) {
		if (!isFinished()) {
			calledOperationDidFail(op);
		}
	}

	/**
	 * Called if an Operation triggered by this Operation failed. Note that a
	 * this kind of event is discarded by AbstractKademliaOperation (and this
	 * callback is not called) if the Node that owns this Operation is ABSENT.
	 * Instead, when the event occurs, this Operation will be aborted.
	 * <p>
	 * This method is intended to be overwritten by subclasses if these need to
	 * be informed about this kind of events. It should not be called from
	 * classes other than AbstractKademliaOperation!
	 * 
	 * @see OperationCallback#calledOperationFailed(Operation)
	 * @param op
	 *            failed operation
	 */
	protected void calledOperationDidFail(final Operation<?> op) {
		// ignore by default - overwrite in subclass if needed
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Intercept event, process it in this class, and inform callbacks in
	 * subclasses.
	 */
	@Override
	public final void calledOperationSucceeded(final Operation op) {
		if (!isFinished()) {
			calledOperationDidSucceed(op);
		}
	}

	/**
	 * Called if an Operation triggered by this Operation finished successfully.
	 * Note that a this kind of event is discarded by AbstractKademliaOperation
	 * (and this callback is not called) if the Node that owns this Operation is
	 * ABSENT. Instead, when the event occurs, this Operation will be aborted.
	 * <p>
	 * This method is intended to be overwritten by subclasses if these need to
	 * be informed about this kind of events. It should not be called from
	 * classes other than AbstractKademliaOperation!
	 * 
	 * @see OperationCallback#calledOperationSucceeded(Operation)
	 * @param op
	 *            finished operation
	 */
	protected void calledOperationDidSucceed(final Operation op) {
		// ignore by default - overwrite in subclass if needed
	}

}
