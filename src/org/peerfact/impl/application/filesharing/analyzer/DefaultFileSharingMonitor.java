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

package org.peerfact.impl.application.filesharing.analyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.peerfact.api.analyzer.Analyzer;
import org.peerfact.api.overlay.OverlayContact;
import org.peerfact.api.overlay.OverlayID;
import org.peerfact.impl.common.DefaultMonitor;


/**
 * Default file sharing monitor implementation which is called by components
 * whenever an action occurs that is important to trace. In particular, upon
 * calling a specific monitor method, the monitor delegates notifications to all
 * installed analyzers.
 * 
 * @author Matthias Feldotto <info@peerfact.org>
 * 
 */
public class DefaultFileSharingMonitor extends DefaultMonitor implements
		FileSharingMonitor {

	private final List<FileSharingAnalyzer> fileSharingAnalyzer;

	public DefaultFileSharingMonitor() {
		super();
		fileSharingAnalyzer = new LinkedList<FileSharingAnalyzer>();
	}

	@Override
	public final void setAnalyzer(final Analyzer analyzer) {
		super.setAnalyzer(analyzer);
		if (analyzer instanceof FileSharingAnalyzer) {
			this.fileSharingAnalyzer.add((FileSharingAnalyzer) analyzer);
		}
	}

	@Override
	public void downloadStarted(OverlayContact<OverlayID<?>> initiator,
			Object queryUID) {
		if (isMonitoring) {
			for (FileSharingAnalyzer fsAna : fileSharingAnalyzer) {
				fsAna.downloadStarted(initiator, queryUID);
			}
		}
	}

	@Override
	public void downloadSucceeded(OverlayContact<OverlayID<?>> initiator,
			Object queryUID, long filesize) {
		if (isMonitoring) {
			for (FileSharingAnalyzer fsAna : fileSharingAnalyzer) {
				fsAna.downloadSucceeded(initiator, queryUID, filesize);
			}
		}
	}

	@Override
	public void publishStarted(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish, Object queryUID) {
		if (isMonitoring) {
			for (FileSharingAnalyzer fsAna : fileSharingAnalyzer) {
				fsAna.publishStarted(initiator, keyToPublish, queryUID);
			}
		}
	}

	@Override
	public void publishSucceeded(OverlayContact<OverlayID<?>> initiator,
			Set<OverlayContact<OverlayID<?>>> holder, int keyPublished,
			Object queryUID,
			long filesize) {
		if (isMonitoring) {
			for (FileSharingAnalyzer fsAna : fileSharingAnalyzer) {
				fsAna.publishSucceeded(initiator, holder, keyPublished,
						queryUID, filesize);
			}
		}
	}

	@Override
	public void downloadFailed(OverlayContact<OverlayID<?>> initiator,
			Object queryUID) {
		if (isMonitoring) {
			for (FileSharingAnalyzer fsAna : fileSharingAnalyzer) {
				fsAna.downloadFailed(initiator, queryUID);
			}
		}

	}

	@Override
	public void publishFailed(OverlayContact<OverlayID<?>> initiator,
			int keyToPublish,
			Object queryUID) {
		if (isMonitoring) {
			for (FileSharingAnalyzer fsAna : fileSharingAnalyzer) {
				fsAna.publishFailed(initiator, keyToPublish, queryUID);
			}
		}

	}

}
