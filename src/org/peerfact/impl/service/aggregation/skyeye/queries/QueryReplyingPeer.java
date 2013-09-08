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

import org.peerfact.impl.service.aggregation.skyeye.SkyNetID;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing.QueryPostProcessor;


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
 * This class defines the representation of a peer, which helps to solve a
 * query. In terms of queries and their performance,
 * <code>QueryReplyingPeer</code> is required by {@link QueryPostProcessor} for
 * the post-processing of the performance of queries within SkyNet.<br>
 * Concerning its utilization during a simulation, every {@link QueryAddend} of
 * a {@link Query}-object keeps track of the traversed peers during a resolution
 * and stores the corresponding information by creating and storing such a
 * <code>QueryReplyingPeer</code>-object for every traversed peer.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 08.12.2008
 * 
 */
public class QueryReplyingPeer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4316074149962790822L;

	private SkyNetID id;

	private int level;

	private int numberOfReplies;

	private boolean querySolver;

	public QueryReplyingPeer(SkyNetID id, int level, int numberOfReplies,
			boolean querySolver) {
		this.id = id;
		this.level = level;
		this.numberOfReplies = numberOfReplies;
		this.querySolver = querySolver;
	}

	/**
	 * This method returns the ID of the traversed node during a resolution of a
	 * query.
	 * 
	 * @return the <code>SkyNetID</code> of the traversed node
	 */
	public SkyNetID getNodeInfo() {
		return id;
	}

	/**
	 * This method returns the level of the node in the SkyNet-tree during a
	 * resolution of a query.
	 * 
	 * @return the level of the traversed node
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * This method returns the amount of matches, that the traversed node added
	 * to the query.
	 * 
	 * @return the amount of added matches of a traversed node
	 */
	public int getNumberOfReplies() {
		return numberOfReplies;
	}

	/**
	 * This method specifies, if a traversed node constitutes the resolver of a
	 * query.
	 * 
	 * @return <code>true</code>, if the represented node could solve the query,
	 *         <code>false</code> otherwise
	 */
	public boolean isQuerySolver() {
		return querySolver;
	}

}
