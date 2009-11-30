package net.webassembletool.parser;

/**
 * An element type. There must be one Element type for each type of tags the
 * parser has to look for in the pages
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public interface ElementType {
	/**
	 * Detects an opening tag for this element type
	 * 
	 * @param tag
	 *            The String to check
	 * @return Returns true if the String is an opening tag
	 */
	public boolean isStartTag(String tag);

	/**
	 * Detects a closing tag for this element type
	 * 
	 * @param tag
	 *            The String to check
	 * @return Returns true if the String is a closing tag
	 */
	public boolean isEndTag(String tag);

	/**
	 * @return A new instance of the corresponding element class
	 */
	public Element newInstance();
}
