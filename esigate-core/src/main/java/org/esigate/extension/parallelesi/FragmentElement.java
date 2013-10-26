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
 */

package org.esigate.extension.parallelesi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.future.CharSequenceFuture;
import org.esigate.parser.future.FutureElementType;
import org.esigate.parser.future.FutureParserContext;

class FragmentElement extends BaseElement {

    public final static FutureElementType TYPE = new BaseElementType("<esi:fragment", "</esi:fragment") {

        @Override
		public FragmentElement newInstance() {
            return new FragmentElement();
        }

    };

    private EsiRenderer esiRenderer;

    private boolean nameMatches;

    private CharSequence replacement = null;

    private boolean initialStateWrite = false;

    FragmentElement() {
    }

    @Override
    public void onTagEnd(String tag, FutureParserContext ctx) throws IOException {
        if (replacement != null) {
            esiRenderer.setWrite(true);
            characters(new CharSequenceFuture(replacement));
        }
        esiRenderer.setWrite(initialStateWrite);
    }

    @Override
    protected void parseTag(Tag tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
        String name = tag.getAttribute("name");
        esiRenderer = ctx.findAncestor(EsiRenderer.class);
        initialStateWrite = esiRenderer.isWrite();
        // If name matches, start writing
        if (esiRenderer != null) {
            nameMatches = name.equals(esiRenderer.getName());
            if (nameMatches) {
                esiRenderer.setWrite(true);
                esiRenderer.setFound(true);
            } else if (esiRenderer.getFragmentsToReplace() != null && initialStateWrite) {
                replacement = esiRenderer.getFragmentsToReplace().get(name);
                if (replacement != null) {
                    esiRenderer.setWrite(false);
                }
            }
        }
    }
}
