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

package org.peerfact.impl.analyzer.visualization2d.ui.common.gnuplot;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * For selecting the relevant Dat-file.
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class GnuplotFileChooser extends JFileChooser {

	static final String CFG_LAST_PATH = "UI/Gnuplot/lastPath";

	static final String FILE_EXTENSION = "dat";

	Component parent;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7876626268968056079L;

	public GnuplotFileChooser(Component parent) {
		this.parent = parent;
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Gnuplot tables", FILE_EXTENSION);
		this.setFileFilter(filter);

		this.setCurrentDirectory(new File(Config.getValue(CFG_LAST_PATH, "")));
	}

	public File askWhereToSave() {
		if (this.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			Config.setValue(CFG_LAST_PATH, this.getSelectedFile()
					.getAbsolutePath());
			return this.validateFileName(this.getSelectedFile());
		} else {
			return null;
		}
	}

	private File validateFileName(final File file) {

		FileFilter filter = this.getFileFilter();

		if (filter.accept(file)) {
			return file;
		}
		String fileName = file.getName();
		final int index = fileName.lastIndexOf(".");
		if (index > 0) {
			fileName = fileName.substring(0, index);
		}

		final String newFileName = fileName + "." + FILE_EXTENSION;

		return new File(file.getParent(), newFileName);
	}

}
