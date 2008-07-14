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

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A special character iterator
 */
public class CharIter implements Iterable<Character>, Iterator<Character> {
    private String s;
    private int i = 0;
    private int l;
    private int mark = 0;
    private Matcher matcher;

    public CharIter(String s) {
	this.s = s;
	l = s.length();
    }

    /**
     * @return the marked position
     */
    public int getMark() {
	return mark;
    }

    /**
     * Set the current position as the mark
     */
    public void setMark() {
	mark = i;
    }

    /**
     * Set the mark to the given location
     * 
     * @param i
     */
    public void setMark(int i) {
	mark = i;
    }

    /**
     * @return true if there is a mark set
     */
    public boolean isMarked() {
	return mark < i;
    }

    /**
     * @return the marked section
     */
    public String getMarked() {
	if (i > l) {
	    i = l;
	}
	return s.substring(mark, i);
    }

    /**
     * @return the marked string as a block
     */
    public Block markedToBlock() {
	Block b = new Block(getMarked());
	// System.err.println("Block: " + b.getText());
	setMark();
	return b;
    }

    /**
     * @param styleName
     * @return the marked string as a styled block
     */
    public Block markedToStyledBlock(String styleName) {
	Block b = new StyledBlock(getMarked(), styleName);
	// System.err.println("StyledBlock: " + b.getText());
	setMark();
	return b;
    }

    /**
     * Increase the pointer
     */
    public void moveNext() {
	i++;
    }

    /**
     * Increase the point with the given offset
     * 
     * @param diff
     */
    public void moveNext(int diff) {
	i += diff;
    }

    /**
     * Increase the pointer and mark the position after it
     */
    public void moveNextAndMark() {
	moveNext();
	setMark();
    }

    /**
     * Move to the end of the string
     */
    public void moveToEnd() {
	i = l;
    }

    /**
     * @return true if the iterator finished
     */
    public boolean finished() {
	return i >= l;
    }

    /**
     * @return the current character
     */
    public Character current() {
	return s.charAt(i);
    }

    /**
     * @return the next character
     */
    public Character next() {
	if (i + 1 < l) {
	    return s.charAt(i + 1);
	}
	return null;
    }

    /**
     * @param diff
     * @return the characters at the given offset
     */
    public Character next(int diff) {
	if (i + diff < l) {
	    return s.charAt(i + diff);
	}
	return null;
    }

    /**
     * @return the previous character
     */
    public Character prev() {
	if (i > 0) {
	    return s.charAt(i - 1);
	}
	return null;
    }

    /**
     * @param diff
     * @return the previous character at a given offset
     */
    public Character prev(int diff) {
	if (i - diff >= 0) {
	    return s.charAt(i - diff);
	}
	return null;
    }

    /**
     * @param prefix
     * @return true if the current position starts with the prefix
     */
    public boolean startsWith(String prefix) {
	return startsWith(prefix, false);
    }

    /**
     * @param prefix
     * @param ignoreCase
     * @return true if the current position starts with the prefix
     */
    public boolean startsWith(String prefix, boolean ignoreCase) {
	return startsWith(prefix, 0, ignoreCase);
    }

    /**
     * @param prefix
     * @param diff
     * @return true if the current position starts with the prefix at a given
     *         offset
     */
    public boolean startsWith(String prefix, int diff) {
	return startsWith(prefix, diff, false);
    }

    /**
     * @param prefix
     * @param diff
     * @param ignoreCase
     * @return true if the current position starts with the prefix at a given
     *         offset
     */
    public boolean startsWith(String prefix, int diff, boolean ignoreCase) {
	return s.startsWith(prefix, i + diff);
    }

    /**
     * Create a pattern matcher
     * 
     * @param pattern
     */
    public void createMatcher(Pattern pattern) {
	matcher = pattern.matcher(s);
    }

    /**
     * @return return true if the created matcher has a match
     */
    public boolean find() {
	boolean found = matcher.find();
	if (found) {
	    i = matcher.start();
	} else {
	    moveToEnd();
	}
	return found;
    }

    /**
     * Mark the matched section
     */
    public void markMatched() {
	setMark(i);
	i = matcher.end();
    }

    /**
     * @return the remaining characters in the buffer
     */
    public int remaining() {
	if (i < l) {
	    return l - i;
	}
	return 0;
    }

    /**
     * @param substr
     * @return the index of the given string
     */
    public int indexOf(String substr) {
	int index = s.indexOf(substr, i);
	return index < 0 ? -1 : index - i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Character> iterator() {
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
	return !finished();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
	throw new UnsupportedOperationException();
    }

}
