package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

/**
 * <p>
 * The remove element allows for specification of non-ESI markup for output if ESI processing is not enabled. If for
 * some reason ESI processing is not enabled, all of the elements will be passed through to clients, which will ignore
 * markup it doesn't understand.
 * </p>
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <pre>
 * &lt;esi:include src=&quot;http://www.example.com/ad.html&quot;/&gt; 
 * &lt;esi:remove&gt; 
 *   &lt;a href=&quot;http://www.example.com&quot;&gt;www.example.com&lt;/a&gt;
 * &lt;/esi:remove&gt;
 * </pre>
 * 
 * <p>
 * Normally, when this block is processed, the ESI Processor fetches the ad.html resource and includes it in the
 * template while silently discarding the remove element and its contents.
 * </p>
 * 
 * <p>
 * With Web clients, this works because browsers ignore invalid HTML, such as &lt;esi:...&gt; and&lt;/esi:...&gt;
 * elements, leaving the HTML a element and its content.
 * </p>
 * 
 * <p>
 * The remove statement cannot include nested ESI markup.
 * </p>
 * 
 * @author Francois-Xavier Bonnet
 * @see <a href="http://www.w3.org/TR/esi-lang">ESI Language Specification 1.0</a>
 * 
 */
public class RemoveElement implements Element {
	public final static ElementType TYPE = new BaseElementType("<esi:remove", "</esi:remove") {
		public RemoveElement newInstance() {
			return new RemoveElement();
		}

	};

	private boolean closed = false;

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		Tag removeTag = new Tag(tag);
		closed = removeTag.isOpenClosed();
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

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		// Just ignore tag body
		return this;
	}

}
