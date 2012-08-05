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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.Highlighter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;

/**
 * Accepts heredoc constructions. Accepted parameters:
 * <dl>
 * <dt>start</dt>
 * <dd>How the heredoc construction starts. <b>Required.</b></dd>
 * <dt>quote</dt>
 * <dd>Allowed quote characters to be used in the identifier name. This
 * parameter can be used more than once.</dd>
 * <dt>noWhiteSpace</dt>
 * <dd>whitespace after start is not allowed.</dd>
 * <dt>looseTerminator</dt>
 * <dd>if set the identifier does not have to start on a new line</dd>
 * </dl>
 * See http://en.wikipedia.org/wiki/Heredoc
 */
public class HeredocHighlighter extends Highlighter {

	/**
	 * The token that initiates a heredoc construction
	 */
	protected String start;

	/**
	 * quote characters that can be used in the heredoc identifier
	 */
	protected Set<String> quoteChar;

	protected boolean noWhiteSpace;

	protected boolean looseTerminator;

	@Override
	public void init(Params params) throws HighlighterConfigurationException {
		super.init(params);
		start = params.getParam("start");
		if (start == null || start.length() == 0) {
			throw new HighlighterConfigurationException(
			        "Required parameter 'start' is not set.");
		}
		quoteChar = new HashSet<String>();
		params.getMutliParams("quote", quoteChar);
		noWhiteSpace = params.isSet("noWhiteSpace");
		looseTerminator = params.isSet("looseTerminator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
	 */
	@Override
	public boolean startsWith(CharIter in) {
		if (in.startsWith(start)) {
			if (noWhiteSpace) {
				return !Character.isWhitespace(in.next(start.length() + 1));
			}
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
		// skip whitespace
		if (!noWhiteSpace) {
			while (!in.finished() && Character.isWhitespace(in.current())) {
				in.moveNext();
			}
		}
		StringBuilder heredocId = new StringBuilder();
		Character quoted = '\0';
		// identifier might me quoted
		if (quoteChar.contains(in.current().toString())) {
			quoted = in.current();
			in.moveNext();
		}
		while (!in.finished()
		        && (Character.isLetterOrDigit(in.current()) || in.current() == '_')
		        && !quoted.equals(in.current())) {
			heredocId.append(in.current());
			in.moveNext();
		}
		if (quoted.equals(in.current())) {
			in.moveNext();
		}
		if (heredocId.length() == 0) {
			return false;
		}
		int i;
		do {
			i = in.indexOf(heredocId.toString());
			if (i < 0) {
				in.moveToEnd();
			} else {
				in.moveNext(i + heredocId.length());
			}
			if (looseTerminator || isNewLine(in.prev(heredocId.length() + 1))) {
				break;
			}
		} while (i != -1);
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
