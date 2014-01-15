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

public class EsiRendererTest extends AbstractElementTest {

    public void testFragmentTagsShouldBeRemoved() throws Exception {
        String page = "begin <esi:fragment name=\"test\">content</esi:fragment> end";
        String result = render(page);
        assertEquals("begin content end", result);
    }
}
