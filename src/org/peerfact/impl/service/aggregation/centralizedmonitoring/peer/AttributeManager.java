package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.peerfact.api.service.aggr.NoSuchValueException;
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
 */
public class AttributeManager<ID> {

	private IAttributeFactory<ID> factory;

	private LinkedHashMap<ID, Attribute<ID>> attributes;

	public AttributeManager(IAttributeFactory<ID> factory) {
		this.factory = factory;
		this.attributes = new LinkedHashMap<ID, Attribute<ID>>();
	}

	public void reset(Collection<ID> ids) {
		for (ID id : ids) {
			this.attributes.get(id).reset();
		}
	}

	public Double getValue(ID id) throws NoSuchValueException {
		if (this.attributes.containsKey(id)) {
			return this.attributes.get(id).getValue();
		} else {
			throw new NoSuchValueException(id);
		}
	}

	public Double setValue(ID identifier, double value) {
		Attribute<ID> old = this.attributes.put(identifier, new Attribute<ID>(
				identifier, value));
		if (old != null) {
			return old.getValue();
		} else {
			return null;
		}
	}

	public Collection<Attribute<ID>> getNew() {
		this.refresh();
		Collection<Attribute<ID>> result = new LinkedList<Attribute<ID>>();
		for (Attribute<ID> attribute : this.attributes.values()) {
			if (attribute.isDirty()) {
				result.add(attribute);
				attribute.setDirty(false);
			}
		}
		// log.warn(result.toString());
		return result;
	}

	private void refresh() {
		for (Attribute<ID> att : this.factory.getAtts()) {
			if (this.attributes.containsKey(att.getIdentifier())) {
				Attribute<ID> old = this.attributes.get(att.getIdentifier());
				old.refresh(att);
			} else {
				this.attributes.put(att.getIdentifier(), att);
			}
		}
	}

	public int getNumberOfAttributes() {
		return attributes.size();
	}
}
