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

import java.util.concurrent.Future;

import org.esigate.parser.future.FutureElementType;

/**
 * The comment element allows developers to comment their ESI instructions, without making the comments available in the
 * processor's output. comment is an empty element, and must not have an end tag.
 * 
 * @author Francois-Xavier Bonnet
 * @see <a href="http://www.w3.org/TR/esi-lang">ESI Language Specification 1.0</a>
 * 
 */
class CommentElement extends BaseElement {
    public static final FutureElementType TYPE = new BaseElementType("<esi:comment", "</esi:comment") {
        @Override
        public CommentElement newInstance() {
            return new CommentElement();
        }

    };

    CommentElement() {
    }

    @Override
    public void characters(Future<CharSequence> csq) {
        // ignore element body
    }
}
