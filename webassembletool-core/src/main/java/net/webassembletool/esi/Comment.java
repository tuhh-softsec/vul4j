package net.webassembletool.esi;

import java.io.IOException;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;

public class Comment implements Element {
	private Appendable parent;
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

	public void doEndTag(String tag) throws IOException {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack)
			throws IOException, HttpErrorPage {
		this.parent = parent;
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		parent.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		parent.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		parent.append(csq, start, end);
		return this;
	}

}
