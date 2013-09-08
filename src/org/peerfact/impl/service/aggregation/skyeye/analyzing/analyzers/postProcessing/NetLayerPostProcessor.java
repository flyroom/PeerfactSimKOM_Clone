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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Vector;

import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetMessage;
import org.peerfact.api.service.skyeye.SkyNetPostProcessing;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.overlay.AbstractOverlayMessage;
import org.peerfact.impl.overlay.dht.centralizedindex.components.CIOverlayID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordID;
import org.peerfact.impl.overlay.dht.chord.base.components.ChordIDFactory;
import org.peerfact.impl.overlay.dht.kademlia.base.messages.KademliaMsg;
import org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.NetLayerAnalyzerEntry;
import org.peerfact.impl.service.aggregation.skyeye.attributes.messages.AttributeUpdateMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateACKMsg;
import org.peerfact.impl.service.aggregation.skyeye.metrics.messages.MetricUpdateMsg;
import org.peerfact.impl.util.toolkits.HashToolkit;


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
 * This class implements the interface {@link SkyNetPostProcessing} and is
 * responsible for processing the data, which was monitored and collected during
 * a simulation in terms of sent, received and dropped messages. The captured
 * data is situated in the netLayerData-directory and will be utilized within
 * this class to generate some dat-files, which are used for the visualization
 * within gnuPlot.<br>
 * <br>
 * <code>MessagesSent.dat</code>, <code>MessagesReceived.dat</code> and
 * <code>MessagesDropped.dat</code> contain the number of sent, received and
 * dropped messages as well as the size of sent, received and dropped messages,
 * which are sorted by the overlay-ID of the peers.<br>
 * <br>
 * <code>SortedMessagesSent.dat</code>, <code>SortedMessagesReceived.dat</code>
 * and <code>SortedMessagesDropped.dat</code> contain a ranking of the number of
 * sent, received and dropped messages (Starting with the highest and ending
 * with the lowest amount of messages). <br>
 * <br>
 * <code>SortedSizeMessagesSent.dat</code>,
 * <code>SortedSizeMessagesReceived.dat</code> and
 * <code>SortedSizeMessagesDropped.dat</code> contain a ranking of the amount of
 * sent, received and dropped bytes (Starting with the highest and ending with
 * the lowest amount of bytes).
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class NetLayerPostProcessor implements SkyNetPostProcessing {

	// private static Logger log = SimLogger
	// .getLogger(NetLayerPostProcessor.class);

	private static final String DROPPED_MESSAGES_DAT_FILE = "MessagesDropped.dat";

	private static final String SENT_MESSAGES_DAT_FILE = "MessagesSent.dat";

	private static final String RECEIVED_MESSAGES_DAT_FILE = "MessagesReceived.dat";

	private static final String SORTED_DROPPED_MESSAGES_DAT_FILE = "SortedMessagesDropped.dat";

	private static final String SORTED_SENT_MESSAGES_DAT_FILE = "SortedMessagesSent.dat";

	private static final String SORTED_RECEIVED_MESSAGES_DAT_FILE = "SortedMessagesReceived.dat";

	private static final String SORTED_SIZE_DROPPED_MESSAGES_DAT_FILE = "SortedSizeMessagesDropped.dat";

	private static final String SORTED_SIZE_SENT_MESSAGES_DAT_FILE = "SortedSizeMessagesSent.dat";

	private static final String SORTED_SIZE_RECEIVED_MESSAGES_DAT_FILE = "SortedSizeMessagesReceived.dat";

	private String writingDataPath;

	private static String READING_DATA_PATH = Constants.TMP_DIR
			+ File.separator + "netLayerData";

	private String[] files;

	private String simulationType;

	private PrintWriter adFile;

	private LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>> droppedMsgs;

	private LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>> sentMsgs;

	private LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>> receivedMsgs;

	private TreeMap<BigInteger, DataEntry> droppedMessagesData;

	private TreeMap<BigInteger, DataEntry> sentMessagesData;

	private TreeMap<BigInteger, DataEntry> receivedMessagesData;

	private TreeMap<Integer, Integer> sortedMsgData;

	private TreeMap<Integer, Integer> sortedOverlayMsgData;

	private TreeMap<Integer, Integer> sortedSkyNetMsgData;

	private TreeMap<Integer, Integer> sortedMetricUpdateMsgData;

	private TreeMap<Integer, Integer> sortedMetricUpdateACKMsgData;

	private TreeMap<Integer, Integer> sortedAttributeUpdateMsgData;

	private TreeMap<Long, Integer> sortedSizeMsgData;

	private TreeMap<Long, Integer> sortedSizeOverlayMsgData;

	private TreeMap<Long, Integer> sortedSizeSkyNetMsgData;

	private TreeMap<Long, Integer> sortedSizeMetricUpdateMsgData;

	private TreeMap<Long, Integer> sortedSizeMetricUpdateACKMsgData;

	private TreeMap<Long, Integer> sortedSizeAttributeUpdateMsgData;

	public NetLayerPostProcessor(String dataPath, String simulationType) {
		this.writingDataPath = dataPath;
		files = null;
		this.simulationType = simulationType;
		droppedMessagesData = new TreeMap<BigInteger, DataEntry>();
		sentMessagesData = new TreeMap<BigInteger, DataEntry>();
		receivedMessagesData = new TreeMap<BigInteger, DataEntry>();
		// treeMaps for the amount of messages
		sortedMsgData = new TreeMap<Integer, Integer>();
		sortedOverlayMsgData = new TreeMap<Integer, Integer>();
		sortedSkyNetMsgData = new TreeMap<Integer, Integer>();
		sortedMetricUpdateMsgData = new TreeMap<Integer, Integer>();
		sortedMetricUpdateACKMsgData = new TreeMap<Integer, Integer>();
		sortedAttributeUpdateMsgData = new TreeMap<Integer, Integer>();

		// treeMaps for the size of messages
		sortedSizeMsgData = new TreeMap<Long, Integer>();
		sortedSizeOverlayMsgData = new TreeMap<Long, Integer>();
		sortedSizeSkyNetMsgData = new TreeMap<Long, Integer>();
		sortedSizeMetricUpdateMsgData = new TreeMap<Long, Integer>();
		sortedSizeMetricUpdateACKMsgData = new TreeMap<Long, Integer>();
		sortedSizeAttributeUpdateMsgData = new TreeMap<Long, Integer>();
	}

	@Override
	public void extractDataOfFiles() {
		getListOfTempFiles(READING_DATA_PATH);
		if (files != null) {
			File file;
			for (int i = 0; i < files.length; i++) {
				file = new File(READING_DATA_PATH + File.separator + files[i]);
				// log.debug("  Reading " + (i + 1) + ". message-file "
				// + file.getName());
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(file));
					droppedMsgs = (LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>>) ois
							.readObject();
					sentMsgs = (LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>>) ois
							.readObject();
					receivedMsgs = (LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>>) ois
							.readObject();
					ois.close();
					collectDataOfSerializedMsgs("dropped", droppedMsgs,
							droppedMessagesData);
					collectDataOfSerializedMsgs("sent", sentMsgs,
							sentMessagesData);
					collectDataOfSerializedMsgs("received", receivedMsgs,
							receivedMessagesData);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void processData() {
		// not needed
	}

	@Override
	public void writeDataFile() {
		writeData(writingDataPath + File.separator + DROPPED_MESSAGES_DAT_FILE,
				"dropped", droppedMessagesData);
		sortMsgData(droppedMessagesData);
		writeSortedData(writingDataPath + File.separator
				+ SORTED_DROPPED_MESSAGES_DAT_FILE, "dropped", sortedMsgData,
				sortedOverlayMsgData, sortedSkyNetMsgData,
				sortedMetricUpdateMsgData, sortedMetricUpdateACKMsgData,
				sortedAttributeUpdateMsgData);

		sortSizeMsgData(droppedMessagesData);
		writeSortedSizeData(writingDataPath + File.separator
				+ SORTED_SIZE_DROPPED_MESSAGES_DAT_FILE, "dropped",
				sortedSizeMsgData, sortedSizeOverlayMsgData,
				sortedSizeSkyNetMsgData, sortedSizeMetricUpdateMsgData,
				sortedSizeMetricUpdateACKMsgData,
				sortedSizeAttributeUpdateMsgData);

		sortedMsgData.clear();
		sortedOverlayMsgData.clear();
		sortedSkyNetMsgData.clear();
		sortedMetricUpdateMsgData.clear();
		sortedMetricUpdateACKMsgData.clear();
		sortedAttributeUpdateMsgData.clear();
		sortedSizeMsgData.clear();
		sortedSizeOverlayMsgData.clear();
		sortedSizeSkyNetMsgData.clear();
		sortedSizeMetricUpdateMsgData.clear();
		sortedSizeMetricUpdateACKMsgData.clear();
		sortedSizeAttributeUpdateMsgData.clear();

		writeData(writingDataPath + File.separator + SENT_MESSAGES_DAT_FILE,
				"sent", sentMessagesData);
		sortMsgData(sentMessagesData);
		writeSortedData(writingDataPath + File.separator
				+ SORTED_SENT_MESSAGES_DAT_FILE, "sent", sortedMsgData,
				sortedOverlayMsgData, sortedSkyNetMsgData,
				sortedMetricUpdateMsgData, sortedMetricUpdateACKMsgData,
				sortedAttributeUpdateMsgData);

		sortSizeMsgData(sentMessagesData);
		writeSortedSizeData(writingDataPath + File.separator
				+ SORTED_SIZE_SENT_MESSAGES_DAT_FILE, "sent",
				sortedSizeMsgData, sortedSizeOverlayMsgData,
				sortedSizeSkyNetMsgData, sortedSizeMetricUpdateMsgData,
				sortedSizeMetricUpdateACKMsgData,
				sortedSizeAttributeUpdateMsgData);

		sortedMsgData.clear();
		sortedOverlayMsgData.clear();
		sortedSkyNetMsgData.clear();
		sortedMetricUpdateMsgData.clear();
		sortedMetricUpdateACKMsgData.clear();
		sortedAttributeUpdateMsgData.clear();
		sortedSizeMsgData.clear();
		sortedSizeOverlayMsgData.clear();
		sortedSizeSkyNetMsgData.clear();
		sortedSizeMetricUpdateMsgData.clear();
		sortedSizeMetricUpdateACKMsgData.clear();
		sortedSizeAttributeUpdateMsgData.clear();

		writeData(
				writingDataPath + File.separator + RECEIVED_MESSAGES_DAT_FILE,
				"received", receivedMessagesData);
		sortMsgData(receivedMessagesData);
		writeSortedData(writingDataPath + File.separator
				+ SORTED_RECEIVED_MESSAGES_DAT_FILE, "received", sortedMsgData,
				sortedOverlayMsgData, sortedSkyNetMsgData,
				sortedMetricUpdateMsgData, sortedMetricUpdateACKMsgData,
				sortedAttributeUpdateMsgData);

		sortSizeMsgData(receivedMessagesData);
		writeSortedSizeData(writingDataPath + File.separator
				+ SORTED_SIZE_RECEIVED_MESSAGES_DAT_FILE, "received",
				sortedSizeMsgData, sortedSizeOverlayMsgData,
				sortedSizeSkyNetMsgData, sortedSizeMetricUpdateMsgData,
				sortedSizeMetricUpdateACKMsgData,
				sortedSizeAttributeUpdateMsgData);

		sortedMsgData.clear();
		sortedOverlayMsgData.clear();
		sortedSkyNetMsgData.clear();
		sortedMetricUpdateMsgData.clear();
		sortedMetricUpdateACKMsgData.clear();
		sortedAttributeUpdateMsgData.clear();
		sortedSizeMsgData.clear();
		sortedSizeOverlayMsgData.clear();
		sortedSizeSkyNetMsgData.clear();
		sortedSizeMetricUpdateMsgData.clear();
		sortedSizeMetricUpdateACKMsgData.clear();
		sortedSizeAttributeUpdateMsgData.clear();

	}

	private void sortMsgData(TreeMap<BigInteger, DataEntry> dataMap) {
		Iterator<DataEntry> dataIter = dataMap.values().iterator();
		DataEntry entry = null;
		Integer count = null;
		int counter = -1;
		while (dataIter.hasNext()) {
			entry = dataIter.next();
			// sort all messages
			count = sortedMsgData.get(entry.getAllMessage());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedMsgData.put(entry.getAllMessage(), Integer.valueOf(counter));

			// sort overlay messages
			count = sortedOverlayMsgData.get(entry.getOverlayMessage());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedOverlayMsgData.put(entry.getOverlayMessage(),
					Integer.valueOf(
							counter));

			// sort skyNet messages
			count = sortedSkyNetMsgData.get(entry.getSkyNetMessage());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSkyNetMsgData.put(entry.getSkyNetMessage(), Integer.valueOf(
					counter));

			// sort metricUpdate messages
			count = sortedMetricUpdateMsgData.get(entry
					.getMetricUpdateMessage());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedMetricUpdateMsgData.put(entry.getMetricUpdateMessage(),
					Integer.valueOf(counter));

			// sort metricUpdateACK messages
			count = sortedMetricUpdateACKMsgData.get(entry
					.getMetricUpdateACKMessage());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedMetricUpdateACKMsgData.put(entry.getMetricUpdateACKMessage(),
					Integer.valueOf(counter));

			// sort attributeUpdate messages
			count = sortedAttributeUpdateMsgData.get(entry
					.getAttributeUpdateMessage());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedAttributeUpdateMsgData.put(entry.getAttributeUpdateMessage(),
					Integer.valueOf(counter));
		}
	}

	private void sortSizeMsgData(TreeMap<BigInteger, DataEntry> dataMap) {
		Iterator<DataEntry> dataIter = dataMap.values().iterator();
		DataEntry entry = null;
		Integer count = null;
		int counter = -1;
		while (dataIter.hasNext()) {
			entry = dataIter.next();
			// sort all messages
			count = sortedSizeMsgData.get(entry.getAllMessageSize());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSizeMsgData.put(entry.getAllMessageSize(), Integer.valueOf(
					counter));

			// sort overlay messages
			count = sortedSizeOverlayMsgData.get(entry.getOverlayMessageSize());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSizeOverlayMsgData.put(entry.getOverlayMessageSize(),
					Integer.valueOf(counter));

			// sort skyNet messages
			count = sortedSizeSkyNetMsgData.get(entry.getSkyNetMessageSize());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSizeSkyNetMsgData.put(entry.getSkyNetMessageSize(),
					Integer.valueOf(counter));

			// sort metricUpdate messages
			count = sortedSizeMetricUpdateMsgData.get(entry
					.getMetricUpdateMessageSize());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSizeMetricUpdateMsgData.put(entry
					.getMetricUpdateMessageSize(), Integer.valueOf(counter));

			// sort metricUpdateACK messages
			count = sortedSizeMetricUpdateACKMsgData.get(entry
					.getMetricUpdateACKMessageSize());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSizeMetricUpdateACKMsgData.put(entry
					.getMetricUpdateACKMessageSize(), Integer.valueOf(counter));

			// sort attributeUpdate messages
			count = sortedSizeAttributeUpdateMsgData.get(entry
					.getAttributeUpdateMessageSize());
			if (count == null) {
				count = 0;
			}
			counter = count.intValue() + 1;
			sortedSizeAttributeUpdateMsgData.put(entry
					.getAttributeUpdateMessageSize(), Integer.valueOf(counter));
		}
	}

	private void collectDataOfSerializedMsgs(String infix,
			LinkedHashMap<Long, Vector<NetLayerAnalyzerEntry>> oldData,
			TreeMap<BigInteger, DataEntry> newData) {
		Iterator<Long> iter = oldData.keySet().iterator();
		Vector<NetLayerAnalyzerEntry> vec;
		DataEntry entry = null;
		long id = 0;
		BigInteger bigID = null;
		long size = 0;
		while (iter.hasNext()) {
			id = iter.next();
			vec = oldData.get(id);
			if (vec != null) {
				// creating the OverlayID of the NetID and getting the
				// corresponding entry
				ChordIDFactory idFactory = ChordIDFactory.getInstance();
				if (simulationType.equals("Chord")) {
					bigID = HashToolkit.getSHA1Hash(new IPv4NetID(id)
							.toString(), ChordID.KEY_BIT_LENGTH);
					entry = newData.remove(bigID);
				} else if (simulationType.equals("Napster")) {
					bigID = new CIOverlayID(new IPv4NetID(id)).getID();
					entry = newData.remove(bigID);
				} else if (simulationType.equals("Kademlia")) {
					bigID = HashToolkit.getSHA1Hash(new IPv4NetID(id)
							.toString());
					entry = newData.remove(bigID);
				}
				if (entry == null) {
					entry = new DataEntry(new IPv4NetID(id));
				}
				entry.incrementAllMessage(vec.size());
				for (int i = 0; i < vec.size(); i++) {
					if (SkyNetMessage.class.isAssignableFrom(vec.get(i)
							.getMsgClass())) {
						size = vec.get(i).getSize();
						entry.incrementSkyNetMessage(1);
						entry.enhanceSkyNetMessageSize(size);
						entry.enhanceAllMessageSize(size);
						if (MetricUpdateMsg.class.isAssignableFrom(vec.get(i)
								.getMsgClass())) {
							size = vec.get(i).getSize();
							entry.incrementMetricUpdateMessage(1);
							entry.enhanceMetricUpdateMessageSize(size);
						} else if (MetricUpdateACKMsg.class
								.isAssignableFrom(vec.get(i).getMsgClass())) {
							size = vec.get(i).getSize();
							entry.incrementMetricUpdateACKMessage(1);
							entry.enhanceMetricUpdateACKMessageSize(size);
						} else if (AttributeUpdateMsg.class
								.isAssignableFrom(vec.get(i).getMsgClass())) {
							size = vec.get(i).getSize();
							entry.incrementAttributeUpdateMessage(1);
							entry.enhanceAttributeUpdateMessageSize(size);
						}
					} else if (AbstractOverlayMessage.class
							.isAssignableFrom(vec.get(i).getMsgClass())
							|| KademliaMsg.class.isAssignableFrom(vec.get(i)
									.getMsgClass())) {
						size = vec.get(i).getSize();
						entry.incrementOverlayMessage(1);
						entry.enhanceOverlayMessageSize(size);
						entry.enhanceAllMessageSize(size);
					}
				}
				newData.put(bigID, entry);
			}
		}
	}

	private void writeSortedData(String fileName, String infix,
			TreeMap<Integer, Integer> allDataMap,
			TreeMap<Integer, Integer> overlayDataMap,
			TreeMap<Integer, Integer> skyNetDataMap,
			TreeMap<Integer, Integer> metricUpdateDataMap,
			TreeMap<Integer, Integer> metricUpdateACKDataMap,
			TreeMap<Integer, Integer> attributeUpdateDataMap) {
		try {
			adFile = new PrintWriter(new BufferedWriter(
					new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		adFile.println("# Sorted number of all " + infix + " Messages");
		adFile.println("# Sorted number of " + infix + " overlay Messages");
		adFile.println("# Sorted number of " + infix + " skyNet Messages");
		adFile
				.println("# Sorted number of " + infix
						+ " metricUpdate Messages");
		adFile.println("# Sorted number of " + infix
				+ " metricUpdateACK Messages");
		adFile.println("# Sorted number of " + infix
				+ " attributeUpdate Messages");
		adFile.println();

		// this line is needed, since the plots for this data use a log-scale
		// for the x-axis. Due to the fact, that gnuPlots starts row-counting
		// with 0, this line is needed as a dummy-line
		adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " " + 0
				+ " ");

		if (allDataMap != null && allDataMap.keySet() != null) {
			Iterator<Integer> allIter = allDataMap.descendingKeySet()
					.iterator();
			Iterator<Integer> overlayIter = overlayDataMap.descendingKeySet()
					.iterator();
			Iterator<Integer> skyNetIter = skyNetDataMap.descendingKeySet()
					.iterator();
			Iterator<Integer> metricUpdateIter = metricUpdateDataMap
					.descendingKeySet().iterator();
			Iterator<Integer> metricUpdateACKIter = metricUpdateACKDataMap
					.descendingKeySet().iterator();
			Iterator<Integer> attributeUpdateIter = attributeUpdateDataMap
					.descendingKeySet().iterator();
			Integer allAmount = null;
			Integer overlayAmount = null;
			Integer skyNetAmount = null;
			Integer metricUpdateAmount = null;
			Integer metricUpdateACKAmount = null;
			Integer attributeUpdateAmount = null;
			int allNumber = 0;
			int overlayNumber = 0;
			int skyNetNumber = 0;
			int metricUpdateNumber = 0;
			int metricUpdateACKNumber = 0;
			int attributeUpdateNumber = 0;
			boolean allRun = true;
			boolean overlayRun = true;
			boolean skyNetRun = true;
			boolean metricUpdateRun = true;
			boolean metricUpdateACKRun = true;
			boolean attributeUpdateRun = true;
			while (allRun || overlayRun || skyNetRun || metricUpdateRun
					|| metricUpdateACKRun || attributeUpdateRun) {
				// print all messages
				if (allNumber == 0) {
					if (allIter.hasNext()) {
						allAmount = allIter.next();
						allNumber = allDataMap.get(allAmount).intValue();
					} else {
						allRun = false;
					}
				}
				if (allRun) {
					adFile.print(allAmount + " ");
					allNumber--;
				}

				// print overlay messages
				if (overlayNumber == 0) {
					if (overlayIter.hasNext()) {
						overlayAmount = overlayIter.next();
						overlayNumber = overlayDataMap.get(overlayAmount)
								.intValue();
					} else {
						overlayRun = false;
					}
				}
				if (overlayRun) {
					adFile.print(overlayAmount + " ");
					overlayNumber--;
				}

				// print skyNet messages
				if (skyNetNumber == 0) {
					if (skyNetIter.hasNext()) {
						skyNetAmount = skyNetIter.next();
						skyNetNumber = skyNetDataMap.get(skyNetAmount)
								.intValue();
					} else {
						skyNetRun = false;
					}
				}
				if (skyNetRun) {
					adFile.print(skyNetAmount + " ");
					skyNetNumber--;
				}

				// print metricUpdate messages
				if (metricUpdateNumber == 0) {
					if (metricUpdateIter.hasNext()) {
						metricUpdateAmount = metricUpdateIter.next();
						metricUpdateNumber = metricUpdateDataMap.get(
								metricUpdateAmount).intValue();
					} else {
						metricUpdateRun = false;
					}
				}
				if (metricUpdateRun) {
					adFile.print(metricUpdateAmount + " ");
					metricUpdateNumber--;
				}

				// print metricUpdateACK messages
				if (metricUpdateACKNumber == 0) {
					if (metricUpdateACKIter.hasNext()) {
						metricUpdateACKAmount = metricUpdateACKIter.next();
						metricUpdateACKNumber = metricUpdateACKDataMap.get(
								metricUpdateACKAmount).intValue();
					} else {
						metricUpdateACKRun = false;
					}
				}
				if (metricUpdateACKRun) {
					adFile.print(metricUpdateACKAmount + " ");
					metricUpdateACKNumber--;
				}

				// print attributeUpdate messages
				if (attributeUpdateNumber == 0) {
					if (attributeUpdateIter.hasNext()) {
						attributeUpdateAmount = attributeUpdateIter.next();
						attributeUpdateNumber = attributeUpdateDataMap.get(
								attributeUpdateAmount).intValue();
					} else {
						attributeUpdateRun = false;
					}
				}
				if (attributeUpdateRun) {
					adFile.println(attributeUpdateAmount);
					attributeUpdateNumber--;
				}
			}
			if (allDataMap.descendingKeySet().size() == 0) {
				adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " "
						+ 0 + " ");
			}
		} else {
			adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " " + 0
					+ " ");
		}
		adFile.flush();
		adFile.close();
	}

	private void writeSortedSizeData(String fileName, String infix,
			TreeMap<Long, Integer> allDataMap,
			TreeMap<Long, Integer> overlayDataMap,
			TreeMap<Long, Integer> skyNetDataMap,
			TreeMap<Long, Integer> metricUpdateDataMap,
			TreeMap<Long, Integer> metricUpdateACKDataMap,
			TreeMap<Long, Integer> attributeUpdateDataMap) {
		try {
			adFile = new PrintWriter(new BufferedWriter(
					new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		adFile.println("# Sorted after Size of all " + infix + " Messages");
		adFile.println("# Sorted after Size of " + infix + " overlay Messages");
		adFile.println("# Sorted after Size of " + infix + " skyNet Messages");
		adFile.println("# Sorted after Size of " + infix
				+ " metricUpdate Messages");
		adFile.println("# Sorted after Size of " + infix
				+ " metricUpdateACK Messages");
		adFile.println("# Sorted after Size of " + infix
				+ " attributeUpdate Messages");
		adFile.println();

		// this line is needed, since the plots for this data use a log-scale
		// for the x-axis. Due to the fact, that gnuPlots starts row-counting
		// with 0, this line is needed as a dummy-line
		adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " " + 0
				+ " ");

		if (allDataMap != null && allDataMap.keySet() != null) {
			Iterator<Long> allIter = allDataMap.descendingKeySet().iterator();
			Iterator<Long> overlayIter = overlayDataMap.descendingKeySet()
					.iterator();
			Iterator<Long> skyNetIter = skyNetDataMap.descendingKeySet()
					.iterator();
			Iterator<Long> metricUpdateIter = metricUpdateDataMap
					.descendingKeySet().iterator();
			Iterator<Long> metricUpdateACKIter = metricUpdateACKDataMap
					.descendingKeySet().iterator();
			Iterator<Long> attributeUpdateIter = attributeUpdateDataMap
					.descendingKeySet().iterator();
			Long allAmount = null;
			Long overlayAmount = null;
			Long skyNetAmount = null;
			Long metricUpdateAmount = null;
			Long metricUpdateACKAmount = null;
			Long attributeUpdateAmount = null;
			int allNumber = 0;
			int overlayNumber = 0;
			int skyNetNumber = 0;
			int metricUpdateNumber = 0;
			int metricUpdateACKNumber = 0;
			int attributeUpdateNumber = 0;
			boolean allRun = true;
			boolean overlayRun = true;
			boolean skyNetRun = true;
			boolean metricUpdateRun = true;
			boolean metricUpdateACKRun = true;
			boolean attributeUpdateRun = true;
			while (allRun || overlayRun || skyNetRun || metricUpdateRun
					|| metricUpdateACKRun || attributeUpdateRun) {
				// print all messages
				if (allNumber == 0) {
					if (allIter.hasNext()) {
						allAmount = allIter.next();
						allNumber = allDataMap.get(allAmount).intValue();
					} else {
						allRun = false;
					}
				}
				if (allRun) {
					adFile.print(allAmount + " ");
					allNumber--;
				}

				// print overlay messages
				if (overlayNumber == 0) {
					if (overlayIter.hasNext()) {
						overlayAmount = overlayIter.next();
						overlayNumber = overlayDataMap.get(overlayAmount)
								.intValue();
					} else {
						overlayRun = false;
					}
				}
				if (overlayRun) {
					adFile.print(overlayAmount + " ");
					overlayNumber--;
				}

				// print skyNet messages
				if (skyNetNumber == 0) {
					if (skyNetIter.hasNext()) {
						skyNetAmount = skyNetIter.next();
						skyNetNumber = skyNetDataMap.get(skyNetAmount)
								.intValue();
					} else {
						skyNetRun = false;
					}
				}
				if (skyNetRun) {
					adFile.print(skyNetAmount + " ");
					skyNetNumber--;
				}

				// print metricUpdate messages
				if (metricUpdateNumber == 0) {
					if (metricUpdateIter.hasNext()) {
						metricUpdateAmount = metricUpdateIter.next();
						metricUpdateNumber = metricUpdateDataMap.get(
								metricUpdateAmount).intValue();
					} else {
						metricUpdateRun = false;
					}
				}
				if (metricUpdateRun) {
					adFile.print(metricUpdateAmount + " ");
					metricUpdateNumber--;
				}

				// print metricUpdateACK messages
				if (metricUpdateACKNumber == 0) {
					if (metricUpdateACKIter.hasNext()) {
						metricUpdateACKAmount = metricUpdateACKIter.next();
						metricUpdateACKNumber = metricUpdateACKDataMap.get(
								metricUpdateACKAmount).intValue();
					} else {
						metricUpdateACKRun = false;
					}
				}
				if (metricUpdateACKRun) {
					adFile.print(metricUpdateACKAmount + " ");
					metricUpdateACKNumber--;
				}

				// print attributeUpdate messages
				if (attributeUpdateNumber == 0) {
					if (attributeUpdateIter.hasNext()) {
						attributeUpdateAmount = attributeUpdateIter.next();
						attributeUpdateNumber = attributeUpdateDataMap.get(
								attributeUpdateAmount).intValue();
					} else {
						attributeUpdateRun = false;
					}
				}
				if (attributeUpdateRun) {
					adFile.println(attributeUpdateAmount);
					attributeUpdateNumber--;
				}
			}
			if (allDataMap.descendingKeySet().size() == 0) {
				adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " "
						+ 0 + " ");
			}
		} else {
			adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " " + 0
					+ " ");
		}
		adFile.flush();
		adFile.close();
	}

	private void writeData(String fileName, String infix,
			TreeMap<BigInteger, DataEntry> dataMap) {
		try {
			adFile = new PrintWriter(new BufferedWriter(
					new FileWriter(fileName)), true);
			adFile.println("# PeerID");
			adFile.println("# Number of all " + infix + " Messages");
			adFile.println("# Size of all " + infix + " Messages");
			adFile.println("# Number of " + infix + " Overlay-Messages");
			adFile.println("# Size of " + infix + " Overlay-Messages");
			adFile.println("# Number of " + infix + " SkyNet-Messages");
			adFile.println("# Size of " + infix + " SkyNet-Messages");
			adFile.println("# Number of " + infix + " MetricUpdate-Messages");
			adFile.println("# Size of " + infix + " MetricUpdate-Messages");
			adFile
					.println("# Number of " + infix
							+ " MetricUpdateACK-Messages");
			adFile.println("# Size of " + infix + " MetricUpdateACK-Messages");
			adFile
					.println("# Number of " + infix
							+ " AttributeUpdate-Messages");
			adFile.println("# Size of " + infix + " AttributeUpdate-Messages");
			adFile.println("# PeerIP");
			adFile.println();
			if (dataMap != null && dataMap.keySet() != null) {
				Iterator<BigInteger> iter = dataMap.keySet().iterator();
				BigInteger id = null;
				DataEntry entry = null;
				while (iter.hasNext()) {
					id = iter.next();
					entry = dataMap.get(id);
					adFile.println(id.toString() + " " + entry.getAllMessage()
							+ " " + entry.getAllMessageSize() + " "
							+ entry.getOverlayMessage() + " "
							+ entry.getOverlayMessageSize() + " "
							+ entry.getSkyNetMessage() + " "
							+ entry.getSkyNetMessageSize() + " "
							+ entry.getMetricUpdateMessage() + " "
							+ entry.getMetricUpdateMessageSize() + " "
							+ entry.getMetricUpdateACKMessage() + " "
							+ entry.getMetricUpdateACKMessageSize() + " "
							+ entry.getAttributeUpdateMessage() + " "
							+ entry.getAttributeUpdateMessageSize() + " "
							+ entry.getId().toString());
				}
				if (dataMap.keySet().size() == 0) {
					adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0
							+ " " + 0 + " " + 0 + " " + 0);
				}
			} else {
				adFile.println(0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " "
						+ 0 + " " + 0 + " " + 0);
			}
			adFile.flush();
			adFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getListOfTempFiles(String dir) {
		File fileDir = new File(dir);
		if (checkForDirectory(fileDir)) {
			files = fileDir.list();
		}
	}

	private static boolean checkForDirectory(File name) {
		if (name.exists() && name.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}
}
