/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package org.peerfact.impl.application.filesharing.operations;

import org.peerfact.api.common.OperationCallback;
import org.peerfact.impl.application.filesharing.FileSharingApplication;
import org.peerfact.impl.common.AbstractOperation;

/**
 * Common asbtract superclass of all file sharing operations.
 * 
 * @author Konstantin Pussep
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * @param <S>
 *            the type of the operation result
 * 
 */
public abstract class AbstractFSOperation<S> extends
AbstractOperation<FileSharingApplication, S> {

	AbstractFSOperation(FileSharingApplication client, OperationCallback<S> callback) {
		super(client, callback);
	}

}
