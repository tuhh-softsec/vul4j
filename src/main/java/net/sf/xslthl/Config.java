/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2008 Michal Molhanec, Jirka Kosek, Michiel Hendriks
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * Michal Molhanec <mol1111 at users.sourceforge.net>
 * Jirka Kosek <kosek at users.sourceforge.net>
 * Michiel Hendriks <elmuerte at users.sourceforge.net>
 */
package net.sf.xslthl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.xslthl.highlighters.AnnotationHighlighter;
import net.sf.xslthl.highlighters.HeredocHighlighter;
import net.sf.xslthl.highlighters.HexaDecimalHighlighter;
import net.sf.xslthl.highlighters.KeywordsHighlighter;
import net.sf.xslthl.highlighters.MultilineCommentHighlighter;
import net.sf.xslthl.highlighters.NestedMultilineCommentHighlighter;
import net.sf.xslthl.highlighters.NumberHighlighter;
import net.sf.xslthl.highlighters.OnelineCommentHighlighter;
import net.sf.xslthl.highlighters.RegexHighlighterEx;
import net.sf.xslthl.highlighters.StringHighlighter;
import net.sf.xslthl.highlighters.WordHighlighter;
import net.sf.xslthl.highlighters.XMLHighlighter;
import net.sf.xslthl.highlighters.HTMLHighlighter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Contains the Xslthl configuration
 */
public class Config {

	/**
	 * System property that defines the path to the configuration file
	 */
	public static final String CONFIG_PROPERTY = "xslthl.config";

	/**
	 * If set to true be verbose during loading of the configuration
	 * 
	 * @deprecated since 2.1 XSLTHL uses the Java logging facility
	 */
	@Deprecated
	public static final String VERBOSE_LOADING_PROPERTY = "xslthl.config.verbose";

	/**
	 * The log level for the xslthl logger
	 * 
	 * @since 2.1
	 */
	public static final String LOGLEVEL_PROPERTY = "xslthl.loglevel";

	/**
	 * Property set to disable external configuration loading
	 */
	public static final String NO_EXTERNAL_PROPERTY = "xslthl.noexternal";

	/**
	 * Instances per config file
	 */
	private static final Map<String, Config> instances = new HashMap<String, Config>();

	/**
	 * Registered highlighter classes
	 */
	public static final Map<String, Class<? extends Highlighter>> highlighterClasses = new HashMap<String, Class<? extends Highlighter>>();

	/**
	 * Prefix used for plug-able highlighters
	 * 
	 * @since 2.1
	 */
	public static final String PLUGIN_PREFIX = "java:";

	static {
		// register builtin highlighers
		highlighterClasses.put("multiline-comment",
		        MultilineCommentHighlighter.class);
		highlighterClasses.put("nested-multiline-comment",
		        NestedMultilineCommentHighlighter.class);
		highlighterClasses.put("oneline-comment",
		        OnelineCommentHighlighter.class);
		highlighterClasses.put("string", StringHighlighter.class);
		highlighterClasses.put("heredoc", HeredocHighlighter.class);
		highlighterClasses.put("keywords", KeywordsHighlighter.class);
		highlighterClasses.put("annotation", AnnotationHighlighter.class);
		highlighterClasses.put("regex", RegexHighlighterEx.class);
		highlighterClasses.put("word", WordHighlighter.class);
		highlighterClasses.put("number", NumberHighlighter.class);
		highlighterClasses.put("hexnumber", HexaDecimalHighlighter.class);
		highlighterClasses.put("xml", XMLHighlighter.class);
		highlighterClasses.put("html", HTMLHighlighter.class);
	}

	/**
	 * Prefix to use on created XML elements
	 */
	protected String prefix = "http://xslthl.sf.net";

	/**
	 * The namespace uri
	 */
	protected String uri = "xslthl";

	/**
	 * Registered highlighters
	 */
	protected Map<String, MainHighlighter> highlighters = new HashMap<String, MainHighlighter>();

	/**
	 * The logging facility
	 */
	protected Logger logger = Logger.getLogger("net.sf.xslthl");

	@Deprecated
	protected boolean verbose;

	/**
	 * Get the default config
	 * 
	 * @return
	 */
	public static Config getInstance() {
		return getInstance(null);
	}

