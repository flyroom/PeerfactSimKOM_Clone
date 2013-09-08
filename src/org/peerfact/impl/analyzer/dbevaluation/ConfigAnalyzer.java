/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.peerfact.impl.analyzer.dbevaluation;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.impl.simengine.Simulator;
import org.xml.sax.SAXException;


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
 * This class write out the configuration to the database. So it is possible to
 * know, which configuration is used to simulate the other information in the
 * database.
 * 
 * @author Christoph Muenker
 * @version 1.0, 04/07/2011
 */
public class ConfigAnalyzer implements Analyzer, IOutputWriterDelegator {

	private IAnalyzerOutputWriter outputWriter;

	private static String CONFIG_ENTITY = "config";

	@Override
	public void start() {
		outputWriter.initialize(CONFIG_ENTITY);
		outputWriter.persist(CONFIG_ENTITY,
				new AnalyzerOutputEntry(System.currentTimeMillis(),
						"configName", Simulator.getInstance().getConfigurator()
								.getConfigFile().getName()));
		outputWriter.persist(CONFIG_ENTITY,
				new AnalyzerOutputEntry(System.currentTimeMillis(),
						"configPath", Simulator.getInstance().getConfigurator()
								.getConfigFile().getName()));

		outputWriter.persist(CONFIG_ENTITY,
				new AnalyzerOutputEntry(System.currentTimeMillis(),
						"configuration", getXML(Simulator.getInstance()
								.getConfigurator().getConfigFile())));
		Map<String, String> variables = Simulator.getInstance()
				.getConfigurator().getVariables();
		for (String key : variables.keySet()) {
			outputWriter.persist(CONFIG_ENTITY,
					new AnalyzerOutputEntry(System.currentTimeMillis(), key,
							variables.get(key)));
		}
	}

	@Override
	public void stop(Writer output) {
		//
	}

	@Override
	public void setAnalyzerOutputWriter(
			IAnalyzerOutputWriter analyzerOutputWriter) {
		outputWriter = analyzerOutputWriter;
	}

	private static String getXML(File configFile) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setXIncludeAware(true);
			factory.setNamespaceAware(false);
			factory.setValidating(false);

			SAXParser parser = factory.newSAXParser();
			SAXReader reader = new SAXReader(parser.getXMLReader());
			Document configuration = reader.read(configFile);

			StringWriter strWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(strWriter);
			writer.write(configuration);
			return strWriter.toString();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Cannot parse/read the configuration";
	}
}
