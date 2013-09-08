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

package org.peerfact.impl.analyzer.visualization2d.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.peerfact.impl.analyzer.visualization2d.util.Config;


/**
 * Lets objects filter from the data model for display.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 04.12.2008
 * 
 */
public class ModelFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 635194455814496635L;

	static final boolean defaultOn = true;

	static final String CFG_PATH = "Model/Filter/";

	public List<ModelFilterListener> listeners = new ArrayList<ModelFilterListener>();

	Map<Integer, TypeInfo> edgeTypes = new LinkedHashMap<Integer, TypeInfo>();

	Map<Class<? extends Object>, List<TypeInfo>> classSets = new LinkedHashMap<Class<? extends Object>, List<TypeInfo>>();

	List<Class<? extends Object>> classes = new ArrayList<Class<? extends Object>>();

	List<TypeInfo> sortedList = null;

	/**
	 * Whether the list has changed since the last call.
	 */
	boolean listHasChanged = true;

	public void registerType(TypeObject e) {
		int typeUID = e.getUniqueTypeIdentifier();

		if (!edgeTypes.containsKey(typeUID)) {

			TypeInfo info = new TypeInfo(e);
			edgeTypes.put(typeUID, info);

			if (!classSets.containsKey(e.getClass())) {
				List<TypeInfo> typeList = new ArrayList<TypeInfo>();
				classSets.put(e.getClass(), typeList);
				classes.add(e.getClass());
				typeList.add(info);
			} else {
				classSets.get(e.getClass()).add(info);
			}
		}

		listHasChanged = true;

	}

	public List<TypeInfo> getAllTypeUIDsForClass(
			Class<? extends Object> edgeClass) {
		return classSets.get(edgeClass);
	}

	public List<Class<?>> getAllClasses() {
		return classes;
	}

	public List<TypeInfo> getAllTypes() {
		if (listHasChanged) {
			sortedList = new ArrayList<TypeInfo>();
			for (Class<?> c : classes) {
				sortedList.addAll(getAllTypeUIDsForClass(c));
			}
			listHasChanged = false;
		}
		return sortedList;
	}

	public TypeInfo getTypeInfoFor(int typeUID) {
		return edgeTypes.get(typeUID);
	}

	public void setTypeActivated(int typeUID, boolean activated) {
		TypeInfo info = edgeTypes.get(typeUID);
		if (info != null) {
			info.typeActivated = activated;
		}
	}

	public boolean typeActivated(TypeObject e) {

		int typeUID = e.getUniqueTypeIdentifier();
		TypeInfo info = edgeTypes.get(typeUID);
		return (info == null) ? defaultOn : info.typeActivated;

	}

	public void addModelFilterListener(ModelFilterListener l) {
		listeners.add(l);
	}

	public void removeModelFilterListener(ModelFilterListener l) {
		listeners.remove(l);
	}

	public void modelFilterReloaded() {
		for (ModelFilterListener l : listeners) {
			l.modelFilterReloaded(this);
		}
	}

	public void modelFilterChanged() {
		for (ModelFilterListener l : listeners) {
			l.filterChanged(this);
		}
	}

	public static class TypeInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3093308336203480982L;

		boolean typeActivated;

		public Class<? extends Object> typeClass;

		public String typeName;

		public ImageIcon typeIcon;

		public Color typeColor;

		public int typeID;

		public TypeInfo(TypeObject e) {
			typeID = e.getUniqueTypeIdentifier();
			typeActivated = Config.getValue(getCfgPathName(typeID), defaultOn);
			typeClass = e.getClass();
			typeName = e.getTypeName();
			typeIcon = e.getRepresentingIcon();
			typeColor = e.getColor();
		}

		public boolean isTypeActivated() {
			return typeActivated;
		}

		public void setTypeActivated(boolean typeActivated) {
			this.typeActivated = typeActivated;
			Config.setValue(getCfgPathName(typeID), typeActivated);
		}

		private String getCfgPathName(int typeID2) {
			return CFG_PATH + "UID" + typeID + "/Activated";
		}
	}
}
