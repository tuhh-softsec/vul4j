package net.webassembletool.esi;

import java.io.IOException;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;
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

	private boolean closed = false;

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack)
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
		Driver driver = DriverFactory.getInstance(provider);
		EsiRenderer esiRenderer = stack.findAncestorWithClass(this,
				EsiRenderer.class);
		driver.render(page, null, out, esiRenderer.getRequest(), esiRenderer
				.getResponse(), new BlockRenderer(null, page));
	}

	public ElementType getType() {
		return TYPE;
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
