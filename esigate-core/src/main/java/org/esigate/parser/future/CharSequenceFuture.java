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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class is a simple wrapper on a CharSequence.
 * <p>
 * This Future object already has it result and does not use any background
 * Thread and simply return the {@link CharSequence} provided at the
 * construction time.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CharSequenceFuture implements Future<CharSequence> {
	private final CharSequence seq;

	/**
	 * Create a new Future object which will return the {@link CharSequence}
	 * provided as parameter.
	 * 
	 * @param charSequence
	 *            the sequence which will be returned on {@link #get()}
	 */
	public CharSequenceFuture(CharSequence charSequence) {
		this.seq = charSequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isDone()
	 */
	public boolean isDone() {
		// This future is already done per implementation.
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	public boolean isCancelled() {
		// Cannot ever been cancelled
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	public CharSequence get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
			TimeoutException {
		// Get will never block, just use the default implementation.
		return get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get()
	 */
	public CharSequence get() throws InterruptedException, ExecutionException {
		// Return the wrapped CharSequence.
		return this.seq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		// Cannot be cancelled.
		return false;
	}
}