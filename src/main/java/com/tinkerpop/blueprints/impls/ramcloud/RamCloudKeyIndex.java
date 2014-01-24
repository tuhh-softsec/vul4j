/* Copyright (c) 2013 Stanford University
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR(S) DISCLAIM ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL AUTHORS BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.tinkerpop.blueprints.impls.ramcloud;

import java.io.Serializable;
import java.util.Set;

import com.tinkerpop.blueprints.Element;

public class RamCloudKeyIndex<T extends RamCloudElement> extends RamCloudIndex<T> implements Serializable {
    public RamCloudKeyIndex(long tableId, String indexName, Object propValue, RamCloudGraph graph, Class<T> indexClass) {
	super(tableId, indexName, propValue, graph, indexClass);
    }

    public RamCloudKeyIndex(long tableId, byte[] rcKey, RamCloudGraph graph, Class<T> indexClass) {
	super(tableId, rcKey, graph, indexClass);
    }

    public boolean autoUpdate(final String key, final Object newValue, final Object oldValue, final T element) {
	if (graph.indexedKeys.contains(key)) {
	    if (oldValue != null) {
		this.remove(key, oldValue, element);
	    }
	    this.put(key, newValue, element);
	    return true;
	} else {
	    return false;
	}
    }

    public void autoRemove(final String key, final Object oldValue, final T element) {
	if (graph.indexedKeys.contains(key)) {
	    this.remove(key, oldValue, element);
	}
    }

    public long reIndexElements(final RamCloudGraph graph, final Iterable<? extends Element> elements, final Set<String> keys) {
	long counter = 0;
	for (final Element element : elements) {
	    for (final String key : keys) {
		final Object value = element.removeProperty(key);
		if (null != value) {
		    counter++;
		    element.setProperty(key, value);
		}
	    }
	}
	return counter;
    }
}
