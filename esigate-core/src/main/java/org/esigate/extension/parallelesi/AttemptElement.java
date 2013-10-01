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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.future.FutureElementType;
import org.esigate.parser.future.FutureParserContext;
import org.esigate.parser.future.StringBuilderFutureAppendable;

/**
 * Implementation of the <esi:attempt/> tag. This tag must be enclosed by a
 * <esi:try/>
 * 
 * <p>
 * This is the implementation for the FutureParser. This tag depends on the
 * behavior of its children : the tag behavior is deferred until all previous
 * tags (including children) have been applied.
 * 
 */
class AttemptElement extends BaseElement {

	public final static FutureElementType TYPE = new BaseElementType("<esi:attempt", "</esi:attempt") {
		public AttemptElement newInstance() {
			return new AttemptElement();
		}
	};

	public static class AttemptTask implements Future<CharSequence> {
		TryElement parent;
		Future<CharSequence> buffer;

		public AttemptTask(TryElement parent, Future<CharSequence> buf) {
			this.parent = parent;
			this.buffer = buf;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return true;
		}

		public CharSequence get() throws InterruptedException, ExecutionException {
			// Requires completion of all Future objects inside this tag, in
			// order to trigger any error.
			CharSequence content = this.buffer.get();

			// If creating content was successful, return it.
			if (this.parent != null && !this.parent.hasErrors()) {
				return content;
			}

			// If there was an error, discard content.
			return "";
		}

		public CharSequence get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
				TimeoutException {
			return get();
		}
	}

	private StringBuilderFutureAppendable buf = new StringBuilderFutureAppendable();

	AttemptElement() {
	}

	@Override
	public void characters(Future<CharSequence> csq) throws IOException {
		this.buf.enqueueAppend(csq);
	}

	@Override
	public void onTagEnd(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		ctx.getCurrent().characters(new AttemptTask(ctx.findAncestor(TryElement.class), this.buf));
	}
}
