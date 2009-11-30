package net.webassembletool.parser;

import java.io.IOException;

import net.webassembletool.HttpErrorPage;

/**
 * An element represents a tag inside a document.
 * @author Francois-Xavier Bonnet
 * 
 */
public interface Element extends Appendable {
	/**
	 * Method called by the parser when it finds an opening tag
	 * 
	 * @param tag
	 *            The tag
	 * @param parent
	 *            The parent tag or document to write to
	 * @param stack
	 *            The current stack of Elements
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void doStartTag(String tag, Appendable parent, ElementStack stack)
			throws IOException, HttpErrorPage;

	/**
	 * Method called by the parser when it finds the matching closing tag
	 * 
	 * @param tag
	 *            The tag
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void doEndTag(String tag) throws IOException, HttpErrorPage;

	/**
	 * @return The Type for this Element
	 */
	public ElementType getType();

	/**
	 * @return Returns true if the tag is already closed, that means that it
	 *         does not need a matching closing tag. Ex: &lt;br /&gt;
	 */
	public boolean isClosed();
}
