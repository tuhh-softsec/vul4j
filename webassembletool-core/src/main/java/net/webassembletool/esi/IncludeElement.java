package net.webassembletool.esi;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;
import net.webassembletool.tags.BlockRenderer;

public class IncludeElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<esi:include");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("</esi:include");
		}

		public Element newInstance() {
			return new IncludeElement();
		}

	};

	private boolean closed= false;
	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag, Writer out, Parser parser) {
		// Nothing to do
	}

	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		Tag includeTag = new Tag(tag);
		String src = includeTag.getAttributes().get("src");
		closed = includeTag.isOpenClosed();
        int idx = src.indexOf("$PROVIDER({");
        if (idx < 0) {
            throw new AggregationSyntaxException(
                    "PROVIDER variable is missing: " + src);
        }
        int startIdx = idx + "$PROVIDER({".length();
        int endIndex = src.indexOf("})", startIdx);
        String provider = src.substring(startIdx, endIndex);
        String page = src.substring(endIndex + "})".length());
		Driver driver= DriverFactory.getInstance(provider);
		driver.render(page, null, out, (HttpServletRequest) parser
				.getAttribute("request"), new BlockRenderer(null, page));
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) {
		// Just ignore tag body
	}

}
