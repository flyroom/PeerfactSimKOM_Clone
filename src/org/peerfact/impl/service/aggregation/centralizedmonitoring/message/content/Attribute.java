package org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content;

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
 * Represents a ID value pair
 * 
 * 
 * @author Alexander Nigl
 * 
 * @param <ID>
 *            Class of identifier
 */
public class Attribute<ID extends Object> {

	private ID identifier;

	private Double value;

	private long updateTime;

	private boolean dirty;

	public Attribute(ID identifier, Double value) {
		this.identifier = identifier;
		this.value = value;
		this.dirty = true;
		this.updateTime = Simulator.getCurrentTime();
	}

	public ID getIdentifier() {
		return this.identifier;
	}

	public Double getValue() {
		return this.value;
	}

	/** Returns size in bytes **/
	public static long getSize() {
		return (Integer.SIZE + Long.SIZE + Double.SIZE + 8) / 8;
	}

	public void reset() {
		this.dirty = true;
	}

	public void refresh(Attribute<ID> att) {
		if (this.value.equals(att.value)) {
			this.value = att.value;
			this.dirty = true;
			this.updateTime = Simulator.getCurrentTime();
		}
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public long getUpdateTime() {
		return updateTime;
	}
}
