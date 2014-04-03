package net.onrc.onos.core.datastore;

import net.onrc.onos.core.datastore.utils.ByteArrayUtil;

/**
 * Exception thrown when object was not expected to be in data store.
 */
public class ObjectExistsException extends RejectRulesException {
    private static final long serialVersionUID = -1488647215779909457L;

    public ObjectExistsException(final String message) {
        super(message);
    }

    public ObjectExistsException(final IKVTableID tableID, final byte[] key,
            final Throwable cause) {
        super(ByteArrayUtil.toHexStringBuffer(key, ":")
                + " already exist on table:" + tableID, cause);
    }

    public ObjectExistsException(final IKVTableID tableID, final byte[] key) {
        super(ByteArrayUtil.toHexStringBuffer(key, ":")
                + " already exist on table:" + tableID);
    }
}
