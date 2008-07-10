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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharIter {
    private String s;
    private int i = 0;
    private int l;
    private int mark = 0;
    private Matcher matcher;

    public CharIter(String s) {
	this.s = s;
	l = s.length();
    }

    public int getMark() {
	return mark;
    }

    public void setMark() {
	mark = i;
    }

    public void setMark(int i) {
	mark = i;
    }

    public boolean isMarked() {
	return mark < i;
    }

    public String getMarked() {
	if (i > l) {
	    i = l;
	}
	return s.substring(mark, i);
    }

    public Block markedToBlock() {
	Block b = new Block(getMarked());
	// System.err.println("Block: " + b.getText());
	setMark();
	return b;
    }

    public Block markedToStyledBlock(String styleName) {
	Block b = new StyledBlock(getMarked(), styleName);
	// System.err.println("StyledBlock: " + b.getText());
	setMark();
	return b;
    }

    public void moveNext() {
	i++;
    }

    public void moveNext(int diff) {
	i += diff;
    }

    public void moveNextAndMark() {
	moveNext();
	setMark();
    }

    public void moveToEnd() {
	i = l;
    }

    public boolean finished() {
	return i >= l;
    }

    public Character current() {
	return s.charAt(i);
    }

    public Character next() {
	if (i + 1 < l) {
	    return s.charAt(i + 1);
	}
	return null;
    }

    public Character next(int diff) {
	if (i + diff < l) {
	    return s.charAt(i + diff);
	}
	return null;
    }

    public Character prev() {
	if (i > 0) {
	    return s.charAt(i - 1);
	}
	return null;
    }

    public Character prev(int diff) {
	if (i - diff >= 0) {
	    return s.charAt(i - diff);
	}
	return null;
    }

    public boolean startsWith(String prefix) {
	return s.startsWith(prefix, i);
    }

    public boolean startsWith(String prefix, int diff) {
	return s.startsWith(prefix, i + diff);
    }

    public void createMatcher(Pattern pattern) {
	matcher = pattern.matcher(s);
    }

    public boolean find() {
	boolean found = matcher.find();
	if (found) {
	    i = matcher.start();
	} else {
	    moveToEnd();
	}
	return found;
    }

    public void markMatched() {
	setMark(i);
	i = matcher.end();
    }

    public int remaining() {
	if (i < l) {
	    return l - i;
	}
	return 0;
    }

    public int indexOf(String substr) {
	int index = s.indexOf(substr, i);
	return index < 0 ? -1 : index - i;
    }

}
