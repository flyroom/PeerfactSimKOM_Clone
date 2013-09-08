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

package org.peerfact.impl.analyzer.visualization2d.ui.common.toolbar.elements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.peerfact.impl.analyzer.visualization2d.controller.commands.Command;


/**
 * This class represents a button you can press , so it has one or more commands
 * implemented by the interface controller.commands.Command to execute. Simply
 * appending the command addCommand (Command c), and it is executed when you
 * press the button.
 * 
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class SimpleToolbarButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7278102175284474958L;

	public SimpleToolbarButton() {
		super();
	}

	public SimpleToolbarButton(ImageIcon icon) {
		super(icon);
	}

	public void addCommand(Command c) {
		this.addActionListener(new CommandAdapter(c));
	}

	public static class CommandAdapter implements ActionListener {

		Command adaptedCommand;

		public CommandAdapter(Command c) {
			this.adaptedCommand = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			this.adaptedCommand.execute();
		}

	}

}
