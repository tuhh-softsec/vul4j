package net.webassembletool;

import java.util.StringTokenizer;

/**
 * Represents a tag in a page. This can be an opening tag or closing tag, with
 * or without parameters. Begin and end index are relative to the original
 * String it was found in.
 * 
 * @author François-Xavier Bonnet
 */
public class Tag {
    private final int beginIndex;
    private final int endIndex;
    private final String[] tokens;

    public int getBeginIndex() {
	return beginIndex;
    }

    public int getEndIndex() {
	return endIndex;
    }

    public String[] getTokens() {
	return tokens;
    }

    public int countTokens() {
	return tokens.length;
    }

    private Tag(int beginIndex, int endIndex, String content) {
	this.beginIndex = beginIndex;
	this.endIndex = endIndex;
	StringTokenizer stringTokenizer = new StringTokenizer(content, "$");
	tokens = new String[stringTokenizer.countTokens()];
	int i = 0;
	while (stringTokenizer.hasMoreTokens()) {
	    tokens[i] = stringTokenizer.nextToken();
	    i++;
	}
    }

    public final static Tag find(String what, String where, int offset) {
	int begin = where.indexOf("<!--$" + what, offset);
	if (begin < 0)
	    return null;
	int end = where.indexOf("-->", begin);
	if (end < 0)
	    return null;
	return new Tag(begin, end + 3, where.substring(begin + 5, end));
    }

    public final static Tag find(String what, StringBuilder where, int offset) {
	int begin = where.indexOf("<!--$" + what, offset);
	if (begin < 0)
	    return null;
	int end = where.indexOf("-->", begin);
	if (end < 0 || end < begin)
	    return null;
	return new Tag(begin, end + 3, where.substring(begin + 5, end));
    }

    @Override
    public final String toString() {
	StringBuilder result = new StringBuilder("<");
	for (int i = 0; i < tokens.length; i++) {
	    result.append(tokens[i]);
	    if (i < tokens.length - 1)
		result.append(",");
	}
	result.append(">");
	return result.toString();
    }

}
