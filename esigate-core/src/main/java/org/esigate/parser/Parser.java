package org.esigate.parser;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {
	private final static Logger LOG = LoggerFactory.getLogger(Parser.class);
	private final Pattern pattern;
	private final ElementType[] elementTypes;
	private ResourceContext resourceContext;

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
		ParserContextImpl ctx = new ParserContextImpl(out, resourceContext);
		Matcher matcher = pattern.matcher(in);
		int currentPosition = 0;
		while (matcher.find()) {
			String tag = matcher.group();
			ctx.characters(in, currentPosition, matcher.start());
			currentPosition = matcher.end();
			if (ctx.isCurrentTagEnd(tag)) {
				// check if this is the end tag for current element
				LOG.info("Processing end tag " + tag);
				ctx.endElement(tag);
			} else {
				// if not, it is an opening tag for a new element
				LOG.info("Processing start tag " + tag);
				ElementType type = null;
				for (ElementType t : elementTypes) {
					if (t.isStartTag(tag)) {
						type = t;
						break;
					}
				}
				if (type != null) {
					Element element = type.newInstance();
					ctx.startElement(type, element, tag);
					if (element.isClosed()) {
						ctx.endElement(tag);
					}
				} else {
					// if no element matches, we just ignore it and write it to the output
					ctx.characters(tag);
				}
			}
		}
		// we reached the end of input
		ctx.characters(in, currentPosition, in.length());
	}

	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}

}
