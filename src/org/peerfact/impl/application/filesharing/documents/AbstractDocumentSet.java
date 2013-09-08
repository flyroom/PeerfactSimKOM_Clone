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

import java.util.LinkedHashMap;
import java.util.Map;

import org.peerfact.impl.simengine.Simulator;


/**
 * An abstract implementation for an document set to implement common
 * functionality like names, sizes, file sizes and document storages.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 */
public abstract class AbstractDocumentSet implements IDocumentSet {

	protected String name;

	protected int size;

	private static int maxFilesize = 10 * 1024; // 10 KB

	protected Map<Integer, FileSharingDocument> documents;

	/**
	 * Constructor creates empty map for documents.
	 */
	public AbstractDocumentSet() {
		this.documents = new LinkedHashMap<Integer, FileSharingDocument>();
	}

	/**
	 * Sets the string name of this document set
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the size of this document set to the given value
	 * 
	 * @param size
	 *            the number of documents
	 */
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	public static void setMaxFilesize(int filesize) {
		AbstractDocumentSet.maxFilesize = filesize;
	}

	protected void createDocument(int id) {
		documents.put(id, new FileSharingDocument(id, Simulator.getRandom()
				.nextInt(maxFilesize)));
	}
}
