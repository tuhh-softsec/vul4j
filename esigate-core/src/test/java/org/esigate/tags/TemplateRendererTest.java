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

package org.esigate.tags;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;

public class TemplateRendererTest extends TestCase {

    public void testRenderTemplateNull1() throws IOException, HttpErrorPage {
        StringWriter out = new StringWriter();
        TemplateRenderer tested = new TemplateRenderer(null, null, null);
        tested.render(null, null, out);
        assertEquals(0, out.toString().length());
    }

    public void testRenderTemplateNull2() throws IOException, HttpErrorPage {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");
        StringWriter out = new StringWriter();

        TemplateRenderer tested = new TemplateRenderer(null, params, null);
        tested.render(null, null, out);

        assertFalse(out.toString().contains("key"));
        assertTrue(out.toString().contains("'value'"));
        assertFalse(out.toString().contains("some other key"));
        assertTrue(out.toString().contains("'another value'"));
    }

    public void testRenderTemplate1() throws IOException, HttpErrorPage {
        final String input = "some <!--$beginparam$key$-->some hidden text goes here<!--$endparam$key$--> printed";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");
        StringWriter out = new StringWriter();
        TemplateRenderer tested = new TemplateRenderer(null, params, null);
        tested.render(null, input, out);

        assertFalse(out.toString().contains("key"));
        assertTrue(out.toString().contains("'value'"));
        assertFalse(out.toString().contains("some other key"));

        assertEquals("some 'value' printed", out.toString());
    }

    public void testRenderTemplateWithSimilarParamNames() throws IOException, HttpErrorPage {
        final String expectedOutput = "some <!--$beginparam$key1$-->" + "some hidden text goes here"
                + "<!--$endparam$key1$--> printed";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "Should not work");
        StringWriter out = new StringWriter();
        TemplateRenderer tested = new TemplateRenderer(null, params, null);
        tested.render(null, expectedOutput, out);

        assertFalse(out.toString().contains("Should not be used as replacement"));
    }

    public void testRenderTemplate2() throws IOException, HttpErrorPage {
        final String expectedOutput = "abc some<!--$begintemplate$A$-->" + "some text goes here"
                + "<!--$endtemplate$A$--> cdf hello";
        StringWriter out = new StringWriter();
        TemplateRenderer tested = new TemplateRenderer("A", null, null);
        tested.render(null, expectedOutput, out);
        assertEquals("some text goes here", out.toString());
    }
}
