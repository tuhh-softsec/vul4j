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
package org.esigate.parser.future;

/**
 * An element type. There must be one FutureElement type for each type of tags the parser has to look for in the pages
 * <p>
 * This class is based on ElementType
 * 
 * @see org.esigate.parser.ElementType
 * 
 * @author Nicolas Richeton
 * 
 */
public interface FutureElementType {
    /**
     * Detects an opening tag for this element type.
     * 
     * @param tag
     *            The String to check
     * @return Returns true if the String is an opening tag
     */
    boolean isStartTag(String tag);

    /**
     * Detects a closing tag for this element type.
     * 
     * @param tag
     *            The String to check
     * @return Returns true if the String is a closing tag
     */
    boolean isEndTag(String tag);

    /**
     * @return A new instance of the corresponding element class
     */
    FutureElement newInstance();
}
