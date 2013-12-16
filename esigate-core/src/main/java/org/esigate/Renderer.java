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

package org.esigate;

import java.io.IOException;
import java.io.Writer;

import org.apache.http.HttpEntityEnclosingRequest;

/**
 * Content rendering strategy.
 * 
 * @author Stanislav Bernatskyi
 */
public interface Renderer {

    /**
     * Renders provided source and writes results to the output.
     * 
     * 
     * @param originalRequest
     *            request received by esigate.
     * @param src
     *            source to be rendered
     * @param out
     *            output destination. This writer is not thread-safe, use proper synchronization if writing output from
     *            multiple threads.
     * @throws IOException
     * @throws HttpErrorPage
     */
    void render(HttpEntityEnclosingRequest originalRequest, String src, Writer out) throws IOException, HttpErrorPage;
}
