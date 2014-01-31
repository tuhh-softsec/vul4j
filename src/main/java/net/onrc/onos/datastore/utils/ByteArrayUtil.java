package net.onrc.onos.datastore.utils;

import java.nio.ByteBuffer;

public class ByteArrayUtil {

    public static StringBuffer toHexStringBuffer(final byte[] bytes,
	    final String sep) {
	return toHexStringBuffer(bytes, sep, new StringBuffer());
    }

    public static StringBuffer toHexStringBuffer(final byte[] bytes,
	    final String sep, StringBuffer buf) {
	if (bytes == null) {
	    return buf;
	}

	ByteBuffer wrap = ByteBuffer.wrap(bytes);

	boolean hasWritten = false;
	while (wrap.hasRemaining()) {
	    if (hasWritten) {
		buf.append(sep);
	    }
	    buf.append(Integer.toHexString(wrap.get()));
	    hasWritten = true;
	}

	return buf;
    }
}
