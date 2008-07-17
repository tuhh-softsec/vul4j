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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
     */
    public static final String VERBOSE_LOADING_PROPERTY = "xslthl.config.verbose";

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

    static {
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
     * Get the highlighter for a given language id
     * 
     * @param id
     * @return
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
		    } else if (verbose) {
			System.out
				.println(String
					.format(
						"%s is not an known language id, valid URI, or existing file name",
						id));
		    }
		}
		if (uri != null) {
		    try {
			MainHighlighter hl = loadHl(uri);
			highlighters.put(id, hl);
			return hl;
		    } catch (Exception e) {
			System.err.println(String.format(
				"Unable to load highlighter from %s: %s", id, e
					.getMessage()));
		    }
		}
	    }
	}
	return highlighters.get(id);
    }

    /**
     * Load a highlighter
     * 
     * @param filename
     * @return
     * @throws Exception
     */
    protected MainHighlighter loadHl(String filename) throws Exception {
	MainHighlighter main = new MainHighlighter();
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = dbf.newDocumentBuilder();
	Document doc = builder.parse(filename);
	createHighlighters(main, doc.getDocumentElement().getElementsByTagName(
		"highlighter"));
	// backwards compatibility
	createHighlighters(main, doc.getDocumentElement().getElementsByTagName(
		"wholehighlighter"));
	return main;
    }

    /**
     * Creates the defined highlighters
     * 
     * @param main
     * @param list
     */
    protected void createHighlighters(MainHighlighter main, NodeList list) {
	for (int i = 0; i < list.getLength(); i++) {
	    Element hl = (Element) list.item(i);
	    Params params = new Params(hl);
	    String type = hl.getAttribute("type");
	    try {
		Class<? extends Highlighter> hlClass = highlighterClasses
			.get(type);
		if (hlClass != null) {
		    Highlighter hlinstance = hlClass.newInstance();
		    hlinstance.init(params);
		    main.add(hlinstance);
		} else {
		    System.err.println(String.format("Unknown highlighter: %s",
			    type));
		}
	    } catch (HighlighterConfigurationException e) {
		System.err.println(String.format(
			"Invalid configuration for highlighter %s: %s", type, e
				.getMessage()));
	    } catch (InstantiationException e) {
		System.err.println(String.format(
			"Error constructing highlighter %s: %s", type, e
				.getMessage()));
	    } catch (IllegalAccessException e) {
		System.err.println(String.format(
			"IError constructing highlighter %s: %s", type, e
				.getMessage()));
	    }
	}
    }

    protected Config() {
	this(null);
    }

    protected Config(String configFilename) {
	verbose = Boolean.getBoolean(VERBOSE_LOADING_PROPERTY);
	if (verbose) {
	    System.out.println("Initializing xslthl " + Version.getVersion());
	}

	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = dbf.newDocumentBuilder();
	    if (configFilename == null || "".equals(configFilename)) {
		if (verbose) {
		    System.out
			    .println("No config file specified, falling back to default behavior");
		}
		if (System.getProperty(CONFIG_PROPERTY) != null) {
		    configFilename = System.getProperty(CONFIG_PROPERTY);
		} else {
		    configFilename = "xslthl-config.xml";
		}
	    }
	    System.out.println("Loading Xslthl configuration from "
		    + configFilename + "...");
	    Document doc = builder.parse(configFilename);
	    NodeList hls = doc.getDocumentElement().getElementsByTagName(
		    "highlighter");
	    Map<String, MainHighlighter> fileMapping = new HashMap<String, MainHighlighter>();
	    for (int i = 0; i < hls.getLength(); i++) {
		Element hl = (Element) hls.item(i);
		String id = hl.getAttribute("id");

		if (highlighters.containsKey(id)) {
		    System.out
			    .println(String
				    .format(
					    "Warning: highlighter with id '%s' already exists!",
					    id));
		}

		String filename = hl.getAttribute("file");
		String absFilename = new URL(new URL(configFilename), filename)
			.toString();
		if (fileMapping.containsKey(absFilename)) {
		    // no need to load the same file twice.
		    if (verbose) {
			System.out.println("Reusing loaded highlighter for "
				+ id + " from " + absFilename + "...");
		    }
		    highlighters.put(id, fileMapping.get(absFilename));
		    continue;
		}
		if (verbose) {
		    System.out.print("Loading " + id + " highligter from "
			    + absFilename + "...");
		}
		try {
		    MainHighlighter mhl = loadHl(absFilename);
		    highlighters.put(id, mhl);
		    if (verbose) {
			System.out.println(" OK");
			fileMapping.put(absFilename, mhl);
		    }
		} catch (Exception e) {
		    if (verbose) {
			System.out.println(" error: " + e.getMessage());
		    } else {
			System.err.println("Error loading highlighter from "
				+ absFilename + ": " + e.getMessage());
		    }
		}
	    }
	    NodeList prefixNode = doc.getDocumentElement()
		    .getElementsByTagName("namespace");
	    if (prefixNode.getLength() == 1) {
		Element e = (Element) prefixNode.item(0);
		prefix = e.getAttribute("prefix");
		uri = e.getAttribute("uri");
	    }
	} catch (Exception e) {
	    System.err
		    .println("XSLT Highlighter: Cannot read xslthl-config.xml, no custom highlighters will be available.");
	}

	if (!highlighters.containsKey("xml")) {
	    // add the built-in XML highlighting if it wasn't overloaded
	    MainHighlighter xml = new MainHighlighter();
	    xml.add(new XMLHighlighter());
	    highlighters.put("xml", xml);
	}
    }
}
