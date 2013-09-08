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

package org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.ConnectMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.OkMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PingMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PongMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.PushMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryHitMessage;
import org.peerfact.impl.overlay.unstructured.homogeneous.gnutella04.messages.QueryMessage;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.AbstractTransMessage;

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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class GnutellaMessageAnalyzer implements TransAnalyzer {

	private Map<BigInteger, Integer> queryHopCount = new LinkedHashMap<BigInteger, Integer>();

	private Map<BigInteger, Integer> queryHitHopCount = new LinkedHashMap<BigInteger, Integer>();

	private Map<BigInteger, Integer> queryHitHopsNeededByQuery = new LinkedHashMap<BigInteger, Integer>();

	private List<BigInteger> queryIds = new LinkedList<BigInteger>();

	private List<BigInteger> queryHitIds = new LinkedList<BigInteger>();

	private List<BigInteger> queriesFailed = new LinkedList<BigInteger>();

	private static double connectCounter = 0;

	private static double okCounter = 0;

	private static double pingCounter = 0;

	private static double pongCounter = 0;

	private static double pushCounter = 0;

	private static double queryCounter = 0;

	private static double queryHitCounter = 0;

	private static double okSentCounter = 0;

	private static double pingSentCounter = 0;

	private static double pongSentCounter = 0;

	private static double pushSentCounter = 0;

	private static double querySentCounter = 0;

	private static double queryHitSentCounter = 0;

	private static double totalMessagesSentCounter = 0;

	private static double totalMessagesCounter = 0;

	private double firstCounter = 0;

	private double secCounter = 0;

	private double queryHopsAveraged = 0;

	private double hopsWhenQueryHitAveraged = 0;

	private int queryHops;

	private int queryHitHops;

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(Writer output) {
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(2);

		double queryPerCent = (100.0 * queryCounter) / totalMessagesCounter;
		double queryHitPerCent = ((100 * queryHitCounter) / totalMessagesCounter);
		double pingPerCent = ((100 * pingCounter) / totalMessagesCounter);
		double pongPerCent = ((100 * pongCounter) / totalMessagesCounter);
		double connectPerCent = ((100 * connectCounter) / totalMessagesCounter);
		double pushPerCent = ((100 * pushCounter) / totalMessagesCounter);
		double okPerCent = ((100 * okCounter) / totalMessagesCounter);

		for (int i = 0; i < queryIds.size(); i++) {
			BigInteger descriptor = queryIds.get(i);
			if (queryHopCount.containsKey(descriptor)) {
				queryHops += queryHopCount.get(descriptor);
				firstCounter++;
			}
		}

		for (int i = 0; i < queryHitIds.size(); i++) {
			BigInteger descriptor = queryHitIds.get(i);
			if (queryHitHopCount.containsKey(descriptor)) {
				queryHitHops += queryHitHopCount.get(descriptor);
				queryHops += queryHopCount.get(descriptor);
				secCounter++;
				firstCounter++;
			}
		}

		if (firstCounter != 0) {
			queryHopsAveraged = queryHops / firstCounter;
		}

		if (secCounter != 0) {
			hopsWhenQueryHitAveraged = queryHitHops / secCounter;
		}

		double queryNotSucceded = ((100 * (double) queriesFailed
				.size()) / queryIds.size());
		// double querySucceded = ((100 * (double) queryHitIds.size()) /
		// queryIds
		// .size());
		int queryErfolgreich = queryIds.size() - queriesFailed.size();

		String f = Simulator.getOuputDir().getAbsolutePath() + File.separator
				+ "Gnutella04Message.dat";
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("	Gnutella-Analyzer	\n");
			out.write("	-----------------------	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("Messages Sent: \n");
			out
					.write("Gesamt		query		queryhit	ping		pong		connect		OK			push	\n");
			out.write(totalMessagesSentCounter + "		"
					+ querySentCounter + "		" + queryHitSentCounter
					+ "		" + pingSentCounter + "		" + pongSentCounter
					+ "		" + connectCounter + "		" + okSentCounter + "		"
					+ pushSentCounter + " \n");
			out.write("	\n");
			out.write("Messages empfangen: \n");
			out
					.write("Gesamt		query		queryhit	ping		pong		connect		OK			push	\n");
			out.write(totalMessagesCounter + "		" + queryCounter + "		"
					+ queryHitCounter + "		" + pingCounter + "		" + pongCounter
					+ "		" + connectCounter + "		" + okCounter + "		"
					+ pushCounter + " \n");
			out.write("			" + n.format(queryPerCent) + "		"
					+ n.format(queryHitPerCent) + "		" + n.format(pingPerCent)
					+ "		" + n.format(pongPerCent) + "		"
					+ n.format(connectPerCent) + "		" + n.format(okPerCent)
					+ "		" + n.format(pushPerCent) + " \n");
			out.write("	\n");
			out.write("Avergae Hops:			\n");
			out
					.write("Overall	QueryHit		querys sent+received		queryHits		query Failed	\n");
			out.write(n.format(queryHopsAveraged) + "		"
					+ n.format(hopsWhenQueryHitAveraged) + "						"
					+ queryIds.size() + "					" + queryErfolgreich + "				"
					+ queriesFailed.size() + "	\n");
			out.write("	\n");
			out
					.write("Percentage succesfull		Percentage failed	\n");
			out.write(n.format(100 - queryNotSucceded) + "								"
					+ n.format(queryNotSucceded) + "	\n");
			out.write("	\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {
		totalMessagesCounter++;
		Message message = msg.getPayload();

		if (message instanceof ConnectMessage) {
			connectCounter++;
		} else if (message instanceof OkMessage) {
			okCounter++;
		} else if (message instanceof PingMessage) {
			pingCounter++;
		} else if (message instanceof PongMessage) {
			pongCounter++;
		} else if (message instanceof PushMessage) {
			pushCounter++;
		} else if (message instanceof QueryMessage) {
			queryCounter++;
			QueryMessage queryMessage = (QueryMessage) message.getPayload();
			BigInteger descriptor = queryMessage.getDescriptor();
			int hops = queryMessage.getHops();
			int hopsGespeichert = 0;
			if (!queryIds.contains(descriptor)) {
				queryIds.add(descriptor);
			}
			if (!queriesFailed.contains(descriptor)) {
				queriesFailed.add(descriptor);
			}
			if (queryHopCount.containsKey(descriptor)) {
				hopsGespeichert = queryHopCount.get(descriptor);
			}
			if (hops > hopsGespeichert) {
				queryHopCount.put(descriptor, hops);
			}

		} else if (message instanceof QueryHitMessage) {

			queryHitCounter++;
			QueryHitMessage queryHitMessage = (QueryHitMessage) message
					.getPayload();
			BigInteger descriptor = queryHitMessage.getDescriptor();
			int hops = queryHitMessage.getHops();
			int hopsGespeichert = 0;
			if (!queryHitIds.contains(descriptor)) {
				queryHitIds.add(descriptor);
			}
			if (queriesFailed.contains(descriptor)) {
				queriesFailed.remove(descriptor);
			}
			if (queryHitHopCount.containsKey(descriptor)) {
				hopsGespeichert = queryHitHopCount.get(descriptor);
			}
			if (hops > hopsGespeichert) {
				queryHitHopCount.put(descriptor, hops);
			}
			if (queryHopCount.containsKey(descriptor)) {
				int numberOfQueryHops = queryHopCount.get(descriptor);
				queryHitHopsNeededByQuery.put(descriptor, numberOfQueryHops);
			}

		}

	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		totalMessagesSentCounter++;
		Message message = msg.getPayload();

		if (message instanceof OkMessage) {
			okSentCounter++;
		} else if (message instanceof PingMessage) {
			pingSentCounter++;
		} else if (message instanceof PongMessage) {
			pongSentCounter++;
		} else if (message instanceof PushMessage) {
			pushSentCounter++;
		} else if (message instanceof QueryMessage) {
			querySentCounter++;
		} else if (message instanceof QueryHitMessage) {
			queryHitSentCounter++;
		}
	}

}
