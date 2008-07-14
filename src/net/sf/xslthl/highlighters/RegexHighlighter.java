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
package net.sf.xslthl.highlighters;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;
import net.sf.xslthl.WholeHighlighter;

/**
 * A regular expression based highlighter. Accepted parameters:
 * <dl>
 * <dt>pattern</dt>
 * <dd>The regular expression pattern to be matched.</dd>
 * <dt>caseInsensitive, dotAll, multiLine, unicodeCase, literal, unixLines,
 * comments, canonEQ</dt>
 * <dd>Flags, see javadoc</dd>
 * </dl>
 */
public class RegexHighlighter extends WholeHighlighter {

    /**
     * The pattern to accept
     */
    protected Pattern pattern;

    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	int flags = 0;
	if (params.isSet("caseInsensitive")) {
	    flags += Pattern.CASE_INSENSITIVE;
	}
	if (params.isSet("dotAll")) {
	    flags += Pattern.DOTALL;
	}
	if (params.isSet("multiLine")) {
	    flags += Pattern.MULTILINE;
	}
	if (params.isSet("unicodeCase")) {
	    flags += Pattern.UNICODE_CASE;
	}
	if (params.isSet("literal")) {
	    flags += Pattern.LITERAL;
	}
	if (params.isSet("unixLines")) {
	    flags += Pattern.UNIX_LINES;
	}
	if (params.isSet("comments")) {
	    flags += Pattern.COMMENTS;
	}
	if (params.isSet("canonEQ")) {
	    flags += Pattern.CANON_EQ;
	}
	String pat = params.getParam("pattern");
	if (params.isSet("pattern")) {
	    try {
		pattern = Pattern.compile(pat, flags);
	    } catch (PatternSyntaxException e) {
		throw new HighlighterConfigurationException(e.getMessage(), e);
	    }
	}
	if (pattern == null) {
	    throw new HighlighterConfigurationException(
		    "Required parameter 'pattern' is not set.");
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
	in.createMatcher(pattern);
	while (in.find()) {
	    if (in.isMarked()) {
		out.add(in.markedToBlock());
	    }
	    in.markMatched();
	    out.add(in.markedToStyledBlock(styleName));
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
	return null;
    }
}
