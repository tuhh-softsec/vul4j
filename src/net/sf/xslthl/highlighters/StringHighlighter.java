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

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.Highlighter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;

/**
 * Recognizes strings. Accepted parameters:
 * <dl>
 * <dt>string</dt>
 * <dd>How the string starts. <b>Required.</b></dd>
 * <dt>endString</dt>
 * <dd>How the string ends. If not present the start value is used.</dd>
 * <dt>escape</dt>
 * <dd>Character to use to escape characters. Optional.</dd>
 * <dt>doubleEscapes</dt>
 * <dd>When present the double usage of start is considered to be an escaped
 * start (used in Pascal). Optional.</dd>
 * <dt>spanNewLines</dt>
 * <dd>When present strings can span newlines, otherwise a newline breaks the
 * string parsing.</dd>
 * </dl>
 */
public class StringHighlighter extends Highlighter {

	/**
	 * The start token and the escape token.
	 */
	private String start, end, escape;
	/**
	 * If set the double occurance of start escapes it.
	 */
	private boolean doubleEscapes;
	/**
	 * If set newlines are ignored in string parsing.
	 */
	private boolean spansNewLines;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.xslthl.Highlighter#init(net.sf.xslthl.Params)
	 */
	@Override
	public void init(Params params) throws HighlighterConfigurationException {
		super.init(params);
		start = params.getParam("string");
		end = params.getParam("endString", start);
		escape = params.getParam("escape");
		doubleEscapes = params.isSet("doubleEscapes");
		spansNewLines = params.isSet("spanNewLines");
		if (start == null || start.length() == 0) {
			throw new HighlighterConfigurationException(
			        "Required parameter 'start' is not set.");
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
		boolean wasEscape = false;
		while (!in.finished()) {
			if (!spansNewLines && isNewLine(in.current())) {
				break;
			}
			if (in.startsWith(end) && !wasEscape) {
				if (doubleEscapes && in.startsWith(end, end.length())) {
					in.moveNext();
				} else {
					in.moveNext(end.length() - 1);
					break;
				}
			} else if (escape != null && in.startsWith(escape) && !wasEscape) {
				wasEscape = true;
			} else {
				wasEscape = false;
			}
			in.moveNext();
		}
		if (!in.finished()) {
			in.moveNext();
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
		return "string";
	}

}
