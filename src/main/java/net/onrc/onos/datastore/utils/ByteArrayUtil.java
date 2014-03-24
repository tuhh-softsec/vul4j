package net.onrc.onos.datastore.utils;

import java.nio.ByteBuffer;

public class ByteArrayUtil {

    // Suppresses default constructor, ensuring non-instantiability.
    private ByteArrayUtil() {}

    /**
     * Returns a StringBuffer with each byte in {@code bytes}
     * converted to a String with {@link Integer#toHexString(int)},
     * separated by {@code sep}.
     *
     * @param bytes byte array to convert
     * @param sep separator between each bytes
     * @return {@code bytes} converted to a StringBuffer
     */
    public static StringBuffer toHexStringBuffer(final byte[] bytes,
            final String sep) {
        return toHexStringBuffer(bytes, sep, new StringBuffer());
    }

    /**
     * Returns a StringBuffer with each byte in {@code bytes}
     * converted to a String with {@link Integer#toHexString(int)},
     * separated by {@code sep}.
     *
     * @param bytes byte array to convert
     * @param sep separator between each bytes
     * @param buf StringBuffer to append to.
     * @return {@code buf}
     */
    public static StringBuffer toHexStringBuffer(final byte[] bytes,
            final String sep, final StringBuffer buf) {
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
