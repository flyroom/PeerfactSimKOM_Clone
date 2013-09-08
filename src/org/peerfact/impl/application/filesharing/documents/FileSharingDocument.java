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

package org.peerfact.impl.application.filesharing.documents;

import org.peerfact.api.overlay.OverlayKey;
import org.peerfact.api.overlay.cd.Document;

public class FileSharingDocument implements Document<OverlayKey<?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5751869102132110844L;

	private State state;

	private int id;

	private OverlayKey<?> key;

	private long size;

	private int popularity;

	public FileSharingDocument(int id, long size) {
		this.id = id;
		this.size = size;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public void setKey(OverlayKey<?> key) {
		this.key = key;
	}

	@Override
	public OverlayKey<?> getKey() {
		return this.key;
	}

	@Override
	public void setSize(long newSize) {
		this.size = newSize;
	}

	@Override
	public long getSize() {
		return this.size;
	}

	@Override
	public int getPopularity() {
		return this.popularity;
	}

	@Override
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public FileSharingDocument copy() {
		FileSharingDocument document = new FileSharingDocument(this.id,
				this.size);
		document.state = this.state;
		document.key = this.key;
		document.popularity = this.popularity;
		return document;
	}

}
