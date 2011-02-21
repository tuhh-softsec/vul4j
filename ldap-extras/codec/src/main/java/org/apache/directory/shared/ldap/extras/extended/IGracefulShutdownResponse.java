package org.apache.directory.shared.ldap.extras.extended;


import org.apache.directory.shared.ldap.model.message.ExtendedResponse;


/**
 * The response sent back from the server when a {@link GracefulShutdownRequest}
 * extended operation is sent. Delivery of this response may block until all
 * connected clients are sent a GracefulDisconnect unsolicited notification.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IGracefulShutdownResponse extends ExtendedResponse
{
    /** The OID for the graceful shutdown extended operation response. */
    String EXTENSION_OID = "1.3.6.1.4.1.18060.0.1.4";
}