package net.webassembletool.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.webassembletool.HttpErrorPage;

/**
 * A Parser parses a CharSequence and finds Strings that match a certain patern
 * passed in the constructor. For every match, the Parser uses the ElementTypes
 * passed into the constructor to identify which Elements they represent. Then
 * it instanciates the matching Elements puts them in a Stack and calls
 * doStartTag and doEndTag methods on them almost like in JSP taglibs.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class Parser {
	private final Pattern pattern;
	private final ElementType[] elementTypes;
	private Stack<Element> stack;
	private Map<String, Object> attributes = new HashMap<String, Object>();

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
	 *            The Writer to write the result to
	 * @param write
	 *            True if the Parser should write to the output the text it
	 *            finds outside the Elements found. For all the texts within the
	 *            Elements, the Element is responsible for writing or not.
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	public void parse(CharSequence in, Writer out, boolean write)
			throws IOException, HttpErrorPage {
		stack = new Stack<Element>();
		Matcher matcher = pattern.matcher(in);
		int currentPosition = 0;
		while (!matcher.hitEnd()) {
			if (matcher.find()) {
				String tag = matcher.group();
				if (write || !stack.isEmpty())
					write(out, in, currentPosition, matcher.start());
				if (!stack.isEmpty() && stack.peek().getType().isEndTag(tag)) {
					// check if this is the end tag for current element
					stack.pop().doEndTag(tag, out, this);
				} else {
					// if not, it is an opening tag for a new element
					Element newElement = null;
					for (ElementType elementType : elementTypes) {
						if (elementType.isStartTag(tag)) {
							newElement = elementType.newInstance();
							newElement.doStartTag(tag, out, this);
							if (newElement.isClosed())
								newElement.doEndTag(tag, out, this);
							else
								stack.push(newElement);
							break;
						}
					}
					// if no element matches, we just ignore it and write it
					// to the output
					if (newElement == null)
						if (write || !stack.isEmpty())
							write(out, tag);
				}
				currentPosition = matcher.end();
			} else {
				// we reached the end of input
				if (write || !stack.isEmpty())
					write(out, in, currentPosition, in.length());
			}
		}
	}

	private void write(Writer out, CharSequence content, int start, int end)
			throws IOException {
		if (stack.isEmpty())
			out.append(content, start, end);
		else
			stack.peek().write(content, start, end, out, this);
	}

	private void write(Writer out, CharSequence content) throws IOException {
		if (stack.isEmpty())
			out.append(content);
		else
			stack.peek().write(content, 0, content.length(), out, this);
	}

	/**
	 * Stores an Object in a Map. Attributes can be used to share things between
	 * Elements.
	 * 
	 * @param key
	 *            The key
	 * @param value
	 *            The value
	 */
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	/**
	 * Retrieves an Object in a Map. Attributes can be used to share things
	 * between Elements.
	 * 
	 * @param key
	 *            The key to look for
	 * @return The value
	 */
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	/**
	 * @return The Element Stack
	 */
	public Stack<Element> getStack() {
		return stack;
	}

}
