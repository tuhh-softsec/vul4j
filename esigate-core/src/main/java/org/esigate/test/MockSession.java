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

package org.esigate.test;

import java.io.Serializable;
import java.util.HashMap;

import org.esigate.api.Session;

/**
 * MockSession can be used in unit test.
 * 
 * @see Session
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class MockSession implements Session {

    private final HashMap<String, Serializable> map = new HashMap<String, Serializable>();

    @Override
    public void setAttribute(String key, Serializable value) {
        map.put(key, value);
    }

    @Override
    public Serializable getAttribute(String key) {
        return map.get(key);
    }

}
