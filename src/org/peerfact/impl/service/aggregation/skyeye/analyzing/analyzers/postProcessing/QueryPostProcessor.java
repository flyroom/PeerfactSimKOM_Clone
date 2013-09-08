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

package org.peerfact.impl.service.aggregation.skyeye.analyzing.analyzers.postProcessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.peerfact.Constants;
import org.peerfact.api.service.skyeye.SkyNetPostProcessing;
import org.peerfact.impl.service.aggregation.skyeye.metrics.MetricsAggregate;
import org.peerfact.impl.service.aggregation.skyeye.queries.Query;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryAddend;
import org.peerfact.impl.service.aggregation.skyeye.queries.QueryReplyingPeer;
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
 * This class implements the interface {@link SkyNetPostProcessing} and is
 * responsible for processing the data, which was monitored and collected during
 * a simulation in terms of the queries. The captured data is situated in the
 * queryData-directory and will be utilized within this class to generate some
 * dat-files, which are used for the visualization within gnuPlot.<br>
 * <br>
 * <code>StartedQueries.dat</code>, <code>HopsOfSolvedQueries.dat</code> and
 * <code>AbsoluteSolvedQueries.dat</code> contain the different levels of the
 * query-origination, the amount of hops of the queries as well as the different
 * levels of the query-resolution.<br>
 * <br>
 * <code>QualityOfQueries.dat</code>, <code>HitsPerLevel.dat</code> and
 * <code>PercHitsOfUnsolvedQueries.dat</code> outline the percentage of
 * connected peers within an answer to a query, show how many matches are found
 * at a level in the SkyNet-tree and describe the percentage of retrieved peers
 * in unsolved queries.<br>
 * <br>
 * <code>QueryMatrix.dat</code> displays the relation between the level of
 * origination and resolution of queries. Besides, the file also shows, where
 * the queries with different complexities are solved within the tree. The
 * complexity can comprise the amount of searched peers or varying conditions.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 05.12.2008
 * 
 */
public class QueryPostProcessor implements SkyNetPostProcessing {

	private static Logger log = SimLogger.getLogger(QueryPostProcessor.class);

	private static final String STARTED_QUERY_DAT_FILE = "StartedQueries.dat";

	private static final String HOPS_FOR_SOLVED_QUERY_DAT_FILE = "HopsOfSolvedQueries.dat";

	private static final String ABSOLUTE_SOLVED_QUERY_DAT_FILE = "AbsoluteSolvedQueries.dat";

	private static final String ANSWER_QUALITY_OF_QUERY_DAT_FILE = "QualityOfQueries.dat";

	private static final String HITS_PER_LEVEL_DAT_FILE = "HitsPerLevel.dat";

	private static final String PERC_HITS_UNSOLVED_QUERIES_DAT_FILE = "PercHitsOfUnsolvedQueries.dat";

	private static final String QUERY_MATRIX_DAT_FILE = "QueryMatrix.dat";

	private final String writingDataPath;

	private static String READING_DATA_PATH = Constants.TMP_DIR
			+ File.separator + "queryData";

	private String[] files;

	private PrintWriter qWriter;

	// collections and fields for handling the de-serialization

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> startedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> tempStartedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> failedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> tempFailedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> unsolvedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> tempUnsolvedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> solvedQueries;

	private LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>> tempSolvedQueries;

	private int numberOfStartedQueries;

	private int numberOfUnsolvedQueries;

	private int numberOfSolvedQueries;

	// collections for post-processing the stored data

	private final TreeMap<Integer, Integer> levelOfQueryOrigination;

	private final TreeMap<Integer, Integer> absoluteQuerySolution;

	private final TreeMap<Integer, Integer> hopsForQuerySolution;

	private final TreeMap<Float, Integer> answerQualities;

	private final TreeMap<Integer, Integer> hitsPerLevel;

	private final TreeMap<Float, Integer> percHitsOfUnsolvedQueries;

	private final TreeMap<Integer, TreeMap<Integer, QueryDataEntry>> queryMatrix;

