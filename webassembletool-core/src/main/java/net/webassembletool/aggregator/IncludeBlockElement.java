package net.webassembletool.aggregator;

import java.io.IOException;

import net.webassembletool.Driver;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;
import net.webassembletool.tags.BlockRenderer;

public class IncludeBlockElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$includeblock$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endincludeblock$");
		}

		public Element newInstance() {
			return new IncludeBlockElement();
		}

	};

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack)
			throws IOException, HttpErrorPage {

		ElementAttributes tagAttributes = ElementAttributesFactory
				.createElementAttributes(tag);
		AggregateRenderer aggregateRenderer = stack.findAncestorWithClass(this,
				AggregateRenderer.class);
		Driver driver = tagAttributes.getDriver();
		String page = tagAttributes.getPage();
		String name = tagAttributes.getName();

		driver.render(page, null, out, aggregateRenderer.getRequest(),
				aggregateRenderer.getResponse(), new BlockRenderer(name, page),
				new AggregateRenderer(aggregateRenderer.getRequest(),
						aggregateRenderer.getResponse()));
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(char c) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		// Just ignore tag body
		return this;
	}

}