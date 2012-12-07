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

package org.esigate.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SM;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;


/**
 * FIXME This is a temporary fix for
 * BrowserCompatSpec: cookies values containing spaces are forwarded without quotes
 * https://issues.apache.org/jira/browse/HTTPCLIENT-1269
 * 
 * Should be removed when migrating to HttpClient 4.3
 * @author Francois-Xavier Bonnet
 *
 */
public class BrowserCompatSpec extends org.apache.http.impl.cookie.BrowserCompatSpec {
	
	public BrowserCompatSpec(){
		registerAttribHandler(ClientCookie.VERSION_ATTR, new BrowserCompatVersionAttributeHandler());
	}
	
	@Override
	public List<Header> formatCookies(List<Cookie> cookies) {
        if (cookies == null) {
            throw new IllegalArgumentException("List of cookies may not be null");
        }
        if (cookies.isEmpty()) {
            throw new IllegalArgumentException("List of cookies may not be empty");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
        buffer.append(SM.COOKIE);
        buffer.append(": ");
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            if (i > 0) {
                buffer.append("; ");
            }
            BasicHeaderValueFormatter.DEFAULT.formatHeaderElement(buffer,new BasicHeaderElement(cookie.getName(), cookie.getValue()), false);
        }
        List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
	}
	
}
