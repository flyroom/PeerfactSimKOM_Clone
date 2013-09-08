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

package org.peerfact.impl.analyzer.csvevaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.common.Message;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
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
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class MessageCounter implements NetAnalyzer {

	Map<Class<? extends Message>, Integer> msgs = new TreeMap<Class<? extends Message>, Integer>(
			new ClassComparator());

	private boolean active;

	private int hostCount;

	private String fileName;

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub

	}

	protected static Message getOverlayMsg(NetMessage msg) {
		return msg.getPayload().getPayload();
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		if (active) {
			countMessage(getOverlayMsg(msg));
		}
	}

	@Override
	public void start() {
		active = true;
	}

	public void setHostCount(int hostCount) {
		this.hostCount = hostCount;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void stop(Writer output) {
		active = false;
		try {
			dumpMsgs(new PrintWriter(System.out));
			writeToFile(Simulator.getOuputDir().getAbsolutePath()
					+ File.separator + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void countMessage(Message olMsg) {

		Integer occurs = msgs.get(olMsg.getClass());
		int newOccurs;

		if (occurs != null) {
			newOccurs = occurs + 1;
		} else {
			newOccurs = 1;
		}

		msgs.put(olMsg.getClass(), newOccurs);

	}

	public void dumpMsgs(Writer w) throws IOException {

		Set<Entry<Class<? extends Message>, Integer>> entries = msgs.entrySet();

		w.write("# Hosts ");
		for (Entry<Class<? extends Message>, Integer> entry : entries) {
			w.write(entry.getKey().getSimpleName() + "	");
		}
		w.write('\n');
		w.write(hostCount + "	");
		for (Entry<Class<? extends Message>, Integer> entry : entries) {
			w.write(entry.getValue() + "	");
		}
		w.write('\n');
	}

	public void writeToFile(String filename) throws IOException {
		FileWriter fstream = new FileWriter(filename, true);
		BufferedWriter out = new BufferedWriter(fstream);
		dumpMsgs(out);
		out.close();
	}

	public static class ClassComparator implements Comparator<Class<?>>,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6749854026087984159L;

		@Override
		public int compare(Class<?> o1, Class<?> o2) {
			return o1.getSimpleName().compareTo(o2.getSimpleName());
		}

	}

}
