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
import org.esigate.parser.Element;
import org.esigate.parser.ParserContext;

abstract class BaseElement implements Element {
    private boolean closed = false;
    private Element parent = null;

    protected BaseElement() {
    }

    /** Additional tag initialization callback. */
    @SuppressWarnings("unused")
    protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
        // Default implementation does nothing
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        Tag tagObj = Tag.create(tag);
        closed = tagObj.isOpenClosed();
        parent = ctx.getCurrent();
        parseTag(tagObj, ctx);
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
    }

    @Override
    public boolean onError(Exception e, ParserContext ctx) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.esigate.parser.Element#characters(java.lang.CharSequence, int, int)
     */
    @Override
    public void characters(CharSequence csq, int start, int end) throws IOException {
        parent.characters(csq, start, end);
    }

}
