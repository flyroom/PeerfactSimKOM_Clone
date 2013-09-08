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

package org.peerfact.impl.analyzer.visualization2d.ui.common.config.general;

import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.gui.LookAndFeel;


/**
 * ComboBox to select a LookAndFeel.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LaFComboBox extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8908823686650489258L;

	public LaFComboBox(List<UIManager.LookAndFeelInfo> list) {
		for (Object o : list) {
			this.addItem(o);
		}

		setStartSelection(list);
		this.setRenderer(new LaFComboBox.LaFRenderer());
	}

	private void setStartSelection(List<UIManager.LookAndFeelInfo> list) {
		for (UIManager.LookAndFeelInfo laf : list) {
			if (laf.getClassName()
					.equals(LookAndFeel.getActivatedLookAndFeel())) {
				this.setSelectedItem(laf);
			}
		}
	}

	/**
	 * Commits the changes
	 */
	public void commit() {
		String selectedClassName = ((UIManager.LookAndFeelInfo) getSelectedItem())
				.getClassName();

		Config.setValue("UI/LookAndFeel", selectedClassName);
		LookAndFeel.setLookAndFeel(selectedClassName);
	}

	public static class LaFRenderer implements ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList arg0,
				Object arg1,
				int arg2, boolean arg3, boolean arg4) {
			return new JLabel(((UIManager.LookAndFeelInfo) arg1).getName());
		}

	}

}
