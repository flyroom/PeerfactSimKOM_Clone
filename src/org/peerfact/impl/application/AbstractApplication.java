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

package org.peerfact.impl.application;

import org.apache.log4j.Logger;
import org.peerfact.api.application.Application;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.common.Operations;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Abstract implementation of an application, which can be used for subclassing.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 */
public abstract class AbstractApplication implements Application {

	public static final Logger log = SimLogger
			.getLogger(AbstractApplication.class);

	private Host host;

	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public int close(OperationCallback<?> callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

	@Override
	public int start(OperationCallback<?> callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

}
