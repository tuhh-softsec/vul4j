package net.webassembletool.aggregator;

import java.io.IOException;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
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
		String[] parameters = tag.split("\\$");
		Driver driver;
		if (parameters.length > 3)
			driver = DriverFactory.getInstance(parameters[2]);
		else
			driver = DriverFactory.getInstance();
		String page = "";
		if (parameters.length > 4)
			page = parameters[3];
		String name = null;
		if (parameters.length > 5)
			name = parameters[4];
		AggregateRenderer aggregateRenderer = stack.findAncestorWithClass(this,
				AggregateRenderer.class);
		driver.render(page, null, out, aggregateRenderer.getRequest(),
				new BlockRenderer(name, page));
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
