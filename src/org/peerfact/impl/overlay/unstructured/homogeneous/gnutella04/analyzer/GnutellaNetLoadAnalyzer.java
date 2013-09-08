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
public class GnutellaNetLoadAnalyzer implements TransAnalyzer {

	private Map<Integer, Integer> gesamtWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private Map<Integer, Integer> queriesWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private Map<Integer, Integer> queryHitsWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private Map<Integer, Integer> pingsWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private Map<Integer, Integer> pongsWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private Map<Integer, Integer> connectWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private Map<Integer, Integer> okWithTimeSteps = new LinkedHashMap<Integer, Integer>();

	private List<Integer> timeSteps = new LinkedList<Integer>();

	long pingSize = 0;

	long pongSize = 0;

	long querySize = 0;

	long queryHitSize = 0;

	long connectSize = 0;

	long okSize = 0;

	int pingGesamt = 0;

	int pongGesamt = 0;

	int queryGesamt = 0;

	int queryHitGesamt = 0;

	int connectGesamt = 0;

	int okGesamt = 0;

	int gesamtGesamt = 0;

	@Override
	public void start() {
		// No implementation here (not sure why) by Thim
		// This text is here so that later on someone will find this.

	}

	@Override
	public void stop(Writer output) {
		String f = Simulator.getOuputDir().getAbsolutePath() + File.separator
				+ "Gnutella04NetLoad.dat";
		FileWriter fstream = null;

		long pingGesamtLoad = 0;
		long pongGesamtLoad = 0;
		long queryGesamtLoad = 0;
		long queryHitGesamtLoad = 0;
		long connectGesamtLoad = 0;
		long okGesamtLoad = 0;
		long gesamtGesamtLoad = 0;
		try {
			fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("	Gnutella-Analyzer	\n");
			out.write("	-----------------------	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("Sent messages per time unit:	\n");
			out
					.write("Time		Overall			Ping			Pong			Query			QueryHit		Connect			ok	\n");
			out.write("	\n");
			for (int i = 0; i < timeSteps.size(); i++) {
				int step1 = timeSteps.get(i) * 10;
				int step2 = step1 + 10;
				int ping = 0;
				int pong = 0;
				int query = 0;
				int queryHit = 0;
				int connect = 0;
				int ok = 0;
				int gesamt = 0;

				if (pingsWithTimeSteps.containsKey(timeSteps.get(i))) {
					ping = pingsWithTimeSteps.get(timeSteps.get(i));
					pingGesamt += ping;
				}
				if (pongsWithTimeSteps.containsKey(timeSteps.get(i))) {
					pong = pongsWithTimeSteps.get(timeSteps.get(i));
					pongGesamt += pong;
				}
				if (queriesWithTimeSteps.containsKey(timeSteps.get(i))) {
					query = queriesWithTimeSteps.get(timeSteps.get(i));
					queryGesamt += query;
				}
				if (queryHitsWithTimeSteps.containsKey(timeSteps.get(i))) {
					queryHit = queryHitsWithTimeSteps.get(timeSteps.get(i));
					queryHitGesamt += queryHit;
				}
				if (connectWithTimeSteps.containsKey(timeSteps.get(i))) {
					connect = connectWithTimeSteps.get(timeSteps.get(i));
					connectGesamt += connect;
				}
				if (okWithTimeSteps.containsKey(timeSteps.get(i))) {
					ok = okWithTimeSteps.get(timeSteps.get(i));
					okGesamt += ok;
				}
				if (gesamtWithTimeSteps.containsKey(timeSteps.get(i))) {
					gesamt = gesamtWithTimeSteps.get(timeSteps.get(i));
					gesamtGesamt += gesamt;
				}
				out.write(step1 + "-" + step2 + "		" + gesamt + "			" + ping
						+ "			" + pong + "			" + query + "			" + queryHit
						+ "				" + connect + "					" + ok + "	\n");
			}
			out.write("	\n");
			out
					.write(" ......................................................................................................................................................................................................................................................................................................................................................................................	\n");
			out.write("			" + gesamtGesamt + "			" + pingGesamt + "			"
					+ pongGesamt + "			" + queryGesamt + "			" + queryHitGesamt
					+ "			" + connectGesamt + "			" + okGesamt + "	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("Netzlast pro Zeiteinheit:	\n");
			out
					.write("Zeit		Gesamt			Ping			Pong			Query			QueryHit		Connect			ok		\n");
			out.write("	\n");
			for (int i = 0; i < timeSteps.size(); i++) {
				long pingLoad = 0;
				long pongLoad = 0;
				long queryLoad = 0;
				long queryHitLoad = 0;
				long connectLoad = 0;
				long okLoad = 0;

				if (pingsWithTimeSteps.containsKey(timeSteps.get(i))) {
					pingLoad = pingsWithTimeSteps.get(timeSteps.get(i))
							* pingSize;
					pingGesamtLoad += pingLoad;
				}
				if (pongsWithTimeSteps.containsKey(timeSteps.get(i))) {
					pongLoad = pongsWithTimeSteps.get(timeSteps.get(i))
							* pongSize;
					pongGesamtLoad += pongLoad;
				}
				if (queriesWithTimeSteps.containsKey(timeSteps.get(i))) {
					queryLoad = queriesWithTimeSteps.get(timeSteps.get(i))
							* querySize;
					queryGesamtLoad += queryLoad;
				}
				if (queryHitsWithTimeSteps.containsKey(timeSteps.get(i))) {
					queryHitLoad = queryHitsWithTimeSteps.get(timeSteps.get(i))
							* queryHitSize;
					queryHitGesamtLoad += queryHitLoad;
				}
				if (connectWithTimeSteps.containsKey(timeSteps.get(i))) {
					connectLoad = connectWithTimeSteps.get(timeSteps.get(i))
							* connectSize;
					connectGesamtLoad += connectLoad;
				}
				if (okWithTimeSteps.containsKey(timeSteps.get(i))) {
					okLoad = okWithTimeSteps.get(timeSteps.get(i)) * okSize;
					okGesamtLoad += okLoad;
				}

				long gesamtLoad = pingLoad + pongLoad + queryLoad
						+ queryHitLoad + connectLoad + okLoad;
				gesamtGesamtLoad += gesamtLoad;

				int step1 = timeSteps.get(i) * 10;
				int step2 = step1 + 10;
				out.write(step1 + "-" + step2 + "		" + gesamtLoad + "				"
						+ pingLoad + "			" + pongLoad + "			" + queryLoad
						+ "			" + queryHitLoad + "				" + connectLoad + "					"
						+ okLoad + "	\n");
			}
			out.write("	\n");
			out
					.write(" ......................................................................................................................................................................................................................................................................................................................................................................................	\n");
			out.write("			" + gesamtGesamtLoad + "			" + pingGesamtLoad + "			"
					+ pongGesamtLoad + "			" + queryGesamtLoad + "			"
					+ queryHitGesamtLoad + "			" + connectGesamtLoad + "			"
					+ okGesamtLoad + "	\n");
			out.write("	\n");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void transMsgReceived(AbstractTransMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		Message message = msg.getPayload();
		Simulator.getInstance();
		long time = Simulator.getCurrentTime();
		int timeStep = (int) (time / 10000000);
		if (!timeSteps.contains(timeStep)) {
			timeSteps.add(timeStep);
		}
		int gesamtAnzahl = 0;
		if (gesamtWithTimeSteps.containsKey(timeStep)) {
			gesamtAnzahl = gesamtWithTimeSteps.get(timeStep);
		}
		gesamtAnzahl += 1;
		gesamtWithTimeSteps.put(timeStep, gesamtAnzahl);

		if (message instanceof ConnectMessage) {
			int anzahl = 0;
			connectSize = ((ConnectMessage) message.getPayload()).getSize();
			if (connectWithTimeSteps.containsKey(timeStep)) {
				anzahl = connectWithTimeSteps.get(timeStep);
			}
			anzahl += 1;
			connectWithTimeSteps.put(timeStep, anzahl);

		} else if (message instanceof OkMessage) {
			int anzahl = 0;
			okSize = ((OkMessage) message.getPayload()).getSize();
			if (okWithTimeSteps.containsKey(timeStep)) {
				anzahl = okWithTimeSteps.get(timeStep);
			}
			anzahl += 1;
			okWithTimeSteps.put(timeStep, anzahl);

		} else if (message instanceof PingMessage) {
			int anzahl = 0;
			pingSize = ((PingMessage) message.getPayload()).getSize();
			if (pingsWithTimeSteps.containsKey(timeStep)) {
				anzahl = pingsWithTimeSteps.get(timeStep);
			}
			anzahl += 1;
			pingsWithTimeSteps.put(timeStep, anzahl);

		} else if (message instanceof PongMessage) {
			int anzahl = 0;
			pongSize = ((PongMessage) message.getPayload()).getSize();
			if (pongsWithTimeSteps.containsKey(timeStep)) {
				anzahl = pongsWithTimeSteps.get(timeStep);
			}
			anzahl += 1;
			pongsWithTimeSteps.put(timeStep, anzahl);

		} else if (message instanceof QueryMessage) {
			int anzahl = 0;
			querySize = ((QueryMessage) message.getPayload()).getSize();
			if (queriesWithTimeSteps.containsKey(timeStep)) {
				anzahl = queriesWithTimeSteps.get(timeStep);
			}
			anzahl += 1;
			queriesWithTimeSteps.put(timeStep, anzahl);
		} else if (message instanceof QueryHitMessage) {
			int anzahl = 0;
			queryHitSize = ((QueryHitMessage) message.getPayload()).getSize();
			if (queryHitsWithTimeSteps.containsKey(timeStep)) {
				anzahl = queryHitsWithTimeSteps.get(timeStep);
			}
			anzahl += 1;
			queryHitsWithTimeSteps.put(timeStep, anzahl);
		}
	}

}
