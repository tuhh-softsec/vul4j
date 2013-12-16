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

package org.esigate.extension;

import java.util.Properties;

import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;

/**
 * This extension adds a X-Powered-By header.
 * 
 * @author Nicolas Richeton
 * 
 */
public class XPoweredBy implements Extension, IEventListener {

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_POST, this);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {

        FragmentEvent e = (FragmentEvent) event;
        e.httpResponse.addHeader("X-Powered-By", "Esigate");

        // Continue processing
        return true;
    }

}
