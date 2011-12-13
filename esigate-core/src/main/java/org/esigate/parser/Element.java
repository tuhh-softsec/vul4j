package org.esigate.parser;

import java.io.IOException;

import org.esigate.HttpErrorPage;


/**
 * An element represents a tag inside a document.
 * @author Francois-Xavier Bonnet
 * 
 */
public interface Element {
	/**
	 * Method called by the parser when it finds an opening tag
	 * 
	 * @param tag
	 *            The tag
	 * @param ctx
	 *            The parser context
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage;

	/**
	 * Method called by the parser when it finds the matching closing tag
	 * 
	 * @param tag
	 *            The tag
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage;

	/** @return <code>true</code> if error has been handled by this element and it should not be propagated further. */
	public boolean onError(Exception e, ParserContext ctx);

	/** Method called by the parser when it finds characters between starting and closing tags. */
    void characters(CharSequence csq, int start, int end)throws IOException;

	/** @return Returns true if the tag is already closed, that means that it does not need a matching closing tag.
	 * Ex: &lt;br /&gt; */
	public boolean isClosed();
}
