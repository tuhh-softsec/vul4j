package net.onrc.onos.core.datastore;

import net.onrc.onos.core.datastore.utils.ByteArrayUtil;

/**
 * Exception thrown when conditional operation failed due to version mismatch.
 */
public class WrongVersionException extends RejectRulesException {
    private static final long serialVersionUID = -1644202495890190823L;

    public WrongVersionException(final String message) {
        super(message);
    }

    public WrongVersionException(final IKVTableID tableID, final byte[] key,
            final long expectedVersion, final Throwable cause) {
        // It will be best if {@code cause} has actual version encountered, but
        // doesn't currently.
        super(ByteArrayUtil.toHexStringBuffer(key, ":") + " on table:"
                + tableID + " was expected to be version:" + expectedVersion,
                cause);
    }

    public WrongVersionException(final IKVTableID tableID, final byte[] key,
            final long expectedVersion, final long encounteredVersion) {
        super(ByteArrayUtil.toHexStringBuffer(key, ":") + " on table:"
                + tableID + " was expected to be version:" + expectedVersion
                + " but found:" + encounteredVersion);
    }

}