	/**
	 * Get the config from a given file
	 * 
	 * @param filename
	 * @return
	 */
	public static Config getInstance(String filename) {
		String key = (filename == null) ? "" : filename;
		if (!instances.containsKey(key)) {
			Config conf = new Config(filename);
			instances.put(key, conf);
		}
		return instances.get(key);
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Get the highlighter for a given language id, URI or filename.
	 * 
	 * @param id
	 * @return A highlighter, or null if no highlighter exists
	 */
	public MainHighlighter getMainHighlighter(String id) {
		if (id != null && id.length() > 0 && !highlighters.containsKey(id)) {
			if (!Boolean.getBoolean(NO_EXTERNAL_PROPERTY)) {
				// might be a URI
				String uri = null;
				try {
					URI location = new URI(id);
					if (location.getScheme() != null) {
						uri = location.toString();
					}
				} catch (URISyntaxException e) {
				}
				if (uri == null) {
					File floc = new File(id);
					if (floc.getAbsoluteFile().exists()) {
						uri = floc.getAbsoluteFile().toURI().toString();
					} else {
						// no highlighter found, which is not a major problem
						// (i.e. fail silently)
						logger.info(String
						        .format("%s is not an known language id, valid URI, or existing file name",
						                id));
					}
				}
				if (uri != null) {
					try {
						logger.info(String.format(
						        "Loading external highlighter from %s",
						        uri.toString()));
						MainHighlighter hl = loadHl(id, uri);
						highlighters.put(id, hl);
						return hl;
					} catch (Exception e) {
						logger.log(Level.SEVERE, String.format(
						        "Unable to load highlighter from %s: %s", id,
						        e.getMessage()), e);
					}
				}
			}
		}
		return highlighters.get(id);
	}

	/**
	 * Load a language highlighter configuration
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	protected MainHighlighter loadHl(String id, String filename)
	        throws Exception {
		MainHighlighter main = new MainHighlighter(id, filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(filename);
		Set<String> tagNames = new HashSet<String>();
		tagNames.add("highlighter");
		tagNames.add("wholehighlighter");
		createHighlighters(main,
		        new FilteredElementIterator(doc.getDocumentElement(), tagNames));
		return main;
	}

	/**
	 * Creates the defined highlighters
	 * 
	 * @param main
	 * @param elements
	 */
	protected void createHighlighters(MainHighlighter main,
	        Iterator<Element> elements) {
		while (elements.hasNext()) {
			Element hl = elements.next();
			Params params = new Params(hl);
			String type = hl.getAttribute("type");
			try {
				Class<? extends Highlighter> hlClass = highlighterClasses
				        .get(type);
				if (hlClass == null && type.startsWith(PLUGIN_PREFIX)) {
					hlClass = loadPlugin(main, type,
					        hl.getAttribute("classpath"));
				}
				if (hlClass != null) {
					Highlighter hlinstance = hlClass.newInstance();
					hlinstance.init(params);
					main.add(hlinstance);
				} else {
					logger.severe(String
					        .format("Unknown highlighter: %s", type));
				}
			} catch (HighlighterConfigurationException e) {
				logger.log(Level.SEVERE, String.format(
				        "Invalid configuration for highlighter %s: %s", type,
				        e.getMessage()), e);
			} catch (InstantiationException e) {
				logger.log(Level.SEVERE, String.format(
				        "Error constructing highlighter %s: %s", type,
				        e.getMessage()), e);
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, String.format(
				        "IError constructing highlighter %s: %s", type,
				        e.getMessage()), e);
			}
		}
	}

	/**
	 * Load a plugin highlighter
	 * 
	 * @param type
	 * @param classpath
	 * @return The plugin class
	 * @since 2.1
	 */
	protected Class<? extends Highlighter> loadPlugin(MainHighlighter main,
	        String type, String classpath) {
		ClassLoader cl = Config.class.getClassLoader();
		if (classpath != null && classpath.length() > 0) {
			String[] paths = classpath.split(";");
			final List<URL> urls = new ArrayList<URL>();
			for (String path : paths) {
				path = path.trim();
				if (path.length() == 0) {
					continue;
				}
				try {
					URL url;
					if (main.getFilename() == null) {
						url = new URL(path);
					} else {
						URL base = new URL(main.getFilename());
						url = new URL(base, path);
					}
					urls.add(url);
				} catch (MalformedURLException e) {
					logger.log(Level.WARNING, String.format(
					        "Invalid classpath entry %s: %s", path,
					        e.getMessage()), e);
				}
			}
			if (urls.size() > 0) {
				final ClassLoader parentCl = cl;
				PrivilegedAction<URLClassLoader> getCl = new PrivilegedAction<URLClassLoader>() {
					public URLClassLoader run() {
						return new URLClassLoader(urls.toArray(new URL[urls
						        .size()]), parentCl);
					}
				};
				try {
					cl = AccessController.doPrivileged(getCl);
				} catch (Exception e) {
					logger.log(Level.SEVERE, String.format(
					        "Unable to create class loader with urls %s. %s",
					        urls, e.getMessage()), e);
					return null;
				}
			}
		}
		try {
			String className = type.substring(PLUGIN_PREFIX.length());
			Class<?> tmp = cl.loadClass(className);
			if (Highlighter.class.isAssignableFrom(tmp)) {
				return tmp.asSubclass(Highlighter.class);
			} else {
				logger.log(
				        Level.SEVERE,
				        String.format("Class %s is not a subclass of %s",
				                tmp.getName(), Highlighter.class.getName()));
			}
		} catch (ClassNotFoundException e) {
			logger.log(
			        Level.SEVERE,
			        String.format("Unable to resolve highlighter class: %s",
			                e.getMessage()), e);
		}
		return null;
	}

