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

package org.peerfact.impl.scenario;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.peerfact.api.scenario.Builder;
import org.peerfact.api.scenario.Composable;
import org.peerfact.api.scenario.Configurable;
import org.peerfact.api.scenario.ConfigurationException;
import org.peerfact.api.scenario.Configurator;
import org.peerfact.impl.simengine.Simulator;
import org.peerfact.impl.util.logging.SimLogger;
import org.peerfact.impl.util.toolkits.Dom4jToolkit;
import org.xml.sax.SAXException;

/**
 * The default implementation of the configuration mechanism. For a detailed
 * explanation, see {@link Configurator}.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 14.12.2007
 * 
 */
public class DefaultConfigurator implements Configurator {

	/**
	 * Prefix for a variable inside the config-file. This is used to clarify,
	 * that the provided value for an attribute is not the name of the variable,
	 * but the value, that is represented by the variable.
	 */
	public static final String CONFIG_VARIABLE_PREFIX_TAG = "$";

	/**
	 * The prefix is combined with the name of the currently processed
	 * attribute, that is used to configure the component specified by the
	 * XML-element in the config-file. Via reflection, the newly created
	 * method-name of the implementing class is invoked. So if an XML-element
	 * contains an attribute <code>someAttribute</code>, it is concatenated to
	 * <code>setSomeAttribute</code> and the respective method of the
	 * implementing class, fitting to the created method-signature, is called.
	 */
	public static final String SET_METHOD_PREFIX_TAG = "set";

	/**
	 * Predefined name for the attribute, that specifies the name of the static
	 * method, which instantiates or retrieves the instance of an object.
	 */
	public static final String STATIC_CREATION_METHOD_TAG = "static";

	/**
	 * Predefined name for the attribute, that contains the fully qualified name
	 * of the class, implementing the associated component.
	 */
	public static final String CLASS_TAG = "class";

	/**
	 * If the parser add a XInclude element, then will be add the attribute
	 * "xml:base" to the root element of the adding XML file.
	 */
	public static final String X_INCLUDE_ATTRIBUTE = "base";

	/**
	 * If an attribute within a XML-element contains multiple classes, these
	 * classes are separated by the specified character.
	 */
	protected static final String CLASS_SEPARATOR = ";";

	private static Logger log = SimLogger.getLogger(DefaultConfigurator.class);

	private Map<String, Configurable> configurables = new LinkedHashMap<String, Configurable>();

	private File configFile;

	private Map<String, String> variables = new LinkedHashMap<String, String>();

	private Map<String, String> specificVariables = new LinkedHashMap<String, String>();

	/**
	 * Create new configurator instance with the configuration data in the given
	 * XML file.
	 * 
	 * @param file
	 *            XML config file
	 */
	public DefaultConfigurator(File file) {
		configFile = file;
	}

	/**
	 * Gets the name of the configuration file.
	 * 
	 * @return Name of the configuration file.
	 */
	public File getConfigFile() {
		return configFile;
	}

	/**
	 * Return a copy of the map with variables.
	 * 
	 * @return A copy of stored variables.
	 */
	public Map<String, String> getVariables() {
		Map<String, String> copy = new LinkedHashMap<String, String>();
		for (String key : variables.keySet()) {
			// to copy
			String value = "" + variables.get(key);
			String key2 = "" + key;
			copy.put(key2, value);
		}
		return copy;
	}

	/**
	 * Return a copy of the map with specific variables.
	 * 
	 * @return A copy of specific variables.
	 */
	public Map<String, String> getSpecificVariables() {
		Map<String, String> copy = new LinkedHashMap<String, String>();
		for (String key : specificVariables.keySet()) {
			// to copy
			String value = "" + specificVariables.get(key);
			String key2 = "" + key;
			copy.put(key2, value);
		}
		return copy;
	}

	/**
	 * Register a specific component module by the provided name.
	 * 
	 * @param name
	 *            unique name for the component module
	 * @param component
	 *            component module
	 */
	// TODO maybe we should allow it only for internal usage in this class
	public void register(String name, Configurable component) {
		configurables.put(name, component);
	}

