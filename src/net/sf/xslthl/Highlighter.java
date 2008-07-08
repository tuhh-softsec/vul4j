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

abstract class Highlighter {

    boolean startsWith(CharIter in) {
	return false;
    }

    abstract boolean highlight(CharIter in, List<Block> out);

    final static class IgnoreCaseComparator implements Comparator<String> {
	public int compare(String s1, String s2) {
	    return s1.compareToIgnoreCase(s2);
	}
    }

    final static boolean isNewline(Character c) {
	if (c == null) {
	    return false;
	}
	if (c == '\n' || c == '\r') {
	    return true;
	}
	return false;
    }

    final static Character APOSTROPHE = '\'';
    final static Character EQUALS = '=';
    final static Character EXCLAMATION_MARK = '!';
    final static Character GREATER_THAN = '>';
    final static Character HYPHEN = '-';
    final static Character LESS_THAN = '<';
    final static Character QUESTION_MARK = '?';
    final static Character QUOTE = '"';
    final static Character SLASH = '/';
}
