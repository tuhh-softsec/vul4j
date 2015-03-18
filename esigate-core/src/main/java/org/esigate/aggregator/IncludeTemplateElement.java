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
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.http.HttpResponseUtils;
import org.esigate.parser.Adapter;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.tags.TemplateRenderer;

class IncludeTemplateElement implements Element {
    public static final ElementType TYPE = new ElementType() {
        @Override
        public boolean isStartTag(String tag) {
            return tag.startsWith("<!--$includetemplate$");
        }

        @Override
        public boolean isEndTag(String tag) {
            return tag.startsWith("<!--$endincludetemplate$");
        }

        @Override
        public Element newInstance() {
            return new IncludeTemplateElement();
        }

    };

    private Driver driver;
    private String page;
    private String name;
    private final Map<String, String> params = new HashMap<String, String>();
    private Appendable out;

    @Override
    public boolean onError(Exception e, ParserContext ctx) {
        return false;
    }

    @Override
    public void onTagStart(String tag, ParserContext ctx) {
        this.out = new Adapter(ctx.getCurrent());

        ElementAttributes tagAttributes = ElementAttributesFactory.createElementAttributes(tag);
        this.driver = tagAttributes.getDriver();
        this.page = tagAttributes.getPage();
        this.name = tagAttributes.getName();

    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        CloseableHttpResponse response =
                driver.render(page, ctx.getHttpRequest().getOriginalRequest(),
                        new TemplateRenderer(name, params, page), new AggregateRenderer());
        out.append(HttpResponseUtils.toString(response));
    }

    public void addParam(String paramName, String value) {
        params.put(paramName, value);
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void characters(CharSequence csq, int start, int end) {
        // Just ignore tag body
    }

}
