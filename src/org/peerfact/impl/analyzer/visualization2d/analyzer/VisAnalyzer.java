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

package org.peerfact.impl.analyzer.visualization2d.analyzer;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.peerfact.api.analyzer.ConnectivityAnalyzer;
import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.analyzer.OperationAnalyzer;
import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.Operation;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.network.NetPosition;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.impl.analyzer.visualization2d.analyzer.netPosTransformers.GeographicalPositionTransformer;
import org.peerfact.impl.analyzer.visualization2d.analyzer.netPosTransformers.GnpPositionTransformer;
import org.peerfact.impl.analyzer.visualization2d.analyzer.netPosTransformers.NewGnpPositionTransformer;
import org.peerfact.impl.analyzer.visualization2d.analyzer.netPosTransformers.SimpleEuclidianPointTransformer;
import org.peerfact.impl.analyzer.visualization2d.analyzer.netPosTransformers.TorusPositionTransformer;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.MultiPositioner;
import org.peerfact.impl.analyzer.visualization2d.analyzer.positioners.multi.TakeFirstPositioner;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.Coords;
import org.peerfact.impl.analyzer.visualization2d.util.visualgraph.PositionInfo;
import org.peerfact.impl.network.gnp.topology.GeographicPosition;
import org.peerfact.impl.network.gnp.topology.GnpPosition;
import org.peerfact.impl.network.modular.st.positioning.GNPPositioning.GNPPosition;
import org.peerfact.impl.network.modular.st.positioning.TorusPositioning.TorusPosition;
import org.peerfact.impl.network.simple.SimpleEuclidianPoint;
import org.peerfact.impl.transport.AbstractTransMessage;
import org.peerfact.impl.util.oracle.GlobalOracle;


/**
 * The analyzer for the visualization interface.
 * 
 * See documentation on <http://www.peerfact.org>.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @edit Leo Nobach
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 05/06/2011
 */
