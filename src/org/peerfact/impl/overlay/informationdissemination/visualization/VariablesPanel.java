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

package org.peerfact.impl.overlay.informationdissemination.visualization;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.peerfact.api.overlay.ido.IDONode;
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
 * 
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class VariablesPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6173325402800352676L;

	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(VariablesPanel.class);

	private JTree variablesTree = null;

	private VisWindow mainWindow = null;

	public VariablesPanel(VisWindow mainWindow) {
		super();
		this.variablesTree = getVariablesTree();
		this.add(variablesTree);
		this.mainWindow = mainWindow;
	}

	public JTree getVariablesTree() {
		if (variablesTree == null) {
			variablesTree = new JTree();
			// update with empty list! Create Data for jTree
			updateVariablesTree(new LinkedHashSet<IDONode<?, ?>>());

			variablesTree.addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode selected = (DefaultMutableTreeNode) getVariablesTree()
							.getLastSelectedPathComponent();
					if (selected != null
							&& selected.getUserObject() instanceof FieldTuple) {
						FieldTuple ft = (FieldTuple) selected.getUserObject();
						VariablesPanel.this.mainWindow.getOutputField()
								.setText(ft.getValue());
					}
				}
			});
		}
		return variablesTree;
	}

	private void buildVariablesTree(Object object,
			DefaultMutableTreeNode parent, int maxRecursion) {
		if (maxRecursion >= 0 && object != null && parent != null) {
			List<Field> fields = getAllFields(object);

			for (Field field : fields) {
				try {
					field.setAccessible(true);
					Object nextObject = field.get(object);
					String name = field.getName() + " : "
							+ field.getType().getSimpleName();
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(
							new FieldTuple(name, nextObject));
					parent.add(child);
					if (!(field.isSynthetic() || field.isEnumConstant() || isPrimitivOrWrapper(field
							.getType()))) {
						buildVariablesTree(nextObject, child, maxRecursion - 1);
					}
					field.setAccessible(false);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean isPrimitivOrWrapper(Class<?> type) {
		if (type.isAssignableFrom(int.class)) {
			return true;
		}
		if (type.isAssignableFrom(Integer.class)) {
			return true;
		}
		if (type.isAssignableFrom(long.class)) {
			return true;
		}
		if (type.isAssignableFrom(Long.class)) {
			return true;
		}
		if (type.isAssignableFrom(float.class)) {
			return true;
		}
		if (type.isAssignableFrom(Float.class)) {
			return true;
		}
		if (type.isAssignableFrom(double.class)) {
			return true;
		}
		if (type.isAssignableFrom(Double.class)) {
			return true;
		}
		if (type.isAssignableFrom(boolean.class)) {
			return true;
		}
		if (type.isAssignableFrom(Boolean.class)) {
			return true;
		}
		if (type.isAssignableFrom(char.class)) {
			return true;
		}
		if (type.isAssignableFrom(Character.class)) {
			return true;
		}
		if (type.isAssignableFrom(short.class)) {
			return true;
		}
		if (type.isAssignableFrom(Short.class)) {
			return true;
		}
		if (type.isAssignableFrom(byte.class)) {
			return true;
		}
		if (type.isAssignableFrom(Byte.class)) {
			return true;
		}
		if (type.isAssignableFrom(String.class)) {
			return true;
		}
		return false;
	}

	private static List<Field> getAllFields(Object object) {
		List<Field> fields = new Vector<Field>();
		Class<? extends Object> clazz = object.getClass();
		while (clazz != null && clazz.getDeclaredFields().length != 0) {
			for (int i = 0; i < clazz.getDeclaredFields().length; i++) {
				fields.add(clazz.getDeclaredFields()[i]);
			}
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	public void updateVariablesTree(LinkedHashSet<IDONode<?, ?>> nodes) {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				new FieldTuple("Selected", "Nothing"));
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		getVariablesTree().setModel(treeModel);

		getVariablesTree().setCellRenderer(new DefaultTreeCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9187720460272477792L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean focus) {

				Component comp = super.getTreeCellRendererComponent(tree,
						value, sel, expanded, leaf, row, focus);
				Object ow = ((DefaultMutableTreeNode) value).getUserObject();
				if (ow instanceof FieldTuple) {
					FieldTuple fieldTuple = (FieldTuple) ow;
					setText(fieldTuple.getName());
				}
				return comp;
			}
		});

		for (IDONode<?, ?> node : nodes) {
			DefaultMutableTreeNode selectedNode = new DefaultMutableTreeNode(
					new FieldTuple("Node : IDONode Nr=" + node.getOverlayID(),
							node));
			buildVariablesTree(node, selectedNode, 5);
			root.add(selectedNode);
		}
		variablesTree.expandRow(0);
	}

	protected static class FieldTuple {
		String name;

		Object value;

		public FieldTuple(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			if (value != null) {
				try {
					if (value.getClass().isArray()) {
						Object[] temp = (Object[]) value;
						StringBuffer buf = new StringBuffer("[");
						for (int i = 0; i < temp.length; i++) {
							buf.append(temp[i].toString());
							buf.append(", ");
						}
						return buf.substring(0, buf.length() - 2) + "]";
					}
				} catch (ClassCastException e) {
					log.warn("Casting to array was not successful!", e);
					return value.toString();
				}
				try {
					return value.toString();
				} catch (ConcurrentModificationException e) {
					return "Hold on the simulation and try again! Datastructure is not Thread-Safe!";
				}
			}
			return "null";
		}
	}
}
