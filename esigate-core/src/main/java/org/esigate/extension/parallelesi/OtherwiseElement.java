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

import org.esigate.HttpErrorPage;
import org.esigate.parser.future.CharSequenceFuture;
import org.esigate.parser.future.FutureElementType;
import org.esigate.parser.future.FutureParserContext;
import org.esigate.parser.future.StringBuilderFutureAppendable;
import org.esigate.vars.VariablesResolver;

class OtherwiseElement extends BaseElement {

	public final static FutureElementType TYPE = new BaseElementType("<esi:otherwise", "</esi:otherwise") {
		public OtherwiseElement newInstance() {
			return new OtherwiseElement();
		}

	};

	private boolean active;
	private StringBuilderFutureAppendable buf = new StringBuilderFutureAppendable();

	OtherwiseElement() {
	}

	@Override
	protected void parseTag(Tag tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		ChooseElement parent = ctx.findAncestor(ChooseElement.class);
		active = (parent != null) && !parent.hadConditionSet();
	}

	@Override
	public void onTagEnd(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		if (active) {
			String result;
			try {
				result = VariablesResolver.replaceAllVariables(buf.get().toString(), ctx.getHttpRequest());
			} catch (InterruptedException e) {
				throw new IOException(e);
			} catch (ExecutionException e) {
				if( e.getCause() instanceof HttpErrorPage ) {
					throw (HttpErrorPage)e.getCause();
				}
				throw new IOException(e);
			}
			super.characters(new CharSequenceFuture(result));
		}
	}

	@Override
	public void characters(Future<CharSequence> csq) throws IOException {
		if (active) {
			buf.enqueueAppend(csq);
		}
	}
}
