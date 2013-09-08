package org.peerfact.impl.service.aggregation.centralizedmonitoring.server.tree;

import static java.lang.Math.pow;

import java.util.ArrayList;

import org.peerfact.api.common.Component;
import org.peerfact.api.common.ComponentFactory;
import org.peerfact.api.common.Host;
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
 * 
 * Factory to construct a full N-ary tree with configurable height and width.
 * Full means every node has the same number of children except of the leafs.
 * 
 * @author Alexander Nigl
 * 
 */
public class ServerTreeFactory implements ComponentFactory {

	int currentParent;

	int childCounter;

	private double height;

	private double width;

	private ArrayList<ITreeNodeApplication> nodes;

	public ServerTreeFactory() {
		this.nodes = new ArrayList<ITreeNodeApplication>();
		this.childCounter = -1;
		this.currentParent = 0;
	}

	/**
	 * Returns number of inner Nodes (inclusive root node)
	 * 
	 * @return number of inner nodes
	 */
	private double numberOfInnerNodes() {
		return this.numberOfNodes() - this.numberOfLeaves();
	}

	private double numberOfNodes() {
		return (pow(this.width, this.height + 1) - 1) / (this.width - 1);
	}

	private double numberOfLeaves() {
		return pow(this.width, this.height);
	}

	/**
	 * Sets height of the server tree.
	 * 
	 * @param height
	 *            of the tree
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Sets number of children for each node.
	 * 
	 * @param width
	 *            number of children
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public Component createComponent(Host host) {
		ITreeNodeApplication c;
		if (this.nodes.size() == 0) {
			c = new RootNodeApplication<Object>(host);
		} else if (this.nodes.size() < this.numberOfInnerNodes() - 1) {
			c = new InnerNodeApplication<Object>(host);
			this.nodes.add(c);
		} else if (this.nodes.size() >= this.numberOfInnerNodes()
				&& this.nodes.size() < this.numberOfNodes()) {
			c = new LeafNodeApplication<Object>(host);
			BootstrapInfo.addServer(host.getTransLayer().getLocalTransInfo(
					Configuration.SERVER_PORT));
		} else {
			throw new RuntimeException("Too much server nodes.");
		}
		this.nodes.add(c);
		c.setParent(this.nodes.get(this.currentParent).getHost()
				.getTransLayer().getLocalTransInfo(Configuration.SERVER_PORT));
		c.setNumberOfChildren(this.width);
		this.childCounter++;
		if (this.childCounter == this.width) {
			this.currentParent++;
			this.childCounter = 0;
		}
		return c;
	}
}
