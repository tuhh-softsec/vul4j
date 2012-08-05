/**
 * 
 */
package net.sf.xslthl.highlighters;

/**
 * Number highlighter for hexadecimal numbers
 */
public class HexaDecimalHighlighter extends NumberHighlighter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.xslthl.highlighters.NumberHighlighter#isDigit(char)
	 */
	@Override
	protected boolean isDigit(char ch) {
		return (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')
		        || super.isDigit(ch);
	}
}
