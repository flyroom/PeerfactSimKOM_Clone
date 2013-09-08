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

package org.peerfact.impl.overlay.unstructured.zeroaccess.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.peerfact.api.analyzer.TransAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.GetLMessage;
import org.peerfact.impl.overlay.unstructured.zeroaccess.message.RetLMessage;
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
public class ZeroAccessMessageAnalyzer implements TransAnalyzer {

	private List<BigInteger> queryIds = new LinkedList<BigInteger>();

	private List<BigInteger> queryHitIds = new LinkedList<BigInteger>();

	private List<BigInteger> queriesFailed = new LinkedList<BigInteger>();

	private static double getLCounter = 0;

	private static double retLCounter = 0;

	private static double getLSentCounter = 0;

	private static double retLSentCounter = 0;

	private static double totalMessagesSentCounter = 0;

	private static double totalMessagesCounter = 0;

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(Writer output) {
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(2);

		double getLPerCent = (100.0 * getLCounter) / totalMessagesCounter;
		double retLPerCent = (100.0 * retLCounter) / totalMessagesCounter;

		double queryNotSucceded = ((100 * (double) queriesFailed
				.size()) / queryIds.size());
		// double querySucceded = ((100 * (double) queryHitIds.size()) /
		// queryIds
		// .size());
		int queryErfolgreich = queryIds.size() - queriesFailed.size();

		String f = Simulator.getOuputDir().getAbsolutePath() + File.separator
				+ "ZeroAccessMessage.dat";
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("	ZeroAccess-Analyzer	\n");
			out.write("	-----------------------	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("Messages Sent: \n");
			out
					.write("Gesamt		getL		retL	\n");
			out.write(totalMessagesSentCounter + "		"
					+ getLCounter + "		" + retLCounter + " \n");
			out.write("	\n");

			out.write("			" + n.format(getLPerCent) + "		"
					+ n.format(retLPerCent) + " \n");
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

		if (message instanceof GetLMessage) {
			getLCounter++;
		} else if (message instanceof RetLMessage) {
			retLCounter++;
		}
	}

	@Override
	public void transMsgSent(AbstractTransMessage msg) {
		totalMessagesSentCounter++;
		Message message = msg.getPayload();

		if (message instanceof GetLMessage) {
			getLSentCounter++;
		} else if (message instanceof RetLMessage) {
			retLSentCounter++;
		}
	}

}
