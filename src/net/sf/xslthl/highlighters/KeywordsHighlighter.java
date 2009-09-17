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

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.Highlighter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;

/**
 * Scans for registered keywords Accepted parameters:
 * <dl>
 * <dt>keywords</dt>
 * <dd>Keywords this highlighter recognizes. Can be used multiple times</dd>
 * <dt>ignoreCase</dt>
 * <dd>If this element is present the keywords are case insensitive.</dd>
 * </dl>
 */
public class KeywordsHighlighter extends Highlighter {
	/**
	 * the keywords this highligher accepts
	 */
	protected Collection<String> keywords;

	/**
	 * Ignore case of the keywords.
	 */
	protected boolean ignoreCase = false;

	/**
	 * Optional extra character sequence that defines the start of an identifier
	 */
	protected String startChars;

	/**
	 * Optional extra character sequence that defines the part of an identifier
	 */
	protected String partChars;

	@Override
	public void init(Params params) throws HighlighterConfigurationException {
		super.init(params);
		ignoreCase = params.isSet("ignoreCase");
		if (ignoreCase) {
			keywords = new TreeSet<String>(new IgnoreCaseComparator());
		} else {
			keywords = new TreeSet<String>();
		}
		params.getMutliParams("keyword", keywords);

		startChars = params.getParam("beginChars");
		partChars = params.getParam("partChars");
	}

	protected void appendUnique(StringBuilder buf, char ch) {
		for (int i = 0; i < buf.length(); ++i) {
			if (buf.charAt(i) == ch) {
				return;
			}
		}
		buf.append(ch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.xslthl.Highlighter#startsWith(net.sf.xslthl.CharIter)
	 */
	@Override
	public boolean startsWith(CharIter in) {
		if (isIdentifierStart(in.current())
		        && (in.prev() == null || !isIdentifierPart(in.prev()))) {
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
		while (!in.finished() && isIdentifierPart(in.current())) {
			in.moveNext();
		}
		if (keywords.contains(in.getMarked())) {
			out.add(in.markedToStyledBlock(styleName));
			return true;
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
		return "keyword";
	}

	/**
	 * @param ch
	 * @return True if this character is a valid start of an identifier
	 */
	protected boolean isIdentifierStart(char ch) {
		if (startChars != null) {
			if (startChars.indexOf(ch) != -1) {
				return true;
			}
		}
		return Character.isJavaIdentifierStart(ch);
	}

	/**
	 * @param ch
	 * @return True if this character is a valid part of an identifier
	 */
	protected boolean isIdentifierPart(char ch) {
		if (partChars != null) {
			if (partChars.indexOf(ch) != -1) {
				return true;
			}
		}
		return Character.isJavaIdentifierPart(ch);
	}
}
