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
import java.util.HashSet;
import java.util.List;

import net.sf.xslthl.Block;
import net.sf.xslthl.CharIter;
import net.sf.xslthl.HighlighterConfigurationException;
import net.sf.xslthl.Params;
import net.sf.xslthl.WholeHighlighter;
import net.sf.xslthl.highlighters.xml.ElementPrefix;
import net.sf.xslthl.highlighters.xml.ElementSet;
import net.sf.xslthl.highlighters.xml.RealElementSet;

/**
 * XML/SGML highlighter. It has a couple of default styles: tag, attribute,
 * value, directive. Accepted parameters:
 * <dl>
 * <dt>elementSet</dt>
 * <dd>Specialized highlighting for set elements</dd>
 * <dt>elementPrefix</dt>
 * <dd>Specialized highlighting for element prefixes</dd>
 * <dt>styleElement</dt>
 * <dd>The style to use for elements, defaults to 'tag'</dd>
 * <dt>styleAttributes</dt>
 * <dd>The style to use for attributes, defaults to 'attribute'</dd>
 * <dt>styleValue</dt>
 * <dd>The style to use for attribute values, defaults to 'value'</dd>
 * <dt>stylePi</dt>
 * <dd>The style to use for processing instructions, defaults to 'directive'</dd>
 * <dt>styleComment</dt>
 * <dd>The style to use for comments, defaults to 'comment'</dd>
 * <dt>styleDoctype</dt>
 * <dd>The style to use for the doctype declaration, defaults to 'doccomment'</dd>
 * </dl>
 */
public class HTMLHighlighter extends WholeHighlighter {

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
	 * Overriden styles
	 */
	protected Collection<ElementSet> elementSets = new HashSet<ElementSet>();

	/**
	 * Style to use for elements
	 */
	protected String styleElement = "tag";

	/**
	 * The style for attributes
	 */
	protected String styleAttribute = "attribute";

	/**
	 * The style for attribute values
	 */
	protected String styleValue = "value";

	/**
	 * The style for processing instructions
	 */
	protected String stylePi = "directive";

	/**
	 * The style for comments
	 */
	protected String styleComment = "comment";

	/**
	 * Style to use for the doctype part
	 */
	protected String styleDoctype = "doctype";

