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
 * This is a special construct to allow HTML marked up with ESI to render without processing. ESI Processors will remove
 * the start ("&lt;!--esi") and end ("--&gt;") when the page is processed, while still processing the contents. If the
 * page is not processed, it will remain, becoming an HTML/XML comment tag.
 * 
 * @author Francois-Xavier Bonnet
 * @see <a href="http://www.w3.org/TR/esi-lang">ESI Language Specification 1.0</a>
 * 
 */
class Comment extends BaseElement {
    public static final ElementType TYPE = new BaseElementType("<!--esi", "-->") {
        @Override
        public Comment newInstance() {
            return new Comment();
        }

    };

    Comment() {
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        // do not try to parse tag string
        super.onTagStart("<esi!-->", ctx);
    }
}