	protected Config() {
		this(null);
	}

	protected Config(String configFilename) {
		Level logLevel = Level.WARNING;
		try {
			logLevel = Level.parse(System.getProperty(LOGLEVEL_PROPERTY));
		} catch (Exception e) {
		}
		logger.setLevel(logLevel);
		logger.info("Initializing xslthl " + Version.getVersion());
		loadConfiguration(configFilename);
	}

	/**
	 * Load the configuration
	 * 
	 * @param configFilename
	 *            When null or empty the default system configuration will be
	 *            used
	 * @since 2.1
	 */
	protected void loadConfiguration(String configFilename) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();

			// Find the configuration filename
			if (configFilename == null || "".equals(configFilename)) {
				logger.config("No config file specified, falling back to default behavior");
				if (System.getProperty(CONFIG_PROPERTY) != null) {
					configFilename = System.getProperty(CONFIG_PROPERTY);
				} else {
					configFilename = "xslthl-config.xml";
				}
			}

			logger.info(String.format("Loading Xslthl configuration from %s",
			        configFilename));
			Document doc = builder.parse(configFilename);
			NodeList hls = doc.getDocumentElement().getElementsByTagName(
			        "highlighter");
			Map<String, MainHighlighter> fileMapping = new HashMap<String, MainHighlighter>();
			for (int i = 0; i < hls.getLength(); i++) {
				// Process the highlighters
				Element hl = (Element) hls.item(i);
				String id = hl.getAttribute("id");

				if (highlighters.containsKey(id)) {
					logger.warning(String.format(
					        "Highlighter with id '%s' already exists!", id));
				}

				String filename = hl.getAttribute("file");
				String absFilename = new URL(new URL(configFilename), filename)
				        .toString();
				if (fileMapping.containsKey(absFilename)) {
					// no need to load the same file twice.
					logger.config(String.format(
					        "Reusing loaded highlighter for %s from %s", id,
					        absFilename));
					highlighters.put(id, fileMapping.get(absFilename));
					continue;
				}
				logger.info(String.format("Loading %s highligter from %s", id,
				        absFilename));
				try {
					MainHighlighter mhl = loadHl(id, absFilename);
					highlighters.put(id, mhl);
					fileMapping.put(absFilename, mhl);
				} catch (Exception e) {
					logger.log(Level.SEVERE, String.format(
					        "Failed to load highlighter from %s: %s",
					        absFilename, e.getMessage()), e);
				}
			}

			// Process the additional settings
			NodeList prefixNode = doc.getDocumentElement()
			        .getElementsByTagName("namespace");
			if (prefixNode.getLength() == 1) {
				Element e = (Element) prefixNode.item(0);
				prefix = e.getAttribute("prefix");
				uri = e.getAttribute("uri");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format(
			        "Cannot read configuration %s: %s", configFilename,
			        e.getMessage()), e);
		}

		if (!highlighters.containsKey("xml")) {
			// add the built-in XML highlighting if it wasn't overloaded
			MainHighlighter xml = new MainHighlighter("xml", null);
			xml.add(new XMLHighlighter());
			highlighters.put("xml", xml);
		}
		if (!highlighters.containsKey("html")) {
			// add the built-in HTML highlighting if it wasn't overloaded
			MainHighlighter html = new MainHighlighter("html", null);
			html.add(new HTMLHighlighter());
			highlighters.put("html", html);
		}
	}
}
