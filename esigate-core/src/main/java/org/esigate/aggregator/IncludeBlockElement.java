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

package org.esigate.aggregator;

import java.io.IOException;

import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.tags.BlockRenderer;

class IncludeBlockElement implements Element {
    public static final ElementType TYPE = new ElementType() {

        @Override
        public boolean isStartTag(String tag) {
            return tag.startsWith("<!--$includeblock$");
        }

        @Override
        public boolean isEndTag(String tag) {
            return tag.startsWith("<!--$endincludeblock$");
        }

        @Override
        public Element newInstance() {
            return new IncludeBlockElement();
        }

    };

    @Override
    public boolean onError(Exception e, org.esigate.parser.ParserContext ctx) {
        return false;
    }

    @Override
    public void onTagEnd(String tag, org.esigate.parser.ParserContext ctx) {
        // Nothing to do
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        ElementAttributes tagAttributes = ElementAttributesFactory.createElementAttributes(tag);
        Driver driver = tagAttributes.getDriver();
        String page = tagAttributes.getPage();
        String name = tagAttributes.getName();

        driver.render(page, null, new Adapter(ctx.getCurrent()), ctx.getHttpRequest(), new BlockRenderer(name, page),
                new AggregateRenderer());
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void characters(CharSequence csq, int start, int end) throws IOException {
        // Just ignore tag body
    }

}
