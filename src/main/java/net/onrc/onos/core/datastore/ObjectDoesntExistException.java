package net.onrc.onos.core.datastore;

import net.onrc.onos.core.datastore.utils.ByteArrayUtil;

/**
 * Exception thrown when object was expected, but not found in data store.
 */
public class ObjectDoesntExistException extends RejectRulesException {
    private static final long serialVersionUID = 859082748533417866L;

    public ObjectDoesntExistException(final String message) {
        super(message);
    }

    public ObjectDoesntExistException(final IKVTableID tableID,
                                      final byte[] key, final Throwable cause) {
        super(ByteArrayUtil.toHexStringBuffer(key, ":")
                + " did not exist on table:" + tableID, cause);
    }

    public ObjectDoesntExistException(final IKVTableID tableID, final byte[] key) {
        super(ByteArrayUtil.toHexStringBuffer(key, ":")
                + " did not exist on table:" + tableID);
    }
}
