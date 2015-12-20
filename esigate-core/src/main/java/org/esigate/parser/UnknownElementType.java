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

package org.esigate.parser;

/**
 * Handle unknown tag type.
 * 
 * @author Alexis thaveau
 */
public class UnknownElementType implements ElementType {
    /**
     * singleton
     */
    private static final UnknownElement INSTANCE = new UnknownElement();

    @Override
    public boolean isStartTag(String tag) {
        return true;
    }

    @Override
    public boolean isEndTag(String tag) {
        return false;
    }

    @Override
    public Element newInstance() {
        return UnknownElementType.INSTANCE;
    }

    @Override
    public boolean isSelfClosing(String tag) {
        return true;
    }
}
