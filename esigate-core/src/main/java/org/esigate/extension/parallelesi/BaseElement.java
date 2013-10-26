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
package org.esigate.extension.parallelesi;

import java.io.IOException;
import java.util.concurrent.Future;

import org.esigate.HttpErrorPage;
import org.esigate.parser.future.FutureElement;
import org.esigate.parser.future.FutureParserContext;

abstract class BaseElement implements FutureElement {
	private boolean closed = false;
	private FutureElement parent = null;

	protected BaseElement() {
	}

	/** Additional tag initialization callback. */
	@SuppressWarnings("unused")
	protected void parseTag(Tag tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		// Default implementation does nothing
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void onTagStart(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		Tag tagObj = Tag.create(tag);
		this.closed = tagObj.isOpenClosed();
		this.parent = ctx.getCurrent();
		parseTag(tagObj, ctx);
	}

	@Override
	public void onTagEnd(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		// Empty, implementation can customize this method with tag logic
	}

	@Override
	public boolean onError(Exception e, FutureParserContext ctx) {
		return false;
	}

	@Override
	public void characters(Future<CharSequence> csq) throws IOException {
		this.parent.characters(csq);
	}

	@Override
	public FutureElement getParent() {
		return this.parent;
	}

}
