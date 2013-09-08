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

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.ui.common.config.general.LaFComboBox;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.gui.LookAndFeel;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public class GeneralTab extends AbstractConfigTab {

	/**
	 * 
	 */
	private static final long serialVersionUID = -721679104977758152L;

	private LaFComboBox LaFBox;

	private JLabel imageLabel = new JLabel("No image loaded");

	private String backgroundImagePath = null;

	boolean useBackgroundImage;

	public static int IMAGE_PREVIEW_WIDTH = 300;

	public GeneralTab() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(createLaFSelector());
		this.add(createBackgroundSelector());

	}

	/*
	 * LookAndFeel
	 */

	private JPanel createLaFSelector() {
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		pane.setBorder(new TitledBorder("Look and Feel"));

		pane.add(new JLabel("LookAndFeel for the application:"));

		LaFBox = new LaFComboBox(LookAndFeel.getAllLookAndFeels());
		pane.add(LaFBox);

		pane.add(new JLabel(
				"(The LookAndFeel applies only to new loaded windows.)"));

		return pane;
	}

	/**
	 * Creates the subpanel for the background image properties
	 * 
	 * @return
	 */
	private JPanel createBackgroundSelector() {
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		// Set the title of the subpanel
		pane.setBorder(new TitledBorder("Background image"));

		// Get path for last used image
		String lastBackgroundPath = Config.getValue("UI/LastBackgroundImage",
				"");

		// Determine if the background image was used last time
		useBackgroundImage = Boolean.parseBoolean(Config.getValue(
				"UI/BackgroundImageEnabled", "false"));

		// Create checkbox
		JCheckBox enableBackgroundCheckbox = new JCheckBox(
				"Use this image as background image", useBackgroundImage);

		// Create a listener to react on checkbox changes
		ItemListener enableBackgroundListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				// Toggle the flag
				useBackgroundImage = !useBackgroundImage;
			}
		};

		// Add listener to checkbox
		enableBackgroundCheckbox.addItemListener(enableBackgroundListener);

		// Add checkbox to panel
		pane.add(enableBackgroundCheckbox);

		Image image = null;

		try {
			// Read last used image
			image = ImageIO.read(new File(lastBackgroundPath));

			imageLabel = new JLabel();

			// Setup the preview for the read image
			setBackgroundPreviewImage(image);
		} catch (IOException e) {
			// Nothing to do
		}

		// Add the created JLabel to the pane
		pane.add(imageLabel);

		// Create the JButton to load new images
		JButton pictureChooser = new JButton("Choose new image...");
		pictureChooser.addActionListener(new ImageChooser(this));

		// Add the JButton to the pane
		pane.add(pictureChooser);

		pane.add(new JLabel(
				"This setting requires a restart of the visualization!"));

		return pane;
	}

	/**
	 * Set the preview image to the given image
	 * 
	 * @param image
	 */
	protected void setBackgroundPreviewImage(Image image) {

		int oldWidth = image.getWidth(null);
		int oldHeight = image.getHeight(null);

		// Calculate new dimensions
		int newWidth = IMAGE_PREVIEW_WIDTH;
		float scaleFactor = (float) newWidth / (float) oldWidth;
		int newHeight = Math.round(oldHeight * scaleFactor);

		Image scaledImage = image
				.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

		imageLabel.setText("");
		imageLabel.setIcon(new ImageIcon(scaledImage));

		imageLabel.repaint();

	}

	public void setBackgroundImagePath(String path) {
		backgroundImagePath = path;
	}

	@Override
	public void commitSettings() {
		LaFBox.commit();

		if (backgroundImagePath != null) {
			Config.setValue("UI/LastBackgroundImage", backgroundImagePath);
		}

		Config.setValue("UI/BackgroundImageEnabled",
				((Boolean) useBackgroundImage).toString());

		if (useBackgroundImage) {
			Controller.getVisApi().setBackgroundImagePath(backgroundImagePath);
		} else {
			Controller.getVisApi().setBackgroundImagePath(null);
		}
	}

}
