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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    private static final Map<String, Config> instances = new HashMap<String, Config>();

    /**
     * Prefix to use on created XML elements
     */
    protected String prefix = "";

    /**
     * The namespace uri
     */
    protected String uri = "";

    protected Map<String, MainHighlighter> highlighters = new HashMap<String, MainHighlighter>();

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
	NodeList hls = doc.getDocumentElement().getElementsByTagName(
		"highlighter");
	for (int i = 0; i < hls.getLength(); i++) {
	    Element hl = (Element) hls.item(i);
	    Params params = new Params(hl);
	    String type = hl.getAttribute("type");
	    try {
		if (type.equals("multiline-comment")) {
		    main.add(new MultilineCommentHighlighter(params));
		} else if (type.equals("nested-multiline-comment")) {
		    main.add(new NestedMultilineCommentHighlighter(params));
		} else if (type.equals("oneline-comment")) {
		    main.add(new OnelineCommentHighlighter(params));
		} else if (type.equals("string")) {
		    main.add(new StringHighlighter(params));
		} else if (type.equals("heredoc")) {
		    main.add(new HeredocHighlighter(params));
		} else if (type.equals("keywords")) {
		    main.add(new KeywordsHighlighter(params));
		} else {
		    System.err.println(String.format("Unknown highlighter: %s",
			    type));
		}
	    } catch (HighlighterConfigurationException hce) {
		System.err.println(String.format(
			"Invalid configuration for highlighter %s: %s", type,
			hce.getMessage()));
	    }
	}
	hls = doc.getDocumentElement().getElementsByTagName("wholehighlighter");
	for (int i = 0; i < hls.getLength(); i++) {
	    Element hl = (Element) hls.item(i);
	    Params params = new Params(hl);
	    String type = hl.getAttribute("type");
	    try {
		if (type.equals("regex")) {
		    main.add(new RegexHighlighter(params));
		} else if (type.equals("xml")) {
		    main.add(new XMLHighlighter(params));
		} else {
		    System.err.println(String.format(
			    "Unknown wholehighlighter: %s", type));
		}
	    } catch (HighlighterConfigurationException hce) {
		System.err.println(String.format(
			"Invalid configuration for highlighter %s: %s", type,
			hce.getMessage()));
	    }
	}
	return main;
    }

    protected Config() {
	this(null);
    }

    protected Config(String configFilename) {
	boolean verbose = Boolean.getBoolean(VERBOSE_LOADING_PROPERTY);

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
	    for (int i = 0; i < hls.getLength(); i++) {
		Element hl = (Element) hls.item(i);
		String id = hl.getAttribute("id");
		String filename = hl.getAttribute("file");
		String absFilename = new URL(new URL(configFilename), filename)
			.toString();
		if (verbose) {
		    System.out.print("Loading " + id + " highligter from "
			    + absFilename + "...");
		}
		try {
		    MainHighlighter old = highlighters.put(id,
			    loadHl(absFilename));
		    if (verbose) {
			if (old != null) {
			    System.out
				    .println(" Warning: highlighter with such id already existed!");
			} else {
			    System.out.println(" OK");
			}
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
		    .println("XSLT Highlighter: Cannot read xslthl-config.xml, no custom highlighter will be available.\n");
	}

	if (!highlighters.containsKey("xml")) {
	    // add the built-in XML highlighting if it wasn't overloaded
	    MainHighlighter xml = new MainHighlighter();
	    try {
		xml.add(new XMLHighlighter(null));
		highlighters.put("xml", xml);
	    } catch (HighlighterConfigurationException e) {
	    }
	}
    }
}
