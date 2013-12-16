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
import org.esigate.vars.Operations;
import org.esigate.vars.VariablesResolver;

class WhenElement extends BaseElement {

    public static final ElementType TYPE = new BaseElementType("<esi:when", "</esi:when") {
        @Override
        public WhenElement newInstance() {
            return new WhenElement();
        }

    };

    private StringBuilder buf = new StringBuilder();
    private boolean active = false;

    WhenElement() {
    }

    @Override
    protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
        String test = tag.getAttribute("test");
        ChooseElement parent = ctx.findAncestor(ChooseElement.class);
        if (test != null && parent != null) {
            // no other 'when' were active before
            active = !parent.hadConditionSet();
            parent.setCondition(Operations.processOperators(VariablesResolver.replaceAllVariables(test,
                    ctx.getHttpRequest())));
            active &= parent.isCondition();
        }
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) throws IOException {
        if (active) {
            String result = VariablesResolver.replaceAllVariables(buf.toString(), ctx.getHttpRequest());
            super.characters(result, 0, result.length());
        }
    }

    @Override
    public void characters(CharSequence csq, int start, int end) {
        if (active) {
            buf.append(csq, start, end);
        }
    }
}
