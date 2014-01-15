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

import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.HttpErrorPage;
import org.esigate.MockRequestExecutor;

public class CommentTest extends AbstractElementTest {

    @Override
    protected void setUp() {
        super.setUp();
        MockRequestExecutor provider1 = MockRequestExecutor.createMockDriver("provider1");
        provider1.addResource("/test", "replacement");
    }

    public void testComment() throws IOException, HttpErrorPage {
        String page = "begin <!--esi<sometag> some text</sometag>--> end";
        String result = render(page);
        assertEquals("begin <sometag> some text</sometag> end", result);
    }

    public void testCommentVars() throws IOException, HttpErrorPage {
        String page = "<!--esi <p><esi:vars>Hello, $(HTTP_COOKIE{name})!</esi:vars></p> -->";
        getRequestBuilder().addCookie(new BasicClientCookie("name", "world"));
        String result = render(page);
        assertEquals(" <p>Hello, world!</p> ", result);
    }

    /**
     * 0000126: Support for commented esi tags.
     * 
     * http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=126
     * 
     * @throws Exception
     */
    public void testCommentedEsiTags() throws Exception {
        String page = "begin "
                + "<!--esi<esi:include src=\"$(PROVIDER{provider1})/test\">--> content <!--esi</esi:include>-->"
                + " end";
        String result = render(page);
        assertEquals("begin replacement end", result);
    }

}