	/**
	 * Configure all components of the simulator. The single components are
	 * either registered via the <code>register(name, component)</code> method
	 * or specified in the config file.
	 * 
	 * @return a collection of components.
	 * @throws ConfigurationException
	 */
	public Collection<Configurable> configureAll()
			throws ConfigurationException {
		log.info("Configure system from file " + configFile);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setXIncludeAware(true);
			factory.setNamespaceAware(false);
			factory.setValidating(false);

			SAXParser parser = factory.newSAXParser();

			SAXReader reader = new SAXReader(parser.getXMLReader());
			Document configuration = reader.read(configFile);

			Element root = configuration.getRootElement();
			assert root.getName().equals(Configurator.CONFIGURATION_ROOT_TAG);
			configureFirstLevel(root);
			return configurables.values();
		} catch (DocumentException e) {
			throw new ConfigurationException(
					"Failed to load configuration from file " + configFile, e);
		} catch (ParserConfigurationException e) {
			throw new ConfigurationException(
					"Failed to load configuration from file " + configFile, e);
		} catch (SAXException e) {
			throw new ConfigurationException(
					"Failed to load configuration from file " + configFile, e);
		}

	}

	/**
	 * Process the XML subtree.
	 * 
	 * @param parent
	 *            root of the subtree
	 */
	private void configureFirstLevel(Element parent) {
		if (log.isDebugEnabled()) {
			log.debug("Configure simulator using " + parent.asXML());
		}
		for (Object obj : parent.elements()) {
			Element elem = (Element) obj;
			if (elem.getName().equals(Configurator.DEFAULT_TAG)) {
				for (Iterator<Element> iter = elem
						.elementIterator(Configurator.VARIABLE_TAG); iter
						.hasNext();) {
					Element variable = iter.next();
					String name = variable
							.attributeValue(Configurator.VARIABLE_NAME_TAG);
					String value = variable
							.attributeValue(Configurator.VARIABLE_VALUE_TAG);
					if (!variables.containsKey(name)) {
						// set to default only if not set yet
						variables.put(name, value);
					}
				}
			} else {
				configureComponent(elem);
			}
		}
	}

	/**
	 * Create (if not existent yet) and configure a configurable component by
	 * parsing the XML subtree.
	 * 
	 * @param elem
	 *            XML subtree with configuration data
	 * @return configured component
	 */
	public Object configureComponent(Element elem) {

		String name = elem.getName();

		if ("IfEqualStr".equalsIgnoreCase(name)) {
			return configureIfEqualStr(elem, true);
		} else if ("IfNotEqualStr".equalsIgnoreCase(name)) {
			return configureIfEqualStr(elem, false);
		}

		log.debug("Configure component " + elem.getName());
		Object component = configurables.get(elem.getName());
		// register new component (if not done yet)
		Set<String> consAttrs = new LinkedHashSet<String>(); // attributes that
		// were
		// part of the
		// constructor
		if (component == null) {
			component = createComponent(elem, consAttrs);
		}

		// configure it
		if (component != null) {
			log.info("Configure component "
					+ component.getClass().getSimpleName() + " with element "
					+ elem.getName());
			configureAttributes(component, elem, consAttrs);
			// configure subcomponents
			if (component instanceof Builder) {
				log.info("Configure builder " + component);
				Builder builder = (Builder) component;
				builder.parse(elem, this);
			} else {
				for (Iterator<Element> iter = elem.elementIterator(); iter
						.hasNext();) {
					Element child = iter.next();
					if (!consAttrs.contains(child.getName().toLowerCase())) {
						processChild(component, child);
					}
				}
			}
		} else {
			// component cannot be created and has not been registered
			log.debug("Skip element " + elem.getName());
		}
		return component;
	}

	private Object configureIfEqualStr(Element ifClause, boolean equal) {

		return processIfEqualStr(ifClause, new EqualStrRunnable() {

			@Override
			public Object run(Element elemToConfigure) {
				return configureComponent(elemToConfigure);
			}
		}, equal);

	}

	private Object createComponent(Element elem, Set<String> consAttrs) {
		if (elem.attribute(CLASS_TAG) == null) {
			return null;
		}

		Object component;
		String className = getAttributeValue(elem.attribute(CLASS_TAG));
		log.debug("Create component " + className + " with element "
				+ elem.getName());
		component = createInstance(className,
				getAttributeValue(elem.attribute(STATIC_CREATION_METHOD_TAG)),
				consAttrs, elem);
		if (component instanceof Configurable) {
			register(elem.getName(), (Configurable) component);
		}
		// composable can use other components
		if (component instanceof Composable) {
			log.debug("Compose composable " + component);
			((Composable) component).compose(this);
		}
		return component;
	}

	Object processChild(Object component, Element child) {

		String name = child.getName();

		if ("IfEqualStr".equalsIgnoreCase(name)) {
			return processIfEqualStr(component, child, true);
		} else if ("IfNotEqualStr".equalsIgnoreCase(name)) {
			return processIfEqualStr(component, child, false);
		}

		Object subcomponent = configureComponent(child);

		String prefix = SET_METHOD_PREFIX_TAG;
		String methodName = getMethodName(prefix, child.getName());
		Method[] methods = component.getClass().getMethods();
		Method match = null;
		for (int i = 0; i < methods.length; i++) {
			if (methodName.equals(methods[i].getName())) {
				match = methods[i];
				log.debug("Match " + match);
				break;
			}
		}
		if (match == null) {
			log.warn("Cannot set " + subcomponent + " as there is no method "
					+ methodName + " declared in " + component);
		} else {
			Class<?>[] types = match.getParameterTypes();
			log.debug("Param types" + Arrays.asList(types));
			if (types.length == 1) {
				try {
					match.invoke(component, types[0].cast(subcomponent));
				} catch (Exception e) {
					throw new ConfigurationException("Failed to configure "
							+ methodName + " in " + component + " with "
							+ subcomponent, e);
				}
			} else {
				throw new ConfigurationException("Wrong number of params for "
						+ methodName + " in " + component);
			}
		}
		return subcomponent;
	}

	private Object processIfEqualStr(final Object component, Element ifClause,
			boolean equal) {

		return processIfEqualStr(ifClause, new EqualStrRunnable() {

			@Override
			public Object run(Element elemToConfigure) {
				return processChild(component, elemToConfigure);
			}
		}, equal);

	}

	public void configureAttributes(Object component, Element elem) {
		Set<String> set = Collections.emptySet();
		configureAttributes(component, elem, set);
	}

	public void configureAttributes(Object component, Element elem,
			Set<String> consAttrs) {
		for (Iterator<Element> iter = elem.attributeIterator(); iter.hasNext();) {
			Attribute attr = (Attribute) iter.next();
			String name = attr.getName();
			if (!name.equals(CLASS_TAG)
					&& !name.equals(STATIC_CREATION_METHOD_TAG)
					&& !consAttrs.contains(name.toLowerCase())
					&& !name.equals(X_INCLUDE_ATTRIBUTE)) {
				try {
					// try to configure as boolean, int, double, String, or long
					String value = getAttributeValue(attr);
					Method method = null;

					String methodName = getMethodName(SET_METHOD_PREFIX_TAG,
							name);
					Class<? extends Object> classToConfigure = component
							.getClass();
					Method[] methods = classToConfigure.getMethods();
					for (int i = 0; i < methods.length; i++) {
						if (methods[i].getName().equals(methodName)
								&& methods[i].getParameterTypes().length == 1) {
							if (method == null) {
								method = methods[i];
							} else {
								log.error("Found two possible methods "
										+ method + " and " + methods[i]);
								throw new IllegalArgumentException(
										"Cannot set property "
												+ name
												+ " as there are more than one matching methods in "
												+ classToConfigure);
							}
						}
					}
					if (method == null) {
						throw new IllegalArgumentException(
								"Cannot set property "
										+ name
										+ " as there are no matching methods in class "
										+ classToConfigure);
					}
					Class<?> typeClass = method.getParameterTypes()[0];
					Object param = convertValue(value, typeClass);
					method.invoke(component, param);
					// TODO legal bool or int parameter could be string!
					// catch (NoSuchMethodException e) { invoke with string ...
				} catch (Exception e) {
					throw new ConfigurationException(
							"Failed to set the property " + name + " in "
									+ component, e);
				}
			}
		}

	}

	/**
	 * Automagically convert the string value to desired type. Supported types
	 * are all simple types, i.e. boolean, int, long, double.
	 * 
	 * @param value
	 * @param typeClass
	 * @return converted
	 */
	public static Object convertValue(String value, Class<?> typeClass) {
		Object param;
		if (typeClass == boolean.class) {
			param = Boolean.valueOf(value.equalsIgnoreCase("true")
					|| value.equalsIgnoreCase("yes"));
		} else if (typeClass == int.class) {
			param = Integer.valueOf(value);
		} else if (typeClass == long.class) {
			param = parseTime(value);
		} else if (typeClass == double.class) {
			param = Double.valueOf(value);
		} else if (typeClass == String.class) {
			param = value;
		} else if (typeClass == short.class) {
			param = Short.valueOf(value);
		} else if (typeClass == Class.class) {
			param = convertToClass(value);
		} else if (typeClass.isArray()
				&& typeClass.getComponentType() == Class.class) {
			String[] valueList = value.split(CLASS_SEPARATOR);
			Class<?>[] paramList = new Class[valueList.length];
			for (int i = 0; i < paramList.length; i++) {
				paramList[i] = convertToClass(valueList[i].trim());
			}
			param = paramList;
		} else {
			throw new IllegalArgumentException("Parameter type " + typeClass
					+ " is not supported");
		}
		return param;
	}

	private static Class<?> convertToClass(String value) {
		try {
			return Class.forName(value);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Failed to parse class object from " + value, e);
		}
	}

	/**
	 * Can be either a variable (if starts with $) or a plain value
	 * 
	 * @param attr
	 * @return proper value
	 */
	private String getAttributeValue(Attribute attr) {
		// TODO implement some arithmetics
		if (attr == null) {
			return null;
		}
		String value = attr.getValue();
		value = parseValue(value);
		if (value == null) {
			throw new IllegalStateException("Variable " + attr.getValue()
					+ " has not been set");
		}
		return value;
	}

	@Override
	public String parseValue(String value) {
		if (value.contains(CONFIG_VARIABLE_PREFIX_TAG)) {
			int posDollar = value.indexOf(CONFIG_VARIABLE_PREFIX_TAG);
			String varName = value.substring(posDollar + 1, value.length());
			String returnvalue = variables.get(varName);
			log.info("Fetched variable " + varName + " as " + returnvalue);
			return returnvalue;
		}
		return value;
	}

	private static String getMethodName(String prefix, String fieldName) {
		return prefix + Character.toUpperCase(fieldName.charAt(0))
				+ fieldName.substring(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.peerfact.impl.scenario.ConfigurablesManager#getComponent(java
	 * .lang.String)
	 */
	// TODO return ComponentFactory?
	@Override
	public Configurable getConfigurable(String name) {// TODO we could use
		// Classesinstead of
		// Strings for type
		// safety ...
		return configurables.get(name);
	}

	/**
	 * Create an instance via the reflection of a class by using the given
	 * (full) class name and the optional method name. If the method name is
	 * null, the default constructor will be used. The method's signature should
	 * have no arguments.
	 * 
	 * @param className
	 * @param staticMethod
	 * @param consAttrs
	 * @return create instance
	 * @throws ConfigurationException
	 */
	private Object createInstance(String className, String staticMethod,
			Set<String> consAttrs, Element element2createfrom)
			throws ConfigurationException {
		try {
			Class<?> forName = Class.forName(className);
			Object component = null;
			if (staticMethod == null) {

				Constructor<?>[] cs = forName.getConstructors();

				for (Constructor<?> c : cs) {
					XMLConfigurableConstructor a = c
							.getAnnotation(XMLConfigurableConstructor.class);
					if (a != null) {
						String[] cArgs = a.value();
						Class<?>[] types = c.getParameterTypes();
						if (cArgs.length != types.length) {
							throw new ConfigurationException(
									"The size of the argument list of the XML configurable constructor ("
											+ Arrays.toString(cArgs)
											+ ") is unequal to the size of arguments of the constructor is was applied to.");
						}

						// Constructor can be called with the given XML
						// attributes.
						Object[] consArgs = new Object[cArgs.length];

						boolean incompatible = false;
						for (int i = 0; i < consArgs.length; i++) {
							Attribute attr = element2createfrom
									.attribute(cArgs[i]);
							if (attr == null) {
								// Element elem =
								// element2createfrom.element(cArgs[i]);
								Element elem = Dom4jToolkit
										.getSubElementFromStrCaseInsensitive(
												element2createfrom, cArgs[i]);
								if (elem == null) {
									incompatible = true;
									break;
								}
								consArgs[i] = configureComponent(elem);
								if (consArgs[i].getClass().isAssignableFrom(
										types[i])) {
									throw new ConfigurationException(
											"The type of the component configured for the parameter '"
													+ cArgs[i]
													+ "', type is "
													+ consArgs[i].getClass()
															.getSimpleName()
													+ " and is not equal to the type "
													+ types[i].getSimpleName()
													+ " required by the constructor as the argument "
													+ i);
								}
							} else {
								consArgs[i] = convertValue(attr.getValue(),
										types[i]);
							}
						}

						if (!incompatible) {
							component = c.newInstance(consArgs);

							for (String consAttr : cArgs) {
								consAttrs.add(consAttr.toLowerCase());
							}
							break;
						}
					}
				}

				if (component == null) {
					component = forName.newInstance();
				}

			} else {
				component = forName.getDeclaredMethod(staticMethod,
						new Class[0]).invoke(null, new Object[0]);
			}
			return component;
		} catch (Exception e) {
			log.error(e);
			throw new ConfigurationException("Failed to create configurable "
					+ className, e);
		}
	}

	/**
	 * Set variables with values which replace the variable names in the
	 * configuration file. Default values will be overwritten.
	 * 
	 * @param variables
	 */
	public void setVariables(Map<String, String> variables) {
		if (variables.size() != 0) {
			log.warn("Set variables " + variables);
		}
		this.variables.putAll(variables);
		this.specificVariables.putAll(variables);
	}

	/**
	 * Parse the time according to the following rule: <code>value</code> is a
	 * number followed by a "ms", "s", "m" or "h" for milliseconds, seconds
	 * etc.. The conversion is done according to the constants defined in the
	 * {@link Simulator} class.
	 * 
	 * @param value
	 *            - time value to parse
	 * @return parsed value
	 */
	public static long parseTime(String value) {
		if (value.matches("\\d+(ms|s|m|h)")) {
			String number;
			long factor;
			if (value.matches("\\d+(ms)")) {
				number = value.substring(0, value.length() - 2);
				factor = Simulator.MILLISECOND_UNIT;
			} else {
				number = value.substring(0, value.length() - 1);
				factor = 1;
				char unit = value.charAt(value.length() - 1);
				switch (unit) {
				case 'h':
					factor *= 60;
					//$FALL-THROUGH$
				case 'm':
					factor *= 60;
					//$FALL-THROUGH$
				case 's':
					factor *= Simulator.SECOND_UNIT;
					break;
				default:
					throw new IllegalStateException("time unit " + unit
							+ " is not allowed");
				}
			}
			return factor * Long.valueOf(number);
		}
		return Long.valueOf(value);
	}

	/**
	 * Writes a time string from a given long simulaton
	 * 
	 * @param value
	 * @return
	 */
	public static String writeTime(long value) {
		if (value == 0) {
			return "0";
		}
		if (value % (3600000l * Simulator.MILLISECOND_UNIT) == 0) {
			return value / (3600000l * Simulator.MILLISECOND_UNIT) + "h";
		}
		if (value % (60000 * Simulator.MILLISECOND_UNIT) == 0) {
			return value / (60000 * Simulator.MILLISECOND_UNIT) + "m";
		}
		if (value % (1000 * Simulator.MILLISECOND_UNIT) == 0) {
			return value / (1000 * Simulator.MILLISECOND_UNIT) + "s";
		}
		if (value % Simulator.MILLISECOND_UNIT == 0) {
			return value / Simulator.MILLISECOND_UNIT + "ms";
		}
		return String.valueOf(value);
	}

	/**
	 * You can create elements like
	 * 
	 * &lt;IfEqualStr arg0="$variable" arg1="value"&gt; [...your configuration
	 * ... ] &lt;/IfEqualStr&gt;, and they will be applied only if the strings
	 * are (not) equal.
	 * 
	 * @param ifClause
	 * @param toExecuteOnTrue
	 * @param equal
	 * @return
	 */
	private Object processIfEqualStr(Element ifClause,
			EqualStrRunnable toExecuteOnTrue, boolean equal) {
		String arg0 = ifClause.attributeValue("arg0");
		String arg1 = ifClause.attributeValue("arg1");

		String arg0p = parseValue(arg0);
		String arg1p = parseValue(arg1);

		if (arg0p == null) {
			throw new RuntimeException("Variable " + arg0 + " not set or null.");
		}
		if (arg1p == null) {
			throw new RuntimeException("Variable " + arg1 + " not set or null.");
		}

		if (equal && arg0p.equals(arg1p) || !equal && !arg0p.equals(arg1p)) {

			Iterator<Element> iter = ifClause.elementIterator();
			if (!iter.hasNext()) {
				log.warn("No component to configure in the ifEqualStr-clause (arg0="
						+ arg0 + ", arg1=" + arg1 + ").");
			} else {
				Element child = iter.next();
				Object result = toExecuteOnTrue.run(child);
				if (iter.hasNext()) {
					throw new RuntimeException(
							"An IfEqualStr-clause may only contain one component to configure.");
				}
				return result;
			}
		}
		return null;

	}

	interface EqualStrRunnable {

		public Object run(Element elemToConfigure);

	}
}
