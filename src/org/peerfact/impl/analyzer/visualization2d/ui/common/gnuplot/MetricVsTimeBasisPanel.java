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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.peerfact.impl.analyzer.visualization2d.controller.Controller;
import org.peerfact.impl.analyzer.visualization2d.controller.player.Player;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.IResultFileWriter;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.PLTFileBuilder_linespoints;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.ResultTable;
import org.peerfact.impl.analyzer.visualization2d.gnuplot.SimpleGnuplotFileWriter;
import org.peerfact.impl.analyzer.visualization2d.util.Config;
import org.peerfact.impl.analyzer.visualization2d.util.gui.JConfigCheckBox;
import org.peerfact.impl.util.logging.SimLogger;


/**
 * 
 * @author <info@peerfact.org>
 * @author Kalman Graffi <info@peerfact.org>
 * 
 * @version 08/18/2011
 * 
 */
public abstract class MetricVsTimeBasisPanel extends JPanel implements
		ActionListener {
	private static final long serialVersionUID = -7946040448823203271L;

	private static Logger log = SimLogger
			.getLogger(MetricVsTimeBasisPanel.class);

	public BeginEndIntervalPanel timeChoicePanel;

	protected CreateGnuplotFileButtons createGnuplotFileButtons;

	protected JConfigCheckBox generatePLTFile = new JConfigCheckBox(
			"Generate PLT file", "Gnuplot/GeneratePLTFile");

	protected JConfigCheckBox generateGraphics = new JConfigCheckBox(
			"Generate graphics", "Gnuplot/generateGraphics");

	public MetricVsTimeBasisPanel() {
		createTimeChoice();
		createGnuplotFileButtons.getGnuplotButton().addActionListener(this);
		long max_end = Controller.getTimeline().getMaxTime()
				/ Player.TIME_UNIT_MULTIPLICATOR;
		timeChoicePanel.setLowerandUpperBound(0, 0, 1, Long.MAX_VALUE, max_end,
				Long.MAX_VALUE);
	}

	private void createTimeChoice() {
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(600, 300));
		timeChoicePanel = new BeginEndIntervalPanel();
		timeChoicePanel.setBounds(319, 58, 217, 66);

		JPanel sideContainer = new JPanel();
		sideContainer.setLayout(new FlowLayout());
		sideContainer.setPreferredSize(new Dimension(200, 200));
		sideContainer.add(timeChoicePanel);

		sideContainer.add(generatePLTFile);
		sideContainer.add(generateGraphics);

		this.add(sideContainer, BorderLayout.EAST);

		createGnuplotFileButtons = new CreateGnuplotFileButtons();
		this.add(createGnuplotFileButtons, BorderLayout.SOUTH);
		// createGnuplotFileButton.setBounds(326, 184, 221, 50);
	}

	public long[] checkTimeChoiceValue() {
		long start = timeChoicePanel.getStartValue();
		if (start == Long.MIN_VALUE) {
			return null;
		}
		long end = timeChoicePanel.getEndValue();
		if (end == Long.MIN_VALUE) {
			return null;
		}
		long interval = timeChoicePanel.getIntervalValue();
		if (interval == Long.MIN_VALUE) {
			return null;
		}
		if (start >= end) {
			JOptionPane
					.showMessageDialog(this,
							"Start time must be smaller than end time");
			return null;
		}
		return new long[] { start, end, interval };
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == createGnuplotFileButtons.getGnuplotButton()) {
			long[] values = checkTimeChoiceValue();
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					values[i] *= Player.TIME_UNIT_MULTIPLICATOR;
				}

				generatePLTFile.saveSettings();
				generateGraphics.saveSettings();

				File saveFile = new GnuplotFileChooser(this).askWhereToSave();
				if (saveFile != null) {

					ResultTable results = createTable(values);
					IResultFileWriter fw = new SimpleGnuplotFileWriter();
					try {
						// File .dat
						fw.writeToFile(saveFile, results);

						if (generatePLTFile.isSelected()) {
							// File .plt
							log.debug("Generate PLT file...");
							String pltFileName = PLTFileBuilder_linespoints
									.writePLTFile(saveFile, results);
							if (generateGraphics.isSelected()) {
								log.debug("Executing Gnuplot...");
								try {
									runGnuplot(pltFileName);
								} catch (IOException ex_GenerateGraphics) {
									JOptionPane
											.showMessageDialog(
													this,
													"Path to Gnuplot binaries invalid, "
															+ "please correct the path in the config.xml",
													"Exported graphics",
													JOptionPane.PLAIN_MESSAGE);
								}
							}
						}
						log.debug("Finished!");

						JOptionPane.showMessageDialog(this,
								"Export successful", "Gnuplot export",
								JOptionPane.PLAIN_MESSAGE);
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this,
								"I/O-Error during Export: " + ex.getMessage(),
								"Gnuplot export", JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		}
	}

	protected abstract ResultTable createTable(long[] values);

	private static void runGnuplot(String pltFileName) throws IOException {

		String[] command = { Config.getValue("Gnuplot/ExecPath", "gnuplot"),
				pltFileName };
		// String command = Config.getValue("Gnuplot/ExecPath", "gnuplot") +
		// " '" + pltFileName + "'";
		// String command = "pwd";

		// log.debug(new
		// File(pltFileName).getParentFile().getAbsolutePath());

		System.out
				.println("Command: \"" + command[0] + " " + command[1] + "\"");
		Process process = Runtime.getRuntime().exec(command, null,
				new File(pltFileName).getParentFile());

		String text = ""; // read buffer
		PrintWriter out = new PrintWriter(System.out);
		BufferedReader in = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		// Reads all the characters from the stream and prints to standard
		// output
		while ((text = in.readLine()) != null) {
			out.println(text);
			out.flush();
		}

		String text2 = ""; // read buffer
		PrintWriter out2 = new PrintWriter(System.out);
		BufferedReader in2 = new BufferedReader(new InputStreamReader(process
				.getErrorStream()));
		// Reads all the characters from the stream and prints to standard
		// output
		while ((text2 = in2.readLine()) != null) {
			out2.println("ERROR: " + text2);
			out2.flush();
		}
	}

}
