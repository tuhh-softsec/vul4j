package net.webassembletool.parser;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;

public interface Element {
	/**
	 * Method called by the parser when it finds an opening tag
	 * 
	 * @param tag
	 *            The tag
	 * @param out
	 *            The writer where to output the result
	 * @param parser
	 *            Gives access to the parser if the tag needs to access the tag
	 *            stack and attributes created by parent tags
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage;

	/**
	 * Method called by the parser when it finds the matching closing tag
	 * 
	 * @param tag
	 *            The tag
	 * @param out
	 *            The Writer where to output the result
	 * @param parser
	 *            Gives access to the parser if the tag needs to access the tag
	 *            stack and attributes created by parent tags
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void doEndTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage;

	/**
	 * Method called by the parser when reading text inside the tag. This method
	 * may be called several times.
	 * 
	 * @param content
	 *            A CharSequence containing the content of the tag
	 * @param begin
	 *            The begin index of the content to process
	 * @param end
	 *            The end index of the content to process
	 * @param out
	 *            The writer where to output the result
	 * @param parser
	 *            Gives access to the parser if the tag needs to access the tag
	 *            stack and attributes created by parent tags
	 * @throws IOException
	 */
	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) throws IOException;

	public ElementType getType();

	/**
	 * @return Returns true if the tag is already closed, that means that it does not
	 *         need a matching closing tag. Ex: &lt;br /&gt;
	 */
	public boolean isClosed();
}
