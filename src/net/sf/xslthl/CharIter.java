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
	private String buffer;
	private int position = 0;
	private int length;
	private int mark = 0;
	private Matcher matcher;

	public CharIter(String input) {
		buffer = input;
		length = input.length();
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
		mark = position;
	}

	/**
	 * Set the mark to the given location
	 * 
	 * @param newMark
	 */
	public void setMark(int newMark) {
		mark = newMark;
	}

	/**
	 * @return true if there is a mark set
	 */
	public boolean isMarked() {
		return mark < position;
	}

	/**
	 * @return the marked section
	 */
	public String getMarked() {
		if (position > length) {
			position = length;
		}
		return buffer.substring(mark, position);
	}

	/**
	 * @return the marked string as a block
	 */
	public Block markedToBlock() {
		Block b = new Block(getMarked());
		setMark();
		return b;
	}

	/**
	 * Create a style block, unless the stylename is equale to
	 * {@value StyledBlock#NO_STYLE}
	 * 
	 * @param styleName
	 * @return the marked string as a styled block
	 */
	public Block markedToStyledBlock(String styleName) {
		Block b = new StyledBlock(getMarked(), styleName);
		setMark();
		return b;
	}

	/**
	 * Increase the pointer
	 */
	public void moveNext() {
		position++;
	}

	/**
	 * Increase the point with the given offset
	 * 
	 * @param offset
	 */
	public void moveNext(int offset) {
		position += offset;
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
		position = length;
	}

	/**
	 * @return true if the iterator finished
	 */
	public boolean finished() {
		return position >= length;
	}

	/**
	 * @return the current character
	 */
	public Character current() {
		return buffer.charAt(position);
	}

	/**
	 * @return the next character
	 */
	public Character next() {
		if (position + 1 < length) {
			return buffer.charAt(position + 1);
		}
		return null;
	}

	/**
	 * @param offset
	 * @return the characters at the given offset
	 */
	public Character next(int offset) {
		if (position + offset < length) {
			return buffer.charAt(position + offset);
		}
		return null;
	}

	/**
	 * @return the previous character
	 */
	public Character prev() {
		if (position > 0) {
			return buffer.charAt(position - 1);
		}
		return null;
	}

	/**
	 * @param offset
	 * @return the previous character at a given offset
	 */
	public Character prev(int offset) {
		if (position - offset >= 0) {
			return buffer.charAt(position - offset);
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
		if (ignoreCase) {
			int end = position + diff + prefix.length();
			if (end >= length) {
				end = length - 1;
			}
			if (position + diff >= end) {
			        return false;
			}
			return buffer.substring(position + diff, end).equalsIgnoreCase(
			        prefix);
		}
		return buffer.startsWith(prefix, position + diff);
	}

	/**
	 * Create a pattern matcher
	 * 
	 * @param pattern
	 */
	public Matcher createMatcher(Pattern pattern) {
		matcher = pattern.matcher(buffer);
		return matcher;
	}

	/**
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the remaining characters in the buffer
	 */
	public int remaining() {
		if (position < length) {
			return length - position;
		}
		return 0;
	}

	/**
	 * @param substr
	 * @return the index of the given string
	 */
	public int indexOf(String substr) {
		int index = buffer.indexOf(substr, position);
		return index < 0 ? -1 : index - position;
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
