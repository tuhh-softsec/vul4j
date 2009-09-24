package net.webassembletool.esi;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;

public class Comment implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.equals("<!--esi");
		}

		public boolean isEndTag(String tag) {
			return tag.equals("-->");
		}

		public Element newInstance() {
			return new Comment();
		}

	};

	public void doEndTag(String tag, Writer out, Parser parser)
			throws IOException {
		// Nothing to do
	}

	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		// Nothing to do
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) throws IOException {
		out.append(content, begin, end);
	}

	public boolean isClosed() {
		return false;
	}

}
