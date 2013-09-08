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

package org.peerfact.impl.common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.peerfact.api.application.Application;
import org.peerfact.api.common.Component;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.HostProperties;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.user.User;
import org.peerfact.impl.transport.DefaultTransLayer;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * Default implementation of a host.
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 */
public class DefaultHost implements Host {
	private static final Logger log = SimLogger.getLogger(DefaultHost.class);

	private NetLayer netLayer;

	private List<OverlayNode<?, ?>> overlays;

	private Application application;

	private HostProperties properties;

	private User user;

	private List<Component> components;

	private TransLayer transLayer;

	/**
	 * Create a new and empty host.
	 */
	public DefaultHost() {
		this.overlays = new LinkedList<OverlayNode<?, ?>>();
		this.components = new LinkedList<Component>();
	}

	public void setNetwork(NetLayer nw) {
		components.add(nw);
		this.netLayer = nw;
		this.netLayer.setHost(this);
		nw.addConnectivityListener(properties);
	}

	public void setApplication(Application appl) {
		components.add(appl);
		this.application = appl;
		this.application.setHost(this);
	}

	public void setOverlayNode(OverlayNode<?, ?> node) {
		components.add(node);
		overlays.add(node);
		node.setHost(this);
	}

	public void setTransport(TransLayer transLayer) {
		components.add(transLayer);
		this.transLayer = transLayer;
		this.transLayer.setHost(this);
	}

	@Override
	public TransLayer getTransLayer() {
		if (transLayer == null) {
			log.warn("transport layer is unset. Create default one.");
			this.transLayer = new DefaultTransLayer(getNetLayer());
		}
		return transLayer;
	}

	public void setProperties(HostProperties properties) {
		this.properties = properties;
		this.properties.setHost(this);
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public NetLayer getNetLayer() {
		return netLayer;
	}

	@Override
	public OverlayNode<?, ?> getOverlay(Class<?> api) {
		for (OverlayNode<?, ?> overlay : overlays) {
			if (api.isInstance(overlay)) {
				return overlay;
			}
		}
		return null;
	}

	@Override
	public Iterator<OverlayNode<?, ?>> getOverlays() {
		return overlays.iterator();
	}

	@Override
	public Application getApplication() {
		return application;
	}

	@Override
	public HostProperties getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Host {");
		sb.append("ID=");
		if (this.properties != null) {
			sb.append(this.properties.getId());
		}
		sb.append(", GroupID=");
		if (this.properties != null) {
			sb.append(this.properties.getGroupID());
		}
		sb.append(", nw=");
		if (this.netLayer != null) {
			sb.append(netLayer.getNetID());
		}
		sb.append(", #olays=");
		sb.append(overlays.size());
		sb.append(", appl=");
		sb.append(application);
		sb.append("}");

		return sb.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(
			Class<? extends Component> componentClass) {
		T found = null;
		for (Component component : components) {
			if (componentClass.isInstance(component)) {
				if (found != null) {
					throw new IllegalStateException("Ambiguious request: both "
							+ found + " and " + component
							+ " are instances of " + componentClass);
				}
				found = (T) component;
			}
		}
		assert componentClass.isInstance(found) : "required class="
				+ componentClass + " but found " + found;
		return found;
	}

	/**
	 * Sets a component in a specific layer.
	 * 
	 * @param comp
	 *            the component to set
	 */
	public void setComponent(Component comp) {
		if (comp instanceof NetLayer) {
			setNetwork((NetLayer) comp);
		} else if (comp instanceof TransLayer) {
			setTransport((TransLayer) comp);
		} else if (comp instanceof OverlayNode) {
			setOverlayNode((OverlayNode<?, ?>) comp);
		} else if (comp instanceof Application) {
			setApplication((Application) comp);
		}
	}

}
