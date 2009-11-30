package net.webassembletool.tags;

import java.io.IOException;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;

public class BlockElement implements Element {
	private BlockRenderer blockRenderer;
	private boolean nameMatches;
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$beginblock$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endblock$");
		}

		public Element newInstance() {
			return new BlockElement();
		}

	};

	public void doEndTag(String tag) throws IOException {
		// Stop writing
		if (nameMatches)
			blockRenderer.setWrite(false);
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		this.blockRenderer = stack.findAncestorWithClass(this,
				BlockRenderer.class);
		// If name matches, start writing
		nameMatches = name.equals(blockRenderer.getName());
		if (nameMatches)
			blockRenderer.setWrite(true);
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		blockRenderer.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		blockRenderer.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		blockRenderer.append(csq, start, end);
		return this;
	}

}
