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

import java.util.List;

/**
 * Performs highlighting for multi-line comments Accepted parameters:
 * <dl>
 * <dt>start</dt>
 * <dd>How the multiline comment starts. <b>Required.</b></dd>
 * <dt>end</dt>
 * <dd>How the multiline comment ends. <b>Required.</b></dd>
 * </dl>
 */
public class MultilineCommentHighlighter extends Highlighter {

    /**
     * The start and end token
     */
    protected String start, end;

    public MultilineCommentHighlighter(Params params) throws HighlighterConfigurationException {
	super(params);
	start = params.getParam("start");
	end = params.getParam("end");
	if (start == null || start.length() == 0) {
	    throw new HighlighterConfigurationException(
		    "Required parameter 'start' is not set.");
	}
	if (end == null || end.length() == 0) {
	    throw new HighlighterConfigurationException(
		    "Required parameter 'end' is not set.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
     */
    @Override
    public boolean startsWith(CharIter in) {
	if (in.startsWith(start)) {
	    return true;
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#highlight(net.sf.xslthl.CharIter,
     * java.util.List)
     */
    @Override
    public boolean highlight(CharIter in, List<Block> out) {
	in.moveNext(start.length()); // skip start
	int endIndex = in.indexOf(end);
	if (endIndex == -1) {
	    in.moveToEnd();
	} else {
	    in.moveNext(endIndex + end.length());
	}
	out.add(in.markedToStyledBlock(styleName));
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.xslthl.Highlighter#getDefaultStyle()
     */
    @Override
    public String getDefaultStyle() {
	return "comment";
    }

}
