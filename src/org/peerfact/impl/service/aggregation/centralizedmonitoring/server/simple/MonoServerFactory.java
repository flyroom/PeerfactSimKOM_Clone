package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.simple;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.AttributeIdentifier;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.Configuration;
import org.peerfact.impl.service.aggregation.centralizedmonitoring.server.BootstrapInfo;

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
public class MonoServerFactory implements ComponentFactory {

	@Override
	public Component createComponent(Host host) {
		BootstrapInfo.addServer(host.getTransLayer().getLocalTransInfo(
				Configuration.SERVER_PORT));
		return new CMonMonoServerApplication<AttributeIdentifier>(host,
				Configuration.SERVER_PORT);
	}

}
