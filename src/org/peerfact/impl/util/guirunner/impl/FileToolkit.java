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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * 
 * Controls directory browsing and file type checking.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 25.11.2008
 * 
 */
public class FileToolkit {

	/**
	 * Appends all files and dirs in given directory rootDir to rootNode,
	 * recursively.
	 * 
	 * @param rootNode
	 * @param rootDir
	 * @return
	 */
	public static boolean getConfigFiles(DefaultMutableTreeNode rootNode,
			File rootDir) {

		File[] filesArray = rootDir.listFiles();
		Set<File> files = new TreeSet<File>();
		for (int i = 0; i < filesArray.length; i++) {
			files.add(filesArray[i]);
		}

		boolean containsAConfig = false;

		List<MutableTreeNode> subFolders = new ArrayList<MutableTreeNode>();
		List<MutableTreeNode> subFiles = new ArrayList<MutableTreeNode>();

		for (File f : files) {

			if (f.isDirectory()) {
				DefaultMutableTreeNode subDir = new ConfigDirTreeNode(f);
				if (getConfigFiles(subDir, f)) {
					containsAConfig = true;
					subFolders.add(subDir);
				}

			} else if (fileIsConfig(f)) {
				MutableTreeNode configFile = new ConfigTreeNode(new ConfigFile(
						f));
				containsAConfig = true;
				subFiles.add(configFile);

			}
		}

		for (MutableTreeNode f : subFolders) {
			rootNode.add(f);
		}

		for (MutableTreeNode f : subFiles) {
			rootNode.add(f);
		}

		return containsAConfig;
	}

	/**
	 * Checks whether the given file is an XML config file or not.
	 * 
	 * @param f
	 * @return
	 */
	private static boolean fileIsConfig(File f) {
		return f.getName().endsWith(".xml");
	}

}