public class VisAnalyzer implements OperationAnalyzer, TransAnalyzer,
		NetAnalyzer, ConnectivityAnalyzer {

	/**
	 * Translator which cares about the passing of information to the
	 * visualization
	 */
	private Translator translator;

	/**
	 * List of currently existing hosts in the scenario
	 */
	private final Map<NetID, Host> hosts = new LinkedHashMap<NetID, Host>();

	/**
	 * the buffer to determine transmitter of an AbstractTransMessage
	 */
	private final LinkedHashMap<AbstractTransMessage, NetID> firstSender = new LinkedHashMap<AbstractTransMessage, NetID>();

	/**
	 * buffer to determine final receiver of an AbstractTransMessage
	 */
	private final LinkedHashMap<AbstractTransMessage, NetID> lastReceiver = new LinkedHashMap<AbstractTransMessage, NetID>();

	/**
	 * Flag read from the config to decide if a fixed bound according to the
	 * dimension of a image should be used or if the bound is determined
	 * dynamical.
	 */
	private final boolean useFixedBound = Boolean.parseBoolean(Config.getValue(
			"UI/BackgroundImageEnabled", "false"));

	// -----------------------VisAnalyzer2---------------------------

	protected List<OverlayAdapter> loadedOLAdapters = new LinkedList<OverlayAdapter>();

	boolean messageEdges = false;

	private MultiPositioner rootPositioner = null;

	// --------------------------------------------------------------

	/**
	 * Returns the instance of the used Translators
	 */
	public Translator getTranslator() {
		return translator;
	}

	private boolean hostIsNew(Host host) {
		return host != null && !hosts.values().contains(host);
	}

	private void checkHost(Host host) {
		if (hostIsNew(host)) {
			hosts.put(host.getNetLayer().getNetID(), host);
			foundNewHost(host);
		}
	}

	public void checkHost(NetID id) {
		if (!hosts.containsKey(id)) {
			Host newHost = GlobalOracle.getHostForNetID(id);
			if (newHost != null) {
				hosts.put(id, newHost);
				foundNewHost(newHost);
			}
		}
	}

	@Override
	public void netMsgSend(NetMessage message, NetID id) {
		checkHost(id);

		AbstractTransMessage tmsg;
		Message payload = message.getPayload();
		if (payload instanceof AbstractTransMessage) {
			tmsg = (AbstractTransMessage) payload;

			if (!lastReceiver.containsKey(tmsg)) {
				firstSender.put(tmsg, message.getSender());
				lastReceiver.put(tmsg, message.getReceiver());
			} else {
				lastReceiver.remove(tmsg);
				lastReceiver.put(tmsg, message.getReceiver());
			}
		}
	}

	@Override
	public void netMsgDrop(NetMessage message, NetID id) {
		// Nothing to do
	}

	@Override
	public void netMsgReceive(NetMessage message, NetID id) {
		// Nothing to do
	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {
		overlayMsgOccured(msg.getPayload(), firstSender.get(msg),
				lastReceiver.get(msg));

		// Hash-Map cleanup
		firstSender.remove(msg);
		lastReceiver.remove(msg);
	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		// Nothing to do
	}

	@Override
	public void start() {
		translator = new Translator();
		getTranslator();
		Translator.setUpperBoundForCoordinates(1.0f, 1.0f);
		preparePositioners();
	}

	@Override
	public void stop(Writer output) {
		Translator.notifyFinished();
	}

	@Override
	public void operationFinished(Operation<?> op) {

		handleOperation(op.getComponent().getHost(), op, true);

	}

	@Override
	public void operationInitiated(Operation<?> op) {
		Host host = op.getComponent().getHost();
		checkHost(host);

		handleOperation(host, op, false);
	}

	/**
	 * 
	 * let an operation treated by Overlay-Adapters
	 * 
	 * @param host
	 * @param op
	 */
	private void handleOperation(Host host, Operation<?> op, boolean finished) {
		checkHost(host);

		for (OverlayAdapter adapter : loadedOLAdapters) {

			Iterator<OverlayNode<?, ?>> it = host.getOverlays();

			while (it.hasNext()) {
				it.next();
				if (adapter.isDedicatedOverlayImplFor(op.getComponent()
						.getClass())) {
					adapter.handleOperation(host, op, finished);
				}
			}
		}
	}

	@Override
	public void offlineEvent(Host host) {
		foundLeavingHost(host);
		hosts.remove(host.getNetLayer().getNetID());
	}

	@Override
	public void onlineEvent(Host host) {
		checkHost(host);
	}

	/**
	 * This method is called when a new host has been discovered in the scenario
	 * 
	 * @param host
	 */
	public void foundNewHost(Host host) {

		// determine NetPosition of the host
		NetPosition netPos = host.getProperties().getNetPosition();

		Coords schem_coords = rootPositioner.getSchematicHostPosition(host);
		// log.debug("Schematische Koordinaten für " + host + ": "+
		// schem_coords);

		// set attribute
		Map<String, Serializable> attributes = new LinkedHashMap<String, Serializable>();

		// insert all Overlay-classnames, the host owns
		Iterator<OverlayNode<?, ?>> iter = host.getOverlays();
		while (iter.hasNext()) {
			addOverlayToAttributes(attributes, iter.next().getClass()
					.getSimpleName(), "overlay_raw");
		}

		// very responsible Overlay-Adapter checks BEFORE creating the node
		for (OverlayAdapter adapter : loadedOLAdapters) {

			Iterator<OverlayNode<?, ?>> it = host.getOverlays();
			while (it.hasNext()) {
				OverlayNode<?, ?> overlayNode = it.next();

				if (adapter.isDedicatedOverlayImplFor(overlayNode.getClass())) {

					addOverlayToAttributes(attributes,
							adapter.getOverlayName(), "overlay");
					adapter.handleNewHost(attributes, host, overlayNode);
					// -> the first OverlayAdapter, which supports assignment of
					// a schematic position of a node, allocates the position to
					// the node.
				}
			}
		}

		PositionInfo pos = new PositionInfo(transformPosition(netPos),
				schem_coords);

		// inform distributor about new node
		getTranslator().overlayNodeAdded(host.getNetLayer().getNetID(),
				host.getProperties().getGroupID(), pos, attributes);

		// every responsible Overlay-Adapter checks AFTER creating the node

		for (OverlayAdapter adapter : loadedOLAdapters) {

			Iterator<OverlayNode<?, ?>> it = host.getOverlays();

			while (it.hasNext()) {
				OverlayNode<?, ?> overlayNode = it.next();

				if (adapter.isDedicatedOverlayImplFor(overlayNode.getClass())) {
					adapter.handleNewHostAfter(host, overlayNode);
				}
			}
		}
	}

	/**
	 * Adds another overlay with a given name in attributes.
	 * 
	 * @param attributes
	 * @param name
	 */
	public static void addOverlayToAttributes(
			Map<String, Serializable> attributes,
			String name, String key) {
		List<Serializable> overlays = (List<Serializable>) attributes.get(key);

		if (overlays == null) {
			overlays = new LinkedList<Serializable>();
			attributes.put(key, (Serializable) overlays);
		}

		overlays.add(name);
	}

	/**
	 * This method is called when it is discovered that a host leaves the
	 * scenario
	 * 
	 * @param host
	 */
	public void foundLeavingHost(Host host) {
		// Every responsible Overlay-Adapter checks everything before the
		// disappearance of a node.

		Iterator<OverlayNode<?, ?>> it = host.getOverlays();
		while (it.hasNext()) {
			OverlayNode<?, ?> overlayNode = it.next();

			for (OverlayAdapter adapter : loadedOLAdapters) {

				if (adapter.isDedicatedOverlayImplFor(overlayNode.getClass())) {
					adapter.handleLeavingHost(host);
				}
			}
		}

		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	/**
	 * This method is called when an Overlay-Message is sent before it is
	 * visualized.
	 * 
	 * @param omsg
	 * @param from
	 * @param to
	 */
	public void overlayMsgOccured(Message omsg, NetID from, NetID to) {
		// Every responsible Overlay-Adapter checks everything before the
		// disappearance of a node.

		for (OverlayAdapter adapter : loadedOLAdapters) {

			if (adapter.isDedicatedOverlayImplFor(omsg.getClass())) {

				Host fromHost = hosts.get(from);
				Host toHost = hosts.get(to);

				if (fromHost == null) {
					System.err
							.println("Warning: netMsgSend: Corresponding (from-) Host for NetID "
									+ from + " is null.");
				}
				if (toHost == null) {
					System.err
							.println("Warning: netMsgSend: Corresponding (to-) Host for NetID "
									+ to + " is null.");
				}

				adapter.handleOverlayMsg(omsg, fromHost, from, toHost, to);
			}
		}

		// a Flash-Overlay-edge is drawn, view to sending a message.

		if (messageEdges) {
			Map<String, Serializable> attributes = new LinkedHashMap<String, Serializable>();
			attributes.put("type", omsg.getClass().getSimpleName());
			attributes.put("msg_class", omsg.getClass().getSimpleName());
			translator.overlayEdgeFlash(from, to, Color.RED, attributes);
		}

		// notifys Distributor that message has been sent
		getTranslator()
				.overlayMessageSent(from, to, omsg.getClass().toString());
	}

	/**
	 * Converts a given NetPosition by distinguishing the specific
	 * implementation in Coords needed to visualize the positioning of the
	 * nodes.
	 * 
	 * @param netPos
	 * @return the converted NetPosition
	 */
	public Coords transformPosition(NetPosition netPos) {

		Coords coords = null;

		if (netPos != null) {

			// Is it a SimpleEuclidianPoint?
			if (netPos instanceof SimpleEuclidianPoint) {
				coords = new SimpleEuclidianPointTransformer()
						.transform((SimpleEuclidianPoint) netPos);

				// Is it a GnpPosition?
			} else if (netPos instanceof GnpPosition) {
				coords = new GnpPositionTransformer()
						.transform((GnpPosition) netPos);

				// Is it a GeographicPosition?
			} else if (netPos instanceof GeographicPosition) {
				coords = new GeographicalPositionTransformer()
						.transform((GeographicPosition) netPos);

				// Is it a GNPPosition (Modular Net Layer)?
			} else if (netPos instanceof GNPPosition) {
				coords = new NewGnpPositionTransformer()
						.transform((GNPPosition) netPos);

				// Is it a TorusPosition (Modular Net Layer)?
			} else if (netPos instanceof TorusPosition) {
				coords = new TorusPositionTransformer()
						.transform((TorusPosition) netPos);

				// Is the type not supported?
			} else {
				coords = new Coords(0, 0);
				System.err
						.println(this.getClass().getName()
								+ " - There is no transformer for the given NetPosition-type.");
			}

			getTranslator();
			// Adjust the lower bound for the visualization
			Coords currentUpperBound = Translator
					.getUpperBoundForCoordinates();
			float currentMaxX = currentUpperBound.x;
			float currentMaxY = currentUpperBound.y;

			// Is one of the coordinates greater than the current bound, the
			// bound is updated.
			if (coords.x > currentMaxX || coords.y > currentMaxY) {
				getTranslator();
				Translator.setUpperBoundForCoordinates(
						Math.max(coords.x, currentMaxX),
						Math.max(coords.y, currentMaxY));
			}

			getTranslator();
			// Adjust the lower bound for the visualization
			Coords currentLowerBound = Translator
					.getLowerBoundForCoordinates();
			float currentMinX = currentLowerBound.x;
			float currentMinY = currentLowerBound.y;

			// Set bound
			if (useFixedBound) {
				// Bound is fixed

				getTranslator();
				// Set lower bound to 0,0
				Translator.setLowerBoundForCoordinates(0, 0);

				// Get last used background image path
				String lastPath = Config.getValue("UI/LastBackgroundImage", "");

				try {
					// Read the image
					Image lastImage = ImageIO.read(new File(lastPath));

					getTranslator();
					// Set upper bound to the image dimensions
					Translator
							.setUpperBoundForCoordinates(
									lastImage.getWidth(null),
									lastImage.getHeight(null));

				} catch (IOException e) {
					getTranslator();
					// If the image could not be read, set the bound to a
					// standard value
					Translator.setUpperBoundForCoordinates(1250, 625);

					System.out
							.println("Could not read dimensions of given background image.");
				}

			} else {
				// Is one of the coordinates smaller than the current bound, the
				// bound is updated.
				if (coords.x < currentMinX || coords.y < currentMinY) {
					getTranslator();
					Translator.setLowerBoundForCoordinates(
							Math.min(coords.x, currentMinX),
							Math.min(coords.y, currentMinY));
				}

			}

		} else {
			System.err
					.println(this.getClass().getName()
							+ " - One Host found which has no NetPosition and thus cannot be visualized.");
		}
		return coords;

	}

	/**
	 * Prepares the positioner.
	 */
	protected void preparePositioners() {
		if (rootPositioner == null) {
			rootPositioner = new TakeFirstPositioner();
		}
		rootPositioner.setAdapters(loadedOLAdapters);
	}

	/**
	 * Settable through XML-Config: Use Overlay-Adapter, which are used to
	 * analyse the tasks. Multiple adapters are possible.
	 * 
	 * Please transmit value as full class name
	 * 
	 * @param overlayClassPaths
	 */
	public void setOverlayAdapter(OverlayAdapter adapter) {
		adapter.setParentAnalyzer(this);
		loadedOLAdapters.add(adapter);
	}

	public void setMultiPositioner(MultiPositioner pos) {
		this.rootPositioner = pos;
	}

	/**
	 * Settable through XML-Config: Displays messages by a flash of the
	 * connections. Recommended if there is no useful Overlay-Adapter for the
	 * scenario.
	 */
	public void setMessageEdges(boolean messageEdges) {
		this.messageEdges = messageEdges;
	}
}