	private int yMax;

	public QueryPostProcessor(String dataPath) {
		writingDataPath = dataPath;
		levelOfQueryOrigination = new TreeMap<Integer, Integer>();
		absoluteQuerySolution = new TreeMap<Integer, Integer>();
		hopsForQuerySolution = new TreeMap<Integer, Integer>();
		answerQualities = new TreeMap<Float, Integer>();
		hitsPerLevel = new TreeMap<Integer, Integer>();
		percHitsOfUnsolvedQueries = new TreeMap<Float, Integer>();
		queryMatrix = new TreeMap<Integer, TreeMap<Integer, QueryDataEntry>>();
		yMax = 0;
	}

	// ----------------------------------------------------------------------
	// methods for de-serializing the query-data out of the files
	// ----------------------------------------------------------------------

	@Override
	public void extractDataOfFiles() {
		getListOfTempFiles(READING_DATA_PATH);
		boolean first = true;
		if (files != null) {
			File file;
			for (int i = 0; i < files.length; i++) {
				file = new File(READING_DATA_PATH + File.separator + files[i]);
				// log.debug("  Reading " + (i + 1) + ". query-file: "
				// + file.getName());
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(file));
					if (file.getName().startsWith("temp")) {
						if (first) {
							first = false;
							startedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							failedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							unsolvedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							solvedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							ois.close();
						} else {
							tempStartedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							tempFailedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							tempUnsolvedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							tempSolvedQueries = (LinkedHashMap<BigDecimal, LinkedHashMap<Integer, Query>>) ois
									.readObject();
							ois.close();
							mergeStartedQueries();
							mergeFailedQueries();
							mergeUnsolvedQueries();
							mergeSolvedQueries();
						}
					} else {
						numberOfStartedQueries = ois.readInt();
						// read the number of failed queries- actually not
						// needed
						ois.readInt();
						numberOfUnsolvedQueries = ois.readInt();
						numberOfSolvedQueries = ois.readInt();
						ois.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void mergeStartedQueries() {
		Iterator<BigDecimal> idIter = tempStartedQueries.keySet().iterator();
		BigDecimal id = null;
		LinkedHashMap<Integer, Query> tempQueries = null;
		LinkedHashMap<Integer, Query> queries = null;
		while (idIter.hasNext()) {
			id = idIter.next();
			tempQueries = tempStartedQueries.get(id);
			queries = startedQueries.remove(id);
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.putAll(tempQueries);
			startedQueries.put(id, queries);
		}
	}

	private void mergeFailedQueries() {
		Iterator<BigDecimal> idIter = tempFailedQueries.keySet().iterator();
		BigDecimal id = null;
		LinkedHashMap<Integer, Query> tempQueries = null;
		LinkedHashMap<Integer, Query> queries = null;
		while (idIter.hasNext()) {
			id = idIter.next();
			tempQueries = tempFailedQueries.get(id);
			queries = failedQueries.remove(id);
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.putAll(tempQueries);
			failedQueries.put(id, queries);
		}
	}

	private void mergeUnsolvedQueries() {
		Iterator<BigDecimal> idIter = tempUnsolvedQueries.keySet().iterator();
		BigDecimal id = null;
		LinkedHashMap<Integer, Query> tempQueries = null;
		LinkedHashMap<Integer, Query> queries = null;
		while (idIter.hasNext()) {
			id = idIter.next();
			tempQueries = tempUnsolvedQueries.get(id);
			queries = unsolvedQueries.remove(id);
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.putAll(tempQueries);
			unsolvedQueries.put(id, queries);
		}
	}

	private void mergeSolvedQueries() {
		Iterator<BigDecimal> idIter = tempSolvedQueries.keySet().iterator();
		BigDecimal id = null;
		LinkedHashMap<Integer, Query> tempQueries = null;
		LinkedHashMap<Integer, Query> queries = null;
		while (idIter.hasNext()) {
			id = idIter.next();
			tempQueries = tempSolvedQueries.get(id);
			queries = solvedQueries.remove(id);
			if (queries == null) {
				queries = new LinkedHashMap<Integer, Query>();
			}
			queries.putAll(tempQueries);
			solvedQueries.put(id, queries);
		}
	}

	// ----------------------------------------------------------------------
	// methods for processing the query-data
	// ----------------------------------------------------------------------

	@Override
	public void processData() {
		processingQueryOrigination();
		processingQueryResults();
		processQuerySolving();
		processPercHitsOfUnsolvedQueries();
		createQueryMatrix();
	}

	private void processingQueryOrigination() {
		Iterator<BigDecimal> idIterator = startedQueries.keySet().iterator();
		LinkedHashMap<Integer, Query> queries = null;
		Iterator<Integer> queryIterator = null;
		Query query = null;
		int startingLevel = -1;
		int count = 0;
		// processing data counting the different levels of query-origination
		while (idIterator.hasNext()) {
			queries = startedQueries.get(idIterator.next());
			queryIterator = queries.keySet().iterator();
			while (queryIterator.hasNext()) {
				query = queries.get(queryIterator.next());
				startingLevel = query.getQueryOriginator().getLevel();
				Integer amount = levelOfQueryOrigination.remove(startingLevel);
				if (amount == null) {
					amount = 0;
				}
				count = amount.intValue() + 1;
				levelOfQueryOrigination.put(Integer.valueOf(startingLevel),
						Integer.valueOf(count));
			}
		}
	}

	private void processingQueryResults() {
		Iterator<BigDecimal> idIterator = solvedQueries.keySet().iterator();
		LinkedHashMap<Integer, Query> queries = null;
		Iterator<Integer> queryIterator = null;
		Query query = null;
		int count = 0;
		int solutionLevel = -1;
		int hops = -1;
		float answerQuality = -1;
		while (idIterator.hasNext()) {
			queries = solvedQueries.get(idIterator.next());
			queryIterator = queries.keySet().iterator();
			while (queryIterator.hasNext()) {
				query = queries.get(queryIterator.next());
				int solvedAddend = query.getIndexOfSolvedAddend();
				if (solvedAddend == -1) {
					// log.error("Something wrong");
				} else {
					// calculation for absolute level
					solutionLevel = query.getAddend(solvedAddend)
							.getSolvingLevel();
					Integer amount = absoluteQuerySolution
							.remove(solutionLevel);
					if (amount == null) {
						amount = 0;
					}
					count = amount.intValue() + 1;
					absoluteQuerySolution.put(Integer.valueOf(solutionLevel),
							Integer.valueOf(count));

					// calculation for hops
					hops = query.getHops();
					amount = hopsForQuerySolution.remove(hops);
					if (amount == null) {
						amount = 0;
					}
					count = amount.intValue() + 1;
					hopsForQuerySolution.put(Integer.valueOf(hops),
							Integer.valueOf(
									count));

					// calculation for answer Quality
					answerQuality = query.getAnswerQuality();
					amount = answerQualities.remove(answerQuality);
					if (amount == null) {
						amount = 0;
					}
					count = amount.intValue() + 1;
					answerQualities.put(Float.valueOf(answerQuality),
							Integer.valueOf(
									count));
				}
			}
		}
	}

	private void processQuerySolving() {

		Iterator<BigDecimal> idIterator = solvedQueries.keySet().iterator();
		LinkedHashMap<Integer, Query> queries = null;
		Iterator<Integer> queryIterator = null;
		Query query = null;
		QueryAddend addend = null;
		LinkedHashMap<BigDecimal, QueryReplyingPeer> replyingPeers = null;
		Iterator<BigDecimal> rpIterator = null;
		QueryReplyingPeer replyingPeer = null;
		int count = 0;
		int level = -1;
		while (idIterator.hasNext()) {
			queries = solvedQueries.get(idIterator.next());
			queryIterator = queries.keySet().iterator();
			while (queryIterator.hasNext()) {
				query = queries.get(queryIterator.next());
				// loop over all addends
				for (int i = 0; i < query.getNumberOfAddends(); i++) {
					addend = query.getAddend(i);
					replyingPeers = addend.getReplyingPeers();
					rpIterator = replyingPeers.keySet().iterator();
					// in every addend loop over all query-answering peers
					while (rpIterator.hasNext()) {
						replyingPeer = replyingPeers.get(rpIterator.next());
						if (replyingPeer.getNumberOfReplies() > 0) {
							level = replyingPeer.getLevel();
							Integer amount = hitsPerLevel.remove(level);
							if (amount == null) {
								amount = 0;
							}
							count = amount.intValue()
									+ replyingPeer.getNumberOfReplies();
							hitsPerLevel.put(Integer.valueOf(level),
									Integer.valueOf(
											count));
						}
					}
				}
			}
		}

		idIterator = unsolvedQueries.keySet().iterator();
		queries = null;
		queryIterator = null;
		query = null;
		addend = null;
		replyingPeers = null;
		rpIterator = null;
		replyingPeer = null;
		count = 0;
		level = -1;
		while (idIterator.hasNext()) {
			queries = unsolvedQueries.get(idIterator.next());
			queryIterator = queries.keySet().iterator();
			while (queryIterator.hasNext()) {
				query = queries.get(queryIterator.next());
				// loop over all addends
				for (int i = 0; i < query.getNumberOfAddends(); i++) {
					addend = query.getAddend(i);
					replyingPeers = addend.getReplyingPeers();
					rpIterator = replyingPeers.keySet().iterator();
					// in every addend loop over all query-answering peers
					while (rpIterator.hasNext()) {
						replyingPeer = replyingPeers.get(rpIterator.next());
						if (replyingPeer.getNumberOfReplies() > 0) {
							level = replyingPeer.getLevel();
							Integer amount = hitsPerLevel.remove(level);
							if (amount == null) {
								amount = 0;
							}
							count = amount.intValue()
									+ replyingPeer.getNumberOfReplies();
							hitsPerLevel.put(Integer.valueOf(level),
									Integer.valueOf(
											count));
						}
					}
				}
			}
		}
	}

	private void processPercHitsOfUnsolvedQueries() {
		Iterator<BigDecimal> idIterator = unsolvedQueries.keySet().iterator();
		LinkedHashMap<Integer, Query> queries = null;
		Iterator<Integer> queryIterator = null;
		Query query = null;
		QueryAddend addend = null;
		float percentage = -1;
		float remainedElements = -1;
		float foundElements = -1;
		int count = -1;
		while (idIterator.hasNext()) {
			queries = unsolvedQueries.get(idIterator.next());
			queryIterator = queries.keySet().iterator();
			while (queryIterator.hasNext()) {
				query = queries.get(queryIterator.next());
				for (int i = 0; i < query.getNumberOfAddends(); i++) {
					addend = query.getAddend(i);
					remainedElements = addend.getSearchedElements();
					foundElements = addend.getMatches().size();
					percentage = foundElements
							/ (foundElements + remainedElements);

					Integer amount = percHitsOfUnsolvedQueries
							.remove(new Float(percentage));
					if (amount == null) {
						amount = 0;
					}
					count = amount.intValue() + 1;
					percHitsOfUnsolvedQueries.put(Float.valueOf(percentage),
							Integer.valueOf(count));
				}
			}
		}
	}

	private void createQueryMatrix() {
		Iterator<BigDecimal> idIter = solvedQueries.keySet().iterator();
		LinkedHashMap<Integer, Query> queries = null;
		Iterator<Integer> queryIterator = null;
		Query query = null;
		TreeMap<Integer, QueryDataEntry> secondDim = null;
		MetricsAggregate ag = null;
		String[] types;
		QueryDataEntry value = null;
		int startLevel = -1;
		int solveLevel = -1;
		int counter = -1;
		while (idIter.hasNext()) {
			queries = solvedQueries.get(idIter.next());
			queryIterator = queries.keySet().iterator();
			while (queryIterator.hasNext()) {
				query = queries.get(queryIterator.next());
				startLevel = query.getQueryOriginator().getLevel();
				solveLevel = query.getAddend(query.getIndexOfSolvedAddend())
						.getSolvingLevel();

				secondDim = queryMatrix.remove(startLevel);
				if (secondDim == null) {
					secondDim = new TreeMap<Integer, QueryDataEntry>();
					if (query.getQueryType() != null) {
						types = query.getQueryType().split(":");
						ag = createAggregate(types[0],
								Double.parseDouble(types[1]));
						if (types[0].equals("PeerVariation")) {
							secondDim.put(Integer.valueOf(solveLevel),
									new QueryDataEntry(1, ag, true));
						} else if (types[0].equals("ConditionVariation")) {
							secondDim.put(Integer.valueOf(solveLevel),
									new QueryDataEntry(1, ag, false));
						} else {
							secondDim.put(Integer.valueOf(solveLevel),
									new QueryDataEntry(1));
						}
					} else {
						secondDim.put(Integer.valueOf(solveLevel),
								new QueryDataEntry(1));
					}
					queryMatrix.put(Integer.valueOf(startLevel), secondDim);
				} else {
					yMax = Math.max(yMax, secondDim.lastKey().intValue() - 1);
					value = secondDim.remove(solveLevel);
					if (value == null) {
						value = new QueryDataEntry();
					}
					counter = value.getCounter() + 1;
					if (query.getQueryType() != null) {
						types = query.getQueryType().split(":");
						ag = createAggregate(types[0],
								Double.parseDouble(types[1]));
						if (types[0].equals("PeerVariation")) {
							ag = mergeTwoAggregates(ag,
									value.getPeerVariation());
							value.setCounter(counter);
							value.setPeerVariation(ag);
						} else if (types[0].equals("ConditionVariation")) {
							ag = mergeTwoAggregates(ag,
									value.getConditionVarition());
							value.setCounter(counter);
							value.setConditionVarition(ag);
						} else {
							value.setCounter(counter);
						}
					} else {
						value.setCounter(counter);
					}
					secondDim.put(Integer.valueOf(solveLevel), value);
					queryMatrix.put(Integer.valueOf(startLevel), secondDim);

				}
			}
		}
	}

	// ----------------------------------------------------------------------
	// methods for writing the processed data in dat-files
	// ----------------------------------------------------------------------

	@Override
	public void writeDataFile() {
		writeIntQueryDataFile(writingDataPath + File.separator
				+ STARTED_QUERY_DAT_FILE, levelOfQueryOrigination,
				numberOfStartedQueries);
		writeIntQueryDataFile(writingDataPath + File.separator
				+ HOPS_FOR_SOLVED_QUERY_DAT_FILE, hopsForQuerySolution,
				numberOfSolvedQueries);
		writeIntQueryDataFile(writingDataPath + File.separator
				+ ABSOLUTE_SOLVED_QUERY_DAT_FILE, absoluteQuerySolution,
				numberOfSolvedQueries);
		writeIntQueryDataFile(writingDataPath + File.separator
				+ HITS_PER_LEVEL_DAT_FILE, hitsPerLevel, 1);
		writeFloatQueryDataFile(writingDataPath + File.separator
				+ ANSWER_QUALITY_OF_QUERY_DAT_FILE, answerQualities,
				numberOfSolvedQueries);
		writeFloatQueryDataFile(writingDataPath + File.separator
				+ PERC_HITS_UNSOLVED_QUERIES_DAT_FILE,
				percHitsOfUnsolvedQueries, numberOfUnsolvedQueries);
		writeQueryMatrixDataFile(writingDataPath + File.separator
				+ QUERY_MATRIX_DAT_FILE, queryMatrix);
		if (log.isDebugEnabled()) {
			writeMatrixToLogFile(queryMatrix);
		}
	}

	private void writeIntQueryDataFile(String fileName,
			TreeMap<Integer, Integer> dataMap, float absoluteValue) {
		try {
			qWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(fileName))), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int offset = 0;
		Integer level = null;
		Iterator<Integer> levelIterator = dataMap.keySet().iterator();
		while (levelIterator.hasNext()) {
			level = levelIterator.next();
			if (offset < level.intValue()) {
				for (int i = offset; i < level.intValue(); i++) {
					qWriter.println(i + " " + 0);
				}
			}
			qWriter.println(level + " "
					+ ((float) dataMap.get(level) / absoluteValue));
			offset = level.intValue() + 1;
		}
		qWriter.flush();
		qWriter.close();
	}

	private void writeFloatQueryDataFile(String fileName,
			TreeMap<Float, Integer> dataMap, float absoluteValue) {
		try {
			qWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(fileName))), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator<Float> qualityIterator = dataMap.keySet().iterator();
		float quality = -1;
		if (qualityIterator.hasNext()) {
			while (qualityIterator.hasNext()) {
				quality = qualityIterator.next();
				qWriter.println(quality + " "
						+ ((float) dataMap.get(quality) / absoluteValue));
			}
		} else {
			qWriter.println(0 + " " + 0);
		}
		qWriter.flush();
		qWriter.close();
	}

	private static void writeMatrixToLogFile(
			TreeMap<Integer, TreeMap<Integer, QueryDataEntry>> matrix) {
		Iterator<Integer> xIter = matrix.keySet().iterator();
		TreeMap<Integer, QueryDataEntry> secondDim = null;
		Iterator<Integer> yIter = null;
		Integer x = null;
		Integer y = null;
		QueryDataEntry value = null;
		while (xIter.hasNext()) {
			x = xIter.next();
			secondDim = matrix.get(x);
			yIter = secondDim.keySet().iterator();
			while (yIter.hasNext()) {
				y = yIter.next();
				value = secondDim.get(y);
				log.debug(x + " " + y + " " + value.getCounter() + " "
						+ min(value.getPeerVariation().getMinimum()) + " "
						+ value.getPeerVariation().getMaximum() + " "
						+ value.getPeerVariation().getAverage() + " "
						+ value.getPeerVariation().getStandardDeviation() + " "
						+ min(value.getConditionVarition().getMinimum()) + " "
						+ value.getConditionVarition().getMaximum() + " "
						+ value.getConditionVarition().getAverage() + " "
						+ value.getConditionVarition().getStandardDeviation());
			}
			log.debug("");
		}
	}

	private void writeQueryMatrixDataFile(String fileName,
			TreeMap<Integer, TreeMap<Integer, QueryDataEntry>> matrix) {
		try {
			qWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(fileName))), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		qWriter.println("# Level of starting peer");
		qWriter.println("# Level of solving peer");
		qWriter.println("# Number of this combination");

		qWriter.println("# Min no of searched peers of this combination");
		qWriter.println("# Max no of searched peers of this combination");
		qWriter.println("# Average no of searched peers of this combination");
		qWriter.println("# Std Dev of no searched peers of this combination");

		qWriter.println("# Min val of searched condition of this combination");
		qWriter.println("# Max val of searched condition of this combination");
		qWriter.println("# Avg val of searched condition of this combination");
		qWriter.println("# Std Dev of val of searched condition of this combination");
		qWriter.println();
		Iterator<Integer> xIter = matrix.keySet().iterator();
		TreeMap<Integer, QueryDataEntry> secondDim = null;
		Iterator<Integer> yIter = null;
		Integer x = null;
		Integer y = null;
		int xOffset = 0;
		int yOffset = 0;
		int xMax = matrix.size() == 0 ? 0 : matrix.lastKey().intValue() + 1;
		QueryDataEntry value = null;
		int counterMaxValue = 0;
		int counterMinValue = Integer.MAX_VALUE;
		while (xIter.hasNext()) {
			x = xIter.next();

			if (xOffset < x.intValue()) {
				for (int i = xOffset; i < x.intValue(); i++) {
					for (int j = 0; j < yMax + 2; j++) {
						qWriter.println(i + " " + j + " 0" + " 0" + " 0" + " 0"
								+ " 0" + " 0" + " 0" + " 0" + " 0");
					}
					qWriter.println();
				}
			}
			xOffset = x.intValue() + 1;

			secondDim = matrix.get(x);
			yIter = secondDim.keySet().iterator();
			while (yIter.hasNext()) {
				y = yIter.next();

				if (yOffset < y.intValue()) {
					for (int i = yOffset; i < y.intValue(); i++) {
						qWriter.println(x.intValue() + " " + i + " 0" + " 0"
								+ " 0" + " 0" + " 0" + " 0" + " 0" + " 0"
								+ " 0");
					}
				}
				yOffset = y.intValue() + 1;
				value = secondDim.get(y);
				counterMaxValue = Math.max(counterMaxValue, value.getCounter());
				counterMinValue = Math.min(counterMinValue, value.getCounter());
				qWriter.println(x + " " + y + " " + value.getCounter() + " "
						+ min(value.getPeerVariation().getMinimum()) + " "
						+ value.getPeerVariation().getMaximum() + " "
						+ value.getPeerVariation().getAverage() + " "
						+ value.getPeerVariation().getStandardDeviation() + " "
						+ min(value.getConditionVarition().getMinimum()) + " "
						+ value.getConditionVarition().getMaximum() + " "
						+ value.getConditionVarition().getAverage() + " "
						+ value.getConditionVarition().getStandardDeviation());
			}
			if (yOffset < xMax) {
				for (int i = yOffset; i < yMax + 2; i++) {
					qWriter.println(x.intValue() + " " + i + " 0" + " 0" + " 0"
							+ " 0" + " 0" + " 0" + " 0" + " 0" + " 0");
				}
			}
			yOffset = 0;
			qWriter.println();
		}
		qWriter.println("# MinValue = " + counterMinValue);
		qWriter.println("# MaxValue = " + counterMaxValue);

		qWriter.flush();
		qWriter.close();
	}

