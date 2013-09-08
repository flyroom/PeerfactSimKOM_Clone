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

package org.peerfact.impl.analyzer.visualization2d.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.peerfact.Constants;


/**
 * Class is designed for easy access to an XML config file.
 * 
 * Additional Information: All needed methods are static. Thus it is no instance
 * of the class is necessary.
 * 
 * @author Julius Rückert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */

public class Config {

	/**
	 * Path for the config file to use
	 */
	private static String configFile = Constants.GUI_CFG_DIR + "/config.xml";

	/**
	 * Contains the XML tree of the config file
	 */
	private static Document config;

	/**
	 * Determines if necessary, a new config file with a root element
	 */
	private static void setupFile() {
		File file = new File(configFile);

		if (!file.exists()) { // if config file does not exists
			try {
				file.getParentFile().mkdirs();
				file.createNewFile(); // create new file

			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}

			config = DocumentHelper.createDocument();
			config.addElement("config");

			writeXMLFile();
		}
	}

	/**
	 * Loads the contents of the config file into memory
	 */
	private static void loadXMLFile() {

		if (config == null) { // Loading content only, if not loaded

			setupFile();

			try {
				config = new SAXReader().read(configFile);

			} catch (DocumentException e) {
				// TODO
				System.err.println("Config: DocumentException!");
			}
		}
	}

	/**
	 * Writes the existing structure in the XML config file
	 */
	public static void writeXMLFile() {

		if (config != null) { // Write file only if the XML tree exists in
								// memory
			try {
				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter writer = new XMLWriter(new FileWriter(configFile),
						format);
				writer.write(Config.config);
				writer.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Examines the XML document <code>config</code> whether leaf is a end of
	 * the passed path
	 * 
	 * @param leafPath
	 * @return true, if leaf with the passed path exists in the XML file, else
	 *         false
	 */
	private static boolean leafForKeyExists(String leafPath) {

		ArrayList<String> path = setupPathArrayList(leafPath);
		Element current = config.getRootElement();

		for (int i = 0; i < path.size(); i++) { // runs the path through each
												// element

			if (current.element(path.get(i)) != null) { // if child exists with
														// name path[i]

				current = current.element(path.get(i)); // Child nodes of
														// current node

				if (i + 1 == path.size()) { // we are already at the required
											// leaf in specified path?

					if (current.elements().size() == 0) { // is the demanded
															// leaf really a
															// leaf in the XML
															// document?
						return true;
					} else { // the requested leaf of path is a leaf in the XML
								// document
						return false;
					}
				}
			} else { // Child with a name requested does not exist
				return false;
			}
		}
		return false;
	}

	/**
	 * Auxiliary function to read values ​​from XML document.
	 * 
	 * Precondition: Absolutely make sure that the element exists in the XML
	 * document!
	 * 
	 * @param leafPath
	 * @return
	 */
	private static String getValueForPath(String leafPath) {
		ArrayList<String> p = setupPathArrayList(leafPath);

		Element current = config.getRootElement();

		for (int i = 0; i < p.size(); i++) {
			current = current.element(p.get(i));
		}
		return current.getText();
	}

	/**
	 * Generated from the given path a ArrayList of the different parts. String
	 * is separated by "/" and empty elements remove.
	 * 
	 * @param path
	 * @return Arraylist of the path elements
	 */
	private static ArrayList<String> setupPathArrayList(String path) {

		ArrayList<String> p = new ArrayList<String>();

		String[] splitted = path.split("/");

		for (String s : splitted) { // remove all empty fields of the path (e.g.
									// caused by / at the beginning or end)
			if (s.length() > 0) {
				p.add(s);
			}
		}
		return p;
	}

	/**
	 * Returns the value of the specified path from the config file. If there is
	 * not a specific configuration item, the passed default value will be
	 * inserted.
	 * 
	 * @param valuePath
	 *            the path within the config file. E.g. "gui/width"
	 * @param standardValue
	 * @return the value of the configuration element from the config file, or
	 *         the default value if the element does not exist
	 */
	public static String getValue(String valuePath, String standardValue) {

		loadXMLFile(); // load XML file

		if (!leafForKeyExists(valuePath)) { //
			setValue(valuePath, standardValue);
			return standardValue;
		} else {
			return getValueForPath(valuePath);
		}
	}

	public static int getValue(String valuePath, int standardValue) {
		String value = getValue(valuePath, String.valueOf(standardValue));
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			System.err
					.println("Config: XML config entry "
							+ valuePath
							+ "=\""
							+ value
							+ "\" cannot be parsed as an integer. Using default value of \""
							+ standardValue + "\" instead.");
			return standardValue;
		}
	}

	public static boolean getValue(String valuePath, boolean standardValue) {
		String value = getValue(valuePath, String.valueOf(standardValue));
		return Boolean.valueOf(value);
	}

	/**
	 * Sets the value of the specified path from the config file. If there is
	 * not the specified configuration element, this is inserted to the passed
	 * value.
	 * 
	 * @param valuePath
	 *            the path within the config file. E.g. "gui/width"
	 * @param value
	 */
	public static void setValue(String valuePath, String value) {

		loadXMLFile(); // Loading XML file

		ArrayList<String> path = setupPathArrayList(valuePath);
		Element current = config.getRootElement();

		for (int i = 0; i < path.size(); i++) { // runs the path through each
												// element

			if (current.element(path.get(i)) == null) { // Child with demanded
														// name does not exist
				current.addElement(path.get(i)); // Child node insert as new
													// element
			}
			current = current.element(path.get(i)); // Child nodes of current
													// node

			if (i + 1 == path.size()) { // we are already at the required
										// leaf in specified path?

				if (current.elements().size() == 0) {// is the demanded leaf
														// really a leaf in the
														// XML document?
					current.setText(value); // set value
				} else { // Requested path of leaf is a leaf in the XML document
				}
			}
		}
		// writeXMLFile(); //taken out by me, is explicitly called at the end.
	}

	/**
	 * Sets the value of the specified path from the config file. If there is
	 * not the specified configuration element, this is inserted with the passed
	 * value.
	 * 
	 * @param valuePath
	 *            the path within the config file. E.g. "gui/width"
	 * @param value
	 */
	public static void setValue(String valuePath, int value) {
		setValue(valuePath, String.valueOf(value));
	}

	/**
	 * Sets the value of the specified path from the config file. If there is
	 * not the specified configuration element, this is inserted with the passed
	 * value.
	 * 
	 * @param valuePath
	 *            the path within the config file. E.g. "gui/width"
	 * @param value
	 */
	public static void setValue(String valuePath, boolean value) {
		setValue(valuePath, String.valueOf(value));
	}

}
