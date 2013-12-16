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

package org.esigate.impl;

import java.util.Comparator;

/**
 * Weight-based comparator for UriMapping objects.
 * 
 * <p>
 * Used to ensure detailed mapping rules are evaluated before the generic ones.
 * 
 * @author Nicolas Richeton
 * 
 */
public class UriMappingComparator implements Comparator<UriMapping> {

    @Override
    public int compare(UriMapping o1, UriMapping o2) {
        int weightCompare = o2.getWeight() - o1.getWeight();

        if (weightCompare != 0) {
            return weightCompare;
        }

        // 2 objects with the same weight are usually not the same.
        // This is required to prevent removal of different rules using the same
        // weight within a SortedMap.
        return o2.hashCode() - o1.hashCode();
    }

}
