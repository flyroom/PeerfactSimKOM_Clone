package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class Attribute<ID> {
	private double value;

	private ID identifier;

	public boolean dirty;

	public Attribute(ID identifier, double value) {
		this.identifier = identifier;
		this.value = value;
		this.dirty = true;
	}

	public ID getIdentifier() {
		return this.identifier;
	}

	public Double getValue() {
		return this.value;
	}

}
