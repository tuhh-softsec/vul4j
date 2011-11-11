package org.esigate.parser;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Parser parses a CharSequence and finds Strings that match a certain pattern
 * passed in the constructor. For every match, the Parser uses the ElementTypes
 * passed into the constructor to identify which Elements they represent. Then
 * it instantiates the matching Elements puts them in a Stack and calls
 * doStartTag and doEndTag methods on them almost like in JSP taglibs. A parser
 * is Thread safe.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class Parser {
	private final static Logger LOG = LoggerFactory.getLogger(Parser.class);
	private final Pattern pattern;
	private final ElementType[] elementTypes;
	private HttpServletRequest request;

	/**
	 * Creates a Parser with a given regular expression pattern and
	 * ElementTypes.
	 * 
	 * @param pattern
	 *            The regular expression Pattern
	 * @param elementTypes
	 *            The element types
	 */
	public Parser(Pattern pattern, ElementType... elementTypes) {
		this.pattern = pattern;
		this.elementTypes = elementTypes;
	}

	/**
	 * Parses all the CharSequence
	 * 
	 * @param in
	 *            The CharSequence to parse
	 * @param out
	 *            The Writable to write the result to
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void parse(CharSequence in, Appendable out) throws IOException, HttpErrorPage {
		ElementStack stack = new ElementStack(out);
		Matcher matcher = pattern.matcher(in);
		int currentPosition = 0;
		while (matcher.find()) {
			String tag = matcher.group();
			stack.getCurrentWriter().append(in, currentPosition, matcher.start());
			if (!stack.isEmpty() && stack.peek().getType().isEndTag(tag)) {
				// check if this is the end tag for current element
				LOG.info("Processing end tag " + tag);
				Element e = stack.pop();
				if (e instanceof BodyTagElement) {
					Appendable parent = stack.getCurrentWriter();
					String body = in.subSequence(currentPosition, matcher.start()).toString();
					((BodyTagElement) e).setRequest(request);
					((BodyTagElement) e).doAfterBody(body, parent, stack);
				}
				e.doEndTag(tag);
			} else {
				// if not, it is an opening tag for a new element
				LOG.info("Processing start tag " + tag);
				Element newElement = null;
				for (ElementType elementType : elementTypes) {
					if (elementType.isStartTag(tag)) {
						newElement = elementType.newInstance();
						Appendable parent = stack.getCurrentWriter();
						stack.push(newElement);
						if (newElement instanceof BodyTagElement) {
							((BodyTagElement) newElement) .setRequest(request);
						}
						newElement.doStartTag(tag, parent, stack);
						if (newElement.isClosed()) {
							newElement.doEndTag(tag);
							stack.pop();
						}
						break;
					}
				}
				// if no element matches, we just ignore it and write it to the output
				if (newElement == null) {
					stack.getCurrentWriter().append(tag);
				}
			}
			currentPosition = matcher.end();
		}
		// we reached the end of input
		stack.getCurrentWriter().append(in, currentPosition, in.length());
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
