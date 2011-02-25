package org.apache.directory.shared.ldap.extras.extended;


import org.apache.directory.shared.ldap.model.message.ExtendedResponse;


/**
 * The response sent back from the server when a {@link GracefulShutdownRequestImpl}
 * extended operation is sent. Delivery of this response may block until all
 * connected clients are sent a GracefulDisconnect unsolicited notification.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface GracefulShutdownResponse extends ExtendedResponse
{
    /** The OID for the graceful shutdown extended operation response. */
    String EXTENSION_OID = GracefulShutdownRequest.EXTENSION_OID;
}