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
 * <dt>flags</dt>
 * <dd>Flags, see javadoc (use the constant names AS IS)</dd>
 * </dl>
 */
@Deprecated
public class RegexHighlighter extends WholeHighlighter {

    /**
     * The pattern to accept
     */
    protected Pattern pattern;

    @Override
    public void init(Params params) throws HighlighterConfigurationException {
	super.init(params);
	int flags = 0;
	String[] flagString = params.getParam("flags", "").split("[;,]");
	for (String flag : flagString) {
	    flag = flag.trim();
	    if ("CASE_INSENSITIVE".equalsIgnoreCase(flag)) {
		flags += Pattern.CASE_INSENSITIVE;
	    } else if ("DOTALL".equalsIgnoreCase(flag)) {
		flags += Pattern.DOTALL;
	    } else if ("MULTILINE".equalsIgnoreCase(flag)) {
		flags += Pattern.MULTILINE;
	    } else if ("UNICODE_CASE".equalsIgnoreCase(flag)) {
		flags += Pattern.UNICODE_CASE;
	    } else if ("LITERAL".equalsIgnoreCase(flag)) {
		flags += Pattern.LITERAL;
	    } else if ("UNIX_LINES".equalsIgnoreCase(flag)) {
		flags += Pattern.UNIX_LINES;
	    } else if ("COMMENTS".equalsIgnoreCase(flag)) {
		flags += Pattern.COMMENTS;
	    } else if ("CANON_EQ".equalsIgnoreCase(flag)) {
		flags += Pattern.CANON_EQ;
	    }
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
