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

package org.esigate.servlet.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.AbstractHttpEntity;
import org.esigate.http.HttpResponseUtils;

public class HttpServletRequestEntity extends AbstractHttpEntity {
    private final HttpServletRequest request;
    private final long length;

    HttpServletRequestEntity(HttpServletRequest request) {
        this.request = request;
        String contentLengthHeader = request.getHeader(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthHeader != null) {
            length = Long.parseLong(contentLengthHeader);
        } else {
            length = -1;
        }
        String contentTypeHeader = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (contentTypeHeader != null) {
            this.setContentType(contentTypeHeader);
        }
        String contentEncodingHeader = request.getHeader(HttpHeaders.CONTENT_ENCODING);
        if (contentEncodingHeader != null) {
            this.setContentEncoding(contentEncodingHeader);
        }

    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long getContentLength() {
        return length;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return request.getInputStream();
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        HttpResponseUtils.writeTo(this, outstream);
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

}
