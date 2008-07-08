package net.sf.xslthl;

import java.util.*;

abstract class WholeHighlighter extends Highlighter {
	private Collection<String> styles = new HashSet<String>();
	private boolean emptyStyle = true;
	private boolean allStyles = false;
	
	void loadStyles(Params params) {
		if (!params.isSet("empty")) {
			emptyStyle = false;
		}
		if (params.isSet("all")) {
			allStyles = true;
			return;
		}
		params.load("style", styles);
	}
	
	boolean appliesOnEmptyStyle() {
		return emptyStyle;
	}
	
	boolean appliesOnAllStyles() {
		return allStyles;
	}
	
	boolean appliesOnStyle(String style) {
		return styles.contains(style);
	}
	
	WholeHighlighter(Params params) {
		if (params != null && params.isSet("applyOnStyles")) {
			loadStyles(params.getParams("applyOnStyles"));
		}
	}
	
}
