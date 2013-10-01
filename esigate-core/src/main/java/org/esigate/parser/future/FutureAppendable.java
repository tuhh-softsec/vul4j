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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.esigate.HttpErrorPage;

/**
 * This interface is similar to Appendable except that it is based on Future
 * objects.
 * <p>
 * This allows to provide the content of the CharSequence to append at the very
 * last moment, when all the CharSequences are effectively appended together.
 * <p>
 * While this interface allows concurrent implementation of an Appendable (based
 * on Future), implementations are not required to be Thread-safe. This means
 * that {@link #enqueueAppend(Future)}, {@link #performAppends()} and
 * {@link #hasPending()} are supposed to be called for the same Thread.
 * <p>
 * Management of Threads concurrency must be implemented by the Future
 * implementation which must perform all the synchronization work between any
 * background Thread and the calling Thread.
 * 
 * @author Nicolas Richeton
 * 
 */
public interface FutureAppendable {

	/**
	 * Queue the Future<CharSequence> for append in this
	 * <tt>FutureAppendable</tt>.
	 * 
	 * <p>
	 * Caller may start computing the content of the Future parameter before or
	 * after calling this method (using other Thread) or simply wait for
	 * Future#get() to be called (defered computation).
	 * 
	 * @param csq
	 *            The Future character sequence to append. If <tt>csq</tt> is
	 *            <tt>null</tt>, then the four characters <tt>"null"</tt> are
	 *            appended to this FutureAppendable.
	 * 
	 * @return A reference to this <tt>FutureAppendable</tt>
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	FutureAppendable enqueueAppend(Future<CharSequence> csq) throws IOException;

	/**
	 * Wait for all computation to complete and perform the pending append
	 * operations.
	 * 
	 * @return A reference to this <tt>FutureAppendable</tt>
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	FutureAppendable performAppends() throws IOException, HttpErrorPage;

	/**
	 * 
	 * Wait for all computation to complete and perform the pending append
	 * operations.
	 * 
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws IOException
	 * @throws HttpErrorPage
	 * @throws TimeoutException
	 */
	FutureAppendable performAppends(int timeout, TimeUnit unit) throws IOException, HttpErrorPage, TimeoutException;

	/**
	 * Check if some append operations are still waiting for
	 * {@link #performAppends()} to be called.
	 * 
	 * @return true if append operations are pending.
	 */
	boolean hasPending();

}
