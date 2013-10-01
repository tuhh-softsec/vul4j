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

/**
 * Implementation of the <esi:attempt/> tag. This tag must be enclosed by a
 * <esi:try/>
 * 
 */
class AttemptElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:attempt", "</esi:attempt") {
		@Override
		public AttemptElement newInstance() {
			return new AttemptElement();
		}
	};

	private StringBuilder buf = new StringBuilder();

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		// Buffer all content inside this tag.
		this.buf.append(csq, start, end);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {

		// Ensure no error has been flagged on the parent <esi:try/> tag.
		// This means the attempt was successful and we can write the tag
		// content in its direct parent.
		TryElement parent = ctx.findAncestor(TryElement.class);
		if (parent != null && !parent.hasErrors()) {
			ctx.getCurrent().characters(this.buf, 0, this.buf.length());
		}
	}
}
