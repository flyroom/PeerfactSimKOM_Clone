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

package org.peerfact.impl.util.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author <info@peerfact.org>
 * @version 05/06/2011
 * 
 */
public class UpdateLicense {

	private static String EMAIL = "<info@peerfact.org>";

	private static String DATE = "07/09/2012";

	private static String AUTHOR_TAG = "@author";

	private static String VERSION_TAG = "@version";

	private static int counter = 0;

	private static int completeHeader = 0;

	private static int authorTag = 0;

	private static int versionTag = 0;

	private static int justVersionTag = 0;

	private static int addedMail = 0;

	private static int noMod = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File licenseFile = new File("license.txt");
		String dir;

		try {
			BufferedWriter logger = new BufferedWriter(new FileWriter(
					"update.log"));
			// "src/de/tud/kom/p2psim/simulator"
			if (args.length == 0) {
				dir = "src";
				updateLicense(new File(dir), licenseFile, logger);
			} else {
				for (int i = 0; i < args.length; i++) {
					dir = args[i];
					updateLicense(new File(dir), licenseFile, logger);
				}
			}
			logger.write("Traversed files = " + counter);
			logger.newLine();
			logger.write("Wrote complete header = " + completeHeader);
			logger.newLine();
			logger.write("Wrote complete author-tag = " + authorTag);
			logger.newLine();
			logger.write("Wrote complete version-tag = " + versionTag);
			logger.newLine();
			logger.write("Wrote complete version-tag (no modifications at authors)= "
					+ justVersionTag);
			logger.newLine();
			logger.write("Added only mail = " + addedMail);
			logger.newLine();
			logger.write("No modification = " + noMod);
			logger.newLine();
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateLicense(File oldFile, File licenseFile,
			BufferedWriter logger) {
		if (oldFile.isDirectory() && !oldFile.getName().equals(".svn")) {
			File[] files = oldFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				updateLicense(files[i], licenseFile, logger);
			}
		} else if (oldFile.getName().endsWith(".java")) {
			try {
				System.out.println("Check file " + oldFile.getAbsolutePath());
				logger.write("Check file " + oldFile.getAbsolutePath());
				logger.newLine();
				counter++;
				// create new version
				File newFile = new File(oldFile.getAbsolutePath() + ".new");

				// copy new license
				// copyLicense(licenseFile, out);

				// modify the file
				insertText(newFile, oldFile, logger);
				// rename new file in old
				oldFile.delete();
				if (newFile.renameTo(oldFile)) {
					System.out.println("Updated " + newFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

	}

	private static void insertText(File newFile, File oldFile,
			BufferedWriter logger) throws IOException {
		// append the original file, but skip old license
		BufferedReader reader = new BufferedReader(new FileReader(oldFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		String line = null;

		// scroll until package
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.newLine();
			if (line.trim().startsWith("package ")) {
				break;
			}
		}

		// now be careful and search for existing header or start of the class
		// description
		while ((line = reader.readLine()) != null) {
			// check if header already exists
			if (line.trim().startsWith("/**")) {
				writer.write(line);
				writer.newLine();
				modifyHeader(writer, reader, logger);
				break;
			}
			// check if there is no header at all
			else if (line.trim().startsWith("public ")
					|| line.trim().startsWith("class ")
					|| line.trim().startsWith("interface ")
					|| line.trim().startsWith("enum ")) {
				writeHeader(writer);
				writer.write(line);
				writer.newLine();
				logger.write("  Complete Header");
				logger.newLine();
				completeHeader++;
				break;
			}
			// write the lines to the new file
			else {
				writer.write(line);
				writer.newLine();
			}
		}

		// complete the rest of the document
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.newLine();
		}

		reader.close();
		writer.close();
	}

	private static void writeHeader(BufferedWriter writer) throws IOException {
		writer.write("/**");
		writer.newLine();
		writer.write(" *");
		writer.newLine();
		writer.write(" * " + AUTHOR_TAG + " " + EMAIL);
		writer.newLine();
		writer.write(" * " + VERSION_TAG + " " + DATE);
		writer.newLine();
		writer.write(" *");
		writer.newLine();
		writer.write(" */");
		writer.newLine();
	}

	private static void modifyHeader(BufferedWriter writer,
			BufferedReader reader, BufferedWriter logger) throws IOException {
		String line = null;
		boolean searchAuthor = true;
		boolean searchVersion = true;
		boolean setEmail = false;
		while ((line = reader.readLine()) != null) {
			// search for the end
			if (line.trim().startsWith("*/")) {
				// check if something was changed at all
				if (!searchAuthor && !searchVersion && !setEmail) {
					logger.write("  No modification");
					logger.newLine();
					noMod++;
				}

				if (!searchAuthor && searchVersion && !setEmail) {
					justVersionTag++;
				}
				// check if author was already written
				if (searchAuthor) {
					writer.write(" * " + AUTHOR_TAG + " " + EMAIL);
					writer.newLine();
					logger.write("  Added Author-tag");
					logger.newLine();
					authorTag++;
				}
				// check if version was already written
				if (searchVersion) {
					writer.write(" * " + VERSION_TAG + " " + DATE);
					writer.newLine();
					logger.write("  Added Version-tag");
					logger.newLine();
					versionTag++;
				}
				writer.write(line);
				writer.newLine();
				break;
			}// check for author-tag
			else if (line.contains(AUTHOR_TAG) && searchAuthor) {
				searchAuthor = false;
				// append email at the end
				if (line.split("@").length < 3) {
					setEmail = true;
					writer.write(line + " " + EMAIL);
					writer.newLine();
					logger.write("  Added Mail");
					logger.newLine();
					addedMail++;
				} else {
					writer.write(line);
					writer.newLine();
				}
			}// check for version-tag
			else if (line.contains(VERSION_TAG) && searchVersion) {
				searchVersion = false;
				writer.write(line);
				writer.newLine();
			} else {
				writer.write(line);
				writer.newLine();
			}
		}
	}

}
