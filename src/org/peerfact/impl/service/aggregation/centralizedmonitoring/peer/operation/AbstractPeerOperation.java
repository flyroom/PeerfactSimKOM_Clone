package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.operation;

import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.common.SupportOperations;
import org.peerfact.impl.common.AbstractOperation;

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
 * Abstract operation to handle connectivity events, should be implemented from
 * every peer command.
 * 
 * @author Alexander Nigl
 * 
 * @param <ID>
 *            The class of identifier
 * @param <T>
 *            The exact type of the owner of this operation
 * @param <S>
 *            The exact type of the operation result
 */
public abstract class AbstractPeerOperation<ID extends Object, T extends SupportOperations, S>
		extends AbstractOperation<T, S>
		implements ConnectivityListener {

	private boolean stopped;

	protected AbstractPeerOperation(T component, OperationCallback<S> callback) {
		super(component, callback);
	}

	protected AbstractPeerOperation(T component) {
		super(component);
		this.stopped = false;
		component.getHost().getNetLayer().addConnectivityListener(this);
	}

	@Override
	protected void execute() {
		if (this.stopped) {
			this.getComponent().getHost().getNetLayer()
					.removeConnectivityListener(this);
			return;
		}
		this.doExecute();
	}

	/**
	 * Execute operation
	 */
	protected abstract void doExecute();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			this.stopped = true;
		}
	}

}
