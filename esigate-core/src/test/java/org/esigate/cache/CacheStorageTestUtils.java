package org.esigate.cache;

import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.Resource;
import org.apache.http.impl.client.cache.HeapResource;
import org.apache.http.message.BasicStatusLine;

public final class CacheStorageTestUtils {

    private CacheStorageTestUtils() {

    }

    static void testBasicOperations(CacheStorage cacheStorage) throws Exception {
        String key = "foo";
        HttpCacheEntry entry = makeCacheEntry("entry");
        TestCase.assertNull("Cache should be empty", cacheStorage.getEntry(key));
        cacheStorage.putEntry(key, entry);
        TestCase.assertEquals("Now the entry should be in cache", getContent(entry),
                getContent(cacheStorage.getEntry(key)));
        final HttpCacheEntry newEntry = makeCacheEntry("new entry");
        HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback() {
            @Override
            public HttpCacheEntry update(HttpCacheEntry existing) throws IOException {
                return newEntry;
            }
        };
        cacheStorage.updateEntry(key, callback);
        TestCase.assertEquals("Entry should have been updated", getContent(newEntry),
                getContent(cacheStorage.getEntry(key)));
        cacheStorage.removeEntry(key);
        TestCase.assertNull("Entry should have been deleted", cacheStorage.getEntry(key));
    }

    static HttpCacheEntry makeCacheEntry(String content) {
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "OK");
        Resource resource = new HeapResource(content.getBytes());
        return new HttpCacheEntry(new Date(), new Date(), statusLine, new Header[0], resource);
    }

    static String getContent(HttpCacheEntry entry) throws Exception {
        if (entry == null) {
            return null;
        }
        return IOUtils.toString(entry.getResource().getInputStream());
    }

}
