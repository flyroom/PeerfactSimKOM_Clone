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

package org.peerfact.impl.util.guirunner.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.peerfact.impl.util.Tuple;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.DOMToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class ConfigFile {

	private File configFile;

	final List<Tuple<String, String>> variables = new LinkedList<Tuple<String, String>>();

	String desc = null;

	boolean parsed = false;

	private String seed = null;

	private static Logger log = SimLogger.getLogger(ConfigFile.class);

	public ConfigFile(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public String toString() {
		return "ConfigFile(" + configFile + ")";
	}

	void parseContent() throws Exception {
		InputStream is = new FileInputStream(configFile);
		BufferedInputStream buf = new BufferedInputStream(is);
		DocumentBuilder b = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = b.parse(buf);

		Element root = DOMToolkit.getFirstChildElemMatching(doc,
				"Configuration");
		if (root == null) {
			throw new ConfigFileException(
					"Cannot parse document. The root element 'Configuration' is missing");
		}
		Element variableRoot = DOMToolkit.getFirstChildElemMatching(root,
				"Default");
		if (variableRoot != null) {
			for (Element elem : DOMToolkit.getAllChildElemsMatching(
					variableRoot, "Variable")) {
				String name = elem.getAttribute("name");
				String value = elem.getAttribute("value");
				if (name != null && value != null) {
					if ("seed".equalsIgnoreCase(name)) {
						seed = value;
					} else {
						variables.add(new Tuple<String, String>(name, value));
					}
				} else {
					log("Cannot add name/value of the attribute to map. Name or value attribute is missing.");
				}
			}
		} else {
			log("Cannot parse variables. There is no 'Default' element as child of 'Configuration'");
		}
		Element descElem = DOMToolkit.getFirstChildElemMatching(root,
				"Description");
		if (descElem != null) {
			desc = descElem.getTextContent().trim();
		} else {
			log("Optional element Description is missing.");
		}
	}

	void parseCond() {
		if (!parsed) {
			try {
				parseContent();
				parsed = true;
			} catch (Exception e) {
				log("Problems while parsing. Will not retry.");
				e.printStackTrace();
				parsed = true;
			}
		}
	}

	public List<Tuple<String, String>> getVariables() {
		parseCond();
		return variables;
	}

	public String getDesc() {
		parseCond();
		return desc;
	}

	public File getFile() {
		return configFile;
	}

	public static void log(Object logContent) {
		log.debug(logContent);
	}

	static class ConfigFileException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3351664218766540201L;

		public ConfigFileException() {
			super();
		}

		public ConfigFileException(String message, Throwable cause) {
			super(message, cause);
		}

		public ConfigFileException(String message) {
			super(message);
		}

		public ConfigFileException(Throwable cause) {
			super(cause);

		}

	}

	public String getSeedInConfig() {
		parseCond();
		return seed;
	}

}
