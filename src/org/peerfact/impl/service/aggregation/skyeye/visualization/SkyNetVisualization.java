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

package org.peerfact.impl.service.aggregation.skyeye.visualization;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DataSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DeviationSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.VisualizationType;
import org.peerfact.impl.simengine.Simulator;
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
public class SkyNetVisualization extends JFrame implements Configurable,
		ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4044876150569280569L;

	private static Logger log = SimLogger.getLogger(SkyNetVisualization.class);

	private static int PLOT_WIDTH = 350;

	private static int PLOT_HEIGHT = 350;

	private static int INTERVAL = 30;

	private static SkyNetVisualization instance;

	private LinkedHashMap<String, MetricsPlot> displayedMetrics;

	private JPanel graphix;

	private JMenuBar mb;

	private int maxPlotsPerRow;

	private boolean activated;

	public static boolean hasInstance() {
		return instance != null;
	}

	public static SkyNetVisualization getInstance() {
		if (instance == null) {
			instance = new SkyNetVisualization();
		}
		return instance;
	}

	private SkyNetVisualization() {
		super("Metric-Visualization");
		createLookAndFeel();
		displayedMetrics = new LinkedHashMap<String, MetricsPlot>();
		setLayout(new GridLayout(1, 1));
		addWindowListener(this);
		graphix = new JPanel(new GridBagLayout());
		graphix.setLayout(new BoxLayout(graphix, BoxLayout.PAGE_AXIS));
		mb = new JMenuBar();
		JScrollPane scroller = new JScrollPane(graphix,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// calculating the size of the application-window as well as of all
		// components, that depend on the size of the window
		Toolkit kit = Toolkit.getDefaultToolkit();
		int appWidth = kit.getScreenSize().width * 3 / 4;
		int appHeight = kit.getScreenSize().height * 3 / 4;
		scroller.setPreferredSize(new Dimension(appWidth, appHeight));
		getContentPane().add(scroller);
		maxPlotsPerRow = 1;
		while ((maxPlotsPerRow + 1) * PLOT_WIDTH < appWidth) {
			maxPlotsPerRow++;
		}
		log.warn("Creating the visualization...");
		mb.add(new JMenu("File"));
		JMenu met = new JMenu("Available Metrics");
		met.setEnabled(false);
		mb.add(met);
		setJMenuBar(mb);
	}

	public void setIsEnabled(String bool) {
		activated = Boolean.parseBoolean(bool);
		if (activated) {
			pack();
			setVisible(true);
			log.warn("Displaying the visualization.");
		} else {
			setVisible(false);
			dispose();
			log.warn("Disposing the visualization");
		}
	}

	public void setAvailableMetrics(Vector<String> names) {
		Vector<String> temp = new Vector<String>();
		temp.addAll(names);
		temp.add("Online Peers");
		temp.add("Available Attributes");
		temp.add("Memory Usage");
		Collections.sort(temp);

		JMenu met = mb.getMenu(1);
		met.setEnabled(true);
		JMenu avgMet = new JMenu("Average Metrics");
		JMenu genMet = new JMenu("General Metrics");
		JMenu recMet = new JMenu("Metrics for Receiving");
		JMenu sentMet = new JMenu("Metrics for Sending");
		JCheckBoxMenuItem item = null;
		for (String name : temp) {
			if (name.startsWith("Aver")) {
				item = new JCheckBoxMenuItem(name, false);
				item.addActionListener(this);
				avgMet.add(item);
			} else if (name.startsWith("Rec")) {
				item = new JCheckBoxMenuItem(name, false);
				item.addActionListener(this);
				recMet.add(item);
			} else if (name.startsWith("Sent")) {
				item = new JCheckBoxMenuItem(name, false);
				item.addActionListener(this);
				sentMet.add(item);
			} else {
				item = new JCheckBoxMenuItem(name, false);
				item.addActionListener(this);
				genMet.add(item);
			}
		}
		avgMet.add(new JSeparator(SwingConstants.HORIZONTAL));
		avgMet.add(createMenuItem("Display all Average Metrics"));
		avgMet.add(createMenuItem("Remove all Average Metrics"));

		genMet.add(new JSeparator(SwingConstants.HORIZONTAL));
		genMet.add(createMenuItem("Display all General Metrics"));
		genMet.add(createMenuItem("Remove all General Metrics"));

		recMet.add(new JSeparator(SwingConstants.HORIZONTAL));
		recMet.add(createMenuItem("Display all Metrics for Receiving"));
		recMet.add(createMenuItem("Remove all Metrics for Receiving"));

		sentMet.add(new JSeparator(SwingConstants.HORIZONTAL));
		sentMet.add(createMenuItem("Display all Metrics for Sending"));
		sentMet.add(createMenuItem("Remove all Metrics for Sending"));

		met.add(avgMet);
		met.add(genMet);
		met.add(recMet);
		met.add(sentMet);

		met.add(new JSeparator(SwingConstants.HORIZONTAL));
		met.add(createMenuItem("Display all Metrics"));
		met.add(createMenuItem("Remove all Metrics"));

		mb.add(met);
		repaint();
		log.warn("Created Menu 'Available Metrics'");
	}

	private JMenuItem createMenuItem(String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(this);
		return menuItem;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String name = arg0.getActionCommand();
		if (arg0.getSource() instanceof JCheckBoxMenuItem) {
			JCheckBoxMenuItem j = (JCheckBoxMenuItem) arg0.getSource();
			if (j.isSelected()) {
				createPlotInWindow(name);
			} else {
				deletePlotInWindow(name);
			}
		} else {
			if (name.contains("Average Metrics")) {
				if (name.startsWith("Display")) {
					displayAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(0));
				} else {
					removeAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(0));
				}
			} else if (name.contains("General Metrics")) {
				if (name.startsWith("Display")) {
					displayAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(1));
				} else {
					removeAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(1));
				}
			} else if (name.contains("Metrics for Receiving")) {
				if (name.startsWith("Display")) {
					displayAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(2));
				} else {
					removeAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(2));
				}
			} else if (name.contains("Metrics for Sending")) {
				if (name.startsWith("Display")) {
					displayAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(3));
				} else {
					removeAllMetricsOfAGroup((JMenu) mb.getMenu(1)
							.getMenuComponent(3));
				}
			} else {
				for (int i = 0; i < mb.getMenu(1).getMenuComponentCount(); i++) {
					if (mb.getMenu(1).getMenuComponent(i) instanceof JMenu) {
						if (name.startsWith("Display")) {
							displayAllMetricsOfAGroup((JMenu) mb.getMenu(1)
									.getMenuComponent(i));
						} else {
							removeAllMetricsOfAGroup((JMenu) mb.getMenu(1)
									.getMenuComponent(i));
						}
					}
				}
			}
		}
		validate();
		repaint();
		// Runtime.getRuntime().gc();
	}

	private void displayAllMetricsOfAGroup(JMenu menu) {
		JCheckBoxMenuItem item = null;
		String name = null;
		for (int i = 0; i < menu.getMenuComponentCount(); i++) {
			if (menu.getMenuComponent(i) instanceof JCheckBoxMenuItem) {
				item = (JCheckBoxMenuItem) menu.getMenuComponent(i);
				name = item.getActionCommand();
				if (!item.isSelected()) {
					item.setSelected(true);
					createPlotInWindow(name);
				}
			}
		}
	}

	private void removeAllMetricsOfAGroup(JMenu menu) {
		JCheckBoxMenuItem item = null;
		String name = null;
		for (int i = 0; i < menu.getMenuComponentCount(); i++) {
			if (menu.getMenuComponent(i) instanceof JCheckBoxMenuItem) {
				item = (JCheckBoxMenuItem) menu.getMenuComponent(i);
				name = item.getActionCommand();
				if (item.isSelected()) {
					item.setSelected(false);
					deletePlotInWindow(name);
				}
			}
		}
	}

	public void updateDisplayedMetric(String plotName, DataSet dataSet) {
		if (activated && displayedMetrics.size() > 0) {
			if (displayedMetrics.containsKey(plotName)) {
				MetricsPlot temp = displayedMetrics.remove(plotName);
				temp.updatePlot(plotName, dataSet);
				displayedMetrics.put(plotName, temp);
				updatePlotInWindow(dataSet.getTime(), plotName);
			}
			validate();
			repaint();
		}
	}

	public void updateDisplayedMetrics(long time,
			LinkedHashMap<String, MetricsAggregate> simulatorMetrics,
			LinkedHashMap<String, MetricsAggregate> rootMetrics/*
																 * , double
																 * nodeCounter
																 */) {
		if (activated && displayedMetrics.size() > 0) {
			Iterator<String> nameIter = simulatorMetrics.keySet().iterator();
			String name = null;
			while (nameIter.hasNext()) {
				name = nameIter.next();
				if (displayedMetrics.containsKey(name)) {
					DeviationSet[] values = null;
					if (rootMetrics.size() > 0) {
						DeviationSet[] temp = {
								new DeviationSet(simulatorMetrics.get(name)
										.getAverage()),
								new DeviationSet(rootMetrics.get(name)
										.getAverage(), rootMetrics.get(name)
										.getStandardDeviation()),
								new DeviationSet(rootMetrics.get(name)
										.getMinimum()),
								new DeviationSet(rootMetrics.get(name)
										.getMaximum()) };
						values = temp;
					} else {
						DeviationSet[] temp = {
								new DeviationSet(simulatorMetrics.get(name)
										.getAverage()), new DeviationSet(0),
								new DeviationSet(0), new DeviationSet(0), };
						values = temp;
					}
					String[] metricNames = { "Real " + name,
							"Measured " + name, "Min_Measured" + name,
							"Max_Measured" + name };

					MetricsPlot temp = displayedMetrics.remove(name);
					temp.updatePlot(name, new DataSet(VisualizationType.Metric,
							time / 1000, values, metricNames));
					displayedMetrics.put(name, temp);
					updatePlotInWindow(time, name);
				}
			}
			validate();
			repaint();
		}
	}

	private void createPlotInWindow(String name) {
		MetricsPlot plot = null;
		if (name.equals("Available Attributes")) {
			plot = new MetricsPlot(name, Simulator.getCurrentTime(), INTERVAL,
					PLOT_WIDTH, PLOT_HEIGHT, VisualizationType.Attribute);
		} else if (name.equals("Online Peers") || name.equals("Memory Usage")) {
			plot = new MetricsPlot(name, Simulator.getCurrentTime(), INTERVAL,
					PLOT_WIDTH, PLOT_HEIGHT, VisualizationType.State);
		} else {
			plot = new MetricsPlot(name, Simulator.getCurrentTime(), INTERVAL,
					PLOT_WIDTH, PLOT_HEIGHT, VisualizationType.Metric);
		}
		if (graphix.getComponentCount() == 0) {
			JPanel temp = new JPanel();
			temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
			temp.add(plot.getPlotPanel());
			graphix.add(temp);
		} else if (((JPanel) graphix
				.getComponent(graphix.getComponentCount() - 1))
				.getComponentCount()
				% maxPlotsPerRow == 0) {
			JPanel temp = new JPanel();
			temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));
			temp.add(plot.getPlotPanel());
			graphix.add(temp);
		} else {
			int c = graphix.getComponentCount();
			JPanel temp = (JPanel) graphix.getComponent(c - 1);
			temp.add(plot.getPlotPanel());
			graphix.add(temp);
		}
		displayedMetrics.put(name, plot);
	}

	private void updatePlotInWindow(long time, String plotName) {
		for (int i = 0; i < graphix.getComponentCount(); i++) {
			for (int j = 0; j < maxPlotsPerRow; j++) {
				if (((JPanel) graphix.getComponent(i)).getComponentCount() > j
						&& getChartPanel(i, j).getChart().getTitle().getText()
								.equals(plotName)) {
					((JPanel) graphix.getComponent(i)).remove(j);
					((JPanel) graphix.getComponent(i)).add(displayedMetrics
							.get(plotName).getPlotPanel(), j);

					return;
				}
			}
		}
	}

	private void deletePlotInWindow(String name) {
		if (displayedMetrics.containsKey(name)) {
			displayedMetrics.remove(name);
			for (int i = 0; i < graphix.getComponentCount(); i++) {
				for (int k = 0; k < maxPlotsPerRow; k++) {
					if (((JPanel) graphix.getComponent(i)).getComponentCount() > k
							&& getChartPanel(i, k).getChart().getTitle()
									.getText().equals(name)) {
						((JPanel) graphix.getComponent(i)).remove(k);
						break;
					}
				}
				if (((JPanel) graphix.getComponent(i)).getComponentCount() == 0) {
					graphix.remove(i);
					log.debug("graphix->compcount = "
							+ graphix.getComponentCount()
							+ "(Empty box after deleting the spec metric)");
					break;
				} else if (i < graphix.getComponentCount() - 1) {
					while (((JPanel) graphix.getComponent(i))
							.getComponentCount() < maxPlotsPerRow) {
						if (i + 1 < graphix.getComponentCount()) {
							if (((JPanel) graphix.getComponent(i + 1))
									.getComponentCount() > 0) {
								((JPanel) graphix.getComponent(i))
										.add(((JPanel) graphix
												.getComponent(i + 1))
												.getComponent(0));
								if (((JPanel) graphix.getComponent(i + 1))
										.getComponentCount() == 0) {
									graphix.remove(i + 1);
									log.debug("graphix->compcount = "
											+ graphix.getComponentCount()
											+ "(Empty box after refilling)");
								}
							}
						} else {
							break;
						}
					}
				}
			}
		} else {
			log.fatal(name + " is currently not displayed,"
					+ " and cannot be removed.");
		}
	}

	private static void createLookAndFeel() {
		LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		boolean hasNimbus = false;
		boolean hasWindows = false;
		for (int i = 0; i < lfs.length; i++) {
			if (lfs[i].getClassName().equals(
					"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")) {
				hasNimbus = true;
			} else if (lfs[i].getClassName().equals(
					"com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
				hasWindows = true;
			}
		}

		String lafName = null;
		if (hasNimbus) {
			lafName = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		} else if (hasWindows) {
			lafName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		} else {
			lafName = "javax.swing.plaf.metal.MetalLookAndFeel";
		}
		try {
			UIManager.setLookAndFeel(lafName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private ChartPanel getChartPanel(int i, int j) {
		JPanel chartContainer = (JPanel) ((JPanel) graphix.getComponent(i))
				.getComponent(j);
		int count = 0;
		while (count < chartContainer.getComponentCount()) {
			if (chartContainer.getComponent(count) instanceof ChartPanel) {
				return (ChartPanel) chartContainer.getComponent(count);
			} else {
				count++;
			}
		}
		return null;
	}

	// *********************************************************************
	// Methods of the interface WindowListener
	// *********************************************************************

	@Override
	public void windowActivated(WindowEvent e) {
		// not needed

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowClosing(WindowEvent e) {
		displayedMetrics.clear();
		activated = false;
		setVisible(false);
		dispose();
		// Runtime.getRuntime().gc();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// not needed

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// not needed

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// not needed

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// not needed

	}
}
