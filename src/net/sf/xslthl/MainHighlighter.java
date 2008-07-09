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

import java.util.LinkedList;
import java.util.List;

/**
 * Main source highlighter. It will call all registered highlighters.
 */
public class MainHighlighter {

    /**
     * Normale highlighter 
     */
    protected List<Highlighter> highlighters = new LinkedList<Highlighter>();
    
    /**
     * 
     */
    protected List<WholeHighlighter> wholehighlighters = new LinkedList<WholeHighlighter>();

    public void add(Highlighter h) {
	if (h instanceof WholeHighlighter) {
	    wholehighlighters.add((WholeHighlighter) h);
	} else {
	    highlighters.add(h);
	}
    }

    @Deprecated
    public void addWhole(WholeHighlighter h) {
	add(h);
    }

    /**
     * Convert the input string into a collection of text blocks
     * 
     * @param source
     * @return
     */
    public List<Block> highlight(String source) {
	CharIter in = new CharIter(source);
	List<Block> out = new LinkedList<Block>();

	if (highlighters.size() > 0) {
	    while (!in.finished()) {
		boolean found = false;
		for (Highlighter h : highlighters) {
		    if (h.startsWith(in)) {
			int oldMark = -2;
			Block preBlock = null;
			if (in.isMarked()) {
			    oldMark = in.getMark();
			    preBlock = in.markedToBlock();
			    out.add(preBlock);
			}
			found = h.highlight(in, out);
			if (found) {
			    break;
			} else {
			    // undo last action when it was a false positive
			    if (preBlock != null) {
				out.remove(preBlock);
			    }
			    if (oldMark != -2) {
				in.setMark(oldMark);
			    }
			}
		    }
		}
		if (!found) {
		    in.moveNext();
		}
	    }
	} else {
	    in.moveToEnd();
	}

	if (in.isMarked()) {
	    out.add(in.markedToBlock());
	}

	if (wholehighlighters.size() > 0) {
	    for (WholeHighlighter h : wholehighlighters) {
		List<Block> oldout = out;
		out = new LinkedList<Block>();
		for (Block b : oldout) {
		    if (b.isStyled()
			    && (h.appliesOnAllStyles() || h
				    .appliesOnStyle(((StyledBlock) b)
					    .getStyle())) || !b.isStyled()
			    && h.appliesOnEmptyStyle()) {
			h.highlight(new CharIter(b.getText()), out);
		    } else {
			out.add(b);
		    }
		}
	    }
	}

	return out;
    }

}
