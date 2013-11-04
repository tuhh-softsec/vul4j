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

import org.esigate.parser.ElementType;

/**
 * <p>
 * The remove element allows for specification of non-ESI markup for output if ESI processing is not enabled. If for
 * some reason ESI processing is not enabled, all of the elements will be passed through to clients, which will ignore
 * markup it doesn't understand.
 * </p>
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <pre>
 * &lt;esi:include src=&quot;http://www.example.com/ad.html&quot;/&gt; 
 * &lt;esi:remove&gt; 
 *   &lt;a href=&quot;http://www.example.com&quot;&gt;www.example.com&lt;/a&gt;
 * &lt;/esi:remove&gt;
 * </pre>
 * 
 * <p>
 * Normally, when this block is processed, the ESI Processor fetches the ad.html resource and includes it in the
 * template while silently discarding the remove element and its contents.
 * </p>
 * 
 * <p>
 * With Web clients, this works because browsers ignore invalid HTML, such as &lt;esi:...&gt; and&lt;/esi:...&gt;
 * elements, leaving the HTML a element and its content.
 * </p>
 * 
 * <p>
 * The remove statement cannot include nested ESI markup.
 * </p>
 * 
 * @author Francois-Xavier Bonnet
 * @see <a href="http://www.w3.org/TR/esi-lang">ESI Language Specification 1.0</a>
 * 
 */
class RemoveElement extends BaseElement {
    public static final ElementType TYPE = new BaseElementType("<esi:remove", "</esi:remove") {
        @Override
        public RemoveElement newInstance() {
            return new RemoveElement();
        }

    };

    RemoveElement() {
    }

    @Override
    public void characters(CharSequence csq, int start, int end) {
        // ignore element body
    }
}
