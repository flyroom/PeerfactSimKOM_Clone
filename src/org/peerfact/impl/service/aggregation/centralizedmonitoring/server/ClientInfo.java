package org.peerfact.impl.service.aggregation.centralizedmonitoring.server;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Attribute;


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
 * @author Alexander Nigl
 * 
 * @param <ID>
 *            Class of identifier
 */
public class ClientInfo<ID extends Object> extends
		LinkedHashMap<ID, Attribute<ID>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1843835238483342067L;

	/**
	 * Indicates if it is marked for deletion.
	 */
	public boolean marked;

	/**
	 * Constructs ClientInfo and initializes marked as false;
	 */
	public ClientInfo() {
		super();
		this.marked = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Attribute<ID> put(final ID key, final Attribute<ID> value) {
		this.marked = false;
		return super.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(Map<? extends ID, ? extends Attribute<ID>> m) {
		this.marked = false;
		super.putAll(m);
	}
}
