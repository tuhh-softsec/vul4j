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
import java.io.Writer;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;

public class BlockRendererTest extends TestCase {

    public void testRenderBlockNull() throws IOException, HttpErrorPage {
        BlockRenderer tested = new BlockRenderer(null, null);
        tested.render(null, null, null);
    }

    public void testRenderBlock() throws IOException, HttpErrorPage {
        final String expectedOutput = "abc some"
                + "<!--$beginblock$myblock$-->some text goes here<!--$endblock$myblock$-->" + " cdf hello";
        Writer out = new StringWriter();
        BlockRenderer tested = new BlockRenderer("myblock", null);
        tested.render(null, expectedOutput, out);
        assertEquals("some text goes here", out.toString());
        // null name means whole page
        out = new StringWriter();
        tested = new BlockRenderer(null, null);
        tested.render(null, expectedOutput, out);
        assertEquals(expectedOutput, out.toString());
    }

    public void testUnknownTag() throws IOException, HttpErrorPage {
        final String input = "abc some<!--$hello$world$-->some text goes here";
        Writer out = new StringWriter();
        BlockRenderer tested = new BlockRenderer(null, null);
        tested.render(null, input, out);
        // input should remain unchanged
        assertEquals(input, out.toString());
    }

}
