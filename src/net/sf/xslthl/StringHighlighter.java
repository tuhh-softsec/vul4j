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

class StringHighlighter extends Highlighter {

    private String start, escape;
    private boolean doubleEscapes;

    StringHighlighter(Params params) {
	start = params.getParam("string");
	escape = params.getParam("escape", null);
	doubleEscapes = params.isSet("doubleEscapes");
    }

    @Override
    boolean startsWith(CharIter in) {
	if (in.startsWith(start)) {
	    return true;
	}
	return false;
    }

    @Override
    boolean highlight(CharIter in, List<Block> out) {
	in.moveNext(start.length()); // skip start
	boolean wasEscape = false;
	while (!in.finished()) {
	    if (in.startsWith(start) && !wasEscape) {
		if (doubleEscapes && in.startsWith(start, start.length())) {
		    in.moveNext();
		} else {
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
	out.add(in.markedToStyledBlock("string"));
	return true;
    }

}
