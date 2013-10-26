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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.esigate.HttpErrorPage;

/**
 * This class is an Adapter for Appendable objects.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FutureAppendableAdapter implements FutureAppendable {

	private Appendable out;
	private List<Future<CharSequence>> futureList;

	public FutureAppendableAdapter(Appendable out) {
		this.out = out;
		this.futureList = new ArrayList<Future<CharSequence>>();
	}

	@Override
	public FutureAppendable enqueueAppend(Future<CharSequence> csq) throws IOException {
		this.futureList.add(csq);
		return this;
	}

	@Override
	public FutureAppendable performAppends(int timeout, TimeUnit unit) throws IOException, HttpErrorPage,
			TimeoutException {

		try {
			for (Future<CharSequence> f : this.futureList) {
				CharSequence csq = f.get(timeout, unit);
				this.out.append(csq);
			}
		} catch (CancellationException e) {
			throw new IOException(e);
		} catch (InterruptedException e) {
			throw new IOException(e);
		} catch (ExecutionException e) {
			// HttpErrorPage must be
			if (e.getCause() instanceof HttpErrorPage) {
				throw (HttpErrorPage) e.getCause();
			}
			throw new IOException(e);
		}

		this.futureList.clear();
		return this;
	}

	@Override
	public boolean hasPending() {
		return this.futureList.size() > 0;
	}

	@Override
	public FutureAppendable performAppends() throws IOException, HttpErrorPage {
		try {
			for (Future<CharSequence> f : this.futureList) {
				CharSequence csq = f.get();
				this.out.append(csq);
			}
		} catch (CancellationException e) {
			throw new IOException(e);
		} catch (InterruptedException e) {
			throw new IOException(e);
		} catch (ExecutionException e) {
			// HttpErrorPage must be
			if (e.getCause() instanceof HttpErrorPage) {
				throw (HttpErrorPage) e.getCause();
			}
			throw new IOException(e);
		}

		this.futureList.clear();
		return this;
	}
}
