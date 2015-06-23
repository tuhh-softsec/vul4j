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
    private FutureElement parent = null;

    protected BaseElement() {
    }

    /**
     * Additional tag initialization callback.
     * 
     * @throws HttpErrorPage
     * @throws IOException
     * 
     **/
    protected boolean parseTag(Tag tag, FutureParserContext ctx) throws HttpErrorPage, IOException {
        // Default implementation does nothing
        return true;
    }

    @Override
    public boolean onTagStart(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
        Tag tagObj = Tag.create(tag);
        this.parent = ctx.getCurrent();
        return parseTag(tagObj, ctx);
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
