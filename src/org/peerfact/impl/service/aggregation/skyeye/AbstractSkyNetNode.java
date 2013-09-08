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

package org.peerfact.impl.service.aggregation.skyeye;

import org.peerfact.api.common.ConnectivityListener;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.overlay.kbr.KBRForwardInformation;
import org.peerfact.api.overlay.kbr.KBRListener;
import org.peerfact.api.overlay.kbr.KBRNode;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.api.service.skyeye.SkyNetNodeInterface;
import org.peerfact.api.service.skyeye.SupportPeer;
import org.peerfact.api.service.skyeye.overlay2SkyNet.MetricsCollectorDelegator;
import org.peerfact.api.service.skyeye.overlay2SkyNet.TreeHandlerDelegator;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.writers.AttributeWriter;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeInputStrategy;
import org.peerfact.impl.service.aggregation.skyeye.attributes.AttributeUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.attributes.SPAttributeInputStrategy;
import org.peerfact.impl.service.aggregation.skyeye.attributes.SPAttributeUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.components.MessageCounter;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetMessageHandler;
import org.peerfact.impl.service.aggregation.skyeye.components.SkyNetNode;
import org.peerfact.impl.service.aggregation.skyeye.components.TreeHandler;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricInputStrategy;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricUpdateStrategy;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsInterpretation;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryHandler;
import org.peerfact.impl.service.aggregation.skyeye.queries.SPQueryHandler;

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
 * This abstract class implements all methods from the interfaces, of which a
 * SkyNet-node consists (except the <code>SimulationEventHandler</code>
 * -interface) and which are utilized by a SkyNet-node to address its several
 * components and by a the corresponding host to enable the communication
 * between its different layers. By putting the listed methods in this abstract
 * class, it relieves the {@link SkyNetNode} of implementing all accessing
 * methods. Instead, <code>SkyNetNode</code> is used to implement the
 * communication and interaction with other SkyNet-nodes.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public abstract class AbstractSkyNetNode implements SkyNetNodeInterface,
		SupportPeer, ConnectivityListener,
		KBRListener<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> {

	// Object-references for common usage
	private SkyNetMessageHandler messageHandler;

	private MessageCounter skyNetMsgCounter;

	private TreeHandler treeHandler;

	private TransLayer transLayer;

	private OverlayNode<?, ?> overlayNode;

	private Host host;

	// Object-references for metrics
	private MetricInputStrategy metricInput;

	private MetricUpdateStrategy metricUpdate;

	private MetricsInterpretation metricsInterpretation;

	// Object-references for attributes
	private AttributeInputStrategy attributeInput;

	private AttributeUpdateStrategy attributeUpdate;

	private QueryHandler queryHandler;

	// Object-references for sp-attributes
	private SPAttributeInputStrategy spAttributeInput;

	private SPAttributeUpdateStrategy spAttributeUpdate;

	private SPQueryHandler spQueryHandler;

	// Variables and object-references for information
	private boolean isSupportPeer;

	private short port;

	private SkyNetNodeInfo nodeInfo;

	public AbstractSkyNetNode(SkyNetNodeInfo nodeInfo, short port,
			TransLayer transLayer, OverlayNode<?, ?> overlayNode,
			TreeHandlerDelegator treeHandlerDelegator,
			MetricsCollectorDelegator metricsCollectorDelegator) {
		this.overlayNode = overlayNode;
		if (this.overlayNode instanceof KBRNode) {
			((KBRNode<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>>) this.overlayNode)
					.setKBRListener(this);
		}
		this.messageHandler = new SkyNetMessageHandler(this, this);
		this.skyNetMsgCounter = new MessageCounter();
		this.nodeInfo = nodeInfo;
		this.port = port;
		this.transLayer = transLayer;
		this.transLayer.addTransMsgListener(messageHandler, getPort());
		metricUpdate = new MetricUpdateStrategy(this);
		metricInput = new MetricInputStrategy(this, metricsCollectorDelegator,
				metricUpdate.getStorage());
		metricsInterpretation = new MetricsInterpretation(this,
				metricUpdate.getStorage());
		treeHandler = new TreeHandler(this, treeHandlerDelegator);

		// Object-references for attributes
		attributeUpdate = new AttributeUpdateStrategy(this, this);
		attributeInput = new AttributeInputStrategy(this,
				attributeUpdate.getStorage());
		queryHandler = new QueryHandler(this, attributeUpdate.getStorage());

		// Object-references for sp-attributes
		spAttributeUpdate = new SPAttributeUpdateStrategy(this,
				attributeUpdate.getStorage());
		spAttributeInput = new SPAttributeInputStrategy(this,
				attributeUpdate.getStorage());
		spQueryHandler = new SPQueryHandler(this, attributeUpdate.getStorage());
	}

	// ---------------------------------------------------------
	// Getter and Setter-Methods of the variables listed above
	// ---------------------------------------------------------

	// methods for the object-references for common usage
	@Override
	public SkyNetMessageHandler getSkyNetMessageHandler() {
		return messageHandler;
	}

	@Override
	public MessageCounter getMessageCounter() {
		return skyNetMsgCounter;
	}

	@Override
	public TreeHandler getTreeHandler() {
		return treeHandler;
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	@Override
	public OverlayNode<?, ?> getOverlayNode() {
		return overlayNode;
	}

	@Override
	public Host getHost() {
		return host;
	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	// methods for the variables and object-references for information
	@Override
	public SkyNetNodeInfo getSkyNetNodeInfo() {
		return nodeInfo;
	}

	@Override
	public void setSkyNetNodeInfo(SkyNetNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}

	@Override
	public void setSupportPeer(boolean flag) {
		this.isSupportPeer = flag;
		if (flag) {
			AttributeWriter.getInstance().incrementAmountOfSupportPeers();
		} else {
			AttributeWriter.getInstance().decrementAmountOfSupportPeers();
		}
	}

	@Override
	public boolean isSupportPeer() {
		return isSupportPeer;
	}

	@Override
	public short getPort() {
		return port;
	}

	@Override
	public OverlayID<?> getOverlayID() {
		return getSkyNetNodeInfo().getSkyNetID();
	}

	// Object-references for metrics
	@Override
	public MetricInputStrategy getMetricInputStrategy() {
		return metricInput;
	}

	@Override
	public MetricUpdateStrategy getMetricUpdateStrategy() {
		return metricUpdate;
	}

	// Object-references for attributes
	@Override
	public AttributeInputStrategy getAttributeInputStrategy() {
		return attributeInput;
	}

	@Override
	public AttributeUpdateStrategy getAttributeUpdateStrategy() {
		return attributeUpdate;
	}

	@Override
	public QueryHandler getQueryHandler() {
		return queryHandler;
	}

	// Object-references for sp-attributes
	@Override
	public SPAttributeInputStrategy getSPAttributeInputStrategy() {
		return spAttributeInput;
	}

	@Override
	public SPAttributeUpdateStrategy getSPAttributeUpdateStrategy() {
		return spAttributeUpdate;
	}

	@Override
	public SPQueryHandler getSPQueryHandler() {
		return spQueryHandler;
	}

	@Override
	public MetricsInterpretation getMetricsInterpretation() {
		return metricsInterpretation;
	}

	@Override
	public boolean isPresent() {
		return overlayNode.isPresent();
	}

	// -----------------------------------------------------
	// Methods, which are not needed
	// -----------------------------------------------------

	@Override
	public void deliver(OverlayKey<?> key, Message msg) {
		// not needed yet
	}

	@Override
	public void forward(
			KBRForwardInformation<OverlayID<?>, OverlayContact<OverlayID<?>>, OverlayKey<?>> information) {
		// not needed yet
	}

	@Override
	public void update(OverlayContact contact, boolean joined) {
		// not needed yet
	}

}
