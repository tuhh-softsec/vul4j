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

import java.io.Serializable;

import org.apache.http.impl.cookie.BasicClientCookie2;
import org.esigate.api.Cookie;

public class SerializableBasicClientCookie2 extends BasicClientCookie2
		implements Serializable {

	/**
	 * Serial Id
	 */
	private static final long serialVersionUID = -5912866621149862535L;

	/**
	 * Default Constructor taking a name and a value. The value may be null.
	 * 
	 * @param name
	 *            The name.
	 * @param value
	 *            The value.
	 */
	public SerializableBasicClientCookie2(String name, String value) {
		super(name, value);
	}

	/**
	 * Create an instance by copying the content of Cookie c.
	 * 
	 * @param c
	 *            Cookie to copy
	 */
	public SerializableBasicClientCookie2(Cookie c) {
		super(c.getName(), c.getValue());
		setComment(c.getComment());
		setCommentURL(c.getCommentURL());
		setExpiryDate(c.getExpiryDate());
		setDomain(c.getDomain());
		setPath(c.getPath());
		setPorts(c.getPorts());
		setSecure(c.isSecure());
		setVersion(c.getVersion());
	}

}
