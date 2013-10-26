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

import org.esigate.parser.future.FutureElementType;
import org.esigate.parser.future.FutureParserContext;
import org.esigate.parser.future.StringBuilderFutureAppendable;

class ExceptElement extends BaseElement {

	public final static FutureElementType TYPE = new BaseElementType("<esi:except", "</esi:except") {
		@Override
		public ExceptElement newInstance() {
			return new ExceptElement();
		}

	};

	public class ExceptTask implements Future<CharSequence> {
		TryElement parent;
		Tag tag;

		public ExceptTask(Tag tag, TryElement parent) {
			this.parent = parent;
			this.tag = tag;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public CharSequence get() throws InterruptedException, ExecutionException {
			int code = (tag.getAttribute("code") != null) ? Integer.parseInt(tag.getAttribute("code")) : -1;
			boolean processContent = (parent.hasErrors() && !parent.exceptProcessed() && (code == -1 || code == parent
					.getErrorCode()));
			if (processContent) {
				parent.setExceptProcessed(processContent);
				return buf.get();
			}

			return "";
		}

		@Override
		public CharSequence get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
				TimeoutException {
			return get();
		}
	}

	ExceptElement() {

	}

	StringBuilderFutureAppendable buf = new StringBuilderFutureAppendable();

	@Override
	protected void parseTag(Tag tag, FutureParserContext ctx) throws IOException {
		TryElement parent = ctx.findAncestor(TryElement.class);

		if (parent != null) {
			parent.setWrite(true);
			ctx.getCurrent().characters(new ExceptTask(tag, parent));
			parent.setWrite(false);
		}
	}

	@Override
	public void characters(Future<CharSequence> csq) throws IOException {
		buf.enqueueAppend(csq);
	}

}
