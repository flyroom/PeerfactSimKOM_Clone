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
package org.peerfact.impl.overlay.unstructured.heterogeneous.gia;

import org.peerfact.api.common.LocalClock;

/**
 * An implementation of a token bucket that can be used for flow control. Lazy
 * filling allows the algorithm to work without any additional operations.
 * 
 * If the tokenAllocationRate is <= 0, no tokens are added to the token bucket
 * at all over time. Else, if the tokenAllocationRate is > 0, it represents the
 * time interval between two tokens that are added to the bucket.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class TokenBucket {

	private LocalClock clock;

	private int tokens;

	private long lastTokenAllocated;

	private long tokenAllocationRate;

	private int maxTokens;

	public TokenBucket(LocalClock clock, int initialTokens, int maxTokens,
			long tokenAllocationRate) {
		this.clock = clock;
		this.tokens = initialTokens;
		this.lastTokenAllocated = clock.getCurrentLocalTime();
		this.tokenAllocationRate = tokenAllocationRate;
		this.maxTokens = maxTokens;
	}

	@Override
	public String toString() {
		return "TBucket(tokens=" + tokens + ", lastAlloc=" + lastTokenAllocated
				+ ", tar=" + tokenAllocationRate + ")";
	}

	/**
	 * Returns the number of tokens that are currently in the token bucket.
	 * 
	 * @return
	 */
	public int getTokenCount() {
		if (tokenAllocationRate <= 0) {
			return tokens;
		}
		long localTime = clock.getCurrentLocalTime();
		long duration = localTime - lastTokenAllocated;
		int addTokens = (int) (duration / tokenAllocationRate);
		lastTokenAllocated += addTokens * tokenAllocationRate;

		if ((tokens += addTokens) > maxTokens) {
			tokens = maxTokens;
		}
		return tokens;

	}

	/**
	 * Sets a new allocation rate this bucket will use.
	 * 
	 * * If the tokenAllocationRate is <= 0, no tokens are added to the token
	 * bucket at all over time. Else, if the tokenAllocationRate is > 0, it
	 * represents the time interval between two tokens that are added to the
	 * bucket.
	 * 
	 * @param tokenAllocationRate
	 */
	public void setAllocationRate(long tokenAllocationRate) {
		getTokenCount();
		this.tokenAllocationRate = tokenAllocationRate;
	}

	/**
	 * Returns whether this bucket holds any tokens.
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return getTokenCount() > 0;
	}

	/**
	 * Takes a token from this bucket. Returns false, if the bucket is empty,
	 * otherwise returns true.
	 * 
	 * @return
	 */
	public boolean takeToken() {
		tokens = getTokenCount();
		if (tokens >= 1) {
			tokens--;
			return true;
		}
		return false;
	}

	/**
	 * Returns the time when the next token will be allocated to this bucket.
	 * Useful if e.g. an application waits for the next token that gets
	 * available.
	 * 
	 * @return
	 */
	public long getTimeToNextTokenAllocation() {
		if (tokenAllocationRate <= 0) {
			return Long.MAX_VALUE;
		}
		getTokenCount();
		long localTime = clock.getCurrentLocalTime();
		return lastTokenAllocated + tokenAllocationRate - localTime;
	}

}
