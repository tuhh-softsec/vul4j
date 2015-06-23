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
import java.util.concurrent.Future;

import org.esigate.HttpErrorPage;

/**
 * Handle unknown tag.
 * 
 * @author Alexis thaveau
 */
public class UnknownElement implements FutureElement {
    /***
     * UnknownElement type.
     */
    public static final FutureElementType TYPE = new FutureElementType() {

        private final UnknownElement instance = new UnknownElement();

        @Override
        public boolean isStartTag(String tag) {
            return true;
        }

        @Override
        public boolean isEndTag(String tag) {
            return false;
        }

        @Override
        public FutureElement newInstance() {
            return instance;
        }

        @Override
        public boolean isSelfClosing(String tag) {
            return true;
        }
    };

    @Override
    public boolean onTagStart(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {

        ctx.characters(new CharSequenceFuture(tag));
        return true;
    }

    @Override
    public void onTagEnd(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {

    }

    @Override
    public boolean onError(Exception e, FutureParserContext ctx) {
        return false;
    }

    @Override
    public void characters(Future<CharSequence> csq) throws IOException {
        throw new UnsupportedOperationException("characters are appended in onTagStart method");
    }

    @Override
    public FutureElement getParent() {
        return null;
    }
}
