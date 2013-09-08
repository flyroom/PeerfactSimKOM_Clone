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

package org.peerfact.impl.overlay.dht.kademlia.base.analyzer;

import java.util.Collection;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.overlay.dht.DHTObject;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayContact;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayID;
import org.peerfact.impl.overlay.dht.kademlia.base.components.KademliaOverlayKey;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.AbstractKademliaOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.DataLookupOperation;
import org.peerfact.impl.overlay.dht.kademlia.base.operations.KClosestNodesLookupOperation;


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
 * Groups interfaces of Analyzers that are only useful in Kademlia.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public interface IKademliaAnalyzer {

	/**
	 * OperationAnalyzers receive notifications when a operation is finished
	 * either with or without success.
	 * 
	 */
	public interface KademliaOperationAnalyzer extends Analyzer {

		/**
		 * This method is called whenever an operation has been triggered.
		 * 
		 * @param op
		 *            the AbstractKademliaOperation that has been triggered.
		 */
		public void operationInitiated(AbstractKademliaOperation<?, ?> op);

		/**
		 * This method is called whenever an operation has completed.
		 * 
		 * @param op
		 *            the AbstractKademliaOperation that has completed.
		 */
		public void operationFinished(AbstractKademliaOperation<?, ?> op);

	}

	public interface KademliaMessageTrafficAnalyzer extends
			KademliaOperationAnalyzer {

		/**
		 * The given message has been sent by the given operation.
		 * 
		 * @param msg
		 *            the KademliaMsg that has been sent.
		 * @param op
		 *            the AbstractKademliaOperation that sent the message.
		 */
		public void messageSent(KademliaMsg<?> msg,
				AbstractKademliaOperation<?, ?> op);
	}

	/**
	 * Interface for Analyzers of DataLookupOperations.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface DataLookupAnalyzer extends Analyzer {

		/**
		 * The given data lookup operation has been initiated.
		 * 
		 * @param op
		 *            the DataLookupOperation that has begun to execute.
		 */
		public void dataLookupInitiated(DataLookupOperation<?> op);

		/**
		 * The given operation sent the given message.
		 * 
		 * @param msg
		 *            the KademliaMsg that has been sent.
		 * @param op
		 *            the DataLookupOperation that sent the message.
		 */
		public void dataLookupMsgSent(KademliaMsg<?> msg,
				DataLookupOperation<?> op);

		/**
		 * A data lookup has completed.
		 * 
		 * @param key
		 *            the KademliaOverlayKey of the data item that has been
		 *            looked up.
		 * @param result
		 *            the DHTObject that has been found (or <code>null</code> if
		 *            nothing has been found).
		 * @param sender
		 *            the KademliaOverlayID of the peer that sent the data item
		 *            (or <code>null</code> if no peer sent it).
		 * @param closestNodes
		 *            the Set of the K closest nodes that the lookup has seen
		 *            (only relevant if lookup did not return a data item).
		 * @param op
		 *            the DataLookupOperation that has completed.
		 */
		public void dataLookupCompleted(KademliaOverlayKey key,
				DHTObject result, KademliaOverlayID sender,
				Collection<? extends KademliaOverlayContact<?>> closestNodes,
				DataLookupOperation<?> op);
	}

	/**
	 * Interface for Analyzers of KClosestNodesLookupOperations.
	 * 
	 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
	 */
	public interface KClosestNodesLookupAnalyzer extends Analyzer {

		/**
		 * A data lookup has completed.
		 * 
		 * @param key
		 *            the KademliaOverlayKey of the data item that has been
		 *            looked up.
		 * @param result
		 *            a Collection containing the k closest nodes that have been
		 *            found.
		 * @param op
		 *            the KClosestNodesLookupOperation that has completed.
		 */
		public void kClosestNodesLookupCompleted(KademliaOverlayKey key,
				Collection<? extends KademliaOverlayContact<?>> result,
				KClosestNodesLookupOperation<?> op);
	}
}
