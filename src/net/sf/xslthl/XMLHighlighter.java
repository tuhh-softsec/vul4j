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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * XML/SGML highlighter.
 * 
 */
public class XMLHighlighter extends WholeHighlighter {

    abstract static class ElementSet {
	String style;

	abstract boolean matches(String tagName);
    }

    final static class RealElementSet extends ElementSet {
	private Collection<String> tagNames;

	RealElementSet(Params params) {
	    boolean ignoreCase = params.isSet("ignoreCase");
	    if (ignoreCase) {
		tagNames = new TreeSet<String>(new IgnoreCaseComparator());
	    } else {
		tagNames = new TreeSet<String>();
	    }
	    params.getMutliParams("element", tagNames);
	    style = params.getParam("style");
	}

	@Override
	boolean matches(String tagName) {
	    return tagNames.contains(tagName);
	}
    }

    final static class ElementPrefix extends ElementSet {
	private String prefix;

	ElementPrefix(Params params) {
	    style = params.getParam("style");
	    prefix = params.getParam("prefix");
	}

	@Override
	boolean matches(String tagName) {
	    return tagName.startsWith(prefix);
	}
    }

    private Collection<ElementSet> elementSets = new HashSet<ElementSet>();
    final static Character APOSTROPHE = '\'';
    final static Character EQUALS = '=';
    final static Character EXCLAMATION_MARK = '!';
    final static Character GREATER_THAN = '>';
    final static Character HYPHEN = '-';
    final static Character LESS_THAN = '<';
    final static Character QUESTION_MARK = '?';
    final static Character QUOTE = '"';
    final static Character SLASH = '/';

    /**
     * @param tagName
     * @return
     */
    protected String getStyleForTagName(String tagName) {
	for (ElementSet es : elementSets) {
	    if (es.matches(tagName)) {
		return es.style;
	    }
	}
	return null;
    }

    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	if (params != null) {
	    params.getMultiParams("elementSet", elementSets,
		    new Params.ParamsLoader<RealElementSet>() {
			public RealElementSet load(Params params) {
			    return new RealElementSet(params);
			}
		    });
	    params.getMultiParams("elementPrefix", elementSets,
		    new Params.ParamsLoader<ElementPrefix>() {
			public ElementPrefix load(Params params) {
			    return new ElementPrefix(params);
			}
		    });
	}
    }

    /**
     * @param in
     * @param out
     */
    void readTagContent(CharIter in, List<Block> out) {
	while (!in.finished()
		&& !XMLHighlighter.GREATER_THAN.equals(in.current())
		&& !XMLHighlighter.SLASH.equals(in.current())) {
	    if (!Character.isWhitespace(in.current())) {
		if (in.isMarked()) {
		    out.add(in.markedToBlock());
		}
		while (!in.finished()
			&& !XMLHighlighter.EQUALS.equals(in.current())
			&& !Character.isWhitespace(in.current())) {
		    in.moveNext();
		}
		out.add(in.markedToStyledBlock("attribute"));
		while (!in.finished() && Character.isWhitespace(in.current())) {
		    in.moveNext();
		}
		if (in.finished()
			|| !XMLHighlighter.EQUALS.equals(in.current())) { // HTML
		    // no-value
		    // attributes
		    continue;
		}
		in.moveNext(); // skip =
		while (!in.finished() && Character.isWhitespace(in.current())) {
		    in.moveNext();
		}
		out.add(in.markedToBlock());
		if (XMLHighlighter.QUOTE.equals(in.current())
			|| XMLHighlighter.APOSTROPHE.equals(in.current())) {
		    Character boundary = in.current();
		    in.moveNext();
		    while (!in.finished() && !boundary.equals(in.current())) {
			in.moveNext();
		    }
		    if (!in.finished()) {
			in.moveNext();
		    }
		    out.add(in.markedToStyledBlock("value"));
		} else {
		    while (!in.finished()
			    && !XMLHighlighter.GREATER_THAN
				    .equals(in.current())
			    && !XMLHighlighter.SLASH.equals(in.current())
			    && !Character.isWhitespace(in.current())) {
			in.moveNext();
		    }
		    out.add(in.markedToStyledBlock("value"));
		}
	    } else {
		in.moveNext();
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#highlight(net.sf.xslthl.CharIter,
     * java.util.List)
     */
    @Override
    public boolean highlight(CharIter in, List<Block> out) {
	while (!in.finished()) {
	    if (XMLHighlighter.LESS_THAN.equals(in.current())) {
		in.moveNext(); // skip <
		out.add(in.markedToBlock());
		if (XMLHighlighter.SLASH.equals(in.current())) { // it's end tag
		    while (!in.finished()
			    && !XMLHighlighter.GREATER_THAN
				    .equals(in.current())) {
			in.moveNext();
		    }
		    String style = getStyleForTagName(in.getMarked().trim()
			    .substring(1));
		    // </dfsdf > trims to </dfsdf> and than to <dfsdf>
		    if (style != null) {
			out.add(in.markedToStyledBlock(style));
		    } else {
			out.add(in.markedToStyledBlock("tag"));
		    }
		} else if (XMLHighlighter.QUESTION_MARK.equals(in.current())) { // it
		    // 's
		    // processing
		    // instruction
		    while (!in.finished()
			    && !(XMLHighlighter.GREATER_THAN.equals(in
				    .current()) && XMLHighlighter.QUESTION_MARK
				    .equals(in.prev()))) {
			in.moveNext();
		    }
		    out.add(in.markedToStyledBlock("tag"));
		} else if (XMLHighlighter.EXCLAMATION_MARK.equals(in.current())
			&& XMLHighlighter.HYPHEN.equals(in.next())
			&& XMLHighlighter.HYPHEN.equals(in.next(2))) {
		    // it's comment
		    while (!in.finished()
			    && !(XMLHighlighter.GREATER_THAN.equals(in
				    .current())
				    && XMLHighlighter.HYPHEN.equals(in.prev()) && XMLHighlighter.HYPHEN
				    .equals(in.prev(2)))) {
			in.moveNext();
		    }
		    out.add(in.markedToStyledBlock("comment"));
		} else {
		    while (!in.finished()
			    && !XMLHighlighter.GREATER_THAN
				    .equals(in.current())
			    && !XMLHighlighter.SLASH.equals(in.current())
			    && !Character.isWhitespace(in.current())) {
			in.moveNext();
		    }
		    String style = getStyleForTagName(in.getMarked());
		    if (style != null) {
			out.add(in.markedToStyledBlock(style));
		    } else {
			out.add(in.markedToStyledBlock("tag"));
		    }
		    if (!in.finished() && Character.isWhitespace(in.current())) {
			readTagContent(in, out);
		    }
		}
		if (!in.finished()) {
		    in.moveNext();
		}
	    } else {
		in.moveNext();
	    }
	}
	if (in.isMarked()) {
	    out.add(in.markedToBlock());
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#getDefaultStyle()
     */
    @Override
    public String getDefaultStyle() {
	return "xml";
    }
}
