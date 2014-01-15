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

public class ReplaceElementTest extends AbstractElementTest {

    public void testErrorIfNotInsideIncludeTag() throws IOException, HttpErrorPage {
        String page = "begin <esi:replace fragment=\"test\">test</esi:replace> end";
        try {
            render(page);
        } catch (EsiSyntaxError e) {
            return;
        }
        fail("We should have had a EsiSyntaxError");
    }
}
