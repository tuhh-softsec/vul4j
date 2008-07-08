package net.sf.xslthl;

import java.util.regex.*;

class CharIter {
	private String s;	
	private int i = 0;
	private int l;
	private int mark = 0;
	private Matcher matcher;
	
	CharIter(String s) {
		this.s = s;
		l = s.length();
	}

	int getMark() {
		return mark;
	}	
	
	void setMark() {
		mark = i;
	}

	void setMark(int i) {
		mark = i;
	}
	
	boolean isMarked() {
		return mark < i;
	}
	
	String getMarked() {
		if (i > l) {
			i = l;
		}
		return s.substring(mark, i);
	}
	
	Block markedToBlock() {
		//System.err.println("Block: " + s.substring(mark, i));
		Block b = new Block(getMarked());
		setMark();
		return b;
	}
	
	Block markedToStyledBlock(String styleName) {
		//System.err.println("StyledBlock: " + s.substring(mark, i));
		Block b = new StyledBlock(getMarked(), styleName);
		setMark();
		return b;
	}
	
	void moveNext() {
		i++;
	}

	void moveNext(int diff) {
		i += diff;
	}
	
	void moveNextAndMark() {
		moveNext();
		setMark();
	}
	
	void moveToEnd() {
		i = l;
	}
	
	boolean finished() {
		return i >= l;
	}
	
	Character current() {
		return s.charAt(i);
	}
	
	Character next() {
		if (i + 1 < l) {
			return s.charAt(i + 1);
		}
		return null;
	}

	Character next(int diff) {
		if (i + diff < l) {
			return s.charAt(i + diff);
		}
		return null;
	}
	
	Character prev() {
		if (i > 0) {
			return s.charAt(i - 1);
		}
		return null;
	}

	Character prev(int diff) {
		if (i - diff >= 0) {
			return s.charAt(i - diff);
		}
		return null;
	}

	boolean startsWith(String prefix) {
		return s.startsWith(prefix, i);
	}

	boolean startsWith(String prefix, int diff) {
		return s.startsWith(prefix, i + diff);
	}
	
	void createMatcher(Pattern pattern) {
		matcher = pattern.matcher(s);
	}
	
	boolean find() {
		boolean found = matcher.find();
		if (found) {
			i = matcher.start();
		} else {
			moveToEnd();
		}
		return found;
	}
	
	void markMatched() {
		setMark(i);
		i = matcher.end();
	}

	int remaining() {
		if (i < l) {
			return l - i;
		}
		return 0;
	}
	
	int indexOf(String substr) {
		int index = s.indexOf(substr, i);
		return index < 0 ? -1 : index - i;
	}

}
