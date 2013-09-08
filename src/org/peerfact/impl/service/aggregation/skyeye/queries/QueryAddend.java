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

package org.peerfact.impl.service.aggregation.skyeye.queries;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;


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
 * This class represents and defines a single clause of a query.
 * <code>QueryAddend</code> consists of several {@link QueryCondition}s and
 * defines the amount of peers, which must fulfill the defined conditions. The
 * IDs of the peers, that fulfill the specified conditions, are stored within
 * this class. Besides the functionality in terms of the resolution of a
 * <code>QueryAddend</code>, this class also keeps track of the peers, which
 * added some matches to the list of searched peers, and stores the ID of the
 * solving peer as well as of its level.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class QueryAddend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1722595932591839218L;

	private Vector<QueryCondition<?>> conditions;

	private int searchedElements;

	private Vector<SkyNetNodeInfo> matches;

	private LinkedHashMap<BigDecimal, QueryReplyingPeer> replyingPeers;

	private SkyNetNodeInfo solvingPeer;

	private int solvingLevel;

	public QueryAddend(int searchedElements) {
		this.conditions = new Vector<QueryCondition<?>>();
		this.searchedElements = searchedElements;
		this.matches = new Vector<SkyNetNodeInfo>();
		this.replyingPeers = new LinkedHashMap<BigDecimal, QueryReplyingPeer>();
	}

	/**
	 * This method returns the number of conditions, of which this clause
	 * consists.
	 * 
	 * @return number of conditions
	 */
	public int getNumberOfConditions() {
		return conditions.size();
	}

	/**
	 * This method adds a further condition to the set of conditions of this
	 * clause.
	 * 
	 * @param condition
	 *            contains the additional condition
	 */
	public void addCondition(QueryCondition<?> condition) {
		conditions.add(condition);
	}

	/**
	 * This method returns a condition of a clause, which is specified by the
	 * provided parameter.
	 * 
	 * @param index
	 *            contains the index of the condition, which shall be returned
	 * @return the specified condition or <code>null</code>, if the index
	 *         exceeds the amount of conditions
	 */
	public QueryCondition<?> getCondition(int index) {
		return conditions.get(index);
	}

	/**
	 * This method returns the amount of peers, that must fulfill the defined
	 * conditions of the clause. During the resolution of a query, the amount of
	 * searched peers is decreased by the number of found matches. So if all
	 * peers, which must fulfill the defined conditions, are found, the method
	 * returns 0, as no further peers needs to be searched.
	 * 
	 * @return the current amount of searched peers, which shall yet fulfill the
	 *         defined conditions
	 */
	public int getSearchedElements() {
		return searchedElements;
	}

	/**
	 * If a peer, that matches the defined conditions, is added to the list of
	 * the searched peers, this method is responsible for decrementing the
	 * amount of searched peers by one. The current amount of searched peers can
	 * be obtained by calling <code>getSearchedElements()</code>.
	 */
	public void decrementSearchedElements() {
		searchedElements--;
	}

	/**
	 * This method returns the list of peers, that match the defined conditions
	 * of this clause.
	 * 
	 * @return a <code>Vector</code> of IDs from the matching peers
	 */
	public Vector<SkyNetNodeInfo> getMatches() {
		return matches;
	}

	/**
	 * This method adds the ID of a matching peer to the list of all matching
	 * peers.
	 * 
	 * @param match
	 *            contains the ID of the peer, that matches the defined
	 *            conditions
	 */
	public void addMatch(SkyNetNodeInfo match) {
		matches.add(match);
	}

	/**
	 * This method returns the list of peers, which helped to solve the query
	 * and provided the query with the IDs of the matching peers. Therefore, the
	 * list contains the IDs of the traversed peers by the query as well as
	 * their amount of provided matches.
	 * 
	 * @return the list of peers, which helped to solve the query
	 */
	public LinkedHashMap<BigDecimal, QueryReplyingPeer> getReplyingPeers() {
		return replyingPeers;
	}

	/**
	 * This method stores the ID of a traversed peer by the query and
	 * additionally specifies how many matches were added, at which level the
	 * added peer is situated in the tree and if the peer could solve this
	 * clause.
	 * 
	 * @param nodeInfo
	 *            contains the ID of the traversed peer
	 * @param level
	 *            contains the level of the traversed peer
	 * @param replies
	 *            contains the amount of matches, which were added by the peer
	 * @param querySolved
	 *            specifies if the traversed peer could solve this clause
	 */
	public void addReplyingPeer(SkyNetNodeInfo nodeInfo, int level,
			int replies, boolean querySolved) {
		replyingPeers.put(nodeInfo.getSkyNetID().getID(),
				new QueryReplyingPeer(nodeInfo.getSkyNetID(), level, replies,
						querySolved));
		if (querySolved) {
			solvingPeer = nodeInfo;
			solvingLevel = level;
		}
	}

	/**
	 * This method returns the ID of the peer, that finally solved this clause
	 * an thereby the whole query.
	 * 
	 * @return the <code>SkyNetNodeInfo</code>-object of the peer, that solved
	 *         the query
	 */
	public SkyNetNodeInfo getSolvingPeer() {
		return solvingPeer;
	}

	/**
	 * This method returns the level of the peer, that solved the clause.
	 * 
	 * @return the level of the solving peer
	 */
	public int getSolvingLevel() {
		return solvingLevel;
	}

	/**
	 * This method calculates and returns the size of this clause.
	 * 
	 * @return the size of this clause
	 */
	public long getSize() {
		return SkyNetConstants.SKY_NET_NODE_INFO_SIZE
				+ replyingPeers.size()
				* (SkyNetConstants.OVERLAY_ID_SIZE + SkyNetConstants.QUERY_REPLYING_PEER_SIZE)
				+ matches.size() * SkyNetConstants.SKY_NET_NODE_INFO_SIZE
				+ Constants.INT_SIZE + conditions.size()
				* SkyNetConstants.QUERY_CONDITION_SIZE_ESTIMATE;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(searchedElements + " with ");
		for (int i = 0; i < conditions.size(); i++) {
			buf.append(conditions.get(i).toString());
			if (i < conditions.size() - 1) {
				buf.append(" , ");
			}
		}
		return buf.toString();
	}

}
