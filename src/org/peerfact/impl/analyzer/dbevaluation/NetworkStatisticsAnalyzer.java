package org.peerfact.impl.analyzer.dbevaluation;

import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peerfact.api.analyzer.NetAnalyzer;
import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.api.network.NetMessage;
import org.peerfact.api.overlay.OverlayNode;
import org.peerfact.api.simengine.SimulationEventHandler;
import org.peerfact.impl.network.IPv4NetID;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.simple.CMonMonoServerApplication;
import org.peerfact.impl.simengine.SimulationEvent;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.oracle.GlobalOracle;


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
 * @author Dominik Stingl
 * 
 */
public class NetworkStatisticsAnalyzer implements NetAnalyzer,
		SimulationEventHandler, IOutputWriterDelegator {

	// constants for the storage

	private final static String COMPLETE_NUMBER_MESSAGES = "COMPLETE_NUMBER_MESSAGES";

	private final static String COMPLETE_SIZE_MESSAGES = "COMPLETE_SIZE_MESSAGES";

	private final static String REC_POSTFIX = "_REC";

	private final static String SENT_POSTFIX = "_SENT";

	private final static String DROPPED_POSTFIX = "_DROPPED";

	private final static String MSG_COUNTER = "MSG_COUNTER";

	private final static String OFFLINE_PEERS = "OFFLINE_PEERS";

	private final static String ONLINE_PEERS = "ONLINE_PEERS";

	private final static String MEASUREMENT_INTERVAL = "MEASUREMENT_INTERVAL";

	private final static String IGNORABLE_IP = "IGNORABLE_IP";

	private final static String[] tableNames = { "sentstatistics",
			"recstatistics", "dropstatistics", "generalstatistics" };

	// fields to store the statistics

	private long sentCounter;

	private long recCounter;

	private long droppedCounter;

	// collections to store the statistics

	private LinkedHashMap<Long, MessageInfo> droppedMsgs;

	private LinkedHashMap<Long, MessageInfo> sentMsgs;

	private LinkedHashMap<Long, MessageInfo> recMsgs;

	// fields for the configuration of this analyzer

	private long measurementInterval;

	private IAnalyzerOutputWriter outputWriter;

	private long lastMeasurement;

	// the constructor

	public NetworkStatisticsAnalyzer() {
		sentCounter = 0;
		recCounter = 0;
		droppedCounter = 0;
		lastMeasurement = 0;
		droppedMsgs = new LinkedHashMap<Long, MessageInfo>();
		sentMsgs = new LinkedHashMap<Long, MessageInfo>();
		recMsgs = new LinkedHashMap<Long, MessageInfo>();
	}

	// ************************************
	// Setting and preparing the analyzer
	// ************************************

	public void setMeasurementInterval(long timeInterval) {
		this.measurementInterval = timeInterval;
	}

	@Override
	public void setAnalyzerOutputWriter(
			IAnalyzerOutputWriter analyzerOutputWriter) {
		this.outputWriter = analyzerOutputWriter;
	}

	// ************************************
	// Starting and stopping the analyzer
	// ************************************

	@Override
	public void start() {
		outputWriter.initialize(tableNames);
		List<Host> hosts = GlobalOracle.getHosts();
		long time = Simulator.getCurrentTime();
		for (Host host : hosts) {
			if (host.getApplication() instanceof CMonMonoServerApplication) {
				outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(
						time, IGNORABLE_IP, ((IPv4NetID) host.getNetLayer()
								.getNetID()).getID()));
			}
		}
		Simulator.scheduleEvent(null, Simulator.getCurrentTime(), this, null);
	}

	@Override
	public void stop(Writer output) {
		long time = Simulator.getCurrentTime();
		extractData(time);
		outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(time,
				MSG_COUNTER + REC_POSTFIX, recCounter));
		outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(time,
				MSG_COUNTER + SENT_POSTFIX, sentCounter));
		outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(time,
				MSG_COUNTER + DROPPED_POSTFIX, droppedCounter));
		// outputWriter.close();
	}

	// ************************************
	// Grabbing the data of the simulation
	// ************************************

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		netMsgProcessor(msg, id, sentMsgs);
		sentCounter++;
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		netMsgProcessor(msg, id, recMsgs);
		recCounter++;
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		netMsgProcessor(msg, id, droppedMsgs);
		droppedCounter++;
	}

	private static void netMsgProcessor(NetMessage msg, NetID id,
			Map<Long, MessageInfo> dataStructure) {
		Long ip = ((IPv4NetID) id).getID();

		MessageInfo res = dataStructure.remove(ip);
		if (res == null) {
			res = new MessageInfo();
		}
		// add the net-message to the total message statistics
		res.incrementCompleteAmount();
		res.increaseCompleteSize(msg.getSize());

		// put updated entry in the storage
		dataStructure.put(ip, res);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		lastMeasurement = Simulator.getCurrentTime();
		extractData(lastMeasurement);
		Simulator.scheduleEvent(null, Simulator.getCurrentTime()
				+ measurementInterval, this, null);
	}

	// ************************************
	// data preparation for persisting
	// ************************************

	private void extractData(long time) {
		List<Host> hostList = GlobalOracle.getHosts();
		int onlineHosts = 0;
		int offlineHosts = 0;
		Long id;

		for (Host host : hostList) {
			boolean online = false;

			// is one overlay online
			Iterator<OverlayNode<?, ?>> ovNos = host.getOverlays();
			while (ovNos.hasNext()) {
				OverlayNode<?, ?> ovNo = ovNos.next();
				if (ovNo.isPresent()) {
					online = true;
					break;
				}
			}

			id = ((IPv4NetID) host.getNetLayer().getNetID()).getID();
			storeData(tableNames[0], sentMsgs, id, time, SENT_POSTFIX);
			storeData(tableNames[1], recMsgs, id, time, REC_POSTFIX);
			storeData(tableNames[2], droppedMsgs, id, time, DROPPED_POSTFIX);

			if (online) {
				onlineHosts++;

			} else {
				offlineHosts++;
			}

		}
		sentMsgs.clear();
		recMsgs.clear();
		droppedMsgs.clear();
		outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(time,
				ONLINE_PEERS, onlineHosts));
		outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(time,
				OFFLINE_PEERS, offlineHosts));
		outputWriter.persist(tableNames[3], new AnalyzerOutputEntry(time,
				MEASUREMENT_INTERVAL, measurementInterval));

	}

	private void storeData(String tableName,
			LinkedHashMap<Long, MessageInfo> msgMap,
			Long id, long time, String postFix) {
		MessageInfo info = msgMap.get(id);
		if (info != null) {
			outputWriter.persist(tableName,
					new AnalyzerOutputEntry(id, time, COMPLETE_NUMBER_MESSAGES
							+ postFix, info.getCompleteAmount()));
			outputWriter.persist(tableName, new AnalyzerOutputEntry(id, time,
					COMPLETE_SIZE_MESSAGES + postFix, info.getCompleteSize()));
		}
	}

	private static class MessageInfo {
		private int completeAmount;

		private long completeSize;

		public MessageInfo() {
			completeAmount = 0;
			completeSize = 0;
		}

		public void incrementCompleteAmount() {
			completeAmount++;
		}

		public void increaseCompleteSize(long newSize) {
			this.completeSize += newSize;
		}

		public int getCompleteAmount() {
			return completeAmount;
		}

		public long getCompleteSize() {
			return completeSize;
		}

	}

}
