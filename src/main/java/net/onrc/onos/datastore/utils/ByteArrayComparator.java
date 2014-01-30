package net.onrc.onos.datastore.utils;

import java.nio.ByteBuffer;
import java.util.Comparator;

public final class ByteArrayComparator implements Comparator<byte[]> {

    public static final ByteArrayComparator BYTEARRAY_COMPARATOR = new ByteArrayComparator();

    @Override
    public int compare(byte[] o1, byte[] o2) {
	final ByteBuffer b1 = ByteBuffer.wrap(o1);
	final ByteBuffer b2 = ByteBuffer.wrap(o2);
	return b1.compareTo(b2);
    }
}
