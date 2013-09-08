package org.peerfact.impl.service.aggregation.centralizedmonitoring.peer;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.peerfact.api.common.Host;
import org.peerfact.api.network.NetID;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.AttributeIdentifier;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.analyzer.CMonNetAnalyzer;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.message.content.Attribute;
import org.peerfact.impl.util.functiongenerator.FunctionGenerator;
import org.peerfact.impl.util.functiongenerator.functions.Function;


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
public class RealAttributeFactory implements
		IAttributeFactory<AttributeIdentifier> {

	private Host host;

	private NetID netID;

	private CMonNetAnalyzer netAnalyzer;

	public RealAttributeFactory(Host host, CMonNetAnalyzer netAnalyzer) {
		this.host = host;
		this.netID = host.getNetLayer().getNetID();
		this.netAnalyzer = netAnalyzer;
	}

	@Override
	public Collection<Attribute<AttributeIdentifier>> getAtts() {
		Collection<Attribute<AttributeIdentifier>> result = new LinkedList<Attribute<AttributeIdentifier>>();
		result.add(new Attribute<AttributeIdentifier>(
				AttributeIdentifier.NET_SENT, this.netAnalyzer.getSentMsg(
						this.netID).doubleValue()));
		result.add(new Attribute<AttributeIdentifier>(
				AttributeIdentifier.NET_RECEIVED, this.netAnalyzer
						.getReceivedMsg(this.netID).doubleValue()));
		result.add(new Attribute<AttributeIdentifier>(
				AttributeIdentifier.BW_DOWN, this.host.getNetLayer()
						.getCurrentBandwidth().getDownBW()));
		result.add(new Attribute<AttributeIdentifier>(
				AttributeIdentifier.BW_UP, this.host.getNetLayer()
						.getCurrentBandwidth().getUpBW()));

		// added the different functions of the function generator

		FunctionGenerator generator = FunctionGenerator.getInstance();
		Map<Class<? extends Function>, Double> functionResults = generator
				.getValues();
		if (functionResults != null && functionResults.size() > 0) {
			Iterator<Class<? extends Function>> iter = functionResults.keySet()
					.iterator();
			Class<? extends Function> clazz = null;
			while (iter.hasNext()) {
				clazz = iter.next();
				String name = clazz.getSimpleName();

				result.add(new Attribute<AttributeIdentifier>(
						Enum.valueOf(AttributeIdentifier.class, "Function_"
								+ name),
						functionResults.get(clazz)));
			}
		}

		return result;
	}

}