	private void getListOfTempFiles(String dir) {
		File fileDir = new File(dir);
		if (checkForDirectory(fileDir)) {
			files = fileDir.list();
			Arrays.sort(files);
		}
	}

	private static boolean checkForDirectory(File name) {
		if (name.exists() && name.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	private static double min(Double value) {
		if (value.doubleValue() == Integer.MAX_VALUE) {
			return 0;
		} else {
			return value.doubleValue();
		}
	}

	private static MetricsAggregate mergeTwoAggregates(MetricsAggregate first,
			MetricsAggregate second) {
		if (second == null) {
			return new MetricsAggregate(first.getAggregateName(),
					first.getMinimum(), first.getMaximum(), first
							.getSumOfAggregates().doubleValue(), first
							.getSumOfSquares().doubleValue(),
					first.getNodeCount(), first.getMinTime(),
					first.getMaxTime(), first.getAvgTime());

		} else {
			long minTime = Math.min(first.getMinTime(), second.getMinTime());
			long maxTime = Math.max(first.getMaxTime(), second.getMaxTime());
			long avgTime = (first.getAvgTime() * first.getNodeCount() + second
					.getAvgTime() * second.getNodeCount())
					/ (first.getNodeCount() + second.getNodeCount());

			return new MetricsAggregate(first.getAggregateName(), Math.min(
					first.getMinimum(), second.getMinimum()), Math.max(
					first.getMaximum(), second.getMaximum()),
					first.getSumOfAggregates() + second.getSumOfAggregates(),
					first.getSumOfSquares() + second.getSumOfSquares(),
					first.getNodeCount() + second.getNodeCount(), minTime,
					maxTime, avgTime);
		}
	}

	private static MetricsAggregate createAggregate(String name, double value) {
		return new MetricsAggregate(name, value, value, value, value * value,
				1, 0, 0, 0);
	}
}
