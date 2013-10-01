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
package org.esigate.extension.parallelesi;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsiExecutor {
	private final static Logger LOG = LoggerFactory.getLogger(EsiExecutor.class);


	static Executor exe = Executors.newFixedThreadPool(20);

	public static void run(RunnableFuture<CharSequence> runnable) {
		if( LOG.isDebugEnabled()){
			LOG.debug("Adding worker: {}", runnable.getClass().getName());
		}
		exe.execute(runnable);
		
	}
}
