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

package org.peerfact.impl.analyzer.visualization2d.model.flashevents;

import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;
import org.peerfact.impl.analyzer.visualization2d.model.TypeObject;

/**
 * Event that has no duration and therefore is infinitely short. It is drawn
 * specifically by the visualization. Examples: Message sending with no fixed
 * time.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 21.10.2008
 * 
 */
public interface FlashEvent extends TypeObject {

	/**
	 * Iterator call. Gets the object suitable for Visitor function.
	 * 
	 * @param it
	 */
	public void iterate(ModelIterator<?, ?, ?> it);

	/**
	 * Is of special importance in FlashEvents. There are never two events drawn
	 * in a frame, which are similar, so the picture is does not "bombarded" by
	 * many FlashEvents.
	 * 
	 * @param e
	 * @return whether there is equality
	 */

	@Override
	public boolean equals(Object o);

	/**
	 * consistent to equals(Object). see
	 * http://www.geocities.com/technofundo/tech/java/equalhash.html
	 * 
	 * @return
	 */

	@Override
	public int hashCode();
}
