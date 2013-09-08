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

package org.peerfact.impl.network.modular;

import org.peerfact.impl.network.modular.st.FragmentingStrategy;
import org.peerfact.impl.network.modular.st.JitterStrategy;
import org.peerfact.impl.network.modular.st.LatencyStrategy;
import org.peerfact.impl.network.modular.st.PLossStrategy;
import org.peerfact.impl.network.modular.st.PacketSizingStrategy;
import org.peerfact.impl.network.modular.st.PositioningStrategy;
import org.peerfact.impl.network.modular.st.TrafficControlStrategy;

/**
 * The set of strategies used by the current NetLayer
 * 
 * @author <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public interface IStrategies {

	public PacketSizingStrategy getPacketSizingStrategy();

	public TrafficControlStrategy getTrafficControlStrategy();

	public PLossStrategy getPLossStrategy();

	public LatencyStrategy getLatencyStrategy();

	public PositioningStrategy getPositioningStrategy();

	public FragmentingStrategy getFragmentingStrategy();

	public JitterStrategy getJitterStrategy();

}
