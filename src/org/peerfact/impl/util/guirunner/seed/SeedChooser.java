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

package org.peerfact.impl.util.guirunner.seed;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.peerfact.impl.util.guirunner.impl.ConfigFile;
import org.peerfact.impl.util.guirunner.seed.SeedDetermination.SeedDeterminationChoice;
import org.peerfact.impl.util.guirunner.seed.SeedDetermination.SeedDeterminationListener;


/**
 * AWT component that allows to control options for a SeedDetermination object.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SeedChooser extends JPanel implements ActionListener,
		SeedDeterminationListener, CaretListener, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 564586638916922829L;

	ButtonGroup group = new ButtonGroup();

	private SeedDetermination det;

	JRadioButton fromLastRun;

	JRadioButton newSeed;

	JRadioButton fromConfig;

	JRadioButton customSeed;

	JTextField customSeedField;

	public SeedChooser(SeedDetermination det) {

		super(new GridLayout(0, 1));

		this.det = det;

		det.addListener(this);

		fromLastRun = new JRadioButton();
		fromLastRun.addActionListener(this);
		group.add(fromLastRun);
		this.add(fromLastRun);

		newSeed = new JRadioButton();
		newSeed.addActionListener(this);
		group.add(newSeed);
		this.add(newSeed);

		fromConfig = new JRadioButton();
		fromConfig.addActionListener(this);
		group.add(fromConfig);
		this.add(fromConfig);

		customSeed = new JRadioButton();
		customSeed.addActionListener(this);
		customSeed.setText("Use custom seed: ");
		group.add(customSeed);
		this.add(customSeed);

		customSeedField = new JTextField("0");
		customSeedField.addCaretListener(this);
		customSeedField.addFocusListener(this);
		this.add(customSeedField);

		updateDisplayStrings();
	}

	public void updateDisplayStrings() {

		String lastUsedSeedText = (det.getLastUsedSeed() != SeedDetermination.NULL_SEED) ? " ("
				+ String.valueOf(det.getLastUsedSeed()) + ")"
				: "";

		fromLastRun.setText("From last run" + lastUsedSeedText);
		fromLastRun.setEnabled(det.foundLastUsedSeed());
		fromLastRun.setSelected(det.foundLastUsedSeed());

		newSeed.setText("New seed (" + det.getNewSeed() + ")");

		ConfigFile f = det.getConfigFile();
		if (f == null) {
			fromConfig.setText("From config");
			fromConfig.setEnabled(false);
		} else {
			String s = f.getSeedInConfig();
			fromConfig.setEnabled(s != null);
			fromConfig.setText("From config (" + s + ")");
		}

		newSeed.setSelected(!fromConfig.isEnabled() && !det.foundLastUsedSeed());
		fromConfig
		.setSelected(!det.foundLastUsedSeed && fromConfig.isEnabled());
		customSeed.setSelected(false);

		if (det.foundLastUsedSeed()) {
			det.choose(SeedDeterminationChoice.fromLastRun);
		} else if (fromConfig.isEnabled()) {
			det.choose(SeedDeterminationChoice.fromConfig);
		} else {
			det.choose(SeedDeterminationChoice.newSeed);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fromLastRun) {
			det.choose(SeedDeterminationChoice.fromLastRun);
		}
		if (e.getSource() == newSeed) {
			det.choose(SeedDeterminationChoice.newSeed);
		}
		if (e.getSource() == fromConfig) {
			det.choose(SeedDeterminationChoice.fromConfig);
		}
		if (e.getSource() == customSeed) {
			det.choose(SeedDeterminationChoice.customSeed);
		}
	}

	@Override
	public void fileChanged() {
		updateDisplayStrings();
	}

	@Override
	public void caretUpdate(CaretEvent e2) {
		try {
			det.setCustomSeed(Integer.parseInt(customSeedField.getText()));
			customSeedField.setForeground(Color.BLACK);
		} catch (NumberFormatException e) {
			customSeedField.setForeground(Color.RED);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		customSeed.setSelected(true);
		det.choose(SeedDeterminationChoice.customSeed);
	}

	@Override
	public void focusLost(FocusEvent e) {
		// Nothing to do
	}

}
