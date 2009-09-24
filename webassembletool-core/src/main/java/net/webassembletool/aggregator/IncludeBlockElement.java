package net.webassembletool.aggregator;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;
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

	public void doEndTag(String tag, Writer out, Parser parser) {
		// Nothing to do
	}

	public void doStartTag(String tag, Writer out, Parser parser)
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
		driver.render(page, null, out, (HttpServletRequest) parser
				.getAttribute("request"), true, new BlockRenderer(name, page));
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) {
		// Just ignore tag body
	}

	public boolean isClosed() {
		return false;
	}

}