	/**
	 * @param tagName
	 * @return
	 */
	protected String getStyleForTagName(String tagName) {
		for (ElementSet es : elementSets) {
			if (es.matches(tagName)) {
				return es.getStyle();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.xslthl.WholeHighlighter#init(net.sf.xslthl.Params)
	 */
	@Override
	public void init(Params params) throws HighlighterConfigurationException {
		super.init(params);
		if (params != null) {

			styleAttribute = params.getParam("styleAttribute", styleAttribute);
			styleComment = params.getParam("styleComment", styleComment);
			styleDoctype = params.getParam("styleDoctype", styleDoctype);
			styleElement = params.getParam("styleElement", styleElement);
			stylePi = params.getParam("stylePi", stylePi);
			styleValue = params.getParam("styleValue", styleValue);

			params.getMultiParams("elementSet", elementSets,
			        new Params.ParamsLoader<RealElementSet>() {
				        public RealElementSet load(Params params)
				                throws HighlighterConfigurationException {
					        return new RealElementSet(params);
				        }
			        });
			params.getMultiParams("elementPrefix", elementSets,
			        new Params.ParamsLoader<ElementPrefix>() {
				        public ElementPrefix load(Params params)
				                throws HighlighterConfigurationException {
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
		        && !HTMLHighlighter.GREATER_THAN.equals(in.current())
		        && !HTMLHighlighter.SLASH.equals(in.current())) {
			if (!Character.isWhitespace(in.current())) {
				if (in.isMarked()) {
					out.add(in.markedToBlock());
				}
				while (!in.finished()
				        && !HTMLHighlighter.EQUALS.equals(in.current())
				        && !Character.isWhitespace(in.current())) {
					in.moveNext();
				}
				out.add(in.markedToStyledBlock(styleAttribute));
				while (!in.finished() && Character.isWhitespace(in.current())) {
					in.moveNext();
				}
				if (in.finished()
				        || !HTMLHighlighter.EQUALS.equals(in.current())) { // HTML
					// no-value
					// attributes
					continue;
				}
				in.moveNext(); // skip '='
				while (!in.finished() && Character.isWhitespace(in.current())) {
					in.moveNext();
				}
				out.add(in.markedToBlock());
				if (HTMLHighlighter.QUOTE.equals(in.current())
				        || HTMLHighlighter.APOSTROPHE.equals(in.current())) {
					Character boundary = in.current();
					in.moveNext();
					while (!in.finished() && !boundary.equals(in.current())) {
						in.moveNext();
					}
					if (!in.finished()) {
						in.moveNext();
					}
					out.add(in.markedToStyledBlock(styleValue));
				} else {
					while (!in.finished()
					        && !HTMLHighlighter.GREATER_THAN
					                .equals(in.current())
					        //&& !HTMLHighlighter.SLASH.equals(in.current())
					        && !Character.isWhitespace(in.current())) {
						in.moveNext();
					}
					out.add(in.markedToStyledBlock(styleValue));
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
			if (HTMLHighlighter.LESS_THAN.equals(in.current())) {
				out.add(in.markedToBlock());
				in.moveNext(); // skip <
				if (HTMLHighlighter.SLASH.equals(in.current())) {
					// closing tag -> tag
					while (!in.finished()
					        && !HTMLHighlighter.GREATER_THAN
					                .equals(in.current())) {
						in.moveNext();
					}
					String style = getStyleForTagName(in.getMarked().trim()
					        .substring(2));
					// </dfsdf > trims to </dfsdf> and than to <dfsdf>
					in.moveNext(); // get >
					if (style != null) {
						out.add(in.markedToStyledBlock(style));
					} else {
						out.add(in.markedToStyledBlock(styleElement));
					}
				} else if (HTMLHighlighter.QUESTION_MARK.equals(in.current())) {
					// processing instruction -> directive
					while (!in.finished()
					        && !(HTMLHighlighter.GREATER_THAN.equals(in
					                .current()) && HTMLHighlighter.QUESTION_MARK
					                .equals(in.prev()))) {
						in.moveNext();
					}
					in.moveNext();
					out.add(in.markedToStyledBlock(stylePi));
				} else if (HTMLHighlighter.EXCLAMATION_MARK.equals(in.current())
				        && HTMLHighlighter.HYPHEN.equals(in.next())
				        && HTMLHighlighter.HYPHEN.equals(in.next(2))) {
					// comment
					while (!in.finished()
					        && !(HTMLHighlighter.GREATER_THAN.equals(in
					                .current())
					                && HTMLHighlighter.HYPHEN.equals(in.prev()) && HTMLHighlighter.HYPHEN
					                .equals(in.prev(2)))) {
						in.moveNext();
					}
					in.moveNext();
					out.add(in.markedToStyledBlock(styleComment));
				} else if (HTMLHighlighter.EXCLAMATION_MARK.equals(in.current())
				        && in.startsWith("[CDATA[", 1)) {
					// CDATA section
					in.moveNext(8);
					out.add(in.markedToStyledBlock(styleElement));
					int idx = in.indexOf("]]>");
					if (idx == -1) {
						in.moveToEnd();
					} else {
						in.moveNext(idx);
					}
					out.add(in.markedToBlock());
					if (idx != -1) {
						in.moveNext(3);
						out.add(in.markedToStyledBlock(styleElement));
					}
				} else if (HTMLHighlighter.EXCLAMATION_MARK.equals(in.current())
				        && in.startsWith("DOCTYPE", 1)) {
					// doctype... just ignore most of it
					int cnt = 1;
					while (!in.finished() && cnt > 0) {
						if (in.current().equals(GREATER_THAN)) {
							--cnt;
						} else if (in.current().equals(LESS_THAN)) {
							++cnt;
						}
						in.moveNext();
					}
					out.add(in.markedToStyledBlock(styleDoctype));
				} else {
					// normal tag
					while (!in.finished()
					        && !HTMLHighlighter.GREATER_THAN
					                .equals(in.current())
					        && !HTMLHighlighter.SLASH.equals(in.current())
					        && !Character.isWhitespace(in.current())) {
						in.moveNext();
					}
					String style = getStyleForTagName(in.getMarked().trim()
					        .substring(1));

					// find short tag
					boolean shortTag = false;
					int cnt = 0;
					while (!in.finished()
					        && !HTMLHighlighter.GREATER_THAN
					                .equals(in.current())
					        && !HTMLHighlighter.SLASH.equals(in.current())
					        && Character.isWhitespace(in.current())) {
						in.moveNext();
						++cnt;
					}
					if (HTMLHighlighter.SLASH.equals(in.current())) {
						in.moveNext();
						++cnt;
					}
					if (HTMLHighlighter.GREATER_THAN.equals(in.current())) {
						in.moveNext();
						shortTag = true;
					} else {
						in.moveNext(-cnt);
					}

					if (style != null) {
						out.add(in.markedToStyledBlock(style));
					} else {
						out.add(in.markedToStyledBlock(styleElement));
					}
					if (!shortTag && !in.finished()
					        && Character.isWhitespace(in.current())) {
						readTagContent(in, out);

						if (!in.finished()) {
							if (HTMLHighlighter.SLASH.equals(in.current())) {
								in.moveNext();
							}
							in.moveNext();
							if (style != null) {
								out.add(in.markedToStyledBlock(style));
							} else {
								out.add(in.markedToStyledBlock(styleElement));
							}
						}
					}
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
		// not really used, just here to prevent an error to pop up
		return "xml";
	}
}
