/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Operation;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.ContentDistribution;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.common.DefaultHost;
import org.peerfact.impl.common.DefaultHostProperties;
import org.peerfact.impl.network.simple.SimpleNetFactory;
import org.peerfact.impl.network.simple.SimpleStaticLatencyModel;
import org.peerfact.impl.overlay.contentdistribution.ContentDistributionFactory;
import org.peerfact.impl.simengine.SimulatorTest;
import org.peerfact.impl.transport.DefaultTransLayer;


/**
 * Class with a lot of usefull methods.
 * 
 * @author pussep
 * @version 3.0, 29.11.2007
 * 
 */
public abstract class ComponentTest extends SimulatorTest {

	private ComponentFactory netFactory;

	/**
	 * Ids of failed operations with error descriptions
	 */
	protected List<Integer> failedOperations;

	/**
	 * Ids of successful operation with results.
	 */
	protected Map<Integer, Object> results;

	protected List<Integer> processedOpIds;

	private ComponentFactory dsFactory;

	protected NetLayer createNetworkWrapper(Host host) {
		if (netFactory == null) {
			this.netFactory = new SimpleNetFactory();
			((SimpleNetFactory) this.netFactory)
					.setLatencyModel(new SimpleStaticLatencyModel(10l));
		}
		NetLayer wrapper = (NetLayer) netFactory.createComponent(host);
		((DefaultHost) host).setNetwork(wrapper);
		return wrapper;
	}

	protected static TransLayer createTransLayer(DefaultHost host) {
		TransLayer transLayer = new DefaultTransLayer(host.getNetLayer());
		host.setTransport(transLayer);
		return transLayer;
	}

	protected OperationCallback getOperationCallback() {
		return new OperationCallback() {
			@Override
			public void calledOperationSucceeded(Operation op) {
				log.debug("operation finished with success "
						+ op.getOperationID());
				processedOpIds.add(op.getOperationID());
				results.put(op.getOperationID(), op.getResult());
			}

			@Override
			public void calledOperationFailed(Operation op) {
				log.debug("operation finished with failure "
						+ op.getOperationID());
				processedOpIds.add(op.getOperationID());
				failedOperations.add(op.getOperationID());
			}

			@Override
			public String toString() {
				return "Operation Callback TestStub";
			}
		};
	}

	public ComponentTest() {
		failedOperations = new LinkedList<Integer>();
		results = new LinkedHashMap<Integer, Object>();
		processedOpIds = new LinkedList<Integer>();
	}

	@Override
	public void setUp() {
		super.setUp();
	}

	protected static DefaultHost createEmptyHost() {
		DefaultHost host = new DefaultHost();
		host.setProperties(new DefaultHostProperties());
		return host;
	}

	protected static void createHostProperties(DefaultHost host) {
		DefaultHostProperties hostProperties = new DefaultHostProperties();
		host.setProperties(hostProperties);
	}

	protected ContentDistribution<OverlayKey<?>> createDistributionStrategy(
			Host host) {
		if (dsFactory == null) {
			dsFactory = new ContentDistributionFactory();
		}
		ContentDistribution<OverlayKey<?>> ds = (ContentDistribution<OverlayKey<?>>) dsFactory
				.createComponent(host);
		((DefaultHost) host).setOverlayNode(ds);
		return ds;
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		failedOperations.clear();
		results.clear();
		processedOpIds.clear();
	}

}
