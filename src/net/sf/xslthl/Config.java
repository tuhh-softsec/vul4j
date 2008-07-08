package net.sf.xslthl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class Config {
    /**
     * System property that defines the path to the configuration file
     */
    public static final String CONFIG_PROPERTY = "xslthl.config";
    /**
     * If set to true be verbose during loading of the configuration
     */
    public static final String VERBOSE_LOADING_PROPERTY = "xslthl.config.verbose";

    public String prefix = "";
    public String uri = "";

    private static Config instance = null;
    private Map<String, MainHighlighter> highlighters = new HashMap<String, MainHighlighter>();

    static Config getInstance() {
	if (instance == null) {
	    instance = new Config();
	}
	return instance;
    }

    private MainHighlighter loadHl(String filename) throws Exception {
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
		System.err.println("Unknown highlighter");
	    }
	}
	hls = doc.getDocumentElement().getElementsByTagName("wholehighlighter");
	for (int i = 0; i < hls.getLength(); i++) {
	    Element hl = (Element) hls.item(i);
	    Params params = new Params(hl);
	    String type = hl.getAttribute("type");
	    if (type.equals("regex")) {
		main.addWhole(new RegexHighlighter(params));
	    } else if (type.equals("xml")) {
		main.addWhole(new XMLHighlighter(params));
	    } else {
		System.err.println("Unknown wholehighlighter");
	    }
	}
	return main;
    }

    MainHighlighter getMainHighlighter(String id) {
	return highlighters.get(id);
    }

    private Config() {
	boolean verbose = Boolean.getBoolean(VERBOSE_LOADING_PROPERTY);
	MainHighlighter xml = new MainHighlighter();
	xml.addWhole(new XMLHighlighter(null));
	highlighters.put("xml", xml);
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = dbf.newDocumentBuilder();
	    String configFilename = "xslthl-config.xml";
	    if (System.getProperty(CONFIG_PROPERTY) != null) {
		configFilename = System.getProperty(CONFIG_PROPERTY);
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
    }
}
