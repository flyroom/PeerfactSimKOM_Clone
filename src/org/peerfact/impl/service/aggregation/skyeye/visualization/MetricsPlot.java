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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.peerfact.impl.service.aggregation.skyeye.SkyNetPropertiesReader;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DataSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.DeviationSet;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.SeriesInfo;
import org.peerfact.impl.service.aggregation.skyeye.visualization.util.VisualizationType;


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
public class MetricsPlot implements ActionListener {

	private JPanel container;

	private ChartPanel plotPanel;

	private JFreeChart chart;

	private static String X_AXIS_TITLE = "Time [h:m:s]";

	private long step;

	private int interval;

	private int plotWidth;

	private int plotHeight;

	private int boxOffset = 25;

	private long upperDomainBound;

	private boolean autoScrolling;

	private LinkedHashMap<String, SeriesInfo> displayedSeries;

	private Color[] colors = { Color.ORANGE, Color.RED, Color.PINK, Color.BLUE,
			Color.GREEN };

	private boolean showSdtDev;

	private boolean showMin;

	private boolean showMax;

	private VisualizationType visType;

	public MetricsPlot(String title, long time, int interval, int plotWidth,
			int plotHeight, VisualizationType visType) {
		this.visType = visType;
		this.container = new JPanel(new BorderLayout(), true);
		this.plotWidth = plotWidth;
		this.plotHeight = plotHeight;
		this.displayedSeries = new LinkedHashMap<String, SeriesInfo>();
		this.autoScrolling = false;
		showSdtDev = false;
		showMin = false;
		showMax = false;
		SkyNetPropertiesReader instance = SkyNetPropertiesReader.getInstance();
		if (visType == VisualizationType.Attribute) {
			this.step = SkyNetPropertiesReader.getInstance().getTimeProperty(
					"AttributeUpdateTime");
			this.interval = (int) (interval * (instance
					.getTimeProperty("MetricUpdateTime") / (double) instance
					.getTimeProperty("AttributeUpdateTime")));
		} else {
			this.step = instance.getTimeProperty("MetricUpdateTime");
			this.interval = interval;
		}
		createChartPanel(title, time);
	}

