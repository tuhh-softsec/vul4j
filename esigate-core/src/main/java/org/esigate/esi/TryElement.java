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

package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class TryElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:try", "</esi:try") {
		@Override
		public TryElement newInstance() {
			return new TryElement();
		}

	};
	private boolean write = false;
	private boolean hasErrors;
	private boolean exceptProcessed;
	private int errorCode;

	TryElement() {
	}

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) {
		this.hasErrors = false;
		this.errorCode = 0;
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean exceptProcessed() {
		return exceptProcessed;
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		if (write)
			super.characters(csq, start, end);
	}

	public void setExceptProcessed(boolean exceptProcessed) {
		this.exceptProcessed = exceptProcessed;
	}

	@Override
	public boolean onError(Exception e, ParserContext ctx) {
		hasErrors = true;
		if (e instanceof HttpErrorPage) {
			errorCode = ((HttpErrorPage) e).getHttpResponse().getStatusLine().getStatusCode();
		}
		return true;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}
}
