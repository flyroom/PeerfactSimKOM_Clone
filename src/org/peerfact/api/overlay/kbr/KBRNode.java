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

/**
 * 
 */
package org.peerfact.api.overlay.kbr;

import java.util.List;

import org.peerfact.api.common.Message;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.transport.TransInfo;


/**
 * The Common API for Downcalls from Paper
 * "Towards a Common API for Structured Peer-to-Peer Overlays"
 * 
 * @author Eser Esen (extended by Julius Rueckert) <peerfact@kom.tu-darmstadt.de>
 * @param <T>
 *            The generic overlay id class
 * @param <S>
 *            the generic overlay contact class
 * 
 * @version 05/06/2011
 */
public interface KBRNode<T extends OverlayID<?>, S extends OverlayContact<T>, K extends OverlayKey<?>>
		extends OverlayNode<T, S> {

	/**
	 * This operation forwards a message <code>msg</code> towards the root of
	 * OverlayKey <code>key</code>. The optional <code>hint</code> argument
	 * specifies a OverlayContact that should be used as a first hop in routing
	 * the message. A good hint, e.g. one that refers to the OverlayKey's
	 * current root, can result in the message being delivered in one hop; a bad
	 * hint adds at most one extra hop to the route. Either <code>key</code> or
	 * hint may be <code>null</code>, but not both. The operation provides a
	 * best-effort service: the message may be lost, duplicated, corrupted, or
	 * delayed indefinitely.
	 * 
	 * @param key
	 *            the specified key
	 * @param msg
	 *            the message to route
	 * @param hint
	 *            the specified hint, which is only used to directly send the
	 *            message to the hint
	 */
	public void route(K key, Message msg, S hint);

	/**
	 * This call produces a list of OverlayContacts that can be used as next
	 * hops on a route towards key key
	 * 
	 * @param key
	 *            the key we want to lookup for
	 * @param num
	 *            the maximum number of entries we want as a result
	 * @return list of OverlayContacts
	 */
	public List<S> local_lookup(K key, int num);

	/**
	 * This operation returns an ordered list of OverlayContacts on which
	 * replicas of the object with key key can be stored. The call returns nodes
	 * with a rank up to and including max rank.
	 * 
	 * @param key
	 *            the key to get a replica set which is near this key
	 * @param maxRank
	 *            the maximum number of replicas in the result set
	 * @return the list of ordered OverlayContacts
	 */
	public List<S> replicaSet(K key, int maxRank);

	/**
	 * This operation produces an unordered list of OverlayContacts that are
	 * neighbors of the local node in the ID space. Up to num OverlayContact are
	 * returned.
	 * 
	 * @param num
	 *            Maximum number of OverlayContatcs returned
	 * @return list of unordered OverlayContacts
	 */
	public List<S> neighborSet(int num);

	/**
	 * This operation computes the range for that the given contact is a r-root.
	 * The range is restricted by the parameter rank.
	 * 
	 * @param contact
	 *            the contact to check the range for
	 * @param rank
	 *            the rank we use as a restriction
	 * @return the bounds of the range as array, null if the range could not be
	 *         determined
	 */
	public T[] range(S contact, int rank);

	/**
	 * This operation checks if the key is the root of a given OverlayKey.
	 * 
	 * @param key
	 * @return true if it is the root for key, false otherwise
	 */
	public boolean isRootOf(K key);

	/**
	 * This operation is used to set the <code>KBRListener</code> (the
	 * application) of the node and should be called inside the constructor of
	 * the application.
	 * 
	 * @param listener
	 */
	public void setKBRListener(KBRListener<T, S, K> listener);

	/**
	 * Creates an instance of a concrete <code>OverlayKey</code> used in an
	 * overlay. This operation is needed to implement applications that are not
	 * aware of the concrete overlay used.
	 * 
	 * @param rank
	 * @return the new <code>OverlayKey</code>
	 */
	public K getNewOverlayKey(int rank);

	/**
	 * Creates an instance of a concrete <code>OverlayKey</code> used in an
	 * overlay. The value is generated randomly from the concrete used id space
	 * of the overlay. This operation is needed to implement applications that
	 * are not aware of the concrete overlay used.
	 * 
	 * @return the new randomly chosen <code>OverlayKey</code>
	 */
	public K getRandomOverlayKey();

	/**
	 * Creates an instance of the concrete local <code>OverlayContact</code>
	 * used in an overlay. This operation is needed to implement applications
	 * that are not aware of the concrete overlay used.
	 * 
	 * @return the local <code>OverlayContact</code>
	 */
	public S getLocalOverlayContact();

	/**
	 * Creates an instance of the concrete local <code>OverlayContact</code>
	 * used in an overlay. This operation is needed to implement applications
	 * that are not aware of the concrete overlay used .
	 * 
	 * @param id
	 * @param transinfo
	 * @return the local <code>OverlayContact</code>
	 */
	public S getOverlayContact(T id, TransInfo transinfo);

	/**
	 * Informs the KBR node about a direct contact to another node. This
	 * information might be important for the overlay to maintain its routing
	 * table. If this is not the case, the implementation of the method can be
	 * left empty.
	 * 
	 * @param contact
	 *            the contact the node had direct contact to
	 */
	public void hadContactTo(S contact);

	/**
	 * Defines an interface to access the <code>KBRLookupProvider</code>. A KBR
	 * node can obtain it when setting the <code>KBRMsgHandler</code>. The
	 * purpose is to avoid the implementation of KBR based lookups for keys over
	 * and over again.
	 * 
	 * We could have solved this in an abstract manner when Java would support
	 * inheritance of multiple classes. Have a look at the existing
	 * implementations of KBR nodes to get an idea how to implement this method.
	 * 
	 * @return the class that allows to perform lookups based on the
	 *         functionality of the KBR layer
	 */
	public KBRLookupProvider<T, S, K> getKbrLookupProvider();
}
