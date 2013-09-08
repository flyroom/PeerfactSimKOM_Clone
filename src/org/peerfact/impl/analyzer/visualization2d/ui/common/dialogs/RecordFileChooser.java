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

package org.peerfact.impl.analyzer.visualization2d.ui.common.dialogs;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.model.VisDataModel;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * File selection dialog, with the new records can be loaded and saved.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 */
public class RecordFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1308955789418633424L;

	private static Logger log = SimLogger
			.getLogger(RecordFileChooser.class);

	static final String CFG_LAST_PATH = "UI/Dialogs/LoadSave/lastPath";

	static final String FILE_EXTENSION = "peerfact";

	public RecordFileChooser() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Visualization recording", FILE_EXTENSION);
		this.setFileFilter(filter);

		this.setCurrentDirectory(new File(Config.getValue(CFG_LAST_PATH, "")));
	}

	/**
	 * Asks for a file to open. When a file is selected, it opens and if
	 * successful returns true, otherwise false.
	 * 
	 * @return
	 */
	public boolean askForOpen() {
		if (this.showOpenDialog(Controller.getUIMainWindow()) == JFileChooser.APPROVE_OPTION) {
			RecordFileChooser.openFile(this.getSelectedFile());
			setLastDirectory();
			return true;
		}
		return false;
	}

	/**
	 * Asks where the file should be saved. When a file is selected, it opens
	 * and if successful returns true, otherwise false.
	 * 
	 * @return
	 */
	public boolean askForSave() {
		if (this.showSaveDialog(Controller.getUIMainWindow()) == JFileChooser.APPROVE_OPTION) {
			RecordFileChooser.saveFile(this.validateFileName(this
					.getSelectedFile()));
			setLastDirectory();
			return true;
		}
		return false;
	}

	private void setLastDirectory() {
		Config.setValue(CFG_LAST_PATH, this.getCurrentDirectory()
				.getAbsolutePath());
	}

	private static void saveFile(File selectedFile) {
		try {
			Controller.getModel().saveTo(selectedFile);
			Controller.getUIMainWindow().setTitleFileName(
					selectedFile.getName());
		} catch (IOException e) {
			log.debug("Exception" + e.getMessage()
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private static void openFile(File selectedFile) {
		try {
			Controller.loadModelFrontend(VisDataModel.fromFile(selectedFile));
			Controller.getModel().setName(selectedFile.getName());
		} catch (IOException e) {
			log.debug("Exception");
			e.printStackTrace();
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
