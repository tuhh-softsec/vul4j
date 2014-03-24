package net.onrc.onos.datastore;

//
// Not sure if we really need this base class.
// Just copied hierarchy from RAMCloud.
//
/**
 * Base exception class for conditional write, etc. failure.
 */
public class RejectRulesException extends Exception {
    private static final long serialVersionUID = -1444683012320423530L;

    public RejectRulesException(final String message) {
        super(message);
    }

    public RejectRulesException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RejectRulesException(final Throwable cause) {
        super(cause);
    }
}
