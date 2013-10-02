/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.parser.future;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Future;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.HttpErrorPage;

/**
 * 
 * The stack of tags corresponding to the current position in the document
 * <p>
 * This class is based on ParserContextImpl
 * 
 * @see org.esigate.parser.ParserContextImpl
 * 
 * 
 * @author Nicolas Richeton
 * 
 */
class FutureParserContextImpl implements FutureParserContext {
	private final RootAdapter root;
	private final HttpEntityEnclosingRequest httpRequest;
	private final HttpResponse httpResponse;

	private final Stack<Pair> stack = new Stack<Pair>();
	private Map<String, Object> data ;

	FutureParserContextImpl(FutureAppendable root, HttpEntityEnclosingRequest httpRequest, HttpResponse httpResponse, Map<String,Object> data) {
		this.root = new RootAdapter(root);
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
		this.data = data;
	}

	@Override
	public <T> T findAncestor(Class<T> type) {
		T result = null;
		for (int i = stack.size() - 1; i > -1; i--) {
			FutureElement currentElement = stack.elementAt(i).element;
			if (type.isInstance(currentElement)) {
				result = type.cast(currentElement);
				break;
			}
		}
		// try with root
		if (result == null && type.isInstance(root.root)) {
			result = type.cast(root.root);
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean reportError(FutureElement el, Exception e) {
		boolean result = false;
		FutureElement current = el.getParent();
		while (current != null) {
			if (current.onError(e, this)) {
				result = true;
				break;
			}
			current = current.getParent();
		}

		return result;
	}

	void startElement(FutureElementType type, FutureElement element, String tag) throws IOException, HttpErrorPage {
		element.onTagStart(tag, this);
		stack.push(new Pair(type, element));
	}

	void endElement(String tag) throws IOException, HttpErrorPage {
		FutureElement element = stack.pop().element;
		element.onTagEnd(tag, this);
	}

	boolean isCurrentTagEnd(String tag) {
		return !stack.isEmpty() && stack.peek().type.isEndTag(tag);
	}

	/** Writes characters into current writer. */
	void characters(Future<CharSequence> csq) throws IOException {
		getCurrent().characters(csq);
	}

	@Override
	public FutureElement getCurrent() {
		return (!stack.isEmpty()) ? stack.peek().element : root;
	}

	@Override
	public HttpEntityEnclosingRequest getHttpRequest() {
		return this.httpRequest;
	}

	private static class Pair {
		private final FutureElementType type;
		private final FutureElement element;

		public Pair(FutureElementType type2, FutureElement element) {
			this.type = type2;
			this.element = element;
		}
	}

	private static class RootAdapter implements FutureElement {
		private final FutureAppendable root;

		public RootAdapter(FutureAppendable root) {
			this.root = root;
		}

		@Override
		public void onTagStart(String tag, FutureParserContext ctx) {
			// Nothing to do, this is the root tag
		}

		@Override
		public void onTagEnd(String tag, FutureParserContext ctx) {
			// Nothing to do, this is the root tag
		}

		@Override
		public boolean onError(Exception e, FutureParserContext ctx) {
			return false;
		}

		@Override
		public void characters(Future<CharSequence> csq) throws IOException {
			this.root.enqueueAppend(csq);
		}

		@Override
		public boolean isClosed() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.esigate.parser.future.FutureElement#getParent()
		 */
		@Override
		public FutureElement getParent() {
			// Root parser has no parent.
			return null;
		}
	}

	@Override
	public HttpResponse getHttpResponse() {
		return this.httpResponse;
	}

	@Override
	public Object getData(String key) {
		return this.data == null ? null : this.data.get(key);
	}
}
