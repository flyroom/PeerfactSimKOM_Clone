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
import java.util.Vector;

import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetConstants;
import org.peerfact.api.service.skyeye.SkyNetNodeInfo;
import org.peerfact.impl.simengine.Simulator;


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
 * This class represents and implements the required functionality of the
 * queries, which are transmitted through the SkyNet-tree. Besides the
 * components of the queries (ID of originator, clauses of the query etc.),
 * <code>Query</code> also contains the additional functionality for the
 * generation of statistics in terms of the queries.<br>
 * The structure of the query is defined by the disjunctive normal form (DNF)
 * and concatenates the clauses of a query (represented by {@link QueryAddend}),
 * which, in turn, consist of several conditions (defined by
 * {@link QueryCondition}).
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class Query implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2227082715046131310L;

	private Vector<QueryAddend> addends;

	private SkyNetNodeInfo queryOriginator;

	private int queryID;

	private int solvedAddend;

	private long timestamp;

	private int hops;

	private float answerQuality;

	private String queryType;

	public Query(SkyNetNodeInfo queryOriginator) {
		this.queryOriginator = queryOriginator;
		addends = new Vector<QueryAddend>();
		solvedAddend = -1;
		timestamp = Simulator.getCurrentTime();
		hops = 0;
		answerQuality = -1;
	}

	/**
	 * This method removes the specified clause from the query and returns it to
	 * the caller.
	 * 
	 * @param index
	 *            contains the index of the clause, which shall be removed
	 * @return the clause, which is specified by the parameter or
	 *         <code>null</code>, if no clause is available for the provided
	 *         index
	 */
	public QueryAddend removeAddend(int index) {
		return addends.remove(index);
	}

	/**
	 * This method just returns the specified clause to the caller, while it
	 * also keeps the clause within the query.
	 * 
	 * @param index
	 *            contains the index of the clause, which shall be returned
	 * @return the clause, which is specified by the parameter or
	 *         <code>null</code>, if no clause is available for the provided
	 *         index
	 */
	public QueryAddend getAddend(int index) {
		return addends.get(index);
	}

	/**
	 * This method inserts the provided clause into the set of already existing
	 * clauses at the position, which is specified by the <code>index</code>
	 * -parameter.
	 * 
	 * @param addend
	 *            contains the clause, which shall be inserted into the query
	 * @param index
	 *            contains the index of insertion
	 */
	public void insertAddend(QueryAddend addend, int index) {
		if (addend.getSearchedElements() == 0) {
			solvedAddend = index;
		}
		addends.insertElementAt(addend, index);
	}

	/**
	 * This method adds the provided clause to the already existing clauses at
	 * the query.
	 * 
	 * @param addend
	 *            contains the clause, which shall be added to the query
	 */
	public void addAddend(QueryAddend addend) {
		addends.add(addend);
		if (addend.getSearchedElements() == 0) {
			solvedAddend = addends.size() - 1;
		}
	}

	/**
	 * This method returns all clauses, of which <code>Query</code> currently
	 * consists.
	 * 
	 * @return a <code>Vector</code>, that contains all clauses of the query
	 */
	public Vector<QueryAddend> getAllAddends() {
		return addends;
	}

	/**
	 * This method stores the provided clauses in <code>Query</code>.
	 * 
	 * @param addends
	 *            contains a <code>Vector</code>, that comprises all clauses,
	 *            which shall be stored in the query
	 */
	public void setAllAddends(Vector<QueryAddend> addends) {
		this.addends = addends;
	}

	/**
	 * This method returns the number of clauses, of which a query currently
	 * consists.
	 * 
	 * @return the number of clauses of a query
	 */
	public int getNumberOfAddends() {
		return addends.size();
	}

	/**
	 * This method returns the originator of the query.
	 * 
	 * @return a <code>SkyNetNodeInfo</code>-object of the originator of the
	 *         query
	 */
	public SkyNetNodeInfo getQueryOriginator() {
		return queryOriginator;
	}

	/**
	 * This method returns the ID of the query.
	 * 
	 * @return the ID of a query, which is represented as an <code>int</code>.
	 */
	public int getQueryID() {
		return queryID;
	}

	/**
	 * This method specifies the ID of a query by the provided parameter.
	 * 
	 * @param queryID
	 *            contains the ID of the query as <code>int</code>
	 */
	public void setQueryID(int queryID) {
		this.queryID = queryID;
	}

	/**
	 * This method returns the index of the clause, whose conditions are
	 * fulfilled and which contains the defined amount of IDs of the searched
	 * peers.
	 * 
	 * @return the index of the fulfilled clause
	 */
	public int getIndexOfSolvedAddend() {
		return solvedAddend;
	}

	/**
	 * This method is responsible for marking the clause of the query, which
	 * depicts the fulfilled clause of a query. The marking-process is realized
	 * by providing and storing the index of the solved clause.
	 * 
	 * @param index
	 *            contains the index of the solved clause
	 */
	private void setSolvedAddend(int index) {
		this.solvedAddend = index;
	}

	/**
	 * This method returns the point in time, when this query was originated.
	 * 
	 * @return the timestamp of this query
	 */
	public long getTimestamp() {
		return timestamp;
	}

	private void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * This method returns the current amount of hops of this query.
	 * 
	 * @return the amount of hops
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * This method increments the amount of hops by one, if the query is sent
	 * from one node to another.
	 */
	public void incrementHops() {
		hops++;
	}

	private void setHops(int hops) {
		this.hops = hops;
	}

	/**
	 * This method returns the quality of the answer of a query, which is
	 * determined by the percentage of present peers in relation to the complete
	 * amount of peers, that are provided by the query.
	 * 
	 * @return the quality of the answer of a query
	 */
	public float getAnswerQuality() {
		return answerQuality;
	}

	/**
	 * This method sets the quality of the answer of a query, which is
	 * determined by the percentage of present peers in relation to the complete
	 * amount of peers, that are provided by the query.
	 * 
	 * @param answerQuality
	 *            contains the quality of the answer of a query
	 */
	public void setAnswerQuality(float answerQuality) {
		this.answerQuality = answerQuality;
	}

	/**
	 * This method returns the type of a query, which is determined in
	 * accordance to the generation of the query (Random, PeerVariation or
	 * ConditionVariation).
	 * 
	 * @return the type of the query as <code>String</code>
	 */
	public String getQueryType() {
		return queryType;
	}

	/**
	 * This method sets the type of a query, which is determined in accordance
	 * to the generation of the query (Random, PeerVariation or
	 * ConditionVariation).
	 * 
	 * @param queryType
	 *            contains the type of a query as <code>String</code>
	 */
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	/**
	 * This method determines and returns the size of the query.
	 * 
	 * @return the size of the query
	 */
	public long getSize() {
		long typeLength = 0;
		if (queryType != null) {
			typeLength = queryType.length();
		}
		long size = SkyNetConstants.SKY_NET_NODE_INFO_SIZE + 3
				* Constants.INT_SIZE + Constants.LONG_SIZE
				+ Constants.FLOAT_SIZE + typeLength
				* Constants.CHAR_SIZE;
		for (int i = 0; i < addends.size(); i++) {
			size = size + addends.get(i).getSize();
		}
		return size;
	}

	@Override
	public Query clone() {
		Query query = new Query(queryOriginator);
		query.setAllAddends(addends);
		query.setQueryID(queryID);
		query.setSolvedAddend(solvedAddend);
		query.setTimestamp(timestamp);
		query.setHops(hops);
		query.setAnswerQuality(answerQuality);
		query.setQueryType(queryType);

		return query;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < addends.size(); i++) {
			buf.append(addends.get(i).toString());
			if (i < addends.size() - 1) {
				buf.append("\n+\n");
			}
		}
		return buf.toString();
	}
}
