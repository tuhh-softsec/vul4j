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

import java.util.Comparator;
import java.util.List;

/**
 * Base highlighter. Accepted parameters:
 * <dl>
 * <dt>Style</dt>
 * <dd>The style to use in the generated block</dd>
 * </dl>
 */
public abstract class Highlighter {

    final static class IgnoreCaseComparator implements Comparator<String> {
	public int compare(String s1, String s2) {
	    return s1.compareToIgnoreCase(s2);
	}
    }

    final static boolean isNewLine(Character c) {
	return '\n' == c || '\r' == c;
    }

    /**
     * The name of the style to use
     */
    protected String styleName;

    public Highlighter(Params params) throws HighlighterConfigurationException {
	if (params == null) {
	    styleName = getDefaultStyle();
	} else {
	    styleName = params.getParam("style", getDefaultStyle());
	}
	if (styleName == null || styleName.length() == 0) {
	    throw new HighlighterConfigurationException(
		    "Required parameter 'style' is not set.");
	}
    }

    /**
     * return true if the current character is a possible match for this
     * highlighter
     * 
     * @param in
     * @return
     */
    public boolean startsWith(CharIter in) {
	return false;
    }

    /**
     * Perform highlighting on the current token stream. Return true when
     * highlighting was performed, or false in case of a false positive.
     * 
     * @param in
     * @param out
     * @return
     */
    public abstract boolean highlight(CharIter in, List<Block> out);

    /**
     * The default style name
     * 
     * @return
     */
    public abstract String getDefaultStyle();
}
