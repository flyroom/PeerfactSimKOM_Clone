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

package org.peerfact.impl.analyzer.visualization2d.ui.common.config;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ImageChooser extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9061069606773506429L;

	private GeneralTab tab;

	public ImageChooser(GeneralTab tab) {
		this.tab = tab;
	}

	/**
	 * Opens the dialog to chose the file and returns an instance of the chosen
	 * file
	 * 
	 * @return the chosen file
	 */
	private File askFileDialog() {
		JFileChooser chooser = new JFileChooser();

		// Set the file filter to .bmp-files
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".bmp")
						|| f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Bitmap picture(*.bmp)";
			}
		});

		chooser.setSelectedFile(new File(Config.getValue(
				"UI/LastBackgroundImage", "")));

		// Open the dialog to choose the file
		int returnVal = chooser.showOpenDialog(this);

		// If dialog was closed by clicking the open button
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// Open the dialog to choose a file
		File file = askFileDialog();
		if (file != null) {
			Image image;
			try {
				// Read file
				image = ImageIO.read(file);

				// Set the image as the new preview image
				tab.setBackgroundPreviewImage(image);

				// Set the path to the image
				tab.setBackgroundImagePath(file.getPath());
			} catch (IOException e1) {
				// Nothing to do
			}

		}

	}

}
