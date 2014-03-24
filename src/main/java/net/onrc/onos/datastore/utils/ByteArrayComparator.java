package net.onrc.onos.datastore.utils;

import java.nio.ByteBuffer;
import java.util.Comparator;

/**
 * Comparator which will compares the content of byte[].
 *
 * Expected to be used with TreeMap, etc. when you want to use byte[] as a key.
 */
public final class ByteArrayComparator implements Comparator<byte[]> {

    /**
     * Instance which can be used, if you want to avoid instantiation per Map.
     */
    public static final ByteArrayComparator BYTEARRAY_COMPARATOR = new ByteArrayComparator();

    @Override
    public int compare(final byte[] o1, final byte[] o2) {
        final ByteBuffer b1 = ByteBuffer.wrap(o1);
        final ByteBuffer b2 = ByteBuffer.wrap(o2);
        return b1.compareTo(b2);
    }
}
