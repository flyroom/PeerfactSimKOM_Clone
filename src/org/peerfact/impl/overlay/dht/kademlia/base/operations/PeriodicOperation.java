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

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.Node;

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
 * Permits to schedule an operation periodically.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class PeriodicOperation<T extends KademliaOverlayID> extends
		AbstractKademliaOperation<Object, T> {

	/**
	 * The interval between two executions of <code>periodicOp</code>.
	 */
	private final long interval;

	/**
	 * Whether the execution of the periodic operation is still desirable. This
	 * value will be checked before each periodic execution and, once false,
	 * this periodic operation will halt.
	 */
	// private boolean executionDesirable = true;
	/**
	 * Schedules the operation
	 * <code>periodicOp={@link #createPeriodicOperation()}</code> to execute
	 * periodically with interval <code>interval</code>. Note that this
	 * PeriodicOperation has to be scheduled itself. <code>periodicOp</code> is
	 * scheduled immediately when this operation executes, hence in order to
	 * have an initial delay before the first execution of
	 * <code>scheduleOp</code>, this PeriodicOperation should be scheduled with
	 * that delay.
	 * <p>
	 * <!-- NOTE: the functionality of this paragraph is currently disabled!!
	 * Before the periodic operation is executed, the condition
	 * <code>executionDesirable == true</code> is checked (it can be set to
	 * false externally via invokation of {@link #unschedule()}). If this
	 * condition does not hold, <code>periodicOp</code> will not be executed and
	 * the periodic scheduling will stop. It has to be resumed manually if
	 * necessary.-->
	 * 
	 * @param interval
	 *            the interval between two executions of <code>periodicOp</code>
	 *            (in simulation time). The execution time of the operation
	 *            returned by {@link #createPeriodicOperation()} is
	 *            <i>ignored</i> by this operation, that is, if it is larger
	 *            than <code>interval</code>, the operation might be active in
	 *            several instances at the same time.
	 * @param node
	 *            the node on which this operation runs. The operation is
	 *            aborted if
	 *            <code>node.getPeerStatus()==PeerStatus.ABSENT</code>.
	 * @param opCallback
	 *            a callback that is informed when this operation terminates.
	 * @param reason
	 *            the reason why this periodic operation runs (most probably
	 *            Reason.MAINTENANCE).
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public PeriodicOperation(final long interval, final Node<T> node,
			final OperationCallback<?> opCallback, final Reason reason,
			final OperationsConfig conf) {
		super(node, opCallback, reason, conf);
		this.interval = interval;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void execute() {
		// if (executionDesirable) {
		createPeriodicOperation().scheduleImmediately();
		this.scheduleWithDelay(interval);
		// } else {
		// // operation stops as requested
		// operationFinished(true);
		// }
	}

	/**
	 * Marks this operation so that it will not trigger future executions of
	 * this periodic operation. (If one instance of the periodic operation is
	 * currently running, it will not be stopped.)
	 */
	// public final void unschedule() {
	// this.executionDesirable = false;
	// }
	/**
	 * @return the KademliaOperation that is to be periodically scheduled as
	 *         long as this peer is online. Depending on the nature of the
	 *         operation, it might be necessary to construct a new instance each
	 *         time. Care should be taken that the operation timeout of this
	 *         operation is <i>shorter</i> than the <code>interval</code> in
	 *         which it will be scheduled.
	 */
	protected abstract KademliaOperation<?> createPeriodicOperation();

}
