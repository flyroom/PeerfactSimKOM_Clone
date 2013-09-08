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

package org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar;

import javax.swing.JToolBar;

import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.CloseApplicationButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.ConfigButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.ExportGnuplotButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.InfoButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.LoadFromFileButton;
import org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements.SaveToFileButton;


/**
 * The menu bar on the main features
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MainToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -69746928225062713L;

	public MainToolBar() {
		this.add(new LoadFromFileButton());
		this.add(new SaveToFileButton());
		this.add(new ExportGnuplotButton());
		this.add(new ConfigButton());
		this.add(new InfoButton());
		this.add(new CloseApplicationButton());
	}

}
