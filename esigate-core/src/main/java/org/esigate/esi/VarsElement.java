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
import org.esigate.vars.VariablesResolver;

class VarsElement extends BaseElement {
    public static final ElementType TYPE = new BaseElementType("<esi:vars", "</esi:vars") {
        @Override
        public VarsElement newInstance() {
            return new VarsElement();
        }

    };

    private StringBuilder buf = new StringBuilder();

    VarsElement() {
    }

    @Override
    public void characters(CharSequence csq, int start, int end) throws IOException {
        buf.append(csq, start, end);
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        String result = VariablesResolver.replaceAllVariables(buf.toString(), ctx.getHttpRequest());
        ctx.getCurrent().characters(result, 0, result.length());
    }

}
