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
