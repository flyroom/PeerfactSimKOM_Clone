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

package org.peerfact.impl.analyzer.visualization2d.model.overlay;

import javax.swing.ImageIcon;

import org.peerfact.Constants;
import org.peerfact.impl.analyzer.visualization2d.model.ModelIterator;
import org.peerfact.impl.analyzer.visualization2d.model.flashevents.FlashEvent;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.VisualGraph;


/**
 * Overlay-Connection, which theoretically has <b>no permanent</b>, i.e. only
 * once to be seen briefly (so "Flash"), e.g. to represent the sending of
 * messages between nodes. Is not registered into the data model.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class FlashOverlayEdge extends VisOverlayEdge implements FlashEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1794025198513940542L;

	protected static final ImageIcon REPR_ICON1 = new ImageIcon(
			Constants.ICONS_DIR + "/model/OverlayEdgeMsg16_16.png");

	/**
	 * Specifies whether the edge was drawn. Is reset for each run.
	 */
	protected boolean painted = false;

	protected boolean removable = false;

	public FlashOverlayEdge(VisOverlayNode a, VisOverlayNode b) {
		super(a, b);
	}

	/*
	 * public void notifyPainted() { if (painted) { log.debug(this +
	 * ": Edge Removed"); this.remove(); //Beim zweiten Zeichenversuch (der
	 * unterdrückt wird) } //wird die Kante gelöscht. Bis dahin bleibt sie
	 * jedoch //im Graphen um z.B. anklickbar zu sein. else { painted = true;
	 * log.debug(this + ": painted=true"); } }
	 */

	@Override
	public void setGraph(VisualGraph<VisOverlayNode, VisOverlayEdge> g) {
		System.out
				.println("Warning: A FlashOverlayEdge should not be inserted in the data model.");
	}

	@Override
	public void iterate(ModelIterator it) {
		it.flashOverlayEdgeVisited(this);
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON1;
	}
}
