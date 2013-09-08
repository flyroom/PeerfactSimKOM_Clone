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

package org.peerfact.impl.application.dhtlookupgenerator;

import org.peerfact.impl.common.AbstractPeriodicOperation.RandomIntervalPeriodicOperation;
import org.peerfact.impl.util.stats.distributions.Distribution;

/**
 * Operation to schedule periodic lookups.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 * @version 01/14/2012
 */
public class PeriodicLookupOperation extends
		RandomIntervalPeriodicOperation<DHTLookupGenerator, Boolean> {

	private DHTLookupGenerator generator;

	private boolean delayed;

	public PeriodicLookupOperation(DHTLookupGenerator generator,
			Distribution intervalDist) {
		super(generator, intervalDist);
		this.generator = generator;
		delayed = true;
	}

	@Override
	protected void executeOnce() {
		if (delayed) {
			delayed = false;
		} else {
			this.generator.startRandomLookup();
		}
	}

	@Override
	public Boolean getResult() {
		return true;
	}

}
