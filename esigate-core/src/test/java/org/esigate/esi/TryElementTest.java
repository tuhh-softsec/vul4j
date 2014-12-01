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

package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;

public class TryElementTest extends AbstractElementTest {

    @Override
    protected void setUp() {
        super.setUp();
        addResource("/test", "test");
        addResource("http://www.foo.com/test", "test");
        addResource("http://www.foo.com/testFragment",
                "before fragment <esi:fragment name=\"fragmentFound\">FRAGMENT FOUND</esi:fragment> after fragment");
        addResource("http://www.foo.com/testWithoutFragment", "no fragment here");
    }

    /**
     * Ensure invalid markup is deleted
     * <p>
     * <code>
     * &lt;esi:try&gt;<br/> 
     * Invalid markup here<br/> 
     *  &lt;esi:attempt&gt;<br/> 
     *    &lt;esi:include ... &gt;<br/>  
     *   This line is valid and will be processed.<br/>  
     * &lt;/esi:attempt&gt;<br/>
     *     Invalid markup here<br/> 
     *  &lt;esi:except&gt;<br/> 
     *   This HTML line is valid and will be processed.<br/> 
     * &lt;/esi:except&gt;<br/> 
     * Invalid markup here 
     * </esi:try>
     * </code>
     * 
     * @throws IOException
     * @throws HttpErrorPage
     */
    public void testInvalidMarkup() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try> invalid " + "<esi:attempt> " + "<esi:include src='http://www.foo.com/test' /> abc "
                        + "<esi:include src=\"http://www.foo2.com/test\" />" + " cba" + "</esi:attempt>  invalid "
                        + "<esi:except>inside except</esi:except>" + " invalid </esi:try> end";
        String result = render(page);
        assertEquals("begin inside except end", result);
    }

    public void testTry() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>" + "<esi:attempt><esi:include src=\"http://www.foo.com/test\" /></esi:attempt>"
                        + "</esi:try> end";
        String result = render(page);
        assertEquals("begin test end", result);
    }

    public void testAttempt1() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>"
                        + "<esi:attempt>abc <esi:include src=\"http://www.foo.com/test\" /> cba</esi:attempt>"
                        + "<esi:except>inside except</esi:except>" + "</esi:try> end";
        String result = render(page);
        assertEquals("begin abc test cba end", result);
    }

    public void testAttempt2() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>" + "<esi:attempt>abc " + "<esi:include src=\"http://www.foo.com/test\" />"
                        + "<esi:include src='http://www.foo.com/not-found' onerror='continue' />"
                        + " cba</esi:attempt>" + "<esi:except>inside except</esi:except>" + "</esi:try> end";
        String result = render(page);
        assertEquals("begin abc test cba end", result);
    }

    public void testExcept1() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>"
                        + "<esi:attempt>abc <esi:include src=\"http://www.foo2.com/test\" /> cba</esi:attempt>"
                        + "<esi:except>inside except</esi:except>" + "</esi:try> end";
        String result = render(page);
        assertEquals("begin inside except end", result);
    }

    public void testExcept2() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>" + "<esi:attempt> " + "<esi:include src='http://www.foo.com/test' /> abc "
                        + "<esi:include src=\"http://www.foo2.com/test\" /> cba" + "</esi:attempt>"
                        + "<esi:except>inside except</esi:except>" + "</esi:try> end";
        String result = render(page);
        assertEquals("begin inside except end", result);
    }

    public void testMultipleExcept() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>" + "<esi:attempt> "
                        + "<esi:attempt>abc <esi:include src='http://www.foo2.com/test' /> cba</esi:attempt>"
                        + "</esi:attempt>" + "<esi:except code='500'>inside incorrect except</esi:except>"
                        + "<esi:except code='404'>inside correct except</esi:except>"
                        + "<esi:except code='412'>inside incorrect except</esi:except>"
                        + "<esi:except>inside default except</esi:except>" + "</esi:try> end";
        String result = render(page);
        assertEquals("begin inside correct except end", result);
    }

    public void testDefaultExcept() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>" + "<esi:attempt> "
                        + "<esi:attempt>abc <esi:include src='http://www.foo2.com/test' /> cba</esi:attempt>"
                        + "</esi:attempt>" + "<esi:except code='500'>inside incorrect except</esi:except>"
                        + "<esi:except code='412'>inside incorrect except</esi:except>"
                        + "<esi:except>inside default except</esi:except>" + "</esi:try> end";
        String result = render(page);
        assertEquals("begin inside default except end", result);
    }

    public void testTryCatchFragmentNotFound() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>" + "<esi:attempt> "
                        + "<esi:attempt>abc <esi:include src='http://www.foo2.com/test' fragment='fragmentNotFound'/>"
                        + " cba" + "</esi:attempt>" + "</esi:attempt>" + "<esi:except>NOT FOUND</esi:except>"
                        + "</esi:try> end";
        String result = render(page);
        assertEquals("begin NOT FOUND end", result);
    }

    public void testTryFragmentNotFound() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>"
                        + "<esi:attempt> "
                        + "<esi:attempt>abc"
                        + "<esi:include src='http://www.foo.com/testFragment' fragment='fragmentFound'/>"
                        + " cba" + "</esi:attempt>" + "</esi:attempt>" + "</esi:try>end";
        String result = render(page);
        assertEquals("begin  abcFRAGMENT FOUND cbaend", result);
    }

    public void testTryFragmentNotFound2() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>"
                        + "<esi:attempt> "
                        + "<esi:attempt>abc"
                        + "<esi:include src='http://www.foo.com/testWithoutFragment' fragment='fragmentFound'/>"
                        + " cba</esi:attempt>" + "</esi:attempt>" + "</esi:try>end";
        String result = render(page);
        assertEquals("begin end", result);
    }

    public void testTryCatchFragmentNotFound2() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:try>"
                        + "<esi:attempt> "
                        + "<esi:attempt>abc"
                        + "<esi:include src='http://www.foo.com/testWithoutFragment' fragment='fragmentFound'/>"
                        + " cba" + "</esi:attempt>" + "</esi:attempt>" + "<esi:except>NOT FOUND</esi:except>"
                        + "</esi:try>end";
        String result = render(page);
        assertEquals("begin NOT FOUNDend", result);
    }

}
