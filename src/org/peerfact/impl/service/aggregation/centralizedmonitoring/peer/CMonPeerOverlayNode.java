package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.peerfact.api.common.ConnectivityEvent;
import org.peerfact.api.common.Message;
import org.peerfact.api.common.OperationCallback;
import org.peerfact.api.network.NetLayer;
import org.peerfact.api.overlay.NeighborDeterminator;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.api.service.aggr.AggregationMap;
import org.peerfact.api.service.aggr.AggregationResult;
import org.peerfact.api.service.aggr.NoSuchValueException;
import org.peerfact.api.transport.TransInfo;
import org.peerfact.api.transport.TransLayer;
import org.peerfact.api.transport.TransMessageListener;
import org.peerfact.impl.overlay.AbstractOverlayNode;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.CMonResultMessage;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Aggregate;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.peer.operation.PeriodicalySendAttributes;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.transport.TransMsgEvent;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This part of the Simulator is not maintained in the current version of
 * PeerfactSim.KOM. There is no intention of the authors to fix this
 * circumstances, since the changes needed are huge compared to overall benefit.
 * 
 * If you want it to work correctly, you are free to make the specific changes
 * and provide it to the community.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class CMonPeerOverlayNode<ID extends Object> extends
		AbstractOverlayNode<OverlayID<?>, OverlayContact<OverlayID<?>>>
		implements TransMessageListener,
		IOverlay<OverlayID<?>, OverlayContact<OverlayID<?>>, ID> {

	protected static final Logger log = SimLogger
			.getLogger(CMonPeerOverlayNode.class);

	private TransInfo server;

	private long delay = Configuration.PUSH_DELAY;

	private AttributeManager<ID> attributes;

	protected PeriodicalySendAttributes<ID> updateOp;

	private LinkedHashMap<ID, Aggregate<ID>> globalState;

	private int dummyMetricsCounter;

	private long globalAggregationReceivingTime;

	protected CMonPeerOverlayNode(OverlayID<?> peerId, short port) {
		super(peerId, port);
		this.globalState = new LinkedHashMap<ID, Aggregate<ID>>();
		dummyMetricsCounter = 0;
		globalAggregationReceivingTime = 0;
	}

	public TransInfo getServer() {
		return this.server;
	}

	/**
	 * Set aggregation delay in milliseconds
	 * 
	 * @param delay
	 *            in milliseconds
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setServer(TransInfo server) {
		this.server = server;
	}

	public void setAttributeFactory(IAttributeFactory<ID> attFactory) {
		this.attributes = new AttributeManager<ID>(attFactory);
	}

	public void init() {
		NetLayer nl = this.getHost().getNetLayer();
		nl.addConnectivityListener(this);
		this.connectivityChanged(new ConnectivityEvent(this, nl.isOnline()));
	}

	@Override
	public TransLayer getTransLayer() {
		return this.getHost().getTransLayer();
	}

	@Override
	public NeighborDeterminator<OverlayContact<OverlayID<?>>> getNeighbors() {
		return null; // Don't have to know any neighbors.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOnline()) {
			this.setPeerStatus(PeerStatus.PRESENT);
			this.updateOp = new PeriodicalySendAttributes<ID>(this);
		} else if (ce.isOffline()) {
			this.setPeerStatus(PeerStatus.ABSENT);
			this.updateOp.stop();
		}
	}

	@Override
	public double getLocalValue(Object identifier) throws NoSuchValueException {
		return this.attributes.getValue((ID) identifier);
	}

	@Override
	public double setLocalValue(Object identifier, double value)
			throws NoSuchValueException {
		Double result = this.attributes.setValue((ID) identifier, value);
		return result != null ? result : Double.NaN;
	}

	public long getDelay() {
		return this.delay;
	}

	@Override
	public int getAggregationResult(Object identifier,
			OperationCallback<AggregationResult> callback)
			throws NoSuchValueException {
		ID id = (ID) identifier;
		if (this.globalState.containsKey(id)) {
			return (int) this.globalState.get(id).getAverage();
		}
		throw new NoSuchValueException(identifier);
	}

	@Override
	public void join(OperationCallback<Object> cb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leave(OperationCallback<Object> cb) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAggregationResultMap(
			OperationCallback<AggregationMap<Object>> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	public AttributeManager<ID> getAttributeManager() {
		return this.attributes;
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof CMonResultMessage) {
			CMonResultMessage<ID> stateMsg = (CMonResultMessage<ID>) msg;

			log.debug(Simulator.getSimulatedRealtime()
					+ " - "
					+ getHost().getNetLayer().getNetID()
					+ " RECEIVED "
					+ (stateMsg.getInfos().size() + stateMsg
							.getDummyMetricsCounter())
					+ " infos to the server with size " + stateMsg.getSize());

			this.setGlobalState(stateMsg.getInfos());
			this.globalAggregationReceivingTime = Simulator.getCurrentTime();
		}
	}

	private void setGlobalState(LinkedHashMap<ID, Aggregate<ID>> infos) {
		this.globalState = infos;
	}

	@Override
	public List<Object> getIdentifiers() {
		List<Object> result = new Vector<Object>();
		result.addAll(globalState.keySet());
		return result;
	}

	@Override
	public AggregationResult getStoredAggregationResult(Object identifier) {
		return this.globalState.get(identifier);
	}

	@Override
	public long getGlobalAggregationReceivingTime(Object identifier) {
		return globalAggregationReceivingTime;
	}

	public void incrementDummyMetrics() {
		dummyMetricsCounter++;
	}

	public void increaseDummyMetrics(int amount) {
		dummyMetricsCounter += amount;
	}

	public int getDummyMetricsCounter() {
		return dummyMetricsCounter;
	}

	@Override
	public int getNumberOfMonitoredAttributes() {
		return attributes.getNumberOfAttributes() + dummyMetricsCounter;
	}

}