	private void createChartPanel(String title, long time) {
		YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
		chart = ChartFactory.createTimeSeriesChart(title, X_AXIS_TITLE, "",
				dataset, true, true, true);
		XYPlot plot = (XYPlot) chart.getPlot();

		DeviationRenderer errorRenderer = new DeviationRenderer();
		errorRenderer.setShapesVisible(false);
		errorRenderer.setLinesVisible(true);
		errorRenderer.setAlpha(0.0f);
		// errorRenderer.setDrawYError(false);
		// errorRenderer.setDrawXError(false);
		plot.setRenderer(errorRenderer);

		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.DARK_GRAY);
		plot.setDomainGridlinePaint(Color.DARK_GRAY);
		upperDomainBound = (time / 1000) + ((interval - 1) * step / 1000);
		DateAxis domain = (DateAxis) plot.getDomainAxis();
		domain.setAutoRange(false);
		domain.setRange((time / 1000), upperDomainBound);
		RelativeDateFormat rdf = new RelativeDateFormat();
		rdf.setHourSuffix(":");
		rdf.setMinuteSuffix(":");
		rdf.setSecondSuffix("");
		rdf.setSecondFormatter(new DecimalFormat("0"));
		domain.setDateFormatOverride(rdf);
		plot.setDomainAxis(domain);
		plotPanel = new ChartPanel(chart, true);
		setSizeOfComponent(plotPanel, new Dimension(plotWidth, plotHeight));
		container.add(plotPanel, BorderLayout.CENTER);
		container.add(createRadioBoxes(visType == VisualizationType.Metric),
				BorderLayout.SOUTH);
		setSizeOfComponent(container, new Dimension(plotWidth, plotHeight
				+ boxOffset));
	}

	public JPanel getPlotPanel() {
		return container;
	}

	public void updatePlot(String plotTitle, DataSet dataSet) {
		if (displayedSeries.size() == 0) {
			if (dataSet.getVisType() == VisualizationType.Metric) {
				displayedSeries.putAll(createMetricSeriesInfo(dataSet));
			} else {
				displayedSeries.putAll(createStateSeriesInfo(dataSet));
			}
		} else {
			updateSeriesInfo(dataSet);
		}
		updateChartPanel(plotTitle);
	}

	private void updateChartPanel(String plotTitle) {
		XYPlot plot = (XYPlot) chart.getPlot();
		YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();// (YIntervalSeriesCollection)
		// plot.getDataset()
		String[] names = displayedSeries.keySet().toArray(
				new String[displayedSeries.keySet().size()]);
		Arrays.sort(names, null);
		if (!autoScrolling
				&& displayedSeries.get(names[0]).getDataSeries().getItemCount() == displayedSeries
						.get(names[0]).getDataSeries().getMaximumItemCount()) {
			autoScrolling = true;
			DateAxis domain = (DateAxis) plot.getDomainAxis();
			domain.setAutoRange(true);
			plot.setDomainAxis(domain);
		}
		for (int i = 0; i < names.length; i++) {
			if (names[i].startsWith("Min_")) {
				if (showMin) {
					dataset.addSeries(displayedSeries.get(names[i])
							.getDataSeries());
				}
			} else if (names[i].startsWith("Max_")) {
				if (showMax) {
					dataset.addSeries(displayedSeries.get(names[i])
							.getDataSeries());
				}
			} else {
				dataset
						.addSeries(displayedSeries.get(names[i])
								.getDataSeries());
			}

		}
		plot.setDataset(dataset);
		plot.setRenderer(configureRendererForDataSet(plot.getRenderer(),
				dataset));
		plotPanel.setChart(chart);
		setSizeOfComponent(plotPanel, new Dimension(plotWidth, plotHeight));
		container.add(plotPanel, BorderLayout.CENTER);
		setSizeOfComponent(container, new Dimension(plotWidth, plotHeight
				+ boxOffset));
	}

	private DeviationRenderer configureRendererForDataSet(XYItemRenderer r,
			YIntervalSeriesCollection dataSet) {
		DeviationRenderer renderer = (DeviationRenderer) r;
		YIntervalSeries serie = null;
		for (int i = 0; i < dataSet.getSeriesCount(); i++) {
			serie = dataSet.getSeries(i);
			renderer.setSeriesStroke(i, displayedSeries.get(serie.getKey())
					.getStroke());
			renderer.setSeriesPaint(i, displayedSeries.get(serie.getKey())
					.getColor());
			renderer.setSeriesFillPaint(i, Color.LIGHT_GRAY);

		}
		if (showSdtDev) {
			renderer.setAlpha(0.3f);
		}
		return renderer;
	}

	private JPanel createRadioBoxes(boolean enable) {
		JPanel rbPanel = new JPanel(new FlowLayout());
		rbPanel.add(setJRadioButton("Min-Values", enable));
		rbPanel.add(setJRadioButton("Max-Values", enable));
		rbPanel.add(setJRadioButton("Standard-Deviation", enable));
		setSizeOfComponent(rbPanel, new Dimension(plotWidth, boxOffset));
		rbPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		return rbPanel;
	}

	private JRadioButton setJRadioButton(String title, boolean enable) {
		JRadioButton radioButton = new JRadioButton(title);
		radioButton.addActionListener(this);
		radioButton.setEnabled(enable);
		return radioButton;
	}

	private static void setSizeOfComponent(JComponent component, Dimension dim) {
		component.setMinimumSize(dim);
		component.setMaximumSize(dim);
		component.setPreferredSize(dim);
		component.setSize(dim);
	}

	private LinkedHashMap<String, SeriesInfo> createMetricSeriesInfo(
			DataSet dataSet) {
		String[] names = dataSet.getNames();
		LinkedHashMap<String, SeriesInfo> tempMap = new LinkedHashMap<String, SeriesInfo>();
		SeriesInfo seriesInfo = null;
		for (int i = 0; i < names.length; i++) {
			DeviationSet set = dataSet.getValues()[i];
			if (names[i].startsWith("Min_")) {
				seriesInfo = new SeriesInfo(interval, names[i], Color.BLUE,
						new BasicStroke(1));
			} else if (names[i].startsWith("Max_")) {
				seriesInfo = new SeriesInfo(interval, names[i], Color.RED,
						new BasicStroke(1));
			} else if (names[i].startsWith("Measured")) {
				seriesInfo = new SeriesInfo(interval, names[i], Color.GRAY,
						new BasicStroke(3));
			} else {
				seriesInfo = new SeriesInfo(interval, names[i], Color.BLACK,
						new BasicStroke(4));
			}

			seriesInfo.getDataSeries().add(dataSet.getTime(), set.getValue(),
					set.getValue() + set.getDeviation(),
					Math.max(set.getValue() - set.getDeviation(), 0));
			tempMap.put(names[i], seriesInfo);
		}
		return tempMap;
	}

	private LinkedHashMap<String, SeriesInfo> createStateSeriesInfo(
			DataSet dataSet) {
		String[] names = dataSet.getNames();
		LinkedHashMap<String, SeriesInfo> tempMap = new LinkedHashMap<String, SeriesInfo>();
		SeriesInfo seriesInfo = null;
		int counter = 0;
		for (int i = 0; i < names.length; i++) {
			DeviationSet set = dataSet.getValues()[i];
			if (names[i].startsWith("Real ")) {
				seriesInfo = new SeriesInfo(interval, names[i], Color.BLACK,
						new BasicStroke(3));
			} else {
				seriesInfo = new SeriesInfo(interval, names[i],
						colors[counter], new BasicStroke(1));
				counter++;
			}

			seriesInfo.getDataSeries().add(dataSet.getTime(), set.getValue(),
					set.getValue() + set.getDeviation(),
					Math.max(set.getValue() - set.getDeviation(), 0));
			tempMap.put(names[i], seriesInfo);
		}
		return tempMap;
	}

	private void updateSeriesInfo(DataSet dataSet) {
		String[] names = dataSet.getNames();
		for (int i = 0; i < names.length; i++) {
			DeviationSet set = dataSet.getValues()[i];

			displayedSeries.get(names[i]).getDataSeries().add(
					dataSet.getTime(), set.getValue(),
					set.getValue() + set.getDeviation(),
					Math.max(set.getValue() - set.getDeviation(), 0));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JRadioButton button = (JRadioButton) e.getSource();
		if (button.isSelected()) {
			if (e.getActionCommand().equals("Min-Values")) {
				showMin = true;
			} else if (e.getActionCommand().equals("Max-Values")) {
				showMax = true;
			} else {
				showSdtDev = true;
			}
		} else {
			if (e.getActionCommand().equals("Min-Values")) {
				showMin = false;
			} else if (e.getActionCommand().equals("Max-Values")) {
				showMax = false;
			} else {
				showSdtDev = false;
			}
		}
	}

}
