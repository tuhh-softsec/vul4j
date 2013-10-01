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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.esigate.HttpErrorPage;

/**
 * This is an implementation of StringBuilder which can append
 * Future<CharSequence> and is a Future<CharSequence> itself.
 * <p>
 * It is intended for temporary buffers when implementing nested tags.
 * 
 * 
 * @author Nicolas Richeton
 * 
 */
public class StringBuilderFutureAppendable implements FutureAppendable, Future<CharSequence> {
	private static final int DEFAULT_CAPACITY = 1024;
	private final StringBuilder builder;
	private final FutureAppendableAdapter futureBuilder;

	/**
	 * Create a new builder with a default capacity of 1024.
	 */
	public StringBuilderFutureAppendable() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Create a new builder with a custom capacity.
	 * 
	 * @param capacity
	 *            Capacity of the builder.
	 */
	public StringBuilderFutureAppendable(int capacity) {
		this.builder = new StringBuilder(capacity);
		this.futureBuilder = new FutureAppendableAdapter(this.builder);
	}

	public FutureAppendable enqueueAppend(Future<CharSequence> csq) throws IOException {
		return this.futureBuilder.enqueueAppend(csq);
	}

	public FutureAppendable performAppends() throws IOException, HttpErrorPage {
		return this.futureBuilder.performAppends();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isDone() {
		return this.futureBuilder.hasPending();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get()
	 */
	public CharSequence get() throws InterruptedException, ExecutionException {
		try {
			this.futureBuilder.performAppends();
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (HttpErrorPage e) {
			throw new ExecutionException(e);
		}
		return this.builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	public CharSequence get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
			TimeoutException {
		try {
			this.futureBuilder.performAppends();
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (HttpErrorPage e) {
			throw new ExecutionException(e);
		}
		return this.builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.esigate.parser.future.FutureAppendable#hasPending()
	 */
	public boolean hasPending() {
		return this.futureBuilder.hasPending();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.esigate.parser.future.FutureAppendable#performAppends(int,
	 * java.util.concurrent.TimeUnit)
	 */
	public FutureAppendable performAppends(int timeout, TimeUnit unit) throws IOException, HttpErrorPage,
			TimeoutException {
		return this.futureBuilder.performAppends(timeout, unit);
	}

}
